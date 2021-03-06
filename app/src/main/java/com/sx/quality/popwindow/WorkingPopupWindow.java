package com.sx.quality.popwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.activity.LoginActivity;
import com.sx.quality.activity.R;
import com.sx.quality.activity.V_2ContractorDetailsActivity;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.model.WorkingModel;
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
import org.litepal.crud.DataSupport;
import org.xutils.common.util.DensityUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.date.DateUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WorkingPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View mView;
    private String nodeName;
    private String levelId;
    private int width;
    private List<Boolean> isSave = new ArrayList<>();

    public WorkingPopupWindow(Activity mActivity, String nodeName, String levelId) {
        super();
        this.mActivity = mActivity;
        this.levelId = levelId;
        this.nodeName = nodeName;
        this.initPopupWindow();
        if (JudgeNetworkIsAvailable.isNetworkAvailable(mActivity)) {
            this.getData();
        } else {
            List<WorkingBean> workList = DataSupport.where("levelId = ?", levelId).find(WorkingBean.class);
            setDate(workList);
        }
    }

    /**
     * 初始化
     */
    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = inflater.inflate(R.layout.popwindow_working, null);
        this.setContentView(mView);
        width = (int) SpUtil.get(mActivity, ConstantsUtil.SCREEN_HEIGHT, DensityUtil.getScreenWidth());
        this.setWidth((int) (width * 0.8));
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable background = new ColorDrawable(0x4f000000);
        this.setBackgroundDrawable(background);
        this.draw();

        TextView txtName = (TextView) mView.findViewById(R.id.txtName);
        String type = (String) SpUtil.get(mActivity, ConstantsUtil.USER_TYPE, "");
        if (type.equals("1")) {
            txtName.setText("隐患内容");
        }
    }

    /**
     * 获取工序列表
     */
    private void getData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("levelId", levelId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        LoadingUtils.showLoading(mActivity);
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.PROCESS_LIST)
                .addHeader("token", (String) SpUtil.get(mActivity, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showLong(mActivity, mActivity.getString(R.string.server_exception));
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
                            final WorkingModel model = gson.fromJson(jsonData, WorkingModel.class);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDate(model.getData() == null ? new ArrayList<WorkingBean>() : model.getData());
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoadingUtils.hideLoading();
                                    switch (code) {
                                        case "3003":
                                        case "3004":
                                            // Token异常重新登录
                                            ToastUtil.showLong(mActivity, "Token过期请重新登录！");
                                            SpUtil.put(mActivity, ConstantsUtil.IS_LOGIN_SUCCESSFUL, false);
                                            ScreenManagerUtil.popAllActivityExceptOne();
                                            mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                                            break;
                                        default:
                                            ToastUtil.showLong(mActivity, msg);
                                            break;
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        ToastUtil.showLong(mActivity, mActivity.getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingUtils.hideLoading();
                            ToastUtil.showLong(mActivity, mActivity.getString(R.string.json_error));
                        }
                    });
                }
            }
        });
    }

    /**
     * 添加数据
     * @param workList
     */
    private void setDate(List<WorkingBean> workList) {
        // 添加数据
        TextView txtTitle = (TextView) mView.findViewById(R.id.txtTitle);
        txtTitle.setText(nodeName);
        TableLayout tabLay = (TableLayout) mView.findViewById(R.id.tabLay);
        // 动态添加行
        for (int i = 0; i < workList.size(); i++) {
            final int point = i;
            final WorkingBean bean = workList.get(i);
            TableRow tableRow = new TableRow(mActivity);
            // 编号
            TextView txtNo = new TextView(mActivity);
            txtNo.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtNo.setText((i+1) + "");
            txtNo.setTextSize(14);
            txtNo.setGravity(Gravity.CENTER);
            txtNo.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            TableRow.LayoutParams lp = new TableRow.LayoutParams(DensityUtil.dip2px(39), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            tableRow.addView(txtNo, lp);

            // 工序名称
            TextView txtWorkingName = new TextView(mActivity);
            txtWorkingName.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtWorkingName.setText(bean.getProcessName());
            txtWorkingName.setTextSize(14);
            txtWorkingName.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            int widthSize = (int) ((width * 0.8) - DensityUtil.dip2px(10 + 40 + 80*4 + 3));
            lp = new TableRow.LayoutParams(widthSize, DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtWorkingName.setPadding(DensityUtil.dip2px(5), 0, DensityUtil.dip2px(5), 0);
            txtWorkingName.setSingleLine(false);
            txtWorkingName.setHorizontallyScrolling(true);
            txtWorkingName.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtWorkingName.setGravity(Gravity.CENTER_VERTICAL);
            tableRow.addView(txtWorkingName, lp);

            // 照片数量
            TextView txtPhotoNum = new TextView(mActivity);
            txtPhotoNum.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtPhotoNum.setText(bean.getActualNumber());
            txtPhotoNum.setTextSize(14);
            txtPhotoNum.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(79), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtPhotoNum.setGravity(Gravity.CENTER);
            tableRow.addView(txtPhotoNum, lp);

            // 拍照者
            TextView txtPhotographers = new TextView(mActivity);
            txtPhotographers.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtPhotographers.setText(bean.getPhotoerAll());
            txtPhotographers.setTextSize(14);
            txtPhotographers.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(79), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtPhotographers.setPadding(DensityUtil.dip2px(5), 0, 0, 0);
            txtPhotographers.setSingleLine(false);
            txtPhotographers.setHorizontallyScrolling(true);
            txtPhotographers.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtPhotographers.setGravity(Gravity.CENTER_VERTICAL);
            tableRow.addView(txtPhotographers, lp);

            // 确认者
            TextView txtValidator = new TextView(mActivity);
            txtValidator.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            txtValidator.setText(bean.getCheckNameAll());
            txtValidator.setTextSize(14);
            txtValidator.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(79), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, DensityUtil.dip2px(1), 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), DensityUtil.dip2px(1), 0);
            }
            txtValidator.setPadding(DensityUtil.dip2px(5), 0, 0, 0);
            txtValidator.setSingleLine(false);
            txtValidator.setHorizontallyScrolling(true);
            txtValidator.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtValidator.setGravity(Gravity.CENTER_VERTICAL);
            tableRow.addView(txtValidator, lp);

            // 状态
            TextView txtStatus = new TextView(mActivity);
            final String status = bean.getProcessState();
            txtStatus.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
            switch (status) {
                case "0":
                    txtStatus.setText("待拍照");
                    break;
                case "1":
                    txtStatus.setText("已拍照");
                    break;
                case "2":
                    txtStatus.setText("初审中");
                    break;
                case "3":
                    txtStatus.setText("初审驳回");
                    break;
                case "4":
                    txtStatus.setText("自检完成");
                    break;
                case "5":
                    txtStatus.setText("复审驳回");
                    break;
                case "6":
                    txtStatus.setText("终审中");
                    break;
                case "7":
                    txtStatus.setText("终审驳回");
                    break;
                case "8":
                    txtStatus.setText("抽检完成");
                    break;
            }

            txtStatus.setTextSize(14);
            txtStatus.setTextColor(ContextCompat.getColor(mActivity, R.color.v_2_main_check_bg));
            lp = new TableRow.LayoutParams(DensityUtil.dip2px(80), DensityUtil.dip2px(30));
            if (i == 0) {
                lp.setMargins(0, 0, 0, 0);
            } else {
                lp.setMargins(0, DensityUtil.dip2px(1), 0, 0);
            }
            txtStatus.setGravity(Gravity.CENTER);
            txtStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, V_2ContractorDetailsActivity.class);
                    intent.putExtra("status", status);
                    intent.putExtra("nodeName", nodeName + "→" + bean.getProcessName());
                    intent.putExtra("processCode", bean.getProcessCode());
                    intent.putExtra("processName", bean.getProcessName());
                    long dateLong = bean.getEnterTime() > 0 ? bean.getEnterTime() : System.currentTimeMillis();
                    intent.putExtra("enterTime", DataUtils.getDataToStr(DateUtil.date(dateLong)));
                    intent.putExtra("actualNumber", bean.getPhotoNumber());
                    intent.putExtra("photoContent", bean.getPhotoContent());
                    intent.putExtra("distanceAngle", bean.getPhotoDistance());
                    intent.putExtra("processId", bean.getProcessId());
                    intent.putExtra("levelId", bean.getLevelId());
                    intent.putExtra("location", bean.getLocation());
                    intent.putExtra("dismissal", bean.getDismissal());
                    intent.putExtra("userType", (String) SpUtil.get(mActivity, ConstantsUtil.USER_TYPE, ""));
                    intent.putExtra("ext1", bean.getExt1());
                    intent.putExtra("ext2", bean.getExt2());
                    intent.putExtra("ext3", bean.getExt3());
                    intent.putExtra("ext4", bean.getExt4());
                    intent.putExtra("ext5", bean.getExt5());
                    intent.putExtra("ext6", bean.getExt6());
                    intent.putExtra("ext7", bean.getExt7());
                    intent.putExtra("ext8", bean.getExt8());
                    intent.putExtra("ext9", bean.getExt9());
                    intent.putExtra("ext10", bean.getExt10());
                    intent.putExtra("canCheck", bean.getCanCheck());
                    List<WorkingBean> list = DataSupport.where("processId = ?", bean.getProcessId()).find(WorkingBean.class);
                    if (list != null && list.size() > 0) {
                        intent.putExtra("check", isSave.get(point));
                    } else {
                        intent.putExtra("check", true);
                    }

                    mActivity.startActivity(intent);
                    dismiss();
                }
            });
            tableRow.addView(txtStatus, lp);

            tabLay.addView(tableRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            if (nodeName.contains("填方")) {
                // 先判断本地内存有没有该条数据
                List<WorkingBean> list = DataSupport.where("processId = ?", bean.getProcessId()).find(WorkingBean.class);
                if (list != null && list.size() > 0) {
                    // 判断本地层厚字段是否都为空
                    WorkingBean local = list.get(0);
                    if (checkLocal(local)) {
                        isSave.add(true);
                        bean.saveOrUpdate("processId = ?", bean.getProcessId());
                    } else {
                        // 是否有不相等的
                        isSave.add(check(local, bean));
                        if (check(local, bean)) {
                            bean.saveOrUpdate("processId = ?", bean.getProcessId());
                        } else {
                            bean.setExt1(local.getExt1());
                            bean.setExt2(local.getExt2());
                            bean.setExt3(local.getExt3());
                            bean.setExt4(local.getExt4());
                            bean.setExt5(local.getExt5());
                            bean.setExt6(local.getExt6());
                            bean.setExt7(local.getExt7());
                            bean.setExt8(local.getExt8());
                            bean.setExt9(local.getExt9());
                            bean.setExt10(local.getExt10());
                            bean.saveOrUpdate("processId = ?", bean.getProcessId());
                        }
                    }
                } else {
                    isSave.add(true);
                    bean.saveOrUpdate("processId = ?", bean.getProcessId());
                }
            } else {
                isSave.add(true);
                bean.saveOrUpdate("processId = ?", bean.getProcessId());
            }
        }
    }

    private boolean checkLocal(WorkingBean bean) {
        if (bean == null) {
            return true;
        }
        String ext1 = bean.getExt1();
        String ext2 = bean.getExt2();
        String ext3 = bean.getExt3();
        String ext4 = bean.getExt4();
        String ext5 = bean.getExt5();
        String ext6 = bean.getExt6();
        String ext7 = bean.getExt7();
        String ext8 = bean.getExt8();
        String ext9 = bean.getExt9();
        String ext10 = bean.getExt10();

        if (TextUtils.isEmpty(ext1) && TextUtils.isEmpty(ext2) && TextUtils.isEmpty(ext3) && TextUtils.isEmpty(ext4) && TextUtils.isEmpty(ext5) && TextUtils.isEmpty(ext6) && TextUtils.isEmpty(ext7) && TextUtils.isEmpty(ext8) && TextUtils.isEmpty(ext9) && TextUtils.isEmpty(ext10)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean check(WorkingBean local, WorkingBean netWork) {
        boolean ext1 = local.getExt1().equals(netWork.getExt1());
        boolean ext2 = local.getExt2().equals(netWork.getExt2());
        boolean ext3 = local.getExt3().equals(netWork.getExt3());
        boolean ext4 = local.getExt4().equals(netWork.getExt4());
        boolean ext5 = local.getExt5().equals(netWork.getExt5());
        boolean ext6 = local.getExt6().equals(netWork.getExt6());
        boolean ext7 = local.getExt7().equals(netWork.getExt7());
        boolean ext8 = local.getExt8().equals(netWork.getExt8());
        boolean ext9 = local.getExt9().equals(netWork.getExt9());
        boolean ext10 = local.getExt10().equals(netWork.getExt10());

        if (ext1 && ext2 && ext3 && ext4 && ext5 && ext6 && ext7 && ext8 && ext9 && ext10) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 绘制
     */
    private void draw() {
        this.mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    /**
     * 显示在控件下右方
     * @param parent
     */
    public void showAtDropDownRight(View parent) {
        if (parent.getVisibility() == View.GONE) {
            this.showAtLocation(parent, 0, 0, 0);
        } else {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0] + parent.getWidth() - this.getWidth(), location[1] + parent.getHeight());
        }
    }
}
