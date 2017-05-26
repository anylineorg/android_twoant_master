package net.twoant.master.ui.main.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by S_Y_H on 2016/12/8.
 * ViewPager 加载 Fragment 适配器
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragments;

    private String[] mTitles;

    public SectionsPagerAdapter(FragmentManager fm, @NonNull Fragment[] fragments, @Nullable String[] titles) {
        super(fm);
        this.mFragments = fragments;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && position < mTitles.length)
            return mTitles[position];

        return "";
    }
}
