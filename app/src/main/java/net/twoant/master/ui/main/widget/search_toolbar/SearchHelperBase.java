package net.twoant.master.ui.main.widget.search_toolbar;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.interfaces.ISaveRestoreInstance;
import net.twoant.master.ui.main.widget.HistoryPopupWindow;
import net.twoant.master.ui.main.widget.SearchTypePopupWindow;

/**
 * Created by S_Y_H on 2017/3/16.
 * 搜索帮助类，用于搜索Toolbar
 */

public abstract class SearchHelperBase implements SearchToolbarLayout.ISearchToolbarListener, ISaveRestoreInstance<Bundle>,
        TextView.OnEditorActionListener, HistoryPopupWindow.IOnItemClickListener, SearchTypePopupWindow.IOnTypeClickListener {

    protected TextView mTypeTextView;
    protected EditText mSearchEditText;
    private IOnSearchListener iOnSearchListener;
    protected HistoryPopupWindow mHistoryPopupWindow;
    protected SearchTypePopupWindow mSearchTypePopupWindow;

    /**
     * 设置搜索监听
     */
    public void setOnSearchListener(IOnSearchListener iOnSearchListener) {
        this.iOnSearchListener = iOnSearchListener;
    }

    protected abstract SearchTypePopupWindow newSearchTypePopupWindow();

    protected abstract HistoryPopupWindow newHistoryPopupWindow();

    /**
     * 子类实现该方法， 父类会在每次需要进行搜索的时候调用该方法， true 进行搜索调用，false 本次搜索无效
     */
    protected abstract boolean canSearch(View click, String currentType, String keyword);

    protected SearchHelperBase() {
        this.mSearchTypePopupWindow = newSearchTypePopupWindow();
        this.mHistoryPopupWindow = newHistoryPopupWindow();
    }

    @CallSuper
    void initData(EditText editText, TextView textView) {
        this.mTypeTextView = textView;
        this.mSearchEditText = editText;
        this.mTypeTextView.setText(mSearchTypePopupWindow.getCurrentType());
        //输入法回车监听
        mSearchEditText.setOnEditorActionListener(this);
        mHistoryPopupWindow.setEditText(editText);
        //点击搜索按钮
        mHistoryPopupWindow.setOnItemClickListener(this);
        //分类点击事件
        mSearchTypePopupWindow.setOnTypeClickListener(this);
    }

    protected void onResume() {
        if (null != mTypeTextView && null != mSearchTypePopupWindow) {
            mTypeTextView.setText(mSearchTypePopupWindow.getCurrentType());
        }
    }

    void onDestroy() {
        cleanState();
    }

    void cleanFocus() {
        cleanFocus(mSearchEditText);
    }

    /**
     * 清除状态
     */
    private void cleanState() {
        if (null != mHistoryPopupWindow) {
            mHistoryPopupWindow.saveUserHistoryData();
            mHistoryPopupWindow.reset();
            mHistoryPopupWindow.setEditText(null);
            mHistoryPopupWindow.setOnItemClickListener(null);
            mHistoryPopupWindow = null;
        }

        if (null != mSearchTypePopupWindow) {
            mSearchTypePopupWindow.dismiss();
            mSearchTypePopupWindow.setOnTypeClickListener(null);
            mSearchTypePopupWindow = null;
        }
    }

    /**
     * @param view 焦点view
     */
    private void searchClick(View view, String keyword) {
        cleanFocus(view);
        view.clearFocus();

        String currentType = mSearchTypePopupWindow.getCurrentType();
        if (iOnSearchListener != null && !TextUtils.isEmpty(keyword)) {
            if (!canSearch(view, currentType, keyword)) {
                return;
            }
            mHistoryPopupWindow.addHistoryData(keyword);
            iOnSearchListener.onSearchListener(currentType, keyword);
        }
    }

    /**
     * 清除搜索框 焦点
     */
    private static void cleanFocus(View view) {
        if (view != null && view.hasFocus()) {
            MainActivity.closeIME(false, view);
            view.clearFocus();

        }
    }

    @Override
    @CallSuper
    public void onSearchBtnClickListener(View view) {
        if (null == mSearchTypePopupWindow || null == mHistoryPopupWindow || null == mSearchEditText) {
            return;
        }
        searchClick(mSearchEditText, mSearchEditText.getText().toString().trim());
    }

    @Override
    @CallSuper
    public void onSearchTypeClickListener(View view) {
        if (null != mSearchTypePopupWindow) {
            mSearchTypePopupWindow.show(view);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (null != mHistoryPopupWindow) {
            mHistoryPopupWindow.onSaveInstanceState(bundle);
        }
        if (null != mSearchTypePopupWindow) {
            mSearchTypePopupWindow.onSaveInstanceState(bundle);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        if (null != mHistoryPopupWindow) {
            mHistoryPopupWindow.onRestoreInstanceState(bundle);
        }
        if (null != mSearchTypePopupWindow) {
            mSearchTypePopupWindow.onRestoreInstanceState(bundle);
        }
    }

    /**
     * 回车键监听
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    searchClick(v, mSearchEditText.getText().toString().trim());
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    /**
     * 分类点击
     */
    @Override
    public void onTypeClickListener(SearchTypePopupWindow popupWindow, View view, String type, String[] types) {
        if (null != SearchHelperBase.this.mTypeTextView) {
            SearchHelperBase.this.mTypeTextView.setText(type);
        }
    }

    /**
     * 清除历史记录
     */
    @Override
    public void onCleanClickListener(View view) {

    }

    /**
     * 点击搜索按钮监听
     */
    @Override
    public void onItemClickListener(View view, String content) {
        if (null != mSearchEditText) {
            mSearchEditText.setText(content);
        }
        cleanFocus(mSearchEditText);
        searchClick(view, content);
    }
}
