package com.smashingboxes.epa_prototype_android.fitbit.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Austin Lanier on 12/29/15.
 * Updated by
 */
public class TimeSeries implements Parcelable {

    private final String dateTime;
    private final double value;

    public TimeSeries(String dateTime, double value) {
        this.dateTime = dateTime;
        this.value = value;
    }

    public TimeSeries(Parcel parcel){
        this(parcel.readString(), parcel.readDouble());
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getValue() {
        return value;
    }

    public static final Parcelable.Creator<TimeSeries> CREATOR
            = new Parcelable.Creator<TimeSeries>() {
        public TimeSeries createFromParcel(Parcel in) {
            return new TimeSeries(in);
        }

        public TimeSeries[] newArray(int size) {
            return new TimeSeries[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDateTime());
        dest.writeDouble(getValue());
    }
}
