package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CompoundButton;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.bean.PayPostOrderBean;
import net.twoant.master.ui.main.model.PayOrderTextWatcher;
import net.twoant.master.ui.main.presenter.PayOrderInfoControl;
import net.twoant.master.ui.main.presenter.ThirdPartyPayOrderControl;
import net.twoant.master.ui.main.widget.MarqueeTextView;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;
import net.twoant.master.widget.PasswordDialog;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;

/**
 * Created by S_Y_H on 2017/4/17.
 * 充值界面
 */

public class RechargeActivity extends BaseActivity implements ThirdPartyPayOrderControl.IOnOrderResultListener, View.OnClickListener, AppCompatRadioButton.OnCheckedChangeListener {
    /**
     * 积分充值
     */
    public final static int TYPE_INTEGRAL = 0xA;
    /**
     * 钱包充值
     */
    public final static int TYPE_WALLET = 0xB;
    /**
     * 联合运营充值
     */
    public final static int TYPE_COMBINED = 0xC;

    private final static String EXTRA_TYPE = "RA_E_T";
    private final static String ACTION_START = "RA_ST";

    private AppCompatRadioButton mRbWeChatPay;
    private AppCompatRadioButton mRbAliPay;
    private AppCompatRadioButton mRbWalletPay;
    private AppCompatEditText mEtInputPrice;
    private MarqueeTextView mTvHintInfo;
    private AppCompatTextView mWalletBalance;
    private AppCompatButton mBtnApplyOrder;
    private ThirdPartyPayOrderControl mPayOrderControl;
    private PayPostOrderBean mPayPostOrderBean = new PayPostOrderBean(null, null, false);
    private final BigDecimal fLimit = new BigDecimal(ApiConstants.COMBINED_LIMIT);
    private ContentLoadingProgressBar mProgressBar;
    private PasswordDialog mPasswordDialog;
    private int mType;
    /**
     * 用于 钱包支付
     */
    private PayOrderInfoControl mPayOrderInfoControl;


