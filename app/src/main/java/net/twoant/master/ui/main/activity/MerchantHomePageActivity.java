package net.twoant.master.ui.main.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ContentFrameLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.RoutePara;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.activity.MerchantChatActivity;
import net.twoant.master.ui.main.adapter.SectionsPagerAdapter;
import net.twoant.master.ui.main.adapter.holder.BannerViewHolder;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.bean.MerchantDataBean;
import net.twoant.master.ui.main.bean.PayOrderBean;
import net.twoant.master.ui.main.fragment.MerchantHomePageGoodsFragment;
import net.twoant.master.ui.main.widget.UMShareHelper;
import net.twoant.master.ui.main.widget.shopping_cart.BuyCarView;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.ScrollHeaderLinearLayout;
import net.twoant.master.widget.entry.DataRow;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2016/12/8.
 * 商家详情首页
 */
public class MerchantHomePageActivity extends BaseActivity implements BuyCarView.IOnBuyCartListener, BuyCarView.ICartDataChangeListener, HttpConnectedUtils.IOnStartNetworkSimpleCallBack, View.OnClickListener {
    private AppCompatTextView yunfei,qisong;
    private final static String ACTION_START = "MHPA_a_s";
    private final static String EXTRA_SHOP_ID = "MHPA_e_s_i";
    /**
     * 详情
     */
    private final static int ID_DETAIL = 1;

    private final static int ID_MERCHANT_DETAIL = 0;
    /**
     * 收藏
     */
    private final static int ID_COLLECTION = 2;
    /**
     * 获取轮播图
     */
    private final static int ID_BANNER = 3;

    /**
     * 查询是否收藏
     */
    private final static int ID_INQUIRE_COLLECTION = 4;

    private HttpConnectedUtils mHttpConnectedUtils;
    private AppCompatTextView mTvAddressHeader;
  //  private ConvenientBanner<String> mConvenientBannerMerchantHomePage;
    private AppCompatTextView mTvShopNameMerchantHomePage;
    private AppCompatTextView mTvShopTelephoneMerchantHomePage;
    private AppCompatTextView mTvShopAddressMerchantHomePage;
    private AppCompatTextView mTvRecommendMerchantHomePage;
    private AppCompatImageView mIvShowMoreDescription;
    private ScrollHeaderLinearLayout mScrollableLayoutMerchantHomePage;
    private View mToolbarBgMerchantHomePage;
    private AppCompatImageView mIvBackMerchantHomePage;
    private AppCompatButton mBtnPay;
    private ArrayMap<String, String> mArrayMap;
    private String mShopId;
    private boolean isCollection;
    private AppCompatImageButton mCollectionMerchant;
    private UMShareHelper mUMShareHelper;
    /**
     * 是否点击过
     */
    private boolean mClicked;
    private View mShowMoreIntroduce;
    private ContentFrameLayout mToolbar;
    private Fragment[] mFragments;
    private TextureMapView mGdTextureMapView;
    private AppCompatTextView mToolbarText;

    private String mLatitude;
    private String mLongitude;
    private AlertDialog mAlertDialog;
    private HintDialogUtil mHintDialogUtil;
    private BannerViewHolder.ViewHolderCreator mViewHolderCreator;
    private String mUid;

    /**
     * 轮播图数据
     */
    private List<String> mBannerData;

