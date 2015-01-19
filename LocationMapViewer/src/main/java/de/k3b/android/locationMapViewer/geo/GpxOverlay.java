package de.k3b.android.locationMapViewer.geo;

import android.content.Context;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EVE on 07.01.2015.
 */
public class GpxOverlay extends ItemizedOverlayWithFocus<POIOverlayItem> {

    public GpxOverlay(final Context context, final List<POIOverlayItem> items, ResourceProxy aResourceProxy) {
        super(items,
                new ItemizedIconOverlay.OnItemGestureListener<POIOverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final POIOverlayItem item) {
                        Toast.makeText(
                                context,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final POIOverlayItem item) {
                        Toast.makeText(
                                context,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, aResourceProxy);
        this.setFocusItemsOnTap(true);
        this.setFocusedItem(0);
    }

    /**
     * @param context
     * @param mMapView
     * @param gpxFile
     * @return null if file not found or empty or invalid
     */
    public static GpxOverlay addGpxOverlay(final Context context, MapView mMapView, File gpxFile) {
        try {
            List<OsmPoiGeoPointDto> gpxLocations = new OsmGpxLocationReader().readTrack(new InputSource(new FileInputStream(gpxFile)));

            if ((gpxLocations != null) && (gpxLocations.size() > 0)) {
                ResourceProxy resourceProxy = new DefaultResourceProxyImpl(context);
                final List<POIOverlayItem> overlayItems = new ArrayList<POIOverlayItem>(gpxLocations.size());
                for (OsmPoiGeoPointDto loc : gpxLocations) {
                    overlayItems.add(new POIOverlayItem(loc,
                            resourceProxy.getDrawable(ResourceProxy.bitmap.marker_default), OverlayItem.HotspotPlace.CENTER));

                }

                GpxOverlay mMyLocationOverlay = new GpxOverlay(context, overlayItems, resourceProxy);
                mMapView.getOverlays().add(mMyLocationOverlay);

/* !!!!!
                ///todo einbauen und dann focus auf overlay


                // Zoom and center on the focused item.
                mMapView.getController().setZoom(5);
                IGeoPoint geoPoint = mMyLocationOverlay.getFocusedItem().getPoint();
                mMapView.getController().animateTo(geoPoint);
*/

                mMapView.getController();


                return mMyLocationOverlay;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
