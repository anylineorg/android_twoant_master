package net.twoant.master.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import net.twoant.master.api.ApiConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/27.
 * 活动支付 model
 */

public class PaymentDataBean implements Parcelable {
//*-*-*-*-*-*-*-*-*-*-*-*-*-* 需要被进行传输的数据 start *-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    /**
     * 活动总积分
     */
    private double mIntegralSum;
    /**
     * 钱包总额
     */
    private double mWalletSum;
    /**
     * 报名 输入的姓名
     */
    private String mName;
    /**
     * 报名 输入的电话
     */
    private String mPhone;
    private String mShopId;
    private String mShopName;

    /**
     * 活动要求积分
     */
    private double mRequestIntegral;


//*-*-*-*-*-*-*-*-*-*-*-*-*-* 需要被进行传输的数据 end *-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

    /**
     * 条目Id
     */
    private List<String> mActivityIdList;
    /**
     * 用户选择使用的积分所代表的RMB金额 （积分与RMB换算后的金额）
     */
    private BigDecimal mUseIntegralSum;

    /**
     * 精确的用户积分
     */
    private BigDecimal mExactUseIntegralSum;

    /**
     * 用户选择使用的钱包金额
     */
    private BigDecimal mUseWalletSum;

    /**
     * 用户需要支付的价格
     */
    private BigDecimal mNeedPayPrice;

    /**
     * 用户钱包总金额（如 当前活动的价格超过用户钱包金额， 则总金额为用户钱包金额，反之为活动价格）
     */
    private BigDecimal mUserAllWallet;

    private BigDecimal mTempBigDecimal;

    /**
     * 用户能使用的积分总额（减去了 活动要求积分， 不是用户积分总额）
     */
    private BigDecimal mUserAllIntegral;

    /**
     * 积分活动 积分
     */
    private BigDecimal mIntegralActivityPrice;

    private BigDecimal mIntegralTransform;

    /**
     * 积分 或者 是 钱的 总额描述
     */
    private String mFinalSumDescription;


    public PaymentDataBean() {
    }

    public double getIntegralSum() {
        return mIntegralSum;
    }

    public void setIntegralSum(double mIntegralSum) {
        this.mIntegralSum = mIntegralSum;
    }

    public BigDecimal getIntegralTransform() {
        return this.mIntegralTransform;
    }

    /**
     * 设置 需要支付的价格 （RMB）
     */
    public void setNeedPayPrice(BigDecimal bigDecimal) {
        this.mNeedPayPrice = bigDecimal;
        this.mIntegralTransform = new BigDecimal(ApiConstants.INTEGRAL_SCALE).divide(new BigDecimal(ApiConstants.WALLET_SCALE), 2, BigDecimal.ROUND_HALF_DOWN);
        if (mUserAllIntegral == null) {
            BigDecimal request = new BigDecimal(mRequestIntegral);
            //减去活动积分要求
            long integralSum = (long) mIntegralSum;
            if (0 != integralSum % 2) {
                integralSum -= 1;
            }
            mUserAllIntegral = new BigDecimal(integralSum).subtract(request).setScale(2, RoundingMode.HALF_DOWN);
            //获取对应的需要支付的 钱数的 对应积分数额
            BigDecimal requestIntegral = mNeedPayPrice.multiply(mIntegralTransform).setScale(2, RoundingMode.HALF_UP);
            //如果当前活动的价格没有用户积分总额高，则积分总数应为活动价对应的积分
            if (requestIntegral.subtract(request).compareTo(mUserAllIntegral) != 1) {
                mUserAllIntegral = requestIntegral;
            }
        }

        if (mUserAllWallet == null) {
            mUserAllWallet = new BigDecimal(mWalletSum);
            if (mUserAllWallet.compareTo(bigDecimal) == 1) {
                mUserAllWallet = bigDecimal;
            }
        }
    }

    /**
     * 获取上一次 剩余积分总数
     */
    public BigDecimal getFinalIntegral() {

        if (mUserAllIntegral == null) return mUserAllIntegral = new BigDecimal(0);

        if (mUseWalletSum == null)
            mUseWalletSum = new BigDecimal(0.00);

        //需要支付的金额 - 钱包抵掉的金额
        mTempBigDecimal = mNeedPayPrice.subtract(mUseWalletSum);

        //剩余需要积分抵扣的金额小于或等于0
        if (mTempBigDecimal.compareTo(BigDecimal.ZERO) != 1) {
            return BigDecimal.ZERO;
        }
        //算出剩余金额对应的积分
        mTempBigDecimal = mTempBigDecimal.multiply(mIntegralTransform).setScale(2, RoundingMode.HALF_DOWN);

        //积分总额 不小于 剩余的
        if (mTempBigDecimal.compareTo(mUserAllIntegral) != 1) {
            return mTempBigDecimal;
        }

        // 积分总额小于 剩余需要支付的
        return mUserAllIntegral;
    }


