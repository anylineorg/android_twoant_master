package net.twoant.master.ui.main.adapter.base;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.interfaces.IRecyclerController;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.interfaces.IStartRequestNetwork;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;

import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2016/12/1.
 * 首页的RecyclerView 适配器基类
 */

public class BaseRecyclerNetworkAdapter extends RecyclerView.Adapter<BaseRecyclerViewHolder> implements IStartRequestNetwork, View.OnClickListener {

    protected IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> iRecyclerController;
    private RecyclerView mRecyclerView;
    protected HomePagerHttpControl mHomePagerHttpControl;
    private Object mFragmentOrActivity;

    /**
     * 每一次网络请求的id
     * 用一个静态的，防止有出现重复的id
     */
    private static int sRequestId;

    /**
     * 配置网络，当上一个网络请求未完成，禁止请求下一条
     */
    protected boolean mConfigNetwork;

    /**
     * 加载、刷新界面 是否处于可见状态
     */
    private boolean isLoadingOrRefreshVisibility;
    /**
     * 类型
     */
    protected int mType;

    /**
     * 分类
     */
    protected int mCategory;

    /**
     * 上次 为设置成功的提示信息 ，用于加载和刷新提示。
     */
    private CharSequence mTempInfo;

    /**
     * 错误提示
     */
    private String mErrorHintInfo;

    /**
     * 加载完成提示
     */
    private String mFinishHintInfo;

    /**
     * 刷新提示信息
     */
    private String mRefreshHintInfo;

    /**
     * 加载中提示
     */
    private String mLoadingHintInfo;

    /**
     * 初始化配置选项
     */
    private void initConfig(Context context, Object fragmentOrActivity, IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> iRecyclerController) {

        this.mFragmentOrActivity = fragmentOrActivity;
        this.mType = this.hashCode();
        this.iRecyclerController = iRecyclerController;

        if (null == context) {
            context = getActivity();
            if (null == context) {
                context = AiSouAppInfoModel.getAppContext();
            }
        }

        if (null != context) {
            Resources resources = context.getResources();
            mErrorHintInfo = resources.getString(R.string.dialog_fail);
            mFinishHintInfo = resources.getString(R.string.dialog_finish);
            mRefreshHintInfo = resources.getString(R.string.dialog_refresh);
            mLoadingHintInfo = resources.getString(R.string.dialog_loading);
        }

        if (sRequestId - 10000 < Integer.MIN_VALUE) {
            sRequestId = 0;
        }

        if (null != iRecyclerController) {
            this.iRecyclerController.bindingAdapter(this);
        }
    }

    /**
     * 设置提示信息
     *
     * @param loading 加载中提示
     * @param finish  加载结束提示
     * @param refresh 刷新提示
     * @param error   错误提示
     */
    public void setHintInfo(@Nullable String loading, @Nullable String finish, @Nullable String refresh, @Nullable String error) {
        if (!TextUtils.isEmpty(loading)) {
            this.mLoadingHintInfo = loading;
        }
        if (!TextUtils.isEmpty(finish)) {
            this.mFinishHintInfo = finish;
        }
        if (!TextUtils.isEmpty(refresh)) {
            this.mRefreshHintInfo = refresh;
        }
        if (!TextUtils.isEmpty(error)) {
            this.mErrorHintInfo = error;
        }
    }

    /**
     * 是否为刷新
     */
    protected boolean isRefresh;

    /**
     * 状态码
     */
    public int mStateCode;

    private RecyclerView.OnScrollListener mOnScrollListener;

    /**
     * 加载数据完成监听
     */
    private IOnDataLoadingFinishListener iOnDataLoadingFinishListener;

    public BaseRecyclerNetworkAdapter(Activity activity, IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> isRecycler) {
        initConfig(AiSouAppInfoModel.getAppContext(), activity, isRecycler);
    }

    public BaseRecyclerNetworkAdapter(Fragment fragment, IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> isRecycler) {
        initConfig(AiSouAppInfoModel.getAppContext(), fragment, isRecycler);
    }

