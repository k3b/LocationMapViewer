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
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.k3b.geo.GeoLoadService;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.util.Unzip;

public class AndroidGeoLoadService extends GeoLoadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidGeoLoadService.class);

    @NonNull
    public static InputStream openGeoInputStream(Context context, Uri uri, String name) throws IOException {
        InputStream is = null;
        is = context.getContentResolver().openInputStream(uri);

        if (iszip(name)) {
            File geoUnzipDir = getUnzipDirFile(context, name);
            Unzip.unzip(name, is, geoUnzipDir);
            GeoLoadService.closeSilently(is);
            is = null;
            File geofile = getFirstGeoFile(geoUnzipDir);
            if (geofile != null) is = new FileInputStream(geofile);
        }
        return is;
    }

    @NonNull
    public static File getUnzipDirFile(Context context, String name) {
        File unzipRootDir = new File(context.getCacheDir(), "unzip");
        File geoUnzipDir = new File(unzipRootDir, name + ".dir");
        return geoUnzipDir;
    }

    public static void loadGeoPointDtos(Context context, DocumentFile documentFile, IGeoInfoHandler pointCollector) {
        if (documentFile != null) {
            loadGeoPointDtos(context, documentFile.getUri(), pointCollector, documentFile.getName());
        }
    }

    public static void loadGeoPointDtos(Context context, Intent intent, IGeoInfoHandler pointCollector) {
        final Uri uri = (intent != null) ? intent.getData() : null;
        loadGeoPointDtos(context, uri, pointCollector, null);
    }


    public static void loadGeoPointDtos(Context context, Uri uri, IGeoInfoHandler pointCollector, String name) {
        if (uri != null) {
            InputStream is = null;
            try {
                if (name == null) {
                    name = getName(uri);
                }
                is = AndroidGeoLoadService.openGeoInputStream(context, uri, name);
                loadGeoPointDtos(is, pointCollector);
            } catch (IOException e) {
                LOGGER.warn("loadGeoPointDtos: Cannot open " + uri, e);
            } finally {
                AndroidGeoLoadService.closeSilently(is);
            }
        }
    }

}
