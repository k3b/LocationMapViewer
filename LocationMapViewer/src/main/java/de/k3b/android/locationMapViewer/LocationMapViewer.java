/*
 * Copyright (C) 2015 k3b
 *
 * This file is part of de.k3b.android.LocationMapViewer (https://github.com/k3b/LocationMapViewer/) .
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package de.k3b.android.locationMapViewer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.FolderOverlay;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import de.k3b.android.locationMapViewer.constants.Constants;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;
import de.k3b.geo.io.gpx.GpxReaderBase;
import microsoft.mappoint.TileSystem;

/**
 * An app that can display geografic info in a map for Android 2.1 (Eclair, API 7) .<br/>
 * no support for actionbar and fragments.<br/>
 * The code is based on "org.osmdroid.samples.SampleWithMinimapItemizedoverlay in DemoApp OpenStreetMapViewer"
 */
public class LocationMapViewer extends Activity implements Constants {
    private static final Logger logger = LoggerFactory.getLogger(LocationMapViewer.class);

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MENU_SETTINGS_ID = Menu.FIRST;
    private static final int MENU_ZOOMIN_ID = Menu.FIRST + 1;
    private static final int MENU_ZOOMOUT_ID = MENU_ZOOMIN_ID + 2;

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * used to remeber last gui settings when app is closed or rotated
     */
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private ItemizedOverlay<OverlayItem> mPointOfInterestOverlay;
    private MyLocationNewOverlay mLocationOverlay;
    private MinimapOverlay mMiniMapOverlay;
    /**
     * where images/icons are loaded from
     */
    private ResourceProxy mResourceProxy;

    /**
     * setCenterZoom does not work in onCreate() because getHeight() and getWidth() are not calculated yet and return 0;
     * setCenterZoom must be set later when getHeight() and getWith() are known (i.e. in onWindowFocusChanged()).
     * <p/>
     * see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
     */
    private DelayedLatLonZoom mDelayedLatLonZoom;

    /**
     * used to visualize item-cluster in the map
     */
    private Drawable mAtomicPoiIcon;
    private boolean mUseClusterPoints = true;
    private FolderOverlay mPOIOverlayNonCluster;
    private RadiusMarkerClusterer mPOIOverlayCluster;

    private Marker currentSelectedPosition = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Called when the activity is first created.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
        mAtomicPoiIcon = getResources().getDrawable(R.drawable.marker_default);

        mResourceProxy = new ResourceProxyImpl(getApplicationContext());

        this.setContentView(R.layout.mapview);

        mMapView = (MapView) this.findViewById(R.id.mapview);

        final List<Overlay> overlays = this.mMapView.getOverlays();

        Intent intent = this.getIntent();
        String extraTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
        if (extraTitle != null) {
            this.setTitle(extraTitle);
        }

        GeoPointDto geoPointFromIntent = getGeoPointDtoFromIntent(intent);

        mUseClusterPoints = mPrefs.getBoolean(PREFS_CLUSTER_POINTS, true);

        mPOIOverlayNonCluster = (mUseClusterPoints) ? null : new FolderOverlay(this);
        mPOIOverlayCluster = (mUseClusterPoints) ? createPointOfInterestOverlay(overlays) : null;

        final IGeoInfoHandler pointCollector = (mUseClusterPoints)
            ? new IGeoInfoHandler() {
                @Override
                public void onGeoInfo(IGeoPointInfo aGeoPoint) {
                    if (aGeoPoint != null) {
                        mPOIOverlayCluster.add(createMarker(mMapView, aGeoPoint, mAtomicPoiIcon));
                    }
                }
            }
            : new IGeoInfoHandler() {
                @Override
                public void onGeoInfo(IGeoPointInfo aGeoPoint) {
                    if (aGeoPoint != null) {
                        mPOIOverlayNonCluster.add(createMarker(mMapView, aGeoPoint, mAtomicPoiIcon));
                    }
                }
            };

        pointCollector.onGeoInfo(geoPointFromIntent);

        loadGeoPointDtosFromFile(intent, pointCollector);

