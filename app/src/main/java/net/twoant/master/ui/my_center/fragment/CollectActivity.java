package net.twoant.master.ui.my_center.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by DZY on 2017/1/3.
 * 佛祖保佑   永无BUG
 */
public class CollectActivity extends LongBaseActivity implements View.OnClickListener{
    private TabLayout tabLayout ;
    private NoScrollViewPager viewPager;
    private List<String> stringList;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_collect;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        viewPager = (NoScrollViewPager) findViewById(net.twoant.master.R.id.vp_content_myactivity);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initData() {
        stringList = new ArrayList<>();
        stringList.add("商品");
        stringList.add("商家");
        stringList.add("活动");
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(net.twoant.master.R.color.navigationTextColor));
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        Fragment[] mFragments;
        private MyPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.mFragments=new Fragment[]{
                    new MyCollectGoodsFragment(),
                    new MyCollectSellerFragment(),
                    new MyCollectActivityFragment()
            };
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }
}
