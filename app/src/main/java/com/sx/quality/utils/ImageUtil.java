package com.sx.quality.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.transition.BitmapTransitionFactory;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sx.quality.activity.R;
import com.vinaygaba.rubberstamp.RubberStamp;
import com.vinaygaba.rubberstamp.RubberStampConfig;
import com.vinaygaba.rubberstamp.RubberStampPosition;

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
    private static String[] str = new String[]{"我"};
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
     *
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
        int watermarkWidth = (int) (350 * widthMultiple);
        int watermarkHeight = (int) (145 * heightMultiple);
        // 施工部位
        level_1 = "施工部位：" + level_1;
        // 施工部位所占长度
        int len = level_1.length();
        // 计算一个16sp中文所占像素
        Paint pFont = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        pFont.setTextSize(16 * widthMultiple);
        pFont.getTextBounds("豆", 0, 1, rect);
        // 一个字的高度和宽度
        int oneSizeWidth = computeMaxStringWidth(str, pFont);
        Paint.FontMetrics fm = pFont.getFontMetrics();
        int oneSizeHeight = (int) (fm.descent - fm.ascent);
        // 去掉左右间距所剩宽度
        int lenWidth = watermarkWidth - DensityUtil.dip2px(10);
        list.clear();
        if (computeMaxStringWidth(new String[]{level_1}, pFont) < lenWidth) {
            list.add(level_1);
        } else {
            byteLen = 0;
            for (int i = 1; i < len; i++) {
                if (i == 0) {
                    txt = level_1.substring(0, i);
                } else {
                    txt = level_1.substring(i - 1, i);
                }

                m = p.matcher(txt);

                if (m.matches()) {
                    byteLen += oneSizeWidth;
                } else {
                    String[] strNum = new String[]{txt};
                    byteLen += (computeMaxStringWidth(strNum, pFont));
                }
                if (byteLen > lenWidth) {
                    list.add(level_1.substring(0, i - 1));
                    level_1 = level_1.substring(i - 1);
                    break;
                }
            }

            // 第二行显示文字实际宽度
            lenWidth = lenWidth - computeMaxStringWidth(new String[]{"施工部位："}, pFont) - DensityUtil.dip2px(5);
            rows(level_1, lenWidth, oneSizeWidth, pFont);
        }

        // 设置水印高度
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect r = new Rect();
        p.setTextSize(20 * widthMultiple);
        p.getTextBounds("豆", 0, 1, r);
        Paint.FontMetrics fm1 = pFont.getFontMetrics();
        int marginSum = (int) (DensityUtil.dip2px(10 * heightMultiple) + (fm1.descent - fm1.ascent) + (list.size() + 2) * oneSizeHeight);
        if (marginSum > watermarkHeight) {
            watermarkHeight = marginSum;
        }

        // 创建水印画布
        Bitmap watermarkBitmap = Bitmap.createBitmap(watermarkWidth, watermarkHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(watermarkBitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        // 将Logo添加到底板中
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.watermark_logo);
        RubberStampConfig config = new RubberStampConfig.RubberStampConfigBuilder()
                .base(watermarkBitmap)
                .rubberStamp(bitmap)
                .rubberStampPosition(RubberStampPosition.TOP_LEFT)
                .margin(10, 2)
                .build();
        RubberStamp rubberStamp = new RubberStamp(context);
        watermarkBitmap = rubberStamp.addStamp(config);
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "山西路桥", p, r, (int) (bitmap.getWidth() + DensityUtil.dip2px(10) + 5 * widthMultiple), DensityUtil.dip2px(2 * heightMultiple));
        // 距离上面的间距
        int marginTopSize = (int) (DensityUtil.dip2px(5 * heightMultiple) + (fm1.descent - fm1.ascent));
        // 添加文字
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "工程名称：" + level_0, pFont, rect, DensityUtil.dip2px(5), marginTopSize);
        // 距离上面间距
        marginTopSize += oneSizeHeight;
        // 循环向图片上添加文字
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                watermarkBitmap = drawTextToLeftTop(watermarkBitmap, list.get(i), pFont, rect, DensityUtil.dip2px(5), marginTopSize);
            } else {
                marginTopSize += oneSizeHeight;
                watermarkBitmap = drawTextToLeftTop(watermarkBitmap, list.get(i), pFont, rect, computeMaxStringWidth(new String[]{"施工部位："}, pFont) + DensityUtil.dip2px(5), marginTopSize);
            }
        }
        marginTopSize += oneSizeHeight;
        String userName = (String) SpUtil.get(context, "UserName", "");
        watermarkBitmap = drawTextToLeftTop(watermarkBitmap, "上传人员：" + userName + " " + createTime, pFont, rect, DensityUtil.dip2px(5), marginTopSize);

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
     *
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text, Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        return drawTextToBitmap(bitmap, text, paint, paddingLeft, paddingTop + bounds.height());
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * 截取level_1
     *
     * @param level_1
     * @param lenSize
     */
    private static void rows(String level_1, int lenSize, int oneSizeWidth, Paint pFont) {
        int len = level_1.length();
        byteLen = 0;
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                txt = level_1.substring(0, i);
            } else {
                txt = level_1.substring(i - 1, i);
            }
            m = p.matcher(txt);
            if (m.matches()) {
                byteLen += oneSizeWidth;
            } else {
                String[] strNum = new String[]{txt};
                byteLen += (computeMaxStringWidth(strNum, pFont));
            }
            if (byteLen > lenSize) {
                list.add(level_1.substring(0, i - 1));
                level_1 = level_1.substring(i - 1);
                break;
            }
        }

        if (computeMaxStringWidth(new String[]{level_1}, pFont) > lenSize) {
            rows(level_1, lenSize, oneSizeWidth, pFont);
        } else {
            list.add(level_1);
        }
    }

    /**
     * 获取字符串所占像素（px）
     *
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
