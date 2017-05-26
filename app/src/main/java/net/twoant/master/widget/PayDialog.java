package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.activity.out.PayDetailActivity;
import net.twoant.master.ui.my_center.activity.out.PostOrderActivity;
import net.twoant.master.ui.my_center.bean.AliPayBean;
import net.twoant.master.ui.my_center.bean.PostOrderBean;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.wxapi.RechargeSuccessActivtity;
import net.twoant.master.wxapi.WXPayBean;
import net.twoant.master.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/6.
 * 佛祖保佑   永无BUG
 */

public class PayDialog extends BaseDialog {
    private RadioGroup radioGroup;
    private int payType = 0;//默认是选择了微信支付
    private static Activity context;
    private TextView payMoney;
    private String price;
    private String shopid;//支付给商家的id
    private String voucher;//用户使用红包抵用的钱
    private String score;//用户使用积分抵用的钱
    private String purse;//用户使用余额抵用的钱
    private String orderId;//店内支付的订单号
    private boolean is2PayFinish;
    private int sort;//用户配送方式
    /** * 1	商品支付
     * 2	店内支付
     * 3	活动支付
     * 4	购买积分
     * */
    private int order_type;//订单类别
    private IWXAPI iwxapi;
    private HintDialogUtil hintDialogUtil;

    private IOnItemClickListener iOnItemClickListener;

    /**
     * item 点击回调接口
     */
    public interface IOnItemClickListener {
        void onItemClickListener(int position);
    }

