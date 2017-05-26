package net.twoant.master.ui.main.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.AppConfig;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/4/13.
 * 第三方支付订单创建
 */

public class ThirdPartyPayOrderControl implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack, Handler.Callback {

    public final static int ID_ALI_PLATFORM = 0x1;
    public final static int ID_WE_CHANT_PLATFORM = 0x2;
    /**
     */
    public final static int ID_OWN_PLATFORM = 0x3;

    private final static int CODE_ALI_PLATFORM = 0x4;
    private final static int CODE_WE_CHANT_PLATFORM = 0x5;

    public final static int STATE_SUCCESSFUL = 0x6;
    public final static int STATE_FAIL = 0x7;
    public final static int STATE_EXCEPTION = 0x8;

    private IWXAPI iwxapi;
    private Handler mHandler;
    private Activity mActivity;
    private HttpConnectedUtils mHttpConnect;
    private final Context fApplicationContext;
    private static int sWeChatCode = Integer.MAX_VALUE;
    private IOnOrderResultListener iOnOrderResultListener;
    private OnWeChatPayResultListener mOnWeChatPayResultListener;

    public interface IOnOrderResultListener {
        /**
         * @param response 生成的订单数据（用于微信或支付宝）
         * @param id       {@link #ID_ALI_PLATFORM}, {@link #ID_WE_CHANT_PLATFORM}, {@link #ID_OWN_PLATFORM}
         * @return false 代表自处理 通知信息， true 显示默认信息
         */
        boolean onOrderResultListener(boolean isSuccessful, @NonNull DataRow response, int id);

        /**
         * @param state {@link #STATE_SUCCESSFUL},{@link #STATE_FAIL}, {@link #STATE_EXCEPTION}
         */
        void onPaymentResult(int state, @NonNull String hintInfo);


    }

    @MainThread
    public ThirdPartyPayOrderControl(@NonNull IOnOrderResultListener iOnOrderResultListener, @NonNull Activity activity) {
        this.mHttpConnect = HttpConnectedUtils.getInstance(this);
        this.iOnOrderResultListener = iOnOrderResultListener;
        this.mActivity = activity;
        this.fApplicationContext = mActivity.getApplicationContext();
    }

    @MainThread
    public void getAliPayOrder(String orderId) {
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        keyValue.put("order", TextUtils.isEmpty(orderId) ? "" : orderId);
        getAliPayOrder(keyValue, ApiConstants.ALI_PAY_NEW);
    }

    @MainThread
    public void getAliPayOrder(@NonNull Map<String, String> parameter, @NonNull String url) {
        if (null == mHandler) {
            this.mHandler = new Handler(this);
        }
        mHttpConnect.startNetworkGetString(ID_ALI_PLATFORM, parameter, url);
    }


    @MainThread
    public void getWeChatOrder(String orderId) {
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        keyValue.put("order", TextUtils.isEmpty(orderId) ? "" : orderId);
        getWeChatOrder(keyValue, ApiConstants.WX_PAY_NEW);
    }

    @MainThread
    public void getWeChatOrder(@NonNull Map<String, String> parameter, @NonNull String url) {
        if (null == mHandler) {
            this.mHandler = new Handler(this);
        }
        mHttpConnect.startNetworkGetString(ID_WE_CHANT_PLATFORM, parameter, url);
    }

    @MainThread
    public void getOWnOrder(@NonNull Map<String, String> parameter, @NonNull String url) {
        mHttpConnect.startNetworkGetString(ID_OWN_PLATFORM, parameter, url);
    }

    /**
     * 调用生命周期
     */
    @MainThread
    public void onResume() {
        if (Integer.MAX_VALUE != sWeChatCode) {
            initWeChatPaymentData(sWeChatCode);
            sWeChatCode = Integer.MAX_VALUE;
        }
    }