    /**
     * @param type 只能是这个三个其中之一 {@link #TYPE_COMBINED} {@link #TYPE_INTEGRAL} {@link #TYPE_WALLET}
     */
    public static void startActivity(Activity activity, int type) {
        Intent intent = new Intent(activity, RechargeActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
    }

    @Override
    public boolean onOrderResultListener(boolean isSuccessful, @NonNull DataRow response, int id) {
        if (isSuccessful && !RechargeActivity.this.isFinishing()) {
            mPayPostOrderBean.setCurrentPaymentPlatform(id);
        } else if (!isSuccessful) {
            resetState(true);
            ToastUtil.showShort(response.getStringDef("message", getString(net.twoant.master.R.string.payment_get_detail_fail)));
        }
        return true;
    }

    @Override
    public void onPaymentResult(int state, @NonNull String hintInfo) {
        if (RechargeActivity.this.isFinishing()) {
            return;
        }
        switch (state) {
            case ThirdPartyPayOrderControl.STATE_SUCCESSFUL:
                PaymentSuccessfulPageActivity.startActivity(RechargeActivity.this, mPayPostOrderBean);
                break;

            case ThirdPartyPayOrderControl.STATE_FAIL:
            case ThirdPartyPayOrderControl.STATE_EXCEPTION:
                ToastUtil.showShort(hintInfo);
                resetState(true);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        AppManager.addPaymentActivity(this);
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mType = intent.getIntExtra(EXTRA_TYPE, TYPE_INTEGRAL);
        return net.twoant.master.R.layout.yh_activity_recharge;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initToolBar();
        initView();
        mPayOrderControl = new ThirdPartyPayOrderControl(this, RechargeActivity.this);
        if (mType != TYPE_WALLET) {
            mPayOrderInfoControl = new PayOrderInfoControl(new RechargeInfo());
            mPayOrderInfoControl.getUserInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mPayOrderControl) {
            mPayOrderControl.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.removePaymentActivity(this);
        if (null != mPasswordDialog) {
            mPasswordDialog.dismiss();
            mPasswordDialog.onDestroy();
        }
        if (null != mPayOrderControl) {
            mPayOrderControl.onDestroy();
        }
        if (null != mPayOrderInfoControl) {
            mPayOrderInfoControl.onDestroy();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.ll_we_chat_parent:
                setChecked(mRbWeChatPay);
                break;

            case net.twoant.master.R.id.ll_ali_pay_parent:
                setChecked(mRbAliPay);
                break;

            case net.twoant.master.R.id.ll_wallet_parent:
                setChecked(mRbWalletPay);
                break;

            case net.twoant.master.R.id.btn_apply_order:
                if (mType == TYPE_COMBINED) {
                    String inputPrice = getInputPrice();
                    if (TextUtils.isEmpty(inputPrice)) {
                        return;
                    }
                    if (0 > new BigDecimal(inputPrice).compareTo(fLimit)) {
                        ToastUtil.showShort(String.format(getString(net.twoant.master.R.string.recharge_combined_limit), ApiConstants.COMBINED_LIMIT));
                        mEtInputPrice.setText(String.valueOf(ApiConstants.COMBINED_LIMIT));
                        mEtInputPrice.setSelection(mEtInputPrice.length());
                        return;
                    }
                }
                if (mRbAliPay.isChecked()) {
                    resetState(initAliPay());
                } else if (mRbWeChatPay.isChecked()) {
                    resetState(initWeChatPay());
                } else if (mRbWalletPay.isChecked()) {
                    resetState(initWalletPay());
                }
                break;

            default:
                RechargeActivity.this.finish();
                break;
        }
    }

    private boolean initWalletPay() {
        String money;
        if (TextUtils.isEmpty(money = getInputPrice())) {
            return true;
        }
        if (0 > Double.compare(mPayOrderInfoControl.getWalletSum(), Double.valueOf(money))) {
            ToastUtil.showShort(net.twoant.master.R.string.recharge_wallet_balance_deficiency);
            return true;
        }
        return checkPayPassword(money);
    }

    /**
     * 检查是否需要弹窗密码弹窗
     */
    private boolean checkPayPassword(@NonNull String price) {
        int hasPassword = mPayOrderInfoControl.isHasPassword();
        switch (hasPassword) {
            case PayOrderInfoControl.NO_PASSWORD:
                new AlertDialog.Builder(RechargeActivity.this, net.twoant.master.R.style.AlertDialogStyle)
                        .setTitle(net.twoant.master.R.string.payment_no_password_hint).setMessage(net.twoant.master.R.string.payment_set_password)
                        .setPositiveButton(net.twoant.master.R.string.payment_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(RechargeActivity.this, SetPayPasswordActivity.class);
                                RechargeActivity.this.startActivity(intent);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(net.twoant.master.R.string.payment_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).create().show();
                return true;
            case PayOrderInfoControl.FAIL_PASSWORD:
                ToastUtil.showShort(net.twoant.master.R.string.payment_data_exception);
                return true;
            case PayOrderInfoControl.HAS_PASSWORD:
                showPasswordDialog(price);
                break;
        }
        return false;
    }

    /**
     * 显示密码弹窗
     */
    private void showPasswordDialog(@NonNull String price) {
        if (null == mPasswordDialog) {
            mPasswordDialog = new PasswordDialog(RechargeActivity.this);
            mPasswordDialog.setOnCompleteInputPassword(new PasswordDialog.IOnCompleteInputPassword() {
                @Override
                public void onCompleteInputPassword(String password) {
                    mBtnApplyOrder.setTag(password);
                    mPayOrderInfoControl.checkPayPassword(password);
                }
            });
            mPasswordDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    resetState(true);
                }
            });
        }
        mPasswordDialog.setCurrentMerchantName(getDisTitle());
        SpannableString priceSpannable = new SpannableString(String.format(getString(
                mType == TYPE_INTEGRAL ? net.twoant.master.R.string.recharge_integral_hint : net.twoant.master.R.string.recharge_wallet_hint), price));
        priceSpannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(RechargeActivity.this, net.twoant.master.R.color.colorPrimary)), priceSpannable.length() - price.length(), priceSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mPasswordDialog.setCurrentPrice(priceSpannable);
        mPasswordDialog.showDialog(true, true);
    }

    /**
     * 重置状态
     *
     * @param isStart true progressBar 隐藏， btn 可点击， false 重置为显示状态
     */
    private void resetState(boolean isStart) {
        if (null != mProgressBar) {
            if (isStart) {
                mProgressBar.hide();
            } else {
                mProgressBar.show();
            }
            if (isStart != mBtnApplyOrder.isEnabled()) {
                mBtnApplyOrder.setEnabled(isStart);
            }
        }
    }

    /**
     * 初始化微信支付
     */
    private boolean initWeChatPay() {
        ArrayMap<String, String> keyValue = getParameterMap();
        if (keyValue == null) {
            return true;
        }
        switch (mType) {
            case TYPE_COMBINED:
                keyValue.put("sort", "3");
                mPayOrderControl.getWeChatOrder(keyValue, ApiConstants.WX_PAY);
                break;

            case TYPE_INTEGRAL:
                keyValue.put("sort", "0");// 0 为积分充值;1或不填为余额充值
                mPayOrderControl.getWeChatOrder(keyValue, ApiConstants.WX_PAY);//充值积分
                break;

            case TYPE_WALLET:
                keyValue.put("sort", "1");// 0 为积分充值;1或不填为余额充值
                mPayOrderControl.getWeChatOrder(keyValue, ApiConstants.WX_PAY);//充值积分
                break;
        }
        return false;
    }

