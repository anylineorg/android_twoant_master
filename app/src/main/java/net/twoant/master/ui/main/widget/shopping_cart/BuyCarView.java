package net.twoant.master.ui.main.widget.shopping_cart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import net.twoant.master.common_utils.SnackBarUtils;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.MerchantGoodsCarAdapter;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.interfaces.IOnDataChangeListener;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2017/4/1.
 * 购物车 view
 */

public class BuyCarView extends ContentFrameLayout implements View.OnClickListener, IOnDataChangeListener {

    private String mCurrentGoodsId = "";
    private int isUpdateDisplay;
    public CarViewHolder mCarViewHolder;
    private GoodsItemBean mCurrentGoodsItem;
    private IOnBuyCartListener iOnBuyCartListener;
    private ICartDataChangeListener iCartDataChangeListener;
    private MerchantGoodsCarAdapter mMerchantGoodsCarAdapter;
    private BottomSheetBehavior<ContentFrameLayout> mBottomSheetBehavior;

    public interface IOnBuyCartListener {
        void onPaymentClickListener(@NonNull View view, ArrayList<GoodsItemBean> goodsItemBeen);
    }

    public interface ICartDataChangeListener {
        @NonNull
        List<GoodsItemBean> getDataList();

        void onDataChange(@NonNull List<GoodsItemBean> cartList, @NonNull GoodsItemBean current);

        void onCurrentGoodsCountChange(int count);
    }

    @Override
    public View getCartView() {
        return null == mCarViewHolder ? null : mCarViewHolder.mIvCart;
    }

    @Override
    public void onBtnClickListener(View pressView, boolean isAdd) {
        ++isUpdateDisplay;
    }