    public void onPause() {
        if (null != iRecyclerController) {
            iRecyclerController.onPause();
        }
    }

    Object getFragmentOrActivity() {
        return this.mFragmentOrActivity;
    }

    public IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> getIRecyclerController() {
        return this.iRecyclerController;
    }

    /**
     * 对应Activity的生命周期
     */
    @CallSuper
    public void onDestroy() {
        iOnDataLoadingFinishListener = null;

        if (mHomePagerHttpControl != null) {
            mHomePagerHttpControl.cancelRequest(this);
        }

        if (null != iRecyclerController) {
            iRecyclerController.onDestroy();
        }

        mHomePagerHttpControl = null;
        mRecyclerView = null;
        mFragmentOrActivity = null;
    }

    public int getType() {
        return this.mType;
    }

    @Nullable
    public Activity getActivity() {
        if (mFragmentOrActivity instanceof Activity) {
            return (Activity) mFragmentOrActivity;
        } else if (mFragmentOrActivity instanceof Fragment) {
            return ((Fragment) mFragmentOrActivity).getActivity();
        } else if (mFragmentOrActivity instanceof android.app.Fragment) {
            return ((android.app.Fragment) mFragmentOrActivity).getActivity();
        }
        return null;
    }

    /**
     * @param code 设置状态码， 用来处理相同布局，不同显示
     */
    public void setStateCode(int code) {
        this.mStateCode = code;
    }

