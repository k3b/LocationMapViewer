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
package de.k3b.android.widgets;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import de.k3b.android.locationMapViewer.R;
import de.k3b.android.locationMapViewer.constants.Constants;

/**
 * Manage permission in lifecycle for Android-6 ff {@link android.app.Activity}.
 * * read from external-storage
 *
 * implemented in {@link #onCreate(Bundle)} when intent contains a data-uri of file to read from
 * when done executes in {@link #onCreateEx(Bundle)} in inherited class
 *
 * how to use:
 * * In all activities replace ".... extends {@link android.app.Activity} with extends {@link FilePermissionActivity}
 * * rename {@link #onCreate(Bundle)} to {@link #onCreateEx(Bundle)}
 * * make shure that in onCreateEx() that there is no call to super.onCreate()
 *
 *  Backport from https://github.com/k3b/aphotomanager/ FilePermissionActivity
 */
public abstract class FilePermissionActivity extends Activity implements Constants {
    public static final String TAG = "k3b.FilePermAct";

    /* TODO allow Mapsforge Support for android 10ff

        https://developer.android.com/training/data-storage/manage-all-files

        Declare the MANAGE_EXTERNAL_STORAGE permission in the manifest

Use the ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION intent action to direct users to a system settings page where they can enable the following option for your app: Allow access to manage all files.

    <application android:requestLegacyExternalStorage="true"  /> android-q (api 29) allow file access


     */

    private static final int REQUEST_ID_READ_EXTERNAL_STORAGE = 2000;
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private Boolean permissionGrantedFile = null;

    private static final int REQUEST_ID_READ_GPS = 2001;
    private static final String PERMISSION_READ_GPS = Manifest.permission.ACCESS_FINE_LOCATION;
    protected Boolean permissionGrantedGps = null;

    private boolean debugLog = true;
    int RESULT_NO_PERMISSIONS = -22;

    protected Bundle lastInstanceState = null;
    private boolean mustCallOnCreateEx = true;

    protected abstract void onCreateEx(Bundle savedInstanceState);

    private boolean hasFileReadPermissions() {
        if (permissionGrantedFile != null) return permissionGrantedFile;

        final Uri uri = (getIntent() != null) ? getIntent().getData() : null;

        // if app was called with uri parameter: ask for permissions
        if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(PERMISSION_READ_EXTERNAL_STORAGE, REQUEST_ID_READ_EXTERNAL_STORAGE);
            return false;
        }

        return true;
    }

    private boolean hasGpsPermissions() {
        if (permissionGrantedGps != null) return permissionGrantedGps;

        boolean showLocation = isShowLocation();

        // if app wants to display my logcation: ask for permissions
        if (showLocation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, PERMISSION_READ_GPS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(PERMISSION_READ_GPS, REQUEST_ID_READ_GPS);
            return false;
        }

        return true;
    }

    protected boolean checkPermissions() {
        if (hasFileReadPermissions() && hasGpsPermissions()) {
            if (mustCallOnCreateEx) {
                onCreateEx(this.lastInstanceState);
                mustCallOnCreateEx = false;
            }
            this.lastInstanceState = null;
            return true;
        }
        return false;
    }

    // workflow onCreate() => requestPermission(PERMISSION_READ_EXTERNAL_STORAGE) => onRequestPermissionsResult() => abstract onCreateEx()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.lastInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);

        checkPermissions();
    }

    private void requestPermission(final String permission, final int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_READ_EXTERNAL_STORAGE: {
                permissionGrantedFile = isGrantSuccess(grantResults, "File");
                if (!permissionGrantedFile) {
                    Toast.makeText(this, R.string.permission_error, Toast.LENGTH_LONG).show();
                    setResult(RESULT_NO_PERMISSIONS, null);
                    finish();
                    return;
                }
                checkPermissions();
                return;
            }
            case REQUEST_ID_READ_GPS: {
                if (isGrantSuccess(grantResults, "Gps")) {
                    // don-t ask again
                    permissionGrantedFile = true;
                } else {
                    boolean value = false;
                    // disable in settings
                    setShowLocation(value);
                }
                checkPermissions();
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected boolean isShowLocation() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean(PREFS_SHOW_LOCATION, false);
    }

    protected void setShowLocation(boolean value) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putBoolean(PREFS_SHOW_LOCATION, value);
        prefs.apply();
    }

    private boolean isGrantSuccess(int[] grantResults, String dbgContext) {
        boolean success = (grantResults != null)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        if (debugLog) {
            Log.i(TAG, this.getClass().getSimpleName()
                    + ": onRequestPermissionsResult(" + dbgContext +
                    "-success=" +success + ") ");
        }

        return success;
    }

    public interface IOnDirectoryPermissionGrantedHandler {
        void afterGrant(FilePermissionActivity activity);
    }
}
