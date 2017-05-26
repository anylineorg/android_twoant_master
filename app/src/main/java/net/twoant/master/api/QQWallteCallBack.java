package net.twoant.master.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import net.twoant.master.R;

import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.wxapi.RechargeSuccessActivtity;
import com.tencent.mobileqq.openpay.api.IOpenApiListener;
import com.tencent.mobileqq.openpay.data.base.BaseResponse;
import com.tencent.mobileqq.openpay.data.pay.PayResponse;

/**
 * Created by kaiguokai on 2017/5/19.
 */

public class QQWallteCallBack extends Activity implements IOpenApiListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QQWalletFactory.openApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        QQWalletFactory.openApi.handleIntent(intent, this);
    }

    @Override
    public void onOpenResponse(BaseResponse baseResponse) {
        String title = "Callback from mqq";
        String message;

        if (baseResponse == null) {
            message = "response is null.";
            return;
        } else {
            if (baseResponse instanceof PayResponse) {
                PayResponse payResponse = (PayResponse) baseResponse;
                message = " apiName:" + payResponse.apiName
                        + " serialnumber:" + payResponse.serialNumber
                        + " isSucess:" + payResponse.isSuccess()
                        + " retCode:" + payResponse.retCode
                        + " retMsg:" + payResponse.retMsg;
                if (payResponse.isSuccess()) {
                    if (!payResponse.isPayByWeChat()) {
                        message += " transactionId:" + payResponse.transactionId
                                + " payTime:" + payResponse.payTime
                                + " callbackUrl:" + payResponse.callbackUrl
                                + " totalFee:" + payResponse.totalFee
                                + " spData:" + payResponse.spData;
                    }

                }


                Log.e("QQ支付返回数据:",message);

                if (payResponse.retCode==0){
                    RechargeSuccessActivtity.paymoney = payResponse.totalFee;
                    RechargeSuccessActivtity.payShopName = payResponse.spData;
                    RechargeSuccessActivtity.payType = "QQ支付";//给支付成功界面赋值
                    Log.e("状态","成功");//https://qpay.qq.com/qpaywiki/showdocument.php?pid=38&docid=165
                }else{
                    String tip="";
                    switch (payResponse.retCode){
                        case -1:
                            tip="放弃支付";
                            break;
                        case -2:
                            tip="登录态超时";
                            break;
                        case -3:
                            tip="重复提交订单";
                            break;
                        case -4:
                            tip="快速注册用户手机号不一致";
                            break;
                        case -5:
                            tip="账户被冻结";
                            break;
                        case -6:
                            tip="支付密码输入错误次数超过上限";
                            break;
                        case -100:
                            tip="网络异常错误";
                            break;
                        case -101:
                            tip="参数错误";
                            break;
                        default:
                            tip="未知错误";
                            break;
                    }

                    ToastUtil.showLong(tip);
                }


            } else {
                message = "response is not PayResponse.";
            }
        }
    }
}
