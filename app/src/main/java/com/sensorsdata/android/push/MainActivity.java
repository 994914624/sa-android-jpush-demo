package com.sensorsdata.android.push;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.huawei.hms.support.api.push.PushReceiver;
import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.service.PluginHuaweiPlatformsReceiver;


public class MainActivity extends AppCompatActivity {

    TextView tvHuawei;
    TextView tvJPush;
    TextView tvXiaomi;
    TextView tvMeizu;
    TextView tvOPPO;
    TextView tvVivo;
    private PushReceiver huaweiReceiver = new PluginHuaweiPlatformsReceiver() {
        @Override
        public void onToken(Context context, String s, Bundle bundle) {
            tvHuawei.setText(String.format("华为：%s", s));
            Log.i("--- 华为推送 ID ---", "" + s);
            super.onToken(context, s, bundle);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        initView();
        registerReceiver(huaweiReceiver, new IntentFilter("com.huawei.android.push.intent.REGISTRATION"));
    }

    private void initView() {
        tvJPush = findViewById(R.id.tv_jpush);//   集成测试 OK
        tvHuawei = findViewById(R.id.tv_huawei);// 集成测试 OK (过滤日志 TAG： ThirdPushManager)
        tvXiaomi = findViewById(R.id.tv_xiaomi);// 集成测试 OK (过滤日志 TAG： ThirdPushManager)
        tvMeizu = findViewById(R.id.tv_meizu);//   集成测试 OK (过滤日志 TAG： ThirdPushManager)
        tvOPPO = findViewById(R.id.tv_oppo);//     集成测试 OK (过滤日志 TAG： ThirdPushManager)
        // TODO vivo
        tvVivo = findViewById(R.id.tv_vivo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvJPush.setText(String.format("极光推送：%s", JPushInterface.getRegistrationID(this)));
        tvXiaomi.setText(String.format("小米：%s", MiPushClient.getRegId(this)));
        tvMeizu.setText(String.format("魅族：%s", com.meizu.cloud.pushsdk.PushManager.getPushId(this)));
        tvOPPO.setText(String.format("OPPO：%s", com.heytap.mcssdk.PushManager.getInstance().getRegisterID()));

        Log.e("--- 极光推送 ID ---", String.format("：%s", JPushInterface.getRegistrationID(this)));
        Log.e("--- 小米推送 ID ---", String.format("：%s", MiPushClient.getRegId(this)));
        Log.e("--- 魅族推送 ID ---", String.format("：%s", com.meizu.cloud.pushsdk.PushManager.getPushId(this)));
        Log.e("--- OPPO 推送 ID ---", String.format("：%s", com.heytap.mcssdk.PushManager.getInstance().getRegisterID()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(huaweiReceiver);
    }

    /**
     * OPPO 推送，开发者必须自己创建通知通道
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = getString(R.string.notification_channel_name);
            NotificationChannel channel = new NotificationChannel("001", channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("通道001的描述");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
