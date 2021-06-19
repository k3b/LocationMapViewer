/*
 * Copyright (c) 2015-2021 by k3b.
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

package de.k3b.geo.geobmp;

import java.io.Serializable;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoFormatter;

/**
 * a GeoPoint with a bitmap.
 *
 * Created by k3b on 24.03.2015.
 * @param <BITMAP> either android.graphics.Bitmap or j2se Bitmap
 */
public class GeoBmpDto<BITMAP> extends GeoPointDto implements Serializable {
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    /** a bitmap representing the GeoPoint */
    private BITMAP bitmap = null;

    public GeoBmpDto() {}

    public GeoBmpDto(IGeoPointInfo src) {
        super(src);
    }
    /** a bitmap representing the GeoPoint */
    public BITMAP getBitmap() {
        return bitmap;
    }

    public GeoBmpDto setBitmap(BITMAP bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    /** formatting helper: */
    public String getSummary() {
        return " (" +
                GeoFormatter.formatLatLon(this.getLatitude()) + "/" +
                GeoFormatter.formatLatLon(this.getLongitude())+ ") z=" + GeoFormatter.formatZoom(this.getZoomMin());
    }

}
