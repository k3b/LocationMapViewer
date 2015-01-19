package de.k3b.android.locationMapViewer.geo;

import org.osmdroid.api.IGeoPoint;

import de.k3b.geo.api.GeoPointDto;

/**
 * A GpxLocationn that is compatible with OsmDroid
 * Created by EVE on 07.01.2015.
 */
public class OsmPoiGeoPointDto extends GeoPointDto implements IGeoPoint {
    public static final int NO_E6VALUE = Integer.MIN_VALUE;
    private int mLatitudeE6 = NO_E6VALUE;
    private int mLongitudeE6 = NO_E6VALUE;

    @Override
    public OsmPoiGeoPointDto setLatitude(double latitude) {
        super.setLatitude(latitude);
        this.mLatitudeE6 = (int) (latitude * 1E6);
        return this;
    }

    @Override
    public OsmPoiGeoPointDto setLongitude(double longitude) {
        super.setLongitude(longitude);
        this.mLongitudeE6 = (int) (longitude * 1E6);
        return this;
    }

    @Override
    public int getLatitudeE6() {
        return this.mLatitudeE6;
    }

    @Override
    public int getLongitudeE6() {
        return this.mLongitudeE6;
    }

    @Override public void clear() {
        super.clear();
        mLatitudeE6 = NO_E6VALUE;
        mLongitudeE6 = NO_E6VALUE;
    }
}
