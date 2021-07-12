/*
 * Copyright (c) 2021 by k3b.
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

import android.os.Build;
import android.text.Html;

import java.util.regex.Pattern;


public class HtmlUtils {
    // include multiline
    private static Pattern isHtml = Pattern.compile("<[a-zA-Z]+");

    // Pattern.DOTALL == mulitline matching; ".*?" anything non-greedy
    private static Pattern htmlComment = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static Pattern htmlImage = Pattern.compile("<img.*?>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    public static CharSequence interpreteHtml(String html) {
        if (html != null && isHtml.matcher(html).find()) {
            String htmlSanitzied = htmlComment.matcher(html).replaceAll("");
            htmlSanitzied = htmlImage.matcher(htmlSanitzied).replaceAll("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(htmlSanitzied, Html.FROM_HTML_MODE_COMPACT);
            } else {
                return Html.fromHtml(htmlSanitzied);
            }
        }
        return html;
    }
}
