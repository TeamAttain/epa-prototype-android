package com.smashingboxes.epa_prototype_android.models;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class Distance {

    private final String activity;
    private final float distance;

    public Distance(String activity, float distance) {
        this.activity = activity;
        this.distance = distance;
    }

    public String getActivity() {
        return activity;
    }

    public float getDistance() {
        return distance;
    }
}
