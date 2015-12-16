package com.smashingboxes.epa_prototype_android.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class DateHelper {

    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";

    public static final SimpleDateFormat formatter = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());

    public static String generateDateTimeString(long time){
        return formatter.format(time);
    }

    public static String generateCurrentDateTime(){
        return generateDateTimeString(new Date().getTime());
    }

}
