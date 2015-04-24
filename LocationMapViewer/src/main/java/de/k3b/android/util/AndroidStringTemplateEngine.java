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

package de.k3b.android.util;

import android.content.Context;
import android.content.res.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import de.k3b.android.GuiUtil;
import de.k3b.util.StringTemplateEngine;

/**
 * Created by k3b on 01.04.2015.
 */
public class AndroidStringTemplateEngine extends StringTemplateEngine implements StringTemplateEngine.IValueResolver {
    private static final Logger logger = LoggerFactory.getLogger(AndroidStringTemplateEngine.class);

    private static final String CLASS_STRING    = "string";
    private static final String CLASS_DRAWABLE  = "drawable";
    private static final String CLASS_SYSTEM    = "system";

    /** translates from CLASS_xxx to corresponding handler for that class. */
    private Map<String, IValueResolver> valueHandler = new HashMap<String, IValueResolver>();

    /** android specific data needed to access android resources */
    private final Resources resources;
    private final String packageName;
    private final Context context;

    public AndroidStringTemplateEngine(Context context) {
        super(null);
        this.valueResolver = this;
        this.context = context;
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        initValueHandler();
    }

    private void initValueHandler() {
        valueHandler.put(CLASS_STRING, new IValueResolver() {
            @Override
            public String get(String className, String propertyName, String templateParameter) {
                return getString(className, propertyName, templateParameter);
            }
        });
        valueHandler.put(CLASS_DRAWABLE, new IValueResolver() {
            @Override
            public String get(String className, String propertyName, String templateParameter) {
                return getDrawable(className, propertyName, templateParameter);
            }
        });
        valueHandler.put(CLASS_SYSTEM, new IValueResolver() {
            @Override
            public String get(String className, String propertyName, String templateParameter) {
                return getSystem(className, propertyName, templateParameter);
            }
        });
    }

    public String format(int idStringResource) {
        String value = this.resources.getString(idStringResource);
        String context = "-";
        if (debugStack != null) {
            context = (value != null)
                    ? this.resources.getResourceName(idStringResource)
                    : "unknown string.#0x" + Integer.toHexString(idStringResource);
        }
        debugPush(context);
        value = format(value);
        onResolverResult(context, value);
        debugPop();
        return value;
    }

    /**
     * IValueResolver implementation
     * ${drawable.person} coresponds to R.drawable.person as <img src=... />
     * ${string.app_name} corresponds to R.string.app_name
     * ${template.some_html} corresponds to R.string.some_html where ${..} will be interpreted
     * ${some_link.some_title} corresponds to R.string.some_title where as <a href='help:some_link'>${template.some_title}</a>
     * on click on the linke the webview will be populated with ${template.some_link}
     * */
    @Override
    public String get(String className, String propertyName, String templateParameter) {
        IValueResolver handler = valueHandler.get(className);

        if (handler != null) {
            return handler.get(className, propertyName, templateParameter);
        }

        trace(className, propertyName, "???", "???", null);
        return null;
    }

    /**
     * ${system.version_name} : get appname + version
     * */
    private String getSystem(String className, String propertyName, String templateParameter) {
        if ("version_name".compareTo(propertyName) == 0) {
            return GuiUtil.getAppVersionName(context);
        }
        return null;
    }

    /**
     * ${string.some_text} translates to content of R.string.some_text)
     * */
    private String getString(String className, String propertyName, String templateParameter) {
        int id = resources.getIdentifier (propertyName, CLASS_STRING, this.packageName);

        String value = (id != 0) ? resources.getString(id) : null;
        if (hasParameters(value)) {
            // expand parameters
            value = format(value);
        }
        trace(className, propertyName, className, propertyName, value);
        return value;
    }

    /** ${drawable.person} coresponds to R.drawable.person as <img src=... /> */
    private String getDrawable(String className, String propertyName, String templateParameter) {
        int id = resources.getIdentifier (propertyName, className, this.packageName);

        String value = (id != 0) ? new StringBuffer().append("<img src=`android.resource://").append(
                this.packageName).append(
                "/").append(
                className).append(
                "/").append(
                className).append(
                "' />").toString() : null;

        trace(className, propertyName, className, propertyName, value);
        return value;
    }

    /** diagnostic output as debug if found or warning if not found */
    private void trace(String className, String propertyName, String resTypName, String resIdName, String value) {
        if (value == null) {
            logger.warn("parameter ${{}.{}} not found in {}.R.{}.{}", className, propertyName, this.packageName, resTypName, resIdName);
        } else {
            logger.debug("parameter ${{}.{}} from {}.R.{}.{} => {}", className, propertyName, this.packageName, resTypName, resIdName, value);
        }
    }
}
