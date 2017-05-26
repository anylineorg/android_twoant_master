package net.twoant.master.api;


import android.content.Context;

import com.tencent.mobileqq.openpay.api.IOpenApi;
import com.tencent.mobileqq.openpay.api.OpenApiFactory;
import com.tencent.mobileqq.openpay.data.pay.PayApi;

/**
 * Created by kaiguokai on 2017/5/19.
 */

public class QQWalletFactory {

    public  static IOpenApi openApi;

    public IOpenApi getInstance(Context context, String app_id){
        if (null==openApi){
            openApi= OpenApiFactory.getInstance(context,app_id);
        }
        return openApi;
    }

    /**
     * 生成支付对象
     * @param appId 应用唯一id，http://open.qq.com申请
     * @param nonce 随机串，随机字段串每次请求都要不一样
     * @param timeStamp 时间戳，时间戳，为1970年1月1日00:00到请求发起时间的秒数
     * @param tokenId QQ钱包的预支付会话标识，具体值请传入调用统一下单接口后返回的“prepay_id”的值
     * @param pubAcc 手Q公众帐号，暂时未对外开放申请。注：所有参与签名的参数，如果value为空, 生成格式如“pubAcc=”
     * @param pubAccHint 关注手Q公众帐号提示语
     * @param bargainorId QQ钱包支付商户号，具体值请传入调用统一下单接口时传入的“mch_id” 的值 注：参与签名参数key为“bargainorId”
     * @param callBackActivityName  回掉的Activity data名
     * @param sigType 加密方式，签名时，使用的加密算法。目前只支持"HMAC-SHA1"，因此该参数填写"HMAC-SHA1"
     * @param sig 签名串，参看“数字签名”
     * @return
     */
    public PayApi BuildPayApi(String appId,String nonce,Long timeStamp,String tokenId,String pubAcc,String pubAccHint,String bargainorId,String callBackActivityName, String sigType,String sig){
        PayApi payApi=new PayApi();
        payApi.appId=appId;
        payApi.nonce=nonce;
        payApi.timeStamp=timeStamp;
        payApi.tokenId=tokenId;
        payApi.pubAcc=pubAcc;
        payApi.pubAccHint=pubAccHint;
        payApi.bargainorId=bargainorId;
        payApi.sigType=sigType;
        payApi.sig=sig;
        payApi.callbackScheme=callBackActivityName;
        return payApi;
    }

}
