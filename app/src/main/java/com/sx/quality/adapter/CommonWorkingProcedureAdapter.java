package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.bean.WorkingBean;

import java.util.List;

/**
 * Create dell By 2018/6/4 9:52
 */

public class CommonWorkingProcedureAdapter extends BaseAdapter<List<WorkingBean>> {
    private Activity mContext;

    public CommonWorkingProcedureAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_procedure, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CommonWorkingProcedureAdapter.MyHolder) holder).bind((WorkingBean) getDataSet().get(position));
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;

        public MyHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }

        public void bind(final WorkingBean bean) {
            txtTitle.setText(bean.getProcessName());
            txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("procedureName", bean.getProcessName());
                    mContext.setResult(Activity.RESULT_OK, intent);
                    mContext.finish();
                }
            });
        }
    }
}
