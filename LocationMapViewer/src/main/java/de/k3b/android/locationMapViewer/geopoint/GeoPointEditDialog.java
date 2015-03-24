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

package de.k3b.android.locationMapViewer.geopoint;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

/**
 * Edit a {@link de.k3b.geo.api.GeoPointDto} item
 *
 * Created by k3b on 23.03.2015.
 */
public class GeoPointEditDialog extends Dialog implements IGeoInfoHandler {
    private final IGeoInfoHandler dialogResultConsumer;
    private final EditText editName;
    private final EditText editDescription;
    private final Button buttonSave;
    private final Button buttonCancel;

    private GeoPointDto mCategory;

    public GeoPointEditDialog(Context context, IGeoInfoHandler dialogResultConsumer) {
        super(context);
        this.dialogResultConsumer = dialogResultConsumer;
        this.setContentView(R.layout.geopoint_edit);
        this.editName = (EditText) this
                .findViewById(R.id.edit_name);
        this.editDescription = (EditText) this
                .findViewById(R.id.edit_description);
        this.buttonSave = (Button) this
                .findViewById(R.id.cmd_save);
        this.buttonCancel = (Button) this
                .findViewById(R.id.cmd_cancel);

        this.editName.setWidth(200);
        this.editDescription.setWidth(404);
        this.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveChangesAndExit(GeoPointEditDialog.this.dialogResultConsumer);
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
        return false;
    }

    private void saveChangesAndExit(final IGeoInfoHandler owner) {
        this.mCategory.setName(this.editName.getText().toString());
        this.mCategory.setDescription(this.editDescription.getText().toString());
        if (owner != null) {
            owner.onGeoInfo(this.mCategory);
        }
        this.dismiss();
    }


}
