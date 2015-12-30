package com.smashingboxes.epa_prototype_android.network.epa.models;

import android.support.annotation.NonNull;

import com.smashingboxes.epa_prototype_android.R;

import org.json.JSONObject;

/**
 * Created by Austin Lanier on 12/23/15.
 * Updated by
 */
public class EpaActivity {

    public enum Location {
        INSIDE(R.string.inside_lower, R.style.AppTheme_TextAppearance_BlackText), OUTSIDE(R.string.outside_lower, R.style.AppTheme_TextAppearance_BlackText),
        NONE(R.string.swipe_to_add_location, R.style.AppTheme_TextAppearance_GreyText);

        public final int titleRes;
        public final int style;

        Location(int titleRes, int style) {
            this.titleRes = titleRes;
            this.style = style;
        }

    }

    private final String lat;
    private final String lng;
    private final String location;

    public EpaActivity(String lat, String lng, Location location) {
        this.lat = lat;
        this.lng = lng;
        this.location = location.name().toLowerCase();
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
