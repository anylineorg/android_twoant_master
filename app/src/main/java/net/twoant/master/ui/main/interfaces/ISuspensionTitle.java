package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2017/2/4.
 * 悬浮标题 获取接口
 */

public interface ISuspensionTitle<D> {

    /**
     * 当悬浮标题需要改变，回调该即可
     */
    void changeTitle(int positionD, D data);
}
