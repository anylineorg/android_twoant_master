package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by S_Y_H on 2017/1/19.
 * 调整 灵敏度
 */

public class MySwipeRefreshLayout extends SwipeRefreshLayout {

    private float y;
    private float x;
    private int mScaledTouchSlop;

    public MySwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        try {

            int touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            Field mTouchSlop = getClass().getSuperclass().getDeclaredField("mTouchSlop");
            mTouchSlop.setAccessible(true);
            mTouchSlop.setInt(this, touchSlop * 6);
        } catch (Exception e) {
            Log.e("MySwipeRefreshLayout", "Exception: " + e.toString());

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canIntercept(ev) && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return canIntercept(ev) && super.onInterceptTouchEvent(ev);
    }

    private boolean canIntercept(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float v1 = Math.abs(event.getX() - x) * 4 - Math.abs(event.getY() - y);
                if (v1 > mScaledTouchSlop) {
                    return false;
                }
                break;
        }
        return true;
    }
}