    @Override
    public void onResponse(String response, int id) {
        initPayOrderData(response, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (null != iOnOrderResultListener) {
            DataRow dataRow = new DataRow();
            dataRow.put("message", NetworkUtils.getNetworkStateDescription(call, e, fApplicationContext.getString(net.twoant.master.R.string.payment_connect_network_fail)));
            iOnOrderResultListener.onOrderResultListener(false, dataRow, id);
        }
    }

    @MainThread
    public void onDestroy() {
        if (null != mHttpConnect) {
            mHttpConnect.onDestroy();
            mHttpConnect = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        sWeChatCode = Integer.MAX_VALUE;
        mActivity = null;
        iOnOrderResultListener = null;
        mOnWeChatPayResultListener = null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_ALI_PLATFORM:
                if (msg.obj instanceof Map) {
                    Map<String, String> obj = (Map) msg.obj;
                    String resultStatus = obj.get("resultStatus");
                    if (null == iOnOrderResultListener) {
                        return true;
                    }
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        iOnOrderResultListener.onPaymentResult(STATE_SUCCESSFUL, fApplicationContext.getString(net.twoant.master.R.string.payment_state_successful));
                    } else {
                        switch (resultStatus) {
                            case "8000"://8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                                iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay_state_8000));
                                break;

                            case "4000":// 4000	订单支付失败
                                iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay_state_4000));
                                break;

                            case "5000"://5000	重复请求
                                iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay_state_5000));
                                break;

                            case "6001"://6001	用户中途取消
                                iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay_state_6001));
                                break;

                            case "6002"://6002	网络连接出错
                                iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay_state_6002));
                                break;

                            case "6004"://6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                                iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_ali_pay));
                                break;
                        }
                    }
                }
                break;

            case CODE_WE_CHANT_PLATFORM:
                initWeChatPaymentData(msg.arg1);
                break;
        }
        return true;
    }

    private void initOwnPayData(DataRow dataRow) {
        if (null != iOnOrderResultListener) {
            iOnOrderResultListener.onPaymentResult(STATE_SUCCESSFUL, fApplicationContext.getString(net.twoant.master.R.string.payment_state_successful));
        }
    }

    /**
     * 微信回调
     */
    private void initWeChatPaymentData(int code) {
        if (null != iOnOrderResultListener) {
            switch (code) {
                case BaseResp.ErrCode.ERR_OK:
                    iOnOrderResultListener.onPaymentResult(STATE_SUCCESSFUL, fApplicationContext.getString(net.twoant.master.R.string.payment_state_successful));
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_we_chat_state_cancel));
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_we_chat_version_low));
                    break;
                default:
                    iOnOrderResultListener.onPaymentResult(STATE_FAIL, fApplicationContext.getString(net.twoant.master.R.string.payment_we_chat_state_fail));
                    break;
            }
        }
    }

    private class ALiPayRunnable implements Runnable {

        private String mData;

        private ALiPayRunnable(String data) {
            this.mData = data;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            PayTask payTask = new PayTask(mActivity);
            Map<String, String> map = payTask.payV2(mData, false);
            if (null != mHandler) {
                Message message = mHandler.obtainMessage();
                message.what = CODE_ALI_PLATFORM;
                message.obj = map;
                mHandler.sendMessage(message);
            }
        }
    }

    /**
     * data":{"
     * package":"Sign=WXPay",
     * "appid":"wxc794fc8561b76d39",
     * "sign":"025EDC6C904BC4148A380BF1E7CBE061",
     * "partnerid":"1383950902",
     * "prepayid":"wx20170413175350e19da893d60747257251",
     * "noncestr":"FQBOXSKSODPKMBOXGEQAHAADZHYHUNWN",
     * "timestamp":"1492077230"},
     */
    private void initWeChatData(DataRow response) {
        DataRow data;
        if (null == response || null == (data = response.getRow("data"))) {
            if (null != iOnOrderResultListener) {
                iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail));
            } else {
                ToastUtil.showShort(net.twoant.master.R.string.payment_get_detail_fail);
            }
            return;
        }

        if (null == iwxapi) {
            iwxapi = WXAPIFactory.createWXAPI(mActivity, AppConfig.APP_ID, false);
        }
        if (iwxapi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            if (null != iOnOrderResultListener) {
                iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_we_chat_version_low));
            } else {
                ToastUtil.showShort(net.twoant.master.R.string.payment_we_chat_version_low);
            }
            return;
        }
        new Thread(new WeChatPayRunnable(data)).start();
    }

    private class WeChatPayRunnable implements Runnable {

        private DataRow mParameter;

        private WeChatPayRunnable(DataRow dataRow) {
            this.mParameter = dataRow;
        }
        @Override
        public void run() {
            PayReq payReq = new PayReq();
            payReq.appId = mParameter.getStringDef("appid", "");
            payReq.partnerId = mParameter.getStringDef("partnerid", "");
            payReq.prepayId = mParameter.getStringDef("prepayid", "");
            payReq.nonceStr = mParameter.getStringDef("noncestr", "");
            payReq.timeStamp = mParameter.getStringDef("timestamp", "");
            payReq.packageValue = mParameter.getStringDef("package", "");
            payReq.sign = mParameter.getStringDef("sign", "");
            iwxapi.sendReq(payReq);
            if (null == mOnWeChatPayResultListener) {
                mOnWeChatPayResultListener = new OnWeChatPayResultListener();
            }
            WXPayEntryActivity.register(mOnWeChatPayResultListener);
        }
    }

    private class OnWeChatPayResultListener implements WXPayEntryActivity.IOnResultListener {

        @Override
        public void onWeChatPaySResultListener(int code) {
            if (null != mHandler) {
                Message message = mHandler.obtainMessage();
                message.what = CODE_WE_CHANT_PLATFORM;
                message.arg1 = code;
                mHandler.sendMessage(message);
            } else {
                sWeChatCode = code;
            }
        }
    }

    /**
     * 支付宝
     */
    private void initAliPayData(DataRow response) {
        if (null == response) {
            if (null != iOnOrderResultListener) {
                iOnOrderResultListener.onPaymentResult(STATE_EXCEPTION, fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail));
            } else {
                ToastUtil.showShort(net.twoant.master.R.string.payment_get_detail_fail);
            }
            return;
        }
        new Thread(new ALiPayRunnable(response.getStringDef("data", ""))).start();
    }


    /**
     */
    private void initPayOrderData(String response, int id) {
        if (null == iOnOrderResultListener || mActivity.isFinishing()) {
            return;
        }
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow) {

            if (dataRow.getBoolean("result", false)) {
                if (iOnOrderResultListener.onOrderResultListener(true, dataRow, id)) {
                    switch (id) {
                        case ThirdPartyPayOrderControl.ID_ALI_PLATFORM:
                            initAliPayData(dataRow);
                            break;

                        case ThirdPartyPayOrderControl.ID_WE_CHANT_PLATFORM:
                            initWeChatData(dataRow);
                            break;
                        case ThirdPartyPayOrderControl.ID_OWN_PLATFORM:
                            initOwnPayData(dataRow);
                            break;
                    }
                }
            } else {
                if (iOnOrderResultListener.onOrderResultListener(true, dataRow, id)) {
                    initPayError(dataRow.getStringDef("message", fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail)), id);
                }
            }
        } else {
            dataRow = new DataRow();
            dataRow.put("message", fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail));
            if (iOnOrderResultListener.onOrderResultListener(true, dataRow, id)) {
                initPayError(null, id);
            }
        }
    }

    private void initPayError(String hint, int id) {
        switch (id) {
            case ThirdPartyPayOrderControl.ID_ALI_PLATFORM:
                iOnOrderResultListener.onPaymentResult(STATE_FAIL, TextUtils.isEmpty(hint) ?
                        fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail) : hint);
                break;

            case ThirdPartyPayOrderControl.ID_WE_CHANT_PLATFORM:
                iOnOrderResultListener.onPaymentResult(STATE_FAIL, TextUtils.isEmpty(hint) ?
                        fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail) : hint);

                break;
            case ThirdPartyPayOrderControl.ID_OWN_PLATFORM:
                iOnOrderResultListener.onPaymentResult(STATE_FAIL, TextUtils.isEmpty(hint) ?
                        fApplicationContext.getString(net.twoant.master.R.string.payment_get_detail_fail) : hint);
                break;
        }
    }
}
