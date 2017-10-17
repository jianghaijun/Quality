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
     * @param pictureName
     *@param pictureDesc
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, String pictureName, String pictureDesc, String createTime) {
        return createWaterMaskBitmap(src, context, pictureName, pictureDesc, createTime);
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Context context, String pictureName, String pictureDesc, String createTime) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight() + 310;
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
        canvas.restore();
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
