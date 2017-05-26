package net.twoant.master.ui.chat.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.activity.MerchantChatActivity;
import net.twoant.master.ui.other.activity.LoginActivity;
import com.hyphenate.EMError;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/2/18.
 * 获取用户信息的工具类
 */

public final class UserInfoUtil {


    private UserInfoUtil() {
    }

    private static StringCallback sStringCallback;
    private static HttpConnectedUtils.IOnStartNetworkSimpleCallBack mCallBack;

    /**
     * @param uid      uid
     * @param callBack 调用
     */
    public static void onDestroy(String uid, HttpConnectedUtils.IOnStartNetworkSimpleCallBack callBack) {
        if (callBack == mCallBack) {
            mCallBack = null;
        }
        OkHttpUtils.getInstance().cancelTag(uid);
    }

    /**
     * 获取单个用户uid 信息
     *
     * @param uid      用户id
     * @param callBack 回调
     */
    public static void getUserInfo(String uid, final HttpConnectedUtils.IOnStartNetworkSimpleCallBack callBack) {

        mCallBack = callBack;

        if (sStringCallback == null) {

            sStringCallback = new com.zhy.http.okhttp.callback.StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {
                    if (mCallBack != null) {
                        mCallBack.onError(call, e, id);
                    }
                    mCallBack = null;
                }

                @Override
                public void onResponse(String response, int id) {
                    if (mCallBack != null) {
                        mCallBack.onResponse(response, id);
                    }
                    mCallBack = null;
                }
            };
        }

        OkHttpUtils.post().url(ApiConstants.USER_INFO).addParams("_t", uid).tag(uid).build().execute(sStringCallback);
    }

    /**
     * 启动聊天  user 为 null 时， shopId 不能为null，相反同理。
     *
     * @param user     用户信息类
     * @param shopId   商家id
     * @param activity 活动
     */
    public static void startChat(@Nullable EaseUser user, @Nullable String shopId, @NonNull final Activity activity) {
        if (AiSouAppInfoModel.getInstance().isChatLogin()) {
            if (null != user) {
                ChatActivity.startActivity(activity, user);
            } else if (null != shopId) {
                MerchantChatActivity.startActivity(activity, shopId);
            } else {
                throw new IllegalArgumentException("user 和 shopId 都为 null");
            }
        } else {
            new AlertDialog.Builder(activity, net.twoant.master.R.style.AlertDialogStyle)
                    .setTitle(AiSouAppInfoModel.getInstance().getChatLoginDescription())
                    .setMessage(net.twoant.master.R.string.chat_not_login)
                    .setPositiveButton(net.twoant.master.R.string.chat_to_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.resetData(activity);
                            LoginActivity.startActivity(activity);
                        }
                    })
                    .setNegativeButton(net.twoant.master.R.string.chat_cancel_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
    }

    /**
     * 获取环信的错误描述
     *
     * @return 错误描述
     */
    public static String getErrorDescription(HyphenateException e, String... def) {
        switch (e.getErrorCode()) {
            case EMError.GROUP_MEMBERS_FULL://群成员已满
                return "群成员已满";
            case EMError.GROUP_ALREADY_JOINED://已经加入的群组
                return "已经加入该群";
            case EMError.NETWORK_ERROR:
                return "网络异常";
            case EMError.SERVER_BUSY:
                return "服务器繁忙";
            case EMError.SERVER_NOT_REACHABLE:
                return "无法访问到服务器";
            case EMError.SERVER_TIMEOUT:
                return "等待服务器响应超时";
            case EMError.GROUP_INVALID_ID:
                return "群id不正确";
            case EMError.GROUP_NOT_EXIST:
                return "群组不存在";
            case EMError.GROUP_NOT_JOINED:
                return "尚未加入此群组";
            case EMError.USER_NOT_FOUND:
                return "账号不存在";
            case EMError.USER_REMOVED:
                return "账号被删除";
            case EMError.INVALID_PASSWORD:
                return "账号密码错误";
            case EMError.USER_AUTHENTICATION_FAILED:
                return "账号密码错误";
            default:
                if (def.length > 0) {
                    return def[0];
                }
                return e.getDescription();
        }
    }

    public static String getErrorDescription(int code, String... hint) {
        switch (code) {
            case EMError.GROUP_MEMBERS_FULL://群成员已满
                return "群成员已满";
            case EMError.GROUP_ALREADY_JOINED://已经加入的群组
                return "已经加入该群";
            case EMError.NETWORK_ERROR:
                return "网络异常";
            case EMError.SERVER_BUSY:
                return "服务器繁忙";
            case EMError.SERVER_NOT_REACHABLE:
                return "无法访问到服务器";
            case EMError.SERVER_TIMEOUT:
                return "等待服务器响应超时";
            case EMError.GROUP_INVALID_ID:
                return "群id不正确";
            case EMError.GROUP_NOT_EXIST:
                return "群组不存在";
            case EMError.GROUP_NOT_JOINED:
                return "尚未加入此群组";
            case EMError.USER_NOT_FOUND:
                return "账号不存在";
            case EMError.USER_REMOVED:
                return "账号被删除";
            case EMError.INVALID_PASSWORD:
                return "账号密码错误";
            case EMError.USER_AUTHENTICATION_FAILED:
                return "账号密码错误";
        }
        return hint.length > 0 ? hint[0] : "错误";
    }
}
