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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;

/**
 * Created by k3b on 10.02.2015.
 */
public class GuestureOverlay extends GuestureOverlayDebug {
    private Point mStart = null;
    private Point mEnd = null;
    // private Rect mRect = null;
    private boolean mRectVisible = false;
    private Paint mPaint;

    public GuestureOverlay(Context ctx) {
        super(ctx);
    }

    @Override public boolean 	onDoubleTap(MotionEvent ev, MapView mapView) {
        Log.d("onDoubleTap", ev.toString());
        super.onDoubleTap(ev, mapView);

        return true; // prevent original onDoubleTap: zoom-in
    }

    @Override public boolean 	onDoubleTapEvent(MotionEvent ev, MapView mapView) {

        switch(ev.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                this.mStart = new Point((int)ev.getX(),(int)ev.getY());
                this.mEnd = new Point();
                this.mPaint = new Paint();
                this.mPaint.setColor(Color.BLUE);
                this.mPaint.setStrokeWidth(3);
                setRect((int) ev.getX(), (int) ev.getY(), mapView);
                Log.d("onDoubleTapEvent-ACTION_DOWN-setRect",this.toString() + "-" + ev.toString());
                break;
            case MotionEvent.ACTION_MOVE:
                if (setRect((int)ev.getX(),(int)ev.getY(), mapView)) {
                    Log.d("onDoubleTapEvent-ACTION_MOVE-setRect",this.toString() + "-" + ev.toString());
                    mapView.invalidate();

                    return true; // i have handled it
                }
                break;
            case MotionEvent.ACTION_UP: {
                    boolean visible = setRect((int) ev.getX(), (int) ev.getY(), mapView);
                    if (visible) {
                        Log.d("onDoubleTapEvent-ACTION_UP-setRect",this.toString() + "-" + ev.toString());
                        zoom(mapView);
                        mapView.invalidate();
                    }
                    // this.mStart = null;
                    // this.mRect = null;
                    // this.mPaint = null;
                    // this.mRectVisible = false;
                    if (visible) return true; // processed
                }
                break;
        }
        Log.d("onDoubleTapEvent", this.toString() + "-" + ev.toString());
        return super.onDoubleTapEvent(ev, mapView); // false: not handled yet
    }

    private void zoom(MapView mapView) {
        Log.d("zoom",this.toString());
        // mapView.setC .zoomToBoundingBox(rect);

// !!!!!! todo
    }

    private boolean setRect(int x, int y, MapView mapView) {
        int dx = Math.abs(mStart.x - x);
        int dy = Math.abs(mStart.y - y);
        /*
        final int minX = Math.min(mStart.x, x);
        final int minY = Math.min(mStart.y, y);
        this.mRect.set(minX, minY,minX + dx,minY + dy);
        */
        this.mRectVisible = (dx > 10) || (dy > 10);
        this.mEnd.set(x,y);
        return mRectVisible;
    }

    /*
    private void drawRectangle(MapView mapView) {
        if (this.mRectVisible) {
            Canvas c = new Canvas();
            c.setBitmap(mapView.getDrawingCache());
            c.drawRect(this.mRect, this.mPaint);
        }
    }
    */

    @Override
    protected void draw(Canvas c, MapView mapView, boolean shadow) {
        super.draw(c, mapView, shadow);
        if ((!shadow) && (this.mRectVisible)) {
            drawBorder(c , this.mStart.x, this.mStart.y, this.mEnd.x, mEnd.y);
            Log.d("zoom", this.toString());
        }
    }

    private void drawBorder(Canvas c, int x1, int y1, int x2, int y2) {
        c.drawLine(x1, y1, x2, y1, this.mPaint);
        c.drawLine(x2, y1, x2, y2, this.mPaint);
        c.drawLine(x2, y2, x1, y2, this.mPaint);
        c.drawLine(x1, y2, x1, y1, this.mPaint);
    }

    public String toString() {
        if (!mRectVisible) return "";
        return "(" + this.mStart.x+"," + this.mStart.y + ".."  + this.mEnd.x+"," + this.mEnd.y + ")";
    }
}
