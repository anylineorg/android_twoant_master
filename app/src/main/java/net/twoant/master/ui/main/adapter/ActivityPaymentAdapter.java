package net.twoant.master.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.adapter.holder.ActivityPaymentViewHolder;
import net.twoant.master.ui.main.bean.ActionDetailBean;
import net.twoant.master.ui.main.bean.PaymentDataBean;
import net.twoant.master.ui.main.interfaces.IOnPayBtnClickListener;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.model.PayOrderTextWatcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/24.
 * 活动支付页的适配器
 */

public class ActivityPaymentAdapter extends RecyclerView.Adapter<ActivityPaymentViewHolder> implements View.OnClickListener {

    public final static int ITEM = 0;
    private final static int COMMON = 1;

    private List<ActionDetailBean.DataBean.ITEMSBean> mDataSet;
    private Context mContext;
    private String mShopName;
    //活动类型
    private int mKind = -1;
    //价格数据
    private PaymentDataBean mPaymentDataBean;
    //积分观察器
    private IntegralTextWatcher mIntegralTextWatch;
    //积分换算钱
    private TranslateTextWatcher mTranslateTextWatcher;
    //钱包观察器
    private WalletTextWatcher mWalletTextWatch;

    private ActivityPaymentViewHolder mActivityPaymentViewHolder;
    //支付回调
    private IOnPayBtnClickListener iOnPayBtnClickListener;


    public void setOnPayBtnClickListener(IOnPayBtnClickListener iOnPayBtnClickListener) {
        this.iOnPayBtnClickListener = iOnPayBtnClickListener;
    }

    public ActivityPaymentAdapter(@NonNull List<ActionDetailBean.DataBean.ITEMSBean> dataSet, PaymentDataBean paymentDataBean) {
        this.mDataSet = dataSet;
        mContext = AiSouAppInfoModel.getAppContext();
        mPaymentDataBean = paymentDataBean;
    }

