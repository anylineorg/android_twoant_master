package net.twoant.master.ui.my_center.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps.TextureMapView;

import net.twoant.master.base_app.BaseActivity;

/**
 * Created by S_Y_H  on 2017/1/18.
 * 添加收货地址
 */
public class SelectLocationActivity extends BaseActivity {

    private Toolbar mTbSimpleToolbar;
    private TextureMapView mMvMapView;
    private RecyclerView mRvRecyclerView;

    public static void startActivityForResult(Context context) {
        Intent intent = new Intent(context, SelectLocationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_select_location;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        mMvMapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMvMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMvMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMvMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMvMapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMvMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_select_location, menu);
        return true;
    }

    private void initView() {
        mTbSimpleToolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(mTbSimpleToolbar);
        mTbSimpleToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectLocationActivity.this.finish();
            }
        });

        mTbSimpleToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case net.twoant.master.R.id.app_bar_search:
                        if (true) throw new RuntimeException();
                        SearchLocationActivity.startActivity(SelectLocationActivity.this);
                        break;
                    case net.twoant.master.R.id.confirm:
                        setResult(RESULT_OK, null);
                        SelectLocationActivity.this.finish();
                        break;
                }
                return false;
            }
        });

        mMvMapView = (TextureMapView) findViewById(net.twoant.master.R.id.mv_map_view);
        mRvRecyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        mRvRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
