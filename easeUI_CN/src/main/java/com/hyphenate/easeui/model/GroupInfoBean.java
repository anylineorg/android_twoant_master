package com.hyphenate.easeui.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S_Y_H on 2017/2/27.
 * 群资料信息实体
 */

public class GroupInfoBean implements Parcelable {

    private String mGroupName;

    private String mGroupAvatar;

    private String mGroupAvatarMD5;

    private String mGroupId;

    public GroupInfoBean(String groupId) {
        this.mGroupId = groupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String mGroupName) {
        this.mGroupName = mGroupName;
    }

    public String getGroupAvatar() {
        return mGroupAvatar;
    }

    public void setGroupAvatar(String mGroupAvatar) {
        this.mGroupAvatar = mGroupAvatar;
    }

    public String getGroupAvatarMD5() {
        return mGroupAvatarMD5;
    }

    public void setGroupAvatarMD5(String mGroupAvatarMD5) {
        this.mGroupAvatarMD5 = mGroupAvatarMD5;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String mGroupId) {
        this.mGroupId = mGroupId;
    }

    private GroupInfoBean(Parcel in) {
        mGroupName = in.readString();
        mGroupAvatar = in.readString();
        mGroupId = in.readString();
        mGroupAvatarMD5 = in.readString();
    }

    public static final Creator<GroupInfoBean> CREATOR = new Creator<GroupInfoBean>() {
        @Override
        public GroupInfoBean createFromParcel(Parcel in) {
            return new GroupInfoBean(in);
        }

        @Override
        public GroupInfoBean[] newArray(int size) {
            return new GroupInfoBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mGroupName);
        dest.writeString(mGroupAvatar);
        dest.writeString(mGroupId);
        dest.writeString(mGroupAvatarMD5);
    }
}
