package com.smashingboxes.epa_prototype_android.helpers;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;

/**
 * Created by Austin Lanier on 12/29/15.
 * Updated by
 *
 * An Activity scoped PreferenceHelper for keeping a local cache (for demo purposes, normally this would be done on the backend)
 * of indoor/outdoor location selections for the currently logged in user
 */
public class LocationHelper {

    private final PreferenceHelper mPreferenceHelper;

    public LocationHelper(Activity activityScope){
        this.mPreferenceHelper = new PreferenceHelper(activityScope, Context.MODE_PRIVATE);
    }

    public void addLocation(String date, EpaActivity.Location location){
        mPreferenceHelper.persistStringAsync(date, location.name());
    }

    public EpaActivity.Location forDate(String date){
        String storedLocation = mPreferenceHelper.getString(date, EpaActivity.Location.NONE.name());
        return EpaActivity.Location.valueOf(storedLocation);
    }

    public void clear(){
        mPreferenceHelper.clear();
    }
}
