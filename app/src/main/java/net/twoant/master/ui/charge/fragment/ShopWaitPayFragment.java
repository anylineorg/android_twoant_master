package net.twoant.master.ui.charge.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.ShopGoodsOrderDetailActivity;
import net.twoant.master.ui.my_center.activity.out.ShopOrderActivity;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class ShopWaitPayFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private ListView listView;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;
    private List<DataRow> rowsList;
    private ShopOrderActivity shopOrderActivity;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity1;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        shopOrderActivity = (ShopOrderActivity) getActivity();
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        swipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary, net.twoant.master.R.color.aquaEmbellishColor, net.twoant.master.R.color.pickerview_timebtn_pre);
        swipeRefreshLayout.setOnRefreshListener(this);
        // 这句话是为了，第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        hintDialogUtil = new HintDialogUtil(getActivity());
        hintDialogUtil.showLoading();
        initData();
        getSourceNet();
    }

    private void initData() {
        myAdapter = new MyAdapter();
    }


    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        map.put("shop",shopOrderActivity.getShop_id() );
        map.put("pay","0" );//0:未付款  1:已付款
        LongHttp(ApiConstants.SHOP_WATI_PAY, "",map,new StringCallback() {
//        LongHttp("http://192.168.11.200:7200/shp/od/jl", "",map,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                swipeRefreshLayout.setRefreshing(false);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                swipeRefreshLayout.setRefreshing(false);
                DataRow dataRow = DataRow.parseJson(response);
                DataSet data = dataRow.getSet("data");
                rowsList = data.getRows();
                if (rowsList.size() > 0) {
                    listView.setAdapter(myAdapter);
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        getSourceNet();
    }

    class MyAdapter extends BaseAdapter {

        public int getCount() {
            return rowsList.size();
        }

        public Object getItem(int position) {
            return rowsList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View view = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_shopwaitpay,null);
                holder = new ViewHolder();
                holder.bugName =  (TextView) convertView.findViewById(net.twoant.master.R.id.tv_shopname_waitpay);
                holder.goodsMoney = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_itemprice_waitpay);
                holder.type = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_item_type_waitpay);
                holder.totalNum = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_item_count_waitpay);
                holder.bugImg = (ImageView) convertView.findViewById(net.twoant.master.R.id.iv_aa);
                holder.contain = (LinearLayout) convertView.findViewById(net.twoant.master.R.id.ll_item_contain_waitpay);
                holder.state = (RelativeLayout) convertView.findViewById(net.twoant.master.R.id.rl_item_state_waitpay);
                DataSet items = rowsList.get(position).getSet("ITEMS");
                if (null!=items && items.size()>0) {
                    for (int i = 0 ; i < items.size();i++){
                        DataRow row = items.getRow(i);
                        View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_goods_waitpay, null);
                        inflate.setTag(rowsList.get(position).getString("ID"));
                        inflate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShopGoodsOrderDetailActivity.class);
                                intent.putExtra("waitpay_id",v.getTag()+"");
                                intent.putExtra("isshop_waitpay_enjoy",true);
                                startActivity(intent);
                            }
                        });
                        ImageView itemImg = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_goodsimg_postorder);
                        ImageLoader.getImageFromNetwork(itemImg, BaseConfig.getCorrectImageUrl(row.getString("GOODS_IMG")),getActivity());
                        TextView itemGoodsName = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_goodsname_waitpay);
                        itemGoodsName.setText(row.getString("GOODS_NM"));
                        TextView itemGoodsDetail = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_detail_waitpay);
                        itemGoodsDetail.setText(row.getString("GOODS_LD"));
                        TextView itemGoodsMoney = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_price_waitpay);
                        itemGoodsMoney.setText("¥"+row.getString("PRICE"));
                        TextView itemGoodsNum = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_num_waitpay);
                        itemGoodsNum.setText("x"+row.getString("QTY"));
                        holder.contain.addView(inflate);
                    }
                }
                convertView.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            DataSet items = rowsList.get(position).getSet("ITEMS");
            if (null != items) {
                String total = items.sum("QTY")+"";
                holder.totalNum.setText(total);
            }
            holder.bugName.setText(rowsList.get(position).getString("BUYER_NM"));
            holder.goodsMoney.setText("¥"+rowsList.get(position).getString("TOTAL_PRICE"));
            holder.state.setVisibility(View.GONE);
            ImageLoader.getImageFromNetwork(holder.bugImg, BaseConfig.getCorrectImageUrl(rowsList.get(position).getString("BUYER_AVATAR")),getActivity());
            holder.type.setText("等待买家付款");
            return convertView;
        }

    }

    class ViewHolder {
        TextView bugName,type,totalNum;
        TextView goodsMoney;
        LinearLayout contain;
        RelativeLayout state;
        ImageView bugImg;
    }

}