    /**
     * 购物车 视图
     */
    private ViewStub mBuyCard;
    private BuyCarView mBuyCarParent;
    private int mAddressHeight;
    private float mBannerHeight;
    private float mToolbarHeight;
    private int mScreenWidth;
    private AppCompatTextView mDistanceText;
    private AppCompatTextView mClickCountText;
    private DataRow mMerchantDetail;
    //    private MerchantGoodsCarAdapter mMerchantGoodsCarAdapter;
//    private BottomSheetBehavior<RecyclerView> mBottomSheetBehavior;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGdTextureMapView.onSaveInstanceState(outState);
        outState.putString("SI", mShopId);
        outState.putBoolean("iC", isCollection);
        outState.putBoolean("mC", mClicked);
        outState.putString("mL", mLatitude);
        outState.putString("mLo", mLongitude);
        outState.putInt("mAH", mAddressHeight);
        outState.putInt("mSW", mScreenWidth);
        outState.putFloat("mTH", mToolbarHeight);
        outState.putFloat("mBH", mBannerHeight);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mShopId = savedInstanceState.getString("SI");
        isCollection = savedInstanceState.getBoolean("iC");
        mClicked = savedInstanceState.getBoolean("mC");
        mLongitude = savedInstanceState.getString("mLo");
        mLatitude = savedInstanceState.getString("mL");
        mAddressHeight = savedInstanceState.getInt("mAH");
        mScreenWidth = savedInstanceState.getInt("mSW");
        mToolbarHeight = savedInstanceState.getFloat("mTH");
        mBannerHeight = savedInstanceState.getFloat("mBH");
    }

    public static void startActivity(Activity activity, String shopId) {
        Intent intent = new Intent(activity, MerchantHomePageActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_SHOP_ID, shopId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }

    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_merchant_home_page;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent);
        startNetworkForBanner();
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        getIntentData(getIntent());
        initView();
        mGdTextureMapView.onCreate(savedInstanceState);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mToolbarHeight = mToolbar.getHeight();
                startNetworkForBanner();
            }
        });
    }

    private void getIntentData(Intent intent) {
        BaseConfig.checkState(intent, ACTION_START);
        mShopId = intent.getStringExtra(EXTRA_SHOP_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUMShareHelper != null)
            mUMShareHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUMShareHelper != null)
            mUMShareHelper.onConfigurationChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String address = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCompletionAddress();
        mTvAddressHeader.setText(address == null ? "获取位置失败" : address);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGdTextureMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGdTextureMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHttpConnectedUtils) {
            mHttpConnectedUtils.onDestroy();
            mHttpConnectedUtils = null;
        }
        if (null != mGdTextureMapView) {
            mGdTextureMapView.onDestroy();
        }
        if (null != mHintDialogUtil) {
            mHintDialogUtil.dismissDialog();
            mHintDialogUtil = null;
        }
        mArrayMap = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mGdTextureMapView) {
            mGdTextureMapView.onLowMemory();
        }
    }

    /**
     * 请求 轮播图数据
     */
    private void startNetworkForBanner() {
        if (mHttpConnectedUtils == null)
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);

        if (mHintDialogUtil == null) {
            mHintDialogUtil = new HintDialogUtil(this);
            mHintDialogUtil.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MerchantHomePageActivity.this.finish();
                }
            });
        }

        mUid = AiSouAppInfoModel.getInstance().getUID();
        mHintDialogUtil.showLoading(net.twoant.master.R.string.dialog_loading);
        mArrayMap = new ArrayMap<>(2);

        /*mArrayMap.put("shop_id", mShopId);
        mArrayMap.put("aisou_id", mUid);
        mHttpConnectedUtils.startNetworkGetString(ID_BANNER, mArrayMap, ApiConstants.MERCHANT_PAGER_DETAIL);*/

        //获取商家详情
        mArrayMap.put("id", mShopId);
        mArrayMap.put("_t", mUid);
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        mArrayMap.put("center", String.valueOf(instance.getLongitude() + "," + instance.getLongitude()));
        mHttpConnectedUtils.startNetworkGetString(ID_MERCHANT_DETAIL, mArrayMap, ApiConstants.GOODS_LIST_4SHOP);

        //获取是否收藏该商家
