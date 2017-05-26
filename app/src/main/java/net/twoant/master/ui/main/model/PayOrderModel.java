package net.twoant.master.ui.main.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.common_utils.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by S_Y_H on 2017/3/24.
 * 处理支付页换算逻辑
 */

public final class PayOrderModel {

    /**
     * 待支付总额
     */
    private BigDecimal mAllPrice;
    /**
     * 钱包总额
     */
    private BigDecimal mAllWalletPrice;
    /**
     * 积分总额
     */
    private BigDecimal mAllIntegralPrice;
    /**
     * 红包价格
     */
    private BigDecimal mRedPacketPrice;
    /**
     * 积分 转 钱 的 比
     */
    private BigDecimal mIntegralTransformScale;

    /**
     * 当前输入的积分（转换成钱的）
     */
    private BigDecimal mCurrentIntegralPrice;

    /**
     * 当前输入的钱包金额
     */
    private BigDecimal mCurrentWalletPrice;

    public PayOrderModel(double integralSum, double walletSum) {
        long integral = (long) integralSum;//积分不能出现小数和 非偶数
        if (0 != integral % 2) {
            integral -= 1;
        }

        this.mAllIntegralPrice = new BigDecimal(integral);
        this.mAllWalletPrice = new BigDecimal(walletSum);
        this.mIntegralTransformScale = new BigDecimal(ApiConstants.INTEGRAL_SCALE).divide(new BigDecimal(ApiConstants.WALLET_SCALE), 2, BigDecimal.ROUND_DOWN);
        this.mCurrentIntegralPrice = BigDecimal.ZERO;
        this.mCurrentWalletPrice = BigDecimal.ZERO;
        this.mRedPacketPrice = BigDecimal.ZERO;
    }

