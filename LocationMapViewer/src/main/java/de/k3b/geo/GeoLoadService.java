/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of of k3b-geoHelper library and LocationMapViewer.
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

package de.k3b.geo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.io.gpx.GeoXmlOrTextParser;
import de.k3b.geo.io.gpx.GpxReaderBase;

/* TODO: move to Geohelper */
public class GeoLoadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoLoadService.class);

    /**
     * @return list of found existing geo-filenames (without path) below dir
     * */
    @NonNull
    public static List<String> getGeoFiles(File dir, Map<String, File> name2file) {
        final List<String> found = new ArrayList<>();
        File[] files = listFiles(dir);
        if (files != null) {
            for (File file : files) {
                String name = getName(file);
                String nameLower = name.toLowerCase();
                name2file.put(nameLower, file);
                if (isGeo(nameLower)) {
                    found.add(name);
                }
            }
        }
        return found;
    }

    public static File getFirstGeoFile(File dir) {
        HashMap<String, File> name2file = new HashMap<>();
        List<String> files = getGeoFiles(dir, name2file);
        if (!files.isEmpty()) return name2file.get(files.get(0));
        return null;
    }

    public static boolean isGeo(String name) {
        return GeoConfig.isOneOf(name, GeoConfig.EXT_ALL);
    }

    public static boolean iszip(String name) {
        return GeoConfig.isOneOf(name, GeoConfig.EXT_ALL_ZIP);
    }


    private static String getName(@NonNull File file) {
        return file.getName();
    }

    // ----- File api abstractions
    @Nullable
    private static File[] listFiles(@Nullable File dir) {
        if (isExistingDirectory(dir)) {
            return dir.listFiles();
        }
        return null;
    }

    private static boolean isExistingDirectory(@Nullable File dir) {
        return dir != null && dir.exists() && dir.isDirectory();
    }

    private static String getUri(File doc) {
        return doc.getAbsolutePath();
    }

    // ----- File api
    public static void closeSilently(java.io.Closeable inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadGeoPointDtos(InputStream is, IGeoInfoHandler pointCollector) throws IOException {
        if (is != null) {
            GpxReaderBase parser = new GpxReaderBase(pointCollector, new GeoPointDto());
            parser.parse(new InputSource(is));
        } else {
            LOGGER.warn("loadGeoPointDtos: No geo found in {}" , is);
        }
    }

    public static String getName(String decodedPath) {
        if (decodedPath == null) return null;
        return new File(decodedPath).getName();
    }
    public static <T extends GeoPointDto>void loadGeoPointDtosFromText(String pois, IGeoInfoHandler pointCollector, T factoryItem) {
        if (pois != null) {
            List<T> result = new GeoXmlOrTextParser<T>().get(factoryItem, pois);

            if (result != null) {
                for(T item : result) {
                    pointCollector.onGeoInfo(item);
                }
            }
        }
    }

}
