package com.hyphenate.easeui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by S_Y_H on 2017/3/5.
 * 主要为了获得，触摸事件的位置
 */

public class CustomListView extends ListView {

    private int mListX;
    private int mListY;

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            mListX = (int) event.getX();
            mListY = (int) event.getY();
        }
        return super.dispatchTouchEvent(event);
    }

    public int getListX() {
        return mListX;
    }

    public int getListY() {
        return mListY;
    }
}
