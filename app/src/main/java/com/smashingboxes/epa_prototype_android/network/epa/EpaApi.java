package com.smashingboxes.epa_prototype_android.network.epa;

import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivityDetails;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 * <p/>
 * An interface to represent {@link https://github.com/smashingboxes/epa-prototype-api/wiki/API-Docs}
 */
public interface EpaApi {


    /**
     * Return an array of air quality data for the given location
     *
     * @param simplePlace
     * @param airQualityListener
     * @param errorListener
     */
    void getAirQualityData(SimplePlace simplePlace, Response.Listener<ArrayList<AirQuality>> airQualityListener, Response.ErrorListener errorListener);

    /**
     * Return an array of activities for a given location
     *
     * @param date
     */
    void getActivityDetails(String date, Response.Listener<ArrayList<EpaActivityDetails>> activitiesListener, Response.ErrorListener errorListener);


    /**
     * Post activities
     *
     * @param activities
     * @param activitiesListener
     * @param errorListener
     */
    void postActivities(List<EpaActivity> activities, Response.Listener<String> activitiesListener, Response.ErrorListener errorListener);


}
