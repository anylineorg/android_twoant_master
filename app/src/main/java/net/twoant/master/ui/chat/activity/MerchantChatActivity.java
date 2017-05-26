package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.chat.control.MerchantChatRecyclerControl;
import net.twoant.master.ui.main.adapter.HomePageAdapter;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by S_Y_H on 2017/2/18.
 * 产品咨询 （用户与商家交流）
 */
public class MerchantChatActivity extends ChatBaseActivity {

    private final static String EXTRA_SHOP_ID = "e_s_i";
    private final static String ACTION_START = "MCA_AS";
    private HomePageAdapter mAdapter;

    public static void startActivity(Activity activity, @NonNull String shopId) {
        Intent intent = new Intent(activity, MerchantChatActivity.class);
        intent.putExtra(EXTRA_SHOP_ID, shopId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setAction(ACTION_START);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_merchant_chat;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        String mShopId = intent.getStringExtra(EXTRA_SHOP_ID);

        initSimpleToolbarData(this, getString(net.twoant.master.R.string.action_consult), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MerchantChatActivity.this.finish();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MerchantChatActivity.this));
        mAdapter = new HomePageAdapter(MerchantChatActivity.this
                , IRecyclerViewConstant.TYPE_MERCHANT_CHAT_LIST,
                IRecyclerViewConstant.CATEGORY_MERCHANT_CHAT_LIST, null,
                new MerchantChatRecyclerControl(mShopId), true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_1));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mAdapter) {
            mAdapter.onPause();
        }
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.onDestroy();
        }
    }
}
