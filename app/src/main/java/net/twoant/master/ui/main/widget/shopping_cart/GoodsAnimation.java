package net.twoant.master.ui.main.widget.shopping_cart;

import android.animation.Animator;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by S_Y_H on 2017/1/24.
 * 商品动画
 */

public class GoodsAnimation {
    /**
     * 动画层
     */
    private IOnEndAnimListener iEndAnimListener;
    private WeakReference<ViewGroup> mAnimParentLayout;
    private final Handler mHandler;

    /**
     * 定义结束之后的接口
     */
    public interface IOnEndAnimListener {
        /**
         * 动画结束了
         */
        void onGoodsAniaEnd();
    }

    public GoodsAnimation() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                ViewGroup viewGroup;
                if (null != mAnimParentLayout && null != (viewGroup = mAnimParentLayout.get()) &&
                        0 < viewGroup.getChildCount()) {
                    viewGroup.removeAllViews();
                }
                return true;
            }
        });
    }

    public static void hideOrShowSubtract(final View subtract, View addition) {

        if (View.VISIBLE != subtract.getVisibility()) {
            subtract.setTranslationX(addition.getRight() - subtract.getRight());
            subtract.animate().translationX(0).rotation(360).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (subtract.getVisibility() != View.VISIBLE)
                        subtract.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
        } else
            subtract.animate().translationX(addition.getRight() - subtract.getRight()).rotation(360).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (subtract.getVisibility() != View.VISIBLE)
                        subtract.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    subtract.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
    }

    public void setOnEndAnimListener(IOnEndAnimListener listener) {
        iEndAnimListener = listener;
    }

    public void initAnim(View pressView, View cartView) {
        if (pressView == null || cartView == null) {
            return;
        }

        final int[] start_location = new int[2];
        pressView.getLocationInWindow(start_location);
        final int[] end_location = new int[2];
        cartView.getLocationInWindow(end_location);
        // 计算位移
        int endY = end_location[1] - start_location[1];// 动画位移的y坐标
        int width = cartView.getWidth() >> 1;
        int endX = end_location[0] - start_location[0] + width;// 动画位移的X坐标

        // 开始执行动画
        startAnim(addViewToAnimLayout(pressView, start_location), cartView, endX, endY);
    }

    /**
     * 开始动画
     */
    private void startAnim(final View v, final View cart, int endX, int endY) {
        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);
        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setRepeatCount(0);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(300);
        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        //set.setStartOffset(300);
        set.setDuration(300);// 动画的执行时间
        v.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                RotateAnimation rotateAnimation =new RotateAnimation(0f,45f,Animation.RELATIVE_TO_SELF,
                        0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                /*RotateAnimation rotateAnimation = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF,
                        (cart.getWidth()) >> 1, Animation.RELATIVE_TO_SELF, (cart.getHeight()) >> 1);*/
                rotateAnimation.setDuration(300);
                rotateAnimation.setRepeatCount(0);
                cart.startAnimation(rotateAnimation);
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(0, 100);
                if (iEndAnimListener != null) {
                    iEndAnimListener.onGoodsAniaEnd();
                }
            }
        });
    }

    /**
     * @return 执行动画的父布局
     */
    private ViewGroup createParent(View view) {
        ViewGroup animView;
        if (null == mAnimParentLayout || null == (animView = mAnimParentLayout.get())) {
            animView = (ViewGroup) view.getRootView();
            View viewById = animView.findViewById(net.twoant.master.R.id.merchant_goods_anim);
            FrameLayout pointViewParent;
            if (null == viewById) {
                pointViewParent = new FrameLayout(view.getContext());
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                pointViewParent.setLayoutParams(lp);
                pointViewParent.setClickable(false);
                pointViewParent.setId(net.twoant.master.R.id.merchant_goods_anim);
                pointViewParent.setBackgroundResource(android.R.color.transparent);
                animView.addView(pointViewParent);
            } else {
                pointViewParent = (FrameLayout) viewById;
            }
            mAnimParentLayout = new WeakReference<ViewGroup>(pointViewParent);
            return pointViewParent;
        } else {
            return animView;
        }
    }

    /**
     * @param pressView 当前事件View
     * @param location  显示的位置
     */
    private View addViewToAnimLayout(View pressView, int[] location) {
        int pressHeight = pressView.getHeight() >> 1;
        // 小球的图片
        ImageView dotImg = new ImageView(pressView.getContext());
        dotImg.setImageResource(net.twoant.master.R.drawable.goods_anim_point);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = location[0];
        lp.topMargin = location[1] + pressHeight;
        dotImg.setLayoutParams(lp);

        ViewGroup animLayout = createParent(pressView);
        animLayout.addView(dotImg);
        return dotImg;
    }

}
