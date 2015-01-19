package de.k3b.android.locationMapViewer.geo;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;


/**
 * Created by k3b on 07.01.2015.
 */
public class POIOverlayItem extends OverlayItem {

    public POIOverlayItem(OsmPoiGeoPointDto aGeoPoint,
                          Drawable aMarker, HotspotPlace aHotspotPlace) {
        super(aGeoPoint.getId(), aGeoPoint.getName(), aGeoPoint.getDescription(), new GeoPoint(aGeoPoint.getLatitude(), aGeoPoint.getLongitude()));
        this.setMarker(aMarker);
        this.setMarkerHotspot(aHotspotPlace);
    }

    public void draw(Canvas canvas) {
        //
    }

}
