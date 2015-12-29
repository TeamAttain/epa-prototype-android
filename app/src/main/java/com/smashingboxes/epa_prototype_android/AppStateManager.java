package com.smashingboxes.epa_prototype_android;

import android.content.Context;

import com.smashingboxes.epa_prototype_android.helpers.PreferenceHelper;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin Lanier on 12/16/15.
 * Updated by
 */
public class AppStateManager {

    private static final String PLACE_KEY = "com.smashingboxes_epa_prototype_android.PLACE_KEY";

    private static AppStateManager ourInstance = new AppStateManager();

    public static AppStateManager getInstance(Context context) {
        if (ourInstance.mPreferenceHelper == null) {
            ourInstance.mPreferenceHelper = new PreferenceHelper(context.getApplicationContext());
        }
        return ourInstance;
    }

    private AppStateManager() {
    }

    private PreferenceHelper mPreferenceHelper;
    private SimplePlace place;
    private Map<String, EpaActivity.Location> mLocationMap = new HashMap<>();

    public SimplePlace getPlace() {
        if (place == null) {
            place = mPreferenceHelper.getObject(PLACE_KEY, SimplePlace.class);
        }
        return place;
    }

    public void savePlace(SimplePlace place) {
        this.place = place;
        mPreferenceHelper.persistObjectAsync(PLACE_KEY, place);
    }

    public void clearPlace() {
        mPreferenceHelper.removeObjectForKey(PLACE_KEY);
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
        clearPlace();
        mLocationMap.clear();
        mPreferenceHelper.clear();
    }

}
