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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

/**
 * Activity to show a list of Favorites as {@link de.k3b.geo.api.GeoPointDto}-s with options to edit/delete/add.
 * <p/>
 * Created by k3b on 23.03.2015.
 */
public class FavoriteListActivity extends ListActivity implements
        IGeoInfoHandler {

    private static final String FAVORITES_FILE_NAME = "favorites.txt";

    // private static final int MENU_ADD_CATEGORY = Menu.FIRST;
    private static final int EDIT_MENU_ID = Menu.FIRST + 1;
    private static final int DELETE_MENU_ID = Menu.FIRST + 2;

    /**
     * parameter from caller to this: paramResourceIdActivityTitle resourceid of the list caption
     */
    private static GeoBmpDto[] paramAdditionalPoints;

    private ImageButton cmdZoomTo   ;
    private ImageButton cmdEdit     ;
    private ImageButton cmdSaveAs   ;
    private ImageButton cmdDelete   ;
    private ImageButton cmdHelp     ;

    private IGeoRepository<GeoBmpDto> repository = null;
    private GeoBmpDto currentItem;
    private GeoBmpEditDialog edit = null;

    /**
     * pseudo item as placeholder for creating a new items for current, initial, gps
     */
    private GeoBmpDto[] additionalPoints = null;

    /**
     * public api to show this list
     */
    public static void show(
            Context context,
            int idOnOkResultCode,
            GeoBmpDto... additionalPoints) {
        // parameters to be consumed in onCreate()
        FavoriteListActivity.paramAdditionalPoints = additionalPoints;

        final Intent intent = new Intent().setClass(context,
                FavoriteListActivity.class);

        if (idOnOkResultCode != 0) {
            ((Activity) context).startActivityForResult(intent,
                    idOnOkResultCode);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.favorite_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            initActionBar();
        }

        this.repository = new GeoBmpFileRepository(this.getDatabasePath(FAVORITES_FILE_NAME));
        this.setTitle(getString(R.string.title_favorite_list));

        this.additionalPoints = FavoriteListActivity.paramAdditionalPoints;
        FavoriteListActivity.paramAdditionalPoints = null;

        for (GeoBmpDto template : this.additionalPoints) {
            FavoriteUtil.markAsTemplate(template);
        }
        final ListView listView = this.getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final GeoBmpDto currenSelection = (GeoBmpDto) listView.getItemAtPosition(position);
                setCurrentItem(currenSelection);
            }
        });
        /* does not work: onItemSelected is never called
        listView.setItemsCanFocus(true);

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View childView,
                                       int position, long id) {
                setCurrentItem((GeoBmpDto) listView.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setCurrentItem(null);
            }
        });
*/
        // this.registerForContextMenu(listView);
        createButtons();

        this.reloadGuiFromRepository();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initActionBar() {
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createButtons() {
        cmdZoomTo = (ImageButton) findViewById(R.id.cmd_zoom_to);
        cmdEdit = (ImageButton) findViewById(R.id.cmd_edit);
        cmdSaveAs = (ImageButton) findViewById(R.id.cmd_save_as);
        cmdDelete = (ImageButton) findViewById(R.id.cmd_delete);
        cmdHelp = (ImageButton) findViewById(R.id.cmd_help);

        cmdZoomTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        cmdEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteListActivity.this.showGeoPointEditDialog(FavoriteListActivity.this.currentItem);
            }
        });

        cmdSaveAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavoriteListActivity.this.showGeoPointEditDialog(FavoriteUtil.createFavorite(FavoriteListActivity.this.currentItem));
            }
        });

        cmdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FavoriteListActivity.this.repository.load().remove(FavoriteListActivity.this.currentItem)) {
                    repository.save();
                    FavoriteListActivity.this.reloadGuiFromRepository();
                }
            }
        });

        cmdHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    private void setCurrentItem(GeoBmpDto newSelection) {
        final GeoBmpListAdapter listAdapter = (GeoBmpListAdapter) getListAdapter();
        listAdapter.setCurrentSelecion(newSelection);

        this.currentItem = newSelection;

        final boolean sel = newSelection != null;
        if (cmdZoomTo != null) {
            cmdZoomTo.setEnabled(sel);
            cmdEdit.setEnabled(sel && FavoriteUtil.isFavorite(newSelection));
            cmdSaveAs.setEnabled(sel && !FavoriteUtil.isFavorite(newSelection));
            cmdDelete.setEnabled(sel && FavoriteUtil.isFavorite(newSelection));
        }
    }

    private String getNewItemName() {
        return getString(R.string.point_new_item_placeholder);
    }

    private void reloadGuiFromRepository() {
        final ArrayAdapter<GeoBmpDto> adapter = GeoBmpListAdapter.createAdapter(this,
                R.layout.geobmp_list_view_row, repository, additionalPoints);
        this.setListAdapter(adapter);
        setCurrentItem(adapter.isEmpty() ? null : adapter.getItem(0));
    }

    /**
     * called by GeoPointEditDialog to inform list about changes
     *
     * @return true if item has been consumed
     */
    @Override
    public boolean onGeoInfo(IGeoPointInfo geoPointInfo) {
        if (FavoriteUtil.isValid(geoPointInfo)) {
            GeoBmpDto item = (GeoBmpDto) geoPointInfo;
            if (FavoriteUtil.isNew(item)) {
                item.setId(repository.createId());
                List<GeoBmpDto> items = this.repository.load();
                items.add(0, item);
            }
            this.repository.save();
            this.reloadGuiFromRepository();
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case EDIT_MENU_ID:
                // case MENU_ADD_CATEGORY:
                return this.edit;
        }

        return null;
    }

    public void showGeoPointEditDialog(final GeoBmpDto geoPointInfo) {
        if (this.edit == null) {
            this.edit = new GeoBmpEditDialog(this, this, R.layout.geobmp_edit_name);
            this.edit.setTitle(getString(R.string.title_favorite_edit));
        }
        this.edit.onGeoInfo(geoPointInfo);
        this.showDialog(FavoriteListActivity.EDIT_MENU_ID);
    }
}
