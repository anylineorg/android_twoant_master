package net.twoant.master.ui.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.main.activity.BuyerGoodsDetailActivity;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.adapter.MerchantHomeGoodsAdapter;
import net.twoant.master.ui.main.adapter.MerchantHomeGoodsCategoryAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.interfaces.IOnItemClickListener;
import net.twoant.master.ui.main.interfaces.IOnLoadingDataListener;
import net.twoant.master.ui.main.interfaces.ISuspensionTitle;
import net.twoant.master.ui.main.widget.SuspensionDecoration;
import net.twoant.master.ui.my_center.bean.ClassifyListBean;
import net.twoant.master.widget.ScrollHeaderLinearLayout;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2016/12/13.
 * 商家首页的商品
 */

public class MerchantHomePageGoodsFragment extends ViewPagerBaseFragment implements BaseRecyclerAdapter.ILoadingData, HttpConnectedUtils.IOnStartNetworkSimpleCallBack, ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer {

    private final static int ID_GET_GOODS = 1;
    private final static int ID_GET_CATEGORY = 2;
    private static final String EXTRA_SHOP_ID = "FE_SI";
    public final static int REQUEST_CODE = 11;

    /**
     * 商店id
     */
    private String mShopId;

    /**
     * 右侧 分页
     */
    private int mRightIndex = 1;

    /**
     * 分类id
     */
    private String mCategoryId;

    private boolean mCanScrollable;

    /**
     * 当前 被触摸的RecyclerView
     */
    private RecyclerView mRecyclerView;

    /**
     * 是接着加，还是清空加
     */
    private boolean isSwitchCategory;

    /**
     * 当前分类, 加载结束
     */
    private boolean isLoadingEnd;

    private boolean isRequestNetwork;

    private RecyclerView mRvLeftView;
    private HttpConnectedUtils mHttpConnectedUtils;
    private ArrayMap<String, String> mArrayMap = new ArrayMap<>();
    private MerchantHomeGoodsCategoryAdapter mLeftMerchantHomeGoodsAdapter;
    private MerchantHomeGoodsAdapter mRightMerchantHomeGoodsAdapter;

