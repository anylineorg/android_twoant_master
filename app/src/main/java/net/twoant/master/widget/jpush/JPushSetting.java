package net.twoant.master.widget.jpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by S_Y_H on 2016/12/10.
 * 极光推送设置类
 */

public class JPushSetting implements Handler.Callback, TagAliasCallback {

    @SuppressLint("StaticFieldLeak")
    private static JPushSetting sJPushSetting;
    private WeakReference<Context> mContext;
    private WeakReference<Handler> mHandler;
    private final static int WHAT_REQUEST_SET_ALIAS_AND_TAG = 1;
    private final static int WHAT_REQUEST_SET_ALIAS = 2;
    private final static int WHAT_REQUEST_SET_TAG = 3;
    private final static int WHAT_CLEAN = 4;
    private final static int TYPE_ALIAS = 5;
    private final static int TYPE_TAG = 6;
    private final static int TYPE_ALIAS_TAG = 7;
    private final static int TYPE_CLEAN = 8;
    private final static String EXTRA_ALIAS = "JPushSetting_extra_alias";
    private final static String EXTRA_TAG = "JPushSetting_extra_tag";
    private final static String TAG = "JPushSetting";
    private int mType;


    private JPushSetting(Context context) {
        if (context != null) {
            AiSouAppInfoModel.getInstance(context.getApplicationContext());
        }
        checkFiled();
    }

    /**
     * @return 极光推送设置实例
     */
    public static JPushSetting getInstance(Context context) {
        if (sJPushSetting == null) {
            synchronized (JPushSetting.class) {
                if (sJPushSetting == null) {
                    sJPushSetting = new JPushSetting(context);
                }
            }
        }
        return sJPushSetting;
    }


    /**
     * 注册 别名和标签 （别名和标签 不能为空）
     *
     * @param alias 别名
     * @param tags  标签
     */
    void setAliasAndTag(String alias, List<String> tags) {
        checkState();
        if (checkTagAndAlias(alias, tags)) {
            return;
        }
        mType = TYPE_ALIAS_TAG;
        Set<String> tagSet = new LinkedHashSet<>(tags.size());
        for (String str : tags) {
            tagSet.add(str);
        }
        tagSet = JPushInterface.filterValidTags(tagSet);
        checkFiled();
        JPushInterface.setAliasAndTags(mContext.get(), alias, tagSet, this);
    }

    /**
     * 设置Alias
     */
    public void setAlias(String alias) {
        if (isEmpty(alias)) return;
        checkState();
        mType = TYPE_ALIAS;
        checkFiled();
        JPushInterface.setAlias(mContext.get(), alias, this);
    }

    /**
     * 设置Tag
     */
    public void setTag(List<String> tags) {
        checkState();
        if (checkTag(tags)) return;
        mType = TYPE_TAG;
        Set<String> tagSet = new LinkedHashSet<>(tags.size());
        for (String str : tags) {
            tagSet.add(str);
        }
        setTag(tagSet);
    }

    public void setTag(Set<String> tagSet) {
        if (checkTag(tagSet)) return;
        mType = TYPE_TAG;
        tagSet = JPushInterface.filterValidTags(tagSet);
        checkFiled();
        JPushInterface.setTags(mContext.get(), tagSet, this);
    }

