package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.R;
import com.sx.quality.activity.V_2ContractorDetailsActivity;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.model.WorkingNewModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DataUtils;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MsgAdapter extends BaseAdapter<List<WorkingBean>> {
    private Activity mContext;

    public MsgAdapter(Context mContext) {
        this.mContext = (Activity) mContext;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MsgHolder) holder).bind((WorkingBean) getDataSet().get(position));
    }

    public class MsgHolder extends RecyclerView.ViewHolder {
        private TextView txtDate;
        private TextView txtTitle;
        private TextView txtContext;

        public MsgHolder(View itemView) {
            super(itemView);
            txtDate = (TextView) itemView.findViewById(R.id.txtDate);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtContext = (TextView) itemView.findViewById(R.id.txtContext);
        }

        public void bind(final WorkingBean data) {
            String ready = data.getIsRead().equals("1") ? "已读" : "未读";
            txtTitle.setText(data.getCreateUserName() + "(" + ready + ")");
            txtDate.setText(DateUtil.formatDateTime(DateUtil.date(data.getSendTime())));
            txtContext.setText(data.getContent().contains("进入app") ? data.getContent().replace("进入app", "点击") : data.getContent());
            txtContext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProcessDetails(data.getProcessId(), data.getTaskId());
                }
            });
        }
    }

    private void getProcessDetails(String processId, String taskId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("processId", processId);
            obj.put("taskId", taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_PROCESS_DETAIL)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, mContext.getString(R.string.server_exception));
                            }
                        });
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
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            Gson gson = new Gson();
                            final WorkingNewModel model = gson.fromJson(jsonData, WorkingNewModel.class);
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    WorkingBean data = model.getData();
                                    LoadingUtils.hideLoading();
                                    Intent intent = new Intent(mContext, V_2ContractorDetailsActivity.class);
                                    intent.putExtra("status", data.getProcessState());
                                    intent.putExtra("nodeName", data.getLevelNameAll().replaceAll(",", "→") + "→" + data.getProcessName());
                                    intent.putExtra("processCode", data.getProcessCode());
                                    intent.putExtra("processName", data.getProcessName());
                                    long dateLong = data.getEnterTime() > 0 ? data.getEnterTime() : System.currentTimeMillis();
                                    intent.putExtra("enterTime", DataUtils.getDataToStr(DateUtil.date(dateLong)));
                                    intent.putExtra("actualNumber", data.getPhotoNumber());
                                    intent.putExtra("photoContent", data.getPhotoContent());
                                    intent.putExtra("distanceAngle", data.getPhotoDistance());
                                    intent.putExtra("processId", data.getProcessId());
                                    intent.putExtra("levelId", data.getLevelId());
                                    intent.putExtra("location", data.getLocation());
                                    intent.putExtra("dismissal", data.getDismissal());
                                    intent.putExtra("userType", (String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
                                    intent.putExtra("ext1", data.getExt1());
                                    intent.putExtra("ext2", data.getExt2());
                                    intent.putExtra("ext3", data.getExt3());
                                    intent.putExtra("ext4", data.getExt4());
                                    intent.putExtra("ext5", data.getExt5());
                                    intent.putExtra("ext6", data.getExt6());
                                    intent.putExtra("ext7", data.getExt7());
                                    intent.putExtra("ext8", data.getExt8());
                                    intent.putExtra("ext9", data.getExt9());
                                    intent.putExtra("ext10", data.getExt10());
                                    intent.putExtra("canCheck", data.getCanCheck());
                                    mContext.startActivity(intent);
                                }
                            });
                        } else {
                            LoadingUtils.hideLoading();
                            switch (code) {
                                case "3003":
                                case "3004":
                                    // Token异常重新登录
                                    ToastUtil.showLong(mContext, "Token过期请重新登录！");
                                    SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                    ScreenManagerUtil.popAllActivityExceptOne();
                                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                    break;
                                default:
                                    ToastUtil.showLong(mContext, msg);
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, mContext.getString(R.string.data_error));
                            }
                        });
                        e.printStackTrace();
                    }
                } else {
                    LoadingUtils.hideLoading();
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

}
