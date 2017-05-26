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
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.charge.activity.GoodsHadPayOrderDetailActivity;
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

public class HadPayFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private ListView listView;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;
    public List<DataRow> rowsList;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity2;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
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


    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        String uid = AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID();
        map.put("user",uid );
        LongHttp(ApiConstants.PERSON_WATI_GOODS,"", map,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
                DataRow dataRow = DataRow.parseJson(response);
                DataSet data = dataRow.getSet("data");
                rowsList = data.getRows();
                listView.setAdapter(myAdapter);
                hintDialogUtil.dismissDialog();
            }
        });
    }

    private void initData() {
        myAdapter = new MyAdapter();
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

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View view = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_waitpay,null);
                holder = new ViewHolder();
                holder.shopName =  (TextView) convertView.findViewById(net.twoant.master.R.id.tv_shopname_waitpay);
                holder.goodsMoney = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_itemprice_waitpay);
                holder.rl_shop_waitpay = (RelativeLayout) convertView.findViewById(net.twoant.master.R.id.rl_shop_waitpay);
                holder.type = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_item_type_waitpay);
                holder.totalNum = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_item_count_waitpay);
                holder.rlState = (RelativeLayout) convertView.findViewById(net.twoant.master.R.id.rl_item_state_waitpay);
                holder.contain = (LinearLayout) convertView.findViewById(net.twoant.master.R.id.ll_item_contain_waitpay);
                DataSet items = rowsList.get(position).getSet("ITEMS");
                for (int i = 0 ; i < items.size();i++){
                    final DataRow row = items.getRow(i);
                    View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_goods_waitpay, null);
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
                    inflate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),GoodsHadPayOrderDetailActivity.class);
                            intent.putExtra("waitpay_id",rowsList.get(position).getString("ID"));
                            startActivity(intent);
                        }
                    });
                    holder.contain.addView(inflate);
                }
                convertView.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.shopName.setText(rowsList.get(position).getString("SHOP_SELLER_NM"));
            holder.goodsMoney.setText("¥"+rowsList.get(position).getString("TOTAL_PRICE"));
            holder.type.setText("买家已付款");
            String total = rowsList.get(position).getSet("ITEMS").sum("QTY")+"";
            LogUtils.d(total);
            holder.totalNum.setText(total);
            holder.rlState.setVisibility(View.GONE);
            holder.rl_shop_waitpay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),GoodsHadPayOrderDetailActivity.class);
                    intent.putExtra("waitpay_id",rowsList.get(position).getString("ID"));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        TextView shopName,type,totalNum;
        TextView goodsMoney;
        LinearLayout contain;
        RelativeLayout rlState,rl_shop_waitpay;
    }

}

