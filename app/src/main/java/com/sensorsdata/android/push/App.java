package com.sensorsdata.android.push;

import android.app.Application;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;


/**
 * Created by yzk on 2019-12-02
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initJPush();
    }

    /**
     * 初始化极光推送 SDK
     */
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }
}
