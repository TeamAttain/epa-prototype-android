package com.smashingboxes.epa_prototype_android.fitbit.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.smashingboxes.epa_prototype_android.LoginActivity;
import com.smashingboxes.epa_prototype_android.helpers.LocationHelper;
import com.smashingboxes.epa_prototype_android.helpers.PreferenceHelper;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitAuthModel;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class FitbitLoginCache {

    private static final String TAG = FitbitLoginCache.class.getName();

    public static final String KEY_LOGGED_IN_MODEL = "com.smashingboxes.dudesolutions.KEY_LOGGED_IN_USER";

    private static FitbitLoginCache sMFitbitLoginCache;

    private FitbitAuthModel loginModel;
    private PreferenceHelper mCacheHelper;

    public static synchronized FitbitLoginCache getInstance(Context context) {
        if (sMFitbitLoginCache == null) {
            sMFitbitLoginCache = new FitbitLoginCache(context.getApplicationContext());
        }
        return sMFitbitLoginCache;
    }

    private FitbitLoginCache(Context context) {
        mCacheHelper = new PreferenceHelper(context);
    }

    /**
     * Asynchronously stores the last login model
     *
     * @param loginModel - the login model to persist
     */
    public void saveLoginModel(FitbitAuthModel loginModel) {
        mCacheHelper.persistObjectAsync(KEY_LOGGED_IN_MODEL, loginModel);
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

    public void clearLogin() {
        mCacheHelper.removeObjectForKey(KEY_LOGGED_IN_MODEL);
        loginModel = null;
    }

    public static void logout(Context context) {
        resetAppState(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(LoginActivity.ACTION_LOGOUT);
        context.startActivity(intent);
    }

    public static void resetAppState(Context context){
        FitbitLoginCache.getInstance(context).clearLogin();
        LocationHelper locationHelper = new LocationHelper(context);
        locationHelper.clear();
    }

}
