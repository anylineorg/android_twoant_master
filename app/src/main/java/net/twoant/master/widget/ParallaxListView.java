package net.twoant.master.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.twoant.master.R;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.interfaces.OnPullToRefreshListener;

/**
 * Created by DZY on 2017/2/9.
 * 佛祖保佑   永无BUG
 */
public class ParallaxListView extends ListView {
    private int MAEG_LEFT;
    private ImageView iv_header;
    private ImageView iv_photo;
    private int orignalHeight;
    private int drawableHeight;
    public RelativeLayout.LayoutParams layoutParams;
    public int Val;
    public RotateAnimation ra;
    public OnPullToRefreshListener onPullToRefreshListener;
    private boolean photoRotate = false;

    public ParallaxListView(Context context) {
        super(context);
    }

    public ParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void registOnPullToRefreshListener(OnPullToRefreshListener onPullToRefreshListener){
        this.onPullToRefreshListener = onPullToRefreshListener;
    }

    public void setParallaxImage(ImageView iv_header, final ImageView iv_photo) {
        this.iv_header = iv_header;
        this.iv_photo = iv_photo;
        MAEG_LEFT = CommonUtil.getDimens(R.dimen.px_80);
        // ImageView 初始高度
        orignalHeight = iv_header.getHeight();

        // 图片的原始高度
        drawableHeight = iv_header.getDrawable().getIntrinsicHeight();

        layoutParams = (RelativeLayout.LayoutParams) iv_photo.getLayoutParams();
        layoutParams.setMargins(MAEG_LEFT,(orignalHeight/3)*2,0,0);//4个参数按顺序分别是左上右下
        iv_photo.setLayoutParams(layoutParams);
        ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(600);
        ra.setRepeatCount(Animation.INFINITE);
        ra.setInterpolator(new LinearInterpolator());
        ra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                iv_photo.setRotation(0);
                ToastUtil.showLong("已刷新");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    /**
     * 滑动到ListView两端才会被调用
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // deltaY 竖直方向滑动的瞬时变化量, 顶部下拉为- , 底部上拉为+
        // scrollY 竖直方向的滑动超出的距离, 顶部为-, 底部为+
        // scrollRangeY 竖直方向滑动的范围
        // maxOverScrollY 竖直方向最大的滑动位置
        // isTouchEvent 是否是用户触摸拉动 , true表示用户手指触摸拉动, false 是惯性

//        System.out.println("deltaY: " + deltaY + " scrollY: " + scrollY
//                + " scrollRangeY: " + scrollRangeY + " maxOverScrollY: " + maxOverScrollY
//                + " isTouchEvent: " + isTouchEvent);

        // 顶部下拉, 用户触摸操作
        if(deltaY < 0 && isTouchEvent){
            // deltaY的绝对值, 累加给Header
            int abs = Math.abs(deltaY / 3);
            int newHeight = iv_header.getHeight() + abs;
            Val += abs;

            iv_photo.clearAnimation();

            if(newHeight <= drawableHeight){
                // 让新的值生效
                iv_header.getLayoutParams().height = newHeight;
                iv_header.requestLayout();
                //头像的变动
//                layoutParams.setMargins(MAEG_LEFT,( Math.round(newHeight/2) + Math.abs(deltaY / 3)) ,0,0);
                layoutParams.setMargins(MAEG_LEFT,( Math.round(newHeight/3)*2) ,0,0);
                iv_photo.setLayoutParams(layoutParams);
                iv_photo.setPivotX(iv_photo.getWidth()/2);
                iv_photo.setPivotY(iv_photo.getHeight()/2);//支点在图片中心
                iv_photo.setRotation(Val);//头像旋转
//                System.out.println(Val);
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (Val >= 190) {
                    iv_photo.startAnimation(ra);
                    onPullToRefreshListener.onLoadMore();
                    photoRotate = true;
                }else{
                    //判断头像是否在旋转
                    if (photoRotate) {
                        iv_photo.startAnimation(ra);
                    }else {
                        iv_photo.setRotation(0);
                    }
                }
                Val = 0;
                // 把当前的头布局的高度currentHeight恢复到初始高度orignalHeight
                final int currentHeight = iv_header.getHeight();

                // 300 -> 160
                ValueAnimator animator = ValueAnimator.ofInt(currentHeight, orignalHeight);
                // 动画更新的监听
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        // 0.0 -> 1.0
                        // 获取动画执行过程中的分度值
                        float fraction = animation.getAnimatedFraction();
                        // Integer evaluate = evaluate(fraction, currentHeight, orignalHeight);

                        // 获取中间的值
                        Integer animatedValue = (Integer) animation.getAnimatedValue();
                        layoutParams.setMargins(MAEG_LEFT,(animatedValue/3)*2,0,0);
                        iv_photo.setLayoutParams(layoutParams);
                        // evaluate == animatedValue

                        // 让新的高度值生效
                        iv_header.getLayoutParams().height = animatedValue;
                        iv_header.requestLayout();
                    }
                });
                animator.setInterpolator(new OvershootInterpolator(2));
                animator.setDuration(500);
                animator.start();
                break;

            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }

    public void clearAnimation(){
        iv_photo.clearAnimation();
        iv_photo.setRotation(0);
        photoRotate = false;
    }
}
