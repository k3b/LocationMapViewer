package de.k3b.android.locationMapViewer.constants;

import android.os.Build;

/**
 * This class contains constants used by the application.
 */
public interface Constants {
    /** false: old android-2.3 or below that does not support actionbars */
    public static final boolean USE_ACTIONBAR = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB);

    // ===========================================================
    // keys for preferences
    // ===========================================================

    public static final String PREFS_TILE_SOURCE = "tilesource";
    public static final String PREFS_SCROLL_X = "scrollX";
    public static final String PREFS_SCROLL_Y = "scrollY";
    public static final String PREFS_ZOOM_LEVEL = "zoomLevel";
    public static final String PREFS_SHOW_LOCATION = "showLocation";
    public static final String PREFS_SHOW_MINIMAP = "showMiniMap";
    public static final String PREFS_CLUSTER_POINTS = "clusterPoints";

    public static final String PREFS_CURRENT_ZOOMLEVEL = "currentZoom";
    public static final String PREFS_CURRENT_NORTH = "currentNorth";
    public static final String PREFS_CURRENT_EAST = "currentEast";

    // public static final String PREFS_SHOW_GUESTURES = "guesturesEnable";
    public static final String PREFS_DEBUG_GUESTURES = "guesturesDebug";

    // ===========================================================
    // Methods
    // ===========================================================
}
