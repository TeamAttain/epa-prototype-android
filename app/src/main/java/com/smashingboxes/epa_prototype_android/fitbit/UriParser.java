package com.smashingboxes.epa_prototype_android.fitbit;

import com.smashingboxes.epa_prototype_android.models.OAuth2Model;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public interface UriParser {

    class ParseException extends Exception {
        public ParseException(String message){
            super(message);
        }
    }

    OAuth2Model parseUri() throws ParseException;
}
