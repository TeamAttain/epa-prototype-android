package com.smashingboxes.epa_prototype_android;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitRequestManager;
import com.smashingboxes.epa_prototype_android.fitbit.activity.ActivityResourcePath;
import com.smashingboxes.epa_prototype_android.fitbit.activity.Period;
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.fitbit.models.ActivityData;
import com.smashingboxes.epa_prototype_android.fitbit.models.FitbitProfile;
import com.smashingboxes.epa_prototype_android.fitbit.models.TimeSeries;
import com.smashingboxes.epa_prototype_android.fitbit.settings.SettingsActivity;
import com.smashingboxes.epa_prototype_android.helpers.LocationHelper;
import com.smashingboxes.epa_prototype_android.helpers.Utils;
import com.smashingboxes.epa_prototype_android.network.epa.EpaRequestManager;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;
import com.smashingboxes.epa_prototype_android.views.DividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public class MainActivity extends AppCompatActivity {

    /*
     * Activity Result Request Codes
     */
    private static final int PICK_PLACE_REQUEST = 123;
    private static final int REQUEST_CODE_PLAY_SERVICES_ERROR = 124;

    private static final String INFO_LINK_HTML = "<a href=\"%s\">%s</a>";

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
    private ActivityData activityData;
    private FitbitProfile userProfile;

    /*
     * A List of AirQualities ordered from most recent to oldest
     */
    private List<AirQuality> airQualityList;

    /*
     * Today's last airquality index.  This is used to style the header
     */
    private AirQuality.IndexType todaysIndexType = AirQuality.IndexType.NONE;

    /*
     * Our Fitbit resource path time series selection, default to distance
     */
    private ActivityResourcePath selectedResourcePath = ActivityResourcePath.DISTANCE;

    /*
     * Network Request Listeners
     */
    private final Response.Listener<ActivityData> activityListener = new Response.Listener<ActivityData>() {
        @Override
        public void onResponse(ActivityData response) {
            onActivityDataRecieved(response);
        }
    };

    private final Response.Listener<ArrayList<AirQuality>> airQualityListener = new Response.Listener<ArrayList<AirQuality>>() {
        @Override
        public void onResponse(ArrayList<AirQuality> response) {
            onAirQualityReceived(response);
        }
    };

    private final Response.Listener<JSONObject> postActivitiesListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private final Response.Listener<ArrayList<TimeSeries>> activityTimeSeriesListener = new Response.Listener<ArrayList<TimeSeries>>() {
        @Override
        public void onResponse(ArrayList<TimeSeries> response) {
            activitiesAdapter.mapActivityDates(response);
        }
    };

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
        }
    };

    /*
     * UI Elements
     */
    @Bind(R.id.air_quality_today_info_container)
    View todayInfoContainerView;
    @Bind(R.id.air_quality_title)
    TextView headerTitle;
    @Bind(R.id.air_quality_info_link)
    TextView headerInfo;
    @Bind(R.id.backdrop)
    ImageView headerBackgroundColor;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    private Snackbar selectLocationSnack;
    private AlertDialog activityLocationDialog;
    private MainActivityAdapter activitiesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        headerInfo.setText(Html.fromHtml(String.format(INFO_LINK_HTML, getString(R.string.air_quality_help),
                getString(R.string.what_does_this_mean))));
        headerInfo.setMovementMethod(LinkMovementMethod.getInstance());

        appStateManager = AppStateManager.getInstance(this);
        loginCache = FitbitLoginCache.getInstance(this);
        fitbitRequestManager = new FitbitRequestManager(this, loginCache.getLoginModel(), this);
        epaRequestManager = new EpaRequestManager(this, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        activitiesAdapter = new MainActivityAdapter(this);
        recyclerView.setAdapter(activitiesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchData();
    }

    private void fetchData() {
        getAirQualityData();
        getUserActivityTimeSeries();
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
        if (selectLocationSnack == null || !selectLocationSnack.isShownOrQueued()) {
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

    private void getUserActivityTimeSeries() {
        fitbitRequestManager.getCurrentUserTimeSeriesTrackerData(selectedResourcePath, Period._6M, activityTimeSeriesListener, errorListener);
    }

    private void getUserActivitySummary() {
        fitbitRequestManager.getCurrentUserDailySummaryActivityData(Utils.generateFitbitCurrentDateTime(), activityListener, errorListener);
    }

    private void onActivityDataRecieved(ActivityData activityData) {
        this.activityData = activityData;
        Toast.makeText(this, activityData.toString(), Toast.LENGTH_LONG).show();
    }

    private void getAirQualityData() {
        if (hasSelectedPlace()) {
            SimplePlace currentPlace = appStateManager.getPlace();
            epaRequestManager.getAirQualityData(currentPlace, airQualityListener, errorListener);
        } else {
            handleLocationError();
        }
    }

    private void onAirQualityReceived(ArrayList<AirQuality> airQualities) {
        airQualities.clear();
        addFakeDataIfEmpty(airQualities);
        if (airQualities.size() > 0) {
            airQualityList = airQualities;
            setUpHeader(airQualities.get(0));
            activitiesAdapter.queuedRemoveAll();
            activitiesAdapter.queuedAddAll(airQualities, getResources().getInteger(android.R.integer.config_shortAnimTime));
        } else {
            makeSnackbar(getString(R.string.error_no_air_quality_data), getString(R.string.try_again), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchData();
                }
            }).show();
        }
    }

    /**
     * Generates random AirQuality data to ensure data for demo purposes.
     *
     * @param airQualities
     */
    private void addFakeDataIfEmpty(ArrayList<AirQuality> airQualities) {
        if (airQualities.size() == 0) {
            final long DAY_MILLIS = 86400000;
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < 7; i++) {
                int aqi = random.nextInt(350);
                AirQuality.IndexType indexType = AirQuality.IndexType.forIndexRange(aqi);
                String now = Utils.formatTimeLong(System.currentTimeMillis() - (DAY_MILLIS * i));
                airQualities.add(new AirQuality(i, aqi, indexType.getTitle(), now, i,
                        0, TimeZone.getDefault().getDisplayName(), 0, "", "", "NC", "", now, now));
            }
        }
    }

    private Snackbar makeSnackbar(String title, String actionTitle, View.OnClickListener actionOnClickListener) {
        return Snackbar.make(findViewById(R.id.coordinator_container), title, Snackbar.LENGTH_LONG).setAction(actionTitle, actionOnClickListener);
    }

    private void setUpHeader(AirQuality airQuality) {
        AirQuality.IndexType indexType = airQuality.getIndexType();
        headerTitle.setText(indexType.getTitle());
        LayerDrawable background = (LayerDrawable) headerBackgroundColor.getBackground();
        todayInfoContainerView.animate().alpha(1).setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        animateBackgroundDrawable((GradientDrawable) background.getDrawable(0), getResources().getColor(indexType.getColor()));
        mCollapsingToolbarLayout.setContentScrim(new ColorDrawable(getResources().getColor(indexType.getColor())));
    }

    private void animateBackgroundDrawable(final GradientDrawable gradientDrawable, int toColor) {
        ValueAnimator animator = ObjectAnimator.ofObject(new ArgbEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                int eval = (Integer) super.evaluate(fraction, startValue, endValue);
                gradientDrawable.setColor(eval);
                return eval;
            }
        }, todaysIndexType.getColor(), toColor).setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        animator.start();
    }

    private void postActivityForCurrentPlace(@NonNull EpaActivity.Location location) {
        if (hasSelectedPlace()) {
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
        dismissActivityPrompt();
        super.onStop();
    }

    private void dismissActivityPrompt() {
        if (activityLocationDialog != null) {
            activityLocationDialog.dismiss();
            activityLocationDialog = null;
        }
    }

    public ActivityResourcePath getSelectedResourcePath() {
        return selectedResourcePath;
    }

    public static class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {

            View airQualityBorder;
            TextView activityDateText;
            TextView activityTypeText;
            TextView activityDistanceText;
            TextView activityDistanceUnit;
            TextView activityLocation;

            public ViewHolder(View view) {
                super(view);
                airQualityBorder = view.findViewById(R.id.air_quality_status_border);
                activityDateText = (TextView) view.findViewById(R.id.activity_date);
                activityTypeText = (TextView) view.findViewById(R.id.activity_name);
                activityDistanceText = (TextView) view.findViewById(R.id.activity_distance);
                activityDistanceUnit = (TextView) view.findViewById(R.id.activity_unit);
                activityLocation = (TextView) view.findViewById(R.id.activity_location);
            }
        }

        private MainActivity mainActivity;
        private List<AirQuality> airQualities = new ArrayList<>();
        private Map<String, TimeSeries> dateToActivityMap = new HashMap<>();
        private LocationHelper locationHelper;
        private Handler handler = new Handler();

        public MainActivityAdapter(@NonNull MainActivity mainActivity) {
            this.mainActivity = mainActivity;
            this.locationHelper = new LocationHelper(mainActivity);
        }

        public void queuedRemoveAll() {
            for (final AirQuality airQuality : airQualities) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        removeItem(airQuality);
                    }
                });
            }
        }

        public void queuedAddAll(List<AirQuality> airQualities, long delay) {
            int count = 0;
            for (final AirQuality airQuality : airQualities) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addItem(airQuality);
                    }
                }, count++ * delay);
            }
        }

        public void addItem(AirQuality airQuality) {
            airQualities.add(airQuality);
            int index = airQualities.indexOf(airQuality);
            notifyItemInserted(index);
        }

        public void setItem(int position, AirQuality airQuality) {
            airQualities.set(position, airQuality);
            notifyItemInserted(position);
        }

        public AirQuality getItem(int position) {
            return airQualities.get(position);
        }

        public boolean removeItem(AirQuality airQuality) {
            int index = airQualities.indexOf(airQuality);
            boolean result = airQualities.remove(airQuality);
            notifyItemRemoved(index);
            return result;
        }

        @Override
        public int getItemCount() {
            return airQualities.size();
        }

        /**
         * Maps a list of time series entiries to their activity dates
         *
         * @param activityTimeSeries
         */
        public void mapActivityDates(List<TimeSeries> activityTimeSeries) {
            dateToActivityMap.clear();
            for (TimeSeries series : activityTimeSeries) {
                addActivityData(series);
            }
            notifyDataSetChanged();
        }

        /**
         * Maps a time series entry to it's activity date
         *
         * @param activityData
         * @return
         */
        public TimeSeries addActivityData(TimeSeries activityData) {
            return dateToActivityMap.put(activityData.getDateTime(), activityData);
        }

        /**
         * Retrieves a TimeSeries point for this AirQuality's date
         *
         * @param key
         * @return
         */
        public TimeSeries forAirQuality(AirQuality key) {
            return dateToActivityMap.get(getAirQualityDateKey(key));
        }

        public String getAirQualityDateKey(AirQuality airQuality){
            return Utils.generateFitbitDateTimeString(airQuality.getCreated_at());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AirQuality airQuality = getItem(position);
            AirQuality.IndexType indexType = airQuality.getIndexType();
            holder.airQualityBorder.setBackground(new ColorDrawable(holder.itemView.getContext()
                    .getResources().getColor(indexType.getColor())));
            holder.activityDateText.setText(Utils.formatDate(airQuality.getDate_observed()));

            TimeSeries activityData = forAirQuality(airQuality);
            if (activityData != null) {
                holder.activityTypeText.setText(mainActivity.getString(R.string.running));
                holder.activityDistanceText.setText(Utils.formatDistance(activityData.getValue()));
            } else {
                holder.activityTypeText.setText(mainActivity.getString(R.string.none));
                holder.activityDistanceText.setText("0");
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mainActivity.getLayoutInflater();
            return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
        }
    }


}
