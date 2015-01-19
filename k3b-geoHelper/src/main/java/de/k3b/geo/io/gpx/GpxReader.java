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
 * Reads {@link de.k3b.geo.api.GeoPointDto} from file or stream.<br/>
 *
 * inspired by http://stackoverflow.com/questions/672454/how-to-parse-gpx-files-with-saxreader
 */
public abstract class GpxReader<T extends GeoPointDto> extends DefaultHandler {
    private List<T> track;
    private StringBuffer buf = new StringBuffer();
    private T current;

    public abstract T getNewInstance();

    public List<T> readTrack(InputSource in) throws IOException {
        try {
            track = new ArrayList<T>();
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
            current = this.getNewInstance();
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
