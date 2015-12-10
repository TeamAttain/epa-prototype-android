package com.smashingboxes.epa_prototype_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitLoginCache;
import com.smashingboxes.epa_prototype_android.fitbit.FitbitRequestManager;
import com.smashingboxes.epa_prototype_android.models.FitbitProfile;

public class MainActivity extends AppCompatActivity {

    private FitbitLoginCache loginCache;
    private FitbitRequestManager requestManager;
    private FitbitProfile userProfile;

    private Response.Listener<FitbitProfile> profileListener = new Response.Listener<FitbitProfile>(){
        @Override
        public void onResponse(FitbitProfile response) {
            userProfile = response;
            ((TextView) findViewById(R.id.user_profile)).setText(new Gson().toJson(response));
        }
    };

    //TODO SUBCLASS
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            ((TextView) findViewById(R.id.user_profile)).setText(new Gson().toJson(error));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginCache = FitbitLoginCache.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        requestManager = new FitbitRequestManager(loginCache.getLoginModel());
        requestManager.getCurrentUserProfile(this, profileListener, errorListener);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if(id == R.id.menu_logout){
            FitbitLoginCache.logout(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
