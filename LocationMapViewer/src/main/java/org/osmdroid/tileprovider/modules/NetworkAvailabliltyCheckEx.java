package org.osmdroid.tileprovider.modules;

import android.content.Context;

import org.osmdroid.tileprovider.IMapTileProviderCallback;

/**
 * Same as original NetworkAvailabliltyCheck but obeys the useDataConnection flag.<br/>
 *
 * Created by k3b on 02.02.2015.
 */
public class NetworkAvailabliltyCheckEx extends NetworkAvailabliltyCheck {
    private IMapTileProviderCallback useDataConnectionSource;

    public NetworkAvailabliltyCheckEx(Context aContext) {
        super(aContext);
    }

    public void setUseDataConnectionSource(IMapTileProviderCallback useDataConnectionSource) {
        this.useDataConnectionSource = useDataConnectionSource;
    }

    /**
     * Whether to use the network connection if it's available.
     */
    private boolean useDataConnection() {
        return ((this.useDataConnectionSource != null) && this.useDataConnectionSource.useDataConnection() );
    }

    @Override
    public boolean getNetworkAvailable() {
        return useDataConnection() && super.getNetworkAvailable();
    }

    @Override
    public boolean getWiFiNetworkAvailable() {
        return useDataConnection() && super.getWiFiNetworkAvailable();
    }

    @Override
    public boolean getCellularDataNetworkAvailable() {
        return useDataConnection() && super.getCellularDataNetworkAvailable();
    }
}
