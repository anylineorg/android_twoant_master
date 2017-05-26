package net.twoant.master.ui.convenient.widget;

import android.view.View;

import net.twoant.master.ui.main.widget.HistoryPopupWindow;
import net.twoant.master.ui.main.widget.SearchTypePopupWindow;
import net.twoant.master.ui.main.widget.search_toolbar.SearchHelperBase;

/**
 * Created by S_Y_H on 2017/3/17.
 * 便民信息搜索条帮助类
 */

public class ConvenientSearchHelper extends SearchHelperBase {

    private final static String PERSONAL = "个人";
    public final static String SHOP = "商家";

    @Override
    protected SearchTypePopupWindow newSearchTypePopupWindow() {
        return new SearchTypePopupWindow(SHOP, PERSONAL);
    }

    @Override
    protected HistoryPopupWindow newHistoryPopupWindow() {
        return new HistoryPopupWindow(null, "convenient");
    }

    @Override
    protected boolean canSearch(View click, String currentType, String keyword) {
        return true;
    }
}
