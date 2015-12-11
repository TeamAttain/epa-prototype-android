package com.smashingboxes.epa_prototype_android.models;

import android.text.TextUtils;

import com.smashingboxes.epa_prototype_android.fitbit.FitbitOAuth2;

import java.util.Collections;
import java.util.List;

/**
 * Created by Austin Lanier on 12/8/15.
 * Updated by
 */
public class FitbitAuthModel extends OAuth2Model {

    public enum FlowType {
        IMPLICIT, AUTHENICATION
    }

    private final List<FitbitOAuth2.Scope> availableScopes;
    private final String user_id;
    private final String token_type;

    public FitbitAuthModel(long created_at, long expires_in, String access_token, String refresh_token, List<FitbitOAuth2.Scope> availableScopes, String user_id, String token_type) {
        super(created_at, expires_in, access_token, refresh_token);
        this.availableScopes = availableScopes;
        this.user_id = user_id;
        this.token_type = token_type;
    }

    /**
     *
     * A List of allowed FitBit api scopes
     *
     * @return an unmodifiable list of available scopes
     */
    public List<FitbitOAuth2.Scope> getAvailableScopes() {
        return Collections.unmodifiableList(availableScopes);
    }

    public String getUserId(){
        return user_id;
    }

    public String getTokenType(){
        return token_type;
    }

    public FlowType getFlowType(){
        return TextUtils.isEmpty(getRefreshToken()) ? FlowType.IMPLICIT : FlowType.AUTHENICATION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FitbitAuthModel that = (FitbitAuthModel) o;

        if (availableScopes != null ? !availableScopes.equals(that.availableScopes) : that.availableScopes != null)
            return false;
        if (user_id != null ? !user_id.equals(that.user_id) : that.user_id != null) return false;
        return !(token_type != null ? !token_type.equals(that.token_type) : that.token_type != null);

    }

    @Override
    public int hashCode() {
        int result = availableScopes != null ? availableScopes.hashCode() : 0;
        result = 31 * result + (user_id != null ? user_id.hashCode() : 0);
        result = 31 * result + (token_type != null ? token_type.hashCode() : 0);
        return result;
    }
}
