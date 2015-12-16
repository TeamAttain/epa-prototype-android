package com.smashingboxes.epa_prototype_android.models;

public class Features {
    final boolean exerciseGoal;

    public Features(boolean exerciseGoal) {
        this.exerciseGoal = exerciseGoal;
    }

    public boolean hasExerciseGoal() {
        return exerciseGoal;
    }
}