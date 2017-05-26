package net.twoant.master.ui.charge.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
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

public class ShopHadPayFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private ListView listView;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;
    private List<DataRow> rowsList;
    private ShopOrderActivity shopOrderActivity;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity2;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        shopOrderActivity = (ShopOrderActivity) getActivity();
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity2);
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

    }

    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        map.put("shop",shopOrderActivity.getShop_id() );
        map.put("pay","1" );//0:未付款  1:已付款
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
                myAdapter = new MyAdapter(rowsList);
                listView.setAdapter(myAdapter);
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        getSourceNet();
    }

    class MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new ShopHadPayHolder();
        }
    }

    class ShopHadPayHolder extends BaseHolder<DataRow>{

        View view;
        TextView bugName,goodsMoney,type,totalNum;
        LinearLayout contain;
        ImageView bugImg;
        RelativeLayout state;
        @Override
        public View initHolderView() {
            view = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_shopwaitpay,null);
            bugName =  (TextView) view.findViewById(net.twoant.master.R.id.tv_shopname_waitpay);
            goodsMoney = (TextView) view.findViewById(net.twoant.master.R.id.tv_itemprice_waitpay);
            type = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_type_waitpay);
            totalNum = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_count_waitpay);
            contain = (LinearLayout) view.findViewById(net.twoant.master.R.id.ll_item_contain_waitpay);
            bugImg = (ImageView) view.findViewById(net.twoant.master.R.id.iv_aa);
            state = (RelativeLayout) view.findViewById(net.twoant.master.R.id.rl_item_state_waitpay);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            DataSet items = data.getSet("ITEMS");
            contain.removeAllViews();
            for (int i = 0 ; i < items.size();i++){
                DataRow row = items.getRow(i);
                View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_goods_waitpay, null);
                inflate.setTag(data.getString("ID"));
                inflate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ShopGoodsOrderDetailActivity.class);
                        intent.putExtra("waitpay_id",v.getTag()+"");
                        startActivity(intent);
                    }
                });
                ImageView itemImg = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_goodsimg_postorder);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(itemImg, BaseConfig.getCorrectImageUrl(row.getString("GOODS_IMG")),getActivity(), net.twoant.master.R.drawable.ic_def_small);
                TextView itemGoodsName = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_goodsname_waitpay);
                itemGoodsName.setText(row.getString("GOODS_NM"));
                TextView itemGoodsDetail = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_detail_waitpay);
                itemGoodsDetail.setText(row.getString("GOODS_LD"));
                TextView itemGoodsMoney = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_price_waitpay);
                itemGoodsMoney.setText("¥"+row.getString("PRICE"));
                TextView itemGoodsNum = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_num_waitpay);
                itemGoodsNum.setText("x"+row.getString("QTY"));
                contain.addView(inflate);
            }
            String total = items.sum("QTY")+"";
            totalNum.setText(total);
            bugName.setText(data.getString("BUYER_NM"));
            goodsMoney.setText("¥"+data.getString("TOTAL_PRICE"));
            state.setVisibility(View.GONE);
            ImageLoader.getImageFromNetworkControlImg(bugImg,BaseConfig.getCorrectImageUrl(data.getString("BUYER_AVATAR")),getActivity(), net.twoant.master.R.drawable.ic_def_circle);
            type.setText("");
        }
    }
   /* class MyAdapter extends BaseAdapter {

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
                convertView = View.inflate(getActivity(), R.layout.zy_item_shopwaitpay,null);
                holder = new ViewHolder();
                holder.bugName =  (TextView) convertView.findViewById(R.id.tv_shopname_waitpay);
                holder.goodsMoney = (TextView) convertView.findViewById(R.id.tv_itemprice_waitpay);
                holder.type = (TextView) convertView.findViewById(R.id.tv_item_type_waitpay);
                holder.totalNum = (TextView) convertView.findViewById(R.id.tv_item_count_waitpay);
                holder.contain = (LinearLayout) convertView.findViewById(R.id.ll_item_contain_waitpay);
                holder.bugImg = (ImageView) convertView.findViewById(R.id.iv_aa);
                holder.state = (RelativeLayout) convertView.findViewById(R.id.rl_item_state_waitpay);
                DataSet items = rowsList.get(position).getSet("ITEMS");
                if (null!=items && items.size()>0) {
                    for (int i = 0 ; i < items.size();i++){
                        DataRow row = items.getRow(i);
                        View inflate = View.inflate(getActivity(), R.layout.zy_item_goods_waitpay, null);
                        inflate.setTag(rowsList.get(position).getString("ID"));
                        inflate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShopGoodsOrderDetailActivity.class);
                                intent.putExtra("waitpay_id",v.getTag()+"");
                                startActivity(intent);
                            }
                        });
                        ImageView itemImg = (ImageView) inflate.findViewById(R.id.iv_goodsimg_postorder);
                        ImageLoader.getImageFromNetwork(itemImg, ApiConstants.GUO+row.getString("GOODS_IMG"));
                        TextView itemGoodsName = (TextView) inflate.findViewById(R.id.tv_goodsname_waitpay);
                        itemGoodsName.setText(row.getString("GOODS_NM"));
                        TextView itemGoodsDetail = (TextView) inflate.findViewById(R.id.tv_detail_waitpay);
                        itemGoodsDetail.setText(row.getString("GOODS_LD"));
                        TextView itemGoodsMoney = (TextView) inflate.findViewById(R.id.tv_price_waitpay);
                        itemGoodsMoney.setText("¥"+row.getString("PRICE"));
                        TextView itemGoodsNum = (TextView) inflate.findViewById(R.id.tv_num_waitpay);
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
            ImageLoader.getImageFromNetwork(holder.bugImg,ApiConstants.GUO+rowsList.get(position).getString("BUYER_AVATAR"));
            holder.type.setText("");
            return convertView;
        }
    }*/

    /*class ViewHolder {
        TextView bugName,type,totalNum;
        TextView goodsMoney;
        LinearLayout contain;
        RelativeLayout state;
        ImageView bugImg;
    }*/

}

