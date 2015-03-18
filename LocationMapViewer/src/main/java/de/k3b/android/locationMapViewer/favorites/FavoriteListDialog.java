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

package de.k3b.android.locationMapViewer.favorites;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

/**
 * Ask for favorite
 *
 * @author k3b
 */
public class FavoriteListDialog extends Dialog {

    private final ListView list;
    final IGeoRepository geoRepository;

    public FavoriteListDialog(final Context context, final int style,
                              final IGeoRepository geoRepository) {
        super(context, style);
        this.geoRepository = geoRepository;
        // setTitle("Punch In for Activity");
        this.list = new ListView(context);
        this.list.setAdapter(this.createCategoryListAdapter());
        final LinearLayout contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.addView(this.list);
        this.setContentView(contentView);
    }

    @Override
    public void show() {
        this.list.setAdapter(this.createCategoryListAdapter());

        super.show();
    }

    private ArrayAdapter<IGeoPointInfo> createCategoryListAdapter() {
        return new FavoriteListAdapter(this.getContext(),
                R.layout.name_list_view_row, geoRepository);
    }

    public FavoriteListDialog setCategoryCallback(
            final IGeoInfoHandler callback) {

        if (callback != null) {
            this.list.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(final AdapterView<?> arg0, final View arg1,
                                        final int position, final long arg3) {
                    final IGeoPointInfo newZoom = (IGeoPointInfo) FavoriteListDialog.this.list
                            .getItemAtPosition(position);

                    if (callback.onGeoInfo(newZoom)) {
                        FavoriteListDialog.this.hide();
                    }
                }
            });
        } else {
            this.list.setOnItemClickListener(null);
        }
        return this;
    }
}
