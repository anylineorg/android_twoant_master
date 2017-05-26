package net.twoant.master.ui.my_center.fragment;

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
import android.view.View;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.convenient.adapter.MyConvenientInfoController;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by J on 2017/2/28.
 */

public class MyConvenientInfoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final static String ACTION_START = "MyConvenientInfoActivity_action_start";
    private final static String SHOP_ID = "shopId";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    public BaseRecyclerNetworkAdapter adapter;
    private boolean isShopPub =false;


    public static void startActivity(Activity activity,boolean isShop) {
        Intent intent = new Intent(activity, MyConvenientInfoActivity.class);
        intent.putExtra(SHOP_ID,isShop);
        intent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }


    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.xj_activity_myself_convenientinfo;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("我的便民");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRecyclerView();
    }
    private void initRecyclerView() {
        Intent intent = getIntent();
        isShopPub = intent.getBooleanExtra(SHOP_ID,false);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.srl_refresh_convenient_list);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, net.twoant.master.R.color.colorPrimary));
        mRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_convenient_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_5, false, 0));
        adapter = new BaseRecyclerNetworkAdapter(this,new MyConvenientInfoController(isShopPub,MyConvenientInfoActivity.this));
        adapter.setOnDataLoadingFinishListener(new IOnDataLoadingFinishListener() {
            @Override
            public void onDataLoadingFinishListener() {

            }
        });
        adapter.refreshData();

        adapter.setOnDataLoadingFinishListener(new IOnDataLoadingFinishListener() {
            @Override
            public void onDataLoadingFinishListener() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        findViewById(net.twoant.master.R.id.btn_back_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });
    }


    @Override
    public void onRefresh() {
        if (adapter != null) {
            adapter.refreshData();
        } else {
            mRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();

    }
}
