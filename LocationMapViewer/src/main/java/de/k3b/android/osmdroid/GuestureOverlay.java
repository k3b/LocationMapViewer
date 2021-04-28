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
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import de.k3b.android.locationMapViewer.R;

/**
 * Additional Guestures for {@see org.osmdroid.views.MapView}:<br/>
 * - DoubleTapDrag: Zoomin to the selected carret. The Zoomlevel is adjusted to fill the carret.<br/>
 * - DoubleTap: Zoomin one level. The click position becomes the new Center.<br/>
 *
 * Note DoubleTapDrag : TapDown+TapUp+TapDown+MoveWhileDown+TapUp
 *
 * Created by k3b on 10.02.2015.
 */
public class GuestureOverlay extends OverlayDebug {
    private PointF mStart = null;
    private PointF mEnd = null;
    private boolean mRectVisible = false;
    private Paint mPaint;
    private int colorDragTo;

    public GuestureOverlay(Context ctx) {
        super(ctx);
        colorDragTo = ctx.getResources().getColor(R.color.drag_to);
    }

    @Override public boolean 	onDoubleTap(MotionEvent ev, MapView mapView) {
        super.onDoubleTap(ev, mapView);

        return this.isEnabled(); // true: prevent original onDoubleTap: zoom-in. My own zoomIn is better ;-)
    }

    @Override public boolean 	onDoubleTapEvent(MotionEvent ev, MapView mapView) {
        if (this.isEnabled()) {
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    startRecordClipboard();
                    this.mStart = new PointF(ev.getX(), ev.getY());
                    this.mEnd = new PointF();
                    this.mPaint = new Paint();
                    this.mPaint.setColor(colorDragTo);
                    this.mPaint.setStrokeWidth(3);
                    setEndPoint("onDoubleTapEvent-ACTION_DOWN", ev, mapView);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (setEndPoint("onDoubleTapEvent-ACTION_MOVE", ev, mapView)) {
                        mapView.invalidate();

                        return true; // i have handled it
                    }
                    break;
                case MotionEvent.ACTION_UP: {
                    boolean visible = setEndPoint("onDoubleTapEvent-ACTION_UP", ev, mapView);
                    zoom(mapView, visible);
                    // this.mStart = null;
                    // this.mRect = null;
                    // this.mPaint = null;
                    // this.mRectVisible = false;
                    if (isDebugEnabled()) {
                        this.copyDebugToClipboard(mapView.getContext());
                    }
                    this.mRectVisible = false;
                    return true; // processed
                }
            }
            if (isDebugEnabled()) debug("onDoubleTapEvent", this, ev);
        }
        return super.onDoubleTapEvent(ev, mapView); // false: not handled yet
    }

    private void zoom(MapView mapView, boolean ddragMode) {
        if (isDebugEnabled())  debug("zoom",this);
        // mapView.setC .zoomToBoundingBox(rect);

        final Projection projection = mapView.getProjection();
        IMapController controller = mapView.getController();
        if (ddragMode) {

            IGeoPoint start = new GeoPoint(this.mStart.x, this.mStart.y);
            IGeoPoint end = new GeoPoint(this.mEnd.x, this.mEnd.y);
            ZoomUtil.zoomTo(mapView, ZoomUtil.NO_ZOOM, start, end);
            if (isDebugEnabled()) debug("zoom(ddrag mode)", start, "..", end,
                    "=>", mapView.getMapCenter(), "z=", mapView.getZoomLevelDouble());
        } else {
            IGeoPoint center = new GeoPoint(this.mStart.x, this.mStart.y);
            controller.setCenter(center);
            controller.zoomIn();
            if (isDebugEnabled()) debug("zoom(to center of)", center,
                    "=>", mapView.getMapCenter(), "z=", mapView.getZoomLevelDouble());
        }
    }

    private boolean setEndPoint(String context, MotionEvent ev, MapView mapView) {
        float x = ev.getX();
        float y =  ev.getY();

        double dx = Math.abs(mStart.x - x);
        double dy = Math.abs(mStart.y - y);
        this.mRectVisible = (dx > 10) || (dy > 10);
        this.mEnd.set(x,y);
        if (mRectVisible && isDebugEnabled()) debug(context,this, ev);

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
    public void draw(Canvas c, MapView mapView, boolean shadow) {
        super.draw(c, mapView, shadow);
        if ((!shadow) && (this.mRectVisible)) {
            drawBorder(c , this.mStart.x, this.mStart.y, this.mEnd.x, mEnd.y);
            if (isDebugEnabled())  debug("draw", this);
        }
    }

    private void drawBorder(Canvas c, float x1, float y1, float x2, float y2) {
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
