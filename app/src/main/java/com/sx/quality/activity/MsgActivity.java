package com.sx.quality.activity;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sx.quality.adapter.MsgAdapter;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.utils.ToastUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class MsgActivity extends BaseActivity {
    private MsgHolder holder;
    private Context mContext;
    private MsgAdapter msgAdapter;

    public MsgActivity(Context mContext, View layoutMsg) {
        this.mContext = mContext;
        holder = new MsgHolder();
        x.view().inject(holder, layoutMsg);
    }

    public void setDate(List<WorkingBean> msgList) {
        if (msgList == null || msgList.size() == 0) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.txtMsg.setVisibility(View.GONE);
            msgAdapter = new MsgAdapter(mContext, msgList);
            holder.rvMsg.setLayoutManager(new GridLayoutManager(mContext, 1));
            holder.rvMsg.setAdapter(msgAdapter);
        }
    }

    /**
     * 容纳器
     */
    private class MsgHolder {
        @ViewInject(R.id.rvMsg)
        private RecyclerView rvMsg;
        @ViewInject(R.id.txtMsg)
        private TextView txtMsg;
    }
}
