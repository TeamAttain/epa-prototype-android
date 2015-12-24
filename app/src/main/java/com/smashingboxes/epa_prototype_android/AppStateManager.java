package com.smashingboxes.epa_prototype_android;

import android.content.Context;

import com.smashingboxes.epa_prototype_android.helpers.PreferenceHelper;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;

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

    public SimplePlace getPlace() {
        if (place == null) {
            place = mPreferenceHelper.getObject(PLACE_KEY, SimplePlace.class);
        }
        return place;
    }

    public void savePlace(SimplePlace place) {
        this.place = place;
        mPreferenceHelper.persistObjectSync(PLACE_KEY, place);
    }

    public void clearPlace() {
        mPreferenceHelper.removeObjectForKey(PLACE_KEY);
    }

}
