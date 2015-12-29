package com.smashingboxes.epa_prototype_android.network.epa.models;

/**
 * Created by Austin Lanier on 12/23/15.
 * Updated by
 */
public class EpaActivityDetails {

    private final long id;
    private final float percentage_outside;
    private final int inside;
    private final int outside;

    public EpaActivityDetails(long id, float percentage_outside, int inside, int outside) {
        this.id = id;
        this.percentage_outside = percentage_outside;
        this.inside = inside;
        this.outside = outside;
    }

    public long getId() {
        return id;
    }

    public float getPercentage_outside() {
        return percentage_outside;
    }

    public int getInside() {
        return inside;
    }

    public int getOutside() {
        return outside;
    }
}
