package de.k3b.android.locationMapViewer.geo;

import de.k3b.geo.io.gpx.GpxReader;

/**
 * Created by EVE on 07.01.2015.
 */
public class OsmGpxLocationReader extends GpxReader<OsmPoiGeoPointDto> {
    @Override
    public OsmPoiGeoPointDto getNewInstance() {
        return new OsmPoiGeoPointDto();
    }
}