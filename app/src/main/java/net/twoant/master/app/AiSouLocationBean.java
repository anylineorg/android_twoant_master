package net.twoant.master.app;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by S_Y_H on 2017/3/29.
 * 地址数据实体
 */

public final class AiSouLocationBean implements Parcelable {

    /**
     * 纬度
     */
    private double mLatitude;

    /**
     * 经度
     */
    private double mLongitude;

    /**
     * 位置是否获取成功
     */
    private boolean isGetLocationSuccessful;

    /**
     * 简单的地址，例：黄岛区月亮湾路。。。
     */
    private String mSimpleAddress;

    /**
     * 完整的地址， 例：山东省青岛市黄岛区月亮湾路。。。
     */
    private String mCompletionAddress;

    /**
     * 县、区
     */
    private String mCounty;
    /**
     * 县、区 ， 只有定位能修改它
     */
    private String mPrivateCounty;

    /**
     * 市
     */
    private String mCity;

    /**
     * 省
     */
    private String mProvince;

    /**
     * 用户主动选择的纬度
     */
    private double mSelectLatitude;
    /**
     * 用户主动选择的纬度
     */
    private double mSelectLongitude;

    /**
     * 用户主动选择的地址
     */
    private String mSelectAddress;

    /**
     * 用户选择地址
     */
    private boolean isUserSelectLocation;

    /**
     * 370201 （_ac）
     * 地区编码(自动定位的)
     */
    private String mAddressCode = AiSouUserBean.STRING_DEFAULT;
    /**
     * 370201 （_ac）
     * 地区编码(手动选择的)
     */
    private String mSelectAddressCode = AiSouUserBean.STRING_DEFAULT;

    /**
     * 城市编码 （自动选择的）
     * 0532 （_cc）
     */
    private String mCityCode = AiSouUserBean.STRING_DEFAULT;
    /**
     * 城市编码
     * 0532 （手动选择的）
     */
    private String mSelectCityCode = AiSouUserBean.STRING_DEFAULT;

    AiSouLocationBean() {

    }

    protected AiSouLocationBean(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        isGetLocationSuccessful = in.readByte() != 0;
        mSimpleAddress = in.readString();
        mCompletionAddress = in.readString();
        mCounty = in.readString();
        mPrivateCounty = in.readString();
        mCity = in.readString();
        mProvince = in.readString();
        mSelectLatitude = in.readDouble();
        mSelectLongitude = in.readDouble();
        mSelectAddress = in.readString();
        isUserSelectLocation = in.readByte() != 0;
        mAddressCode = in.readString();
        mSelectAddressCode = in.readString();
        mCityCode = in.readString();
        mSelectCityCode = in.readString();
    }

    public static final Creator<AiSouLocationBean> CREATOR = new Creator<AiSouLocationBean>() {
        @Override
        public AiSouLocationBean createFromParcel(Parcel in) {
            return new AiSouLocationBean(in);
        }

        @Override
        public AiSouLocationBean[] newArray(int size) {
            return new AiSouLocationBean[size];
        }
    };

    /**
     * @param latitude        纬度
     * @param longitude       经度
     * @param simpleAddress   简化地址
     * @param completeAddress 完整的地址
     * @param addressCode     地区编码
     * @param cityCode        城市编码
     */
    void setLatitudeAndLongitude(double latitude, double longitude, String simpleAddress, String completeAddress
            , String addressCode, String cityCode) {
        if (latitude != 0) {
            this.mLatitude = latitude;
        }
        if (longitude != 0) {
            this.mLongitude = longitude;
        }
        if (!TextUtils.isEmpty(simpleAddress)) {
            this.mSimpleAddress = simpleAddress;
        }
        if (!TextUtils.isEmpty(completeAddress)) {
            this.mCompletionAddress = completeAddress;
        }
        if (!TextUtils.isEmpty(addressCode)) {
            this.mAddressCode = addressCode;
        }
        if (!TextUtils.isEmpty(cityCode)) {
            this.mCityCode = cityCode;
        }
    }

    void setGetLocationSuccessful(boolean isSuccess) {
        this.isGetLocationSuccessful = isSuccess;
    }

    /**
     * @return 定位是否成功
     */
    public boolean getLocationSuccessfulState() {
        return this.isGetLocationSuccessful;
    }

    /**
     * 设置用户选择的地区
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param county    区域
     */
    public void setSelectLatitudeAndLongitude(double latitude, double longitude, String county, String address
            , String addressCode, String cityCode) {
        isUserSelectLocation = true;
        if (0 != latitude) {
            this.mSelectLatitude = latitude;
        }
        if (0 != longitude) {
            this.mSelectLongitude = longitude;
        }
        if (!TextUtils.isEmpty(county)) {
            this.mCounty = county;
        }
        if (!TextUtils.isEmpty(address)) {
            this.mSelectAddress = address;
        }
        if (!TextUtils.isEmpty(addressCode)) {
            this.mSelectAddressCode = addressCode;
        }
        if (!TextUtils.isEmpty(cityCode)) {
            this.mSelectCityCode = cityCode;
        }
    }

    public String getSelectAddress() {
        return this.mSelectAddress;
    }

    public double getSelectLatitude() {
        return mSelectLatitude;
    }

    public double getSelectLongitude() {
        return mSelectLongitude;
    }

    public boolean isUserSelectLocation() {
        return isUserSelectLocation;
    }

    public void setUserSelectLocation(boolean userSelectLocation) {
        isUserSelectLocation = userSelectLocation;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String mProvince) {
        this.mProvince = mProvince;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getCompletionAddress() {
        return mCompletionAddress;
    }

    /**
     * 获取当前完整地址
     */
    public String getCurrentCompletionAddress() {
        if (isUserSelectLocation) {
            return mSelectAddress;
        }
        return mCompletionAddress;
    }

    /**
     * 获取当前 城市编码
     */
    public String getCurrentCityCode() {
        if (isUserSelectLocation) {
            return this.mSelectCityCode;
        }
        return this.mCityCode;
    }

    /**
     * 获取当前 地区编码
     */
    public String getCurrentAddressCode() {
        if (isUserSelectLocation) {
            return this.mSelectAddressCode;
        }
        return this.mAddressCode;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getSimpleAddress() {
        return mSimpleAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setCounty(String county) {
        this.mCounty = county;
    }

    /**
     * 获取区县
     */
    public String getCounty() {
        return this.mCounty;
    }

    public String getPrivateCounty() {
        return mPrivateCounty;
    }

    void setPrivateCounty(String privateCounty) {
        this.mPrivateCounty = privateCounty;
        setCounty(privateCounty);
    }

    /**
     * 重置 省市县区 数据
     */
    void resetLocationData() {
        this.mCounty = null;
        this.mCity = null;
        this.mProvince = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeByte((byte) (isGetLocationSuccessful ? 1 : 0));
        dest.writeString(mSimpleAddress);
        dest.writeString(mCompletionAddress);
        dest.writeString(mCounty);
        dest.writeString(mPrivateCounty);
        dest.writeString(mCity);
        dest.writeString(mProvince);
        dest.writeDouble(mSelectLatitude);
        dest.writeDouble(mSelectLongitude);
        dest.writeString(mSelectAddress);
        dest.writeByte((byte) (isUserSelectLocation ? 1 : 0));
        dest.writeString(mAddressCode);
        dest.writeString(mSelectAddressCode);
        dest.writeString(mCityCode);
        dest.writeString(mSelectCityCode);
    }
}
