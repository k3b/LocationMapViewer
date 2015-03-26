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

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import de.k3b.android.locationMapViewer.R;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.api.IGeoRepository;

/**
 * Activity to show a list of {@link de.k3b.geo.api.GeoPointDto} items with options to edit/delete/add.
 *
 * Created by k3b on 23.03.2015.
 */
public class GeoBmpListActivity extends ListActivity implements
        IGeoInfoHandler {
    // private static final int MENU_ADD_CATEGORY = Menu.FIRST;
    private static final int EDIT_MENU_ID = Menu.FIRST + 1;
    private static final int DELETE_MENU_ID = Menu.FIRST + 2;
    private static final String NEW_ITEM = "#newitem";

    /** parameter from caller to this: paramRepository where does data come from/go to */
    private static IGeoRepository<GeoBmpDto> paramRepository;
    /** parameter from caller to this: paramResourceIdActivityTitle resourceid of the list caption */
    private static int paramResourceIdActivityTitle;
    /** parameter from caller to this: paramResourceIdActivityTitle resourceid of the list caption */
    private static GeoBmpDto paramCurrentZoom;

    private IGeoRepository<GeoBmpDto> repository = null;
    private GeoBmpDto geoPointInfoClicked;
    private GeoBmpEditDialog edit = null;

    /** pseudo item as placeholder for creating a new item */
    private GeoBmpDto newGeoPointInfo = null;

    /** public api to show this list */
    public static void show(
            Context context,
            IGeoRepository<GeoBmpDto> repository,
            int resourceIdActivityTitle,
            int idOnOkResultCode,
            GeoBmpDto currentZoom) {
        // parameters to be consumed in onCreate()
        GeoBmpListActivity.paramRepository = repository;
        GeoBmpListActivity.paramResourceIdActivityTitle = resourceIdActivityTitle;
        GeoBmpListActivity.paramCurrentZoom = currentZoom;

        final Intent intent = new Intent().setClass(context,
                GeoBmpListActivity.class);

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
        this.setContentView(R.layout.geobmp_list);
        this.repository = paramRepository;
        paramRepository = null;
        this.setTitle(getString(paramResourceIdActivityTitle));

        this.newGeoPointInfo = GeoBmpListActivity.paramCurrentZoom;
        GeoBmpListActivity.paramCurrentZoom = null;

        setNewItemPlaceholder(this.newGeoPointInfo);
        this.registerForContextMenu(this.getListView());
        this.reloadGuiFromRepository();
    }

    /** sets data for NewItemPlaceholder */
    private GeoBmpDto setNewItemPlaceholder(final GeoBmpDto newGeoPointInfo) {
        newGeoPointInfo.setName(getNewItemName()).setId(NEW_ITEM);
        return newGeoPointInfo;
    }

    private String getNewItemName() {
        return getString(R.string.point_new_item_placeholder);
    }

    private void reloadGuiFromRepository() {
        this.setListAdapter(GeoBmpListAdapter.createAdapter(this,
                R.layout.geobmp_list_view_row, newGeoPointInfo, repository));
    }

    /**
     * called by GeoPointEditDialog to inform list about changes
     *
     * @return true if item has been consumed
     */
    @Override
    public boolean onGeoInfo(IGeoPointInfo geoPointInfo) {
        if (!isValid(geoPointInfo)) {
            this.showGeoPointEditDialog(null);
            return true;
        } else if (NEW_ITEM.compareTo(geoPointInfo.getId()) == 0) {
            if ( getNewItemName().compareTo(geoPointInfo.getName()) != 0) {
                List<GeoBmpDto> items = this.repository.load();
                GeoBmpDto newItem = (GeoBmpDto) this.newGeoPointInfo.clone();
                newItem.setBitmap(this.newGeoPointInfo.getBitmap()).setId(this.repository.createId());
                items.add(1, newItem);
                this.setNewItemPlaceholder(this.newGeoPointInfo);
                saveChangesToRepository();
            }
        } else {
            saveChangesToRepository();
        }
        this.reloadGuiFromRepository();
        return true;
    }

    private void saveChangesToRepository() {
        this.setNewItemPlaceholder(this.newGeoPointInfo);
        this.repository.save();
    }

    private boolean isValid(final IGeoPointInfo geoPointInfo) {
        return (geoPointInfo != null) && (geoPointInfo instanceof GeoBmpDto);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.geoPointInfoClicked = (GeoBmpDto) this
                .getListView()
                .getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

        if (this.geoPointInfoClicked != null) {
            menu.setHeaderTitle("" + this.geoPointInfoClicked.getName());
        }
        menu.add(0, GeoBmpListActivity.EDIT_MENU_ID, 0, R.string.cmd_edit);
        menu.add(0, GeoBmpListActivity.DELETE_MENU_ID, 0, R.string.cmd_delete);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case EDIT_MENU_ID:
                if (!isValid(this.geoPointInfoClicked)) {
                    this.showGeoPointEditDialog(null);
                } else {
                    this.showGeoPointEditDialog(this.geoPointInfoClicked);
                }
                return true;
            case DELETE_MENU_ID:
                if (this.repository.load().remove(this.geoPointInfoClicked)) {
                    this.reloadGuiFromRepository();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        /*
        menu.add(0, GeoBmpListActivity.MENU_ADD_CATEGORY, 0,
                "Create a new geoPointInfo.");
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        /*
        switch (item.getItemId()) {
            case MENU_ADD_CATEGORY:
                this.showGeoPointEditDialog(null);
                return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    public void showGeoPointEditDialog(final GeoBmpDto geoPointInfo) {
        if (this.edit == null) {
            this.edit = new GeoBmpEditDialog(this, this, R.layout.geobmp_edit_name);
        }
        this.edit.onGeoInfo(geoPointInfo);
        this.showDialog(GeoBmpListActivity.EDIT_MENU_ID);
    }
}
