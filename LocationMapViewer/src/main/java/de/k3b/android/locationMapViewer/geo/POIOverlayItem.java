package de.k3b.android.locationMapViewer.geo;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;


/**
 * Created by k3b on 07.01.2015.
 */
public class POIOverlayItem extends OverlayItem {

    public POIOverlayItem(OsmPoiGeoPointDto aGeoPoint,
                          Drawable aMarker, HotspotPlace aHotspotPlace) {
        super(aGeoPoint.getId(), aGeoPoint.getName(), aGeoPoint.getDescription(), aGeoPoint);
        this.setMarker(aMarker);
        this.setMarkerHotspot(aHotspotPlace);
    }

    public void draw(Canvas canvas) {
        //
    }

}
