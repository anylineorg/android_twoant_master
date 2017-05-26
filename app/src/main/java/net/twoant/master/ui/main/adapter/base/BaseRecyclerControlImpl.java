package net.twoant.master.ui.main.adapter.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.ui.main.interfaces.IRecyclerController;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/5.
 * recyclerView 控制实现基类
 */

public abstract class BaseRecyclerControlImpl<T> implements IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> {

    protected List<T> mDataBean;
    protected int mIndex;
    protected ArrayMap<String, String> mKeySet;
    protected String mKeyword;
    protected Context mContext;
    protected AiSouAppInfoModel mAiSouAppInfoModel;
    private boolean isLoadingFinish;
    /**
     * 子类不希望父类拦截标记
     */
    protected ArrayList<Integer> mFlagDisable;
    /**
     * 自动扩展范围，加载。
     */
    protected boolean mAutoincrement;
    private final static int AUTOINCREMENT_SIZE = 3000;
    protected final static int MAX_AUTOINCREMENT_SIZE = 50000;
    /**
     * 查询半径
     */
    protected int mRadiusSize;
    /**
     * 每页大小
     */
    protected int mEachPageSize = ApiConstants.REQUEST_PAGE_SIZE;
    /**
     * 屏幕宽度
     */
    protected int mScreenWidth;

    protected String mLatLng;

    /**
     * 起始页 郭哥 是 0， 张哥是 1
     */
    private final int mStartPageIndex;

    /**
     * 只用于加载图片使用， 可能是Activity 、 Fragment、 Context
     */
    protected Object mEnvironment;

    private BaseRecyclerNetworkAdapter mBaseRecyclerNetworkAdapter;

    protected BaseRecyclerControlImpl(int startPage) {
        this.mStartPageIndex = this.mIndex = startPage;
        mScreenWidth = DisplayDimensionUtils.getScreenWidth();
        mDataBean = new ArrayList<>(12);
        mDataBean.add(null);
        mKeySet = new ArrayMap<>();
        mContext = AiSouAppInfoModel.getAppContext();
        mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        mRadiusSize = ApiConstants.NEARBY_REQUEST_RADIUS;
        mFlagDisable = new ArrayList<>();
        initLatLng();

    }

    /**
     * 初始化经纬度
     */
    private void initLatLng() {
        /*if (mAiSouAppInfoModel.isUserSelectLocation())
            mLatLng = String.valueOf(mAiSouAppInfoModel.getSelectLongitude() + "," + mAiSouAppInfoModel.getSelectLatitude());
        else*/// TODO: 2017/3/23 注释了用户当前选择的经纬度
        mLatLng = String.valueOf(mAiSouAppInfoModel.getAiSouLocationBean().getLongitude() + ","
                + mAiSouAppInfoModel.getAiSouLocationBean().getLatitude());
    }


    @Override
    public int getItemCount() {
        return mDataBean.size();
    }

