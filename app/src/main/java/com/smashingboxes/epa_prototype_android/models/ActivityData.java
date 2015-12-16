package com.smashingboxes.epa_prototype_android.models;

import java.util.List;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class ActivityData {

    private final List<FitbitActivity> activities;
    private final Goals goals;
    private final Summary sumamry;

    public ActivityData(List<FitbitActivity> activities, Goals goals, Summary sumamry) {
        this.activities = activities;
        this.goals = goals;
        this.sumamry = sumamry;
    }

    public List<FitbitActivity> getActivities() {
        return activities;
    }

    public Goals getGoals() {
        return goals;
    }

    public Summary getSumamry() {
        return sumamry;
    }
}
