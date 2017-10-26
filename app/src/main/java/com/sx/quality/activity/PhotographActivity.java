package com.sx.quality.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.sx.quality.utils.Constants;
import com.sx.quality.utils.ConstantsUtil;
import com.sx.quality.utils.SensorUtil;
import com.sx.quality.utils.SoundUtils;
import com.sx.quality.view.FinderView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * 自定义相机
 */
public class PhotographActivity extends AppCompatActivity implements SurfaceHolder.Callback, SensorEventListener {
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView surface_view;
    private FinderView finder_view;
    private Button flashlightBtn;
    // 定义传感器管理器
    private SensorManager sensorMag = null;
    // 闪光灯默认关闭
    private boolean flashlight = false;
    public boolean bIsFocus = false;
    public boolean bIsEditBySensor = false;
    public boolean bIsFocusing = false;
    private Button btn_name;
    // 回调函数开关
    private boolean Enabled = false;
    // 消息常量值
    private static final int PHOTOGRAPH = Constants.MESSAGE_ONE;
    private Bitmap mBitmap;

    private int mOrientation = 270;
    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    // 拍照
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PHOTOGRAPH:
                    if (!Enabled) {
                        mCamera.takePicture(null, null, myJpegCallback);
                        Enabled = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置相机横屏显示---SCREEN_ORIENTATION_PORTRAIT竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置视图
        setContentView(R.layout.activity_photograph);
        WindowManager WM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        //初始化界面
        init();
        Monitor();

        // 屏幕方向监听
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        }
    }

    /**
     * 屏幕方向旋转监听
     */
    private class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;

            if (newOrientation != mOrientation) {
                // 返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
                mOrientation = newOrientation;
                if (mCamera != null) {
                    updateCameraParameters();
                    switch (mOrientation) {
                        case 0:
                            mCamera.setDisplayOrientation(90);
                            break;
                        case 90:
                            mCamera.setDisplayOrientation(0);
                            break;
                        case 180:
                            mCamera.setDisplayOrientation(90);
                            break;
                        case 270:
                            mCamera.setDisplayOrientation(0);
                            break;
                    }
                    //开启预览
                    mCamera.startPreview();
                    // 2如果要实现连续的自动对焦，这一句必须加
                    mCamera.cancelAutoFocus();
                }
            }
        }
    }

    /**
     * 传感器注销事件
     */
    private void SensorCancellation() {
        if (SensorUtil.isStart()) {
            SensorUtil.setIsStart(false);
            sensorMag.unregisterListener(PhotographActivity.this);
        }
    }

    /**
     * 按鈕点击事件处理
     */
    private void Monitor() {
        // 拍照按钮
        btn_name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    SoundUtils.playerScanOkWav(getApplicationContext(), 1);
                    SensorCancellation();
                    btn_name.setEnabled(false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = PHOTOGRAPH;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        });

        // 点击屏幕对焦
        finder_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });

        // 闪光灯按钮
        flashlightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openFlashLamp();
            }
        });
    }

    /**
     * 初始化
     */
    private void init() {
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        btn_name = (Button) findViewById(R.id.btn_name);

        finder_view = (FinderView) findViewById(R.id.finder_view);
        finder_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mHolder = surface_view.getHolder();
        // translucent半透明 transparent透明
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
        restartSensor();
        updateCameraParameters();
        // 闪光灯
        flashlightBtn = (Button) findViewById(R.id.flashlightBtn);
    }

    /**
     * 传感器管理器
     */
    public void restartSensor() {
        // 获取系统传感器管理器
        if (sensorMag == null) {
            sensorMag = (SensorManager) getSystemService(SENSOR_SERVICE);
        }
        // 判断系统是否存在所需的传感器
        if (sensorMag != null) {
            SensorUtil.startSensor(sensorMag, this);
        }
    }

    /**
     * 开启相机
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bIsEditBySensor = false;

        try {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(270);
            bIsFocus = false;
            finder_view.bFocused = false;
        } catch (Exception e) {
            // 打开相机异常
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    /**
     * 相机预览数据
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            // 旋转镜头
            mCamera.setDisplayOrientation(0);
            mCamera.setPreviewDisplay(mHolder);
            updateCameraParameters();
            restartSensor();
            mCamera.startPreview();
            // 开始预览
            mCamera.autoFocus(autoFocusCB);
        } catch (Exception e) {
            e.getMessage();
            // 设置相机参数异常
        }
    }

    /**
     * Activity被暂停或收回cpu和其他资源时调用时调stopPreview释放资源
     */
    public void onPause() {
        super.onPause();
        sensorMag.unregisterListener(this);
        stopPreview();
    }

    /**
     * 释放资源
     */
    private void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }


    /**
     * 闪光灯操作
     */
    private void openFlashLamp() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        Parameters params = mCamera.getParameters();
        // 闪光灯是否打开
        if (flashlight) {
            // 关闭闪光灯
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            flashlightBtn.setBackgroundResource(R.drawable.zx_code_closelight);
            flashlight = false;
        } else {
            // 打开闪光灯
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            flashlightBtn.setBackgroundResource(R.drawable.zx_code_openlight);
            flashlight = true;
        }
        mCamera.setParameters(params);
    }

    /**
     * 打开相机
     */
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.getMessage();
        }
        return c;
    }

    /**
     * 关闭相机
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (flashlight) {
            if (mCamera != null) {
                Parameters params = mCamera.getParameters();
                // 关闭闪光灯
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
            }
            flashlightBtn.setBackgroundResource(R.drawable.flashlightclose);
            flashlight = false;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        // 判断是否开启传感器监听并注销监听
        SensorCancellation();
    }

    /**
     * 传感器精度改变事件
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }


    /**
     * 传感器改变事件
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 判断相机是否准备好并且手机移动超过一定的范围
        if (mCamera != null && SensorUtil.isStart() && SensorUtil.isOverRange(event) && !bIsFocusing) {
            // 调用自动聚焦回调
            bIsFocus = false;
            bIsFocusing = true;
            finder_view.bFocused = false;
            finder_view.invalidate();
            // 只有加上了这一句，才会自动对焦
            mCamera.cancelAutoFocus();
            mCamera.autoFocus(autoFocusCB);
        }
    }

    /**
     * AutoFocusCallback自动对焦
     */
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            bIsFocusing = false;
            if (success) {
                bIsFocus = true;
                finder_view.bFocused = true;
                finder_view.invalidate();
            } else {
                bIsFocus = false;
                finder_view.bFocused = false;
                finder_view.invalidate();
            }
        }
    };

    /**
     * 修改相机参数
     */
    private void updateCameraParameters() {
        if (mCamera != null) {
            Parameters p = mCamera.getParameters();
            Size picSize = p.getPreviewSize();
            //Size previewSize = getOptimalPreviewSize(p.getSupportedPreviewSizes(), (double) picSize.width / picSize.height);
            Size previewSize = getOptimalPreviewSize(p.getSupportedPreviewSizes(), picSize.width, picSize.height);
            if (previewSize != null) {
                p.setPreviewSize(previewSize.width, previewSize.height);
            }
            picSize = p.getPictureSize();
            //Size pictureSize = getOptimalPictureSize(p.getSupportedPictureSizes(), (double) picSize.width / picSize.height);
            Size pictureSize = getOptimalSize(p.getSupportedPictureSizes(), (double) picSize.width / picSize.height);
            if (pictureSize != null) {
                p.setPictureSize(pictureSize.width, pictureSize.height);
            }
            mCamera.setParameters(p);
        }
    }

    /**
     * 设定的屏幕的比例不是图片的比例 匹配分辨率
     */
    /*private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        if (sizes == null) {
            return null;
        }
        Size optimalSize = null;
        Collections.sort(sizes, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return new Double(lhs.width).compareTo(new Double(rhs.width));
            }
        });
        for (int i = sizes.size() - 1; i >= 0; i--) {
            Size size = sizes.get(i);
            if (((Constants.EIGHT_HUNDRED < size.width && size.width < Constants.TWO_THOUSAND)
                    || (Constants.EIGHT_HUNDRED < size.height && size.height < Constants.TWO_THOUSAND))
                    && ((size.width * 9) == (size.height * 16))) {
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }*/

    /**
     * 设置拍照的图片的比例 匹配分辨率
     */
    /*private Size getOptimalPictureSize(List<Size> sizes, double targetRatio) {
        if (sizes == null)
            return null;
        Size optimalSize = null;
        Collections.sort(sizes, new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                return new Double(lhs.width).compareTo(new Double(rhs.width));
            }
        });
        for (int i = sizes.size() - 1; i >= 0; i--) {
            Size size = sizes.get(i);
            if (((Constants.NUMBER_ONE_THOUSAN < size.width && size.width < Constants.NUMBER_TWO_THOUSAND)
                    || (Constants.NUMBER_ONE_THOUSAN < size.height && size.height < Constants.NUMBER_TWO_THOUSAND))
                    && ((size.width * 9) == (size.height * 16))) {
                optimalSize = size;
                break;
            }
        }
        *//**如果没找到16/9的就选择最接近的*//*
        if (optimalSize == null) {
            double dMin = 100.0;
            Size RightSize = null;
            for (Size size : sizes) {
                double fRate = size.width / (float) size.height;
                double fDistance = Math.abs(fRate - 16.0 / 9.0);
                //找最接近16比9的size;
                if (fDistance < dMin) {
                    dMin = fDistance;
                    RightSize = size;
                }
            }
            //最接近的值赋给变量optimalSize
            optimalSize = RightSize;
        }
        return optimalSize;
    }*/

    //这里是预览图尺寸处理的方法，就是在这把宽高调换，就可以达到效果
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
//                double ratio = (double) size.width / size.height;
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.width - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.width - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the
        // requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.width - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.width - targetHeight);
                }
            }
        }

        return optimalSize;
    }
    //这个是做拍下来图片的尺寸处理方法，宽高我已经完全调换了
    private Size getOptimalSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        int optimalSideLen = 0;
        double optimalDiffRatio = Double.MAX_VALUE;

        for (Size size : sizes) {

            int sideLen = Math.max(size.width, size.height);
            //LogEx.i("size.width: " + size.width + ", size.height: " + size.height);
            boolean select = false;
            if (sideLen < 1920) {
                if (0 == optimalSideLen || sideLen > optimalSideLen) {
                    select = true;
                }
            } else {
                if (1920 > optimalSideLen) {
                    select = true;
                } else {
                    double diffRatio = Math.abs((double) size.height / size.width - targetRatio);
                    if (diffRatio + ASPECT_TOLERANCE < optimalDiffRatio) {
                        select = true;
                    } else if (diffRatio < optimalDiffRatio + ASPECT_TOLERANCE && sideLen < optimalSideLen) {
                        select = true;
                    }
                }
            }

            if (select) {
                optimalSize = size;
                optimalSideLen = sideLen;
                optimalDiffRatio = Math.abs((double) size.height / size.width - targetRatio);
            }
        }

        return optimalSize;
    }

    /**
     * 无意中按返回键时要释放内存
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onPause();
        finish();
    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumOrientationEventListener.disable();
    }

    /**
     * 拍照回调
     */
    Camera.PictureCallback myJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            if (null != data && data.length > 0) {
                // data是字节数据，将其解析成位图
                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();

                String maxImgPath = saveJpeg(mBitmap);
                Intent intent = new Intent();
                intent.putExtra("maxImgPath", maxImgPath);
                intent.putExtra("degree", mOrientation);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };

    /*给定一个Bitmap，进行保存*/
    public String saveJpeg(Bitmap bm) {
        String savePath = ConstantsUtil.SAVE_PATH;
        File folder = new File(savePath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        long dataTake = System.currentTimeMillis();
        String jpegName = savePath + dataTake + ".png";

        File jpegFile = new File(jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegFile);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            //			//如果需要改变大小(默认的是宽960×高1280),如改成宽600×高800
            //			Bitmap newBM = bm.createScaledBitmap(bm, 600, 800, false);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegName;
    }
}