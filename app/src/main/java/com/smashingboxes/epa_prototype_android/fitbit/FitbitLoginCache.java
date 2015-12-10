package com.smashingboxes.epa_prototype_android.fitbit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.smashingboxes.epa_prototype_android.LoginActivity;
import com.smashingboxes.epa_prototype_android.helpers.PreferenceHelper;
import com.smashingboxes.epa_prototype_android.models.FitbitAuthModel;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class FitbitLoginCache {

    private static final String TAG = FitbitLoginCache.class.getName();

    public static final String KEY_LOGGED_IN_MODEL = "com.smashingboxes.dudesolutions.KEY_LOGGED_IN_USER";

    private static FitbitLoginCache sMFitbitLoginCache;

    private FitbitAuthModel loginModel;
    private PreferenceHelper<FitbitAuthModel> mCacheHelper;

    public static synchronized FitbitLoginCache getInstance(Context context) {
        if (sMFitbitLoginCache == null) {
            sMFitbitLoginCache = new FitbitLoginCache(context.getApplicationContext());
        }
        return sMFitbitLoginCache;
    }

    private FitbitLoginCache(Context context) {
        mCacheHelper = new PreferenceHelper<>(context);
    }

    /**
     * Stores the last login model
     *
     * @param loginModel - the login model to persist
     */
    public void saveLoginModel(FitbitAuthModel loginModel) {
        mCacheHelper.persistObject(KEY_LOGGED_IN_MODEL, loginModel);
        this.loginModel = loginModel;
    }

    /**
     * Attempts to retrieve the last login model
     *
     * @return the previous login model, or null if not present or an error occurred
     */
    public FitbitAuthModel getLoginModel() {
        if (loginModel == null) {
            loginModel = mCacheHelper.getObject(KEY_LOGGED_IN_MODEL, FitbitAuthModel.class);
        }
        return loginModel;
    }

    public void clearLogin(){
        mCacheHelper.removeObjectForKey(KEY_LOGGED_IN_MODEL);
        loginModel = null;
    }

    public static void logout(Activity activity){
        FitbitLoginCache.getInstance(activity).clearLogin();

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(LoginActivity.ACTION_LOGOUT);
        activity.startActivity(intent);
    }

}
