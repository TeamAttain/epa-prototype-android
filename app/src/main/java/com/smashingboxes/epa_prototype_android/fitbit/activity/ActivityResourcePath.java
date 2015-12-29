package com.smashingboxes.epa_prototype_android.fitbit.activity;

/**
 * Represents an individual resource path when requesting FitBit metrics that could
 * be displayed as a time series.
 *
 * @see https://dev.fitbit.com/docs/activity/#activity-time-series
 */
public enum ActivityResourcePath {
    CALORIES("calories"), CALORIES_BMR("caloriesBMR"), STEPS("steps"),
    DISTANCE("distance"), FLOORS("floors"), EVEVATION("elevation"),
    MINUTES_SEDENTARY("minutesSedentary"), MINUTES_LIGHTLY_ACTIVE("minutesLightlyActive"),
    MINUTES_FAIRLY_ACTIVE("minutesFairlyActive"), MINUTES_VERY_ACTIVE("minutesVeryActive"),
    ACTIVITY_CALORIES("activityCalories"), NONE("none");

    static String ACTVITIES_PATH = "activities";

    private final String path;

    ActivityResourcePath(String path) {
        this.path = path;
    }

    public String getDisplayName(){
        return name().replace("_", " ");
    }

    public String getActvitiesPath() {
        return ACTVITIES_PATH;
    }

    public String getPath() {
        return path;
    }

    public String getArrayResponseKey(){
        return String.format("%s-%s", getActvitiesPath(), getPath());
    }

    public String getArrayTrackerResponseKey(){
        return String.format("%s-tracker-%s", getActvitiesPath(), getPath());
    }

    public String getTrackerPath() {
        return getActvitiesPath() + "/tracker/" + getPath();
    }

    public String getFullPath() {
        return getActvitiesPath() + "/" + getPath();
    }
}