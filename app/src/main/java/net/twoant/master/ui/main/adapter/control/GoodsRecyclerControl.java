package net.twoant.master.ui.main.adapter.control;

import android.app.Activity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.activity.BuyerGoodsDetailActivity;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.holder.GoodsHeaderViewHolder;
import net.twoant.master.ui.main.adapter.holder.GoodsViewHolder;
import net.twoant.master.ui.main.fragment.HomeFragment;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import net.twoant.master.ui.main.activity.ShopGoodsActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/1/16.
 * 商品列表 控制器
 */

public class GoodsRecyclerControl extends BaseRecyclerControlImpl<DataRow> {
    public final static int TYPE_HEADER = 0x1;
    public final static int SCORE_STORE = 666;

    private boolean isHasHeadView = false;
    private boolean isHasScoreView = false;

    public GoodsRecyclerControl(int isHasHeadView) {
        super(1);
        this.isHasHeadView = isHasHeadView == TYPE_HEADER;
        this.isHasScoreView = isHasHeadView == SCORE_STORE;

    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new GoodsHeaderViewHolder(inflater.inflate(net.twoant.master.R.layout.yh_fragment_recycler_back_top_shop, parent, false), viewType);
        }
        return new GoodsViewHolder(inflater.inflate(net.twoant.master.R.layout.yh_item_goods_list, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {

        if (viewType == IRecyclerViewConstant.KIND_MERCHANT_COMMON_GOODS) {
            GoodsViewHolder goodsViewHolder = (GoodsViewHolder) holder;

            View itemView = goodsViewHolder.itemView;

            DataRow dataBean = mDataBean.get(position);
            // itemView.setOnClickListener(onClickListener);
            goodsViewHolder.setDataRow(dataBean);
            goodsViewHolder.getMiv_addd_goodsdetail().setOnClickListener(onClickListener);
            goodsViewHolder.getMiv_minus_goodsdetail().setOnClickListener(onClickListener);
            goodsViewHolder.getMiv_addd_goodsdetail().setTag(goodsViewHolder);
            goodsViewHolder.getMiv_minus_goodsdetail().setTag(goodsViewHolder);


            if (mDataBean != null) {
                itemView.setTag(dataBean.getString("ID"));
                if (isHasScoreView){//判断是不是积分商城item
                    goodsViewHolder.getJia_jian_layout().setVisibility(View.GONE);
                    goodsViewHolder.getmExChange().setVisibility(View.VISIBLE);
                    goodsViewHolder.getmExChange().setOnClickListener(onClickListener);
                    String 会员价 = dataBean.getStringDef("MEMBER_PRICE", dataBean.getStringDef("PUB_PRICE", "?"));
                    会员价 = " 积分:" + 会员价;
                    itemView.setOnClickListener(onClickListener);
                    goodsViewHolder.getTvPriceGoodsList().setText(StringUtils.subZeroAndDot(String.valueOf(dataBean.getStringDef("PUB_PRICE", "?"))) + 会员价);

                }else{

                    String 会员价 = dataBean.getStringDef("MEMBER_PRICE", dataBean.getStringDef("PUB_PRICE", "?"));
                    会员价 = " 会员价:" + 会员价;
                    goodsViewHolder.getTvPriceGoodsList().setText(StringUtils.subZeroAndDot(String.valueOf(dataBean.getStringDef("PUB_PRICE", "?"))) + 会员价);

                }


                AppCompatImageView ivGoodsImg = goodsViewHolder.getIvGoodsImg();
                ViewGroup.LayoutParams layoutParams = ivGoodsImg.getLayoutParams();
                int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
                layoutParams.width = width;
                layoutParams.height = (int) (width * 168F / 200F + 0.5F);
                ivGoodsImg.setLayoutParams(layoutParams);
                ImageLoader.getImageFromNetwork(ivGoodsImg, dataBean.getStringDef("IMG_FILE_PATH", ""), mContext, net.twoant.master.R.drawable.ic_def_small);

                AppCompatTextView tvDistanceGoodsList = goodsViewHolder.getTvDistanceGoodsList();

                if (state == IRecyclerViewConstant.STATE_CODE_NOT_DISTANCE) {
                    if (tvDistanceGoodsList.getVisibility() != View.GONE)
                        tvDistanceGoodsList.setVisibility(View.GONE);
                } else {
                    if (tvDistanceGoodsList.getVisibility() != View.VISIBLE)
                        tvDistanceGoodsList.setVisibility(View.VISIBLE);
                    //  ActivityRecyclerControl.getDistance(tvDistanceGoodsList, "-", dataBean.getStringDef("_DISTANCE_TXT", ""));
                }

                goodsViewHolder.getTvGoodsName().setText(dataBean.getString("TITLE"));

                /*AppCompatTextView tvMerchantNameGoodsList = goodsViewHolder.getTvMerchantNameGoodsList();
                tvMerchantNameGoodsList.setTag(dataBean.getString("SHOP_ID"));
                tvMerchantNameGoodsList.setOnClickListener(onClickListener);*/

                AppCompatTextView needPayNum = goodsViewHolder.getMtv_number_goodsdetail();

                needPayNum.setVisibility(View.VISIBLE);
                if (HomeFragment.selectedList != null && HomeFragment.selectedList.containsKey(dataBean.getString("PRODUCT_ID"))) {
                    GoodsItem goodsItem = HomeFragment.selectedList.get(dataBean.getString("PRODUCT_ID"));
                    needPayNum.setText(goodsItem.getGoodsCount() + "");
                } else {
                    needPayNum.setText("0");
                }
                 goodsViewHolder.getTvMerchantNameGoodsList().setText(String.valueOf("库存: " +
                        dataBean.getStringDef("QTY", "未知")));
                goodsViewHolder.getMtv_goodscount().setText("剩余:" + dataBean.getStringDef("QTY", "0"));
                goodsViewHolder.getTvIntroduceGoodsList().setText(dataBean.getStringDef("SUB_TITLE", "暂无"));
              goodsViewHolder.getTvReadCountGoodsList().setText(String.valueOf(dataBean.getStringDef("CLICK", "")));
            }
        } else if (viewType == TYPE_HEADER) {
            DataRow dataRow = mDataBean.get(position);
            if (null == dataRow) {
                return;
            }

            //初始化头部数据
            GoodsHeaderViewHolder goodsHeaderViewHolder = (GoodsHeaderViewHolder) holder;
            if (null!= HomeFragment.shop_data) {

                DataRow detail=HomeFragment.shop_data.getRow("data");
                String shop_name = detail.getString("NM");
                // goodsHeaderViewHolder.getTvShopNameMerchantHomePage().setText(dataRow.getString("TITLE"));
                goodsHeaderViewHolder.getTvShopNameMerchantHomePage().setText("店铺名:"+shop_name);
                goodsHeaderViewHolder.getmIs_open().setBackground(mContext.getResources().getDrawable(detail.getInt("IS_OPEN")==0? net.twoant.master.R.drawable.close: net.twoant.master.R.drawable.open));  //.setText(detail.getInt("IS_OPEN")==0?"已打烊":"营业中");
                goodsHeaderViewHolder.getTvClickMerchantHomePage().setText("销量:"+detail.getStringDef("SELL_QTY","0"));
                // goodsHeaderViewHolder.getDistanceText().setText(detail.getStringDef("_DISTANCE_TXT", ""));
                goodsHeaderViewHolder.getTvShopTelephoneMerchantHomePage().setText(detail.getStringDef("MEMBER_MOBILE", "未设置"));
                goodsHeaderViewHolder.getTvShopAddressMerchantHomePage().setText(String.valueOf("地址:" + detail.getString("SCHOOL_NM")+ detail.getString("BUILD_NM")));
                //  goodsHeaderViewHolder.getTvRecommendMerchantHomePage().setText(detail.getString("DESCRIPTION"));
                goodsHeaderViewHolder.getQisong().setText("起送:满"+detail.getString("MIN_ORDER_PRICE")+"元起送");
                goodsHeaderViewHolder.getYunfei().setText("运费:"+detail.getString("POST_PRICE")+"元(满"+detail.getString("FREE_POST_ORDER_PRICE")+"元免运费)");
            }
        }
    }
//    @Override
//    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
//        GoodsViewHolder goodsViewHolder = (GoodsViewHolder) holder;
//        if (viewType == IRecyclerViewConstant.KIND_MERCHANT_COMMON_GOODS) {
//            View itemView = goodsViewHolder.itemView;
//            itemView.setOnClickListener(onClickListener);
//            DataRow dataBean = mDataBean.get(position);
//            if (mDataBean != null) {
//
//                itemView.setTag(dataBean.getString("ID"));
//
//                AppCompatImageView ivGoodsImg = goodsViewHolder.getIvGoodsImg();
//                ViewGroup.LayoutParams layoutParams = ivGoodsImg.getLayoutParams();
//                int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
//                layoutParams.width = width;
//                layoutParams.height = (int) (width * 168F / 200F + 0.5F);
//                ivGoodsImg.setLayoutParams(layoutParams);
//                ImageLoader.getImageFromNetworkPlaceholderControlImg(ivGoodsImg, BaseConfig.getCorrectImageUrl(dataBean.getStringDef("IMG", "")), mEnvironment, R.drawable.ic_def_small);
//
//                AppCompatTextView tvDistanceGoodsList = goodsViewHolder.getTvDistanceGoodsList();
//
//                if (state == IRecyclerViewConstant.STATE_CODE_NOT_DISTANCE) {
//                    if (tvDistanceGoodsList.getVisibility() != View.GONE)
//                        tvDistanceGoodsList.setVisibility(View.GONE);
//                } else {
//                    if (tvDistanceGoodsList.getVisibility() != View.VISIBLE)
//                        tvDistanceGoodsList.setVisibility(View.VISIBLE);
//                    ControlUtils.getDistance(tvDistanceGoodsList, "-", dataBean.getStringDef("_DISTANCE_TXT", ""));
//                }
//
//                goodsViewHolder.getTvGoodsName().setText(dataBean.getString("NM"));
//
//                /*AppCompatTextView tvMerchantNameGoodsList = goodsViewHolder.getTvMerchantNameGoodsList();
//                tvMerchantNameGoodsList.setTag(dataBean.getString("SHOP_ID"));
//                tvMerchantNameGoodsList.setOnClickListener(onClickListener);*/
//
//                goodsViewHolder.getTvMerchantNameGoodsList().setText(String.valueOf("所属商家: " +
//                        dataBean.getStringDef("SHOP_NM", "未知")));
//
//                goodsViewHolder.getTvIntroduceGoodsList().setText(dataBean.getStringDef("GOODS_INTRODUCE", ""));
//                goodsViewHolder.getTvPriceGoodsList().setText(StringUtils.subZeroAndDot(String.valueOf(dataBean.getStringDef("PRICE", "?"))));
//                goodsViewHolder.getTvReadCountGoodsList().setText(String.valueOf(dataBean.getStringDef("CLICK", "")));
//            }
//        }
//    }

//    @Override
//    public String getUrl(int category) {
//
//        switch (category) {
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME:
//                return ApiConstants.GOODS_LIST_HOME;
//
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_NEARBY:
//                break;
//
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE:
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW:
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY:
//                return ApiConstants.GOODS_LIST_SEARCH;
//        }
//        return "";
//    }
@Override
public String getUrl(int category) {

    switch (category) {
        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME1:
            return ApiConstants.YM_SHOP;
        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME2:
            return ApiConstants.HOME_MINDLE;//GOODS_LIST_HOME;
        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME3:
            return ApiConstants.SCORE_STORE;//GOODS_CARD;

        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME:
            return ApiConstants.GOODS_LIST_HOME;//ApiConstants.GOODS_LIST_4SHOP;//ApiConstants.GOODS_LIST_HOME;

        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_NEARBY:
            break;

        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE:
        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW:
        case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY:
            return ApiConstants.GOODS_LIST_SEARCH;
    }
    return "";
}
    @Override
    protected List<DataRow> subRemoveMethod(boolean nonNull, List<DataRow> resultBean, boolean isRefresh) {
        return ControlUtils.removeDuplicate(resultBean, isRefresh ? null : mDataBean, "PRODUCT_ID");
    }
    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("center", mLatLng);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        mAutoincrement = false;
        switch (category) {
            //首页最新 商品
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME1:
                mKeySet.put("id", "TT00");
                break;
            //首页最新 商品
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME2:
                mKeySet.put("type", "1");
                mKeySet.put("id", "TT00");
                break;
            //首页最新 商品
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME3:
                mKeySet.put("type", "2");
             //   mKeySet.put("sort", "PS00");

                break;
            //首页最新 商品
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME:
                mKeySet.put("id", ShopGoodsActivity.SHOP_ID);//商家CD
              //  ShopGoodsActivity

                break;
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_NEARBY:
                break;
            //搜索商品 距离
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "0");
                break;
            //搜索商品 最新
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "1");
                break;
            //搜索搜索商品 最热
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "2");
                break;
        }
        return mKeySet;
    }
