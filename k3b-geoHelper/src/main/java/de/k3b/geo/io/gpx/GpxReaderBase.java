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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;

/**
 * Parser for http://www.topografix.com/GPX/1/1/ and http://www.topografix.com/GPX/1/0/
 * and a little bit of http://www.opengis.net/kml/2.2.
 *
 * This parser is not acurate: it might pick elements from wrong namespace.
 * 
 * Created by k3b on 20.01.2015.
 */
public class GpxReaderBase extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(GpxReaderBase.class);

    protected IGeoInfoHandler onGotNewWaypoint;

    /** if not null this instance is cleared and then reused for every new gpx found */
    protected final GeoPointDto mReuse;

    /** if not null gpx-v11: "trkpt" parsing is active */
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
            // factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse(in, this);
        } catch (ParserConfigurationException e) {
            logger.error("Error parsing gpx or kml", e);
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            logger.error("Error parsing gpx or kml", e);
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
        String name = getElementName(localName, qName);
        
        logger.debug("startElement {}-{}", localName, qName);
        if (name.equals(GpxDef_11.TRKPT) || name.equals(GpxDef_10.WPT)) {
            current = this.newInstance();
            current.setLatitude(Double.parseDouble(attributes.getValue(GpxDef_11.ATTR_LAT)));
            current.setLongitude(Double.parseDouble(attributes.getValue(GpxDef_11.ATTR_LON)));
        } else if (name.equals(KmlDef_22.PLACEMARK)) {
            current = this.newInstance();
        }
		if (current != null) {
			buf.setLength(0);
		}
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String name = getElementName(localName, qName);
        logger.debug("endElement {} {}", localName, qName);
        if (name.equals(GpxDef_11.TRKPT) || name.equals(GpxDef_10.WPT) || name.equals(KmlDef_22.PLACEMARK)) {
            this.onGotNewWaypoint.onGeoInfo(current);
            current = null;
        } else if (current != null) {
            if (name.equals(GpxDef_11.NAME)) {
                current.setName(buf.toString());
            } else if (name.equals(GpxDef_11.DESC) || name.equals(KmlDef_22.DESCRIPTION)) {
                current.setDescription(buf.toString());
            } else if (name.equals(GpxDef_11.LINK) || name.equals(GpxDef_10.URL)) {
                current.setUri(buf.toString());
            } else if (name.equals(GpxDef_11.TIME) || name.equals(KmlDef_22.TIMESTAMP_WHEN) || name.equals(KmlDef_22.TIMESPAN_BEGIN)) {
                try {
                    current.setTimeOfMeasurement(GpxFormatter.TIME_FORMAT.parse(buf.toString()));
                } catch (ParseException e) {
                    throw new SAXException("/gpx//time or /kml//when or /kml//begin: invalid time " + buf.toString());
                }
            } else if (name.equals(KmlDef_22.COORDINATES)) {
                // <coordinates>lon,lat,height blank lon,lat,height ...</coordinates>
                try {
                    String parts[] = buf.toString().split("[,\\s]");
                    current.setLatitude(Double.parseDouble(parts[1]));
                    current.setLongitude(Double.parseDouble(parts[0]));
                } catch (NumberFormatException e) {
                    throw new SAXException("/kml//Placemark/Point/coordinates>Expected: 'lon,lat,...' but got " + buf.toString());
                }
            }
        }
    }

    private String getElementName(String localName, String qName) {
        if ((localName != null) && (localName.length() > 0))
            return localName;
        if (qName == null) return "";

        int delim = qName.indexOf(":");
        if (delim < 0) return qName;

        return qName.substring(delim+1);
    }

    @Override
    public void characters(char[] chars, int start, int length)
            throws SAXException {
		if (current != null) {
			buf.append(chars, start, length);
		}
    }
}