    /**
     * 初始化 支付宝支付
     */
    private boolean initAliPay() {
        ArrayMap<String, String> keyValue = getParameterMap();
        if (keyValue == null) {
            return true;
        }
        switch (mType) {
            case TYPE_COMBINED:
                keyValue.put("sort", "3");
                mPayOrderControl.getAliPayOrder(keyValue, ApiConstants.ALI_GETCASH);
                break;

            case TYPE_INTEGRAL:
                keyValue.put("sort", "0");// 0 为积分充值;1或不填为余额充值
                mPayOrderControl.getAliPayOrder(keyValue, ApiConstants.ALI_GETCASH);//充值积分
                break;

            case TYPE_WALLET:
                keyValue.put("sort", "1");// 0 为积分充值;1或不填为余额充值
                mPayOrderControl.getAliPayOrder(keyValue, ApiConstants.ALI_GETCASH);//充值积分
                break;
        }
        return false;
    }

    @Nullable
    private ArrayMap<String, String> getParameterMap() {
        String money;
        if (TextUtils.isEmpty(money = getInputPrice())) {
            return null;
        }
        BigDecimal bigDecimalPrice = new BigDecimal(money);
        final String subZeroAndDot = StringUtils.subZeroAndDot(bigDecimalPrice.toString());
        if (mType == TYPE_INTEGRAL) {
            long price = bigDecimalPrice.multiply(new BigDecimal(ApiConstants.INTEGRAL_SCALE).divide(new BigDecimal(ApiConstants.WALLET_SCALE), 2, BigDecimal.ROUND_HALF_UP)).longValue();
            if (0 != price % 2) {
                price -= 1;
            }
            mPayPostOrderBean.setSumPayPriceResult(String.format(getString(net.twoant.master.R.string.payment_remain_integral), String.valueOf(price)));
        } else {
            mPayPostOrderBean.setSumPayPriceResult(String.format(getString(net.twoant.master.R.string.payment_remain_price), subZeroAndDot));
        }
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        keyValue.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        keyValue.put("price", subZeroAndDot);
        return keyValue;
    }

