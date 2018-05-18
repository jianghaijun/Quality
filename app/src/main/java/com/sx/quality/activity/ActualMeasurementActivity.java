package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.sx.quality.bean.MeasuredRecordBean;
import com.sx.quality.model.ProcessActualModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DataUtils;
import com.sx.quality.utils.JsonUtils;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ScreenManagerUtil;
import com.sx.quality.utils.SpUtil;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActualMeasurementActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    // 头部信息
    @ViewInject(R.id.txtWorkingName)
    private TextView txtWorkingName;
    @ViewInject(R.id.txtWorkingPosition)
    private TextView txtWorkingPosition;
    @ViewInject(R.id.txtConstructionDate)
    private TextView txtConstructionDate;
    @ViewInject(R.id.txtCheckDate)
    private TextView txtCheckDate;
    @ViewInject(R.id.workingName)
    private TextView workingName;
    @ViewInject(R.id.workingPosition)
    private TextView workingPosition;
    // 表格信息
    @ViewInject(R.id.tabLayStandard)
    private TableLayout tabLayStandard;
    
    private Context mContext;
    private String processId, projectId, rootNodeName, processName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_measurement);
        x.view().inject(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(R.string.app_name);

        mContext = this;
        ScreenManagerUtil.pushActivity(this);
        processId = getIntent().getStringExtra("processId");
        projectId = getIntent().getStringExtra("projectId");
        rootNodeName = getIntent().getStringExtra("rootNodeName");
        processName = getIntent().getStringExtra("processName");

        String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
        if (type.equals("1")) {
            workingName.setText("隐患内容");
            workingPosition.setText("隐患部位");
        }

        // 没有网络并且没有加载过
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            ToastUtil.showShort(mContext, getString(R.string.not_network));
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        LoadingUtils.showLoading(mContext);
        JSONObject obj = new JSONObject();
        try {
            obj.put("processId", processId);
            obj.put("checkLevelId", projectId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_PROCESS_ACTUAL_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
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
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            Gson gson = new Gson();
                            final ProcessActualModel model = gson.fromJson(jsonData, ProcessActualModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 查询本地保存的照片
                                    setData(model.getData());
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
                                            ToastUtil.showLong(mContext, "Token过期请重新登录");
                                            SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                            ScreenManagerUtil.popAllActivityExceptOne();
                                            startActivity(new Intent(mContext, LoginActivity.class));
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 保存数据
     */
    private void saveData (List<MeasuredRecordBean> measuredList) {
        LoadingUtils.showLoading(mContext);
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(measuredList));
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.SAVE_PROCESS_ACTUAL)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
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
                String jsonData = response.body().string().toString();
                if (JsonUtils.isGoodJson(jsonData)) {
                    try {
                        JSONObject obj = new JSONObject(jsonData);
                        boolean resultFlag = obj.getBoolean("success");
                        final String msg = obj.getString("message");
                        final String code = obj.getString("code");
                        if (resultFlag) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    ToastUtil.showShort(mContext, msg);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
                                            ToastUtil.showLong(mContext, "Token过期请重新登录");
                                            SpUtil.put(mContext, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                            ScreenManagerUtil.popAllActivityExceptOne();
                                            startActivity(new Intent(mContext, LoginActivity.class));
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mContext, mContext.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 赋值
     * @param measuredList
     */
    private void setData(List<MeasuredRecordBean> measuredList) {
        txtWorkingName.setText(processName);
        txtWorkingPosition.setText(rootNodeName);

        if (measuredList != null && measuredList.size() > 0) {
            if (measuredList.get(0).getConstructionTime() > 0) {
                txtConstructionDate.setText(DateUtil.format(new Date(measuredList.get(0).getConstructionTime()), "yyyy-MM-dd HH:mm"));
            }
            if (measuredList.get(0).getCheckTime() > 0) {
                txtCheckDate.setText(DateUtil.format(new Date(measuredList.get(0).getCheckTime()), "yyyy-MM-dd HH:mm"));
            }
        }

        if (measuredList == null) {
            measuredList = new ArrayList<>();
        }
        int width = (int) SpUtil.get(mContext, ConstantsUtil.SCREEN_HEIGHT, DensityUtil.getScreenWidth());
        width = (width - (DensityUtil.dip2px(51 * 3 + 101 + 4 + 8))) / 10;
        for (int n = 0; n < measuredList.size(); n++) {
            MeasuredRecordBean measured = measuredList.get(n);

            TableRow row = new TableRow(mContext);
            // 编号
            TextView txtNo = new TextView(mContext);
            txtNo.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtNo.setText(n + 1 + "");
            txtNo.setTextSize(14);
            txtNo.setGravity(Gravity.CENTER);
            txtNo.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(DensityUtil.dip2px(50), DensityUtil.dip2px(30));
            if (n == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            row.addView(txtNo, lp);

            // 检测项目
            TextView txtTextProject = new TextView(mContext);
            txtTextProject.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtTextProject.setText(measured.getCheckProjectName());
            txtTextProject.setTextSize(14);
            txtTextProject.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(100), DensityUtil.dip2px(30));
            if (n == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtTextProject.setPadding(DensityUtil.dip2px(5), 0, 0, 0);
            txtTextProject.setSingleLine(false);
            txtTextProject.setHorizontallyScrolling(true);
            txtTextProject.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtTextProject.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(txtTextProject, lp);

            // 规定值
            TextView txtStandardNum = new TextView(mContext);
            txtStandardNum.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtStandardNum.setText(measured.getStandardNum());
            txtStandardNum.setTextSize(14);
            txtStandardNum.setGravity(Gravity.CENTER);
            txtStandardNum.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(50), DensityUtil.dip2px(30));
            if (n == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtStandardNum.setMaxLines(1);
            txtStandardNum.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            txtStandardNum.setGravity(Gravity.CENTER);
            row.addView(txtStandardNum, lp);

            // 允许偏差
            TextView txtDeviation = new TextView(mContext);
            txtDeviation.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            txtDeviation.setText(measured.getDeviation());
            txtDeviation.setTextSize(14);
            txtDeviation.setGravity(Gravity.CENTER);
            txtDeviation.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(50), DensityUtil.dip2px(30));
            if (n == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtDeviation.setMaxLines(1);
            txtDeviation.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            txtDeviation.setGravity(Gravity.CENTER);
            row.addView(txtDeviation, lp);

            List<String> sList = new ArrayList<>();
            sList.add(measured.getResult1());
            sList.add(measured.getResult2());
            sList.add(measured.getResult3());
            sList.add(measured.getResult4());
            sList.add(measured.getResult5());
            sList.add(measured.getResult6());
            sList.add(measured.getResult7());
            sList.add(measured.getResult8());
            sList.add(measured.getResult9());
            sList.add(measured.getResult10());

            for (int i = 0; i < sList.size(); i++) {
                // 检测结果
                EditText txtResult = new EditText(mContext);
                txtResult.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.white));
                txtResult.setPadding(0, 0, 0, 0);
                txtResult.setText(sList.get(i));
                txtResult.setTextSize(14);
                txtResult.setGravity(Gravity.CENTER);
                txtResult.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                lp = new TableRow.LayoutParams(width, DensityUtil.dip2px(30));
                if (i == sList.size() - 1) {
                    if (n == 0) {
                        lp.setMargins(0, 0, 0, 0);
                    } else {
                        lp.setMargins(0, DensityUtil.dip2px(1), 0, 0);
                    }
                } else {
                    if (n == 0) {
                        lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
                    } else {
                        lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
                    }
                }
                row.addView(txtResult, lp);
            }

            // 实测Id
            TextView id = new TextView(mContext);
            id.setText(measured.getActualId());
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(0), DensityUtil.dip2px(30));
            id.setVisibility(View.GONE);
            row.addView(id, lp);

            tabLayStandard.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    @Event({ R.id.imgBtnLeft, R.id.btnSaveInfo, R.id.txtConstructionDate, R.id.txtCheckDate })
    private void onClick(View v) {
        Calendar sDate = Calendar.getInstance();
        sDate.set(1970, 0, 1);
        Calendar eDate = Calendar.getInstance();
        eDate.set(2030, 0, 1);
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.txtConstructionDate:
                Calendar startDate = Calendar.getInstance();
                if(TextUtils.isEmpty(txtConstructionDate.getText().toString())){
                    Date date = new Date();
                    startDate.set(DateUtil.year(date), DateUtil.month(date), DateUtil.dayOfMonth(date), DateUtil.hour(date, true), DateUtil.minute(date));
                } else {
                    Date date = DateUtil.parse(txtConstructionDate.getText().toString());
                    startDate.set(DateUtil.year(date), DateUtil.month(date), DateUtil.dayOfMonth(date), DateUtil.hour(date, true), DateUtil.minute(date));
                }

                TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        txtConstructionDate.setText(DataUtils.getDataToStr(date));
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                .isCyclic(false) // 是否循环滚动
                .setDate(startDate)
                .setLabel("年", "月", "日", "时", "分", "")
                .setRangDate(sDate, eDate)//起始终止年月日设定
                .isDialog(true) // 是否显示为对话框样式
                .build();
                pvTime.show();
                break;
            case R.id.txtCheckDate:
                Calendar endDate = Calendar.getInstance();
                if(TextUtils.isEmpty(txtCheckDate.getText().toString())){
                    Date date = new Date();
                    endDate.set(DateUtil.year(date), DateUtil.month(date), DateUtil.dayOfMonth(date), DateUtil.hour(date, true), DateUtil.minute(date));
                } else {
                    Date date = DateUtil.parse(txtCheckDate.getText().toString());
                    endDate.set(DateUtil.year(date), DateUtil.month(date), DateUtil.dayOfMonth(date), DateUtil.hour(date, true), DateUtil.minute(date));
                }

                TimePickerView time = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        txtCheckDate.setText(DataUtils.getDataToStr(date));
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})// 默认全部显示
                .isCyclic(false) // 是否循环滚动
                .setDate(endDate) // 如果不设置的话，默认是系统时间
                .setLabel("年", "月", "日", "时", "分", "") //默认设置为年月日时分秒
                .setRangDate(sDate, eDate)//起始终止年月日设定
                .isDialog(true) // 是否显示为对话框样式
                .build();
                time.show();
                break;
            case R.id.btnSaveInfo:
                if (txtConstructionDate.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请选择施工时间！");
                    break;
                } else if (txtCheckDate.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请选择检查时间！");
                    break;
                }

                if (tabLayStandard.getChildCount() > 0) {
                    List<MeasuredRecordBean> measuredList = new ArrayList<>();
                    for (int i = 0; i < tabLayStandard.getChildCount(); i++) {
                        MeasuredRecordBean measured = new MeasuredRecordBean();
                        // 循环遍历表格行
                        TableRow row = (TableRow) tabLayStandard.getChildAt(i);
                        // 获取行相关单元格的数据信息
                        measured.setOrderFlag(((TextView) row.getChildAt(0)).getText().toString());
                        measured.setCheckProjectName(((TextView) row.getChildAt(1)).getText().toString());
                        measured.setStandardNum(((TextView) row.getChildAt(2)).getText().toString());
                        measured.setDeviation(((TextView) row.getChildAt(3)).getText().toString());
                        measured.setResult1(((EditText) row.getChildAt(4)).getText().toString());
                        measured.setResult2(((EditText) row.getChildAt(5)).getText().toString());
                        measured.setResult3(((EditText) row.getChildAt(6)).getText().toString());
                        measured.setResult4(((EditText) row.getChildAt(7)).getText().toString());
                        measured.setResult5(((EditText) row.getChildAt(8)).getText().toString());
                        measured.setResult6(((EditText) row.getChildAt(9)).getText().toString());
                        measured.setResult7(((EditText) row.getChildAt(10)).getText().toString());
                        measured.setResult8(((EditText) row.getChildAt(11)).getText().toString());
                        measured.setResult9(((EditText) row.getChildAt(12)).getText().toString());
                        measured.setResult10(((EditText) row.getChildAt(13)).getText().toString());
                        measured.setActualId(((TextView) row.getChildAt(14)).getText().toString());
                        measured.setProcessId(processId);
                        measured.setProjectName(txtWorkingName.getText().toString());
                        measured.setStakeMark(txtWorkingPosition.getText().toString());
                        measured.setConstructionTimeStr(txtConstructionDate.getText().toString() + ":00");
                        measured.setCheckTimeStr(txtCheckDate.getText().toString() + ":00");
                        measured.setCheckLevelId(projectId);
                        measuredList.add(measured);
                    }
                    saveData(measuredList);
                } else {
                    ToastUtil.showShort(mContext, "暂无可保存的数据！");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenManagerUtil.popActivity(this);
    }
}
