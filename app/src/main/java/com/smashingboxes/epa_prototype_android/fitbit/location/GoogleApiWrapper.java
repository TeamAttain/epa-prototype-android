package com.smashingboxes.epa_prototype_android.fitbit.location;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin Lanier on 12/15/15.
 * Updated by
 */
public abstract class GoogleApiWrapper implements ConnectionListener {

    enum ConnectionStatus {
        CONNECTED, DISCONNECTED, CONNECTING, SUSPENDED, FAILED
    }

    private final GoogleApiClient mGoogleApiClient;
    private final List<ConnectionListener> connectionListeners;
    private final Object connectionStatusLock = new Object();
    private ConnectionStatus mConnectionStatus = ConnectionStatus.DISCONNECTED;

    public GoogleApiWrapper(Activity activity) {
        this.connectionListeners = new ArrayList<>();
        this.mGoogleApiClient = buildApiClient(activity);
    }

    protected abstract GoogleApiClient buildApiClient(Context context);

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        synchronized (connectionStatusLock) {
            mConnectionStatus = connectionStatus;
        }
    }

    public ConnectionStatus getConnectionStatus() {
        synchronized (connectionStatusLock) {
            return mConnectionStatus;
        }
    }

    public void connect() {
        mGoogleApiClient.connect();
        setConnectionStatus(ConnectionStatus.CONNECTING);
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
        setConnectionStatus(ConnectionStatus.DISCONNECTED);
    }

    public boolean addConnectionListener(ConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            return connectionListeners.add(listener);
        }
        return false;
    }

    public int removeConnectionListener(ConnectionListener listener) {
        int index = connectionListeners.indexOf(listener);
        if (index != -1) {
            connectionListeners.remove(index);
        }
        return index;
    }

    @Override
    public void onConnected(Bundle bundle) {
        setConnectionStatus(ConnectionStatus.CONNECTED);
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnected(bundle);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        setConnectionStatus(ConnectionStatus.SUSPENDED);
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionSuspended(i);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        setConnectionStatus(ConnectionStatus.FAILED);
        for (ConnectionListener listener : connectionListeners) {
            listener.onConnectionFailed(connectionResult);
        }
    }
}
