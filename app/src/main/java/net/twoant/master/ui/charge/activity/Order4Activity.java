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

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.fragment.HadPayFragment;
import net.twoant.master.ui.charge.fragment.HadSendFragment;
import net.twoant.master.ui.charge.fragment.WaitPayFragment;
import net.twoant.master.ui.charge.fragment.WaitSendFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2016/12/26.
 * 佛祖保佑   永无BUG
 */
public class Order4Activity extends LongBaseActivity implements View.OnClickListener{
    private TabLayout tabLayout ;
    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private List<String> stringList;
    private int page;
    public static void startActivity(Context context,int page) {
        Intent intent = new Intent(context, Order4Activity.class);
        intent.putExtra("page",page+"");
        Activity activity = (Activity) context;
        activity.overridePendingTransition(net.twoant.master.R.anim.act_fade_in_center, net.twoant.master.R.anim.act_fade_out_center);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_order4;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        page = Integer.parseInt(getIntent().getStringExtra("page"));
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        viewPager = (ViewPager) findViewById(net.twoant.master.R.id.vp_content_myactivity);
        viewPager.setOffscreenPageLimit(3);
        AiSouAppInfoModel.getInstance().addWaitOrderActivity(this);
    }

    @Override
    protected void initData() {
        stringList = new ArrayList<>();
        stringList.add("待付款");
        stringList.add("已付款");
//        stringList.add("待发货");
//        stringList.add("已完成");
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
//        tabLayout.addTab(tabLayout.newTab());
//        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(net.twoant.master.R.color.navigationTextColor));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(1);
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
        viewPager.setCurrentItem(page);


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
                    WaitPayFragment tab1 = new WaitPayFragment();
                    return tab1;
                case 1:
                    HadPayFragment tab2 = new HadPayFragment();
                    return tab2;
                case 2:
                    WaitSendFragment tab3 = new WaitSendFragment();
                    return tab3;
                case 3:
                    HadSendFragment tab4 = new HadSendFragment();
                    return tab4;
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AiSouAppInfoModel.getInstance().exitWaitOrderActivity();
    }
}
