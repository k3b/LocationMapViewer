/*
 * Copyright (C) 2015 k3b
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.android.locationMapViewer.constants.Constants;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements Constants {
    private static final Logger logger = LoggerFactory.getLogger(SettingsActivity.class);
    /**
     * public api to start settings-activity
     */
    public static void show(Activity context, int resultID) {
        final Intent i = new Intent(context, SettingsActivity.class);
        if (logger.isDebugEnabled()) logger.debug("show(resultID"+resultID+")");

        context.startActivityForResult(i, resultID);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);
    }

}
