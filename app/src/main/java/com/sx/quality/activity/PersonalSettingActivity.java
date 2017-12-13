package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.dialog.DownloadApkDialog;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.model.CheckVersionModel;
import com.sx.quality.utils.AppInfoUtil;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.GlideCatchUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    @ViewInject(R.id.txtUserName)
    private TextView txtUserName;
    @ViewInject(R.id.txtCachingSize)
    private TextView txtCachingSize;

    private Context mContext;
    private Long fileLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_setting);
        x.view().inject(this);

        mContext = this;
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.person_setting));

        txtVersion.setText("当前版本" + AppInfoUtil.getVersion(this));
        txtUserName.setText((String) SpUtil.get(this, "UserName", ""));
        txtCachingSize.setText(GlideCatchUtil.getCacheSize());
    }

    @Event({R.id.btnSignOut, R.id.imgBtnLeft, R.id.imgViewUpdatePassword, R.id.imgViewCheckVersion, R.id.imgViewCleanUpCaching })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnSignOut:
                SpUtil.put(this, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                ScreenManagerUtil.popAllActivityExceptOne();
                //ScreenManagerUtil.popAllActivityExceptOne(LoginActivity.class);
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
            case R.id.imgViewUpdatePassword:
                startActivity(new Intent(this, UpdatePassWordActivity.class));
                break;
            case R.id.imgViewCheckVersion:
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    checkVersion();
                } else {
                    ToastUtil.showShort(this, getString(R.string.not_network));
                }
                break;
            case R.id.imgViewCleanUpCaching:
                // 清除已加载工序列表
                SpUtil.put(mContext, ConstantsUtil.NODE_ID, "");
                GlideCatchUtil.cleanCatchDisk();
                txtCachingSize.setText(GlideCatchUtil.getCacheSize());
                break;
        }
    }

    /**
     * 版本检查
     */
    private void checkVersion() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.CHECK_VERSION)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();

                if (JsonUtils.isGoodJson(data)) {
                    Gson gson = new Gson();
                    final CheckVersionModel model = gson.fromJson(data, CheckVersionModel.class);
                    if (model.isSuccess()) {
                        float serverVersion = Float.valueOf(model.getVersion());
                        if (serverVersion > Float.valueOf(AppInfoUtil.getVersion(mContext))) {
                            // 发现新版本
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fileLength = model.getFileLength();
                                    PromptDialog promptDialog = new PromptDialog(mContext, choiceListener, "发现新版本", "是否更新？", "否", "是");
                                    promptDialog.show();
                                }
                            });
                        } else {
                            // 当前为最新版本
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txtVersion.setText("当前已是最新版本！");
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(mContext, getString(R.string.get_data_exception));
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort(mContext, getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 版本更新监听
     */
    private ChoiceListener choiceListener = new ChoiceListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            if (trueOrFalse) {
                DownloadApkDialog downloadApkDialog = new DownloadApkDialog(mContext, fileLength);
                downloadApkDialog.setCanceledOnTouchOutside(false);
                downloadApkDialog.show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
