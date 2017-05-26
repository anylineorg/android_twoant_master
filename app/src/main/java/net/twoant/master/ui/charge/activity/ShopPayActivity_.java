package net.twoant.master.ui.charge.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.AESHelper;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.ShopPayBean;
import net.twoant.master.ui.my_center.activity.out.PayDetailActivity;
import net.twoant.master.ui.my_center.bean.BalanceBean;
import net.twoant.master.ui.my_center.bean.RedListToShopBean;
import net.twoant.master.ui.my_center.httputils.PayHttpUtils;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.ErrorPayPasswordDialog;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.PayDialog;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.wxapi.RechargeSuccessActivtity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by guo15 on 2017/3/16.
 */

public class ShopPayActivity_ extends LongBaseActivity implements View.OnClickListener {
    public static ShopPayActivity_ instanceShopActivity = null;
    public String shopid;//支付给对方店家的shopid
    private EditText oldPrice;
    private TextView tvSurplusIntegral;
    private TextView etIntegral;
    private EditText integral2Money;
    private EditText useBalance;
    private TextView payPrice;
    private ArrayList<String> redList;
    private ArrayList<String> redUseAbleList;
    private HintDialogUtil hintDialogUtil;
    private boolean flag = false;
    private ErrorPayPasswordDialog errorPayPasswordDialog;
    private PassViewDialog passViewDialog;
    private float total_price;

    private int x = 0;//记录两个网络请求是否都请求完毕

    //用户 红包列表
    public List<RedListToShopBean.DataBean> itemReds;
    private double purse_balance;
    private double score_balance;
    private TextView receiveParty;
    private float red;//红包抵用钱
    private String red_price;
    private List<String> redSelectedListId = new ArrayList<>();
    private int redSelectedPosition = -1;
    private TextView redUseAble;
    private long need_score;//需要支付的积分
    private double need_score_money;//需要积分抵用金额
    private long score_pay; //支付积分
    private double score_money_pay;//积分抵用金额
    private double need_purse; //需要用余额支付的金额
    private double purse_pay; //余额抵用金额
    private double needPay;
    private TextView pursebalance;
    private BigDecimal old_price = null;
    private BigDecimal integral = null;
    private double score_money;
    private TextWatcher integralWatcher;
    private TextWatcher oldPriceWatcher;
    private int selectionStart;
    private TextWatcher userBalanceWatcher;
    private boolean canBalanceEdite = false;
    private boolean canScoreEdite = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PayDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_shop_pay;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        instanceShopActivity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        String shopidAES = getIntent().getStringExtra("result");
        shopid = AESHelper.decrypt(shopidAES, "aisou");

        //收款方
        receiveParty = (TextView) findViewById(net.twoant.master.R.id.tv_receiveparty_paydetail);
        //支付金额
        oldPrice = (EditText) findViewById(net.twoant.master.R.id.et_oldprice_paydetail);
        //积分余额
        tvSurplusIntegral = (TextView) findViewById(net.twoant.master.R.id.tv_surplus_paydetail);
        //钱包余额
        pursebalance = (TextView) findViewById(net.twoant.master.R.id.cc);
        //使用积分
        etIntegral = (TextView) findViewById(net.twoant.master.R.id.et_integral_shoppay);
        //积分抵金额
        integral2Money = (EditText) findViewById(net.twoant.master.R.id.tv_integral_to_money_paydetail);
        //钱包支付
        useBalance = (EditText) findViewById(net.twoant.master.R.id.bb);
        //还需支付
        payPrice = (TextView) findViewById(net.twoant.master.R.id.tv_payprice_paydetail);

