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

package org.osmdroid.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheckEx;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import de.k3b.android.locationMapViewer.R;

class MapTileProviderBasicEx extends MapTileProviderBasic {
    public MapTileProviderBasicEx(IRegisterReceiver pRegisterReceiver, NetworkAvailabliltyCheckEx aNetworkAvailablityCheck, ITileSource pTileSource) {
        super(pRegisterReceiver, aNetworkAvailablityCheck, pTileSource);
        aNetworkAvailablityCheck.setUseDataConnectionSource(this);
    }
}

/**
 * Same as MapView with custom changes that will go into next maven build .
 */
public class MapViewEx extends MapView {
    public MapViewEx(Context context) {
        this(context, 256, new DefaultResourceProxyImpl(context), null, null, null);
    }

    public MapViewEx(Context context, AttributeSet attrs) {
        this(context, 256, new DefaultResourceProxyImpl(context), null, null, attrs);
    }

    public MapViewEx(Context context, AttributeSet attrs, int defStyle) {
        this(context, 256, new DefaultResourceProxyImpl(context), null, null, attrs);
    }

    protected MapViewEx(final Context context, final int tileSizePixels,
                        final ResourceProxy resourceProxy, MapTileProviderBase tileProvider,
                        final Handler tileRequestCompleteHandler, final AttributeSet attrs) {
        super(context, tileSizePixels, resourceProxy,
                new MapTileProviderBasicEx(new SimpleRegisterReceiver(context), new NetworkAvailabliltyCheckEx(context),
                        TileSourceFactory.DEFAULT_TILE_SOURCE),
                tileRequestCompleteHandler, attrs);
    }
}
