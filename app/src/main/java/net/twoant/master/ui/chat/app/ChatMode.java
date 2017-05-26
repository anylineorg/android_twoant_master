package net.twoant.master.ui.chat.app;

import android.support.v4.util.SimpleArrayMap;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.UserDao;
import net.twoant.master.ui.chat.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/2/26.
 * 交流中心 数据
 */

public class ChatMode {

    private UserDao mUserDao;
    private SimpleArrayMap<String, Object> mValueCache = new SimpleArrayMap<>();

    ChatMode() {
        PreferenceManager.init(ChatHelper.sApplicationContext);
    }

    boolean saveContactList(Collection<EaseUser> contactList) {
        UserDao dao = new UserDao(ChatHelper.sApplicationContext);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, EaseUser> getContactList() {
        UserInfoDiskHelper.getInstance().getUserInfoAndGroupCache();
        UserDao dao = new UserDao(ChatHelper.sApplicationContext);
        return dao.getContactList();
    }

    public void saveContact(EaseUser user) {
        UserDao dao = new UserDao(ChatHelper.sApplicationContext);
        dao.saveContact(user);
    }

    /**
     * save current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName() {
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgNotification(paramBoolean);
        mValueCache.put(ChatMode.Key.VIBRATE_AND_PLAY_TONE_ON, paramBoolean);
    }

    /**
     * 信息通知
     */
    public boolean getSettingMsgNotification() {
        Object val = mValueCache.get(ChatMode.Key.VIBRATE_AND_PLAY_TONE_ON);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgNotification();
            mValueCache.put(ChatMode.Key.VIBRATE_AND_PLAY_TONE_ON, val);
        }

        return (Boolean) val;
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSound(paramBoolean);
        mValueCache.put(ChatMode.Key.PLAY_TONE_ON, paramBoolean);
    }

    /**
     * 声音
     */
    public boolean getSettingMsgSound() {
        Object val = mValueCache.get(ChatMode.Key.PLAY_TONE_ON);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSound();
            mValueCache.put(ChatMode.Key.PLAY_TONE_ON, val);
        }

        return (Boolean) val;
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgVibrate(paramBoolean);
        mValueCache.put(ChatMode.Key.VIBRATE_ON, paramBoolean);
    }

    /**
     * 震动
     */
    public boolean getSettingMsgVibrate() {
        Object val = mValueCache.get(ChatMode.Key.VIBRATE_ON);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgVibrate();
            mValueCache.put(ChatMode.Key.VIBRATE_ON, val);
        }

        return (Boolean) val;
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.getInstance().setSettingMsgSpeaker(paramBoolean);
        mValueCache.put(ChatMode.Key.SPAKER_ON, paramBoolean);
    }

    /**
     * 使用扬声器
     */
    public boolean getSettingMsgSpeaker() {
        Object val = mValueCache.get(ChatMode.Key.SPAKER_ON);

        if (val == null) {
            val = PreferenceManager.getInstance().getSettingMsgSpeaker();
            mValueCache.put(ChatMode.Key.SPAKER_ON, val);
        }

        return (Boolean) val;
    }


    public void setDisabledGroups(List<String> groups) {
        if (mUserDao == null) {
            mUserDao = new UserDao(ChatHelper.sApplicationContext);
        }

        List<String> list = new ArrayList<String>();
        list.addAll(groups);
        for (int i = 0; i < list.size(); i++) {
            if (EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))) {
                list.remove(i);
                i--;
            }
        }

        mUserDao.setDisabledGroups(list);
        mValueCache.put(ChatMode.Key.DISABLED_GROUPS, list);
    }

    public List<String> getDisabledGroups() {
        Object val = mValueCache.get(ChatMode.Key.DISABLED_GROUPS);

        if (mUserDao == null) {
            mUserDao = new UserDao(ChatHelper.sApplicationContext);
        }

        if (val == null) {
            val = mUserDao.getDisabledGroups();
            mValueCache.put(ChatMode.Key.DISABLED_GROUPS, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setDisabledIds(List<String> ids) {
        if (mUserDao == null) {
            mUserDao = new UserDao(ChatHelper.sApplicationContext);
        }

        mUserDao.setDisabledIds(ids);
        mValueCache.put(ChatMode.Key.DISABLED_IDS, ids);
    }

    /**
     * 禁用、黑名单
     */
    List<String> getDisabledIds() {
        Object val = mValueCache.get(ChatMode.Key.DISABLED_IDS);

        if (mUserDao == null) {
            mUserDao = new UserDao(ChatHelper.sApplicationContext);
        }

        if (val == null) {
            val = mUserDao.getDisabledIds();
            mValueCache.put(ChatMode.Key.DISABLED_IDS, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }

    public void setGroupsSynced(boolean synced) {
        PreferenceManager.getInstance().setGroupsSynced(synced);
    }

    public boolean isGroupsSynced() {
        return PreferenceManager.getInstance().isGroupsSynced();
    }

    public void setContactSynced(boolean synced) {
        PreferenceManager.getInstance().setContactSynced(synced);
    }

    public boolean isContactSynced() {
        return PreferenceManager.getInstance().isContactSynced();
    }

    public void setBlacklistSynced(boolean synced) {
        PreferenceManager.getInstance().setBlacklistSynced(synced);
    }

    public boolean isBacklistSynced() {
        return PreferenceManager.getInstance().isBacklistSynced();
    }

    public void allowChatroomOwnerLeave(boolean value) {
        PreferenceManager.getInstance().setSettingAllowChatroomOwnerLeave(value);
    }

    public boolean isChatroomOwnerLeaveAllowed() {
        return PreferenceManager.getInstance().getSettingAllowChatroomOwnerLeave();
    }

    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.getInstance().setDeleteMessagesAsExitGroup(value);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.getInstance().isDeleteMessagesAsExitGroup();
    }

    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.getInstance().setAutoAcceptGroupInvitation(value);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.getInstance().isAutoAcceptGroupInvitation();
    }


    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.getInstance().setAdaptiveVideoEncode(value);
    }

    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.getInstance().isAdaptiveVideoEncode();
    }

    public void setRestServer(String restServer) {
        PreferenceManager.getInstance().setRestServer(restServer);
    }

    public String getRestServer() {
        return PreferenceManager.getInstance().getRestServer();
    }

    public void setIMServer(String imServer) {
        PreferenceManager.getInstance().setIMServer(imServer);
    }

    public String getIMServer() {
        return PreferenceManager.getInstance().getIMServer();
    }

    public void enableCustomServer(boolean enable) {
        PreferenceManager.getInstance().enableCustomServer(enable);
    }

    public boolean isCustomServerEnable() {
        return PreferenceManager.getInstance().isCustomServerEnable();
    }

    /**
     * true 自动接受邀请， FALSE 手动接受
     */
    boolean getAutoAcceptInvitation() {
        return false;
    }

    /**
     * true 需要确认已读状态
     */
    boolean getRequireDeliveryAck() {
        return false;
    }

    interface Key {
        String VIBRATE_AND_PLAY_TONE_ON = "v_a_p_t_o";
        String VIBRATE_ON = "v_o";
        String PLAY_TONE_ON = "p_t_o";
        String SPAKER_ON = "s_o";
        String DISABLED_GROUPS = "d_g";
        String DISABLED_IDS = "d_i_s";
    }
}
