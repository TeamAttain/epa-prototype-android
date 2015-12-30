package com.smashingboxes.epa_prototype_android.fitbit;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.fitbit.activity.ActivityResourcePath;
import com.smashingboxes.epa_prototype_android.fitbit.activity.Period;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.models.ActivityData;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitAuthModel;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.fitbit.models.TimeSeries;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;
import com.smashingboxes.epa_prototype_android.network.RequestHandler;
import com.smashingboxes.epa_prototype_android.network.RequestKeys;
import com.smashingboxes.epa_prototype_android.network.parsing.ArrayParseStrategy;
import com.smashingboxes.epa_prototype_android.network.parsing.ClassParseStrategy;
import com.smashingboxes.epa_prototype_android.network.requests.BaseRequest;
import com.smashingboxes.epa_prototype_android.network.requests.ForceCacheRequest;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class FitbitRequestManager implements FitbitApi, RequestHandler<BaseRequest<?>> {

    private static NetworkRequestManager delegate;

    private FitbitAuthModel authencationModel;
    private Object cancelTag;

    public FitbitRequestManager(Context context, FitbitAuthModel authencationModel) {
        if (delegate == null) {
            delegate = NetworkRequestManager.getInstance(context);
        }
        this.authencationModel = authencationModel;
    }

    public FitbitRequestManager(Context context, FitbitAuthModel authencationModel, Object cancelTag) {
        this(context, authencationModel);
        setCancelTag(cancelTag);
    }

    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
        if (authencationModel == null || authencationModel.isExpired()) {
            FitbitLoginCache.logout(delegate.getContext());
            return;
        }
        request.setHeaders(authencationModel.getAuthHeaders());
        delegate.addRequest(request, tag);
    }

    @Override
    public void cancelAllForTag(Object tag) {
        delegate.cancelAllForTag(tag);
    }

    public void setCancelTag(Object tag) {
        this.cancelTag = tag;
    }

    @Override
    public void getUserProfile(String userId, Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        String url = FitbitUrlGenerator.getFitbitUserProfileUrl(userId);
        BaseRequest<FitbitProfile> getProfile = new ForceCacheRequest<>(Request.Method.GET, url,
                fitbitProfileListener, errorListener, new ClassParseStrategy<>(FitbitProfile.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserProfile(Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        getUserProfile(CURRENT_USER_ID, fitbitProfileListener, errorListener);
    }

    @Override
    public void getUserDailySummaryActivityData(String userId, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        String url = FitbitUrlGenerator.getFitbitUserActivitiesUrl(userId, date);
        BaseRequest<ActivityData> getProfile = new ForceCacheRequest<>(Request.Method.GET, url,
                fitbitActivityListener, errorListener, new ClassParseStrategy<>(ActivityData.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserDailySummaryActivityData(String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        getUserDailySummaryActivityData(CURRENT_USER_ID, date, fitbitActivityListener, errorListener);
    }

    @Override
    public void getUserTimeSeriesTrackerData(String userId, ActivityResourcePath resourcePath, String start, String end, Response.Listener<ArrayList<TimeSeries>> responseListener, Response.ErrorListener errorListener) {
        String url = FitbitUrlGenerator.getActivityTimeSeriesUrl(userId, resourcePath.getFullPath(), start, end);
        BaseRequest<ArrayList<TimeSeries>> getTimeSeriesData = new ForceCacheRequest<>(Request.Method.GET, url, responseListener,
                errorListener, new ArrayParseStrategy<>(TimeSeries.class, resourcePath.getArrayResponseKey()));
        addRequest(getTimeSeriesData, cancelTag);
    }

    @Override
    public void getCurrentUserTimeSeriesTrackerData(ActivityResourcePath resourcePath, Period period, Response.Listener<ArrayList<TimeSeries>> responseListener, Response.ErrorListener errorListener) {
        getUserTimeSeriesTrackerData(CURRENT_USER_ID, resourcePath, RequestKeys.TODAY.getParamValue(), period.durationKey, responseListener, errorListener);
    }
}
