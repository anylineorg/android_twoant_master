package net.twoant.master.ui.charge.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.base_app.LongBaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DZY on 2016/12/30.
 * 佛祖保佑   永无BUG
 */

public class IntegralSuccessActivity extends LongBaseActivity implements View.OnClickListener{
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_integral_successfinish;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        TextView num = (TextView) findViewById(net.twoant.master.R.id.tv_num_integral_success);
        TextView time = (TextView) findViewById(net.twoant.master.R.id.tv_time_success_integral);
        num.setText("本次充值:"+getIntent().getStringExtra("integral")+"积分");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        time.setText("充值时间:"+str);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_enter_recharge_success).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_save).setOnClickListener(this);
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
            case net.twoant.master.R.id.tv_save:
                finish();
                break;
        }
    }
}
