package net.twoant.master.ui.my_center.fragment;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.my_center.activity.FixPhoneActivity;
import net.twoant.master.ui.my_center.activity.out.AddressManageActivity;
import net.twoant.master.ui.other.activity.FixPwdActivity;

import java.util.List;

/**
 * Created by DZY on 2016/12/23.
 * 佛祖保佑   永无BUG
 */
public class AccountInformationActivity extends LongBaseActivity implements View.OnClickListener{
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_accountinformation;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.rl_fixlogin_accountinformation).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_fixpay_accountinformation).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_tel_accountinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_abut_accountinformation).setOnClickListener(this);



        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String phone = AiSouAppInfoModel.getInstance().getAiSouUserBean().getPhone();
        if (!TextUtils.isEmpty(phone)) {
            String start = phone.substring(0,3);
            String end = phone.substring(7,11);
            String resultphone = start+"****"+end;
            TextView phone1 = (TextView) findViewById(net.twoant.master.R.id.tv_phone_account_information);
            phone1.setText(resultphone);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case net.twoant.master.R.id.rl_fixlogin_accountinformation:
                Intent forgetPassword = new Intent(AccountInformationActivity.this,FixPwdActivity.class);
                startActivity(forgetPassword);
                break;
            case net.twoant.master.R.id.rl_fixpay_accountinformation:
                Intent intent = new Intent(AccountInformationActivity.this,SetPayPasswordActivity.class);
                startActivity(intent);
                break;
            case net.twoant.master.R.id.rl_tel_accountinfo:
                startActivity(new Intent(this,FixPhoneActivity.class));
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.rl_abut_accountinformation:
                startActivity(new Intent(AccountInformationActivity.this, AddressManageActivity.class));
                break;
        }
    }
}
