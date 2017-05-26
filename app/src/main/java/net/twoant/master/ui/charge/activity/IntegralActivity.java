package net.twoant.master.ui.charge.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.fragment.IntegralIncomeFragment;
import net.twoant.master.ui.charge.fragment.IntegralPayFragment;
import net.twoant.master.ui.main.activity.RechargeActivity;
import net.twoant.master.widget.ScrollHeaderLinearLayout;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.timer.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/5.
 */

public class IntegralActivity extends LongBaseActivity implements View.OnClickListener {
    private TabLayout tabLayout ;
    private ViewPager viewPager;
    private MyPagerAdapter pagerAdapter;
    private List<String> stringList;
    private TimePickerView pvTime;
    private TextView month;
    private TextView num;
    private Map<String,String> map = new HashMap<>();
    private IntegralIncomeFragment tab1;
    private IntegralPayFragment tab2;
    private TextView tvTotal;
    public String time ;
    public int tabPosition = 0;
    private TranslateAnimation translateAnimationHide;
    private TranslateAnimation translateAnimation;
    private ScrollHeaderLinearLayout scrollableLayout;
    public List<BaseFragment> fragments;
    private AppCompatImageButton img;

    public int getTabPosition() {
        return tabPosition;
    }

    public String getTime() {
        return time;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_integral;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        scrollableLayout = (ScrollHeaderLinearLayout) findViewById(R.id.scrollableLayout);
        tabLayout = (TabLayout) findViewById(R.id.tab_tablayout_integral);
        viewPager = (ViewPager) findViewById(R.id.vp_vipager_integral);
        num = (TextView) findViewById(R.id.tv_num_integral);
        month = (TextView) findViewById(R.id.tv_month_integral);
        tvTotal = (TextView) findViewById(R.id.tv_total_integral);
        findViewById(R.id.iv_back).setOnClickListener(this);
        img = (AppCompatImageButton) findViewById(R.id.fab_back_top);
        img.setOnClickListener(this);
        findViewById(R.id.tv_getintegral_integral).setOnClickListener(this);
        findViewById(R.id.rl_selecte_integral).setOnClickListener(this);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.navigationTextColor));
        //设置下划线高度，宽度跟随tab的宽度
        tabLayout.setSelectedTabIndicatorHeight(4);
        stringList = new ArrayList<>();
        stringList.add("收入");
        stringList.add("支出");
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.navigationTextColor));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        //内容的fragment
        fragments = new ArrayList<>();
        tab1 = new IntegralIncomeFragment();
        tab2 = new IntegralPayFragment();
        fragments.add(tab1);
        fragments.add(tab2);
        scrollableLayout.setCurrentScrollableContainer((IntegralIncomeFragment)fragments.get(0));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position==0) {
                    scrollableLayout.setCurrentScrollableContainer((IntegralIncomeFragment)fragments.get(position));
                }else {
                    scrollableLayout.setCurrentScrollableContainer((IntegralPayFragment)fragments.get(position));
                }
            }
        });
        ViewTreeObserver vto = scrollableLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //当前窗口获取焦点时，才能正真拿到titlebar的高度，此时将需要固定的偏移量设置给scrollableLayout即可
                scrollableLayout.setTopOffset(0);
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tabPosition = position;
                viewPager.setCurrentItem(tabPosition);
                switch (position){
                    case 0:
                        tvTotal.setText(""+tab1.getTotalIntegral());
                        tvTotal.setTextColor(CommonUtil.getColor(R.color.navigationTextColor));
                        tab1.onResume();
                        break;
                    case 1:
                        tvTotal.setText(""+tab2.getTotalIntegral());
                        tvTotal.setTextColor(Color.GRAY);
                        tab2.onResume();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 时间选择器qb_mouth
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M");
        time = sdf.format(new Date());
        month.setText(time);
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener(){
            @Override
            public void onTimeSelect(Date date){
                String t = WalletActivity.getTime(date);
                int tp=Integer.parseInt( t.split("-")[1]);
                int tempYear=Integer.parseInt( t.split("-")[0]);
                tp++;
                if (tp == 13) {
                    tp=1;
                    time=tempYear+1+"-"+tp;
                }else{
                    time = t.split("-")[0]+"-"+tp;
                }

                month.setText(time);
                //根据选择的tab 刷新收入和支出
                if (tabPosition==0){
                    tab1.monthSelected(time);
                    tvTotal.setTextColor(CommonUtil.getColor(R.color.navigationTextColor));
                    tvTotal.setText(""+tab1.getTotalIntegral());
                }


                if (tabPosition==1){
                    tab2.monthSelected(time);
                    tvTotal.setTextColor(Color.GRAY);
                    tvTotal.setText(""+tab2.getTotalIntegral());
                }
            }
        });
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        translateAnimationHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,2f);
        translateAnimationHide.setDuration(500);
        translateAnimationHide.setFillAfter(true);
        translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,0f,
                Animation.RELATIVE_TO_SELF,2f,
                Animation.RELATIVE_TO_SELF,0f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        img.startAnimation(translateAnimation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNetDataFonIntegral();
    }

    protected void requestNetDataFonIntegral() {

        LongHttp(ApiConstants.USERPRICEBAG, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("积分获取失败");
            }

            @Override
            public void onResponse(String response, int id) {
                DataRow data1 = DataRow.parseJson(response).getRow("data");
                num.setText(data1.getString("SCORE_BALANCE")+"");//账户积分
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_getintegral_integral:
                RechargeActivity.startActivity(IntegralActivity.this, RechargeActivity.TYPE_INTEGRAL);
                break;
            case R.id.rl_selecte_integral:
                pvTime.show();
                break;
            case R.id.fab_back_top:
                scrollableLayout.scrollTo(0,0);
                if (tabPosition == 0) {
                    tab1.setToTop();
                }else {
                    tab2.setToTop();
                }
                setImgHide();
                break;
        }
    }

    public void setTotal(String total){
        tvTotal.setText(total);
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
                    return tab1;
                case 1:
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

    public void setImgHide(){
        img.setVisibility(View.VISIBLE);
        img.startAnimation(translateAnimationHide);
        img.setEnabled(false);

    }

    public void setImgShow(){
        img.setVisibility(View.VISIBLE);
        img.startAnimation(translateAnimation);
        img.setEnabled(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
