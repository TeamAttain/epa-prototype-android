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
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.smashingboxes.epa_prototype_android.fitbit.auth.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.location.SimplePlace;
import com.smashingboxes.epa_prototype_android.fitbit.models.ActivityData;
import com.smashingboxes.epa_prototype_android.fitbit.settings.SettingsActivity;
import com.smashingboxes.epa_prototype_android.helpers.DateHelper;
import com.smashingboxes.epa_prototype_android.network.epa.EpaRequestManager;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivityDetails;
import com.smashingboxes.epa_prototype_android.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /*
     * Activity Result Request Codes
     */
    private static final int PICK_PLACE_REQUEST = 123;
    private static final int REQUEST_CODE_PLAY_SERVICES_ERROR = 124;

    private static final String INFO_LINK_HTML = "<a href=\"%s\">%s</a>";

    /*
     * Fragment Tags
     */
    private static final String TAG_ADD_LOCATION_DIALOG = "com.smashingboxes.epa_prototype_android.TAG_ADD_LOCATION_DIALOG";

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

    /*
     * A List of AirQualities ordered from most recent to oldest
     */
    private List<AirQuality> airQualityList;
    private AirQuality.IndexType todaysIndexType = AirQuality.IndexType.NONE;

    /*
     * Network Request Listeners
     */

    private final Response.Listener<ActivityData> activityListener = new Response.Listener<ActivityData>() {
        @Override
        public void onResponse(ActivityData response) {
            onActivityDataReceievd(response);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        activitiesAdapter = new MainActivityAdapter(this);
        recyclerView.setAdapter(activitiesAdapter);

        fetchData();
    }

    private void fetchData() {
        getUserActivity();
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

    private void getUserActivity() {
        fitbitRequestManager.getCurrentUserDailySummaryActivityData(DateHelper.generateCurrentDateTime(), activityListener, errorListener);
    }

    private void onActivityDataReceievd(ActivityData activityData) {
        this.activityData = activityData;
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
        airQualityList = airQuality;
        setUpHeader(airQuality.get(0));
        activitiesAdapter.queuedRemoveAll();
        activitiesAdapter.queuedAddAll(airQuality);
    }

    private void setUpHeader(AirQuality airQuality) {
        AirQuality.IndexType indexType = airQuality.getIndexType();
        headerTitle.setText(indexType.getTitle());
        LayerDrawable background = (LayerDrawable) headerBackgroundColor.getBackground();
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

    private void showSelectActivityLocationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final ListView optionsListView = (ListView) getLayoutInflater().inflate(R.layout.listview, null, false);
        final List<EpaActivity.Location> items = new ArrayList<>(Arrays.asList(EpaActivity.Location.values()));
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismissActivityPrompt();
                postActivityForCurrentPlace(items.get(position));
            }
        });
        List<String> options = new ArrayList<>(items.size());
        for (EpaActivity.Location location : items) {
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
        private Handler handler = new Handler();

        public MainActivityAdapter(@NonNull MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        public void queuedRemoveAll() {
            Iterator<AirQuality> iterator = airQualities.iterator();
            while (iterator.hasNext()) {
                iterator.remove();
                notifyItemChanged(0);
            }
        }

        public void queuedAddAll(List<AirQuality> airQualities) {
            for (final AirQuality airQuality : airQualities) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        addItem(airQuality);
                    }
                });
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

        @Override
        public int getItemCount() {
            return airQualities.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = mainActivity.getLayoutInflater();
            return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
        }
    }


}
