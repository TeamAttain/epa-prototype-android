package com.smashingboxes.epa_prototype_android.fitbit.location;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LocationSelectionAdapter extends RecyclerView.Adapter<LocationSelectionAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(android.R.id.text1);
        }
    }

    private final List<PlaceLikelihood> places = new ArrayList<>();
    private Context context;

    public LocationSelectionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceLikelihood currentLikelihood = getForPosition(position);
        holder.textView.setText(currentLikelihood.getPlace().getAddress());
        holder.itemView.setTag(currentLikelihood);
    }

    public void clear() {
        synchronized (places) {
            places.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(Collection<PlaceLikelihood> toAdd) {
        synchronized (places) {
            places.clear();
            places.addAll(toAdd);
            notifyDataSetChanged();
        }
    }

    public PlaceLikelihood getForPosition(int position) {
        synchronized (places) {
            return places.get(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        return new ViewHolder(view);
    }
}