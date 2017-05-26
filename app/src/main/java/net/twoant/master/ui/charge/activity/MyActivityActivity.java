package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.fragment.MyActivityFragment1;
import net.twoant.master.ui.charge.fragment.MyActivityFragment2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class MyActivityActivity extends LongBaseActivity implements View.OnClickListener{

    private TabLayout tabLayout ;
    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private List<String> stringList;

    public static void startActivity(Context context){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, MyActivityActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_myactivity;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        viewPager = (ViewPager) findViewById(net.twoant.master.R.id.vp_content_myactivity);
    }

    @Override
    protected void initData() {
        stringList = new ArrayList<>();
        stringList.add("已参加活动");
        stringList.add("已完成活动");
        tabLayout.addTab(tabLayout.newTab().setText("已参加活动"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成活动"));
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
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
                    MyActivityFragment1 tab1 = new MyActivityFragment1();
                    return tab1;
                case 1:
                    MyActivityFragment2 tab2 = new MyActivityFragment2();
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
