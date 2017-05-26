package net.twoant.master.ui.my_center.activity;

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
import android.widget.TextView;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.convenient.adapter.ConvenientInfoController;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DZY on 2017/2/27.
 * 佛祖保佑   永无BUG
 */

public class DynamicListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final static String ACTION_START = "MerchantListActivity_action_start";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    public BaseRecyclerNetworkAdapter adapter;
    public int firstItemPosition;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, DynamicListActivity.class);
        intent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_dynamic_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("动态列表");
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
        mRefreshLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.srl_refresh_merchant_list);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, net.twoant.master.R.color.colorPrimary));
        mRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_content_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_5, false, 0));
        adapter = new BaseRecyclerNetworkAdapter(this,new ConvenientInfoController("",DynamicListActivity.this));
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


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    firstItemPosition = linearManager.findFirstVisibleItemPosition();

                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            final int alterPosition = data.getIntExtra("alterPosition",-1);
            if (alterPosition!=-1) {
               getWindow().getDecorView().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       int i = alterPosition - firstItemPosition;
                       View childAt = mRecyclerView.getChildAt(i);
                       TextView tvZan = (TextView) childAt.findViewById(net.twoant.master.R.id.item_zan_count_dynamic);

                       String str = tvZan.getText().toString();
                       String regEx="[^0-9]";
                       Pattern p = Pattern.compile(regEx);
                       Matcher m = p.matcher(str);
                       int zanNum = Integer.parseInt(m.replaceAll("").trim());
                       tvZan.setText((++zanNum)+"人觉得很赞!");
                   }
               }, 500);
            }
        }

    }



}
