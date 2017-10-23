package com.sx.quality.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sx.quality.utils.ToastUtil;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

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
 *       Created by dell on 2017/10/23 15:06
 */
public class UpdatePassWordActivity extends BaseActivity {
    @ViewInject(R.id.imgBtnLeft)
    private ImageView imgBtnLeft;
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    @ViewInject(R.id.edtOldPassWord)
    private EditText edtOldPassWord;
    @ViewInject(R.id.edtNewPassWord)
    private EditText edtNewPassWord;
    @ViewInject(R.id.edtQueryPassWord)
    private EditText edtQueryPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pass_word);

        x.view().inject(this);

        imgBtnLeft.setVisibility(View.VISIBLE);
        imgBtnLeft.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.back_btn));
        txtTitle.setText(getString(R.string.update_password));
    }

    @Event({ R.id.imgBtnLeft, R.id.btnQuery })
    private void onClick(View v){
        switch (v.getId()) {
            case R.id.imgBtnLeft:
                this.finish();
                break;
            case R.id.btnQuery:
                checkPassWord();
                break;
        }
    }

    /**
     * 密码校验
     */
    private void checkPassWord() {
        String oldPassWord = edtOldPassWord.getText().toString().trim();
        String newPassWord = edtNewPassWord.getText().toString().trim();
        String queryPassWord = edtQueryPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassWord)) {
            ToastUtil.showShort(this, getString(R.string.old_pswd_can_not_be_empty));
        } else if (TextUtils.isEmpty(newPassWord)) {
            ToastUtil.showShort(this, getString(R.string.new_pswd_can_not_be_empty));
        } else if (oldPassWord.equals(newPassWord)) {
            ToastUtil.showShort(this, getString(R.string.new_pswd_can_not_be_the_same_as_old_pswd));
        } else if (!newPassWord.equals(queryPassWord)) {
            ToastUtil.showShort(this, getString(R.string.two_pswd_are_not_consistent));
        } else {

        }
    }
}
