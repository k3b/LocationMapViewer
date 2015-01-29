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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.bonuspack.overlays.InfoWindow;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;

/**
 * Created by k3b on 29.01.2015.
 */
public class RadiusMarkerClustererWithInfo extends RadiusMarkerClusterer {
    public RadiusMarkerClustererWithInfo(Context ctx) {
        super(ctx);
    }

    @Override public Marker buildClusterMarker(StaticCluster cluster, MapView mapView) {
        Marker m = new Marker(mapView);
        m.setPosition(cluster.getPosition());

        final InfoWindow infoWindow = (cluster.getSize() > 0) ? cluster.getItem(0).getInfoWindow() : null;
        m.setInfoWindow(infoWindow);
        m.setAnchor(mAnchorU, mAnchorV);

        final BitmapDrawable icon = getClusterMarkerIcon("" + cluster.getSize(), mapView);
        m.setIcon(icon);

        return m;
    }

    /** calculates the icon for the clusterMarker (with text = number of items) */
    protected BitmapDrawable getClusterMarkerIcon(String text, MapView mapView) {

        // this works for android 4.4 but not thows an exception in android 2.1
        // Bitmap finalIcon = Bitmap.createBitmap(mClusterIcon.getWidth(), mClusterIcon.getHeight(), mClusterIcon.getConfig());
        // this works with android2.1
        Bitmap finalIcon = Bitmap.createBitmap(mClusterIcon.getWidth(), mClusterIcon.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas iconCanvas = new Canvas(finalIcon);
        iconCanvas.drawBitmap(mClusterIcon, 0, 0, null);
        int textHeight = (int) (mTextPaint.descent() + mTextPaint.ascent());
        iconCanvas.drawText(text,
                mTextAnchorU * finalIcon.getWidth(),
                mTextAnchorV * finalIcon.getHeight() - textHeight / 2,
                mTextPaint);
        return new BitmapDrawable(mapView.getContext().getResources(), finalIcon);
    }
}