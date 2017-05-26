package net.twoant.master.ui.main.adapter.base;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

/**
 * Created by S_Y_H on 2016/12/1.
 * 首页的recyclerView 的适配器 ViewHolder基类
 */

public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    private int mViewType;
    private View mProgressBar;
    private AppCompatTextView mHintInfo;

    public BaseRecyclerViewHolder(View itemView, int viewType) {
        super(itemView);
        this.mViewType = viewType;
        if (viewType == IRecyclerViewConstant.TYPE_REFRESH || viewType == IRecyclerViewConstant.TYPE_LOADING) {
            this.mProgressBar = itemView.findViewById(net.twoant.master.R.id.progress_bar);
            this.mHintInfo = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_hint_info);
        } else {
            initView(itemView, viewType);
        }
    }

    protected void initView(View itemView, int viewType) {}

    public View getProgressBar() {
        return mProgressBar;
    }

    public AppCompatTextView getHintInfo() {
        return mHintInfo;
    }

    public int getViewType() {
        return mViewType;
    }
}
