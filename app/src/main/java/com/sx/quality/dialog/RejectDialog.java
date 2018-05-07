package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sx.quality.activity.R;
import com.sx.quality.listener.ChoiceListener;
import com.sx.quality.listener.ReportListener;

/**
 * @author Administrator
 * @time 2017/10/11 0011 20:38
 */

public class RejectDialog extends Dialog implements View.OnClickListener {
    private ReportListener reportListener;
    private EditText edtContext;


    private String sTitle, sLeftText, sRightText;

    /**
     * @param context
     * @param reportListener
     * @param sTitle		提示框标题
     * @param sLeftText		左侧白色按钮文本
     * @param sRightText	右侧黄色按钮文本
     *
     */
    public RejectDialog(@NonNull Context context, ReportListener reportListener, String sTitle, String sLeftText, String sRightText) {
        super(context);
        this.sTitle = sTitle;
        this.sLeftText = sLeftText;
        this.sRightText = sRightText;
        this.reportListener = reportListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rejcet);

        Button btnRight = (Button) findViewById(R.id.query_setting_btn);
        Button btnLeft = (Button) findViewById(R.id.close_setting_btn);

        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        edtContext = (EditText) findViewById(R.id.edtContext);

        txtTitle.setText(sTitle);
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
                reportListener.returnUserId(edtContext.getText().toString().trim());
                break;
            // 左侧
            case R.id.close_setting_btn:
                dismiss();
                break;
        }
    }
}