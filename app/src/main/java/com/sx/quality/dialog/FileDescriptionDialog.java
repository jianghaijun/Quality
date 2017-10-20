package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.sx.quality.activity.R;
import com.sx.quality.listener.FileInfoListener;
import com.sx.quality.utils.ToastUtil;

/**
 * @author Administrator
 * @time 2017/10/11 0011 20:38
 */

public class FileDescriptionDialog extends Dialog implements View.OnClickListener {
    private EditText edtName1, edtName2, edtName3, edtName4;
    private Context mContext;

    private FileInfoListener fileInfoListener;

    public FileDescriptionDialog(@NonNull Context mContext, FileInfoListener fileInfoListener) {
        super(mContext);
        this.mContext = mContext;
        this.fileInfoListener = fileInfoListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_description);

        edtName1 = (EditText) findViewById(R.id.edtName1);
        edtName2 = (EditText) findViewById(R.id.edtName2);
        edtName3 = (EditText) findViewById(R.id.edtName3);
        edtName4 = (EditText) findViewById(R.id.edtName4);

        Button btnQuery = (Button) findViewById(R.id.btnQuery);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnQuery.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 立即上传
            case R.id.btnQuery:
                if (edtName1.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name1!");
                } else if (edtName2.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name2!");
                } else if (edtName3.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name3!");
                } else if (edtName4.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name4!");
                } else {
                    dismiss();
                    fileInfoListener.fileInfo(edtName1.getText().toString().trim(), edtName1.getText().toString().trim(), true);
                }
                break;
            case R.id.btnCancel:
                if (edtName1.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name1!");
                } else if (edtName2.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name2!");
                } else if (edtName3.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name3!");
                } else if (edtName4.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入Name4!");
                } else {
                    dismiss();
                    fileInfoListener.fileInfo(edtName1.getText().toString().trim(), edtName1.getText().toString().trim(), false);
                }
                break;
        }
    }
}