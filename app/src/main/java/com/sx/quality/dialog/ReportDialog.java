package com.sx.quality.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.R;
import com.sx.quality.adapter.SelectAuditorsAdapter;
import com.sx.quality.bean.SelectAuditorsBean;
import com.sx.quality.listener.ReportListener;
import com.sx.quality.model.SelectAuditorsModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create dell By 2017/12/8 17:49
 */

public class ReportDialog extends Dialog {
    private Activity mContext;
    private ReportListener reportListener;
    private Button btnLeft;
    private Button btnRight;
    private ListView lvAuditors;

    private String userId;

    private SelectAuditorsAdapter adapter;
    private List<SelectAuditorsBean> selectAuditorsData;

    public ReportDialog(Context context, ReportListener reportListener) {
        super(context);
        this.mContext = (Activity) context;
        this.reportListener = reportListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report);

        btnLeft = (Button) findViewById(R.id.btnQuery);
        btnRight = (Button) findViewById(R.id.btnCancel);
        lvAuditors = (ListView) findViewById(R.id.lvAuditors);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userId)) {
                    ToastUtil.showShort(mContext, "请选择审核人！");
                } else {
                    dismiss();
                    reportListener.returnUserId(userId);
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                reportListener.returnUserId("");
            }
        });

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
        JSONObject json = new JSONObject();
        try {
            json.put("roleFlag", ConstantsUtil.roleFlag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, json.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_AUDITORS)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            LoadingUtils.hideLoading();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ReportDialog.this.dismiss();
                    ToastUtil.showLong(mContext, mContext.getString(R.string.server_exception));
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
                    final String code = obj.getString("code");

                    if (resultFlag) {
                        Gson gson = new Gson();
                        final SelectAuditorsModel model = gson.fromJson(jsonData, SelectAuditorsModel.class);
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                selectAuditorsData = model.getData();
                                setData();
                                LoadingUtils.hideLoading();
                            }
                        });
                    } else {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LoadingUtils.hideLoading();
                                switch (code) {
                                    case "3003":
                                    case "3004":
                                        // Token异常重新登录
                                        ToastUtil.showLong(mContext, msg);
                                        SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                        ScreenManagerUtil.popAllActivityExceptOne();
                                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                        break;
                                    default:
                                        ToastUtil.showLong(mContext, msg);
                                        break;
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
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
}
