package com.smashingboxes.epa_prototype_android.network.parsing;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smashingboxes.epa_prototype_android.network.requests.BaseRequest;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class ArrayParseStrategy<T> implements BaseRequest.ResponseParseStrategy<ArrayList<T>> {

    private static final Gson GSON = new Gson();
    private Class<T> clazz;
    private String array_key;

    public ArrayParseStrategy(Class<T> clazz, String array_key) {
        this.clazz = clazz;
        this.array_key = array_key;
    }

    @Override
    public ArrayList<T> parseResponse(String rawResponse) throws VolleyError {
        try {
            ArrayList<T> mItems;
            JsonParser jsonParser = new JsonParser();
            JsonObject element = jsonParser.parse(rawResponse).getAsJsonObject();
            JsonArray array = element.get(array_key).getAsJsonArray();
            final int count = array.size();
            mItems = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                mItems.add(GSON.fromJson(array.get(i), clazz));
            }
            return mItems;
        } catch(Exception e){
            throw new ParseError(e);
        }
    }
}
