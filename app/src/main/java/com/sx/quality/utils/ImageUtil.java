package com.sx.quality.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by jack on 2017/10/16.
 */

public class ImageUtil {

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

        int width = src.getWidth();
        int height = src.getHeight();

        // 创建水印为原图三分之一1280*960画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(src.getWidth() / 5 * 3, src.getHeight() / 3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.drawColor(Color.rgb(1, 107, 94));

        int testSize = 16;
        if (width > height) {
            testSize = 10;
        }

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_0, testSize, Color.WHITE, 10, 10);
        int len = level_1.length();
        int marginTopSize = 10;
        int textLen = 13;
        if (width > height) {
            textLen = 19;
        }

        for (int i = 0; i < len; i+=textLen) {
            if (width > height) {
                marginTopSize+=20;
            } else {
                marginTopSize+=30;
            }

            if (width < height && i == textLen) {
                textLen+=1;
            }

            if (i + textLen < len) {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_1.substring(i, i+textLen), testSize, Color.WHITE, 10, marginTopSize);
            } else {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_1.substring(i), testSize, Color.WHITE, 10, marginTopSize);
            }
        }

        if (width > height) {
            marginTopSize+=20;
        } else {
            marginTopSize+=30;
        }
        String userName = ((String) SpUtil.get(context, "UserName", "")).equals("超级管理员") ? "管理员" : (String) SpUtil.get(context, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, userName + " " + createTime, testSize, Color.WHITE, 10, marginTopSize);

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
}
