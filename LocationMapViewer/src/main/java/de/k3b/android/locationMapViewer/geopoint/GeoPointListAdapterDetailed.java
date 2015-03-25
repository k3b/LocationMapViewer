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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

/** Adapter to display IGeoPointInfo in a ListView. */
class GeoPointListAdapterDetailed extends
        ArrayAdapter<GeoPointDtoWithBitmap> {

    /**
     * Corresponds to filter ignore catagory or edit to create a new category
     */
    public static final int NO_CATEGORY = -1;

    /**
     * The Resource used for the adapter
     */
    private final int viewId;

    private GeoPointListAdapterDetailed(final Context context,
                                        final int textViewResourceId, final List<GeoPointDtoWithBitmap> items) {
        super(context, textViewResourceId, items);
        this.viewId = textViewResourceId;
    }

    public static ArrayAdapter<GeoPointDtoWithBitmap> createAdapter(
            final Context context, final int viewId,
            final GeoPointDtoWithBitmap firstElement,
            final IGeoRepository repository) {
        final List<GeoPointDtoWithBitmap> items = repository.reload();

        if (firstElement != null) {
            items.add(0, firstElement);
        }
        return new GeoPointListAdapterDetailed(context, viewId, items);
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        final GeoPointDtoWithBitmap obj = this.getItem(position);

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

    private void setItemContent(final View itemView, final GeoPointDtoWithBitmap dto) {
        if (dto != null) {
            final TextView nameView = (TextView) itemView
                    .findViewById(R.id.name);
            final TextView descriptionView = (TextView) itemView
                    .findViewById(R.id.description);
            final ImageView thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            this.setTextViewContent(descriptionView, toString(dto));
            this.setTextViewContent(nameView, dto.getName());

            thumbnail.setImageBitmap(dto.getBitmap());
        }
    }

    private void setTextViewContent(final TextView view, final String text) {
        if (view != null) {
            if ((text != null) && (text.length() > 0)) {
                view.setText(text);
            }
        }
    }

    private static final DecimalFormat latLonFormatter = new DecimalFormat("#.#######", new DecimalFormatSymbols(Locale.ENGLISH));

    /** formatting helper: */
    private static String toString(IGeoPointInfo geoPoint) {
        return " (" +
                latLonFormatter.format(geoPoint.getLatitude()) + "/" +
                latLonFormatter.format(geoPoint.getLongitude())+ ") z=" + geoPoint.getZoomMin();
    }
}
