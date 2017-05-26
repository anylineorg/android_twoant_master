package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.ActivityUseFinishedActivity;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class MyActivityFragment2 extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity2;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity2);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityUseFinishedActivity.startActivity(getActivity(), mDataRow.get(position).getString("ID"),mDataRow.get(position).getString("SORT_ID"));
            }
        });
        getSourceNet();
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                    android.R.color.holo_orange_light, android.R.color.holo_red_light);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getSourceNet();
            }
        });
    }

    void getSourceNet(){
        HashMap<String,String> map=new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp(ApiConstants.ACTIVITYNF, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
                ToastUtil.showLong(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                swipeRefreshLayout.setRefreshing(false);
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                listView.setAdapter(new MyAdapter());
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
            View view = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_activitylist_myactivity, null);
            TextView state = (TextView) view.findViewById(net.twoant.master.R.id.tv_state_myactivity);
            state.setVisibility(View.VISIBLE);
            TextView activity_name = (TextView) view.findViewById(net.twoant.master.R.id.tv_name_myactivity);
            TextView shop_tel = (TextView) view.findViewById(net.twoant.master.R.id.tv_tel_myactivity);
            TextView shop_name = (TextView) view.findViewById(net.twoant.master.R.id.tv_shopname_myactivity);
            TextView shop_address = (TextView) view.findViewById(net.twoant.master.R.id.tv_address_myactivity);
            ImageView imageView = (ImageView) view.findViewById(net.twoant.master.R.id.iv_head_img_myactivity);
            activity_name.setText(mDataRow.get(position).getString("TITLE"));
            shop_tel.setText(mDataRow.get(position).getString("SHOP_TEL"));
            shop_name.setText(mDataRow.get(position).getString("SHOP_NM"));
            shop_address.setText(mDataRow.get(position).getString("SHOP_ADDRESS"));
            ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(mDataRow.get(position).getString("COVER_IMG")),getActivity());
            return view;
        }
    }
}
