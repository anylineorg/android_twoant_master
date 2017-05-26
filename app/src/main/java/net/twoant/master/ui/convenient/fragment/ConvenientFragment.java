package net.twoant.master.ui.convenient.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.district.DistrictItem;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.HomeBaseFragment;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.convenient.activity.SearchConventionActivity;
import net.twoant.master.ui.convenient.adapter.CfViewPagerAdapter;
import net.twoant.master.ui.convenient.adapter.MyGridViewAdapter;
import net.twoant.master.ui.convenient.widget.ConvenientSearchHelper;
import net.twoant.master.ui.convenient.widget.ConvenientSearchToolbarSetter;
import net.twoant.master.ui.main.interfaces.IOnRefreshListener;
import net.twoant.master.ui.main.widget.MySwipeRefreshLayout;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSearchListener;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSwitchListener;
import net.twoant.master.ui.main.widget.search_toolbar.SearchToolbar;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/11/18.
 * 首页 便民信息fragment
 */

public class ConvenientFragment extends HomeBaseFragment implements HttpConnectedUtils.IOnStartNetworkCallBack, SwipeRefreshLayout.OnRefreshListener, ConvenientInfoListFragment.OnSwifeRefreshListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<ConvenientInfoListFragment> mFragments = new ArrayList<>();
    private PopupWindow mPopupWindow;
    private GridView mGridView;
    private View mInflate;
    private MyGridViewAdapter mMyGridViewAdapter;
    private CfViewPagerAdapter mViewPagerAdapter;
    //    private boolean isPopupWindowShowing = false;
    private HttpConnectedUtils mHttpConnectedUtils;
    private final static int ID_MENU = 0X11;
    private final static int ID_CLASSIFY = 0X12;
    private final static int ID_BANNER = 0X13;

    private List<DataRow> mData;
    private List<DataRow> mData1;
    private boolean mResult;
    private Map<String, String> mMap;
    private String mNm;
    private int mId1;
    private int tabPosition = -1;
    private FragmentManager mFragmentManager;
    private float mWidth;
    //    private boolean isFirstUpdata = true;
    private int mGridViewHeight;
    private float mStartX;
    private Bitmap mBitmap;
    private BitmapDrawable mBmd;
    private MySwipeRefreshLayout mySwipeRefreshLayout;

    private String type = null;
    private int listId = 0;
    private ConvenientInfoListFragment fragment;

    private RelativeLayout mRlAdposition;
    private ConvenientBanner convenientBanner;

    private Map map = new HashMap();
    private SearchToolbar mSearchToolbar;
    private IOnRefreshListener iOnRefreshListener;
    private ConvenientBanner<DataRow> mConvenientBannerMainActivity;
    private List<DataRow> mBannerListBean;
    private CBViewHolderCreator<BannerImageHolderView> mViewCBViewHolderCreator;
    private ArrayMap<String, String> mParameter;

    //TODO 界面就用这个，只需修改里面的布局内容
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.yh_fragment_convenient;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initFragmentComponentsData(View view) {
        initToolbarData(view);

        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        mWidth = wm.getDefaultDisplay().getWidth();
        mFragmentManager = getActivity().getSupportFragmentManager();

        mySwipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_layout);
        mySwipeRefreshLayout.setOnRefreshListener(this);
        mySwipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary);
        AppBarLayout appBar = (AppBarLayout) mView.findViewById(net.twoant.master.R.id.appbar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private boolean isEnable;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (!isEnable && 0 == verticalOffset) {

                    isEnable = true;
                    if (mySwipeRefreshLayout.isRefreshing()) {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                    mySwipeRefreshLayout.setEnabled(true);
                } else if (isEnable && 0 != verticalOffset) {
                    isEnable = false;
                    mySwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        //轮播图
        mConvenientBannerMainActivity = (ConvenientBanner<DataRow>) mView.findViewById(net.twoant.master.R.id.cb_convenientBanner);
        ViewGroup.LayoutParams layoutParams = mConvenientBannerMainActivity.getLayoutParams();
        layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() * 374F / 640F + .5F);
        mConvenientBannerMainActivity.setLayoutParams(layoutParams);
        initBanner();

        //真实数据
        mTabLayout = (TabLayout) view.findViewById(net.twoant.master.R.id.tab_layout_new);
        mViewPager = (ViewPager) view.findViewById(net.twoant.master.R.id.vp_content_convenient);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mInflate = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.xj_convenient_gridview, null, false);
        mGridView = (GridView) mInflate.findViewById(net.twoant.master.R.id.grd_convenient);

        initPopupWindow();

        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        startNetwork();
        mMap = new HashMap<>();
        mHttpConnectedUtils.startNetworkGetString(ID_MENU, mMap, ApiConstants.CONVENIENT_INFO_A_MENU);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            //未选择点击执行2
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG", "onTabSelected: " + tab.getPosition());

                mViewPager.setCurrentItem(tab.getPosition(), false);
//                isPopupWindowShowing = false;
                if (tabPosition != -1) {
                    if (mMap == null) {
                        mMap = new HashMap<>();
                    }
                    mMap.clear();
                    type = "base";
                    listId = mData.get(tab.getPosition()).getInt("ID");
                    mMap.put(type, listId + "");
                    mHttpConnectedUtils.startNetworkGetString(ID_CLASSIFY, mMap, ApiConstants.CONVENIENT_INFO_B_MENU);
                }
                tabPosition = tab.getPosition();
//                isFirstUpdata = true;
            }

            //未选择点击执行1
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            //选择后再次点击执行
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                if (isPopupWindowShowing) {
//                    mPopupWindow.dismiss();
//                    isPopupWindowShowing = false;
//                } else {
                if (mPopupWindow == null) {
                    initPopupWindow();
                }
//                    if (isFirstUpdata) {
//
//                        if (0 == tab.getPosition()) {
//                            mStartX = mWidth / 12;
//                        } else if (1 == tab.getPosition()) {
//                            mStartX = 3 * mWidth / 12;
//                        }else if (2==tab.getPosition()){
//                            mStartX = 5 * mWidth / 12;
//                        }
//                        else if ((mData.size() - 1) == tab.getPosition()) {
//                            mStartX = 11 * mWidth / 12;
//                        } else if ((mData.size() - 2) == tab.getPosition()) {
//                            mStartX = 9 * mWidth / 12;
//                        } else if ((mData.size() - 3) == tab.getPosition()){
//                            mStartX = 7 * mWidth / 12;
//                        }else {
//                            mStartX = mWidth /2;
//                        }

//                        mBitmap = makePopupBg(mStartX, 0, mWidth, mGridViewHeight);
//                        mBmd = new BitmapDrawable(mBitmap);
//                        mPopupWindow.setBackgroundDrawable(mBmd);
//                        isFirstUpdata = false;
//                    }

//                    mPopupWindow.showAsDropDown(mTabLayout, 0, 0);
                //二级分类不为空 show
                if (null != mData1 && 0 != mData1.size()) {
                    mPopupWindow.showAsDropDown(mTabLayout, 0, 0);
//                        isPopupWindowShowing = true;
                }
//                    backgroundAlpha(0.5f);
            }
