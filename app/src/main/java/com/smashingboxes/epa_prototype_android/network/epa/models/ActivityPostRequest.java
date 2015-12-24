package com.smashingboxes.epa_prototype_android.network.epa.models;

import java.util.List;

public class ActivityPostRequest {
    List<EpaActivity> activities;

    public ActivityPostRequest(List<EpaActivity> activities) {
        this.activities = activities;
    }
}