//        mArrayMap.clear();
//        mArrayMap.put("shop", mShopId);
//        mArrayMap.put("user", mUid);
//        mHttpConnectedUtils.startNetworkGetString(ID_INQUIRE_COLLECTION, mArrayMap, ApiConstants.INQUIRE_COLLECTION);

    }

   /* private void startNetworkForInfo() {
        mArrayMap.clear();
        //获取商家详情
        mArrayMap = new ArrayMap<>();
        mArrayMap.put("shop_id", mShopId);
        mArrayMap.put("aisou_id", mUid);
        mHttpConnectedUtils.startNetworkGetString(ID_DETAIL, mArrayMap, ApiConstants.MERCHANT_PAGER_DETAIL);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_shop_telephone_merchant_home_page:
                new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle).setCancelable(true)
                        .setMessage("是否拨打电话:" + mTvShopTelephoneMerchantHomePage.getText() + "?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent call = new Intent();
                            call.setAction(Intent.ACTION_CALL);
                            call.setData(Uri.parse("tel:" + mTvShopTelephoneMerchantHomePage.getText().toString()));
                            startActivity(call);
                        } catch (Exception e) {
                            ToastUtil.showShort("跳转失败");
                        } finally {
                            dialog.dismiss();
                        }
                    }
                }).create().show();
                break;
            //客服
            case net.twoant.master.R.id.btn_shop_server_merchant_home_page:
                v.setClickable(false);
                MerchantChatActivity.startActivity(MerchantHomePageActivity.this, mShopId);
                v.setClickable(true);
                break;
            case net.twoant.master.R.id.btn_share_merchant_home_page:
                if (mUMShareHelper == null)
                    mUMShareHelper = new UMShareHelper(this);
                mUMShareHelper.showDialogAtBottom("分享商家", true);
                break;
            case net.twoant.master.R.id.btn_collection_merchant_home_page:
                mArrayMap.clear();
                mArrayMap.put("user", mUid);
                mArrayMap.put("shop", mShopId);
                mHttpConnectedUtils.startNetworkGetString(ID_COLLECTION, mArrayMap, ApiConstants.COLLECTION);
                break;
            //显示更多介绍
            case net.twoant.master.R.id.fl_show_more_description_parent:
                startAnimationForShowMoreIntroduce(mIvShowMoreDescription);
                break;
            case net.twoant.master.R.id.iv_back_merchant_home_page:
                this.finish();
                break;
            //店内支付
            case net.twoant.master.R.id.btn_pay:
                PayOrderActivity.startActivity(new PayOrderBean(PayOrderBean.TYPE_INNER, null, mShopId,
                        mTvShopNameMerchantHomePage.getText().toString(), null, null, null), MerchantHomePageActivity.this);
                break;
            //导航
            case net.twoant.master.R.id.merchant_navigation:
                startNavigation();
                break;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (mToolbarHeight == 0)
                mToolbarHeight = mToolbar.getHeight();
        }
    }

    @Override
    public void onPaymentClickListener(@NonNull View view, ArrayList<GoodsItemBean> goodsItemBeen) {
        if (null != goodsItemBeen && !goodsItemBeen.isEmpty()) {
//            if (null != mMerchantDetail) {
//                mBuyCarParent.restoreData();
//                GoodsPayOrderListActivity.startActivity(MerchantHomePageActivity.this, goodsItemBeen,
//                        new PayOrderBean(PayOrderBean.TYPE_GOODS, null, mShopId,
//                                mMerchantDetail.getString("SHOP_NAME"),
//                                mMerchantDetail.getString("SHOP_AVATAR"), null, null));
//               /* Fragment goodsFragment = mFragments[mFragments.length - 1];
//                if (goodsFragment instanceof MerchantHomePageGoodsFragment) {
//                    ((MerchantHomePageGoodsFragment) goodsFragment).cleanSelect();
//                }*/
//            } else {
//                ToastUtil.showShort(R.string.merchant_get_detail_fail);
//            }
                    PostOrderActivity.startActivity(this, goodsItemBeen, mTvShopNameMerchantHomePage.getText().toString(), mShopId,0);
                    finish();

        } else {
            ToastUtil.showShort(net.twoant.master.R.string.merchant_empty_goods);
        }
    }

    @NonNull
    public BuyCarView getBuyCartView() {
        initBuyCartView();
        return mBuyCarParent;
    }

    @NonNull
    @Override
    public List<GoodsItemBean> getDataList() {
        Fragment goodsFragment = mFragments[mFragments.length - 1];
        if (goodsFragment instanceof MerchantHomePageGoodsFragment) {
            return ((MerchantHomePageGoodsFragment) goodsFragment).getGoodsList();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onDataChange(@NonNull List<GoodsItemBean> cartList, @NonNull GoodsItemBean current) {
        Fragment goodsFragment = mFragments[mFragments.length - 1];
        if (goodsFragment instanceof MerchantHomePageGoodsFragment) {
            ArrayMap<String, GoodsItemBean> arrayMap = new ArrayMap<>(cartList.size());
            for (GoodsItemBean bean : cartList) {
                if (null == bean) {
                    continue;
                }
                arrayMap.put(bean.getGoodsId(), bean);
            }
            ((MerchantHomePageGoodsFragment) goodsFragment).setGoodsList(arrayMap);
        }
    }

    @Override
    public void onCurrentGoodsCountChange(int count) {

    }

    /**
     * 启动导航
     */
    private void startNavigation() {
        if (mLatitude == null || mLongitude == null) {
            ToastUtil.showShort("获取商家位置失败");
            return;
        }

        if (mAlertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle);
            builder.setTitle("请选择导航方式");
            builder.setSingleChoiceItems(new String[]{"驾车", "公交", "步行"}, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Context applicationContext = getApplicationContext();
                    Double latitude = Double.valueOf(mLatitude);
                    Double longitude = Double.valueOf(mLongitude);
                    AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
                    try {

                        RoutePara routePara = new RoutePara();
                        routePara.setStartName(instance.getSimpleAddress());
                        routePara.setStartPoint(new LatLng(instance.getLatitude(), instance.getLongitude()));
                        routePara.setEndPoint(new LatLng(latitude, longitude));
                        routePara.setEndName(mTvShopNameMerchantHomePage.getText().toString());
                        switch (which) {
                            case 0:
                                AMapUtils.openAMapDrivingRoute(routePara, applicationContext);
                                break;
                            case 1:
                                AMapUtils.openAMapTransitRoute(routePara, applicationContext);
                                break;
                            case 2:
                                AMapUtils.openAMapWalkingRoute(routePara, applicationContext);
                                break;
                        }
                    } catch (Exception e) {
                        try {
                            SDKInitializer.initialize(applicationContext);
                            RouteParaOption routeParaOption = new RouteParaOption();
                            CoordinateConverter coordinateConverter = new CoordinateConverter();

                            coordinateConverter.coord(new com.baidu.mapapi.model.LatLng(instance.getLatitude(), instance.getLongitude()));
                            coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
                            routeParaOption.startPoint(coordinateConverter.convert());
                            routeParaOption.startName(instance.getSimpleAddress());

                            coordinateConverter.coord(new com.baidu.mapapi.model.LatLng(latitude, longitude));
                            coordinateConverter.from(CoordinateConverter.CoordType.COMMON);
                            routeParaOption.endPoint(coordinateConverter.convert());
                            routeParaOption.endName(mTvShopNameMerchantHomePage.getText().toString());

                            switch (which) {
                                case 0:
                                    BaiduMapRoutePlan.openBaiduMapDrivingRoute(routeParaOption, applicationContext);
                                    break;
                                case 1:
                                    BaiduMapRoutePlan.openBaiduMapTransitRoute(routeParaOption, applicationContext);
                                    break;
                                case 2:
                                    BaiduMapRoutePlan.openBaiduMapWalkingRoute(routeParaOption, applicationContext);
                                    break;
                            }
                        } catch (Exception | Error e1) {
                            ToastUtil.showShort("没有安装高德/百度地图客户端");
                        }
                    } finally {
                        dialog.dismiss();
                    }
                }
            });
            mAlertDialog = builder.create();
        }
        mAlertDialog.show();
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            /*

            {"result":true,"code":"200",

            "data":{"AREA_ID":null,"REG_TIME":"2017-02-25 16:26:14","INVITE_USER_REALNAME":"张柄政",
            "SHOP_KEYWORD3":null,"SHOP_KEYWORD2":null,"SHOP_LON":"119.983947","SHOP_NAME":"稻香鲜米行","SHOP_KEYWORD":null,
            "USER_ID":468,"IMG1":"/ig?id=2193","IMG2":"/ig?id=2200","SHOP_SHOP_SHOPPIC":"/ig?id=2198","SHOP_LAT":"35.87172",
            "IMG3":"/ig?id=2196","SHOP_LONGITUDE":"119.983947","IMG4":null,"SHOP_TEL":"18661989928","SHOP_OWNER_NAME":"陈彦学",
            "TOWN_CODE":"370211","UID_PK":170225009,"ID":327,"SHOP_COMMENT_COUNT":0,"SHOP_OWNER_PERSION_CARD_F":"/ig?id=2197",
            "UPT_TIME":"2017-03-30 14:34:30","MAP_ID":5795,"SHOP_ADDRESS":"山东省青岛市黄岛区珠海街道珠山路48号市委家属院(珠山路)",
            "CITY_CODE":"0532","SHOP_INTRODUCE":"主要从事黑龙江原产地水稻现场加工大米，各大品牌面粉粮油销售","INVITE_USER_ID":196,
            "TIME":"2017-02-25 16:26:14","IS_REVIEWED":2,"SHOP_TRADING_COUNT":0,"IMGS":{"IMG3":"/ig?id=2196","IMG4":null,
            "SHOP_ID":327,"ID":281,"IMG1":"/ig?id=2193","IMG2":"/ig?id=2200"},"USER_NM":"18661989928","SELL_QTY_SUM":11,
            "SHOP_LATITUDE":"35.87172","CLICK":69,"AREA_NM":null,"SHOP_AVATAR":"/ig?id=2199","BANK_NAME":"农行","NM":"稻香鲜米行"},

            "success":true,"type":"map","message":null}

             */
            case ID_MERCHANT_DETAIL:
