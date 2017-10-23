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
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.model.LoginModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
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

        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(this).load(R.drawable.add).apply(options).into(imgLogo);
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
                    if (!isLogin) {
                        isLogin = true;
                        Login ();
                    }
                }
                break;
        }
    }

    /**
     * 登录
     */
    private void Login () {
        LoadingUtils.showLoading(mContext);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        try {
            object.put("name", edtUserName.getText().toString().trim());
            object.put("password", edtUserPassWord.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, object.toString());
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

                final LoginModel loginModel = gson.fromJson(jsonData, LoginModel.class);
                if (loginModel.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SpUtil.put(mContext, "UserName", loginModel.getData().getRealName());
                            LoadingUtils.hideLoading();
                            LoginSuccessful();
                            isLogin = false;
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isLogin = false;
                            LoadingUtils.hideLoading();
                            ToastUtil.showShort(mContext, getString(R.string.login_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 登录成功
     */
    private void LoginSuccessful () {
        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, true);
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}
