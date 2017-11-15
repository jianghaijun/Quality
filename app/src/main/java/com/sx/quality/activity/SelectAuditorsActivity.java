package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.adapter.SelectAuditorsAdapter;
import com.sx.quality.bean.PictureBean;
import com.sx.quality.bean.SelectAuditorsBean;
import com.sx.quality.model.ContractorListModel;
import com.sx.quality.model.PictureModel;
import com.sx.quality.model.SelectAuditorsModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
 *       Created by HaiJun on 2017/11/9 16:47
 *       审核人员选择
 */
public class SelectAuditorsActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.txtRight)
    private Button btnRight;

    @ViewInject(R.id.lvAuditors)
    private ListView lvAuditors;

    private Context mContext;
    private SelectAuditorsAdapter adapter;
    private List<SelectAuditorsBean> selectAuditorsData;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_auditors);
        x.view().inject(this);

        mContext = this;

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.auditors));
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setText(getString(R.string.reported));

        lvAuditors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isSelect = selectAuditorsData.get(position).isSelect();
                if (!isSelect) {
                    for (SelectAuditorsBean bean : selectAuditorsData) {
                        bean.setSelect(false);
                    }
                }

                if (isSelect) {
                    userId = "";
                } else {
                    userId = selectAuditorsData.get(position).getUserId();
                }
                selectAuditorsData.get(position).setSelect(!isSelect);

                adapter.notifyDataSetChanged();
            }
        });

        initData();
    }

    /**
     * 获取数据
     */
    private void initData() {
        LoadingUtils.showLoading(mContext);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("roleFlag", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_AUDITORS)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LoadingUtils.hideLoading();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLong(mContext, getString(R.string.server_exception));
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Gson gson = new Gson();
            String jsonData = response.body().string().toString();
            jsonData = null == jsonData || jsonData.equals("null") || jsonData.equals("") ? "{}" : jsonData;
            final SelectAuditorsModel model = gson.fromJson(jsonData, SelectAuditorsModel.class);

            if (model.isSuccess()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        selectAuditorsData = model.getData();
                        setData();
                        LoadingUtils.hideLoading();
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, getString(R.string.get_data_exception));
                    }
                });
            }
        }
    };

    /**
     * 设置数据
     */
    private void setData() {
        adapter = new SelectAuditorsAdapter(mContext, selectAuditorsData);
        lvAuditors.setAdapter(adapter);
    }

    @Event({R.id.imgBtnLeft, R.id.txtRight})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.txtRight:
                if (TextUtils.isEmpty(userId)) {
                    ToastUtil.showShort(mContext, "请选择审核人！");
                } else {
                    submitReported(userId);
                }
                break;
        }
    }

    /**
     * 上报
     */
    private void submitReported(String userId) {
        LoadingUtils.showLoading(mContext);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        /*JSONObject json = new JSONObject();
        try {
            json.put("sxZlPictureList", getIntent().getStringExtra("sxZlPictureList"));
            json.put("selectUserId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        PictureModel model = new PictureModel();
        model.setSelectUserId(userId);
        model.setSxZlPictureList(ContractorDetailsActivity.beanList);

        Gson gson = new Gson();
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.d(gson.toJson(model));
        Logger.clearLogAdapters();

        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(model).toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.SUBMIT_AUDITORS_PICTURE)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(mContext, "上报失败!");
                        LoadingUtils.hideLoading();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showShort(mContext, "上报成功!");
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContractorDetailsActivity.beanList.clear();
    }
}
