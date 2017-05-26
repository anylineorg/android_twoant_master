package net.twoant.master.ui.main.widget.search_toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by S_Y_H on 2017/3/15.
 * 搜索toolbar 的配置接口
 */

public interface ISearchToolbarSetter {
    /**
     * 右侧行为图标
     */
    boolean getRightIcon(ImageView view);

    /**
     * 左侧图标
     *
     * @return true 表示已经处理，表示显示
     */
    boolean getLeftIcon(ImageView view);

    /**
     * 右侧图标描述
     * @return true 表示已经处理，表示显示
     */
    boolean getRightText(TextView textView);

    /**
     * 左侧标题
     * @return true 表示已经处理，表示显示
     */
    boolean getLeftText(TextView textView);

    /**
     * 右侧图标被点击
     */
    void onRightClickListener(View view);

    /**
     * 左侧图片被点击
     */
    void onLeftIconClickListener(View view);

    /**
     * 左侧标题被点击
     */
    void onLeftTextClickListener(View view);
}
