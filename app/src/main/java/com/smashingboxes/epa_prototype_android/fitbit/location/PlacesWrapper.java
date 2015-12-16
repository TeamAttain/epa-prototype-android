package com.smashingboxes.epa_prototype_android.fitbit.location;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Austin Lanier on 12/15/15.
 * Updated by
 */
public class PlacesWrapper extends GoogleApiWrapper {

    public PlacesWrapper(Activity context) {
        super(context);
    }

    public PendingResult<PlaceLikelihoodBuffer> getCurrentPlace() {
        return Places.PlaceDetectionApi.getCurrentPlace(getGoogleApiClient(), null);
    }

    @Override
    protected GoogleApiClient buildApiClient(Context context) {
        return new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
}
