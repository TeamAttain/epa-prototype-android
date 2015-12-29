package com.smashingboxes.epa_prototype_android.helpers;

import android.content.Context;

import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin Lanier on 12/29/15.
 * Updated by
 * <p/>
 * An Application scoped PreferenceHelper for keeping a local cache (for demo purposes, normally this would be done on the backend)
 * of indoor/outdoor location selections for the currently logged in user
 */
public class LocationHelper {

    private final PreferenceHelper mPreferenceHelper;

    private Map<String, EpaActivity.Location> mLocationMap = new HashMap<>();

    public LocationHelper(Context context) {
        this.mPreferenceHelper = new PreferenceHelper(context);
    }

    public void addLocation(String date, EpaActivity.Location location) {
        mPreferenceHelper.persistStringAsync(date, location.name());
        mLocationMap.put(date, location);
    }

    public EpaActivity.Location getLocation(String date) {
        EpaActivity.Location location = mLocationMap.get(date);
        if (location == null) {
            location = EpaActivity.Location.valueOf(mPreferenceHelper.getString(date, EpaActivity.Location.NONE.name()));
        }
        return location;
    }

    public void clear() {
        mLocationMap.clear();
        mPreferenceHelper.clear();
    }
}
