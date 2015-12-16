package com.smashingboxes.epa_prototype_android.models;

/**
 * Created by Austin Lanier on 12/10/15.
 * Updated by
 * <p/>
 */
public class FitbitProfile {


    private final UserModel user;

    public FitbitProfile(UserModel user) {
        this.user = user;
    }

    public UserModel getUser(){
        return user;
    }


    /**
     * A Model based on the JSON response returned from the FitBit API.  This is likely to
     * change in the future.  We're only interested in a subset of the data, so we may be able
     * to remove some of these fields, but for now we'll include the full model.
     */
    public final class UserModel {
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

        public int getAge() {
            return age;
        }

        public String getAboutMe() {
            return aboutMe;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getAvatar150() {
            return avatar150;
        }

        public int getAverageDailySteps() {
            return averageDailySteps;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDistanceUnit() {
            return distanceUnit;
        }

        public String getEncodedId() {
            return encodedId;
        }

        public Features getFeatures() {
            return features;
        }

        public String getFoodsLocale() {
            return foodsLocale;
        }

        public String getFullName() {
            return fullName;
        }

        public String getGender() {
            return gender;
        }

        public String getGlucoseUnit() {
            return glucoseUnit;
        }

        public float getHeight() {
            return height;
        }

        public String getHeightUnit() {
            return heightUnit;
        }

        public String getLocale() {
            return locale;
        }

        public String getMemberSince() {
            return memberSince;
        }

        public String getNickname() {
            return nickname;
        }

        public long getOffsetFromUTCMillis() {
            return offsetFromUTCMillis;
        }

        public String getStartDayOfWeek() {
            return startDayOfWeek;
        }

        public String getState() {
            return state;
        }

        public float getStrideLengthRunning() {
            return strideLengthRunning;
        }

        public float getStrideLengthWalking() {
            return strideLengthWalking;
        }

        public String getTimezone() {
            return timezone;
        }

        public String[] getTopBadges() {
            return topBadges;
        }

        public String getWaterUnit() {
            return waterUnit;
        }

        public String getWaterUnitName() {
            return waterUnitName;
        }

        public float getWeight() {
            return weight;
        }

        public String getWeightUnit() {
            return weightUnit;
        }
    }
}
