package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;

import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;


/**
 * Created by kaiguokai on 2017/4/8.
 * 商品列表 的头部 数据
 */

public class GoodsHeaderViewHolder extends BaseRecyclerViewHolder {


    private AppCompatTextView mTvShopNameMerchantHomePage;
    private AppCompatImageButton mBtnShopServerMerchantHomePage;
    private AppCompatImageButton mBtnShareMerchantHomePage;
    private AppCompatImageButton mBtnCollectionMerchantHomePage;
    private AppCompatTextView mYunfei;
    private AppCompatTextView mQisong;
    private AppCompatTextView mTvShopTelephoneMerchantHomePage;
    private AppCompatTextView mTvClickMerchantHomePage;
    private AppCompatTextView mTvShopAddressMerchantHomePage;
    private ImageView mIs_open;
    public GoodsHeaderViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    protected void initView(View rootView, int viewType) {
        this.mTvShopNameMerchantHomePage = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_shop_name_merchant_home_page);
        this.mBtnShopServerMerchantHomePage = (AppCompatImageButton) rootView.findViewById(net.twoant.master.R.id.btn_shop_server_merchant_home_page);
        this.mBtnShareMerchantHomePage = (AppCompatImageButton) rootView.findViewById(net.twoant.master.R.id.btn_share_merchant_home_page);
        this.mBtnCollectionMerchantHomePage = (AppCompatImageButton) rootView.findViewById(net.twoant.master.R.id.btn_collection_merchant_home_page);
        this.mYunfei = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.yunfei);
        this.mQisong = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.qisong);
        this.mTvShopTelephoneMerchantHomePage = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_shop_telephone_merchant_home_page);
        this.mTvClickMerchantHomePage = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_click_merchant_home_page);
        this.mTvShopAddressMerchantHomePage = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_shop_address_merchant_home_page);
        mIs_open=(ImageView) rootView.findViewById(net.twoant.master.R.id.is_open);
    }

    public AppCompatTextView getTvShopNameMerchantHomePage() {
        return mTvShopNameMerchantHomePage;
    }

    public AppCompatImageButton getBtnShopServerMerchantHomePage() {
        return mBtnShopServerMerchantHomePage;
    }

    public ImageView getmIs_open() {
        return mIs_open;
    }

    public void setmIs_open(ImageView mIs_open) {
        this.mIs_open = mIs_open;
    }

    public AppCompatImageButton getBtnShareMerchantHomePage() {
        return mBtnShareMerchantHomePage;
    }

    public AppCompatImageButton getBtnCollectionMerchantHomePage() {
        return mBtnCollectionMerchantHomePage;
    }

    public AppCompatTextView getYunfei() {
        return mYunfei;
    }

    public AppCompatTextView getQisong() {
        return mQisong;
    }

    public AppCompatTextView getTvShopTelephoneMerchantHomePage() {
        return mTvShopTelephoneMerchantHomePage;
    }

    public AppCompatTextView getTvClickMerchantHomePage() {
        return mTvClickMerchantHomePage;
    }

    public AppCompatTextView getTvShopAddressMerchantHomePage() {
        return mTvShopAddressMerchantHomePage;
    }
}
