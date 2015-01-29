/*
 * Copyright (c) 2015 by k3b.
 *
 * This file is part of LocationMapViewer.
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.BasicInfoWindow;
import org.osmdroid.bonuspack.overlays.GroundOverlay;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerDragListener;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * This is the implementation of OSMBonusPack tutorials. 
 * Sections of code can be commented/uncommented depending on the progress in the tutorials. 
 * see http://code.google.com/p/osmbonuspack/
 * @author M.Kergall
 *
 */
public class BonusTutorialActivity extends Activity implements MapEventsReceiver {

	MapView map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		
		//Introduction
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bonus_tutorial);
		map = (MapView) findViewById(R.id.map);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);
		GeoPoint startPoint = new GeoPoint(48.13, -1.63);
		IMapController mapController = map.getController();
		mapController.setZoom(10);
		mapController.setCenter(startPoint);

        Marker startMarker = createMarker(startPoint, "Start point");
        // startMarker.setImage(getResources().getDrawable(R.drawable.marker_yellow));
		//startMarker.setIcon(getResources().getDrawable(R.drawable.marker_kml_point).mutate());
		//startMarker.setImage(getResources().getDrawable(R.drawable.ic_launcher));
		//startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble_black, map));
		startMarker.setDraggable(true);
		startMarker.setOnMarkerDragListener(new OnMarkerDragListenerDrawer());
		map.getOverlays().add(startMarker);
		
		//FolderOverlay poiMarkers = new FolderOverlay(this);
		//10. Marker Clustering
        Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_red);
		RadiusMarkerClusterer poiMarkers = new SimpleRadiusMarkerClusterer(this, (BitmapDrawable) clusterIconD);
		//end of 10.
		//11. Customizing the clusters design
		poiMarkers.getTextPaint().setTextSize(12.0f);
		poiMarkers.mAnchorV = Marker.ANCHOR_BOTTOM;
		poiMarkers.mTextAnchorU = 0.70f;
		poiMarkers.mTextAnchorV = 0.27f;
		//end of 11.
		map.getOverlays().add(poiMarkers);

        poiMarkers.add(createMarker(new GeoPoint(48.131, -1.631), "point 1"));
        poiMarkers.add(createMarker(new GeoPoint(48.132, -1.632), "point 2"));
        poiMarkers.add(createMarker(new GeoPoint(48.133, -1.633), "point 3"));
        poiMarkers.add(createMarker(new GeoPoint(48.134, -1.634), "point 4"));

        Drawable poiIcon = getResources().getDrawable(R.drawable.marker_yellow);

		//16. Handling Map events
		MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
		map.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays
	}

    private Marker createMarker(GeoPoint startPoint, String title) {
        //0. Using the Marker overlay
        Marker startMarker = new Marker(map);

        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(title);
        return startMarker;
    }


    //0. Using the Marker and Polyline overlays - advanced options
	class OnMarkerDragListenerDrawer implements OnMarkerDragListener {
		ArrayList<GeoPoint> mTrace;
		Polyline mPolyline;
		OnMarkerDragListenerDrawer() {
			mTrace = new ArrayList<GeoPoint>(100);
			mPolyline = new Polyline(map.getContext());
			mPolyline.setColor(0xAA0000FF);
			mPolyline.setWidth(2.0f);
			mPolyline.setGeodesic(true);
			map.getOverlays().add(mPolyline);
		}
		@Override public void onMarkerDrag(Marker marker) {
			//mTrace.add(marker.getPosition());
		}
		@Override public void onMarkerDragEnd(Marker marker) {
			mTrace.add(marker.getPosition());
			mPolyline.setPoints(mTrace);
			map.invalidate();
		}
		@Override public void onMarkerDragStart(Marker marker) {
			//mTrace.add(marker.getPosition());
		}
	}
	
	//16. Handling Map events
	@Override public boolean singleTapConfirmedHelper(GeoPoint p) {
		Toast.makeText(this, "Tap on ("+p.getLatitude()+","+p.getLongitude()+")", Toast.LENGTH_SHORT).show();
		InfoWindow.closeAllInfoWindowsOn(map);
		return true;
	}
	float mGroundOverlayBearing = 0.0f;
	@Override public boolean longPressHelper(GeoPoint p) {
		Toast.makeText(this, "Long press", Toast.LENGTH_SHORT).show();

		return false;
	}
}
