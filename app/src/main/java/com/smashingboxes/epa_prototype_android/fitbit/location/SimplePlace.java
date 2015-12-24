package com.smashingboxes.epa_prototype_android.fitbit.location;

/**
 * Created by Austin Lanier on 12/16/15.
 * Updated by
 */
public class SimplePlace {

    private final String id;
    private final String name;
    private final String address;
    private final double lat;
    private final double lng;

    public SimplePlace(String id, String name, String address, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
