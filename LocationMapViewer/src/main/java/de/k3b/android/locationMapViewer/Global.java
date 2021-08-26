/*
 * Copyright (C) 2021 k3b
 *
 * This file is part of de.k3b.android.LocationMapViewer (https://github.com/k3b/LocationMapViewer/) .
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
package de.k3b.android.locationMapViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

/**
 * Global Settings used throughout the app.
 *
 * Non-final values can be modified by the app settings
 *
 * Created by k3b on 04.06.2015.
 */
public class Global {
    /** document tree supported since andrid-5.0. For older devices use folder picker */
    public static final boolean USE_DOCUMENT_PROVIDER = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);

    private static final File externalStorageDirectory = Environment.getExternalStorageDirectory();

    /** where osm-mapsforge-offline-maps (*.map) are found. defaults to /extDir/osmdroid/ */
    public static File mapsForgeDir = (externalStorageDirectory == null)
            ? null
            : new File(Environment.getExternalStorageDirectory(), "osmdroid");

    public static boolean mapsForgeEnabled = false;

    public static void prefs2Global(Context context) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());

        Global.mapsForgeDir = getPref(prefs, "mapsForgeDir", Global.mapsForgeDir);
        Global.mapsForgeEnabled = getPref(prefs, "mapsForgeEnabled", Global.mapsForgeEnabled);
    }

    /** load File preference from SharedPreferences */
    private static File getPref(SharedPreferences prefs, String key, File defaultValue) {
        String value         = prefs.getString(key, null);
        if (isNullOrEmpty(value)) return defaultValue;

        return new File(value);
    }

    private static boolean isNullOrEmpty(String value) {
        return (value == null) || (value.trim().length() == 0);
    }

    /** load value from SharedPreferences */
    private static boolean getPref(SharedPreferences prefs, String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }


}
