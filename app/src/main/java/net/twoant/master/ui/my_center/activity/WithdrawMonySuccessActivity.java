package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;

/**
 * Created by DZY on 2017/1/10.
 * 佛祖保佑   永无BUG
 */

public class WithdrawMonySuccessActivity extends LongBaseActivity implements View.OnClickListener{
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_withdraw_success;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_enter_recharge_success).setOnClickListener(this);
        Intent intent = getIntent();
        TextView tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_title_withdraw_success);
        TextView tvBank = (TextView) findViewById(net.twoant.master.R.id.tv_bankcard_withdraw_success);
        TextView tvBankNum = (TextView) findViewById(net.twoant.master.R.id.tv_banknum_withdraw_success);
        TextView tvMoney = (TextView) findViewById(net.twoant.master.R.id.tv_money_withdraw_success);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_withdraw_cuccess);
        tvTitle.setText(intent.getStringExtra("tvTitle"));
        String tvBank1 = intent.getStringExtra("tvBank");
        if ("蚂蚁号".equals(tvBank1)) {
            relativeLayout.setVisibility(View.GONE);
        }
        tvBank.setText(intent.getStringExtra("tvBank"));
        tvBankNum.setText(intent.getStringExtra("tvBankNum"));
        tvMoney.setText(intent.getStringExtra("tvMoney"));

        //发送广播
        Intent intent1 = new Intent();
        intent1.putExtra("key", "数据数据");
        intent1.setAction("getcash");
        sendBroadcast(intent1);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_enter_recharge_success:
                finish();
                break;
        }
    }
}
