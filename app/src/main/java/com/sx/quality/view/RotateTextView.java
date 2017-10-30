package com.sx.quality.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;

import com.sx.quality.activity.R;

/**
 * Create dell By 2017/10/30 16:10
 */

public class RotateTextView extends android.support.v7.widget.AppCompatTextView {
    private static final int DEFAULT_DEGREES = 0;
    private int mDegrees;

    public RotateTextView(Context context) {
        super(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textViewStyle);
        this.setGravity(Gravity.CENTER);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WatermarkTextView);
        mDegrees = a.getDimensionPixelSize(R.styleable.WatermarkTextView_degree, DEFAULT_DEGREES);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setDegrees(int degrees) {
        mDegrees = degrees;
    }
}
