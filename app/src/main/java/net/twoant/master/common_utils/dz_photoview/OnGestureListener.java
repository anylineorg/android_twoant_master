package net.twoant.master.common_utils.dz_photoview;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public interface OnGestureListener {

    public void onDrag(float dx, float dy);

    public void onFling(float startX, float startY, float velocityX,
                        float velocityY);

    public void onScale(float scaleFactor, float focusX, float focusY);

}
