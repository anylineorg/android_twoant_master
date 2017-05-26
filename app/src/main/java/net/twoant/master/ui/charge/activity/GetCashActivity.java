package net.twoant.master.ui.charge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.bean.AliPayBean;
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
 * Created by DZY on 2016/12/10.
 * 佛祖保佑   永无BUG
 */

public class GetCashActivity extends LongBaseActivity implements View.OnClickListener{
    public static GetCashActivity instanceShopActivity;
    private EditText etMoney;
    private IWXAPI iwxapi;
    private RadioButton rbWXButton,tbAliButton;
    private int checkedType;
    private HintDialogUtil hintDialogUtil;

    private static final int SDK_PAY_FLAG = 1;

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
                        startActivity(new Intent(GetCashActivity.this,RechargeSuccessActivtity.class));
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
        return net.twoant.master.R.layout.zy_activity_getcash;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        hintDialogUtil = new HintDialogUtil(this);
        instanceShopActivity = this;
        etMoney = (EditText) findViewById(net.twoant.master.R.id.et_money_getcash);
        rbWXButton = (RadioButton) findViewById(net.twoant.master.R.id.rb_wx_getcash);
        tbAliButton = (RadioButton) findViewById(net.twoant.master.R.id.rb_ali_getcash);
        findViewById(net.twoant.master.R.id.btn_next_getcash).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        rbWXButton.setOnClickListener(this);
        tbAliButton.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        rbWXButton.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.rb_wx_getcash:
                rbWXButton.setChecked(true);
                tbAliButton.setChecked(false);
                checkedType = 0;
                break;
            case net.twoant.master.R.id.rb_ali_getcash:
                rbWXButton.setChecked(false);
                tbAliButton.setChecked(true);
                checkedType = 1;
                break;
            case net.twoant.master.R.id.btn_next_getcash://下一步
                String money = etMoney.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    ToastUtil.showLong("请输入金额");
                    return;
                }
                LogUtils.d(checkedType+"");
                hintDialogUtil.showLoading(net.twoant.master.R.string.startpay);
                if (checkedType == 0) {
                    //微信充值
                    iwxapi = WXAPIFactory.createWXAPI(this, ApiConstants.APP_ID, false);
                    iwxapi.registerApp(ApiConstants.APP_ID);
                    if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                        startWXNetPay(money);
                        RechargeSuccessActivtity.paymoney = money;
                        RechargeSuccessActivtity.payShopName = "余额自充";
                        RechargeSuccessActivtity.payType = "微信";//给支付成功界面赋值
                    } else {
                        hintDialogUtil.dismissDialog();
                        Toast.makeText(this, "您的微信版本过低", Toast.LENGTH_SHORT).show();
                    }
                }else if (checkedType==1) {
                    //支付宝充值
                    startAliPay(money);
                    RechargeSuccessActivtity.paymoney = money;
                    RechargeSuccessActivtity.payShopName = "余额自充";
                    RechargeSuccessActivtity.payType = "支付宝";//给支付成功界面赋值
                }
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
        }
    }

    String pay_info;
    private void startAliPay(String money) {
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        m.put("price",money);
        LongHttp(ApiConstants.ALI_GETCASH,"" ,m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
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
                            PayTask alipay = new PayTask(GetCashActivity.this);
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
                    hintDialogUtil.dismissDialog();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startWXNetPay(String money){
        HashMap<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        m.put("price",money);
        LongHttp(ApiConstants.WX_PAY,"" ,m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                response = response.replace("package","packageX");
                WXPayBean wxPayBean = GsonUtil.gsonToBean(response, WXPayBean.class);
                if (wxPayBean.isResult()) {
                    sendPayReq(wxPayBean);
                }else{
                    ToastUtil.showLong("请先登录");
                    return;
                }
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
        hintDialogUtil.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instanceShopActivity != null) {
            instanceShopActivity = null;
        }
    }
}
