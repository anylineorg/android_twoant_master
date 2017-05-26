package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.ErrorPayPasswordDialog;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.ScrollViewListView;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class ActivityUseFinishedActivity extends LongBaseActivity implements View.OnClickListener{

    private PassViewDialog passViewDialog;
    private TextView titleActivity;
    private TextView shopName;
    private TextView useTime;
    private TextView shopTel,shopAddress;
    private ScrollViewListView lvHistory;
//    private  List<MyActivityDetailFinishBean.DataBean.ITEMSBean> items;
    private Map<Integer, Boolean> isSelected;//判断checkbox
    private List beSelectedData = new ArrayList();
    private static String parent_activity_id;
    private static int type;
//    private MyAdapter adapter;
    private HintDialogUtil hintDialogUtil;
    private String data;//是否有支付密码 1有; 2没有
    private HintDialogUtil stateDialog;
    private List<String> itemTitleList;//存放item卡的标题名
    private int itemSlectedPosition = 0;//item默认选中第一个
    private  List<DataRow> uses;//历史记录集合
    private MyActicityHistoryAdapter historyAdapter;
    private ErrorPayPasswordDialog errorPayPasswordDialog;

    public static void startActivity(Context context){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, ActivityUseFinishedActivity.class);
        activity.startActivity(intent);
    }
    public static void startActivity(Context context,String p_id,String typestr){
        Activity activity = (Activity) context;
        parent_activity_id = p_id;
        type = Integer.parseInt(typestr);
        Intent intent = new Intent(context, ActivityUseFinishedActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return net.twoant.master.R.layout.zy_activity_activityusefinish;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.ll_shop_activityusedetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        titleActivity = (TextView) findViewById(net.twoant.master.R.id.tv_title_activityusedetail);
        shopName = (TextView) findViewById(net.twoant.master.R.id.tv_shopname_activityusedetail);
        useTime = (TextView) findViewById(net.twoant.master.R.id.tv_time_activityusedetail);
        shopTel = (TextView) findViewById(net.twoant.master.R.id.tv_tel_activityusedetail);
        shopAddress = (TextView) findViewById(net.twoant.master.R.id.tv_address_activityusedetail);
        lvHistory = (ScrollViewListView) findViewById(net.twoant.master.R.id.lv_history_activityusedetail);
        errorPayPasswordDialog = new ErrorPayPasswordDialog(this,Gravity.CENTER);
        passViewDialog = new PassViewDialog(ActivityUseFinishedActivity.this, Gravity.BOTTOM, true);
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), strPassword, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hintDialogUtil.dismissDialog();
                        ToastUtil.showLong("连接失败:"+e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = (boolean) dataRow.get("success");
                        if (success) {
                            hintDialogUtil.showLoading(net.twoant.master.R.string.use_activity);
                        }else{
                            hintDialogUtil.dismissDialog();
                            passViewDialog.clearn();
                            passViewDialog.cancel();
                            errorPayPasswordDialog.showDialog(true,true);
                            errorPayPasswordDialog.setReTry(new CancelCenterDialog.IOnClickListener() {
                                @Override
                                public void onClickListener(View v) {
                                    passViewDialog.showDialog(true,true);
                                }
                            });
//                            ToastUtil.showLong("支付密码错误,请重新输入");
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void initData() {
        hintDialogUtil = new HintDialogUtil(this);
        itemTitleList = new ArrayList<>();
        stateDialog = new HintDialogUtil(ActivityUseFinishedActivity.this);
        if (isSelected != null)
            isSelected = null;
        isSelected = new HashMap<>();
        // 清除已经选择的项
        if (beSelectedData.size() > 0) {
            beSelectedData.clear();
        }
    }

    @Override
    protected void requestNetData() {
        hintDialogUtil.showLoading();
        String url = ApiConstants.ACTIVITY_DETAIL;
        url = String.format(url, AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(),parent_activity_id);
        LongHttpGet(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("连接失败"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
//                MyActivityDetailFinishBean myActivityDetailBean = JsonUtil.parseJsonToBean(response,MyActivityDetailFinishBean.class);
//                MyActivityDetailFinishBean.DataBean data = myActivityDetailBean.getData();
                DataRow dataRow = DataRow.parseJson(response);
                DataRow data = dataRow.getRow("data");
                titleActivity.setText(data.getString("TITLE"));
                shopName.setText(data.getString("SHOP_NM"));
                useTime.setText(data.getString("START_TIME")+"至"+ data.getString("END_TIME"));
                shopTel.setText(data.getString("SHOP_TEL"));
                shopAddress.setText(data.getString("SHOP_ADDRESS"));
                uses = data.getSet("USES").getRows();
                historyAdapter = new MyActicityHistoryAdapter(ActivityUseFinishedActivity.this.uses);
                lvHistory.setAdapter(historyAdapter);
                hintDialogUtil.dismissDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.ll_shop_activityusedetail:
                //商家详情页
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }


    class MyActicityHistoryHolder extends BaseHolder<DataRow> {
        private TextView time,name,moeny;
        @Override
        public View initHolderView() {
            View view = View.inflate(AiSouAppInfoModel.getAppContext(), net.twoant.master.R.layout.zy_item_activityhistory,null);
            time = (TextView) view.findViewById(net.twoant.master.R.id.tv_time_activityhistory);
            name = (TextView) view.findViewById(net.twoant.master.R.id.tv_name_activityhistory);
            moeny = (TextView) view.findViewById(net.twoant.master.R.id.tv_money_activityhistory);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            time.setText(data.getString("LOG_TIME"));
            name.setText(data.getString("ACTIVITY_ITEM_TITLE"));
            switch(type){
                case 0:
                    moeny.setText(data.getString("ORDER_ITEM_SCORE")+"积分");
                    break;
                case 1:
                    moeny.setText("¥"+data.getString("ORDER_ITEM_PRICE")+" 使用积分"+data.getString("ORDER_ITEM_SCORE"));
                    break;
                case 2:
                    moeny.setText("¥"+data.getString("VAL"));
                    break;
                case 3:
                    moeny.setText(data.getString("VAL")+"次");
                    break;
            }
        }
    }
    class MyActicityHistoryAdapter extends BasicAdapter<DataRow> {
        public MyActicityHistoryAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new MyActicityHistoryHolder();
        }
    }
}