//            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mPopupWindow.dismiss();
//                isPopupWindowShowing = false;
                fragment = mFragments.get(mViewPager.getCurrentItem());
                fragment.updataUI(mData1.get(position).getInt("ID") + "", "sort");
            }
        });

//        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
////                backgroundAlpha(1f);
////                isPopupWindowShowing = false;
//            }
//        });

    }

    /**
     * 配置toolbar
     */
    private void initToolbarData(View view) {
        mSearchToolbar = (SearchToolbar) view.findViewById(net.twoant.master.R.id.st_tool_bar);
        ConvenientSearchHelper searchHelperBase = new ConvenientSearchHelper();
        ConvenientSearchToolbarSetter iSearchToolbarSetter = new ConvenientSearchToolbarSetter(mActivity);
        mSearchToolbar.setSearchHelperBase(iSearchToolbarSetter, searchHelperBase);
        searchHelperBase.setOnSearchListener(new IOnSearchListener() {
            @Override
            public void onSearchListener(String type, String keyword) {
                SearchConventionActivity.startActivity(mActivity, type, keyword);
            }
        });
        iSearchToolbarSetter.setOnSwitchListener(new IOnSwitchListener() {
            @Override
            public void onSwitchListener(boolean isSuccessful, @Nullable ArrayList<DistrictItem> districtItems) {
                if (isSuccessful && null != districtItems) {
                    DistrictItem districtItem = districtItems.get(0);
                    if (null != mFragments && !mFragments.isEmpty() && null != districtItem) {
                        for (ConvenientInfoListFragment fragment : mFragments) {
                            fragment.switchCityCodeAndAddressCode(districtItem.getCitycode(), districtItem.getAdcode());
                        }
                        if (null != mHttpConnectedUtils) {
                            mHttpConnectedUtils.setCityAndAddressCode(districtItem.getCitycode(), districtItem.getAdcode());
                        }
                        startNetwork();
                        ToastUtil.showShort("切换区域成功");
                    }
                } else {
                    ToastUtil.showShort("切换区域失败");
                }
            }
        });
    }

    private boolean isImageChangeState = ImageLoader.canLoadingImg();

    @Override
    protected void onUserVisible() {
        try {
            if (isImageChangeState != ImageLoader.canLoadingImg()) {
                isImageChangeState = ImageLoader.canLoadingImg();
                onRefresh();
            }
        } catch (Exception e) {
            //Empty
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mSearchToolbar) {
            mSearchToolbar.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHttpConnectedUtils != null) {
            mHttpConnectedUtils.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mSearchToolbar) {
            mSearchToolbar.onDestroy();
        }
    }

    @Override
    protected void onUserInvisible() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void initPopupWindow() {
        mPopupWindow = new PopupWindow(mInflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(false);
//        mBitmap = makePopupBg(100, 0, mWidth, mGridViewHeight);
//        mBmd = new BitmapDrawable(mBitmap);

    }

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
            case ID_BANNER:

                /*HomePageBannerBean homePageBannerBean = new Gson().fromJson(response, HomePageBannerBean.class);
                List<HomePageBannerBean.ResultBean> resultBeen;
                if ((resultBeen = homePageBannerBean.getResult()) != null && resultBeen.size() != 0) {
                    initBannerData(resultBeen);
                }*/

                DataRow row = DataRow.parseJson(response);
                if (row != null && row.getSet("result") != null) {
                    initBannerData(row.getSet("result").getRows());
                }
                if (null != mySwipeRefreshLayout && mySwipeRefreshLayout.isRefreshing()) {
                    mySwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case ID_MENU:
                mResult = DataRow.parseJson(response).getBoolean("result", false);
                if (mResult) {
                    mData = DataRow.parseJson(response).getSet("data").getRows();
                    for (int i = 0; i < mData.size(); i++) {
                        mNm = mData.get(i).getString("NM");
                        mId1 = mData.get(i).getInt("ID");
                        mFragments.add(ConvenientInfoListFragment.newInstance(mId1 + "", "base"));
                        View inflate = LayoutInflater.from(mContext).inflate(net.twoant.master.R.layout.xj_item_convenient_tab, null, false);
                        TextView textView = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_item_tab_convenient);
                        textView.setText(mNm);
                        TabLayout.Tab tab = mTabLayout.newTab().setCustomView(inflate);
                        mTabLayout.addTab(tab);
                    }
                    mViewPagerAdapter = new CfViewPagerAdapter(mFragmentManager, mFragments, mData);
                    mViewPager.setAdapter(mViewPagerAdapter);

                    if (mMap == null) {
                        mMap = new HashMap<>();
                    } else {
                        mMap.clear();
                    }
                    type = "base";
                    listId = mData.get(0).getInt("ID");
                    mMap.put(type, listId + "");
                    mHttpConnectedUtils.startNetworkGetString(ID_CLASSIFY, mMap, ApiConstants.CONVENIENT_INFO_B_MENU);
                    mySwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case ID_CLASSIFY:
                mResult = DataRow.parseJson(response).getBoolean("result", false);
                if (mResult) {
                    mData1 = DataRow.parseJson(response).getSet("data").getRows();

                    if (mMyGridViewAdapter == null) {
                        mMyGridViewAdapter = new MyGridViewAdapter(mContext, mData1);
                        mGridView.setAdapter(mMyGridViewAdapter);
                    } else {
                        mMyGridViewAdapter.setDatas(mData1);
                    }
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    mGridView.measure(widthSpec, heightSpec);
                    mGridViewHeight = mGridView.getMeasuredHeight();
                    if (null != mData1 && 0 != mData1.size()) {
                        mPopupWindow.showAsDropDown(mTabLayout, 0, 0);
//                        isPopupWindowShowing = true;
                    }
                }
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private Bitmap makePopupBg(float startX, float startY, float totalWidth, float gridViewHeight) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        //只画边框STROKE  充满FILL
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
        //防锯齿
        paint.setAntiAlias(true);

        Bitmap bitmap = Bitmap.createBitmap((int) totalWidth, (int) (gridViewHeight + 56), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        paint.setColor(getResources().getColor(net.twoant.master.R.color.backgroundColor));
        canvas2.drawRect(0, 0, (int) totalWidth, (int) (gridViewHeight + 56), paint);
        paint.setColor(Color.BLACK);

        Bitmap iconBmp = Bitmap.createBitmap((int) totalWidth, (int) (gridViewHeight + 56), Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(iconBmp);
        Path path = new Path();
        path.moveTo(startX, startY);

        path.lineTo(startX + 12, startY + 16);
        path.lineTo(totalWidth - 22, startY + 16);
        path.arcTo(new RectF(totalWidth - 42, startY + 16, totalWidth - 2, startY + 56), -90, 90, false);
        path.lineTo(totalWidth - 2, startY + 36 + gridViewHeight);
        path.arcTo(new RectF(totalWidth - 42, startY + 16 + gridViewHeight, totalWidth - 2, startY + 56 + gridViewHeight), 0, 90, false);
        path.lineTo(22, startY + 56 + gridViewHeight);
        path.arcTo(new RectF(2, startY + 16 + gridViewHeight, 42, startY + 56 + gridViewHeight), 90, 90, false);
        path.lineTo(2, startY + 36);
        path.arcTo(new RectF(2, startY + 16, 42, startY + 56), -180, 90, false);
        path.lineTo(startX - 12, startY + 16);
        path.lineTo(startX, startY);
        path.close();
        canvas1.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas1.drawBitmap(bitmap, new Rect(0, 0, (int) totalWidth, (int) (gridViewHeight + 56)), new Rect(0, 0, (int) totalWidth, (int) (gridViewHeight + 56)), paint);
        canvas2.drawBitmap(iconBmp, new Rect(0, 0, (int) totalWidth, (int) (gridViewHeight + 56)), new Rect(0, 0, (int) totalWidth, (int) (gridViewHeight + 56)), null);
        return iconBmp;
    }

    @Override
    public void onRefresh() {
        if (null == mFragments || 0 == mFragments.size()) {
            if (null == mHttpConnectedUtils) {
                mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
            }
            if (null == mMap) {
                mMap = new HashMap<>();
            } else {
                mMap.clear();
            }
            mHttpConnectedUtils.startNetworkGetString(ID_MENU, mMap, ApiConstants.CONVENIENT_INFO_A_MENU);
        } else {
            fragment = (ConvenientInfoListFragment) mFragments.get(mViewPager.getCurrentItem());
            fragment.updataUI(listId + "", type);
            mySwipeRefreshLayout.setRefreshing(false);
            fragment.setOnSwifeRefreshListener(this);
        }
        if (null == mBannerListBean || 0 == mBannerListBean.size()) {
            startNetwork();
        }
    }

    @Override
    public void setRefresh(boolean onRefreshing) {
        mySwipeRefreshLayout.setRefreshing(onRefreshing);
    }

    private void longHttp(String url, Map<String, String> map, StringCallback callback) {
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
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

    private void initBanner() {
        mConvenientBannerMainActivity.setPointViewVisible(true)
                .setPageIndicator(new int[]{net.twoant.master.R.drawable.yh_banner_grey_dot, net.twoant.master.R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(4000);

//        mConvenientBannerMainActivity.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
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
//                                            , mActivity, temp, ad_activity_type);
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
//            }
//        });
    }

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
            ImageLoader.getImageFromNetwork(imageView, data == null ? "" : BaseConfig.getCorrectImageUrl(data.getStringDef("ad_picurl", "")), context, net.twoant.master.R.drawable.ic_def_large);
        }
    }

    private void startNetwork() {
        if (mHttpConnectedUtils == null) return;

        if (mParameter == null)
            mParameter = new ArrayMap<>();

        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
        String latLng;
        if (instance.getAiSouLocationBean().isUserSelectLocation()) {
            latLng = instance.getAiSouLocationBean().getSelectLongitude() + "," + instance.getAiSouLocationBean().getSelectLongitude();
        } else {
            latLng = instance.getAiSouLocationBean().getLongitude() + "," + instance.getAiSouLocationBean().getLatitude();
        }

        mParameter.clear();
        mParameter.put("ad_index", "0");
        mParameter.put("center", latLng);
        mHttpConnectedUtils.startNetworkGetString(ID_BANNER, mParameter, ApiConstants.HOME_PAGE_BANNER);
    }
}