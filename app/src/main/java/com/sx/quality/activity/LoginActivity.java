package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.sx.quality.bean.UserInfo;
import com.sx.quality.model.AliasModel;
import com.sx.quality.model.LoginModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
 *       Created by dell on 2017/10/20 14:13
 */
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.edtUserName)
    private EditText edtUserName;
    @ViewInject(R.id.edtUserPassWord)
    private EditText edtUserPassWord;
    private Context mContext;
    @ViewInject(R.id.imgLogo)
    private ImageView imgLogo;

    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        x.view().inject(this);
        mContext = this;

        ScreenManagerUtil.pushActivity(this);

        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(this).load(R.mipmap.logo).apply(options).into(imgLogo);

        String userName = (String) SpUtil.get(this, "user", "");
        edtUserName.setText(userName);
    }

    @Event(R.id.btnLogin)
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (TextUtils.isEmpty(edtUserName.getText().toString().trim())) {
                    ToastUtil.showShort(this, getString(R.string.please_input_user_name));
                } else if (TextUtils.isEmpty(edtUserPassWord.getText().toString().trim())) {
                    ToastUtil.showShort(this, getString(R.string.please_input_user_password));
                } else {
                    if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                        if (!isLogin) {
                            isLogin = true;
                            Login ();
                        }
                    } else {
                        ToastUtil.showShort(this, getString(R.string.not_network));
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void Login () {
        LoadingUtils.showLoading(mContext);
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        try {
            object.put("userId", edtUserName.getText().toString().trim());
            object.put("userPwd", edtUserPassWord.getText().toString().trim());
            object.put("accountId", ConstantsUtil.ACCOUNT_ID);
            object.put("loginType", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, object.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.LOGIN)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isLogin = false;
                        LoadingUtils.hideLoading();
                        ToastUtil.showShort(mContext, getString(R.string.server_exception));
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        if (resultFlag) {
                            final LoginModel loginModel = gson.fromJson(jsonData, LoginModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    UserInfo userInfo = loginModel.getData().getUserInfo();
                                    SpUtil.put(mContext, "UserName", userInfo.getRealName());
                                    SpUtil.put(mContext, "user", edtUserName.getText().toString().trim());
                                    SpUtil.put(mContext, ConstantsUtil.USER_ID, userInfo.getUserId());
                                    SpUtil.put(mContext, ConstantsUtil.TOKEN, loginModel.getData().getToken());
                                    // 设置极光别名
                                    int sequence = (int) System.currentTimeMillis();
                                    JPushInterface.setAlias(mContext, sequence, userInfo.getUserId());
                                    LoginSuccessful();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isLogin = false;
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, msg);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLogin = false;
                            LoadingUtils.hideLoading();
                            ToastUtil.showShort(mContext, getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 登录成功--->上传极光别名
     */
    private void LoginSuccessful () {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
            object.put("alias", SpUtil.get(mContext, ConstantsUtil.USER_ID, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, object.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.SUBMIT_ALIAS)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isLogin = false;
                        LoadingUtils.hideLoading();
                        ToastUtil.showShort(mContext, "别名上传失败！");
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
                        if (resultFlag) {
                            Gson gson = new Gson();
                            final AliasModel aliasModel = gson.fromJson(jsonData, AliasModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    isLogin = false;
                                    SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, true);
                                    SpUtil.put(mContext, ConstantsUtil.USER_LEVEL, aliasModel.getData().getUserType());
                                    startActivity(new Intent(mContext, V_2MainActivity.class));
                                    LoginActivity.this.finish();
                                    edtUserPassWord.setText("");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    isLogin = false;
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, msg);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLogin = false;
                            LoadingUtils.hideLoading();
                            ToastUtil.showShort(mContext, getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
