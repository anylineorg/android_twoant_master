package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.WaitPayBean;
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

public class WaitSendFragment extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
    public List<WaitPayBean.DataBean> dataList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyAdapter myAdapter;
    public List<DataRow> rowsList;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity2;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        hintDialogUtil = new HintDialogUtil(getActivity());
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        hintDialogUtil.showLoading();
        getSourceNet();
    }


    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        String uid = AiSouAppInfoModel.getInstance().getUID();
        map.put("user",uid );
        LongHttp(ApiConstants.PERSON_WATI_GOODS,"", map,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("连接失败："+e);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
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
                convertView = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_waitpay,null);
                holder = new ViewHolder();
                holder.shopName =  (TextView) convertView.findViewById(net.twoant.master.R.id.tv_shopname_waitpay);
                holder.goodsMoney = (TextView) convertView.findViewById(net.twoant.master.R.id.tv_itemprice_waitpay);
                holder.contain = (LinearLayout) convertView.findViewById(net.twoant.master.R.id.ll_item_contain_waitpay);
                DataSet items = rowsList.get(position).getSet("ITEMS");
                for (int i = 0 ; i < items.size();i++){
                    DataRow row = items.getRow(i);
                    View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_goods_waitpay, null);
                    ImageView itemImg = (ImageView) inflate.findViewById(net.twoant.master.R.id.iv_goodsimg_postorder);
                    TextView itemGoodsName = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_goodsname_waitpay);
                    itemGoodsName.setText(row.getString("GOODS_NM"));
                    TextView itemGoodsDetail = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_detail_waitpay);
//                    itemGoodsDetail.setText(row.getString("GOODS_NM"));
                    TextView itemGoodsMoney = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_price_waitpay);
                    itemGoodsMoney.setText("¥"+row.getString("PRICE"));
                    TextView itemGoodsNum = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_num_waitpay);
                    itemGoodsNum.setText("x"+row.getString("QTY"));
                    holder.contain.addView(inflate);
                }
                convertView.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.shopName.setText(rowsList.get(position).getString("SHOP_SELLER_NM"));
            holder.goodsMoney.setText(rowsList.get(position).getString("TOTAL_PRICE"));
            return convertView;
        }

    }

    class ViewHolder {
        TextView shopName;
        TextView goodsMoney;
        LinearLayout contain;
    }
}

