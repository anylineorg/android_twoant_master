package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.main.adapter.ActivityPaymentAdapter;

/**
 * Created by S_Y_H on 2016/12/24.
 *活动支付页的适配器
 */

public class ActivityPaymentViewHolder extends RecyclerView.ViewHolder {

    //item部分
    private AppCompatImageView mIvImg;
    private AppCompatTextView mTvItemPrice;
    private AppCompatTextView mTvDescription;
    private AppCompatTextView mTvStartDate;

    //公共部分
    private AppCompatTextView mTvReceiver;
    private AppCompatTextView mTvPrice;
    private AppCompatEditText mEtUseIntegral;
//    private AppCompatTextView mTvIntegralPrice;
    private AppCompatTextView mTvIntegralAll;
    private AppCompatTextView mTvWalletBalance;
    private AppCompatEditText mEtUseWallet;
    private AppCompatTextView mTvRemainPrice;
    private AppCompatButton mBtnPay;
    private AppCompatEditText mEtUseIntegralPrice;
    private LinearLayoutCompat mWalletParentLayout;
    private LinearLayoutCompat mIntegralParentLayout;

    public ActivityPaymentViewHolder(View itemView, int viewType) {
        super(itemView);
        if (viewType == ActivityPaymentAdapter.ITEM)
            initItem(itemView);
        else 
            initCommon(itemView);
    }


    private void initCommon(View rootView) {
        this.mWalletParentLayout = (LinearLayoutCompat) rootView.findViewById(R.id.ll_wallet_parent);
        this.mIntegralParentLayout = (LinearLayoutCompat) rootView.findViewById(R.id.ll_integral_parent) ;
        this.mTvReceiver = (AppCompatTextView) rootView.findViewById(R.id.tv_receiver);
        this.mTvPrice = (AppCompatTextView) rootView.findViewById(R.id.tv_price);
        this.mEtUseIntegral = (AppCompatEditText) rootView.findViewById(R.id.et_use_integral);
        this.mEtUseIntegralPrice = (AppCompatEditText) rootView.findViewById(R.id.et_use_integral_price);
//        this.mTvIntegralPrice = (AppCompatTextView) rootView.findViewById(R.id.tv_integral_price);
        this.mTvIntegralAll = (AppCompatTextView) rootView.findViewById(R.id.tv_integral_all);
        this.mTvWalletBalance = (AppCompatTextView) rootView.findViewById(R.id.tv_wallet_balance);
        this.mEtUseWallet = (AppCompatEditText) rootView.findViewById(R.id.et_use_wallet);
        this.mTvRemainPrice = (AppCompatTextView) rootView.findViewById(R.id.tv_remain_price);
        this.mBtnPay = (AppCompatButton) rootView.findViewById(R.id.btn_pay);
    }

    private void initItem(View rootView) {
        this.mIvImg = (AppCompatImageView) rootView.findViewById(R.id.iv_img);
        this.mTvItemPrice = (AppCompatTextView) rootView.findViewById(R.id.tv_price);
        this.mTvDescription = (AppCompatTextView) rootView.findViewById(R.id.tv_description);
        this.mTvStartDate = (AppCompatTextView) rootView.findViewById(R.id.tv_start_date);
    }

    public LinearLayoutCompat getWalletParentLayout() {
        return mWalletParentLayout;
    }

    public LinearLayoutCompat getIntegralParentLayout() {
        return mIntegralParentLayout;
    }

    public AppCompatEditText getUseIntegralPrice() {
        return this.mEtUseIntegralPrice;
    }

    public AppCompatImageView getIvImg() {
        return mIvImg;
    }

    public AppCompatTextView getTvItemPrice() {
        return mTvItemPrice;
    }

    public AppCompatTextView getTvDescription() {
        return mTvDescription;
    }

    public AppCompatTextView getTvStartDate() {
        return mTvStartDate;
    }

    public AppCompatTextView getTvReceiver() {
        return mTvReceiver;
    }

    public AppCompatTextView getTvPrice() {
        return mTvPrice;
    }

    public AppCompatEditText getUseIntegral() {
        return mEtUseIntegral;
    }

    /*public AppCompatTextView getTvIntegralPrice() {
        return mTvIntegralPrice;
    }*/

    public AppCompatTextView getTvIntegralAll() {
        return mTvIntegralAll;
    }

    public AppCompatTextView getTvWalletBalance() {
        return mTvWalletBalance;
    }

    public AppCompatEditText getUseWallet() {
        return mEtUseWallet;
    }

    public AppCompatTextView getRemainPrice() {
        return mTvRemainPrice;
    }

    public AppCompatButton getBtnPay() {
        return mBtnPay;
    }
}
