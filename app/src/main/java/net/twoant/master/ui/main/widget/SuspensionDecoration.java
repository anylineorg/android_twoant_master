package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.main.interfaces.ISuspensionInterface;
import net.twoant.master.ui.main.interfaces.ISuspensionTitle;

/**
 * Created by S_Y_H on 2017/1/24.
 * 悬停的Decoration
 */

public class SuspensionDecoration<D> extends RecyclerView.ItemDecoration {

    private ISuspensionInterface<D> mData;
    private ISuspensionTitle<D> iSuspensionTitle;
    private Paint mPaint;
    private Rect mBounds;//用于存放测量文字Rect

    private int mTitleHeight;//title的高
    private int mTitleBg;
    private int mTitleTextColor;
    private int mPaddingSize;
    private String mTempTag;


    public SuspensionDecoration(Context context, ISuspensionInterface<D> data, @ColorRes int titleColor
            , @ColorRes int titleBg, @DimenRes int paddingSize) {
        mTitleBg = ContextCompat.getColor(context, titleBg);
        mTitleTextColor = ContextCompat.getColor(context, titleColor);
        mData = data;
        iSuspensionTitle = data;
        mPaint = new Paint();
        mBounds = new Rect();
        Resources resources = context.getResources();
        mTitleHeight = resources.getDimensionPixelSize(R.dimen.px_50);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, resources.getDisplayMetrics()));
        mPaint.setAntiAlias(true);
        this.mPaddingSize = resources.getDimensionPixelSize(paddingSize);
    }

    public SuspensionDecoration setData(ISuspensionInterface<D> mData) {
        this.mData = mData;
        return this;
    }

    public SuspensionDecoration setISuspensionTitle(ISuspensionTitle<D> iSuspensionTitle) {
        this.iSuspensionTitle = iSuspensionTitle;
        return this;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft() + mPaddingSize;
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = parent.getChildAdapterPosition(child);

            //pos为1，size为1，1>0? true
            if (position < 0 || mData == null || mData.isEmpty() || position > mData.size() - 1 || !mData.isShowSuspension(position)) {
                continue;
            }
            if (position > -1) {
                if (position == 0) {//等于0肯定要有title的
                    drawTitleArea(c, left, right, child, params, position);

                } else {//其他的通过判断
                    if (null != mData.getSuspensionTag(position) && !mData.getSuspensionTag(position).equals(mData.getSuspensionTag(position - 1))) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        drawTitleArea(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    /**
     * 绘制Title区域背景和文字的方法
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(mTitleBg);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(mTitleTextColor);
        String suspensionTag = mData.getSuspensionTag(position);
        suspensionTag = suspensionTag == null ? "" : suspensionTag;
        mPaint.getTextBounds(suspensionTag, 0, suspensionTag.length(), mBounds);
        c.drawText(suspensionTag, child.getPaddingLeft() + mPaddingSize, child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDrawOver(Canvas c, final RecyclerView parent, RecyclerView.State state) {
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        //pos为1，size为1，1>0? true
        if (mData == null || mData.isEmpty() || pos > mData.size() - 1 || pos < 0 || !mData.isShowSuspension(pos)) {
            return;//越界
        }

        String tag = mData.getSuspensionTag(pos);
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        //View child = parent.getChildAt(pos);
        RecyclerView.ViewHolder adapter = parent.findViewHolderForAdapterPosition(pos);
        if (adapter == null) return;

        View child = adapter.itemView;
        //定义一个flag，Canvas是否位移过的标志
        boolean flag = false;

        if ((pos + 1) < mData.size()) {
            //当前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了
            if (!tag.equals(mTempTag) && iSuspensionTitle != null) {
                mTempTag = tag;
                iSuspensionTitle.changeTitle(pos, mData.getPositionData(pos));
            }
            if (!tag.equals(mData.getSuspensionTag(pos + 1))) {
                //当getTop开始变负，它的绝对值，是第一个可见的Item移出屏幕的距离，
                //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，该开始做悬浮Title的“交换动画”
                if (child.getHeight() + child.getTop() < mTitleHeight) {
                    //每次绘制前 保存当前Canvas状态，
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight);
                }
            }
        }
        mPaint.setColor(mTitleBg);
        c.drawRect(parent.getPaddingLeft() + mPaddingSize, parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(mTitleTextColor);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft() + mPaddingSize,
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
        if (flag)
            c.restore();//恢复画布到之前保存的状态
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super里会先设置0 0 0 0
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        Log.e("getItemOffsets", "getItemOffsets: = " + position + " mData.size() = " + mData.size());

        if (mData == null || mData.isEmpty() || position > mData.size() - 1) {
            return;
        }
        if (position > -1) {
            //等于0肯定要有title的,
            if (mData.isShowSuspension(position)) {
                if (position == 0) {
                    outRect.set(0, mTitleHeight, 0, 0);
                } else {
                    String suspensionTag = mData.getSuspensionTag(position);
                    if (null != suspensionTag && !suspensionTag.equals(mData.getSuspensionTag(position - 1))) {
                        //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                        outRect.set(0, mTitleHeight, 0, 0);
                    }
                }
            }
        }
    }

}
