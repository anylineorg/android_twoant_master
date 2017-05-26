package net.twoant.master.ui.main.presenter;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.bean.PayOrderBean;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/3/23.
 * 订单支付的 信息获取
 */

public class PayOrderInfoControl implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack {

    public final static int HAS_PASSWORD = 0xA0;
    public final static int NO_PASSWORD = 0xA1;
    /**
     * 检测是否有支付密码失败
     */
    public final static int FAIL_PASSWORD = 0xA2;

    /**
     * 请求指定shopId 的红包
     */
    private final static int ID_RED_PACKET = 1;
    /**
     * 账号的积分和钱包余额
     */
    private final static int ID_AMOUNT = 2;
    /**
     * 收货地址
     */
    private final static int ID_ADDRESS = 3;
    /**
     * 是否有支付密码
     */
    private final static int ID_HAS_PASSWORD = 4;

    /**
     * 验证支付密码
     */
    private final static int ID_AFFIRM_PASSWORD = 5;

    /**
     * 商品订单
     */
    private final static int ID_GOODS_ORDER = 6;

    /**
     * 店内支付订单
     */
    private final static int ID_MERCHANT_ORDER = 7;

    /**
     * 获取商家详情
     */
    private final static int ID_MERCHANT_DETAIL = 8;

    private final Context fAppliacationContext;

    /**
     * 当前用户在指定店铺中的会员信息
     */
    private final static int ID_MERCHANT_MEMBER_INFO = 9;

    private HttpConnectedUtils mHttpConnectedUtils;
    /**
     * 红包数据
     */
    private List<DataRow> mRedDataBean;

    private boolean hasPassword;
    private boolean isCheckPasswordSuccessful;

    private double mWalletSum;
    private double mIntegralSum;
    private IPayOderStater iPayOderStater;
    /**
     * 记录网络成功次数
     */
    private int mRecodeTime;
    private String mReceiptName;
    private String mReceiptTel;
    private String mReceiptAddress;
    /**
     * 商家详情数据
     */
    private DataRow mMerchantDetailData;

    /**
     * 商家会员信息
     */
    private DataRow mMerchantMemberData;


    public PayOrderInfoControl(@NonNull IPayOderStater iPayOderStater) {
        this.iPayOderStater = iPayOderStater;
        fAppliacationContext = AiSouAppInfoModel.getAppContext();
    }

    @Nullable
    public DataRow getMerchantDetailData() {
        return this.mMerchantDetailData;
    }

    @Nullable
    public DataRow getMerchantMemberData() {
        return this.mMerchantMemberData;
    }

    @Nullable
    public List<DataRow> getRedDataRows() {
        return this.mRedDataBean;
    }

    public double getWalletSum() {
        return this.mWalletSum;
    }

    public double getIntegralSum() {
        return this.mIntegralSum;
    }

