package net.twoant.master.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.FragmentViewPagerAdapter;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.BitmapUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.interfaces.IOnRefreshListener;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.MySwipeRefreshLayout;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by kaiguokai on 2017/4/7.
 */

public class ShopDetailsFragment extends ViewPagerBaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, HttpConnectedUtils.IOnStartNetworkCallBack {

    private final static int ID_BANNER = 0;
    private final static int ID_CHECK = 1;
//    public static HashMap<String,GoodsItem> selectedList;//bottomsheet选中集合
//    public static float payPrice;
//    public static float totalPrice;//selectedList的总价
//    public static int totalCount;//selectedList的总数量

 //   private SearchToolbarHelper mSearchToolbarHelper;
    private TabLayout mTabLayoutNew;
    private ViewPager mVpContainer;
    private IOnRefreshListener iOnRefreshListener;
    private ActivityFragment[] mFragments;
    private ActivityFragment mActivityFragment;
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
    private boolean isEnable;


    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.fragment_shop;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView();

        initTabData();
//        mSearchToolbarHelper = new SearchToolbarHelper(mView, getActivity());
//        mSearchToolbarHelper.setOnclickListener(this);
//        mSearchToolbarHelper.setOnSwitchListener(this);
//        mSearchToolbarHelper.setOnSearchListener(new SearchToolbarHelper.IOnSearchListener() {
//            @Override
//            public void onSearchListener(String type, String keyword) {
//                SearchActivity.startActivity(getActivity(), type, keyword);
//            }
//        });
        mConnectedUtils = HttpConnectedUtils.getInstance(this);

    }
    private void longHttp(String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getToken());
        OkHttpUtils.post().url(ApiConstants.USER_INFO).params(map).build().execute(callback);
    }
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
                address = row.getString("RECEIVE_ADDRESS");
                AiSouAppInfoModel.SCHOOL_CD=row.getString("SCHOOL_ID");
                AiSouAppInfoModel.BUILD_CD=row.getString("BUILD_ID");
                MIN_ORDER_PRICE=row.getFloat("MIN_ORDER_PRICE");
                AiSouAppInfoModel.IS_VIP = row.getInt("IS_VIP");

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        requestNetForInformation();//更新会员状态
        //重新刷新数据
    //    if (RechargeSuccessActivtity.pay_result) {
            if (iOnRefreshListener != null) {
                iOnRefreshListener.onRefreshListener();
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        //    RechargeSuccessActivtity.pay_result=false;
    //    }


    }



    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCheckSuccessful = savedInstanceState.getBoolean("isCheckSuccessful");
        isEnable = savedInstanceState.getBoolean("isEnable");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void onUserVisible() {
        if (!isCheckSuccessful) {
            if (mParameter == null) {
                mParameter = new ArrayMap<>();
            } else {
                mParameter.clear();
            }

            mParameter.put("type", "0");

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        if (mConnectedUtils != null) {
            mConnectedUtils.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("isCheckSuccessful", isCheckSuccessful);
        outState.putBoolean("isEnable", isEnable);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected boolean isShowHintInfo() {
        return true;
    }

    @Override
    protected void onUserInvisible() {
    }

    private void initTabData() {
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getChildFragmentManager(), null);

        if (mFragments == null)
            mFragments = new ActivityFragment[]{ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER
                    , IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW
                    , IRecyclerViewConstant.CATEGORY_MERCHANT_GOODS_LIST_HOME, null, false)};

        adapter.setFragmentList(mFragments);

        mVpContainer.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                iOnRefreshListener = mActivityFragment = mFragments[position];
            }
        });
        mVpContainer.setAdapter(adapter);

        mVpContainer.setOffscreenPageLimit(1);
        iOnRefreshListener = mActivityFragment = mFragments[0];
        mTabLayoutNew.setupWithViewPager(mVpContainer);
        mTabLayoutNew.removeAllTabs();
        mTabLayoutNew.addTab(addTab(net.twoant.master.R.drawable.ic_home_activity, "附近店铺", true));
//        mTabLayoutNew.addTab(addTab(R.drawable.ic_home_good, "百货店", false));
//        mTabLayoutNew.addTab(addTab(R.drawable.ic_home_merchant, "会员卡", false));

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

//    @Override
//    public void onSwitchListener(boolean isSuccessful, ArrayList<DistrictItem> districtItems) {
//        if (isSuccessful) {
//            if (iOnRefreshListener != null) {
//                iOnRefreshListener.onRefreshListener();
//            }
//
//            ToastUtil.showShort("区域切换成功");
//        } else {
//            ToastUtil.showShort("区域切换失败");
//            if (mSearchToolbarHelper != null) {
//                mSearchToolbarHelper.setLocationInfo("区域");
//            }
//        }
//    }



    @Override
    public void onBefore(Request request, int id) {

    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {


            case ID_CHECK:
                DataRow dataRow = DataRow.parseJson(response);
                if (dataRow != null && dataRow.getBoolean("result", false)) {
                    if (dataRow.getInt("data") == 0) {
                        MainActivity.removeAccount(getActivity());
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
        if (iOnRefreshListener != null) {
            iOnRefreshListener.onRefreshListener();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
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

    @SuppressWarnings("unchecked")
    private void initView() {
        mSwipeRefreshLayout = (MySwipeRefreshLayout) mView.findViewById(net.twoant.master.R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        AppBarLayout viewById = (AppBarLayout) mView.findViewById(net.twoant.master.R.id.appbar);
        viewById.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isEnable;
            private int mTotalScrollRange;
            private boolean isExpanded;
            private int mLastOffset;
            private int mTouchSlop = ViewConfiguration.get(ShopDetailsFragment.this.mContext).getScaledTouchSlop();

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



        fab_back_top=(ImageButton) getActivity().findViewById(net.twoant.master.R.id.fab_back_top);

        if (fab_back_top!=null){
            Log.e("购物车","不是空");
            fab_back_top.setOnClickListener(this);
        }
        mTabLayoutNew = (TabLayout) mView.findViewById(net.twoant.master.R.id.tab_layout_new);
        mVpContainer = (ViewPager) mView.findViewById(net.twoant.master.R.id.vp_container);
    }
    void create_order(){

        String url=ApiConstants.CREATE_ORDER;
        String head="";
        Set<String> keys= HomeFragment.selectedList.keySet();
        for (String str : keys) {
            GoodsItem item= HomeFragment.selectedList.get(str);
            head+="pro="+str+"&qty="+item.getGoodsCount()+"&";
        }
        head=head.substring(0,head.length()-1);
        url+=head;

        PostOrderActivity.startActivity(getActivity(),HomeFragment.selectedList,"",head,url);
        Log.e("订单链接==",url);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.fab_back_top:
                if (HomeFragment.selectedList==null || HomeFragment.selectedList.size()==0){
                    ToastUtil.showLong("购物车里暂无商品");
                    return;
                }
                //MIN_ORDER_PRICE
                create_order();
                if (mActivityFragment != null) {
                    mActivityFragment.scrollToPosition(0, false);
                }
                break;

            default:
//                if (mSearchToolbarHelper != null) {
//                    mSearchToolbarHelper.onClick(v);
//                }
        }
    }

}


