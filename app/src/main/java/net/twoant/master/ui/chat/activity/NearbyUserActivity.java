package net.twoant.master.ui.chat.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GdNearbyUserHelp;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.chat.control.NearbyUserController;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by DZY on 2017/3/13.
 * 佛祖保佑   永无BUG
 */

public class NearbyUserActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,GdNearbyUserHelp.OnNearbyUserListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private BaseRecyclerNetworkAdapter adapter;
    public GdNearbyUserHelp gdNearbyUserHelp;
    public ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_dynamic_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(R.id.tv_title_tool_bar)).setText("附近");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gdNearbyUserHelp = new GdNearbyUserHelp();
        gdNearbyUserHelp.search(AiSouAppInfoModel.getInstance().getUID() + "", this);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh_merchant_list);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_content_fragment);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, R.color.dividerLineColor, 0, R.dimen.px_2, false, 0));
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new BaseRecyclerNetworkAdapter(this,new NearbyUserController(progressBar,NearbyUserActivity.this));

//        adapter.refreshData();

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
    public void setResponse(String response) {

    }
}
