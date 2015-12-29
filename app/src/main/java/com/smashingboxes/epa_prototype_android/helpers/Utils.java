package com.smashingboxes.epa_prototype_android.helpers;

import java.io.Closeable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Austin Lanier on 12/28/15.
 * Updated by
 */
public class Utils {

    public static final String DISPLAY_FORMAT = "MMM dd";
    public static final SimpleDateFormat displayFormatter = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
    public static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

    public static final String FITBIT_SERVER_DATE_FORMAT = "yyyy-MM-dd";

    public static final SimpleDateFormat fitbit_formatter = new SimpleDateFormat(FITBIT_SERVER_DATE_FORMAT, Locale.getDefault());

    public static String generateFitbitDateTimeString(long time){
        return fitbit_formatter.format(time);
    }

    public static String generateFitbitDateTimeString(String toParse){
        try {
            toParse = generateFitbitDateTimeString(parser.parse(toParse).getTime());
        } catch(ParseException e){
            e.printStackTrace();
        }
        return toParse;
    }

    public static String generateFitbitCurrentDateTime(){
        return generateFitbitDateTimeString(new Date().getTime());
    }

    private static final DecimalFormat df = new DecimalFormat("###.##");
    static {
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public static String formatDate(String date){
        try {
            return displayFormatter.format(parser.parse(date));
        } catch(java.text.ParseException e){
            e.printStackTrace();
            return date;
        }
    }

    public static String formatTimeLong(long time){
        return parser.format(new Date(time));
    }

    public static String formatDistance(double distance){
        return df.format(distance);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
