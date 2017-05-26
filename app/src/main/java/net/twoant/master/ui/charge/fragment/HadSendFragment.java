package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseFragment;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.widget.entry.DataRow;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

//import net.twoant.model.ui.charge.holder.MyAcitivityHolder;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class HadSendFragment extends BaseFragment {
    private ListView listView;
    private List<DataRow> mDataRow;
    private HintDialogUtil hintDialogUtil;
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_myactivity2;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        listView = (ListView) view.findViewById(R.id.lv_myactivity1);
        hintDialogUtil = new HintDialogUtil(getActivity());
        hintDialogUtil.showLoading();
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
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
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
            return null;//new MyAcitivityHolder();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时加载数据
        } else {
            //不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

}

