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

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;

import de.k3b.geo.api.GeoPointDto;

/**
 * Created by k3b on 14.06.2014.
 */
public class GpxReaderTest {
    String xmlMinimal = "<trkpt lat='53.1099972' lon='8.7178206'><time>2014-12-19T21:13:21Z</time><name>262:3:562:54989</name><desc>type: cell, accuracy: 1640, confidence: 75</desc></trkpt>\n";

    @Test
    public void parseFormatTest() throws IOException {
        GpxReader reader = new GpxReader(null);
        GeoPointDto location = reader.readTrack(new InputSource(new StringReader(xmlMinimal))).get(0);
        String formatted = GpxFormatter.toGpx(new StringBuffer(), location, location.getDescription()).toString();

        Assert.assertEquals(xmlMinimal, formatted);
    }
}