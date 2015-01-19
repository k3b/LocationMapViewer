package de.k3b.geo.api;

import java.util.Date;

/**
 * Created by EVE on 13.01.2015.
 */
public interface IGeoPoint {
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
     * NO_ZOOM means no lower bound. */
    int getZoomMin();

    /** Optional in geo data as filter criteria: this item is only shown if current zoom-level is <= this value. NO_ZOOM means no upper bound. */
    int getZoomMax();

    /** Optional: Date when the measurement was taken. Null if unknown.<br/>
     * This may be shown in a map as an alternative label.
     * or used as a filter to include only geopoints of a certain date range */
    Date getTimeOfMeasurement();

    /** Optional: Short non-unique text used as marker label. Null if not set. */
    String getName();

    /** Optional: Detailed description of the point displayed in popup on long-click . Null if not set. */
    String getDescription();

    /** Optional: if not null: a unique id for this item. */
    String getId();

    /** Optional: if not null: uri belonging to this item. */
    String getUri();
}
