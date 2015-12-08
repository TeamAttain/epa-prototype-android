package com.smashingboxes.epa_prototype_android.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smashingboxes.epa_prototype_android.R;

/**
 * Created by Austin Lanier on 9/25/15.
 * Updated by
 */
public class RecyclerViewFragment extends Fragment {

    public interface RecyclerViewActivity {
        RecyclerView.Adapter<?> getAdapter();
    }

    private static final String TAG = RecyclerViewFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerViewActivity activity;
    private RecyclerView.Adapter<?> mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof RecyclerViewActivity) {
            this.activity = (RecyclerViewActivity) activity;
        } else {
            throw new IllegalAccessError("Activities adding " + TAG + "must implement " + RecyclerViewActivity.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(activity.getAdapter());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    protected int getLayoutId() {
        return R.layout.recycler_view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
