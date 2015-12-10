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

    /**
     * GET https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json
     *
     * @param userId
     * @param date
     */
    public static String getFitbitUserActivityUrl(String userId, String date){
        return String.format("https://api.fitbit.com/1/user/%s/activities/date/%s.json", userId, date);
    }

}
