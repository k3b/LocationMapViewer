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

package de.k3b.geo.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

/**
 * Repository to load/save List<IGeoPointInfo> in a file.
 *
 * Created by k3b on 17.03.2015.
 */
public class GeoFileRepository implements IGeoRepository {
    private static final GeoUri converter = new GeoUri(GeoUri.OPT_DEFAULT);

    private final File file;
    private List<IGeoPointInfo> data = null;

    public GeoFileRepository(File file) {
        this.file = file;
    }

    /** load from repository
     *
     * @return data loaded
     */
    public List<IGeoPointInfo> load() {
        if (data == null) {
            data = new ArrayList<>();
            try {
                load(data, new FileReader(this.file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /** save to repository
     *
     * @return false: error.
     */
    public boolean save() {
        try {
            if (data != null) {
            save(data, new FileWriter(this.file, false));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // load(new InputStreamReader(inputStream, "UTF-8"))
    static void load(List<IGeoPointInfo> result, Reader reader) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(reader);
        while ((line = br.readLine()) != null) {
            IGeoPointInfo geo = converter.fromUri(line);
            if (geo != null) result.add(geo);
        }
        br.close();
    }

    static void save(List<IGeoPointInfo> source, Writer writer) throws IOException {
        for (IGeoPointInfo geo : source) {
            writer.write(converter.toUriString(geo));
            writer.write("\n");
        }
        writer.close();
    }
}
