package com.smashingboxes.epa_prototype_android.models;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class LoginModel {
    private final String loginString;

    public LoginModel(String loginString){
        this.loginString = loginString;
    }

    public String getLoginString(){
        return loginString;
    }
}
