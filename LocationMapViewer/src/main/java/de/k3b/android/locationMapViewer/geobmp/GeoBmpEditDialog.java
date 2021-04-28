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

package de.k3b.android.locationMapViewer.geobmp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.k3b.android.locationMapViewer.R;
import de.k3b.android.widgets.IViewHolder;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Edit a {@link de.k3b.geo.api.GeoPointDto} item
 *
 * Created by k3b on 23.03.2015.
 */
public class GeoBmpEditDialog extends Dialog implements IGeoInfoHandler, IViewHolder {
    private final IGeoInfoHandler dialogResultConsumer;

    private final Button buttonSave;
    private final Button buttonCancel;

    private GeoBmpDto currentItem = null;

    public GeoBmpEditDialog(Context context, IGeoInfoHandler dialogResultConsumer, int layoutID) {
        super(context);
        this.dialogResultConsumer = dialogResultConsumer;
        this.setContentView(layoutID); // R.layout.geobmp_edit);

        this.buttonSave = this.findViewById(R.id.cmd_save);
        this.buttonCancel = this.findViewById(R.id.cmd_cancel);

        this.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveChangesAndExit(GeoBmpEditDialog.this.dialogResultConsumer);
            }

        });
        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                cancel();
            }
        });
    }

    @Override
    public boolean onGeoInfo(IGeoPointInfo geoInfo) {
        this.currentItem = (GeoBmpDto) geoInfo;

        load(this.currentItem);
        return true;
    }

    void load(GeoBmpDto currentItem) {
        GeoBmpBinder.toGui(this, currentItem);
    }

    private void save(GeoBmpDto currentItem) {
        GeoBmpBinder.fromGui(this, currentItem);
    }

    private void saveChangesAndExit(final IGeoInfoHandler owner) {
        save(this.currentItem);
        if (owner != null) {
            owner.onGeoInfo(this.currentItem);
        }
        this.dismiss();
    }
}
