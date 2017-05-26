package net.twoant.master.base_app;

import android.app.Activity;
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
import android.view.ViewParent;

/**
 * Created by S_Y_H on 2016/11/15.22:52
 * 用于首页的五个tab 页的基类
 * 初始化是 在 onActivityCreated（）中进行初始化操作的
 */

public abstract class HomeBaseFragment extends Fragment {

    protected View mView;

    protected Context mContext;

    protected Activity mActivity;

    protected boolean isVisibility;

    private boolean isInitialization;

    /**
     * @return 子类的资源布局id
     */
    protected abstract
    @LayoutRes
    int getLayoutRes();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    /**
     * 初始化 控件等
     */
    protected abstract void initFragmentComponentsData(View view);

    /**
     * 当前Fragment 为用户可见状态
     */
    protected abstract void onUserVisible();

    /**
     * 当前Fragment 为用户不可见状态，
     */
    protected abstract void onUserInvisible();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisibility = isVisibleToUser;
        dispatchVisibility();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isVisibility = !hidden;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == mActivity) {
            mActivity = getActivity();
        }
        initFragmentComponentsData(mView);
        isInitialization = true;
        dispatchVisibility();
    }


    /**
     * @param savedInstanceState 恢复数据
     */
    @CallSuper
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        isInitialization = savedInstanceState.getBoolean("H_B_F_II");
    }

    @Override
    @CallSuper
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("H_B_F_II", isInitialization);
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        return inflater.inflate(getLayoutRes(), container, false);
    }


    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mContext = mView.getContext();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        isInitialization = false;
        isVisibility = false;
        if (null != mView) {
            final ViewParent parent = mView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mView);
            }
        }
    }

    private void dispatchVisibility() {
        if (isInitialization) {
            if (isVisibility) {
                onUserVisible();
            } else {
                onUserInvisible();
            }
        }
    }
}
