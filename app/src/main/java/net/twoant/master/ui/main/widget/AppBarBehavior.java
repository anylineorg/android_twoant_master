package net.twoant.master.ui.main.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import net.twoant.master.common_utils.LogUtils;

import java.lang.reflect.Field;

/**
 * Created by S_Y_H on 2017/2/7.
 * 处理滑动事件
 */

public class AppBarBehavior extends AppBarLayout.Behavior {

    private boolean isScrollTop;
    private AppBarLayout mAppBarLayout;
    private RecyclerView mRecyclerView;

    public AppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AppBarBehavior() {
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        boolean is = super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        mAppBarLayout = child;
        if (!is && consumed && velocityY < 0) {
            isScrollTop = velocityY < -3200;
            if (isTop(target)) {
                isScrollTop = false;
                try {
                    Field downPreScrollRange = child.getClass().getDeclaredField("mDownPreScrollRange");
                    downPreScrollRange.setAccessible(true);
                    int anInt = downPreScrollRange.getInt(child);
                    downPreScrollRange.setInt(child, child.getTotalScrollRange());
                    is = super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, true);
                    downPreScrollRange.setInt(child, anInt);
                } catch (Exception e) {
                    LogUtils.e("AppBarBehavior", e.toString());
                }
            }
        }
        return is;
    }

    private boolean isTop(View target) {
        if (target instanceof RecyclerView) {
            if (mRecyclerView != target) {
                mRecyclerView = (RecyclerView) target;
                mRecyclerView.removeOnScrollListener(mOnScrollListener);
                mRecyclerView.addOnScrollListener(mOnScrollListener);
            }

            RecyclerView.LayoutManager layoutManager = ((RecyclerView) target).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                if (((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (isScrollTop && mAppBarLayout != null) {
                    isScrollTop = false;
                    if (isTop(mRecyclerView)) {
                        mAppBarLayout.setExpanded(true, true);
                    }
                }
            }
        }
    };
}
