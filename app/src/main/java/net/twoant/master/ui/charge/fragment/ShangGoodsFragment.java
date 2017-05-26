package net.twoant.master.ui.charge.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.activity.GoodsManagerActivity;
import net.twoant.master.ui.my_center.activity.out.GoodsDetailSellerActivity;
import net.twoant.master.ui.my_center.bean.GoodsListBean;
import net.twoant.master.ui.my_center.bean.GroundGoodsBean;
import net.twoant.master.ui.my_center.httputils.SearchGoodsHttpUtils;
import net.twoant.master.widget.sidesliplistview.SwipeMenu;
import net.twoant.master.widget.sidesliplistview.SwipeMenuCreator;
import net.twoant.master.widget.sidesliplistview.SwipeMenuItem;
import net.twoant.master.widget.sidesliplistview.SwipeMenuListView;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.ui.charge.holder.GoodsDetailShangHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class ShangGoodsFragment extends BaseFragment {
    private HintDialogUtil hintDialogUtil;
    private SwipeMenuListView listView;
    private String shop_id;
    private List<GoodsListBean.ResultBean> resultList;
    private MyAdapter adapter;
    private String open_goodsid;
    public GoodsManagerActivity activity;
//    public SwipeRefreshLayout mSwipeRefreshLayout;

    boolean isLastItem = false;//是否是最后一项

    int temp_data_count_page = 0;//临时存放当前加载页对应的pos
    int data_count_page_all = 2;//模拟3页数据

    int temp_how = 0;//【测试用】存放加载的方式

    boolean isLoading = false;//是否正在加载中
    boolean isComp = false;//标记一次是否加载完成
    final String loading_Load_More = "加载中...";
    final String comp_Load_More = "加载完成";

    String nowNormalText = "";//存放当前footview显示的文字

    RelativeLayout id_rl_loading;
    ProgressBar id_pull_to_refresh_load_progress;
    TextView id_pull_to_refresh_loadmore_text;
    public Map<String, String> mapXia;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_seller_goodslist;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        activity = (GoodsManagerActivity) getActivity();
        shop_id = activity.getShopId();
        listView = (SwipeMenuListView) view.findViewById(net.twoant.master.R.id.listView_swipe_goods);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefreshlayout);
//        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestLineNet();
//            }
//        });
        hintDialogUtil = new HintDialogUtil(getActivity());
        hintDialogUtil.showLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLineNet();
    }

    void requestLineNet(){
        SearchGoodsHttpUtils.LongHttp("", shop_id, "", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(),"0", new StringCallback() {//0是查上架商品
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("连接失败");
//                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
//                mSwipeRefreshLayout.setRefreshing(false);
                hintDialogUtil.dismissDialog();
                resultList = GsonUtil.gsonToBean(response, GoodsListBean.class).getResult();
//                View listview_footer_view = LayoutInflater.from(getActivity()).inflate(R.layout.zy_listview_footer, null);
//                id_rl_loading = (RelativeLayout) listview_footer_view.findViewById(R.id.id_rl_loading);
//                id_pull_to_refresh_load_progress = (ProgressBar) listview_footer_view.findViewById(R.id.id_pull_to_refresh_load_progress);
//                id_pull_to_refresh_load_progress.setVisibility(View.GONE);
//                id_pull_to_refresh_loadmore_text = (TextView) listview_footer_view.findViewById(R.id.id_pull_to_refresh_loadmore_text);
//                id_pull_to_refresh_loadmore_text.setClickable(false);
                adapter = new MyAdapter(resultList);
             /*   if (resultList.size()>10) {
                    mChatListView.addFooterView(listview_footer_view);
                }
                mChatListView.setOnScrollListener(OnScrollListenerOne);*/
                listView.setAdapter(adapter);
                //创建item侧滑测单
                createSideslipMenu();
            }
        });
    }

    /**
     * 上拉加载1
     */
    AbsListView.OnScrollListener OnScrollListenerOne = new AbsListView.OnScrollListener() {

        //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
        //回调顺序如下
        //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
        //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
        //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //当滚到最后一行且停止滚动时，执行加载
            if (isLastItem && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && !isLoading) {
                //加载元素
//                loadMore();
                isLastItem = false;
            }
        }

        //滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。
        //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
        //visibleItemCount：当前能看见的列表项个数（小半个也算）
        //totalItemCount：列表项共数
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            //判断是否滚到最后一行
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                isLastItem = true;
            }
        }
    };

    private void createSideslipMenu() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(net.twoant.master.R.color.umeng_socialize_text_time)));
                // set item width
                openItem.setWidth((int) getResources().getDimension(net.twoant.master.R.dimen.px_130));
                // set item title
                openItem.setTitle("下架");
                // set item title fontsize
                openItem.setTitleSize((int) getResources().getDimension(net.twoant.master.R.dimen.px_text_12));
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, final int index) {
                // ApplicationInfo item = mAppList.get(position);
//                LogUtils.d(index+"");
                switch (index) {
                    case 0:
                        try {
                            if (null == mapXia) {
                                mapXia = new HashMap();
                            }
                            mapXia.put("state","1");// 1 下架
                            mapXia.put("g_id",resultList.get(position).getGoods_id()+"");
                            LongHttp(ApiConstants.GDS_JU,"" , mapXia, new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
    //                                LogUtils.d("s");
                                    hintDialogUtil.showError("连接失败");
                                }

                                @Override
                                public void onResponse(String response, int id) {
//                                    LogUtils.d(response);
                                    GroundGoodsBean httpResultBean = GsonUtil.gsonToBean(response, GroundGoodsBean.class);
                                    GroundGoodsBean.DataBean data = httpResultBean.getData();
                                    String msg = data.getMsg();
                                    if (msg.contains("成功")) {
                                        ToastUtil.showLong(msg);
                                        resultList.remove(position);
                                        adapter.notifyDataSetChanged();
                                        activity.setTab2();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });

        // set MenuStateChangeListener
        listView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
                LogUtils.d(position+""+resultList.size());
                try {
                    open_goodsid = resultList.get(position).getGoods_id()+"";
                } catch (Exception e) {
                    open_goodsid = "-1";
                }
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (resultList.size()>position) {
                    Intent intent = new Intent(getActivity(), GoodsDetailSellerActivity.class);
                    String xx = resultList.get(position).getGoods_id()+"";
                    intent.putExtra("goods_id",xx);
                    startActivity(intent);
                }
            }
        });
    }

    class MyAdapter extends BasicAdapter<GoodsListBean.ResultBean> {

        public MyAdapter(List<GoodsListBean.ResultBean> list) {
            super(list);
        }

        @Override
        protected BaseHolder<GoodsListBean.ResultBean> getHolder(int position) {
            return new GoodsDetailShangHolder();
        }
    }

}

