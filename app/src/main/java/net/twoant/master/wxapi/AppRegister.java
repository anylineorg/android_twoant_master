package net.twoant.master.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.twoant.master.api.AppConfig;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(AppConfig.APP_ID);
	}
}
