package net.twoant.master.ui.main.adapter.base;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.twoant.master.R;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.twoant.master.R.string.dialog_loading;

/**
 * Created by S_Y_H on 2017/1/24.
 * recyclerView适配器基类
 */

public abstract class BaseRecyclerAdapter<D, E extends BaseRecyclerViewHolder> extends
        RecyclerView.Adapter<BaseRecyclerViewHolder> {

    protected Activity mContext;
    protected List<D> mDataList;

    private RecyclerView mRecyclerView;

    /**
     * 配置网络，当上一个网络请求未完成，禁止请求下一条
     */
    private boolean mConfigNetwork;

    /**
     * 加载、刷新界面 是否处于可见状态
     */
    private boolean isLoadingOrRefreshVisibility;

    /**
     * 上次 为设置成功的提示信息 ，用于加载和刷新提示。
     */
    protected CharSequence mTempInfo;

    /**
     * 是否为刷新
     */
    private boolean isRefresh;

    private RecyclerView.OnScrollListener mOnScrollListener;

    protected ILoadingData iLoadingData;

    /**
     * 是否显示加载中的提示 （默认显示）
     */
    private boolean isShowLoadingHint = true;

    public void refreshData() {
        if (iLoadingData instanceof IRefreshData) {
            ((IRefreshData) iLoadingData).refreshData();
        }
        if (null != this.mDataList) {
            mDataList.clear();
            mDataList.add(null);
            this.notifyDataSetChanged();
        }
    }

    public boolean removePosition(int position) {
        if (null != mDataList && !mDataList.isEmpty() && 0 <= position && mDataList.size() > position) {
            mDataList.remove(position);
            this.notifyItemRemoved(position);
            return true;
        }
        return false;
    }

    public interface IRefreshData extends ILoadingData {
        void refreshData();
    }

    /**
     * 用于加载数据
     */
    public interface ILoadingData {
        /**
         * +
         * 加载数据，进行网络请求
         */
        void getData();

        /**
         * 是否可以继续加载数据
         *
         * @return true 有数据可以加载
         */
        boolean canLoadingData();

    }

    protected BaseRecyclerAdapter(Activity context, @Nullable ILoadingData iLoadingData) {
        this.iLoadingData = iLoadingData;
        this.mContext = context;
        mDataList = new ArrayList<>();
        mDataList.add(null);
    }

    protected BaseRecyclerAdapter(Activity context) {
        this.mContext = context;
    }

    public void setILoadingData(ILoadingData iLoadingData) {
        this.iLoadingData = iLoadingData;
    }

    protected abstract void onBindViewHolder(E holder, int viewType, int position);

    protected abstract BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType);

    protected int getItemViewType(int position, D data) {
        return 0;
    }

    public List<D> getDataList() {
        return mDataList;
    }

    /**
     * 删除数组重复项
     *
     * @param nonNull    非空集合
     * @param resultBean 集合
     * @param isRefresh  处于刷新
     * @return true 使用子类定的规则
     */
    protected List<D> subRemoveMethod(boolean nonNull, List<D> resultBean, boolean isRefresh) {
        return resultBean;
    }

    public boolean isLoadingOrRefreshVisibility() {
        return this.isLoadingOrRefreshVisibility;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        D d = mDataList.get(position);
        if (isShowLoadingHint && iLoadingData != null) {
            if (1 == mDataList.size()) {
                return IRecyclerViewConstant.TYPE_REFRESH;
            }

            if (d == null && position == mDataList.size() - 1)
                return IRecyclerViewConstant.TYPE_LOADING;
        }
        return getItemViewType(position, d);
    }

    /**
     * @param data      数据集合
     * @param isRefresh 是否是刷新
     */
    public List<D> setDataBean(List<D> data, boolean isRefresh) {
        data = subRemoveMethod(data != null, data, isRefresh);
        mConfigNetwork = false;
        if (isRefresh) {
            this.mDataList = data;
            this.mDataList.add(null);
            this.notifyDataSetChanged();
        } else {
            if (data != null) {
                int size = mDataList.size() - 1;
                this.mDataList.addAll(size, data);
                this.notifyItemRangeInserted(size, data.size());
            }
        }
        return data;
    }

    /**
     * @param data 数据体
     */
    @SafeVarargs
    public final void setDataBean(boolean isShowLoadingHint, D... data) {
        mConfigNetwork = false;
        this.isShowLoadingHint = isShowLoadingHint;
        this.mDataList = new ArrayList<>(Arrays.asList(data));
        this.notifyDataSetChanged();
    }

    public List<D> setDataBean(List<D> data) {
        data = subRemoveMethod(null != data, data, isRefresh);
        mConfigNetwork = false;
        this.mDataList = data;
        this.notifyDataSetChanged();
        return data;
    }

    public List<D> setDataBean(boolean isShowLoadingHint, List<D> data) {
        this.isShowLoadingHint = isShowLoadingHint;
        data = subRemoveMethod(null != data, data, isRefresh);
        mConfigNetwork = false;
        this.mDataList = data;
        this.notifyDataSetChanged();
        return data;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case IRecyclerViewConstant.TYPE_REFRESH:
            case IRecyclerViewConstant.TYPE_LOADING:
                return new BaseRecyclerViewHolder(layoutInflater.inflate(R.layout.yh_item_transparent_loading, parent, false), viewType);
            default:
                return onCreateViewHolder(layoutInflater, parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        int viewType = holder.getViewType();
        switch (viewType) {
            case IRecyclerViewConstant.TYPE_REFRESH:
                initRefreshLoadingData(holder);
                isRefresh = true;
                break;
            case IRecyclerViewConstant.TYPE_LOADING:
                initRefreshLoadingData(holder);
                isRefresh = false;
                break;
            default:
                onBindViewHolder((E) holder, viewType, position);
                break;
        }
    }

    private void initRefreshLoadingData(BaseRecyclerViewHolder holder) {
//        holder.itemView.setOnClickListener(this);
        View progressBar = holder.getProgressBar();
        AppCompatTextView hintInfo = holder.getHintInfo();
        if (progressBar == null || hintInfo == null) {
            return;
        }

        if (mTempInfo != null) {
            if (progressBar.getVisibility() != View.GONE) {
                progressBar.setVisibility(View.GONE);
            }
            hintInfo.setText(mTempInfo);
            mTempInfo = null;
        } else if (null != iLoadingData && !iLoadingData.canLoadingData()) {
            if (progressBar.getVisibility() != View.GONE) {
                progressBar.setVisibility(View.GONE);
            }
            hintInfo.setText("没有更多");
        } else {
            if (null != iLoadingData) {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                hintInfo.setText(R.string.dialog_loading);
                iLoadingData.getData();
                mConfigNetwork = true;
            } else {
                if (progressBar.getVisibility() != View.GONE) {
                    progressBar.setVisibility(View.GONE);
                }
                hintInfo.setText("没有更多");
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        if (mOnScrollListener == null) {
            mOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ImageLoader.onResume(mContext);
                    } else {
                        ImageLoader.onPause(mContext);
                    }
                }
            };
        }
        if (mRecyclerView != null) {
            this.mRecyclerView.addOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mRecyclerView != null) {
            this.mRecyclerView.removeOnScrollListener(mOnScrollListener);
            this.mRecyclerView = null;
        }
    }

    /**
     * 显示加载错误信息
     *
     * @param sequence 错误信息
     */
    public void showErrorLoadingHint(CharSequence sequence) {
        mConfigNetwork = false;
        if (!showLoadingHint(false, sequence)) {
            mTempInfo = sequence;
        }
    }

    /**
     * 指定位置是否可见，若无可见，则返回对应的view
     */
    public View getVisibilityViewAtPosition(int position) {
        if (null != mRecyclerView && mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int first = manager.findFirstVisibleItemPosition();
            int last = manager.findLastVisibleItemPosition();
            if (position >= first || position <= last) {
                return manager.findViewByPosition(position);
            }
        }
        return null;
    }

    /**
     * 获取指定位置上的 ViewHolder
     */
    public E getVisibilityViewHolder(int position) {
        View view = getVisibilityViewAtPosition(position);
        return null == view ? null : (E) mRecyclerView.getChildViewHolder(view);
    }

    /**
     * 重置刷新或加载显示状态
     *
     * @param view view
     */
    protected static void resetState(ViewGroup view) {
        if (view.getChildCount() != 2) {
            return;
        }

        View progressBar = view.getChildAt(0);
        if (progressBar instanceof ProgressBar && progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar = view.getChildAt(1);
            if (progressBar instanceof AppCompatTextView) {
                ((AppCompatTextView) progressBar).setText(dialog_loading);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseRecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //判断 刷新、加载 是否处于可见
        if (holder.getViewType() == IRecyclerViewConstant.TYPE_LOADING
                || holder.getViewType() == IRecyclerViewConstant.TYPE_REFRESH) {
            isLoadingOrRefreshVisibility = true;
            if (mTempInfo != null) {
                showLoadingHint(false, mTempInfo);
                mTempInfo = null;
            }
        }

        if (!mConfigNetwork && null != iLoadingData && isLoadingOrRefreshVisibility && iLoadingData.canLoadingData()) {
            iLoadingData.getData();
            mConfigNetwork = true;
        }
    }

    @Override
    public void onViewDetachedFromWindow(BaseRecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.getViewType() == IRecyclerViewConstant.TYPE_LOADING
                || holder.getViewType() == IRecyclerViewConstant.TYPE_REFRESH) {
            isLoadingOrRefreshVisibility = false;
        }
    }

    /**
     * 设置显示的信息
     *
     * @param showProgress true显示progressBar
     * @param sequence     提示信息
     * @return false 未处理成功， true 更改成功
     */

    private boolean showLoadingHint(boolean showProgress, CharSequence sequence) {
        if (mRecyclerView != null) {


            int index = mRecyclerView.getChildCount() - 1;

            if (index < 0) {
                return false;
            }

            RecyclerView.ViewHolder viewHolderForAdapterPosition = mRecyclerView.getChildViewHolder(mRecyclerView.getChildAt(index));

            if (viewHolderForAdapterPosition instanceof BaseRecyclerViewHolder) {
                BaseRecyclerViewHolder viewHolder = (BaseRecyclerViewHolder) viewHolderForAdapterPosition;
                int visibility = showProgress ? View.VISIBLE : View.GONE;
                View progressBar = viewHolder.getProgressBar();
                //为空说明被回收了
                if (progressBar == null)
                    return false;
                if (visibility != progressBar.getVisibility()) {
                    progressBar.setVisibility(visibility);
                }
                AppCompatTextView hintInfo = viewHolder.getHintInfo();
                if (hintInfo != null) {
                    hintInfo.setText(sequence);
                }
                return true;
            }

        }
        return false;
    }
}
