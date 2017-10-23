package com.sx.quality.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sx.quality.adapter.ContractorDetailsAdapter;
import com.sx.quality.bean.ContractorListPhotosBean;
import com.sx.quality.dialog.FileDescriptionDialog;
import com.sx.quality.dialog.SelectPhotoWayDialog;
import com.sx.quality.dialog.UpLoadPhotosDialog;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.FileInfoListener;
import com.sx.quality.listener.PermissionListener;
import com.sx.quality.listener.ShowPhotoListener;
import com.sx.quality.model.ContractorDetailsModel;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.DataUtils;
import com.sx.quality.utils.FileUtil;
import com.sx.quality.utils.ImageUtil;
import com.sx.quality.utils.JudgeNetworkIsAvailable;
import com.sx.quality.utils.LoadingUtils;
import com.sx.quality.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 承包商详情
 */
public class ContractorDetailsActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageButton imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.txtRight)
    private Button txtRight;
    @ViewInject(R.id.edtSearch)
    private EditText edtSearch;

    @ViewInject(R.id.rvContractorDetails)
    private RecyclerView rvContractorDetails;

    private Context mContext;
    private ContractorDetailsAdapter adapter;

    // 照片保存到SD卡路径
    private String strFilePath;
    private File imgFile;
    private Uri uri = null;
    private String fileUrlName;

    private String nodeId, rootNodeName; // , parentNodeName, nodeName;
    private boolean uploadNow = false;
    private ContractorListPhotosBean addPhotoBean;
    private List<ContractorListPhotosBean> upLoadNowList;

    private List<ContractorListPhotosBean> listPhotosBeen = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_details);

        mContext = this;
        x.view().inject(this);

        strFilePath = mContext.getExternalCacheDir().getAbsolutePath() + "/";
        imgFile = new File(strFilePath);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }

        nodeId = getIntent().getStringExtra("nodeId");
        rootNodeName = getIntent().getStringExtra("rootNodeName");
        /*parentNodeName = getIntent().getStringExtra("parentNodeName");
        nodeName = getIntent().getStringExtra("nodeName");*/

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(R.string.show_photo);
        /*txtRight.setVisibility(View.VISIBLE);
        txtRight.setText(R.string.choice_type);*/

        // 将添加图片按钮保存到数据库
        ContractorListPhotosBean contractorListPhotosBean = new ContractorListPhotosBean();
        contractorListPhotosBean.setPictureName("add.png");
        contractorListPhotosBean.setNodeId("-1");
        contractorListPhotosBean.setPictureAddress("");
        contractorListPhotosBean.setPictureDesc("添加图片按钮");

        contractorListPhotosBean.saveOrUpdate("nodeId=-1");
        listPhotosBeen.add(contractorListPhotosBean);

        /**
         * 搜索条件监听
         */
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = edtSearch.getText().toString();
                listPhotosBeen.clear();

                listPhotosBeen = DataSupport.where("pictureName LIKE ? AND pictureAddress != '' AND nodeId = ? or pictureDesc like ? AND pictureAddress != '' AND nodeId = ?", "%" + search + "%", nodeId, "%" + search + "%", nodeId)
                        .find(ContractorListPhotosBean.class);

                // 添加图片按钮
                listPhotosBeen.add(0, new ContractorListPhotosBean());

                adapter = new ContractorDetailsAdapter(mContext, listPhotosBeen, listener);
                rvContractorDetails.setLayoutManager(new GridLayoutManager(mContext, 3));
                rvContractorDetails.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (JudgeNetworkIsAvailable.isNetworkAvailable(this)) {
            getData(nodeId);
        } else {
            ToastUtil.showLong(mContext, getString(R.string.not_network));
        }
    }

    /**
     * 获取数据
     *
     * @param nodeId
     */
    private void getData(String nodeId) {
        LoadingUtils.showLoading(mContext);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        JSONObject object = new JSONObject();
        try {
            object.put("nodeId", nodeId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, object.toString());
        Request request = new Request.Builder()
                .url(ConstantsUtil.BASE_URL + ConstantsUtil.GET_PHONE_LIST)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 数据请求回调
     */
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LoadingUtils.hideLoading();
                    ToastUtil.showLong(mContext, getString(R.string.server_exception));
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Gson gson = new Gson();
            String jsonData = response.body().string().toString();
            jsonData = null == jsonData || jsonData.equals("null") ? "{}" : jsonData;
            try {
                final ContractorDetailsModel model = gson.fromJson(jsonData, ContractorDetailsModel.class);

                if (model.isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 添加待上传的照片
                            List<ContractorListPhotosBean> toUploadList = DataSupport.where("nodeId = ? AND isToBeUpLoad = 1 order by createtime desc", nodeId).find(ContractorListPhotosBean.class);
                            for (ContractorListPhotosBean toUploadBean : toUploadList) {
                                listPhotosBeen.add(toUploadBean);
                            }
                            int len = model.getData().size();
                            for (int i = 0; i < len; i++) {
                                listPhotosBeen.add(model.getData().get(i));
                            }
                            initData();
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
            } catch (Exception e) {
                e.getMessage();
                ToastUtil.showShort(mContext, getString(R.string.data_error));
            }

        }
    };

    /**
     * 初始化数据
     */
    private void initData() {
        // 将查询到数据的不重复的数据存储到LitePal数据库
        int length = listPhotosBeen.size();
        for (int i = 0; i < length; i++) {
            List<ContractorListPhotosBean> photosList = DataSupport.where("pictureAddress = ?", listPhotosBeen.get(i).getPictureAddress()).find(ContractorListPhotosBean.class);
            if (photosList.size() <= 0 && !listPhotosBeen.get(i).getPictureAddress().equals("")) {
                listPhotosBeen.get(i).save();
            }
        }

        adapter = new ContractorDetailsAdapter(mContext, listPhotosBeen, listener);
        rvContractorDetails.setLayoutManager(new GridLayoutManager(mContext, 3));
        rvContractorDetails.setAdapter(adapter);
    }

    /**
     * 图片点击事件监听
     */
    private ShowPhotoListener listener = new ShowPhotoListener() {
        @Override
        public void selectWayOrShowPhoto(boolean isShowPhoto, String thumbUrl, String photoUrl, int isUpLoad) {
            // 点击添加按钮--->选择照片
            if (isShowPhoto) {
                SelectPhotoWayDialog selectPhotoWayDialog = new SelectPhotoWayDialog(mContext, selectPhotosWayListener);
                selectPhotoWayDialog.setCanceledOnTouchOutside(false);
                selectPhotoWayDialog.show();
            } else {
                // 查看照片
                Intent intent = new Intent(mContext, ShowPhotoActivity.class);
                intent.putExtra("thumbUrl", thumbUrl);
                intent.putExtra("photoUrl", photoUrl);
                intent.putExtra("isUpload", isUpLoad);
                startActivity(intent);
            }
        }
    };

    /**
     * 选择照片
     */
    private ChoiceListener selectPhotosWayListener = new ChoiceListener() {
        @Override
        public void returnTrueOrFalse(boolean trueOrFalse) {
            // 拍照
            if (trueOrFalse) {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestAuthority(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                        @Override
                        public void agree() {
                            takePictures();
                        }

                        @Override
                        public void refuse(List<String> refusePermission) {
                            for (String refuse : refusePermission) {
                                ToastUtil.showLong(mContext, "您已拒绝：" + refuse + "权限!");
                            }
                        }
                    });
                } else {
                    takePictures();
                }

                // 相册
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestAuthority(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                        @Override
                        public void agree() {
                            album();
                        }

                        @Override
                        public void refuse(List<String> refusePermission) {
                            for (String refuse : refusePermission) {
                                ToastUtil.showLong(mContext, "您已拒绝：" + refuse + "权限!");
                            }
                        }
                    });
                } else {
                    album();
                }
            }
        }
    };

    /**
     * 拍照
     */
    private void takePictures(){
        Uri imageUri = null;
        String fileName = null;
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 删除上一次截图的临时文件
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // 保存本次截图临时文件名字
        fileName = String.valueOf(System.currentTimeMillis()) + ".png";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tempName", fileName);
        editor.commit();

        imageUri = Uri.parse(Uri.fromFile(imgFile) + "/" + fileName);
        // 指定照片保存路径（SD卡），image.png为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, 2);
    }

    /**
     * 相册
     */
    private void album(){
        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 2:
                    if (data != null) {
                        uri = data.getData();
                    } else {
                        String fileName = PreferenceManager.getDefaultSharedPreferences(mContext).getString("tempName", "");
                        String path = strFilePath + fileName;

                        uri = Uri.parse("file://" + path);
                        // 如果图像是旋转的，需要旋转后保存,目前只发现三星如此
                        int degree = readPictureDegree(path);
                        if (degree != 0) {
                            Bitmap bitmap = rotaingImageView(degree, path);
                            String newPath = saveBitmap(bitmap, imgFile.getAbsolutePath(), fileName);
                            uri = Uri.parse("file://" + newPath);
                        }
                    }

                    fileUrlName = String.valueOf(System.currentTimeMillis()) + ".png";

                    // 填写图片信息
                    FileDescriptionDialog fileDescriptionDialog = new FileDescriptionDialog(mContext, rootNodeName, fileInfoListener);
                    fileDescriptionDialog.show();
                    break;
                default:
                    break;
            }
        }
    }

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
            bitmap = ImageUtil.createWaterMaskLeftTop(mContext, bitmap, params[0], params[1], addPhotoBean.getCreatetime());
            // 保存到SD卡指定文件夹下
            saveBitmapFile(bitmap, fileUrlName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (uploadNow) {
                UpLoadPhotosDialog upLoadNow = new UpLoadPhotosDialog(mContext, upLoadNowList, new ChoiceListener() {
                    @Override
                    public void returnTrueOrFalse(boolean trueOrFalse) {
                        LoadingUtils.hideLoading();
                        if (null != adapter) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                upLoadNow.setCancelable(false);
                upLoadNow.setCanceledOnTouchOutside(false);
                upLoadNow.show();
            } else {
                LoadingUtils.hideLoading();
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
            addPhotoBean.setPictureAddress(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setNodeId(nodeId);
            addPhotoBean.setThumbPath(ConstantsUtil.SAVE_PATH + fileUrlName);
            addPhotoBean.setPictureDesc(rootNodeName); //描述换成rootNodeName
            addPhotoBean.setPictureName(fileUrlName);
            addPhotoBean.setCreatetime(DataUtils.getCurrentData());
            String[] strings = new String[]{engineeringName, rootNodeName};
            if (isUploadNow) {
                upLoadNowList = new ArrayList<>();
                upLoadNowList.add(addPhotoBean);
                addPhotoBean.save();
                // 添加图片按钮
                listPhotosBeen.add(1, addPhotoBean);
                uploadNow = true;
                // 异步将图片存储到SD卡指定文件夹下
                new StorageTask().execute(strings);
            } else {
                addPhotoBean.setIsToBeUpLoad(1);
                addPhotoBean.save();
                // 添加图片按钮
                listPhotosBeen.add(1, addPhotoBean);

                uploadNow = false;
                // 异步将图片存储到SD卡指定文件夹下
                new StorageTask().execute(strings);
            }
        }
    };

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

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, String path) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // 此处采样，导致分辨率降到1/4,否则会报OOM
        bitmapOptions.inSampleSize = 4;
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
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
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

    @Event({R.id.imgBtnLeft})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
        }
    }
}
