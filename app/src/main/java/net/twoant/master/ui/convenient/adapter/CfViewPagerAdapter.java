package net.twoant.master.ui.convenient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import net.twoant.master.ui.convenient.fragment.ConvenientInfoListFragment;
import net.twoant.master.widget.entry.DataRow;

import java.util.List;

/**
 * Created by J on 2017/2/16.
 */

public class CfViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<ConvenientInfoListFragment> mFragments;
    private List<DataRow> dataRows;
    private FragmentManager fm;

    public CfViewPagerAdapter(FragmentManager fm, List<ConvenientInfoListFragment> mFragments, List<DataRow> dataRows) {
        super(fm);
        this.mFragments = mFragments;
        this.dataRows = dataRows;
        this.fm = fm;
    }

    public void setDatas(List<ConvenientInfoListFragment> mFragments) {
        this.mFragments = mFragments;
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        fm.beginTransaction().hide(mFragments.get(position)).commitAllowingStateLoss();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataRows.get(position).getString("NM");
    }
}
