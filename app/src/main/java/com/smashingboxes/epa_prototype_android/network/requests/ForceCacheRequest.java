package com.smashingboxes.epa_prototype_android.network.requests;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.network.requests.BaseRequest;

/**
 * Created by Austin Lanier on 12/16/15.
 * Updated by
 * <p/>
 * The Fitbit API doesn't provide a cache control (from the headers)
 * "Cache-control" -> "no-cache, private", so we eventually get 429 errors.
 * <p/>
 * This request is used to force our own caching duration
 */
public class ForceCacheRequest<T> extends BaseRequest<T> {

    private static final int DEFAULT_MAX_AGE = 5 * 60; //5 minutes

    private int max_age_seconds;

    public ForceCacheRequest(int method, String url, Response.Listener<T> mListener, Response.ErrorListener errorListener,
                             ResponseParseStrategy<T> mParseStrategy) {
        this(method, url, mListener, errorListener, mParseStrategy, DEFAULT_MAX_AGE);
    }

    public ForceCacheRequest(int method, String url, Response.Listener<T> mListener, Response.ErrorListener errorListener,
                             ResponseParseStrategy<T> mParseStrategy, int forcedMaxAge) {
        super(method, url, mListener, errorListener, mParseStrategy);
        this.max_age_seconds = forcedMaxAge;
    }

    @Override
    protected Cache.Entry parseCacheEntry(NetworkResponse response) {
        response.headers.put("Cache-Control", String.format("private, max-age=%d", max_age_seconds));
        return super.parseCacheEntry(response);
    }

}
