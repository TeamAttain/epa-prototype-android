package com.smashingboxes.epa_prototype_android.network.parsing;

import com.android.volley.ParseError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.smashingboxes.epa_prototype_android.network.requests.BaseRequest;

public class ClassParseStrategy<T> implements BaseRequest.ResponseParseStrategy<T> {

    private static final Gson GSON = new Gson();
    private Class<T> clazz;

    public ClassParseStrategy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parseResponse(String rawResponse) throws ParseError {
        T response;
        try {
            response = GSON.fromJson(rawResponse, clazz);
        } catch (JsonSyntaxException e) {
            throw new ParseError(e);
        }
        return response;
    }
}