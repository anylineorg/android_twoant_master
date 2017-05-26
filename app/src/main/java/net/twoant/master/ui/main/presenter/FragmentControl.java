package net.twoant.master.ui.main.presenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;

import net.twoant.master.R;
import net.twoant.master.common_utils.FragmentFactoryUtil;
import net.twoant.master.common_utils.LogUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/1.
 * fragment 控制显示隐藏
 * 不支持重复的Fragment切换
 */

public class FragmentControl {

    private final static String EXTRA = "fragment_C";

    private IOrderTabProvider iOrderTabProvider;
    private FragmentManager mSupportFragmentManager;
    private Fragment mCurrentPrimaryFragment = null;
    private OrderClass mLastOrderClass;
    private final FragmentActivity mFragmentActivity;

    private ArrayMap<String, OrderClass> mClassList = new ArrayMap<>();


    public FragmentControl(FragmentActivity fragmentActivity, @NonNull IOrderTabProvider orderTabProvider) {
        this.mFragmentActivity = fragmentActivity;
        this.iOrderTabProvider = orderTabProvider;
        mSupportFragmentManager = fragmentActivity.getSupportFragmentManager();
    }


    private void switchFragment(String tag) {
        final OrderClass aClass = mClassList.get(tag);
        if (null == aClass || null == mFragmentActivity) {
            return;
        }

        final Class current = aClass.mClassName;

        if (null == mSupportFragmentManager) {
            mSupportFragmentManager = mFragmentActivity.getSupportFragmentManager();
        }

        final String simpleName = getFragmentTag(current);

        if (null != mCurrentPrimaryFragment && simpleName.equals(mCurrentPrimaryFragment.getTag())) {
            return;
        }

        Fragment fragmentByTag = mSupportFragmentManager.findFragmentByTag(simpleName);


        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();

        if (null == fragmentByTag) {
            fragmentByTag = FragmentFactoryUtil.createFragment_v4(current);
            fragmentTransaction.add(aClass.mContentId, fragmentByTag,
                    simpleName).setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        } else {
            if (fragmentByTag.isHidden()) {
                fragmentTransaction.show(fragmentByTag).setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            } else if (fragmentByTag.isDetached()) {
                fragmentTransaction.attach(fragmentByTag).setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            }
        }

        if (null != mCurrentPrimaryFragment) {

            if (fragmentByTag != mCurrentPrimaryFragment) {
                mCurrentPrimaryFragment.setMenuVisibility(false);
                mCurrentPrimaryFragment.setUserVisibleHint(false);
            }
            if (null != mLastOrderClass && mLastOrderClass.isDetach) {
                fragmentTransaction.detach(mCurrentPrimaryFragment);
            } else if (!mCurrentPrimaryFragment.isHidden()) {
                fragmentTransaction.hide(mCurrentPrimaryFragment);
            }
        }

        if (null != fragmentByTag) {
            fragmentByTag.setMenuVisibility(true);
            fragmentByTag.setUserVisibleHint(true);
            mCurrentPrimaryFragment = fragmentByTag;
            mLastOrderClass = aClass;
        }
        try {
            fragmentTransaction.commitNow();
        } catch (Exception e) {
            LogUtils.e("Fragment switch e =" + e.toString());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            switchFragment(savedInstanceState.getString(EXTRA));
        } else {
            final List<OrderClass> orders = iOrderTabProvider.getOrderTabProvider();
            String tag = null;
            for (OrderClass o : orders) {
                if (!mClassList.containsKey(o.mTag)) {
                    mClassList.put(o.mTag, o);
                }
                if (o.isSelect) {
                    tag = o.mTag;
                }
            }
            switchFragment(tag);
        }
    }

    public Fragment getCurrentFragment() {
        return this.mCurrentPrimaryFragment;
    }


    public void onLowMemory() {
        Collection<OrderClass> values = mClassList.values();
        if (values.isEmpty() || null == mFragmentActivity) {
            return;
        }

        if (null == mSupportFragmentManager) {
            mSupportFragmentManager = mFragmentActivity.getSupportFragmentManager();
        }

        FragmentTransaction fragmentTransaction = mSupportFragmentManager.beginTransaction();
        boolean hasTransaction = false;
        Fragment tempFragment;
        for (OrderClass orderClass : values) {
            if (null != mCurrentPrimaryFragment //当前的
                    && !orderClass.isSaved  //没有保留状态
                    && !orderClass.mTag.contains(mCurrentPrimaryFragment.getTag())//非处于前台
                    ) {
                tempFragment = mSupportFragmentManager.findFragmentByTag(orderClass.mTag);
                if (null != tempFragment) {
                    hasTransaction = true;
                    fragmentTransaction.detach(tempFragment);
                }
            }
        }
        try {
            if (hasTransaction) {
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            LogUtils.e("low_memory exception" + e.toString());
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (null != mCurrentPrimaryFragment) {
            outState.putString(EXTRA, mCurrentPrimaryFragment.getTag());
        }
    }

    public void switchFragment(@NonNull Class chatPageFragmentClass) {
        switchFragment(getFragmentTag(chatPageFragmentClass));
    }

    public interface IOrderTabProvider {
        @NonNull
        List<OrderClass> getOrderTabProvider();
    }

    private static String getFragmentTag(@NonNull Class cla) {
        return cla.getSimpleName();
    }

    public static class OrderClass {

        private String mTag;//tag 标记
        /**
         * 当前选中，若为选中，则会被先显示
         */
        private boolean isSelect;
        private Class mClassName; //Fragment 类
        /**
         * 是否保留，如出现内存不足
         */
        private boolean isSaved;
        /**
         * 替换的id
         */
        private
        @IdRes
        int mContentId;
        private boolean isDetach;

        public OrderClass(Class className, @IdRes int id) {
            this.mTag = getFragmentTag(className);
            this.mContentId = id;
            this.mClassName = className;
        }

        public OrderClass setTag(String tag) {
            this.mTag = tag;
            return this;
        }

        public OrderClass setSelected(boolean isSelect) {
            this.isSelect = isSelect;
            return this;
        }

        public OrderClass setSaved(boolean isSaved) {
            this.isSaved = isSaved;
            return this;
        }

        public OrderClass setDetach(boolean isDetach) {
            this.isDetach = isDetach;
            return this;
        }
    }
}
