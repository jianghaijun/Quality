package com.sx.quality.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.quality.utils.AppInfoUtil;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 			   佛祖保佑       永无BUG
 *       Created by dell on 2017/10/23 8:58
 */
public class PersonalSettingActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;

    @ViewInject(R.id.txtVersion)
    private TextView txtVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_setting);
        x.view().inject(this);

        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.person_setting));

        txtVersion.setText("当前版本" + AppInfoUtil.getVersion(this));
    }

    @Event({R.id.btnSignOut, R.id.imgBtnLeft, R.id.imgViewUpdatePassword, R.id.imgViewCheckVersion })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnSignOut:
                SpUtil.put(this, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                ScreenManagerUtil.popAllActivityExceptOne(LoginActivity.class);
                break;
            case R.id.imgViewUpdatePassword:
                startActivity(new Intent(this, UpdatePassWordActivity.class));
                break;
            case R.id.imgViewCheckVersion:
                checkVersion();
                break;
        }
    }

    /**
     * 版本检查
     */
    private void checkVersion() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
