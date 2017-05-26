package net.twoant.master.ui.other.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import net.twoant.master.common_utils.CommonUtil;

/**
 * Created by DZY on 2017/2/8.
 * 佛祖保佑   永无BUG
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public String smss;
    SmsImp smsImp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pduses = (Object[]) intent.getExtras().get("pdus");
        for (Object pdus : pduses) {
            byte[] pdusmessage = (byte[]) pdus;
            SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
            String content = sms.getMessageBody(); //短信内容
            smss = CommonUtil.getYzmFromSms(content);
            smsImp.onGetMsmCode(smss);
        }
    }

    public void setOnClickListener(SmsImp smsImp) {
        this.smsImp = smsImp;
    }
}
