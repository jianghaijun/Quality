package com.sx.quality.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.sx.quality.activity.R;
import com.sx.quality.adapter.SelectAuditorsAdapter;
import com.sx.quality.bean.SelectAuditorsBean;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.ReportListener;
import com.sx.quality.model.SelectAuditorsModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

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
            Gson gson = new Gson();
            String jsonData = response.body().string().toString();
            if (JsonUtils.isGoodJson(jsonData)) {
                jsonData = null == jsonData || jsonData.equals("null") || jsonData.equals("") ? "{}" : jsonData;
                final SelectAuditorsModel model = gson.fromJson(jsonData, SelectAuditorsModel.class);

                if (model.isSuccess()) {
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
                            ToastUtil.showLong(mContext, mContext.getString(R.string.get_data_exception));
                        }
                    });
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
