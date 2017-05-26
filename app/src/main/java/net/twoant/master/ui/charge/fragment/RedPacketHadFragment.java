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
import net.twoant.master.ui.charge.holder.RedFinishListHolder;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class RedPacketHadFragment extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_redactivity;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(net.twoant.master.R.id.lv_myactivity1);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(net.twoant.master.R.id.swipe_refresh_waitpay);
        hintDialogUtil = new HintDialogUtil(getActivity());

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
        HashMap<String,String> map=new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp( ApiConstants.REDUSEED,"", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                mDataRow = DataRow.parseJson(response).getSet("data").getRows();
                listView.setAdapter(new MyAdapter(mDataRow));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        ActivityUseDetailActivity.startActivity(getActivity());
                    }
                });
                hintDialogUtil.dismissDialog();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    class MyAdapter extends BasicAdapter<DataRow> {

        public MyAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new RedFinishListHolder();
        }
    }
}

