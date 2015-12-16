package com.smashingboxes.epa_prototype_android.fitbit.activity;

/**
 * Represents an individual resource path when requesting FitBit metrics that could
 * be displayed as a time series.
 *
 * @see https://dev.fitbit.com/docs/activity/#activity-time-series
 */
public enum TimeSeriesResourcePath {
    CALORIES("calories"), CALORIES_BMR("caloriesBMR"), STEPS("steps"),
    DISTANCE("distance"), FLOORS("floors"), EVEVATION("elevation"),
    MINUTES_SEDENTARY("minutesSedentary"), MINUTES_LIGHTLY_ACTIVE("minutesLightlyActive"),
    MINUTES_FAIRLY_ACTIVE("minutesFairlyActive"), MINUTES_VERY_ACTIVE("minutesVeryActive"),
    ACTIVITY_CALORIES("activityCalories");

    static String ACTVITIES_PATH = "activities";

    private final String path;

    TimeSeriesResourcePath(String path) {
        this.path = path;
    }

    public String getActvitiesPath() {
        return ACTVITIES_PATH;
    }

    public String getPath() {
        return path;
    }

    public String getTrackerPath() {
        return getActvitiesPath() + "/tracker/" + getPath();
    }

    public String getFullPath() {
        return getActvitiesPath() + "/" + getPath();
    }
}