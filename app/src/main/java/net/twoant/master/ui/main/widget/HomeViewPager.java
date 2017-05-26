package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by S_Y_H on 2017/1/17.
 * 解决滑动冲突
 */

public class HomeViewPager extends ViewPager {

    private boolean mEnable;
    private boolean isFirst = true;
    private boolean isInterceptFirst = true;

    public boolean getEnable() {
        return mEnable;
    }

    public void setEnable(boolean enable) {
        if (enable != mEnable)
            this.mEnable = enable;
    }

    public HomeViewPager(Context context) {
        super(context);
    }

    public HomeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEnable) {
            isFirst = true;
            return super.onTouchEvent(ev);
        }

        if (isFirst) {
            isFirst = false;
            ev.setAction(MotionEvent.ACTION_CANCEL);
            super.onTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEnable) {
            isInterceptFirst = true;
            return super.onInterceptTouchEvent(ev);
        }
        if (isInterceptFirst) {
            isInterceptFirst = false;
            ev.setAction(MotionEvent.ACTION_CANCEL);
            super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
