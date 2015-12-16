package com.smashingboxes.epa_prototype_android.fitbit.user;

import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface FitbitUserApi {

    String CURRENT_USER_ID = "-";

    /**
     * GET https://api.fitbit.com/1/user/[user-id]/profile.json]
     * <p/>
     * Retrieves the user by id
     *
     * @param userId                - The encoded ID of the user. Use "-" (dash) for current logged-in user
     * @param fitbitProfileListener
     * @param errorListener
     */
    void getUserProfile(String userId, Response.Listener<FitbitProfile> fitbitProfileListener,
                        Response.ErrorListener errorListener);


    /**
     * GET https://api.fitbit.com/1/user/[user-id]/profile.json
     * <p/>
     * Retrieves the current logged in user's data by CURRENT_USER_ID
     *
     * @param fitbitProfileListener
     * @param errorListener
     */
    void getCurrentUserProfile(Response.Listener<FitbitProfile> fitbitProfileListener,
                               Response.ErrorListener errorListener);


}