    /**
     * 获取当前输入的金额
     */
    @Nullable
    private String getInputPrice() {
        String input = mEtInputPrice.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            ToastUtil.showShort(net.twoant.master.R.string.recharge_error_empty_hint);
            return null;
        }
        input = PayOrderTextWatcher.checkInputNumber(input, true);
        if (-1 != BigDecimal.ZERO.compareTo(new BigDecimal(input))) {
            ToastUtil.showShort(net.twoant.master.R.string.recharge_error_format_hint);
            return null;
        }
        return input;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            keepSingleChecked(buttonView);
        }
    }

    /**
     * 保证RadioButton 单选
     */
    private void keepSingleChecked(CompoundButton buttonView) {
        if (buttonView != mRbAliPay && mRbAliPay.isChecked()) {
            mRbAliPay.setChecked(false);
        }
        if (buttonView != mRbWeChatPay && mRbWeChatPay.isChecked()) {
            mRbWeChatPay.setChecked(false);
        }
        if (buttonView != mRbWalletPay && mRbWalletPay.isChecked()) {
            mRbWalletPay.setChecked(false);
        }
    }

    private void initToolBar() {
        final String title = getDisTitle();
        mPayPostOrderBean.setShopName(title);
        ChatBaseActivity.initSimpleToolbarData(this, title, this);
    }

    /**
     * @return 返回当前 类型 对应的标题
     */
    private String getDisTitle() {
        switch (mType) {
            case TYPE_INTEGRAL:
                return getString(net.twoant.master.R.string.recharge_title_integral);
            case TYPE_WALLET:
                return getString(net.twoant.master.R.string.recharge_title_wallet);
            case TYPE_COMBINED:
                return getString(net.twoant.master.R.string.recharge_title_combined);
            default:
                return "-";
        }
    }

    private void setChecked(AppCompatRadioButton button) {
        if (!button.isChecked()) {
            button.setChecked(true);
        }
    }

    private void initView() {
        this.mWalletBalance = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_wallet_balance);
        this.mProgressBar = (ContentLoadingProgressBar) findViewById(net.twoant.master.R.id.pb_progress);
        this.mRbAliPay = (AppCompatRadioButton) findViewById(net.twoant.master.R.id.rb_ali_pay);
        this.mRbAliPay.setOnCheckedChangeListener(this);

        this.mRbWalletPay = (AppCompatRadioButton) findViewById(net.twoant.master.R.id.rb_waller_pay);
        this.mRbWalletPay.setOnCheckedChangeListener(this);

        this.mRbWeChatPay = (AppCompatRadioButton) findViewById(net.twoant.master.R.id.rb_we_chat_pay);
        this.mRbWeChatPay.setOnCheckedChangeListener(this);

        this.mEtInputPrice = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_input_price);
        this.mEtInputPrice.addTextChangedListener(new RechargeTextWatcher(getString(TYPE_INTEGRAL == mType ? net.twoant.master.R.string.recharge_integral_result_hint : net.twoant.master.R.string.recharge_result_hint)));

        this.mTvHintInfo = (MarqueeTextView) findViewById(net.twoant.master.R.id.tv_hint_info);
        this.mBtnApplyOrder = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_apply_order);

        this.mBtnApplyOrder.setOnClickListener(this);

        switch (mType) {
            case TYPE_COMBINED:
                this.mEtInputPrice.setHint(String.format(getString(net.twoant.master.R.string.recharge_combined_limit), ApiConstants.COMBINED_LIMIT));

            case TYPE_INTEGRAL:
                findViewById(net.twoant.master.R.id.ll_wallet_parent).setOnClickListener(this);
                break;

            case TYPE_WALLET:
                findViewById(net.twoant.master.R.id.ll_wallet_parent).setVisibility(View.GONE);
                break;
        }
        findViewById(net.twoant.master.R.id.ll_we_chat_parent).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ll_ali_pay_parent).setOnClickListener(this);
    }

    private class RechargeTextWatcher implements TextWatcher {

        private String mHintInfo;
        private final BigDecimal fScale = new BigDecimal(ApiConstants.INTEGRAL_SCALE).divide(new BigDecimal(ApiConstants.WALLET_SCALE), 2, BigDecimal.ROUND_DOWN);

        private RechargeTextWatcher(String hint) {
            this.mHintInfo = hint;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            final String input = s.toString();
            final String temp = PayOrderTextWatcher.checkInputZeroNumber(
                    PayOrderTextWatcher.checkInputNumber(
                            PayOrderTextWatcher.checkInputNumberLength(input), false));
            if (!input.equals(temp)) {
                mEtInputPrice.setText(temp);
                mEtInputPrice.setSelection(mEtInputPrice.length());
            } else {
                switch (mType) {
                    case TYPE_INTEGRAL:
                        mTvHintInfo.setText(String.format(mHintInfo, String.valueOf(new BigDecimal(PayOrderTextWatcher.checkInputNumber(temp, true)).multiply(fScale).longValue())));
                        break;

                    case TYPE_COMBINED:
                    case TYPE_WALLET:
                        mTvHintInfo.setText(String.format(mHintInfo, temp));
                        break;
                }
            }

        }
    }

    private class RechargeInfo implements PayOrderInfoControl.IPayOderStater {

        @Override
        public void onCheckPasswordResult(boolean isSuccessful, String errorHint) {
            if (RechargeActivity.this.isFinishing()) {
                return;
            }
            if (isSuccessful) {
                if (null != mPayOrderControl) {
                    String money;
                    if (TextUtils.isEmpty(money = getInputPrice())) {
                        resetState(true);
                        return;
                    }
                    ArrayMap<String, String> keyValue = new ArrayMap<>();
                    keyValue.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
                    keyValue.put("price", money);
                    mPayOrderControl.getOWnOrder(keyValue, ApiConstants.YU_E_GETINTEGRAL);
                }
            } else {
                ToastUtil.showShort(errorHint);
                resetState(true);
            }
        }

        @Override
        public void showDialog(String hint, boolean isError) {
           if (isError) {
                if (null != mProgressBar) {
                    mProgressBar.hide();
                }
                ToastUtil.showShort(hint);
            } else {
                resetState(false);
            }
        }

        @Override
        public void closeDialog() {
            resetState(true);
        }

        @Override
        public void inflateData() {
            if (RechargeActivity.this.isFinishing()) {
                return;
            }
            if (null != mWalletBalance) {
                mWalletBalance.setText(String.format(getString(net.twoant.master.R.string.recharge_wallet_balance), StringUtils.subZeroAndDot(String.valueOf(mPayOrderInfoControl.getWalletSum()))));
            }
        }

        @Override
        public void onCreateOrderResult(boolean isSuccessful, @Nullable DataRow dataRow) {
        }
    }
}
