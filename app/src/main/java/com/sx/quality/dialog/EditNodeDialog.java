package com.sx.quality.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.sx.quality.activity.R;
import com.sx.quality.listener.EditNodeListener;

/**
 *                     _ooOoo_
 *                    o8888888o
 *                    88" . "88
 *                    (| -_- |)
 *                    O\  =  /O
 *                 ____/`---'\____
 *               .'  \\|     |//  `.
 *              /  \\|||  :  |||//  \
 *             /  _||||| -:- |||||-  \
 *             |   | \\\  -  /// |   |
 *             | \_|  ''\---/''  |   |
 *             \  .-\__  `-`  ___/-. /
 *           ___`. .'  /--.--\  `. . __
 *        ."" '<  `.___\_<|>_/___.'  >'"".
 *       | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *       \  \ `-.   \_ __\ /__ _/   .-` /  /
 * ======`-.____`-.___\_____/___.-`____.-'======
 *                     `=---='
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * 			   佛祖保佑       永无BUG
 *       Created by dell on 2017/10/25 11:01
 */

public class EditNodeDialog extends Dialog implements View.OnClickListener{
    private EditNodeListener editNodeListener;

    public EditNodeDialog(@NonNull Context context, EditNodeListener editNodeListener) {
        super(context);
        this.editNodeListener = editNodeListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_node);

        findViewById(R.id.txtAddNode).setOnClickListener(this);
        findViewById(R.id.txtDeleteNode).setOnClickListener(this);
        findViewById(R.id.txtUpDateNode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAddNode:
                editNodeListener.editNodeType(1);
                dismiss();
                break;
            case R.id.txtDeleteNode:
                editNodeListener.editNodeType(2);
                dismiss();
                break;
            case R.id.txtUpDateNode:
                editNodeListener.editNodeType(3);
                dismiss();
                break;
        }
    }
}
