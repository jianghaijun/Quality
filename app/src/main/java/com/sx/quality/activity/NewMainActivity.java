package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 程序主界面
 */
public class NewMainActivity extends BaseActivity {
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;

    @ViewInject(R.id.rlSecurity)
    private RelativeLayout rlSecurity;

    @ViewInject(R.id.rlQuality)
    private RelativeLayout rlQuality;


    @ViewInject(R.id.txtSubmitPhotosNum)
    private TextView txtSubmitPhotosNum;
    /*@ViewInject(R.id.txtUserDepartment)
    private TextView txtUserDepartment;*/
    /*@ViewInject(R.id.txtRealName)
    private TextView txtRealName;*/
    /*@ViewInject(R.id.rlDepartment)
    private RelativeLayout rlDepartment;*/

    private Context mContext;
    /**
     * 退出监听
     */
    private ChoiceListener choiceListener = new ChoiceListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                NewMainActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        x.view().inject(this);
        mContext = this;

        ScreenManagerUtil.pushActivity(this);

        txtTitle.setText(R.string.app_title);
        imgBtnRight.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.sign_out));
        // 用户部门
        /*String userDepartment = (String) SpUtil.get(mContext, ConstantsUtil.USER_DEPARTMENT, "");
        if (!TextUtils.isEmpty(userDepartment)) {
            rlDepartment.setVisibility(View.VISIBLE);
            txtUserDepartment.setText(userDepartment);
        }*/
        //txtRealName.setText((String) SpUtil.get(mContext, "UserName", ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 查询待上传图片个数
        List<ContractorListPhotosBean> upLoadPhotosBeanList = DataSupport.where("isToBeUpLoad = 1 AND userId = ?", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, "")).find(ContractorListPhotosBean.class);
        txtSubmitPhotosNum.setText(upLoadPhotosBeanList == null ? "" : (upLoadPhotosBeanList.size() + ""));
    }

    @Event({R.id.imgBtnPersonalSetting, R.id.imgBtnToUploadPhotos, R.id.imgBtnSecurity, R.id.imgBtnQuality, R.id.imgBtnUseHelp, R.id.imgBtnRight})
    private void onClick(View view) {
        String userLevel = (String) SpUtil.get(mContext, ConstantsUtil.USER_LEVEL, "-1");
        Intent intent = new Intent();
        switch (view.getId()) {
            // 个人设置
            case R.id.imgBtnPersonalSetting:
                intent.setClass(mContext, PersonalSettingActivity.class);
                startActivity(intent);
                break;
            // 待上传照片
            case R.id.imgBtnToUploadPhotos:
                intent.setClass(mContext, UpLoadPhotosActivity.class);
                startActivity(intent);
                break;
            // 安全
            case R.id.imgBtnSecurity:
                if (TextUtils.isEmpty(userLevel) || userLevel.equals("1")) {
                    // 可查看安全
                    SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "1");
                    intent.setClass(mContext, ContractorActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(mContext, "您没有权限查看该模块！");
                }
                break;
            // 质量
            case R.id.imgBtnQuality:
                if (TextUtils.isEmpty(userLevel) || userLevel.equals("0")) {
                    // 可查看质量
                    SpUtil.put(mContext, ConstantsUtil.USER_TYPE, "0");
                    intent.setClass(mContext, ContractorActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort(mContext, "您没有权限查看该模块！");
                }
                break;
            // 使用帮助
            case R.id.imgBtnUseHelp:
                break;
            // 退出
            case R.id.imgBtnRight:
                signOut();
                break;
        }
    }

    /**
     * 退出
     */
    private void signOut() {
        PromptDialog promptDialog = new PromptDialog(this, choiceListener, "退出应用", "是否退出应用?", "取消", "确定");
        promptDialog.setCanceledOnTouchOutside(false);
        promptDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //signOut();
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
