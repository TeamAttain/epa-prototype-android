package com.smashingboxes.epa_prototype_android.fitbit.models;

import java.util.List;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class Summary {

    private final int activityCalories;
    private final int caloriesBMR;
    private final  int caloriesOut;
    private final List<Distance> distances;
    private final float elevation;
    private final int fairlyActiveMinutes;
    private final int floors;
    private final  int lightlyActiveMinutes;
    private final int marginalCalories;
    private final int sedentaryMinutes;
    private final int steps;
    private final int veryActiveMinutes;

    public Summary(int activityCalories, int caloriesBMR, int caloriesOut, List<Distance> distances, float elevation, int fairlyActiveMinutes, int floors, int lightlyActiveMinutes, int marginalCalories, int sedentaryMinutes, int steps, int veryActiveMinutes) {
        this.activityCalories = activityCalories;
        this.caloriesBMR = caloriesBMR;
        this.caloriesOut = caloriesOut;
        this.distances = distances;
        this.elevation = elevation;
        this.fairlyActiveMinutes = fairlyActiveMinutes;
        this.floors = floors;
        this.lightlyActiveMinutes = lightlyActiveMinutes;
        this.marginalCalories = marginalCalories;
        this.sedentaryMinutes = sedentaryMinutes;
        this.steps = steps;
        this.veryActiveMinutes = veryActiveMinutes;
    }

    public int getActivityCalories() {
        return activityCalories;
    }

    public int getCaloriesBMR() {
        return caloriesBMR;
    }

    public int getCaloriesOut() {
        return caloriesOut;
    }

    public List<Distance> getDistances() {
        return distances;
    }

    public float getElevation() {
        return elevation;
    }

    public int getFairlyActiveMinutes() {
        return fairlyActiveMinutes;
    }

    public int getFloors() {
        return floors;
    }

    public int getLightlyActiveMinutes() {
        return lightlyActiveMinutes;
    }

    public int getMarginalCalories() {
        return marginalCalories;
    }

    public int getSedentaryMinutes() {
        return sedentaryMinutes;
    }

    public int getSteps() {
        return steps;
    }

    public int getVeryActiveMinutes() {
        return veryActiveMinutes;
    }
}