//    @Override
//    public Map<String, String> getParameter(int category) {
//        mKeySet.clear();
//        mKeySet.put("center", mLatLng);
//        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
//        mAutoincrement = false;
//        switch (category) {
//            //首页最新 商品
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME:
//                break;
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_NEARBY:
//                break;
//            //搜索商品 距离
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY:
//                mKeySet.put("k", mKeyword);
//                mKeySet.put("order", "0");
//                break;
//            //搜索商品 最新
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW:
//                mKeySet.put("k", mKeyword);
//                mKeySet.put("order", "1");
//                break;
//            //搜索搜索商品 最热
//            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE:
//                mKeySet.put("k", mKeyword);
//                mKeySet.put("order", "2");
//                break;
//        }
//        return mKeySet;
//    }

    private Object mTemp;

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
        Object id = view.getTag();
        switch (view.getId()) {
            case net.twoant.master.R.id.tv_merchant_name_goods_list:
                if (id instanceof String) {
                    if (id.equals(mTemp)) {
                        return;
                    }
                    mTemp = id;
                    MerchantHomePageActivity.startActivity(adapter.getActivity(), (String) id);
                }
                break;
            case net.twoant.master.R.id.exchange_bt:
                break;
            default://跳转商品详情页
                if (id instanceof String) {
                    if (id.equals(mTemp)) {
                        return;
                    }
                    mTemp = id;
                    Activity activity = adapter.getActivity();
                    if (null != activity) {
                        BuyerGoodsDetailActivity.startActivity(activity, (String) id);
                    }
                }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTemp = null;
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {
        if (isHasHeadView && 0 == position) {
            return TYPE_HEADER;
        }
        return IRecyclerViewConstant.KIND_MERCHANT_COMMON_GOODS;
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {

        DataRow dataRow = DataRow.parseJson(response);
        MainActivity.checkState(adapter.getActivity(), dataRow);

        if (intercept) {
            DataSet dataSet;

            if (dataRow == null || (dataSet = dataRow.getSet("data")) == null || (dataSet.getRows()) == null)
                return null;

            return dataSet.getRows();

        }
        return null;
    }
}
