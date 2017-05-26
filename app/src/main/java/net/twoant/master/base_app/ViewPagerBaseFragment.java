package net.twoant.master.base_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2016/11/15.22:52
 * 该类 用于 ViewPager
 */

public abstract class ViewPagerBaseFragment extends Fragment {

    /**
     * OnViewCreate方法是否被执行
     */
    private boolean isCreated;
    /**
     * 当前Fragment 是否可见
     */
    protected boolean isVisibility;

    /**
     * 是第一次 创建
     */
    private boolean isFirstCreate;

    /**
     * 是否是从可见到不可见状态
     */
    private boolean isVisibilityLast;

    protected View mView;

    protected Context mContext;

    /**
     * @return 子类的资源布局id
     */
    protected abstract
    @LayoutRes
    int getLayoutRes();

    /**
     * 初始化组件数据，该方法只会调用一次。
     * 初始化 控件等
     */
    protected abstract void initFragmentComponentsData(View view);

    /**
     * 当前Fragment 为用户可见状态， 在Viewpager滑动过程中，只要当前界面为可见状态，就会被回调
     */
    protected abstract void onUserVisible();

    /**
     * 当前Fragment 为用户不可见状态，
     * 在Viewpager滑动过程中，只要当前界面为由可见状态到不可见状态，就会被回调
     */
    protected abstract void onUserInvisible();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisibility = getUserVisibleHint();
        dispatchFragmentState();
    }


    /**
     * 是否显示提示信息，默认不显示。
     *
     * @return true 显示
     */
    protected boolean isShowHintInfo() {
        return false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }


    /**
     * @param savedInstanceState 恢复数据
     */
    @CallSuper
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }


    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        isFirstCreate = true;
        if (isShowHintInfo()) {
            mView = inflater.inflate(R.layout.yh_viewpager_loading, container, false);
        } else {
            mView = inflater.inflate(getLayoutRes(), container, false);
        }
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isCreated = false;
        isFirstCreate = false;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isCreated = true;
        mView = view;
        mContext = mView.getContext();
        dispatchFragmentState();
    }

    private void dispatchFragmentState() {

        initFragmentState();
    }

    private void initFragmentState() {
        if (isVisibility && isCreated && isFirstCreate) {
            isFirstCreate = false;
            isVisibilityLast = true;
            if (isShowHintInfo() && mView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) mView;
                viewGroup.removeAllViews();
                LayoutInflater.from(mView.getContext()).inflate(getLayoutRes(), viewGroup, true);
            }
            initFragmentComponentsData(mView);
            onUserVisible();
        } else if (isVisibility && isCreated) {
            isVisibilityLast = true;
            onUserVisible();
        } else if (!isVisibility && isCreated && isVisibilityLast) {
            isVisibilityLast = false;
            onUserInvisible();
        }
    }
}
