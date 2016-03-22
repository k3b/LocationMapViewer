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

package de.k3b.android.osmdroid;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import de.k3b.android.widgets.Clipboard;

/**
 * This is a replacement for {@see org.osmdroid.views.overlay.Overlay}
 * that echos every event to the debug console if {@see #setDebugEnabled()} is set.
 *
 * Created by k3b on 10.02.2015.
 */
public class OverlayDebug extends Overlay {
    private static final String TAG = "Guesture";
    private static final int MAX_CLIPBOARD_LENGTH = 32000;
    private static final int MIN_CLIPBOARD_LENGTH = 20000;

    private boolean debugEnabled = false;
    private StringBuilder clipboard = null;

    public OverlayDebug(Context ctx) {
        super(ctx);
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    @Override
    protected void draw(Canvas c, MapView mapView, boolean shadow) {
    }

    @Override
    public boolean onDoubleTap(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onDoubleTap", ev);
        return super.onDoubleTap(ev, mapView);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onDoubleTapEvent", ev);
        return super.onDoubleTapEvent(ev, mapView);
    }

    @Override
    public boolean onDown(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onDownd", ev);
        return super.onDown(ev, mapView);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, MapView mapView) {
        if (isDebugEnabled()) debug("onFling", e1,
                ";e2:", e2,
                ";vX:", velocityX, ";vy:", velocityY);
        return super.onFling(e1, e2, velocityX, velocityY, mapView);
    }

    @Override
    public boolean onLongPress(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onLongPress", ev);
        return super.onLongPress(ev, mapView);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, MapView mapView) {
        if (isDebugEnabled()) debug("onScroll", e1,
                ";e2:", e2,
                ";dX:", distanceX, ";dy:", distanceY);
        return super.onScroll(e1, e2, distanceX, distanceY, mapView);
    }

    @Override
    public void onShowPress(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onShowPress", ev);
        super.onShowPress(ev, mapView);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onSingleTapConfirmed", ev);
        return super.onSingleTapConfirmed(ev, mapView);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent ev, MapView mapView) {
        if (isDebugEnabled()) debug("onSingleTapUp", ev);
        return super.onSingleTapUp(ev, mapView);
    }

    protected void startRecordClipboard() {
        if (isDebugEnabled()) {
            clipboard = new StringBuilder();
            Log.d(TAG,"started clipboard recording.");
        }
    }

    protected void copyDebugToClipboard(Context context) {
        if (this.clipboard != null) {
            Clipboard.addToClipboard(context, this.clipboard);
            final String message = "added " + this.clipboard.length() +
                    " chars debug info to clipboard";
            Log.d(TAG, message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            clipboard = null;
        }
    }

    /** all debug formatting goes here */
    protected void debug(Object... values) {
        if (isDebugEnabled()) {
            StringBuilder result = new StringBuilder();
            for (Object value : values) {
                if (value != null) {
                    result.append(value.toString()).append(" ");
                }
            }
            outputDebug(result.toString());
        }
    }

    /** all debug output goes here.
     * Can be overwritten if debug should go to a different media. */
    protected void outputDebug(String message) {
        Log.d(TAG,message);
        if (clipboard != null) {
            clipboard.append(message).append("\n").append("\n");
            final int oldLength = clipboard.length();
            if (oldLength > MAX_CLIPBOARD_LENGTH) {
                clipboard.delete(0, MAX_CLIPBOARD_LENGTH - MIN_CLIPBOARD_LENGTH);
                Log.d(TAG, "Reduced clipboard from " + oldLength + " to " + clipboard.length() + " chars.");
            }
        }
    }

}
