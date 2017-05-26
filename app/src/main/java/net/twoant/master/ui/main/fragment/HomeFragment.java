package net.twoant.master.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.amap.api.services.district.DistrictItem;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.FragmentViewPagerAdapter;
import net.twoant.master.base_app.HomeBaseFragment;
import net.twoant.master.common_utils.BitmapUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.ActionListActivity;
import net.twoant.master.ui.main.activity.DiscoverActivity;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.activity.MerchantEnteredActivity;
import net.twoant.master.ui.main.activity.MerchantListActivity;
import net.twoant.master.ui.main.activity.SearchActivity;
import net.twoant.master.ui.main.adapter.control.GoodsRecyclerControl;
import net.twoant.master.ui.main.interfaces.IOnRefreshListener;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.HomePageSearchHelper;
import net.twoant.master.ui.main.widget.HomePageToolbarSetter;
import net.twoant.master.ui.main.widget.MySwipeRefreshLayout;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSearchListener;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSwitchListener;
import net.twoant.master.ui.main.widget.search_toolbar.SearchToolbar;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.widget.entry.DataRow;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;


/**
 * Created by S_Y_H  on 2016/11/18.
 * 首页 fragment
 */

public class HomeFragment extends HomeBaseFragment implements SwipeRefreshLayout.OnRefreshListener, IOnSwitchListener, View.OnClickListener, HttpConnectedUtils.IOnStartNetworkSimpleCallBack {

