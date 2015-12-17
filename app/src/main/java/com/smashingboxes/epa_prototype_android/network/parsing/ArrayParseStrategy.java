package com.smashingboxes.epa_prototype_android.network.parsing;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.smashingboxes.epa_prototype_android.network.BaseRequest;

import java.util.ArrayList;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class ArrayParseStrategy<T> implements BaseRequest.ResponseParseStrategy<ArrayList<T>> {

    private static final Gson GSON = new Gson();
    private Class<T> clazz;

    public ArrayParseStrategy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public ArrayList<T> parseResponse(String rawResponse) throws VolleyError {
        ArrayList<T> mItems = null;
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement element = jsonParser.parse(rawResponse);
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                final int count = array.size();
                mItems = new ArrayList<>(count);
                for(int i = 0; i < count; i++){
                    mItems.add(GSON.fromJson(array.get(i), clazz));
                }
            } else {
                throw new ParseError();
            }
        } catch(JsonParseException e){
            throw new ParseError(e);
        }
        return mItems;
    }
}
