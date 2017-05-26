package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.fragment.ShopBeingActivityFragment;
import net.twoant.master.ui.charge.fragment.ShopFinishActivityFragment;
import net.twoant.master.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/11/19.
 */
public class ActivityManagerActivity extends LongBaseActivity implements View.OnClickListener{
    private TabLayout tabLayout ;
    private NoScrollViewPager viewPager;
    private TextView tvPublish;
    private List<String> stringList;
    private MyPagerAdapter pagerAdapter;
    private String shop_id;
    public ShopBeingActivityFragment tab1;
    public ShopFinishActivityFragment tab2;
    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_activity_manager;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shop_id = getIntent().getStringExtra("shop_id");
        initView();
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.tab_FindFragment_title);
        viewPager = (NoScrollViewPager) findViewById(R.id.vp_findfragment_pager);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvPublish = (TextView) findViewById(R.id.tv_Save);
        tvPublish.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        tvPublish.setVisibility(View.VISIBLE);
        stringList = new ArrayList<>();
        stringList.add("进行中");
        stringList.add("已完成");
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.navigationTextColor));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tv_Save:
                Intent intent = new Intent(ActivityManagerActivity.this,PublishDetailActivity.class);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
             break;
        }
    }
    public String getShopId(){
        return shop_id;
    }
    /**
     *
     * */
    public void setTab2(){
        tab2.onResume();
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public MyPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    tab1 = new ShopBeingActivityFragment();
                    return tab1;
                case 1:
                    tab2 = new ShopFinishActivityFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
