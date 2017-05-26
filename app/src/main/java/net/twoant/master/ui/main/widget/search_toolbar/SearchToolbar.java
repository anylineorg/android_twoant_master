package net.twoant.master.ui.main.widget.search_toolbar;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import net.twoant.master.R;
import net.twoant.master.ui.main.interfaces.ISaveRestoreInstance;

import java.util.Stack;


/**
 * Created by S_Y_H on 2016/12/6.
 * 带搜索框的Toolbar
 */

public class SearchToolbar extends FrameLayout {

    private SearchHelperBase mSearchHelperBase;
    private SearchToolbarLayout mSearchToolbarLayout;
    private Stack<ISaveRestoreInstance<Bundle>> mSaveRestoreStack;


    public SearchToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public SearchToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SearchToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attr) {
        View view = LayoutInflater.from(context).inflate(R.layout.yh_include_navigation_action_bar, this, true);
        mSearchToolbarLayout = new SearchToolbarLayout(view);
        setSaveEnabled(true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (null != mSaveRestoreStack) {
            SavedState savedState = new SavedState(super.onSaveInstanceState());
            Bundle bundle = new Bundle();
            for (ISaveRestoreInstance<Bundle> iSaveRestoreInstance : mSaveRestoreStack) {
                iSaveRestoreInstance.onSaveInstanceState(bundle);
            }
            savedState.mBundle = bundle;
            return savedState;
        }
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (null != mSaveRestoreStack && state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            super.onRestoreInstanceState(savedState.getSuperState());
            for (ISaveRestoreInstance<Bundle> iSaveRestoreInstance : mSaveRestoreStack) {
                iSaveRestoreInstance.onRestoreInstanceState(savedState.mBundle);
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @SuppressWarnings("unchecked")
    public void setSearchHelperBase(ISearchToolbarSetter iSearchToolbarSetter, SearchHelperBase searchHelperBase) {
        this.mSearchHelperBase = searchHelperBase;
        this.mSearchToolbarLayout.setSearchToolbarListener(this.mSearchHelperBase);
        this.mSearchToolbarLayout.setSearchToolbarSetter(iSearchToolbarSetter);
        this.mSearchHelperBase.initData(mSearchToolbarLayout.getSearchAutoComplete(), mSearchToolbarLayout.getTvSelectSearchOption());
        if (null != searchHelperBase) {
            addSaveRestoreInstances(searchHelperBase);
        }
        if (iSearchToolbarSetter instanceof ISaveRestoreInstance) {
            addSaveRestoreInstances((ISaveRestoreInstance<Bundle>) iSearchToolbarSetter);
        }
    }


    /**
     * 获取布局组件
     */
    public SearchToolbarLayout getSearchToolbarLayout() {
        return this.mSearchToolbarLayout;
    }

    /**
     * 测试发现，搜索和 首页的选择不统一，因此在 onResume 进行设置统一。
     */
    public void onResume() {
        if (null != mSearchHelperBase) {
            mSearchHelperBase.onResume();
        }
    }

    /**
     * 防止内存泄漏，不用时调用
     */
    public void onDestroy() {
        if (null != mSearchHelperBase) {
            mSearchHelperBase.onDestroy();
        }

        if (null != mSaveRestoreStack) {
            mSaveRestoreStack.clear();
        }
    }

    public void cleanFocus() {
        if (null != mSearchHelperBase) {
            mSearchHelperBase.cleanFocus();
        }
    }

    private void addSaveRestoreInstances(ISaveRestoreInstance<Bundle> iSaveRestoreInstances) {
        if (null == mSaveRestoreStack) {
            mSaveRestoreStack = new Stack<>();
        }
        mSaveRestoreStack.add(iSaveRestoreInstances);
    }

    private static class SavedState extends BaseSavedState {

        private Bundle mBundle;

        public SavedState(Parcel source) {
            super(source);
            mBundle = source.readBundle(getClass().getClassLoader());
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBundle(mBundle);
        }
    }

}
