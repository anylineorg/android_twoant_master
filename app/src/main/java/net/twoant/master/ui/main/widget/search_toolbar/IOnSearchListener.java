package net.twoant.master.ui.main.widget.search_toolbar;

/**
 * Created by S_Y_H on 2017/3/15.
 * 搜索监听
 */

public interface IOnSearchListener {
    /**
     * 点击搜索图标后，会回调该方法
     *
     * @param type 当前选择的RecycleContraType
     * @param keyword 关键字
     */
    void onSearchListener(String type, String keyword);

}
