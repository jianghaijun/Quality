package com.sx.quality.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.sx.quality.activity.R;
import com.sx.quality.utils.Constants;

import org.xutils.common.util.DensityUtil;


public class FinderVerticalView extends View {
    // 上、下、左、右矩阵
    private Rect topRect = new Rect();
    private Rect bottomRect = new Rect();
    private Rect rightRect = new Rect();
    private Rect leftRect = new Rect();
    private Rect middleRect = new Rect();

    public static boolean bFocused = false;
    /**
     * finderMaskPaint
     */
    private Paint finderMaskPaint;
    /**
     * measureedWidth
     */
    private int measureedWidth;
    /**
     * measureedHeight
     */
    private int measureedHeight;

    /***
     * 四个角的线
     */
    private Paint linerPaint;
    /**
     * 画框
     */
    private Paint framePaint;

    private Paint idcardPaint;
    /**
     * 上边半透明矩形框的高
     */
    private int topHeight;
    /**
     * 中间对焦框的高
     */
    private int middleHeight;
    /**
     * 左边对焦框的宽
     */
    private int leftWidth;

    public FinderVerticalView(Context context) {
        super(context);
        init(context);
    }

    public FinderVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(leftRect, finderMaskPaint);
        canvas.drawRect(topRect, finderMaskPaint);
        canvas.drawRect(rightRect, finderMaskPaint);
        canvas.drawRect(bottomRect, finderMaskPaint);
        canvas.drawRect(middleRect, framePaint);
        if (bFocused) {
            linerPaint.setColor(Color.GREEN);//设置颜色
        } else {
            linerPaint.setColor(Color.GRAY);//设置颜色
        }

        /**左边线*/
        canvas.drawLine(50, 40, 50, DensityUtil.getScreenWidth() - 100, linerPaint);
        /**右边线*/
        canvas.drawLine((float) (DensityUtil.getScreenHeight()*0.8 - 50), 40, (float) (DensityUtil.getScreenHeight()*0.8 - 50), DensityUtil.getScreenWidth() - 100, linerPaint);
        /**上边线*/
        canvas.drawLine(50, 40, (float) (DensityUtil.getScreenHeight()*0.8 - 50), 40, linerPaint);
        /**下边线*/
        canvas.drawLine(50, DensityUtil.getScreenWidth() - 100, (float) (DensityUtil.getScreenHeight() * 0.8 - 50), DensityUtil.getScreenWidth() - 100, linerPaint);//左边线
    }

    private void init(Context context) {
        int finderMask = context.getResources().getColor(R.color.cover_color);
        /**
         * 创建四个半透明矩形框的画笔
         */
        finderMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        finderMaskPaint.setColor(finderMask);
        finderMaskPaint.setARGB(128, 30, 30, 30);
        /**
         * 创建中间对焦框的画笔
         */
        framePaint = new Paint();
        framePaint.setAntiAlias(true);// 抗锯齿
        framePaint.setDither(true);// 防抖动
        framePaint.setColor(Color.WHITE);//设置颜色
        framePaint.setStrokeWidth(0.8f);//设置线条大小
        framePaint.setStyle(Paint.Style.STROKE);// 实心
        /**
         * 创建四个角的画笔
         */
        linerPaint = new Paint();
        linerPaint.setAntiAlias(true);// 抗锯齿
        linerPaint.setDither(true);// 防抖动
        linerPaint.setColor(Color.GREEN);//设置颜色
        linerPaint.setStrokeWidth(Constants.FOCUS_FRAME_EIGHT);
        linerPaint.setStyle(Paint.Style.FILL);// 实心
        /**
         * 创建中间对焦框的画笔
         */
        idcardPaint = new Paint();
        idcardPaint.setAntiAlias(true);// 抗锯齿
        idcardPaint.setDither(true);// 防抖动
        idcardPaint.setColor(Color.GREEN);//设置颜色
        idcardPaint.setStrokeWidth(0.8f);//设置线条大小
        idcardPaint.setStyle(Paint.Style.STROKE);// 实心
    }

    public Rect getScanImageRect(int w, int h) {
        Rect rect = new Rect();
        double heightRate = h / ((double) measureedHeight);
        double widthRate = w / ((double) measureedWidth);
        rect.top = (int) (middleRect.left * widthRate);
        rect.bottom = (int) (middleRect.right * widthRate);
        rect.left = (int) (middleRect.top * heightRate);
        rect.right = (int) (middleRect.bottom * heightRate);
        return rect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获取宽度
         */
        measureedWidth = MeasureSpec.getSize(widthMeasureSpec);
        /**
         * 获取高度
         */
        measureedHeight = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * 获取中间对焦框的高度值
         */
        middleHeight = measureedHeight * Constants.FOCUS_FRAME_WIDE / Constants.FOCUS_FRAME_FIVE;
        /**
         * 获取上面半透明矩形的高度值
         */
        topHeight = (measureedHeight - middleHeight) / 2;
        /**
         * 获取左边半透明矩形的宽度值==对焦框的x起点坐标
         */
        leftWidth = (measureedWidth - (middleHeight * Constants.FOCUS_FRAME_HEIGHT / Constants.FOCUS_FRAME_THREE)) / Constants.FOCUS_FRAME_HEIGHT;
        /**
         * 中间矩形对焦框
         */
        middleRect.set(50, 49, (int) (DensityUtil.getScreenHeight()*0.8-50), DensityUtil.getScreenWidth()-101);
        /**
         * 上面半透明矩形框
         */
        //topRect.set(Constants.FOCUS_FRAME_ZERO, Constants.FOCUS_FRAME_ZERO, measureedWidth, topHeight);
        topRect.set(50, 0, (int) (DensityUtil.getScreenHeight()*0.8 -50), 50);
        /**
         * 左边半透明矩形框
         */
        //leftRect.set(Constants.FOCUS_FRAME_ZERO, topHeight, leftWidth, topHeight + middleHeight);
        leftRect.set(0, 0, 50, (int) (DensityUtil.getScreenHeight()*0.8));

        /**
         * 右边半透明矩形框
         */
        rightRect.set((int) (DensityUtil.getScreenHeight()*0.8 - 50), 0, (int) (DensityUtil.getScreenHeight() * 0.8), DensityUtil.getScreenWidth());
        /**
         * 下边半透明矩形框
         */
        bottomRect.set(50, DensityUtil.getScreenWidth(), (int) (DensityUtil.getScreenHeight() * 0.8-50), DensityUtil.getScreenWidth()-100);
    }
}
