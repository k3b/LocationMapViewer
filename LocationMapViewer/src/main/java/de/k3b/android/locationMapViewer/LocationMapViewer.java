/*
 * Copyright (C) 2015-2021 k3b
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

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.k3b.android.GeoUtil;
import de.k3b.android.locationMapViewer.constants.Constants;
import de.k3b.android.locationMapViewer.geobmp.BookmarkListOverlay;
import de.k3b.android.locationMapViewer.geobmp.BookmarkUtil;
import de.k3b.android.locationMapViewer.geobmp.GeoBmpDto;
import de.k3b.android.osmdroid.GuestureOverlay;
import de.k3b.android.osmdroid.ZoomUtil;
import de.k3b.android.widgets.AboutDialogPreference;
import de.k3b.android.widgets.FilePermissionActivity;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;
import de.k3b.geo.io.gpx.GeoXmlOrTextParser;
import de.k3b.geo.io.gpx.GpxReaderBase;
import de.k3b.util.ImageResize;

/**
 * An app that can display geografic info in a map for Android 2.1 (Eclair, API 7) .<br/>
 * The view can be customized via intent-api. Supported intent elements
 * - "geo:..." show this location in the map
 * - "file:/.../*.kml" or "file:/.../*.gpx" show these in map
 * - Supported Action
 * - - VIEW show in map
 * - - PICK select a position from a map and return it to the caller
 * - extra title:
 * - - on android >= 3.0 (honycomp) show actionbar with this tiltle and actionbar-menubar if title set. Else button for popup-menu
 * - - on android < 3.0 show with this tiltle in titlebar if title set. Else no titlebar. Menu via the menu-key.
 * no support for fragments.<br/>
 * The code is based on "org.osmdroid.samples.SampleWithMinimapItemizedoverlay in DemoApp OpenStreetMapViewer"
 */
public class LocationMapViewer extends FilePermissionActivity implements Constants, BookmarkListOverlay.AdditionalPoints  {
    private static final Logger logger = LoggerFactory.getLogger(LocationMapViewer.class);

    // ===========================================================
    // Constants
    // ===========================================================

    private static final DecimalFormat LAT_LON2TEXT = new DecimalFormat("#.#########");
    private static final int REQUEST_ID_PICK_KML_DIR = 126;
    private static final boolean LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * used to remeber last gui settings when app is closed or rotated
     */
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private MinimapOverlay mMiniMapOverlay;

    /**
     * setCenterZoom does not work in onCreate() because getHeight() and getWidth() are not calculated yet and return 0;
     * setCenterZoom must be set later when getHeight() and getWith() are known (i.e. in onWindowFocusChanged()).
     * <p/>
     * see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
     */
    private DelayedSetCenterZoom mDelayedSetCenterZoom;

    /**
     * used to visualize item-cluster in the map
     */
    private Drawable mPoiIconWithData;
    private Drawable mPoiIconWithoutData;
    private boolean mUseClusterPoints = true;
    private FolderOverlay mPOIOverlayNonCluster;
    private RadiusMarkerClusterer mPOIOverlayCluster;

    private Marker currentSelectedPosition = null;
    private boolean mUsePicker;
    private GuestureOverlay mGuesturesOverlay;
    private SeekBar mZoomBar;

