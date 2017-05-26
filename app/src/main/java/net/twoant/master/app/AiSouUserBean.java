package net.twoant.master.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/29.
 *  用户数据 实体
 */

public final class AiSouUserBean implements Parcelable {
    final static String STRING_DEFAULT = "";

    public void initData(@NonNull DataRow dataRow) {
        //token
        this.mLoginToken = dataRow.getStringDef("LOGIN_TOKEN", "");
        //真实姓名
        this.mRealName = dataRow.getStringDef("REALNAME", "");
        //手机号
        this.mPhone = dataRow.getStringDef("PHONE", "");
        //性别
        this.mSex = dataRow.getStringDef("LOGIN_TOKEN", "");
        //昵称
        this.mNickName = dataRow.getStringDef("NICK_NAME", "");
        //积分余额
        this.mIntegralBalance = dataRow.getStringDef("SCORE_BALANCE", "0");
        //个性签名
        this.mAutograph = dataRow.getStringDef("AUTOGRAPH", "这个人很懒，什么也没留下!");
        //是否已经实名认证
        this.isAuthentication = "1".equals(dataRow.getString("IDENTITY_STATUS"));
        //头像
        this.mAvatar = BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", ""));
        //年龄
        this.mAge = dataRow.getStringDef("AGE", "");
        //钱包余额
        this.mPurseBalance = dataRow.getStringDef("PURSE_BALANCE", "0");
        //注册纬度
        this.mRegisterLatitude = dataRow.getStringDef("LATITUDE", "");
        //注册经度
        this.mRegisterLongitude = dataRow.getStringDef("LONGITUDE", "");
        //登录名
        this.mLoginName = dataRow.getStringDef("LOGIN_NAME", "");
        //
        this.mAiSouID = dataRow.getStringDef("CODE", "");
        //地址
        this.mAddress = dataRow.getStringDef("ADDRESS", "");
        //管理的店铺
        DataSet shops = dataRow.getSet("SHOPS");
        this.mManageMerchantsID = new ArrayList<>();
        if (null != shops) {
            String tel;
            for (int i = 0, size = shops.size(); i < size; ++i) {
                tel = shops.getRow(i).getString("SHOP_TEL");
                mManageMerchantsID.add(tel);
            }
        }
        this.mManageMerchantsID.add(mPhone);

        //邀请人
        this.mInvitedCode = dataRow.getStringDef("INVITECODE", "");
        //超级管理员
        this.mRoleId = dataRow.getStringDef("ROLE_ID", "");
    }

    AiSouUserBean() {
        this.mRealName = STRING_DEFAULT;
        this.mPhone = STRING_DEFAULT;
        this.mSex = STRING_DEFAULT;
        this.mNickName = STRING_DEFAULT;
        this.mIntegralBalance = STRING_DEFAULT;
        this.mAutograph = STRING_DEFAULT;
        this.mAvatar = STRING_DEFAULT;
        this.mAge = STRING_DEFAULT;
        this.mPurseBalance = STRING_DEFAULT;
        this.mRegisterLatitude = STRING_DEFAULT;
        this.mRegisterLongitude = STRING_DEFAULT;
        this.mLoginName = STRING_DEFAULT;
        this.mAiSouID = STRING_DEFAULT;
        this.mAddress = STRING_DEFAULT;
        this.mLoginToken = STRING_DEFAULT;
        this.mInvitedCode = STRING_DEFAULT;
        this.isAuthentication = false;
        this.mRoleId = STRING_DEFAULT;
    }

    /**
     * 随机串
     */
    private String mLoginToken;

    /**
     * 真实姓名
     */
    private String mRealName;
    /**
     * 手机号
     */
    private String mPhone;
    /**
     * 性别
     */
    private String mSex;
    /**
     * 昵称
     */
    private String mNickName;
    /**
     * 积分总额
     */
    private String mIntegralBalance;
    /**
     * 个性签名
     */
    private String mAutograph;

    /**
     * 是否已经实名认证
     */
    private boolean isAuthentication;
    /**
     * 头像
     */
    private String mAvatar;
    /**
     * 年龄
     */
    private String mAge;
    /**
     * 钱包余额
     */
    private String mPurseBalance;
    /**
     * 注册时的纬度
     */
    private String mRegisterLatitude;
    /**
     * 注册时的经度
     */
    private String mRegisterLongitude;
    /**
     * 用于的登录的账号名
     */
    private String mLoginName;
    /**
     *
     */
    private String mAiSouID;
    /**
     * 地址
     */
    private String mAddress;
    /**
     * 管理的店铺手机号集合
     */
    private List<String> mManageMerchantsID;
    /**
     * 邀请人
     */
    private String mInvitedCode;

