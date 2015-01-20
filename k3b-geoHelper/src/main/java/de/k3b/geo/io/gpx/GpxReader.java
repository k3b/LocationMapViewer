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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.k3b.geo.api.GeoPointDto;

/**
 * Reads {@link de.k3b.geo.api.GeoPointDto} from gpx file or stream.<br/>
 *
 * inspired by http://stackoverflow.com/questions/672454/how-to-parse-gpx-files-with-saxreader
 */
public class GpxReader extends DefaultHandler {
    /** if not null this instance is cleared and then reused for every new gpx found */
    private final GeoPointDto mReuse;
    private List<GeoPointDto> track;
    private StringBuffer buf = new StringBuffer();
    private GeoPointDto current;

    /**
     * Creates a new GpxReader
     * @param reuse if not null this instance is cleared and then reused for every new gpx found
     */
    public GpxReader(final GeoPointDto reuse) {
        this.mReuse = reuse;
    }

    /** returns an instance of an empty {@link de.k3b.geo.api.GeoPointDto} */
    protected GeoPointDto newInstance() {
        if (mReuse != null) return mReuse.clear();
        return new GeoPointDto();
    }

    public List<GeoPointDto> readTrack(InputSource in) throws IOException {
        try {
            track = new ArrayList<GeoPointDto>();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            GpxReader reader = this;
            parser.parse(in, reader);
            return track;
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        buf.setLength(0);
        if (qName.equals("trkpt")) {
            current = this.newInstance();
            current.setLatitude(Double.parseDouble(attributes.getValue("lat")));
            current.setLongitude(Double.parseDouble(attributes.getValue("lon")));
        }
    }

    // gpx//trkpt/time|name|desc|@lat|@lon
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("trkpt")) {
            track.add(current);
            current = null;
        } else if (current != null) {
            if (qName.equals("name")) {
                current.setName(buf.toString());
            } else if (qName.equals("desc")) {
                current.setDescription(buf.toString());
            } else if (qName.equals("time")) {
                try {
                    current.setTimeOfMeasurement(GpxFormatter.TIME_FORMAT.parse(buf.toString()));
                } catch (ParseException e) {
                    throw new SAXException("Invalid time " + buf.toString());
                }
            }
        }
    }

    @Override
    public void characters(char[] chars, int start, int length)
            throws SAXException {
        buf.append(chars, start, length);
    }
}
