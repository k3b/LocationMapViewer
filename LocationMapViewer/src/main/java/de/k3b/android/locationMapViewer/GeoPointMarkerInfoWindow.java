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

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//import org.osmdroid.bonuspack.overlays.Marker;
//import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import de.k3b.geo.api.GeoPointDto;

/**
* Created by k3b on 17.04.2015.
*/ //7. Customizing the bubble behaviour
class GeoPointMarkerInfoWindow extends MarkerInfoWindow implements View.OnClickListener {
    private GeoPointDto mSelectedPoi = null;

    public GeoPointMarkerInfoWindow(MapView mapView) {
        super(R.layout.bubble_geo_point_dto, mapView);
    }

    // View.OnClickListener
    public void onClick(View view) {
        if ((mSelectedPoi != null) && (mSelectedPoi.getLink() != null)) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedPoi.getLink()));
            view.getContext().startActivity(myIntent);
        }
    }

    @Override
    public void onOpen(Object item) {
        super.onOpen(item);
        Marker marker = (Marker) item;
        mSelectedPoi = (GeoPointDto) marker.getRelatedObject();
        if (mSelectedPoi != null) {
            setText(R.id.bubble_title, mSelectedPoi.getName());
            setText(R.id.bubble_description, mSelectedPoi.getDescription());

            if (mSelectedPoi.getLink() != null) {
                Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo));
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(this);
            }

            // R.id.bubble_image not implemented yet
            // R.id.bubble_subdescription not implemented
/* !!!
        //8. put thumbnail image in bubble, fetching the thumbnail in background:
        if (mSelectedPoi.mThumbnailPath != null){
            ImageView imageView = (ImageView)mView.findViewById(R.id.bubble_image);
            mSelectedPoi.fetchThumbnailOnThread(imageView);
        }
        */
        }
    }

    private void setText(int resourceId, String value) {
        if (value != null) {
            TextView tv = (TextView) mView.findViewById(resourceId);
            tv.setVisibility(View.VISIBLE);
            tv.setText(value);
        }
    }
}
