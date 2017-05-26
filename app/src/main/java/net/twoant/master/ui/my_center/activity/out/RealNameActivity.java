package net.twoant.master.ui.my_center.activity.out;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/22.
 */

public class RealNameActivity extends LongBaseActivity implements View.OnClickListener {
    private EditText etName,etBankNum,etIdentityNum,etTelePhone;
    private TextView tvTitle;
    private HintDialogUtil hintDialogUtil;
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_realname;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        tvTitle = (TextView) findViewById(net.twoant.master.R.id.tv_title_realname);
        tvTitle.setText("实名认证");
        etName = (EditText) findViewById(net.twoant.master.R.id.et_name_realname);
        etBankNum = (EditText) findViewById(net.twoant.master.R.id.et_banknum_realname);
        etIdentityNum = (EditText) findViewById(net.twoant.master.R.id.et_identity_realname);
        etTelePhone = (EditText) findViewById(net.twoant.master.R.id.et_telephone_realname);
        findViewById(net.twoant.master.R.id.bt_post_realname).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        hintDialogUtil = new HintDialogUtil(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case net.twoant.master.R.id.bt_post_realname:
                postRealName();
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    private void postRealName() {
        String realName = etName.getText().toString().trim();
        if (TextUtils.isEmpty(realName)) {
            ToastUtil.showLong("姓名不能不空");
            return;
        }

        String realBankNum = etBankNum.getText().toString().trim();
        if (TextUtils.isEmpty(realBankNum)) {
            ToastUtil.showLong("银行卡号不能不空");
            return;
        }

        String realIdentityNum = etIdentityNum.getText().toString().trim();
        if (TextUtils.isEmpty(realIdentityNum)) {
            ToastUtil.showLong("身份证号不能不空");
            return;
        }

        String realTelePhone = etTelePhone.getText().toString().trim();
        if (TextUtils.isEmpty(realTelePhone)) {
            ToastUtil.showLong("手机号不能不空");
            return;
        }
        HashMap<String,String> m = new HashMap<>();
//        m.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());
        m.put("bankcard",realBankNum);
        m.put("idcard",realIdentityNum);
        m.put("mobile",realTelePhone);
        m.put("realname",realName);
        String content = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude()
                + "," + AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude();
        m.put("center",content);
        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.BING_BANK,"" ,m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                String msg = dataRow.getString("message");
                if (result) {
                    ToastUtil.showLong(msg);
                }else {
                    finish();
                    ToastUtil.showLong("认证成功");
                    AiSouAppInfoModel.getInstance().getAiSouUserBean().setAuthentication(true);
                    startActivity(new Intent(RealNameActivity.this, RealNameHasActivity.class));
                }
            }
        });
    }
}
