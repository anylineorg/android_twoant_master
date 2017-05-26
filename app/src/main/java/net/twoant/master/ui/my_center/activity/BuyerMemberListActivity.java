package net.twoant.master.ui.my_center.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;
import net.twoant.master.ui.my_center.adapter.BuyerMemberListControl;

/**
 * Created by S_Y_H on 2017/4/8.
 * 我的店铺会员
 */

public class BuyerMemberListActivity extends BaseActivity {

    private BaseRecyclerNetworkAdapter mRecyclerNetworkAdapter;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, BuyerMemberListActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_buyer_merchant_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initToolbar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mRecyclerNetworkAdapter) {
            mRecyclerNetworkAdapter.onDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mRecyclerNetworkAdapter) {
            mRecyclerNetworkAdapter.onPause();
        }
    }



    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(BuyerMemberListActivity.this));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(BuyerMemberListActivity.this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));
        mRecyclerNetworkAdapter = new BaseRecyclerNetworkAdapter(BuyerMemberListActivity.this, new BuyerMemberListControl());
        recyclerView.setAdapter(mRecyclerNetworkAdapter);
    }

    private void initToolbar() {
        ChatBaseActivity.initSimpleToolbarData(this, getString(net.twoant.master.R.string.buyer_member_merchant_list), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyerMemberListActivity.this.finish();
            }
        });
    }

}
