package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ContentFrameLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;

import net.twoant.master.R;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.ui.main.model.PayOrderTextWatcher;
import net.twoant.master.ui.main.widget.MarqueeTextView;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;

import java.math.BigDecimal;

/**
 * Created by S_Y_H on 2017/4/12.
 * 输入密码弹窗
 */

public class PasswordDialog extends BaseDialog implements View.OnClickListener {
    private final byte[] fPassword;
    private final int fSumLength;
    private AppCompatTextView mTvPaymentPrice;
    private final static byte[] NUMBER = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private SimpleOnlyShowPasswordView mShowPasswordView;
    private IOnCompleteInputPassword iOnCompleteInputPassword;
    private net.twoant.master.ui.main.widget.MarqueeTextView mTvPaymentMerchantName;
    private ContentLoadingProgressBar mProgressBar;
    private ContentFrameLayout mProgressBarParent;
    private TableLayout mPasswordParent;
    //        private AppCompatButton mBtnNumberNone;

    public interface IOnCompleteInputPassword {
        /**
         * @param password 当前为默认MD5加密的
         */
        void onCompleteInputPassword(String password);
    }

    /**
     * @param activity 必须是activity实例
     */
    public PasswordDialog(Activity activity) {
        this(activity, 6);
    }

    private PasswordDialog(Activity context, int passwordLength) {
        super(context, Gravity.BOTTOM, true);
        fPassword = new byte[passwordLength];
        fSumLength = passwordLength;
        View view = context.getLayoutInflater().inflate(R.layout.yh_dialog_passwrod, null);
        mAlertDialog.setView(view);
        initView(view);
    }

    /**
     * 设置回调监听
     */
    public void setOnCompleteInputPassword(IOnCompleteInputPassword iOnCompleteInputPassword) {
        this.iOnCompleteInputPassword = iOnCompleteInputPassword;
    }


