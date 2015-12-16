package com.smashingboxes.epa_prototype_android.fitbit.location;

import com.google.android.gms.common.api.GoogleApiClient;

public interface ConnectionListener extends GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

}