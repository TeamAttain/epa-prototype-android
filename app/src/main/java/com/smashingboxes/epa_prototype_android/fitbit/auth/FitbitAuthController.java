package com.smashingboxes.epa_prototype_android.fitbit.auth;

import android.net.Uri;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface FitbitAuthController {
    void startAuthenticationFlow();
    void handleRedirectUri(Uri uri);
    void onFitbitUserAuthenticated();
    void sendFitbitRefreshRequest();
}
