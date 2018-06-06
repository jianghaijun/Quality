package com.sx.quality.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.BaseAdapter;
import com.sx.quality.adapter.ILoadCallback;
import com.sx.quality.adapter.LoadMoreAdapterWrapper;
import com.sx.quality.adapter.MsgAdapter;
import com.sx.quality.adapter.OnLoad;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.model.WorkingModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocalMsgActivity extends BaseActivity {
    private MsgHolder holder;
    private Context mContext;
    private MsgAdapter msgAdapter;
    private BaseAdapter mAdapter;
    private int size = 5;
    private int sum;

    public LocalMsgActivity(Context mContext, View layoutMsg) {
        this.mContext = mContext;
        holder = new MsgHolder();
        x.view().inject(holder, layoutMsg);
        size = (int) ((DensityUtil.getScreenHeight() - DensityUtil.getScreenHeight() * 0.18 - DensityUtil.dip2px(5)) / DensityUtil.dip2px(96));
    }

    public void setDate(boolean isLoading) {
        if (!JudgeNetworkIsAvailable.isNetworkAvailable((Activity) mContext)) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.txtMsg.setVisibility(View.GONE);

            // 创建被装饰者类实例
            if (isLoading) {
                msgAdapter = new MsgAdapter(mContext);
                msgAdapter.updateData();
                // 创建装饰者实例，并传入被装饰者和回调接口
                mAdapter = new LoadMoreAdapterWrapper(msgAdapter, new OnLoad() {
                    @Override
                    public void load(int pagePosition, int pageSize, ILoadCallback callback) {
                        /*if (pagePosition != 1 && (pagePosition-1) * pageSize > sum) {
                            callback.onFailure();
                        } else {*/
                            getMsgData(pagePosition, pageSize, callback, pagePosition != 1 && (pagePosition-1) * pageSize > sum);
                        /*}*/
                    }
                });
                holder.rvMsg.setAdapter(mAdapter);
                holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            } else {
                if (msgAdapter == null) {
                    msgAdapter = new MsgAdapter(mContext);
                    // 创建装饰者实例，并传入被装饰者和回调接口
                    mAdapter = new LoadMoreAdapterWrapper(msgAdapter, new OnLoad() {
                        @Override
                        public void load(int pagePosition, int pageSize, ILoadCallback callback) {
                            /*if (pagePosition != 1 && (pagePosition-1) * pageSize > sum) {
                                callback.onFailure();
                            } else {*/
                                getMsgData(pagePosition, pageSize, callback, pagePosition != 1 && (pagePosition-1) * pageSize > sum);
                            /*}*/
                        }
                    });
                    holder.rvMsg.setAdapter(mAdapter);
                    holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                }
            }
        }
    }

    /**
     * 获取消息列表
     * @param pagePosition
     * @param callback
     */
    private void getMsgData(int pagePosition, int pageSize, final ILoadCallback callback, final boolean isHave) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("page", pagePosition);
            obj.put("limit", pageSize);
            obj.put("msgLevel", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_TIMER_TASK_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        if (resultFlag) {
                            Gson gson = new Gson();
                            final WorkingModel model = gson.fromJson(jsonData, WorkingModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isHave) {
                                        sum = model.getTotalNumber();
                                        // 数据的处理最终还是交给被装饰的adapter来处理
                                        msgAdapter.appendData(model.getData());
                                        callback.onSuccess();

                                        if (model == null || model.getData() == null || model.getData().size() == 0 || model.getData().size() < size) {
                                            callback.onFailure();
                                        }
                                    } else {
                                        sum = model.getTotalNumber();
                                        // 数据的处理最终还是交给被装饰的adapter来处理
                                        msgAdapter.appendData(new ArrayList());
                                        callback.onSuccess();

                                        callback.onFailure();
                                    }
                                }
                            });
                        } else {
                            callback.onFailure();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onFailure();
                }
            }
        });
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
