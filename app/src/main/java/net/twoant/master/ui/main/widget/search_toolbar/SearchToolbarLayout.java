package net.twoant.master.ui.main.widget.search_toolbar;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatMultiAutoCompleteTextView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2017/3/15.
 * 搜索布局初始化及相关设置
 */

public final class SearchToolbarLayout implements View.OnClickListener {
    private AppCompatImageButton mIvBackNavigation;
    private AppCompatTextView mTvLocationNavigation;
    private AppCompatTextView mTvRightInfoNavigation;
    private AppCompatImageView mIvInfoNavigation;
    private LinearLayoutCompat mLlInformationNavigation;
    private AppCompatTextView mTvSelectSearchOption;
    private AppCompatMultiAutoCompleteTextView mSearchAutoComplete;
    private ISearchToolbarSetter iSearchToolbarSetter;
    private ISearchToolbarListener iSearchToolbarListener;

    interface ISearchToolbarListener {
        void onSearchBtnClickListener(View view);

        void onSearchTypeClickListener(View view);
    }

    void setSearchToolbarListener(ISearchToolbarListener iSearchToolbarListener) {
        this.iSearchToolbarListener = iSearchToolbarListener;
    }

    void setSearchToolbarSetter(ISearchToolbarSetter iSearchToolbarSetter) {
        this.iSearchToolbarSetter = iSearchToolbarSetter;
        initSearchToolbarSetter();
    }

    SearchToolbarLayout(View view) {
        this.mIvBackNavigation = (AppCompatImageButton) view.findViewById(R.id.iv_back_navigation);
        this.mTvLocationNavigation = (AppCompatTextView) view.findViewById(R.id.tv_location_navigation);
        this.mIvInfoNavigation = (AppCompatImageView) view.findViewById(R.id.iv_info_navigation);
        this.mLlInformationNavigation = (LinearLayoutCompat) view.findViewById(R.id.ll_information_navigation);
        this.mTvSelectSearchOption = (AppCompatTextView) view.findViewById(R.id.tv_select_search_option);
        view.findViewById(R.id.ll_drop_down_navigation).setOnClickListener(this);
        view.findViewById(R.id.iv_search_navigation).setOnClickListener(this);
        this.mTvRightInfoNavigation = (AppCompatTextView) view.findViewById(R.id.tv_right_hint_info);
        this.mSearchAutoComplete = (AppCompatMultiAutoCompleteTextView) view.findViewById(R.id.search_auto_complete);
    }

    public AppCompatTextView getTvLocationNavigation() {
        return this.mTvLocationNavigation;
    }

    AppCompatTextView getTvSelectSearchOption() {
        return this.mTvSelectSearchOption;
    }

    AppCompatMultiAutoCompleteTextView getSearchAutoComplete() {
        return this.mSearchAutoComplete;
    }

    /**
     * 设置点击监听
     */
    private void initSearchToolbarSetter() {

        if (null != iSearchToolbarSetter) {
            if (iSearchToolbarSetter.getLeftIcon(mIvBackNavigation)) {
                checkState(mIvBackNavigation, View.VISIBLE);
                mIvBackNavigation.setOnClickListener(this);
            } else {
                mIvBackNavigation.setVisibility(View.GONE);
            }

            if (iSearchToolbarSetter.getLeftText(mTvLocationNavigation)) {
                checkState(mTvLocationNavigation, View.VISIBLE);
                mTvLocationNavigation.setOnClickListener(this);
            } else {
                mTvLocationNavigation.setVisibility(View.GONE);
            }

            boolean hasChild = false;
            if (iSearchToolbarSetter.getRightText(mTvRightInfoNavigation)) {
                checkState(mTvRightInfoNavigation, View.VISIBLE);
                hasChild = true;
            } else {
                mTvRightInfoNavigation.setVisibility(View.GONE);
            }

            if (iSearchToolbarSetter.getRightIcon(mIvInfoNavigation)) {
                checkState(mIvInfoNavigation, View.VISIBLE);
                hasChild = true;
            } else {
                mIvInfoNavigation.setVisibility(View.GONE);
            }
            if (hasChild) {
                mLlInformationNavigation.setOnClickListener(this);
            }
        }
    }

    private static void checkState(View view, int state) {
        if (state != view.getVisibility()) {
            view.setVisibility(state);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //选择位置
            case R.id.tv_location_navigation:
                if (null != iSearchToolbarSetter) {
                    iSearchToolbarSetter.onLeftTextClickListener(view);
                }
                break;
            case R.id.ll_drop_down_navigation:
                if (null != iSearchToolbarListener) {
                    iSearchToolbarListener.onSearchTypeClickListener(view);
                }
                break;
            case R.id.ll_information_navigation:
                if (null != iSearchToolbarSetter) {
                    iSearchToolbarSetter.onRightClickListener(view);
                }
                break;
            //搜索按钮
            case R.id.iv_search_navigation:
                if (null != iSearchToolbarListener) {
                    iSearchToolbarListener.onSearchBtnClickListener(view);
                }
                break;
            //返回键
            case R.id.iv_back_navigation:
                if (null != iSearchToolbarSetter) {
                    iSearchToolbarSetter.onLeftIconClickListener(view);
                }
                break;
        }
    }
}
