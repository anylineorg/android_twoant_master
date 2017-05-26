package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.fragment.ShangGoodsFragment;
import net.twoant.master.ui.charge.fragment.XiaGoodsFragment;
import net.twoant.master.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/11/18.
 */
public class GoodsManagerActivity extends LongBaseActivity implements View.OnClickListener{
    private RelativeLayout rlBack;
    private TextView tvPublish;
    private String shop_id;

    private TabLayout tabLayout ;
    private NoScrollViewPager viewPager;

    private List<String> stringList;
    private MyPagerAdapter pagerAdapter;
    public ShangGoodsFragment tab1;
    public XiaGoodsFragment tab2;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_goodsmanager;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        shop_id = getIntent().getStringExtra("shop_id");
        System.out.println(shop_id);
        initView();
    }

    private void initView() {
        rlBack = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_back_goodsmanager);
        tvPublish = (TextView) findViewById(net.twoant.master.R.id.tv_publish_goodsmanager);
        rlBack.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        viewPager = (NoScrollViewPager) findViewById(net.twoant.master.R.id.vp_content_myactivity);
    }

    @Override
    protected void initData() {
        stringList = new ArrayList<>();
        stringList.add("已上架");
        stringList.add("已下架");
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(net.twoant.master.R.color.navigationTextColor));
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

    public String getShopId(){
        return shop_id;
    }
    /**
     *
     * */
    public void setTab1(){
        tab1.onResume();
    }
    public void setTab2(){
        tab2.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case net.twoant.master.R.id.rl_back_goodsmanager:
                finish();
                break;
            case net.twoant.master.R.id.tv_publish_goodsmanager:
                Intent intent = new Intent(GoodsManagerActivity.this,PublishGoodsActivity.class);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
                break;
        }
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
                    tab1 = new ShangGoodsFragment();
                    return tab1;
                case 1:
                    tab2 = new XiaGoodsFragment();
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
