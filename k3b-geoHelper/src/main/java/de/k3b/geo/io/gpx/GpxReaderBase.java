/*
 * Copyright (c) 2015 by k3b.
 *
 * This file is part of LocationMapViewer.
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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Created by k3b on 20.01.2015.
 */
public class GpxReaderBase extends DefaultHandler {
    protected IGeoInfoHandler onGotNewWaypoint;

    /** if not null this instance is cleared and then reused for every new gpx found */
    protected final GeoPointDto mReuse;
    protected GeoPointDto current;
    private StringBuffer buf = new StringBuffer();

    public GpxReaderBase(final IGeoInfoHandler onGotNewWaypoint, final GeoPointDto reuse) {
        this.onGotNewWaypoint = onGotNewWaypoint;
        this.mReuse = reuse;
    }

    /** processes in and calls onGotNewWaypoint for every waypoint found */
    public void parse(InputSource in) throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(in, this);
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        }
    }

    /** returns an instance of an empty {@link de.k3b.geo.api.GeoPointDto} */
    protected GeoPointDto newInstance() {
        if (mReuse != null) return mReuse.clear();
        return new GeoPointDto();
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
            this.onGotNewWaypoint.onGeoInfo(current);
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