    /**
     * 角色 类型 id
     */
    private String mRoleId;


    private AiSouUserBean(Parcel in) {
        mLoginToken = in.readString();
        mRealName = in.readString();
        mPhone = in.readString();
        mSex = in.readString();
        mNickName = in.readString();
        mIntegralBalance = in.readString();
        mAutograph = in.readString();
        isAuthentication = in.readByte() != 0;
        mAvatar = in.readString();
        mAge = in.readString();
        mPurseBalance = in.readString();
        mRegisterLatitude = in.readString();
        mRegisterLongitude = in.readString();
        mLoginName = in.readString();
        mAiSouID = in.readString();
        mAddress = in.readString();
        mManageMerchantsID = in.createStringArrayList();
        mInvitedCode = in.readString();
        mRoleId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLoginToken);
        dest.writeString(mRealName);
        dest.writeString(mPhone);
        dest.writeString(mSex);
        dest.writeString(mNickName);
        dest.writeString(mIntegralBalance);
        dest.writeString(mAutograph);
        dest.writeByte((byte) (isAuthentication ? 1 : 0));
        dest.writeString(mAvatar);
        dest.writeString(mAge);
        dest.writeString(mPurseBalance);
        dest.writeString(mRegisterLatitude);
        dest.writeString(mRegisterLongitude);
        dest.writeString(mLoginName);
        dest.writeString(mAiSouID);
        dest.writeString(mAddress);
        dest.writeStringList(mManageMerchantsID);
        dest.writeString(mInvitedCode);
        dest.writeString(mRoleId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AiSouUserBean> CREATOR = new Creator<AiSouUserBean>() {
        @Override
        public AiSouUserBean createFromParcel(Parcel in) {
            return new AiSouUserBean(in);
        }

        @Override
        public AiSouUserBean[] newArray(int size) {
            return new AiSouUserBean[size];
        }
    };

    public String getRealName() {
        return mRealName;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getSex() {
        return mSex;
    }

    public String getNickName() {
        return mNickName;
    }

    public String getIntegralSum() {
        return mIntegralBalance;
    }

    public String getAutograph() {
        return mAutograph;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getAge() {
        return mAge;
    }

    public String getPurseBalance() {
        return mPurseBalance;
    }

    public String getRegisterLatitude() {
        return mRegisterLatitude;
    }

    public String getRegisterLongitude() {
        return mRegisterLongitude;
    }

    public String getLoginName() {
        return mLoginName;
    }

    @NonNull
    public String getAiSouID() {
        if (TextUtils.isEmpty(mAiSouID)) {//为空则进行移除账号
            AiSouAppInfoModel.getInstance().setRequestRemoveAccount(true);
            return "";//防止报空
        }
        return mAiSouID;
    }

    public String getAddress() {
        return mAddress;
    }

    public List<String> getShopsPhone() {
        return mManageMerchantsID;
    }

    public void setRealName(String mRealName) {
        this.mRealName = mRealName;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setSex(String mSex) {
        this.mSex = mSex;
    }

    public void setNickName(String mNickName) {
        this.mNickName = mNickName;
    }

    public void setIntegralSum(String mIntegralSum) {
        this.mIntegralBalance = mIntegralSum;
    }

    public void setAutograph(String mAutograph) {
        this.mAutograph = mAutograph;
    }

    public void setAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public void setAge(String mAge) {
        this.mAge = mAge;
    }

    public void setPurseBalance(String mPurseBalance) {
        this.mPurseBalance = mPurseBalance;
    }

    public void setRegisterLatitude(String mRegisterLatitude) {
        this.mRegisterLatitude = mRegisterLatitude;
    }

    public void setRegisterLongitude(String mRegisterLongitude) {
        this.mRegisterLongitude = mRegisterLongitude;
    }

    public void setLoginName(String loginName) {
        this.mLoginName = loginName;
    }

    public void setAiSouID(String aiSouID) {
        this.mAiSouID = aiSouID;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setShopsPhone(List<String> mShopsPhone) {
        this.mManageMerchantsID = mShopsPhone;
    }

    public boolean isAuthentication() {
        return isAuthentication;
    }

    public void setAuthentication(boolean authentication) {
        isAuthentication = authentication;
    }

    public String getInvitedCode() {
        return mInvitedCode;
    }

    public void setInvitedCode(String mInvitedCode) {
        this.mInvitedCode = mInvitedCode;
    }

    public String getLoginToken() {
        return mLoginToken;
    }

    public void setLoginToken(String token) {
        this.mLoginToken = token;
    }

    public String getRoleId() {
        return mRoleId;
    }
}
