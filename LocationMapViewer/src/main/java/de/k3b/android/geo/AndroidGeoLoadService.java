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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.k3b.geo.GeoLoadService;
import de.k3b.geo.api.IGeoPointInfo;

public class AndroidGeoLoadService extends GeoLoadService {
    /**
     * @return list of found existing geo-filenames (without path) below dir
     * */
    @NonNull
    public static List<String> getGeoFiles(DocumentFile dir, Map<String, DocumentFile> name2file) {
        final List<String> found = new ArrayList<>();
        DocumentFile[] files = listFiles(dir);
        if (files != null) {
            for (DocumentFile file : files) {
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

    public static String convertSymbol(final IGeoPointInfo aGeoPoint, final DocumentFile dir, final Map<String, DocumentFile> name2file) {
        String symbol = aGeoPoint != null ? aGeoPoint.getSymbol() : null;
        if (symbol != null && !symbol.contains(":") && symbol.contains(".")) {
            symbol = symbol.toLowerCase();
            DocumentFile doc = name2file.get(symbol);
            if (doc == null && symbol.contains("/")) {
                doc = addFiles(dir, symbol.split("/"), name2file);
            }
            if (doc != null) return getUri(doc);
        }
        return null;
    }

    private static DocumentFile addFiles(DocumentFile dir, String[] pathElements, Map<String, DocumentFile> name2file) {
        DocumentFile currentdir = dir;
        StringBuilder path = new StringBuilder();
        DocumentFile doc = null;
        int last = pathElements.length - 1;
        for (int i = 0; i <= last; i++) {
            if (path.length() > 0) path.append("/");
            String parentPath = path.toString();
            path.append(pathElements[i]);
            String pathLowerCase = path.toString();
            doc = name2file.get(pathLowerCase);
            if (doc == null && i <= last) {
                DocumentFile[] children = listFiles(currentdir);
                if (children != null) {
                    for (DocumentFile child : children) {
                        name2file.put(parentPath  + getName(child).toLowerCase(), child);
                    }
                    doc = name2file.get(pathLowerCase);
                }
            }
            currentdir = doc;
        }
        return doc;
    }

    private static String getName(@NonNull DocumentFile file) {
        return file.getName();
    }

    @Nullable
    private static DocumentFile[] listFiles(@Nullable DocumentFile dir) {
        if (isExistingDirectory(dir)) {
            return dir.listFiles();
        }
        return null;
    }

    private static boolean isExistingDirectory(@Nullable DocumentFile dir) {
        return dir != null && dir.exists() && dir.isDirectory();
    }

    private static String getUri(DocumentFile doc) {
        return doc.getUri().toString();
    }
}
