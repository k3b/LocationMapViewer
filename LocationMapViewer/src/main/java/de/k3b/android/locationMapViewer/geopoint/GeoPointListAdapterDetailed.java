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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoRepository;

/** Adapter to display IGeoPointInfo in a ListView. */
class GeoPointListAdapterDetailed extends
        ArrayAdapter<GeoPointDto> {

    /**
     * Corresponds to filter ignore catagory or edit to create a new category
     */
    public static final int NO_CATEGORY = -1;

    /**
     * false means short disply only with categoryname but witout description
     */
    private final boolean withDescription;

    /**
     * The Resource used for the adapter
     */
    private final int viewId;

    /**
     * Workaround for recycled Items: Sometimes Text is not visible because
     * previous ItemHeight==0 is sometimes remembered.
     */
    private int itemHight = 0;

    private GeoPointListAdapterDetailed(final Context context,
                                        final int textViewResourceId, final List<GeoPointDto> items,
                                        final boolean withDescription) {
        super(context, textViewResourceId, items);
        this.withDescription = withDescription;
        this.viewId = textViewResourceId;
    }

    public static ArrayAdapter<GeoPointDto> createAdapter(
            final Context context, final int viewId,
            final boolean withDescription,
            final GeoPointDto firstElement,
            final IGeoRepository repository) {
        final List<GeoPointDto> categories = repository.load();

        if (firstElement != null) {
            categories.add(0, firstElement);
        }
        return new GeoPointListAdapterDetailed(context, viewId, categories,
                withDescription);
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        final GeoPointDto obj = this.getItem(position);

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

        if ((this.itemHight == 0) && (this.withDescription)) {
            this.itemHight = itemView.findViewById(R.id.description)
                    .getHeight();

        }
        return itemView;
    }

    private void setItemContent(final View itemView, final GeoPointDto dto) {
        if (dto != null) {
            final TextView nameView = (TextView) itemView
                    .findViewById(R.id.name);
            final TextView descriptionView = (TextView) itemView
                    .findViewById(R.id.description);
            final TextView activeView = (TextView) itemView
                    .findViewById(R.id.active);

            if (this.withDescription) {
                this.setTextViewContent(descriptionView, dto.getDescription());
                this.setTextViewContent(activeView, dto.getUri());
                this.setTextViewContent(nameView, dto.toString());
            } else {
                this.setTextViewContent(descriptionView, null);
                this.setTextViewContent(activeView, null);
                this.setTextViewContent(nameView, dto.toString());
            }
        }
    }

    private void setTextViewContent(final TextView view, final String text) {
        if (view != null) {
            if (this.itemHight == 0) {
                this.itemHight = view.getHeight();
            }
            if ((text != null) && (text.length() > 0)) {
                view.setText(text);
                if (this.itemHight == 0) {
                    this.itemHight = view.getHeight();
                }
                if ((this.itemHight > 0) && (view.getHeight() == 0)) {
                    // Workaround for recycled Items:
                    // Sometimes Text is not visible because previous
                    // ItemHeight==0 is sometimes remembered.
                    view.setHeight(this.itemHight);
                }
            } else {
                view.setHeight(0);
            }
        }
    }
}
