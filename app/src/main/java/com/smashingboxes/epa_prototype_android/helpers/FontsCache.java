package com.smashingboxes.epa_prototype_android.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class FontsCache {

    public static final class FontNotFoundException extends Exception {
        public FontNotFoundException(String cause) {
            super(cause);
        }
    }

    private FontsCache() {
    }

    private static final Map<String, Typeface> fontsMap = new HashMap<>();

    public static void initializeTypefaces(Context context) {
        Resources resources = context.getResources();
        AssetManager manager = context.getAssets();
        //TODO
    }

    public static Typeface typefaceForName(String name) throws FontNotFoundException {
        Typeface typeFace = fontsMap.get(name);
        if (typeFace == null) {
            throw new FontNotFoundException("Font not found for " + name);
        }
        return typeFace;
    }

}