//                mMerchantDetail = DataRow.parseJson(response);
//                if (mMerchantDetail != null && mMerchantDetail.getBoolean("result", false) && (mMerchantDetail = mMerchantDetail.getRow("data")) != null) {
//
//                    setDetailData(mMerchantDetail);
//                    setBannerData(mMerchantDetail.getRow("IMGS"));
//                } else {
//                    if (null != mHintDialogUtil) {
//                        mHintDialogUtil.showError("获取数据失败", true, false);
//                    }
//                    return;
//                }
                DataRow detail = DataRow.parseJson(response);
                if (detail != null && detail.getBoolean("result", false) && (detail = detail.getRow("data")) != null) {

                    setDetailData(detail);
                    //  setBannerData(detail.getRow("IMGS"));
                } else {
                    mHintDialogUtil.showError("获取数据失败", true, false);
                    return;
                }
                break;
            case ID_DETAIL:
                MerchantDataBean merchantDataBean = new Gson().fromJson(response, MerchantDataBean.class);
                List<MerchantDataBean.ResultBean> dataBeen;
                if (merchantDataBean != null && (dataBeen = merchantDataBean.getResult()) != null && dataBeen.size() != 0) {
                    setData(dataBeen.get(0));
                } else {
                    if (null != mHintDialogUtil) {
                        mHintDialogUtil.showError("获取数据失败", true, false);
                    }
                    return;
                }
                break;
            case ID_BANNER:
               /* startNetworkForInfo();
                MerchantHomePageBannerBean innerBanner = new Gson().fromJson(response, MerchantHomePageBannerBean.class);
                setBannerData(innerBanner.getResult());*/
                break;
            case ID_COLLECTION:
                DataRow parse = DataRow.parseJson(response);
                if (parse == null) return;
                if (!parse.getBoolean("RESULT", false)) {
                    ToastUtil.showShort(parse.getString("MESSAGE"));
                    return;
                }

                isCollection = parse.getBoolean("DATA", false);
                mCollectionMerchant.setImageResource(isCollection ? net.twoant.master.R.drawable.ic_collection_check : net.twoant.master.R.drawable.ic_collection_uncheck);
                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏");
                break;

            case ID_INQUIRE_COLLECTION:
                DataRow inquire = DataRow.parseJson(response);
                if (inquire == null) return;
                if (!inquire.getBoolean("RESULT", false)) {
                    ToastUtil.showShort(inquire.getString("MESSAGE"));
                    return;
                }
                isCollection = inquire.getBoolean("DATA", false);
                mCollectionMerchant.setImageResource(isCollection ? net.twoant.master.R.drawable.ic_collection_check : net.twoant.master.R.drawable.ic_collection_uncheck);
                break;
        }
        if (null != mHintDialogUtil) {
            mHintDialogUtil.dismissDialog();
        }
    }

    /**
     * 设置详情数据
     */
    private void setDetailData(DataRow detail) {
        String shop_name = detail.getString("NM");
        mTvShopNameMerchantHomePage.setText(shop_name);
        mClickCountText.setText("销量:"+detail.getString("SELL_QTY"));
        mDistanceText.setText(detail.getStringDef("_DISTANCE_TXT", ""));
        mTvShopTelephoneMerchantHomePage.setText(detail.getStringDef("MEMBER_MOBILE", "未设置"));
        mTvShopAddressMerchantHomePage.setText(String.valueOf("地址:" + detail.getString("SCHOOL_NM")+ detail.getString("BUILD_NM")));
        mTvRecommendMerchantHomePage.setText(detail.getString("DESCRIPTION"));
        // qisong.setText("满送:满"+detail.getString("FREE_POST_ORDER_PRICE")+"元起送");
        // yunfei.setText("运费:"+detail.getString("POST_PRICE")+"元");

        qisong.setText("起送:满"+detail.getString("MIN_ORDER_PRICE")+"元起送");
        yunfei.setText("运费:"+detail.getString("POST_PRICE")+"元(满"+detail.getString("FREE_POST_ORDER_PRICE")+"元免运费)");
        if(detail.getInt("POST_PRICE") ==0){
            yunfei.setVisibility(View.GONE);
        }
        if (mTvRecommendMerchantHomePage.getLineCount() < 3) {
            if (mShowMoreIntroduce.getVisibility() != View.GONE)
                mShowMoreIntroduce.setVisibility(View.GONE);
        } else {
            if (mShowMoreIntroduce.getVisibility() != View.VISIBLE)
                mShowMoreIntroduce.setVisibility(View.VISIBLE);
        }
        int staue=-1;
        staue=detail.getInt("BUILD_POSITION_FLAG");
        if (staue==1) {
            mLatitude = detail.getStringDef("_DISTANCE", "");
            mLongitude = detail.getStringDef("LON", "");
            initGdMapData(shop_name, mLatitude, mLongitude);
        }else{
            findViewById(net.twoant.master.R.id.tmv_gd_map).setVisibility(View.GONE);
        }
    }

    /**
     * 初始化 banner数据
     */
    private void setBannerData(DataRow set) {
        if (set == null) {
            return;
        }

        String temp;
        ArrayList<String> strings = new ArrayList<>(4);
        if ((temp = set.getString("IMG1")) != null && temp.length() > 0 && !"null".equals(temp))
            strings.add(temp);
        if ((temp = set.getString("IMG2")) != null && temp.length() > 0 && !"null".equals(temp))
            strings.add(temp);
        if ((temp = set.getString("IMG3")) != null && temp.length() > 0 && !"null".equals(temp))
            strings.add(temp);
        if ((temp = set.getString("IMG4")) != null && temp.length() > 0 && !"null".equals(temp))
            strings.add(temp);

        if (mViewHolderCreator == null)
            mViewHolderCreator = new BannerViewHolder.ViewHolderCreator();

//        mConvenientBannerMerchantHomePage.setPages(mViewHolderCreator, mBannerData = strings)
//                .setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(int position) {
//                        displayImage(position);
//                    }
//                });
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        switch (id) {
            case ID_COLLECTION:
                ToastUtil.showShort(isCollection ? "取消失败" : "收藏失败");
                break;
            case ID_INQUIRE_COLLECTION:
                ToastUtil.showShort(NetworkUtils.getNetworkStateDescription(call, e, "查询收藏状态失败"));
                break;
            default:
                if (null != mHintDialogUtil) {
                    mHintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call, e,
                            getString(net.twoant.master.R.string.dialog_loading_fail)));
                }
                break;
        }
    }

    private void startAnimationForShowMoreIntroduce(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator;
        ValueAnimator valueAnimator;
        if (!mClicked) {
            valueAnimator = ValueAnimator.ofInt(3, mTvRecommendMerchantHomePage.getLineCount());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int mLastValue;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    if (animatedValue != mLastValue)
                        mTvRecommendMerchantHomePage.setMaxLines(animatedValue);
                    mLastValue = animatedValue;
                }
            });
            objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 0, 180);
        } else {
            valueAnimator = ValueAnimator.ofInt(mTvRecommendMerchantHomePage.getLineCount(), 3);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int mLastValue;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    if (animatedValue != mLastValue)
                        mTvRecommendMerchantHomePage.setMaxLines(animatedValue);
                    mLastValue = animatedValue;
                }
            });
            objectAnimator = ObjectAnimator.ofFloat(view, "rotation", 180, 0);
        }
        animatorSet.setDuration(200).playTogether(objectAnimator, valueAnimator);
        animatorSet.start();
        mClicked = !mClicked;
    }

    /**
     * 展示照片
     */
    private void displayImage(int position) {
        ArrayList<String> images = new ArrayList<>();
        if (mBannerData != null) {
            for (String str : mBannerData)
                images.add(ApiConstants.CURRENT_SELECT + str);
        } else {
            return;
        }
        Intent intent = new Intent(this, ImageScaleActivity.class);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, images);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
        this.overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
    }

    /**
     * @param dataBean 请求的数据
     */
    private void setData(MerchantDataBean.ResultBean dataBean) {
        if (dataBean == null) return;
        isCollection = dataBean.getCollection() == 1;

        if (isCollection)
            mCollectionMerchant.setImageResource(net.twoant.master.R.drawable.ic_collection_check);
        else
            mCollectionMerchant.setImageResource(net.twoant.master.R.drawable.ic_collection_uncheck);

        mTvShopNameMerchantHomePage.setText(dataBean.getShop_name());

        String tel = dataBean.getShop_tel();
        tel = tel == null ? "未设置" : tel;
        mTvShopTelephoneMerchantHomePage.setText(tel);

        mTvShopAddressMerchantHomePage.setText(String.valueOf("地址:" + dataBean.getShop_address()));
        mTvRecommendMerchantHomePage.setText(dataBean.getShop_introduce());

        if (mTvRecommendMerchantHomePage.getLineCount() < 3) {
            if (mShowMoreIntroduce.getVisibility() != View.GONE)
                mShowMoreIntroduce.setVisibility(View.GONE);
        } else {
            if (mShowMoreIntroduce.getVisibility() != View.VISIBLE)
                mShowMoreIntroduce.setVisibility(View.VISIBLE);
        }
        mLatitude = dataBean.getShop_latitude();
        mLongitude = dataBean.getShop_longitude();
        initGdMapData(dataBean.getShop_name(), mLatitude, mLongitude);
    }

    /**
     * 初始化view
     */
    @SuppressWarnings("unchecked")
    private void initView() {
        yunfei=(AppCompatTextView) findViewById(net.twoant.master.R.id.yunfei);
        qisong=(AppCompatTextView) findViewById(net.twoant.master.R.id.qisong);
        mClickCountText = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_click_merchant_home_page);
        mDistanceText = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_distance_merchant_home_page);
        mTvAddressHeader = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_address_header);

       // mConvenientBannerMerchantHomePage = (ConvenientBanner) findViewById(R.id.cb_convenientBanner);

        mTvShopNameMerchantHomePage = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_name_merchant_home_page);

        mTvShopTelephoneMerchantHomePage = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_telephone_merchant_home_page);
        mTvShopTelephoneMerchantHomePage.setOnClickListener(this);
        mTvShopAddressMerchantHomePage = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_address_merchant_home_page);

        mTvRecommendMerchantHomePage = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_recommend_merchant_home_page);

        mIvShowMoreDescription = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_show_more_description);

        mGdTextureMapView = (TextureMapView) findViewById(net.twoant.master.R.id.tmv_gd_map);

        TabLayout mTabs = (TabLayout) findViewById(net.twoant.master.R.id.tabs);
        ViewPager mVpContainerViewPager = (ViewPager) findViewById(net.twoant.master.R.id.vp_container_view_pager);

        mScrollableLayoutMerchantHomePage = (ScrollHeaderLinearLayout) findViewById(net.twoant.master.R.id.scrollableLayout_merchant_home_page);
        mToolbarBgMerchantHomePage = findViewById(net.twoant.master.R.id.toolbar_bg_merchant_home_page);

        mIvBackMerchantHomePage = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_back_merchant_home_page);
        mToolbarText = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_name_toolbar);
        mCollectionMerchant = (AppCompatImageButton) findViewById(net.twoant.master.R.id.btn_collection_merchant_home_page);
        mBuyCard = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_buy_card);
        mToolbar = (ContentFrameLayout) findViewById(net.twoant.master.R.id.fl_toolbar);
        mShowMoreIntroduce = findViewById(net.twoant.master.R.id.fl_show_more_description_parent);
        findViewById(net.twoant.master.R.id.btn_shop_server_merchant_home_page).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_share_merchant_home_page).setOnClickListener(this);
        mCollectionMerchant.setOnClickListener(this);
        mShowMoreIntroduce.setOnClickListener(this);
        mIvBackMerchantHomePage.setOnClickListener(this);
        mBtnPay = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_pay);
        mBtnPay.setOnClickListener(this);

        mAddressHeight = getResources().getDimensionPixelSize(net.twoant.master.R.dimen.px_80);
        mScreenWidth = DisplayDimensionUtils.getScreenWidth();
        int height = (int) (mScreenWidth * 480F / 640F + .5F);
        mBannerHeight = mAddressHeight + height;

