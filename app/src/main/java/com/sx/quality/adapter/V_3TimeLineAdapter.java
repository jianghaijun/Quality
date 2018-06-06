package com.sx.quality.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.R;
import com.sx.quality.bean.OrderStatus;
import com.sx.quality.bean.TimeLineModel;
import com.sx.quality.utils.VectorDrawableUtils;

import java.util.List;

/**
 *
 */
public class V_3TimeLineAdapter extends RecyclerView.Adapter<V_3TimeLineAdapter.TimeLineViewHolder> {
    private List<TimeLineModel> mDataList;
    private Context mContext;

    public V_3TimeLineAdapter(List<TimeLineModel> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = View.inflate(parent.getContext(), R.layout.item_time_line, null);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        TimeLineModel timeLineModel = mDataList.get(position);
        if (timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if (timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorAccent));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.v_2_main_check_bg));
        }

        holder.mDate.setText(mDataList.get(position).getDate());
        holder.mMessage.setText(mDataList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        private TimelineView mTimelineView;
        private TextView mDate;
        private TextView mMessage;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.timeMarker);
            mDate = (TextView) itemView.findViewById(R.id.txtTimeLineDate);
            mMessage = (TextView) itemView.findViewById(R.id.txtTimeLineTitle);
            mTimelineView.initLine(viewType);
        }
    }

}
