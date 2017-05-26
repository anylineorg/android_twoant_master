package net.twoant.master.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.ui.main.adapter.HomePageAdapter;
import net.twoant.master.ui.main.interfaces.IOnDataLoadingFinishListener;
import net.twoant.master.ui.main.interfaces.IOnRefreshListener;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;
import net.twoant.master.widget.ScrollHeaderLinearLayout;


/**
 * Created by S_Y_H on 2016/12/20.
 * 所有的活动Fragment
 */

public class ActivityFragment extends ViewPagerBaseFragment implements IOnRefreshListener, ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer {

    /**
     * 带 滚动到顶部 View
     */
    public final static int TYPE_LAYOUT_HAS_SCROLL_TOP = 0xAA;
    /**
     * 什么也没有的View
     */
    public final static int TYPE_LAYOUT_NORMAL = 0xBB;
    /**
     * 带下拉刷新的View
     */
    public final static int TYPE_LAYOUT_REFRESH = 0xCC;


    private final static String TYPE = "AF_t";
    private final static String CATEGORY = "AF_c";
    private final static String KEY_KEYWORD = "AF_k";
    private final static String HAS_SCROLL_TOP_VIEW = "AF_t_v";
    private final static String STATE = "AF_s";
    private final static String TOP_DIVIDER = "t_d";

    /**
     * 类型
     */
    private int mType;

    private int mState;

    /**
     * 是否显示 第一个item 顶部divider
     */
    private boolean mShowTopDivider;

    /**
     * 分类
     */
    private int mCategory;

    /**
     * 搜索关键字
     */
    private String mKey;

    private RecyclerView mRecyclerView;

    private boolean hasLastRefreshing;

    private HomePageAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private IOnTypeAndCategoryChangeListener iOnTypeAndCategoryChangeListener;
    /**
     * view类型
     */
    private int mViewType;

    public interface IOnTypeAndCategoryChangeListener {

        void onChangeListener(int type, int category, @NonNull String keyword);
    }


    public static ActivityFragment newInstance(int state, int type, int category, @Nullable String keyword) {
        return newInstance(ActivityFragment.TYPE_LAYOUT_NORMAL, state, type, category, keyword);
    }

    /**
     * @param viewType            View 的类型
     * @param state               item的状态值
     * @param type                item的类型
     * @param category            item 的分类
     * @param keyword             关键字
     * @param showFirstTopDivider 显示第一个item 的顶部 分割线
     * @return 实例
     */
    public static ActivityFragment newInstance(int viewType, int state, int type, int category, @Nullable String keyword, boolean showFirstTopDivider) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        args.putInt(STATE, state);
        args.putInt(CATEGORY, category);
        args.putInt(HAS_SCROLL_TOP_VIEW, viewType);
        args.putString(KEY_KEYWORD, keyword);
        args.putBoolean(TOP_DIVIDER, showFirstTopDivider);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActivityFragment newInstance(int viewType, int state, int type, int category, @Nullable String keyword) {
        return newInstance(viewType, state, type, category, keyword, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mType = arguments.getInt(TYPE);
            mState = arguments.getInt(STATE);
            mCategory = arguments.getInt(CATEGORY);
            mKey = arguments.getString(KEY_KEYWORD);
            mViewType = arguments.getInt(HAS_SCROLL_TOP_VIEW);
            mShowTopDivider = arguments.getBoolean(TOP_DIVIDER);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mT", mType);
        outState.putInt("mS", mState);
        outState.putInt("mC", mCategory);
        outState.putString("mK", mKey);
        outState.putInt("mVT", mViewType);
        outState.putBoolean("hLR", hasLastRefreshing);
    }

    /**
     * @param position     滚动到的位置
     * @param smoothScroll 缓慢滚动
     */
    public void scrollToPosition(int position, boolean smoothScroll) {
        if (smoothScroll)
            mRecyclerView.smoothScrollToPosition(position);
        else
            mRecyclerView.scrollToPosition(position);
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_content_fragment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (mShowTopDivider) {
            mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(getActivity()
                    , R.color.dividerLineColor, 0, R.dimen.px_5, true, R.dimen.px_2));
        } else {
            mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(getActivity()
                    , R.color.dividerLineColor, 0, R.dimen.px_5, false, R.dimen.px_2));
        }
        mAdapter = new HomePageAdapter(getActivity(), mType, mCategory, mKey,mState);
        mAdapter.setStateCode(mState);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        switch (mViewType) {
            case TYPE_LAYOUT_HAS_SCROLL_TOP:
                view.findViewById(R.id.fab_back_top).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecyclerView.scrollToPosition(0);
                    }
                });
                break;

            case TYPE_LAYOUT_REFRESH:
                mAdapter.setOnDataLoadingFinishListener(new IOnDataLoadingFinishListener() {
                    @Override
                    public void onDataLoadingFinishListener() {
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
                mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_action_list);
                mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mAdapter.refreshData();
                    }
                });
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mType = savedInstanceState.getInt("mT");
        mState = savedInstanceState.getInt("mS");
        mCategory = savedInstanceState.getInt("mC");
        mKey = savedInstanceState.getString("mK");
        mViewType = savedInstanceState.getInt("mVT");
        hasLastRefreshing = savedInstanceState.getBoolean("hLR");
    }

    @Override
    public void onRefreshListener() {
        if (mAdapter != null && isVisibility) {
            hasLastRefreshing = false;
            mAdapter.refreshData();
        } else {
            hasLastRefreshing = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mAdapter) {
            mAdapter.onPause();
        }
    }

    @Override
    public View getScrollableView() {
        return mRecyclerView;
    }

    @Override
    public boolean canScrollable() {
        return true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IOnTypeAndCategoryChangeListener) {
            this.iOnTypeAndCategoryChangeListener = (IOnTypeAndCategoryChangeListener) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.iOnTypeAndCategoryChangeListener = null;
    }

    @Override
    protected int getLayoutRes() {

        switch (mViewType) {
            case TYPE_LAYOUT_REFRESH:
                return R.layout.yh_fragment_recycler_refresh;
            case TYPE_LAYOUT_HAS_SCROLL_TOP:
                return R.layout.yh_fragment_recycler_back_top;

            case TYPE_LAYOUT_NORMAL:
                return R.layout.yh_fragment_recycler_view;
        }
        return 0;
    }

    @Override
    protected void onUserVisible() {
        if (iOnTypeAndCategoryChangeListener != null
                && mAdapter.getType() != mType
                || mKey != null && !mKey.equals(mAdapter.getKeyword())) {
            mAdapter.setRecyclerController(mType, mCategory, mKey);
        }
        if (hasLastRefreshing && null != mAdapter) {
            hasLastRefreshing = false;
            mAdapter.refreshData();
        }
    }

    @Override
    protected void onUserInvisible() {
        if (iOnTypeAndCategoryChangeListener != null)
            iOnTypeAndCategoryChangeListener.onChangeListener(mType, mCategory, mKey);
    }

    /**
     * 切换 type和category
     *
     * @param type     类型
     * @param category 分类
     * @param keyword  关键字
     */
    public void replaceTypeAndCategory(int type, int category, @NonNull String keyword) {
        if (mAdapter != null && isVisibility) {
            mAdapter.setRecyclerController(type, category, keyword);
        }
        this.mType = type;
        this.mCategory = category;
        this.mKey = keyword;
    }
}
