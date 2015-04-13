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
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import de.k3b.android.locationMapViewer.R;
import de.k3b.android.locationMapViewer.constants.Constants;
import de.k3b.geo.api.IGeoInfoHandler;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;

/**
 * Activity to show a list of Bookmarks as {@link de.k3b.geo.api.GeoPointDto}-s with options to edit/delete/add.
 * <p/>
 * Created by k3b on 23.03.2015.
 */
public class BookmarkListActivity extends ListActivity implements
        IGeoInfoHandler, Constants {

    // private static final int MENU_ADD_CATEGORY = Menu.FIRST;
    private static final int EDIT_MENU_ID = Menu.FIRST + 1;

    /**
     * parameter from caller to this
     */
    private static GeoBmpDto[] paramAdditionalPoints;

    private ImageButton cmdZoomTo   = null;
    private ImageButton cmdEdit     = null;
    private ImageButton cmdSaveAs   = null;
    private ImageButton cmdDelete   = null;
    private ImageButton cmdHelp     = null;

    private MenuItem menuItemZoomTo  = null ;
    private MenuItem menuItemEdit    = null ;
    private MenuItem menuItemSaveAs  = null ;
    private MenuItem menuItemDelete  = null ;
    private MenuItem menuItemHelp    = null ;

    private GeoBmpEditDialog edit = null;

    private BookmarkListController bookMarkController;

    /**
     * public api to show this list
     */
    public static void show(
            Context context,
            int idOnOkResultCode,
            GeoBmpDto... additionalPoints) {
        // parameters to be consumed in onCreate()
        BookmarkListActivity.paramAdditionalPoints = additionalPoints;

        final Intent intent = new Intent().setClass(context,
                BookmarkListActivity.class);

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
        if (USE_ACTIONBAR) {
            this.setContentView(R.layout.bookmark_list_ab);
            initActionBar();
        } else {
            this.setContentView(R.layout.bookmark_list);
            createButtons();
        }

        this.bookMarkController = new BookmarkListController(this, this.getListView(), BookmarkListActivity.paramAdditionalPoints);
        this.bookMarkController.setSelChangedListener(new BookmarkListController.OnSelChangedListener() {
            @Override
            public void onSelChanged(GeoBmpDto newSelection) {
                BookmarkListActivity.this.onSelChanged(newSelection);
            }
        });

        BookmarkListActivity.paramAdditionalPoints = null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
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
                setResult(R.id.cmd_zoom_to, createIntent(bookMarkController.getCurrentItem()));
                finish();

            }
        });

        cmdEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookmarkListActivity.this.showGeoPointEditDialog(bookMarkController.getCurrentItem());
            }
        });

        cmdSaveAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookmarkListActivity.this.showGeoPointEditDialog(BookmarkUtil.createBookmark(bookMarkController.getCurrentItem()));
            }
        });

        cmdDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirm();
            }
        });

        cmdHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    /** called by options/actionBar-menu */
    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cmd_cancel   :
                this.setResult(R.id.cmd_cancel, createIntent(null));
                this.finish();
                return true;

            case R.id.cmd_zoom_to   :
                this.setResult(R.id.cmd_zoom_to, createIntent(this.bookMarkController.getCurrentItem()));
                this.finish();
                return true;

            case R.id.cmd_edit      :
                BookmarkListActivity.this.showGeoPointEditDialog(bookMarkController.getCurrentItem());
                return true;

            case R.id.cmd_save_as   :
                BookmarkListActivity.this.showGeoPointEditDialog(BookmarkUtil.createBookmark(BookmarkListActivity.this.bookMarkController.getCurrentItem()));
                return true;

            case R.id.cmd_delete    :
                deleteConfirm();
                return true;

            case R.id.cmd_help:
                // this.showDialog(R.id.cmd_help);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private Intent createIntent(GeoBmpDto currentItem) {
        if (currentItem == null) return null;
        return new Intent(Intent.ACTION_PICK , Uri.parse(new GeoUri(GeoUri.OPT_DEFAULT).toUriString(this.bookMarkController.getCurrentItem())));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (USE_ACTIONBAR) {
            // Inflate the menu items for use in the action bar
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.bookmark_list_ab, menu);

            menuItemZoomTo  = menu.findItem(R.id.cmd_zoom_to)   ;
            menuItemEdit    = menu.findItem(R.id.cmd_edit)      ;
            menuItemSaveAs  = menu.findItem(R.id.cmd_save_as)   ;
            menuItemDelete  = menu.findItem(R.id.cmd_delete)    ;
            menuItemHelp    = menu.findItem(R.id.cmd_help)      ;

        }
        return super.onCreateOptionsMenu(menu);
    }


    /** called after selection has changed */
    private void onSelChanged(GeoBmpDto newSelection) {
        final boolean sel = (newSelection != null);
        if ((USE_ACTIONBAR) && (menuItemZoomTo != null)) {
            onActionIconsChanged();
        } else if (cmdZoomTo != null) {
            cmdZoomTo.setEnabled(sel);
            cmdEdit.setEnabled(sel && BookmarkUtil.isBookmark(newSelection));
            cmdSaveAs.setEnabled(sel && !BookmarkUtil.isBookmark(newSelection));
            cmdDelete.setEnabled(sel && BookmarkUtil.isBookmark(newSelection));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onActionIconsChanged() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (USE_ACTIONBAR) {
            updateMenu(this.bookMarkController.getCurrentItem());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateMenu(GeoBmpDto newSelection) {
        if (menuItemZoomTo != null) {
            final boolean sel = (newSelection != null);
            enable(menuItemZoomTo, true, sel);
            enable(menuItemEdit, true, sel && BookmarkUtil.isBookmark(newSelection));
            enable(menuItemSaveAs, true, sel && !BookmarkUtil.isBookmark(newSelection));
            enable(menuItemDelete, false, sel && BookmarkUtil.isBookmark(newSelection));
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void enable(MenuItem menuItem, boolean alwaysIfEnabled, boolean enabled) {
        menuItem.setEnabled(enabled);

        if (enabled) {
            menuItem.setShowAsAction(alwaysIfEnabled ? MenuItem.SHOW_AS_ACTION_ALWAYS : MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER); //
        }
//      menuItem.setVisible(enabled);
    }

    /**
     * called by GeoPointEditDialog to inform list about changes
     *
     * @return true if item has been consumed
     */
    @Override
    public boolean onGeoInfo(IGeoPointInfo geoPointInfo) {
        this.bookMarkController.update(geoPointInfo);
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
            this.edit.setTitle(getString(R.string.title_bookmark_edit));
        }
        this.edit.onGeoInfo(geoPointInfo);
        this.showDialog(BookmarkListActivity.EDIT_MENU_ID);
    }

    /** before deleting: "Are you shure?" */
    private void deleteConfirm() {
        final GeoBmpDto currentItem = this.bookMarkController.getCurrentItem();
        if (currentItem != null) {
            final String message = String.format(
                    this.getString(R.string.format_question_delete).toString(),
                    currentItem.getName() +"\n"+ currentItem.getSummary());

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.title_confirm_delete);
            Bitmap bitmap = currentItem.getBitmap();

            if (bitmap != null) {
                BitmapDrawable drawable = (bitmap == null) ? null : new BitmapDrawable(getResources(), bitmap);
                builder.setIcon(drawable);
            }

            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.cmd_yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int id) {
                                    BookmarkListActivity.this.bookMarkController.deleteCurrent();
                                }
                            }
                    )
                    .setNegativeButton(R.string.cmd_no,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        final DialogInterface dialog,
                                        final int id) {
                                    dialog.cancel();
                                }
                            }
                    );

            final AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
