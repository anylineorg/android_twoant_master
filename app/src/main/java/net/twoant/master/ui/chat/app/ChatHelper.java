package net.twoant.master.ui.chat.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.GroupInfoBean;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.R;
import net.twoant.master.api.AppConfig;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.activity.VideoCallActivity;
import net.twoant.master.ui.chat.activity.VoiceCallActivity;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.config.UserProfileManager;
import net.twoant.master.ui.chat.db.ChatDBManager;
import net.twoant.master.ui.chat.domain.EmojiconExampleGroupData;
import net.twoant.master.ui.chat.receiver.CallReceiver;
import net.twoant.master.ui.chat.utils.PreferenceManager;
import net.twoant.master.ui.main.activity.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/2/21.
 * 环信的帮助类
 */

public final class ChatHelper implements EMConnectionListener {

    /**
     * 数据同步监听
     */
    public interface IDataSyncListener {
        /**
         * 同步完成
         *
         * @param success true：同步成功，false: 同步失败
         */
        void onSyncComplete(boolean success);
    }

    private final static String TAG = "ChatHelper";

    private static ChatHelper sChatHelper;
    private static ChatMode sChatMode;
    private static EaseUI sEaseUiInstance;
    static Context sApplicationContext;
    private static UserProfileManager sUserProfileManager;
    public static boolean isVoiceCalling = false;
    public static boolean isVideoCalling = false;

    private final Object F_SYNC_LOCK = new Object();
    /**
     * 联系人列表
     */
    private static Map<String, EaseUser> sContactList;

    /**
     * 群组
     */
    private List<IDataSyncListener> mSyncGroupsListeners;
    /**
     * 联系人
     */
    private List<IDataSyncListener> mSyncContactsListeners;
    /**
     * 黑名单
     */
    private List<IDataSyncListener> mSyncBlackListListeners;

    /**
     * 是否与服务器进行同步过群列表
     */
    private boolean isGroupsSyncedWithServer = false;
    private boolean isSyncingGroupsWithServer = false;


    private boolean isContactsSyncedWithServer = false;
    private boolean isSyncingContactsWithServer = false;

    private boolean isBlackListSyncedWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    /**
     * 是否 注册过了 群组、联系人监听
     */
    private boolean isInitListenerRegister;


    /**
     * 广播监听 语音
     */
    private CallReceiver mCallReceiver;


    static {
        sApplicationContext = AiSouAppInfoModel.getAppContext();
        sChatHelper = new ChatHelper();
        sChatMode = new ChatMode();
    }

    /**
     * @return 实例
     */
    public static ChatHelper getInstance() {
        return sChatHelper;
    }

    public static ChatMode getModel() {
        return sChatMode;
    }

    /**
     * 获取当前的用户名
     */
    public String getCurrentUserName() {
        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
        if (null != instance) {
            return instance.getUID();
        } else {
            return sChatMode.getCurrentUsernName();
        }
    }

    /**
     * 保存聊天信息
     */
    public static void saveContact(EaseUser user) {
        if (null != sContactList && null != user) {
            sContactList.put(user.getUsername(), user);
            sChatMode.saveContact(user);
        }
    }

    public EaseNotifier getEaseNotifier() {
        return sEaseUiInstance.getNotifier();
    }

