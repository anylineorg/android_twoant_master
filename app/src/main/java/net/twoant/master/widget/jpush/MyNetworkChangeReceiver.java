package net.twoant.master.widget.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.NetworkUtils;

/**
 * Created by S_Y_H on 2016/12/11.
 * 监听网络改变
 * TODO Android 7.0后这种广播已经不能用了。
 */

public class MyNetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtils.initConnectivityManager(context);
        if (NetworkUtils.isNetworkConnected()) {
            if (AiSouAppInfoModel.getInstance() != null && AiSouAppInfoModel.getInstance().getHasJPushFailTags()) {
                HandlePushSever.startRegisterTag(context, AiSouAppInfoModel.getInstance().getJPushTags());
            }
        }
    }
}
