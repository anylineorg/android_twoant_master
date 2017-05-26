package net.twoant.master.ui.chat.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;

import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.GroupInfoBean;
import com.jakewharton.disklrucache.DiskLruCache;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.ui.main.adapter.control.ControlUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Response;

/**
 * Created by S_Y_H on 2017/2/21.
 * 用户数据本地存储帮助类
 */

public class UserInfoDiskHelper {

    /**
     * 缓存的大小( 80 M )
     */
    private final static int SIZE_CACHE = 80 * 1024 * 1024;

    private static UserInfoDiskHelper sUserInfoDiskHelper;
    private static int sUserImgSize;
    private static int sGroupImgHeight;
    private static int sGroupImgWidth;
    private static ArrayMap<String, EaseUser> sCacheEaseUsers;
    private static ArrayMap<String, GroupInfoBean> sCacheGroupInfo;
    private final static Object M_CONTACT_LOCK = new Object();
    private final static Object M_GROUP_LOCK = new Object();

    static {
        sUserInfoDiskHelper = new UserInfoDiskHelper();
    }

    final static String TAG = "UserInfoDiskHelper";

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    /**
     * 是否与群组数据库同步过
     */
    private boolean isSynchronizationGroupDB;

    public void getUserInfoAndGroupCache() {
        if (null != mUserManage) {
            putUserCacheAll(mUserManage.getAllUserInfo());
        }
        if (null != mGroupManage) {
            putGroupCacheAll(mGroupManage.getAllUserInfo());
        }
    }

    /**
     * 更新用户信息 监听
     */
    public interface IOnUpdateFinishListener<T> {
        void onUpdateFinishListener(boolean isSuccessful, T easeUser);
    }

    private final static SimpleArrayMap<String, IOnUpdateFinishListener<EaseUser>> STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP = new SimpleArrayMap<>();

    private final static int APP_VERSION = 1;
    private final static String HEADER_IMG_FOLDER = "head";
    private final static String AI_SOU_FOLDER = "contact";
    private DiskLruCache mDiskLruCache;
    private UserManage mUserManage;
    private GroupManage mGroupManage;
    static String sImgPath;
    public static UserInfoDiskHelper getInstance() {
        if (sUserInfoDiskHelper == null) {
            synchronized (UserInfoDiskHelper.class) {
                if (sUserInfoDiskHelper == null) {
                    sUserInfoDiskHelper = new UserInfoDiskHelper();
                }
            }
        }
        return sUserInfoDiskHelper;
    }

    @Nullable
    public static UserInfoDiskHelper getInstanceNull() {
        return sUserInfoDiskHelper;
    }
    /**
     * 适当的位置调用该方法来关闭
     */
    public synchronized void onDestroy() {
        if (null != mUserManage) {
            mUserManage.closeDB();
            mUserManage = null;
        }

        if (null != mGroupManage) {
            mGroupManage.closeDB();
            mGroupManage = null;
        }

        if (null != mDiskLruCache) {
            try {
                mDiskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDiskLruCache = null;
        }

        if (null != sCacheEaseUsers) {
            sCacheEaseUsers.clear();
            sCacheEaseUsers = null;
        }

        if (null != sCacheGroupInfo) {
            sCacheGroupInfo.clear();
            sCacheGroupInfo = null;
        }

        if (null != STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP) {
            STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.clear();
        }

        sImgPath = null;
        sUserInfoDiskHelper = null;
        Log.d(TAG, "onDestroy: ");
    }

    private UserInfoDiskHelper() {
        Log.e(TAG, "new instance: ");
        initContactCatch(10);
        initGroupCatch(8);
        Context context = AiSouAppInfoModel.getAppContext();
        sUserImgSize = DisplayDimensionUtils.getPxFromRes(net.twoant.master.R.dimen.px_90, context);
        sGroupImgWidth = DisplayDimensionUtils.getScreenWidth();
        sGroupImgHeight = (int) (sGroupImgWidth / 5F * 3 + 0.5F);
        mUserManage = new UserManage(context);
        mGroupManage = new GroupManage(context);
        String name = AiSouAppInfoModel.getInstance() == null ? "default_head" : AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID();
        File file;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {

                file = new File(context.getExternalFilesDir(null) + File.separator +
                        AI_SOU_FOLDER + File.separator + name + File.separator + HEADER_IMG_FOLDER);

                FileUtils.createOrExistsDir(file);
                mDiskLruCache = DiskLruCache.open(file, APP_VERSION, 1, SIZE_CACHE);
            } else {
                file = new File(context.getFilesDir().getAbsolutePath()
                        + File.separator + AI_SOU_FOLDER + File.separator + name + File.separator + HEADER_IMG_FOLDER);
                FileUtils.createOrExistsDir(file);
                mDiskLruCache = DiskLruCache.open(file, APP_VERSION, 1, SIZE_CACHE / 4);
            }
            sImgPath = file.getAbsolutePath() + File.separator;
        } catch (Exception e) {
            Log.e(TAG, "构造" + e);
        }
    }

