package com.smashingboxes.epa_prototype_android.network;


public class UrlGenerator {

    /**
     * @param userId - the user's id to retrieve, or - for the current logged-in user
     *
     * @return a GET request url
     */
    public static String getFitbitUserProfileUrl(String userId){
        return String.format("https://api.fitbit.com/1/user/%s/profile.json", userId);
    }

}
