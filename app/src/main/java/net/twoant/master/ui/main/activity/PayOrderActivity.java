package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.adapter.PreferentialListAdapter;
import net.twoant.master.ui.main.bean.PayOrderBean;
import net.twoant.master.ui.main.bean.PayPostOrderBean;
import net.twoant.master.ui.main.interfaces.IOnItemSelectListener;
import net.twoant.master.ui.main.model.PayOrderModel;
import net.twoant.master.ui.main.model.PayOrderTextWatcher;
import net.twoant.master.ui.main.presenter.PayOrderInfoControl;
import net.twoant.master.ui.main.presenter.ThirdPartyPayOrderControl;
import net.twoant.master.ui.main.widget.ListBottomSheetDialog;
import net.twoant.master.ui.main.widget.MarqueeTextView;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;
import net.twoant.master.widget.PasswordDialog;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/22.
 * 支付页（店内、商品）
 */
public class PayOrderActivity extends BaseActivity implements PayOrderInfoControl.IPayOderStater, View.OnClickListener {

    private final static String EXTRA_TYPE = "e_te";
    private final static String ACTION_START = "a_st";

    private ViewHolder mViewHolder;
    private PayOrderBean mPayOrderBean;
    private HintDialogUtil mHintDialog;
    private AppCompatEditText mEtPrice;
    private ViewStub mViewStubPayOrder;
    /**
     * 订单信息bean
     */
    private PayOrderModel mPayOrderModel;
    /**
     * 会员卡 优惠后的价格
     */
    private AppCompatTextView mPrizePrice;
    private AppCompatTextView mTvReceiver;
    private PasswordDialog mPassViewDialog;
    private ListBottomSheetDialog mRedPacketListDialog;
    /**
     * 订单支付 bean
     */
    private PayPostOrderBean mPayPostOrderBean;
    private PriceTextWatcher mPriceTextWatcher;//输入金额的观察器
    private PayOrderTextWatcher mWalletTextWatcher;
    private PayOrderTextWatcher mIntegerTextWatcher;
    private PayOrderInfoControl mPayOrderInfoControl;
    private PayOrderTextWatcher mTransformTextWatcher;
    /**
     * 红包数据
     */
    private ArrayList<DataRow> mRedPacketDataRows;
    /**
     * 储值卡数据
     */
    private ArrayList<DataRow> mStoredCardDataRows;
    private ViewStub mViewStubMember;
    private PreferentialListAdapter mPreferentialListAdapter;
    private String mRatePrice = "1";//10折


    public static void startActivity(@NonNull PayOrderBean payOrderBean, Activity activity) {
        Intent intent = new Intent(activity, PayOrderActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_TYPE, payOrderBean);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        AppManager.addPaymentActivity(PayOrderActivity.this);
        mPayOrderBean = intent.getParcelableExtra(EXTRA_TYPE);
        return R.layout.yh_activity_pay_order;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initToolbar();
        initView();
        initDisplayData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null == mPayOrderInfoControl) {
            mPayOrderInfoControl = new PayOrderInfoControl(PayOrderActivity.this);
        }
        if (null != mPassViewDialog && mPassViewDialog.isShowing()) {
            return;
        }
        mPayOrderInfoControl.getInfo(mPayOrderBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.removePaymentActivity(PayOrderActivity.this);
        if (null != mHintDialog) {
            mHintDialog.dismissDialog();
        }
        mIntegerTextWatcher = null;
        mWalletTextWatcher = null;
        mTransformTextWatcher = null;

        if (null != mPassViewDialog) {
            mPassViewDialog.dismiss();
            mPassViewDialog.onDestroy();
        }

        if (null != mPayOrderInfoControl) {
            mPayOrderInfoControl.onDestroy();
        }
    }

    private void initDisplayData() {
        switch (mPayOrderBean.getType()) {
            case PayOrderBean.TYPE_GOODS:
                mTvReceiver.setText(mPayOrderBean.getShopName());
                mEtPrice.setText(String.format(getString(R.string.payment_remain_price), StringUtils.subZeroAndDot(mPayOrderBean.getPrice())));
                break;

            case PayOrderBean.TYPE_INNER:
                mTvReceiver.setText(mPayOrderBean.getShopName());
                mEtPrice.setHint(R.string.payment_input_amount);
                break;

            case PayOrderBean.TYPE_SCANNER:
                mEtPrice.setHint(R.string.payment_input_amount);
                break;
        }
    }

