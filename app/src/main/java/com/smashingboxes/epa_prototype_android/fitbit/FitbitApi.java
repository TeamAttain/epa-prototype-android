package com.smashingboxes.epa_prototype_android.fitbit;

import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.models.ActivityData;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface FitbitApi {

    /**
     * GET https://api.fitbit.com/1/user/[user-id]/profile.json]
     *
     * Retrieves the user by id
     *
     * @param cancelTag
     * @param userId - The encoded ID of the user. Use "-" (dash) for current logged-in user
     * @param fitbitProfileListener
     * @param errorListener
     */
    void getUserProfile(Object cancelTag, String userId, Response.Listener<FitbitProfile> fitbitProfileListener,
                        Response.ErrorListener errorListener);


    /**
     *
     * GET https://api.fitbit.com/1/user/[user-id]/profile.json
     *
     * Retrieves the current logged in user's profile
     *
     * @param cancelTag
     * @param fitbitProfileListener
     * @param errorListener
     */
    void getCurrentUserProfile(Object cancelTag, Response.Listener<FitbitProfile> fitbitProfileListener,
                        Response.ErrorListener errorListener);


    /**
     *
     * GET https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json
     *
     * Retrieves the user's activity data by id
     *
     * @param cancelTag
     * @param userId - The encoded ID of the user. Use "-" (dash) for current logged-in user
     * @param date - The date in the format yyyy-MM-dd
     * @param fitbitActivityListener
     * @param errorListener
     */
    void getUserActivityData(Object cancelTag, String userId, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener);


}
