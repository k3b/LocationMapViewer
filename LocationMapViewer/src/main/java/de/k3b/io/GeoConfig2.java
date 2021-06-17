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

/* TODO: remove this copy as it is already part of Geohelper */
package de.k3b.io;

public class GeoConfig2 {
    public static final int THUMBSIZE = 200;

    /* File extensions (lowercase) */
    public static final String EXT_ZIP = ".zip";

    public static final String EXT_KML = ".kml";
    public static final String EXT_KMZ = ".kmz";
    public static final String EXT_KMZ2 = EXT_KML+EXT_ZIP;
    public static final String[] EXT_ALL_KML = new String[]{EXT_KMZ, EXT_KMZ2, EXT_KML};

    public static final String EXT_GPX = ".gpx";
    public static final String EXT_GPZ = ".gpz";
    public static final String EXT_GPZ2 = EXT_GPX+EXT_ZIP;
    public static final String[] EXT_ALL_GPX = new String[]{EXT_GPZ, EXT_GPZ2, EXT_GPX};

    public static final String EXT_POI = ".poi";
    public static final String EXT_POZ = ".poz";
    public static final String EXT_POZ2 = EXT_POI+EXT_ZIP;
    public static final String[] EXT_ALL_POI = new String[]{EXT_POZ, EXT_POZ2, EXT_POI};

    public static final String[] EXT_ALL_ZIP = new String[]{EXT_GPZ,EXT_KMZ,EXT_POZ,EXT_ZIP};
    public static final String[] EXT_ALL = new String[]{
            EXT_GPZ,EXT_KMZ,EXT_POZ,
            EXT_GPZ2,EXT_KMZ2,EXT_POZ2,
            EXT_GPX,EXT_KML,EXT_POI};

    public static boolean isOneOf(String outFileName, String... suffixes) {
        String lowerCase = outFileName.toLowerCase();
        for (String ext : suffixes) {
            if (lowerCase.endsWith(ext)) return true;
        }
        return false;
    }
}
