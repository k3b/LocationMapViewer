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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

class FavoriteListAdapter extends ArrayAdapter<IGeoPointInfo> {

    /**
     * The Resource used for the adapter
     */
    private final int viewId;

    public FavoriteListAdapter(final Context context,
                                final int textViewResourceId,
                                final IGeoRepository repository) {
        super(context, textViewResourceId, repository.load());
        this.viewId = textViewResourceId;
    }

    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        final IGeoPointInfo obj = this.getItem(position);

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

    private void setItemContent(final View itemView, final IGeoPointInfo obj) {
        if (obj != null) {
            final TextView nameView = (TextView) itemView
                    .findViewById(R.id.name);
            this.setTextViewContent(nameView, obj.toString());
        }
    }

    private void setTextViewContent(final TextView view, final String text) {
        if (view != null) {
            view.setText(text);
        }
    }
}
