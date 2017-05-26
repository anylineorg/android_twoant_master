package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2017/1/24.
 * 分类悬停的接口
 */

public interface ISuspensionInterface<B> extends ISuspensionTitle<B> {

    /**
     * 是否为空
     */
    boolean isEmpty();

    /**
     * title 数量
     */
    int size();

    /**
     * 是否需要显示悬停title
     */
    boolean isShowSuspension(int position);

    /**
     * 悬停的title
     */
    String getSuspensionTag(int position);

    /**
     * 获取指定位置上的数据
     */
    B getPositionData(int position);

}
