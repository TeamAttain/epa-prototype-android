package com.smashingboxes.epa_prototype_android.fitbit.location;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.smashingboxes.epa_prototype_android.R;
import com.smashingboxes.epa_prototype_android.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Austin Lanier on 12/15/15.
 * Updated by
 */
public class LocationSelectionFragment extends AppCompatDialogFragment {

    private static final int PERMISSIONS_READ_LOCATION = 123;

    private PendingResult<PlaceLikelihoodBuffer> mPendingResult;
    private PlacesWrapper placesWrapper;
    private LocationSelectionAdapter mAdapter;
    private boolean hasLocationPermission;

    private ConnectionListener mConnectionListener = new SimpleConnectionListener() {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            super.onConnectionFailed(connectionResult);
        }

        @Override
        public void onConnected(Bundle bundle) {
            super.onConnected(bundle);
            getCurrentPlaceLikelihoods();
        }
    };

    protected int getLayoutId() {
        return R.layout.recycler_view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new LocationSelectionAdapter(activity);
        placesWrapper = new PlacesWrapper(activity);
        checkLocationPermissions(activity);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setBackgroundColor(Color.WHITE);
        recyclerView.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog_Alert_V7);
        builder.setTitle(R.string.select_location);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setView(view);
        return builder.create();
    }

    /**
     * Starts the permissions location flow
     *
     * @param thisActivity
     */
    public void checkLocationPermissions(Activity thisActivity) {
        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder requestPermission = new AlertDialog.Builder(thisActivity);
                requestPermission.setTitle(getString(R.string.permission_location_title, getString(R.string.app_name)));
                requestPermission.setMessage(getString(R.string.permission_location_msg));
                requestPermission.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSIONS_READ_LOCATION);
                    }
                });
                requestPermission.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                requestPermission.show();
            } else {
                ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_READ_LOCATION);
            }
        } else {
            onPermissionsValidated();
        }
    }

    private void onPermissionsValidated(){
        hasLocationPermission = true;
        placesWrapper.addConnectionListener(mConnectionListener);
        placesWrapper.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_READ_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionsValidated();
                } else {
                    hasLocationPermission = false;
                    Toast.makeText(getActivity(), R.string.permission_location_denied, Toast.LENGTH_LONG).show();
                    dismiss();
                }
        }
    }

    @Override
    public void onPause() {
        cancelPendingResult();
        super.onPause();
    }

    private void getCurrentPlaceLikelihoods() {
        if(hasLocationPermission && placesWrapper.getConnectionStatus() == GoogleApiWrapper.ConnectionStatus.CONNECTED) {
            mPendingResult = placesWrapper.getCurrentPlace();
            mPendingResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                    setPlaceList(fromBuffer(placeLikelihoods));
                }
            });
        }
    }

    private List<PlaceLikelihood> fromBuffer(PlaceLikelihoodBuffer placeLikelihood) {
        Iterator<PlaceLikelihood> iterator = placeLikelihood.iterator();
        List<PlaceLikelihood> places = new ArrayList<>();
        while (iterator.hasNext()) {
            places.add(iterator.next());
        }
        return places;
    }

    private void cancelPendingResult() {
        if (mPendingResult != null) {
            mPendingResult.cancel();
        }
    }

    @Override
    public void onDetach() {
        placesWrapper.disconnect();
        super.onDetach();
    }

    private void setPlaceList(List<PlaceLikelihood> places) {
        mAdapter.clear();
        mAdapter.addAll(places);

    }

}
