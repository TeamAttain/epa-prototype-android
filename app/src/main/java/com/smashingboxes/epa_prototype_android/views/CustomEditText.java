package com.smashingboxes.epa_prototype_android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.smashingboxes.epa_prototype_android.R;
import com.smashingboxes.epa_prototype_android.helpers.FontsCache;

/**
 * Created by Austin Lanier on 12/8/15.
 */
public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context, String fontName) {
        super(context);
        setTypeface(typefaceForAttrName(fontName));
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        handleAttributes(attrs);
    }

    protected void handleAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        setTypeface(typefaceForAttrName(a.getString(R.styleable.CustomTextView_customFontName)));
        a.recycle();
    }

    private Typeface typefaceForAttrName(String name) {
        try {
            return FontsCache.typefaceForName(name);
        } catch (FontsCache.FontNotFoundException e) {
            e.printStackTrace();
            return getTypeface();
        }
    }

}
