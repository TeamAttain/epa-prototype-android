package com.smashingboxes.epa_prototype_android.network;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface RequestHandler {
    void addRequest(BaseRequest<?> request, Object tag);
    void cancelAllForTag(Object tag);
}
