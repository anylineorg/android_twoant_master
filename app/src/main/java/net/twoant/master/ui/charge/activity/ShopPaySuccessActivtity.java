package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.fragment.TransationRecordActivity;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.my_center.fragment.EditDataActivity;

import java.util.Date;

/**
 * Created by DZY on 2016/12/12.
 * 佛祖保佑   永无BUG
 */
public class ShopPaySuccessActivtity extends LongBaseActivity implements View.OnClickListener{

    private TextView money;
    private TextView part;
    private TextView type;

    public static void startActivity(Context context,String money,String part,String type){
        Intent intent = new Intent(context, ShopPaySuccessActivtity.class);
        intent.putExtra("money",money);
        intent.putExtra("part",part);
        intent.putExtra("type",type);
        Activity activity = (Activity) context;
        activity.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_recharge_suceess;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        findViewById(net.twoant.master.R.id.btn_enter_recharge_success).setOnClickListener(this);
        String moneyStr = getIntent().getStringExtra("money");
        String partStr = getIntent().getStringExtra("part");
        String typeStr = getIntent().getStringExtra("type");
        money = (TextView) findViewById(net.twoant.master.R.id.tv_price_recharge_success);
        part = (TextView) findViewById(net.twoant.master.R.id.tv_payee_recharge_success);
        type = (TextView) findViewById(R.id.tv_paytype_recharge_success);
        TextView startTime = (TextView) findViewById(R.id.tv_transaction_recharge_success);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        startTime.setText(EditDataActivity.getTime(curDate));
        money.setText(moneyStr);
        part.setText(partStr);
        type.setText(typeStr);
        /**
         * 支付成功，关闭层叠activity，清空商品列表集合
         * */
        AiSouAppInfoModel.getInstance().exitOrderActivity();
        if (null != PostOrderActivity.selectedList) {
            PostOrderActivity.selectedList.clear();
        }
        /**
         * 清空待付款订单 层叠activity
         * */
        AiSouAppInfoModel.getInstance().exitOrderActivity();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.btn_enter_recharge_success:
//                Order4Activity.startActivity(ShopPaySuccessActivtity.this,1);
                startActivity(new Intent(ShopPaySuccessActivtity.this,TransationRecordActivity.class));
                finish();
                break;
        }
    }

}
