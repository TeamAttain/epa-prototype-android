package com.smashingboxes.epa_prototype_android.network.epa;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class EpaUrlGenerator {

    private static final String BASE_URL = "http://epa-prototype.sbox.es/api";

    public static final String getAirQualityUrl(double lat, double lng){
        return BASE_URL + String.format("/air_quality_observations?lat=%1$,.2f&lng=%2$,.2f", lat, lng);
    }

    public static final String getActivtiesUrl(){
        return BASE_URL + "/api/activities";
    }

    public static final String getActivityDetailsUrl(String date){
        return EpaUrlGenerator.getActivtiesUrl() + String.format("?date=%s", date);
    }


}
