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
import java.util.regex.Pattern;

/**
 * Created by jack on 2017/10/16.
 */

public class ImageUtil {
    // 计算每行应该有多少内容
    private static List<String> list = new ArrayList<>();
    // 计算第一行文字个数
    private static Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
    private static Matcher m;
    private static String[] str = new String[] {"我"};
    private static String txt = "";
    private static int byteLen = 0;

    /**
     * 设置水印图片在左上角
     *
     * @param context
     * @param src
     */
    public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, String level_0, String level_1, String createTime) {
        return createWaterMaskBitmap(src, context, level_0, level_1, createTime);
    }

    /**
     * 给图片添加水印图和文字
     * @param src
     * @param context
     * @param level_0
     * @param level_1
     * @param createTime
     * @return
     */
    private static Bitmap createWaterMaskBitmap(Bitmap src, Context context, String level_0, String level_1, String createTime) {
        if (src == null) {
            return null;
        }
        // 原图尺寸
        int width = src.getWidth();
        int height = src.getHeight();
        // 原图与固定尺寸比
        float widthMultiple = Float.valueOf(width) / Float.valueOf(1280);
        float heightMultiple = Float.valueOf(height) / Float.valueOf(960);
        // 水印尺寸
        int watermarkWidth = (int) (350 *  widthMultiple);
        int watermarkHeight = (int) (145 *  heightMultiple);
        // Logo所占高度+距离上部分的距离
        int logoHeight = (int) (16 * heightMultiple) + (int) (5 * heightMultiple);
        // 施工部位
        level_1 = "施工部位：" + level_1;
        // 施工部位所占长度
        int len = level_1.length();
        // 计算一个16sp中文所占像素
        Paint pFont = new Paint();
        Rect rect = new Rect();
        pFont.setTextSize(16 * widthMultiple);
        pFont.getTextBounds("豆", 0, 1, rect);

        int oneSizeWidth = computeMaxStringWidth(str, pFont);
        int oneSizeHeight = rect.height();
        // 去掉左右间距所剩宽度
        int lenWidth = watermarkWidth - DensityUtil.dip2px(10);
        // 水印图上文字大小
        int testSize = DensityUtil.px2dip(16 * widthMultiple);
        list.clear();
        if (computeMaxStringWidth(new String[] {level_1}, pFont) < lenWidth) {
            list.add(level_1);
        } else {
            byteLen = 0;
            for (int i = 1; i < len; i++) {
                if (i == 0) {
                    txt = level_1.substring(0, i);
                } else {
                    txt = level_1.substring(i-1, i);
                }

                m = p.matcher(txt);

                if(m.matches()){
                    byteLen+=oneSizeWidth;
                } else {
                    String[] strNum = new String[] {txt};
                    byteLen+=(computeMaxStringWidth(strNum, pFont));
                }
                if (byteLen > lenWidth) {
                    list.add(level_1.substring(0, i-1));
                    level_1 = level_1.substring(i-1);
                    break;
                }
            }

            // 第二行显示文字实际宽度
            lenWidth = lenWidth - computeMaxStringWidth(new String[] {"施工部位："} , pFont) - DensityUtil.dip2px(5);
            rows(level_1, lenWidth, oneSizeWidth, pFont);
        }

        int marginTop = 8;
        // 设置水印高度 上下距离+(行高+行距) * 行数 + logo高度
        int marginSum = (int) ((10 + (oneSizeHeight + marginTop) * (list.size() + 1) + logoHeight)  * heightMultiple);
        if (marginSum > watermarkHeight) {
            watermarkHeight = marginSum;
        }

        // 创建水印画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        // Color.argb(255 ,255, 255, 255) 白色不透明（0~255）
        canvas.drawColor(Color.argb(255 ,255, 255, 255));

        // 将Logo添加到底板中
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermark_logo);
        Rect rectSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); // 创建一个指定的新矩形的坐标
        Rect dst = new Rect(dp2px(context, 5 * widthMultiple), dp2px(context, 1 * heightMultiple), bitmap.getWidth() + dp2px(context, 5 * widthMultiple), bitmap.getHeight() + dp2px(context, 1 * heightMultiple)); // 创建一个指定的新矩形的坐标
        canvas.drawBitmap(bitmap, rectSrc, dst, null); // 将photo 缩放或则扩大搜索到

        //canvas.drawBitmap(bitmap, dp2px(context, 5 * widthMultiple), 5 * heightMultiple, null);

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "山西路桥", DensityUtil.px2dip(20 * heightMultiple), Color.rgb(0, 0, 0), (int) (20 * widthMultiple), (int) (2 * heightMultiple));

        int marginTopSize = DensityUtil.px2dip(logoHeight + 12 * heightMultiple);

        // 添加文字
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "工程名称：" + level_0, testSize, Color.rgb(0, 0, 0), 5, marginTopSize);

        marginTopSize +=  DensityUtil.px2dip(3 * heightMultiple);

        // 循环向图片上添加文字
        for (int i = 0; i < list.size(); i++) {
            marginTopSize += (DensityUtil.px2dip((oneSizeHeight + marginTop * heightMultiple)));
            if (i == 0) {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, list.get(i), testSize, Color.rgb(0, 0, 0), 5, marginTopSize);
            } else {
                watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, list.get(i), testSize, Color.rgb(0, 0, 0), DensityUtil.px2dip(computeMaxStringWidth(new String[] {"施工部位："}, pFont)) + 5, marginTopSize);
            }
        }

        marginTopSize += (DensityUtil.px2dip((oneSizeHeight + marginTop * heightMultiple)) + 2);
        String userName = (String) SpUtil.get(context, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(context, watermarkBitmap, "上传人员：" + userName + " " + createTime, testSize, Color.rgb(0, 0, 0), 5, marginTopSize);

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
    private static void rows(String level_1, int lenSize, int oneSizeWidth, Paint pFont){
        int len = level_1.length();
        byteLen = 0;
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                txt = level_1.substring(0, i);
            } else {
                txt = level_1.substring(i-1, i);
            }
            m = p.matcher(txt);
            if(m.matches()){
                byteLen+=oneSizeWidth;
            } else {
                String[] strNum = new String[] {txt};
                byteLen+=(computeMaxStringWidth(strNum, pFont));
            }
            if (byteLen > lenSize) {
                list.add(level_1.substring(0, i-1));
                level_1 = level_1.substring(i-1);
                break;
            }
        }

        if (level_1.getBytes().length > lenSize) {
            rows(level_1, lenSize, oneSizeWidth, pFont);
        } else {
            list.add(level_1);
        }
    }

    /**
     * 获取字符串所占像素（px）
     * @param strings
     * @param p
     * @return
     */
    private static int computeMaxStringWidth(String[] strings, Paint p) {
        float maxWidthF = 0.0f;
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            float width = p.measureText(strings[i]);
            maxWidthF = Math.max(width, maxWidthF);
        }
        int maxWidth = (int) (maxWidthF + 0.5);
        return maxWidth;
    }
}
