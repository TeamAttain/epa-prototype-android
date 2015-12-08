package com.smashingboxes.epa_prototype_android;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.smashingboxes.epa_prototype_android.network.NetworkRequestManager;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        NetworkRequestManager.initQueue(this);
    }

}
