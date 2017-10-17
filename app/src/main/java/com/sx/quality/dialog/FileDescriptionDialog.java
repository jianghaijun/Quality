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
    private EditText edtFileName;
    private EditText edtFileDescription;
    private Button btnQuery, btnCancel;
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

        edtFileName = (EditText) findViewById(R.id.edtFileName);
        edtFileDescription = (EditText) findViewById(R.id.edtFileDescription);
        btnQuery = (Button) findViewById(R.id.btnQuery);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnQuery.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 立即上传
            case R.id.btnQuery:
                if (edtFileName.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入文件名称!");
                } else if (edtFileDescription.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入描述文件信息!");
                } else {
                    dismiss();
                    fileInfoListener.fileInfo(edtFileName.getText().toString().trim(), edtFileDescription.getText().toString().trim(), true);
                }
                break;
            case R.id.btnCancel:
                if (edtFileName.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入文件名称!");
                } else if (edtFileDescription.getText().toString().trim().equals("")) {
                    ToastUtil.showShort(mContext, "请输入描述文件信息!");
                } else {
                    dismiss();
                    fileInfoListener.fileInfo(edtFileName.getText().toString().trim(), edtFileDescription.getText().toString().trim(), false);
                }
                break;
        }
    }
}