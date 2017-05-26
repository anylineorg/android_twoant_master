package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2017/2/5.
 * 循环滚动的TextView
 */

public class MarqueeTextView extends AppCompatTextView {

    private boolean isFocused;

    public MarqueeTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    private void init(Context context, AttributeSet attrs) {
        setLines(1);
        setSingleLine(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextView);
        isFocused = typedArray.getBoolean(R.styleable.MarqueeTextView_startMarquee, false);
        typedArray.recycle();
    }
}
