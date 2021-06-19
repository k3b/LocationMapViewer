/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of k3b-geoHelper library.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.k3b.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* TODO: remove this copy as it is already part of Geohelper */
public class Unzip2 {
    public static final String TAG = Unzip.class.getSimpleName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);

    // see https://stackoverflow.com/questions/1809007/best-way-to-detect-if-a-stream-is-zipped-in-java/68038770#68038770
    public static boolean isZipStream(InputStream inputStream) {
        if (inputStream == null || !inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream must support mark-reset. Use BufferedInputstream()");
        }
        boolean isZipped = false;
        try {
            inputStream.mark(64);
            isZipped = new ZipInputStream(inputStream).getNextEntry() != null;
            inputStream.reset();
        } catch (IOException ex) {
            // cannot be opend as zip.
        }
        return isZipped;
    }

    public static void unzip(String name, InputStream inputStream, File destinationDir) throws IOException {
        ZipInputStream zipInputStream = null;
        String message = "unzip('" + name +
                "' => '" + destinationDir + "') ";
        LOGGER.info( message);

        try {
            zipInputStream = new ZipInputStream(inputStream);

            for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream
                    .getNextEntry()) {
                unzipEntry(zipInputStream, zipEntry, destinationDir);
            }
        } catch (IOException e) {
            LOGGER.error( "err " + message + e.getMessage(), e);
            throw new IOException(message, e);
        } finally {
            if (zipInputStream != null) zipInputStream.close();
        }
    }

    private static void unzipEntry(ZipInputStream zipInputStream, ZipEntry entry, File outputDir) throws IOException {
        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }

        String message = "unzipEntry(" + entry + ")[" + entry.getSize() + "] ";
        LOGGER.debug( message);

        OutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            copy(zipInputStream, output);
        } catch (IOException e) {
            LOGGER.error( "err " + message + e.getMessage(), e);
            throw new IOException(message, e);
        } finally {
            // keep zipInputStream open for next entry-file
            if (output != null) output.close();
        }

    }

    private static void createDir(File dir) {
        if (dir.exists()) {
            return;
        }
        LOGGER.debug("Creating dir " + dir.getName());

        if (!dir.mkdirs()) {
            throw new RuntimeException("Can not create dir " + dir);
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte data[] = new byte[10240];
        int count;

        int total = 0;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
            total += count;
        }
        output.flush();
    }

    public static void deleteRecursive(File f) {
        if (f != null && f.exists()) {
            if (f.isDirectory()) {
                for (File c : f.listFiles()) {
                    deleteRecursive(c);
                }
            }
            f.delete();
        }
    }
}
