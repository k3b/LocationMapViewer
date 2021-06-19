/*
 * Copyright (c) 2015-2021 by k3b.
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

package de.k3b.android.locationMapViewer.geobmp;

import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import de.k3b.android.GeoUtil;
import de.k3b.geo.api.IGeoRepository;
import de.k3b.geo.geobmp.BookmarkUtil;
import de.k3b.geo.io.GeoFileRepository;

/**
 * Created by k3b on 24.03.2015.
 */
public class GeoBmpFileRepository extends GeoFileRepository<GeoBmpDtoAndroid> {
    private final File iconDir;

    /**
     * connect repository to file
     *
     * @param file
     */
    public GeoBmpFileRepository(File file) {
        super(file, new GeoBmpDtoAndroid());
        this.iconDir = new File(file.getAbsolutePath() + ".icons");
        this.iconDir.mkdirs();
    }

    protected GeoBmpDtoAndroid loadItem(String line) {
        GeoBmpDtoAndroid geo = (GeoBmpDtoAndroid) super.loadItem(line);
        File bmpFile = getBmpFile(geo);

        if ((bmpFile != null) && (bmpFile.exists())) {
            geo.setBitmap(BitmapFactory.decodeFile(bmpFile.getAbsolutePath()));
        }
        return geo;
    }

    protected boolean saveItem(Writer writer, GeoBmpDtoAndroid geo) throws IOException {
        final boolean valid = super.saveItem(writer, geo);

        File bmpFile = (valid) ? getBmpFile(geo) : null;

        if ((bmpFile != null) && (geo.getBitmap() != null) && (!bmpFile.exists())) {
            GeoUtil.saveBitmapAsFile(geo.getBitmap(), bmpFile);
        }
        return valid;
    }

    private File getBmpFile(GeoBmpDtoAndroid geo) {
        if (geo == null) return null;
        final String id = geo.getId();

        if (BookmarkUtil.isNotEmpty(id)) {
            return new File(this.iconDir, id + ".icon");
        }
        return null;
    }

    /**
     * removes item from repository.
     *
     * @param item that should be removed
     * @return true if successful
     */
    @Override
    public IGeoRepository<GeoBmpDtoAndroid> delete(GeoBmpDtoAndroid item) {
        File file = getBmpFile(item);
        if (file != null) file.delete();

        super.delete(item);
        return this;
    }


}
