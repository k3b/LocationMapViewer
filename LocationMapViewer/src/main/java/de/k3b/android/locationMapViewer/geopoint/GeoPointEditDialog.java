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
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoFormatter;
import de.k3b.util.IsoDateTimeParser;

/**
 * Edit a {@link de.k3b.geo.api.GeoPointDto} item
 *
 * Created by k3b on 23.03.2015.
 */
public class GeoPointEditDialog extends Dialog implements IGeoInfoHandler {
    private final IGeoInfoHandler dialogResultConsumer;
    private final EditText editName;
    private final EditText editDescription;

    private final EditText editLatitude;
    private final EditText editLongitude;
    private final EditText editZoomMin;
    private final EditText editTimeOfMeasurement;
    private final EditText editUri;

    private final Button buttonSave;
    private final Button buttonCancel;

    private GeoPointDtoWithBitmap currentItem = null;

    public GeoPointEditDialog(Context context, IGeoInfoHandler dialogResultConsumer) {
        super(context);
        this.dialogResultConsumer = dialogResultConsumer;
        this.setContentView(R.layout.geopoint_edit);
        this.editName = (EditText) this.findViewById(R.id.edit_name);
        this.editDescription = (EditText) this.findViewById(R.id.edit_description);

        this.editLatitude = (EditText) this.findViewById(R.id.edit_latitude);
        this.editLongitude = (EditText) this.findViewById(R.id.edit_longitude);
        this.editZoomMin = (EditText) this.findViewById(R.id.edit_zoom);
        this.editTimeOfMeasurement = (EditText) this.findViewById(R.id.edit_time);
        this.editUri = (EditText) this.findViewById(R.id.edit_uri);

        this.buttonSave = (Button) this.findViewById(R.id.cmd_save);
        this.buttonCancel = (Button) this.findViewById(R.id.cmd_cancel);

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
        this.currentItem = (GeoPointDtoWithBitmap) geoInfo;

        load(this.currentItem);
        return true;
    }

    void load(GeoPointDtoWithBitmap currentItem) {
        this.editName.setText(this.currentItem.getName());
        this.editDescription.setText(this.currentItem.getDescription());

        this.editLatitude.setText(GeoFormatter.formatLatLon(this.currentItem.getLatitude()));
        this.editLongitude.setText(GeoFormatter.formatLatLon(this.currentItem.getLongitude()));
        this.editZoomMin.setText(GeoFormatter.formatZoom(this.currentItem.getZoomMin()));
        this.editTimeOfMeasurement.setText(GeoFormatter.formatDate(this.currentItem.getTimeOfMeasurement()));
        this.editUri.setText(this.currentItem.getUri());

        final ImageView thumbnail = (ImageView) this.findViewById(R.id.thumbnail);
        thumbnail.setImageBitmap(this.currentItem.getBitmap());
        final TextView lblId = (TextView) this.findViewById(R.id.label_id);
        lblId.setText(this.currentItem.getId());
    }

    private void save(GeoPointDtoWithBitmap currentItem) {
        currentItem.setName(getStringOrNull(this.editName));
        currentItem.setDescription(getStringOrNull(this.editDescription));
        currentItem.setUri(getStringOrNull(this.editUri));
        currentItem.setLatitude(getLatLon(this.editLatitude));
        currentItem.setLongitude(getLatLon(this.editLongitude));
        currentItem.setTimeOfMeasurement(IsoDateTimeParser.parse(this.editTimeOfMeasurement.getText().toString()));
        currentItem.setZoomMin(GeoFormatter.parseZoom (this.editZoomMin.getText().toString()));
    }

    private String getStringOrNull(EditText edit) {
        String result = edit.getText().toString();
        if ((result != null) && (result.length() > 0)) return result;
        return null;
    }

    private double getLatLon(EditText edit) {
        try {
            return GeoFormatter.parseLatOrLon(edit.getText().toString());
        } catch (ParseException e) {
        }

        return IGeoPointInfo.NO_LAT_LON;
    }


    private void saveChangesAndExit(final IGeoInfoHandler owner) {
        save(this.currentItem);
        if (owner != null) {
            owner.onGeoInfo(this.currentItem);
        }
        this.dismiss();
    }

}
