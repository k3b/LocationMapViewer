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

package de.k3b.util;

public class ImageResize  {
    public static Double getResizeFactor(double width, double height, double targetSize) {
        if (width > 0 && height > 0 && (width > targetSize || height > targetSize)) {
            double scale = (width < height) ? width / height : height / width;
            return scale;
        }

        return null;
    }
}
