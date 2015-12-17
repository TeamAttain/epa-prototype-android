package com.smashingboxes.epa_prototype_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitRequestManager;
import com.smashingboxes.epa_prototype_android.fitbit.activity.Period;
import com.smashingboxes.epa_prototype_android.fitbit.activity.TimeSeriesResourcePath;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.settings.SettingsActivity;
import com.smashingboxes.epa_prototype_android.helpers.DateHelper;
import com.smashingboxes.epa_prototype_android.models.ActivityData;
import com.smashingboxes.epa_prototype_android.models.AirQuality;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.models.SimplePlace;
import com.smashingboxes.epa_prototype_android.network.epa.EpaRequestManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_PLACE_REQUEST = 123;
    private static final int REQUEST_CODE_PLAY_SERVICES_ERROR = 124;

    private FitbitLoginCache loginCache;
    private FitbitRequestManager fitbitRequestManager;
    private EpaRequestManager epaRequestManager;

    private Picasso picasso;
    private FitbitProfile userProfile;
    private ActivityData activityData;
    private String timeSeries;
    private ArrayList<AirQuality> airQualityTimeSeries;

    private final Response.Listener<FitbitProfile> profileListener = new Response.Listener<FitbitProfile>() {
        @Override
        public void onResponse(FitbitProfile response) {
            onUserProfileReceived(response);
        }
    };

    private final Response.Listener<ActivityData> activityListener = new Response.Listener<ActivityData>() {
        @Override
        public void onResponse(ActivityData response) {
            onActivityDataReceievd(response);
        }
    };

    private final Response.Listener<String> timeSeriesListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            onTimeSeriesReceived(response);
        }
    };

    private final Response.Listener<ArrayList<AirQuality>> airQualityListener = new Response.Listener<ArrayList<AirQuality>>(){
        @Override
        public void onResponse(ArrayList<AirQuality> response) {
            onAirQualityReceived(response);
        }
    };

    //TODO SUBCLASS
    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    private JsonDetailsAdapter detailsAdapter;
    private TimeSeriesResourcePath selectedTimeSeries = TimeSeriesResourcePath.STEPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginCache = FitbitLoginCache.getInstance(this);
        fitbitRequestManager = new FitbitRequestManager(this, loginCache.getLoginModel(), this);
        epaRequestManager = new EpaRequestManager(this, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        detailsAdapter = new JsonDetailsAdapter(this);
        recyclerView.setAdapter(detailsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!startSelectUserLocationActivity()) {
            fetchData();
        }
    }

    private void fetchData() {
        getUserProfile();
        getUserActivity();
        getCurrentlySelectedTimeSeries();
        getAirQualityData();
    }

    private boolean startSelectUserLocationActivity() {
        if (AppStateManager.getInstance(this).getPlace() == null) {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(this), PICK_PLACE_REQUEST);
                return true;
            } catch (GooglePlayServicesRepairableException e) {
                Toast.makeText(getApplicationContext(), R.string.play_services_repairable, Toast.LENGTH_LONG).show();
            } catch (GooglePlayServicesNotAvailableException ex) {
                GooglePlayServicesUtil.showErrorDialogFragment(ex.errorCode, this, null, REQUEST_CODE_PLAY_SERVICES_ERROR, null);
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                SimplePlace placeToStore = new SimplePlace(place.getId(), place.getName().toString(), place.getAddress().toString(),
                        place.getLatLng().latitude, place.getLatLng().longitude);
                AppStateManager.getInstance(this).savePlace(placeToStore);
                Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile() {
        fitbitRequestManager.getCurrentUserProfile(profileListener, errorListener);
    }

    private void onUserProfileReceived(FitbitProfile profile) {
        this.userProfile = profile;
        detailsAdapter.addObject(profile);
    }

    private void getUserActivity() {
        fitbitRequestManager.getCurrentUserDailySummaryActivityData(DateHelper.generateCurrentDateTime(), activityListener, errorListener);
    }

    private void onActivityDataReceievd(ActivityData activityData) {
        this.activityData = activityData;
        detailsAdapter.addObject(activityData);
    }


    private void getCurrentlySelectedTimeSeries() {
        fitbitRequestManager.getCurrentUserTimeSeriesTrackerData(selectedTimeSeries, DateHelper.generateCurrentDateTime(),
                Period._3M, timeSeriesListener, errorListener);
    }

    private void onTimeSeriesReceived(String timeSeries) {
        this.timeSeries = timeSeries;
        detailsAdapter.addObject(timeSeries);
    }

    private void getAirQualityData(){
        SimplePlace currentPlace = AppStateManager.getInstance(this).getPlace();
        if(currentPlace != null){
            epaRequestManager.getAirQualityData(currentPlace, airQualityListener, errorListener);
        }
    }

    private void onAirQualityReceived(ArrayList<AirQuality> airQuality){
        airQualityTimeSeries = airQuality;
        detailsAdapter.addObject(airQuality);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
        fitbitRequestManager.cancelAllForTag(this);
        epaRequestManager.cancelAllForTag(this);
        super.onStop();
    }
}
