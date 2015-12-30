package com.smashingboxes.epa_prototype_android.fitbit.activity;

import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.fitbit.models.ActivityData;
import com.smashingboxes.epa_prototype_android.fitbit.models.TimeSeries;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/15/15.
 * Updated by
 *
 * An interface intended to the FitBit Activity endpoint @see <a href="https://dev.fitbit.com/docs/activity/">
 *     Activity & Exercise Logs</a> @see https://dev.fitbit.com/docs/activity/
 *
 */
public interface FitbitActivityApi {

    /**
     * GET https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json
     * <p/>
     * Retrieves the user's activity data by id
     *
     * @param userId                 - The encoded ID of the user. Use "-" (dash) for current logged-in user
     * @param date                   - The date in the format yyyy-MM-dd
     * @param fitbitActivityListener
     * @param errorListener
     */
    void getUserDailySummaryActivityData(String userId, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener);


    /**
     * GET https://api.fitbitcom/1/user/[user-id]/activities/date/[date].json
     * <p/>
     * Retrieves the user's activity data by CURRENT_USER_ID
     *
     * @param date
     * @param fitbitActivityListener
     * @param errorListener
     */
    void getCurrentUserDailySummaryActivityData(String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener);

    /**
     *
     * GET /1/user/[user-id]/[resource-path]/date/[date]/[period].json
     *
     * A general GET method for retrieving time series data via a resource path
     *
     * @param userId
     * @param resourcePath
     * @param start - RequestKeys.TODAY or yyyy-MM-dd
     * @param end - Period or yyyy-MM-dd
     * @param responseListener
     * @param errorListener
     */
    void getUserTimeSeriesTrackerData(String userId, ActivityResourcePath resourcePath, String start, String end, Response.Listener<ArrayList<TimeSeries>> responseListener, Response.ErrorListener errorListener);

    /**
     * GET /1/user/-/[resource-path]/date/[base-date]/[end-date].json
     *
     * A general get method for retrieving time series data via a resource path
     * for the current user
     *
     * @param resourcePath
     * @param period
     * @param responseListener
     * @param errorListener
     */
    void getCurrentUserTimeSeriesTrackerData(ActivityResourcePath resourcePath, Period period, Response.Listener<ArrayList<TimeSeries>> responseListener, Response.ErrorListener errorListener);

}
