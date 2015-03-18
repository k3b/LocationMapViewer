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

package de.k3b.geo.api;

import java.util.List;

/**
 * Abstract Repository to load/save List<IGeoPointInfo> .
 *
 * Created by k3b on 17.03.2015.
 */
public interface IGeoRepository {

    /** load from repository
     *
     * @return data loaded
     */
    List<IGeoPointInfo> load();

    /** save back to repository
     *
     * @return false: error.
     */
    boolean save();
}
