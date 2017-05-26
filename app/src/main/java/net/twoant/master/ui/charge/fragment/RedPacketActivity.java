package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/12/19.
 * 佛祖保佑   永无BUG
 */
public class RedPacketActivity extends LongBaseActivity implements View.OnClickListener{
    private TabLayout tabLayout ;
    private NoScrollViewPager viewPager;
    public List<String> stringList;
    private MyPagerAdapter pagerAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_redpacket;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(R.id.iv_back).setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tab_title_redpacket);
        viewPager = (NoScrollViewPager) findViewById(R.id.vp_content_redpacket);
    }

    @Override
    protected void initData() {
        stringList = new ArrayList<>();
        stringList.add("已领取");
        stringList.add("已完成");
        tabLayout.addTab(tabLayout.newTab().setText("已领取"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                finish();
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
                    RedPacketHaveFragment tab1 = new RedPacketHaveFragment();
                    return tab1;
                case 1:
                    RedPacketHadFragment tab2 = new RedPacketHadFragment();
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
