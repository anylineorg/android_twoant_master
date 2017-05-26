/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.domain;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hyphenate.chat.EMContact;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class EaseUser extends EMContact implements Parcelable {

    /**
     * initial letter for nickname
     */
    private String initialLetter;
    /**
     * avatar of the user
     */
    protected String avatar;

    /**
     * 性别
     */
    private String mSex;

    /**
     * 个性签名
     */
    private String mSignature;

    /**
     * 年龄
     */
    private int mAge;

    /**
     * 标识用的， 不属于用户信息
     */
    private String mAvatarMd5;


    public EaseUser(String username) {
        this.username = username;
    }

    protected EaseUser(Parcel in) {
        nick = in.readString();
        username = in.readString();
        initialLetter = in.readString();
        avatar = in.readString();
        mSex = in.readString();
        mSignature = in.readString();
        mAge = in.readInt();
        mAvatarMd5 = in.readString();
    }

    public static final Creator<EaseUser> CREATOR = new Creator<EaseUser>() {
        @Override
        public EaseUser createFromParcel(Parcel in) {
            return new EaseUser(in);
        }

        @Override
        public EaseUser[] newArray(int size) {
            return new EaseUser[size];
        }
    };

    public String getInitialLetter() {
        if (initialLetter == null) {
            EaseCommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }


    public String getSex() {
        return mSex;
    }

    public void setSex(String mSex) {
        this.mSex = mSex;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String mSignature) {
        this.mSignature = mSignature;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int mAge) {
        this.mAge = mAge;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatarMd5(String md5) {
        this.mAvatarMd5 = md5;
    }

    public String getAvatarMd5() {
        return this.mAvatarMd5;
    }

    /**
     * 用户昵称和头像 相同
     */
    public final static int INFO_SAME = 1;
    /**
     * 用户uid 相同
     */
    public final static int NAME_SAME = 0;
    /**
     * 用户信息不符
     */
    public final static int INFO_DEFAULT = -1;

    public int isEquals(EaseUser easeUser) {
        if (null != easeUser && null != this.username && this.username.equals(easeUser.getUsername())) {

            if (null != this.nick && this.nick.equals(easeUser.getNickname())
                    && null != this.mAvatarMd5 && this.mAvatarMd5.equals(easeUser.getAvatarMd5())) {
                return INFO_SAME;
            } else {
                return NAME_SAME;
            }
        }
        return INFO_DEFAULT;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int hashCode() {
        return 31 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EaseUser && this.username != null && this.username.equals(((EaseUser) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nick);
        dest.writeString(username);
        dest.writeString(initialLetter);
        dest.writeString(avatar);
        dest.writeString(mSex);
        dest.writeString(mSignature);
        dest.writeInt(mAge);
        dest.writeString(mAvatarMd5);
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(avatar) || TextUtils.isEmpty(nick) || TextUtils.isEmpty(username);
    }
}
