package net.twoant.master.ui.charge.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import net.twoant.master.widget.ScrollHeaderLinearLayout;

public abstract class HeaderViewPagerFragment extends Fragment implements ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer {

    private boolean isCreated;
    private boolean isVisibility;
    private boolean isFirstExecutive = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isCreated = true;
        if (isVisibility && isFirstExecutive) {
            initSubData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisibility = true;
        } else {
            isVisibility = false;
            isCreated = false;
        }
        if (isVisibility && isCreated) {
            initSubData();
        }
    }

    private void initSubData() {
        if (isFirstExecutive) {
            isFirstExecutive = false;
            initComponents();
        } else {
            executeAtVisibility();
        }
    }
    /**
     * 该方法只调用一次
     */
    protected abstract void initComponents();

    /**
     * 该方法只要当前fragment为可见状态 就会被调用
     * 子类可以选择覆写
     */
    protected void executeAtVisibility(){};
}

