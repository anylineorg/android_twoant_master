package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;

/**
 * Created by S_Y_H on 2016/12/15.
 * 商家详情页的 商品列表viewHolder
 */

public class MerchantHomeGoodsViewHolder extends BaseRecyclerViewHolder {

    private AppCompatImageView mIvGoodsImg;
    private AppCompatTextView mTvGoodsTitle;
    private AppCompatTextView mTvGoodsPrice;
    private AppCompatImageButton mIvAddGoods;
    private AppCompatTextView mTvGoodsCount;
    private AppCompatImageButton mIvSubtractGoods;
    private AppCompatTextView mTvIntroduce;
    private AppCompatTextView mTvData;

    public MerchantHomeGoodsViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    protected void initView(View itemView, int viewType) {
        this.mTvData = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_data);
        this.mTvIntroduce = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_introduce_goods_list);
        this.mIvGoodsImg = (AppCompatImageView) itemView.findViewById(net.twoant.master.R.id.iv_goods_img);
        this.mTvGoodsTitle = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_goods_title);
        this.mTvGoodsPrice = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_goods_price);
        this.mIvAddGoods = (AppCompatImageButton) itemView.findViewById(net.twoant.master.R.id.iv_add_goods);
        this.mTvGoodsCount = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_goods_count);
        this.mIvSubtractGoods = (AppCompatImageButton) itemView.findViewById(net.twoant.master.R.id.iv_subtract_goods);
    }

    public AppCompatTextView getTvData() {
        return this.mTvData;
    }

    public void setTvData(AppCompatTextView mTvData) {
        this.mTvData = mTvData;
    }

    public AppCompatTextView getTvIntroduce() {
        return this.mTvIntroduce;
    }

    public void setTvIntroduce(AppCompatTextView mTvIntroduce) {
        this.mTvIntroduce = mTvIntroduce;
    }

    public AppCompatImageView getIvGoodsImg() {
        return mIvGoodsImg;
    }

    public AppCompatTextView getTvGoodsTitle() {
        return mTvGoodsTitle;
    }

    public AppCompatTextView getTvGoodsPrice() {
        return mTvGoodsPrice;
    }

    public AppCompatImageButton getIvAddGoods() {
        return mIvAddGoods;
    }

    public AppCompatTextView getTvGoodsCount() {
        return mTvGoodsCount;
    }

    public AppCompatImageButton getIvSubtractGoods() {
        return mIvSubtractGoods;
    }
}
