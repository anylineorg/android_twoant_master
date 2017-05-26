package net.twoant.master.ui.chat.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S_Y_H on 2017/3/2.
 * 作为登录环信时用来传数据的实体
 */

public class LoginUserBean implements Parcelable {

    private String mUid;
    private String mPassword;
    private String mNickName;
    private String mAvatar;

    public LoginUserBean(String mUid, String mPassword, String mNickName, String mAvatar) {
        this.mUid = mUid;
        this.mPassword = mPassword;
        this.mNickName = mNickName;
        this.mAvatar = mAvatar;
    }

    private LoginUserBean(Parcel in) {
        mUid = in.readString();
        mPassword = in.readString();
        mNickName = in.readString();
        mAvatar = in.readString();
    }

    public static final Creator<LoginUserBean> CREATOR = new Creator<LoginUserBean>() {
        @Override
        public LoginUserBean createFromParcel(Parcel in) {
            return new LoginUserBean(in);
        }

        @Override
        public LoginUserBean[] newArray(int size) {
            return new LoginUserBean[size];
        }
    };

    public String getUid() {
        return mUid;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getNickName() {
        return mNickName;
    }

    public String getAvatar() {
        return mAvatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUid);
        dest.writeString(mPassword);
        dest.writeString(mNickName);
        dest.writeString(mAvatar);
    }
}