    @Override
    public void decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, boolean isRefresh, int id) {

        if (!mFlagDisable.contains(id)) {
            List<T> resultBean = decodeResponseData(adapter, response, id, true);

            boolean nonNull = resultBean != null;

            int dataCount = 0;

            //若当前为加载，为避免重复添加，所以要删除上次一样的
            if (nonNull = (null != (resultBean = subRemoveMethod(nonNull, resultBean, isRefresh)))) {
                dataCount = resultBean.size();
                isLoadingFinish = dataCount == 0;
            }

            if (nonNull && !isLoadingFinish && dataCount > 0) {
                initLatLng();

                if (isRefresh) {
                    mDataBean = resultBean;
                    mDataBean.add(null);
                    adapter.notifyDataSetChanged();
                    adapter.setRefreshFinish();
                } else {
                    int size = mDataBean.size() - 1;
                    mDataBean.addAll(size, resultBean);
                    adapter.notifyItemRangeInserted(size, dataCount);
                }

            } else if (canAutoLoading()) {

                mRadiusSize += AUTOINCREMENT_SIZE;
                requestFail(id, adapter);
                mAutoincrement = false;
                adapter.getDataFormNetwork(adapter);

            } else {
                isLoadingFinish = true;
                if (isRefresh) {
                    if (!nonNull) {
                        mDataBean.clear();
                    } else {
                        mDataBean = resultBean;
                    }
                    mDataBean.add(null);
                    adapter.notifyDataSetChanged();
                    adapter.setRefreshFinish();
                }
                adapter.showErrorLoadingHint("没有更多", id);
            }
        } else {
            mFlagDisable.remove(Integer.valueOf(id));
            decodeResponseData(adapter, response, id, false);
        }
    }

    /**
     * 删除数组重复项
     *
     * @param nonNull    非空集合
     * @param resultBean 集合
     * @param isRefresh  处于刷新
     * @return true 使用子类定的规则
     */
    protected List<T> subRemoveMethod(boolean nonNull, List<T> resultBean, boolean isRefresh) {
        if (nonNull && resultBean.size() > 0 && !isRefresh) {
            resultBean.removeAll(mDataBean);
        }

        return resultBean;
    }

    /**
     * 是否可以扩大 范围加载
     *
     * @return true 扩大范围加载
     */
    protected boolean canAutoLoading() {
        return !isLoadingFinish && mAutoincrement && mRadiusSize <= MAX_AUTOINCREMENT_SIZE;
    }

    @Override
    public int getItemViewType(int position) {

        if (1 == mDataBean.size())
            return IRecyclerViewConstant.TYPE_REFRESH;

        T t = mDataBean.get(position);
        if (t == null && position == mDataBean.size() - 1)
            return IRecyclerViewConstant.TYPE_LOADING;

        return getItemViewType(position, t);
    }

    /**
     * @param position 位置
     * @param dataBean 数据体
     * @return 类型 {@link IRecyclerViewConstant}
     */
    protected int getItemViewType(int position, T dataBean) {
        return 0;
    }

    /**
     * 获取子类的解析后的数据
     */
    protected abstract List<T> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept);


    @Override
    public boolean canLoadingData() {

        return !isLoadingFinish;
    }

    @Override
    public boolean requestFail(int id, BaseRecyclerNetworkAdapter adapter) {
        if (!mFlagDisable.contains(id)) {
            initLatLng();
            //当前下标 小于 默认的初始下标
            if (mIndex - 1 <= mStartPageIndex) {
                mIndex = mStartPageIndex;
            } else {
                --mIndex;
            }
            return true;
        } else {
            mFlagDisable.remove(Integer.valueOf(id));
            return false;
        }
    }

    @Override
    public void requestLoadingData() {
        initLatLng();
        if (!mAutoincrement)
            isLoadingFinish = false;
    }

    @Override
    public void refreshData(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            this.mKeyword = keyword;
            this.mDataBean.clear();
            this.mDataBean.add(null);
        }
        isLoadingFinish = false;
        initLatLng();
        this.mIndex = mStartPageIndex;
        mRadiusSize = ApiConstants.NEARBY_REQUEST_RADIUS;
    }

    @Override
    public void bindingAdapter(BaseRecyclerNetworkAdapter adapter) {
        this.mBaseRecyclerNetworkAdapter = adapter;
        if (null != adapter) {
            this.mEnvironment = adapter.getFragmentOrActivity();
        } else {
            mEnvironment = AiSouAppInfoModel.getAppContext();
        }
    }

    @Nullable
    public BaseRecyclerNetworkAdapter getBaseRecyclerNetworkAdapter() {
        return this.mBaseRecyclerNetworkAdapter;
    }

    @Override
    @CallSuper
    public void onDestroy() {
        mEnvironment = null;
        mBaseRecyclerNetworkAdapter = null;
    }


    @Override
    @CallSuper
    public void onResume() {

    }

    @Override
    @CallSuper
    public void onStop() {

    }

    @Override
    @CallSuper
    public void onPause() {

    }

    @Override
    public List getDataList() {
        return mDataBean;
    }
}
