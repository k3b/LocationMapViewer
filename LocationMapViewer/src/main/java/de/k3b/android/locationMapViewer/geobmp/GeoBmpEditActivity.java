/*
 * Copyright (c) 2015-2021 by k3b.
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.k3b.android.locationMapViewer.R;
import de.k3b.android.widgets.IViewHolder;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;

public class GeoBmpEditActivity extends Activity implements IGeoInfoHandler, IViewHolder {
    private IGeoInfoHandler dialogResultConsumer;

    private Button buttonSave;
    private Button buttonCancel;

    private GeoBmpDtoAndroid currentItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geobmp_edit);

        this.dialogResultConsumer = dialogResultConsumer;
        this.setContentView(R.layout.geobmp_edit);

        this.buttonSave = (Button) this.findViewById(R.id.cmd_save);
        this.buttonCancel = (Button) this.findViewById(R.id.cmd_cancel);

        this.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveChangesAndExit(null);
            }

        });
        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                GeoBmpEditActivity.this.finish();
            }
        });

    }

    protected void onOkCLick() {
        this.save(this.currentItem);
        final Intent intent = this.getFinishIntent();
        this.setResult(R.id.cmd_save, intent);
        this.finish();
    }

    /**
     * @return Override with result intent with extra-paramaters for the caller
     */
    protected Intent getFinishIntent() {
        return null;
        /*
        final Intent intent = new Intent();
        intent.putExtra(Global.EXTRA_FILTER, this.filter);
        return intent;
        */
    }

    @Override
    public boolean onGeoInfo(IGeoPointInfo geoInfo) {
        this.currentItem = (GeoBmpDtoAndroid) geoInfo;

        load(this.currentItem);
        return true;
    }

    void load(GeoBmpDtoAndroid currentItem) {
        GeoBmpBinder.toGui(this, currentItem);
    }

    private void save(GeoBmpDtoAndroid currentItem) {
        GeoBmpBinder.fromGui(this, currentItem);
    }

    private void saveChangesAndExit(final IGeoInfoHandler owner) {
        save(this.currentItem);
        if (owner != null) {
            owner.onGeoInfo(this.currentItem);
        }
        this.finish();
    }

}
