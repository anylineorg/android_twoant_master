package net.twoant.master.base_app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.twoant.master.common_utils.FragmentFactoryUtil;

/**
 * Created by S_Y_H on 2016/11/23.
 * 使用Viewpager 加载多个Fragment时，用这个适配器。
 */

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {
    private Class[] mStrings;
    private Fragment[] mFragmentList;
    private String[] mTitles;

    public FragmentViewPagerAdapter(FragmentManager fm, @Nullable Class[] strings) {
        super(fm);
        this.mStrings = strings;
        if (mStrings != null) {
            mFragmentList = new Fragment[mStrings.length];
        }
    }

    public Fragment[] getFragmentList() {
        return this.mFragmentList;
    }

    public void setFragmentList(@NonNull Fragment[] fragmentList) {
        this.mFragmentList = fragmentList;
    }

    public void setTitles(@NonNull String[] strings) {
        this.mTitles = strings;
        mFragmentList = new Fragment[strings.length];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragmentList[position];
        if (fragment != null) {
            return fragment;
        }
        return mFragmentList[position] = FragmentFactoryUtil.createFragment_v4(mStrings[position]);
    }

    @Override
    public int getCount() {
        return mStrings == null ? mFragmentList == null ? 0 : mFragmentList.length : mStrings.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.length > position) {
            return mTitles[position];
        }
        return super.getPageTitle(position);
    }
}
