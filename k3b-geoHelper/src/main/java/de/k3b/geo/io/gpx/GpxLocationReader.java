package de.k3b.geo.io.gpx;

import de.k3b.geo.api.GeoPointDto;

/**
 * Created by k3b on 07.01.2015.
 */
public class GpxLocationReader extends GpxReader<GeoPointDto> {
    @Override
    public GeoPointDto getNewInstance() {
        return new GeoPointDto();
    }
}
