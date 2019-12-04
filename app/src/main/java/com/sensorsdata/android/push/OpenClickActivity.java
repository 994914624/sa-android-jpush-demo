package com.sensorsdata.android.push;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 处理厂商通道消息的点击事件的 Activity。
 */
public class OpenClickActivity extends AppCompatActivity {

    private static final String TAG = "OpenClickActivity";
    // 消息 id
    private static final String KEY_MSGID = "msg_id";
    // 该通知的下发通道(0为极光，1为小米，2为华为，3为魅族，4为OPPO，8为FCM)
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    // 通知标题
    private static final String KEY_TITLE = "n_title";
    // 通知内容
    private static final String KEY_CONTENT = "n_content";
    // 通知附加字段
    private static final String KEY_EXTRAS = "n_extras";

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView = new TextView(this);
        setContentView(mTextView);
        handleOpenClick();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    /**
     * 处理厂商通道消息的点击。
     * 当前启动配置的 Activity 都是使用 Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK 方式启动，
     * 只需要在 onCreate 中进行处理。
     *
     * 对于 SDK 版本 3.4.1：
     * 华为通道消息数据：通过 getIntent().getData().toString(); 获取。
     *
     * 小米、vivo、OPPO、FCM 通道消息数据 ：通过 getIntent().getExtras().getString("JMessageExtra") 获取。
     *
     * 魅族通道消息数据：通过 onNotifyMessageOpened 获取。
     */
    private void handleOpenClick() {
        try {
            Intent intent = getIntent();
            if (intent == null) {
                return;
            }
            String pushData = null;
            // 华为通道消息数据
            if (getIntent().getData() != null) {
                pushData = getIntent().getData().toString();
            }
            // 小米、vivo、OPPO、FCM 通道消息数据
            if (TextUtils.isEmpty(pushData) && getIntent().getExtras() != null) {
                pushData = getIntent().getExtras().getString("JMessageExtra");
            }



            Log.i(TAG, "--- 用户点击打开了通知 ---： " + pushData);

            if (TextUtils.isEmpty(pushData)) return;
            JSONObject jsonObject = new JSONObject(pushData);
            String msgId = jsonObject.optString(KEY_MSGID);
            byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
            // 推送消息标题
            String title = jsonObject.optString(KEY_TITLE);
            // 推送消息内容
            String content = jsonObject.optString(KEY_CONTENT);
            // 推送消息附加字段
            String extras = jsonObject.optString(KEY_EXTRAS);

            //上报点击事件
//            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parse jpush notification error");
        }
    }

    /**
     * 厂商通道类型
     */
    private String getPushSDKName(byte whichPushSDK) {
        String name;
        switch (whichPushSDK) {
            case 0:
                name = "jpush";
                break;
            case 1:
                name = "xiaomi";
                break;
            case 2:
                name = "huawei";
                break;
            case 3:
                name = "meizu";
                break;
            case 4:
                name = "oppo";
                break;
            case 8:
                name = "fcm";
                break;
            default:
                name = "jpush";
        }
        return name;
    }


}
