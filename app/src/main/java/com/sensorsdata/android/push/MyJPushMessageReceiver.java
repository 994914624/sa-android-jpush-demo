package com.sensorsdata.android.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Map;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by yzk on 2019-12-05
 *
 * 极光推送
 *
 * 具体说明（开发者要做的）：
 * 一、埋点 "App 打开推送消息"
 *   1.1 需要在 onNotifyMessageOpened 接口中，调用 trackAppOpenNotification(notificationMessage.notificationExtras, notificationMessage.notificationTitle, notificationMessage.notificationContent);
 *   1.2 如果使用了厂商通道需要在处理厂商通道的 Activity 的 onCreate & onNewIntent 中处理 intent，解析出相应的参数，调用 trackAppOpenNotification(extras, title, content);
 *
 * 二、上报 "推送 ID"
 *   2.1 在 onRegister 接口中，调用 SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",s);
 *   2.2 在调用神策 SDK login 接口之后。调用 SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",JPushInterface.getRegistrationID(this));
 *
 * 三、处理神策 SF 推送的 "打开 App 消息"、"打开 URL 消息"、"自定义消息"
 *   3.1 需要在 onNotifyMessageOpened 接口中，调用 handleSFPushMessage(notificationMessage.notificationExtras); 同时在 handleSFPushMessage 方法内加上处理动作。
 *   3.2 如果使用了厂商通道，需要在处理厂商通道的 Activity 的 onCreate & onNewIntent 中处理 intent，解析出相应的参数，调用 handleSFPushMessage(extras); 同时在 handleSFPushMessage 方法内加上处理动作。
 *
 * 具体接入过程中，如果有什么疑问，请及时在微信群里提出来！！！
 */

public class MyJPushMessageReceiver extends JPushMessageReceiver {

