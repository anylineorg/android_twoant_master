package net.twoant.master.ui.my_center.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.my_center.activity.Fi_ShopActivity;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
public class fi_in_fragment extends ViewPagerBaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    TextView fi_sum_price;
    String shop_id="",ym;
    public Fi_ShopActivity fi_shopActivity;

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.fi_in_fragment;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.fi_in_listview);
        fi_sum_price = (TextView) view.findViewById(net.twoant.master.R.id.fi_sum_price);
        shop_id = getArguments().getString("shop_id");
        ym = getArguments().getString("ym");
        fi_shopActivity = (Fi_ShopActivity) getActivity();
    }

    HashMap<String,String> map = new HashMap<>();
    private String zongji = "";
    public void initData(){
        map.put("shop",shop_id);
        String month = fi_shopActivity.getMonth();
        String[] split = month.split("-");
        String s1 = split[0];
        String s2 = ((int)Integer.parseInt(split[1]))+"";
        if (Integer.parseInt(s2) <= 9) {
            month = s1+"-0"+s2;
        }
        map.put("ym",month);
        LongHttp(ApiConstants.SHOPMONEYIN, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                mDataRow= DataRow.parseJson(response).getSet("data").getRows();
                zongji=DataRow.parseJson(response).getSet("data").sum("SHOP_VAL")+"";
                fi_sum_price.setText(zongji);
                listView.setAdapter(new MyAdapter());
            }
        });
    }
    public void LongHttp(String url, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    @Override
    protected void onUserVisible() {

//        initData();
        map.put("shop",shop_id);
        LongHttp(ApiConstants.SHOPMONEYIN, map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                zongji=DataRow.parseJson(response).getSet("data").sum("SHOP_VAL")+"";
                fi_sum_price.setText(zongji);
                listView.setAdapter(new MyAdapter());
            }
        });
    }

    @Override
    protected void onUserInvisible() {

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
            View view = View.inflate(getActivity(), net.twoant.master.R.layout.fi_in_fragment_listitem, null);
            TextView time = (TextView) view.findViewById(net.twoant.master.R.id.time);
            ImageView img=(ImageView) view.findViewById(net.twoant.master.R.id.user_avatar);
            TextView sum_price = (TextView) view.findViewById(net.twoant.master.R.id.sum_price);
            TextView red_price = (TextView) view.findViewById(net.twoant.master.R.id.red_price);
            TextView sx_price = (TextView) view.findViewById(net.twoant.master.R.id.sx_price);
            TextView end_price = (TextView) view.findViewById(net.twoant.master.R.id.end_price);
            TextView product_name = (TextView) view.findViewById(net.twoant.master.R.id.product_name);
            ImageLoader.getImageFromNetwork(img,(BaseConfig.getCorrectImageUrl(mDataRow.get(position).getString("USER_AVATAR"))),getActivity());
            String timae=mDataRow.get(position).getString("PAY_TIME");
            String time_str=timae.split("-")[0]+"-"+timae.split("-")[1]+"-"+timae.split("-")[2];
            time.setText(time_str);
            sum_price.setText(mDataRow.get(position).getString("TOTAL_PRICE"));
            red_price.setText(mDataRow.get(position).getString("VOUCHER_VAL"));
            sx_price.setText(mDataRow.get(position).getString("SYSTEM_VAL"));
            end_price.setText(mDataRow.get(position).getString("SHOP_VAL"));
            product_name.setText(mDataRow.get(position).getString("USER_NM"));
            return view;
        }
    }
}
