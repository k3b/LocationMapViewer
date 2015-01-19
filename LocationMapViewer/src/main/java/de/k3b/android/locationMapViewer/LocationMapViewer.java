package de.k3b.android.locationMapViewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;

import de.k3b.android.locationMapViewer.constants.Constants;
import de.k3b.android.locationMapViewer.geo.OsmPoiGeoPointDto;
import de.k3b.android.locationMapViewer.geo.POIOverlayItem;
import de.k3b.geo.io.GeoUri;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import org.osmdroid.ResourceProxy.bitmap;

/**
 * minimal android cardviewer app for Android 2.1 (Eclair, API 7).<br/>
 * no support for actionbar and fragments.<br/>
 * The code is based on "org.osmdroid.samples.SampleWithMinimapItemizedoverlay in DemoApp OpenStreetMapViewer"
 */
public class LocationMapViewer extends Activity  implements Constants {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MENU_SETTINGS_ID = Menu.FIRST;
    private static final int MENU_ZOOMIN_ID = Menu.FIRST + 1;
    private static final int MENU_ZOOMOUT_ID = MENU_ZOOMIN_ID + 2;

    // ===========================================================
    // Fields
    // ===========================================================

    /** used to remeber last gui settings when app is closed or rotated */
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private ItemizedOverlay<OverlayItem> mPointOfInterestOverlay;
    private MyLocationNewOverlay mLocationOverlay;
    private MinimapOverlay mMiniMapOverlay;
    /** where images/icons are loaded from */
    private ResourceProxy mResourceProxy;

    // ===========================================================
    // Constructors
    // ===========================================================
    /** Called when the activity is first created. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Intent whatToDo = this.getIntent();
        final Uri uri = (whatToDo != null) ? whatToDo.getData() : null;
        String uriAsString = (uri != null) ? uri.toString() : null;
        OsmPoiGeoPointDto targetPoint = null;
        if (uriAsString != null) {
            Toast.makeText(this, getString(R.string.app_name) + ": received  " + uriAsString, Toast.LENGTH_LONG).show();
            GeoUri parser = new GeoUri(GeoUri.OPT_PARSE_INFER_MISSING);
            targetPoint = (OsmPoiGeoPointDto) parser.fromUri(uriAsString, new OsmPoiGeoPointDto());
        }

        mResourceProxy = new ResourceProxyImpl(getApplicationContext());

        this.setContentView(R.layout.mapview);

        mMapView = (MapView) this.findViewById(R.id.mapview);

        final List<Overlay> overlays = this.mMapView.getOverlays();

        final ArrayList<OverlayItem> items = loadPointOfInterests(targetPoint);

        createPointOfInterestOverlay(overlays, items);

        createMyLocationOverlay(overlays);

        createMiniMapOverlay(overlays);

        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);

    }

    /** Create some Hardcoded Markers on some cities.
     * @param targetPoint*/
    private ArrayList<OverlayItem> loadPointOfInterests(OsmPoiGeoPointDto targetPoint) {
        final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        if (targetPoint != null) {
            items.add(new POIOverlayItem(targetPoint, getResources().getDrawable(R.drawable.marker_red), null));
        }
        items.add(new OverlayItem("Hannover", "SampleDescription", new GeoPoint(52370816, 9735936)));
        items.add(new OverlayItem("Berlin", "SampleDescription", new GeoPoint(52518333, 13408333)));
        items.add(new OverlayItem("Washington", "SampleDescription", new GeoPoint(38895000, -77036667)));
        items.add(new OverlayItem("San Francisco", "SampleDescription", new GeoPoint(37779300, -122419200)));
        items.add(new OverlayItem("Tolaga Bay", "SampleDescription", new GeoPoint(-38371000, 178298000)));
        return items;
    }

    private void createPointOfInterestOverlay(List<Overlay> overlays, ArrayList<OverlayItem> items) {
    /* OnTapListener for the Markers, shows a simple Toast. */
        this.mPointOfInterestOverlay = new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(
                                LocationMapViewer.this,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(
                                LocationMapViewer.this,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, mResourceProxy);
        overlays.add(this.mPointOfInterestOverlay);
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
    public void onPause()
    {
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putInt(PREFS_SCROLL_X, mMapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mMapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mMapView.getZoomLevel());
        edit.putBoolean(PREFS_SHOW_LOCATION, mLocationOverlay.isMyLocationEnabled());
        edit.putBoolean(PREFS_SHOW_MINIMAP, mMiniMapOverlay.isEnabled());
        edit.commit();

        this.mLocationOverlay.disableMyLocation();

        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
}
