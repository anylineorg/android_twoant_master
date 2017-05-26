package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.ui.main.adapter.GoodsPayOrderListAdapter;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.bean.PayOrderBean;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/3/30.
 * 商品订单 确认 页
 */
public class GoodsPayOrderListActivity extends BaseActivity implements View.OnClickListener {

    private final static String ACTION_START = "G_P_O_L_A";
    private final static String EXTRA_GOODS_LIST = "e_gl";
    private final static String EXTRA_ORDER = "e_pob";

    private CircleImageView mIvShopAvatar;
    private AppCompatTextView mTvShopName;
    private AppCompatTextView mTvCountPrice;
    private AppCompatButton mBtnApplyOrder;


    private PayOrderBean mPayOrderBean;
    private ArrayList<GoodsItemBean> mGoodsItems;
    /**
     * 当前选择的配送方式 view
     */
    private AppCompatTextView mSelectDispatchType;
    private AppCompatEditText mBuyerInputMessage;


    public static void startActivity(Activity activity, @NonNull ArrayList<GoodsItemBean> selectedList, @NonNull PayOrderBean payOrderBean) {
        Intent intent = new Intent(activity, GoodsPayOrderListActivity.class);
        intent.setAction(ACTION_START);
        intent.putParcelableArrayListExtra(EXTRA_GOODS_LIST, selectedList);
        intent.putExtra(EXTRA_ORDER, payOrderBean);
        activity.startActivity(intent);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        AppManager.addPaymentActivity(GoodsPayOrderListActivity.this);
        mPayOrderBean = intent.getParcelableExtra(EXTRA_ORDER);
        mGoodsItems = intent.getParcelableArrayListExtra(EXTRA_GOODS_LIST);
        return net.twoant.master.R.layout.yh_activity_goods_pay_order_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        initData();
        initPrice();
        mPayOrderBean.setPurchaseSort("1");//// TODO: 2017/3/30 暂时写死为“店内支付”
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.removePaymentActivity(GoodsPayOrderListActivity.this);
    }

    /**
     * 初始化 价格
     */
    private void initPrice() {
        if (null != mGoodsItems) {
            BigDecimal mAllPrice = BigDecimal.ZERO;
            int count = 0;
            for (GoodsItemBean item : mGoodsItems) {
                count += item.getGoodsCount();
                mAllPrice = mAllPrice.add(new BigDecimal(item.getGoodsUnitPrice()).multiply(new BigDecimal(item.getGoodsCount())).setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            String all = StringUtils.subZeroAndDot(mAllPrice.toString());
            SpannableString spannableString = new SpannableString(String.format(getString(net.twoant.master.R.string.payment_goods_hint), count, all));
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(GoodsPayOrderListActivity.this, net.twoant.master.R.color.colorPrimary)),
                    spannableString.length() - all.length() - 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvCountPrice.setText(spannableString);
            mPayOrderBean.setPrice(all);
            mPayOrderBean.setGoodsItemBean(mGoodsItems);
        } else {
            mTvCountPrice.setText(net.twoant.master.R.string.payment_not_find_data);
            mBtnApplyOrder.setEnabled(false);
        }
    }

    private void initData() {
        String shopAvatar = mPayOrderBean.getShopAvatar();
        if (!TextUtils.isEmpty(shopAvatar)) {
            ImageLoader.getImageFromNetwork(mIvShopAvatar, BaseConfig.getCorrectImageUrl(shopAvatar), GoodsPayOrderListActivity.this, net.twoant.master.R.drawable.ic_def_circle);
        }
        mTvShopName.setText(mPayOrderBean.getShopName());
    }

    private void initView() {
        ChatBaseActivity.initSimpleToolbarData(this, getString(net.twoant.master.R.string.payment_submit_order), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsPayOrderListActivity.this.finish();
            }
        });
        findViewById(net.twoant.master.R.id.ll_select_dispatch_type).setOnClickListener(this);
        mBuyerInputMessage = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_buyer_input_message);
        mSelectDispatchType = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_select_type);
        mIvShopAvatar = (CircleImageView) findViewById(net.twoant.master.R.id.iv_shop_avatar);
        mTvShopName = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_shop_name);
        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        mTvCountPrice = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_count_price);
        mBtnApplyOrder = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_apply_order);
        mBtnApplyOrder.setOnClickListener(this);
        GoodsPayOrderListAdapter adapter = new GoodsPayOrderListAdapter(GoodsPayOrderListActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(GoodsPayOrderListActivity.this));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(GoodsPayOrderListActivity.this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        ((NestedScrollView) findViewById(net.twoant.master.R.id.nsv_nested_scroll_view)).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY > scrollY && mBuyerInputMessage.hasFocus()) {
                    MainActivity.closeIME(false, mBuyerInputMessage);
                    mBuyerInputMessage.clearFocus();
                }
            }
        });
        adapter.setDataBean(mGoodsItems);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //选择配送方式
            case net.twoant.master.R.id.ll_select_dispatch_type:

                break;

            case net.twoant.master.R.id.btn_apply_order:
                mPayOrderBean.setLeaveMessage(mBuyerInputMessage.getText().toString());
                PayOrderActivity.startActivity(mPayOrderBean, GoodsPayOrderListActivity.this);
                break;
        }
    }
}
