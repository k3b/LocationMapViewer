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

package de.k3b.geo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * A {@link IGeoInfoHandler} in a chain of {@link IGeoInfoHandler}s that converts
 * {@link IGeoPointInfo#getSymbol()}s from "relative to the containing geo-file" to absolute.
 *
 * @param <T> OS-Specific File-Translations system
 */
public abstract class SymbolConverterBase<T>  implements IGeoInfoHandler {
    @NonNull protected final T rootDir;
    @NonNull protected final Map<String, T> name2file;
    @Nullable protected final IGeoInfoHandler nextConverter;

    /**
     * @param rootDir where all relative paths refer to
     * @param name2file a map that translates relative items to absolute items
     * @param nextConverter if not null: next converter in a chain to be executed
     */
    protected SymbolConverterBase(@NonNull final T rootDir, @Nullable final Map<String, T> name2file, @Nullable IGeoInfoHandler nextConverter) {
        this.rootDir = rootDir;
        this.name2file = name2file != null ? name2file : new HashMap<String, T>();
        this.nextConverter = nextConverter;
    }

    // IGeoInfoHandler
    @Override
    public boolean onGeoInfo(IGeoPointInfo aGeoPoint) {
        String symbol = convertSymbol(aGeoPoint);
        if (symbol != null) {
            ((GeoPointDto) aGeoPoint).setSymbol(symbol);
        }
        if (nextConverter != null) nextConverter.onGeoInfo(aGeoPoint);
        return true;
    }

    private String convertSymbol(final IGeoPointInfo aGeoPoint) {
        String symbol = aGeoPoint != null ? aGeoPoint.getSymbol() : null;
        if (symbol != null && !symbol.contains(":") && symbol.contains(".")) {
            symbol = symbol.toLowerCase();
            T doc = name2file.get(symbol);
            if (doc == null && symbol.contains("/")) {
                doc = addFiles(symbol.split("/"));
            }
            if (doc != null) return getUri(doc);
        }
        return null;
    }

    private T addFiles(String[] pathElements) {
        T currentdir = rootDir;
        StringBuilder path = new StringBuilder();
        T doc = null;
        int last = pathElements.length - 1;
        for (int i = 0; i <= last; i++) {
            if (path.length() > 0) path.append("/");
            String parentPath = path.toString();
            path.append(pathElements[i]);
            String pathLowerCase = path.toString();
            doc = name2file.get(pathLowerCase);
            if (doc == null && i <= last) {
                T[] children = listFiles(currentdir);
                if (children != null) {
                    for (T child : children) {
                        name2file.put(parentPath  + getName(child).toLowerCase(), child);
                    }
                    doc = name2file.get(pathLowerCase);
                }
            }
            currentdir = doc;
        }
        return doc;
    }

    // Abstract OS-Specific methods
    @NonNull protected abstract String getName(@NonNull T child);

    @Nullable protected abstract T[] listFiles(@Nullable T currentdir);

    @NonNull protected abstract String getUri(@NonNull T doc);

    protected abstract boolean isExistingDirectory(@Nullable T dir);
}
