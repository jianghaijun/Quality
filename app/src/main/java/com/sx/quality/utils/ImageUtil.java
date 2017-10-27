package com.sx.quality.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.transition.BitmapTransitionFactory;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.R;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by jack on 2017/10/16.
 */

public class ImageUtil {
    // 计算每行应该有多少内容
    private static List<String> list = new ArrayList<>();

    /**
     * 设置水印图片在左上角
     *
     * @param context
     * @param src
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, String level_0, String level_1, String createTime) {
        return createWaterMaskBitmap(src, context, level_0, level_1, createTime);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Context context, String level_0, String level_1, String createTime) {
        if (src == null) {
            return null;
        }

        // 原图尺寸
        int width = src.getWidth();
        int height = src.getHeight();
        // 水印尺寸
        float widthMultiple = Float.valueOf(width) / Float.valueOf(1280);
        float heightMultiple = Float.valueOf(height) / Float.valueOf(960);
        int watermarkWidth = (int) (350 *  widthMultiple);
        int watermarkHeight = (int) (140 *  heightMultiple);

        // 创建水印画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.drawColor(Color.argb(100 ,255, 255, 255));

        level_1 = "施工部位：" + level_1;
        int len = level_1.length();

        // 计算一个14sp中文所占像素
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.setTextSize(14 * widthMultiple);
        pFont.getTextBounds("豆", 0, 1, rect);
        int oneSize = rect.width();

        // 水印图上每行显示多少个字
        int lenSize = (watermarkWidth - DensityUtil.dip2px(15 * widthMultiple)) / oneSize * 3;
        // 水印图上文字大小
        int testSize = DensityUtil.px2dip(14 * widthMultiple);

        list.clear();
        for (int i = 0; i < len; i++) {
            int byteLen = level_1.substring(0, i).getBytes().length;
            if (byteLen > lenSize) {
                list.add(level_1.substring(0, i-1));
                level_1 = level_1.substring(i-1);
                break;
            }
        }

        // 第二行显示多少个字
        lenSize = ((watermarkWidth - DensityUtil.dip2px(15 * widthMultiple)) / oneSize - 5) * 3;
        rows(level_1, lenSize);

        // 将Logo添加到底板中
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
        canvas.drawBitmap(bitmap, dp2px(context, 5 * widthMultiple), 5 * widthMultiple, null);
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "山西路桥", DensityUtil.px2dip(20 * widthMultiple), Color.rgb(0, 97, 174), (int) (20 * widthMultiple), (int) (2* widthMultiple));

        int marginTopSize = (int) (10 * widthMultiple);
        /*switch (list.size()) {
            case 3:
                marginTopSize = (int) (8 * widthMultiple);
                break;
            case 2:
                marginTopSize = (int) (12 * widthMultiple);
                break;
            case 1:
                marginTopSize = (int) (16 * widthMultiple);
                break;
        }*/

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "工程名称：" + level_0, testSize, Color.rgb(0, 97, 174), 5, marginTopSize);
        for (int i = 0; i < list.size(); i++) {
            marginTopSize+= (int) (6 * widthMultiple);
            if (i == 0) {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, list.get(i), testSize, Color.rgb(0, 97, 174), 5, marginTopSize);
            } else {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "                    " + list.get(i), testSize, Color.rgb(0, 97, 174), 5, marginTopSize);
            }
        }

        marginTopSize+=(int) (6 * widthMultiple);
        String userName = (String) SpUtil.get(context, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "上传人员：" + userName + " " + createTime, testSize, Color.rgb(0, 97, 174), 5, marginTopSize);

        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 将该图片作为画布
        canvas = new Canvas(newBitmap);
        // 在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermarkBitmap, src.getWidth() - watermarkBitmap.getWidth(), src.getHeight() - watermarkBitmap.getHeight(), null);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();

        return newBitmap;
    }

    /**
     * 给图片添加文字到左上角
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, dp2px(context, paddingLeft),  dp2px(context, paddingTop) + bounds.height());
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text, Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * dip转pix
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 截取level_1
     * @param level_1
     * @param lenSize
     */
    private static void rows(String level_1, int lenSize){
        int len = level_1.length();

        for (int i = 0; i < len; i++) {
            int byteLen = level_1.substring(0, i).getBytes().length;
            if (byteLen > lenSize) {
                list.add(level_1.substring(0, i-1));
                level_1 = level_1.substring(i-1);
                break;
            }
        }

        if (level_1.getBytes().length > lenSize) {
            rows(level_1, lenSize);
        } else {
            list.add(level_1);
        }
    }
}
