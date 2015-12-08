package com.smashingboxes.epa_prototype_android.network.parsing;

import com.smashingboxes.epa_prototype_android.network.BaseRequest;

/**
 * Returns the response sent from the server as a JSON string
 */
public class StringParseStrategy implements BaseRequest.ResponseParseStrategy<String> {

    @Override
    public String parseResponse(String rawResponse) {
        return rawResponse;
    }

}