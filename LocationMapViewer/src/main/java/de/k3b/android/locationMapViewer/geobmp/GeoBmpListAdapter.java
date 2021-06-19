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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoRepository;

/** Adapter to display IGeoPointInfo in a ListView. */
class GeoBmpListAdapter extends
        ArrayAdapter<GeoBmpDtoAndroid> {

    /**
     * Corresponds to filter ignore catagory or edit to create a new category
     */
    public static final int NO_CATEGORY = -1;

    /**
     * The Resource used for the adapter
     */
    private final int viewId;
    private GeoBmpDtoAndroid currentSelecion;

    private int colorSelected;
    private int colorNotSelected;

    private GeoBmpListAdapter(final Context context,
                              final int textViewResourceId, final List<GeoBmpDtoAndroid> items) {
        super(context, textViewResourceId, items);
        this.viewId = textViewResourceId;
        colorSelected = context.getResources().getColor(R.color.list_overlay_selected);
        colorNotSelected = context.getResources().getColor(R.color.list_overlay_not_selected);
    }

    public static ArrayAdapter<GeoBmpDtoAndroid> createAdapter(
            final Context context, final int viewId,
            final IGeoRepository repository,
            final GeoBmpDtoAndroid... additionalPoints
    ) {
        final List<GeoBmpDtoAndroid> items = repository.reload();

        if (additionalPoints != null) {
            for(int index = additionalPoints.length -1; index >= 0; index--) {
                final GeoBmpDtoAndroid item = additionalPoints[index];
                if (item != null) {
                    items.add(0, item);
                }
            }
        }
        return new GeoBmpListAdapter(context, viewId, items);
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        final GeoBmpDtoAndroid obj = this.getItem(position);

        View itemView = convertView;
        if (itemView == null) {
            itemView = this.createItemView();
        }

        this.setItemContent(itemView, obj);
        return itemView;
    }

    private View createItemView() {
        View itemView;
        final LayoutInflater vi = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = vi.inflate(this.viewId, null);

        return itemView;
    }

    private void setItemContent(final View itemView, final GeoBmpDtoAndroid dto) {
        if (dto != null) {
            GeoBmpBinder.toGui(itemView, dto);

            final int backgroundColor = ((this.currentSelecion == dto)) ? colorSelected : colorNotSelected;
            itemView.setBackgroundColor(backgroundColor);
        }
    }

    private void setTextViewContent(final TextView view, final String text) {
        if (view != null) {
            if ((text != null) && (text.length() > 0)) {
                view.setText(text);
            }
        }
    }

    public void setCurrentSelecion(GeoBmpDtoAndroid currentSelecion) {
        if (this.currentSelecion != currentSelecion) {
            this.currentSelecion = currentSelecion;
            notifyDataSetChanged();
        }
    }
}
