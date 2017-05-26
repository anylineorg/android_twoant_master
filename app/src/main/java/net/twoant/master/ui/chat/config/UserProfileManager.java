package net.twoant.master.ui.chat.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.utils.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserProfileManager {

    private final static String TAG = "UserProfileManager";

    /**
     * init flag: test if the sdk has been inited before, we don't need to init
     * again
     */
    private boolean sdkInited = false;

    private String mCurrentUser;
    /**
     * HuanXin sync contact nick and avatar listener
     */
    private List<ChatHelper.IDataSyncListener> syncContactInfosListeners;

    private boolean isSyncingContactInfosWithServer = false;

    private EaseUser mCurrentEaseUser;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        syncContactInfosListeners = new ArrayList<ChatHelper.IDataSyncListener>();
        sdkInited = true;
        return true;
    }

    public void addSyncContactInfoListener(ChatHelper.IDataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.add(listener);
        }
    }

    public void removeSyncContactInfoListener(ChatHelper.IDataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactInfosListeners.contains(listener)) {
            syncContactInfosListeners.remove(listener);
        }
    }

    public void notifyContactInfosSyncListener(boolean success) {
        for (ChatHelper.IDataSyncListener listener : syncContactInfosListeners) {
            listener.onSyncComplete(success);
        }
    }

    public boolean isSyncingContactInfoWithServer() {
        return isSyncingContactInfosWithServer;
    }

    public synchronized void reset() {
        isSyncingContactInfosWithServer = false;
        mCurrentEaseUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }

    public synchronized EaseUser getCurrentUserInfo() {
        if (mCurrentEaseUser == null) {
            String username = EMClient.getInstance().getCurrentUser();
            mCurrentEaseUser = new EaseUser(username);
            String nick = getCurrentUserNick();
            mCurrentEaseUser.setNick((nick != null) ? nick : username);
            String currentUserAvatar = getCurrentUserAvatar();
            if (TextUtils.isEmpty(currentUserAvatar) || !new File(currentUserAvatar).exists()) {
                EaseUser userInfo = UserInfoDiskHelper.getSyncUserInfo(username);
                if (null != userInfo) {
                    mCurrentEaseUser.setAvatarMd5(userInfo.getAvatarMd5());
                    mCurrentEaseUser.setAvatar(userInfo.getAvatar());
                } else {
                    EaseUser temp = mCurrentEaseUser;
                    mCurrentEaseUser = null;
                    return temp;
                }
            } else {
                mCurrentEaseUser.setAvatarMd5(getCurrentUserAvatarMd5());
                mCurrentEaseUser.setAvatar(currentUserAvatar);
            }
        }
        return mCurrentEaseUser;
    }

    /**
     * 同步任务，需开线程
     * 更新 当前登录的 用户信息
     */
    public synchronized boolean updateCurrentUserInfo(EaseUser nickname) {
        if (null == nickname) {
            Log.e(TAG, "updateCurrentUserInfo: update fail because null");
            return false;
        }

        String username = nickname.getUsername();

        if (!TextUtils.isEmpty(username)) {
            boolean isSuccess = UserInfoDiskHelper.getInstance().syncUpdateUserInfo(nickname, false);
            Log.d(TAG, "updateCurrentUserInfo: isSuccess = " + isSuccess);
            if (isSuccess) {
                setCurrentUserNick(nickname.getNickname());
                String avatar = nickname.getAvatar();
                if (null != avatar) {
                    if (!avatar.contains(UserInfoDiskHelper.sImgPath)) {
                        Log.e(TAG, "updateCurrentUserInfo: avatar not local");
                        avatar = BaseConfig.getCorrectImageUrl(avatar);
                    }

                    if (!avatar.endsWith(".0")) {
                        avatar += ".0";
                    }
                }
                setCurrentUserAvatar(avatar);
                setCurrentUserAvatarMd5(nickname.getAvatarMd5());
                mCurrentUser = username;
            }
            return isSuccess;
        }
        return false;
    }

    private void setCurrentUserNick(String nickname) {
        getCurrentUserInfo().setNick(nickname);
        EMClient.getInstance().updateCurrentUserNick(nickname);
        PreferenceManager.getInstance().setCurrentUserNick(nickname);
    }

    private void setCurrentUserAvatar(String avatar) {
        getCurrentUserInfo().setAvatar(avatar);
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
    }

    private String getCurrentUserNick() {
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }

    private String getCurrentUserAvatarMd5() {
        return PreferenceManager.getInstance().getCurrentUserAvatarMd5();
    }



    private void setCurrentUserAvatarMd5(String currentUserAvatarMd5) {
        getCurrentUserInfo().setAvatarMd5(currentUserAvatarMd5);
        PreferenceManager.getInstance().setCurrentUserAvatarMd5(currentUserAvatarMd5);
    }
}
