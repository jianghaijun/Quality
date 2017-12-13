package com.sx.quality.application;


import android.app.Application;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.xutils.x;

/**
 *
 * Created by jack on 2017/10/10.
 */

public class MyApplication extends LitePalApplication {
    public static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
        LitePal.initialize(this);
        instance = this;
    }
}
