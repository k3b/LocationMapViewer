package de.k3b.geo.io.gpx;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;

import de.k3b.geo.api.GeoPointDto;

/**
 * Created by EVE on 14.06.2014.
 */
public class GpxReaderTest {
    String xmlMinimal = "<trkpt lat='53.1099972' lon='8.7178206'><time>2014-12-19T21:13:21Z</time><name>262:3:562:54989</name><desc>type: cell, accuracy: 1640, confidence: 75</desc></trkpt>\n";

    @Test
    public void parseFormatTest() throws IOException {
        GpxLocationReader reader = new GpxLocationReader();
        GeoPointDto location = reader.readTrack(new InputSource(new StringReader(xmlMinimal))).get(0);
        String formatted = GpxFormatter.toGpx(new StringBuffer(), location, location.getDescription()).toString();

        Assert.assertEquals(xmlMinimal, formatted);
    }
}