    public void getInfo(@NonNull PayOrderBean payOrderBean) {
        final String shopId = TextUtils.isEmpty(payOrderBean.getShopId()) ? "" : payOrderBean.getShopId();

        if (null == mHttpConnectedUtils) {
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        }
        if (null != iPayOderStater) {
            iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_synchronization_data), false);
        }

        mRecodeTime = 0;
        //获取红包
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        String uid = AiSouAppInfoModel.getInstance().getUID();

        if (PayOrderBean.TYPE_SCANNER == payOrderBean.getType()) {
            //获取商家详情
            keyValue.clear();
            keyValue.put("id", shopId);
            keyValue.put("user", uid);
            AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
            keyValue.put("center", String.valueOf(instance.getLongitude() + "," + instance.getLongitude()));
            ++mRecodeTime;
            mHttpConnectedUtils.startNetworkGetString(ID_MERCHANT_DETAIL, keyValue, ApiConstants.MERCHANT_HOME_PAGE_DETAIL);
        }

        keyValue.put("user", uid);
        keyValue.put("shop", shopId);
        keyValue.put("sort", "12");//获取红包
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_RED_PACKET, keyValue, ApiConstants.SHOP_REDABLE);

        //获取积分和余额
        keyValue.clear();
        keyValue.put("user", uid);
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_AMOUNT, keyValue, ApiConstants.USERPRICEBAG);

        //获取收货地址
       /* keyValue.clear();TODO 因为暂时未用到，所以未实现
        keyValue.put("user", AiSouAppInfoModel.getInstance().getUID());
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_ADDRESS, keyValue, ApiConstants.GET_DEFAULT_ADDRESS);*/
        //是否有支付密码
        keyValue.clear();
        keyValue.put("user", uid);
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_HAS_PASSWORD, keyValue, ApiConstants.STATE_PAY_PASSWORD);
        //会员信息
        keyValue.clear();
        keyValue.put("shop", shopId);
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_MERCHANT_MEMBER_INFO, keyValue, ApiConstants.MERCHANT_MEMBER_INFO);
    }

    public void getUserInfo() {

        if (null == mHttpConnectedUtils) {
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        }
        if (null != iPayOderStater) {
            iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_synchronization_data), false);
        }

        mRecodeTime = 0;
        //获取红包
        ArrayMap<String, String> keyValue = new ArrayMap<>();
        String uid = AiSouAppInfoModel.getInstance().getUID();

        //获取积分和余额
        keyValue.clear();
        keyValue.put("user", uid);
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_AMOUNT, keyValue, ApiConstants.USERPRICEBAG);

        //是否有支付密码
        keyValue.clear();
        keyValue.put("user", uid);
        ++mRecodeTime;
        mHttpConnectedUtils.startNetworkGetString(ID_HAS_PASSWORD, keyValue, ApiConstants.STATE_PAY_PASSWORD);
    }

    /**
     * 获取店内支付订单
     */
    public void getMerchantOrder(PayOrderBean payOrderBean, String integral, String wallet, @Nullable String password) {
        if (TextUtils.isEmpty(payOrderBean.getPrice()) || TextUtils.isEmpty(payOrderBean.getShopId())) {
            if (null != iPayOderStater) {
                iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_get_merchant_data_fail), true);
            }
            return;
        }
        if (null != iPayOderStater) {
            iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_update_order), false);
        }

        ArrayMap<String, String> keyValue = new ArrayMap<>(13);

        keyValue.put("shop", payOrderBean.getShopId());
        keyValue.put("user", AiSouAppInfoModel.getInstance().getUID());

        keyValue.put("voucher", TextUtils.isEmpty(payOrderBean.getRedPacketId()) ? "" : payOrderBean.getRedPacketId());

        keyValue.put("score", TextUtils.isEmpty(integral) ? "0" : integral);
        keyValue.put("purse", TextUtils.isEmpty(wallet) ? "0" : wallet);

        keyValue.put("total", payOrderBean.getPrice());
        keyValue.put("sort", "2");//2 店内支付

        if (!TextUtils.isEmpty(password)) {
            keyValue.put("pwd", password);
        }

        mHttpConnectedUtils.startNetworkGetString(ID_MERCHANT_ORDER, keyValue, ApiConstants.MERCHANT_PAY);
    }

    /*
   * 获取商品订单号
   * */
    public void getGoodsOrder(PayOrderBean payOrderBean, String integral, String wallet, @Nullable String password) {
        final ArrayList<GoodsItemBean> goodsItemBean = payOrderBean.getGoodsItemBean();

        if (null == goodsItemBean || goodsItemBean.isEmpty() || TextUtils.isEmpty(payOrderBean.getShopId())) {
            if (null != iPayOderStater) {
                iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_get_goods_data_fail), true);
            }
            return;
        }
        if (null != iPayOderStater) {
            iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_update_order), false);
        }

        IdentityHashMap<String, String> keyValue = new IdentityHashMap<>(16 + goodsItemBean.size());

        for (GoodsItemBean goodsItem : goodsItemBean) {
            keyValue.put(new String("goods"), goodsItem.getGoodsId());
            keyValue.put(new String("qty"), String.valueOf(goodsItem.getGoodsCount()));
        }


        keyValue.put("shop", payOrderBean.getShopId());
        keyValue.put("user", AiSouAppInfoModel.getInstance().getUID());
        keyValue.put("voucher", TextUtils.isEmpty(payOrderBean.getRedPacketId()) ? "" : payOrderBean.getRedPacketId());
        keyValue.put("score", TextUtils.isEmpty(integral) ? "0" : integral);
        keyValue.put("purse", TextUtils.isEmpty(wallet) ? "0" : wallet);
        keyValue.put("sort", "1");//1为商品支付
        keyValue.put("ship_sort", TextUtils.isEmpty(payOrderBean.getPurchaseSort()) ? "" : payOrderBean.getPurchaseSort());

        if (!TextUtils.isEmpty(password)) {
            keyValue.put("pwd", password);
        }
        if (!TextUtils.isEmpty(mReceiptName)) {
            keyValue.put("receive_name", mReceiptName);
        }
        if (!TextUtils.isEmpty(mReceiptTel)) {
            keyValue.put("receive_tel", mReceiptTel);
        }
        if (!TextUtils.isEmpty(mReceiptAddress)) {
            keyValue.put("receive_address", mReceiptAddress);
        }
        if (!TextUtils.isEmpty(payOrderBean.getLeaveMessage())) {
            keyValue.put("des", payOrderBean.getLeaveMessage());
        }
        mHttpConnectedUtils.startNetworkGetString(ID_GOODS_ORDER, keyValue, ApiConstants.GET_GOODS_ORDER);
    }

    /**
     * 是否有支付密码
     */
    public int isHasPassword() {
        if (isCheckPasswordSuccessful && hasPassword) {
            return HAS_PASSWORD;
        } else if (isCheckPasswordSuccessful) {
            return NO_PASSWORD;
        } else {
            return FAIL_PASSWORD;
        }
    }

    @MainThread
    public void onDestroy() {
        if (null != mHttpConnectedUtils) {
            mHttpConnectedUtils.onDestroy();
        }
        if (null != mRedDataBean) {
            mRedDataBean.clear();
        }

        iPayOderStater = null;
    }

    public interface IPayOderStater {
        /**
         * 检测支付密码正确、错误 回调
         */
        void onCheckPasswordResult(boolean isSuccessful, String errorHint);

        void showDialog(String hint, boolean isError);

        void closeDialog();

        void inflateData();

        void onCreateOrderResult(boolean isSuccessful, @Nullable DataRow dataRow);
    }

    public void checkPayPassword(String password) {
        ArrayMap<String, String> pass = new ArrayMap<>();
        pass.put("user", AiSouAppInfoModel.getInstance().getUID());
        pass.put("pwd", password);
        mHttpConnectedUtils.startNetworkGetString(ID_AFFIRM_PASSWORD, pass, ApiConstants.CHECKPAYPWD);
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_RED_PACKET:
                initRedPacketData(response);
                break;
            case ID_AMOUNT:
                initAmountData(response);
                break;
            case ID_ADDRESS:
                initAddressData(response);
                break;
            case ID_HAS_PASSWORD:
                initPasswordState(response);
                break;
            case ID_AFFIRM_PASSWORD:
                initCheckPasswordState(response);
                break;
            case ID_GOODS_ORDER:
                initGoodsOrderData(response);
                break;
            case ID_MERCHANT_ORDER:
                initMerchantOrderData(response);
                break;
            case ID_MERCHANT_DETAIL:
                initMerchantInfoData(response);
                break;
            case ID_MERCHANT_MEMBER_INFO:
                initMerchantMemberInfo(response);
                break;
        }
    }

    /**
     * 初始化商家会员信息
     * {"result":true,"code":"200",
     * <p>
     * "data":{"LEVEL_NM":"VIP6","SCORE":60000,"SHOP_ID":211,"RATE":null,"ALIAS":null,
     * "SHOP_NM":"青岛隐珠假日酒店","USER_ID":200,"LEVEL_ID":6,"BALANCE":22,"IS_RECEIVE_PUSH":1,
     * "USER_PHONE":"18396800426","USER_NM":"晴","ID":14,"SHOP_AVATAR":"/ig?id=4545"}
     * <p>
     * ,"success":true,"type":"map","message":null}
     */
    private void initMerchantMemberInfo(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false)) {
            mMerchantMemberData = dataRow.getRow("data");
            checkAllNetworkState(true);
        } else {
            checkAllNetworkState(false);
        }
    }

    /**
     * 初始化商家详情数据
     */
    private void initMerchantInfoData(String response) {
        DataRow detail = DataRow.parseJson(response);
        if (null != detail && detail.getBoolean("result", false)
                && null != (detail = detail.getRow("data"))) {
            this.mMerchantDetailData = detail;
            checkAllNetworkState(true);
        } else {
            checkAllNetworkState(false);
        }
    }

    /**
     * 店内支付订单
     * data :
     * <p>
     * {"BUYER_ID":"12","CASH_PRICE":0.15,"CASH_SCORE":10,"ID":31,"PURSE_PRICE":0.2,
     * "SELLER_ID":"8","SHOP_SELLER_ID":"8","SORT_ID":"2","TOTAL_PRICE":0.4,
     * "VOUCHER_ID":"","VOUCHER_VAL":0}
     * <p>
     * result : true
     * success : true
     * type : map
     */
    private void initMerchantOrderData(String response) {
        //店内支付与商品支付返回数据 暂时一样
        initGoodsOrderData(response);
    }

    /**
     * 提交商品订单后
     * data :
     * {"BUYER_ID":"12","CASH_PRICE":0.15,"CASH_SCORE":10,"ID":31,"PURSE_PRICE":0.2,"SELLER_ID":"8",
     * "SHOP_SELLER_ID":"8","SORT_ID":"2","TOTAL_PRICE":0.4,"VOUCHER_ID":"","VOUCHER_VAL":0}
     * result : true
     * success : true
     * type : map
     */
    private void initGoodsOrderData(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false) && null != dataRow.getRow("data")) {
            if (null != iPayOderStater) {
                iPayOderStater.onCreateOrderResult(true, dataRow.getRow("data"));
            }
        } else {
            if (null != iPayOderStater) {
                iPayOderStater.onCreateOrderResult(false, dataRow);
            }
        }

    }

    /**
     * 检查输入的支付密码正确与否
     */
    private void initCheckPasswordState(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false)) {
            if (null != iPayOderStater) {
                iPayOderStater.onCheckPasswordResult(true, null);
            }
        } else {
            if (null != iPayOderStater) {
                iPayOderStater.onCheckPasswordResult(false, null == dataRow ? fAppliacationContext.getString(net.twoant.master.R.string.payment_pay_password_error) : dataRow.getString("message"));
            }
        }
    }

    /**
     * 是否有支付密码
     */
    private void initPasswordState(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false)) {
            hasPassword = "1".equalsIgnoreCase(dataRow.getString("DATA"));
            isCheckPasswordSuccessful = true;
            checkAllNetworkState(true);
        } else {
            isCheckPasswordSuccessful = false;
            checkAllNetworkState(false);
        }
    }

    /**
     * 地址信息数据
     */
    private void initAddressData(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false) && null != (dataRow = dataRow.getRow("data"))) {
            mReceiptName = dataRow.getString("RECEIPT_NAME");
            mReceiptTel = dataRow.getString("RECEIPT_TEL");
            mReceiptAddress = dataRow.getString("RECEIPT_ADDRESS");
            checkAllNetworkState(true);
        } else {
            checkAllNetworkState(false);
        }
    }

    /**
     * 账号余额、积分
     */
    private void initAmountData(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false) && null != (dataRow = dataRow.getRow("data"))) {
            mIntegralSum = dataRow.getDouble("SCORE_BALANCE");
            mWalletSum = dataRow.getDouble("PURSE_BALANCE");
            checkAllNetworkState(true);
        } else {
            checkAllNetworkState(false);
        }
    }

    /**
     * data : [
     * {
     * "ACTIVE_VAL":0, 激活总价
     * "ACTIVITY_ID":54,
     * "ACTIVITY_ITEM_ID":157,
     * "ACTIVITY_ITEM_TITLE":"系统红包100",
     * "ACTIVITY_SORT_ID":4,
     * "ACTIVITY_SORT_NM":"红包活动",
     * "ACTIVITY_TITLE":"系统红包100",
     * "BALANCE":100,
     * "BUY_TIME":"2017-01-05 05:47:28",
     * "END_TIME":"2017-01-26 12:00:00",
     * "ID":273,
     * "IS_ENABLE":1,
     * "ORDER_ID":671,
     * "ORDER_ITEM_ID":29,
     * "ORDER_ITEM_PRICE":0,
     * "ORDER_ITEM_SCORE":0,
     * "SORT_ID":12,   sort_id 12 - 红包， 21 - 储值卡
     * "SORT_NM":"商家一次性代金券",
     * "USER_AVATAR":"/avatar/14833582468327101fa7b.jpg",
     * "USER_ID":62,
     * "USER_NM":
     * "17660635389",
     * "USE_STATUS":0,
     * "VAL":100
     * }]
     * result : true
     * success : true
     * type : list
     */
    private void initRedPacketData(String response) {
        DataRow dataRow = DataRow.parseJson(response);
        if (null != dataRow && dataRow.getBoolean("result", false)) {
            DataSet data = dataRow.getSet("data");
            if (null != data) {
                mRedDataBean = data.getRows();
            }
            checkAllNetworkState(true);
            return;
        }
        checkAllNetworkState(false);
    }

    private void checkAllNetworkState(boolean isSuccessful) {
        if (isSuccessful) {
            --mRecodeTime;
            if (0 == mRecodeTime && null != iPayOderStater) {
                iPayOderStater.closeDialog();
                iPayOderStater.inflateData();
            }
        } else if (null != iPayOderStater) {
            iPayOderStater.showDialog(fAppliacationContext.getString(net.twoant.master.R.string.payment_synchronization_data_fail), true);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        switch (id) {
            case ID_MERCHANT_MEMBER_INFO:
            case ID_MERCHANT_DETAIL:
            case ID_RED_PACKET:
            case ID_AMOUNT:
            case ID_ADDRESS:
                checkAllNetworkState(false);
                break;
            case ID_HAS_PASSWORD:
                checkAllNetworkState(false);
                isCheckPasswordSuccessful = false;
                break;
            case ID_AFFIRM_PASSWORD:
                if (null != iPayOderStater) {
                    iPayOderStater.onCheckPasswordResult(false, NetworkUtils.getNetworkStateDescription(call, e, fAppliacationContext.getString(net.twoant.master.R.string.payment_pay_password_fail)));
                }
                break;
            case ID_GOODS_ORDER:
            case ID_MERCHANT_ORDER:
                if (null != iPayOderStater) {
                    iPayOderStater.onCreateOrderResult(false, null);
                }
                break;
        }
    }
}
