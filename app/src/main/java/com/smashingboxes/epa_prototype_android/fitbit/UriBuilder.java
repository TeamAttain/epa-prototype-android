package com.smashingboxes.epa_prototype_android.fitbit;

import android.app.Activity;
import android.net.Uri;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface UriBuilder {
    Uri getBaseRequestUri();
    Uri buildRequestUri();
    Uri buildDefault(Activity activity);
}
