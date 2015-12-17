package com.smashingboxes.epa_prototype_android.network.epa;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.models.AirQuality;
import com.smashingboxes.epa_prototype_android.models.SimplePlace;
import com.smashingboxes.epa_prototype_android.network.BaseRequest;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;
import com.smashingboxes.epa_prototype_android.network.RequestHandler;
import com.smashingboxes.epa_prototype_android.network.parsing.ArrayParseStrategy;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class EpaRequestManager implements EpaApi, RequestHandler {


    private static NetworkRequestManager delegate;

    private Object cancelTag;

    public EpaRequestManager(Context context, Object cancelTag) {
        if (delegate == null) {
            delegate = NetworkRequestManager.getInstance(context);
        }
        setCancelTag(cancelTag);
    }

    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
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
}
