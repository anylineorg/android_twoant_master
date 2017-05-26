package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.activity.out.SellerManagerActivity;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
public class ManagerShopList extends LongBaseActivity {
    public static String from_mymessage="0";
    private List<DataRow> mDataRow;
    private ListView listView;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_manager;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {


        listView=(ListView) findViewById(net.twoant.master.R.id.manager_list);
        TextView tip=(TextView)findViewById(net.twoant.master.R.id.title_bar).findViewById(net.twoant.master.R.id.tv_Title);
        if ("1".equals(from_mymessage)) {
            tip.setText("商家管理");
        }else{
            tip.setText("我管理的商家");
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if ("1".equals(from_mymessage)) {
                    int index= mDataRow.get(position).getInt("IS_REVIEWED");
                    if (index!=2){
                        new HintDialogUtil(ManagerShopList.this).showError(index==1?"审核中":"未审核通过");
                        return;
                    }
                }
               // Intent intent=new Intent(ManagerShopList.this,Fi_ShopActivity.class);//SellerManagerActivity.class);
                Intent intent = new Intent(ManagerShopList.this,SellerManagerActivity.class);//SellerManagerActivity.class);
                intent.putExtra("from_mymessage",from_mymessage);
                String id1 = "";
                if ("1".equals(from_mymessage)) {
                    id1 = mDataRow.get(position).getString("ID");
                }else{
                    id1 = mDataRow.get(position).getString("SHOP_ID");
                }
                LogUtils.d(id1+"");
                intent.putExtra("shop_id",id1);
                PublishDetailActivity.SHOP_ADDRESS = mDataRow.get(position).getString("SHOP_ADDRESS");
                startActivity(intent);
            }
        });
        View toolbar = findViewById(net.twoant.master.R.id.title_bar).findViewById(net.twoant.master.R.id.iv_back);
        if (toolbar!=null) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ManagerShopList.this.finish();
                }
            });
        }
    }
    @Override
    protected void initData() {

        HashMap<String,String> map = new HashMap<>();

        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        //0我管理的商家 1 商家管理
        String url_str=from_mymessage.equals("0")? ApiConstants.MANAGERSHOP: ApiConstants.MYSHOPMANAGER;
        LongHttp(url_str, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d("输出:"+response);
                try {
                    mDataRow=DataRow.parseJson(response).getSet("data").getRows();
                    if (mDataRow!=null && mDataRow.size()>0){
                        listView.setAdapter(new MyAdapter());
                    }else{
                        ToastUtil.showShort("暂无店铺");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataRow.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), net.twoant.master.R.layout.manager_admin_listitem, null);
            ImageView  shop_icon= (ImageView) view.findViewById(net.twoant.master.R.id.shop_icon);
            TextView shop_time = (TextView) view.findViewById(net.twoant.master.R.id.shop_time);
            TextView shop_name = (TextView) view.findViewById(net.twoant.master.R.id.shop_name);
            TextView shop_address = (TextView) view.findViewById(net.twoant.master.R.id.shop_address);
            shop_time.setText(mDataRow.get(position).getString("TIME"));
            shop_name.setText(mDataRow.get(position).getString("SHOP_NAME"));
            String shop_address1 = mDataRow.get(position).getString("SHOP_ADDRESS");
            if (TextUtils.isEmpty(shop_address1) || "null".equals(shop_address1) ) {
                shop_address.setVisibility(View.GONE);
            }else{
                shop_address.setVisibility(View.VISIBLE);
                shop_address.setText("地址:"+shop_address1);
            }
            ImageLoader.getImageFromNetwork(shop_icon, BaseConfig.getCorrectImageUrl(mDataRow.get(position).getString("SHOP_AVATAR")));
            return view;
        }
    }
}
