package net.twoant.master.ui.convenient.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.ui.convenient.activity.ConvenientInfoContentActivity;
import net.twoant.master.ui.convenient.adapter.ConvenientRvAdapter;
import net.twoant.master.widget.entry.DataRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by J on 2017/2/22.
 */

public class ConvenientInfoListFragment extends ViewPagerBaseFragment implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack, ConvenientRvAdapter.OnRecyclerViewListener {
    private static final String EXTRA_INFO_LIST_ID = "INFO_LIST";
    private static final String REQUEST_TYPR = "REQUEST_TYPR";
    private final static int ID_INFO_LIST = 0X13;

    private String mInfoListId;
    private String mRequestType;
    private HttpConnectedUtils mHttpConnectedUtils;
    private boolean mResult;
    private List<DataRow> mData = new ArrayList<>();
    private ConvenientRvAdapter convenientRvAdapter;
    private HashMap<String, String> mMap;
    private SwipeRefreshLayout mMySwipeRefreshLayout;
    private AppCompatImageButton mAppCompatImageButton;
    private int anyline_page = 1;
    private boolean isScrollToBottom;//是否滑动到底部
    private RecyclerView recycler_convenient;
    boolean isSlidingToLast = false;
    private boolean onRefreshing = false;
    private OnSwifeRefreshListener onSwifeRefreshListener;
    private TextView tv_hasdata;
    private int height;
    private boolean isLoadingMore = false;//是否正在加载更多中
    private boolean canLoadingMore = false;

    /**
     * 是否进行了刷新 城市编码 和 地区编码
     */
    private boolean hasSwitchAdAndCityCode;

    @Override
    protected int getLayoutRes() {
        return R.layout.xj_fragment_convenient_list;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        initView(view);
        initDatas();
    }

    //初始化数据
    private void initDatas() {
        isLoadingMore = true;
        if (mHttpConnectedUtils == null) {
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        }
        if (mMap == null) {
            mMap = new HashMap<>();
        }
        mMap.clear();
        mMap.put(mRequestType, mInfoListId);
        Log.d("TAG", "initDatas:anyline_page " + anyline_page);
        mMap.put("_anyline_page", anyline_page + "");
        mHttpConnectedUtils.startNetworkGetString(ID_INFO_LIST, mMap, ApiConstants.CONVENIENT_INFO_LIST);
    }

    //初始化控件
    private void initView(View view) {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        height = wm.getDefaultDisplay().getHeight();
        recycler_convenient = (RecyclerView) view.findViewById(R.id.recycler_convenient);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_convenient.setLayoutManager(layoutManager);
        recycler_convenient.setMinimumHeight(height);

        mMySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mAppCompatImageButton = (AppCompatImageButton) view.findViewById(R.id.fab_back_top);
        mMySwipeRefreshLayout.setEnabled(false);

        recycler_convenient.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否向上滑动
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                    int itemCount = manager.getItemCount();

                    //判断是否滚动到底部，并且是向下滚动
                    if (lastVisibleItemPosition == (itemCount - 1) && isSlidingToLast) {
                        if (canLoadingMore && !isLoadingMore) {
                            //加载更多
                            initDatas();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                if (dy > 0) {
                    //大于0表示正在向上滚动
                    Log.d("TAG", "onScrolled: " + dy);
                    isSlidingToLast = true;
                } else {
                    //小于等于0表示停止或向左滚动
                    isSlidingToLast = false;
                }
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                if (0 != firstVisibleItemPosition) {
                    mAppCompatImageButton.setVisibility(View.VISIBLE);
                } else {
                    mAppCompatImageButton.setVisibility(View.GONE);
                }
            }
        });

        //回到顶部
        mAppCompatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycler_convenient.smoothScrollToPosition(0);
            }
        });
    }

    //获取ConvenientInfoListFragment对象实例
    public static ConvenientInfoListFragment newInstance(@NonNull String infoListId, String type) {
        ConvenientInfoListFragment fragment = new ConvenientInfoListFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_INFO_LIST_ID, infoListId);
        args.putString(REQUEST_TYPR, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mInfoListId = arguments.getString(EXTRA_INFO_LIST_ID);
            mRequestType = arguments.getString(REQUEST_TYPR);
        }
    }

    @Override
    protected void onUserVisible() {
        if (hasSwitchAdAndCityCode) {
            hasSwitchAdAndCityCode = false;
            updataUI(null, null);
        }
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hasSwitchAdAndCityCode = false;
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_INFO_LIST:
                mResult = DataRow.parseJson(response).getBoolean("result", false);
                if (mResult) {
                    //请求到数据
                    if (anyline_page >= 2) {
                        mData.addAll(DataRow.parseJson(response).getSet("data").getRows());
                        anyline_page++;
                        canLoadingMore = true;
                    } else if (0 != (DataRow.parseJson(response).getSet("data").getRows().size())) {
                        mData.clear();
                        mData = DataRow.parseJson(response).getSet("data").getRows();
                        anyline_page++;
                        canLoadingMore = true;
                    }
                    //
                    if (0 == (DataRow.parseJson(response).getSet("data").getRows().size())) {
                        mData.add(null);
                        canLoadingMore = false;
                    }
                    if (convenientRvAdapter == null) {
                        convenientRvAdapter = new ConvenientRvAdapter(mContext, mData);
                        recycler_convenient.setAdapter(convenientRvAdapter);
                    } else {
                        convenientRvAdapter.setDatas(mData);
                    }
                }
//                mMySwipeRefreshLayout.setRefreshing(false);
                convenientRvAdapter.setOnRecyclerViewListener(this);
                isLoadingMore = false;
                onRefreshing = false;
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    //更新数据
    public void updataUI(String infoListId, String type) {
        onRefreshing = true;
        anyline_page = 1;
        mData.clear();
        if (!TextUtils.isEmpty(infoListId)) {
            mInfoListId = infoListId;
        }
        if (!TextUtils.isEmpty(type)) {
            mRequestType = type;
        }
        initDatas();
    }


    @Override
    public void onItemClick(int position) {
        if (null!=mData&&0!=mData.size()){
            if (null != mData.get(position)) {
                ConvenientInfoContentActivity.startActivity(mContext, mData.get(position).getInt("ID") + "");
            }
        }
    }

    @Override
    public boolean onItemLongClick(int position) {
        return false;
    }

    /**
     * 切换城市代码 和 地区编码
     *
     * @param cityCode    城市代码
     * @param addressCode 地区编码
     */
    public void switchCityCodeAndAddressCode(String cityCode, String addressCode) {
        if (null != mHttpConnectedUtils) {
            mHttpConnectedUtils.setCityAndAddressCode(cityCode, addressCode);
            if (isVisibility) {
                updataUI(null, null);
            } else {
                hasSwitchAdAndCityCode = true;
            }
        }
    }

    public interface OnSwifeRefreshListener {
        void setRefresh(boolean onRefreshing);
    }

    public void setOnSwifeRefreshListener(OnSwifeRefreshListener onSwifeRefreshListener) {
        this.onSwifeRefreshListener = onSwifeRefreshListener;
    }
}
