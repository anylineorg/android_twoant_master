package net.twoant.master.ui.main.interfaces;

import android.view.View;

import net.twoant.master.ui.main.bean.GoodsItemBean;

import java.util.List;

/**
 * Created by S_Y_H on 2017/4/5.
 * 定义结束之后的接口
 */
public interface IOnDataChangeListener {


    /**
     * @return 购物车View
     */
    View getCartView();

    void onBtnClickListener(View pressView, boolean isAdd);

    /**
     * @param goodsItems 购物车数据
     */
    void onDataChangeListener(List<GoodsItemBean> goodsItems);
}
