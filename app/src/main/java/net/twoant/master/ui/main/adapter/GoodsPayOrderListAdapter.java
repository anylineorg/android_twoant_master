package net.twoant.master.ui.main.adapter;

import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.bean.GoodsItemBean;

import java.math.BigDecimal;

/**
 * Created by S_Y_H on 2017/3/30.
 * 商品订单提交页 适配器
 */

public class GoodsPayOrderListAdapter extends BaseRecyclerAdapter<GoodsItemBean, GoodsPayOrderListAdapter.GoodsPayOrderViewHolder> {

    private int mScreenWidth;

    public GoodsPayOrderListAdapter(Activity context) {
        super(context);
        mScreenWidth = DisplayDimensionUtils.getScreenWidth();
    }

    @Override
    protected void onBindViewHolder(GoodsPayOrderViewHolder holder, int viewType, int position) {
        GoodsItemBean goodsItemBean = mDataList.get(position);
        if (null != goodsItemBean) {
            ViewGroup.LayoutParams layoutParams = holder.mIvGoodsImg.getLayoutParams();

            int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
            layoutParams.width = width;
            layoutParams.height = (int) (width * 168F / 200F + 0.5F);
            holder.mIvGoodsImg.setLayoutParams(layoutParams);

            ImageLoader.getImageFromNetworkPlaceholderControlImg(holder.mIvGoodsImg, goodsItemBean.getGoodsAvatar(), mContext, R.drawable.ic_def_small);
            holder.mTvGoodsTitle.setText(goodsItemBean.getGoodsName());
            holder.mTvNumber.setText(String.valueOf("数量:" + goodsItemBean.getGoodsCount()));
            holder.mTvPrice.setText(String.valueOf(StringUtils.subZeroAndDot(
                    new BigDecimal(goodsItemBean.getGoodsPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString()) + "元"));
        }
    }

    @Override
    protected BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType) {
        return new GoodsPayOrderViewHolder(layoutInflater.inflate(net.twoant.master.R.layout.yh_item_submit_goods_order_list, parent, false), viewType);
    }

    static class GoodsPayOrderViewHolder extends BaseRecyclerViewHolder {

        private AppCompatImageView mIvGoodsImg;
        private AppCompatTextView mTvGoodsTitle;
        private AppCompatTextView mTvPrice;
        private AppCompatTextView mTvNumber;

        private GoodsPayOrderViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mIvGoodsImg = (AppCompatImageView) itemView.findViewById(net.twoant.master.R.id.iv_goods_img);
            this.mTvGoodsTitle = (AppCompatTextView) itemView.findViewById(R.id.tv_goods_title);
            this.mTvPrice = (AppCompatTextView) itemView.findViewById(R.id.tv_price);
            this.mTvNumber = (AppCompatTextView) itemView.findViewById(R.id.tv_number);
        }
    }
}
