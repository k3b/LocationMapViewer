/**
 * This Package defines Android independant code to handle *.gpx files with locations/trackpoints.
 *
 * <ul>
 *     <li>{@link de.k3b.geo.api.GeoPointDto}:
 *          a location or trackpoint that can be represented in a gpx file.</li>
 *     <li>{@link de.k3b.geo.io.gpx.GpxFormatter}:
 *          Formats {@link de.k3b.geo.api.GeoPointDto}-s or {@link de.k3b.geo.api.ILocation}-s as geo-xml.</li>
 *     <li>{@link de.k3b.geo.io.gpx.GpxReader}:
 *          reads {@link de.k3b.geo.api.GeoPointDto} from file or stream.</li>
 * </ul>
 *
 **/
package de.k3b.geo.io.gpx;
