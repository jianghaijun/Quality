package com.sx.quality.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dalong.marqueeview.MarqueeView;
import com.sx.quality.adapter.AppInfoAdapter;
import com.sx.quality.bean.AppInfoBean;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.loader.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

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
        if (data != null && StrUtil.isNotEmpty(data.getProcessId())) {
            hold.mMarqueeView.setText("最新上传照片工序位置：" + data.getLevelNameAll().replaceAll(",", "→") + "→" + data.getProcessName());
            hold.mMarqueeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, V_3ContractorDetailsActivity.class);
                    intent.putExtra("processId", data.getProcessId());
                    intent.putExtra("processState", data.getProcessState());
                    intent.putExtra("processPath", data.getLevelNameAll());
                    intent.putExtra("taskId", data.getTaskId());
                    intent.putExtra("canCheck", data.getCanCheck());
                    intent.putExtra("isPopTakePhoto", data);
                    mContext.startActivity(intent);
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

    /**
     * 开始轮播
     */
    public void startBanner() {
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
    public void stopBanner() {
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
