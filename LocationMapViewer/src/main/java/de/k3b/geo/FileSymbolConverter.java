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

import java.io.File;
import java.util.Map;

import de.k3b.geo.api.IGeoInfoHandler;

public class FileSymbolConverter extends SymbolConverterBase<File> implements IGeoInfoHandler {
    public FileSymbolConverter(@NonNull final File rootDir,
                               @Nullable final Map<String, File> name2file,
                               @Nullable IGeoInfoHandler nextConverter) {
        super(rootDir, name2file, nextConverter);
    }

    // ----- File api abstractions
    @NonNull @Override protected  String getName(@NonNull File file) {
        return file.getName();
    }

    @Nullable
    @Override protected File[] listFiles(@Nullable File dir) {
        if (isExistingDirectory(dir)) {
            return dir.listFiles();
        }
        return null;
    }

    @Override protected boolean isExistingDirectory(@Nullable File dir) {
        return dir != null && dir.exists() && dir.isDirectory();
    }

    @NonNull @Override protected String getUri(@NonNull File doc) {
        return "file://" + doc.getAbsolutePath();
    }
}