//        ViewGroup.LayoutParams layoutParams = mConvenientBannerMerchantHomePage.getLayoutParams();
//        layoutParams.height = height;
//        mConvenientBannerMerchantHomePage.setLayoutParams(layoutParams);
//
//        mConvenientBannerMerchantHomePage.setPageIndicator(new int[]{R.drawable.yh_banner_grey_dot, R.drawable.yh_banner_orange_dot})
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
//                .setCanLoop(true);

        mScrollableLayoutMerchantHomePage.setOnScrollListener(new ScrollHeaderLinearLayout.OnScrollListener() {

            private int mTopMarginSize = -1;
            private float mAlphaSize;
            private boolean isRestore = true;
            private float mToolbarTranslationSize = -1;

            @Override
            public void onScroll(int currentY, int maxY) {
                if (currentY <= mAddressHeight) {

                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
                    layoutParams.topMargin = mTopMarginSize = mAddressHeight - currentY;
                    mToolbar.setLayoutParams(layoutParams);

                    if (mAlphaSize != 0) {
                        isRestore = true;
                        mAlphaSize = 0;
                        mToolbarBgMerchantHomePage.setAlpha(mAlphaSize);
                        mIvBackMerchantHomePage.setImageResource(net.twoant.master.R.drawable.yh_press_back_circle_bg);
                        if (mToolbarText.getVisibility() != View.GONE) {
                            mToolbarText.setVisibility(View.GONE);
                        }
                    }

                } else if (currentY <= mBannerHeight) {

                    if (mTopMarginSize != 0) {
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
                        layoutParams.topMargin = mTopMarginSize = 0;
                        mToolbar.setLayoutParams(layoutParams);
                    }

                    if (mToolbarTranslationSize != 0) {
                        mToolbarTranslationSize = 0;
                        mToolbar.setTranslationY(mToolbarTranslationSize);
                    }

                    if (isRestore) {
                        isRestore = false;
                        mIvBackMerchantHomePage.setImageResource(net.twoant.master.R.drawable.ic_action_back);
                        if (mToolbarText.getVisibility() != View.VISIBLE) {
                            mToolbarText.setVisibility(View.VISIBLE);
                            mToolbarText.setText(mTvShopNameMerchantHomePage.getText());
                        }

                    }

                    mToolbarBgMerchantHomePage.setAlpha(mAlphaSize = currentY / mBannerHeight);


                } else {

                    if (mTopMarginSize != 0) {
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mToolbar.getLayoutParams();
                        layoutParams.topMargin = mTopMarginSize = 0;
                        mToolbar.setLayoutParams(layoutParams);
                    }


                    if (mAlphaSize != 1) {
                        isRestore = false;
                        mAlphaSize = 1;
                        mToolbarBgMerchantHomePage.setAlpha(mAlphaSize);
                        if (mToolbarText.getVisibility() != View.VISIBLE) {
                            mToolbarText.setVisibility(View.VISIBLE);
                            mToolbarText.setText(mTvShopNameMerchantHomePage.getText());
                        }
                        mIvBackMerchantHomePage.setImageResource(net.twoant.master.R.drawable.ic_action_back);
                    }

                    float max = maxY - mToolbarHeight;
                    if (currentY >= max) {
                        mToolbarTranslationSize = (currentY - max) / mToolbarHeight * mToolbarHeight;
                        mToolbar.setTranslationY(-mToolbarTranslationSize);
                    } else if (mToolbarTranslationSize != 0) {
                        mToolbarTranslationSize = 0;
                        mToolbar.setTranslationY(mToolbarTranslationSize);
                    }
                }
            }
        });

