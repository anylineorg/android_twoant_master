package net.twoant.master.ui.charge.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.SellerManageListBean;
import net.twoant.master.ui.charge.holder.SellerActivityListHolder;
import net.twoant.master.ui.main.activity.ActionDetailActivity;
import net.twoant.master.ui.my_center.activity.ActivityManagerActivity;
import net.twoant.master.widget.CloseActivityDialog;
import net.twoant.master.widget.sidesliplistview.SwipeMenu;
import net.twoant.master.widget.sidesliplistview.SwipeMenuCreator;
import net.twoant.master.widget.sidesliplistview.SwipeMenuItem;
import net.twoant.master.widget.sidesliplistview.SwipeMenuListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class ShopBeingActivityFragment extends BaseFragment {
    private HintDialogUtil hintDialogUtil;
    private SwipeMenuListView listView;
    private String shop_id;

    private MyAdapter adapter;
    private String open_goodsid;
    private ActivityManagerActivity activity;
    private List<SellerManageListBean.DataBean> dataList;
    private Map<String,String> map = new HashMap<>();

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_seller_activitylist;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        activity = (ActivityManagerActivity) getActivity();
        shop_id = activity.getShopId();
        listView = (SwipeMenuListView) view.findViewById(net.twoant.master.R.id.listView_swipe_goods);

        hintDialogUtil = new HintDialogUtil(getActivity());
        dataList = new ArrayList<>();
        hintDialogUtil.showLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLineNet();
    }

    void requestLineNet(){
        Map<String,String> map = new HashMap();
        map.put("shop",shop_id);
        LongHttp(ApiConstants.SELLER_ACTIVTTY, "",map,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                try {
                    SellerManageListBean sellerManageListBean = JsonUtil.parseJsonToBean(response, SellerManageListBean.class);
                    dataList.clear();
                    dataList = sellerManageListBean.getData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new MyAdapter(dataList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_MERCHANT,getActivity(),dataList.get(position).getID()+"",1);
                    }
                });
                createSideslipMenu();
                hintDialogUtil.dismissDialog();
            }
        });
    }

    private void createSideslipMenu() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(getResources().getColor(net.twoant.master.R.color.listview_edit)));
                // set item width
                openItem.setWidth((int) getResources().getDimension(net.twoant.master.R.dimen.px_130));
                // set item title
                openItem.setTitle(" 关闭\r\n报名");
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
                LogUtils.d(index+"");
                switch (index) {
                    case 0:
                        if ("-1".equals(open_goodsid)) {
                            ToastUtil.showLong("关闭活动失败,请重试");
                        }else{
                            //关闭活动
                            final CloseActivityDialog closeActivityDialog = new CloseActivityDialog(getActivity(), Gravity.CENTER,false);
                            closeActivityDialog.showDialog(true,false);
                            closeActivityDialog.setOnClickListener(new CloseActivityDialog.IOnClickListener() {
                                @Override
                                public void onClickListener(View v) {
                                    closeActivityDialog.dismiss();
                                    //关闭活动操作
                                    hintDialogUtil.showLoading();
//                                    map.put("id",open_goodsid);
                                    map.put("id",dataList.get(position).getID()+"");
                                    LongHttp(ApiConstants.SELLER_ACTIVITY_CL, "", map, new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            hintDialogUtil.dismissDialog();
                                            ToastUtil.showLong("连接失败"+e);
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            try {
                                                hintDialogUtil.dismissDialog();
                                                JSONObject jsonObject = new JSONObject(response);
                                                String result = jsonObject.getString("result");
                                                if (result.contains("true")) {
                                                    ToastUtil.showLong("已关闭");
                                                    dataList.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                    activity.setTab2();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
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
                try {
                    open_goodsid = dataList.get(position).getID()+"";
                } catch (Exception e) {
                    e.printStackTrace();
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
                ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_MERCHANT,getActivity(),dataList.get(position).getID()+"",dataList.get(position).getSORT_ID());
            }
        });
    }

    class MyAdapter extends BasicAdapter<SellerManageListBean.DataBean> {

        public MyAdapter(List<SellerManageListBean.DataBean> list) {
            super(list);
        }

        @Override
        protected BaseHolder<SellerManageListBean.DataBean> getHolder(int position) {
            return new SellerActivityListHolder();
        }
    }

}

