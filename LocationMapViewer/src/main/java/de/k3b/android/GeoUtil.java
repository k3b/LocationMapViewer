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

package de.k3b.android;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Created by k3b on 18.03.2015.
 */
public class GeoUtil {
    public static GeoPointDto createFavorite(IGeoPoint center, int zoomLevel, String name) {
        return new GeoPointDto()
                .setLatitude(center.getLatitude())
                .setLongitude(center.getLongitude())
                .setZoomMin(zoomLevel)
                .setName(name);
    }

    public static IGeoPoint createOsmPoint(IGeoPointInfo geoInfo) {
        return  new GeoPoint(geoInfo.getLatitude(), geoInfo.getLongitude());
    }
}
