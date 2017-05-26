package net.twoant.master.ui.main.adapter.holder;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.twoant.master.R;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by S_Y_H on 2016/11/24.
 * 商品 viewHolder
 */

public class GoodsViewHolder extends BaseRecyclerViewHolder {


    private AppCompatImageView mIvGoodsImg;
    private AppCompatTextView mTvGoodsName;
    private AppCompatTextView mTvDistanceGoodsList;
    private AppCompatTextView mTvMerchantNameGoodsList;
    private AppCompatTextView mTvIntroduceGoodsList;
    private AppCompatTextView mTvPriceGoodsList;
    private AppCompatTextView mTvReadCountGoodsList;
    private AppCompatTextView mtv_number_goodsdetail;
    private AppCompatTextView mtv_goodscount;
    private ImageView miv_addd_goodsdetail;
    private ImageView miv_minus_goodsdetail;
    private LinearLayout jia_jian_layout;
    private ImageView mExChange;
    private DataRow dataRow;

    public AppCompatTextView getmTvGoodsCount() {
        return mTvGoodsCount;
    }

    public void setmTvGoodsCount(AppCompatTextView mTvGoodsCount) {
        this.mTvGoodsCount = mTvGoodsCount;
    }

    private AppCompatTextView mTvGoodsCount;

    public AppCompatTextView getMtv_goodscount() {
        return mtv_goodscount;
    }

    public void setMtv_goodscount(AppCompatTextView mtv_goodscount) {
        this.mtv_goodscount = mtv_goodscount;
    }

    public GoodsViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    protected void initView(View itemView, int viewType) {
        if (viewType == IRecyclerViewConstant.KIND_MERCHANT_COMMON_GOODS) {
            this.mIvGoodsImg = (AppCompatImageView) itemView.findViewById(R.id.iv_goods_img);
            this.mTvGoodsName = (AppCompatTextView) itemView.findViewById(R.id.tv_goods_name);
            this.mTvDistanceGoodsList = (AppCompatTextView) itemView.findViewById(R.id.tv_distance_goods_list);
            this.mTvMerchantNameGoodsList = (AppCompatTextView) itemView.findViewById(R.id.tv_merchant_name_goods_list);
            this.mTvIntroduceGoodsList = (AppCompatTextView) itemView.findViewById(R.id.tv_introduce_goods_list);
            this.mTvPriceGoodsList = (AppCompatTextView) itemView.findViewById(R.id.tv_price_goods_list);
            this.mTvReadCountGoodsList = (AppCompatTextView) itemView.findViewById(R.id.tv_read_count_goods_list);
            this.mtv_number_goodsdetail=(AppCompatTextView) itemView.findViewById(R.id.tv_number_goodsdetail);
            this.miv_addd_goodsdetail=(ImageView) itemView.findViewById(R.id.iv_addd_goodsdetail);
            this.miv_minus_goodsdetail=(ImageView) itemView.findViewById(R.id.iv_minus_goodsdetail);
            this.mtv_goodscount=(AppCompatTextView) itemView.findViewById(R.id.tv_goods_count);
            this.jia_jian_layout= (LinearLayout) itemView.findViewById(R.id.jia_jian_layout);
            this.mExChange= (ImageView) itemView.findViewById(R.id.exchange_bt);
        }
    }

    public DataRow getDataRow() {
        return dataRow;
    }

    public LinearLayout getJia_jian_layout() {
        return jia_jian_layout;
    }

    public void setJia_jian_layout(LinearLayout jia_jian_layout) {
        this.jia_jian_layout = jia_jian_layout;
    }

    public ImageView getmExChange() {
        return mExChange;
    }

    public void setmExChange(ImageView mExChange) {
        this.mExChange = mExChange;
    }

    public void setDataRow(DataRow dataRow) {
        this.dataRow = dataRow;
    }

    public AppCompatImageView getmIvGoodsImg() {
        return mIvGoodsImg;
    }

    public void setmIvGoodsImg(AppCompatImageView mIvGoodsImg) {
        this.mIvGoodsImg = mIvGoodsImg;
    }

    public AppCompatTextView getMtv_number_goodsdetail() {
        return mtv_number_goodsdetail;
    }

    public void setMtv_number_goodsdetail(AppCompatTextView mtv_number_goodsdetail) {
        this.mtv_number_goodsdetail = mtv_number_goodsdetail;
    }

    public ImageView getMiv_addd_goodsdetail() {
        return miv_addd_goodsdetail;
    }

    public void setMiv_addd_goodsdetail(ImageView miv_addd_goodsdetail) {
        this.miv_addd_goodsdetail = miv_addd_goodsdetail;
    }

    public ImageView getMiv_minus_goodsdetail() {
        return miv_minus_goodsdetail;
    }

    public void setMiv_minus_goodsdetail(ImageView miv_minus_goodsdetail) {
        this.miv_minus_goodsdetail = miv_minus_goodsdetail;
    }

    public AppCompatImageView getIvGoodsImg() {
        return mIvGoodsImg;
    }

    public AppCompatTextView getTvGoodsName() {
        return mTvGoodsName;
    }

    public AppCompatTextView getTvDistanceGoodsList() {
        return mTvDistanceGoodsList;
    }

    public AppCompatTextView getTvMerchantNameGoodsList() {
        return mTvMerchantNameGoodsList;
    }

    public AppCompatTextView getTvIntroduceGoodsList() {
        return mTvIntroduceGoodsList;
    }

    public AppCompatTextView getTvPriceGoodsList() {
        return mTvPriceGoodsList;
    }

    public AppCompatTextView getTvReadCountGoodsList() {
        return mTvReadCountGoodsList;
    }
}
