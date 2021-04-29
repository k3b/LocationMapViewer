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
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;

import de.k3b.android.locationMapViewer.R;

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
public abstract class FilePermissionActivity extends Activity {
    private static final int REQUEST_ROOT_DIR = 2001;
    public static final String TAG = "k3b.FilePermAct";
    private static IOnDirectoryPermissionGrantedHandler currentPermissionGrantedHandler = null;

    private static final int REQUEST_ID_READ_EXTERNAL_STORAGE = 2000;
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private boolean debugLog = true;
    int RESULT_NO_PERMISSIONS = -22;

    protected abstract void onCreateEx(Bundle savedInstanceState);

    // workflow onCreate() => requestPermission(PERMISSION_READ_EXTERNAL_STORAGE) => onRequestPermissionsResult() => abstract onCreateEx()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Uri uri = (getIntent() != null) ? getIntent().getData() : null;

        // if app was called with uri parameter: ask for permissions
        if (uri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(PERMISSION_READ_EXTERNAL_STORAGE, REQUEST_ID_READ_EXTERNAL_STORAGE);
        } else {
            onCreateEx(null);
        }
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
                final boolean success = (grantResults != null)
                        && (grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (success) {
                    if (debugLog) {
                        Log.i(TAG, this.getClass().getSimpleName()
                                + ": onRequestPermissionsResult(success) ");
                    }
                    onCreateEx(null);
                } else {
                    Log.i(TAG, this.getClass().getSimpleName()
                            + ": " + getText(R.string.permission_error));
                    Toast.makeText(this, R.string.permission_error, Toast.LENGTH_LONG).show();
                    setResult(RESULT_NO_PERMISSIONS, null);
                    finish();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface IOnDirectoryPermissionGrantedHandler {
        void afterGrant(FilePermissionActivity activity);
    }
}
