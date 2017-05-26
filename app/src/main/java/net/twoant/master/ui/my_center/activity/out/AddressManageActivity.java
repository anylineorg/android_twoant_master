package net.twoant.master.ui.my_center.activity.out;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.AddressListBean;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.ScrollViewListView;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/6.
 * 佛祖保佑   永无BUG
 */
public class AddressManageActivity extends LongBaseActivity implements View.OnClickListener{
    private ScrollViewListView listView;
    private MyAdapter adapter;
    private List<AddressListBean.ResultBean> resultList;
    private HintDialogUtil hintDialogUtil;
    private Map<Integer, Boolean> isSelected;
    private CancelCenterDialog cancelCenterDialog;
    private List beSelectedData = new ArrayList();
    public int position;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_addressmanage;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        hintDialogUtil = new HintDialogUtil(this);
        cancelCenterDialog = new CancelCenterDialog(this, Gravity.CENTER,false);
        listView = (ScrollViewListView) findViewById(net.twoant.master.R.id.lv_address_addressmanage);
        findViewById(net.twoant.master.R.id.btn_register_addressmanege).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<>();

        // 清除已经选择的项
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNet();
    }


    protected void requestNet() {
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap();
        map.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());
        LongHttp("select_receipt.do", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("请求失败");
                LogUtils.d("s");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                AddressListBean addressListBean = GsonUtil.gsonToBean(response, AddressListBean.class);
                resultList = addressListBean.getResult();
                adapter = new MyAdapter(AddressManageActivity.this,resultList);
                listView.setAdapter(adapter);
                //全部cb先存入false状态
                for (int i = 0; i < resultList.size(); i++) {
                    int receipt_default = resultList.get(i).getReceipt_default();
                    if (1 == receipt_default) {
                        isSelected.put(i, true);
                    }else{
                        isSelected.put(i, false);
                    }
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_register_addressmanege:
                startActivity(new Intent(AddressManageActivity.this,AddAddressActivity.class));
                break;
        }
    }

    class MyAdapter extends BaseAdapter{

        private Context context;
        private List<AddressListBean.ResultBean> resultList;

        public MyAdapter(Context context, List<AddressListBean.ResultBean> resultList) {
            this.context = context;
            this.resultList = resultList;
        }

        public int getCount() {
            return resultList.size();
        }

        public Object getItem(int position) {
            return resultList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(final int position1, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            View view = null;
            if (convertView == null) {
                convertView = View.inflate(context, net.twoant.master.R.layout.zy_item_address_addressmanege,null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView .findViewById(net.twoant.master.R.id.tv_item_name_addressmanage);
                holder.tel = (TextView) convertView .findViewById(net.twoant.master.R.id.tv_item_tel_addressmanage);
                holder.address = (TextView) convertView .findViewById(net.twoant.master.R.id.tv_item_address_addressmanage);
                holder.del = (TextView) convertView .findViewById(net.twoant.master.R.id.tv_delete_addressmanage);
                holder.checkBox = (CheckBox) convertView .findViewById(net.twoant.master.R.id.ch_item_addressmanage);
                convertView.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    map.put("id",resultList.get(position1).getId()+"");
                    hintDialogUtil.showLoading();
                    LongHttp(ApiConstants.SET_DEFAULT_ADDRESS, "", map, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                            hintDialogUtil.dismissDialog();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            LogUtils.d(response);
                            boolean result = DataRow.parseJson(response).getBoolean("result",false);
                            if (result) {
                                hintDialogUtil.dismissDialog();
                                // 当前点击的CB
                                boolean cu = !isSelected.get(position1);
                                // 先将所有的置为FALSE
                                for(Integer p : isSelected.keySet()) {
                                    isSelected.put(p, false);
                                }
                                // 再将当前选择CB改为实际状态
                                isSelected.put(position1, cu);
                                MyAdapter.this.notifyDataSetChanged();
                                //beSelectedData集合存放cb选中的那个item
                                beSelectedData.clear();
                                if(cu) beSelectedData.add(resultList.get(position1));
                            }
                        }
                    });
                }
            });
            holder.name.setText("收货人:"+resultList.get(position1).getReceipt_name());
            holder.tel.setText(resultList.get(position1).getReceipt_tel());
            holder.address.setText("收货地址:"+resultList.get(position1).getReceipt_address());
            holder.checkBox.setChecked(isSelected.get(position1));
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelCenterDialog.showDialog(true,true);
                    cancelCenterDialog.setTitle("删除地址");
                    cancelCenterDialog.setOnClickListener(new CancelCenterDialog.IOnClickListener() {
                        @Override
                        public void onClickListener(View v) {
                            requestNet2DelAddress(resultList.get(position1).getId()+"");
                            cancelCenterDialog.dismiss();
                        }
                    });
                }
            });
            return convertView;
        }
    }

    private void requestNet2DelAddress(String addressId) {
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("id",addressId);
        LongHttp(ApiConstants.DEL_ADDRESS, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("网络故障");
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                Boolean success = dataRow.getBoolean("success",false);
                if (success) {
                    requestNet();
                }else {
                    ToastUtil.showLong(dataRow.getString(dataRow.getString("success")));
                }
            }
        });
    }

    class ViewHolder {

        CheckBox checkBox;

        TextView name,tel,address,del;

    }
}


