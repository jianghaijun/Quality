package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.listener.ChoiceListener;

import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 程序主界面
 * Created by jack on 2017/10/10.
 */
public class MainActivity extends BaseActivity {
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.imgBtnRight)
    private ImageButton imgBtnRight;

    @ViewInject(R.id.txtSubmitPhoneNum)
    private TextView txtSubmitPhoneNum;

    private Context mContext;
    /**
     * 退出监听
     */
    private ChoiceListener choiceListener = new ChoiceListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                MainActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        mContext = this;

        txtTitle.setText(R.string.app_title);
        imgBtnRight.setVisibility(View.VISIBLE);
        imgBtnRight.setImageDrawable(getResources().getDrawable(R.drawable.sign_out_btn));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ContractorListPhotosBean> upLoadPhotosBeanList = DataSupport.where("isToBeUpLoad = 1").find(ContractorListPhotosBean.class);
        txtSubmitPhoneNum.setText(upLoadPhotosBeanList.size() + "");
    }

    @Event({R.id.imgBtnPersonalSetting, R.id.imgBtnContractor, R.id.imgBtnOwner, R.id.imgBtnSupervision, R.id.imgBtnToUploadPhotos, R.id.imgBtnRight})
    private void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            // 个人设置
            case R.id.imgBtnPersonalSetting:
                break;
            // 承包商
            case R.id.imgBtnContractor:
                intent.setClass(mContext, ContractorActivity.class);
                startActivity(intent);
                break;
            // 业主
            case R.id.imgBtnOwner:
                break;
            // 监理
            case R.id.imgBtnSupervision:
                break;
            // 待上传照片
            case R.id.imgBtnToUploadPhotos:
                intent.setClass(mContext, UpLoadPhotosActivity.class);
                startActivity(intent);
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
            signOut();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
