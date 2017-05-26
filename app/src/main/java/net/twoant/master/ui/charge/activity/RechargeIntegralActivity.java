package net.twoant.master.ui.charge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.AliPayBean;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.ErrorPayPasswordDialog;
import net.twoant.master.widget.NoEnoughBalanceDialog;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.wxapi.RechargeSuccessActivtity;
import net.twoant.master.wxapi.WXPayBean;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/23.
 * 佛祖保佑   永无BUG
 */
public class RechargeIntegralActivity extends LongBaseActivity implements View.OnClickListener{
    public static RechargeIntegralActivity integralActivity;
    private EditText etMoney;
    private TextView tvGetIntegral;
    private Button button;
    private RadioButton weixin;
    private int paytype = 0;//默认微信支付
    private RadioGroup radioGroup;
    public static final int SDK_PAY_FLAG = 1;
    private HintDialogUtil hintDialogUtil;
    private PassViewDialog passViewDialog;
    private IWXAPI iwxapi;
    private NoEnoughBalanceDialog noEnoughBalanceDialog;
    private ErrorPayPasswordDialog errorPayPasswordDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    AliPayBean payResult = new AliPayBean((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        startActivity(new Intent(RechargeIntegralActivity.this,RechargeSuccessActivtity.class));
                        ToastUtil.showLong("支付成功");
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.showLong("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtil.showLong("支付失败");
                        }
                    }
                    break;
                }
            }
        };
    };
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_rechargeintegral;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        integralActivity = this;
        hintDialogUtil = new HintDialogUtil(this);
        noEnoughBalanceDialog = new NoEnoughBalanceDialog(RechargeIntegralActivity.this, Gravity.CENTER, false);
        etMoney = (EditText) findViewById(net.twoant.master.R.id.et_money_rechargeintegral);
        tvGetIntegral = (TextView) findViewById(net.twoant.master.R.id.tv_integral_rechargeintegral);
        button = (Button) findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay);
        button.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(net.twoant.master.R.id.rg_group_rechargeintegral);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case net.twoant.master.R.id.rb_weixin_rechargeintegral:
                        paytype = 0;
                        break;
                    case net.twoant.master.R.id.rb_zhifubao_rechargeintegral:
                        paytype = 1;
                        break;
                    case net.twoant.master.R.id.rb_yue_rechargeintegral:
                        paytype = 2;
                        break;
                }
            }
        });
        weixin = (RadioButton) findViewById(net.twoant.master.R.id.rb_weixin_rechargeintegral);
        weixin.setChecked(true);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s + "")) {
                    float f = Float.parseFloat(s+"");
                    int v = (int) (f * 200);
                    tvGetIntegral.setText(v+"");
                }
                if (start>=0&&count==1) {
                    //输入有值
                    button.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_frame_btn));
                    button.setEnabled(true);
                }else if (start==0&&before==1) {
                    //没有值
                    button.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_gray_bg));
                    button.setEnabled(false);
                    tvGetIntegral.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        errorPayPasswordDialog = new ErrorPayPasswordDialog(this,Gravity.CENTER);
        /**
         * 自定义输入密码框输密码的回调
         * */
        passViewDialog = new PassViewDialog(RechargeIntegralActivity.this, Gravity.BOTTOM, true);
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
                            //余额支付
                            HashMap<String,String> m = new HashMap<>();
                            m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
                            m.put("price",etMoney.getText().toString().trim());
                            LongHttp(ApiConstants.YU_E_GETINTEGRAL, "", m, new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    hintDialogUtil.dismissDialog();
                                    ToastUtil.showLong("连接失败:"+e);
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    hintDialogUtil.dismissDialog();
                                    DataRow row = DataRow.parseJson(response);
                                    String result = row.getString("result");
                                    String message = row.getString("message");
                                    if (result.equals("true")) {
                                        Intent intent = new Intent(RechargeIntegralActivity.this, IntegralSuccessActivity.class);
                                        intent.putExtra("integral",tvGetIntegral.getText().toString().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                    if (message.contains("余额不足")) {
                                        passViewDialog.dismiss();
                                        noEnoughBalanceDialog.showDialog(false,true);
                                    }
                                }
                            });
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
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_paymoney_dialogpay:
                String money = etMoney.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    return;
                }
                switch(paytype){
                    case 0://微信
                        //微信充值
                        iwxapi = WXAPIFactory.createWXAPI(this, ApiConstants.APP_ID, false);
                        iwxapi.registerApp(ApiConstants.APP_ID);
                        if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                            startWXNetPay(money);
                            RechargeSuccessActivtity.paymoney = money;
                            RechargeSuccessActivtity.payShopName = "积分自充";
                            RechargeSuccessActivtity.payType = "微信";//给支付成功界面赋值
                        } else {
                            Toast.makeText(this, "您的微信版本过低", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1://支付宝
                        //支付宝充值
                        startAliPay(money);
                        RechargeSuccessActivtity.paymoney = money;
                        RechargeSuccessActivtity.payShopName = "积分自充";
                        RechargeSuccessActivtity.payType = "支付宝";//给支付成功界面赋值
                        break;
                    case 2://余额
                        //YU_E_GETINTEGRAL
                        balanceRecharge(money);
                        break;
                }
                break;
        }
    }

    private void balanceRecharge(String money) {
        hintDialogUtil.showLoading();
        //获取支付密码状态  0：未设置
        PayHttpUtils.getPayPasswordState(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("获取支付密码状态失败:"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                DataRow dataRow = DataRow.parseJson(response);
                String data = (String) dataRow.get("data");
                if (data.equals("0")) {
                    //没有支付密码，去设置
                    SetPasswordDialog setPasswordDialog = new SetPasswordDialog(RechargeIntegralActivity.this, Gravity.CENTER,false);
                    setPasswordDialog.showDialog(true,true);
                    hintDialogUtil.dismissDialog();
                    return;
                }else{
                    //输入平台的支付密码
                    passViewDialog.clearn();
                    passViewDialog.showDialog(true,true);
                    passViewDialog.setTile("积分充值");
                    passViewDialog.setPrice("¥"+etMoney.getText().toString().trim());
                    RechargeSuccessActivtity.paymoney = etMoney.getText().toString().trim();//给支付成功界面赋值
                    RechargeSuccessActivtity.payShopName = "两只蚂蚁";//给支付成功界面赋值
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    String pay_info;
    private void startAliPay(String money) {
        hintDialogUtil.showLoading(net.twoant.master.R.string.startpay);
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        m.put("price",money);
        m.put("sort","0");// 0 为积分充值;1或不填为余额充值
        LongHttp(ApiConstants.ALI_GETCASH,"" ,m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                try{
                    JSONObject json = new JSONObject(response);
                    String result  = json.getString("result");
                    if (result.equals("true")) {
                        //true成功，false失败
                    }
                    pay_info = json.getString("data");
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(RechargeIntegralActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(pay_info,false);//第二个参数是否有加载框
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    private void startWXNetPay(String money){
        hintDialogUtil.showLoading(net.twoant.master.R.string.startpay);
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        m.put("price",money);
        m.put("sort","0");// 0 为积分充值;1或不填为余额充值
        LongHttp(ApiConstants.WX_PAY,"" ,m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                response = response.replace("package","packageX");
                WXPayBean wxPayBean = GsonUtil.gsonToBean(response, WXPayBean.class);
                if (wxPayBean.isResult()) {
                    sendPayReq(wxPayBean);
                }else{
                    ToastUtil.showLong("开启微信支付失败");
                    return;
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }

    private void sendPayReq(WXPayBean data){
        PayReq payReq = new PayReq();
        WXPayBean.DataBean dataBean = data.getData();
        payReq.appId = dataBean.getAppid();
        payReq.partnerId = dataBean.getPartnerid();
        payReq.prepayId = dataBean.getPrepayid();
        payReq.nonceStr = dataBean.getNoncestr();
        payReq.timeStamp = dataBean.getTimestamp();
        payReq.packageValue = dataBean.getPackageX();
        payReq.sign = dataBean.getSign();
        iwxapi.sendReq(payReq);
        ToastUtil.showLong("开启支付");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (integralActivity != null) {
            integralActivity = null;
        }
    }
}
