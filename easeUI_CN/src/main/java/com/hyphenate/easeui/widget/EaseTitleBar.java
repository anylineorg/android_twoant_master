package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * title bar
 */
public class EaseTitleBar extends FrameLayout {

    protected AppCompatImageButton mBtnLeft;//最左边的按钮
    protected AppCompatImageButton mBtnRight;//最右边的按钮
    protected AppCompatTextView mTitleTextView;//中间的标题

    public EaseTitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EaseTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseTitleBar(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mBtnLeft = new AppCompatImageButton(context, null,
                android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
        LayoutParams left = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START);
        mBtnLeft.setId(com.hyphenate.easeui.R.id.left_btn_tool_bar);
        mBtnLeft.setLayoutParams(left);

        mBtnRight = new AppCompatImageButton(context, null,
                android.support.v7.appcompat.R.attr.toolbarNavigationButtonStyle);
        LayoutParams right = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.END);
        right.gravity = GravityCompat.END | (Gravity.CENTER_VERTICAL & Gravity.VERTICAL_GRAVITY_MASK);
        mBtnRight.setId(com.hyphenate.easeui.R.id.right_btn_tool_bar);
        mBtnRight.setLayoutParams(right);

        mTitleTextView = new AppCompatTextView(context);
        LayoutParams center = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mTitleTextView.setLayoutParams(center);
        mTitleTextView.setSingleLine();
        mTitleTextView.setTextSize(18);//18sp
        mTitleTextView.setTextColor(Color.WHITE);//懒得改了，现在只是白色，如果后来改需求了，自己定义吧
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);

        parseStyle(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.addView(mBtnLeft);
        this.addView(mTitleTextView);
        this.addView(mBtnRight);
    }

    private void parseStyle(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, com.hyphenate.easeui.R.styleable.EaseTitleBar);
            String title = ta.getString(com.hyphenate.easeui.R.styleable.EaseTitleBar_titleBarTitle);
            mTitleTextView.setText(title);

            Drawable leftDrawable = ta.getDrawable(com.hyphenate.easeui.R.styleable.EaseTitleBar_titleBarLeftImage);
            if (null != leftDrawable) {
                mBtnLeft.setImageDrawable(leftDrawable);
            }
            Drawable rightDrawable = ta.getDrawable(com.hyphenate.easeui.R.styleable.EaseTitleBar_titleBarRightImage);
            if (null != rightDrawable) {
                mBtnRight.setImageDrawable(rightDrawable);
            }

            Drawable background = ta.getDrawable(com.hyphenate.easeui.R.styleable.EaseTitleBar_titleBarBackground);
            if (null != background) {
                setBackground(background);
            } else {
                setBackgroundResource(com.hyphenate.easeui.R.color.common_top_bar_primary);
            }
            ta.recycle();
        }
    }

    public void setLeftImageResource(int resId) {
        mBtnLeft.setImageResource(resId);
    }

    public void setRightImageResource(int resId) {
        mBtnRight.setImageResource(resId);
    }

    public void setLeftLayoutClickListener(OnClickListener listener) {
        mBtnLeft.setOnClickListener(listener);
    }

    public void setRightLayoutClickListener(OnClickListener listener) {
        mBtnRight.setOnClickListener(listener);
    }

    public void setLeftLayoutVisibility(int visibility) {
        mBtnLeft.setVisibility(visibility);
    }

    public void setRightLayoutVisibility(int visibility) {
        mBtnRight.setVisibility(visibility);
    }

    public void setTitle(String title) {
        if (null != mTitleTextView) {
            mTitleTextView.setText(title);
        }
    }

    public AppCompatImageButton getLeftLayout() {
        return mBtnLeft;
    }

    public AppCompatImageButton getRightLayout() {
        return mBtnRight;
    }
}
