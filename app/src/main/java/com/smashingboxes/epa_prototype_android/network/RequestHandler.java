package com.smashingboxes.epa_prototype_android.network;

import com.android.volley.Request;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface RequestHandler<T extends Request<?>> {
    void addRequest(T request, Object tag);
    void cancelAllForTag(Object tag);
}
