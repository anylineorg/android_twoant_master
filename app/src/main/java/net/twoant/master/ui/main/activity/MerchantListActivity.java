package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.main.adapter.HomePageAdapter;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by S_Y_H on 2016/12/8.
 * 商家列表界面
 */
public class MerchantListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static String ACTION_START = "MerchantListActivity_action_start";
    private AppCompatTextView mAddressTextView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private HomePageAdapter mAdapter;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MerchantListActivity.class);
        intent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mAdapter) {
            mAdapter.onPause();
        }
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_merchant_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("附近商家");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MerchantListActivity.this.finish();
            }
        });
        initAddressInfo();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.srl_refresh_merchant_list);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, net.twoant.master.R.color.colorPrimary));
        mRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_content_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_5, false, 0));
        mAdapter = new HomePageAdapter(this, IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW
                , IRecyclerViewConstant.CATEGORY_MERCHANT_LIST_NEARBY, null);

        mAdapter.setOnDataLoadingFinishListener(new IOnDataLoadingFinishListener() {
            @Override
            public void onDataLoadingFinishListener() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        findViewById(net.twoant.master.R.id.btn_back_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });
    }

    private void initAddressInfo() {
        mAddressTextView = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_address_header);
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        /*if (instance.isUserSelectLocation()) {
            mAddressTextView.setText(instance.getSelectAddress());
        } else {*/
            if (!TextUtils.isEmpty(instance.getCompletionAddress())) {
                mAddressTextView.setText(instance.getCompletionAddress());
            }
            if (!instance.getLocationSuccessfulState()) {
                GDLocationHelper.getInstance().getOnceLocation(iOnLocationListener);
            } else {
                GDLocationHelper.getInstance().getOnceLocation(null);
            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GDLocationHelper.getInstance().onDestroy();
        if (null != mAdapter) {
            mAdapter.onDestroy();
        }
        GDLocationHelper.getInstance().removeLocationListener(iOnLocationListener);
    }

    @Override
    public void onRefresh() {
        if (mAdapter != null) {
            mAdapter.refreshData();
        } else {
            mRefreshLayout.setRefreshing(false);
        }
    }

    private GDLocationHelper.IOnLocationListener iOnLocationListener = new GDLocationHelper.IOnLocationListener() {
        @Override
        public void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation) {
            if (isSuccessful) {
                mAddressTextView.setText(aMapLocation.getAddress());
                mAdapter.refreshData();
            } else
                mAddressTextView.setText("获取失败");
        }
    };
}
