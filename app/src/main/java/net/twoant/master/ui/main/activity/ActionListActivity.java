package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.ui.main.adapter.SectionsPagerAdapter;
import net.twoant.master.ui.main.fragment.ActivityFragment;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;


/**
 * Created by S_Y_H on 2016/12/7.
 * 活动列表界面
 */
public class ActionListActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private final static String ACTION_START = "ActionListActivity_action_start";
    private ActivityFragment[] mFragments;

    private ActivityFragment mCurrentFragment;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ActionListActivity.class);
        intent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }

    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_action_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("附近活动");
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionListActivity.this.finish();
            }
        });

        mFragments = new ActivityFragment[]{
                ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_REFRESH,IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_INTEGRAL_ACTIVITY, null)

                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_REFRESH, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_CHARGE_ACTIVITY, null)

                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_REFRESH, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_SAVED_ACTIVITY, null)

                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_REFRESH, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_METER_ACTIVITY, null)

                , ActivityFragment.newInstance(ActivityFragment.TYPE_LAYOUT_REFRESH, IRecyclerViewConstant.STATE_CODE_NORMAL_HEADER, IRecyclerViewConstant.TYPE_ACTIVITY, IRecyclerViewConstant.CATEGORY_RED_PACKER_ACTIVITY, null)
        };

        mCurrentFragment = mFragments[0];
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), mFragments, new String[]{"积分", "收费", "储值", "记次", "红包"});
        ViewPager mViewPager = (ViewPager) findViewById(net.twoant.master.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        TabLayout tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tabs);
        findViewById(net.twoant.master.R.id.btn_back_top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentFragment.scrollToPosition(0, false);
            }
        });
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(this);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_address_header)).setText(
                AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCompletionAddress());
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentFragment = mFragments[position];
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
