package com.smashingboxes.epa_prototype_android;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;
import com.smashingboxes.epa_prototype_android.fitbit.models.TimeSeries;
import com.smashingboxes.epa_prototype_android.helpers.Utils;
import com.smashingboxes.epa_prototype_android.network.epa.models.AirQuality;
import com.smashingboxes.epa_prototype_android.network.epa.models.EpaActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.ViewHolder> implements SwipeableItemAdapter<MainActivityAdapter.ViewHolder> {

    public static class ViewHolder extends AbstractSwipeableItemViewHolder {

        View swipeContainer;
        View airQualityBorder;
        TextView activityDateText;
        TextView activityTypeText;
        TextView activityDistanceText;
        TextView activityDistanceUnit;
        TextView activityLocation;
        TextView activityOutside;
        TextView activityInside;

        public ViewHolder(View view) {
            super(view);
            swipeContainer = view.findViewById(R.id.swipe_container);
            airQualityBorder = view.findViewById(R.id.air_quality_status_border);
            activityDateText = (TextView) view.findViewById(R.id.activity_date);
            activityTypeText = (TextView) view.findViewById(R.id.activity_name);
            activityDistanceText = (TextView) view.findViewById(R.id.activity_distance);
            activityDistanceUnit = (TextView) view.findViewById(R.id.activity_unit);
            activityLocation = (TextView) view.findViewById(R.id.activity_location);
            activityOutside = (TextView) view.findViewById(R.id.activity_outside);
            activityInside = (TextView) view.findViewById(R.id.activity_inside);
        }

        @Override
        public View getSwipeableContainerView() {
            return swipeContainer;
        }

    }

    private MainActivity mainActivity;
    private List<AirQuality> airQualities = new ArrayList<>();
    private Map<String, TimeSeries> dateToActivityMap = new HashMap<>();
    private AppStateManager appStateManager;
    private Handler handler = new Handler();

    public MainActivityAdapter(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.appStateManager = AppStateManager.getInstance(mainActivity);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return airQualities.get(position).getId();
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

    public String getAirQualityDateKey(AirQuality airQuality) {
        return Utils.generateFitbitDateTimeString(airQuality.getCreated_at());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Swiping setup
        boolean isLocationPinned = isLocationPinned(position);
        holder.setSwipeItemHorizontalSlideAmount(
                isLocationPinned ? SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
        if (isLocationPinned) {
            holder.setSwipeItemHorizontalSlideAmount(SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_LEFT);
            holder.activityInside.setOnClickListener(mItemViewOnClickListener);
            holder.activityOutside.setOnClickListener(mItemViewOnClickListener);
        } else {
            holder.setSwipeItemHorizontalSlideAmount(0);
            holder.activityOutside.setOnClickListener(null);
            holder.activityInside.setOnClickListener(null);
        }

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

        EpaActivity.Location location = appStateManager.getLocation(getAirQualityDateKey(airQuality));
        holder.activityLocation.setText(location.titleRes);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.activityLocation.setTextAppearance(location.style);
        } else {
            holder.activityLocation.setTextAppearance(mainActivity, location.style);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    /*
     * Swipe Implementation
     */

    private SwipeEventListener mSwipeEventListener;
    private Set<Integer> pinnedLocationList = new HashSet<>();

    public void setLocationPinned(int location) {
        pinnedLocationList.add(location);
    }

    public boolean removePinnedLocation(int location) {
        return pinnedLocationList.remove(location);
    }

    public boolean isLocationPinned(int location) {
        return pinnedLocationList.contains(location);
    }

    public void clearPinnedLocations() {
        pinnedLocationList.clear();
    }

    public void setSwipeEventListener(SwipeEventListener eventListener) {
        this.mSwipeEventListener = eventListener;
    }

    private View.OnClickListener mItemViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSwipeEventListener != null) {
                ViewHolder viewHolder = (ViewHolder) RecyclerViewAdapterUtils.getViewHolder(v);
                mSwipeEventListener.onItemViewClicked(viewHolder, v.getId(), true);  // false -- not pinned

                //Handle location addition update, this should usually be done after the network call has finished
                //but we're doing it here for the sake of time
                AirQuality airQuality = getItem(viewHolder.getAdapterPosition());
                appStateManager.addLocation(getAirQualityDateKey(airQuality),
                        EpaActivity.Location.valueOf(((TextView) v).getText().toString()));

                //Unpin
                UnpinAction unpinAction = new UnpinAction(MainActivityAdapter.this, viewHolder.getAdapterPosition());
                unpinAction.onPerformAction();
                unpinAction.onCleanUp();
            }
        }
    };

    public interface SwipeEventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(ViewHolder v, int clickedViewId, boolean pinned);
    }

    private static class UnpinAction extends SwipeResultActionDefault {
        private MainActivityAdapter mAdapter;
        private final int mPosition;

        UnpinAction(MainActivityAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            if (mAdapter.removePinnedLocation(mPosition)) {
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class SwipeLeftAction extends SwipeResultActionMoveToSwipedDirection {
        private MainActivityAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;

        SwipeLeftAction(MainActivityAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            if (!mAdapter.isLocationPinned(mPosition)) {
                mAdapter.setLocationPinned(mPosition);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

            if (mSetPinned && mAdapter.mSwipeEventListener != null) {
                mAdapter.mSwipeEventListener.onItemPinned(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }


    @Override
    public SwipeResultAction onSwipeItem(ViewHolder holder, int position, int result) {
        switch (result) {
            case SwipeableItemConstants.RESULT_SWIPED_LEFT:
                return new SwipeLeftAction(this, position);
            case SwipeableItemConstants.RESULT_CANCELED:
            default:
                if (position != RecyclerView.NO_POSITION) {
                    return new UnpinAction(this, position);
                } else {
                    return null;
                }
        }
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT;
    }

    @Override
    public void onSetSwipeBackground(ViewHolder holder, int position, int type) {
        switch (type) {
            case SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                AirQuality.IndexType airQualityIndex = getItem(position).getIndexType();
                holder.itemView.setBackground(new ColorDrawable(mainActivity.getResources().getColor(airQualityIndex.getColor())));
                break;
            default:
                holder.itemView.setBackground(null);
        }
    }
}