package com.smashingboxes.epa_prototype_android.network;


public class UrlGenerator {

    public static String getUserPath(String userId){
        return String.format("https://api.fitbit.com/1/user/%s", userId);
    }

    /**
     * @param userId - the user's id to retrieve, or - for the current logged-in user
     *
     * @return a GET request url
     */
    public static String getFitbitUserProfileUrl(String userId){
        return String.format("%s/profile.json", getUserPath(userId));
    }

    /**
     * GET https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json
     *
     * @param userId
     * @param date
     */
    public static String getFitbitUserActivitiesUrl(String userId, String date){
        return String.format("%s/activities/date/%s.json", getUserPath(userId), date);
    }

    /**
     * GET /1/user/[user-id]/[resource-path]/date/[date]/[period].json
     * GET /1/user/[user-id]/[resource-path]/date/[base-date]/[end-date].json
     *
     * @return
     */
    public static String getActivityTimeSeriesUrl(String userId, String resource_path, String start, String end){
        return String.format("%s/%s/date/%s/%s.json", getUserPath(userId), resource_path, start, end);
    }


}
