package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.holder.RedListHolder;
import net.twoant.master.ui.main.activity.ActionDetailActivity;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class RedPacketHaveFragment extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private SwipeRefreshLayout refreshLayout;
    private HintDialogUtil hintDialogUtil;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_redactivity;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        hintDialogUtil = new HintDialogUtil(getActivity());
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                    android.R.color.holo_orange_light, android.R.color.holo_red_light);
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestNet();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requestNet();
        hintDialogUtil.showLoading();
    }

    public void requestNet(){
        HashMap<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp( ApiConstants.REDUSE,"", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (null != hintDialogUtil) {
                    hintDialogUtil.dismissDialog();
                }
                ToastUtil.showLong("连接失败");
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                listView.setAdapter(new MyAdapter(mDataRow));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_PURCHASER,getActivity(),mDataRow.get(position).getString("ACTIVITY_ID"), IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY);
                    }
                });
                if (null != hintDialogUtil) {
                    hintDialogUtil.dismissDialog();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != hintDialogUtil) {
            hintDialogUtil.dismissDialog();
            hintDialogUtil = null;
        }
    }

    class MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new RedListHolder();
        }
    }

}

