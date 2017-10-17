package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.listener.ChoiceListener;

/**
 * @author Administrator
 * @time 2017/10/11 0011 20:38
 */

public class PromptDialog extends Dialog implements View.OnClickListener {
    private ChoiceListener choiceListener;
    private Button btnLeft, btnRight;
    private TextView txtTitle, txtContext;
    private String sTitle, sContext, sLeftText, sRightText;

    /**
     * @param context
     * @param choiceListener
     * @param sTitle		提示框标题
     * @param sContext		提示内容
     * @param sLeftText		左侧白色按钮文本
     * @param sRightText	右侧黄色按钮文本
     *
     */
    public PromptDialog(@NonNull Context context, ChoiceListener choiceListener, String sTitle, String sContext, String sLeftText, String sRightText) {
        super(context);
        this.sTitle = sTitle;
        this.sContext = sContext;
        this.sLeftText = sLeftText;
        this.sRightText = sRightText;
        this.choiceListener = choiceListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_prompt);

        btnRight = (Button) findViewById(R.id.query_setting_btn);
        btnLeft = (Button) findViewById(R.id.close_setting_btn);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtContext = (TextView) findViewById(R.id.txtContext);

        txtTitle.setText(sTitle);
        txtContext.setText(sContext);
        btnLeft.setText(sLeftText);
        btnRight.setText(sRightText);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 右侧
            case R.id.query_setting_btn:
                dismiss();
                choiceListener.returnTrueOrFalse(true);
                break;
            // 左侧
            case R.id.close_setting_btn:
                dismiss();
                choiceListener.returnTrueOrFalse(false);
                break;
        }
    }
}