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

package de.k3b.android.locationMapViewer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by k3b on 10.02.2015.
 */
public class GuestureOverlayDebug extends Overlay {
    public GuestureOverlayDebug(Context ctx) {
        super(ctx);
    }

    @Override
    protected void draw(Canvas c, MapView mapView, boolean shadow) {
    }

    @Override public boolean 	onDoubleTap(MotionEvent ev, MapView mapView) {
        Log.d("onDoubleTap", ev.toString());
        return super.onDoubleTap(ev, mapView);
    }
    @Override public boolean 	onDoubleTapEvent(MotionEvent ev, MapView mapView) {
        Log.d("onDoubleTapEvent",ev.toString());
        return super.onDoubleTapEvent(ev, mapView);
    }
    @Override
    public boolean onDown(MotionEvent ev, MapView mapView) {
        Log.d("onDownd",ev.toString());
        return super.onDown(ev, mapView);
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, MapView mapView) {
        Log.d("onFling",e1.toString() +
                ";e2:" +  e2.toString() +
                ";vX:" + velocityX + ";vy:" + velocityY);
        return super.onFling(e1, e2, velocityX, velocityY, mapView);
    }
    @Override
    public boolean onLongPress(MotionEvent ev, MapView mapView) {
        Log.d("onLongPress",ev.toString());
        return super.onLongPress(ev, mapView);
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, MapView mapView) {
        Log.d("onScroll",e1.toString() +
                ";e2:" +  e2.toString() +
                ";dX:" + distanceX + ";dy:" + distanceY);
        return super.onScroll(e1,e2,distanceX,distanceY, mapView);
    }
    @Override
    public void onShowPress(MotionEvent ev, MapView mapView) {
        Log.d("onShowPress",ev.toString());
        super.onShowPress(ev, mapView);
    }
    @Override
    public boolean 	onSingleTapConfirmed(MotionEvent ev, MapView mapView) {
        Log.d("onSingleTapConfirmed",ev.toString());
        return super.onSingleTapConfirmed(ev, mapView);
    }
    @Override
    public boolean onSingleTapUp(MotionEvent ev, MapView mapView) {
        Log.d("onSingleTapUp",ev.toString());
        return super.onSingleTapUp(ev, mapView);
    }

}
