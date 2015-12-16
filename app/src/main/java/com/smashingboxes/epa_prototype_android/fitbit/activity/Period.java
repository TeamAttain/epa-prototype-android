package com.smashingboxes.epa_prototype_android.fitbit.activity;

public enum Period {
    _1D("1d"), _7D("7d"), _30D("30d"), _1W("1w"),
    _1M("1m"), _3M("3m"), _6M("6m"), _1Y("1y"), MAX("max");

    public final String durationKey;

    Period(String durationKey) {
        this.durationKey = durationKey;
    }
}
