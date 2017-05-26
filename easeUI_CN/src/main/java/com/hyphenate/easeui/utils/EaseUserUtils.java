package com.hyphenate.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.GroupInfoBean;

public class EaseUserUtils {

    private static EaseUI.EaseUserProfileProvider userProvider;


    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    public static GroupInfoBean getGroupInfo(String groupId) {
        if (null != userProvider) {
            return userProvider.getGroup(groupId);
        }
        return null;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static boolean setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            Glide.with(context).load(user.getAvatar()).error(com.hyphenate.easeui.R.drawable.ease_default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            return true;
        } else {
            Glide.with(context).load(com.hyphenate.easeui.R.drawable.ease_default_avatar).into(imageView);
            return false;
        }
    }

    /**
     * 获取群
     */
    public static boolean setGroupAvatar(Context context, String groupId, ImageView imageView) {
        GroupInfoBean groupInfo = getGroupInfo(groupId);
        if (null != groupInfo && null != (groupId = groupInfo.getGroupAvatar())) {
            Glide.with(context).load(groupId).error(com.hyphenate.easeui.R.drawable.ease_group_icon).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().into(imageView);
            return true;
        } else {
            imageView.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
            return false;
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

}
