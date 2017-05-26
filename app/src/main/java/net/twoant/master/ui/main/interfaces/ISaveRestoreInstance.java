package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2017/3/16.
 * 保存、恢复实例
 */

public interface ISaveRestoreInstance<T> {

    /**
     * 保存实例状态
     */
    void onSaveInstanceState(T t);

    /**
     * 恢复保存的状态
     */
    void onRestoreInstanceState(T t);
}