        AbstractList<? extends Overlay> items = (mUseClusterPoints) ? mPOIOverlayCluster.getItems() : mPOIOverlayNonCluster.getItems();
        final int zoom = (geoPointFromIntent != null) ? geoPointFromIntent.getZoomMin() : GeoPointDto.NO_ZOOM;
        this.mDelayedLatLonZoom = (items.size() > 0) ? new DelayedLatLonZoom(items, zoom) : null;
        if (items.size() == 0) {
            loadDemoItems(pointCollector);
        }

        createMyLocationOverlay(overlays);

        createMiniMapOverlay(overlays);

        // interactive overlay last=on top
        if (geoPointFromIntent != null) {
            final String title = geoPointFromIntent.getName();
            createMarkerOverlayForMovablePosition(overlays, mMapView, title, toOsmGeoPoint(geoPointFromIntent));
        }

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

        loadFromSettings();
        // setCenterZoom does not work in Android2.1-onCreate() because getHeight() and getWidth() return 0;
        // initial center must be set later
        // see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
//        if (initalMapCenterZoom != null) {
//            setCenterZoom(initalMapCenterZoom);
//        }
    }

    private Marker createMarker(MapView map, IGeoPointInfo aGeoPoint, Drawable defaultIcon) {
        // final OverlayItem overlayItem = new OverlayItem(aGeoPoint.getId(), aGeoPoint.getName(), aGeoPoint.getDescription(), toOsmGeoPoint(aGeoPoint));
        // items.add(overlayItem);

        Marker poiMarker = new Marker(map);
        poiMarker.setTitle(aGeoPoint.getName());
        poiMarker.setSnippet(aGeoPoint.getDescription());
        poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        poiMarker.setPosition(toOsmGeoPoint(aGeoPoint));

        if (defaultIcon != null) {
            poiMarker.setIcon(defaultIcon);
        }
        /*
        if (poi.mThumbnail != null){
            poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
        }*/

        // 7.
        poiMarker.setInfoWindow(new CustomInfoWindow(map));
        poiMarker.setRelatedObject(aGeoPoint);

        return poiMarker;
    }

    private RadiusMarkerClusterer createPointOfInterestOverlay(List<Overlay> overlays) {
        //10. Marker Clustering
        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClustererWithInfo(this);

        Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_red_empty);
        poiMarkers.setIcon(((BitmapDrawable) clusterIconD).getBitmap());

        //end of 10.
        //11. Customizing the clusters design
        poiMarkers.getTextPaint().setTextSize(12.0f);
        poiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        poiMarkers.mTextAnchorU = 0.70f;
        poiMarkers.mTextAnchorV = 0.27f;
        //end of 11.
        overlays.add(poiMarkers);
        return poiMarkers;
    }

    private void createMarkerOverlayForMovablePosition(List<Overlay> overlays, MapView map, String title, GeoPoint geoPoint) {
        // from com.example.osmbonuspacktuto.MainActivity
        //0. Using the Marker overlay
        currentSelectedPosition = new Marker(map);
        currentSelectedPosition.setPosition((geoPoint != null) ? geoPoint : new GeoPoint(0, 0));
        currentSelectedPosition.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentSelectedPosition.setTitle(title);
        currentSelectedPosition.setIcon(getResources().getDrawable(R.drawable.marker_yellow));
        //startMarker.setImage(getResources().getDrawable(R.drawable.ic_launcher));
        //startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble_black, map));
        currentSelectedPosition.setDraggable(true);
        overlays.add(currentSelectedPosition);

        Button cmdOk = (Button) findViewById(R.id.ok);
        cmdOk.setVisibility(View.VISIBLE);
        cmdOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSelectedPosition != null) {

                    GeoPointDto geoPoint = new GeoPointDto().setLatitude(currentSelectedPosition.getPosition().getLatitude()).setLongitude(currentSelectedPosition.getPosition().getLongitude());
                    String uri = new GeoUri(GeoUri.OPT_DEFAULT).toUriString(geoPoint);
                    setResult(0, new Intent(Intent.ACTION_PICK, Uri.parse(uri)));
                }

                finish();
            }
        });
    }

    private GeoPoint toOsmGeoPoint(IGeoPointInfo aGeoPoint) {
        if (aGeoPoint == null) return null;

        return new GeoPoint(aGeoPoint.getLatitude(), aGeoPoint.getLongitude());
    }

    private void loadGeoPointDtosFromFile(Intent intent, IGeoInfoHandler pointCollector) {
        final Uri uri = (intent != null) ? intent.getData() : null;
        if (uri != null) {
            ContentResolver cr = getContentResolver();
            GeoPointDto p = new GeoPointDto();
            try {
                InputStream is = cr.openInputStream(uri);
                GpxReaderBase parser = new GpxReaderBase(pointCollector, p);
                parser.parse(new InputSource(is));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private GeoPointDto getGeoPointDtoFromIntent(Intent intent) {
        final Uri uri = (intent != null) ? intent.getData() : null;
        String uriAsString = (uri != null) ? uri.toString() : null;
        GeoPointDto pointFromIntent = null;
        if (uriAsString != null) {
            Toast.makeText(this, getString(R.string.app_name) + ": received  " + uriAsString, Toast.LENGTH_LONG).show();
            GeoUri parser = new GeoUri(GeoUri.OPT_PARSE_INFER_MISSING);
            pointFromIntent = (GeoPointDto) parser.fromUri(uriAsString, new GeoPointDto());
        }
        return pointFromIntent;
    }

    /**
     * Create some Hardcoded Markers on some cities.
     */
    private void loadDemoItems(final IGeoInfoHandler handler) {
        handler.onGeoInfo(new GeoPointDto(52.370816, 9.735936, "Hannover", "Tiny SampleDescription"));
        handler.onGeoInfo(new GeoPointDto(52.518333, 13.408333, "Berlin", "This is a relatively short SampleDescription."));
        handler.onGeoInfo(new GeoPointDto(38.895000, -77.036667, "Washington",
                "This SampleDescription is a pretty long one. Almost as long as a the great wall in china."));
        handler.onGeoInfo(new GeoPointDto(37.779300, -122.419200, "San Francisco", "SampleDescription"));
    }

    private void createMyLocationOverlay(List<Overlay> overlays) {
        this.mLocationOverlay = new MyLocationNewOverlay(this, new GpsMyLocationProvider(this),
                mMapView);
        overlays.add(this.mLocationOverlay);
        mLocationOverlay.enableMyLocation(); // could be made configurable through settings
    }

    private void createMiniMapOverlay(List<Overlay> overlays) {
        mMiniMapOverlay = new MinimapOverlay(this,
                mMapView.getTileRequestCompleteHandler());
        overlays.add(mMiniMapOverlay);
    }

    @Override
    public void onPause() {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_MINIMAP, mMiniMapOverlay.isEnabled());
        edit.putBoolean(PREFS_CLUSTER_POINTS, this.mUseClusterPoints);

        edit.commit();

        saveLastXYZ();

        this.mLocationOverlay.disableMyLocation();

        super.onPause();
    }

    private void saveLastXYZ() {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
        edit.commit();
        if (logger.isDebugEnabled()) {
            logger.debug("saved LastXYZ:" + getStatusForDebug());
        }
    }

    private String getStatusForDebug() {
        if (logger.isDebugEnabled()) {
            StringBuilder result = new StringBuilder();
            final int scrollX = mMapView.getScrollX();
            final int scrollY = mMapView.getScrollY();
            final int zoomLevel = mMapView.getZoomLevel();
            GeoPoint geoPoint = TileSystem.PixelXYToLatLong(scrollX, scrollY, zoomLevel, null);
            GeoPoint geoPointLR = TileSystem.PixelXYToLatLong(scrollX + this.mMapView.getWidth(), scrollY + this.mMapView.getHeight(), zoomLevel, null);
            result.append("Current scrollXYZ:")
                    .append(scrollX).append("/").append(scrollY).append("/").append(zoomLevel)
                    .append("; screen w/h:").append(this.mMapView.getWidth()).append("/").append(this.mMapView.getHeight())
                    .append("; LatLon:").append(geoPoint.toDoubleString()).append("..").append(geoPointLR.toDoubleString());
            ;
            return result.toString();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadFromSettings();

        boolean useClusterPoints = mPrefs.getBoolean(PREFS_CLUSTER_POINTS, true);
        if (useClusterPoints != this.mUseClusterPoints) {
            List<Overlay> overlays = mMapView.getOverlays();
            if (useClusterPoints) {
                mPOIOverlayNonCluster.closeAllInfoWindows();
                mPOIOverlayCluster = new RadiusMarkerClustererWithInfo(this);
                for (Overlay item : mPOIOverlayNonCluster.getItems()) {
                    mPOIOverlayCluster.add((Marker) item);
                }
                int oldPos = overlays.indexOf(mPOIOverlayNonCluster);
                overlays.remove(oldPos);
                overlays.add(oldPos, mPOIOverlayCluster);
                mPOIOverlayNonCluster.getItems().clear();;
                mPOIOverlayNonCluster = null;
            } else {
                // !useClusterPoints
                mPOIOverlayNonCluster = new FolderOverlay(this);
                for (Marker item : mPOIOverlayCluster.getItems()) {
                    mPOIOverlayNonCluster.add((Marker) item);
                }
                int oldPos = overlays.indexOf(mPOIOverlayCluster);
                overlays.remove(oldPos);
                overlays.add(oldPos, mPOIOverlayNonCluster);
                mPOIOverlayCluster.getItems().clear();;
                mPOIOverlayCluster = null;
            }
            this.mUseClusterPoints = useClusterPoints;
            this.mMapView.invalidate();
        }

        final int zoom = mPrefs.getInt(PREFS_ZOOM_LEVEL, 3);
        final int scrollX = mPrefs.getInt(PREFS_SCROLL_X, 0);
        final int scrollY = mPrefs.getInt(PREFS_SCROLL_Y, 0);

        final IMapController controller = mMapView.getController();
        controller.setZoom(mPrefs.getInt(PREFS_ZOOM_LEVEL, zoom));

        mMapView.scrollTo(scrollX, scrollY);
        if (logger.isDebugEnabled()) {
            logger.debug("onResume loaded lastXYZ:" + scrollX + "/" + scrollY + "/" + zoom + " => "
                    + getStatusForDebug());
        }
    }

    private void loadFromSettings() {
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }
        if (mPrefs.getBoolean(PREFS_SHOW_LOCATION, false)) {
            this.mLocationOverlay.enableMyLocation();
        }

        this.mMiniMapOverlay.setEnabled(mPrefs.getBoolean(PREFS_SHOW_MINIMAP, true));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (this.mDelayedLatLonZoom != null) {
            // setCenterZoom does not work in onCreate() because getHeight() and getWidth() return 0;
            // initial center must be set later when getHeight() and getWith() are set (i.e. in onWindowFocusChanged()).
            // see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
            this.mDelayedLatLonZoom.setCenterZoom(mMapView);
            this.mDelayedLatLonZoom = null; // donot call it again
        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onCreateOptionsMenu(final Menu pMenu) {
        pMenu.add(0, MENU_ZOOMIN_ID, Menu.NONE, "ZoomIn");
        pMenu.add(0, MENU_ZOOMOUT_ID, Menu.NONE, "ZoomOut");

        pMenu.add(0, MENU_SETTINGS_ID, Menu.NONE, R.string.title_activity_settings);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ZOOMIN_ID:
                this.mMapView.getController().zoomIn();
                return true;

            case MENU_ZOOMOUT_ID:
                this.mMapView.getController().zoomOut();
                return true;

            case MENU_SETTINGS_ID:
                SettingsActivity.show(this);
                return true;
        }
        return false;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    /**
     * setCenterZoom does not work in onCreate() because getHeight() and getWidth() are not calculated yet and return 0;
     * setCenterZoom must be set later when getHeight() and getWith() are known (i.e. in onWindowFocusChanged()).
     * <p/>
     * see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
     */
    private class DelayedLatLonZoom {
        private GeoPoint mMin = null;
        private GeoPoint mMax = null;
        private int mZoom;

        public DelayedLatLonZoom(final GeoPoint min, final GeoPoint max, final int zoom) {
            mMin = min;
            mMax = max;
            mZoom = zoom;
        }

        // MarkerClusterer
        public DelayedLatLonZoom(AbstractList<? extends Overlay> items, int zoom) {
            if (items.size() > 0) {
                Marker first = (Marker) items.get(0);
                GeoPoint min = new GeoPoint(first.getPosition().clone());
                GeoPoint max = null;
                if (items.size() > 1) {
                    max = min.clone();
                    for (Overlay item : items) {
                        getMinMax(min, max, ((Marker) item).getPosition());
                    }
                }
                mMin = min;
                mMax = max;
            }
            mZoom = zoom;
        }

        public DelayedLatLonZoom(ArrayList<OverlayItem> items, int zoom) {
            if (items.size() > 0) {
                OverlayItem first = items.get(0);
                final IGeoPoint firstPoint = first.getPoint();
                GeoPoint min = new GeoPoint(firstPoint.getLatitudeE6(), firstPoint.getLongitudeE6());
                GeoPoint max = null;
                if (items.size() > 1) {
                    max = min.clone();
                    for (OverlayItem item : items) {
                        getMinMax(min, max, item.getPoint());
                    }
                }
                mMin = min;
                mMax = max;
            }
            mZoom = zoom;
        }

        private void getMinMax(GeoPoint resultMin, GeoPoint resultMax, IGeoPoint candidate) {
            if (resultMin.getLatitudeE6() > candidate.getLatitudeE6()) {
                resultMin.setLatitudeE6(candidate.getLatitudeE6());
            }
            if (resultMin.getLongitudeE6() > candidate.getLongitudeE6()) {
                resultMin.setLongitudeE6(candidate.getLongitudeE6());
            }
            if (resultMax.getLatitudeE6() < candidate.getLatitudeE6()) {
                resultMax.setLatitudeE6(candidate.getLatitudeE6());
            }
            if (resultMax.getLongitudeE6() < candidate.getLongitudeE6()) {
                resultMax.setLongitudeE6(candidate.getLongitudeE6());
            }
        }

        public void setCenterZoom(MapView mapView) {
            int zoom = mZoom;

            MapTileProviderBase tileProvider = mapView.getTileProvider();
            IMapController controller = mapView.getController();
            GeoPoint center = mMin;
            if (mMax != null) {
                center = new GeoPoint((mMax.getLatitudeE6() + mMin.getLatitudeE6()) / 2, (mMax.getLongitudeE6() + mMin.getLongitudeE6()) / 2);

                if (zoom == GeoPointDto.NO_ZOOM) {
                    final double requiredMinimalGroundResolutionInMetersPerPixel = ((double) mMin.distanceTo(mMax)) / Math.min(mapView.getWidth(), mapView.getHeight());
                    zoom = calculateZoom(center.getLatitude(), requiredMinimalGroundResolutionInMetersPerPixel, tileProvider.getMaximumZoomLevel(), tileProvider.getMinimumZoomLevel());
                }
            }
            if (zoom != GeoPointDto.NO_ZOOM) {
                controller.setZoom(zoom);
            }

            controller.setCenter(center);

            if (logger.isDebugEnabled()) {
                logger.debug("DelayedLatLonZoom.setCenterZoom(({}) .. ({}),z={}) => ({}), z={} => {}",
                        mMin, mMax, mZoom, center, zoom, getStatusForDebug());
            }
        }

        private int calculateZoom(double latitude, double requiredMinimalGroundResolutionInMetersPerPixel, int maximumZoomLevel, int minimumZoomLevel) {
            for (int zoom = maximumZoomLevel; zoom >= minimumZoomLevel; zoom--) {
                if (TileSystem.GroundResolution(latitude, zoom) > requiredMinimalGroundResolutionInMetersPerPixel)
                    return zoom;
            }

            return GeoPointDto.NO_ZOOM;
        }
    }

    //7. Customizing the bubble behaviour
    class CustomInfoWindow extends MarkerInfoWindow {
        GeoPointDto mSelectedPoi;

        public CustomInfoWindow(MapView mapView) {
            super(R.layout.bubble_geo_point_dto, mapView);
            Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mSelectedPoi.getUri() != null) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedPoi.getUri()));
                        view.getContext().startActivity(myIntent);
                    } else {
                        Toast.makeText(view.getContext(), "Button clicked", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public void onOpen(Object item) {
            super.onOpen(item);
            mView.findViewById(R.id.bubble_moreinfo).setVisibility(View.VISIBLE);
            Marker marker = (Marker) item;
            mSelectedPoi = (GeoPointDto) marker.getRelatedObject();
/* !!!
            //8. put thumbnail image in bubble, fetching the thumbnail in background:
            if (mSelectedPoi.mThumbnailPath != null){
                ImageView imageView = (ImageView)mView.findViewById(R.id.bubble_image);
                mSelectedPoi.fetchThumbnailOnThread(imageView);
            }
            */
        }
    }


}