    public static MerchantHomePageGoodsFragment newInstance(@NonNull String shopId) {
        MerchantHomePageGoodsFragment fragment = new MerchantHomePageGoodsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_SHOP_ID, shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mSI", mShopId);
        outState.putInt("mRI", mRightIndex);
        outState.putString("mCI", mCategoryId);
        outState.putBoolean("iSC", isSwitchCategory);
        outState.putBoolean("iLE", isLoadingEnd);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mShopId = arguments.getString(EXTRA_SHOP_ID);
        }
    }

    @NonNull
    public ArrayList<GoodsItemBean> getGoodsList() {
        if (mRightMerchantHomeGoodsAdapter != null) {
            return mRightMerchantHomeGoodsAdapter.getGoodsItems();
        }
        return new ArrayList<>();
    }

    public void setGoodsList(@NonNull ArrayMap<String, GoodsItemBean> goodsList) {
        if (null != mRightMerchantHomeGoodsAdapter) {
            mRightMerchantHomeGoodsAdapter.updateList(goodsList);
        }
    }

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.yh_fragment_goods_merchant_home_page;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mShopId = savedInstanceState.getString("mSI");
            mRightIndex = savedInstanceState.getInt("mRI");
            mCategoryId = savedInstanceState.getString("mCI");
            isSwitchCategory = savedInstanceState.getBoolean("iSC");
            isLoadingEnd = savedInstanceState.getBoolean("iLE");
        }
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        initView(view);
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHttpConnectedUtils != null) {
            mHttpConnectedUtils.onDestroy();
        }
    }

    @Override
    protected void onUserInvisible() {
    }

    @Override
    public View getScrollableView() {
        return mRecyclerView;
    }

    @Override
    public boolean canScrollable() {
        return mCanScrollable;
    }

    private void initView(View view) {
        //右侧 商品列表
        RecyclerView mRvRightView = (RecyclerView) view.findViewById(net.twoant.master.R.id.rv_right_view);
        mRvLeftView = (RecyclerView) view.findViewById(net.twoant.master.R.id.rv_left_view);

        mLeftMerchantHomeGoodsAdapter = new MerchantHomeGoodsCategoryAdapter();
        mLeftMerchantHomeGoodsAdapter.setOnItemClickListener(new IOnItemClickListener<String>() {
            @Override
            public void onItemClickListener(String id) {
                if (id != null) {
                    if (!id.equals(mCategoryId)) {
                        isSwitchCategory = true;
                        mRightIndex = 1;
                        onLoadingRightData(id);
                    }
                }
            }

            @Override
            public void onAutoClickListener(String id) {
                if (id != null) {
                    if (!id.equals(mCategoryId)) {
                        mRightIndex = 1;
                        onLoadingRightData(id);
                    }
                }
            }
        });

        mLeftMerchantHomeGoodsAdapter.setOnLoadingDataListener(new IOnLoadingDataListener() {
            @Override
            public void onLoadingDataListener() {
                getCategory();
            }
        });

        mRvLeftView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvLeftView.setAdapter(mLeftMerchantHomeGoodsAdapter);
        mRvLeftView.addItemDecoration(new InnerRecyclerItemDecoration(mContext, net.twoant.master.R.color.lightGreyColor, net.twoant.master.R.dimen.px_1, false));
        mRvLeftView.setHasFixedSize(true);

        mRvRightView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRightMerchantHomeGoodsAdapter = new MerchantHomeGoodsAdapter(getActivity(), this);
        mRvRightView.setAdapter(mRightMerchantHomeGoodsAdapter);
        mRvRightView.setHasFixedSize(true);

        mRightMerchantHomeGoodsAdapter.setOnBtnClickListener(new MerchantHomeGoodsAdapter.IOnBtnClickListener() {
            @Override
            public void onBtnClickListener(View view, String goodsId, boolean isAdd) {
                FragmentActivity activity = getActivity();
                if (activity instanceof MerchantHomePageActivity) {
                    ((MerchantHomePageActivity) activity).getBuyCartView().changeGoodsCountNotNotify(isAdd, goodsId, view);
                }
            }
        });

//        mRvRightView.addItemDecoration(new InnerRecyclerItemDecoration(mActivity, R.color.lightGreyColor, R.dimen.px_1, true));
        mRvRightView.addItemDecoration(new SuspensionDecoration<>(mContext, mRightMerchantHomeGoodsAdapter
                , net.twoant.master.R.color.principalTitleTextColor, net.twoant.master.R.color.whiteGreyColor, net.twoant.master.R.dimen.px_10).setISuspensionTitle(new ISuspensionTitle<DataRow>() {
            @Override
            public void changeTitle(int positionD, DataRow data) {
                loadingNextCategoryData(false, true, data);
            }
        }));

        mRightMerchantHomeGoodsAdapter.setOnItemClickListener(new IOnItemClickListener<String>() {
            @Override
            public void onItemClickListener(String id) {
//                FragmentActivity activity = getActivity();
//                if (null != activity) {
//                    BuyerGoodsDetailActivity.startActivityForResult(MerchantHomePageGoodsFragment.this, activity, id, mRightMerchantHomeGoodsAdapter.getGoodsItems(), MerchantHomePageGoodsFragment.REQUEST_CODE);
//                }
            }

            @Override
            public void onAutoClickListener(String s) {
            }
        });

        mRecyclerView = mRvRightView;
        InnerRecyclerOnTouchListener listener = new InnerRecyclerOnTouchListener();
        mRvLeftView.setOnTouchListener(listener);
        mRvRightView.setOnTouchListener(listener);
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_GET_CATEGORY:
//                ClassifyListBean classifyListBean = new Gson().fromJson(response, ClassifyListBean.class);
//                if (classifyListBean != null) {
//                    List<ClassifyListBean.ResultBean> result = classifyListBean.getResult();
//                    mLeftMerchantHomeGoodsAdapter.setDataBean(result);
//                    mRightMerchantHomeGoodsAdapter.setTitleList(result);
//                    if (result.size() != 0) {
//                        ClassifyListBean.ResultBean resultBean = result.get(0);
//                        if (resultBean != null) {
//                            isSwitchCategory = true;
//                            onLoadingRightData(String.valueOf(resultBean.getGoods_typeid()));
//                        }
//                    } else {
//                        mLeftMerchantHomeGoodsAdapter.showErrorLoadingHint(mRvLeftView, "没有更多");
//                        resetRightAdapterNetData("没有商品");
//                    }
//                } else {
//                    mLeftMerchantHomeGoodsAdapter.showErrorLoadingHint(mRvLeftView, "没有更多");
//                    resetRightAdapterNetData("没有商品");
//                }

                DataRow data_set=DataRow.parseJson(response);
                //    data=data.getRow("data");
                ClassifyListBean classifyListBean = new ClassifyListBean(); //new Gson().fromJson(response, ClassifyListBean.class);
                if (data_set!=null && classifyListBean != null) {
                    List<ClassifyListBean.ResultBean> result = new ArrayList<>();
                    DataSet set=data_set.getSet("data");
                    if (set==null){
                        return;
                    }
                    for (int i = 0; i < set.getRows().size(); i++) {
                        DataRow row=set.getRow(i);
                        ClassifyListBean.ResultBean bean=new ClassifyListBean.ResultBean();
                        bean.setCD(row.getString("ID"));
                        bean.setNM(row.getString("NM"));
                        result.add(bean);

                    }

                    mLeftMerchantHomeGoodsAdapter.setDataBean(result);
                    mRightMerchantHomeGoodsAdapter.setTitleList(result);

                    mLeftMerchantHomeGoodsAdapter.showErrorLoadingHint(mRvLeftView, "没有更多");

                    mRightMerchantHomeGoodsAdapter.showErrorLoadingHint("没有更多");

                    resetRightAdapterNetData("没有更多");
                } else {
                    mLeftMerchantHomeGoodsAdapter.showErrorLoadingHint(mRvLeftView, "没有更多");
                    resetRightAdapterNetData("没有商品");
                }
                break;
            case ID_GET_GOODS:
//                isRequestNetwork = false;
//                DataRow dataRow = DataRow.parseJson(response);
//                if (dataRow != null) {
//                    if (dataRow.getBoolean("result", false)) {
//                        DataSet data = dataRow.getSet("data");
//                        if (data != null) {
//                            List<DataRow> list = mRightMerchantHomeGoodsAdapter.setDataBean(data.getRows(), isSwitchCategory);
//                            isSwitchCategory = false;
//                            if (data.size() <= 0 || list.size() == 0) {
//                                if (mLeftMerchantHomeGoodsAdapter.getTempCurrentPosition() < mLeftMerchantHomeGoodsAdapter.getItemCount() - 1) {
//                                    isLoadingEnd = false;
//                                    mCategoryId = "null";
//                                    if (mRightMerchantHomeGoodsAdapter.isLoadingOrRefreshVisibility()) {
//                                        getData();
//                                    }
//                                } else {
//                                    resetRightAdapterNetData("没有更多");
//                                    mCategoryId = null;
//                                    isLoadingEnd = true;
//                                }
//                            }
//                        }
//                    } else {
//                        resetRightAdapterNetData("没有更多");
//                        isLoadingEnd = true;
//                    }
//                } else {
//                    resetRightAdapterNetData("没有更多");
//                    isLoadingEnd = true;
//                }
                isRequestNetwork = false;
                DataRow dataRow = DataRow.parseJson(response);
                if (dataRow != null) {
                    if (dataRow.getBoolean("result", false)) {
                        DataSet data = dataRow.getSet("data");
                        if (data != null) {
                            List<DataRow> list = mRightMerchantHomeGoodsAdapter.setDataBean(data.getRows(), isSwitchCategory);
                            isSwitchCategory = false;
                            if (data.size() <= 0 || list.size() == 0) {
                                resetRightAdapterNetData("没有更多");
                                mCategoryId = null;
                                isLoadingEnd = true;
                            }
                        }
                    } else {
                        resetRightAdapterNetData("没有更多");
                        isLoadingEnd = true;
                    }
                } else {
                    resetRightAdapterNetData("没有更多");
                    isLoadingEnd = true;
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (id == ID_GET_CATEGORY) {
            mLeftMerchantHomeGoodsAdapter.showErrorLoadingHint(mRvLeftView, NetworkUtils.getNetworkStateDescription(call, e, "获取失败"));
            resetRightAdapterNetData(NetworkUtils.getNetworkStateDescription(call, e, "获取失败"));
        } else if (id == ID_GET_GOODS) {
            isRequestNetwork = false;
            resetRightAdapterNetData(NetworkUtils.getNetworkStateDescription(call, e, "获取失败"));
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<GoodsItemBean> arrayList;
            if (data != null && null != (arrayList = data.getParcelableArrayListExtra(BuyerGoodsDetailActivity.EXTRA_GOODS_LIST))) {//GoodsDetailActivity.EXTRA_GOODS_LIST
                if (arrayList.size() != 0) {
                    ArrayMap<String, GoodsItemBean> goods = new ArrayMap<>();
                    for (GoodsItemBean goodsItem : arrayList) {
                        goods.put(goodsItem.getGoodsId(), goodsItem);
                    }
                    mRightMerchantHomeGoodsAdapter.setGoodsItems(goods, true);
                } else {
                    mRightMerchantHomeGoodsAdapter.setGoodsItems(null, false);
                }
                FragmentActivity activity = getActivity();
                if (activity instanceof MerchantHomePageActivity) {
                    ((MerchantHomePageActivity) activity).getBuyCartView().initCartPriceData(arrayList);
                }
            }
        }
    }

    /**
     * 重置参数状态
     *
     * @param hintInfo 提示信息
     */
    private void resetRightAdapterNetData(String hintInfo) {
        mRightIndex = --mRightIndex <= 1 ? 1 : mRightIndex;
        mRightMerchantHomeGoodsAdapter.showErrorLoadingHint(hintInfo);
    }

    /**
     * 获取对应商品分类下的商品
     *
     * @param integer 商品id
     */
    private void onLoadingRightData(String integer) {
        mArrayMap.clear();
        if (!isRequestNetwork && integer != null) {
            isRequestNetwork = true;
            mCategoryId = integer;
            mArrayMap.put("id", mShopId);
            mArrayMap.put("sort", mCategoryId);
            mArrayMap.put("_anyline_page", String.valueOf(mRightIndex++));
            mHttpConnectedUtils.startNetworkGetString(ID_GET_GOODS, mArrayMap, ApiConstants.GOODS_LIST_HOME);

        }
    }

    /**
     * 获取商品分类
     */
    private void getCategory() {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("action", "0");
        arrayMap.put("tt", mShopId);
        mHttpConnectedUtils.startNetworkGetString(ID_GET_CATEGORY, arrayMap, ApiConstants.MERCHANT_GOODS_CATEGORY);
    }

    @Override
    public void getData() {
//        if (mCategoryId != null) {
//            if ("null".equals(mCategoryId)) {
//                loadingNextCategoryData(true, false, null);
//            } else {
//                onLoadingRightData(mCategoryId);
//            }
//
//        }
        onLoadingRightData();
    }
    /**
     * 获取对应商品分类下的商品
     *
     * @param
     */
    private void onLoadingRightData() {
        mArrayMap.clear();
        if (!isRequestNetwork) {
            isRequestNetwork = true;
            mArrayMap.put("id", mShopId);
            mArrayMap.put("_anyline_page", String.valueOf(mRightIndex++));
            mHttpConnectedUtils.startNetworkGetString(ID_GET_GOODS, mArrayMap, ApiConstants.GOODS_LIST_HOME);
        }
    }
    /**
     * @param autoLoading 加载
     * @param change      改变 分类列表的状态展示
     * @param data        商品的实体数据
     */
    private void loadingNextCategoryData(boolean autoLoading, boolean change, DataRow data) {
        List<ClassifyListBean.ResultBean> dataBean = mLeftMerchantHomeGoodsAdapter.getDataBean();
        if (data != null && dataBean != null) {
            String type = data.getStringDef("ID", "");
            ClassifyListBean.ResultBean currentResult = null;
            for (ClassifyListBean.ResultBean resultBean : dataBean) {
                if (type.equals(resultBean.getCD())) {
                    currentResult = resultBean;
                    break;
                }
            }

            int index = dataBean.indexOf(currentResult);

            if (index < dataBean.size() - 1) {
                mLeftMerchantHomeGoodsAdapter.setCurrentItem(autoLoading, change, mRvLeftView.getLayoutManager().findViewByPosition(index)
                        , dataBean.get(index).getCD(), index);
            }

        } else {
            int position = mLeftMerchantHomeGoodsAdapter.getTempCurrentPosition();
            if (dataBean != null && position + 1 < dataBean.size()) {
                mRightIndex = 1;
                mLeftMerchantHomeGoodsAdapter.setCurrentItem(autoLoading, change, mRvLeftView.getLayoutManager().findViewByPosition(position + 1),
                        dataBean.get(position + 1).getCD(), position + 1);
            }
        }
    }

    @Override
    public boolean canLoadingData() {
        return !isSwitchCategory && !isLoadingEnd;
    }

    private class InnerRecyclerOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mRecyclerView != v)
                mRecyclerView = (RecyclerView) v;
            mCanScrollable = mRvLeftView != v;
            return false;
        }
    }

    private static class InnerRecyclerItemDecoration extends RecyclerView.ItemDecoration {

        private final int mSize;
        private final boolean mIsLeftBottom;
        private Paint mDividerPaint;
        private final int mPaddingSize;

        InnerRecyclerItemDecoration(Context context, @ColorRes int color,
                                    @DimenRes int size, boolean isLeftBottom) {
            Resources resources = context.getResources();
            mSize = resources.getDimensionPixelSize(size);
            mPaddingSize = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_10);
            mIsLeftBottom = isLeftBottom;
            mDividerPaint = new Paint();
            mDividerPaint.setColor(ContextCompat.getColor(context, color));

        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + mPaddingSize;
            int right = parent.getWidth() - parent.getPaddingRight() - mPaddingSize;
            View view;
            float top, bottom;
            for (int i = 0; i < childCount; ++i) {
                view = parent.getChildAt(i);
                top = view.getBottom();
                bottom = top + mSize;
                c.drawRect(left, top, right, bottom, mDividerPaint);
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            if (mIsLeftBottom) {
                outRect.set(mSize, 0, 0, mSize);
            } else {
                outRect.set(0, mSize, 0, 0);
            }

        }


    }

}