    private final static int ID_BANNER = 0;
    private final static int ID_CHECK = 1;
    public static DataRow shop_data;
    public static HashMap<String,GoodsItem> selectedList;//bottomsheet选中集合
    private ConvenientBanner<DataRow> mConvenientBannerMainActivity;
    private SearchToolbar mSearchToolbar;
    private TabLayout mTabLayoutNew;
    private ViewPager mVpContainer;
    private IOnRefreshListener iOnRefreshListener;
    private ActivityFragment[] mFragments;
    private ActivityFragment mActivityFragment;
    private List<DataRow> mBannerListBean;
    private HttpConnectedUtils mConnectedUtils;
    private ArrayMap<String, String> mParameter;
    private MySwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton fab_back_top;
    private float MIN_ORDER_PRICE;//满多少可用
    public static String address;
    /**
     * 用户状态检查是否成功
     */
    private boolean isCheckSuccessful;
    private CBViewHolderCreator<BannerImageHolderView> mViewCBViewHolderCreator;
    private boolean isEnable;


    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.yh_fragment_home;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView();
        initBanner();
        initTabData();
        initToolbar();
        mConnectedUtils = HttpConnectedUtils.getInstance(this);
        startNetwork();
        getshop_info();
    }

    /**
     * 初始化toolbar数据
     */
    private void initToolbar() {
        HomePageToolbarSetter iSearchToolbarSetter = new HomePageToolbarSetter(mActivity);
        mSearchToolbar = (SearchToolbar) mView.findViewById(net.twoant.master.R.id.st_tool_bar);
        HomePageSearchHelper homePageSearchHelper = new HomePageSearchHelper();
        mSearchToolbar.setSearchHelperBase(iSearchToolbarSetter, homePageSearchHelper);
        iSearchToolbarSetter.setOnSwitchListener(this);
        homePageSearchHelper.setOnSearchListener(new IOnSearchListener() {
            @Override
            public void onSearchListener(String type, String keyword) {
                SearchActivity.startActivity(mActivity, type, keyword);
            }
        });
    }

    private void startNetwork() {
        if (mConnectedUtils == null) return;

        if (mParameter == null)
            mParameter = new ArrayMap<>();

        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        String latLng = instance.getLongitude() + "," + instance.getLatitude();
       /* if (instance.isUserSelectLocation()) {
            latLng = instance.getSelectLongitude() + "," + instance.getSelectLongitude();
        } else {
            latLng = instance.getLongitude() + "," + instance.getLatitude();
        }*/

        mParameter.clear();
        mParameter.put("ad_index", "0");
        mParameter.put("center", latLng);
        mConnectedUtils.startNetworkGetString(ID_BANNER, mParameter, ApiConstants.HOME_PAGE_BANNER);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCheckSuccessful = savedInstanceState.getBoolean("iCS");
        isEnable = savedInstanceState.getBoolean("iE");
    }

    private boolean isImageChangeState = ImageLoader.canLoadingImg();

    @Override
    protected void onUserVisible() {
        if (!isCheckSuccessful && mConnectedUtils != null) {
            if (mParameter == null) {
                mParameter = new ArrayMap<>();
            } else {
                mParameter.clear();
            }

            mParameter.put("user", AiSouAppInfoModel.getInstance().getUID());
            mConnectedUtils.startNetworkGetString(ID_CHECK, mParameter, ApiConstants.CHECK_USER);
        }

        if (isImageChangeState != ImageLoader.canLoadingImg()) {
            isImageChangeState = ImageLoader.canLoadingImg();
            refreshData();
        }
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onResume() {
        requestNetForInformation();
        super.onResume();
        if (null != mSearchToolbar) {
            mSearchToolbar.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mSearchToolbar) {
            mSearchToolbar.onDestroy();
        }

        if (mConnectedUtils != null) {
            mConnectedUtils.onDestroy();
        }
    }
    private void longHttp(String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getToken());
        OkHttpUtils.post().url(ApiConstants.USER_INFO).params(map).build().execute(callback);
    }
    //更新会员状态
    private void requestNetForInformation() {
        HashMap<String,String> m = new HashMap<>();
        m.put("_t", AiSouAppInfoModel.getInstance().getToken());
        longHttp("", m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("网络连接失败"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                DataRow row = DataRow.parseJson(response);
                row = row.getRow("data");
                address = row.getString("RECEIVE_ROOM");//("RECEIVE_ADDRESS");
                AiSouAppInfoModel.SCHOOL_CD=row.getString("RECEIVE_SCHOOL_ID");//row.getString("SCHOOL_CD");
                AiSouAppInfoModel.BUILD_CD=row.getString("RECEIVE_BUILD_ID");//row.getString("BUILD_CD");
                AiSouAppInfoModel.RECEIVE_MOBILE=row.getString("RECEIVE_MOBILE");
                AiSouAppInfoModel.RECEIVE_NAME=row.getString("RECEIVE_NAME");
                MIN_ORDER_PRICE=row.getFloat("MIN_ORDER_PRICE");
                AiSouAppInfoModel.IS_VIP = row.getInt("IS_VIP");

            }
        });
    }

    private void getshop_info(){

        OkHttpUtils.post().url(ApiConstants.HOME_SHOP_INFO).params(new HashMap<String, String>()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                DataRow row=DataRow.parseJson(response);
                shop_data=row;
                Shop_nm=row.getRow("data").getString("NM");
                SHOP_ID=row.getRow("data").getString("ID");
            }
        });
    }

    public String Shop_nm,SHOP_ID;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("iCS", isCheckSuccessful);
        outState.putBoolean("iE", isEnable);
    }

    private void initTabData() {
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getChildFragmentManager(), null);

        mFragments = new ActivityFragment[]{
//                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
//                        , IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW
//                        // , IRecyclerViewConstant.TYPE_ACTIVITY
//                , IRecyclerViewConstant.CATEGORY_ACTIVITY_LIST_HOME, null, false)
//
//                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
//                , IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW,
//                IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME, null, false)
//
//
//                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
//                , IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW,
//                IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_HOME, null, false)};
                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
                        , IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW
                        //, IRecyclerViewConstant.CATEGORY_ACTIVITY_LIST_HOME, null, false)
                        , IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME1, null, false)
                ,


                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_NORMAL, GoodsRecyclerControl.TYPE_HEADER
                        , IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME2, null, false)
                ,

                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, GoodsRecyclerControl.SCORE_STORE//IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
                        , IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW,
                        IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME3, null, false)};

        adapter.setFragmentList(mFragments);

        mVpContainer.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                iOnRefreshListener = mActivityFragment = mFragments[position];
            }
        });
        mVpContainer.setAdapter(adapter);
        mVpContainer.setOffscreenPageLimit(2);
        iOnRefreshListener = mActivityFragment = mFragments[0];
        mTabLayoutNew.setupWithViewPager(mVpContainer);
        mTabLayoutNew.removeAllTabs();
        mTabLayoutNew.addTab(addTab(net.twoant.master.R.drawable.ic_home_activity, "附近店铺", true));
        mTabLayoutNew.addTab(addTab(net.twoant.master.R.drawable.ic_home_good, "常用商品", false));
        mTabLayoutNew.addTab(addTab(net.twoant.master.R.drawable.ic_home_merchant, "积分商城", false));

        mTabLayoutNew.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    View textView = viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                    if (textView instanceof AppCompatTextView) {
                        ((AppCompatTextView) textView).setTextColor(BitmapUtils.getColorFromRes(net.twoant.master.R.color.currentSelectColor));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    View textView = viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                    if (textView instanceof AppCompatTextView) {
                        ((AppCompatTextView) textView).setTextColor(BitmapUtils.getColorFromRes(net.twoant.master.R.color.principalTitleTextColor));
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    View textView = viewGroup.getChildAt(viewGroup.getChildCount() - 1);
                    if (textView instanceof AppCompatTextView) {
                        ((AppCompatTextView) textView).setTextColor(BitmapUtils.getColorFromRes(net.twoant.master.R.color.currentSelectColor));
                    }
                }
            }
        });
    }

    @Override
    public void onSwitchListener(boolean isSuccessful, ArrayList<DistrictItem> districtItems) {
        if (isSuccessful) {
            refreshData();
            ToastUtil.showShort("区域切换成功");
        } else {
            ToastUtil.showShort("区域切换失败");
            if (mSearchToolbar != null) {
                mSearchToolbar.getSearchToolbarLayout().getTvLocationNavigation().setText("区域");
            }
        }
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        if (iOnRefreshListener != null) {
            iOnRefreshListener.onRefreshListener();
            for (ActivityFragment fragment : mFragments) {
                if (iOnRefreshListener != fragment) {
                    fragment.onRefreshListener();
                }
            }
        }
        startNetwork();
    }

    /**
     * 初始化 广告数据
     */
    private void initBannerData(final List<DataRow> list) {
        mBannerListBean = list;

        if (mViewCBViewHolderCreator == null)
            mViewCBViewHolderCreator = new CBViewHolderCreator<BannerImageHolderView>() {
                @Override
                public BannerImageHolderView createHolder() {
                    return new BannerImageHolderView();
                }
            };

        mConvenientBannerMainActivity.setPages(mViewCBViewHolderCreator, list);
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_BANNER:

                /*HomePageBannerBean homePageBannerBean = new Gson().fromJson(response, HomePageBannerBean.class);
                List<HomePageBannerBean.ResultBean> resultBeen;
                if ((resultBeen = homePageBannerBean.getResult()) != null && resultBeen.size() != 0) {
                    initBannerData(resultBeen);
                }*/

                DataRow row = DataRow.parseJson(response);
                if (row != null && row.getSet("data") != null) {
                    initBannerData(row.getSet("data").getRows());
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;

            case ID_CHECK:
                DataRow dataRow = DataRow.parseJson(response);
                if (dataRow != null && dataRow.getBoolean("result", false)) {
                    if (dataRow.getInt("data") == 0) {
                        MainActivity.accountRemove(mActivity);
                    } else {
                        isCheckSuccessful = true;
                    }
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (null != iOnRefreshListener) {
            iOnRefreshListener.onRefreshListener();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        startNetwork();
    }

    /**
     * 首页的轮播图
     */
    private static class BannerImageHolderView implements Holder<DataRow> {
        private AppCompatImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new AppCompatImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, DataRow data) {
            ImageLoader.getImageFromNetwork(imageView, data == null ? "" : data.getStringDef("IMG_FILE_PATH", ""), context, net.twoant.master.R.drawable.ic_def_large);
        }
    }


    private TabLayout.Tab addTab(@DrawableRes int rest, String title, boolean isSelect) {
        TabLayout.Tab activityTab = mTabLayoutNew.newTab().setCustomView(net.twoant.master.R.layout.yh_tab_layout_custom_view);
        View activityView = activityTab.getCustomView();
        if (activityView != null) {
            ((AppCompatImageView) activityView.findViewById(net.twoant.master.R.id.iv_icon_tab_layout)).setImageResource(rest);
            if (isSelect) {
                AppCompatTextView textView = ((AppCompatTextView) activityView.findViewById(net.twoant.master.R.id.tv_title_tab_layout));
                textView.setText(title);
                textView.setTextColor(BitmapUtils.getColorFromRes(net.twoant.master.R.color.currentSelectColor));
            } else
                ((AppCompatTextView) activityView.findViewById(net.twoant.master.R.id.tv_title_tab_layout)).setText(title);
        }
        return activityTab;
    }

    private void initBanner() {
        mConvenientBannerMainActivity.setPointViewVisible(true)
                .setPageIndicator(new int[]{net.twoant.master.R.drawable.yh_banner_grey_dot, net.twoant.master.R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(4000);

        mConvenientBannerMainActivity.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                DataRow resultBean = mBannerListBean.get(position);
//
//                if (resultBean != null) {
//                    String temp;
//                    /*
//                     * 0是跳商品详情, 1是活动详情, 2是商家详情， 3是跳链接
//                     * {"ad_picurl":"http://xxx/1.png","ad_jump_url":"","ad_shop_id":-1,"ad_goods_id":-1,
//                     "ad_activity_id":-1,"ad_type":3}
//                     */
//                    switch (resultBean.getInt("ad_type")) {
//                        case 0:
//                            if ((temp = resultBean.getString("ad_goods_id")) != null && !("-1".equals(temp))) {
//                                GoodsDetailActivity.startActivity(mActivity, temp, null);
//                            }
//                            break;
//                        case 1:
//                            if ((temp = resultBean.getString("ad_activity_id")) != null && !("-1".equals(temp))) {
//                                int ad_activity_type = resultBean.getInt("ad_activity_type");
//                                if (ad_activity_type >= 0 && ad_activity_type < 4)
//                                    ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_MERCHANT
//                                            , mActivity, temp, ad_activity_type, true);
//                            }
//                            break;
//                        case 2:
//                            if ((temp = resultBean.getString("ad_shop_id")) != null && !("-1".equals(temp))) {
//                                MerchantHomePageActivity.startActivity(mActivity, temp);
//                            }
//                            break;
//                        case 3:
//                            if ((temp = resultBean.getString("ad_jump_url")) != null) {
//                                DiscoverActivity.startActivity(mActivity, "广告", temp);
//                            }
//                            break;
//                    }
//                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initView() {
        mSwipeRefreshLayout = (MySwipeRefreshLayout) mView.findViewById(net.twoant.master.R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        AppBarLayout viewById = (AppBarLayout) mView.findViewById(net.twoant.master.R.id.appbar);
        viewById.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isEnable;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (!isEnable && 0 == verticalOffset) {

                    isEnable = true;
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mSwipeRefreshLayout.setEnabled(true);
                } else if (isEnable && 0 != verticalOffset) {
                    isEnable = false;
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
//        mView.findViewById(R.id.fab_back_top).setOnClickListener(this);
        mConvenientBannerMainActivity = (ConvenientBanner<DataRow>) mView.findViewById(net.twoant.master.R.id.cb_convenientBanner);
        ViewGroup.LayoutParams layoutParams = mConvenientBannerMainActivity.getLayoutParams();
        layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() * 374F / 640F + .5F);
        mConvenientBannerMainActivity.setLayoutParams(layoutParams);
        mView.findViewById(net.twoant.master.R.id.ll_merchant_main_activity).setOnClickListener(this);
        mView.findViewById(net.twoant.master.R.id.ll_commodity_main_activity).setOnClickListener(this);
        mView.findViewById(net.twoant.master.R.id.ll_activity_main_activity).setOnClickListener(this);
        mView.findViewById(net.twoant.master.R.id.ll_entered_main_activity).setOnClickListener(this);
            fab_back_top=(ImageButton) getActivity().findViewById(net.twoant.master.R.id.fab_back_top);
        fab_back_top.setOnClickListener(this);
        if(fab_back_top!=null){
            Log.e("购物车","不是空");
        }
        mTabLayoutNew = (TabLayout) mView.findViewById(net.twoant.master.R.id.tab_layout_new);
        mVpContainer = (ViewPager) mView.findViewById(net.twoant.master.R.id.vp_container);
    }
    void create_order(){

        String url=ApiConstants.CREATE_ORDER;
        String head="";
        Set<String> keys= selectedList.keySet();
        for (String str : keys) {
            GoodsItem item= selectedList.get(str);
            head+="pro="+str+"&qty="+item.getGoodsCount()+"&";
        }
        head=head.substring(0,head.length()-1);
        url+=head;

        PostOrderActivity.startActivity(getActivity(),selectedList,"",head,url);
        Log.e("订单链接==",url);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.fab_back_top:
                if (selectedList==null || selectedList.size()==0){
                    ToastUtil.showLong("购物车里暂无商品");
                    return;
                }
                //MIN_ORDER_PRICE
                Set<String> set=selectedList.keySet();

                Iterator<String> it = set.iterator();
                if (it.hasNext()) {
                    String str = it.next();
                    GoodsItem item=selectedList.get(str);
                    item.setShop_name(Shop_nm);
                    item.setShop_id(SHOP_ID);
                }
                create_order();
                if (mActivityFragment != null) {
                    mActivityFragment.scrollToPosition(0, false);
                }
                break;
//            case R.id.fab_back_top:
//                if (mActivityFragment != null) {
//                    mActivityFragment.scrollToPosition(0, false);
//                }
//                break;
            case net.twoant.master.R.id.ll_merchant_main_activity:
                MerchantListActivity.startActivity(mActivity);
                break;
            case net.twoant.master.R.id.ll_commodity_main_activity:
                DiscoverActivity.startActivity(mActivity, null, null);
                break;
            case net.twoant.master.R.id.ll_activity_main_activity:
                ActionListActivity.startActivity(mActivity);
                break;
            case net.twoant.master.R.id.ll_entered_main_activity:
                MerchantEnteredActivity.startActivity(mContext, MerchantEnteredActivity.TYPE_MERCHANT_ENTRY, null);
                break;
        }
    }

}
