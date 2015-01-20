/*
 * Copyright (C) 2015 k3b
 *
 * This file is part of de.k3b.android.LocationMapViewer (https://github.com/k3b/LocationMapViewer/) .
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
package de.k3b.geo.io.gpx;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.k3b.geo.api.ILocation;

/**
 * Formats {@link de.k3b.geo.api.GeoPointDto}-s or {@link de.k3b.geo.api.ILocation}-s as gpx-xml.<br/>
 *
 * Created by k3b on 07.01.2015.
 */
public class GpxFormatter {
    static final DateFormat TIME_FORMAT
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static StringBuffer toGpx(StringBuffer result, ILocation location, String description) {
        return toGpx(result, location.getLatitude(), location.getLongitude(), location.getTimeOfMeasurement(), location.toString(),description);
    }

    private static StringBuffer toGpx(StringBuffer result, double latitude, double longitude, Date timeOfMeasurement, String name, String description) {
        result.append("<trkpt lat='")
                .append(latitude)
                .append("' lon='")
                .append(longitude)
                .append("'>");
        if (timeOfMeasurement != null) {
            addElement(result, "time", TIME_FORMAT.format(timeOfMeasurement).toString());
        }
        if (name != null) {
            addElement(result, "name", name);
        }
        if (description != null) {
            addElement(result, "desc", description);
        }
        result.append("</trkpt>\n");
        return result;
    }

    private static void addElement(StringBuffer result, String name, String value) {
        result.append("<").append(name).append(">").append(value).append("</").append(name).append(">");
    }
}
