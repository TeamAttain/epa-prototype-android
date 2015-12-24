package com.smashingboxes.epa_prototype_android.network.epa;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;
import com.smashingboxes.epa_prototype_android.network.RequestHandler;
import com.smashingboxes.epa_prototype_android.network.epa.models.ActivityPostRequest;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivityDetails;
import com.smashingboxes.epa_prototype_android.network.parsing.ArrayParseStrategy;
import com.smashingboxes.epa_prototype_android.network.requests.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class EpaRequestManager implements EpaApi, RequestHandler {

    private static NetworkRequestManager delegate;
    private static Gson gson = new Gson();
    private static DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private Object cancelTag;

    public EpaRequestManager(Context context, Object cancelTag) {
        if (delegate == null) {
            delegate = NetworkRequestManager.getInstance(context);
            gson = new Gson();
        }
        setCancelTag(cancelTag);
    }

    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
        request.setRetryPolicy(retryPolicy);
        delegate.addRequest(request, tag);
    }

    @Override
    public void cancelAllForTag(Object tag) {
        delegate.cancelAllForTag(tag);
    }

    public void setCancelTag(Object tag) {
        this.cancelTag = tag;
    }

    @Override
    public void getAirQualityData(SimplePlace simplePlace, Response.Listener<ArrayList<AirQuality>> airQualityListener, Response.ErrorListener errorListener) {
        String url = EpaUrlGenerator.getAirQualityUrl(simplePlace.getLat(), simplePlace.getLng());
        BaseRequest<ArrayList<AirQuality>> getAirQuality = new BaseRequest<>(Request.Method.GET, url, airQualityListener,
                errorListener, new ArrayParseStrategy<>(AirQuality.class));
        addRequest(getAirQuality, cancelTag);
    }


    @Override
    public void getActivityDetails(String date, Response.Listener<ArrayList<EpaActivityDetails>> activitiesListener, Response.ErrorListener errorListener) {
        String url = EpaUrlGenerator.getActivityDetailsUrl(date);
        BaseRequest<ArrayList<EpaActivityDetails>> getActivityDetails = new BaseRequest<>(Request.Method.GET, url,
                activitiesListener, errorListener, new ArrayParseStrategy<>(EpaActivityDetails.class));
        addRequest(getActivityDetails, cancelTag);
    }

    @Override
    public void postActivities(List<EpaActivity> activities, Response.Listener<String> activitiesListener, Response.ErrorListener errorListener) {
        ActivityPostRequest requestBody = new ActivityPostRequest(activities);
        String myJsonRequestBody = gson.toJson(requestBody);
        BaseRequest<String> objectRequest = new BaseRequest<>(Request.Method.POST, EpaUrlGenerator.getActivtiesUrl(),
                activitiesListener, errorListener, BaseRequest.NO_PARSE_STRAT);
        objectRequest.setRequestBody(myJsonRequestBody.getBytes());
        addRequest(objectRequest, cancelTag);
    }

}
