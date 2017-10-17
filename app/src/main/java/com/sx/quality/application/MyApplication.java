package com.sx.quality.application;


import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import org.xutils.x;

/**
 *
 * Created by jack on 2017/10/10.
 */

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false);
        LitePal.initialize(this);
    }
}