    /** first visible window as bookmark candidate */
    private GeoBmpDto initialWindow = null;
    private BookmarkListOverlay bookmarkListOverlay;
    private ImageButton cmdShowMenu = null;
    private Boolean showLocation = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Called when the activity is first created.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //https://github.com/osmdroid/osmdroid/issues/366
        //super important. Many tile servers, including open street maps, will
        // BAN applications by user if this is not set
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateEx(Bundle savedInstanceState) {
        Intent intent = this.getIntent();

        GeoPointDto geoPointFromIntent = getGeoPointDtoFromIntent(intent);

        mUsePicker = ((Intent.ACTION_PICK.equals(intent.getAction())) || (Intent.ACTION_GET_CONTENT.equals(intent.getAction())));

        String extraTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
        if (extraTitle == null && (geoPointFromIntent == null)) {
            extraTitle = getString(R.string.app_name);
        }
        if (extraTitle == null) {
              this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        mPoiIconWithData = getResources().getDrawable(R.drawable.marker_green);
        mPoiIconWithoutData = getResources().getDrawable(R.drawable.marker_no_data);

        this.setContentView(R.layout.mapview);

        mMapView = (MapView) this.findViewById(R.id.mapview);

        final List<Overlay> overlays = this.mMapView.getOverlays();

        if (extraTitle != null) {
            this.setTitle(extraTitle);
        } else {
            setNoTitle();
        }

        mUseClusterPoints = getPrefs().getBoolean(PREFS_CLUSTER_POINTS, true);

        mPOIOverlayNonCluster = (mUseClusterPoints) ? null : createNonClusterOverlay(overlays);
        mPOIOverlayCluster = (mUseClusterPoints) ? createPointOfInterestOverlay(overlays) : null;

        final IGeoInfoHandler pointCollector = createPointCollector(geoPointFromIntent);

        if (geoPointFromIntent != null) {
            initialWindow = new GeoBmpDto(geoPointFromIntent);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_no_data);
            initialWindow.setBitmap(drawable.getBitmap());

            initialWindow.setName(getString(R.string.bookmark_template_initial) + geoPointFromIntent.getName());
        }

        loadGeoPointDtos(intent, pointCollector);
        loadGeoPointDtosFromExtra(intent.getStringExtra("de.k3b.POIS"), pointCollector);

        zoomTo(geoPointFromIntent, pointCollector);

        createMyLocationOverlay(overlays);

        createMiniMapOverlay(overlays);

        createZoomBar();

        // interactive overlay last=on top
        if (geoPointFromIntent != null) {
            final String title = geoPointFromIntent.getName();
            createMarkerOverlayForMovablePosition(overlays, mMapView, title, toOsmGeoPoint(geoPointFromIntent));
        }

        mGuesturesOverlay = new GuestureOverlay(this);
        overlays.add(mGuesturesOverlay);

        mMapView.setMultiTouchControls(true);

        loadFromSettings();
        // setCenterZoom does not work in Android2.1-onCreate() because getHeight() and getWidth() return 0;
        // initial center must be set later
        // see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
//        if (initalMapCenterZoom != null) {
//            setCenterZoom(initalMapCenterZoom);
//        }

        this.bookmarkListOverlay = new BookmarkListOverlay(this, this) {
            @Override
            protected void onSelChanged(GeoBmpDto newSelection) {
                super.onSelChanged(newSelection);

                if (newSelection != null) {
                    setDelayedCenterZoom(newSelection);
                }
            }
        };

        // else html a-href-links do not work.
        TextView t2 = (TextView) findViewById(R.id.cright_osm);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private IGeoInfoHandler createPointCollector(GeoPointDto geoPointFromIntent) {
        final IGeoInfoHandler pointCollector = (mUseClusterPoints)
            ? new IGeoInfoHandler() {
                @Override
                public boolean onGeoInfo(IGeoPointInfo aGeoPoint) {
                    if (aGeoPoint != null) {
                        mPOIOverlayCluster.add(createMarker(mMapView, aGeoPoint.clone()));
                    }
                    return true;
                }
            }
            : new IGeoInfoHandler() {
                @Override
                public boolean onGeoInfo(IGeoPointInfo aGeoPoint) {
                    if (aGeoPoint != null) {
                        mPOIOverlayNonCluster.add(createMarker(mMapView, aGeoPoint.clone()));
                    }
                    return true;
                }
            };

        if (mPOIOverlayCluster != null) mPOIOverlayCluster.getItems().clear();
        if (mPOIOverlayNonCluster != null) mPOIOverlayNonCluster.getItems().clear();

        pointCollector.onGeoInfo(geoPointFromIntent);
        return pointCollector;
    }

    private void zoomTo(GeoPointDto geoPointFromIntent, IGeoInfoHandler pointCollector) {
        List<? extends Overlay> items = (mUseClusterPoints) ? mPOIOverlayCluster.getItems() : mPOIOverlayNonCluster.getItems();
        final int zoom = (geoPointFromIntent != null) ? geoPointFromIntent.getZoomMin() : GeoPointDto.NO_ZOOM;
        this.mDelayedSetCenterZoom = (items.size() > 0) ? new DelayedSetCenterZoom(items, zoom) : null;
        if (items.size() == 0) {
            loadDemoItems(pointCollector);
        }
    }

    private SharedPreferences getPrefs() {
        if (mPrefs == null) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        }

        return mPrefs;
    }

    private void loadGeoPointDtosFromExtra(String pois, IGeoInfoHandler pointCollector) {
        if (pois != null) {
            List<GeoBmpDto> result = new GeoXmlOrTextParser<GeoBmpDto>().get(new GeoBmpDto(), pois);

            if (result != null) {
                for(GeoBmpDto item : result) {
                    pointCollector.onGeoInfo(item);
                }
            }
        }
    }

