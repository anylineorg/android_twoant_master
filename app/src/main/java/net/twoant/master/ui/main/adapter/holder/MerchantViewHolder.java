package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageView;

import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

/**
 * Created by S_Y_H on 2016/11/24.
 * 商家列表的 viewHolder
 */

public class MerchantViewHolder extends BaseRecyclerViewHolder {

    private AppCompatImageView mIvMerchantImg;
    private AppCompatTextView mNameMerchantList;
    private AppCompatTextView mTvDistanceMerchantList;
    private AppCompatTextView mTvPhoneNumberMerchantList;
    private AppCompatTextView mTvDealVolumeMerchantList;
    private AppCompatTextView mTvCommentCountMerchantList;
    private AppCompatTextView mTvAddressMerchantList;
    private LinearLayoutCompat mMerchant_root;
    private ImageView mShop_staue;
    public MerchantViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    protected void initView(View itemView, int viewType) {
        if (viewType == IRecyclerViewConstant.KIND_MERCHANT_LIST_COMMON) {
            this.mIvMerchantImg = (AppCompatImageView) itemView.findViewById(net.twoant.master.R.id.iv_merchant_img);
            this.mTvDistanceMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_distance_merchant_list);
            this.mTvPhoneNumberMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_phone_number_merchant_list);
            this.mTvDealVolumeMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_deal_volume_merchant_list);
            this.mTvCommentCountMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_comment_count_merchant_list);
            this.mTvAddressMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_address_merchant_list);
            this.mNameMerchantList = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_name_merchant_list);
            this.mMerchant_root=(LinearLayoutCompat)itemView.findViewById(net.twoant.master.R.id.merchant_root);
            this.mShop_staue= (ImageView) itemView.findViewById(net.twoant.master.R.id.shop_staue);
        }
    }

    public ImageView getmShop_staue() {
        return mShop_staue;
    }

    public void setmShop_staue(ImageView mShop_staue) {
        this.mShop_staue = mShop_staue;
    }

    public LinearLayoutCompat getmMerchant_root() {
        return mMerchant_root;
    }

    public void setmMerchant_root(LinearLayoutCompat mMerchant_root) {
        this.mMerchant_root = mMerchant_root;
    }

    public AppCompatImageView getIvMerchantImg() {
        return mIvMerchantImg;
    }

    public AppCompatTextView getNameMerchantList() {
        return mNameMerchantList;
    }

    public AppCompatTextView getTvDistanceMerchantList() {
        return mTvDistanceMerchantList;
    }

    public AppCompatTextView getTvPhoneNumberMerchantList() {
        return mTvPhoneNumberMerchantList;
    }

    public AppCompatTextView getTvDealVolumeMerchantList() {
        return mTvDealVolumeMerchantList;
    }

    public AppCompatTextView getTvCommentCountMerchantList() {
        return mTvCommentCountMerchantList;
    }

    public AppCompatTextView getTvAddressMerchantList() {
        return mTvAddressMerchantList;
    }
}
