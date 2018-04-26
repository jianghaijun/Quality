package com.sx.quality.activity;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sx.quality.adapter.AppInfoAdapter;
import com.sx.quality.bean.AppInfoBean;
import com.sx.quality.loader.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends BaseActivity {
    private AppHold hold;
    private Context mContext;
    private AppInfoAdapter appInfoAdapter;

    public AppActivity(Context mContext, View layoutApp) {
        this.mContext = mContext;
        hold = new AppHold();
        x.view().inject(hold, layoutApp);
    }

    public void setDate(List<String> objList) {
        //设置banner样式
        hold.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        hold.banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        hold.banner.setImages(objList);
        //设置banner动画效果
        hold.banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        hold.banner.isAutoPlay(true);
        //设置轮播时间
        hold.banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        hold.banner.setIndicatorGravity(BannerConfig.RIGHT);
        hold.banner.start();

        // 添加图片、标题
        List<AppInfoBean> appInfoList = new ArrayList<>();
        AppInfoBean bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.bim_platform);
        bean.setTitle(mContext.getString(R.string.bim_platform));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.mail_list);
        bean.setTitle(mContext.getString(R.string.mail_list));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.qr_code);
        bean.setTitle(mContext.getString(R.string.qr_code));
        appInfoList.add(bean);
        bean = new AppInfoBean();
        bean.setImgUrl(R.drawable.qr_code_generate);
        bean.setTitle(mContext.getString(R.string.qr_code_generate));
        appInfoList.add(bean);
        bean = new AppInfoBean();
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
        bean.setImgUrl(R.drawable.experimental_management);
        bean.setTitle(mContext.getString(R.string.experimental_management));
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
    public void startBanner(){
        //开始轮播
        if (hold != null && hold.banner != null) {
            hold.banner.startAutoPlay();
        }
    }

    /**
     * 停止轮播
     */
    public void stopBanner(){
        if (hold != null && hold.banner != null) {
            hold.banner.stopAutoPlay();
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
    }
}
