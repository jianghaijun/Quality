package com.sx.quality.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.sx.quality.adapter.V_2ContractorDetailsAdapter;
import com.sx.quality.application.MyApplication;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.bean.PictureBean;
import com.sx.quality.bean.WorkingBean;
import com.sx.quality.dialog.MeasuredProjectDialog;
import com.sx.quality.dialog.PromptDialog;
import com.sx.quality.dialog.RejectDialog;
import com.sx.quality.dialog.ReportDialog;
import com.sx.quality.dialog.UpLoadPhotosDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.FileInfoListener;
import com.sx.quality.listener.PermissionListener;
import com.sx.quality.listener.ReportListener;
import com.sx.quality.listener.ShowPhotoListener;
import com.sx.quality.manager.GPSLocationListener;
import com.sx.quality.manager.GPSLocationManager;
import com.sx.quality.manager.GPSProviderStatus;
import com.sx.quality.model.ContractorDetailsModel;
import com.sx.quality.model.PictureModel;
import com.sx.quality.service.LocationService;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DataUtils;
import com.sx.quality.utils.FileUtil;
import com.sx.quality.utils.ImageUtil;
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
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 承包商详情
 */
public class V_2ContractorDetailsActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.btnRight)
    private Button btnRight;
    /*数据信息*/
    @ViewInject(R.id.txtWorkingPath)
    private TextView txtWorkingPath;
    @ViewInject(R.id.txtWorkingNo)
    private TextView txtWorkingNo;
    @ViewInject(R.id.txtWorkingName)
    private TextView txtWorkingName;
    @ViewInject(R.id.txtEntryTime)
    private TextView txtEntryTime;
    @ViewInject(R.id.txtLocation)
    private TextView txtLocation;
    @ViewInject(R.id.txtLocationPosition)
    private TextView txtLocationPosition;
    @ViewInject(R.id.txtDistanceAngle)
    private TextView txtDistanceAngle;
    @ViewInject(R.id.txtTakePhotoNum)
    private TextView txtTakePhotoNum;
    @ViewInject(R.id.txtTakePhotoRequirement)
    private EditText txtTakePhotoRequirement;
    @ViewInject(R.id.btnLocalSave)
    private Button btnLocalSave;
    @ViewInject(R.id.btnSavePhoto)
    private Button btnSavePhoto;
    @ViewInject(R.id.btnMeasuredRecord)
    private Button btnMeasuredRecord;
    @ViewInject(R.id.imgBtnAdd)
    private ImageButton imgBtnAdd;
    @ViewInject(R.id.rlRejectPhotos)
    private RelativeLayout rlRejectPhotos;
    @ViewInject(R.id.rlFixedPoint)
    private RelativeLayout rlFixedPoint;
    @ViewInject(R.id.rlLocationPosition)
    private RelativeLayout rlLocationPosition;
    @ViewInject(R.id.workingNo)
    private TextView workingNo;
    @ViewInject(R.id.workingName)
    private TextView workingName;
    @ViewInject(R.id.txtRejectPhoto)
    private TextView txtRejectPhoto;
    /*层厚*/
    @ViewInject(R.id.edtPositionOfPileNumber1)
    private EditText edtPositionOfPileNumber1;
    @ViewInject(R.id.edtElevation1)
    private EditText edtElevation1;
    @ViewInject(R.id.edtPositionOfPileNumber2)
    private EditText edtPositionOfPileNumber2;
    @ViewInject(R.id.edtElevation2)
    private EditText edtElevation2;
    @ViewInject(R.id.edtPositionOfPileNumber3)
    private EditText edtPositionOfPileNumber3;
    @ViewInject(R.id.edtElevation3)
    private EditText edtElevation3;
    @ViewInject(R.id.edtPositionOfPileNumber4)
    private EditText edtPositionOfPileNumber4;
    @ViewInject(R.id.edtElevation4)
    private EditText edtElevation4;
    @ViewInject(R.id.edtPositionOfPileNumber5)
    private EditText edtPositionOfPileNumber5;
    @ViewInject(R.id.edtElevation5)
    private EditText edtElevation5;
    /*图片信息*/
    @ViewInject(R.id.rvContractorDetails)
    private RecyclerView rvContractorDetails;
    private V_2ContractorDetailsAdapter adapter;
    private List<ContractorListPhotosBean> phoneList = new ArrayList<>();
    /*定位信息*/
    private LocationService locationService;
    private final int SDK_PERMISSION_REQUEST = 127;
    private GPSLocationManager gpsLocationManager;
    /*拍照*/
    private Uri uri = null;
    private String fileUrlName;
    private ContractorListPhotosBean addPhotoBean;
    private String strFilePath;
    private File imgFile;

    private Context mContext;
    private String processId, rootNodeName, status, processName;
    private double longitude, latitude;
    private String sLocation;

    /**
     * 是否已点击上报按钮
     */
    public static boolean isCanSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_2activity_contractor_details);

        mContext = this;
        x.view().inject(this);
        ScreenManagerUtil.pushActivity(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));

        txtTitle.setText(R.string.app_name);

        strFilePath = mContext.getExternalCacheDir().getAbsolutePath() + "/";
        imgFile = new File(strFilePath);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }

        processId = getIntent().getStringExtra("processId");
        rootNodeName = getIntent().getStringExtra("nodeName");
        status = getIntent().getStringExtra("status");
        processName = getIntent().getStringExtra("processName");

        // 如果是驳回状态 显示驳回原因
        if (status.equals("3")) {
            rlRejectPhotos.setVisibility(View.VISIBLE);
            txtRejectPhoto.setText(getIntent().getStringExtra("dismissal"));
        }

        String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
        if (type.equals("1")) {
            workingNo.setText("隐患编号");
            workingName.setText("隐患内容");
        }
        // 所有填方工序显示层厚定点位置 隐藏当前位置和定位位置
        if (rootNodeName.contains("填方")) {
            rlFixedPoint.setVisibility(View.VISIBLE);
            btnSavePhoto.setVisibility(View.VISIBLE);
            btnSavePhoto.setText("保存层厚\n定点位置");
        }

        String userLevel = (String) SpUtil.get(this, ConstantsUtil.USER_LEVEL, "");
        if (userLevel.equals("1")) {
            // 监理--->显示驳回按钮、上传云端和完成按钮
            btnLocalSave.setVisibility(View.VISIBLE);
            if (rlFixedPoint.getVisibility() != View.VISIBLE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnLocalSave.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                btnLocalSave.setLayoutParams(params);
            }

            //btnSavePhoto.setVisibility(View.VISIBLE);
            if (rlFixedPoint.getVisibility() != View.VISIBLE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMeasuredRecord.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.setMargins(0, 0, DensityUtil.dip2px(60), 0);
                btnMeasuredRecord.setLayoutParams(params);
            }
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setText("完成");
        } else if(userLevel.equals("2")) {
            // 领导-->隐藏拍照功能
            imgBtnAdd.setVisibility(View.GONE);
            if (rlFixedPoint.getVisibility() != View.VISIBLE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMeasuredRecord.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                btnMeasuredRecord.setLayoutParams(params);
            }
        } else {
            // 班组，施工人员--->显示上传云端和审核按钮
            //btnSavePhoto.setVisibility(View.VISIBLE);
            if (rlFixedPoint.getVisibility() != View.VISIBLE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnMeasuredRecord.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                btnMeasuredRecord.setLayoutParams(params);
            }
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setText("审核");
        }

        getPermissions();

        // 没有网络并且没有加载过
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData();
        } else {
            // 查询本地保存的照片
            phoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), processId).find(ContractorListPhotosBean.class);
            setData();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_PHONE_LIST)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingUtils.hideLoading();
                runChildrenThread(getString(R.string.server_exception));
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
                            final ContractorDetailsModel model = gson.fromJson(jsonData, ContractorDetailsModel.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 查询本地保存的照片
                                    phoneList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), processId).find(ContractorListPhotosBean.class);
                                    for (ContractorListPhotosBean photo: model.getData()) {
                                        phoneList.add(photo);
                                    }

                                    setData();
                                    LoadingUtils.hideLoading();
                                }
                            });
                        } else {
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 赋值
     */
    private void setData() {
        String distance = getIntent().getStringExtra("location")  == null || TextUtils.isEmpty(getIntent().getStringExtra("location")) || getIntent().getStringExtra("location").equals("unknown") || getIntent().getStringExtra("location").equals("null") ? "" : getIntent().getStringExtra("location");
        txtLocation.setText(distance);
        txtWorkingPath.setText(rootNodeName);
        txtWorkingNo.setText(getIntent().getStringExtra("processCode"));
        txtWorkingName.setText(getIntent().getStringExtra("processName"));
        txtDistanceAngle.setText(getIntent().getStringExtra("distanceAngle"));
        if (!TextUtils.isEmpty(distance)) {
            rlLocationPosition.setVisibility(View.GONE);
        }
        txtEntryTime.setText(getIntent().getStringExtra("enterTime"));
        txtTakePhotoNum.setText("最少拍照" + getIntent().getStringExtra("actualNumber") + "张");
        txtTakePhotoRequirement.setText(getIntent().getStringExtra("photoContent"));
        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
        WorkingBean bean = null;
        if (workList != null && workList.size() > 0) {
            bean = workList.get(0);
        }
        edtPositionOfPileNumber1.setText(checkText() ? bean == null ? "" : bean.getExt1() : getIntent().getStringExtra("ext1"));
        edtElevation1.setText(checkText() ? bean == null ? "" : bean.getExt2() : getIntent().getStringExtra("ext2"));
        edtPositionOfPileNumber2.setText(checkText() ? bean == null ? "" : bean.getExt3() : getIntent().getStringExtra("ext3"));
        edtElevation2.setText(checkText() ? bean == null ? "" : bean.getExt4() : getIntent().getStringExtra("ext4"));
        edtPositionOfPileNumber3.setText(checkText() ? bean == null ? "" : bean.getExt5() : getIntent().getStringExtra("ext5"));
        edtElevation3.setText(checkText() ? bean == null ? "" : bean.getExt6() : getIntent().getStringExtra("ext6"));
        edtPositionOfPileNumber4.setText(checkText() ? bean == null ? "" : bean.getExt7() : getIntent().getStringExtra("ext7"));
        edtElevation4.setText(checkText() ? bean == null ? "" : bean.getExt8() : getIntent().getStringExtra("ext8"));
        edtPositionOfPileNumber5.setText(checkText() ? bean == null ? "" : bean.getExt9() : getIntent().getStringExtra("ext9"));
        edtElevation5.setText(checkText() ? bean == null ? "" : bean.getExt10() : getIntent().getStringExtra("ext10"));

        if (phoneList != null) {
            adapter = new V_2ContractorDetailsAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), status);

            LinearLayoutManager ms = new LinearLayoutManager(this);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvContractorDetails.setLayoutManager(ms);
            rvContractorDetails.setAdapter(adapter);
        }

        // 有网络时提交层厚定点位置信息
        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            if (checkText() && !checkLocal(bean)) {
                PromptDialog promptDialog = new PromptDialog(mContext, new ChoiceListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            submitElevation(true);
                        }
                    }
                }, "提示", "该工序填写的层厚定点位置信息还未上传到云端，是否上传？", "否", "是");
                promptDialog.show();
            }
        }
    }

    /**
     * 是否都为空
     * @return
     */
    private boolean checkText() {
        String ext1 = getIntent().getStringExtra("ext1");
        String ext2 = getIntent().getStringExtra("ext2");
        String ext3 = getIntent().getStringExtra("ext3");
        String ext4 = getIntent().getStringExtra("ext4");
        String ext5 = getIntent().getStringExtra("ext5");
        String ext6 = getIntent().getStringExtra("ext6");
        String ext7 = getIntent().getStringExtra("ext7");
        String ext8 = getIntent().getStringExtra("ext8");
        String ext9 = getIntent().getStringExtra("ext9");
        String ext10 = getIntent().getStringExtra("ext10");

        if (TextUtils.isEmpty(ext1) && TextUtils.isEmpty(ext2) && TextUtils.isEmpty(ext3) && TextUtils.isEmpty(ext4) && TextUtils.isEmpty(ext5) && TextUtils.isEmpty(ext6) && TextUtils.isEmpty(ext7) && TextUtils.isEmpty(ext8) && TextUtils.isEmpty(ext9) && TextUtils.isEmpty(ext10)) {
            return true;
        } else {
            return false;
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

    /**
     * 图片点击事件监听--->全屏预览图片
     */
    private ShowPhotoListener listener = new ShowPhotoListener() {
        @Override
        public void selectWayOrShowPhoto(boolean isShowPhoto, String point, String photoUrl, int isUpLoad) {
            // 图片浏览
            ArrayList<String> urls = new ArrayList<>();
            int len = phoneList.size();
            for (int i = 0; i < len; i++) {
                String fileUrl = phoneList.get(i).getPhotoAddress();
                if (!TextUtils.isEmpty(fileUrl) && !fileUrl.contains(ConstantsUtil.SAVE_PATH)) {
                    fileUrl = ConstantsUtil.BASE_URL + ConstantsUtil.prefix + fileUrl;
                }
                urls.add(fileUrl);
            }
            Intent intent = new Intent(mContext, ShowPhotosActivity.class);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_URLS, urls);
            intent.putExtra(ShowPhotosActivity.EXTRA_IMAGE_INDEX, Integer.valueOf(point));
            startActivity(intent);
        }
    };

    /**
     * 选择上报给谁
     * @param pictureBeanList
     * @param submitPictureList
     */
    private void reported(final List<PictureBean> pictureBeanList, final List<ContractorListPhotosBean> submitPictureList) {
        ReportDialog reportDialog = new ReportDialog(mContext, new ReportListener() {
            @Override
            public void returnUserId(String userId) {
                submitElevation(false);

                // 设置为已上传
                for (ContractorListPhotosBean newBean : submitPictureList) {
                    // 待审核
                    PictureBean pictureBean = new PictureBean();
                    pictureBean.setPhotoId(newBean.getPhotoId());
                    pictureBeanList.add(pictureBean);
                    String photoId = newBean.getPhotoId();
                    for (ContractorListPhotosBean bean : phoneList) {
                        if (bean.getPhotoAddress().equals(newBean.getPhotoAddress())){
                            bean.setIsNewAdd(-1);
                            bean.setPhotoId(photoId);
                            bean.setIsToBeUpLoad(-1);
                            bean.setCheckFlag("0");
                        }
                    }
                }

                adapter = new V_2ContractorDetailsAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), status);

                LinearLayoutManager ms = new LinearLayoutManager(mContext);
                ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                rvContractorDetails.setLayoutManager(ms);
                rvContractorDetails.setAdapter(adapter);

                if (StrUtil.isNotEmpty(userId)) {
                    submitReported(userId, pictureBeanList);
                }
            }
        });
        reportDialog.setCanceledOnTouchOutside(false);
        reportDialog.show();
    }

    /**
     * 上报审核图片
     * @param userId
     * @param pictureBeanList
     */
    private void submitReported(String userId, final List<PictureBean> pictureBeanList) {
        LoadingUtils.showLoading(mContext);
        PictureModel model = new PictureModel();
        model.setSelectUserId(userId);
        model.setPushMessage(rootNodeName);
        model.setProcessId(processId);
        model.setRecordType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "0"));
        /*model.setSxZlPhotoList(pictureBeanList);*/
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.SUBMIT_AUDITORS_PICTURE)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runChildrenThread("上报审核失败!");
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
                                    ToastUtil.showShort(mContext, "上报成功!");
                                    // 上报成功修改状态
                                    for (ContractorListPhotosBean bean : phoneList) {
                                        bean.setCanSelect(false);
                                        for (PictureBean picBean : pictureBeanList) {
                                            if (bean.getPhotoId().equals(picBean.getPhotoId())) {
                                                bean.setCheckFlag("1");
                                                break;
                                            }
                                        }
                                    }
                                    //isCanSelect = !isCanSelect;
                                    if (null != adapter) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        } else {
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void takePictures(){
        Intent intent = new Intent();
        intent.setClass(mContext, PhotographActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            /*FileDescriptionDialog fileDescriptionDialog;*/
            switch (requestCode) {
                case 1:
                    if (data == null) {
                        return;
                    }

                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        String path = extras.getString("maxImgPath");
                        if (path != null) {
                            uri = Uri.parse(path);
                            // 如果图像是旋转的，需要旋转后保存,目前只发现三星如此
                            int degree = extras.getInt("degree");
                            switch (degree) {
                                case 0:
                                    degree = 90;
                                    break;
                                case 90:
                                    degree = 180;
                                    break;
                                case 180:
                                    degree = 270;
                                    break;
                                case 270:
                                    degree = 0;
                                    break;
                            }
                            if (degree != 0) {
                                Bitmap bitmap = rotateImageView(degree, path);
                                String newPath = saveBitmap(bitmap, ConstantsUtil.SAVE_PATH, System.currentTimeMillis() + ".png");
                                uri = Uri.parse("file://" + newPath);
                            }
                            LoadingUtils.hideLoading();
                        }
                    }

                    fileUrlName = String.valueOf(System.currentTimeMillis()) + ".png";

                    fileInfoListener.fileInfo("东二环高速公路", rootNodeName, "", "", false);
                    // 填写图片信息
                    /*fileDescriptionDialog = new FileDescriptionDialog(mContext, rootNodeName, fileInfoListener);
                    fileDescriptionDialog.show();*/
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @return Bitmap
     */
    public static Bitmap rotateImageView(int angle, String path) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // 此处采样，导致分辨率降到1/4,否则会报OOM
        bitmapOptions.inSampleSize = 1;
        Bitmap cameraBitmap = BitmapFactory.decodeFile(path, bitmapOptions);
        // 旋转图片 动作
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(cameraBitmap, 0, 0, cameraBitmap.getWidth(), cameraBitmap.getHeight(), matrix, true);
        cameraBitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 保存图片
     * @param bm
     * @param path
     * @param filename
     * @return
     */
    private String saveBitmap(Bitmap bm, String path, String filename) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        path = path + "/" + filename;
        File f2 = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(f2);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 文件描述监听
     */
    private FileInfoListener fileInfoListener = new FileInfoListener() {
        @Override
        public void fileInfo(String engineeringName, String rootNodeName, String parentNodeName, String nodeName, boolean isUploadNow) {
            LoadingUtils.showLoading(mContext);
            // 向LitePal数据库中添加一条数据
            addPhotoBean = new ContractorListPhotosBean();
            addPhotoBean.setPhotoAddress(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setProcessId(processId);
            addPhotoBean.setThumbPath(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setPhotoDesc(rootNodeName); //描述换成rootNodeName
            addPhotoBean.setPhotoName(fileUrlName);
            addPhotoBean.setCheckFlag("-1");
            addPhotoBean.setIsNewAdd(1);
            addPhotoBean.setLatitude(String.valueOf(latitude));
            addPhotoBean.setLongitude(String.valueOf(longitude));
            addPhotoBean.setLocation(sLocation);
            addPhotoBean.setUserId((String) SpUtil.get(mContext,ConstantsUtil.USER_ID, ""));
            addPhotoBean.setPhotoType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, ""));
            addPhotoBean.setCreateTime(DataUtils.getCurrentData());
            String[] strings = new String[]{engineeringName, rootNodeName};
            addPhotoBean.setIsToBeUpLoad(1);
            addPhotoBean.save();
            // 添加图片按钮
            phoneList.add(0, addPhotoBean);
            // 异步将图片存储到SD卡指定文件夹下
            new V_2ContractorDetailsActivity.StorageTask().execute(strings);
        }
    };

    /**
     * 将照片存储到SD卡
     */
    private class StorageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getRealFilePath(mContext, uri));
            // 压缩图片
            bitmap = FileUtil.compressBitmap(bitmap);
            // 在图片上添加水印
            bitmap = ImageUtil.createWaterMaskLeftTop(mContext, bitmap, params[0], params[1], addPhotoBean.getCreateTime());
            // 保存到SD卡指定文件夹下
            saveBitmapFile(bitmap, fileUrlName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LoadingUtils.hideLoading();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 保存图片到SD卡指定目录下
     * @param bitmap
     * @param fileName
     */
    public void saveBitmapFile(Bitmap bitmap, String fileName){
        // 将要保存图片的路径
        File imgFile = new File(ConstantsUtil.SAVE_PATH);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }

        File file = new File(ConstantsUtil.SAVE_PATH + fileName);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (locationService != null) {
            locationService.unregisterListener(mListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        super.onStop();
    }

    /**
     * 定位结果回调
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                final StringBuffer sb = new StringBuffer(256);
                /*sb.append("经度 : ");// 经度
                sb.append(location.getLongitude());
                sb.append("  纬度 : ");// 纬度
                sb.append(location.getLatitude());*/
                sb.append(location.getAddrStr());
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                sLocation = sb.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != sb.toString() && !TextUtils.isEmpty(sb.toString())) {
                            txtLocationPosition.setText(sb.toString());
                        }
                    }
                });
            }
        }

    };

    @TargetApi(23)
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissionInfo = "";
            ArrayList<String> permissions = new ArrayList<>();
            // 定位精确位置
            if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            // 读写权限
            if (addPermission(permissions, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, android.Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            } else {
                if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorDetailsActivity.this)) {
                    locationService = ((MyApplication) getApplication()).locationService;
                    locationService.registerListener(mListener);
                    //注册监听
                    int type = getIntent().getIntExtra("from", 0);
                    if (type == 0) {
                        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
                    } else if (type == 1) {
                        locationService.setLocationOption(locationService.getOption());
                    }
                    locationService.start();// 定位SDK
                } else {
                    //开启GPS定位
                    gpsLocationManager = GPSLocationManager.getInstances(this);
                    gpsLocationManager.start(new GpsListener());
                }
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }
        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorDetailsActivity.this)) {
            locationService = ((MyApplication) getApplication()).locationService;
            locationService.registerListener(mListener);
            //注册监听
            int type = getIntent().getIntExtra("from", 0);
            if (type == 0) {
                locationService.setLocationOption(locationService.getDefaultLocationClientOption());
            } else if (type == 1) {
                locationService.setLocationOption(locationService.getOption());
            }
            locationService.start();// 定位SDK
        } else {
            //开启GPS定位
            gpsLocationManager.start(new GpsListener());
            gpsLocationManager = GPSLocationManager.getInstances(this);
        }
    }

    /**
     * GPS定位监听
     */
    private class GpsListener implements GPSLocationListener {
        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                sLocation = "";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtLocationPosition.setText("经度：" + longitude + " 纬度：" + latitude);
                    }
                });
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps" == provider) {
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    //Toast.makeText(mContext, "GPS开启", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    //Toast.makeText(mContext, "GPS关闭", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    //Toast.makeText(mContext, "GPS不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    //Toast.makeText(mContext, "GPS暂时不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    //Toast.makeText(mContext, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 审核照片
     */
    private void toExaminePhoto(final boolean isFinish) {
        final List<PictureBean> checkPictureList = new ArrayList<>();
        final List<ContractorListPhotosBean> submitPictureList = new ArrayList<>();
        for (ContractorListPhotosBean phoneListBean : phoneList) {
            // 需要上传的照片
            if (phoneListBean.getCheckFlag().equals("-1")) {
                // 待上传
                submitPictureList.add(phoneListBean);
            }

            if (phoneListBean.getCheckFlag().equals("0")) {
                // 待审核
                PictureBean bean = new PictureBean();
                bean.setPhotoId(phoneListBean.getPhotoId());
                checkPictureList.add(bean);
            }
        }

        if (!isFinish) {
            if (checkPictureList.size() == 0 && submitPictureList.size() == 0) {
                ToastUtil.showLong(mContext, "没有待审核的照片，请先拍照再进行审核!");
                return;
            }
        }

        // 上报审核
        if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorDetailsActivity.this)) {
            if (submitPictureList.size() > 0) {
                // 上传
                UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, submitPictureList, new ChoiceListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            if (isFinish) {
                                // 完成工序接口
                                for (ContractorListPhotosBean phone: phoneList) {
                                    phone.setIsToBeUpLoad(-1);
                                    phone.setIsNewAdd(-1);
                                    phone.setCheckFlag("0");
                                }

                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }

                                RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
                                    @Override
                                    public void returnUserId(String userId) {
                                        rejectSubmit(userId, "1");
                                    }
                                }, "处理意见", "取消", "确认");
                                rejectDialog.show();
                            } else {
                                reported(checkPictureList, submitPictureList);
                            }
                        }
                    }
                });
                upLoadPhotosDialog.setCanceledOnTouchOutside(false);
                upLoadPhotosDialog.show();
            } else {
                if (isFinish) {
                    // 完成工序接口
                    RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
                        @Override
                        public void returnUserId(String userId) {
                            // 调用驳回接口
                            rejectSubmit(userId, "1");
                        }
                    }, "备注", "取消", "确认");
                    rejectDialog.show();
                } else {
                    reported(checkPictureList, submitPictureList);
                }
            }
        } else {
            ToastUtil.showLong(mContext, "当前无网络，请连接网络再进行审核!");
        }
    }

    /**
     * 完成工序
     */
    private void finishPhoto() {
        // 设置为驳回状态
        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
        if (workList != null && workList.size() > 0) {
            WorkingBean bean = workList.get(0);
            if (bean.getProcessState().equals("4")) {
                ToastUtil.showShort(mContext, "该工序已为完成状态，不能再次提交!");
                return;
            } else if (bean.getProcessState().equals("3")) {
                ToastUtil.showShort(mContext, "驳回状态不能进行完成操作!");
                return;
            }  else if (!status.equals("2")) {
                ToastUtil.showShort(mContext, "该工序还未提交审核！");
                return;
            }
        }

        /*if (status.equals("3")) {
            ToastUtil.showShort(mContext, "驳回状态不能进行完成操作!");
            return;
        }*/

        toExaminePhoto(true);
    }

    /**
     * 拍照
     */
    private void takePhotos() {
        if (status.equals("4")) {
            String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
            if (type.equals("1")) {
                ToastUtil.showLong(mContext, "审核通过的隐患不能再进行拍照!");
            } else {
                ToastUtil.showLong(mContext, "审核通过的工序不能再进行拍照!");
            }
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestAuthority(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, new PermissionListener() {
                    @Override
                    public void agree() {
                        takePictures();
                    }

                    @Override
                    public void refuse(List<String> refusePermission) {
                        ToastUtil.showLong(mContext, "您已拒绝拍照权限!");
                    }
                });
            } else {
                takePictures();
            }
        }
    }

    /**
     * 实测记录
     */
    private void measuredRecord() {
        if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorDetailsActivity.this)) {
            MeasuredProjectDialog measuredProjectDialog = new MeasuredProjectDialog(mContext, new ReportListener() {
                @Override
                public void returnUserId(String projectId) {
                    Intent intent = new Intent(mContext, ActualMeasurementActivity.class);
                    intent.putExtra("processId", processId);
                    intent.putExtra("projectId", projectId);
                    intent.putExtra("rootNodeName", rootNodeName);
                    intent.putExtra("processName", processName);
                    startActivity(intent);
                }
            });
            measuredProjectDialog.show();
        } else {
            ToastUtil.showLong(mContext, "当前无网络，请连接你的网络!");
        }
    }

    /**
     * 驳回
     */
    private void reject() {
        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
        if (workList != null && workList.size() > 0) {
            WorkingBean bean = workList.get(0);
            if (bean.getProcessState().equals("3")) {
                ToastUtil.showShort(mContext, "驳回状态不能进行完成操作!");
                return;
            }
        }

        /*if (status.equals("3")) {
            ToastUtil.showShort(mContext, "驳回状态不能进行完成操作!");
            return;
        } else*/ if (status.equals("4")) {
            String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
            if (type.equals("1")) {
                ToastUtil.showLong(mContext, "审核通过的隐患不能进行驳回操作!");
            } else {
                ToastUtil.showLong(mContext, "审核通过的工序不能进行驳回操作!");
            }
        } else if (!status.equals("2")) {
            String type = (String) SpUtil.get(this, ConstantsUtil.USER_TYPE, "");
            if (type.equals("1")) {
                ToastUtil.showLong(mContext, "该隐患还未进行审核照片步骤!");
            } else {
                ToastUtil.showLong(mContext, "该工序还未进行审核照片步骤!");
            }
        } else {
            RejectDialog rejectDialog = new RejectDialog(mContext, new ReportListener() {
                @Override
                public void returnUserId(String userId) {
                    // 调用驳回接口
                    rejectSubmit(userId, "0");
                }
            }, "驳回原因", "取消", "确认");
            rejectDialog.show();
        }
    }

    /**
     * 提交驳回
     * @param remark
     */
    private void rejectSubmit(String remark, final String stateFlag) {
        LoadingUtils.showLoading(mContext);
        PictureModel model = new PictureModel();
        model.setProcessId(processId);
        model.setRecordType((String) SpUtil.get(mContext, ConstantsUtil.USER_TYPE, "0"));
        model.setDismissal(remark);
        model.setStateFlag(stateFlag);

        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, gson.toJson(model).toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.REJECT_FINISH)
                .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                .post(requestBody)
                .build();
        ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runChildrenThread("操作失败!");
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
                                    ToastUtil.showShort(mContext, "操作成功!");
                                    // 设置状态
                                    /*finish();*/
                                    List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
                                    if (workList != null && workList.size() > 0) {
                                        WorkingBean bean = workList.get(0);
                                        if (stateFlag.equals("0")) {
                                            bean.setProcessState("3");
                                        } else {
                                            bean.setProcessState("4");
                                        }
                                        bean.saveOrUpdate("processId = ?", bean.getProcessId());
                                    }

                                    if (null != adapter) {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        } else {
                            tokenErr(code, msg);
                        }
                    } catch (JSONException e) {
                        LoadingUtils.hideLoading();
                        runChildrenThread(getString(R.string.data_error));
                        e.printStackTrace();
                    }
                } else {
                    runChildrenThread(getString(R.string.json_error));
                }
            }
        });
    }

    /**
     * 上传云端
     */
    /*private void submitCloudEnd() {
        final List<ContractorListPhotosBean> newList = getNewAddPhone();
        List<ContractorListPhotosBean> oldList = DataSupport.where("isToBeUpLoad = 1 AND userId = ? AND processId = ? AND isNewAdd = -1 order by createTime desc", (String) SpUtil.get(mContext, ConstantsUtil.USER_ID, ""), processId).find(ContractorListPhotosBean.class);
        if (newList.size() == 0 && oldList.size() == 0) {
            ToastUtil.showShort(mContext, "没有可上传的照片，请拍摄后再保存！");
        } else {
            if (JudgeNetworkIsAvailable.isNetworkAvailable(V_2ContractorDetailsActivity.this)) {
                for (ContractorListPhotosBean oldBean : oldList) {
                    newList.add(oldBean);
                }
                // 上传
                UpLoadPhotosDialog upLoadPhotosDialog = new UpLoadPhotosDialog(mContext, newList, new ChoiceListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        if (trueOrFalse) {
                            // 设置为已上传
                            for (ContractorListPhotosBean newBean : newList) {
                                for (ContractorListPhotosBean bean : phoneList) {
                                    if (bean.getPhotoAddress().equals(newBean.getPhotoAddress())){
                                        bean.setIsNewAdd(-1);
                                        bean.setPhotoId(newBean.getPhotoId());
                                        bean.setIsToBeUpLoad(-1);
                                        bean.setCheckFlag("0");
                                    }
                                }
                            }
                            adapter = new V_2ContractorDetailsAdapter(mContext, phoneList, listener, getIntent().getStringExtra("levelId"), status);

                            LinearLayoutManager ms = new LinearLayoutManager(mContext);
                            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                            rvContractorDetails.setLayoutManager(ms);
                            rvContractorDetails.setAdapter(adapter);
                        }
                    }
                });
                upLoadPhotosDialog.show();
            } else {
                ToastUtil.showLong(mContext, "当前无网络，请先连接网络再进行上传!");
            }
        }
    }*/

    /**
     * 获取新拍摄的照片
     * @return
     */
    private List<ContractorListPhotosBean> getNewAddPhone() {
        List<ContractorListPhotosBean> pictureBeanList = new ArrayList<>();
        for (ContractorListPhotosBean phoneListBean : phoneList) {
            if (phoneListBean.getIsNewAdd() == 1) {
                pictureBeanList.add(phoneListBean);
            }
        }
        return pictureBeanList;
    }

    /**
     * 子线程运行
     */
    private void runChildrenThread(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showLong(mContext, msg);
            }
        });
    }

    /**
     * Token过期
     * @param code
     * @param msg
     */
    private void tokenErr(final String code, final String msg) {
        runOnUiThread(new Runnable() {
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
                        startActivity(new Intent(mContext, LoginActivity.class));
                        break;
                    default:
                        ToastUtil.showLong(mContext, msg);
                        break;
                }
            }
        });
    }

    /**
     * 保存到服务器
     */
    private void submitElevation(final boolean islogin) {
        if (islogin) {
            LoadingUtils.showLoading(mContext);
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("processId", processId);
            obj.put("ext1", edtPositionOfPileNumber1.getText().toString().trim());
            obj.put("ext2", edtElevation1.getText().toString().trim());
            obj.put("ext3", edtPositionOfPileNumber2.getText().toString().trim());
            obj.put("ext4", edtElevation2.getText().toString().trim());
            obj.put("ext5", edtPositionOfPileNumber3.getText().toString().trim());
            obj.put("ext6", edtElevation3.getText().toString().trim());
            obj.put("ext7", edtPositionOfPileNumber4.getText().toString().trim());
            obj.put("ext8", edtElevation4.getText().toString().trim());
            obj.put("ext9", edtPositionOfPileNumber5.getText().toString().trim());
            obj.put("ext10", edtElevation5.getText().toString().trim());
            RequestBody requestBody = RequestBody.create(ConstantsUtil.JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(ConstantsUtil.BASE_URL + ConstantsUtil.UPDATE_SX_ZL_PROCESS)
                    .addHeader("token", (String) SpUtil.get(mContext, ConstantsUtil.TOKEN, ""))
                    .post(requestBody)
                    .build();
            ConstantsUtil.okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runChildrenThread("保存失败!");
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
                                        ToastUtil.showShort(mContext, "保存成功!");
                                        saveOnLocal();
                                    }
                                });
                            } else {
                                tokenErr(code, msg);
                            }
                        } catch (JSONException e) {
                            runChildrenThread(getString(R.string.data_error));
                            e.printStackTrace();
                        }
                    } else {
                        runChildrenThread(getString(R.string.json_error));
                    }
                    if (islogin) {
                        LoadingUtils.hideLoading();
                    }
                }
            });
        } catch (JSONException e) {
            if (islogin) {
                LoadingUtils.hideLoading();
            }
            ToastUtil.showShort(mContext, "参数有误！");
            e.printStackTrace();
        }
    }

    /**
     * 保存至本地
     */
    private void saveOnLocal() {
        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
        if (workList != null && workList.size() > 0) {
            WorkingBean bean = workList.get(0);
            bean.setExt1(edtPositionOfPileNumber1.getText().toString().trim());
            bean.setExt2(edtElevation1.getText().toString().trim());
            bean.setExt3(edtPositionOfPileNumber2.getText().toString().trim());
            bean.setExt4(edtElevation2.getText().toString().trim());
            bean.setExt5(edtPositionOfPileNumber3.getText().toString().trim());
            bean.setExt6(edtElevation3.getText().toString().trim());
            bean.setExt7(edtPositionOfPileNumber4.getText().toString().trim());
            bean.setExt8(edtElevation4.getText().toString().trim());
            bean.setExt9(edtPositionOfPileNumber5.getText().toString().trim());
            bean.setExt10(edtElevation5.getText().toString().trim());
            bean.saveOrUpdate("processId = ?", processId);
        }
    }

    @Event({ R.id.imgBtnLeft, R.id.btnRight, R.id.btnRightOne, R.id.imgBtnAdd, R.id.btnMeasuredRecord, R.id.btnLocalSave, R.id.btnSavePhoto })
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            // 审核
            case R.id.btnRight:
                int num = Integer.valueOf(getIntent().getStringExtra("actualNumber"));
                if (phoneList.size() < num) {
                    ToastUtil.showShort(mContext, "拍照数量不能小于最少拍照张数！");
                } else {
                    if (btnRight.getText().toString().equals("审核")) {
                        // 审核照片
                        List<WorkingBean> workList = DataSupport.where("processId = ?", processId).find(WorkingBean.class);
                        if (workList != null && workList.size() > 0) {
                            WorkingBean bean = workList.get(0);
                            if (bean.getProcessState().equals("4")) {
                                ToastUtil.showShort(mContext, "该工序已为完成状态，不能再次审核!");
                                return;
                            }
                        }
                        toExaminePhoto(false);
                    } else {
                        // 完成
                        finishPhoto();
                    }
                }
                break;
            // 拍照
            case R.id.imgBtnAdd:
                if (txtLocationPosition.getText().toString().length() < 5 || txtLocationPosition.getText().toString().contains("正在定位")) {
                    PromptDialog promptDialog = new PromptDialog(mContext, new ChoiceListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                takePhotos();
                            }
                        }
                    }, "提示", "未定位到当前位置，拍照后会导致拍摄照片无地理位置信息。是否继续拍照？", "否", "是");
                    promptDialog.show();
                } else {
                    takePhotos();
                }
                break;
            // 实测记录
            case R.id.btnMeasuredRecord:
                measuredRecord();
                break;
            // 驳回
            case R.id.btnLocalSave:
                reject();
                break;
            // 保存层厚定点位置信息
            case R.id.btnSavePhoto:
                if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
                    submitElevation(true);
                } else {
                    PromptDialog promptDialog = new PromptDialog(mContext, new ChoiceListener() {
                        @Override
                        public void returnTrueOrFalse(boolean trueOrFalse) {
                            if (trueOrFalse) {
                                saveOnLocal();
                                ToastUtil.showShort(mContext, "保存成功！");
                            }
                        }
                    }, "提示", "当前无可用网络，是否先保存至本地？", "否", "是");
                    promptDialog.show();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCanSelect = false;
        ScreenManagerUtil.popActivity(this);

        //在onPause()方法终止定位
        if (gpsLocationManager != null) {
            gpsLocationManager.stop();
        }
    }
}
