package net.twoant.master.ui.chat.app;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;

import java.util.List;

/**
 * Created by S_Y_H on 2017/2/28.
 * 消息侦听接口，可以用来侦听消息接受情况，成功发送到对方手机后会有回执， 对方阅读了这条消息也会收到回执。
 * 发送消息过程中，消息的ID会发生改变，由最初本地生成的一个UUID，变更为服务器端生成的全局唯一的ID，
 * 这个ID在所有使用Hyphenate SDK的 设备上都是唯一的。 应用需要实现此接口来监听消息变更状态。
 * 请参考: EMChatManager.addMessageListener(EMMessageListener listener)
 */

class AiSouEMMessageListener implements EMMessageListener {

    private EaseUI mEaseUI;
    private LocalBroadcastManager mLocalBroadcastManager;

    AiSouEMMessageListener(Context context, EaseUI easeUI) {
        this.mEaseUI = easeUI;
        this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            // in background, do not refresh UI, notify it in notification bar
            if (!mEaseUI.hasForegroundActivies()) {
                mEaseUI.getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            //get message body
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            final String action = cmdMsgBody.action();//获取自定义action
            //red packet code : 处理红包回执透传消息
            if (!mEaseUI.hasForegroundActivies()) {
               /* if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                    RedPacketUtil.receiveRedPacketAckMessage(message);
                    mLocalBroadcastManager.sendBroadcast(new Intent(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION));
                }*/
            }
            //end of red packet code
            //获取扩展属性 此处省略
            //maybe you need get extension of your message
            //message.getStringAttribute("");
//            EMLog.d(TAG, String.format("Command：action:%s,message:%s", action, message.toString()));
        }
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> messages) {
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> message) {
    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }
}
