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

package de.k3b.geo.api;

import java.util.Date;

/**
 * A location or trackpoint that can be displayed in a locationmap.<br/>
 * <p/>
 * Created by k3b on 13.01.2015.
 */
public interface IGeoPointInfo extends ILocation, Cloneable  {
    public static final double NO_LAT_LON = Double.MIN_VALUE;
    public static final int NO_ZOOM = -1;

    /** Mandatory: Latitude, in degrees north. <br/>
     * In show view: navigate map to this location.<br/>
     * In geo data: map display data.<br/>
     * NO_LAT_LON if not set. */
    double getLatitude();

    /** Mandatory: Longitude, in degrees east.  <br/>
     * In show view: navigate map to this location.<br/>
     * In geo data: map display data.<br/>
     * NO_LAT_LON if not set. */
    double getLongitude();

    /** Optional:
     * In show view: navigate map to this zoom level.<br/>
     * In geo data: filter - this item is only shown if current zoom-level is >= this value.<br/>
     * NO_LAT_LON if not set.<br/>
     * NO_ZOOM means no lower bound.
     * persistet in uri as ...&z=4 */
    int getZoomMin();

    /** Optional in geo data as filter criteria: this item is only shown
     * if current zoom-level is <= this value. NO_ZOOM means no upper bound.
     * persistet in uri as ...&z2=6 */
    int getZoomMax();

    /** Optional: Date when the measurement was taken. Null if unknown.<br/>
     * This may be shown in a map as an alternative label.
     * or used as a filter to include only geopoints of a certain date range.
     * persistet in uri as ...&t=2015-03-24T15:39:52z  */
    Date getTimeOfMeasurement();

    /** Optional: Short non-unique text used as marker label. Null if not set.
     * persistet in uri as ?q=...(name) */
    String getName();

    /** Optional: Detailed description of the point displayed in popup on long-click .
     * Null if not set.
     * persistet in uri as ...&d=someDescription */
    String getDescription();

    /** Optional: if not null: a unique id for this item.
     * persistet in uri as ...&id=4711 */
    String getId();

    /** Optional: if not null: uri belonging to this item.
     * persistet in uri as ...&uri=file://path/to/file.jpg */
    String getUri();

    IGeoPointInfo clone();
}
