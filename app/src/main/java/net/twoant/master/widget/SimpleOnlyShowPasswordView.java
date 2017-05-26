package net.twoant.master.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by S_Y_H on 2017/4/11.
 * 密码输入 黑点view
 */

public class SimpleOnlyShowPasswordView extends View {

    private int mSumLength = 6;
    private int mCurrentLength = 0;
    private int mDefHeight;
    private Paint mPaint;
    private Paint mLinePaint;
    private int mRadiusSize;

    public SimpleOnlyShowPasswordView(Context context) {
        super(context);
        init(context, null);
    }

    public SimpleOnlyShowPasswordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SimpleOnlyShowPasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        mCurrentLength = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int width = MeasureSpec.AT_MOST != MeasureSpec.getMode(widthMeasureSpec) ?
                size : mDefHeight * mSumLength > size ? size : mDefHeight * mSumLength;
        size = MeasureSpec.getSize(heightMeasureSpec);
        int height = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.AT_MOST ?
                size : mDefHeight > size ? size : mDefHeight;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int height = getHeight();
        float lineX = getWidth() * 1.0F / mSumLength;
        float temp;
        for (int i = 1; i < mSumLength; ++i) {
            canvas.drawLine(temp = lineX * i, 0, temp, height, mLinePaint);
        }
        final float pointY = height >> 1;
        final float pointX = lineX / 2;
        for (int i = 0, size = mCurrentLength - 1; i <= size; ++i) {
            canvas.drawCircle(pointX + lineX * i, pointY, mRadiusSize, mPaint);
        }
    }

    /**
     * 添加一个黑点
     */
    public void addPoint() {
        ++mCurrentLength;
        invalidate();
    }

    /**
     * 获取当前下标位置
     */
    public int getCurrentPosition() {
        return mCurrentLength;
    }

    /**
     * 回退一个黑点
     */
    public void backspace() {
        mCurrentLength = 0 > --mCurrentLength ? 0 : mCurrentLength;
        invalidate();
    }

    /**
     * 清除所有黑点
     */
    public void clean() {
        mCurrentLength = 0;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();
        mDefHeight = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_90);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, net.twoant.master.R.styleable.SimpleOnlyShowPasswordView);
            mRadiusSize = typedArray.getDimensionPixelSize(net.twoant.master.R.styleable.SimpleOnlyShowPasswordView_pointSize, resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_20));
            mLinePaint.setColor(typedArray.getColor(net.twoant.master.R.styleable.SimpleOnlyShowPasswordView_dividerLineColor, ContextCompat.getColor(context, net.twoant.master.R.color.dividerLineColor)));
            mPaint.setColor(typedArray.getColor(net.twoant.master.R.styleable.SimpleOnlyShowPasswordView_pointColor, ContextCompat.getColor(context, net.twoant.master.R.color.principalTitleTextColor)));
            mLinePaint.setStrokeWidth(typedArray.getDimensionPixelSize(net.twoant.master.R.styleable.SimpleOnlyShowPasswordView_dividerLineWidth, resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_2)));
            typedArray.recycle();
        } else {
            mRadiusSize = resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_20);
            mLinePaint.setColor(ContextCompat.getColor(context, net.twoant.master.R.color.dividerLineColor));
            mPaint.setColor(ContextCompat.getColor(context, net.twoant.master.R.color.principalTitleTextColor));
            mLinePaint.setStrokeWidth(resources.getDimensionPixelSize(net.twoant.master.R.dimen.px_2));
        }
    }
}

