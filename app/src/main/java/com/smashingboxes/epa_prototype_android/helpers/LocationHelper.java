package com.smashingboxes.epa_prototype_android.helpers;

import android.app.Activity;
import android.content.Context;

import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;

/**
 * Created by Austin Lanier on 12/29/15.
 * Updated by
 *
 * An Activity Scoped preference helper cache for keeping local cache (for demo purposes, normally this would be done on the backend)
 * of indoor/outdoor values for the currently logged in user
 */
public class LocationHelper {

    private final PreferenceHelper mPreferenceHelper;

    public LocationHelper(Activity activityScope){
        this.mPreferenceHelper = new PreferenceHelper(activityScope, Context.MODE_PRIVATE);
    }

    public void addLocation(String date, EpaActivity.Location location){
        //mPreferenceHelper.
    }
}