    /**
     * 获取上次钱余额总数
     */
    public BigDecimal getFinalWallet() {
        if (mUserAllWallet == null)
            return BigDecimal.ZERO;

        if (mUseIntegralSum == null)
            mUseIntegralSum = new BigDecimal(0.00);

        // 需要支付的金额 - 输入积分抵掉的金额
        mTempBigDecimal = mNeedPayPrice.subtract(mUseIntegralSum);

        //剩余需要支付的金额小于或等于零
        if (mTempBigDecimal.compareTo(BigDecimal.ZERO) != 1) {
            return BigDecimal.ZERO;
        }

        //剩余需要的支付金额 小于或等于 用户总钱包金额
        if (mTempBigDecimal.compareTo(mUserAllWallet) != 1) {
            return mTempBigDecimal;
        }

        //直接返回用户钱包总金额
        return mUserAllWallet;

    }


    public BigDecimal getFinalNeedPayPrice(BigDecimal bigDecimal, boolean isIntegral) {
        if (isIntegral) {
            mUseIntegralSum = bigDecimal;
        } else {
            mUseWalletSum = bigDecimal;
        }

        if (mUseIntegralSum == null)
            mUseIntegralSum = new BigDecimal(0.00);

        if (mUseWalletSum == null)
            mUseWalletSum = new BigDecimal(0.00);

        return mNeedPayPrice.subtract(mUseIntegralSum).subtract(mUseWalletSum).setScale(2, RoundingMode.HALF_UP);
    }

    public double getWalletSum() {
        return mWalletSum;
    }

    public void setWalletSum(double mWalletSum) {
        this.mWalletSum = mWalletSum;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setExactUseIntegralSum(BigDecimal bigDecimal) {
        this.mExactUseIntegralSum = bigDecimal;
    }

    /**
     * @return 最终用户选择的积分 和 活动所需积分（如果有）；
     * TODO 现在积分为int 类型
     */
    public String getFinalUseIntegralSum() {
        if (mRequestIntegral > 0) {
            if (mExactUseIntegralSum != null) {
                return String.valueOf(mExactUseIntegralSum.add(new BigDecimal(mRequestIntegral)).setScale(2, RoundingMode.HALF_UP).longValue());
            } else {
                return String.valueOf((int) mRequestIntegral);
            }
        } else if (mExactUseIntegralSum != null) {
            return String.valueOf(mExactUseIntegralSum.setScale(2, BigDecimal.ROUND_HALF_UP).longValue());
        } else
            return "0";
    }

    public List<String> getActivityId() {
        return mActivityIdList;
    }

    public void setActivityId(String activityId) {

        if (mActivityIdList == null)
            mActivityIdList = new ArrayList<>();

        if (!mActivityIdList.contains(activityId))
            mActivityIdList.add(activityId);
    }

    public String getShopId() {
        return mShopId;
    }

    public String getShopName() {
        return this.mShopName;
    }

    public void setShopId(String mShopId) {
        this.mShopId = mShopId;
    }

    public void setShopName(String shopName) {
        this.mShopName = shopName;
    }

    public String getUseWalletSumString() {
        return mUseWalletSum == null ? "0" : mUseWalletSum.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public double getRequestIntegral() {
        return mRequestIntegral;
    }

    public void setRequestIntegral(double mRequestIntegral) {
        this.mRequestIntegral = mRequestIntegral;
    }

    private PaymentDataBean(Parcel in) {
        mIntegralSum = in.readDouble();
        mWalletSum = in.readDouble();
        mName = in.readString();
        mPhone = in.readString();
        mRequestIntegral = in.readDouble();
        mShopId = in.readString();
        mShopName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mIntegralSum);
        dest.writeDouble(mWalletSum);
        dest.writeString(mName);
        dest.writeString(mPhone);
        dest.writeDouble(mRequestIntegral);
        dest.writeString(mShopId);
        dest.writeString(mShopName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentDataBean> CREATOR = new Creator<PaymentDataBean>() {
        @Override
        public PaymentDataBean createFromParcel(Parcel in) {
            return new PaymentDataBean(in);
        }

        @Override
        public PaymentDataBean[] newArray(int size) {
            return new PaymentDataBean[size];
        }
    };

    public void setIntegralActivityPrice(BigDecimal integralActivityPrice) {
        this.mIntegralActivityPrice = integralActivityPrice;
    }

    public BigDecimal getIntegralActivityPrice() {
        if (null ==  mIntegralActivityPrice) {
            this.mIntegralActivityPrice = BigDecimal.ZERO;
        }
        return this.mIntegralActivityPrice;
    }

    public void setFinalSumDescription(String finalSumDescription) {
        this.mFinalSumDescription = finalSumDescription;
    }

    public String getFinalSumDescription() {
        return this.mFinalSumDescription;
    }
}