    /**
     * 设置当前使用的积分
     */
    public void setCurrentIntegralPrice(String currentIntegral) {
        this.mCurrentIntegralPrice = getBigDecimal(currentIntegral).divide(mIntegralTransformScale, 2, BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     * @return 当前使用的积分
     */
    public BigDecimal getCurrentIntegralPrice() {
        return this.mCurrentIntegralPrice.multiply(mIntegralTransformScale);
    }

    /**
     * 设置当前使用的钱包金额
     */
    public void setCurrentWalletPrice(String currentWalletPrice) {
        this.mCurrentWalletPrice = getBigDecimal(currentWalletPrice);
    }

    /**
     * @return 当前使用的钱包金额
     */
    public BigDecimal getCurrentWalletPrice() {
        return this.mCurrentWalletPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    /**
     * 更新红包价格
     *
     * @param redPacketPrice 为null 时， 减去红包金额后的总待付金额 = 原始总待付金额
     */
    public void updateRedPacketPrice(@Nullable String redPacketPrice) {
        this.mRedPacketPrice = getBigDecimal(PayOrderTextWatcher.checkInputNumber(redPacketPrice, true));
    }

    public void updateNeedPayPrice(BigDecimal allPrice) {
        this.mAllPrice = allPrice;
    }

    /**
     * 获取积分价格
     *
     * @param integralPrice 当前输入的积分
     * @param integralText  积分输入编辑框
     * @param transformText 钱转积分编辑框
     * @param remainText    显示剩余待支付金额的 显示框
     */
    void getIntegralPrice(@NonNull String integralPrice, @NonNull EditText integralText, @NonNull EditText transformText, @NonNull TextView remainText) {
        if (!integralText.hasFocus()) {
            return;
        }
        final BigDecimal currentIntegralInput = new BigDecimal(PayOrderTextWatcher.checkInputNumber(integralPrice, true));

        //待支付 金额
        BigDecimal remainPrice = getRemainPrice(mCurrentWalletPrice);
        //待支付的 转换积分
        BigDecimal remainIntegralPrice = remainPrice.multiply(mIntegralTransformScale);

        boolean compareTo = 1 == remainIntegralPrice.compareTo(mAllIntegralPrice);
        remainIntegralPrice = compareTo ? mAllIntegralPrice : remainIntegralPrice;
        remainPrice = compareTo ? mAllIntegralPrice.divide(mIntegralTransformScale, 2, BigDecimal.ROUND_HALF_DOWN) : remainPrice;

        if (1 == BigDecimal.ZERO.compareTo(remainPrice)) {
            integralText.setTag("");
            integralText.setText(String.valueOf(0));
            transformText.setTag("");
            transformText.setText(String.valueOf(0));
            return;
        }

        //当前输入积分 比 总积分或待支付
        switch (currentIntegralInput.compareTo(remainIntegralPrice)) {
            case 1://当前输入积分 比 总积分或待支付 多
                mCurrentIntegralPrice = remainPrice;
                integralText.setTag("");
                integralText.setText(getIntegralLong(remainIntegralPrice));
                transformText.setTag("");
                transformText.setText(StringUtils.subZeroAndDot(mCurrentIntegralPrice.toString()));
                break;

            case 0://当前输入积分 比 总积分或待支付 一样
            case -1://当前输入积分 比 总积分或待支付 少
                mCurrentIntegralPrice = currentIntegralInput.divide(mIntegralTransformScale, 2, BigDecimal.ROUND_HALF_DOWN);
                transformText.setTag("");
                transformText.setText(StringUtils.subZeroAndDot(mCurrentIntegralPrice.toString()));
                break;
        }
        getRemainPrice(remainText);
    }

    void getWalletPrice(@NonNull String walletPrice, @NonNull EditText walletText, @NonNull TextView remainText) {
        if (!walletText.hasFocus()) {
            return;
        }
        final BigDecimal currentInputWallet = new BigDecimal(PayOrderTextWatcher.checkInputNumber(walletPrice, true));

        //待支付 金额
        BigDecimal remainPrice = getRemainPrice(mCurrentIntegralPrice);

        remainPrice = 1 == remainPrice.compareTo(mAllWalletPrice) ? mAllWalletPrice : remainPrice;

        if (1 == BigDecimal.ZERO.compareTo(remainPrice)) {
            walletText.setTag("");
            walletText.setText(String.valueOf(0));
            return;
        }

        //判断是否超过 拥有的钱包总额
        switch (currentInputWallet.compareTo(remainPrice)) {
            case -1: //当前输入 钱包 比 待支付或钱包余额 少
            case 0://当前输入 钱包 比 待支付或钱包余额 一样
                mCurrentWalletPrice = currentInputWallet;
                break;

            case 1://当前输入 钱包 比 待支付或钱包余额 多
                mCurrentWalletPrice = remainPrice;
                walletText.setTag("");
                walletText.setText(StringUtils.subZeroAndDot(mCurrentWalletPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()));
                break;
        }
        getRemainPrice(remainText);
    }

    void getTransformPrice(@NonNull String transformPrice, @NonNull EditText transformText, @NonNull EditText integralText, @NonNull TextView remainText) {
        if (!transformText.hasFocus()) {
            return;
        }
        final BigDecimal currentInputTransform = new BigDecimal(PayOrderTextWatcher.checkInputNumber(transformPrice, true));
        //转换后的积分
        final BigDecimal currentPrice = currentInputTransform.multiply(mIntegralTransformScale).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        //待支付 金额
        BigDecimal remainPrice = getRemainPrice(mCurrentWalletPrice);
        //待支付的 转换积分
        BigDecimal remainIntegralPrice = remainPrice.multiply(mIntegralTransformScale);

        boolean compareTo = 1 == remainIntegralPrice.compareTo(mAllIntegralPrice);
        remainIntegralPrice = compareTo ? mAllIntegralPrice : remainIntegralPrice;
        remainPrice = compareTo ? mAllIntegralPrice.divide(mIntegralTransformScale, 2, BigDecimal.ROUND_HALF_DOWN) : remainPrice;

        if (1 == BigDecimal.ZERO.compareTo(remainPrice)) {
            integralText.setTag("");
            integralText.setText(String.valueOf(0));
            transformText.setTag("");
            transformText.setText(String.valueOf(0));
            return;
        }

        //当前输入金额 转换后 比 总积分 小或相等
        switch (currentPrice.compareTo(remainIntegralPrice)) {
            case 1://当前输入 比 待支付或余额 多
                mCurrentIntegralPrice = remainPrice;
                integralText.setTag("");
                integralText.setText(getIntegralLong(remainIntegralPrice));
                transformText.setTag("");
                transformText.setText(StringUtils.subZeroAndDot(remainPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()));
                break;

            case 0://当前输入 和 待支付或余额 一样
            case -1://当前输入 比 待支付或余额 少
                mCurrentIntegralPrice = currentInputTransform;
                integralText.setTag("");
                integralText.setText(getIntegralLong(currentPrice));
                break;
        }
        getRemainPrice(remainText);
    }

    /**
     * @param other 相抵的 金额
     */
    private BigDecimal getRemainPrice(BigDecimal other) {
        return getSubtractRedPacketPrice().subtract(other);
    }

    private BigDecimal getSubtractRedPacketPrice() {
        return 0 >= BigDecimal.ZERO.compareTo(mRedPacketPrice) ? mAllPrice : mAllPrice.subtract(mRedPacketPrice);
    }

    private String getIntegralLong(@NonNull BigDecimal integral) {
        long value = integral.setScale(2, RoundingMode.HALF_DOWN).longValue();
        if (0 != value % 2) {
            value -= 1;
        }
        return String.valueOf(value);
    }

    private void getRemainPrice(@NonNull TextView textView) {
        textView.setText(StringUtils.subZeroAndDot(getSubtractRedPacketPrice().subtract(mCurrentIntegralPrice).subtract(mCurrentWalletPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
    }

    private static BigDecimal getBigDecimal(String s) {
        return new BigDecimal(PayOrderTextWatcher.checkInputNumber(PayOrderTextWatcher.checkInputNumberLength(s), true));
    }
}
