package com.smashingboxes.epa_prototype_android.models;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 * <p/>
 */
public class FitbitProfile {

    /**
     * A Model based on the JSON response returned from the FitBit API.  This is likely to
     * change in the future.  We're only interested in a subset of the data, so we may be able
     * to remove some of these fields, but for now we'll include the full model.
     */
    public class UserModel {
        private int age;
        private String aboutMe;
        private String avatar;
        private String avatar150;
        private int averageDailySteps;
        private String city;
        private String country;
        private String dateOfBirth;
        private String displayName;
        private String distanceUnit;
        private String encodedId;
        private Features features;
        private String foodsLocale;
        private String fullName;
        private String gender;
        private String glucoseUnit;
        private float height;
        private String heightUnit;
        private String locale;
        private String memberSince;
        private String nickname;
        private long offsetFromUTCMillis;
        private String startDayOfWeek;
        private String state;
        private float strideLengthRunning;
        private float strideLengthWalking;
        private String timezone;
        private String[] topBadges;
        private String waterUnit;
        private String waterUnitName;
        private float weight;
        private String weightUnit;

        //TODO EXPOSE ATTRS HERE OR IN PROFILE
    }

    private final UserModel user;

    public FitbitProfile(UserModel user) {
        this.user = user;
    }

    public UserModel getUser(){
        return user;
    }

}
