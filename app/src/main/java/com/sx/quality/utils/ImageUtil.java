package com.sx.quality.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import com.sx.quality.activity.R;

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
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, String level_0, String level_1, String level_2, String level_3, String createTime) {
        return createWaterMaskBitmap(src, context, level_0, level_1, level_2, level_3, createTime);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //将该图片作为画布
        Canvas canvas = new Canvas(newBitmap);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        return newBitmap;
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Context context, String level_0, String level_1, String level_2, String level_3, String createTime) {
        if (src == null) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();

        // 创建水印为原图三分之一1280*960画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(src.getWidth() / 5 * 3, src.getHeight() / 3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.drawColor(Color.rgb(1, 107, 94));

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_0, 20, Color.WHITE, 10, 10);
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_1, 20, Color.WHITE, 10, 40);
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_2, 20, Color.WHITE, 10, 70);
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, level_3, 20, Color.WHITE, 10, 100);
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, createTime, 20, Color.WHITE, 10, 130);

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

        /*int width = src.getWidth();
        int height = src.getHeight();
        // 创建一个bitmap
        // 创建一个新的和SRC长度宽度一样的位图
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 将该图片作为画布
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.rgb(1, 107, 94));
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);

        Resources r = context.getResources();

        Bitmap imgOne = BitmapFactory.decodeResource(r, R.drawable.action_create);
        // 在画布上绘制水印图片
        canvas.drawBitmap(imgOne, 20, height - imgOne.getHeight() - 230, null);
        newBitmap = drawTextToLeftBottom(context, newBitmap, pictureName, 20, Color.WHITE, 40, 80);

        canvas = new Canvas(newBitmap);
        canvas.drawBitmap(src, 0, 0, null);
        Bitmap imgTwo = BitmapFactory.decodeResource(r, R.drawable.action_gist);
        // 在画布上绘制水印图片
        canvas.drawBitmap(imgTwo, 20, height - imgTwo.getHeight() - 130, null);
        newBitmap = drawTextToLeftBottom(context, newBitmap, pictureDesc, 20, Color.WHITE, 40, 50);

        canvas = new Canvas(newBitmap);
        canvas.drawBitmap(src, 0, 0, null);
        Bitmap imgThree = BitmapFactory.decodeResource(r, R.drawable.action_save);
        // 在画布上绘制水印图片
        canvas.drawBitmap(imgThree, 20, height - imgThree.getHeight() - 30, null);
        newBitmap = drawTextToLeftBottom(context, newBitmap, createTime, 20, Color.WHITE, 40, 15);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();*/
        return newBitmap;
    }

    /**
     * 绘制文字到左下方
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(dp2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds, dp2px(context, paddingLeft), bitmap.getHeight() - dp2px(context, paddingBottom));
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
