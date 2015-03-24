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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoRepository;

/**
 * Repository to load/save List< {@link de.k3b.geo.api.GeoPointDto} > in a file.
 *
 * Created by k3b on 17.03.2015.
 */
public class GeoFileRepository implements IGeoRepository {
    private static final Logger logger = LoggerFactory.getLogger(GeoFileRepository.class);

    private static final GeoUri converter = new GeoUri(GeoUri.OPT_DEFAULT);

    private final File file;
    private List<GeoPointDto> data = null;

    public GeoFileRepository(File file) {
        this.file = file;
    }

    /** load from repository
     *
     * @return data loaded
     */
    public List<GeoPointDto> load() {
        if (data == null) {
            data = new ArrayList<>();
            if (this.file.exists()) {
                try {
                    load(data, new FileReader(this.file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("load(): " + data.size() + " items from " + this.file);
            }
        } else if (logger.isDebugEnabled()) {
            logger.debug("load() cached value : " + data.size() + " items from " + this.file);
        }
        return data;
    }

    public String createId() {
        return UUID.randomUUID().toString();
    }
    /** save to repository
     *
     * @return false: error.
     */
    public boolean save() {
        try {
            if ((data != null) && (data.size() > 0)) {
                if (!this.file.exists()) {
                    this.file.getParentFile().mkdirs();
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("save(): " + data.size() + " items to " + this.file);
                }
                save(data, new FileWriter(this.file, false));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("save(): no items for " + this.file);
        }
        return false;
    }

    // load(new InputStreamReader(inputStream, "UTF-8"))
    static void load(List<GeoPointDto> result, Reader reader) throws IOException {
        String line;
        BufferedReader br = new BufferedReader(reader);
        while ((line = br.readLine()) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("load(" +line + ")");
            }

            GeoPointDto geo = converter.fromUri(line, new GeoPointDto());
            if (geo != null) result.add(geo);
        }
        br.close();
    }

    static void save(List<GeoPointDto> source, Writer writer) throws IOException {
        for (GeoPointDto geo : source) {
            final String line = converter.toUriString(geo);
            if (logger.isDebugEnabled()) {
                logger.debug("save(" +line + ")");
            }
            writer.write(line);
            writer.write("\n");
        }
        writer.close();
    }
}
