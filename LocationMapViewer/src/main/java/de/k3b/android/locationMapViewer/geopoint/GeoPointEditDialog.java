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
    private final EditText catNameField;
    private final EditText catDescField;
    private final Button saveButton;
    private final Button cancelButton;

    private GeoPointDto mCategory;

    public GeoPointEditDialog(Context context, IGeoInfoHandler dialogResultConsumer) {
        super(context);
        this.dialogResultConsumer = dialogResultConsumer;
        this.setContentView(R.layout.geopoint_edit);
        this.catNameField = (EditText) this
                .findViewById(R.id.edit_time_category_name_field);
        this.catDescField = (EditText) this
                .findViewById(R.id.edit_time_category_desc_field);
        this.saveButton = (Button) this
                .findViewById(R.id.edit_time_category_save_button);
        this.cancelButton = (Button) this
                .findViewById(R.id.edit_time_category_cancel_button);

        this.catNameField.setWidth(200);
        this.catDescField.setWidth(404);
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveChangesAndExit(GeoPointEditDialog.this.dialogResultConsumer);
            }

        });
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
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
        this.mCategory.setName(this.catNameField.getText().toString());
        this.mCategory.setDescription(this.catDescField.getText().toString());
        if (owner != null) {
            owner.onGeoInfo(this.mCategory);
        }
        this.dismiss();
    }


}
