package net.twoant.master.ui.main.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.FragmentViewPagerAdapter;
import net.twoant.master.ui.main.fragment.ShopDetailsFragment;


/**
 * Created by kaiguokai on 2017/4/7.
 */

public class ShopGoodsActivity extends BaseActivity {
    private ViewPager mVpContentMainActivity;
    public static  String SHOP_ID;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_shop_goods;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        SHOP_ID= getIntent().getStringExtra("ID");
          initView();
    }
    private void initView() {
        mVpContentMainActivity = (ViewPager) findViewById(net.twoant.master.R.id.vp_content_main_activity);
        FragmentViewPagerAdapter mFragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), getFragmentFullNameTag());
        mVpContentMainActivity.setAdapter(mFragmentViewPagerAdapter);
        mVpContentMainActivity.setCurrentItem(0);
       // mVpContentMainActivity.addOnPageChangeListener(this);
        //TODO 具体保留几个 未定
        mVpContentMainActivity.setOffscreenPageLimit(1);
    }
    @Override
    protected void onStart() {
        super.onStart();
        AppManager.getAppManager().cleanAllOtherActivity(this);

    }
    private Class[] getFragmentFullNameTag() {

        return new Class[]{ShopDetailsFragment.class};// ChargeFragment.class, MyCenterFragment.class};
        // TODO: 2017/1/1 注册 前两个模块
       /* return new Class[]{ChatFragment.class, ConvenientFragment.class,
                HomeFragment.class, ChargeFragment.class, MyCenterFragment.class};*/
    }
}
