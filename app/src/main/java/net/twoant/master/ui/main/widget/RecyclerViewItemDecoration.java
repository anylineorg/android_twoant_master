package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2016/12/6.
 * 首页 Recycler View 分割线
 */

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    private int mWidth;
    private int mHeight;
    private Paint mDividerPaint;
    private int mTopDivider;
    private boolean mShowTop;
    private int mPaddingSize;

    public RecyclerViewItemDecoration(Context context, @ColorRes int color, @DimenRes int width, @DimenRes int height) {
        this(context, color, width, height, true, R.dimen.px_2);
    }

    public RecyclerViewItemDecoration(Context context, @ColorRes int color, @DimenRes int width
            , @DimenRes int height, boolean showTop, @DimenRes int topHeight) {
        this(context, color, width, height, showTop, topHeight, R.dimen.px_20);
    }

    public RecyclerViewItemDecoration(Context context, @ColorRes int color, @DimenRes int width
            , @DimenRes int height, boolean showTop, @DimenRes int topHeight, @DimenRes int paddingSize) {
        Resources resources = context.getResources();

        if (width != 0)
            mWidth = resources.getDimensionPixelSize(width);
        if (height != 0)
            mHeight = resources.getDimensionPixelSize(height);
        if (showTop)
            mTopDivider = resources.getDimensionPixelSize(topHeight);
        if (paddingSize != 0) {
            mPaddingSize = resources.getDimensionPixelSize(paddingSize);
        }
        mDividerPaint = new Paint();
        mDividerPaint.setColor(ContextCompat.getColor(context, color));
        mShowTop = showTop;
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft() + mPaddingSize;
        int right = parent.getWidth() - parent.getPaddingRight() - mPaddingSize;
        View view;
        float top, bottom;

        if (childCount > 0 && mShowTop && mHeight != 0) {
            view = parent.getChildAt(0);
            top = view.getTop();
            c.drawRect(left, 0, right, top, mDividerPaint);
        }

        for (int i = 0; i < childCount; ++i) {
            view = parent.getChildAt(i);
            top = view.getBottom();
            bottom = top + mHeight;
            c.drawRect(left, top, right, bottom, mDividerPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int count = parent.getAdapter().getItemCount() - 1;
        if (position == 0) {
            outRect.set(mWidth, mTopDivider, mWidth, mHeight >> 1);
        } else if (count == position) {
            outRect.set(mWidth, mHeight >> 1, mWidth, 0);
        } else {
            outRect.set(mWidth, mHeight >> 1, mWidth, mHeight >> 1);
        }
    }
}