    private static final int SDK_PAY_FLAG = 1;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    String obj = (String) msg.obj;
                    System.out.println(obj);
                    AliPayBean payResult = new AliPayBean((String) msg.obj);
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签

                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent(context,RechargeSuccessActivtity.class);
                        intent.putExtra("is_shop_pay","is_shop_pay");
                        context.startActivity(intent);
                        ToastUtil.showLong("支付成功");
                        context.finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.showLong("支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtil.showLong("支付取消");
                        }
                    }
                    break;
                }
            }
        };
    };

    String pay_info;
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public PayDialog(final Activity context, int gravity, boolean fillWidth,
                     String shopid,String voucher,String score,String purse,int sort) {
        super(context, gravity, fillWidth);
        this.context = (PayDetailActivity) context;
        hintDialogUtil = new HintDialogUtil((Activity) context);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_pay, null);
        radioGroup = (RadioGroup) mView.findViewById(net.twoant.master.R.id.rg_group_pay);
        payMoney = (TextView) mView.findViewById(net.twoant.master.R.id.tv_paymoney_dialogpay);
        mView.findViewById(net.twoant.master.R.id.tv_exit_dialog_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroup.check(net.twoant.master.R.id.rb_weixin_pay);
        this.shopid = shopid;
        this.voucher = voucher;
        this.score = score;
        this.purse = purse;
        this.sort = sort;
        mView.findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取订单信息
                postOrder();
            }
        });
        mAlertDialog.setView(mView);

    }
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public PayDialog(final Activity context, int gravity, boolean fillWidth,
                     String shopid, String voucher, String score, String purse, int sort, final int order_type) {
        super(context, gravity, fillWidth);
        this.context = context;
        hintDialogUtil = new HintDialogUtil((Activity) context);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_pay, null);
        radioGroup = (RadioGroup) mView.findViewById(net.twoant.master.R.id.rg_group_pay);
        payMoney = (TextView) mView.findViewById(net.twoant.master.R.id.tv_paymoney_dialogpay);
        mView.findViewById(net.twoant.master.R.id.tv_exit_dialog_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroup.check(net.twoant.master.R.id.rb_weixin_pay);
        this.shopid = shopid;
        this.voucher = voucher;
        this.score = score;
        this.purse = purse;
        this.sort = sort;
        this.order_type=order_type;
        mView.findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取订单信息
                postOrderWXAli(order_type);
            }
        });
        mAlertDialog.setView(mView);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case net.twoant.master.R.id.rb_weixin_pay:
                        payType = 0;
                        iOnItemClickListener.onItemClickListener(0);
                        break;
                    case net.twoant.master.R.id.rb_zhifubao_pay:
                        payType = 1;
                        iOnItemClickListener.onItemClickListener(1);
                        break;
                }
            }
        });
    }

    public void setNeedPayMoney(String moeny){
        payMoney.setText(moeny+"元");
    }

    /**
     * 店内支付使用开启
     * */
    public PayDialog(final Activity context, int gravity, boolean fillWidth, final String orderId) {
        super(context, gravity, fillWidth);
        this.context = context;
        this.orderId = orderId;
        hintDialogUtil = new HintDialogUtil((Activity) context);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_pay, null);
        radioGroup = (RadioGroup) mView.findViewById(net.twoant.master.R.id.rg_group_pay);
        payMoney = (TextView) mView.findViewById(net.twoant.master.R.id.tv_paymoney_dialogpay);
        mView.findViewById(net.twoant.master.R.id.tv_exit_dialog_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroup.check(net.twoant.master.R.id.rb_weixin_pay);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case net.twoant.master.R.id.rb_weixin_pay:
                        payType = 0;
                        break;
                    case net.twoant.master.R.id.rb_zhifubao_pay:
                        payType = 1;
                        break;
                }
            }
        });
        mView.findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType == 0) {
                    iwxapi = WXAPIFactory.createWXAPI(context, ApiConstants.APP_ID, false);
                    iwxapi.registerApp(ApiConstants.APP_ID);
                    if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                        //开启微信支付
                        payWX(orderId);
                    } else {
                        ToastUtil.showLong("您的微信版本过低");
                    }
                    RechargeSuccessActivtity.payType = "微信";//给支付成功界面赋值
                }else if (payType == 1) {
                    //支付宝支付
                    PayHttpUtils.AliPay(orderId, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject json = new JSONObject(response);
                                pay_info = json.getString("data");
                                String result  = json.getString("result");
                                if (result.equals("true")) {
                                    //true成功，false失败
                                }
                                System.out.println(pay_info);
                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(PayDialog.context);
                                        // 调用支付接口，获取支付结果
                                        String result = alipay.pay(pay_info,true);//第二个参数是否有加载框
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RechargeSuccessActivtity.payType = "支付宝";//给支付成功界面赋值
                }
            }
        });
        mAlertDialog.setView(mView);
    }
    /**
     * 店内支付使用开启
     * */
    public PayDialog(final Activity context, int gravity, boolean fillWidth, final String orderId, String prices) {
        super(context, gravity, fillWidth);
        this.context = (Activity) context;
        this.orderId = orderId;
        this.price=prices;
        hintDialogUtil = new HintDialogUtil((Activity) context);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_pay, null);
        radioGroup = (RadioGroup) mView.findViewById(net.twoant.master.R.id.rg_group_pay);
        payMoney = (TextView) mView.findViewById(net.twoant.master.R.id.tv_paymoney_dialogpay);
        mView.findViewById(net.twoant.master.R.id.tv_exit_dialog_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setNeedPayMoney(prices);
        radioGroup.check(net.twoant.master.R.id.rb_weixin_pay);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case net.twoant.master.R.id.rb_weixin_pay:
                        payType = 0;
                        break;
                    case net.twoant.master.R.id.rb_zhifubao_pay:
                        payType = 1;
                        break;
                }
            }
        });
        mView.findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType == 0) {
                    ToastUtil.showLong("我们正在抓紧开发中");
                    return;

                }else if (payType == 1) {
                    //支付宝支付
                    PayHttpUtils.AliPay(orderId, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject json = new JSONObject(response);
                                pay_info = json.getString("data");
                                String result  = json.getString("result");
                                if (result.equals("true")) {
                                    //true成功，false失败
                                }
                                System.out.println(pay_info);
                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(PayDialog.context);
                                        // 调用支付接口，获取支付结果
                                        String result = alipay.pay(pay_info,true);//第二个参数是否有加载框
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RechargeSuccessActivtity.payType = "支付宝";//给支付成功界面赋值
                    RechargeSuccessActivtity.paymoney = price;
                    RechargeSuccessActivtity.payShopName = PostOrderActivity.shop_name;

                }
            }
        });
        mAlertDialog.setView(mView);
    }
    /**
     * 店内支付使用开启
     * @param context
     * @param gravity
     * @param orderId 订单号
     * @param is2PayFinish 支付完成是否跳转 订单的支付完成列表
     * */
    public PayDialog(final Activity context, int gravity, boolean fillWidth, final String orderId,boolean is2PayFinish) {
        super(context, gravity, fillWidth);
        this.context =  context;
        this.orderId = orderId;
        this.is2PayFinish = is2PayFinish;
        hintDialogUtil = new HintDialogUtil((Activity) context);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_pay, null);
        radioGroup = (RadioGroup) mView.findViewById(net.twoant.master.R.id.rg_group_pay);
        payMoney = (TextView) mView.findViewById(net.twoant.master.R.id.tv_paymoney_dialogpay);
        mView.findViewById(net.twoant.master.R.id.tv_exit_dialog_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        radioGroup.check(net.twoant.master.R.id.rb_weixin_pay);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case net.twoant.master.R.id.rb_weixin_pay:
                        payType = 0;
                        break;
                    case net.twoant.master.R.id.rb_zhifubao_pay:
                        payType = 1;
                        break;
                }
            }
        });
        mView.findViewById(net.twoant.master.R.id.btn_paymoney_dialogpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType == 0) {
                    iwxapi = WXAPIFactory.createWXAPI(context, ApiConstants.APP_ID, false);
                    iwxapi.registerApp(ApiConstants.APP_ID);
                    if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                        //开启微信支付
                        payWX(orderId);
                    } else {
                        ToastUtil.showLong("您的微信版本过低");
                    }
                    RechargeSuccessActivtity.payType = "微信";//给支付成功界面赋值
                }else if (payType == 1) {
                    //支付宝支付
                    PayHttpUtils.AliPay(orderId, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject json = new JSONObject(response);
                                pay_info = json.getString("data");
                                String result  = json.getString("result");
                                if (result.equals("true")) {
                                    //true成功，false失败
                                }
                                System.out.println(pay_info);
                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 构造PayTask 对象
                                        PayTask alipay = new PayTask(PayDialog.context);
                                        // 调用支付接口，获取支付结果
                                        String result = alipay.pay(pay_info,true);//第二个参数是否有加载框
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                // 必须异步调用
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    RechargeSuccessActivtity.payType = "支付宝";//给支付成功界面赋值
                }
            }
        });
        mAlertDialog.setView(mView);
    }


    private void postOrderWXAli(int order_type){

        PayHttpUtils.getOder(shopid,voucher,score,purse,sort+"",order_type+"",new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("错误");
                ToastUtil.showLong("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                PostOrderBean postOrderBean = JsonUtil.parseJsonToBean(response, PostOrderBean.class);
                List<String> data = postOrderBean.getData();
                String money = data.get(1);//还需要支付的钱
                String order = data.get(0);//订单号
                if ("0".equals(money)){//如果是0代表无需再支付，输入密码从用户余额中扣除
                    //输入密码从用户余额中扣除

                }else {
                    if (payType == 0) {
                        iwxapi = WXAPIFactory.createWXAPI(context, ApiConstants.APP_ID, false);
                        iwxapi.registerApp(ApiConstants.APP_ID);
                        if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                            //开启微信支付
                            payWX(order);
                            RechargeSuccessActivtity.paymoney = money;
                        } else {
                            ToastUtil.showLong("您的微信版本过低");
                        }
                    }else if (payType == 1) {
                        //支付宝支付,用获取到的订单号获取支付信息，并调用支付宝支付
                        PayHttpUtils.AliPay(order, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean("result")){
                                        pay_info=json.getString("data");
                                        Runnable payRunnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                // 构造PayTask 对象
                                                PayTask alipay = new PayTask(PayDialog.context);
                                                // 调用支付接口，获取支付结果
                                                String result = alipay.pay(pay_info,true);//第二个参数是否有加载框
                                                Message msg = new Message();
                                                msg.what = SDK_PAY_FLAG;
                                                msg.obj = result;
                                                mHandler.sendMessage(msg);
                                            }
                                        };
                                        // 必须异步调用
                                        Thread payThread = new Thread(payRunnable);
                                        payThread.start();
                                    }
                                }catch (Exception e){

                                }
                            }
                        });
                        //支付宝支付
                    }
                }
            }
        });
    }

    private void postOrder(){
        hintDialogUtil.showLoading(net.twoant.master.R.string.startpay);
        PayHttpUtils.getOder(shopid,voucher,score,purse,sort+"","",new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("错误");
                ToastUtil.showLong("连接失败");
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                PostOrderBean postOrderBean = JsonUtil.parseJsonToBean(response, PostOrderBean.class);
                List<String> data = postOrderBean.getData();
                String money = data.get(1);//还需要支付的钱
                String order = data.get(0);//订单号
                if ("0".equals(money)){//如果是0代表无需再支付，输入密码从用户余额中扣除
                   //输入密码从用户余额中扣除

                }else {
                    if (payType == 0) {
                        iwxapi = WXAPIFactory.createWXAPI(context, ApiConstants.APP_ID, false);
                        iwxapi.registerApp(ApiConstants.APP_ID);
                        if (iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                            //开启微信支付
                            payWX(order);
                            RechargeSuccessActivtity.paymoney = money;
                        } else {
                            ToastUtil.showLong("您的微信版本过低");
                        }
                    }else if (payType == 1) {
                          //支付宝支付
                          PayHttpUtils.AliPay(order, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hintDialogUtil.dismissDialog();
                                ToastUtil.showLong("获取失败:" + e);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                System.out.println(response);
                                hintDialogUtil.dismissDialog();
                                try {
                                    JSONObject json = new JSONObject(response);
                                    pay_info = json.getString("data");
                                    Runnable payRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            // 构造PayTask 对象
                                            PayTask alipay = new PayTask(PayDialog.context);
                                            // 调用支付接口，获取支付结果
                                            String result = alipay.pay(pay_info,true);//第二个参数是否有加载框
                                            Message msg = new Message();
                                            msg.what = SDK_PAY_FLAG;
                                            msg.obj = result;
                                            mHandler.sendMessage(msg);
                                        }
                                    };
                                    // 必须异步调用
                                    Thread payThread = new Thread(payRunnable);
                                    payThread.start();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void payWX(String order) {
        hintDialogUtil.showLoading(net.twoant.master.R.string.startpay);
        PayHttpUtils.WXPay(order, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("获取失败:"+e);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                response = response.replace("package","packageX");
                System.out.println(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        WXPayBean wxPayBean = GsonUtil.gsonToBean(response, WXPayBean.class);
                        if (wxPayBean.isResult()) {
                            PayReq payReq = new PayReq();
                            WXPayBean.DataBean dataBean = wxPayBean.getData();
                            payReq.appId = dataBean.getAppid();
                            payReq.partnerId = dataBean.getPartnerid();
                            payReq.prepayId = dataBean.getPrepayid();
                            payReq.nonceStr = dataBean.getNoncestr();
                            payReq.timeStamp = dataBean.getTimestamp();
                            payReq.packageValue = dataBean.getPackageX();
                            payReq.sign = dataBean.getSign();
                            if (is2PayFinish) {
                                WXPayEntryActivity.isShop = true;//给微信支付结果赋值变量 是商家支付传进来的
                            }else {
                                WXPayEntryActivity.isShop = false;
                            }
                            iwxapi.sendReq(payReq);
                        }else{
                            ToastUtil.showLong("请先登录");
                            return;
                        }
                        hintDialogUtil.dismissDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置listView item 点击回调
     *
     * @param clickListener 监听对象
     */
    public <D> void setOnItemClickListener(IOnItemClickListener clickListener) {
        if (iOnItemClickListener == null) {
            iOnItemClickListener = clickListener;
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch(checkedId){
                        case net.twoant.master.R.id.rb_weixin_pay:
                            payType = 0;
                            iOnItemClickListener.onItemClickListener(0);
                            break;
                        case net.twoant.master.R.id.rb_zhifubao_pay:
                            payType = 1;
                            iOnItemClickListener.onItemClickListener(1);
                            break;
                    }
                }
            });
        }
    }

    public void onDestroy() {
        mActivity = null;
        if (null != mAlertDialog && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        mAlertDialog = null;
        context = null;
    }
}
