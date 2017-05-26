package net.twoant.master.common_utils.dz_photoview;

import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public interface GestureDetector {

    public boolean onTouchEvent(MotionEvent ev);

    public boolean isScaling();

    public void setOnGestureListener(OnGestureListener listener);
}
