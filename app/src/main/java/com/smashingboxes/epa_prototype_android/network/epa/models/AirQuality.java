package com.smashingboxes.epa_prototype_android.network.epa.models;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class AirQuality {

    private final long id;
    private final int aqi;
    private final String category;
    private final String date_observed;
    private final int hour_observed;
    private final double lat;
    private final String local_time_zone;
    private final double lng;
    private final String parameter_name;
    private final String reporting_area;
    private final String state_code;
    private final String created_at;
    private final String updated_at;

    public AirQuality(long id, int aqi, String category, String date_observed, int hour_observed, double lat,
                      String local_time_zone, double lng, String parameter_name, String reporting_area, String state_code, String created_at, String updated_at) {
        this.id = id;
        this.aqi = aqi;
        this.category = category;
        this.date_observed = date_observed;
        this.hour_observed = hour_observed;
        this.lat = lat;
        this.local_time_zone = local_time_zone;
        this.lng = lng;
        this.parameter_name = parameter_name;
        this.reporting_area = reporting_area;
        this.state_code = state_code;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public long getId() {
        return id;
    }

    public int getAqi() {
        return aqi;
    }

    public String getCategory() {
        return category;
    }

    public String getDate_observed() {
        return date_observed;
    }

    public int getHour_observed() {
        return hour_observed;
    }

    public double getLat() {
        return lat;
    }

    public String getLocal_time_zone() {
        return local_time_zone;
    }

    public double getLng() {
        return lng;
    }

    public String getParameter_name() {
        return parameter_name;
    }

    public String getReporting_area() {
        return reporting_area;
    }

    public String getState_code() {
        return state_code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
