package net.twoant.master.ui.main.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import java.util.regex.Pattern;

/**
 * Created by S_Y_H on 2017/3/25.
 * 支付订单text观察器
 */

public final class PayOrderTextWatcher implements TextWatcher {

    interface ITextWatcher {
        void afterTextChanged(Editable s, @NonNull IPayOrderViewHolder viewHolder,
                              @NonNull PayOrderModel payOrderModel);
    }

    public interface IPayOrderViewHolder {

        AppCompatEditText getUseWallet();

        AppCompatEditText getUseIntegral();

        AppCompatTextView getRemainPrice();

        AppCompatEditText getUseIntegralPrice();
    }

    private IPayOrderViewHolder mViewHolder;
    private PayOrderModel mPayOrderModel;
    private ITextWatcher iTextWatcher;


    private PayOrderTextWatcher(@NonNull ITextWatcher iTextWatcher, @NonNull IPayOrderViewHolder viewHolder,
                                @NonNull PayOrderModel payOrderModel) {
        this.mViewHolder = viewHolder;
        this.mPayOrderModel = payOrderModel;
        this.iTextWatcher = iTextWatcher;
    }

    /**
     * 获取积分观察器
     */
    public static PayOrderTextWatcher getIntegralTextWatcher(@NonNull IPayOrderViewHolder viewHolder,
                                                             @NonNull PayOrderModel payOrderModel) {
        return new PayOrderTextWatcher(new IntegralTextWatcher(), viewHolder, payOrderModel);
    }

    /**
     * 获取红包观察器
     */
    @NonNull
    public static PayOrderTextWatcher getWalletTextWatcher(@NonNull IPayOrderViewHolder viewHolder,
                                                           @NonNull PayOrderModel payOrderModel) {
        return new PayOrderTextWatcher(new WalletTextWatcher(), viewHolder, payOrderModel);
    }

    /**
     * 获取钱转积分观察器
     */
    public static PayOrderTextWatcher getTransformTextWatcher(@NonNull IPayOrderViewHolder viewHolder,
                                                              @NonNull PayOrderModel payOrderModel) {
        return new PayOrderTextWatcher(new TransformTextWatcher(), viewHolder, payOrderModel);
    }

    /**
     * 检查输入的金额是否合格
     */
    @NonNull
    public static String checkInputNumberLength(String s) {
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        int indexOf = s.indexOf(".");
        if (-1 != indexOf && s.length() > indexOf + 3) {
            return s.substring(0, indexOf + 3);
        }
        return s;
    }

    public static String checkInputZeroNumber(String s) {
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        if (s.startsWith("0") && 1 < s.length()) {
            return s.substring(1, s.length());
        }
        return s;
    }

    @NonNull
    public static String checkInputNumber(String s, boolean checkPoint) {
        if (TextUtils.isEmpty(s)) {
            return "0";
        }
        if (!Pattern.matches("[0-9]*(\\.?)[0-9]*", s)) {
            return "0";
        }
        if (checkPoint && 1 == s.length() && ".".equals(s)) {
            return ".0";
        }
        return s;
    }

    public static boolean isEmptyOrZero(CharSequence s) {
        return null == s || 0 == s.length() || "0".equals(s);
    }

    /**
     * 检查积分、 钱包输入的是否不符合规定
     *
     * @return true 不符合， false 符合
     */
    private static boolean checkEditTextInputData(String transform, AppCompatEditText etUseIntegralPrice) {
        if (null != etUseIntegralPrice.getTag()) {
            etUseIntegralPrice.setTag(null);
            return true;
        }

        String temp = checkInputNumberLength(transform);
        if (!transform.equals(temp) || !transform.equals(temp = checkInputNumber(transform, false)) || !transform.equals(temp = checkInputZeroNumber(transform))) {
            etUseIntegralPrice.setText(temp);
            return true;
        }

        return 1 == transform.length() && ".".equals(transform);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        iTextWatcher.afterTextChanged(s, mViewHolder, mPayOrderModel);
    }


    /**
     * 积分观察
     */
    private static class IntegralTextWatcher implements ITextWatcher {

        @Override
        public void afterTextChanged(Editable s, @NonNull IPayOrderViewHolder viewHolder, @NonNull PayOrderModel payOrderModel) {
            final AppCompatEditText etUseWallet = viewHolder.getUseIntegral();
            String integral = s.toString();

            if (checkEditTextInputData(integral, etUseWallet)) {
                etUseWallet.setSelection(etUseWallet.length());
                return;
            }
            payOrderModel.getIntegralPrice(integral, viewHolder.getUseIntegral(),
                    viewHolder.getUseIntegralPrice(), viewHolder.getRemainPrice());
        }
    }

    /**
     * 钱包观察者
     */
    private static class WalletTextWatcher implements ITextWatcher {

        @Override
        public void afterTextChanged(Editable s, @NonNull IPayOrderViewHolder viewHolder, @NonNull PayOrderModel payOrderModel) {
            final AppCompatEditText etUseWallet = viewHolder.getUseWallet();
            String transform = s.toString();

            if (checkEditTextInputData(transform, etUseWallet)) {
                etUseWallet.setSelection(etUseWallet.length());
                return;
            }
            payOrderModel.getWalletPrice(transform, viewHolder.getUseWallet(), viewHolder.getRemainPrice());
        }
    }

    /**
     * 钱转积分
     */
    private static class TransformTextWatcher implements ITextWatcher {
        @Override
        public void afterTextChanged(Editable s, @NonNull IPayOrderViewHolder viewHolder, @NonNull PayOrderModel payOrderModel) {
            final AppCompatEditText etUseIntegralPrice = viewHolder.getUseIntegralPrice();
            String transform = s.toString();
            if (checkEditTextInputData(transform, etUseIntegralPrice)) {
                etUseIntegralPrice.setSelection(etUseIntegralPrice.length());
                return;
            }
            payOrderModel.getTransformPrice(transform, viewHolder.getUseIntegralPrice(),
                    viewHolder.getUseIntegral(), viewHolder.getRemainPrice());

        }
    }
}
