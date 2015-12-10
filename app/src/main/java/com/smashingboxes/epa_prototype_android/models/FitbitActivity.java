package com.smashingboxes.epa_prototype_android.models;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class FitbitActivity {
    
    private final long activityId;
    private final long activityParentId;
    private final int calories;
    private final String description;
    private final float distance;
    private final long duration;
    private final boolean hasStartTime;
    private final boolean isFavorite;
    private final long logId;
    private final String name;
    private final String startTime;
    private final int steps;

    public FitbitActivity(long activityId, long activityParentId, int calories, String description, float distance, long duration, boolean hasStartTime, boolean isFavorite, long logId, String name, String startTime, int steps) {
        this.activityId = activityId;
        this.activityParentId = activityParentId;
        this.calories = calories;
        this.description = description;
        this.distance = distance;
        this.duration = duration;
        this.hasStartTime = hasStartTime;
        this.isFavorite = isFavorite;
        this.logId = logId;
        this.name = name;
        this.startTime = startTime;
        this.steps = steps;
    }

    public long getActivityId() {
        return activityId;
    }

    public long getActivityParentId() {
        return activityParentId;
    }

    public int getCalories() {
        return calories;
    }

    public String getDescription() {
        return description;
    }

    public float getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isHasStartTime() {
        return hasStartTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public long getLogId() {
        return logId;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getSteps() {
        return steps;
    }
}
