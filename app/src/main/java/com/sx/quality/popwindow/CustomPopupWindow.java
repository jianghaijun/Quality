package com.sx.quality.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.sx.quality.activity.R;
import com.sx.quality.listener.ChoiceListener;

public class CustomPopupWindow extends PopupWindow {
    private Activity mActivity;
    private View mView;

    // 用于保存PopupWindows的宽度
    private int width;
    // 用于保存PopupWindows的高度
    private int height;

    private ChoiceListener isReported;

    public CustomPopupWindow(Activity mActivity, ChoiceListener isReported) {
        super();
        this.mActivity = mActivity;
        this.isReported = isReported;
        this.initPopupWindow();
    }

    /**
     * 初始化
     */
    private void initPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = inflater.inflate(R.layout.popwindow_reported, null);
        this.setContentView(mView);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable background = new ColorDrawable(0x4f000000);
        this.setBackgroundDrawable(background);
        this.mandatorDraw();
        mView.findViewById(R.id.txtReported).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReported.returnTrueOrFalse(true);
                dismiss();
            }
        });
        mView.findViewById(R.id.txtCancelReported).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReported.returnTrueOrFalse(false);
                dismiss();
            }
        });

    }

    private void mandatorDraw() {
        this.mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        this.width = this.mView.getMeasuredWidth();
        this.height = this.mView.getMeasuredHeight();
    }

    /**
     * 显示在控件下右方
     * @param parent
     */
    public void showAtDropDownRight(View parent) {
        if (parent.getVisibility() == View.GONE) {
            this.showAtLocation(parent, 0, 0, 0);
        } else {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0] + parent.getWidth() - this.getWidth(), location[1] + parent.getHeight());
        }
    }

    /**
     * 显示在控件的下左方
     * @param parent parent
     */
    public void showAtDropDownLeft(View parent) {
        if (parent.getVisibility() == View.GONE) {
            this.showAtLocation(parent, 0, 0, 0);
        } else {
            int[] location = new int[2];
            //获取在整个屏幕内的绝对坐标
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0], location[1] + parent.getHeight());
        }
    }

    /**
     * 显示在控件的下中方
     * @param parent parent
     */
    public void showAtDropDownCenter(View parent) {
        if (parent.getVisibility() == View.GONE) {
            this.showAtLocation(parent, 0, 0, 0);
        } else {
            int[] location = new int[2];
            //获取在整个屏幕内的绝对坐标
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, 0, location[0] / 2 + parent.getWidth() / 2 - this.width / 6, location[1] + parent.getHeight());
        }
    }

}
