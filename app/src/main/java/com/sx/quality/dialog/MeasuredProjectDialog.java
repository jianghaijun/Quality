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
import android.widget.TextView;

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

public class MeasuredProjectDialog extends Dialog {
    private Activity mContext;
    private ReportListener reportListener;
    private TextView txtTitle;
    private Button btnLeft;
    private Button btnRight;
    private ListView lvAuditors;

    private SelectAuditorsAdapter adapter;
    private List<SelectAuditorsBean> selectAuditorsData;

    private String projectId;

    public MeasuredProjectDialog(Context context, ReportListener reportListener) {
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
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        lvAuditors = (ListView) findViewById(R.id.lvAuditors);

        txtTitle.setText("选择检测项目");

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(projectId)) {
                    ToastUtil.showShort(mContext, "请选择需要检测的项目！");
                } else {
                    dismiss();
                    reportListener.returnUserId(projectId);
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        lvAuditors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (SelectAuditorsBean bean : selectAuditorsData) {
                    bean.setSelect(false);
                }

                selectAuditorsData.get(position).setSelect(true);
                projectId = selectAuditorsData.get(position).getCheckLevelId();

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
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, "");
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_CHECK_LEVEL_LIST)
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
                    MeasuredProjectDialog.this.dismiss();
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
                                for (SelectAuditorsBean bean: selectAuditorsData) {
                                    bean.setRealName(bean.getCheckLevelName());
                                }
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
                                        ToastUtil.showLong(mContext, "Token过期请重新登录！");
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
                    LoadingUtils.hideLoading();
                    ToastUtil.showLong(mContext, mContext.getString(R.string.data_error));
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
