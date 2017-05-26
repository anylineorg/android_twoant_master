package net.twoant.master.ui.chat.app;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.db.UserDao;
import net.twoant.master.ui.chat.domain.InviteMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/2/28.
 * 联系人监听器，监听联系变化，包括添加好友的申请，对方删除好友的通知, 对方同意好友请求，对方拒绝好友请求。
 */

class AiSouEMContactListener implements EMContactListener {

    private UserDao mUserDao;
    private LocalBroadcastManager mLocalBroadcastManager;
    private InviteMessageDao mInviteMessageDao;
    private EaseUI mEaseUI;

    AiSouEMContactListener(Context context, EaseUI easeUI) {
        this.mUserDao = new UserDao(context);
        this.mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.mEaseUI = easeUI;
        this.mInviteMessageDao = new InviteMessageDao(context);
    }

    @Override
    public void onContactAdded(String username) {

        Map<String, EaseUser> localUsers = ChatHelper.getContactList();
        EaseUser user = new EaseUser(username);
        if (!localUsers.containsKey(username)) {
            UserInfoDiskHelper.getInstance().syncUpdateUserInfo(user, true);
            mUserDao.saveContact(user);
        }
        localUsers.put(username, user);

        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
    }

    @Override
    public void onContactDeleted(String username) {
        Map<String, EaseUser> localUsers = ChatHelper.getContactList();
        localUsers.remove(username);
        mUserDao.deleteContact(username);
        mInviteMessageDao.deleteMessage(username);

        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
    }

    @Override
    public void onContactInvited(String username, String reason) {
        List<InviteMessage> msgs = mInviteMessageDao.getMessagesList();

        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                mInviteMessageDao.deleteMessage(username);
            }
        }
        // save invitation as message
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());
        msg.setReason(reason);
        // set invitation status
        msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
        AiSouGroupChangeListener.notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
    }

    @Override
    public void onContactAgreed(String username) {
        List<InviteMessage> msgs = mInviteMessageDao.getMessagesList();
        for (InviteMessage inviteMessage : msgs) {
            if (inviteMessage.getFrom().equals(username)) {
                return;
            }
        }
        // save invitation as message
        InviteMessage msg = new InviteMessage();
        msg.setFrom(username);
        msg.setTime(System.currentTimeMillis());
        msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
        AiSouGroupChangeListener.notifyNewInviteMessage(msg, mInviteMessageDao, mEaseUI);
        mLocalBroadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
    }

    @Override
    public void onContactRefused(String username) {
        //// TODO: 2017/2/28 请求被拒绝 
    }
}