    @Override
    public ActivityPaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            return new ActivityPaymentViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_activity_pay, parent, false), viewType);
        }
        return new ActivityPaymentViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_activity_pay_info, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(ActivityPaymentViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM) {
            ActionDetailBean.DataBean.ITEMSBean itemsBean = mDataSet.get(position);
            if (itemsBean != null) {
                if (mShopName == null)
                    mShopName = itemsBean.getSHOP_NM();

                if (mKind == -1)
                    mKind = itemsBean.getACTIVITY_SORT_ID();

                ImageLoader.getImageFromNetworkPlaceholderControlImg(holder.getIvImg(), BaseConfig.getCorrectImageUrl(itemsBean.getACTIVITY_COVER_IMG()), mContext, net.twoant.master.R.drawable.ic_def_large);

                mPaymentDataBean.setActivityId(itemsBean.getID());

                if (mKind == IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY) {

                    holder.getTvItemPrice().setText(String.valueOf(itemsBean.getSCORE() + "积分"));
                } else {
                    holder.getTvItemPrice().setText(String.valueOf("￥" + itemsBean.getPRICE()));
                }
                holder.getTvDescription().setText(itemsBean.getTITLE());
                String activity_start_time = itemsBean.getACTIVITY_START_TIME();
                activity_start_time = activity_start_time == null ? "" : activity_start_time.substring(0, activity_start_time.lastIndexOf(":"));
                holder.getTvStartDate().setText(String.valueOf(activity_start_time + "开始"));
            }
        } else {
            holder.getTvReceiver().setText(mShopName == null ? "" : mShopName);

            if (mKind == IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY) {
                BigDecimal bigDecimal = new BigDecimal(.00);
                for (ActionDetailBean.DataBean.ITEMSBean items : mDataSet) {
                    bigDecimal = bigDecimal.add(new BigDecimal(items.getSCORE()));
                }
                mPaymentDataBean.setIntegralActivityPrice(bigDecimal);
                AppCompatEditText etUseWallet = holder.getUseWallet();
                String allIntegral = StringUtils.subZeroAndDot(bigDecimal.setScale(2, RoundingMode.HALF_UP).toString()) + "积分";
                mPaymentDataBean.setFinalSumDescription(allIntegral);
                holder.getTvPrice().setText(allIntegral);
                //设置积分不可输入
                if (holder.getUseIntegral().isEnabled()) {
                    holder.getUseIntegral().setEnabled(false);
                }
                if (holder.getUseIntegralPrice().isEnabled()) {
                    holder.getUseIntegralPrice().setEnabled(false);
                }
                if (View.GONE != holder.getIntegralParentLayout().getVisibility()) {
                    holder.getIntegralParentLayout().setVisibility(View.GONE);
                }
                //设置钱包不可输入
                if (etUseWallet.isEnabled()) {
                    etUseWallet.setEnabled(false);
                }
                holder.getRemainPrice().setText(allIntegral);
                if (View.GONE != holder.getWalletParentLayout().getVisibility()) {
                    holder.getWalletParentLayout().setVisibility(View.GONE);
                }

            } else {

                BigDecimal allPriceBigDecimal = new BigDecimal(.00);
                String price;
                for (ActionDetailBean.DataBean.ITEMSBean items : mDataSet) {
                    if (items != null && (price = items.getPRICE()) != null)
                        allPriceBigDecimal = allPriceBigDecimal.add(new BigDecimal(price));
                }
                allPriceBigDecimal = allPriceBigDecimal.setScale(2, RoundingMode.HALF_UP);
                mPaymentDataBean.setNeedPayPrice(allPriceBigDecimal);

                //钱包
                initWalletData(holder);
                //积分
                initIntegralData(holder);

                final String allPrice = StringUtils.subZeroAndDot(allPriceBigDecimal.toString());
                holder.getRemainPrice().setText(String.valueOf(allPrice + "元"));
                mPaymentDataBean.setFinalSumDescription(String.valueOf(allPrice + "元"));
                holder.getTvPrice().setText(String.valueOf(allPrice + "元"));
                mActivityPaymentViewHolder = holder;

                mActivityPaymentViewHolder.getUseIntegral().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mActivityPaymentViewHolder) {
                            mActivityPaymentViewHolder.getUseIntegralPrice().setText(allPrice);
                            mActivityPaymentViewHolder.getUseIntegralPrice();
                            mActivityPaymentViewHolder.getUseWallet().setText(String.valueOf(mPaymentDataBean.getWalletSum()));
                        }
                    }
                }, 1000);
            }
            holder.getTvIntegralAll().setText(String.valueOf("积分余额: " + StringUtils.subZeroAndDot(new BigDecimal(mPaymentDataBean.getIntegralSum()).subtract(new BigDecimal(mPaymentDataBean.getRequestIntegral())).setScale(2, RoundingMode.HALF_DOWN).toString())));
            holder.getTvWalletBalance().setText(String.valueOf("钱包余额: " + StringUtils.subZeroAndDot(new BigDecimal(mPaymentDataBean.getWalletSum()).setScale(2, RoundingMode.HALF_DOWN).toString())));
            holder.getBtnPay().setOnClickListener(this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mDataSet.size())
            return ITEM;
        return COMMON;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size() + 1;
    }

    @Override
    public void onViewAttachedToWindow(ActivityPaymentViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null == mActivityPaymentViewHolder && holder.getAdapterPosition() == mDataSet.size()) {
            mActivityPaymentViewHolder = holder;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ActivityPaymentViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mActivityPaymentViewHolder != null && mActivityPaymentViewHolder.getAdapterPosition() == holder.getAdapterPosition()) {
            mActivityPaymentViewHolder = null;
        }
    }

    @Override
    public void onClick(View v) {
        applyOrder(true);
    }

    /**
     * 提交订单
     */
    public void applyOrder() {
        applyOrder(false);
    }

    public void onDestroy() {

        if (mIntegralTextWatch != null) {
            mIntegralTextWatch.onDestroy();
            mIntegralTextWatch = null;
        }

        mActivityPaymentViewHolder = null;
        mPaymentDataBean = null;
        iOnPayBtnClickListener = null;
    }

    public static boolean isEmpty(String str) {
        try {
            return null == str || 0 == str.length() || 0 == BigDecimal.ZERO.compareTo(new BigDecimal(str));
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 提交订单
     *
     * @param isCheckPassword 是否检查支付密码 true 检查
     */
    private void applyOrder(boolean isCheckPassword) {
        if (iOnPayBtnClickListener != null && null != mActivityPaymentViewHolder) {

            AppCompatEditText useIntegral = mActivityPaymentViewHolder.getUseIntegral();
            AppCompatEditText useWallet = mActivityPaymentViewHolder.getUseWallet();
            if (null == useIntegral || null == useWallet) {
                ToastUtil.showShort(net.twoant.master.R.string.action_verification_fail);
                return;
            }


            String integralSum = mPaymentDataBean.getFinalUseIntegralSum();
            String wallet = mPaymentDataBean.getUseWalletSumString();

            if ((!isEmpty(integralSum) && !integralSum.equals(useIntegral.getText().toString()))
                    || (!isEmpty(wallet) && 0 != new BigDecimal(wallet).compareTo(new BigDecimal(useWallet.getText().toString())))) {
                ToastUtil.showShort(net.twoant.master.R.string.action_verification_fail);
                return;
            }

            if (isCheckPassword &&
                    (mKind == IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY || !isEmpty(integralSum) || !isEmpty(wallet))) {
                iOnPayBtnClickListener.showPasswordDialog(
                        mKind == IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY ?
                                StringUtils.subZeroAndDot(mPaymentDataBean.getIntegralActivityPrice()
                                        .setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                                : integralSum, wallet);
            } else {
                iOnPayBtnClickListener.onPayBtnClickListener(integralSum, wallet, mPaymentDataBean.getShopId(), mPaymentDataBean.getActivityId());
            }

        }
    }

    /**
     * 钱包 输入 观察器
     */
    private class WalletTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String temp = s.toString();
            if (mActivityPaymentViewHolder != null) {
                AppCompatEditText wallet = mActivityPaymentViewHolder.getUseWallet();
                if (null != wallet.getTag()) {
                    wallet.setTag(null);
                    wallet.setSelection(wallet.length());
                }

                if (!wallet.hasFocus()) {
                    return;
                }

                String number = PayOrderTextWatcher.checkInputNumber(PayOrderTextWatcher.checkInputZeroNumber(PayOrderTextWatcher.checkInputNumberLength(temp)), false);

                if (PayOrderTextWatcher.isEmptyOrZero(number)) {
                    mActivityPaymentViewHolder.getRemainPrice().setText(String.valueOf("￥" +
                            StringUtils.subZeroAndDot(mPaymentDataBean.getFinalNeedPayPrice(BigDecimal.ZERO, false).toString())));
                }

                if (!temp.equals(number)) {
                    wallet.setTag("");
                    wallet.setText(number);
                    return;
                }

                final BigDecimal finalWallet = mPaymentDataBean.getFinalWallet();
                BigDecimal bigDecimal = new BigDecimal(PayOrderTextWatcher.checkInputNumber(temp, true));
                if (bigDecimal.compareTo(finalWallet) != 1) {
                    mActivityPaymentViewHolder.getRemainPrice().setText(String.valueOf("￥" +
                            StringUtils.subZeroAndDot(mPaymentDataBean.getFinalNeedPayPrice(bigDecimal, false).toString())));
                } else {
                    mActivityPaymentViewHolder.getRemainPrice().setText(String.valueOf("￥" +
                            StringUtils.subZeroAndDot(mPaymentDataBean.getFinalNeedPayPrice(finalWallet, false).toString())));
                    wallet.setTag("");
                    wallet.setText(StringUtils.subZeroAndDot(finalWallet.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString()));
                }
            }
        }
    }


    /**
     * 初始化 非积分活动的 钱包数据
     */
    private void initWalletData(ActivityPaymentViewHolder holder) {
        if (mWalletTextWatch == null) {
            mWalletTextWatch = new WalletTextWatcher();
        }
        if (View.VISIBLE != holder.getWalletParentLayout().getVisibility()) {
            holder.getWalletParentLayout().setVisibility(View.VISIBLE);
        }

        AppCompatEditText etUseWallet = holder.getUseWallet();
        if (!etUseWallet.isEnabled()) {
            etUseWallet.setEnabled(true);
        }
        etUseWallet.removeTextChangedListener(mWalletTextWatch);
        etUseWallet.addTextChangedListener(mWalletTextWatch);
    }

    /**
     * 初始化非积分活动 积分数据
     */
    private void initIntegralData(ActivityPaymentViewHolder holder) {
        if (View.VISIBLE != holder.getIntegralParentLayout().getVisibility()) {
            holder.getIntegralParentLayout().setVisibility(View.VISIBLE);
        }

        AppCompatEditText etUseIntegral = holder.getUseIntegral();
        AppCompatEditText etTransitionIntegral = holder.getUseIntegralPrice();

        if (etUseIntegral.isEnabled()) {
            etUseIntegral.setEnabled(false);
        }

        if (!etTransitionIntegral.isEnabled()) {
            etTransitionIntegral.setEnabled(true);
        }

        //设置积分输入监听
        if (mIntegralTextWatch == null) {
            mIntegralTextWatch = new IntegralTextWatcher();
        }

        if (mTranslateTextWatcher == null) {
            mTranslateTextWatcher = new TranslateTextWatcher();
        }

        etUseIntegral.removeTextChangedListener(mIntegralTextWatch);
        etUseIntegral.addTextChangedListener(mIntegralTextWatch);

        etTransitionIntegral.removeTextChangedListener(mTranslateTextWatcher);
        etTransitionIntegral.addTextChangedListener(mTranslateTextWatcher);
    }

    /**
     * 钱 转换 积分 观察器
     */
    private class TranslateTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String temp = s.toString();
            if (mActivityPaymentViewHolder != null) {
                AppCompatEditText etUseIntegralPrice = mActivityPaymentViewHolder.getUseIntegralPrice();
                AppCompatEditText useIntegral = mActivityPaymentViewHolder.getUseIntegral();
                if (null != etUseIntegralPrice.getTag()) {
                    etUseIntegralPrice.setTag(null);
                    etUseIntegralPrice.setSelection(etUseIntegralPrice.length());
                }

                /*if (!etUseIntegralPrice.hasFocus()) {
                    return;
                }*/

                String number = PayOrderTextWatcher.checkInputNumber(PayOrderTextWatcher.checkInputZeroNumber(PayOrderTextWatcher.checkInputNumberLength(temp)), false);

                if (PayOrderTextWatcher.isEmptyOrZero(number)) {
                    useIntegral.setText(number);
                    etUseIntegralPrice.setSelection(etUseIntegralPrice.length());
                }

                if (!temp.equals(number)) {
                    etUseIntegralPrice.setTag("");
                    etUseIntegralPrice.setText(number);
                    return;
                }

                final BigDecimal inputPrice = new BigDecimal(PayOrderTextWatcher.checkInputNumber(temp, true));
                final BigDecimal inputIntegral = inputPrice.multiply(mPaymentDataBean.getIntegralTransform()).setScale(2, BigDecimal.ROUND_HALF_DOWN);
                final BigDecimal maxIntegral = mPaymentDataBean.getFinalIntegral();
                //当前输入的积分大于 最大积分额度（可能是用户积分总额，也可能是活动金额换算的积分总额）
                if (1 == inputIntegral.compareTo(maxIntegral)) {
                    useIntegral.setTag("");
                    long value = maxIntegral.longValue();
                    if (0 != value % 2) {
                        value -= 1;
                    }
                    useIntegral.setText(String.valueOf(value));
                    etUseIntegralPrice.setTag("");
                    etUseIntegralPrice.setText(StringUtils.subZeroAndDot(String.valueOf(maxIntegral.
                            divide(mPaymentDataBean.getIntegralTransform(), 2, BigDecimal.ROUND_HALF_DOWN).toString())));

                } else {
                    mPaymentDataBean.setExactUseIntegralSum(inputIntegral);
                    mActivityPaymentViewHolder.getRemainPrice().setText(String.valueOf("￥" +
                            StringUtils.subZeroAndDot(mPaymentDataBean.getFinalNeedPayPrice(inputPrice, true).toString())));
                    long value = inputIntegral.longValue();
                    useIntegral.setTag("");
                    if (0 != value % 2) {
                        value -= 1;
                    }
                    useIntegral.setText(String.valueOf(value));
                }
            }

        }
    }

    /**
     * 积分 输入 观察器
     */
    private class IntegralTextWatcher implements TextWatcher {
//        private BigDecimal mIntegral;
//        private BigDecimal mMaxIntegral;

        private void onDestroy() {
//            mIntegral = null;
//            mMaxIntegral = null;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String temp = s.toString();

            if (mActivityPaymentViewHolder != null) {
                AppCompatEditText useIntegral = mActivityPaymentViewHolder.getUseIntegral();
//                AppCompatEditText etUseIntegralPrice = mActivityPaymentViewHolder.getUseIntegralPrice();

                if (null != useIntegral.getTag()) {
                    useIntegral.setTag(null);
                    useIntegral.setSelection(useIntegral.length());
                    return;
                }

                if (!useIntegral.hasFocus()) {
                    return;
                }

                String number = PayOrderTextWatcher.checkInputNumber(PayOrderTextWatcher.checkInputZeroNumber(PayOrderTextWatcher.checkInputNumberLength(temp)), true);

                if (!temp.equals(number)) {
                    useIntegral.setTag("");
                    useIntegral.setText(number);
//                    return;
                }

                /*
                mIntegral = new BigDecimal(temp);
                mMaxIntegral = mPaymentDataBean.getFinalIntegral();
                //当前输入的积分大于 最大积分额度（可能是用户积分总额，也可能是活动金额换算的积分总额）
                if (1 == mIntegral.compareTo(mMaxIntegral)) {
                    etUseIntegralPrice.setTag("");
                    etUseIntegralPrice.setText(StringUtils.subZeroAndDot(String.valueOf(mMaxIntegral.
                            divide(mPaymentDataBean.getIntegralTransform(), 2, BigDecimal.ROUND_HALF_DOWN).toString())));

                    useIntegral.setTag("");
                    long value = mMaxIntegral.longValue();
                    if (0 != value % 2) {
                        value -= 1;
                    }
                    useIntegral.setText(String.valueOf(value));
                    return;
                }

                mPaymentDataBean.setExactUseIntegralSum(mIntegral);

                mIntegral = mIntegral.divide(mPaymentDataBean.getIntegralTransform(), 2, RoundingMode.HALF_DOWN);


                mActivityPaymentViewHolder.getRemainPrice().setText(String.valueOf("￥" +
                        StringUtils.subZeroAndDot(mPaymentDataBean.getFinalNeedPayPrice(mIntegral, true).toString())));

                etUseIntegralPrice.setTag("");
                etUseIntegralPrice.setText(StringUtils.subZeroAndDot(mIntegral.toString()));*/
            }
        }
    }
}
