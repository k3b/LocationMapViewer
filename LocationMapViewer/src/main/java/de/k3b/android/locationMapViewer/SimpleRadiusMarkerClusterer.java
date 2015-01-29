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
import android.graphics.drawable.BitmapDrawable;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
import org.osmdroid.views.MapView;

/**
 * Created by k3b on 29.01.2015.
 */
public class SimpleRadiusMarkerClusterer extends RadiusMarkerClusterer {
    private BitmapDrawable mClusterIconDrawable;

    public SimpleRadiusMarkerClusterer(Context ctx, BitmapDrawable aClusterIconDrawable) {
        super(ctx);
        mClusterIconDrawable = aClusterIconDrawable;
    }

    /**
     * returns a static clusterMarker
     */
    protected BitmapDrawable getClusterMarkerIcon(StaticCluster cluster, MapView mapView) {
        return this.mClusterIconDrawable;
    }
}