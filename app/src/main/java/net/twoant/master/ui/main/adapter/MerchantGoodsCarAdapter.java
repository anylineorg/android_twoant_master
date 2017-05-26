package net.twoant.master.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.interfaces.IOnDataChangeListener;
import net.twoant.master.ui.main.widget.shopping_cart.GoodsAnimation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/1/9.
 * 商家首页的 goods 的适配器
 */

public class MerchantGoodsCarAdapter extends RecyclerView.Adapter<MerchantGoodsCarAdapter.MerchantGoodsCarViewHolder> implements View.OnClickListener {

    private List<GoodsItemBean> mDataBean;
    private final static int ID_ADD_GOODS = 4 << 24;
    private final static int ID_SUBTRACT_GOODS = 5 << 24;

    /**
     * 当前在窗口中的ViewHolder
     */
    private SparseArray<MerchantGoodsCarAdapter.MerchantGoodsCarViewHolder> mViewHolders;

    /**
     * 动画
     */
    private GoodsAnimation mGoodsAnimation;

    private IOnDataChangeListener iOnDataChangeListener;

    public MerchantGoodsCarAdapter() {
        initData();
    }

    public void setOnDataChangeListener(IOnDataChangeListener iOnDataChangeListener) {
        this.iOnDataChangeListener = iOnDataChangeListener;
    }

    public void setDataBean(List<GoodsItemBean> dataBean) {
        if (dataBean != null) {
            mDataBean = dataBean;
            this.notifyDataSetChanged();
        }
    }

    public void cleanData() {
        this.mDataBean.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    public List<GoodsItemBean> getDataBean() {
        return this.mDataBean;
    }

    @Override
    public MerchantGoodsCarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MerchantGoodsCarViewHolder(LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.yh_item_car_goods_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MerchantGoodsCarViewHolder holder, int position) {
        GoodsItemBean goodsItem = mDataBean.get(position);
        if (goodsItem != null) {
            holder.mTvGoodsName.setText(goodsItem.getGoodsName());
            holder.mTvPrice.setText(StringUtils.subZeroAndDot(String.valueOf("￥" + goodsItem.getGoodsPrice())));
            holder.mTvGoodsCount.setText(String.valueOf(goodsItem.getGoodsCount()));

            AppCompatImageButton temp = holder.mIvAddGoods;
            temp.setOnClickListener(this);
            temp.setTag(position);
            temp.setTag(ID_ADD_GOODS, goodsItem.getGoodsId());

            temp = holder.mIvSubtractGoods;
            temp.setOnClickListener(this);
            temp.setTag(ID_SUBTRACT_GOODS, goodsItem.getGoodsId());
            temp.setTag(position);
        }
    }


    @Override
    public int getItemCount() {
        return mDataBean.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //添加商品
            case net.twoant.master.R.id.iv_add_goods:
                Object position = v.getTag();
                Object goodsId = v.getTag(ID_ADD_GOODS);

                if (position instanceof Integer && goodsId instanceof String) {
                    setGoodsBean(v, (Integer) position, (String) goodsId, true);
                }
                break;
            //减少商品
            case net.twoant.master.R.id.iv_subtract_goods:
                Object pos = v.getTag();
                Object goods = v.getTag(ID_SUBTRACT_GOODS);

                if (pos instanceof Integer && goods instanceof String) {
                    setGoodsBean(v, (Integer) pos, (String) goods, false);
                }
                break;
        }

    }

    @Override
    public void onViewAttachedToWindow(MerchantGoodsCarAdapter.MerchantGoodsCarViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (mViewHolders != null) {
            mViewHolders.put(holder.getAdapterPosition(), holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(MerchantGoodsCarAdapter.MerchantGoodsCarViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (mViewHolders != null) {
            mViewHolders.delete(holder.getAdapterPosition());
        }
    }


    /**
     * @param pressView 当前事件View
     * @param position  适配器位置
     * @param goodsId   商品id
     * @param isAdd     是否是增加
     */
    private void setGoodsBean(View pressView, int position, String goodsId, boolean isAdd) {
        if (mDataBean.size() <= position) {
            return;
        }
        GoodsItemBean goodsItem = mDataBean.get(position);
        if (goodsItem != null && goodsId.equals(goodsItem.getGoodsId())) {
            int goodsCount = goodsItem.getGoodsCount();

            if (isAdd) {
                goodsItem.setGoodsCount(++goodsCount);
                goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).add(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
            } else {
                goodsItem.setGoodsCount(--goodsCount);

                if (goodsCount >= 0) {
                    goodsItem.setGoodsPrice(new BigDecimal(goodsItem.getGoodsPrice()).subtract(new BigDecimal(goodsItem.getGoodsUnitPrice())).setScale(2, RoundingMode.HALF_UP).floatValue());
                }
            }

            MerchantGoodsCarAdapter.MerchantGoodsCarViewHolder merchantHomeGoodsViewHolder = mViewHolders.get(position);
            if (merchantHomeGoodsViewHolder != null) {
                //隐藏减号图标 清空显示
                if (goodsCount <= 0) {
                    merchantHomeGoodsViewHolder.mTvGoodsCount.setText("");
                    mDataBean.remove(position);
                    this.notifyDataSetChanged();
                } else {
                    merchantHomeGoodsViewHolder.mTvPrice.setText(StringUtils.subZeroAndDot(String.valueOf("￥" + goodsItem.getGoodsPrice())));
                    merchantHomeGoodsViewHolder.mTvGoodsCount.setText(String.valueOf(goodsCount));
                }
                if (null != iOnDataChangeListener) {
                    iOnDataChangeListener.onBtnClickListener(pressView, isAdd);
                }
                animChange(pressView, isAdd);
            }
        }
    }

    public void animChange(View pressView, boolean isAdd) {
        if (iOnDataChangeListener != null) {
            if (isAdd) {
                mGoodsAnimation.initAnim(pressView, iOnDataChangeListener.getCartView());
            } else {
                iOnDataChangeListener.onDataChangeListener(mDataBean);
            }
        }
    }

    /**
     * 初始化
     */
    private void initData() {
        mDataBean = new ArrayList<>();
        mViewHolders = new SparseArray<>();
        mGoodsAnimation = new GoodsAnimation();
        mGoodsAnimation.setOnEndAnimListener(new GoodsAnimation.IOnEndAnimListener() {
            @Override
            public void onGoodsAniaEnd() {

                if (iOnDataChangeListener != null) {
                    iOnDataChangeListener.onDataChangeListener(mDataBean);
                }
            }
        });
    }

    static class MerchantGoodsCarViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView mTvGoodsName;
        private AppCompatImageButton mIvSubtractGoods;
        private AppCompatTextView mTvGoodsCount;
        private AppCompatImageButton mIvAddGoods;
        private AppCompatTextView mTvPrice;

        MerchantGoodsCarViewHolder(View itemView) {
            super(itemView);
            mTvPrice = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_price);
            this.mTvGoodsName = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_goods_name);
            this.mTvGoodsCount = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_goods_count);
            this.mIvAddGoods = (AppCompatImageButton) itemView.findViewById(net.twoant.master.R.id.iv_add_goods);
            this.mIvSubtractGoods = (AppCompatImageButton) itemView.findViewById(net.twoant.master.R.id.iv_subtract_goods);
        }
    }
}
