package com.smashingboxes.epa_prototype_android.models;

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

    public boolean isExpired(){
        long timeElapsedSinceCreation = System.currentTimeMillis() - created_at;
        return timeElapsedSinceCreation > expires_in;
    }

}
