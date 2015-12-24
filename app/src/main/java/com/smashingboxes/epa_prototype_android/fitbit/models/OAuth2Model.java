package com.smashingboxes.epa_prototype_android.fitbit.models;

import java.util.concurrent.TimeUnit;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class OAuth2Model {

    private final long created_at;
    private final long expires_in;
    private final String access_token;
    private final String refresh_token;

    public OAuth2Model(long created_at, long expires_in, String access_token, String refresh_token) {
        this.created_at = created_at;
        this.expires_in = expires_in;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public long getExpiresIn() {
        return expires_in;
    }

    public long getCreatedAt() {
        return created_at;
    }

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public TimeUnit getExpirationTimeUnit(){
        return TimeUnit.SECONDS;
    }

    public boolean isExpired(){
        long convertedExpiresIn = TimeUnit.MILLISECONDS.convert(getExpiresIn(), getExpirationTimeUnit());
        long timeElapsedSinceCreation = System.currentTimeMillis() - getCreatedAt();
        return timeElapsedSinceCreation > convertedExpiresIn;
    }

}
