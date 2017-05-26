package net.twoant.master.widget.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.ui.main.activity.PushInfoDetailActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by S_Y_H on 2016/12/10.
 * 接受 极光推送的消息
 * 主要用途：
 * 1) 用户点击推送收打开的界面
 * 2) 接收自定义消息
 */

public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    /**
     * 入驻
     */
    private final static int ENTERED = 1;

    /**
     * 消费
     */
    private final static int CONSUMPTION = 2;

    /**
     * 使用活动
     */
    private final static int USE_ACTIVITY = 3;

    /**
     * 委托店铺
     */
    private final static int ENTRUST_MERCHANT = 4;


    /**
     * 1入驻（）shopId（逗号分割）跳的商家管理
     * 2消费，购买人名，商品名，价格
     * 3使用活动 ： 购买人的名字，价格，活动名，次数
     * 4委托管理店铺 ： 法人名，店铺名，管理的shopId（新添加的，可能有多个，用逗号拼接），shop电话号码 ；跳我管理的管理
     * message : 1,2,3,4
     * data : extras
     */



    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//        LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (null != AiSouAppInfoModel.getInstance() && TextUtils.isEmpty(AiSouAppInfoModel.getInstance().getUID())) {
            HandlePushSever.cleanAliasAndTag(context);
            return;
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
      /*      String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...*/
//          商家入驻、认证消息走这,购买活动走这
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
         /*   bundle.getString(JPushInterface.EXTRA_APP_KEY);
            String tite = bundle.getString(JPushInterface.EXTRA_TITLE);
            String messe = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
            String fie = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String tit = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String contet = bundle.getString(JPushInterface.EXTRA_ALERT);
            String tile = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " +
                    bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
*/
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtils.d(TAG, "[MyReceiver]接收到推送下来的通知");
//            Bundle[{cn.jpush.android.ALERT=活动参与成功 计次活动 商家:八颗牙齿晒太阳,
// cn.jpush.android.EXTRA={},
// cn.jpush.android.NOTIFICATION_ID=189989385,
// cn.jpush.android.NOTIFICATION_CONTENT_TITLE=活动参与成功,
// cn.jpush.android.MSG_ID=5168546311}]
//            JPushInterface.EXTA_ALERT;第二行内容
//            JPushInterface.EXTRA_NOTIFICATION_TITLE;
//            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//            LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");

            //打开自定义的Activity
//            MainActivity.startActivity(context);
            Intent i = new Intent(context, PushInfoDetailActivity.class);
            i.putExtras(bundle);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
          //  LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
          //  boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
           // LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
          //  LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }
/*
    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }

//            }
//            context.sendBroadcast(msgIntent);
//        }
    }*/
}