    private FolderOverlay createNonClusterOverlay(List<Overlay> overlays) {
        FolderOverlay result = new FolderOverlay(this);
        overlays.add(result);

        return result;
    }

    @Override
    public void onBackPressed() {
        // back should close the overlay if active
        if (this.bookmarkListOverlay.isBookmarkListVisible()) {
            this.bookmarkListOverlay.setBookmarkListVisible(false);
        } else {
            // close the activity
            super.onBackPressed();
        }
    }

    private void setNoTitle() {
        if (USE_ACTIONBAR) {
            setNoActionbar();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setNoActionbar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // android-version < 3.0 does not need this. they use the menu key instead
        // ImageButton as replacement for hidden action menue

        final PopupMenu.OnMenuItemClickListener popUpListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return LocationMapViewer.this.onMenuItemClick(menuItem);
            }
        };

        cmdShowMenu = (ImageButton) findViewById(R.id.cmd_menu);
        cmdShowMenu.setVisibility(View.VISIBLE);
        cmdShowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(LocationMapViewer.this, view);
                popup.setOnMenuItemClickListener(popUpListener);
                Menu menu = popup.getMenu();
                MenuInflater inflater = popup.getMenuInflater();
                if (LOLLIPOP) {
                    inflater.inflate(R.menu.location_map_viewer_21, menu);
                }
                inflater.inflate(R.menu.location_map_viewer, menu);
                popup.show();
            }
        });
    }

    private void createZoomBar() {
        mMapView.setBuiltInZoomControls(true);

        mZoomBar = (SeekBar) findViewById(R.id.zoomBar);

        mZoomBar.setMax((int) (mMapView.getMaxZoomLevel() - mMapView.getMinZoomLevel()));
        mZoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    mMapView.getController().setZoom(progress - mMapView.getMinZoomLevel());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // not used
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // not used
            }
        });

        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                mZoomBar.setProgress((int) mMapView.getZoomLevelDouble());
                return false;
            }
        });
    }

    private Marker createMarker(MapView map, IGeoPointInfo aGeoPoint) {
        Marker poiMarker = new Marker(map);
        poiMarker.setTitle(aGeoPoint.getName());
        final String description = aGeoPoint.getDescription();
        String symbol = aGeoPoint.getSymbol();

        poiMarker.setSnippet(description);
        poiMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        poiMarker.setPosition(toOsmGeoPoint(aGeoPoint));

        boolean hasMarkerInfo = BookmarkUtil.isNotEmpty(description) ||
                BookmarkUtil.isNotEmpty(aGeoPoint.getLink()) ||
                BookmarkUtil.isNotEmpty(aGeoPoint.getName());
        GeoPointMarkerInfoWindow infoWindow = null;
        Drawable icon = mPoiIconWithoutData;

        if (hasMarkerInfo) {
            icon = this.mPoiIconWithData;
            infoWindow = new GeoPointMarkerInfoWindow(map);
        }
        setImage(poiMarker, icon, symbol);

        poiMarker.setInfoWindow(infoWindow);
        poiMarker.setRelatedObject(aGeoPoint);

        return poiMarker;
    }

    private void setImage(Marker poiMarker, Drawable defaultIcon, String symbol) {
        Drawable icon = defaultIcon;
        if (true && symbol != null && symbol.contains(":") && symbol.contains("/")) {
            // must be absolute image uri probably "https://..." or "content://..."
            InputStream inputStream = null;
            BitmapDrawable drawable = null;
            try {
                inputStream = getContentResolver().openInputStream(Uri.parse(symbol));
                drawable = (inputStream == null) ? null : new BitmapDrawable(getResources(), inputStream);
            } catch (Exception e) {
                logger.info("symbol '" + symbol + "' not loaded: " + e.getMessage(), e);
            } finally {
                closeSilently(inputStream);
            }
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                //!!! 50 pix is too small and needs a border to be visible in map
                Double scale = ImageResize.getResizeFactor(width, height, 50);

                if (scale != null) {
                    width *= scale;
                    height *= scale;
                    Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, width, height, false);

                    icon = new BitmapDrawable(getResources(), bitmapResized);
                } else {
                    icon = drawable;
                }

            }
            poiMarker.setImage(drawable);
        }
        poiMarker.setIcon(icon);
    }

    private void closeSilently(java.io.Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private RadiusMarkerClusterer createPointOfInterestOverlay(List<Overlay> overlays) {
        //10. Marker Clustering
        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClustererWithInfo(this) {
            @Override
            public Marker buildClusterMarker(StaticCluster cluster, MapView mapView) {
                Marker result = super.buildClusterMarker(cluster,mapView);
                if (cluster.getSize() > 0) {
                    // show data of the first object in cluster
                    result.setRelatedObject(cluster.getItem(0).getRelatedObject());
                }
                return result;
            }
        };

        Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_red_empty);
        poiMarkers.setIcon(((BitmapDrawable) clusterIconD).getBitmap());

        //end of 10.
        //11. Customizing the clusters design
        poiMarkers.getTextPaint().setTextSize(12.0f);
        poiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
        poiMarkers.mTextAnchorU = 0.70f;
        poiMarkers.mTextAnchorV = 0.27f;
        //end of 11.
        if (overlays != null) {
            overlays.add(poiMarkers);
        }
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
        currentSelectedPosition.setDraggable(true);
        overlays.add(currentSelectedPosition);

        if (mUsePicker) {
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
    }

    private GeoPoint toOsmGeoPoint(IGeoPointInfo aGeoPoint) {
        if (aGeoPoint == null) return null;

        return new GeoPoint(aGeoPoint.getLatitude(), aGeoPoint.getLongitude());
    }

    private void loadGeoPointDtos(DocumentFile documentFile, IGeoInfoHandler pointCollector) {
        final Uri uri = (documentFile != null) ? documentFile.getUri() : null;
        loadGeoPointDtos(uri, pointCollector);
    }

    private void loadGeoPointDtos(Intent intent, IGeoInfoHandler pointCollector) {
        final Uri uri = (intent != null) ? intent.getData() : null;
        loadGeoPointDtos(uri, pointCollector);
    }

    private void loadGeoPointDtos(Uri uri, IGeoInfoHandler pointCollector) {
        if (uri != null) {
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
                GpxReaderBase parser = new GpxReaderBase(pointCollector, new GeoPointDto());
                parser.parse(new InputSource(is));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeSilently(is);
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
                "This SampleDescription is a pretty long one. Almost as long as a the great wall in china.")
                .setLink("geo:0,0?q=38.895000,-77.036667(Washington)"));
        handler.onGeoInfo(new GeoPointDto(37.779300, -122.419200, "San Francisco", "SampleDescription"));
    }

    private void createMyLocationOverlay(List<Overlay> overlays) {
        this.mLocationOverlay = new MyLocationNewOverlay(mMapView);
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
        final SharedPreferences.Editor edit = getPrefs().edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putBoolean(PREFS_SHOW_MINIMAP, mMiniMapOverlay.isEnabled());
        edit.putBoolean(PREFS_CLUSTER_POINTS, this.mUseClusterPoints);
        //edit.putBoolean(PREFS_SHOW_GUESTURES, this.mGuesturesOverlay.isEnabled());
        edit.putBoolean(PREFS_DEBUG_GUESTURES, this.mGuesturesOverlay.isDebugEnabled());

        edit.commit();

        saveLastXYZ();

        this.mLocationOverlay.disableMyLocation();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // prevent exception "LocationMapViewer has leaked IntentReceiver"
        // search for "osmdroid MinimapOverlay unregisterReceiver" found this solution
        // https://stackoverflow.com/questions/37304905/leaked-intentreceiver-exception-using-osmdroid
        this.mMapView.onDetach();

        super.onDestroy();
    }

    private void saveLastXYZ() {
        final SharedPreferences.Editor edit = getPrefs().edit();
        edit.putFloat(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putFloat(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putFloat(PREFS_ZOOM_LEVEL, (float) mMapView.getZoomLevelDouble());
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
            final int zoomLevel = (int) mMapView.getZoomLevelDouble();

            GeoPoint geoPoint = MapView.getTileSystem().getGeoFromMercator(scrollX, scrollY, TileSystem.MapSize(zoomLevel), null, true, true);
            GeoPoint geoPointLR = MapView.getTileSystem().getGeoFromMercator(scrollX + this.mMapView.getWidth(), scrollY + this.mMapView.getHeight(), TileSystem.MapSize(zoomLevel), null, true, true);
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

        boolean useClusterPoints = getPrefs().getBoolean(PREFS_CLUSTER_POINTS, true);
        if (useClusterPoints != this.mUseClusterPoints) {
            List<Overlay> overlays = mMapView.getOverlays();
            if (useClusterPoints) {
                mPOIOverlayNonCluster.closeAllInfoWindows();
                mPOIOverlayCluster = createPointOfInterestOverlay(null);
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

        RestoreXYZ();
    }

    private void RestoreXYZ() {
        final float zoom = getPrefs().getFloat(PREFS_ZOOM_LEVEL, 3);
        final float scrollX = getPrefs().getFloat(PREFS_SCROLL_X, 0);
        final float scrollY = getPrefs().getFloat(PREFS_SCROLL_Y, 0);

        final IMapController controller = mMapView.getController();
        controller.setZoom(getPrefs().getFloat(PREFS_ZOOM_LEVEL, zoom));

        mMapView.scrollTo((int) scrollX, (int) scrollY);
        if (logger.isDebugEnabled()) {
            logger.debug("onResume loaded lastXYZ:" + scrollX + "/" + scrollY + "/" + zoom + " => "
                    + getStatusForDebug());
        }
    }

    protected void initShowLocation() {
        setShowLocation(super.isShowLocation());
    }

    @Override
    protected boolean isShowLocation() {
        if (showLocation == null) {
            initShowLocation();
        }
        return showLocation;
    }

    @Override
    protected void setShowLocation(boolean value) {
        showLocation = value;
        super.setShowLocation(value);
        if (this.mLocationOverlay != null) {
            if (value) {
                mLocationOverlay.enableMyLocation();
            } else {
                mLocationOverlay.disableMyLocation();
            }
        }
    }

    private void loadFromSettings() {
        SharedPreferences mPrefs = getPrefs();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }
        initShowLocation();

        this.mGuesturesOverlay.setDebugEnabled(mPrefs.getBoolean(PREFS_DEBUG_GUESTURES, false));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (this.mDelayedSetCenterZoom != null) {
            // setCenterZoom does not work in onCreate() because getHeight() and getWidth() return 0;
            // initial center must be set later when getHeight() and getWith() are set (i.e. in onWindowFocusChanged()).
            // see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
            this.mDelayedSetCenterZoom.execute("onWindowFocusChanged()", mMapView);
            this.mDelayedSetCenterZoom = null; // donot call it again

        }
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();

        if (LOLLIPOP) {
            inflater.inflate(R.menu.location_map_viewer_21, menu);
        }
        inflater.inflate(R.menu.location_map_viewer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = this.bookmarkListOverlay.isBookmarkListVisible();

        if (cmdShowMenu != null) {
            cmdShowMenu.setVisibility((drawerOpen) ? View.INVISIBLE : View.VISIBLE );
        }

        menu.findItem(R.id.cmd_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.cmd_help).setVisible(!drawerOpen);
        if (LOLLIPOP) {
            menu.findItem(R.id.cmd_open_file).setVisible(!drawerOpen);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /** called by options/actionBar-menu */
    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        if (onMenuItemClick(item)) {
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /** called by popup-menu */
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cmd_help:
                this.showDialog(R.id.cmd_help);
                return true;

            case R.id.cmd_open_file:
                onOpenDir(item.getTitle());
                return true;
                
            case R.id.cmd_settings: {
                // set current xyz to prefs so they can be displayed/modified in the settings
                final IGeoPoint mapCenter = mMapView.getMapCenter();

                final SharedPreferences.Editor edit = getPrefs().edit();
                edit.putString(PREFS_CURRENT_ZOOMLEVEL, "" + (int) mMapView.getZoomLevelDouble());
                edit.putString(PREFS_CURRENT_NORTH, LAT_LON2TEXT.format(mapCenter.getLatitude()));
                edit.putString(PREFS_CURRENT_EAST, LAT_LON2TEXT.format(mapCenter.getLongitude()));
                edit.commit();
                SettingsActivity.show(this, R.id.cmd_settings);
                return true;
            }
        }
        return false;
    }

    private void onOpenDir(CharSequence title) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_TITLE, title);

        this.startActivityForResult(intent, REQUEST_ID_PICK_KML_DIR);
    }

    /** implements interface BookmarkListOverlay.AdditionalPoints */
    public GeoBmpDto[] getAdditionalPoints() {
        GeoPoint gps = (this.mLocationOverlay != null) ? this.mLocationOverlay.getMyLocation() : null;

        GeoBmpDto gpsWindow = null;
        if (gps != null) {
            gpsWindow = new GeoBmpDto();
            GeoUtil.createBookmark(gps, IGeoPointInfo.NO_ZOOM, getString(R.string.bookmark_template_gps), gpsWindow);
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.person);
            gpsWindow.setBitmap(drawable.getBitmap());

        }
        GeoBmpDto currentWindow = getCurrentAsGeoPointDto(getString(R.string.bookmark_template_current));
        return new GeoBmpDto[]{currentWindow, gpsWindow, initialWindow};
    }

    private GeoBmpDto getCurrentAsGeoPointDto(String name) {
        GeoBmpDto current = new GeoBmpDto();
        GeoUtil.createBookmark(this.mMapView.getMapCenter(), (int) this.mMapView.getZoomLevelDouble(), name, current);
        current.setBitmap(GeoUtil.createBitmapFromMapView(mMapView, GeoBmpDto.WIDTH, GeoBmpDto.HEIGHT));
        return current;
    }

    /** called if a sub-activity finishes */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        if (logger.isDebugEnabled()) logger.debug("onActivityResult(requestCode="+requestCode+", resultCode="+resultCode+", data="+data+")");
        if (requestCode == R.id.cmd_settings) {
            onSettingsResult();
        } else if (requestCode == REQUEST_ID_PICK_KML_DIR && resultCode == RESULT_OK && data != null) {
            onPickDirResult(data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onPickDirResult(Uri data) {
        final DocumentFile dir = DocumentFile.fromTreeUri(this, data);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            DocumentFile[] files = dir.listFiles();
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_singlechoice);
            if (files != null) {
                final Map<String, DocumentFile> name2file = new HashMap<>();
                for (DocumentFile file : files) {
                    String name = file.getName();
                    String nameLower = name.toLowerCase();
                    name2file.put(nameLower, file);
                    if (nameLower.endsWith(".gpx") || nameLower.endsWith(".kml") || nameLower.endsWith(".poi")) {
                        arrayAdapter.add(name);
                    }
                }

                int count = arrayAdapter.getCount();
                if (count == 1) {
                    onPickFileResult(dir, arrayAdapter.getItem(0), name2file);
                } else if (count > 1) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(LocationMapViewer.this);
                    builderSingle.setIcon(R.drawable.ic_launcher);
                    builderSingle.setTitle(R.string.title_open_file);
                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String strName = arrayAdapter.getItem(which);
                            onPickFileResult(dir, strName, name2file);
                            dialog.dismiss();
                        }
                    });
                    builderSingle.show();
                }
            }
        }
    }

    private void onPickFileResult(final DocumentFile dir, String name, final Map<String, DocumentFile> name2file) {
        GeoPointDto geoPointFromIntent = getGeoPointDtoFromIntent(this.getIntent());

        final IGeoInfoHandler pointCollector = createPointCollector(geoPointFromIntent);
        final IGeoInfoHandler pointConverter = new IGeoInfoHandler() {
            @Override
            public boolean onGeoInfo(IGeoPointInfo aGeoPoint) {
                String symbol = convertSymbol(aGeoPoint, dir, name2file);
                if (symbol != null) {
                    ((GeoPointDto) aGeoPoint).setSymbol(symbol);
                }
                pointCollector.onGeoInfo(aGeoPoint);
                return true;
            }
        };

        loadGeoPointDtos(name2file.get(name.toLowerCase()), pointConverter);

        zoomTo(geoPointFromIntent, pointConverter);
    }

    private String convertSymbol(final IGeoPointInfo aGeoPoint,final DocumentFile dir, final Map<String, DocumentFile> name2file) {
        String symbol = aGeoPoint != null ? aGeoPoint.getSymbol() : null;
        if (symbol != null && !symbol.contains(":") && symbol.contains(".")) {
            symbol = symbol.toLowerCase();
            DocumentFile doc = name2file.get(symbol);
            if (doc == null && symbol.contains("/")) {
                doc = addFiles(dir, symbol.split("/"), name2file);
            }
            if (doc != null) return doc.getUri().toString();
        }
        return null;
    }

    private DocumentFile addFiles(DocumentFile dir, String[] pathElements, Map<String, DocumentFile> name2file) {
        DocumentFile currentdir = dir;
        StringBuilder path = new StringBuilder();
        DocumentFile doc = null;
        int last = pathElements.length - 1;
        for (int i = 0; i <= last; i++) {
            if (path.length() > 0) path.append("/");
            String parentPath = path.toString();
            path.append(pathElements[i]);
            String pathLowerCase = path.toString();
            doc = name2file.get(pathLowerCase);
            if (doc == null && i <= last && currentdir != null && currentdir.isDirectory()) {
                DocumentFile[] children = currentdir.listFiles();
                if (children != null) {
                    for (DocumentFile child : children) {
                        name2file.put(parentPath  + child.getName().toLowerCase(), child);
                    }
                    doc = name2file.get(pathLowerCase);
                }
            }
            currentdir = doc;
        }
        return doc;
    }

    private void onSettingsResult() {
        RestoreXYZ(); // onActivityResult is called before onResume(): maxe shure last xyz are restored.

        // apply xyz changes from settings back to view
        int changes = setZoom(getPrefs().getString(PREFS_CURRENT_ZOOMLEVEL, "").trim())
                + setCenter(getPrefs().getString(PREFS_CURRENT_NORTH, "").trim(), getPrefs().getString(PREFS_CURRENT_EAST, "").trim());

        if (changes > 0) {
            saveLastXYZ(); // otherwhise onResume() would overwrite the new values
        }

        if (super.isShowLocation() && this.permissionGrantedGps == null) {
            // settings: show location has just been enabled
            super.checkPermissions();
        }
        initShowLocation();
    }

    private int setCenter(String newNorthString, String newEastString) {
        if ((newNorthString.length() > 0) && (newEastString.length() > 0)) {
            try {
                final Number east = LAT_LON2TEXT.parse(newEastString);
                final Number north = LAT_LON2TEXT.parse(newNorthString);
                final IGeoPoint newCenter = new GeoPoint(north.doubleValue(),east.doubleValue());
                final IGeoPoint oldCenter = mMapView.getMapCenter();

                if ((newCenter.getLatitude() != oldCenter.getLatitude()) || (newCenter.getLongitude() != oldCenter.getLongitude())) {
                    setDelayedCenter(newCenter);
                    return 1;
                }
            } catch (Exception ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Cannot set setCenter(n={},e={}) => {}",
                            newNorthString, newEastString, ex.getMessage());
                }
            }
        }
        return 0;
    }

    private int setZoom(String newZoomString) {
        if (newZoomString.length() > 0) {
            try {
                int newZoom = Integer.parseInt(getPrefs().getString(PREFS_CURRENT_ZOOMLEVEL, "-1"));
                if ((newZoom != -1) && (newZoom != (int) mMapView.getZoomLevelDouble())) {
                    setDelayedZoom(newZoom);
                    return 1;
                }
            } catch (Exception ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Cannot set setZoom({}) => {}",
                            newZoomString, ex.getMessage());
                }
            }
        }
        return 0;
    }

    // ===========================================================
    // Methods
    // ===========================================================

    /** Move viewport to newCenter. */
    public void setDelayedCenter(final IGeoPoint newCenter) {
        setDelayedCenterZoom("setDelayedCenter", newCenter, null, null);
    }

    /** Zoom as much as possible so that min an max are both visible. zoomLevel is calculate from mMin,mMax */
    public void setDelayedCenter(final IGeoPoint min, final IGeoPoint max) {
        setDelayedCenterZoom("setDelayedCenterMinMax", min, max, GeoPointDto.NO_ZOOM);
    }

    /** Zoom to zoomLevel. */
    public void setDelayedZoom(final int zoomLevel) {
        setDelayedCenterZoom("setDelayedZoom", null, null, zoomLevel);
    }

    /** Zoom to center / zoomLevel from bookmark. */
    public void setDelayedCenterZoom(IGeoPointInfo bookmark) {
        this.setDelayedCenterZoom("fromBookmark", GeoUtil.createOsmPoint(bookmark), null, bookmark.getZoomMin());
    }

    /** impelementation of setDelayedXXXX() */
    private void setDelayedCenterZoom(String debugContext, final IGeoPoint min, final IGeoPoint max, final Integer zoomLevel) {
        DelayedSetCenterZoom delayedSetCenterZoom = this.mDelayedSetCenterZoom;
        this.mDelayedSetCenterZoom = null;

        if (delayedSetCenterZoom == null) delayedSetCenterZoom = new DelayedSetCenterZoom();

        if (min != null) delayedSetCenterZoom.setMin(min).setMax(max);
        if (zoomLevel != null) delayedSetCenterZoom.setZoom(zoomLevel.intValue());

        // can execute immediately
        if (this.mMapView.getWidth() > 0) {
            delayedSetCenterZoom.execute(debugContext, this.mMapView);
        } else {
            // this.mMapView not fully initialized. do it later
            this.mDelayedSetCenterZoom = delayedSetCenterZoom;
        }
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Dialog result = this.bookmarkListOverlay.onCreateDialog(id);
        if (result != null) return result;

        if (id == R.id.cmd_help) {
            return AboutDialogPreference.createAboutDialog(this);
        }
        return null;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    /**
     * execute does not work in onCreate() because getHeight() and getWidth() are not calculated yet and return 0;
     * execute must be set later when getHeight() and getWith() are known (i.e. in onWindowFocusChanged()).
     * <p/>
     * see http://stackoverflow.com/questions/10411975/how-to-get-the-width-and-height-of-an-image-view-in-android/10412209#10412209
     */
    private class DelayedSetCenterZoom {
        /** Coordinate, where the delayed setCenter schould go. null: no setCenter */
        private IGeoPoint mMin = null;

        /** Coordinate, where the delayed setCenter schould go. Not null: set center in the middle between mMin and mMax. null: set center to mMin */
        private IGeoPoint mMax = null; //

        /** zoomLevel, where the delayed setZoom schould go.  GeoPointDto.NO_ZOOM with set mMin and mMax: calculate from mMin,mMax.
         * GeoPointDto.NO_ZOOM without mMax: do not zoom. */
        private double mZoomLevel = GeoPointDto.NO_ZOOM;

        DelayedSetCenterZoom() {}

        DelayedSetCenterZoom(final GeoPoint min, final GeoPoint max, final int zoomLevel) {
            setMin(min).setMax(max).setZoom(zoomLevel);
        }

        public DelayedSetCenterZoom setZoom(int zoomLevel) {
            mZoomLevel = zoomLevel;
            return this;
        }

        public DelayedSetCenterZoom setMin(IGeoPoint min) {
            mMin = min;
            return this;
        }

        public DelayedSetCenterZoom setMax(IGeoPoint max) {
            mMax = max;
            return this;
        }

        /** calculate min/max from all Markers in all overlaysWithMarkers */
        public DelayedSetCenterZoom(List<? extends Overlay> overlaysWithMarkers, int zoomLevel) {
            if (overlaysWithMarkers.size() > 0) {
                Marker first = (Marker) overlaysWithMarkers.get(0);
                GeoPoint min = new GeoPoint(first.getPosition().clone());
                GeoPoint max = null;
                if (overlaysWithMarkers.size() > 1) {
                    max = min.clone();
                    for (Overlay item : overlaysWithMarkers) {
                        getMinMax(min, max, ((Marker) item).getPosition());
                    }
                }
                mMin = min;
                mMax = max;
            }
            mZoomLevel = zoomLevel;
        }

        /** calculate min/max from all Markers in all overlaysWithMarkers */
        public DelayedSetCenterZoom(ArrayList<OverlayItem> overlaysWithMarkers, int zoomLevel) {
            if (overlaysWithMarkers.size() > 0) {
                OverlayItem first = overlaysWithMarkers.get(0);
                final IGeoPoint firstPoint = first.getPoint();
                GeoPoint min = new GeoPoint(firstPoint.getLatitudeE6(), firstPoint.getLongitudeE6());
                GeoPoint max = null;
                if (overlaysWithMarkers.size() > 1) {
                    max = min.clone();
                    for (OverlayItem item : overlaysWithMarkers) {
                        getMinMax(min, max, item.getPoint());
                    }
                }
                mMin = min;
                mMax = max;
            }
            mZoomLevel = zoomLevel;
        }

        /** Helper to find min/max in a range */
        private void getMinMax(GeoPoint resultMin, GeoPoint resultMax, IGeoPoint candidate) {
            if (resultMin.getLatitude() > candidate.getLatitude()) {
                resultMin.setLatitude(candidate.getLatitude());
            }
            if (resultMin.getLongitude() > candidate.getLongitude()) {
                resultMin.setLongitude(candidate.getLongitude());
            }
            if (resultMax.getLatitude() < candidate.getLatitude()) {
                resultMax.setLatitude(candidate.getLatitude());
            }
            if (resultMax.getLongitude() < candidate.getLongitude()) {
                resultMax.setLongitude(candidate.getLongitude());
            }
        }

        /** the delayed execute */
        public void execute(String debugContext, MapView mapView) {
            ZoomUtil.zoomTo(mapView, mZoomLevel, mMin, mMax);

            if (logger.isDebugEnabled()) {
                logger.debug("DelayedSetCenterZoom.execute({}: ({}) .. ({}),z={}) => ({}), z={} => {}",
                        debugContext,
                        mMin, mMax, mZoomLevel, mapView.getMapCenter(), (int) mapView.getZoomLevelDouble(), getStatusForDebug());
            }
        }

     }
}
