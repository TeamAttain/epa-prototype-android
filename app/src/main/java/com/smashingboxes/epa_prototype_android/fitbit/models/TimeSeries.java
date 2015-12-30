package com.smashingboxes.epa_prototype_android.fitbit.models;

/**
 * Created by Austin Lanier on 12/29/15.
 * Updated by
 */
public class TimeSeries {

    private final String dateTime;
    private final double value;

    public TimeSeries(String dateTime, double value) {
        this.dateTime = dateTime;
        this.value = value;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getValue() {
        return value;
    }
}