        //使用红包
        redUseAble = (TextView) findViewById(net.twoant.master.R.id.tv_red_useable_paydetail);
        //返回
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        //确认付款
        findViewById(net.twoant.master.R.id.btn_entery_shoppay).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_useablered_paydetail).setOnClickListener(this);

        old_price = new BigDecimal("0");

        integral2Money.setEnabled(false);
        useBalance.setEnabled(false);

        redList = new ArrayList();
        redUseAbleList = new ArrayList();
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.showLoading();
        hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (flag) {
                    finish();
                }
            }
        });

        oldPriceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                //*******************这里展示***********************
                integral2Money.removeTextChangedListener(integralWatcher);
                oldPrice.removeTextChangedListener(oldPriceWatcher);
                useBalance.removeTextChangedListener(userBalanceWatcher);


                //需支付金额
                String trim = s.toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    if (".".equals(trim.substring(0, 1))) {
                        if (subZeroAndDot(trim).length() > 3) {
                            trim = trim.subSequence(1, trim.length()).toString();
                            oldPrice.setText(subZeroAndDot(trim));
                        } else {
                            trim = "0" + trim;
                            oldPrice.setText(trim);
                            oldPrice.setSelection(2);
                        }
                    }
                    if (trim.length() > 1 && "0".equals(trim.substring(0, 1)) && !".".equals(trim.substring(1, 2))) {
                        trim = trim.substring(1, s.length()).toString();
                        oldPrice.setText(trim);
                        oldPrice.addTextChangedListener(oldPriceWatcher);
                        integral2Money.addTextChangedListener(integralWatcher);
                        useBalance.addTextChangedListener(userBalanceWatcher);
                        return;
                    }
                    if (trim.contains(".") && 2 < trim.substring(trim.indexOf(".") + 1, trim.length()).length()) {
                        selectionStart = oldPrice.getSelectionStart();
                        if (trim.length() != selectionStart) {
                            trim = trim.replace(".", "");
                            oldPrice.setText(trim);
                            oldPrice.setSelection(selectionStart - 1);
                        } else {
                            trim = trim.substring(0, trim.length() - 1);
                            oldPrice.setText(trim);
                            oldPrice.setSelection(trim.length());
                        }
                        oldPrice.addTextChangedListener(oldPriceWatcher);
                        integral2Money.addTextChangedListener(integralWatcher);
                        useBalance.addTextChangedListener(userBalanceWatcher);
                        return;
                    }
                    old_price = new BigDecimal(trim);
                } else {
                    old_price = new BigDecimal("0");
                }
                red = 0;
                red_price = "0";
                redUseAble.setText("");
                redSelectedPosition = -1;
                //红包
                getRed();
                calculate();
                oldPrice.addTextChangedListener(oldPriceWatcher);
                integral2Money.addTextChangedListener(integralWatcher);
                useBalance.addTextChangedListener(userBalanceWatcher);
            }
        };

        integralWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                integral2Money.removeTextChangedListener(integralWatcher);
                useBalance.removeTextChangedListener(userBalanceWatcher);

                scorecalculate();

                integral2Money.addTextChangedListener(integralWatcher);
                useBalance.addTextChangedListener(userBalanceWatcher);
            }
        };

        userBalanceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                useBalance.removeTextChangedListener(userBalanceWatcher);
                balancecalculate();
                useBalance.addTextChangedListener(userBalanceWatcher);
            }
        };

        //输入总金额变化时
        oldPrice.addTextChangedListener(oldPriceWatcher);

        //积分抵用
        integral2Money.addTextChangedListener(integralWatcher);

        //钱包支付
        useBalance.addTextChangedListener(userBalanceWatcher);

        errorPayPasswordDialog = new ErrorPayPasswordDialog(ShopPayActivity_.this, Gravity.CENTER);
        /**
         * 自定义输入密码框输密码的回调
         * */
        passViewDialog = new PassViewDialog(ShopPayActivity_.this, Gravity.BOTTOM, true);
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), strPassword, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hintDialogUtil.dismissDialog();
                        ToastUtil.showLong("连接失败:" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = (boolean) dataRow.get("success");
                        if (success) {
                            hintDialogUtil.showLoading(net.twoant.master.R.string.use_activity);
                            getOrder();
                        } else {
                            hintDialogUtil.dismissDialog();
                            passViewDialog.clearn();
                            passViewDialog.cancel();
                            errorPayPasswordDialog.showDialog(true, true);
                            errorPayPasswordDialog.setReTry(new CancelCenterDialog.IOnClickListener() {
                                @Override
                                public void onClickListener(View v) {
                                    passViewDialog.showDialog(true, true);
                                }
                            });
//                            ToastUtil.showLong("支付密码错误,请重新输入");
                        }
                    }
                });
            }
        });
    }

    private void calculate() {
        integral = null;//积分抵用的钱

        if (!TextUtils.isEmpty(red_price)) {
            red = Float.parseFloat(red_price);
        }
        //积分
        need_score = old_price.subtract(BigDecimal.valueOf(red)).multiply(BigDecimal.valueOf(200)).longValue();
        if (need_score < 0) {
            need_score = 0;
        }
        if (score_balance >= need_score) {
            //积分充足--默认全部积分支付
            score_pay = need_score;
            score_money_pay = old_price.subtract(BigDecimal.valueOf(red)).doubleValue();
            if (score_money_pay < 0) {
                score_money_pay = 0;
            }
            purse_pay = 0;
            needPay = 0;

            etIntegral.setText(subZeroAndDot(score_pay + ""));
            tvSurplusIntegral.setText(BigDecimal.valueOf(score_balance).subtract(BigDecimal.valueOf(score_pay)).doubleValue() + "");
            integral2Money.setText(subZeroAndDot(score_money_pay + ""));
            useBalance.setText("0");
            payPrice.setText("0");
            if (0 == need_score) {
                integral2Money.setEnabled(false);
                useBalance.setEnabled(false);
            } else {
                if (canScoreEdite) {
                    integral2Money.setEnabled(true);
                }
                useBalance.setEnabled(false);
            }
        } else {
            //积分不足  自动使用积分抵扣（int）金额
            score_money_pay = BigDecimal.valueOf(score_balance).divide(BigDecimal.valueOf(200), 10, BigDecimal.ROUND_DOWN).longValue();
            score_pay = BigDecimal.valueOf(score_money_pay).multiply(BigDecimal.valueOf(200)).longValue();

            etIntegral.setText(subZeroAndDot(score_pay + ""));
            tvSurplusIntegral.setText(BigDecimal.valueOf(score_balance).subtract(BigDecimal.valueOf(score_pay)).doubleValue() + "");
            integral2Money.setText(subZeroAndDot(score_money_pay + ""));
            pursecalculate();
            if (canScoreEdite) {
                integral2Money.setEnabled(true);
            }
            if (canBalanceEdite) {
                useBalance.setEnabled(true);
            }
        }
    }

    private void scorecalculate() {
//        integral2Money.removeTextChangedListener(integralWatcher);
        //积分抵用的钱
        integral = null;

        //积分
        //输入积分抵用金额
        String s = integral2Money.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            if (".".equals(s.substring(0, 1))) {
                if (s.length() > 3) {
                    s = s.substring(1, s.length()).toString();
                    integral2Money.setText(s);
                    integral2Money.addTextChangedListener(integralWatcher);
                    useBalance.addTextChangedListener(userBalanceWatcher);
                    return;
                } else {
                    s = "0" + s;
                    integral2Money.setText(s);
                    integral2Money.setSelection(2);
                }
            }
            if (s.length() > 1 && "0".equals(s.substring(0, 1)) && !".".equals(s.substring(1, 2))) {
                s = s.substring(1, s.length()).toString();
                integral2Money.setText(s);
                integral2Money.addTextChangedListener(integralWatcher);
                useBalance.addTextChangedListener(userBalanceWatcher);
                return;
            }
            if (s.contains(".") && 2 < s.substring(s.indexOf(".") + 1, s.length()).length()) {
                selectionStart = integral2Money.getSelectionStart();
                if (s.length() != selectionStart) {
                    s = s.replace(".", "");
                    integral2Money.setText(s);
                    integral2Money.setSelection(selectionStart - 1);
                } else {
                    s = s.substring(0, s.length() - 1);
                    integral2Money.setText(s);
                    integral2Money.setSelection(s.length());
                }
                integral2Money.addTextChangedListener(integralWatcher);
                useBalance.addTextChangedListener(userBalanceWatcher);

                return;
            }
            integral = new BigDecimal(s);
        } else {
            integral = new BigDecimal("0");
        }
        //（若全部积分支付）需要积分
        need_score_money = old_price.subtract(BigDecimal.valueOf(red)).doubleValue();
        if (need_score_money < 0) {
            need_score_money = 0;
        }
        need_score = BigDecimal.valueOf(need_score_money).multiply(BigDecimal.valueOf(200)).longValue();
        if (score_balance >= need_score) {
            //积分足够支付且能支付完成
            if (integral.doubleValue() > need_score_money) {
                score_money_pay = need_score_money;
                score_pay = (int) need_score;
                purse_pay = 0;
                needPay = 0;
                useBalance.setText("0");
                payPrice.setText("0");
                integral2Money.setText(subZeroAndDot(score_money_pay + ""));
                integral2Money.setSelection(subZeroAndDot(score_money_pay + "").length());
                useBalance.setEnabled(false);
            } else {
                score_money_pay = integral.doubleValue();
                score_pay = BigDecimal.valueOf(score_money_pay).multiply(BigDecimal.valueOf(200)).longValue();
                pursecalculate();
                if (canBalanceEdite) {
                    useBalance.setEnabled(true);
                }
            }
            etIntegral.setText(subZeroAndDot(score_pay + ""));
            tvSurplusIntegral.setText(BigDecimal.valueOf(score_balance).subtract(BigDecimal.valueOf(score_pay)).doubleValue() + "");
        } else {
            //积分足够不足以支付所需金额

            //积分可抵用金额
            score_money = BigDecimal.valueOf(score_balance).divide(BigDecimal.valueOf(200), 2, BigDecimal.ROUND_DOWN).doubleValue();
            if (integral.doubleValue() >= score_money) {
                score_money_pay = score_money;

                score_pay = BigDecimal.valueOf(score_money_pay).multiply(BigDecimal.valueOf(200)).longValue();
                integral2Money.setText(subZeroAndDot(score_money_pay + ""));
                integral2Money.setSelection(subZeroAndDot(score_money_pay + "").length());

            } else {
                score_money_pay = integral.doubleValue();

                score_pay = BigDecimal.valueOf(score_money_pay).multiply(BigDecimal.valueOf(200)).longValue();
            }
            etIntegral.setText(subZeroAndDot(score_pay + ""));
            tvSurplusIntegral.setText(BigDecimal.valueOf(score_balance).subtract(BigDecimal.valueOf(score_pay)).doubleValue() + "");
            pursecalculate();
            if (canBalanceEdite) {
                useBalance.setEnabled(true);
            }
        }
    }

    private void pursecalculate() {
        need_purse = old_price.subtract(BigDecimal.valueOf(red)).subtract(BigDecimal.valueOf(score_money_pay)).doubleValue();
        if (need_purse < 0) {
            need_purse = 0;
        }
        //钱包
        if (purse_balance >= need_purse) {
            //钱包金额足够
            purse_pay = need_purse;
            useBalance.setText(purse_pay + "");
            payPrice.setText("0");
            needPay = 0;
        } else {
            //钱包金额不足
            purse_pay = BigDecimal.valueOf(purse_balance).setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
//                useBalance.setText(purse_pay + "元");
            useBalance.setText(subZeroAndDot(purse_pay + ""));
            //微信、支付宝
            needPay = old_price.subtract(BigDecimal.valueOf(red)).subtract(BigDecimal.valueOf(score_money_pay)).subtract(BigDecimal.valueOf(purse_pay)).doubleValue();
//                payPrice.setText(needPay + "元");
            payPrice.setText(needPay + "");
        }
    }

    private void balancecalculate() {
//        private double score_money_pay;//积分抵用金额
//        private double need_purse; //需要用余额支付的金额
//        private double purse_pay; //余额抵用金额
//        private double needPay;
        if (!TextUtils.isEmpty(oldPrice.getText().toString().trim())) {
            old_price = new BigDecimal(oldPrice.getText().toString().trim());
        } else {
            old_price = new BigDecimal("0");
        }
        if (!TextUtils.isEmpty(integral2Money.getText().toString().trim())) {
            integral = new BigDecimal(integral2Money.getText().toString().trim());
        } else {
            integral = new BigDecimal("0");
        }

        String s = useBalance.getText().toString().trim();
        if (!TextUtils.isEmpty(s)) {
            if (".".equals(s.substring(0, 1))) {
                if (s.length() > 3) {
                    s = s.substring(1, s.length()).toString();
                    useBalance.setText(s);
                    useBalance.addTextChangedListener(userBalanceWatcher);
                    return;
                } else {
                    s = "0" + s;
                    useBalance.setText(s);
                    useBalance.setSelection(2);
                }
            }
            if (s.length() > 1 && "0".equals(s.substring(0, 1)) && !".".equals(s.substring(1, 2))) {
                s = s.substring(1, s.length()).toString();
                useBalance.setText(s);
                useBalance.addTextChangedListener(userBalanceWatcher);
                return;
            }
            if (s.contains(".") && 2 < s.substring(s.indexOf(".") + 1, s.length()).length()) {
                selectionStart = useBalance.getSelectionStart();
                if (s.length() != selectionStart) {
                    s = s.replace(".", "");
                    useBalance.setText(s);
                    useBalance.setSelection(selectionStart - 1);
                } else {
                    s = s.substring(0, s.length() - 1);
                    useBalance.setText(s);
                    useBalance.setSelection(s.length());
                }
                useBalance.addTextChangedListener(userBalanceWatcher);
                return;
            }
            purse_pay = new BigDecimal(s).doubleValue();
        } else {
            purse_pay = new BigDecimal("0").doubleValue();
        }
        need_purse = old_price.subtract(BigDecimal.valueOf(red)).subtract(BigDecimal.valueOf(score_money_pay)).doubleValue();
        if (need_purse < 0) {
            need_purse = 0;
        }
        //钱包
        if (purse_balance >= need_purse) {
            if (purse_pay > need_purse) {
                //钱包金额足够
                purse_pay = need_purse;
                useBalance.setText(subZeroAndDot(purse_pay + ""));
            }
            needPay = old_price.subtract(BigDecimal.valueOf(red)).subtract(BigDecimal.valueOf(score_money_pay)).subtract(BigDecimal.valueOf(purse_pay)).doubleValue();
//                payPrice.setText(needPay + "元");
            payPrice.setText(subZeroAndDot(needPay + ""));


        } else {
            //钱包余额不足以支付
            if (purse_pay > purse_balance) {
                //输入大于钱包余额
                purse_pay = BigDecimal.valueOf(purse_balance).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                useBalance.setText(subZeroAndDot(purse_pay + ""));
            }

            //微信、支付宝
            needPay = old_price.subtract(BigDecimal.valueOf(red)).subtract(BigDecimal.valueOf(score_money_pay)).subtract(BigDecimal.valueOf(purse_pay)).doubleValue();
//                payPrice.setText(needPay + "元");
            payPrice.setText(needPay + "");
        }
    }

    private void getRed() {
        float v1 = 0;
        ListViewDialog listViewDialog = new ListViewDialog(ShopPayActivity_.this, Gravity.BOTTOM, true);
        redList.clear();
        redUseAbleList.clear();
        redSelectedListId.clear();
        String etOldprice = oldPrice.getText().toString().trim();
        if (!TextUtils.isEmpty(etOldprice)) {
            v1 = Float.parseFloat(etOldprice);
        }
        if (itemReds.size() != 0) {
            for (RedListToShopBean.DataBean itemsBean : itemReds) {
                float val = itemsBean.getACTIVE_VAL();//获取满的钱的条件对比
                if (v1 >= val) {
                    float val1 = itemsBean.getVAL();
                    redList.add(val1 + "元红包");//添加红包名
                    redUseAbleList.add(val1 + "");//添加红包钱
                    redSelectedListId.add(itemsBean.getID() + "");//添加红包id
                }
            }
            if (redUseAbleList.size() > 0) {
                for (int i = 0; i < redUseAbleList.size(); i++) {
                    if ((Float.parseFloat(red_price)) < (Float.parseFloat(redUseAbleList.get(i)))) {
                        red_price = redUseAbleList.get(i);
                        System.out.println(red_price);
                        redUseAble.setText(red_price + "元红包");
                        redSelectedPosition = i;
                    }
                }
            } else {
                redUseAble.setText("无");
            }
        } else {
            redUseAble.setText("无");
        }
    }

    @Override
    protected void initData() {
        AiSouAppInfoModel.getInstance().addOrderActivity(this);
    }

    @Override
    protected void requestNetData() {
        Map<String, String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        map.put("shop", shopid);
        if (TextUtils.isEmpty(shopid)) {
            ToastUtil.showLong("仅限于蚂蚁平台扫码使用");
            finish();
            return;
        }

        //获取红包，写死
        map.put("sort", "12");
        LongHttp(ApiConstants.SHOP_REDABLE, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("获取信息失败，点击关闭");
                flag = true;
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                //获取商家红包
                RedListToShopBean redListToShopBean = JsonUtil.parseJsonToBean(response, RedListToShopBean.class);
                itemReds = redListToShopBean.getData();

                ++x;
                if (x == 3) {
                    hintDialogUtil.dismissDialog();
                    x = 0;
                }
            }
        });

        //获取账户(钱包)余额  账户积分
        Map<String, String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID());
        LongHttp(ApiConstants.USERPRICEBAG, "", m, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("积分获取失败" + e);
                hintDialogUtil.showError("获取信息失败，请检查网络");
                flag = true;
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                try {
                    BalanceBean balanceBean = JsonUtil.parseJsonToBean(response, BalanceBean.class);
                    BalanceBean.DataBean data = balanceBean.getData();
                    //账户余额
                    purse_balance = data.getPURSE_BALANCE();
                    pursebalance.setText(purse_balance + "元");
                    //账户积分
                    score_balance = data.getSCORE_BALANCE();
                    tvSurplusIntegral.setText(score_balance + "");
                    if (0 == purse_balance) {
                        canBalanceEdite = false;
                    } else {
                        canBalanceEdite = true;
                    }
                    if (score_balance < 2) {
                        canScoreEdite = false;
                    } else {
                        canScoreEdite = true;
                    }

                    ++x;
                    if (x == 3) {
                        hintDialogUtil.dismissDialog();
                        x = 0;
                    }
                } catch (Exception e) {
                    ToastUtil.showShort(e.getMessage() + "");
                    hintDialogUtil.dismissDialog();
                    x = 0;
                }
            }
        });

        //获取商家信息
        Map<String, String> shopNameMap = new HashMap<>();
        shopNameMap.put("id", shopid);
        LongHttp(ApiConstants.GET_SHOPINFOR, "", shopNameMap, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("获取信息失败，请检查网络");
                flag = true;
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                DataRow dataRow = DataRow.parseJson(response);
                String shopName = dataRow.getRow("data").getString("SHOP_NAME");
                receiveParty.setText(shopName);

                try {
                    ++x;
                    if (x == 3) {
                        hintDialogUtil.dismissDialog();
                        x = 0;
                    }
                } catch (Exception e) {
                    ToastUtil.showShort(e.getMessage() + "");
                    hintDialogUtil.dismissDialog();
                    x = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.rl_useablered_paydetail:
                float v1 = 0;
                ListViewDialog listViewDialog = new ListViewDialog(ShopPayActivity_.this, Gravity.BOTTOM, true);
                redList.clear();
                redSelectedListId.clear();
                String etOldprice = oldPrice.getText().toString().trim();
                if (!TextUtils.isEmpty(etOldprice)) {
                    v1 = Float.parseFloat(etOldprice);
                }
                if (itemReds.size() != 0) {
                    for (RedListToShopBean.DataBean itemsBean : itemReds) {
                        float val = itemsBean.getACTIVE_VAL();//获取满的钱的条件对比
                        if (v1 >= val) {
                            float val1 = itemsBean.getVAL();
                            redList.add(val1 + "元红包");//添加红包名
                            redUseAbleList.add(val1 + "");//添加红包钱
                            redSelectedListId.add(itemsBean.getID() + "");//添加红包id
                        }
                    }
                    listViewDialog.setInitData(redList, "取消");
                } else {
                    listViewDialog.setInitData(redList, "暂无该商家可用红包");
                }
                listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
                listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position, View v) {
                        red_price = redUseAbleList.get(position);
                        System.out.println(red_price);
                        redUseAble.setText(red_price + "元红包");
                        redSelectedPosition = position;
                        calculate();
                    }
                });
                listViewDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.btn_entery_shoppay:
                String trim = oldPrice.getText().toString().trim();

                if ("".equals(trim)) {
                    ToastUtil.showLong("未输入支付金额");
                    break;
                } else {
                    BigDecimal bigDecimal = new BigDecimal(trim);
                    if (0 == bigDecimal.doubleValue()) {
                        ToastUtil.showLong("输入支付金额为零");
                        break;
                    }
                }