    /**
     * 初始化群组列表缓存
     */
    private static void initGroupCatch(int size) {
        if (null == sCacheGroupInfo) {
            synchronized (M_GROUP_LOCK) {
                if (null == sCacheGroupInfo) {
                    sCacheGroupInfo = new ArrayMap<>(size);
                }
            }
        }
    }

    /**
     * 获取用户信息
     */
    public static EaseUser getSyncUserInfo(String user) {
        EaseUser cache = getUserCache(user);
        if (null == cache) {
            if (null != sUserInfoDiskHelper) {
                sUserInfoDiskHelper.saveUser(user, null);
            }
            Log.e(TAG, "getSyncUserInfo: 获取用户数据" + user);
            return getDefaultInfo(user);
        }
        return cache;
    }

    private static EaseUser getDefaultInfo(String user) {
        EaseUser easeUser = new EaseUser(null == user ? "" : user);
        easeUser.setAvatar("");
        easeUser.setNickname(user);
        return easeUser;
    }

    private static void initContactCatch(int size) {
        if (null == sCacheEaseUsers) {
            synchronized (M_CONTACT_LOCK) {
                if (null == sCacheEaseUsers) {
                    sCacheEaseUsers = new ArrayMap<>(size);
                }
            }
        }
    }

    private static EaseUser getUserCache(String key) {
        if (null != sCacheEaseUsers) {
            synchronized (M_CONTACT_LOCK) {
                return sCacheEaseUsers.get(key);
            }
        }
        return null;
    }

    private static EaseUser removeCache(String key) {
        if (null != sCacheEaseUsers) {
            synchronized (M_CONTACT_LOCK) {
                return sCacheEaseUsers.remove(key);
            }
        }
        return null;
    }

    private static void putCache(EaseUser easeUser) {
        if (null != sCacheEaseUsers && null != easeUser) {
            synchronized (M_CONTACT_LOCK) {
                sCacheEaseUsers.put(easeUser.getUsername(), easeUser);
            }
        }
    }

    public void saveUser(final String userName, final IOnUpdateFinishListener<EaseUser> onUpdateFinishListener) {
        STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.put(userName, onUpdateFinishListener);
        execute(new Runnable() {
            @Override
            public void run() {
                EaseUser andSave = getAndSave(userName, mUserManage, mDiskLruCache);
                if (null != andSave) {
                    IOnUpdateFinishListener<EaseUser> listener = STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.remove(andSave.getUsername());
                    if (null != listener) {
                        listener.onUpdateFinishListener(true, andSave);
                    }
                } else {
                    IOnUpdateFinishListener<EaseUser> listener = STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.remove(userName);
                    if (null != listener) {
                        listener.onUpdateFinishListener(false, null);
                    }
                }
            }
        });
    }

