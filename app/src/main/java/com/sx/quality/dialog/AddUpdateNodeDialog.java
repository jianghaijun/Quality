package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.sx.quality.activity.R;
import com.sx.quality.utils.ToastUtil;

/**
 * Create dell By 2017/10/25 11:51
 */

public class AddUpdateNodeDialog extends Dialog implements View.OnClickListener {
    private EditText edtNodeName;
    private Context mContext;

    public AddUpdateNodeDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_update_node);

        edtNodeName = (EditText) findViewById(R.id.edtNodeName);

        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnQuery).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnQuery:
                String nodeName = edtNodeName.getText().toString().trim();
                if (TextUtils.isEmpty(nodeName)) {
                    ToastUtil.showShort(mContext, "");
                }
                break;
        }
    }
}
