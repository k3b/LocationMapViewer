/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of of k3b-geoHelper library.
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

import de.k3b.geo.SymbolConverterBase;
import de.k3b.geo.api.IGeoInfoHandler;

public class DocumentFileSymbolConverter extends SymbolConverterBase<DocumentFile> implements IGeoInfoHandler {
    public DocumentFileSymbolConverter(@NonNull final DocumentFile rootDir,
                                       @Nullable final Map<String, DocumentFile> name2file,
                                       @Nullable IGeoInfoHandler nextConverter) {
        super(rootDir, name2file, nextConverter);
    }

    public static List<String> getGeoFiles(DocumentFile dir, Map<String, DocumentFile> name2file) {
        return new DocumentFileSymbolConverter(dir, name2file, null).getGeoFiles(dir);
    }

    /**
     * @return list of found existing geo-filenames (without path) below dir
     * */
    @NonNull
    public List<String> getGeoFiles(DocumentFile dir) {
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

    // ----- File api abstractions
    @NonNull @Override protected  String getName(@NonNull DocumentFile file) {
        return file.getName();
    }

    @Nullable
    @Override protected DocumentFile[] listFiles(@Nullable DocumentFile dir) {
        if (isExistingDirectory(dir)) {
            return dir.listFiles();
        }
        return null;
    }

    @Override protected boolean isExistingDirectory(@Nullable DocumentFile dir) {
        return dir != null && dir.exists() && dir.isDirectory();
    }

    @NonNull @Override protected String getUri(@NonNull DocumentFile doc) {
        return doc.getUri().toString();
    }

}
