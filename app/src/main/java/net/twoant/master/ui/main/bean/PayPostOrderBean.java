package net.twoant.master.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S_Y_H on 2017/4/13.
 * 第三方支付数据bean
 */

public class PayPostOrderBean implements Parcelable {

    private String mShopName;
    private String mShopId;
    private String mOrderId;
    /**
     * 是否有抽奖活动
     */
    private boolean hasPrize;
    private String mNeedPayPrice;
    private String mSumPayPriceResult;
    /**
     * ID_ALI_PLATFORM， ID_WE_CHANT_PLATFORM
     */
    private int mCurrentPaymentPlatformCode;

    /**
     * 使用默认的订单完成页
     */
    private boolean mUseDefPaymentCompletePage = true;

    private PayPostOrderBean(Parcel in) {
        mShopName = in.readString();
        mShopId = in.readString();
        mOrderId = in.readString();
        hasPrize = in.readByte() != 0;
        mNeedPayPrice = in.readString();
        mCurrentPaymentPlatformCode = in.readInt();
        mUseDefPaymentCompletePage = in.readByte() != 0;
        mSumPayPriceResult = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mShopName);
        dest.writeString(mShopId);
        dest.writeString(mOrderId);
        dest.writeByte((byte) (hasPrize ? 1 : 0));
        dest.writeString(mNeedPayPrice);
        dest.writeInt(mCurrentPaymentPlatformCode);
        dest.writeByte((byte) (mUseDefPaymentCompletePage ? 1 : 0));
        dest.writeString(mSumPayPriceResult);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PayPostOrderBean> CREATOR = new Creator<PayPostOrderBean>() {
        @Override
        public PayPostOrderBean createFromParcel(Parcel in) {
            return new PayPostOrderBean(in);
        }

        @Override
        public PayPostOrderBean[] newArray(int size) {
            return new PayPostOrderBean[size];
        }
    };

    public void setOrderIdAndPrice(String orderId, String needPayPrice) {
        this.mOrderId = orderId;
        this.mNeedPayPrice = needPayPrice;
    }

    public void setSumPayPriceResult(String result) {
        this.mSumPayPriceResult = result;
    }

    public String getSumPayPriceResult() {
        return this.mSumPayPriceResult;
    }

    public String getShopName() {
        return this.mShopName;
    }

    public PayPostOrderBean(String shopId, String shopName, boolean hasPrize) {
        this.mShopId = shopId;
        this.mShopName = shopName;
        this.hasPrize = hasPrize;
    }

    public void setShopName(String shopName) {
        this.mShopName = shopName;
    }

    public int getCurrentPaymentPlatform() {
        return this.mCurrentPaymentPlatformCode;
    }

    public void setCurrentPaymentPlatform(int mCurrentPaymentPlatform) {
        this.mCurrentPaymentPlatformCode = mCurrentPaymentPlatform;
    }

    public String getOrderId() {
        return this.mOrderId;
    }

    public String getNeedPayPrice() {
        return this.mNeedPayPrice;
    }

    public boolean isUseDefPaymentCompletePage() {
        return this.mUseDefPaymentCompletePage;
    }

    public void setUseDefPaymentCompletePage(boolean useDef) {
        this.mUseDefPaymentCompletePage = useDef;
    }

    public void setHasPrize(boolean hasPrize) {
        this.hasPrize = hasPrize;
    }

    public boolean isHasPrize() {
        return this.hasPrize;
    }
}