    /**
     * 设置支付的商店名称
     *
     * @param name 商店名
     */
    public void setCurrentMerchantName(String name) {
        SpannableString shopName = new SpannableString(String.format(mActivity.getString(R.string.payment_dialog_hint), name));
        shopName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.colorPrimary)), shopName.length() - name.length(), shopName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvPaymentMerchantName.setText(shopName);
        mTvPaymentMerchantName.setFocused(true);
    }

    /**
     * 积分和钱包一定 要有一个是有值的，否则根本没必须显示该弹窗.
     *
     * @param integral 使用的积分
     * @param price    使用的钱包
     */
    public void setCurrentPrice(String integral, String price) {
        if (TextUtils.isEmpty(price) || 0 == BigDecimal.ZERO.compareTo(new BigDecimal(PayOrderTextWatcher.checkInputNumber(price, true)))) {
            String format = String.format(mActivity.getString(R.string.payment_dialog_price_hint_integral), integral);
            SpannableString integralSpan = new SpannableString(format);
            integralSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.colorPrimary)), format.length() - integral.length(), format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvPaymentPrice.setText(integralSpan);
        } else if (TextUtils.isEmpty(integral) || 0 == BigDecimal.ZERO.compareTo(new BigDecimal(PayOrderTextWatcher.checkInputNumber(integral, true)))) {
            String format = String.format(mActivity.getString(R.string.payment_dialog_price_hint_wallet), price);
            SpannableString walletSpan = new SpannableString(format);
            walletSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.colorPrimary)), format.indexOf(price), format.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvPaymentPrice.setText(walletSpan);
        } else {
            String format = String.format(mActivity.getString(R.string.payment_dialog_price_hint), integral, price);
            SpannableString all = new SpannableString(format);
            int color = ContextCompat.getColor(mActivity, R.color.colorPrimary);
            int start = format.indexOf(integral);
            all.setSpan(new ForegroundColorSpan(color), start, start + integral.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            all.setSpan(new ForegroundColorSpan(color), start = format.indexOf(price, start + 1), start + price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvPaymentPrice.setText(all);
        }
    }

    public void setCurrentPrice(SpannableString price) {
        mTvPaymentPrice.setText(price);
    }

    public void cleanPassword() {
        if (null != mShowPasswordView) {
            mAlertDialog.setCancelable(true);
            if (mProgressBar.isShown()) {
                mProgressBar.hide();
            }
            if (View.GONE != mProgressBarParent.getVisibility()) {
                mProgressBarParent.setVisibility(View.GONE);
            }
            if (View.VISIBLE != mPasswordParent.getVisibility()) {
                mPasswordParent.setVisibility(View.VISIBLE);
            }
            mShowPasswordView.clean();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        cleanPassword();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        cleanPassword();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_number_zero:
                calculateInput(NUMBER[0]);
                break;
            case R.id.btn_number_one:
                calculateInput(NUMBER[1]);
                break;
            case R.id.btn_number_two:
                calculateInput(NUMBER[2]);
                break;
            case R.id.btn_number_three:
                calculateInput(NUMBER[3]);
                break;
            case R.id.btn_number_four:
                calculateInput(NUMBER[4]);
                break;
            case R.id.btn_number_five:
                calculateInput(NUMBER[5]);
                break;
            case R.id.btn_number_six:
                calculateInput(NUMBER[6]);
                break;
            case R.id.btn_number_seven:
                calculateInput(NUMBER[7]);
                break;
            case R.id.btn_number_eight:
                calculateInput(NUMBER[8]);
                break;
            case R.id.btn_number_nine:
                calculateInput(NUMBER[9]);
                break;
            case R.id.btn_number_delete:
                mShowPasswordView.backspace();
                break;
            case R.id.tv_forget_password:
                if (null != mActivity && View.VISIBLE != mProgressBarParent.getVisibility()) {
                    mActivity.startActivity(new Intent(mActivity, SetPayPasswordActivity.class));
                }
                break;
            case R.id.btn_close_password_dialog:
                if (!mActivity.isFinishing() && mProgressBar.isShown()) {
                    return;
                }
                cancel();
                break;
        }
    }

    public void showDialog(boolean cancelable) {
        mShowPasswordView.clean();
        super.showDialog(cancelable, false);
    }

    @Override
    public void showDialog(boolean cancelable, boolean canceledOnTouchOutside) {
        mShowPasswordView.clean();
        super.showDialog(cancelable, false);
    }

    /**
     * 统计输入的密码
     */
    private void calculateInput(byte integer) {
        int position = mShowPasswordView.getCurrentPosition();
        if (0 > position && position >= fPassword.length || 9 < integer || 0 > integer) {
            mShowPasswordView.clean();
            return;
        }
        fPassword[position++] = integer;
        mShowPasswordView.addPoint();

        if (position >= fSumLength) {
            if (null != iOnCompleteInputPassword) {
                StringBuilder passwords = new StringBuilder(fSumLength);
                for (byte password : fPassword) {
                    passwords.append(password);
                }
                if (View.VISIBLE != mProgressBarParent.getVisibility()) {
                    mProgressBarParent.setVisibility(View.VISIBLE);
                }
                if (View.INVISIBLE != mPasswordParent.getVisibility()) {
                    mPasswordParent.setVisibility(View.INVISIBLE);
                }
                mProgressBar.show();
                mAlertDialog.setCancelable(false);
                iOnCompleteInputPassword.onCompleteInputPassword(MD5Util.getMD5ToHex(passwords.toString()));
            }
        }
    }

    private void initView(View rootView) {
        this.mPasswordParent = (TableLayout) rootView.findViewById(R.id.tl_password_parent);
        this.mProgressBarParent = (ContentFrameLayout) rootView.findViewById(R.id.progress_bar_parent);
        this.mProgressBar = (ContentLoadingProgressBar) rootView.findViewById(R.id.loading_progress_bar);
        this.mShowPasswordView = (SimpleOnlyShowPasswordView) rootView.findViewById(R.id.show_password_view);
        this.mTvPaymentMerchantName = (MarqueeTextView) rootView.findViewById(R.id.tv_payment_merchant_name);
        this.mTvPaymentPrice = (AppCompatTextView) rootView.findViewById(R.id.tv_payment_price);
        rootView.findViewById(R.id.btn_number_one).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_two).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_six).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_nine).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_zero).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_four).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_five).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_three).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_seven).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_eight).setOnClickListener(this);
        rootView.findViewById(R.id.btn_number_delete).setOnClickListener(this);
        rootView.findViewById(R.id.tv_forget_password).setOnClickListener(this);
        rootView.findViewById(R.id.btn_close_password_dialog).setOnClickListener(this);
//            this.mBtnNumberNone = (AppCompatButton) rootView.findViewById(R.id.btn_number_none);
    }
}
