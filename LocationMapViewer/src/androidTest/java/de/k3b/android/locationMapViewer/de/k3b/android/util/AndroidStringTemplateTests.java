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

package de.k3b.android.locationMapViewer.de.k3b.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.test.ActivityTestCase;
import android.util.Log;

import junit.framework.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.android.locationMapViewer.R;
import de.k3b.android.util.AndroidStringTemplateEngine;

/**
 * Created by k3b on 02.04.2015.
 */
public class AndroidStringTemplateTests extends ActivityTestCase {
    private static final Logger logger = LoggerFactory.getLogger(AndroidStringTemplateTests.class);

    /**
     * Verifys that all template parameters in all string resources are vaild.
     */
    public void testAllTemplateLinks() {
        Context testContext = getInstrumentation().getTargetContext();
        Resources resourcesUnderTest = testContext.getResources();

        // collect all not found items
        AndroidStringTemplateEngine engine = new AndroidStringTemplateEngine(testContext);
        engine.sedDebugEnabled(true);
        for (int id : getAllStringResourceIds(R.string.class, resourcesUnderTest)) {
            try {
                engine.format(id);
            } catch (StackOverflowError err) {
            }
        }

        String errors = engine.getErrors();
        assertEquals(errors, 0, errors.length());
    }

    /**
     * Verifys that all template parameters in all string resources are vaild.
     */
    public void testSmokeTest() {
        Context testContext = getInstrumentation().getTargetContext();
        Resources resourcesUnderTest = testContext.getResources();

        AndroidStringTemplateEngine engine = new AndroidStringTemplateEngine(testContext);

        String valueToCheck = resourcesUnderTest.getString(R.string.help_bookmarks);
        String result = engine.format(valueToCheck);


        // logoutput does not work
        System.out.println(result);
        logger.info("string.help_bookmarks : \n{}", result);
        Log.i("TestRunner", result);

    }

    private int[] getAllStringResourceIds(Class<R.string> stringClass, Resources resourcesUnderTest) {
        return new int[]{
        R.string.about_content,
        R.string.about_content_about,
        R.string.about_summary,
        R.string.app_name,
        R.string.cancel,
        R.string.cmd_cancel,
        R.string.cmd_no,
        R.string.cmd_save,
        R.string.cmd_yes,
        R.string.bookmark_template_current,
        R.string.bookmark_template_gps,
        R.string.bookmark_template_initial,
        R.string.format_question_delete,
        R.string.label_select,
        R.string.no_items_found,
        R.string.prefs_cat_overlays_title,
        R.string.prefs_cluster_points,
        R.string.prefs_cluster_points_description,
        R.string.prefs_current_east,
        R.string.prefs_current_header,
        R.string.prefs_current_north,
        R.string.prefs_current_zoom,
        R.string.prefs_guestures_debug_enable,
        R.string.prefs_guestures_debug_enable_description,
        R.string.prefs_header_development,
        R.string.prefs_showLocation_summary,
        R.string.prefs_showMiniMap_summary,
        R.string.title_activity_geopoint_edit,
        R.string.title_activity_settings,
        R.string.title_confirm_delete,
        R.string.title_delete,
        R.string.title_bookmark_edit,
        R.string.title_bookmark_list,
        R.string.help_bookmarks
        };
    }

}
