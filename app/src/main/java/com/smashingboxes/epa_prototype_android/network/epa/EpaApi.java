package com.smashingboxes.epa_prototype_android.network.epa;

import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.models.AirQuality;
import com.smashingboxes.epa_prototype_android.models.SimplePlace;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public interface EpaApi {


    /**
     *
     * Return an array of air quality data for the given location
     *
     * @param simplePlace
     * @param airQualityListener
     * @param errorListener
     */
    void getAirQualityData(SimplePlace simplePlace, Response.Listener<ArrayList<AirQuality>> airQualityListener, Response.ErrorListener errorListener);


}
