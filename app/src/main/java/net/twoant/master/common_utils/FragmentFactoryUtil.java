package net.twoant.master.common_utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import net.twoant.master.base_app.ViewPagerBaseFragment;

/**
 * Created by Administrator on 2016/11/18.
 * fragment 生产类，通过反射的方式来实例化
 */

public final class FragmentFactoryUtil {

    /**
     * 通过类全名 实例化 fragment v4包
     * @param className 类全名
     * @param <T> 泛型，继承v4包的fragment的子类
     * @return 通过反射生成的实例
     */
    public static <T extends Fragment> T createFragment_v4(String className) {

        T fragment = null;
        try {
            fragment = (T) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            LogUtils.e("FragmentFactoryUtil#createFragment_v4#InstantiationException");
        } catch (IllegalAccessException e) {
            LogUtils.e("FragmentFactoryUtil#createFragment_v4#IllegalAccessException");
        } catch (ClassNotFoundException e) {
            LogUtils.e("FragmentFactoryUtil#createFragment_v4#ClassNotFoundException");
        }
        return fragment;
    }

    public static <T extends Fragment> T createFragment_v4(Class cla) {
        return createFragment_v4(cla.getName());
    }

    public static <T extends android.app.Fragment> T createFragment(Class cla) {
        return createFragment(cla.getName());
    }

        /**
         * 通过类全名 实例化 fragment v4包
         * @param className 类全名
         * @param <T> 泛型，继承 app 包下 fragment的子类
         * @return 通过反射生成的实例
         */
    public static <T extends android.app.Fragment> T createFragment(String className) {

        T fragment = null;
        try {
            fragment = (T) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            LogUtils.e("FragmentFactoryUtil#create_android_app_Fragment#InstantiationException");
        } catch (IllegalAccessException e) {
            LogUtils.e("FragmentFactoryUtil#create_android_app_Fragment#IllegalAccessException");
        } catch (ClassNotFoundException e) {
            LogUtils.e("FragmentFactoryUtil#create_android_app_Fragment#ClassNotFoundException");
        }
        return fragment;
    }

    /**
     * 通过反射的方式来动态的加载Fragment
     *
     * @param needShowFragmentTag 需要显示的Fragment的tag
     * @param arrayOfFragment     Fragment集合
     * @param resLayoutID         资源布局id
     * @return Fragment
     */
    public Fragment changeFragmentByTag(FragmentActivity activity, String needShowFragmentTag, String[] arrayOfFragment, int resLayoutID) {

        if (TextUtils.isEmpty(needShowFragmentTag) || arrayOfFragment == null || arrayOfFragment.length == 0) {
            return null;
        }

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ViewPagerBaseFragment fragment = (ViewPagerBaseFragment) fragmentManager.findFragmentByTag(needShowFragmentTag);
        if (fragment == null) {
            fragment = FragmentFactoryUtil.createFragment_v4(needShowFragmentTag);
            fragmentTransaction.add(resLayoutID, fragment, needShowFragmentTag);
        } else {
            fragmentTransaction.show(fragment);
        }

        for (String MAIN_FRAGMENT : arrayOfFragment) {
            ViewPagerBaseFragment tempFragment = (ViewPagerBaseFragment) fragmentManager.findFragmentByTag(MAIN_FRAGMENT);
            if (tempFragment != null && !tempFragment.getTag().equals(needShowFragmentTag)) {
                fragmentTransaction.hide(tempFragment);
            }
        }

        fragmentTransaction.commit();
        return fragment;
    }
}