//        mFragments = new Fragment[]{
//
//                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_HAS_SCROLL_TOP,
//                        IRecyclerViewConstant.STATE_CODE_NOT_HEADER
//                        , IRecyclerViewConstant.TYPE_ACTIVITY
//                        , IRecyclerViewConstant.CATEGORY_ACTIVITY_MERCHANT_PAGE
//                        , mShopId, false)
//                , MerchantHomePageGoodsFragment.newInstance(mShopId)};
//        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager()
//                , mFragments, new String[]{"最新活动", "商品展示"});
//        mScrollableLayoutMerchantHomePage.setCurrentScrollableContainer((ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer) mFragments[0]);
//        mVpContainerViewPager.setAdapter(sectionsPagerAdapter);
//        mVpContainerViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                showOrHindBuyCard(position == 1);
//                mScrollableLayoutMerchantHomePage.setCurrentScrollableContainer((ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer) mFragments[position]);
//            }
//        });
//        mTabs.setupWithViewPager(mVpContainerViewPager);
//    }
        mFragments = new Fragment[]{
                MerchantHomePageGoodsFragment.newInstance(mShopId)};
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager()
                , mFragments, new String[]{"商品展示"});//, "商品展示"});
        //  mScrollableLayoutMerchantHomePage.setCurrentScrollableContainer((ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer) mFragments[0]);
        mVpContainerViewPager.setAdapter(sectionsPagerAdapter);
        showOrHindBuyCard(true);
        mScrollableLayoutMerchantHomePage.setCurrentScrollableContainer((ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer) mFragments[0]);
        mTabs.setupWithViewPager(mVpContainerViewPager);
    }
    /**
     * 切换底部 店内支付和购物车界面
     *
     * @param isCar 是否是购物车
     */
    private void showOrHindBuyCard(boolean isCar) {
        initBuyCartView();
        if (mBuyCarParent != null) {
            AnimatorSet set = new AnimatorSet();

            if (isCar) {
                set.playTogether(ObjectAnimator.ofFloat(mBtnPay, "translationX", 0, -mScreenWidth)
                        , ObjectAnimator.ofFloat(mBuyCarParent, "translationX", mScreenWidth, 0));

            } else {
                set.playTogether(ObjectAnimator.ofFloat(mBtnPay, "translationX", -mScreenWidth, 0)
                        , ObjectAnimator.ofFloat(mBuyCarParent, "translationX", 0, mScreenWidth));
            }
            set.setDuration(400).start();
        }
    }

    /**
     * 初始化 购物车视图
     */
    private void initBuyCartView() {
        if (mBuyCarParent == null && mBuyCard != null) {
            mBuyCarParent = (BuyCarView) mBuyCard.inflate();
            mBuyCarParent.setOnBuyCartListener(this);
            mBuyCarParent.setCartDataChangeListener(this);
        }
    }

    private void initGdMapData(String shopName, String latitude, String longitude) {
        AMap map = mGdTextureMapView.getMap();
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setAllGesturesEnabled(false);

        AppCompatButton navigation = new AppCompatButton(this);
        navigation.setId(net.twoant.master.R.id.merchant_navigation);
        Resources resources = getResources();
        int space = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_80);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(space, space, Gravity.BOTTOM | Gravity.END);
        int size = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_20);
        layoutParams.setMargins(size, size, size, size >> 2);
        navigation.setText(getString(net.twoant.master.R.string.merchant_navigation));
        navigation.setTextSize(10);
        navigation.setTextColor(ContextCompat.getColor(this, net.twoant.master.R.color.whiteTextColor));
        int padding = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_14);
        navigation.setPadding(padding, padding, padding, padding);
        navigation.setGravity(Gravity.CENTER);
        navigation.setBackgroundResource(net.twoant.master.R.drawable.yh_press_circle_bg);
        Drawable drawable = ContextCompat.getDrawable(this, net.twoant.master.R.drawable.ic_gd_navigation);
        int minimumWidth = drawable.getMinimumWidth();
        int minimumHeight = drawable.getMinimumHeight();
        drawable.setBounds(0, 0, minimumWidth, minimumHeight);
        navigation.setCompoundDrawables(null, drawable, null, null);
        navigation.setOnClickListener(this);
        mGdTextureMapView.addView(navigation, layoutParams);
        mGdTextureMapView.setOnClickListener(this);
        try {
            LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            map.addMarker(new MarkerOptions().title(shopName).position(latLng));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    latLng, 18, 0, 0));
            map.moveCamera(cameraUpdate);

            map.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    GDMapsActivity.startActivity(MerchantHomePageActivity.this, mLatitude, mLongitude, mTvShopNameMerchantHomePage.getText().toString());
                }
            });
        } catch (Exception e) {
            LogUtils.e("MerchantHomePageActivity，从接口获取的数据经纬度，转换失败");
        }
    }
}
