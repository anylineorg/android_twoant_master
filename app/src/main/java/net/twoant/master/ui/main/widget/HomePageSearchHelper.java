package net.twoant.master.ui.main.widget;

import android.text.TextUtils;
import android.view.View;

import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.widget.search_toolbar.SearchHelperBase;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/17.
 * 首页的搜索帮助类
 */

public class HomePageSearchHelper extends SearchHelperBase {

    public final static String ACTIVITY = "活动";
    public final static String INFORMATION = "信息";
    public final static String FRIEND = "好友";
    public final static String GOODS = "商品";
    public final static String MERCHANT = "商家";

    private static String sCurrentType;
    private static String[] sType;
    private static String sKeyword;
    private static ArrayList<String> sSearchHistory;


    public HomePageSearchHelper() {
        super();
    }

    @Override
    protected void onResume() {
        if (null != mTypeTextView && !TextUtils.isEmpty(sCurrentType)) {
            mTypeTextView.setText(sCurrentType);
        }
        if (null != mSearchEditText) {
            mSearchEditText.setText(sKeyword);
        }
        if (null != mHistoryPopupWindow && null != sSearchHistory) {
            mHistoryPopupWindow.setUserHistoryData(sSearchHistory);
        }
        if (!TextUtils.isEmpty(sCurrentType) && null != sType && null != mSearchTypePopupWindow
                && mSearchTypePopupWindow.syncData(sCurrentType, sType)
                && null != mTypeTextView) {
            mTypeTextView.setText(sCurrentType);
        }
    }

    public static void logoutClean() {
        sCurrentType = null;
        sKeyword = null;
        sType = null;
        if (null != sSearchHistory) {
            sSearchHistory.clear();
            sSearchHistory = null;
        }
        sSearchHistory = null;
    }

    /**
     * 清除历史记录
     */
    @Override
    public void onCleanClickListener(View view) {
        if (null != sSearchHistory) {
            sSearchHistory.clear();
        }
    }

    @Override
    protected SearchTypePopupWindow newSearchTypePopupWindow() {
        SearchTypePopupWindow searchTypePopupWindow;
        if (null == sType || TextUtils.isEmpty(sCurrentType)) {
            searchTypePopupWindow = new SearchTypePopupWindow(MERCHANT, GOODS, ACTIVITY, FRIEND, INFORMATION);
        } else {
            searchTypePopupWindow = new SearchTypePopupWindow(sCurrentType, sType);
        }
        return searchTypePopupWindow;
    }

    @Override
    protected HistoryPopupWindow newHistoryPopupWindow() {
        return new HistoryPopupWindow(sSearchHistory, "homePage");
    }

    @Override
    protected boolean canSearch(View click, String currentType, String keyword) {
        sCurrentType = currentType;
        sKeyword = keyword;
        if (null == sSearchHistory) {
            sSearchHistory = mHistoryPopupWindow.getUserHistoryData();
            sSearchHistory = null == sSearchHistory ? new ArrayList<String>() : sSearchHistory;

        }
        if (!sSearchHistory.contains(keyword)) {
            sSearchHistory.add(keyword);
        }
        sType = mSearchTypePopupWindow.getTypes();
        if (currentType.equals(FRIEND)) {
            ToastUtil.showShort("功能暂未开放");
            return false;
        }
        return true;
    }
}