//获取支付密码状态  0：未设置
                PayHttpUtils.getPayPasswordState(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showLong("获取支付密码状态失败:" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d(response);
                        DataRow dataRow = DataRow.parseJson(response);
                        String data = (String) dataRow.get("data");
                        LogUtils.d(data);
                        if (data.equals("0")) {
                            //没有支付密码，去设置
                            SetPasswordDialog setPasswordDialog = new SetPasswordDialog(ShopPayActivity_.this, Gravity.CENTER, false);
                            setPasswordDialog.showDialog(true, true);
                            return;
                        } else {
                            //输入平台的支付密码
                            passViewDialog.clearn();
                            passViewDialog.setPrice("¥" + oldPrice.getText().toString().trim());
                            if (("".equals(integral2Money.getText().toString().trim()) || "0".equals(subZeroAndDot(integral2Money.getText().toString().trim()))) && ("".equals(useBalance.getText().toString().trim()) || "0".equals(subZeroAndDot(useBalance.getText().toString().trim())))) {
                                getOrder();
                            } else {
                                passViewDialog.showDialog(true, true);
                            }
                            RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                            RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
                        }
                    }
                });
                break;
        }
    }

    public void getOrder() {
        String redid;
        if (redSelectedPosition != -1) {
            redid = redSelectedListId.get(redSelectedPosition);
        } else {
            redid = "";
        }
        String url = ApiConstants.SHOP_PAY;
        String url_shoppay = String.format(url, shopid, AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), redid, etIntegral.getText().toString().trim(), useBalance.getText().toString().trim(), oldPrice.getText().toString().trim(), "2");
        LogUtils.d(url_shoppay);
        LongHttpGet(url_shoppay, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("连接失败" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                System.out.println(response);
                ShopPayBean shopPayBean = GsonUtil.gsonToBean(response, ShopPayBean.class);
                ShopPayBean.DataBean data = shopPayBean.getData();
                double cash_price = data.getCASH_PRICE();
                /*
                * 0 系统内支付，其他微信、支付宝支付
                * */
                if ("0.0".equals(cash_price + "") || "0".equals(cash_price + "")) {
                    ShopPaySuccessActivtity.startActivity(ShopPayActivity_.this, oldPrice.getText().toString().trim(), receiveParty.getText().toString(), "平台内支付");
                } else {
                    passViewDialog.dismiss();
                    int dataID = data.getID();
                    PayDialog payDialog = new PayDialog(ShopPayActivity_.this, Gravity.BOTTOM, true, dataID + "");
                    payDialog.setNeedPayMoney(needPay + "");
                    payDialog.showDialog(true, true);
                }
                RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instanceShopActivity != null) {
            instanceShopActivity = null;
        }
    }

    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
