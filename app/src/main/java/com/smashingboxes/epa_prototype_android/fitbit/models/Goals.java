package com.smashingboxes.epa_prototype_android.fitbit.models;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 */
public class Goals {
    private final int caloriesOut;
    private final float distance;
    private final int floors;
    private final int steps;

    public Goals(int caloriesOut, float distance, int floors, int steps) {
        this.caloriesOut = caloriesOut;
        this.distance = distance;
        this.floors = floors;
        this.steps = steps;
    }

    public int getCaloriesOut() {
        return caloriesOut;
    }

    public float getDistance() {
        return distance;
    }

    public int getFloors() {
        return floors;
    }

    public int getSteps() {
        return steps;
    }
}
