package com.smashingboxes.epa_prototype_android.network.epa.models;

import com.smashingboxes.epa_prototype_android.R;

import java.util.Arrays;

/**
 * Created by Austin Lanier on 12/17/15.
 * Updated by
 */
public class AirQuality {

    /**
     * Start and end ranges are referenced from {@linkplain http://airnow.gov/index.cfm?action=aqibasics.aqi }
     * <p/>
     * This enum follows a parallel representation to R.array.air_quality_titles and air_quality_colors
     */
    public enum IndexType {
        GOOD(0, 50, R.color.air_good), MODERATE(51, 100, R.color.air_moderate), UNHEALTHY_FOR_SENSITIVE_GROUPS(101, 150, R.color.air_unhealthy_for_sensitive),
        UNHEALTHY(151, 200, R.color.air_unhealthy), VERY_UNHEALTHY(201, 300, R.color.air_very_unhealthy), HAZARDOUS(301, 500, R.color.air_hazardous),
        NONE(-1,-1, R.color.splash_blue);

        final int startRange, endRange;
        final int color;

        IndexType(int startRange, int endRange, int color) {
            this.startRange = startRange;
            this.endRange = endRange;
            this.color = color;
        }

        public static IndexType forIndexRange(int range) {
            for (IndexType current : values()) {
                if (current.startRange <= range && current.endRange >= range) {
                    return current;
                }
            }
            return IndexType.NONE;
        }

        public int getColor(){
            return color;
        }

        public String getTitle(){
            return name().replace("_", " ");
        }

    }

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
    private final String zip_code;
    private final String created_at;
    private final String updated_at;

    public AirQuality(long id, int aqi, String category, String date_observed, int hour_observed, double lat,
                      String local_time_zone, double lng, String parameter_name, String reporting_area, String state_code, String zip_code, String created_at, String updated_at) {
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
        this.zip_code = zip_code;
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

    public String getZip_code() {
        return zip_code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public IndexType getIndexType() {
        return IndexType.forIndexRange(getAqi());
    }
}