    public void init(Context context) {
        if (null != context && !(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        sApplicationContext = context;
        sEaseUiInstance = EaseUI.getInstance();
        if (sEaseUiInstance.init(context, initChatOptions(sChatMode))) {
            EMClient.getInstance().setDebugMode(AppConfig.IS_DEBUG);
            setEaseUIProviders();
            PreferenceManager.init(context);
            getUserProfileManager().init(context);
            setGlobalListeners();
        }else{
            Log.e("e","rror");
        }
    }

    //------------- 监听配置 start      ------------------------------------------------------------------------------

    public void addSyncGroupListener(IDataSyncListener listener) {
        if (null == listener || null == mSyncGroupsListeners) {
            return;
        }
        if (!mSyncGroupsListeners.contains(listener)) {
            mSyncGroupsListeners.add(listener);
        }
    }

    public void removeSyncGroupListener(IDataSyncListener listener) {
        if (null == listener || null == mSyncGroupsListeners) {
            return;
        }
        if (mSyncGroupsListeners.contains(listener)) {
            mSyncGroupsListeners.remove(listener);
        }
    }

    public void addSyncContactListener(IDataSyncListener listener) {
        if (null == mSyncContactsListeners || null == listener) {
            return;
        }
        if (!mSyncContactsListeners.contains(listener)) {
            mSyncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(IDataSyncListener listener) {
        if (null == mSyncContactsListeners || null == listener) {
            return;
        }
        if (mSyncContactsListeners.contains(listener)) {
            mSyncContactsListeners.remove(listener);
        }
    }

    public void addSyncBlackListListener(IDataSyncListener listener) {
        if (null == mSyncBlackListListeners || null == listener) {
            return;
        }
        if (!mSyncBlackListListeners.contains(listener)) {
            mSyncBlackListListeners.add(listener);
        }
    }

    public void removeSyncBlackListListener(IDataSyncListener listener) {
        if (null == mSyncBlackListListeners || null == listener) {
            return;
        }
        if (mSyncBlackListListeners.contains(listener)) {
            mSyncBlackListListeners.remove(listener);
        }
    }

    public boolean isSyncingGroupsWithServer() {
        return isSyncingGroupsWithServer;
    }

    public boolean isSyncingContactsWithServer() {
        return isSyncingContactsWithServer;
    }

    public boolean isSyncingBlackListWithServer() {
        return isSyncingBlackListWithServer;
    }

    public boolean isGroupsSyncedWithServer() {
        return isGroupsSyncedWithServer;
    }

    public boolean isContactsSyncedWithServer() {
        return isContactsSyncedWithServer;
    }

    public boolean isBlackListSyncedWithServer() {
        return isBlackListSyncedWithServer;
    }

    //------------- 监听配置 end      ------------------------------------------------------------------------------


    //-------------  环信连接监听 start  ---------------------------------------------------------------------------
    @Override
    public void onConnected() {
        // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
        if (isGroupsSyncedWithServer && isContactsSyncedWithServer) {
            //已经同步
        } else {
            if (!isGroupsSyncedWithServer) {
                asyncFetchGroupsFromServer(null);
            }

            if (!isContactsSyncedWithServer) {
                asyncFetchContactsFromServer(null);
            }

            if (!isBlackListSyncedWithServer) {
                asyncFetchBlackListFromServer(null);
            }
        }
    }


    @Override
    public void onDisconnected(int error) {
        if (error == EMError.USER_REMOVED) {
            MainActivity.accountRemoved(sApplicationContext);
        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            MainActivity.accountConflict(sApplicationContext);
        }
    }

//-------------  环信连接监听 end  ---------------------------------------------------------------------------

    /**
     * 获取群列表 从环信的服务器
     */
    private void asyncFetchGroupsFromServer(final EMCallBack callback) {
        synchronized (F_SYNC_LOCK) {
            if (isSyncingGroupsWithServer) {
                return;
            } else {
                isSyncingGroupsWithServer = true;
            }

            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (!isLoggedIn()) {
                            isGroupsSyncedWithServer = false;
                            isSyncingGroupsWithServer = false;
                            notifyGroupSyncListeners(false);
                            return;
                        }

                        UserInfoDiskHelper.getInstance().syncGroupInfo(EMClient.getInstance().groupManager().getJoinedGroupsFromServer());

                        sChatMode.setGroupsSynced(true);

                        isGroupsSyncedWithServer = true;
                        isSyncingGroupsWithServer = false;

                        notifyGroupSyncListeners(true);

                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } catch (HyphenateException e) {
                        Log.e(TAG, "sync group: fail =" + e.toString());
                        sChatMode.setGroupsSynced(false);
                        isGroupsSyncedWithServer = false;
                        isSyncingGroupsWithServer = false;
                        notifyGroupSyncListeners(false);
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.toString());
                        }
                    }

                }
            });
        }
    }

    /**
     * 分发群列表与环信服务器的同步状态
     */
    private void notifyGroupSyncListeners(boolean state) {
        for (IDataSyncListener listener : mSyncGroupsListeners) {
            listener.onSyncComplete(state);
        }
    }

    /**
     * 从环信服务器上同步联系人数据
     */
    private void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback) {
        synchronized (F_SYNC_LOCK) {

            if (isSyncingContactsWithServer) {
                return;
            } else {
                isSyncingContactsWithServer = true;
            }

            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (!isLoggedIn()) {
                            isContactsSyncedWithServer = false;
                            isSyncingContactsWithServer = false;
                            notifyContactsSyncListener(false);
                            return;
                        }

                        List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                        ArrayMap<String, EaseUser> easeUserArrayMap = UserInfoDiskHelper.getInstance().syncUserInfo(userNames);
                        final Collection<EaseUser> values = easeUserArrayMap.values();

                        for (EaseUser username : values) {
                            EaseCommonUtils.setUserInitialLetter(username);
                        }

                        AiSouAppInfoModel aiSouAppInfoModel = AiSouAppInfoModel.getInstance();
                        if (null != aiSouAppInfoModel) {
                            easeUserArrayMap.remove(aiSouAppInfoModel.getUID());
                        }

                        //进行缓存
                        getContactList().clear();
                        getContactList().putAll(easeUserArrayMap);
                        //保存到数据库
                        sChatMode.saveContactList(values);
                        sChatMode.setContactSynced(true);

                        isContactsSyncedWithServer = true;
                        isSyncingContactsWithServer = false;

                        notifyContactsSyncListener(true);


                        getUserProfileManager().notifyContactInfosSyncListener(true);

                        if (callback != null) {
                            callback.onSuccess(userNames);
                        }
                    } catch (HyphenateException e) {
                        sChatMode.setContactSynced(false);
                        isContactsSyncedWithServer = false;
                        isSyncingContactsWithServer = false;
                        notifyContactsSyncListener(false);
                        Log.e(TAG, "sync contact: fail " + e.toString());
                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.toString());
                        }
                    }
                }
            });
        }
    }


    /**
     * 分发从服务器获取联系人的同步状态
     */
    private void notifyContactsSyncListener(boolean success) {
        for (IDataSyncListener listener : mSyncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }

    /**
     * 从环信服务器上同步黑名单
     */
    private void asyncFetchBlackListFromServer(final EMValueCallBack<List<String>> callback) {
        synchronized (F_SYNC_LOCK) {

            if (isSyncingBlackListWithServer) {
                return;
            } else {
                isSyncingBlackListWithServer = true;
            }

            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<String> userNames = EMClient.getInstance().contactManager().getBlackListFromServer();

                        if (!isLoggedIn()) {
                            isBlackListSyncedWithServer = false;
                            isSyncingBlackListWithServer = false;
                            notifyBlackListSyncListener(false);
                            return;
                        }

                        sChatMode.setBlacklistSynced(true);

                        isBlackListSyncedWithServer = true;
                        isSyncingBlackListWithServer = false;

                        notifyBlackListSyncListener(true);
                        if (callback != null) {
                            callback.onSuccess(userNames);
                        }
                    } catch (HyphenateException e) {
                        sChatMode.setBlacklistSynced(false);

                        isBlackListSyncedWithServer = false;
                        isSyncingBlackListWithServer = true;
                        e.printStackTrace();

                        if (callback != null) {
                            callback.onError(e.getErrorCode(), e.toString());
                        }
                    }
                }
            });
        }
    }

    /**
     * 分发与环信服务器同步黑名单的同步状态
     */
    private void notifyBlackListSyncListener(boolean success) {
        for (IDataSyncListener listener : mSyncBlackListListeners) {
            listener.onSyncComplete(success);
        }
    }

    /**
     * 设置全局监听
     */
    private void setGlobalListeners() {
        mSyncGroupsListeners = new ArrayList<>();
        mSyncContactsListeners = new ArrayList<>();
        mSyncBlackListListeners = new ArrayList<>();

        isGroupsSyncedWithServer = sChatMode.isGroupsSynced();
        isContactsSyncedWithServer = sChatMode.isContactSynced();
        isBlackListSyncedWithServer = sChatMode.isBacklistSynced();


        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (mCallReceiver == null) {
            mCallReceiver = new CallReceiver();
        }
        //注册语音接收器
        sApplicationContext.registerReceiver(mCallReceiver, callFilter);

        //注册连接监听
        EMClient.getInstance().addConnectionListener(this);

        //注册联系人、群组监听、消息监听
        registerGroupAndContactListener();
    }

    /**
     * 群组、联系人监听
     */
    private void registerGroupAndContactListener() {
        if (!isInitListenerRegister) {
            EMClient.getInstance().groupManager().addGroupChangeListener(new AiSouGroupChangeListener(sApplicationContext, sEaseUiInstance));
            EMClient.getInstance().contactManager().setContactListener(new AiSouEMContactListener(sApplicationContext, sEaseUiInstance));
            EMClient.getInstance().chatManager().addMessageListener(new AiSouEMMessageListener(sApplicationContext, sEaseUiInstance));
            isInitListenerRegister = true;
        }
    }

    /**
     * 重置状态
     */
    public void reset() {
        synchronized (F_SYNC_LOCK) {
            endCall();
            isSyncingGroupsWithServer = false;
            isSyncingContactsWithServer = false;
            isSyncingBlackListWithServer = false;

            sChatMode.setGroupsSynced(false);
            sChatMode.setContactSynced(false);
            sChatMode.setBlacklistSynced(false);

            isGroupsSyncedWithServer = false;
            isContactsSyncedWithServer = false;
            isBlackListSyncedWithServer = false;

            isVoiceCalling = false;
            isVideoCalling = false;

            isInitListenerRegister = false;

            if (null != sContactList) {
                sContactList.clear();
                sContactList = null;
            }
            if (null!=sUserProfileManager) {
                sUserProfileManager.reset();
            }
            try {
                getUserProfileManager().reset();
                ChatDBManager instanceNull = ChatDBManager.getInstanceNull();
                if (null != instanceNull) {
                    instanceNull.closeDB();
                }
            }catch (Exception e){

                Log.e("重置用户信息错误:",""+e.getMessage());
            }

        }
    }

    public static UserProfileManager getUserProfileManager() {
        if (sUserProfileManager == null) {
            sUserProfileManager = new UserProfileManager();
        }
        return sUserProfileManager;
    }

    /**
     * 结束当前通话
     */
    private static void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            Log.e(TAG, "endCall: fail" + e.toString());
        }
    }

    /**
     * 获取联系人列表
     */
    @NonNull
    public static Map<String, EaseUser> getContactList() {

        if (isLoggedIn() && sContactList == null) {
            sContactList = sChatMode.getContactList();
        }

        if (sContactList == null) {
            return new Hashtable<>();
        }

        return sContactList;
    }

    /**
     * 是否登录
     */
    public static boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    private static void setEaseUIProviders() {
        //用户信息
        sEaseUiInstance.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username, null);
            }

            @Override
            public GroupInfoBean getGroup(String groupId) {
                return UserInfoDiskHelper.getInstance().getSyncSpecifyGroupInfo(groupId, true);
            }
        });

        //设置选项
        sEaseUiInstance.setSettingsProvider(new EaseUI.EaseSettingsProvider() {

            @Override
            public boolean isSpeakerOpened() {
                return sChatMode.getSettingMsgSpeaker();
            }

            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return sChatMode.getSettingMsgVibrate();
            }

            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return sChatMode.getSettingMsgSound();
            }

            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {

                if (message == null) {
                    return sChatMode.getSettingMsgNotification();
                }

                if (!sChatMode.getSettingMsgNotification()) {
                    return false;
                } else {
                    String chatUseName;
                    List<String> notNotifyIds;

                    if (message.getChatType() == EMMessage.ChatType.Chat) {//单聊
                        chatUseName = message.getFrom();
                        notNotifyIds = sChatMode.getDisabledIds();
                    } else {
                        chatUseName = message.getTo();
                        notNotifyIds = sChatMode.getDisabledGroups();
                    }
                    return notNotifyIds == null || !notNotifyIds.contains(chatUseName);
                }
            }
        });

        //聊天表情
        sEaseUiInstance.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
                List<EaseEmojicon> list;
                if (null != data && null != (list = data.getEmojiconList())) {
                    if (!list.isEmpty()) {
                        ArrayMap<String, Object> arrayList = new ArrayMap<>(list.size());
                        for (EaseEmojicon emojicon : list) {
                            arrayList.put(emojicon.getEmojiText(), emojicon.getIcon());
                        }
                        return arrayList;
                    }
                }
                return null;
            }
        });


        sEaseUiInstance.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                if (message.getChatType() == EMMessage.ChatType.Chat) {
                    return getUserInfo(message.getFrom(), message).getNickname();
                } else {
                    return getGroupInfo(message.getTo(), message).getGroupName();
                }
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                return R.drawable.ic_notification;
            }

            @Override
            public Bitmap getLargeIcon(EMMessage message) {
                Bitmap bitmap;
                try {
                    if (message.getChatType() == EMMessage.ChatType.Chat) {
                        bitmap = BitmapFactory.decodeFile(getUserInfo(message.getFrom(), message).getAvatar());
                    } else {
                        bitmap = BitmapFactory.decodeFile(getGroupInfo(message.getTo(), message).getGroupAvatar());
                    }
                } catch (OutOfMemoryError error) {
                    bitmap = BitmapFactory.decodeResource(sApplicationContext.getResources(), net.twoant.master.R.mipmap.ic_launcher);
                }
                return bitmap;
            }

            @Override
            public String getDisplayedText(EMMessage message) {

                String ticker = EaseCommonUtils.getMessageDigest(message, sApplicationContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom(), message);
                if (user != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(sApplicationContext.getString(R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(sApplicationContext.getString(net.twoant.master.R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                String ticker = EaseCommonUtils.getMessageDigest(message, sApplicationContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom(), message);
                if (user != null) {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(sApplicationContext.getString(net.twoant.master.R.string.at_your_in_group), user.getNick());
                    }
                    return user.getNick() + ": " + ticker;
                } else {
                    if (EaseAtMessageHelper.get().isAtMeMsg(message)) {
                        return String.format(sApplicationContext.getString(net.twoant.master.R.string.at_your_in_group), message.getFrom());
                    }
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {

                Intent intent = new Intent(sApplicationContext, ChatActivity.class);

                if (isVideoCalling) {
                    intent = new Intent(sApplicationContext, VideoCallActivity.class);
                } else if (isVoiceCalling) {
                    intent = new Intent(sApplicationContext, VoiceCallActivity.class);
                } else {
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) {
                        intent.putExtra(ChatActivity.USER_ID, message.getFrom());
                        intent.putExtra(ChatActivity.CHANT_TYPE, Constant.CHATTYPE_SINGLE);
                    } else {
                        intent.putExtra(ChatActivity.USER_ID, message.getTo());
                        if (chatType == EMMessage.ChatType.GroupChat) {
                            intent.putExtra(ChatActivity.CHANT_TYPE, Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra(ChatActivity.CHANT_TYPE, Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });
    }

    /**
     * 获取群信息
     */
    private static GroupInfoBean getGroupInfo(String groupId, EMMessage message) {
        GroupInfoBean groupInfoBean = new GroupInfoBean(groupId);
        groupInfoBean.setGroupName("群消息");
        return groupInfoBean;
    }

    /**
     * 根据uid 获取用户信息
     */
    private static EaseUser getUserInfo(String username, EMMessage message) {
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentUserInfo();
        EaseUser user = getContactList().get(username);
        if (null == user || TextUtils.isEmpty(user.getAvatar()) || !new File(user.getAvatar()).exists()) {
            user = UserInfoDiskHelper.getSyncUserInfo(username);
            EaseCommonUtils.setUserInitialLetter(user);
        } else {
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    /**
     * 初始化 配置选项
     */
    private static EMOptions initChatOptions(ChatMode chatMode) {

        EMOptions options = new EMOptions();
        // 不自动接受邀请
        options.setAcceptInvitationAlways(chatMode.getAutoAcceptInvitation());
        // 设置是否需要接受方已读确认
        options.setRequireAck(true);
        // 设置是否需要接受方已读确认
        options.setRequireDeliveryAck(chatMode.getRequireDeliveryAck());
        //设置自动登录
        options.setAutoLogin(true);
        //设置聊天室 创建者可离开
        options.allowChatroomOwnerLeave(true);
        //设置退出群组自动删除信息
        options.setDeleteMessagesAsExitGroup(chatMode.isDeleteMessagesAsExitGroup());
        //设置是否自动同意群邀请
        options.setAutoAcceptGroupInvitation(chatMode.isAutoAcceptGroupInvitation());

        return options;
    }


}
