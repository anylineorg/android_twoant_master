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
import net.twoant.master.ui.charge.activity.IntegralActivity;
import net.twoant.master.ui.charge.holder.IntegralIncomeHolder;
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

public class                                                                                                                                                     IntegralIncomeFragment extends BaseFragment implements ScrollHeaderLinearLayout.HeaderScrollHelper.ScrollableContainer {
    private ListView listView;
    private MyAdapter adapter;
    private List<DataRow> dataList;
    private Map map = new HashMap();
    private SwipeRefreshLayout swipeRefreshLayout;
    private double totalIntegral;
    public IntegralActivity integralActivity;
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
        integralActivity = (IntegralActivity) getActivity();
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        dataList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        swipeRefreshLayout.setEnabled(false);
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
        if (tabPosition==0) {
            String[] split = month.split("-");
            String s1 = split[0];
            String s2 = split[1];
            if (Integer.parseInt(s2) <= 9) {
                month = s1+"-0"+s2;
            }
            map.put("ym", month);
            requesNet();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int tabPosition = integralActivity.getTabPosition();
        if (tabPosition==0) {
            String month = integralActivity.getTime();
            String[] split = month.split("-");
            String s1 = split[0];
            String s2 = split[1];
            if (Integer.parseInt(s2) <= 9) {
                month = s1+"-0"+s2;
            }
            map.put("ym", month);
            requesNet();
        }
    }

    private void requesNet() {
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp(ApiConstants.INTEGRAL_IN, "",map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
//                response="{\"result\":true,\"code\":\"200\",\"data\":[{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"2.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:06:20\",\"FK_ID\":353,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":116,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"},{\"VAL\":\"20.00\",\"LOG_DATE\":\"2017-02-04\",\"SORT_ID\":1231,\"SHOP_ID\":null,\"LOG_TIME\":\"2017-02-04 03:04:46\",\"FK_ID\":352,\"USER_ID\":197,\"SHOP_NM\":null,\"USER_NM\":\"邓\",\"PK_ID\":null,\"TITLE\":null,\"ID\":115,\"SHOP_AVATAR\":null,\"SORT_NM\":\"用户现金购买积分\"}],\"success\":true,\"type\":\"list\",\"message\":null}";
                swipeRefreshLayout.setRefreshing(false);
//                IntegralBean integralBean = JsonUtil.parseJsonToBean(response, IntegralBean.class);
                dataList = DataRow.parseJson(response).getSet("data").getRows();
                totalIntegral = 0;
                for (DataRow dataBean:dataList){
                    totalIntegral += Double.parseDouble(dataBean.getString("VAL"));
                }
                BigDecimal bigDecimal = new BigDecimal(totalIntegral);
                integralActivity.setTotal("+"+bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                adapter = new MyAdapter(dataList);
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
        return true;
    }


    class MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new IntegralIncomeHolder();
        }
    }

    public void setToTop(){
        listView.setSelection(0);
    }

}

