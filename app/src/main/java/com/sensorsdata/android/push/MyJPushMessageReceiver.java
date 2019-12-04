package com.sensorsdata.android.push;

import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by yzk on 2019-12-02
 */

public class MyJPushMessageReceiver extends JPushMessageReceiver {

    private static final String TAG = "MyJPushMessageReceiver";

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.i(TAG, "-------- onRegister ------: " + s);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        Log.i(TAG, "-------- onNotifyMessageOpened ------: " + notificationMessage.toString());
    }
}
