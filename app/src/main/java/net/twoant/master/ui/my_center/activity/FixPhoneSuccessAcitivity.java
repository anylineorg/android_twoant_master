package net.twoant.master.ui.my_center.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.FixPhoneActivityManager;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.other.activity.LoginActivity;

public class FixPhoneSuccessAcitivity extends LongBaseActivity implements View.OnClickListener{

    public EditText etCode;
    public TextView etTitle;
    public String phone;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_fix_phone_success_acitivity;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        phone = getIntent().getStringExtra("phone");
        etCode = (EditText) findViewById(net.twoant.master.R.id.et_code_fixphone_getcode);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_fixphone_success).setOnClickListener(this);
        etTitle = (TextView) findViewById(net.twoant.master.R.id.textView23);
        etTitle.setText("当前绑定的手机号为:"+ phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_fixphone_success:
            case net.twoant.master.R.id.iv_back:
                FixPhoneActivityManager.getStackManager().popAllActivitys();
                AiSouAppInfoModel.getInstance().getAiSouUserBean().setPhone(phone);
                finish();
                LoginActivity.logoutResetData(FixPhoneSuccessAcitivity.this);
                break;
        }
    }
}
