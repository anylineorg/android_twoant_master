package net.twoant.master.ui.chat.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.controller.EaseUI;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.domain.InviteMessage;

import java.util.List;
import java.util.UUID;

/**
 * Created by S_Y_H on 2017/2/28.
 * 群组更改 监听
 */

class AiSouGroupChangeListener implements EMGroupChangeListener {

    private InviteMessageDao mInviteMessageDao;
    private LocalBroadcastManager mLocalBroadcastManager;
    private EaseUI mEaseUI;
    private Context mContext;

    AiSouGroupChangeListener(Context context, EaseUI easeUI) {
        mContext = context;
        mInviteMessageDao = new InviteMessageDao(context);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        mEaseUI = easeUI;
    }

    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

        mInviteMessageDao.deleteMessage(groupId);

        //收到群邀请
        InviteMessage msg = new InviteMessage();
        msg.setFrom(groupId);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(groupName);
        msg.setReason(reason);
        msg.setGroupInviter(inviter);
        msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION);
        notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onInvitationAccepted(String groupId, String invitee, String reason) {

        mInviteMessageDao.deleteMessage(groupId);

        boolean hasGroup = false;
        EMGroup emGroup = null;

        //获取内存中我的所有群组
        List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();

        if (null == allGroups) {
            return;
        }

        for (EMGroup group : allGroups) {
            if (group.getGroupId().equals(groupId)) {
                hasGroup = true;
                emGroup = group;
                break;
            }
        }
        if (!hasGroup)
            return;

        InviteMessage msg = new InviteMessage();
        msg.setFrom(groupId);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(emGroup.getGroupName());
        msg.setReason(reason);
        msg.setGroupInviter(invitee);
        msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED);
        notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {

        mInviteMessageDao.deleteMessage(groupId);

        List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();
        if (null == allGroups) {
            return;
        }

        EMGroup group = null;

        for (EMGroup e : allGroups) {
            if (e.getGroupId().equals(groupId)) {
                group = e;
                break;
            }
        }

        if (null == group) {
            return;
        }

        InviteMessage msg = new InviteMessage();
        msg.setFrom(groupId);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(group.getGroupName());
        msg.setReason(reason);
        msg.setGroupInviter(invitee);
        msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED);
        notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

        InviteMessage msg = new InviteMessage();
        msg.setFrom(applyer);
        msg.setTime(System.currentTimeMillis());
        msg.setGroupId(groupId);
        msg.setGroupName(groupName);
        msg.setReason(reason);
        msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
        notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onApplicationAccept(String groupId, String groupName, String accepter) {

        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        msg.setChatType(EMMessage.ChatType.GroupChat);
        msg.setFrom(accepter);
        msg.setTo(groupId);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.addBody(new EMTextMessageBody(accepter + " " + mContext.getString(net.twoant.master.R.string.Agreed_to_your_group_chat_application)));
        msg.setStatus(EMMessage.Status.SUCCESS);

        EMClient.getInstance().chatManager().saveMessage(msg);

        mEaseUI.getNotifier().vibrateAndPlayTone(msg);

        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    @Override
    public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
        // TODO: 2017/2/28 申请被拒绝
    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

        EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
        msg.setChatType(EMMessage.ChatType.GroupChat);
        msg.setFrom(inviter);
        msg.setTo(groupId);
        msg.setMsgId(UUID.randomUUID().toString());
        msg.addBody(new EMTextMessageBody(inviter + " " + mContext.getString(net.twoant.master.R.string.Invite_you_to_join_a_group_chat)));
        msg.setStatus(EMMessage.Status.SUCCESS);
        EMClient.getInstance().chatManager().saveMessage(msg);
        mEaseUI.getNotifier().vibrateAndPlayTone(msg);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_GROUP_CHANAGED));
    }

    /**
     * 保存并通知 新的邀请消息
     */
    static void notifyNewInviteMessage(InviteMessage msg, InviteMessageDao inviteMessageDao, @NonNull EaseUI easeUI) {
        if (inviteMessageDao == null) {
            if (null == AiSouAppInfoModel.getAppContext()) {
                return;
            }
            inviteMessageDao = new InviteMessageDao(AiSouAppInfoModel.getAppContext());
        }
        inviteMessageDao.saveMessage(msg);
        inviteMessageDao.saveUnreadMessageCount(1);
        easeUI.getNotifier().vibrateAndPlayTone(null);
    }
}
