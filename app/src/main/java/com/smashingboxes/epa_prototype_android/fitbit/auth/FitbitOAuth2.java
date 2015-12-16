package com.smashingboxes.epa_prototype_android.fitbit.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.smashingboxes.epa_prototype_android.R;
import com.smashingboxes.epa_prototype_android.fitbit.UriBuilder;
import com.smashingboxes.epa_prototype_android.fitbit.UriParser;
import com.smashingboxes.epa_prototype_android.models.FitbitAuthModel;
import com.smashingboxes.epa_prototype_android.network.ParamBuilder;
import com.smashingboxes.epa_prototype_android.network.RequestKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class FitbitOAuth2 {

    private static final String FITBIT_AUTH_URL = "https://www.fitbit.com/oauth2/authorize?";

    private static final String FITBIT_REFRESH_AUTH_URL = "https://api.fitbit.com/oauth2/token";

    private static final String CLIENT_ID = "229XJ8";

    /**
     * @see https://dev.fitbit.com/docs/oauth2/
     */
    public enum AuthFlowType {
        IMPLICIT, AUTHENICATION
    }

    private AuthFlowType mAuthFlowType;

    public FitbitOAuth2(AuthFlowType flowType){
        this.mAuthFlowType = flowType;
    }

    public enum ResponseType {
        CODE, TOKEN
    }

    public enum Scope {
        ACTIVITY, HEARTRATE, LOCATION, NUTRITION, PROFILE, SETTINGS, SLEEP, SOCIAL, WEIGHT
    }

    public enum Prompt {
        NONE, LOGIN, CONSENT
    }

    public enum ExpiresIn {
        _86400("86400"), _604800("604800"), _2592000("2592000");

        final String keyValue;

        ExpiresIn(String keyValue){
            this.keyValue = keyValue;
        }
    }

    public class FitbitAuthUriBuilder implements UriBuilder {

        private ParamBuilder mParamBuilder;

        @Override
        public Uri getBaseRequestUri() {
            return Uri.parse(FITBIT_AUTH_URL);
        }

        @Override
        public Uri buildRequestUri() {
            return Uri.parse(mParamBuilder.buildEncodedUri(getBaseRequestUri()));
        }

        /**
         * Uses Implicit code since we shouldn't store o
         * @param activity
         * @return
         */
        @Override
        public Uri buildDefault(Activity activity) {
            FitbitAuthUriBuilder builder = new FitbitAuthUriBuilder();
            builder.setClientId(CLIENT_ID).setResponseType(ResponseType.TOKEN)
                    .setScopes(Arrays.asList(Scope.ACTIVITY, Scope.PROFILE, Scope.NUTRITION, Scope.PROFILE,
                            Scope.SETTINGS, Scope.SLEEP, Scope.SOCIAL, Scope.WEIGHT))
                    .setRedirectUri(FitbitOAuth2.this.getRedirectUriScheme(activity.getString(R.string.fitbit_scheme),
                            activity.getString(R.string.fitbit_host)))
                    .setExpiresIn(ExpiresIn._86400);
            return builder.buildRequestUri();
        }

        public FitbitAuthUriBuilder() {
            mParamBuilder = new ParamBuilder();
        }

        public FitbitAuthUriBuilder setClientId(String clientId) {
            mParamBuilder.put(RequestKeys.CLIENT_ID.getParamValue(), clientId);
            return this;
        }

        public FitbitAuthUriBuilder setResponseType(@NonNull ResponseType responseType) {
            mParamBuilder.put(RequestKeys.RESPONSE_TYPE.getParamValue(), responseType.name().toLowerCase());
            return this;
        }

        public FitbitAuthUriBuilder setRedirectUri(@NonNull String uri) {
            mParamBuilder.put(RequestKeys.REDIRECT_URI.getParamValue(), uri);
            return this;
        }

        /**
         * The list of fitbit scopes our application is interested in
         *
         * @param scopes
         * @return
         */
        public FitbitAuthUriBuilder setScopes(@NonNull List<Scope> scopes) {
            mParamBuilder.put(RequestKeys.SCOPE.getParamValue(), formatScopeList(scopes));
            return this;
        }

        public FitbitAuthUriBuilder setExpiresIn(ExpiresIn expiresIn) {
            mParamBuilder.put(RequestKeys.EXPIRES_IN.getParamValue(), expiresIn.keyValue);
            return this;
        }

        public FitbitAuthUriBuilder setState(@NonNull String state) {
            mParamBuilder.put(RequestKeys.STATE.getParamValue(), state);
            return this;
        }

        public FitbitAuthUriBuilder setPrompt(@NonNull Prompt prompt) {
            mParamBuilder.put(RequestKeys.SCOPE.getParamValue(), prompt.name().toLowerCase());
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

    }

    public UriBuilder getUrlBuilder(){
        return new FitbitAuthUriBuilder();
    }

    /**
     * A Parser for the Uri returned from FitBit's IMPLICIT
     * authentication flow
     *
     * fitbitepaauth://com.smashingboxes.epa_prototype_android?#scope=nutrition+weight+social+settings+sleep+activity+profile&user_id=3WTVT8&token_type=Bearer&expires_in=86400&access_token=eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE0NDk4NjE3NTYsInNjb3BlcyI6Indwcm8gd251dCB3c2xlIHdzZXQgd3dlaSB3YWN0IHdzb2MiLCJzdWIiOiIzV1RWVDgiLCJhdWQiOiIyMjlYSjgiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJpYXQiOjE0NDk3NzUzNTZ9.tD4Qy3148GwjhqL7nOv7bh3Y5oxfC0cvYq0O2a0BpDQ
     */
    public class FitbitAuthUriParser implements UriParser {

        private Map<String, String> uriInfoMap;

        public FitbitAuthUriParser(@NonNull Uri fitbitAuthRedirectUri){
            uriInfoMap = buildUrlInfoMap(fitbitAuthRedirectUri.getFragment());
        }

        private Map<String, String> buildUrlInfoMap(final String uriFragment){
            Map<String, String> uriInfoMap = new HashMap<>();

            String[] splitFragment = uriFragment.split("&");
            for(String current : splitFragment){
                String[] keyValue = current.split("=");
                if(keyValue.length == 2) {
                    uriInfoMap.put(keyValue[0], keyValue[1]);
                }
            }

            return uriInfoMap;
        }

        public FitbitAuthModel parseUri() throws ParseException {
            return new FitbitAuthModel(System.currentTimeMillis(),
                    getExpiresIn(), getAccessToken(),
                    getRefreshToken(), getScopes(),
                    getUserId(), getTokenType());
        }

        private long getExpiresIn(){
            try {
                return Long.parseLong(uriInfoMap.get(RequestKeys.EXPIRES_IN.getParamValue()));
            } catch(NumberFormatException e){
                e.printStackTrace();
                return 0;
            }
        }

        private String getAccessToken() throws ParseException {
            String accessToken = uriInfoMap.get(RequestKeys.ACCESS_TOKEN.getParamValue());
            if(TextUtils.isEmpty(accessToken)){
                throw new ParseException("Access token not found");
            }
            return accessToken;
        }

        private List<Scope> getScopes() {
            List<Scope> scopeList = new ArrayList<>();
            final String scopeListString = uriInfoMap.get(RequestKeys.SCOPE.getParamValue());
            final String scopeDelimiter = "\\+";
            final String[] scopes = scopeListString.split(scopeDelimiter);
            for(String scope : scopes){
                scopeList.add(Scope.valueOf(scope.toUpperCase()));
            }
            return scopeList;
        }

        public String getTokenType(){
            return uriInfoMap.get(RequestKeys.TOKEN_TYPE.getParamValue());
        }

        public String getUserId(){
            return uriInfoMap.get(RequestKeys.USER_ID.getParamValue());
        }

        /**
         * This is unused and more of a placeholder since we're using the implicit flow for now
         *
         * @return an empty string
         */
        private String getRefreshToken(){
            return "";
        }

    }

    public UriParser getParser(@NonNull Uri uri){
        return new FitbitAuthUriParser(uri);
    }

    public void launchAuthenticationIntent(Context context, Uri fitbitRequestUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(fitbitRequestUri);
        context.startActivity(intent);
    }

    public String getRedirectUriScheme(String scheme, String host) {
        return Uri.parse(String.format("%s://%s?", scheme, host)).toString();
    }

}
