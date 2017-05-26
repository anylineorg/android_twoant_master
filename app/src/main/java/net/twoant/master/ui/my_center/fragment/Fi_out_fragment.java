package net.twoant.master.ui.my_center.fragment;

import android.content.IntentFilter;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.ViewPagerBaseFragment;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.ui.my_center.activity.Fi_ShopActivity;
import net.twoant.master.ui.my_center.activity.UpdateUIListenner;
import net.twoant.master.widget.entry.DataRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/24 0024.
 */
public class Fi_out_fragment extends ViewPagerBaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    String shop_id="",zongji;
    String ym="";
    HashMap<String,String> map = new HashMap<>();
    TextView fi_sum_price;
    public Fi_ShopActivity fi_shopActivity;
    public GetCashBroadcastReceiver myBroadcastReceiver;

    @Override
    protected int getLayoutRes() {
        return R.layout.fi_out_fragment;
    }


    public void LongHttp(String url, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    public void initData(){
        //注册广播  ------接口回调不能使用静态注册,只能动态注册
        myBroadcastReceiver = new GetCashBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("getcash");
        getActivity().registerReceiver(myBroadcastReceiver, intentFilter);
        myBroadcastReceiver.SetOnUpdateUIListenner(new UpdateUIListenner() {
            @Override
            public void UpdateUI(String str) {
                requestNetData();
            }
        });
        requestNetData();
    }

    private void requestNetData(){
        map.put("shop",shop_id);
        String month;
        if (null!=fi_shopActivity) {
            month = fi_shopActivity.getMonth();
            String[] split = month.split("-");
            String s1 = split[0];
            String s2 = (Integer.parseInt(split[1]))+"";
            if (Integer.parseInt(s2) <= 9) {
                month = s1+"-0"+s2;
            }
            map.put("ym",month);
            LongHttp(ApiConstants.SHOPTIXIAN, map, new StringCallback() {
                double doubl = 0;
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                    zongji = DataRow.parseJson(response).getSet("data").sum("VAL")+"";

                    for (int i = 0; i < mDataRow.size() ;i++){
                        if (mDataRow.get(i).getInt("EXE_STATUS")==-1) {
                            doubl += Double.parseDouble(mDataRow.get(i).getString("VAL"));
                        };
                    }
                    fi_sum_price.setText("-"+(Double.parseDouble(zongji)-doubl));
                    listView.setAdapter(new MyAdapter());
                }
            });
        }
    }
    @Override
    protected void initFragmentComponentsData(View view) {
        shop_id = getArguments().getString("shop_id");
        ym = getArguments().getString("ym");
        fi_sum_price = (TextView) view.findViewById(R.id.fi_sum_price);
        listView = (ListView) view.findViewById(R.id.fi_out_listview);
        fi_shopActivity = (Fi_ShopActivity) getActivity();

        map.put("shop",shop_id);
        if (null != fi_shopActivity) {
            LongHttp(ApiConstants.SHOPTIXIAN, map, new StringCallback() {
                double doubl = 0;
                @Override
                public void onError(Call call, Exception e, int id) {

                }
                @Override
                public void onResponse(String response, int id) {
                    mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                    zongji = DataRow.parseJson(response).getSet("data").sum("VAL")+"";

                    for (int i = 0; i < mDataRow.size() ;i++){
                        if (mDataRow.get(i).getInt("EXE_STATUS")==-1) {
                            doubl += Double.parseDouble(mDataRow.get(i).getString("VAL"));
                        };
                    }
                    fi_sum_price.setText("-"+(Double.parseDouble(zongji)-doubl));
                    listView.setAdapter(new MyAdapter());
                }
            });
        }
    }

    @Override
    protected void onUserVisible() {

    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != myBroadcastReceiver ) {
            getActivity().unregisterReceiver(myBroadcastReceiver);
        }
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
            View view = View.inflate(getActivity(), R.layout.fi_out_fragment_listitem, null);
            TextView time = (TextView) view.findViewById(R.id.tx_time);
            TextView tx_name = (TextView) view.findViewById(R.id.tx_name);
            TextView tx_price = (TextView) view.findViewById(R.id.tx_price);
            TextView tx_tip = (TextView) view.findViewById(R.id.tx_tip);
            time.setText(mDataRow.get(position).getString("CREATE_TIME"));
            int data_flag = mDataRow.get(position).getInt("DATA_FLAG");
            if (data_flag != 1) {
                tx_name.setText(mDataRow.get(position).getInt("TAR")==0?"提现-银行卡":"提现-钱包");
            }else{//手续费
                tx_name.setText("提现-手续费");
            }
            if (mDataRow.get(position).getInt("EXE_STATUS")==1){//提现成功
                tx_price.setText("-"+mDataRow.get(position).getString("VAL")+"");
                if (data_flag != 1) {
                    tx_tip.setText("提现成功");
                }else{//手续费
                    tx_tip.setText("");
                }
                tx_tip.setTextColor(CommonUtil.getColor(R.color.subordinationTitleTextColor));
            }else if (mDataRow.get(position).getInt("EXE_STATUS")==-1) {//提现失败
                tx_price.setText("-"+mDataRow.get(position).getString("VAL")+"");
                tx_tip.setText("提现失败:"+mDataRow.get(position).getString("EXE_MSG"));
                tx_tip.setTextColor(CommonUtil.getColor(R.color.subordinationTitleTextColor));
                tx_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }else if (mDataRow.get(position).getInt("EXE_STATUS")==0) {//提现中
                tx_price.setText("-"+mDataRow.get(position).getString("VAL")+"");
                if (data_flag != 1) {
                    tx_tip.setText("预计5个工作日到账");
                }else{//手续费
                    tx_tip.setText("");
                }
                tx_tip.setTextColor(CommonUtil.getColor(R.color.red_f9));
            }
            return view;
        }
    }
}
