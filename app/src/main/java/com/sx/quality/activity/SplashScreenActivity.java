package com.sx.quality.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 启动界面
 * Created by jack on 2017/10/10.
 */
public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // 闪屏的核心代码
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNextActivity();
            }
        }, 3000); // 启动动画持续3秒钟
    }

    /**
     * 启动下一界面
     */
    private void startNextActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }
}