    @Override
    public void onDataChangeListener(List<GoodsItemBean> goodsItems) {
        if (null != goodsItems) {
            resetCurrentItem(goodsItems);
            if (null != iCartDataChangeListener) {
                if (0 < isUpdateDisplay) {
                    isUpdateDisplay = 0 > --isUpdateDisplay ? 0 : isUpdateDisplay;
                    iCartDataChangeListener.onDataChange(goodsItems, mCurrentGoodsItem);
                }
                iCartDataChangeListener.onCurrentGoodsCountChange(mCurrentGoodsItem.getGoodsCount());
            }
            initCartPriceData(goodsItems);
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (null != mBottomSheetBehavior) {
            closeCartView();
        }
    }

    /**
     * 初始化 第一次使用的数据
     */
    public void initData(String goodId, List<GoodsItemBean> goodsItems) {
        initCurrentGoodItem(goodId, goodsItems);
        if (null != iCartDataChangeListener) {
            iCartDataChangeListener.onDataChange(goodsItems, mCurrentGoodsItem);
            iCartDataChangeListener.onCurrentGoodsCountChange(mCurrentGoodsItem.getGoodsCount());
        }
        initCartPriceData(goodsItems);
    }

    /**
     * 设置当前 goods ID
     */
    public void setCurrentGoodsId(String goodsId) {
        this.mCurrentGoodsId = goodsId;
    }

    /**
     * 初始化购物车的价格和数量 数据
     */
    public void initCartPriceData(@NonNull List<GoodsItemBean> goodsItems) {
        BigDecimal allPrice = BigDecimal.ZERO;
        int count = 0;
        for (GoodsItemBean bean : goodsItems) {
            allPrice = allPrice.add(new BigDecimal(bean.getGoodsPrice())).setScale(2, RoundingMode.HALF_UP);
            count += bean.getGoodsCount();
        }

        Context context = getContext();
        AppCompatTextView countHint = mCarViewHolder.mTvCountHint;
        if (0 >= count) {
            if (View.GONE != countHint.getVisibility()) {
                countHint.setVisibility(View.GONE);
            }
        } else {
            if (View.VISIBLE != countHint.getVisibility()) {
                countHint.setVisibility(View.VISIBLE);
            }
            countHint.setText(String.valueOf(count));
        }

        mCarViewHolder.mTvGoodsCount.setText(String.format(context.getString(net.twoant.master.R.string.goods_count_hint), count));
        mCarViewHolder.mTvPrice.setText(String.format(context.getString(net.twoant.master.R.string.goods_price_sum),
                StringUtils.subZeroAndDot(allPrice.toString())));
    }

    public void setOnBuyCartListener(IOnBuyCartListener iOnBuyCartListener) {
        this.iOnBuyCartListener = iOnBuyCartListener;
    }

    public void setCartDataChangeListener(ICartDataChangeListener iCartDataChangeListener) {
        this.iCartDataChangeListener = iCartDataChangeListener;
    }

    public BuyCarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 改变当前 goodId 的 数据
     *
     * @param isAdd  是否为加 1
     * @param goodId 商品id
     */
    public void changeGoodsCount(boolean isAdd, @NonNull String goodId, @NonNull View pressView) {
        if (null == iCartDataChangeListener) {
            return;
        }

        final List<GoodsItemBean> goodsItemBeen = iCartDataChangeListener.getDataList();

        if (initCurrentGoodItem(goodId, goodsItemBeen)) {
            return;
        }

        int goodsCount = mCurrentGoodsItem.getGoodsCount();
        if (isAdd) {
            mCurrentGoodsItem.setGoodsCount(++goodsCount);
        } else {
            mCurrentGoodsItem.setGoodsCount(goodsCount = 0 >= goodsCount - 1 ? 0 : --goodsCount);
        }
        if (0 < goodsCount) {
            mCurrentGoodsItem.setGoodsPrice(new BigDecimal(mCurrentGoodsItem.getGoodsUnitPrice())
                    .multiply(new BigDecimal(goodsCount)).setScale(2, RoundingMode.HALF_UP).floatValue());
            if (!goodsItemBeen.contains(mCurrentGoodsItem)) {
                goodsItemBeen.add(mCurrentGoodsItem);
            }
            mMerchantGoodsCarAdapter.setDataBean(goodsItemBeen);
        } else {
            final ArrayList<GoodsItemBean> temp = new ArrayList<>(goodsItemBeen);
            temp.remove(mCurrentGoodsItem);
            mMerchantGoodsCarAdapter.setDataBean(temp);
        }
        ++isUpdateDisplay;
        mMerchantGoodsCarAdapter.animChange(pressView, isAdd);
        iCartDataChangeListener.onCurrentGoodsCountChange(goodsCount);
    }

    /**
     * 改变商品数量，只有动画效果和 数据显示， 不回调接口。
     *
     * @param isAdd     true 添加 ， false 减
     * @param goodId    当前的商品id
     * @param pressView 当前按下的view
     */
    public void changeGoodsCountNotNotify(boolean isAdd, @NonNull String goodId, @NonNull View pressView) {
        if (null == iCartDataChangeListener) {
            return;
        }
        final List<GoodsItemBean> goodsItemBeen = iCartDataChangeListener.getDataList();

        if (initCurrentGoodItem(goodId, goodsItemBeen)) {
            return;
        }
        mMerchantGoodsCarAdapter.setDataBean(goodsItemBeen);
        mMerchantGoodsCarAdapter.animChange(pressView, isAdd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //显示购物车
            case net.twoant.master.R.id.buy_card:
                if (null != iCartDataChangeListener) {
                    int state = mBottomSheetBehavior.getState();
                    if (state == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    } else {
                        final ArrayList<GoodsItemBean> goodsItemBeen = new ArrayList<>(iCartDataChangeListener.getDataList());
                        initCurrentGoodItem(mCurrentGoodsId, goodsItemBeen);
                        mMerchantGoodsCarAdapter.setDataBean(goodsItemBeen);
                        mBottomSheetBehavior.setPeekHeight(-1);
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                break;
            //去结算
            case net.twoant.master.R.id.btn_clearing:
                if (null != iOnBuyCartListener) {
                    iOnBuyCartListener.onPaymentClickListener(v, new ArrayList<>(mMerchantGoodsCarAdapter.getDataBean()));
                }
                break;
            //清空购物车
            case net.twoant.master.R.id.tv_clean_cart:
                if (mMerchantGoodsCarAdapter.getDataBean().isEmpty()) {
                    closeCartView();
                    return;
                }
                SnackBarUtils.show(mCarViewHolder.mClCarParent, getContext().getString(net.twoant.master.R.string.goods_clean_cart)
                        , Snackbar.LENGTH_SHORT, net.twoant.master.R.color.colorPrimary, net.twoant.master.R.color.whiteTextColor, net.twoant.master.R.drawable.yh_btn_press_orange)
                        .setAction(net.twoant.master.R.string.goods_confirm, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                restoreData();
                                closeCartView();
                            }
                        }).setActionTextColor(ContextCompat.getColor(getContext(), net.twoant.master.R.color.whiteTextColor)).show();

                break;
        }
    }

    /**
     * 清空
     */
    public void onDestroy() {
        iOnBuyCartListener = null;
        iCartDataChangeListener = null;
    }

    /**
     * 重置数据
     */
    public void restoreData() {
        mMerchantGoodsCarAdapter.cleanData();
        final List<GoodsItemBean> dataBean = mMerchantGoodsCarAdapter.getDataBean();
        resetCurrentItem(dataBean);
        if (null != iCartDataChangeListener) {
            iCartDataChangeListener.onDataChange(dataBean, mCurrentGoodsItem);
            iCartDataChangeListener.onCurrentGoodsCountChange(0);
        }
        initCartPriceData(dataBean);
    }

    /**
     * 重置当前goodsItem
     */
    private void resetCurrentItem(List<GoodsItemBean> list) {
        if (null != list && null != mCurrentGoodsItem) {
            //重写了equals 方法，比较的是goodsId
            int indexOf = list.indexOf(mCurrentGoodsItem);
            if (-1 != indexOf) {
                mCurrentGoodsItem = list.get(indexOf);
            } else {
                mCurrentGoodsItem = new GoodsItemBean(mCurrentGoodsItem);
            }
        }
    }

    /**
     * 初始化 当前 商品条目
     *
     * @return true 出现数据问题
     */
    private boolean initCurrentGoodItem(@NonNull String goodId, @NonNull List<GoodsItemBean> goodsItemBeen) {
        if (null == mCurrentGoodsItem || !goodId.equals(mCurrentGoodsItem.getGoodsId())) {
            if (goodsItemBeen.isEmpty()) {
                return true;
            }
            for (GoodsItemBean bean : goodsItemBeen) {
                if (null != bean && goodId.equals(bean.getGoodsId())) {
                    mCurrentGoodsItem = bean;
                    break;
                }
            }
            if (null == mCurrentGoodsItem) {
                return true;
            }
        }
        return false;
    }


    /**
     * 初始化View
     */
    private void initView() {
        Context context = getContext();
        mCarViewHolder = new CarViewHolder(LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_view_buy_card, this));
        mMerchantGoodsCarAdapter = new MerchantGoodsCarAdapter();
        final RecyclerView recyclerView = mCarViewHolder.mRvCardListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(context, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2, false, 0));
        recyclerView.setAdapter(mMerchantGoodsCarAdapter);
        mMerchantGoodsCarAdapter.setOnDataChangeListener(this);
        mBottomSheetBehavior = BottomSheetBehavior.from(mCarViewHolder.mClCarParent);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setSkipCollapsed(true);

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mCarViewHolder.mBuyCard.setOnClickListener(this);
        mCarViewHolder.mCleanCart.setOnClickListener(this);
        mCarViewHolder.mBtnClearing.setOnClickListener(this);
    }

    /**
     * 关闭 购物车 view
     */
    private void closeCartView() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    public static class CarViewHolder {
        private RecyclerView mRvCardListRecyclerView;
        private ContentFrameLayout mClCarParent;
        public AppCompatButton mBtnClearing;
        private View mBuyCard;
        private AppCompatImageView mIvCart;
        private AppCompatTextView mCleanCart;
        private AppCompatTextView mTvCountHint;
        public AppCompatTextView mTvGoodsCount;
        public AppCompatTextView mTvPrice;


        private CarViewHolder(View rootView) {
            this.mCleanCart = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_clean_cart);
            this.mRvCardListRecyclerView = (RecyclerView) rootView.findViewById(net.twoant.master.R.id.rv_card_list_recycler_view);
            this.mClCarParent = (ContentFrameLayout) rootView.findViewById(net.twoant.master.R.id.cl_car_parent);
            this.mBtnClearing = (AppCompatButton) rootView.findViewById(net.twoant.master.R.id.btn_clearing);
            this.mBuyCard = rootView.findViewById(net.twoant.master.R.id.buy_card);
            this.mIvCart = (AppCompatImageView) rootView.findViewById(net.twoant.master.R.id.iv_cart);
            this.mTvCountHint = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_count_hint);
            this.mTvGoodsCount = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_goods_count);
            this.mTvPrice = (AppCompatTextView) rootView.findViewById(net.twoant.master.R.id.tv_price);
        }

    }
}
