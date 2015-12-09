package com.smashingboxes.epa_prototype_android.fitbit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.smashingboxes.epa_prototype_android.R;
import com.smashingboxes.epa_prototype_android.network.ParamBuilder;
import com.smashingboxes.epa_prototype_android.network.RequestKeys;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class FitbitOAuth2UrlBuilder {

    private static final String FITBIT_AUTH_URL = "https://www.fitbit.com/oauth2/authorize?";

    private static final String FITBIT_REFRESH_AUTH_URL = "https://api.fitbit.com/oauth2/token";

    private static final String CLIENT_ID = "229XJ8";

    private ParamBuilder mParamBuilder;

    public enum ResponseType {
        CODE, TOKEN
    }

    public enum Scope {
        ACTIVITY, HEARTRATE, LOCATION, NUTRITION, PROFILE, SETTINGS, SLEEP, SOCIAL, WEIGHT
    }

    public enum Prompt {
        NONE, LOGIN, CONSENT
    }

    public static void launchDefault(Activity activity) {
        FitbitOAuth2UrlBuilder builder = new FitbitOAuth2UrlBuilder();
        builder.setClientId(CLIENT_ID).setResponseType(ResponseType.TOKEN)
                .setScopes(Arrays.asList(Scope.ACTIVITY, Scope.PROFILE, Scope.NUTRITION, Scope.PROFILE,
                        Scope.SETTINGS, Scope.SLEEP, Scope.SOCIAL, Scope.WEIGHT))
                .setRedirectUri(getUriScheme(activity))
                .setExpiresIn().launchAuthenticationIntent(activity);
    }

    public static String getUriScheme(Context context) {
        String scheme = context.getString(R.string.fitbit_scheme);
        String host = context.getString(R.string.fitbit_host);
        return Uri.parse(String.format("%s://%s?", scheme, host)).toString();
    }

    public FitbitOAuth2UrlBuilder() {
        mParamBuilder = new ParamBuilder();
    }

    public FitbitOAuth2UrlBuilder setClientId(String clientId) {
        mParamBuilder.put(RequestKeys.CLIENT_ID.getParamValue(), clientId);
        return this;
    }

    public FitbitOAuth2UrlBuilder setResponseType(@NonNull ResponseType responseType) {
        mParamBuilder.put(RequestKeys.RESPONSE_TYPE.getParamValue(), responseType.name().toLowerCase());
        return this;
    }

    public FitbitOAuth2UrlBuilder setRedirectUri(@NonNull String uri) {
        mParamBuilder.put(RequestKeys.REDIRECT_URI.getParamValue(), uri);
        return this;
    }

    /**
     * The list of fitbit scopes our application is interested in
     *
     * @param scopes
     * @return
     */
    public FitbitOAuth2UrlBuilder setScopes(@NonNull List<Scope> scopes) {
        mParamBuilder.put(RequestKeys.SCOPE.getParamValue(), formatScopeList(scopes));
        return this;
    }

    public FitbitOAuth2UrlBuilder setExpiresIn() {
        mParamBuilder.put(RequestKeys.EXPIRES_IN.getParamValue(), "86400");
        return this;
    }

    public FitbitOAuth2UrlBuilder setState(@NonNull String state) {
        mParamBuilder.put(RequestKeys.STATE.getParamValue(), state);
        return this;
    }

    private String formatScopeList(List<Scope> scopes) {
        final StringBuilder builder = new StringBuilder();

        Iterator<Scope> scopeIterator = scopes.iterator();
        boolean hasNext = scopeIterator.hasNext();
        while (hasNext) {
            Scope scope = scopeIterator.next();
            builder.append(scope.toString().toLowerCase());
            hasNext = scopeIterator.hasNext();
            if (hasNext) {
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    public FitbitOAuth2UrlBuilder setPrompt(@NonNull Prompt prompt) {
        mParamBuilder.put(RequestKeys.SCOPE.getParamValue(), prompt.name().toLowerCase());
        return this;
    }

    public Uri buildRequestUri() {
        return Uri.parse(mParamBuilder.buildEncodedUri(Uri.parse(FITBIT_AUTH_URL)));
    }

    public void launchAuthenticationIntent(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = buildRequestUri();
        intent.setData(uri);
        context.startActivity(intent);
    }

}
