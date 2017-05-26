package net.twoant.master.ui.charge.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.PayDialog;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.wxapi.RechargeSuccessActivtity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2017/1/8.
 * 佛祖保佑   永无BUG
 */

public class WaitPayDetailActivity extends LongBaseActivity implements View.OnClickListener{

    private RelativeLayout relativeLayout;
    private HintDialogUtil hintDialogUtil;
    private String shopName;
    private PassViewDialog passViewDialog;
    private String cash_price;
    private DataRow data;

    private PayDialog payDialog;
    private static String partName;
    private static String totalPrice;
    private static String red;
    private static String useIntegral;
    private static String yue;
    private static String pay;
    private static String orderId;

    public static void startActivity(Activity activity,String partName,String totalPrice,String red,String useIntegral,String yue,String pay,String orderId) {
        WaitPayDetailActivity.partName = partName;
        WaitPayDetailActivity.totalPrice = totalPrice;
        WaitPayDetailActivity.red = red;
        WaitPayDetailActivity.useIntegral = useIntegral;
        WaitPayDetailActivity.yue = yue;
        WaitPayDetailActivity.pay = pay;
        WaitPayDetailActivity.orderId = orderId;
        Intent intent = new Intent(activity, WaitPayDetailActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.startActivity(intent);
        else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_waitpay_detail;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        TextView red1 = (TextView) findViewById(R.id.tv_red_useable_paydetail);
        red1.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_entery_paydetail).setOnClickListener(this);
        TextView partName1 = (TextView) findViewById(R.id.tv_receiveparty_paydetail);
        TextView totalPrice1 = (TextView) findViewById(R.id.tv_oldprice_paydetail);
        TextView integral1 = (TextView) findViewById(R.id.tv_integral);
        TextView yue1 = (TextView) findViewById(R.id.cc);
        TextView haixu1 = (TextView) findViewById(net.twoant.master.R.id.tv_payprice_paydetail);
        TextView integral2Money = (TextView) findViewById(R.id.tv_integral_to_money_paydetail);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_useablered_paydetail);
        integral2Money.setText(Double.parseDouble(useIntegral)/200+"元");
        if (TextUtils.isEmpty(red) || "0.0".equals(red) || "0".equals(red) || "0.00".equals(red)) {
            relativeLayout.setVisibility(View.GONE);
        }else{
            relativeLayout.setVisibility(View.VISIBLE);
            red1.setText(red);
        }
        partName1.setText(partName);
        totalPrice1.setText("¥"+totalPrice);
        integral1.setText(useIntegral);
        yue1.setText("¥"+yue);
        haixu1.setText("¥"+pay);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl_useablered_paydetail);
        hintDialogUtil = new HintDialogUtil(this);
        passViewDialog = new PassViewDialog(this,Gravity.BOTTOM,true);

        AiSouAppInfoModel.getInstance().addWaitOrderActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_entery_paydetail:
                requestNetPay();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void requestNetPay() {
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("id",orderId);
        LongHttp(ApiConstants.WAITPAY_PAY, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("获取订单失败:"+e);
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                data = dataRow.getRow("data");
                cash_price = data.getString("CASH_PRICE");
                shopName = data.getString("SHOP_SELLER_NM");
                String orderId = data.getString("ID");
                if ("0.0".equals(cash_price) || "0".equals(cash_price)) {
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
                            hintDialogUtil.dismissDialog();
                            DataRow dataRow = DataRow.parseJson(response);
                            String data = (String) dataRow.get("data");
                            LogUtils.d(data);
                            if (data.equals("0")) {
                                //没有支付密码，去设置
                                SetPasswordDialog setPasswordDialog = new SetPasswordDialog(WaitPayDetailActivity.this, Gravity.CENTER,false);
                                setPasswordDialog.showDialog(true,true);
                                return;
                            }else{
                                //输入平台的支付密码
                                passViewDialog.clearn();
                                passViewDialog.showDialog(true,true);
                                passViewDialog.setTile(partName);
                            }
                        }
                    });
                }else {
                    payDialog = new PayDialog(WaitPayDetailActivity.this,Gravity.BOTTOM,true,orderId,false);
                    payDialog.setNeedPayMoney(cash_price +"");
                    payDialog.showDialog(false,true);
                    RechargeSuccessActivtity.paymoney = totalPrice;
                }
            }
        });
    }
}
