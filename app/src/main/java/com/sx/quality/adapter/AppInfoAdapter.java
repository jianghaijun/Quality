package com.sx.quality.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.ProcessReportActivity;
import com.sx.quality.activity.R;
import com.sx.quality.activity.UpLoadPhotosActivity;
import com.sx.quality.activity.V_2ContractorActivity;
import com.sx.quality.bean.AppInfoBean;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.model.SameDayModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create dell By 2018/4/8 10:44
 */
public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.AppInfoHold> {
    private Activity mContext;
    private List<AppInfoBean> appInfoBeanList;

    public AppInfoAdapter(Context mContext, List<AppInfoBean> appInfoBeanList) {
        this.mContext = (Activity) mContext;
        this.appInfoBeanList = appInfoBeanList;
    }

    @Override
    public AppInfoHold onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AppInfoHold(LayoutInflater.from(mContext).inflate(R.layout.item_app_info, parent, false));
    }

    @Override
    public void onBindViewHolder(AppInfoHold holder, final int position) {
        Drawable top = ContextCompat.getDrawable(mContext, appInfoBeanList.get(position).getImgUrl());
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        holder.imgView.setImageDrawable(ContextCompat.getDrawable(mContext, appInfoBeanList.get(position).getImgUrl()));
        holder.txtTitle.setText(appInfoBeanList.get(position).getTitle());
        if (position == 2) {
            holder.txtSubmitPhoneNum.setVisibility(View.VISIBLE);
            List<ContractorListPhotosBean> upLoadPhotosBeenList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "")).find(ContractorListPhotosBean.class);
            if (upLoadPhotosBeenList != null) {
                holder.txtSubmitPhoneNum.setText(upLoadPhotosBeenList.size() > 99 ? "99+" : upLoadPhotosBeenList.size() + "");
            } else {
                holder.txtSubmitPhoneNum.setVisibility(View.GONE);
            }
        } else {
            holder.txtSubmitPhoneNum.setVisibility(View.GONE);
        }

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (position) {
                    // 工序检查
                    case 0:
                        intent = new Intent(mContext, V_2ContractorActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "0");
                        mContext.startActivity(intent);
                        break;
                    // 安全隐患
                    case 1:
                        intent = new Intent(mContext, V_2ContractorActivity.class);
                        SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "1");
                        mContext.startActivity(intent);
                        break;
                    // 待上传照片
                    case 2:
                        intent = new Intent(mContext, UpLoadPhotosActivity.class);
                        mContext.startActivity(intent);
                        break;
                    // 工序报表
                    case 4:
                        if (JudgeNetworkIsAvailable.isNetworkAvailable(mContext)) {
                            getSameDayData();
                        } else {
                            ToastUtil.showShort(mContext, mContext.getString(R.string.not_network));
                        }
                        break;
                    default:
                        ToastUtil.showShort(mContext, "该功能正在开发中，敬请期待!");
                        break;
                }
            }
        });
    }

    /**
     * 获取数据
     */
    private void getSameDayData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("startDate", System.currentTimeMillis());
            obj.put("endDate", DateUtil.tomorrow().getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PROCESS_REPORT_TODAY)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread(mContext.getString(R.string.server_exception));
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
                            SameDayModel model = gson.fromJson(jsonData, SameDayModel.class);
                            ConstantsUtil.sameDayBean = model.getData();
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(mContext, ProcessReportActivity.class);
                                    mContext.startActivity(intent);
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            LoadingUtils.hideLoading();
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    LoadingUtils.hideLoading();
                    runChildrenThread(mContext.getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 子线程运行
     */
    private void runChildrenThread(final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    /**
     * Token过期
     *
     * @param code
     * @param msg
     */
    private void tokenErr(final String code, final String msg) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    @Override
    public int getItemCount() {
        return appInfoBeanList.size();
    }

    public class AppInfoHold extends RecyclerView.ViewHolder {
        private ImageView imgView;
        private TextView txtTitle;
        private TextView txtSubmitPhoneNum;

        public AppInfoHold(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.imgInfo);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtSubmitPhoneNum = (TextView) itemView.findViewById(R.id.txtSubmitPhoneNum);
        }
    }

}