    /**
     * 刷新数据
     */
    public void refreshData() {
        if (canLoadingDataFormNetwork()) {
            if (null != mHomePagerHttpControl) {
                mHomePagerHttpControl.cancelRequest(this);
            }
            isRefresh = true;
            mConfigNetwork = false;
            iRecyclerController.refreshData(null);
            showLoadingHint(true, mRefreshHintInfo);
            getDataFormNetwork(this);
        } else if (null != iOnDataLoadingFinishListener) {
            iOnDataLoadingFinishListener.onDataLoadingFinishListener();
        }
    }


    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case IRecyclerViewConstant.TYPE_REFRESH:
            case IRecyclerViewConstant.TYPE_LOADING:
                return new BaseRecyclerViewHolder(layoutInflater.inflate(R.layout.yh_item_transparent_loading, parent, false), viewType);
            default:
                return iRecyclerController.onCreateViewHolder(layoutInflater, parent, viewType);
        }
    }

    /**
     * 删除指定位置的item
     *
     * @param position 删除item 的适配器位置
     * @return true 删除成功， FALSE 删除失败
     */
    public boolean removePositionItem(int position) {
        if (this.getItemCount() > position && null != iRecyclerController) {
            List dataList = iRecyclerController.getDataList();
            if (null != dataList) {
                dataList.remove(position);
                this.notifyItemRemoved(position);
                return true;
            }
        }
        return false;
    }

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
                iRecyclerController.onBindViewHolder(mStateCode, viewType, holder, position, this);
                break;
        }
    }

    private void initRefreshLoadingData(BaseRecyclerViewHolder holder) {
        View progressBar = holder.getProgressBar();
        AppCompatTextView hintInfo = holder.getHintInfo();
        if (progressBar == null || hintInfo == null) {
            return;
        }
        holder.itemView.setOnClickListener(this);
        if (mTempInfo != null) {
            if (progressBar.getVisibility() != View.GONE) {
                progressBar.setVisibility(View.GONE);
            }
            hintInfo.setText(mTempInfo);
            mTempInfo = null;
        } else if (!iRecyclerController.canLoadingData()) {
            if (progressBar.getVisibility() != View.GONE) {
                progressBar.setVisibility(View.GONE);
            }
            hintInfo.setText(mFinishHintInfo);
        } else {
            if (progressBar.getVisibility() != View.VISIBLE) {
                progressBar.setVisibility(View.VISIBLE);
            }
            hintInfo.setText(mLoadingHintInfo);
            if (canLoadingDataFormNetwork()) {
                getDataFormNetwork(this);
            }
        }
    }

    /**
     * @return true 联网进行加载
     * 这个方法若返回FALSE ，会被再次调用
     */
    protected boolean canLoadingDataFormNetwork() {
        return true;
    }

    @Override
    public int getItemCount() {
        return iRecyclerController == null ? 0 : iRecyclerController.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return iRecyclerController.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        if (mOnScrollListener == null) {
            mOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    if (newState == RecyclerView.SCROLL_STATE_IDLE && null != getActivity() &&
                            !getActivity().isFinishing()) {
                        ImageLoader.onResume(mFragmentOrActivity);
                    } else {
                        ImageLoader.onPause(mFragmentOrActivity);
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

    public void setOnDataLoadingFinishListener(IOnDataLoadingFinishListener iOnDataLoadingFinishListener) {
        this.iOnDataLoadingFinishListener = iOnDataLoadingFinishListener;
    }

    /**
     * 显示加载错误信息
     *
     * @param sequence 错误信息
     * @param id       请求的id
     */
    void showErrorLoadingHint(CharSequence sequence, int id) {
        if (iRecyclerController.requestFail(id, this) && !showLoadingHint(false, sequence)) {
            mTempInfo = sequence;
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

        if (!mConfigNetwork && isLoadingOrRefreshVisibility
                && canLoadingDataFormNetwork() && iRecyclerController.canLoadingData()) {
            getDataFormNetwork(this);
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

    protected boolean showLoadingHint(boolean showProgress, CharSequence sequence) {
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

    /**
     * 指定位置是否可见，若无可见，则返回对应的view
     */
    public View getVisibilityViewAtPosition(int position) {
        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
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
     * 数据请求成功
     *
     * @param response json串
     *                 param id id
     */
    @Override
    public void loadingDataSuccessful(String response, int id) {
        mConfigNetwork = false;
        try {
            iRecyclerController.decodeResponseData(this, response, isRefresh, id);
            setRefreshFinish();
        } catch (Exception e) {
            LogUtils.e("BaseHomePagerAdapter失败" + e.toString() + "接口数据：" + response);
            showErrorLoadingHint(mErrorHintInfo, id);
            setRefreshFinish();
        }
    }

    /**
     * 加载失败
     *
     * @param describe 描述
     * @param id       id
     */
    @Override
    public void loadingDataFail(String describe, int id) {
        mConfigNetwork = false;
        setRefreshFinish();
        this.showErrorLoadingHint(describe, id);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_item_loading) {
            if (canLoadingDataFormNetwork()) {
                if (iRecyclerController != null)
                    iRecyclerController.requestLoadingData();
                getDataFormNetwork(this);
                BaseRecyclerAdapter.resetState((LinearLayoutCompat) v);
            }
        } else {
            iRecyclerController.onClickListener(v, mHomePagerHttpControl, this);
        }
    }

    /**
     * 刷新完成
     */
    void setRefreshFinish() {
        if (null != iOnDataLoadingFinishListener)
            iOnDataLoadingFinishListener.onDataLoadingFinishListener();
    }

    /**
     * 进行网络请求
     *
     * @param adapter 当前对象
     */
    void getDataFormNetwork(BaseRecyclerNetworkAdapter adapter) {
        if (mRecyclerView != null && !mConfigNetwork) {
            if (mHomePagerHttpControl == null) {
                mHomePagerHttpControl = HomePagerHttpControl.getInstance();
            }
            mConfigNetwork = true;
            Map<String, String> parameter = iRecyclerController.getParameter(mCategory);
            String url = iRecyclerController.getUrl(mCategory);
            if (null != parameter && !TextUtils.isEmpty(url)) {
                mHomePagerHttpControl.startNetwork(--sRequestId, adapter
                        , parameter
                        , url);
            }
        }
    }

}
