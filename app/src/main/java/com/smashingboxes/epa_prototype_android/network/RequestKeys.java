package com.smashingboxes.epa_prototype_android.network;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public enum RequestKeys {

    /**
     * Fitbit Authentication request keys
     */
    CLIENT_ID("client_id"), RESPONSE_TYPE("response_type"), SCOPE("scope"),
    REDIRECT_URI("redirect_uri"), EXPIRES_IN("expires_in"), PROMPT("prompt"),
    STATE("state");

    private final String paramValue;

    RequestKeys(String paramValue){
        this.paramValue = paramValue;
    }

    public String getParamValue(){
        return paramValue;
    }

}
