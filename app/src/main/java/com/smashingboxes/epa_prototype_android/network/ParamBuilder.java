package com.smashingboxes.epa_prototype_android.network;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder {

    private final Map<String, String> params;

    public ParamBuilder() {
        params = new HashMap<>();
    }

    public ParamBuilder put(String key, String value) {
        params.put(key, value);
        return this;
    }

    /**
     * Appends the currently set parameters
     * to the provided URI
     *
     * @return a Uri formatted string
     */
    public String buildEncodedUri(@NonNull Uri uri){
        return buildEncodedParametersInternal(uri);
    }

    /**
     * @return a Uri formatted parameter String
     */
    public String buildEncodedParameters(){
        return buildEncodedParametersInternal(Uri.EMPTY);
    }

    private String buildEncodedParametersInternal(Uri uri) {
        Uri.Builder builder = uri.buildUpon();
        for (Map.Entry<String, String> currentEntry : params.entrySet()) {
            builder.appendQueryParameter(currentEntry.getKey(), currentEntry.getValue());
        }
        return builder.toString();
    }

}