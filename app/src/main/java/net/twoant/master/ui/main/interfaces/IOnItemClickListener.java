package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2016/12/18.
 * item 点击回调接口
 */

public interface IOnItemClickListener<T> {

    void onItemClickListener(T t);

    void onAutoClickListener(T t);
}
