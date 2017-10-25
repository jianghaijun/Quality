package com.sx.quality.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

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

        // 创建水印为原图三分之一w-1280*h-960画布 256*3=768
        Bitmap watermarkBitmap = Bitmap.createBitmap(src.getWidth() / 5 * 3 + 3, src.getHeight() / 3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.drawColor(Color.rgb(1, 107, 94));

        level_1 = "工序描述：" + level_1;
        int len = level_1.length();

        // 计算每行应该有多少内容
        List<String> list = new ArrayList<>();
        int lenSize = 17*3; // 水印图上每行显示多少个字
        for (int i = 0; i < len; i++) {
            int byteLen = level_1.substring(0, i).getBytes().length;
            if (list.size() > 0) {
                byteLen-=15;
            }

            if (byteLen % lenSize == 0 || (byteLen + 1) % lenSize == 0 || (byteLen + 2) % lenSize == 0) {
                if (i != 0 && (byteLen / lenSize == 1 || (byteLen + 1) / lenSize == 1) || (byteLen + 2) / lenSize == 1) {
                    list.add(level_1.substring(0, i));
                    lenSize = 12*3;
                } else if (i != 0 && (byteLen / lenSize > 1 || (byteLen + 1) / lenSize > 1) || (byteLen + 2) / lenSize > 1) {
                    int otherLen = 0;
                    for (int j = 0; j < list.size(); j++) {
                        otherLen+=list.get(j).length();
                    }
                    list.add(level_1.substring(otherLen, i));
                }
            }
        }

        // 判断是否还有一行
        int nowLen = 0;
        for (String s : list) {
            nowLen+=s.length();
        }

        if (nowLen < len) {
            list.add(level_1.substring(nowLen));
        }

        int testSize = 14;
        int marginTopSize = 4;
        switch (list.size()) {
            case 3:
                marginTopSize = 2;
                break;
            case 2:
                marginTopSize = 10;
                break;
            case 1:
                marginTopSize = 20;
                break;
        }

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "工程描述：" + level_0, testSize, Color.WHITE, 10, marginTopSize);
        for (int i = 0; i < list.size(); i++) {
            marginTopSize+=18;
            if (i == 0) {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, list.get(i), testSize, Color.WHITE, 10, marginTopSize);
            } else {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "                    " + list.get(i), testSize, Color.WHITE, 10, marginTopSize);
            }
        }

        marginTopSize+=18;
        String userName = (String) SpUtil.get(context, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "上传人员：" + userName, testSize, Color.WHITE, 10, marginTopSize);
        marginTopSize+=18;
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "上传时间：" + createTime, testSize, Color.WHITE, 10, marginTopSize);

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
