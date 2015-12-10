package com.smashingboxes.epa_prototype_android.fitbit;

import com.android.volley.Request;
import com.android.volley.Response;
import com.smashingboxes.epa_prototype_android.models.ActivityData;
import com.smashingboxes.epa_prototype_android.models.FitbitAuthModel;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.network.BaseRequest;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;
import com.smashingboxes.epa_prototype_android.network.RequestHandler;
import com.smashingboxes.epa_prototype_android.network.UrlGenerator;
import com.smashingboxes.epa_prototype_android.network.parsing.ClassParseStrategy;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class FitbitRequestManager implements FitbitApi, RequestHandler {

    private static NetworkRequestManager delegate;
    private FitbitAuthModel authencationModel;

    public FitbitRequestManager(FitbitAuthModel authencationModel){
        if(delegate == null){
            delegate = NetworkRequestManager.getInstance();
        }
        this.authencationModel = authencationModel;
    }

    @Override
    public void addRequest(BaseRequest<?> request, Object tag) {
        if(authencationModel == null || authencationModel.isExpired()){
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

    @Override
    public void getUserProfile(Object cancelTag, String userId, Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        String url = UrlGenerator.getFitbitUserProfileUrl(userId);
        BaseRequest<FitbitProfile> getProfile = new BaseRequest<>(Request.Method.GET, url,
                fitbitProfileListener, errorListener, new ClassParseStrategy<>(FitbitProfile.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserProfile(Object cancelTag, Response.Listener<FitbitProfile> fitbitProfileListener, Response.ErrorListener errorListener) {
        getUserProfile(cancelTag, CURRENT_USER_ID, fitbitProfileListener, errorListener);
    }

    @Override
    public void getUserActivityData(Object cancelTag, String userId, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        String url = UrlGenerator.getFitbitUserActivityUrl(userId, date);
        BaseRequest<ActivityData> getProfile = new BaseRequest<>(Request.Method.GET, url,
                fitbitActivityListener, errorListener, new ClassParseStrategy<>(ActivityData.class));
        addRequest(getProfile, cancelTag);
    }

    @Override
    public void getCurrentUserActivityData(Object cancelTag, String date, Response.Listener<ActivityData> fitbitActivityListener, Response.ErrorListener errorListener) {
        getUserActivityData(cancelTag, CURRENT_USER_ID, date, fitbitActivityListener, errorListener);
    }
}