    public void saveUser(@NonNull final EaseUser userName, final IOnUpdateFinishListener<EaseUser> onUpdateFinishListener) {
        STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.put(userName.getUsername(), onUpdateFinishListener);
        execute(new Runnable() {
            @Override
            public void run() {
                if (null != mUserManage && mUserManage.saveUserInfo(userName) && null != mDiskLruCache) {
                    putFileToDisk(userName, mDiskLruCache);
                    putCache(userName);
                }
                IOnUpdateFinishListener<EaseUser> listener = STRING_I_ON_UPDATE_FINISH_LISTENER_SIMPLE_ARRAY_MAP.remove(userName.getUsername());

                if (null != listener) {
                    listener.onUpdateFinishListener(!userName.getAvatar().startsWith("http:"), userName);
                }
            }
        });
    }

    private void putGroupCacheAll(SimpleArrayMap<String, GroupInfoBean> groupInfo) {
        if (null != sCacheGroupInfo && null != groupInfo) {
            synchronized (M_GROUP_LOCK) {
                sCacheGroupInfo.putAll(groupInfo);
            }
        }
    }

    private void putUserCacheAll(SimpleArrayMap<String, EaseUser> allUserInfo) {
        if (null != sCacheEaseUsers && null != allUserInfo) {
            synchronized (M_CONTACT_LOCK) {
                sCacheEaseUsers.putAll(allUserInfo);
            }
        }
    }

