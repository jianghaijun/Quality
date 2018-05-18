package com.sx.quality.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dalong.marqueeview.MarqueeView;
import com.google.gson.Gson;
import com.sx.quality.adapter.AppInfoAdapter;
import com.sx.quality.bean.AppInfoBean;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.loader.GlideImageLoader;
import com.sx.quality.model.WorkingNewModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DataUtils;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppActivity extends BaseActivity {
    private AppHold hold;
    private Activity mContext;
    private AppInfoAdapter appInfoAdapter;

    public AppActivity(Activity mContext, View layoutApp) {
        this.mContext = mContext;
        hold = new AppHold();
        x.view().inject(hold, layoutApp);
    }

    public void setDate(List<Integer> objList, final WorkingBean data) {
        //设置banner样式
        hold.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        hold.banner.setImageLoader(new GlideImageLoader());
        List<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("");
        hold.banner.setBannerTitles(strings);

        //设置banner动画效果
        hold.banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        hold.banner.isAutoPlay(true);
        //设置轮播时间
        hold.banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        hold.banner.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片集合
        hold.banner.setImages(objList);
        hold.banner.start();
        // 跑马灯文字
        if (data != null) {
            hold.mMarqueeView.setText("最新上传照片工序位置：" + data.getLevelNameAll().replaceAll(",", "→") + "→" + data.getProcessName());
            hold.mMarqueeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProcessDetails(data.getProcessId());
                }
            });
        }
        // 添加图片、标题
        List<AppInfoBean> appInfoList = new ArrayList<>();
        AppInfoBean bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.process_inspection);
        bean.setTitle(mContext.getString(R.string.process_inspection));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.hidden_danger);
        bean.setTitle(mContext.getString(R.string.hidden_danger));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.submit_photo);
        bean.setTitle(mContext.getString(R.string.to_upload_photos));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.experimental_management);
        bean.setTitle(mContext.getString(R.string.experimental_management));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.mail_list);
        bean.setTitle(mContext.getString(R.string.process_report));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.bim_platform);
        bean.setTitle(mContext.getString(R.string.bim_platform));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.qr_code);
        bean.setTitle(mContext.getString(R.string.qr_code));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.video_surveillance);
        bean.setTitle(mContext.getString(R.string.video_surveillance));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.group_management);
        bean.setTitle(mContext.getString(R.string.group_management));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.drawing_inquiry);
        bean.setTitle(mContext.getString(R.string.drawing_inquiry));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.standard_query);
        bean.setTitle(mContext.getString(R.string.standard_query));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.progress_plan);
        bean.setTitle(mContext.getString(R.string.progress_plan));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.first_batch);
        bean.setTitle(mContext.getString(R.string.first_batch));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.online_examination);
        bean.setTitle(mContext.getString(R.string.online_examination));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.labor_competition);
        bean.setTitle(mContext.getString(R.string.labor_competition));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.attendance_card);
        bean.setTitle(mContext.getString(R.string.attendance_card));
        appInfoList.add(bean);

        appInfoAdapter = new AppInfoAdapter(mContext, appInfoList);
        hold.rvAppInfo.setLayoutManager(new GridLayoutManager(mContext, 4));
        hold.rvAppInfo.setAdapter(appInfoAdapter);
    }

    private void getProcessDetails(String processId) {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("processId", processId);
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

    /**
     * 开始轮播
     */
    public void startBanner(){
        //开始轮播
        if (hold != null && hold.banner != null) {
            hold.banner.startAutoPlay();
        }
        if (hold != null && hold.mMarqueeView != null) {
            hold.mMarqueeView.startScroll();
        }
    }

    /**
     * 停止轮播
     */
    public void stopBanner(){
        if (hold != null && hold.banner != null) {
            hold.banner.stopAutoPlay();
        }
        if (hold != null && hold.mMarqueeView != null) {
            hold.mMarqueeView.stopScroll();
        }
    }

    /**
     * 容纳器
     */
    private class AppHold {
        @ViewInject(R.id.banner)
        private Banner banner;

        @ViewInject(R.id.rvAppInfo)
        private RecyclerView rvAppInfo;

        @ViewInject(R.id.mMarqueeView)
        private MarqueeView mMarqueeView;
    }
}
