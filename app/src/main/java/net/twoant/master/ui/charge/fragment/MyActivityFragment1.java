package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.ui.charge.activity.ActivityUseDetailActivity;
import net.twoant.master.ui.charge.holder.MyAcitivityHolder;
import net.twoant.master.widget.entry.DataRow;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class MyActivityFragment1 extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected int getLayoutRes() {
        return R.layout.zy_fragment_myactivity1;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.lv_myactivity1);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_waitpay);
        hintDialogUtil = new HintDialogUtil(getActivity());
        hintDialogUtil.showLoading();

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

    @Override
    public void onResume() {
        super.onResume();
        getSourceNet();
    }

    void getSourceNet(){
        HashMap<String,String> map = new HashMap<>();
        String uid = AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID();
        map.put("user",uid );
        LongHttpGet(ApiConstants.ACTIVITYF+"?user="+uid, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                swipeRefreshLayout.setRefreshing(false);
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                listView.setAdapter(new MyAdapter(mDataRow));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActivityUseDetailActivity.startActivity(getActivity(), mDataRow.get(position).getString("ID"),mDataRow.get(position).getString("SORT_ID"));
                    }
                });
                hintDialogUtil.dismissDialog();
            }
        });
    }

    class  MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new MyAcitivityHolder();
        }
    }
}

