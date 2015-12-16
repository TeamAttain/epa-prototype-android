package com.smashingboxes.epa_prototype_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitRequestManager;
import com.smashingboxes.epa_prototype_android.fitbit.activity.Period;
import com.smashingboxes.epa_prototype_android.fitbit.activity.TimeSeriesResourcePath;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.location.LocationSelectionFragment;
import com.smashingboxes.epa_prototype_android.fitbit.settings.SettingsActivity;
import com.smashingboxes.epa_prototype_android.helpers.DateHelper;
import com.smashingboxes.epa_prototype_android.models.ActivityData;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_LOCATION_SELECTION = "com.smashingboxes.epa_prototype_android.TAG_LOCATION_SELECTION";

    private FitbitLoginCache loginCache;
    private FitbitRequestManager requestManager;

    private Picasso picasso;
    private FitbitProfile userProfile;
    private ActivityData activityData;
    private String timeSeries;

    private Response.Listener<FitbitProfile> profileListener = new Response.Listener<FitbitProfile>() {
        @Override
        public void onResponse(FitbitProfile response) {
            onUserProfileReceived(response);
        }
    };

    private Response.Listener<ActivityData> activityListener = new Response.Listener<ActivityData>() {
        @Override
        public void onResponse(ActivityData response) {
            onActivityDataReceievd(response);
        }
    };

    private Response.Listener<String> timeSeriesListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            onTimeSeriesReceived(response);
        }
    };

    //TODO SUBCLASS
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private RecyclerView recyclerView;
    private JsonDetailsAdapter detailsAdapter;
    private TimeSeriesResourcePath selectedTimeSeries = TimeSeriesResourcePath.STEPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginCache = FitbitLoginCache.getInstance(this);
        requestManager = new FitbitRequestManager(this, loginCache.getLoginModel(), this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        detailsAdapter = new JsonDetailsAdapter(this);
        recyclerView.setAdapter(detailsAdapter);

        getUserProfile();
        getUserActivity();
        getCurrentlySelectedTimeSeries();
        showSelectLocationDialog();
    }

    private void showSelectLocationDialog(){
        if(getSupportFragmentManager().findFragmentByTag(TAG_LOCATION_SELECTION) == null) {
            LocationSelectionFragment fragment = new LocationSelectionFragment();
            fragment.show(getSupportFragmentManager(), TAG_LOCATION_SELECTION);
        }

    }

    private void getUserProfile() {
        requestManager.getCurrentUserProfile(profileListener, errorListener);
    }

    private void onUserProfileReceived(FitbitProfile profile) {
        this.userProfile = profile;
        detailsAdapter.addObject(profile);
    }

    private void getUserActivity() {
        requestManager.getCurrentUserDailySummaryActivityData(DateHelper.generateCurrentDateTime(), activityListener, errorListener);
    }

    private void onActivityDataReceievd(ActivityData activityData) {
        this.activityData = activityData;
        detailsAdapter.addObject(activityData);
    }


    private void getCurrentlySelectedTimeSeries() {
        requestManager.getCurrentUserTimeSeriesData(selectedTimeSeries, DateHelper.generateCurrentDateTime(),
                Period._3M, timeSeriesListener, errorListener);
    }

    private void onTimeSeriesReceived(String timeSeries) {
        this.timeSeries = timeSeries;
        detailsAdapter.addObject(timeSeries);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.menu_logout) {
            FitbitLoginCache.logout(this);
        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        requestManager.cancelAllForTag(this);
        super.onStop();
    }
}
