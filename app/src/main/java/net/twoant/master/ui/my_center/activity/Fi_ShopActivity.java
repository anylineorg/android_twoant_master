package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.my_center.fragment.Fi_out_fragment;
import net.twoant.master.ui.my_center.fragment.fi_in_fragment;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.timer.TimePickerView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/24 0024.
 */
public class Fi_ShopActivity extends LongBaseActivity {
    TimePickerView pvTime;
    ViewPager pager;
    private TabLayout tabLayout ;
    private MyPagerAdapter pagerAdapter;
    public List<String> stringList;
    private String shop_id;
    private TextView mouth,accont_price;
    public fi_in_fragment tab1;
    public Fi_out_fragment tab2;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.fi_shop_activity;
    }

    @Override
    protected void initData() {
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        tabLayout.addTab(tabLayout.newTab().setText("进账记录"));
        tabLayout.addTab(tabLayout.newTab().setText("提现记录"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(net.twoant.master.R.color.navigationTextColor));
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
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
    protected void onStart() {
        super.onStart();
        HashMap<String,String>parm=new HashMap<>();
        parm.put("shop",getIntent().getStringExtra("shop_id"));
        LongHttp(ApiConstants.SHOPACOUNT,"", parm, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    String price = DataRow.parseJson(response).getRow("data").getString("PURSE_BALANCE");
                    accont_price.setText(price);
                }catch (Exception e){

                }
            }
        });
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        pager=(ViewPager) findViewById(net.twoant.master.R.id.pager_view);
        tabLayout = (TabLayout) findViewById(net.twoant.master.R.id.tab_title_myactivity);
        mouth=(TextView) findViewById(net.twoant.master.R.id.mouth);
        accont_price =(TextView) findViewById(net.twoant.master.R.id.accont_price);
        shop_id=getIntent().getStringExtra("shop_id");
        findViewById(net.twoant.master.R.id.claear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 pvTime.show();
            }
        });
        stringList = new ArrayList<>();
        stringList.add("进账记录");
        stringList.add("提现记录");
        // 时间选择器qb_mouth
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//        mouth.setText(sdf.format(new Date()));

        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener(){
            @Override
            public void onTimeSelect(Date date){
                String t=getTime(date);
                int tp=Integer.parseInt( t.split("-")[1]);
                tp++;
                String time = t.split("-")[0]+"-"+tp;
                int tempYear=Integer.parseInt( t.split("-")[0]);
                if (tp == 13) {
                    tp=1;
                    time=tempYear+1+"-"+tp;
                }
                mouth.setText(time);
                tab1.initData();
                tab2.initData();
            }
        });
        TextView title = (TextView) findViewById(net.twoant.master.R.id.tv_Title);
        title.setText("财务收支");
        TextView save = (TextView) findViewById(net.twoant.master.R.id.tv_Save);
        save.setText("提现");
        //若为0则是从“我管理的商家界面”进入“商家管理”，否则直接进入“商家管理”
        String from_mymessage = getIntent().getStringExtra("from_mymessage");
        if ("0".equals(from_mymessage)) {
            save.setVisibility(View.GONE);
        }else{
            save.setVisibility(View.VISIBLE);
        }
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fi_ShopActivity.this.finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Fi_ShopActivity.this, WithdrawalsActivity.class);
                    intent.putExtra("shop_id", shop_id);
                    intent.putExtra("accont_price", accont_price.getText()+"");
                    startActivity(intent);
                }catch (Exception e){
                }
            }
        });
        initData();
    }
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(date);
    }

    public String getMonth(){
        String trim = mouth.getText().toString().trim();
        return trim;
    }
    class MyPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public MyPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return stringList.get(position);
        }
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    tab1 = new fi_in_fragment();
                    Bundle b = new Bundle();
                    b.putString("shop_id",shop_id);
                    b.putString("ym",mouth.getText()+"");
                    tab1.setArguments(b);
                    return tab1;
                case 1:
                    tab2 = new Fi_out_fragment();
                    b = new Bundle();
                    b.putString("shop_id",shop_id);
                    b.putString("ym",mouth.getText()+"");
                    tab2.setArguments(b);
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
