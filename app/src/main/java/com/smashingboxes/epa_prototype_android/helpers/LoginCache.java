package com.smashingboxes.epa_prototype_android.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.smashingboxes.epa_prototype_android.LoginActivity;
import com.smashingboxes.epa_prototype_android.models.LoginModel;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class LoginCache {

    private static final String TAG = LoginCache.class.getName();

    public static final String KEY_LOGGED_IN_MODEL = "com.smashingboxes.dudesolutions.KEY_LOGGED_IN_USER";

    private static LoginCache mLoginCache;

    private LoginModel loginModel;
    private PreferenceHelper<LoginModel> mCacheHelper;

    public static synchronized LoginCache getInstance(Context context) {
        if (mLoginCache == null) {
            mLoginCache = new LoginCache(context.getApplicationContext());
        }
        return mLoginCache;
    }

    private LoginCache(Context context) {
        mCacheHelper = new PreferenceHelper<>(context);
    }

    /**
     * Stores the last login model
     *
     * @param loginModel - the login model to persist
     */
    public void saveLoginModel(LoginModel loginModel) {
        mCacheHelper.persistObject(KEY_LOGGED_IN_MODEL, loginModel);
        this.loginModel = loginModel;
    }

    /**
     * Attempts to retrieve the last login model
     *
     * @return the previous login model, or null if not present or an error occurred
     */
    public LoginModel getLoginModel() {
        if (loginModel == null) {
            loginModel = mCacheHelper.getObject(KEY_LOGGED_IN_MODEL, LoginModel.class);
        }
        return loginModel;
    }

    public void clearLogin(){
        mCacheHelper.removeObjectForKey(KEY_LOGGED_IN_MODEL);
        loginModel = null;
    }

    public static void logout(Activity activity){
        LoginCache.getInstance(activity).clearLogin();

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(LoginActivity.ACTION_LOGOUT);
        activity.startActivity(intent);
    }

}
