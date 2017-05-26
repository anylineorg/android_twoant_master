package net.twoant.master.ui.my_center.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.base_app.BaseActivity;

/**
 * Created by S_Y_H  on 2017/1/18.
 * 地址搜索
 */
public class SearchLocationActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatAutoCompleteTextView mSearchView;
    private RecyclerView mRvRecyclerView;

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, SearchLocationActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(0, 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.yh_activity_search_location;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void initView() {
        this.mSearchView = (AppCompatAutoCompleteTextView) findViewById(R.id.search_view);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.tv_search).setOnClickListener(this);
        this.mRvRecyclerView = (RecyclerView) findViewById(R.id.rv_recycler_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                SearchLocationActivity.this.finish();
                break;
            case R.id.tv_search:

                break;
        }
    }
}
