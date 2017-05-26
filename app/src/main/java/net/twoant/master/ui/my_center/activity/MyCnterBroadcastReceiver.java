package net.twoant.master.ui.my_center.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by DZY on 2017/1/2.
 * 佛祖保佑   永无BUG
 */

public class MyCnterBroadcastReceiver   extends BroadcastReceiver {
    public final static String REQUEST_REFRESH = "request_refresh";
    private UpdateUIListenner updateUIListenner;

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("key");
        updateUIListenner.UpdateUI(key);
        String action;
        if (null != intent && (action = intent.getAction()) != null && REQUEST_REFRESH.equals(action)) {
//            requestNetForInformation();
        }
    }

    /**
     * 监听广播接收器的接收到的数据
     * @param updateUIListenner
     */
    public void SetOnUpdateUIListenner(UpdateUIListenner updateUIListenner) {
        this.updateUIListenner = updateUIListenner;

    }

}