package com.sx.quality.activity;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.BaseAdapter;
import com.sx.quality.adapter.ILoadCallback;
import com.sx.quality.adapter.LoadMoreAdapterWrapper;
import com.sx.quality.adapter.OnLoad;
import com.sx.quality.adapter.WorkingProcedureListAdapter;
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

public class WorkingProcedureListActivity extends BaseActivity {
    private WorkingProcedureListAdapter mAdapter;
    private WorkingProcedureHolder holder;
    private BaseAdapter baseAdapter;
    private Activity mContext;
    private int size = 4;
    private int sum;

    public WorkingProcedureListActivity(Activity mContext, View layoutWorkingProcedure) {
        this.mContext = mContext;
        holder = new WorkingProcedureHolder();
        x.view().inject(holder, layoutWorkingProcedure);
        size = (int) ((DensityUtil.getScreenHeight() - DensityUtil.getScreenHeight() * 0.08 - DensityUtil.dip2px(101)) / DensityUtil.dip2px(152)) + 1;
    }

    public void setDate() {
        if (!JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
            holder.rvMsg.setVisibility(View.GONE);
            holder.txtMsg.setVisibility(View.VISIBLE);
        } else {
            holder.rvMsg.setVisibility(View.VISIBLE);
            holder.txtMsg.setVisibility(View.GONE);
            // 创建被装饰者类实例
            mAdapter = new WorkingProcedureListAdapter(mContext);
            mAdapter.updateData();
            // 创建装饰者实例，并传入被装饰者和回调接口
            baseAdapter = new LoadMoreAdapterWrapper(mAdapter, new OnLoad() {
                @Override
                public void load(int pagePosition, int pageSize, ILoadCallback callback) {
                    boolean isHave = pagePosition != 1 && (pagePosition-1) * pageSize > sum;
                    getData(pagePosition, pageSize, callback, isHave);
                }
            });
            holder.rvMsg.setAdapter(baseAdapter);
            holder.rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
    }

    /**
     * 获取消息列表
     * @param pagePosition
     * @param callback
     */
    private void getData(int pagePosition, int pageSize, final ILoadCallback callback, final boolean isHave) {
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
                                    sum = model.getTotalNumber();
                                    // 数据的处理最终还是交给被装饰的adapter来处理
                                    mAdapter.appendData(model.getData());
                                    callback.onSuccess();
                                    if (!isHave) {
                                        if (model == null || model.getData() == null || model.getData().size() == 0 || model.getData().size() < size) {
                                            callback.onFailure();
                                        }
                                    } else {
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
    private class WorkingProcedureHolder {
        @ViewInject(R.id.rvMsg)
        private RecyclerView rvMsg;
        @ViewInject(R.id.txtMsg)
        private TextView txtMsg;
    }
}
