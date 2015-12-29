package com.smashingboxes.epa_prototype_android.network.epa.models;

import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 * Created by Austin Lanier on 12/23/15.
 * Updated by
 */
public class EpaActivity {

    public enum Location {
        INSIDE("inside"), OUTSIDE("outside");

        public final String name;

        Location(String name) {
            this.name = name;
        }

    }

    private final String lat;
    private final String lng;
    private final String location;

    public EpaActivity(String lat, String lng, Location location) {
        this.lat = lat;
        this.lng = lng;
        this.location = location.name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLocation() {
        return location;
    }

}
