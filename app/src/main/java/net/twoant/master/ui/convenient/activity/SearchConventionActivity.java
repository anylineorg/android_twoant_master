package net.twoant.master.ui.convenient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.main.adapter.control.SearchConventionControl;
import net.twoant.master.ui.convenient.widget.ConvenientSearchHelper;
import net.twoant.master.ui.main.adapter.HomePageAdapter;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

/**
 * Created by S_Y_H on 2017/3/17.
 * 便民信息搜索
 */
public class SearchConventionActivity extends BaseActivity {

    private final static String EXTRA_KEYWORD = "K_W";
    private final static String ACTION_START = "SCA_AS";
    private final static String EXTRA_CATEGORY = "E_GY";

    private String mKeyword;
    private String mCategory;
    private RecyclerView mRvRecyclerView;
    private HomePageAdapter mAdapter;


    public static void startActivity(Activity context, String category, String keyword) {
        Intent intent = new Intent(context, SearchConventionActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_KEYWORD, keyword);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mKeyword = intent.getStringExtra(EXTRA_KEYWORD);
        mCategory = intent.getStringExtra(EXTRA_CATEGORY);
        return net.twoant.master.R.layout.yh_activity_search_convention;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        ChatBaseActivity.initSimpleToolbarData(this, mKeyword, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchConventionActivity.this.finish();
            }
        });

        findViewById(net.twoant.master.R.id.ib_scroll_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mRvRecyclerView) {
                    mRvRecyclerView.scrollToPosition(0);
                }
            }
        });

        mRvRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        mRvRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRvRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));
        mAdapter = new HomePageAdapter(this, hashCode(),
                ConvenientSearchHelper.SHOP.equals(mCategory) ? SearchConventionControl.CATEGORY_MERCHANT
                        : SearchConventionControl.CATEGORY_PERSON, mKeyword, new SearchConventionControl(), true);
        mRvRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mAdapter) {
            mAdapter.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mAdapter) {
            mAdapter.onDestroy();
        }
    }
}