    @Override
    public void gotResult(int code, String alias, Set<String> tags) {
        switch (code) {
            case 0:
                LogUtils.d(TAG, "成功" + mType);
                sJPushSetting = null;
                break;

            case 6002:
                switch (mType) {
                    case TYPE_ALIAS:
                        if (NetworkUtils.isNetworkConnected()) {
                            checkFiled();
                            Handler handler = mHandler.get();
                            handler.sendMessageDelayed(handler.obtainMessage(WHAT_REQUEST_SET_ALIAS, tags), 1000 * 60);
                        } else {
                            AiSouAppInfoModel.getInstance().setRequestRemoveAccount(true);
                        }
                        break;
                    case TYPE_TAG:
                        if (NetworkUtils.isNetworkConnected()) {
                            checkFiled();
                            Handler handler = mHandler.get();
                            handler.sendMessageDelayed(handler.obtainMessage(WHAT_REQUEST_SET_TAG, tags), 1000 * 60);
                        } else {
                            saveFailTags(tags);
                        }
                        break;
                    case TYPE_ALIAS_TAG:
                        if (NetworkUtils.isNetworkConnected()) {
                            checkFiled();
                            Handler handler = mHandler.get();
                            Message message = handler.obtainMessage();
                            message.what = WHAT_REQUEST_SET_ALIAS_AND_TAG;
                            message.obj = tags;
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_ALIAS, alias);
                            handler.sendMessageDelayed(message, 1000 * 60);
                        } else {
                            AiSouAppInfoModel.getInstance().setRequestRemoveAccount(true);
                        }
                        break;
                    case TYPE_CLEAN://退出不了，就给他关了
                        checkFiled();
                        mHandler.get().sendEmptyMessage(WHAT_CLEAN);
                }
                break;
            default:
                checkFiled();
                mHandler.get().sendEmptyMessage(WHAT_CLEAN);
                LogUtils.d(TAG, "失败 code = " + code + "mType = " + mType);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_REQUEST_SET_ALIAS_AND_TAG:
                Bundle bundle = msg.getData();
                setAliasAndTag(bundle.getString(EXTRA_ALIAS), (Set<String>) msg.obj);
                break;
            case WHAT_REQUEST_SET_ALIAS:
                setAlias((String) msg.obj);
                break;
            case WHAT_REQUEST_SET_TAG:
                setTag((Set<String>) msg.obj);
                break;
            case WHAT_CLEAN:
                stopJPush();
                break;
        }
        return true;
    }

    private void setAliasAndTag(String alias, Set<String> tagSet) {
        if (checkTagAndAlias(alias, tagSet)) return;
        checkState();
        mType = TYPE_ALIAS_TAG;
        tagSet = JPushInterface.filterValidTags(tagSet);
        checkFiled();
        JPushInterface.setAliasAndTags(mContext.get(), alias, tagSet, this);
    }

    /**
     * @return true 为空
     */
    public static boolean checkTagAndAlias(String alias, Collection tagSet) {
        return isEmpty(alias) || checkTag(tagSet);
    }

    private static boolean checkTag(Collection tagSet) {
        return tagSet == null || tagSet.isEmpty();
    }

    /**
     * 保存tag到本地，下次启动时进行注册。
     *
     * @param tags tag
     */
    private void saveFailTags(Set<String> tags) {
        checkState();
        if (checkTag(tags)) return;
        StringBuilder stringBuilder = new StringBuilder(tags.size() << 1);
        for (String tag : tags) {
            stringBuilder.append(tag).append("-");
        }
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(AiSouAppInfoModel.KEY_IS_SET_J_PUSH_TAG, true);
        String tag = stringBuilder.toString();
        edit.putString(AiSouAppInfoModel.KEY_J_PUSH_LAST_TAGS, tag);
        if (!edit.commit()) {
            AiSouAppInfoModel.getInstance().setRequestRemoveAccount(true);
        } else {
            AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
            instance.setHasJPushFailTags(true);
            instance.setJPushTags(tag);
        }
    }

    /**
     * 检查 极光推送是否可用
     */
    private void checkState() {
        checkFiled();
        Context context = mContext.get();
        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
    }

    private void checkFiled() {
        if (mContext == null || mContext.get() == null || mHandler == null || mHandler.get() == null) {
            mContext = new WeakReference<>(AiSouAppInfoModel.getAppContext());
            mHandler = new WeakReference<>(new Handler(AiSouAppInfoModel.getAppContext().getMainLooper(), this));
        }
    }

    /**
     * 关闭极光推送
     */
    private void stopJPush() {
        checkFiled();
        Context context = mContext.get();
        if (!JPushInterface.isPushStopped(context)) {
            JPushInterface.stopPush(context);
        }
    }


    /**
     * 清除alias 和tags,清除通知
     */
    void cleanAliasAndTag() {
        checkFiled();
        checkState();
        mType = TYPE_CLEAN;
        Set<String> tagSet = new LinkedHashSet<>(1);
        tagSet.clear();
        Context context = mContext.get();
        JPushInterface.setAliasAndTags(context, "", tagSet, this);
        JPushInterface.clearAllNotifications(context);
    }

    /**
     * 判空
     */
    private static boolean isEmpty(String s) {
        return null == s || s.length() == 0 || s.trim().length() == 0;
    }

    // 校验Tag Alias 只能是数字,英文字母和中文
    public static boolean isValidTagAndAlias(String s) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_!@#$&*+=.|]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    // 取得版本号
    public static String GetVersion(Context context) {
        try {
            PackageInfo manager = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return manager.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }


    /**
     * 应用程序在第一次成功注册到 JPush 服务器时，
     * JPush 服务器会给客户端返回一个唯一的该设备的标识 - RegistrationID。
     * JPush SDK 会以广播的形式发送 RegistrationID 到应用程序。
     *
     * @return RegistrationID
     */
    public String getDeviceId() {
        checkFiled();
        return JPushInterface.getUdid(mContext.get());
    }


}
