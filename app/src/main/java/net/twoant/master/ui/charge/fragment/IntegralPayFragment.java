package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.IntegralActivity;
import net.twoant.master.ui.charge.holder.IntegralPayHolder;
import net.twoant.master.widget.ScrollHeaderLinearLayout;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class IntegralPayFragment extends BaseFragment implements ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer {
    private ListView listView;
    private IntegralPayAdapter adapter;
    private List<DataRow> dataList;
    private Map map = new HashMap();
    private SwipeRefreshLayout swipeRefreshLayout;
    private IntegralActivity integralActivity;
    private double totalIntegral;

    private boolean flag_1 = false;
    private boolean flag_2 = true;
    private boolean btnFlag = false;

    public double getTotalIntegral() {
        return totalIntegral;
    }

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity1;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
             android.R.color.holo_orange_light, android.R.color.holo_red_light);
        }
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestNet();
            }
        });
        dataList = new ArrayList<>();
        integralActivity = (IntegralActivity) getActivity();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());

        // 监听 ListView 滑动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int oldVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem > oldVisibleItem) {
                    if (flag_1) {
                        //隐藏
                        integralActivity.setImgHide();
                        flag_1 = false;
                        flag_2 = true;
                    }
                }
                if (firstVisibleItem < oldVisibleItem) {
                    if (!btnFlag) {
                        if (flag_2) {
                            //显示
                            integralActivity.setImgShow();
                            flag_1 = true;
                            flag_2 = false;
                            btnFlag = false;
                        }
                    }else{
                        btnFlag = false;
                    }
                }
                oldVisibleItem = firstVisibleItem;
            }
        });
    }

    public void monthSelected(String month){
        int tabPosition = integralActivity.getTabPosition();
        if (tabPosition==1) {
            String[] split = month.split("-");
            String s1 = split[0];
            String s2 = split[1];
            if (Integer.parseInt(s2) <= 9) {
                month = s1+"-0"+s2;
            }
            map.put("ym", month);
            requestNet();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int tabPosition = integralActivity.getTabPosition();
        if (tabPosition==1) {
            String month = integralActivity.getTime();
            String[] split = month.split("-");
            String s1 = split[0];
            String s2 = split[1];
            if (Integer.parseInt(s2) <= 9) {
                month = s1+"-0"+s2;
            }
            map.put("ym", month);
            requestNet();
        }
    }

    private void requestNet() {
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp(ApiConstants.INTEGRAL_OUT,"" ,map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
                ToastUtil.showLong("连接失败:"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                swipeRefreshLayout.setRefreshing(false);
//                IntegralBean integralBean = JsonUtil.parseJsonToBean(response, IntegralBean.class);
                dataList = DataRow.parseJson(response).getSet("data").getRows();

                totalIntegral = 0;
                for (DataRow dataBean:dataList){
                    totalIntegral += Double.parseDouble(dataBean.getString("VAL"));
                }
                BigDecimal bigDecimal = new BigDecimal(totalIntegral);
                integralActivity.setTotal("-"+bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                adapter = new IntegralPayAdapter(dataList);
                listView.setAdapter(adapter);
            }
        });
    }

    @Override
    public View getScrollableView() {
        return listView;
    }

    @Override
    public boolean canScrollable() {
        return false;
    }

    class IntegralPayAdapter extends BasicAdapter<DataRow> {

        public IntegralPayAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new IntegralPayHolder();
        }
    }

    public void setToTop(){
        listView.setSelection(0);
    }
}

