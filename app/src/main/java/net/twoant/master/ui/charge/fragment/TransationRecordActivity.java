package net.twoant.master.ui.charge.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.WalletActivity;
import net.twoant.master.ui.charge.holder.TransationHolder;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.timer.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */
public class TransationRecordActivity extends LongBaseActivity implements View.OnClickListener {
    private TimePickerView pvTime;
    private ListView listView;
    private TransationAdapter transationAdapter;
    private HintDialogUtil hintDialogUtil;
    private TextView month;
    public List<DataRow> data;
    private String time ;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_transation_record;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        listView = (ListView) findViewById(net.twoant.master.R.id.lv_listrecord_transation);
        month = (TextView) findViewById(net.twoant.master.R.id.tv_month_transation);
        findViewById(R.id.iv_month_transation_record).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.showLoading();
        // 时间选择器qb_mouth
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        month.setText(sdf.format(new Date()));
        // 时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener(){
            @Override
            public void onTimeSelect(Date date){
                String t = WalletActivity.getTime(date);
                int tp=Integer.parseInt( t.split("-")[1]);
                int tempYear=Integer.parseInt( t.split("-")[0]);
                tp++;
                if (tp == 13) {
                    tp=1;
                    time=tempYear+1+"-"+tp;
                }else{
                    time = t.split("-")[0]+"-"+tp;
                }
                month.setText(time);
                requestMonth(time);
                System.out.println(time);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestNetData();
    }

    @Override
    protected void requestNetData() {
        Map map = new HashMap();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.TRANSATION_RECORD, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                data = DataRow.parseJson(response).getSet("data").getRows();
                transationAdapter = new TransationAdapter(data);
                listView.setAdapter(transationAdapter);
                transationAdapter.notifyDataSetChanged();
                hintDialogUtil.dismissDialog();
            }
        });
    }

    private void requestMonth(String time) {
        hintDialogUtil.showLoading();
        Map map = new HashMap();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        String[] split = time.split("-");
        String s1 = split[0];
        String s2 = split[1];
        if (Integer.parseInt(s2) <= 9) {
            time = s1+"-0"+s2;
        }
        map.put("ym", time);
        LongHttp(ApiConstants.TRANSATION_RECORD, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                data = DataRow.parseJson(response).getSet("data").getRows();
                transationAdapter = new TransationAdapter(data);
                listView.setAdapter(transationAdapter);
                transationAdapter.notifyDataSetChanged();
                hintDialogUtil.dismissDialog();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_month_transation_record:
                pvTime.show();
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    class TransationAdapter extends BasicAdapter<DataRow> {

        public TransationAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new TransationHolder();
        }
    }
}
