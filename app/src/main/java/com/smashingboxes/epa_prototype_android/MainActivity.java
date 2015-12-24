package com.smashingboxes.epa_prototype_android;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
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
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.fitbit.models.ActivityData;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.fitbit.settings.SettingsActivity;
import com.smashingboxes.epa_prototype_android.helpers.DateHelper;
import com.smashingboxes.epa_prototype_android.network.epa.EpaRequestManager;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivityDetails;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;

public class MainActivity extends AppCompatActivity {

    /*
     * Activity Result Request Codes
     */
    private static final int PICK_PLACE_REQUEST = 123;
    private static final int REQUEST_CODE_PLAY_SERVICES_ERROR = 124;

    /*
     * Application state singleton helpers
     */
    private AppStateManager appStateManager;
    private FitbitLoginCache loginCache;

    /*
     * Network request managers
     */
    private FitbitRequestManager fitbitRequestManager;
    private EpaRequestManager epaRequestManager;

    /*
     * Response Objects
     */
    private FitbitProfile userProfile;
    private ActivityData activityData;
    private String timeSeries;
    private ArrayList<AirQuality> airQualityTimeSeries;

    /*
     * Network Request Listeners
     */
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

    private final Response.Listener<ArrayList<AirQuality>> airQualityListener = new Response.Listener<ArrayList<AirQuality>>() {
        @Override
        public void onResponse(ArrayList<AirQuality> response) {
            onAirQualityReceived(response);
        }
    };

    private final Response.Listener<String> postActivitiesListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
        }
    };

    private final Response.Listener<ArrayList<EpaActivityDetails>> activityDetailsListener = new Response.Listener<ArrayList<EpaActivityDetails>>() {
        @Override
        public void onResponse(ArrayList<EpaActivityDetails> response) {
            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        }
    };

    //TODO SUBCLASS
    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    /*
     * UI Elements
     */
    private Snackbar selectLocationSnack;
    private AlertDialog activityLocationDialog;
    private JsonDetailsAdapter detailsAdapter;
    private TimeSeriesResourcePath selectedTimeSeries = TimeSeriesResourcePath.STEPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appStateManager = AppStateManager.getInstance(this);
        loginCache = FitbitLoginCache.getInstance(this);
        fitbitRequestManager = new FitbitRequestManager(this, loginCache.getLoginModel(), this);
        epaRequestManager = new EpaRequestManager(this, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        detailsAdapter = new JsonDetailsAdapter(this);
        recyclerView.setAdapter(detailsAdapter);

        fetchData();
    }

    private void fetchData() {
        getUserProfile();
        getUserActivity();
        getCurrentlySelectedTimeSeries();
        getAirQualityData();
    }

    private void startSelectUserLocationActivity() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PICK_PLACE_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(getApplicationContext(), R.string.play_services_repairable, Toast.LENGTH_LONG).show();
        } catch (GooglePlayServicesNotAvailableException ex) {
            GooglePlayServicesUtil.showErrorDialogFragment(ex.errorCode, this, null, REQUEST_CODE_PLAY_SERVICES_ERROR, null);
        }
    }

    private boolean hasSelectedPlace() {
        return appStateManager.getPlace() != null;
    }

    private void handleLocationError() {
        if(selectLocationSnack == null || !selectLocationSnack.isShownOrQueued()) {
            selectLocationSnack = Snackbar.make(findViewById(R.id.coordinator_container), R.string.error_no_location, Snackbar.LENGTH_INDEFINITE).setAction(R.string.select_location,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startSelectUserLocationActivity();
                            selectLocationSnack.dismiss();
                            selectLocationSnack = null;
                        }
                    });
            selectLocationSnack.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                SimplePlace placeToStore = new SimplePlace(place.getId(), place.getName().toString(), place.getAddress().toString(),
                        place.getLatLng().latitude, place.getLatLng().longitude);
                AppStateManager.getInstance(this).savePlace(placeToStore);
                fetchData();
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
                Period._1W, timeSeriesListener, errorListener);
    }

    private void onTimeSeriesReceived(String timeSeries) {
        this.timeSeries = timeSeries;
        detailsAdapter.addObject(timeSeries);
    }

    private void getAirQualityData() {
        if (hasSelectedPlace()) {
            SimplePlace currentPlace = appStateManager.getPlace();
            epaRequestManager.getAirQualityData(currentPlace, airQualityListener, errorListener);
        } else {
            handleLocationError();
        }
    }

    private void onAirQualityReceived(ArrayList<AirQuality> airQuality) {
        airQualityTimeSeries = airQuality;
        detailsAdapter.addObject(airQuality);
    }

    private void showSelectActivityLocationDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Alert_V7);
        final ListView optionsListView = (ListView) getLayoutInflater().inflate(R.layout.listview, null, false);
        final List<EpaActivity.Location> items = new ArrayList<>(Arrays.asList(EpaActivity.Location.values()));
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(activityLocationDialog != null){
                    activityLocationDialog.dismiss();
                    activityLocationDialog = null;
                }
                postActivityForCurrentPlace(items.get(position));
            }
        });
        List<String> options = new ArrayList<>(items.size());
        for(EpaActivity.Location location : items){
            options.add(location.name);
        }
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(this, R.layout.list_item, options);
        optionsListView.setAdapter(optionsAdapter);
        optionsListView.setSelection(0);
        dialogBuilder.setTitle(R.string.activity_location_selection);
        dialogBuilder.setView(optionsListView);
        dialogBuilder.setNegativeButton(R.string.cancel, null);
        activityLocationDialog = dialogBuilder.show();
    }

    private void postActivityForCurrentPlace(@NonNull EpaActivity.Location location) {
        if(hasSelectedPlace()) {
            SimplePlace currentPlace = appStateManager.getPlace();
            List<EpaActivity> mActivities = new ArrayList<>();
            mActivities.add(new EpaActivity(String.valueOf(currentPlace.getLat()), String.valueOf(currentPlace.getLng()), location));
            epaRequestManager.postActivities(mActivities, postActivitiesListener, errorListener);
        } else {
            handleLocationError();
        }
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
