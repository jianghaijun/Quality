package com.sx.quality.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.sx.quality.dialog.DownloadApkDialog;
import com.sx.quality.listener.ChoiceListener;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class V_2MainActivity extends BaseActivity {
    @ViewInject(R.id.vpMain)
    private ViewPager vpMain;
    @ViewInject(R.id.btnMsg)
    private Button btnMsg;
    @ViewInject(R.id.btnApplication)
    private Button btnApplication;
    @ViewInject(R.id.btnProject)
    private Button btnProject;
    @ViewInject(R.id.btnFriends)
    private Button btnFriends;
    @ViewInject(R.id.btnMe)
    private Button btnMe;

    private Context mContext;

    // 子布局
    private View layoutMsg, layoutApp, layoutProject, layoutFriends, layoutMe;
    private AppActivity appActivity;
    private MySettingActivity mySettingActivity;
    // View列表
    private ArrayList<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_2_main);
        x.view().inject(this);

        mContext = this;

        //将要分页显示的View装入数组中
        LayoutInflater viewLI = LayoutInflater.from(this);
        layoutMsg = viewLI.inflate(R.layout.layout_empty, null);
        layoutApp = viewLI.inflate(R.layout.activity_app, null);
        layoutProject = viewLI.inflate(R.layout.layout_empty, null);
        layoutFriends = viewLI.inflate(R.layout.layout_empty, null);
        layoutMe = viewLI.inflate(R.layout.activity_my_setting, null);

        // 应用
        appActivity = new AppActivity(mContext, layoutApp);
        // 我的
        mySettingActivity = new MySettingActivity(mContext, layoutMe, choiceListener);

        List<String> objList = new ArrayList<>();
        objList.add("http://img8.zol.com.cn/bbs/upload/21223/21222249.jpg");
        objList.add("http://img8.zol.com.cn/bbs/upload/21223/21222247.jpg");
        objList.add("http://img8.zol.com.cn/bbs/upload/21223/21222236.jpg");

        //每个页面的view数据
        views = new ArrayList<>();
        views.add(layoutMsg);
        views.add(layoutProject);
        views.add(layoutApp);
        views.add(layoutFriends);
        views.add(layoutMe);

        vpMain.setOnPageChangeListener(new MyOnPageChangeListener());
        vpMain.setAdapter(mPagerAdapter);
        vpMain.setCurrentItem(2);

        appActivity.setDate(objList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        appActivity.startBanner();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        appActivity.stopBanner();
    }

    /**
     * 填充ViewPager的数据适配器
     */
    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    };

    /**
     * 页卡切换监听
     */
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            setStates(arg0);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    /**
     * 设置背景
     * @param option
     */
    private void setStates(int option) {
        Drawable top = ContextCompat.getDrawable(mContext, R.drawable.msg_un_select);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        btnMsg.setCompoundDrawables(null, top, null, null);
        btnMsg.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));

        top = ContextCompat.getDrawable(mContext, R.drawable.application_un_select);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        btnApplication.setCompoundDrawables(null, top, null, null);
        btnApplication.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));

        top = ContextCompat.getDrawable(mContext, R.drawable.project_un_select);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        btnProject.setCompoundDrawables(null, top, null, null);
        btnProject.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));

        top = ContextCompat.getDrawable(mContext, R.drawable.friend_un_select);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        btnFriends.setCompoundDrawables(null, top, null, null);
        btnFriends.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));

        top = ContextCompat.getDrawable(mContext, R.drawable.me_un_select);
        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
        btnMe.setCompoundDrawables(null, top, null, null);
        btnMe.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_color));

        switch (option) {
            case 0:
                top = ContextCompat.getDrawable(mContext, R.drawable.msg_select);
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                btnMsg.setCompoundDrawables(null, top, null, null);
                btnMsg.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_select_color));
                break;
            case 1:
                top = ContextCompat.getDrawable(mContext, R.drawable.project_select);
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                btnProject.setCompoundDrawables(null, top, null, null);
                btnProject.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_select_color));
                break;
            case 2:
                top = ContextCompat.getDrawable(mContext, R.drawable.application_select);
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                btnApplication.setCompoundDrawables(null, top, null, null);
                btnApplication.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_select_color));
                break;
            case 3:
                top = ContextCompat.getDrawable(mContext, R.drawable.friend_select);
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                btnFriends.setCompoundDrawables(null, top, null, null);
                btnFriends.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_select_color));
                break;
            case 4:
                top = ContextCompat.getDrawable(mContext, R.drawable.me_select);
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                btnMe.setCompoundDrawables(null, top, null, null);
                btnMe.setTextColor(ContextCompat.getColor(mContext, R.color.v_2_tab_select_color));
                break;
        }
    }

    /**
     * 版本检查权限申请
     */
    private ChoiceListener choiceListener = new ChoiceListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                getPermissions();
            }
        }
    };

    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            // 读写权限
            addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            addPermission(permissions, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 127);
            } else {
                mySettingActivity.downloadApk();
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }
        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mySettingActivity.downloadApk();
    }

    /**
     * 点击事件
     * @param v
     */
    @Event({ R.id.btnMsg, R.id.btnProject, R.id.btnApplication, R.id.btnFriends, R.id.btnMe })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMsg:
                vpMain.setCurrentItem(0);
                break;
            case R.id.btnProject:
                vpMain.setCurrentItem(1);
                break;
            case R.id.btnApplication:
                vpMain.setCurrentItem(2);
                break;
            case R.id.btnFriends:
                vpMain.setCurrentItem(3);
                break;
            case R.id.btnMe:
                vpMain.setCurrentItem(4);
                break;
            default:
                break;
        }
    }

}
