package net.twoant.master.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.twoant.master.api.ApiConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static boolean isShop;
    private static IOnResultListener sIOnResultListener;

    public interface IOnResultListener {
        /**
         * @param code 参考下面的
         * @see com.tencent.mm.sdk.modelbase.BaseResp.ErrCode
         */
        void onWeChatPaySResultListener(int code);
    }

    /**
     * 只有在明确确认需要获取微信支付的结果时，才可以调用该方法
     */
    public static void register(IOnResultListener iOnResultListener) {
        sIOnResultListener = iOnResultListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ApiConstants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        System.out.println("SS");
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (null != sIOnResultListener) {
                sIOnResultListener.onWeChatPaySResultListener(resp.errCode);
                sIOnResultListener = null;
            }
            WXPayEntryActivity.this.finish();
            /*
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                if (isShop) {
                    Intent intent = new Intent(WXPayEntryActivity.this, RechargeSuccessActivtity.class);
                    intent.putExtra("is_shop_pay", "is_shop_pay");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WXPayEntryActivity.this, RechargeSuccessActivtity.class);
                    startActivity(intent);
                }
                finish();
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                this.finish();
                //取消订单
                ToastUtil.showLong("订单已取消");
            } else {

            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        isShop = false;
    }
}