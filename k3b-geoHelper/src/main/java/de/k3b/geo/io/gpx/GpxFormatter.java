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
