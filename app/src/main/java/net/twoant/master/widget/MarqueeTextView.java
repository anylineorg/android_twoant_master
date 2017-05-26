package net.twoant.master.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by DZY on 2016/12/15.
 * 佛祖保佑   永无BUG
 */

public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context con) {
        super(con);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}