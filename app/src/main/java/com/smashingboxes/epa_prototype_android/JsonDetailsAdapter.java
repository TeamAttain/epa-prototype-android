package com.smashingboxes.epa_prototype_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin Lanier on 11/19/15.
 * Updated by
 */
public class JsonDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> displayObjects;
    private Gson gson = new Gson();
    private SparseArray<String> gsonCache;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView text1;

        public SimpleViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    public JsonDetailsAdapter(Context context) {
        this.context = context;
        this.displayObjects = new ArrayList<>();
        this.gsonCache = new SparseArray<>();
    }

    @Override
    public int getItemCount() {
        return displayObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return android.R.layout.simple_list_item_1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SimpleViewHolder viewHolder = (SimpleViewHolder) holder;
        String cachedEntry = gsonCache.get(position);
        if (TextUtils.isEmpty(cachedEntry)) {
            cachedEntry = gson.toJson(displayObjects.get(position));
            gsonCache.put(position, cachedEntry);
        }
        viewHolder.text1.setText(cachedEntry);
    }

    public void addObject(Object object) {
        clearCache();
        displayObjects.add(object);
        notifyItemInserted(displayObjects.indexOf(object));
    }

    private void clearCache() {
        gsonCache.clear();
    }
}
