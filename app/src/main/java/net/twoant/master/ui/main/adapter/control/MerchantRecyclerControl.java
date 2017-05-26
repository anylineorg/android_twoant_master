package net.twoant.master.ui.main.adapter.control;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.holder.MerchantViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/1/16.
 * 商家列表 控制器
 */

public class MerchantRecyclerControl extends BaseRecyclerControlImpl<DataRow> {

    private final ForegroundColorSpan mForegroundColorSpan;

    public MerchantRecyclerControl() {
        super(1);
        mForegroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(mContext, net.twoant.master.R.color.colorPrimary));
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new MerchantViewHolder(inflater.inflate(net.twoant.master.R.layout.yh_item_merchant_list, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow dataBean = mDataBean.get(position);
        MerchantViewHolder merchantViewHolder = (MerchantViewHolder) holder;

        if (dataBean != null) {
            merchantViewHolder.itemView.setOnClickListener(onClickListener);
            merchantViewHolder.itemView.setTag(dataBean.getString("ID"));
            AppCompatImageView ivMerchantImg = merchantViewHolder.getIvMerchantImg();
            ViewGroup.LayoutParams layoutParams = ivMerchantImg.getLayoutParams();
            int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
            layoutParams.width = width;
            layoutParams.height = (int) (width * 168F / 200F + 0.5F);
            ivMerchantImg.setLayoutParams(layoutParams);
            ImageLoader.getImageFromNetwork(ivMerchantImg
                    , dataBean.getString("IMG_FILE_PATH"), mContext, net.twoant.master.R.drawable.ic_def_small);
            merchantViewHolder.getNameMerchantList().setText(String.valueOf(dataBean.getStringDef("NM", "")));
            if (dataBean.getInt("IS_OPEN")==0) {
                merchantViewHolder.getmShop_staue().setBackground(mContext.getResources().getDrawable(net.twoant.master.R.drawable.close));
                merchantViewHolder.itemView.setTag("打烊");
            }else{
                merchantViewHolder.getmShop_staue().setBackground(mContext.getResources().getDrawable(net.twoant.master.R.drawable.open));
                // merchantViewHolder.getNameMerchantList().setText("营业中-"+String.valueOf(dataBean.getStringDef("NM", "")));
            }

            if (dataBean.getInt("IS_OPEN")==0){
                merchantViewHolder.getmMerchant_root().setBackgroundColor(mContext.getResources().getColor(net.twoant.master.R.color.lightGreyColor));
            }
            merchantViewHolder.getTvPhoneNumberMerchantList().setText(String.valueOf("电话:" + dataBean.getStringDef("MEMBER_MOBILE", "-")));

            String seller_count = dataBean.getStringDef("SELL_QTY", "");
            seller_count = seller_count == null ? "" : seller_count;
            SpannableString saleCount = new SpannableString("交易量 " + seller_count + "单");
            saleCount.setSpan(mForegroundColorSpan, 4, 4 + seller_count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            merchantViewHolder.getTvDealVolumeMerchantList().setText(saleCount);

            //是否显示距离
            AppCompatTextView tvDistanceMerchantList = merchantViewHolder.getTvDistanceMerchantList();
            if (state == IRecyclerViewConstant.STATE_CODE_NOT_DISTANCE) {
                if (tvDistanceMerchantList.getVisibility() != View.GONE)
                    tvDistanceMerchantList.setVisibility(View.GONE);
            } else {
                if (tvDistanceMerchantList.getVisibility() != View.VISIBLE)
                    tvDistanceMerchantList.setVisibility(View.VISIBLE);
                ActivityRecyclerControl.getDistance(tvDistanceMerchantList, "-", dataBean.getString("_DISTANCE_TXT"));

            }

            merchantViewHolder.getTvAddressMerchantList().setText(String.valueOf("地址:" + dataBean.getStringDef("SCHOOL_NM", "")+dataBean.getStringDef("BUILD_NM", "")));
        }


//        if (dataBean != null) {
//            merchantViewHolder.itemView.setOnClickListener(onClickListener);
//            merchantViewHolder.itemView.setTag(dataBean.getString("ID"));
//            AppCompatImageView ivMerchantImg = merchantViewHolder.getIvMerchantImg();
//            ViewGroup.LayoutParams layoutParams = ivMerchantImg.getLayoutParams();
//            int width = (int) (mScreenWidth * 200F / 640F + 0.5F);
//            layoutParams.width = width;
//            layoutParams.height = (int) (width * 168F / 200F + 0.5F);
//            ivMerchantImg.setLayoutParams(layoutParams);
//            ImageLoader.getImageFromNetworkPlaceholderControlImg(ivMerchantImg, BaseConfig.getCorrectImageUrl(dataBean.getString("SHOP_AVATAR")), mEnvironment, R.drawable.ic_def_small);
//
//            merchantViewHolder.getNameMerchantList().setText(String.valueOf(dataBean.getStringDef("NM", "")));
//
//            merchantViewHolder.getTvPhoneNumberMerchantList().setText(String.valueOf("电话:" + dataBean.getStringDef("SHOP_TEL", "-")));
//
//            if (0 >= dataBean.getInt("ACTIVITY_QTY")) {
//                if (View.GONE != merchantViewHolder.getTvHasAction().getVisibility()) {
//                    merchantViewHolder.getTvHasAction().setVisibility(View.GONE);
//                }
//            } else {
//                if (View.VISIBLE != merchantViewHolder.getTvHasAction().getVisibility()) {
//                    merchantViewHolder.getTvHasAction().setVisibility(View.VISIBLE);
//                }
//            }
//
//            String clickSum = dataBean.getStringDef("CLICK", "0");
//            clickSum = clickSum == null ? "0" : clickSum;
//            SpannableString click = new SpannableString("查看量 " + clickSum + "次");
//            click.setSpan(mForegroundColorSpan, 4, 4 + clickSum.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            merchantViewHolder.getTvCommentCountMerchantList().setText(click);
//
//            String seller_count = dataBean.getStringDef("SELL_QTY_SUM", "");
//            seller_count = seller_count == null ? "" : seller_count;
//            SpannableString saleCount = new SpannableString("交易量 " + seller_count + "单");
//            saleCount.setSpan(mForegroundColorSpan, 4, 4 + seller_count.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            merchantViewHolder.getTvDealVolumeMerchantList().setText(saleCount);
//
//            //是否显示距离
//            AppCompatTextView tvDistanceMerchantList = merchantViewHolder.getTvDistanceMerchantList();
//            if (state == IRecyclerViewConstant.STATE_CODE_NOT_DISTANCE) {
//                if (tvDistanceMerchantList.getVisibility() != View.GONE)
//                    tvDistanceMerchantList.setVisibility(View.GONE);
//            } else {
//                if (tvDistanceMerchantList.getVisibility() != View.VISIBLE)
//                    tvDistanceMerchantList.setVisibility(View.VISIBLE);
//                ControlUtils.getDistance(tvDistanceMerchantList, "-", dataBean.getString("_DISTANCE_TXT"));
//
//            }
//
//            merchantViewHolder.getTvAddressMerchantList().setText(String.valueOf("地址:" + dataBean.getStringDef("SHOP_ADDRESS", "")));
//        }
    }

    @Override
    public String getUrl(int category) {

        switch (category) {
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME1:
                return ApiConstants.YM_SHOP;
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_HOME:
                return ApiConstants.MERCHANT_LIST_HOME;

            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_NEARBY:
                return ApiConstants.MERCHANT_LIST_NEARBY;

            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_COMMENT:
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_SALES:
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_NEARBY:
                return ApiConstants.MERCHANT_LIST_SEARCH;
        }
        return "";
    }


    @Override
    public Map<String, String> getParameter(int category) {

        mKeySet.clear();
        mAutoincrement = false;
        mKeySet.put("center", mLatLng);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));

        switch (category) {
            case IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME1:
                mKeySet.put("type", "0");
                mKeySet.put("type", "0");
                break;
            //附近的商家
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_NEARBY:
                break;
            //首页最新商家
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_HOME:
                break;
            //搜索商家 按浏览量
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_COMMENT:
                mKeySet.put("k", mKeyword);
                //按浏览量排序
                mKeySet.put("order", "2");
                break;
            //搜索商家 按销量
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_SALES:
                mKeySet.put("k", mKeyword);
                //按销售量排序
                mKeySet.put("order", "1");
                break;
            //搜索商家 按距离
            case IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_SEARCH_NEARBY:
                mKeySet.put("k", mKeyword);
                //按距离
                mKeySet.put("order", "0");
                break;
        }
        return mKeySet;
    }

    private Object mTemp;

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
//        Object tag = view.getTag();
//        if (tag instanceof String) {
//            if (tag.equals(mTemp)) {
//                return;
//            }
//            mTemp = tag;
//            MerchantHomePageActivity.startActivity(adapter.getActivity(), (String) tag);
//        }
        Object tag = view.getTag();
        if (tag instanceof String) {
            if (tag.equals(mTemp)) {
                return;
            }
            mTemp = tag;
//            Intent intent=new Intent(adapter.getActivity(),ShopGoodsActivity.class);
//            intent.putExtra("ID",tag+"");
//            adapter.getActivity().startActivity(intent);
            if (tag.equals("打烊")){
                ToastUtil.showLong("已经打烊了,明天再来吧");
                return;
            }
            MerchantHomePageActivity.startActivity(adapter.getActivity(), (String) tag);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTemp = null;
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {
        return IRecyclerViewConstant.KIND_MERCHANT_LIST_COMMON;
    }

    @Override
    protected List<DataRow> subRemoveMethod(boolean nonNull, List<DataRow> resultBean, boolean isRefresh) {
        return ControlUtils.removeDuplicate(resultBean, isRefresh ? null : mDataBean, "ID");
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
