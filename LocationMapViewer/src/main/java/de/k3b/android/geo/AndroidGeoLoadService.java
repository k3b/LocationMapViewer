/*
 * Copyright (c) 2021 by k3b.
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

package de.k3b.android.geo;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import de.k3b.android.locationMapViewer.geobmp.GeoBmpDto;
import de.k3b.geo.GeoLoadService;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.io.gpx.GeoXmlOrTextParser;
import de.k3b.util.Unzip;
import de.k3b.util.Unzip2;

public class AndroidGeoLoadService extends GeoLoadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidGeoLoadService.class);

    /**
     * @param isZipped null: means unknows: try to find out
     * @return
     * @throws IOException
     */
    @NonNull
    public static InputStream openGeoInputStream(Context context, Uri uri, String name, Boolean isZipped) throws IOException {
        InputStream inputStream = null;
        inputStream = new BufferedInputStream(context.getContentResolver().openInputStream(uri));

        // https://stackoverflow.com/questions/1809007/best-way-to-detect-if-a-stream-is-zipped-in-java
        // find out if stream inputStream zip
        if (isZipped == null) {
            isZipped = isZipStream(inputStream);
        }

        if (isZipped) {
            File geoUnzipDir = getUnzipDirFile(context, name);
            Unzip.unzip(name, inputStream, geoUnzipDir);
            GeoLoadService.closeSilently(inputStream);
            inputStream = null;
            File geofile = getFirstGeoFile(geoUnzipDir);
            if (geofile != null) inputStream = new BufferedInputStream(new FileInputStream(geofile));
        }
        return inputStream;
    }

    public static boolean isZipStream(InputStream inputStream) {
        // todo inline with Unzip.isZipStream
        return Unzip2.isZipStream(inputStream);
    }

    public static boolean isZipStream(Context context, Uri uri) {
        boolean result = false;
        if (uri != null) {
            InputStream is = null;
            try {
                is = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
                result = Unzip2.isZipStream(is);
            } catch (FileNotFoundException e) {
                // assume no zip stream
            } finally {
                closeSilently(is);
            }
        }
        return result;
    }

    @NonNull
    public static File getUnzipDirFile(Context context, String name) {
        File unzipRootDir = new File(context.getCacheDir(), "unzip");
        File geoUnzipDir = new File(unzipRootDir, name + ".dir");
        return geoUnzipDir;
    }

    public static void loadGeoPointDtos(Context context, Uri uri, IGeoInfoHandler pointCollector,
                                        String name, Boolean isZipped) {
        if (uri != null) {
            InputStream is = null;
            try {
                if (name == null) {
                    name = getName(uri);
                }
                is = AndroidGeoLoadService.openGeoInputStream(context, uri, name, isZipped);

                loadGeoPointDtos(is, pointCollector);
            } catch (IOException e) {
                LOGGER.warn("loadGeoPointDtos: Cannot open " + uri, e);
            } finally {
                AndroidGeoLoadService.closeSilently(is);
            }
        }
    }
    public static void loadGeoPointDtosFromText(String pois, IGeoInfoHandler pointCollector) {
        if (pois != null) {
            List<GeoBmpDto> result = new GeoXmlOrTextParser<GeoBmpDto>().get(new GeoBmpDto(), pois);

            if (result != null) {
                for(GeoBmpDto item : result) {
                    pointCollector.onGeoInfo(item);
                }
            }
        }
    }
    
    public static String getName(Uri uri) {
        String decodedPath = null;
        if (uri != null) {
            String lastPathSegment = uri.getLastPathSegment();
            try {
                decodedPath = URLDecoder.decode(lastPathSegment, "UTF8");

            } catch (UnsupportedEncodingException ignore) {
                LOGGER.warn("getName(uri={}) => {}" , uri, ignore.getMessage());
                return lastPathSegment;
            }
            decodedPath = getName(decodedPath);
        }
        return decodedPath;
    }

}