    private static final String TAG = "MyJPushMessageReceiver";

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        Log.i(TAG, "-------- onRegister ------: " + s);
        // TODO 上报极光 "推送 ID"
        SensorsDataAPI.sharedInstance().profilePushId("jiguang_id", s);
        // TODO 注意在调用神策 SDK login 接口后，也需要调用 profilePushId 接口上报极光 "推送 ID"
        //SensorsDataAPI.sharedInstance().profilePushId("jiguang_id",JPushInterface.getRegistrationID(this));
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
        if(notificationMessage ==null)return;
        Log.i(TAG, "-------- onNotifyMessageOpened ------: " + notificationMessage.toString());
        // TODO 处理神策 SF 推送的 "打开 App"、"打开 URL 消息"、"自定义消息" 的动作
        handleSFPushMessage(notificationMessage.notificationExtras);
        // TODO 埋点 App 打开推送消息 事件
        trackAppOpenNotification(notificationMessage.notificationExtras, notificationMessage.notificationTitle, notificationMessage.notificationContent);
    }

    // ---------------------------------------- 下边是处理厂商通道消息示例代码 ----------------------------------------

    /**
     * TODO 如果使用了极光 VIP 的厂商通道（uri_activity/uri_action），需要在厂商消息打开的 Activity 中处理厂商通道消息。
     * 处理厂商通道消息的点击事件的 Activity。
     */
    public  class OpenClickActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // 处理厂商通道消息的点击
            handleOpenClick();
        }

        @Override
        protected void onNewIntent(Intent intent) {
            super.onNewIntent(intent);
            // 处理厂商通道消息的点击
            handleOpenClick();
        }

        /**
         * 处理厂商通道消息的点击。
         *
         * 华为通道消息数据：通过 getIntent().getData().toString(); 获取。
         * 小米、vivo、OPPO、FCM 通道消息数据 ：通过 getIntent().getExtras().getString("JMessageExtra") 获取。
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
                // 小米、vivo、OPPO、FCM 通道消息数据(魅族会回调 onNotifyMessageOpened )
                if (TextUtils.isEmpty(pushData) && getIntent().getExtras() != null) {
                    pushData = getIntent().getExtras().getString("JMessageExtra");
                }
                if (TextUtils.isEmpty(pushData)) {
                    return;
                }
                JSONObject jsonObject = new JSONObject(pushData);
                // 推送消息标题
                String title = jsonObject.optString("n_title");
                // 推送消息内容
                String content = jsonObject.optString("n_content");
                // 推送消息附加字段
                String extras = jsonObject.optString("n_extras");
                // TODO 处理神策智能运营推送的 "打开 App"、"打开 URL"、"自定义消息" 动作
                handleSFPushMessage(extras);
                // TODO 埋点 App 打开推送消息 事件
                trackAppOpenNotification(extras, title, content);
                Log.e("TODO", String.format("--- 通知标题：%s。 ---通知内容：%s。 ---附加字段：%s。", title, content, extras));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ---------------------------------------- 下边是 神策智能运营 推送埋点示例代码 ----------------------------------------

    /**
     * 埋点 App 打开推送消息
     * <p>
     * 事件名：AppOpenNotification
     *
     * @param notificationExtras  推送消息的 extras（参数类型只能传 String 或 Map<String,String>）
     * @param notificationTitle   推送消息的标题
     * @param notificationContent 推送消息的内容
     */
    public static void trackAppOpenNotification(Object notificationExtras, String notificationTitle, String notificationContent) {
        try {
            JSONObject properties = new JSONObject();
            // 推送消息的标题
            properties.put("$sf_msg_title", notificationTitle);
            // 推送消息的内容
            properties.put("$sf_msg_content", notificationContent);
            try {
                String sfData = null;
                if (notificationExtras != null) {
                    if (notificationExtras instanceof String) {
                        sfData = new JSONObject((String) notificationExtras).optString("sf_data");
                    } else if (notificationExtras instanceof Map) {
                        sfData = new JSONObject((Map) notificationExtras).optString("sf_data");
                    }
                }
                if (!TextUtils.isEmpty(sfData)) {
                    JSONObject sfJson = new JSONObject(sfData);
                    // 推送消息中 SF 的内容
                    properties.put("$sf_msg_id", sfJson.optString("sf_msg_id", null));
                    properties.put("$sf_plan_id", sfJson.optString("sf_plan_id", null));
                    if (!"null".equals(sfJson.optString("sf_audience_id", null))) {
                        properties.put("$sf_audience_id", sfJson.optString("sf_audience_id", null));
                    }
                    properties.put("$sf_link_url", sfJson.optString("sf_link_url", null));
                    properties.put("$sf_plan_name", sfJson.optString("sf_plan_name", null));
                    properties.put("$sf_plan_strategy_id", sfJson.optString("sf_plan_strategy_id", null));
                    JSONObject customized = sfJson.optJSONObject("customized");
                    if (customized != null) {
                        Iterator<String> iterator = customized.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            properties.put(key, customized.opt(key));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 埋点 "App 打开推送消息" 事件
            SensorsDataAPI.sharedInstance().track("AppOpenNotification", properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理神策 SF 推送的 "打开 App 消息"、"打开 URL 消息"、"自定义消息" 动作
     * TODO 此方法只是解析了神策智能运营推送消息，具体的业务逻辑需要开发者加上！！！
     *
     * @param notificationExtras 推送消息的 extra
     */
    public static void handleSFPushMessage(Object notificationExtras) {
        try {
            String sfData = null;
            if (notificationExtras != null) {
                if (notificationExtras instanceof String) {
                    sfData = new JSONObject((String) notificationExtras).optString("sf_data");
                } else if (notificationExtras instanceof Map) {
                    sfData = new JSONObject((Map) notificationExtras).optString("sf_data");
                }
            }
            if (!TextUtils.isEmpty(sfData)) {
                JSONObject sfJson = new JSONObject(sfData);
                if ("OPEN_APP".equals(sfJson.optString("sf_landing_type"))) {
                    // TODO 处理打开 App 消息，--> 请启动 App
                    Log.e("TODO", "-- 请启动 App --");
                } else if ("LINK".equals(sfJson.optString("sf_landing_type"))) {
                    String url = sfJson.optString("sf_link_url");
                    if (!TextUtils.isEmpty(url)) {
                        // TODO 处理打开 URL 消息，--> 请处理 URL
                        Log.e("TODO", "-- 请处理 URL --: " + url);
                    }
                } else if ("CUSTOMIZED".equals(sfJson.optString("sf_landing_type"))) {
                    JSONObject custom = sfJson.optJSONObject("customized");
                    if (custom != null) {
                        // TODO 处理自定义消息
                        // 如果你们已经有了根据附加字段跳转逻辑，此处无需处理。（因为神策智能运营发的推送消息会兼容极光控制台的 "附加字段"）
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