    /**
     * 设置价格 et 的状态
     *
     * @param isEnable true 启用
     */
    private void setPriceEditState(boolean isEnable) {
        if (isEnable != mEtPrice.isEnabled()) {
            mEtPrice.setEnabled(isEnable);
        }
        if (isEnable != mEtPrice.isFocusable()) {
            mEtPrice.setFocusable(isEnable);
        }
        if (isEnable) {
            if (null == mPriceTextWatcher) {
                mPriceTextWatcher = new PriceTextWatcher();
            } else {
                mEtPrice.removeTextChangedListener(mPriceTextWatcher);
            }
            mEtPrice.addTextChangedListener(mPriceTextWatcher);
        }
    }

    /**
     * 初始化 商店 会员卡信息
     */
    private void initMemberData() {
        DataRow merchantMemberData = mPayOrderInfoControl.getMerchantMemberData();
        if (null != merchantMemberData) {

            if (null == mPrizePrice) {
                View inflate = mViewStubMember.inflate();
                mPrizePrice = (AppCompatTextView) inflate.findViewById(R.id.tv_prize_price);
                String rate = merchantMemberData.getStringDef("RATE", "1");
                try {
                    mRatePrice = new BigDecimal(rate).setScale(2, BigDecimal.ROUND_DOWN).toString();
                } catch (Exception e) {
                    mRatePrice = "1";
                }
                final String favourable = StringUtils.subZeroAndDot(new BigDecimal(mRatePrice).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN).toString());
                ((MarqueeTextView) inflate.findViewById(R.id.tv_marquee_vip_info)).setText(String.format(getString(R.string.payment_marquee_vip_info),
                        merchantMemberData.getStringDef("SHOP_NM", "-"),
                        merchantMemberData.getStringDef("LEVEL_NM", "VIP0"),
                        favourable));
                ((AppCompatTextView) inflate.findViewById(R.id.tv_current_prize)).setText(String.format(getString(R.string.payment_current_prize), favourable));
            }
            final BigDecimal needPayPrice = getNeedPayPrice();
            if (BigDecimal.ZERO.equals(needPayPrice)) {
                mPrizePrice.setText(String.format(getString(R.string.payment_favour), " "));
            } else {
                mPrizePrice.setText(String.format(getString(R.string.payment_favour), StringUtils.subZeroAndDot(needPayPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString())));
            }
        }
    }


    private class PriceTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            initHintPayData();
            if (PayOrderTextWatcher.isEmptyOrZero(s)) {
                setViewVisibilityState(mViewHolder.mLinearLayoutParent, View.GONE);
                if (null != mPrizePrice) {
                    mPrizePrice.setText(String.format(getString(R.string.payment_favour), " "));
                }
                return;
            } else {
                setViewVisibilityState(mViewHolder.mLinearLayoutParent, View.VISIBLE);
            }
            final String input = s.toString();
            final String temp = PayOrderTextWatcher.checkInputZeroNumber(
                    PayOrderTextWatcher.checkInputNumber(
                            PayOrderTextWatcher.checkInputNumberLength(input), false));
            if (!input.equals(temp)) {
                mEtPrice.setText(temp);
                mEtPrice.setSelection(mEtPrice.length());
            } else {
                mPayOrderBean.setPrice(temp);
                resetCompute(null, false);
            }
        }
    }

    /**
     * 初始化 提示性的 支付数据
     */
    private void initHintPayData() {
        if (null == mViewHolder) {
            mViewHolder = new ViewHolder(mViewStubPayOrder.inflate());
            mViewHolder.mLlRedPacketParent.setOnClickListener(PayOrderActivity.this);
            initUserData();
        }
    }

    /**
     * 显示密码弹窗
     */
    private void showPasswordDialog() {
        mViewHolder.mBtnPay.setEnabled(false);
        if (null == mPassViewDialog) {
            mPassViewDialog = new PasswordDialog(PayOrderActivity.this);
            mPassViewDialog.setOnCompleteInputPassword(new PasswordDialog.IOnCompleteInputPassword() {
                @Override
                public void onCompleteInputPassword(String password) {
                    mViewHolder.mBtnPay.setTag(password);
                    mPayOrderInfoControl.checkPayPassword(password);
                }
            });
            mPassViewDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mViewHolder.mBtnPay.setEnabled(true);
                }
            });
        }
        mPassViewDialog.setCurrentMerchantName(mPayOrderBean.getShopName());
        mPassViewDialog.setCurrentPrice(mViewHolder.mEtUseIntegral.getText().toString(), mViewHolder.mEtUseWallet.getText().toString());
        mPassViewDialog.showDialog(true, true);
    }

    /**
     * 初始化支付弹窗
     *
     * @param id 订单号
     */
    private void initPayDialog(String payPrice, String id) {
        if (TextUtils.isEmpty(payPrice) || TextUtils.isEmpty(id) || null == mPayPostOrderBean) {
            return;
        }
        mPayPostOrderBean.setOrderIdAndPrice(id, payPrice);
        PayPostOrderActivity.startActivity(PayOrderActivity.this, mPayPostOrderBean);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mHintDialog = new HintDialogUtil(this);
        mHintDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                PayOrderActivity.this.finish();
            }
        });
        this.mTvReceiver = (AppCompatTextView) findViewById(R.id.tv_receiver);
        this.mEtPrice = (AppCompatEditText) findViewById(R.id.et_price);
        this.mViewStubPayOrder = (ViewStub) findViewById(R.id.view_stub_pay_order);
        this.mViewStubMember = (ViewStub) findViewById(R.id.view_stub_member);
        ((NestedScrollView) findViewById(R.id.nsv_nested_scroll_view)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (0 != oldScrollY && scrollY > oldScrollY && mViewHolder.mEtUseWallet.hasFocus()) {
                    MainActivity.closeIME(false, mViewHolder.mEtUseWallet);
                }
            }
        });
    }

    /**
     * 设置view 的可见状态
     */
    private static void setViewVisibilityState(@NonNull View view, int state) {
        if (state != view.getVisibility()) {
            view.setVisibility(state);
        }
    }

    private void initToolbar() {
        ChatBaseActivity.initSimpleToolbarData(PayOrderActivity.this, mPayOrderBean.getType() == PayOrderBean.TYPE_INNER ? getString(R.string.payment_title_inner) : getString(R.string.payment_title_goods)
                , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PayOrderActivity.this.finish();
                    }
                });
    }

    /**
     * 检查支付密码结果回调
     */
    @Override
    public void onCheckPasswordResult(boolean isSuccessful, String errorHint) {
        if (PayOrderActivity.this.isFinishing()) {
            return;
        }

        if (isSuccessful) {
            createOrder();
        } else {
            if (null != mPassViewDialog && mPassViewDialog.isShowing()) {
                mPassViewDialog.cleanPassword();
            }
            ToastUtil.showShort(errorHint);
        }
    }

    @Override
    public void showDialog(String hint, boolean isError) {
        if (isError) {
            mHintDialog.showError(hint);
        } else {
            if (TextUtils.isEmpty(hint)) {
                mHintDialog.showLoading();
            } else {
                mHintDialog.showLoading(hint, false, false);
            }
        }
    }

    @Override
    public void closeDialog() {
        if (null != mHintDialog) {
            mHintDialog.dismissDialog();
        }
    }

    @Override
    public void inflateData() {
        if (null == mPayOrderModel) {
            mPayOrderModel = new PayOrderModel(mPayOrderInfoControl.getIntegralSum(), mPayOrderInfoControl.getWalletSum());
        }
        //会员卡
        initMemberData();
        switch (mPayOrderBean.getType()) {
            case PayOrderBean.TYPE_GOODS:
                setPriceEditState(false);
                initHintPayData();
                resetCompute(null, false);
                break;

            case PayOrderBean.TYPE_INNER:
                setPriceEditState(true);
                break;

            case PayOrderBean.TYPE_SCANNER:
                DataRow detailData = mPayOrderInfoControl.getMerchantDetailData();
                if (null == detailData) {
                    mTvReceiver.setText(R.string.payment_get_merchant_info_fail);
                    return;
                }
                mPayOrderBean.setType(PayOrderBean.TYPE_INNER);
                mTvReceiver.setText(detailData.getString("SHOP_NAME"));
                setPriceEditState(true);
                break;
        }
    }

    @Override
    public void onCreateOrderResult(boolean isSuccessful, @Nullable DataRow dataRow) {
        if (null != mHintDialog) {
            mHintDialog.dismissDialog();
        }
        /*

        {"BUYER_ID":"12","CASH_PRICE":0.15,"CASH_SCORE":10,"ID":31,"PURSE_PRICE":0.2,"SELLER_ID":"8",
      "SHOP_SELLER_ID":"8","SORT_ID":"2","TOTAL_PRICE":0.4,"VOUCHER_ID":"","VOUCHER_VAL":0}

         */

        if (isSuccessful && null != dataRow) {
            String cash_price = dataRow.getStringDef("CASH_PRICE", "0");
            BigDecimal bigDecimal = new BigDecimal(PayOrderTextWatcher.checkInputNumber(cash_price, true));
            if (null == mPayPostOrderBean) {
                mPayPostOrderBean = new PayPostOrderBean(mPayOrderBean.getShopId(), mPayOrderBean.getShopName(), dataRow.getBoolean("IS_JOIN_PRIZE", false));
            }
            if (PayOrderBean.TYPE_INNER == mPayOrderBean.getType()) {
                mPayPostOrderBean.setSumPayPriceResult(String.format(getString(R.string.payment_remain_price), new BigDecimal(PayOrderTextWatcher.checkInputNumber(mEtPrice.getText().toString(), true)).setScale(2, BigDecimal.ROUND_DOWN).toString()));
            } else {
                mPayPostOrderBean.setSumPayPriceResult(mEtPrice.getText().toString());
            }
            if (0 == BigDecimal.ZERO.compareTo(bigDecimal)) {
                mPayPostOrderBean.setCurrentPaymentPlatform(ThirdPartyPayOrderControl.ID_OWN_PLATFORM);
                mPayPostOrderBean.setOrderIdAndPrice(dataRow.getString("ID"), dataRow.getString("TOTAL_PRICE"));
                PaymentSuccessfulPageActivity.startActivity(PayOrderActivity.this, mPayPostOrderBean);
            } else {
                initPayDialog(cash_price, dataRow.getString("ID"));
            }
        } else {
            ToastUtil.showShort(null == dataRow ? getString(R.string.payment_create_fail) : dataRow.getStringDef("message", getString(R.string.payment_create_fail)));
        }
    }

    /**
     * 初始化用户数据
     */
    private void initUserData() {
        mViewHolder.mTvIntegralAll.setText(String.format(getString(R.string.payment_integral_balance), StringUtils.subZeroAndDot(String.valueOf(mPayOrderInfoControl.getIntegralSum()))));
        mViewHolder.mTvWalletBalance.setText(String.format(getString(R.string.payment_wallet_balance), StringUtils.subZeroAndDot(String.valueOf(mPayOrderInfoControl.getWalletSum()))));
        mViewHolder.mBtnPay.setOnClickListener(PayOrderActivity.this);
        if (null == mIntegerTextWatcher) {
            mIntegerTextWatcher = PayOrderTextWatcher.getIntegralTextWatcher(mViewHolder, mPayOrderModel);
        } else {
            mViewHolder.mEtUseIntegral.removeTextChangedListener(mIntegerTextWatcher);
        }
        mViewHolder.mEtUseIntegral.addTextChangedListener(mIntegerTextWatcher);

        if (null == mWalletTextWatcher) {
            mWalletTextWatcher = PayOrderTextWatcher.getWalletTextWatcher(mViewHolder, mPayOrderModel);
        } else {
            mViewHolder.mEtUseWallet.removeTextChangedListener(mWalletTextWatcher);
        }
        mViewHolder.mEtUseWallet.addTextChangedListener(mWalletTextWatcher);

        if (null == mTransformTextWatcher) {
            mTransformTextWatcher = PayOrderTextWatcher.getTransformTextWatcher(mViewHolder, mPayOrderModel);
        } else {
            mViewHolder.mEtUseIntegralPrice.addTextChangedListener(mTransformTextWatcher);
        }
        mViewHolder.mEtUseIntegralPrice.addTextChangedListener(mTransformTextWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示红包列表
            case R.id.ll_red_packet_parent:
                initRedPacketListData();
                if (((null != mRedPacketDataRows && !mRedPacketDataRows.isEmpty()) ||
                        (null != mStoredCardDataRows && !mStoredCardDataRows.isEmpty()))
                        && null != mRedPacketListDialog) {
                    mRedPacketListDialog.show();
                }
                break;
            //提交订单
            case R.id.btn_pay:
                confirmOrder();
                break;
        }
    }

    /**
     * 获取需要支付的金额
     */
    @NonNull
    private BigDecimal getNeedPayPrice() {
        String input;
        if (PayOrderBean.TYPE_GOODS == mPayOrderBean.getType()) {
            input = mPayOrderBean.getPrice();
        } else {
            input = mEtPrice.getText().toString();
        }
        if (TextUtils.isEmpty(input)) {
            return BigDecimal.ZERO;
        }
        input = PayOrderTextWatcher.checkInputNumberLength(input);
        input = PayOrderTextWatcher.checkInputNumber(input, true);
        final BigDecimal inputPriceBigDecimal = new BigDecimal(input);
        final BigDecimal favourable = new BigDecimal(mRatePrice);
        if (1 == BigDecimal.ONE.compareTo(favourable) && -1 == BigDecimal.ZERO.compareTo(favourable)) {
            final BigDecimal rate = inputPriceBigDecimal.multiply(favourable).setScale(2, RoundingMode.HALF_UP);
            if (0 <= BigDecimal.ZERO.compareTo(rate)) {
                return inputPriceBigDecimal;
            } else {
                return rate;
            }
        }
        return inputPriceBigDecimal;
    }

    /**
     * 初始化，并筛选红包列表数据
     */
    private void initRedPacketListData() {
        final BigDecimal inputPriceBigDecimal = getNeedPayPrice();
        List<DataRow> redDataRows = mPayOrderInfoControl.getRedDataRows();
        if (null != redDataRows && !redDataRows.isEmpty()) {
            initRedPacketData();
            if (null == mRedPacketDataRows) {
                mRedPacketDataRows = new ArrayList<>();
                mStoredCardDataRows = new ArrayList<>();
            } else {
                mRedPacketDataRows.clear();
                mStoredCardDataRows.clear();
            }

            for (DataRow redPacketBean : redDataRows) {
                if (null == redPacketBean) {
                    continue;
                }

                switch (redPacketBean.getInt("SORT_ID")) {
                    case 12:
                        if (-1 != inputPriceBigDecimal.compareTo(new BigDecimal(PayOrderTextWatcher.checkInputNumber(
                                redPacketBean.getStringDef("ACTIVE_VAL", "0"), true)))) {
                            mRedPacketDataRows.add(redPacketBean);
                        }
                        break;

                    case 21:
                        mStoredCardDataRows.add(redPacketBean);
                        break;
                }
            }
        }
    }

    /**
     * 创建订单
     */
    private void createOrder() {
        mViewHolder.mBtnPay.setEnabled(true);
        final Object passwordObj = mViewHolder.mBtnPay.getTag();
        String password = null;
        if (passwordObj instanceof String) {
            password = (String) passwordObj;
        }

        if (checkNeedShowPasswordDialog() && TextUtils.isEmpty(password)) {
            showPasswordDialog();
            return;
        } else if (null != mPassViewDialog && mPassViewDialog.isShowing()) {
            mPassViewDialog.dismiss();
        }

        switch (mPayOrderBean.getType()) {
            case PayOrderBean.TYPE_GOODS:
                mPayOrderInfoControl.getGoodsOrder(mPayOrderBean, mViewHolder.mEtUseIntegral.getText().toString().trim(),
                        mViewHolder.mEtUseWallet.getText().toString().trim(), password);
                break;

            case PayOrderBean.TYPE_INNER:
                mPayOrderInfoControl.getMerchantOrder(mPayOrderBean, mViewHolder.mEtUseIntegral.getText().toString().trim(),
                        mViewHolder.mEtUseWallet.getText().toString().trim(), password);
                break;
        }
    }

    /**
     * 重新进行计算
     *
     * @param redPacketBeanHigh 为null 则 选取删选后的集合的最高值红包
     * @param isClean           是否清除红包
     */
    private void resetCompute(DataRow redPacketBeanHigh, boolean isClean) {
        //待支付总金额
        BigDecimal needPayPriceBigDecimal = getNeedPayPrice();
        if (null != mPrizePrice) {
            mPrizePrice.setText(String.format(getString(R.string.payment_favour), StringUtils.subZeroAndDot(needPayPriceBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString())));
        }
        mPayOrderModel.updateNeedPayPrice(needPayPriceBigDecimal);
        needPayPriceBigDecimal = getFavorablePrice(redPacketBeanHigh, isClean, needPayPriceBigDecimal);

        //红包或者是储值已经抵扣了总金额
        if (1 == BigDecimal.ZERO.compareTo(needPayPriceBigDecimal)) {
            setPrice(0, "0", "0", "0");
            return;
        }
        subtractWalletPrice(needPayPriceBigDecimal);
    }

    /**
     * 抵扣用户钱包金额
     *
     * @param needPayPriceBigDecimal 剩余需要支付的总金额
     */
    private void subtractWalletPrice(BigDecimal needPayPriceBigDecimal) {
        //用户积分余额， 取整
        long integralSum = (long) mPayOrderInfoControl.getIntegralSum();
        //是否能被2整除
        if (0 != integralSum % 2) {
            integralSum -= 1;
        }
        //处理后的用户积分余额
        BigDecimal integerBigDecimal = new BigDecimal(integralSum);
        //用户积分余额转换后的 能 抵扣的 金额
        BigDecimal transform = integerBigDecimal.divide(new BigDecimal(ApiConstants.INTEGRAL_SCALE), 3, BigDecimal.ROUND_HALF_DOWN).multiply(new BigDecimal(ApiConstants.WALLET_SCALE)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        //用户积分所抵扣的金额 比 待支付的金额
        switch (transform.compareTo(needPayPriceBigDecimal)) {
            case 0://积分正好抵扣待支付
                setPrice(integralSum, needPayPriceBigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(), "0", "0");
                break;
            case 1://积分余额比 待支付多
                BigDecimal divide = needPayPriceBigDecimal.multiply(new BigDecimal(ApiConstants.INTEGRAL_SCALE)).divide(new BigDecimal(ApiConstants.WALLET_SCALE), 2, BigDecimal.ROUND_HALF_UP);
                long temp = divide.longValue();
                if (0 != temp % 2 && 1 + temp <= integralSum) {
                    temp += 1;
                }
                setPrice(temp, needPayPriceBigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString(), "0", "0");
                break;
            case -1://积分余额没有待支付多
                //减去积分顶掉的 得到剩余待支付的
                BigDecimal subtract = needPayPriceBigDecimal.subtract(transform).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                //钱包余额
                BigDecimal walletBigDecimal = new BigDecimal(mPayOrderInfoControl.getWalletSum());
                // 钱包余额 比 剩余待支付的
                int walletCompare = walletBigDecimal.compareTo(subtract);
                if (0 >= walletCompare) {//钱包余额 比 待支付 少或一样
                    setPrice(integralSum, transform.toString(), subtract.subtract(walletBigDecimal).setScale(2, BigDecimal.ROUND_HALF_UP).
                            toString(), walletBigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                } else if (1 == walletCompare) {//钱包余额 比 待支付 多
                    setPrice(integralSum, transform.toString(), "0", subtract.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString());
                }
                break;
        }
    }

    /**
     * 获取优惠信息
     *
     * @param redPacketBeanHigh      选中的最高项
     * @param isClean                true 清除数据， false 重新计算
     * @param needPayPriceBigDecimal 需要支付的总金额
     * @return 减去优惠后的金额
     */
    private BigDecimal getFavorablePrice(@Nullable DataRow redPacketBeanHigh, boolean isClean, BigDecimal needPayPriceBigDecimal) {
        if (isClean) {
            initRedPacketHint(null);
        } else {
            if (null == redPacketBeanHigh) {
                //初始化红包数据
                initRedPacketListData();
                BigDecimal redPriceHighBigDecimal = BigDecimal.ZERO;
                //获取红包的
                if (null != mRedPacketDataRows && !mRedPacketDataRows.isEmpty()) {//不为空，说明有可用红包
                    String temp;
                    BigDecimal tempBigDecimal;
                    for (DataRow redPacketBean : mRedPacketDataRows) {//获取最高金额的红包
                        temp = PayOrderTextWatcher.checkInputNumber(redPacketBean.getStringDef("BALANCE", "0"), true);
                        if (-1 != (tempBigDecimal = new BigDecimal(temp)).compareTo(redPriceHighBigDecimal)) {
                            redPriceHighBigDecimal = tempBigDecimal;
                            redPacketBeanHigh = redPacketBean;
                        }
                    }
                    if (null != mPreferentialListAdapter) {
                        mPreferentialListAdapter.setDataBean(mRedPacketDataRows, redPacketBeanHigh);
                    }
                }
                //获取储值卡的
                if (null != mStoredCardDataRows && !mStoredCardDataRows.isEmpty()) {
                    if (null == redPacketBeanHigh) {
                        String temp;
                        BigDecimal tempBigDecimal;
                        for (DataRow redPacketBean : mStoredCardDataRows) {//获取最高金额的红包
                            temp = PayOrderTextWatcher.checkInputNumber(redPacketBean.getStringDef("BALANCE", "0"), true);
                            if (-1 != (tempBigDecimal = new BigDecimal(temp)).compareTo(redPriceHighBigDecimal)) {
                                redPriceHighBigDecimal = tempBigDecimal;
                                redPacketBeanHigh = redPacketBean;
                            }
                        }
                        if (null != mPreferentialListAdapter) {
                            mPreferentialListAdapter.setDataBean(mStoredCardDataRows, redPacketBeanHigh);
                        }
                    } else {
                        if (null != mPreferentialListAdapter) {
                            mPreferentialListAdapter.addDataBean(mStoredCardDataRows);
                        }
                    }
                }

                //是否能被红包抵扣
                if (0 > BigDecimal.ZERO.compareTo(redPriceHighBigDecimal) && null != redPacketBeanHigh) {
                    initRedPacketHint(redPacketBeanHigh);
                    needPayPriceBigDecimal = needPayPriceBigDecimal.subtract(redPriceHighBigDecimal);//红包抵扣
                } else {//没有可用红包
                    mViewHolder.mTvRedPacketHint.setText(R.string.payment_no_red_packet);
                }

            } else {
                initRedPacketHint(redPacketBeanHigh);
                needPayPriceBigDecimal = needPayPriceBigDecimal.subtract(new BigDecimal(PayOrderTextWatcher.checkInputNumber(redPacketBeanHigh.getStringDef("BALANCE", "0"), true)));//红包抵扣
            }
        }
        return needPayPriceBigDecimal;
    }

    /**
     * 设置当前选择红包的数据
     */
    private void initRedPacketHint(@Nullable DataRow redPacketBeanHigh) {
        String balance = null;
        mPayOrderModel.updateRedPacketPrice(null == redPacketBeanHigh ? "0" : (balance = StringUtils.subZeroAndDot(redPacketBeanHigh.getStringDef("BALANCE", "0"))));
        mPayOrderBean.setRedPacketId(null == redPacketBeanHigh ? null : redPacketBeanHigh.getStringDef("ID", "-1"));
        mPayOrderBean.setRedPacketPrice(balance);
        if (null == balance) {
            mViewHolder.mTvRedPacketHint.setText("");
        } else {
            int sortId = redPacketBeanHigh.getInt("SORT_ID");
            mViewHolder.mTvRedPacketHint.setText(12 == sortId ? String.format(getString(R.string.payment_red_packet_description), balance) :
                    21 == sortId ? String.format(getString(R.string.payment_stored_hint), balance) : "");
        }
    }

    /**
     * 提交订单
     */
    private void confirmOrder() {
        //检查输入的支付金额 是否为0元（只判断店内支付）
        if (PayOrderBean.TYPE_INNER == mPayOrderBean.getType() &&
                0 <= BigDecimal.ZERO.compareTo(new BigDecimal(PayOrderTextWatcher.checkInputNumber(mEtPrice.getText().toString(), true)))) {
            ToastUtil.showShort(R.string.action_inner_input_zero);
            return;
        }

        //检查 输入的积分、钱包 是否符合要求
        if (checkIntegralAndWallet()) {
            ToastUtil.showShort(R.string.action_verification_fail);
            return;
        }

        //检查是否使用了积分和钱包
        if (checkNeedShowPasswordDialog()) {
            checkPayPassword();
            if (null == mPassViewDialog || !mPassViewDialog.isShowing()) {
                checkPayPassword();
            }
        } else {
            createOrder();
        }
    }

    /**
     * 检查输入的 积分 和钱包 的是否不合法
     *
     * @return true 不合格  FALSE 合格
     */
    private boolean checkIntegralAndWallet() {
        long currentIntegralPrice = mPayOrderModel.getCurrentIntegralPrice().longValue();
        return 0 != currentIntegralPrice % 2
                || !mViewHolder.mEtUseIntegral.getText().toString().equals(String.valueOf(currentIntegralPrice))
                || 0 != mPayOrderModel.getCurrentWalletPrice().compareTo(new BigDecimal(PayOrderTextWatcher.checkInputNumber(
                mViewHolder.mEtUseWallet.getText().toString(), true)).setScale(2, BigDecimal.ROUND_HALF_DOWN));
    }

    /**
     * 检查是否使用了积分和余额
     *
     * @return true 使用了
     */
    private boolean checkNeedShowPasswordDialog() {
        if (!TextUtils.isEmpty(mPayOrderBean.getRedPacketId())) {
            return true;
        }
        String integral = mViewHolder.mEtUseIntegral.getText().toString();
        integral = PayOrderTextWatcher.checkInputNumber(integral, true);
        String wallet = mViewHolder.mEtUseWallet.getText().toString();
        wallet = PayOrderTextWatcher.checkInputNumber(wallet, true);
        return 0 != BigDecimal.ZERO.compareTo(new BigDecimal(integral))
                || 0 != BigDecimal.ZERO.compareTo(new BigDecimal(wallet));
    }

    /**
     * 检查是否需要弹窗密码弹窗
     */
    private void checkPayPassword() {
        int hasPassword = mPayOrderInfoControl.isHasPassword();
        switch (hasPassword) {
            case PayOrderInfoControl.NO_PASSWORD:
                new AlertDialog.Builder(PayOrderActivity.this, R.style.AlertDialogStyle)
                        .setTitle(R.string.payment_no_password_hint).setMessage(R.string.payment_set_password)
                        .setPositiveButton(R.string.payment_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(PayOrderActivity.this, SetPayPasswordActivity.class);
                                PayOrderActivity.this.startActivity(intent);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.payment_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).create().show();
                break;
            case PayOrderInfoControl.FAIL_PASSWORD:
                ToastUtil.showShort(R.string.payment_data_exception);
                PayOrderActivity.this.finish();
                break;
            case PayOrderInfoControl.HAS_PASSWORD:
                showPasswordDialog();
                break;
        }
    }

    /**
     * 设置显示的状态
     *
     * @param useIntegral   积分总额
     * @param integralPrice 当前输入积分价格
     * @param remain        剩余待支付
     * @param useWallet     当前输入的钱包
     */
    private void setPrice(long useIntegral, String integralPrice, String remain, String useWallet) {
        mPayOrderModel.setCurrentIntegralPrice(String.valueOf(useIntegral));
        mPayOrderModel.setCurrentWalletPrice(useWallet);
        mViewHolder.mEtUseIntegral.setTag("");
        mViewHolder.mEtUseIntegral.setText(String.valueOf(useIntegral));
        mViewHolder.mTvRemainPrice.setText(String.valueOf(StringUtils.subZeroAndDot(remain) + "元"));
        mViewHolder.mEtUseIntegralPrice.setTag("");
        mViewHolder.mEtUseIntegralPrice.setText(StringUtils.subZeroAndDot(integralPrice));
        mViewHolder.mEtUseWallet.setTag("");
        mViewHolder.mEtUseWallet.setText(StringUtils.subZeroAndDot(useWallet));
    }

    /**
     * 初始化红包列表数据
     */
    private void initRedPacketData() {
        if (null == mRedPacketListDialog) {
            mRedPacketListDialog = new ListBottomSheetDialog(PayOrderActivity.this);
            mPreferentialListAdapter = new PreferentialListAdapter(PayOrderActivity.this);
            mRedPacketListDialog.setAdapter(mPreferentialListAdapter);
            mRedPacketListDialog.setTitle(R.string.payment_preferential);
            mRedPacketListDialog.setTvRightOperation(R.string.merchant_dialog_cancel);
            mPreferentialListAdapter.setOnItemSelectListener(new IOnItemSelectListener<DataRow>() {
                @Override
                public void onItemSelectListener(@Nullable DataRow redPacketBean) {
                    if (null != redPacketBean) {
                        initRedPacketHint(redPacketBean);
                        resetCompute(redPacketBean, false);
                    } else if (!TextUtils.isEmpty(mPayOrderBean.getRedPacketId())) {
                        resetCompute(null, true);
                    }
                    mRedPacketListDialog.dismiss();
                }

            });
            mRedPacketListDialog.setRightOperationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(mPayOrderBean.getRedPacketId())) {
                        resetCompute(null, true);
                    }
                    if (null != mPreferentialListAdapter) {
                        mPreferentialListAdapter.setCurrentPosition(-1);
                    }
                    mRedPacketListDialog.dismiss();
                }
            });
        }
    }

    public static class ViewHolder implements PayOrderTextWatcher.IPayOrderViewHolder {
        private LinearLayoutCompat mLinearLayoutParent;
        private AppCompatTextView mTvRedPacketHint;
        private LinearLayoutCompat mLlRedPacketParent;
        private AppCompatTextView mTvIntegralAll;
        private AppCompatEditText mEtUseIntegralPrice;
        //        private AppCompatTextView mTvIntegralPrice;
        private AppCompatEditText mEtUseIntegral;
        //        private LinearLayoutCompat mLlIntegralParent;
        private AppCompatTextView mTvWalletBalance;
        private AppCompatEditText mEtUseWallet;
        //        private LinearLayoutCompat mLlWalletParent;
        private AppCompatTextView mTvRemainPrice;
        private AppCompatButton mBtnPay;

        private ViewHolder(View view) {
            mLinearLayoutParent = (LinearLayoutCompat) view.findViewById(R.id.ll_parent);
            this.mTvRedPacketHint = (AppCompatTextView) view.findViewById(R.id.tv_red_packet_hint);
            this.mLlRedPacketParent = (LinearLayoutCompat) view.findViewById(R.id.ll_red_packet_parent);
            this.mTvIntegralAll = (AppCompatTextView) view.findViewById(R.id.tv_integral_all);
            this.mEtUseIntegralPrice = (AppCompatEditText) view.findViewById(R.id.et_use_integral_price);
//            this.mTvIntegralPrice = (AppCompatTextView) view.findViewById(R.id.tv_integral_price);
            this.mEtUseIntegral = (AppCompatEditText) view.findViewById(R.id.et_use_integral);
//            this.mLlIntegralParent = (LinearLayoutCompat) view.findViewById(R.id.ll_integral_parent);
            this.mTvWalletBalance = (AppCompatTextView) view.findViewById(R.id.tv_wallet_balance);
            this.mEtUseWallet = (AppCompatEditText) view.findViewById(R.id.et_use_wallet);
//            this.mLlWalletParent = (LinearLayoutCompat) view.findViewById(R.id.ll_wallet_parent);
            this.mTvRemainPrice = (AppCompatTextView) view.findViewById(R.id.tv_remain_price);
            this.mBtnPay = (AppCompatButton) view.findViewById(R.id.btn_pay);
        }

        public AppCompatEditText getUseIntegralPrice() {
            return this.mEtUseIntegralPrice;
        }

        public AppCompatEditText getUseIntegral() {
            return this.mEtUseIntegral;
        }

        public AppCompatEditText getUseWallet() {
            return this.mEtUseWallet;
        }

        public AppCompatTextView getRemainPrice() {
            return this.mTvRemainPrice;
        }
    }
}
