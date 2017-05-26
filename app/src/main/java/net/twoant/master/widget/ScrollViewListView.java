package net.twoant.master.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by DZY on 2016/12/16.
 * 佛祖保佑   永无BUG
 */

public class ScrollViewListView extends ListView {

    public ScrollViewListView(Context context) {
        super(context);
    }

    public ScrollViewListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}