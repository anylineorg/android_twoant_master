package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.adapter.holder.BannerViewHolder;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.bean.PayOrderBean;
import net.twoant.master.ui.main.presenter.BuyerGoodsDetailControl;
import net.twoant.master.ui.main.widget.UMShareHelper;
import net.twoant.master.ui.main.widget.shopping_cart.BuyCarView;
import net.twoant.master.ui.main.widget.shopping_cart.GoodsAnimation;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.MarqueeTextView;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by S_Y_H on 2017/1/3.
 * 买家看到在商品详情页
 */
public class BuyerGoodsDetailActivity extends BaseActivity implements View.OnClickListener,
        BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener,
        BuyCarView.IOnBuyCartListener, BuyCarView.ICartDataChangeListener {

    private final static String EXTRA_GOODS_ID = "E_GI";
    private final static String ACTION_START = "A_ST";
    private final static String EXTRA_GOODS_BEAN = "E_GB";
    public final static String EXTRA_GOODS_LIST = "E_GL";
    //距离
    private AppCompatTextView mTvDistance;
    //当前地址
    private MarqueeTextView mTvAddressHeader;
    private AppCompatTextView mTvShopTitle;
    private ConvenientBanner mCbConvenientBanner;
    private AppCompatTextView mTvMerchantName;
    private AppCompatTextView mTvClickCount;
    private AppCompatTextView mTvGoodsPrice;
    private AppCompatImageButton mIbSubtractGoodsCount;
    private AppCompatTextView mTvNumberGoodsCount;
    private AppCompatImageButton mIbAddGoodsCount;
    private AppCompatImageButton mIbCollectGoodsDetail;
    private WebView mWvGoodsDetail;
    private BuyCarView mBuyCarView;
    private PayOrderBean mPayOrderBean;
    private String price,goods_nm;
    private BuyerGoodsDetailControl mBuyerGoodsDetailControl;

    /**
     * 商品id
     */
    private String mGoodsId;
    private List<GoodsItemBean> mGoodsItemBean;

    private BannerViewHolder.ViewHolderCreator mViewHolderCreator;
    /**
     * 轮播图数据
     */
    private ArrayList<String> mBannerData;
    private HintDialogUtil mHintDialog;
    private UMShareHelper mUMShareHelper;


    public static void startActivity(Activity activity, String goodsId) {
        Intent intent = new Intent(activity, BuyerGoodsDetailActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_GOODS_ID, goodsId);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(@Nullable Fragment fragment, @NonNull Activity activity, String goodsId, @NonNull ArrayList<GoodsItemBean> goodsItemBeen, int requestCode) {
        Intent intent = new Intent(activity, BuyerGoodsDetailActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_GOODS_ID, goodsId);
        intent.putParcelableArrayListExtra(EXTRA_GOODS_BEAN, goodsItemBeen);
        if (null != fragment) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntentData(intent);
    }

    @Override
    public void onPaymentClickListener(@NonNull View view, ArrayList<GoodsItemBean> goodsItemBeen) {

        mBuyCarView.changeGoodsCount(true, mGoodsId, view);
        mBuyCarView.restoreData();

        String url= ApiConstants.CREATE_ORDER;
        String head="pro="+mGoodsId+"&qty=1";
       /* Set<String> keys= HomeFragment.selectedList.keySet();
        for (String str : keys) {
            GoodsItem item= HomeFragment.selectedList.get(str);
            head+="pro="+str+"&qty="+item.getGoodsCount()+"&";
        }
        head=head.substring(0,head.length()-1);
        url+=head;*/
        url+=head;
        HashMap<String,GoodsItem> map=new HashMap<>();
        GoodsItemBean item=new GoodsItemBean();
        map.put(mGoodsId,item);
        item.setShop_name(mPayOrderBean.getShopName());
        item.setShop_id(mPayOrderBean.getShopId()+"");
        item.setGoodsCount(1);
        item.setGoodsCount("1");
        item.setGoodsId(mGoodsId);
        item.setGoodsCount("1");
        item.setGoodsUnitPrice(Float.parseFloat(price));
        item.setMember_price(Float.parseFloat(price));
        item.setGoodsPrice(Float.parseFloat(price));
        item.setGoodsPrice(price);
        item.setGoodsName(goods_nm);
        item.setGoods_img(mPayOrderBean.getShopAvatar());
        List<GoodsItemBean> list=new ArrayList<>();
        list.add(item);
        PostOrderActivity.startActivity(BuyerGoodsDetailActivity.this,list,mPayOrderBean.getShopName(),mPayOrderBean.getShopId(),true);


       // GoodsPayOrderListActivity.startActivity(BuyerGoodsDetailActivity.this, goodsItemBeen, mPayOrderBean);
       /* if (null != mPayOrderBean) {
            goodsItemBeen = deleteZero(goodsItemBeen);
            if (goodsItemBeen.isEmpty()) {
                ToastUtil.showShort(R.string.goods_is_empty);
            } else {
                mBuyCarView.restoreData();
                GoodsPayOrderListActivity.startActivity(BuyerGoodsDetailActivity.this, goodsItemBeen, mPayOrderBean);
            }
        } else {
            ToastUtil.showShort(R.string.goods_apply_order_fail);
        }*/
    }

    @NonNull
    @Override
    public List<GoodsItemBean> getDataList() {
        return mGoodsItemBean;
    }

    @Override
    public void onDataChange(@NonNull List<GoodsItemBean> cartList, @NonNull GoodsItemBean current) {
        this.mGoodsItemBean = cartList;
    }

    @Override
    public void onCurrentGoodsCountChange(int count) {
        if (null != mTvNumberGoodsCount) {
            mTvNumberGoodsCount.setText(String.valueOf(count));
        }
        if (null != mIbSubtractGoodsCount) {
            int visibility = mIbSubtractGoodsCount.getVisibility();
            if ((0 >= count && View.VISIBLE == visibility) || (0 < count && View.VISIBLE != visibility)) {
                GoodsAnimation.hideOrShowSubtract(mIbSubtractGoodsCount, mIbAddGoodsCount);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        initIntentData(getIntent());
        return net.twoant.master.R.layout.yh_activity_goods_detail;
    }

    private void initIntentData(Intent intent) {
        BaseConfig.checkState(intent, ACTION_START);
        mGoodsId = intent.getStringExtra(EXTRA_GOODS_ID);
        if (TextUtils.isEmpty(mGoodsId)) {
            ToastUtil.showShort(net.twoant.master.R.string.goods_data_exception);
            BuyerGoodsDetailActivity.this.finish();
        }
        mGoodsItemBean = intent.getParcelableArrayListExtra(EXTRA_GOODS_BEAN);
        if (null == mGoodsItemBean) {
            mGoodsItemBean = new ArrayList<>();
        }



    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        mHintDialog = new HintDialogUtil(this).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                BuyerGoodsDetailActivity.this.finish();
            }
        });
        mBuyCarView.setCurrentGoodsId(mGoodsId);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (null == mBuyerGoodsDetailControl) {
                    mBuyerGoodsDetailControl = new BuyerGoodsDetailControl(BuyerGoodsDetailActivity.this);
                }
                mBuyerGoodsDetailControl.start(mGoodsId);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (null != mWvGoodsDetail) {
            mWvGoodsDetail.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mWvGoodsDetail) {
            mWvGoodsDetail.onResume();
        }
        if (null != mCbConvenientBanner) {
            mCbConvenientBanner.startTurning(5000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mWvGoodsDetail) {
            mWvGoodsDetail.onPause();
        }

        if (null != mCbConvenientBanner) {
            mCbConvenientBanner.stopTurning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mWvGoodsDetail) {
            ViewParent parent = mWvGoodsDetail.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mWvGoodsDetail);
            }
            mWvGoodsDetail.destroy();
        }
        if (null != mBuyerGoodsDetailControl) {
            mBuyerGoodsDetailControl.onDestroy();
        }
        if (null != mBuyCarView) {
            mBuyCarView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mUMShareHelper) {
            mUMShareHelper.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null != mUMShareHelper) {
            mUMShareHelper.onConfigurationChanged();
        }
    }

    @Override
    public void onRequestCollection(int state) {
        switch (state) {
            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.DO_COLLECTION_SUCCESSFUL:
                ToastUtil.showShort(net.twoant.master.R.string.goods_collection_successful);

            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.IS_COLLECTION:
                mIbCollectGoodsDetail.setImageResource(net.twoant.master.R.drawable.ic_collection_check);
                break;

            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.CANCEL_COLLECTION_SUCCESSFUL:
                ToastUtil.showShort(net.twoant.master.R.string.goods_collection_cancel);

            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.NOT_COLLECTION:
                mIbCollectGoodsDetail.setImageResource(net.twoant.master.R.drawable.ic_collection_uncheck);
                break;

            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.QUERY_COLLECTION_FAIL:
                ToastUtil.showShort(net.twoant.master.R.string.goods_query_collection_fail);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.ib_subtract_goods_count:
                mBuyCarView.changeGoodsCount(false, mGoodsId, v);
                break;
            case net.twoant.master.R.id.ib_add_goods_count:
                if (View.INVISIBLE == mIbSubtractGoodsCount.getVisibility()) {
                    GoodsAnimation.hideOrShowSubtract(mIbSubtractGoodsCount, v);
                }
                mBuyCarView.changeGoodsCount(true, mGoodsId, v);
                break;
            case net.twoant.master.R.id.ib_collect_goods_detail:
                mBuyerGoodsDetailControl.chageCollectionGoodState(mGoodsId);
                break;
            case net.twoant.master.R.id.ib_share_goods_detail:
                if (null == mUMShareHelper) {
                    mUMShareHelper = new UMShareHelper(BuyerGoodsDetailActivity.this);
                }
                mUMShareHelper.setText(mTvShopTitle.getText().toString(), getString(net.twoant.master.R.string.share_goods), "");
                mUMShareHelper.showDialogAtBottom(null, true);
                break;
            case net.twoant.master.R.id.ib_connect_merchant:
                Object shopId = mTvMerchantName.getTag();
                if (shopId instanceof String) {
                    UserInfoUtil.startChat(null, (String) shopId, BuyerGoodsDetailActivity.this);
                }
                break;
            default:
                setResult();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    /**
     * "data":{
     * "REG_TIME":"2017-03-30 14:15:06",
     * "SHOP_TOWN_CODE":"370211",
     * "SORT_ID":584,
     * "SHOP_ID":335,
     * "GOODS_TYPE":584,
     * "SHOP_LON":"119.98423",
     * "LONGITUDE":null,
     * "SHOP_CITY_CODE":"0532",
     * "GOODS_IMG":"activity_img/149085432937176db1396.jpg",
     * "GOODS_LD":null,
     * "SHOP_LAT":"35.8403",
     * "SHOP_LONGITUDE":"119.98423",
     * "GOODS_SEND_PRICE":0,
     * "GOODS_INTRODUCE":"大鹅一只",
     * "GOODS_NAME":"正宗东北大鹅，东北家常菜",
     * "ID":1000,
     * "UPT_TIME":"2017-03-31 22:06:09","SORT_NM":"炒菜，凉菜",
     * "MAP_ID":3725,
     * "SHOP_ADDRESS":"山东省青岛市黄岛区滨海街道珠山南路22-5号",
     * <p>
     * "IMG":{
     * "GOODS_BANDER_IMG1":"activity_img/1490854149789d4304415.jpg",
     * "GOODS_BANDER_IMG2":"activity_img/1490854149789d1987ed5.jpg",
     * "GOODS_BANDER_IMG3":"activity_img/1490854149774f8885a3e.jpg",
     * "GOODS_BANDER_IMG4":"activity_img/1490854149789cf03f6d4.jpg",
     * "GOODS_ID":1000,
     * "ID":966,
     * "GOODS_BANDER_DT1":null,
     * "GOODS_BANDER_DT3":null,
     * "GOODS_BANDER_DT2":null
     * "GOODS_BANDER_DT4":null,
     * "GOODS_IMG_TXT":"大鹅特价一只168，半只88",
     * },
     * <p>
     * "PRICE":168,
     * "GOODS_TITLE":"商品标题",
     * "GOODS_STATE":0,
     * "SHOP_NM":"东北骨头城",
     * "TIME":"2017-03-30 14:15:06",
     * "UNIT_NM":null,
     * "DATA_STATUS":1,
     * "SELL_QTY":0,
     * "SHOP_IS_REVIEWED":2,
     * "GOODS_PRICE":168,
     * "GOODS_ID":1000,
     * "ADDRESS":"山东省青岛市黄岛区珠山南路靠近禾田园蛋糕",
     * "GOODS_COUNT":0,
     * "CLICK":37,
     * "SHOP_LATITUDE":"35.8403",
     * "GOODS_PK_SHOP":335,
     * "LATITUDE":null,
     * "NM":"正宗东北大鹅，东北家常菜"},
     */
    @Override
    public void onRequestDetailSuccessful(@NonNull DataRow result) {
        if (BuyerGoodsDetailActivity.this.isFinishing()) {
            return;
        }

        mTvAddressHeader.setText(AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCompletionAddress());
        mTvDistance.setText(result.getStringDef("_DISTANCE_TXT", ""));

        mTvShopTitle.setText(result.getStringDef("TITLE", ""));
        goods_nm=result.getStringDef("TITLE", "");
        mTvClickCount.setText(String.format(getString(net.twoant.master.R.string.goods_click_count), result.getInt("CLICK")));
        String temp = result.getStringDef("TENANT_NM", "");
        SpannableString shopSpannable = new SpannableString(String.format(getString(net.twoant.master.R.string.goods_merchant), temp));
        int primaryColor = ContextCompat.getColor(BuyerGoodsDetailActivity.this, net.twoant.master.R.color.colorPrimary);
        shopSpannable.setSpan(new ForegroundColorSpan(primaryColor), shopSpannable.length() - temp.length(), shopSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        shopSpannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Object tag = mTvMerchantName.getTag();
                if (tag instanceof String) {
                    MerchantHomePageActivity.startActivity(BuyerGoodsDetailActivity.this, (String) tag);
                }
            }
        }, shopSpannable.length() - temp.length(), shopSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvMerchantName.setText(shopSpannable);
        mTvMerchantName.setMovementMethod(LinkMovementMethod.getInstance());
        mTvMerchantName.setTag(result.getString("TENANT_ID"));
        mGoodsId=result.getString("ID");
        temp = result.getStringDef("SCORE", "");
        price=result.getStringDef("SCORE", "");
        SpannableString priceSpannable = new SpannableString(String.format(getString(net.twoant.master.R.string.goods_price), temp));
        priceSpannable.setSpan(new ForegroundColorSpan(primaryColor), priceSpannable.length() - temp.length() - 1, priceSpannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvGoodsPrice.setText(priceSpannable);
        DataRow imgRow = result.getRow("IMGS");
        if (BuyerGoodsDetailActivity.this.isFinishing()) {
            return;
        }

        if (null != imgRow) {
            mWvGoodsDetail.loadDataWithBaseURL(null, result.getString("DESCRIPTION"), "text/html", "UTF-8", null);
            setBannerData(result);
            initDisplayImg(result);
        }
        initGoodsItemData(result);
    }

    private void initGoodsItemData(DataRow result) {
        mPayOrderBean = new PayOrderBean(PayOrderBean.TYPE_GOODS, null, result.getString("TENANT_ID"),
                result.getStringDef("TENANT_NM", "-"),
                result.getString("IMG_FILE_PATH"), null, null);

        if (BuyerGoodsDetailActivity.this.isFinishing()) {
            return;
        }
        GoodsItemBean currentGoodsItem = null;
        GoodsItemBean bean;
        int index = -1, size = mGoodsItemBean.size();
        for (int i = 0; i < size; ++i) {
            bean = mGoodsItemBean.get(i);
            if (null != bean && mGoodsId.equals(bean.getGoodsId())) {
                index = i;
                currentGoodsItem = bean;
                break;
            }
        }
        if (null == currentGoodsItem) {
            currentGoodsItem = new GoodsItemBean(mGoodsId, result.getString("TITLE"), BaseConfig.getCorrectImageUrl(result.getString("GOODS_IMG")), result.getStringDef("PRICE", "999999999"));
            mGoodsItemBean.add(currentGoodsItem);
        } else {
            Collections.swap(mGoodsItemBean, size - 1, index);
        }

        if (0 < currentGoodsItem.getGoodsCount() && View.VISIBLE != mIbSubtractGoodsCount.getVisibility()) {
            GoodsAnimation.hideOrShowSubtract(mIbSubtractGoodsCount, mIbAddGoodsCount);
        }
        mBuyCarView.initData(mGoodsId, mGoodsItemBean);

        mBuyCarView.mCarViewHolder.mTvGoodsCount.setText("数量:1");
        mBuyCarView.mCarViewHolder.mTvPrice.setText("合计:"+price+"积分");
        mBuyCarView.mCarViewHolder.mBtnClearing.setText("去兑换");
    }

    /**
     * 初始化商品展图
     */
    private void initDisplayImg(@NonNull DataRow imgRow) {
        ArrayList<String> display = new ArrayList<>();
        String temp;
        DataSet dataSet=imgRow.getSet("IMGS");
        for (int i=0;i<dataSet.getRows().size();i++){
            DataRow row=dataSet.getRow(i);
            display.add(row.getStringDef("IMG_FILE_PATH",""));
        }
       /* for (int i = 1; i < 5; ++i) {
            if (!ControlUtils.isNull(temp = imgRow.getString("GOODS_BANDER_DT" + i))) {
                if (null == display) {
                    display = new ArrayList<>(4);
                }
                display.add(temp);
            }
        }*/
        if (null != display) {
            ViewGroup.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final ViewGroup parent = (ViewGroup) mWvGoodsDetail.getParent();

            for (String str : display) {
                AppCompatImageView imageView = new AppCompatImageView(BuyerGoodsDetailActivity.this);
                imageView.setLayoutParams(layoutParams);
                parent.addView(imageView, parent.indexOfChild(mWvGoodsDetail));
                ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(str), BuyerGoodsDetailActivity.this, net.twoant.master.R.drawable.ic_def_action);
            }
        }
    }

    @Override
    public void showDialog(int state, String hint) {
        if (BuyerGoodsDetailActivity.this.isFinishing()) {
            return;
        }

        switch (state) {
            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.DIALOG_CLOSE:
                mHintDialog.dismissDialog();
                break;
            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.DIALOG_SHOW_ERROR:
                mHintDialog.showError(hint);
                break;

            case BuyerGoodsDetailControl.IOnBuyerGoodsDetailListener.DIALOG_SHOW_LOADING:
                mHintDialog.showLoading(hint, true, false);
                break;


        }
    }

    /**
     * 初始化 banner数据
     */
    @SuppressWarnings("unchecked")
    private void setBannerData(@NonNull DataRow set) {

        String temp;
        ArrayList<String> strings = new ArrayList<>(4);
        DataSet dataSet=set.getSet("IMGS");
        for (int i=0;i<dataSet.getRows().size();i++){
            DataRow row=dataSet.getRow(i);
            strings.add(row.getStringDef("IMG_FILE_PATH",""));
        }
       /* for (int i = 1; i < 5; ++i) {
            if (!ControlUtils.isNull(temp = set.getString("GOODS_BANDER_IMG" + i))) {
                strings.add(temp);
            }
        }*/

        if (mViewHolderCreator == null)
            mViewHolderCreator = new BannerViewHolder.ViewHolderCreator();

        mCbConvenientBanner.setPages(mViewHolderCreator, mBannerData = strings)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        displayImage(position);
                    }
                }).setScrollDuration(1500);
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

    private void initView() {
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText(net.twoant.master.R.string.buyer_goods_detail);
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(this);
        mBuyCarView = (BuyCarView) findViewById(net.twoant.master.R.id.bcv_buy_car_view);
        mBuyCarView.setCartDataChangeListener(this);
        mBuyCarView.setOnBuyCartListener(this);




        mTvAddressHeader = (MarqueeTextView) findViewById(net.twoant.master.R.id.tv_address_header);
        mTvDistance = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_distance);

        //初始化 轮播图
        initBannerView();
        initWebView();

        mTvShopTitle = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_title);
        mTvMerchantName = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_merchant_name);
        mTvClickCount = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_click_cout);
        mTvGoodsPrice = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_goods_price);
        mIbSubtractGoodsCount = (AppCompatImageButton) findViewById(net.twoant.master.R.id.ib_subtract_goods_count);
        mTvNumberGoodsCount = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_number_goods_count);
        mIbAddGoodsCount = (AppCompatImageButton) findViewById(net.twoant.master.R.id.ib_add_goods_count);
        mIbCollectGoodsDetail = (AppCompatImageButton) findViewById(net.twoant.master.R.id.ib_collect_goods_detail);

        mIbSubtractGoodsCount.setOnClickListener(this);
        mIbAddGoodsCount.setOnClickListener(this);
        mIbCollectGoodsDetail.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ib_share_goods_detail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ib_connect_merchant).setOnClickListener(this);
    }

    /**
     * 初始化 浏览器 配置
     */
    private void initWebView() {
        mWvGoodsDetail = (WebView) findViewById(net.twoant.master.R.id.wv_goods_detail);
        WebSettings settings = mWvGoodsDetail.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultFontSize(18);
        mWvGoodsDetail.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    /**
     * 初始化轮播图 配置
     */
    private void initBannerView() {
        mCbConvenientBanner = (ConvenientBanner) findViewById(net.twoant.master.R.id.cb_convenientBanner);
        ViewGroup.LayoutParams layoutParams = mCbConvenientBanner.getLayoutParams();
        layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() * ApiConstants.GOODS_BANNER_HEIGHT_SCALE
                * 1.0F / ApiConstants.GOODS_BANNER_WIDTH_SCALE + 0.5F);
        mCbConvenientBanner.setLayoutParams(layoutParams);

        mCbConvenientBanner.setPageIndicator(new int[]{net.twoant.master.R.drawable.yh_banner_grey_dot, net.twoant.master.R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setCanLoop(true);
    }

    private static ArrayList<GoodsItemBean> deleteZero(@NonNull List<GoodsItemBean> list) {
        ArrayList<GoodsItemBean> temp = new ArrayList<>(list.size());
        for (GoodsItemBean item : list) {
            if (0 < item.getGoodsCount()) {
                temp.add(item);
            }
        }
        return temp;
    }

    private void setResult() {
        if (mGoodsItemBean instanceof ArrayList) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(EXTRA_GOODS_LIST, deleteZero(mGoodsItemBean));
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        BuyerGoodsDetailActivity.this.finish();
    }
}