    /**
     * 删除用户信息
     */
    public void deleteUserInfo(String... users) {
        synchronized (M_CONTACT_LOCK) {
            mUserManage.deleteUserInfo(users);
            try {
                for (String user : users) {
                    if (null != sCacheEaseUsers) {
                        EaseUser easeUser = removeCache(user);
                        if (null != easeUser) {
                            mDiskLruCache.remove(MD5Util.getMD5ToHex(easeUser.getUsername()));
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * 存储用户信息
     */
    public void putUserInfo(boolean sync, final EaseUser... easeUser) {
        if (sync) {
            synchronized (M_CONTACT_LOCK) {

                for (EaseUser user : easeUser) {
                    if (mUserManage.saveUserInfo(user)) {
                        putCache(user);
                        putFileToDisk(user, mDiskLruCache);
                    }
                }
            }
        } else {
            execute(new Runnable() {
                @Override
                public void run() {
                    putUserInfo(true, easeUser);
                }
            });
        }
    }

    /**
     * 获取服务器用户信息 并进行保存
     *
     * @param user          uid
     * @param mUserManage   dbManage
     * @param mDiskLruCache cache
     * @return instance
     */
    private static EaseUser getAndSave(String user, UserManage mUserManage, DiskLruCache mDiskLruCache) {
        EaseUser userInfo = getUserInfo(user);
        if (null != userInfo && null != mUserManage && mUserManage.saveUserInfo(userInfo) && null != mDiskLruCache) {
            putFileToDisk(userInfo, mDiskLruCache);
            putCache(userInfo);
        }
        return userInfo;
    }

    @NonNull
    public List<EaseUser> getGroupBlackListUsersInfo(List<String> easeUsers) {
        ArrayList<EaseUser> list = new ArrayList<>();
        if (null == easeUsers || easeUsers.isEmpty()) {
            return list;
        }
        synchronized (M_CONTACT_LOCK) {

            EaseUser userInfo;
            for (String user : easeUsers) {
                userInfo = getUserCache(user);
                if (null == userInfo) {
                    userInfo = getUserInfo(user);
                    if (null != userInfo) {
                        putCache(userInfo);
                        list.add(userInfo);
                    }
                } else {
                    list.add(userInfo);
                }
            }
            return list;
        }
    }

    /**
     * 同步用户信息
     * 从后台获取用户信息
     */
    @NonNull
    public ArrayMap<String, EaseUser> syncUserInfo(List<String> easeUsers) {
        if (null == easeUsers || easeUsers.isEmpty()) {
            return new ArrayMap<>();
        }
        synchronized (M_CONTACT_LOCK) {
            final ArrayMap<String, EaseUser> usersData = new ArrayMap<>(easeUsers.size());

            ArrayMap<String, EaseUser> allUserInfo = mUserManage.getAllUserInfo();
            if (null != allUserInfo)
                for (EaseUser e : allUserInfo.values()) {
                    Log.e(TAG, "数据库中的好友: " + e.getUsername());
                }

            EaseUser userInfo;
            for (String user : easeUsers) {
                if (null == allUserInfo || !allUserInfo.containsKey(user)) {
                    userInfo = getUserInfo(user);
                    if (null != userInfo && null != mUserManage && mUserManage.saveUserInfo(userInfo) && null != mDiskLruCache) {
                        usersData.put(user, userInfo);
                        putFileToDisk(userInfo, mDiskLruCache);
                        putCache(userInfo);
                    }
                } else {
                    usersData.put(user, allUserInfo.get(user));
                }
            }

            putUserCacheAll(allUserInfo);
            return usersData;
        }
    }

    /**
     * 同步获取用户信息
     * 从后台获取用户信息
     */
    @NonNull
    public ArrayMap<String, EaseUser> getUserInfo(List<String> easeUsers) {
        ArrayMap<String, EaseUser> userArrayMap = new ArrayMap<>();
        if (null == easeUsers || easeUsers.isEmpty()) {
            return userArrayMap;
        }
        synchronized (M_CONTACT_LOCK) {

            EaseUser userInfo;

            for (String user : easeUsers) {
                userInfo = getUserCache(user);
                if (null == userInfo) {
                    userInfo = getUserInfo(user);
                    if (null != userInfo && null != mUserManage && mUserManage.saveUserInfo(userInfo) && null != mDiskLruCache) {
                        putFileToDisk(userInfo, mDiskLruCache);
                        putCache(userInfo);
                        userArrayMap.put(user, userInfo);
                    }
                } else {
                    userArrayMap.put(user, userInfo);
                }
            }

            return userArrayMap;
        }
    }

    /**
     * 同步群组数据
     */
    public void syncGroupInfo(List<EMGroup> groups) {
        if (null == groups || groups.isEmpty()) {
            return;
        }
        synchronized (M_GROUP_LOCK) {
            ArrayMap<String, GroupInfoBean> allUserInfo = mGroupManage.getAllUserInfo();
            isSynchronizationGroupDB = true;

            if (null != allUserInfo)
                for (GroupInfoBean e : allUserInfo.values()) {
                    Log.e(TAG, "数据库中的群: " + e.getGroupId());
                }

            int size = null == allUserInfo ? 0 : allUserInfo.size();

            GroupInfoBean userInfo;

            for (EMGroup user : groups) {
                if (null == user) {
                    continue;
                }
                if (0 == size || !allUserInfo.containsKey(user.getGroupId())) {
                    userInfo = getGroupInfo(user.getGroupId());
                    if (null != userInfo && null != mUserManage && mGroupManage.saveUserInfo(userInfo) && null != mDiskLruCache) {
                        putFileToDisk(userInfo, mDiskLruCache);
                        putGroupCache(userInfo);
                    }
                }
            }

            if (0 != size) {
                putGroupCacheAll(allUserInfo);
            }
        }
    }

    @NonNull
    public ArrayMap<String, GroupInfoBean> getGroupInfo(List<String> groups) {
        if (null == groups || groups.isEmpty()) {
            return new ArrayMap<>();
        }
        synchronized (M_GROUP_LOCK) {
            final ArrayMap<String, GroupInfoBean> allUserInfo = getGroupCache();

            int size = null == allUserInfo ? 0 : allUserInfo.size();

            GroupInfoBean userInfo;

            for (String user : groups) {
                if (0 == size || !allUserInfo.containsKey(user)) {
                    userInfo = getGroupInfo(user);
                    if (null != userInfo && null != mUserManage && mGroupManage.saveUserInfo(userInfo) && null != mDiskLruCache) {
                        putFileToDisk(userInfo, mDiskLruCache);
                        putGroupCache(userInfo);
                    }
                }
            }

            if (0 != size) {
                putGroupCacheAll(allUserInfo);
            }
        }
        return getGroupCache();
    }

    /**
     * 同步获取 群资料
     *
     * @param groupId 群id
     */
    @Nullable
    public GroupInfoBean getSyncSpecifyGroupInfo(final String groupId, boolean isAsync) {
        synchronized (M_GROUP_LOCK) {
            if (!isSynchronizationGroupDB) {
                isSynchronizationGroupDB = true;
                ArrayMap<String, GroupInfoBean> allUserInfo = mGroupManage.getAllUserInfo();
                if (null != allUserInfo && !allUserInfo.isEmpty()) {
                    putGroupCacheAll(allUserInfo);
                }
            }

            GroupInfoBean groupCache = getGroupCache(groupId);
            if (null != groupCache) {
                return groupCache;
            }

            if (isAsync) {
                execute(new Runnable() {
                    @Override
                    public void run() {
                        getGroupInfoBean(groupId);
                    }
                });
            } else {
                return getGroupInfoBean(groupId);
            }
        }
        return null;
    }

    @Nullable
    private GroupInfoBean getGroupInfoBean(String groupId) {
        synchronized (M_GROUP_LOCK) {
            GroupInfoBean groupInfo = getGroupInfo(groupId);
            if (null != groupId) {
                if (null != mUserManage && mGroupManage.saveUserInfo(groupInfo)) {
                    putFileToDisk(groupInfo, mDiskLruCache);
                }
                putGroupCache(groupInfo);
                return groupInfo;
            }
        }
        return null;
    }

    /**
     * 异步 获取指定群的信息
     *
     * @param groupId 群id
     */
    public void getSpecifyGroupInfo(final String groupId, final IOnUpdateFinishListener<GroupInfoBean> iOnUpdateFinishListeners) {
        synchronized (M_GROUP_LOCK) {
            GroupInfoBean groupCache = getGroupCache(groupId);
            if (groupCache == null) {
                execute(new Runnable() {
                    @Override
                    public void run() {
                        GroupInfoBean groupInfo = getGroupInfo(groupId);
                        if (null != groupId) {
                            putGroupCache(groupInfo);
                            if (null != iOnUpdateFinishListeners) {
                                iOnUpdateFinishListeners.onUpdateFinishListener(true, groupInfo);
                            }
                        } else {
                            if (null != iOnUpdateFinishListeners) {
                                iOnUpdateFinishListeners.onUpdateFinishListener(false, null);
                            }
                        }
                    }
                });
            } else {
                if (null != iOnUpdateFinishListeners) {
                    iOnUpdateFinishListeners.onUpdateFinishListener(true, groupCache);
                }
            }
        }
    }

    public ArrayMap<String, GroupInfoBean> getSpecifyGroupInfo(boolean isAsync, @NonNull final List<EMGroup> groups,
                                                               final IOnUpdateFinishListener<ArrayMap<String, GroupInfoBean>> iOnUpdateFinishListener) {

        synchronized (M_GROUP_LOCK) {
            if (isAsync) {
                execute(new Runnable() {
                    @Override
                    public void run() {
                        getSpecifyGroupInfo(false, groups, iOnUpdateFinishListener);
                    }
                });
            } else {
                ArrayList<GroupInfoBean> saveList = new ArrayList<>();
                ArrayMap<String, GroupInfoBean> arrayMap = new ArrayMap<>(groups.size());
                GroupInfoBean groupCache;
                for (EMGroup emGroup : groups) {
                    if (null == emGroup) {
                        continue;
                    }
                    groupCache = getGroupCache(emGroup.getGroupId());
                    if (null != groupCache) {
                        arrayMap.put(emGroup.getGroupId(), groupCache);
                    } else {
                        groupCache = getGroupInfo(emGroup.getGroupId());
                        if (null != groupCache) {
                            saveList.add(groupCache);
                            arrayMap.put(emGroup.getGroupId(), groupCache);
                        }
                    }
                }

                saveGroupInfoToLocal(saveList);

                if (null != iOnUpdateFinishListener) {
                    iOnUpdateFinishListener.onUpdateFinishListener(!arrayMap.isEmpty(), arrayMap);
                }
                return arrayMap;
            }
            return null;
        }
    }


    @Nullable
    public ArrayMap<String, GroupInfoBean> getSpecifyGroupInfo(final List<EMGroupInfo> groups,
                                                               boolean isAsync,
                                                               final IOnUpdateFinishListener<ArrayMap<String, GroupInfoBean>> iOnUpdateFinishListener) {

        if (null == groups || groups.isEmpty()) {
            return null;
        }

        synchronized (M_GROUP_LOCK) {
            if (isAsync) {
                execute(new Runnable() {
                    @Override
                    public void run() {
                        getSpecifyGroupInfo(groups, false, iOnUpdateFinishListener);
                    }
                });
            } else {
                final ArrayList<EMGroupInfo> tempEmGroupInfoList = new ArrayList<>(groups);
                final ArrayMap<String, GroupInfoBean> infoBeanArrayMap = new ArrayMap<>(groups.size());
                GroupInfoBean groupCache;
                final ArrayList<GroupInfoBean> saveList = new ArrayList<>();
                for (EMGroupInfo emGroup : tempEmGroupInfoList) {
                    if (null == emGroup) {
                        continue;
                    }
                    groupCache = getGroupCache(emGroup.getGroupId());
                    if (null != groupCache) {
                        infoBeanArrayMap.put(emGroup.getGroupId(), groupCache);
                    } else {
                        groupCache = getGroupInfo(emGroup.getGroupId());
                        if (null != groupCache) {
                            putGroupCache(groupCache);
                            saveList.add(groupCache);
                            infoBeanArrayMap.put(emGroup.getGroupId(), groupCache);
                        }
                    }
                }

                saveGroupInfoToLocal(saveList);

                if (null != iOnUpdateFinishListener) {
                    iOnUpdateFinishListener.onUpdateFinishListener(!infoBeanArrayMap.isEmpty(), infoBeanArrayMap);
                }
                return infoBeanArrayMap;
            }
            return null;
        }
    }

    private void saveGroupInfoToLocal(final ArrayList<GroupInfoBean> saveList) {
        if (!saveList.isEmpty()) {
            execute(new Runnable() {
                @Override
                public void run() {
                    for (GroupInfoBean groupInfo : saveList) {
                        if (null != mUserManage && mGroupManage.saveUserInfo(groupInfo)) {
                            putFileToDisk(groupInfo, mDiskLruCache);
                        }
                    }
                }
            });
        }
    }


    private static void putGroupCache(GroupInfoBean userInfo) {
        if (null != sCacheGroupInfo && null != userInfo) {
            synchronized (M_GROUP_LOCK) {
                sCacheGroupInfo.put(userInfo.getGroupId(), userInfo);
            }
        }
    }


    private static GroupInfoBean getGroupCache(String groupId) {
        if (null != sCacheGroupInfo) {
            synchronized (M_GROUP_LOCK) {
                return sCacheGroupInfo.get(groupId);
            }
        }
        return null;
    }

    /**
     * 获取所有已缓存的群组信息
     */
    private static ArrayMap<String, GroupInfoBean> getGroupCache() {
        if (null != sCacheGroupInfo) {
            synchronized (M_GROUP_LOCK) {
                ArrayMap<String, GroupInfoBean> groups = new ArrayMap<>(sCacheGroupInfo.size());
                groups.putAll((SimpleArrayMap<? extends String, ? extends GroupInfoBean>) sCacheGroupInfo);
                return groups;
            }
        }
        return new ArrayMap<>();
    }

    /**
     * 同步 更新用户信息
     * 需要开新的线程
     */
    public boolean syncUpdateUserInfo(EaseUser user, boolean checkIdentical) {
        synchronized (M_CONTACT_LOCK) {

            if (checkIdentical) {

                int result = hasIdenticalUser(user);

                Log.e(TAG, "syncUpdateUserInfo: result = " + result);

                if (-2 == result || EaseUser.INFO_SAME == result) return false;

                if (EaseUser.INFO_DEFAULT == result) {
                    if (null != user.getAvatar()) {
                        return mUserManage.saveUserInfo(user) | putFileToDisk(user, mDiskLruCache);
                    } else {
                        getAndSave(user.getUsername(), mUserManage, mDiskLruCache);
                    }
                }

                if (EaseUser.NAME_SAME == result) {
                    if (null != user.getAvatar()) {
                        return mUserManage.updateUserInfo(user) | putFileToDisk(user, mDiskLruCache);
                    } else {
                        user = getUserInfo(user.getUsername());
                        return mUserManage.updateUserInfo(user) | putFileToDisk(user, mDiskLruCache);
                    }
                }
            }

            boolean isSuccess = null != mUserManage && mUserManage.saveUserInfo(user) | putFileToDisk(user, mDiskLruCache);
            if (isSuccess) {
                putCache(user);
            }
            return isSuccess;
        }
    }


    public int hasIdenticalUser(@NonNull EaseUser user) {
        synchronized (M_CONTACT_LOCK) {
            if (null != sCacheEaseUsers && null != sCacheEaseUsers.get(user.getUsername())) {
                return user.isEquals(getUserCache(user.getUsername()));
            }
            return -2;
        }
    }

    /**
     * 到线程池里执行
     */
    public boolean execute(Runnable runnable) {
        if (null == mExecutorService) return false;

        mExecutorService.execute(runnable);

        return true;
    }

    /**
     * 根据uid 从服务器获取 信息
     *
     * @param easeUser uid
     * @return easeUser实体
     */
    private static EaseUser getUserInfo(String easeUser) {
        if (TextUtils.isEmpty(easeUser)) {
            return null;
        }

        AiSouAppInfoModel aiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        if (null == aiSouAppInfoModel) {
            return null;
        }

        Response response = null;
        try {
            response = OkHttpUtils.get().url(ApiConstants.FIND_USER)
                    .addParams("_t", aiSouAppInfoModel.getAiSouUserBean().getLoginToken())
                    .addParams("_cc", aiSouAppInfoModel.getAiSouLocationBean().getCurrentCityCode())
                    .addParams("_ac", aiSouAppInfoModel.getAiSouLocationBean().getCurrentAddressCode())
                    .addParams("m", easeUser).build().execute();
            if (response.isSuccessful()) {
                DataRow dataRow = DataRow.parseJson(response.body().string());
                if (dataRow != null && dataRow.getBoolean("result", false)) {
                    DataSet set=dataRow.getSet("data");
                    dataRow=set.getRow(0);
                    if (easeUser.equals(dataRow.getString("CODE"))) {
                        EaseUser user = new EaseUser(easeUser);
                        user.setAvatar(BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", "")));
                        user.setAvatarMd5(MD5Util.getMD5ToHex(user.getAvatar()));
                        user.setNickname(dataRow.getString("NM"));
                        user.setAge(dataRow.getInt("AGE"));
                        user.setSex(dataRow.getInt("SEX") != 0 ? "男" : "女");
                        String temp = dataRow.getStringDef("SIGN", "");
                        temp = ControlUtils.isNull(temp) ? "这个人很懒，什么也没留下。" : temp;
                        user.setSignature(temp);
                        return user;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            Log.d(TAG, "get user info" + e);
            return null;
        } finally {
            CloseUtils.closeIO(response);
        }
    }

    /**
     * 根据群id 从服务器获取 信息
     *
     * @param groupId uid
     * @return easeUser实体
     */
    private static GroupInfoBean getGroupInfo(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }

        AiSouAppInfoModel aiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        if (null == aiSouAppInfoModel) {
            return null;
        }

        Response response = null;
        try {
            response = OkHttpUtils.get().url(ApiConstants.CHAT_INQUIRY_GROUP_INFO)
                    .addParams("_t", aiSouAppInfoModel.getAiSouUserBean().getLoginToken())
                    .addParams("_cc", aiSouAppInfoModel.getAiSouLocationBean().getCurrentCityCode())
                    .addParams("_ac", aiSouAppInfoModel.getAiSouLocationBean().getCurrentAddressCode())
                    .addParams("code", groupId).build().execute();
            if (response.isSuccessful()) {
                DataRow dataRow = DataRow.parseJson(response.body().string());
                if (dataRow != null && dataRow.getBoolean("result", false) && (dataRow = dataRow.getRow("data")) != null) {
                    if (groupId.equals(dataRow.getString("code"))) {
                        GroupInfoBean user = new GroupInfoBean(groupId);
                        user.setGroupAvatar(BaseConfig.getCorrectImageUrl(dataRow.getStringDef("AVATAR", "")));
                        user.setGroupAvatarMD5(MD5Util.getMD5ToHex(user.getGroupAvatar()));
                        return user;
                    }
                }
            }
            return null;
        } catch (IOException e) {
            Log.d(TAG, "get user info" + e);
            return null;
        } finally {
            CloseUtils.closeIO(response);
        }
    }


    /**
     * 从服务器获取用户头像图片
     */
    private static boolean getImageFromNet(OutputStream outputStream, String url, int width, int height) {
        if (url == null) return false;
        if (!url.startsWith("http:")) {
            url = BaseConfig.getCorrectImageUrl(url);
        }
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setConnectTimeout(12000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.connect();
            bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            Log.e(TAG, "getImageFromNet length: " + httpURLConnection.getContentLength());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(httpURLConnection.getContentLength());
            byte[] temp = new byte[1024];
            int len;
            while (-1 != (len = bufferedInputStream.read(temp))) {
                byteArrayOutputStream.write(temp, 0, len);
            }
            temp = byteArrayOutputStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
            int simpleSize = 1;
            while (options.outWidth / simpleSize > width || options.outHeight / simpleSize > height) {
                simpleSize = simpleSize << 1;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = simpleSize;
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            Log.d(TAG, "get img success");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "get img =" + e);
            return false;
        } catch (OutOfMemoryError err) {
            Log.e(TAG, "getImageFromNet: oom" + err);
        } finally {
            CloseUtils.closeIO(bufferedInputStream);

            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return false;
    }

    /**
     * 将用户头像放入缓存
     */
    private static boolean putFileToDisk(EaseUser easeUser, DiskLruCache diskLruCache) {
        if (easeUser == null || diskLruCache == null) return false;

        String avatar = easeUser.getAvatar();
        if (avatar == null) {
            return false;
        }


        String key = MD5Util.getMD5ToHex(easeUser.getUsername());
        try {
            DiskLruCache.Editor edit = diskLruCache.edit(key);
            if (edit != null) {
                OutputStream outputStream = edit.newOutputStream(0);
                if (getImageFromNet(outputStream, easeUser.getAvatar(), sUserImgSize, sUserImgSize)) {
                    edit.commit();
                    Log.d(TAG, "edit.commit()");
                    easeUser.setAvatar(sImgPath + key + ".0");
                } else {
                    Log.d(TAG, "edit.abort()");
                    edit.abort();
                }
                diskLruCache.flush();
                return true;
            }
            return false;
        } catch (Exception io) {
            Log.d(TAG, "put File To Disk" + io);
            return false;
        }
    }


    /**
     * 将群组头像放入缓存
     */
    private static boolean putFileToDisk(GroupInfoBean groupInfoBean, DiskLruCache diskLruCache) {
        if (groupInfoBean == null || diskLruCache == null) return false;

        String avatar = groupInfoBean.getGroupAvatar();
        if (avatar == null) {
            return false;
        }


        String key = MD5Util.getMD5ToHex(groupInfoBean.getGroupId());
        try {
            DiskLruCache.Editor edit = diskLruCache.edit(key);
            if (edit != null) {
                OutputStream outputStream = edit.newOutputStream(0);
                if (getImageFromNet(outputStream, groupInfoBean.getGroupAvatar(), sGroupImgWidth, sGroupImgHeight)) {
                    edit.commit();
                    Log.d(TAG, "group edit.commit()");
                    groupInfoBean.setGroupAvatar(sImgPath + key + ".0");
                } else {
                    Log.d(TAG, "group edit.abort()");
                    edit.abort();
                }
                diskLruCache.flush();
                return true;
            }
            return false;
        } catch (Exception io) {
            Log.d(TAG, "group put File To Disk" + io);
            return false;
        }
    }

}
