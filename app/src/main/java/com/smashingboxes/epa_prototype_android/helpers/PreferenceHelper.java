package com.smashingboxes.epa_prototype_android.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class PreferenceHelper {

    private SharedPreferences preferences;

    private Gson gson = new Gson();

    /**
     * Constructs a preference helper with activity scoped shared preferences
     *
     * @param activity       - the activity to associate the stored preferences with
     * @param preferenceMode - the preference mode to use
     */
    public PreferenceHelper(Activity activity, int preferenceMode) {
        preferences = activity.getPreferences(preferenceMode);
    }

    /**
     * Constructs an application scoped shared preferences
     *
     * @param context
     */
    public PreferenceHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void persistStringAsync(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void persistStringSync(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defaultValue){
        return preferences.getString(key, defaultValue);
    }

    /**
     * Converts the provided object to the json string and stores it in the activity's
     * local shared preferences
     *
     * @param key    - the key to associate with this object
     * @param object - the object to persist
     */
    public void persistObjectAsync(String key, Object object) {
        persistStringAsync(key, gson.toJson(object));
    }

    public void persistObjectSync(String key, Object object){
        persistStringSync(key, gson.toJson(object));
    }

    /**
     * Removes the value mapping to the provided key for this preference scope
     *
     * @param key
     */
    public void removeObjectForKey(String key) {
        preferences.edit().remove(key).apply();
    }

    public void clear(){
        preferences.edit().clear().apply();
    }

    /**
     * @param key   - the key to of the preference to retrieve
     * @param clazz - the class of the returned object type
     * @return the object associated with the key, or null if an error occurred
     */
    public <T> T getObject(String key, Class<T> clazz) {
        T t = null;
        try {
            String objectAsJson = getString(key, null);
            t = gson.fromJson(objectAsJson, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

}
