package net.twoant.master.ui.charge.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
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
import net.twoant.master.widget.LastInputEditText;
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
 * Created by DZY on 2016/12/24.
 * 佛祖保佑   永无BUG
 */
public class ShopPayActivity extends LongBaseActivity implements View.OnClickListener {
    public static ShopPayActivity instanceShopActivity = null;
    private List<String> redList;
    private List<String> redUseAbleList;
    private static float total_price;
    private TextView receiveParty,payPrice, redUseAble,tvSurplusIntegral, tvBanlance;
    private EditText useBalance,oldPrice,integral2Money;
    private double score_balance;
    private int x = 0;//记录两个网络请求是否都请求完毕
    private HintDialogUtil hintDialogUtil;
    public String shopid;//支付给对方店家的shopid
    public List<RedListToShopBean.DataBean> itemReds;
    private int redSelectedPosition = -1;
    private List<String> redSelectedListId = new ArrayList<>();
    private LastInputEditText etIntegral;
    public String red_price;
    public double integral2f;//抵用积分的钱
    public BigDecimal needPay;

    float red = 0;//红包抵用钱

    private PassViewDialog passViewDialog;
    private ErrorPayPasswordDialog errorPayPasswordDialog;
    public double purse_balance;

    private boolean flag = false;
    public double score2Money;

    private boolean edittextFlag = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PayDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_shoppay;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        instanceShopActivity = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        String shopidAES = getIntent().getStringExtra("result");
        shopid = AESHelper.decrypt(shopidAES, "aisou");

        receiveParty = (TextView) findViewById(net.twoant.master.R.id.tv_receiveparty_paydetail);
        oldPrice = (EditText) findViewById(net.twoant.master.R.id.et_oldprice_paydetail);
        payPrice = (TextView) findViewById(net.twoant.master.R.id.tv_payprice_paydetail);
        etIntegral = (LastInputEditText) findViewById(net.twoant.master.R.id.et_integral_shoppay);
        redUseAble = (TextView) findViewById(net.twoant.master.R.id.tv_red_useable_paydetail);
        integral2Money = (EditText) findViewById(net.twoant.master.R.id.tv_integral_to_money_paydetail);
        tvSurplusIntegral = (TextView) findViewById(net.twoant.master.R.id.tv_surplus_paydetail);
        useBalance = (EditText) findViewById(net.twoant.master.R.id.bb);
        tvBanlance = (TextView) findViewById(net.twoant.master.R.id.cc);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.rl_useablered_paydetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_entery_shoppay).setOnClickListener(this);
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
        integral2Money.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edittextFlag = false;
                return false;
            }
        });
        etIntegral.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edittextFlag = true;
                return false;
            }
        });
        //积分钱数变化时
        integral2Money.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                double i = 0;
                integral2Money.removeTextChangedListener(this);
                if (TextUtils.isEmpty(s + "")) {
                    integral2Money.setText("");
                    etIntegral.setText("");
                    tvSurplusIntegral.setText(score_balance+"");
                } else {
                    try {
                        i = Double.parseDouble("" + s);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (edittextFlag) {
                    }else{
                        etIntegral.setText((int)(i*200)+"");
                    }
                }
                if (i > score2Money) {
                    ToastUtil.showLong("超出最大可抵用金额");
                    BigDecimal bigDecimal = new BigDecimal(score2Money);
                    double v = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    integral2Money.setText("" + v);
                    etIntegral.setText((int) score_balance+"");
                }
                integral2Money.addTextChangedListener(this);
                calculate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //输入钱包余额变化时
        useBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //设置useBalance只能输入到小数点后两位
        useBalance.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if (mlength == 3) {
                        return "";
                    }
                }
                return null;
            }
        }});
        //输入总金额变化时
        oldPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                red = 0;
                red_price = "0";
                redUseAble.setText("");
                redSelectedPosition = -1;
                calculate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //输入积分变化时
        etIntegral.addTextChangedListener(new IngegralEdit());

        errorPayPasswordDialog = new ErrorPayPasswordDialog(ShopPayActivity.this,Gravity.CENTER);
        /**
         * 自定义输入密码框输密码的回调
         * */
        passViewDialog = new PassViewDialog(ShopPayActivity.this, Gravity.BOTTOM, true);
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(), strPassword,new StringCallback() {
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
                            getOrder();
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
    protected void initData() {
        BigDecimal bigDecimal = new BigDecimal(total_price );
        double integral2f = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (integral2f<=0) {
            integral2f = 0;
        }
        payPrice.setText(integral2f + "元");  //除去余额后，最终还需要支付的价钱
        AiSouAppInfoModel.getInstance().addOrderActivity(this);

        //在多个EditText中将光标直接指定某个EditText
        oldPrice.setFocusable(true);
        oldPrice.requestFocus();
        oldPrice.setFocusableInTouchMode(true);
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
        map.put("sort", "12");//获取红包，写死
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
                    score_balance = data.getSCORE_BALANCE();//账户积分
                    score2Money = score_balance / 200;
                    //默认用全部积分
//                    etIntegral.setText(score_balance + "");
//                    setIntegralMoney(score_balance + "");
                    tvSurplusIntegral.setText(score_balance+"");
                    tvBanlance.setText(purse_balance + "");
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

    public void getOrder(){
        String redid;
        if (redSelectedPosition!=-1) {
            redid = redSelectedListId.get(redSelectedPosition);
        }else{
            redid="";
        }
        String url = ApiConstants.SHOP_PAY;
        String url_shoppay = String.format(url,shopid, AiSouAppInfoModel.getInstance().getAiSouUserBean().getAiSouID(),redid,etIntegral.getText().toString().trim(),useBalance.getText().toString().trim(),oldPrice.getText().toString().trim(),"2");
        LogUtils.d(url_shoppay);
        LongHttpGet(url_shoppay, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                ToastUtil.showLong("连接失败"+e);
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
                if ("0.0".equals(cash_price+"") || "0".equals(cash_price+"")) {
                    ShopPaySuccessActivtity.startActivity(ShopPayActivity.this,oldPrice.getText().toString().trim(),receiveParty.getText().toString(),"平台内支付");
                  }else{
                    passViewDialog.dismiss();
                    int dataID = data.getID();
                    PayDialog payDialog = new PayDialog(ShopPayActivity.this,Gravity.BOTTOM,true,dataID+"");
                    payDialog.setNeedPayMoney(needPay.toString());
                    payDialog.showDialog(true,true);
                }
                RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
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
                ListViewDialog listViewDialog = new ListViewDialog(ShopPayActivity.this, Gravity.BOTTOM, true);
                redList.clear();
                redSelectedListId.clear();
                String etOldprice = oldPrice.getText().toString().trim();
                if (!TextUtils.isEmpty(etOldprice)) {
                    v1 = Float.parseFloat(etOldprice);
                }
                if (itemReds.size() != 0) {
                    for (RedListToShopBean.DataBean itemsBean:itemReds){
                        float val = itemsBean.getACTIVE_VAL();//获取满的钱的条件对比
                        if (v1>=val) {
                            float val1 = itemsBean.getVAL();
                            redList.add(val1+"元红包");//添加红包名
                            redUseAbleList.add(val1+"");//添加红包钱
                            redSelectedListId.add(itemsBean.getID()+"");//添加红包id
                        }
                    }
                    listViewDialog.setInitData(redList, "取消");
                }else{
                    listViewDialog.setInitData(redList, "暂无该商家可用红包");
                }
                listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
                listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position, View v) {
                        red_price = redUseAbleList.get(position);
                        System.out.println(red_price);
                        redUseAble.setText(red_price +"元红包");
                        redSelectedPosition = position;
                        calculate();
                    }
                });
                listViewDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.btn_entery_shoppay:
                if (TextUtils.isEmpty(oldPrice.getText().toString().trim())) {
                    ToastUtil.showLong("支付钱数为空");
                    return;
                }
                String trim = useBalance.getText().toString().trim();
                try {
                    if (TextUtils.isEmpty(trim)) {
                        trim = "0";
                    }
                    if (purse_balance < Float.parseFloat(trim)) {
                        ToastUtil.showLong("输入余额超出");
                        return;
                    }
                } catch (NumberFormatException e) {
                    ToastUtil.showLong("输入余额有误");
                    return;
                }
                if (needPay.intValue() < 0) {
                    ToastUtil.showLong("所抵用钱大于所支付的钱，请重新输入");
                    return;
                }
                String useIntegral = etIntegral.getText().toString().trim();
                if (TextUtils.isEmpty(useIntegral) | "0".equals(useIntegral) && "0".equals(trim)) {
                    getOrder();
                    return;
                }
                //获取支付密码状态  0：未设置
                PayHttpUtils.getPayPasswordState(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showLong("获取支付密码状态失败:"+e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d(response);
                        DataRow dataRow = DataRow.parseJson(response);
                        String data = (String) dataRow.get("data");
                        LogUtils.d(data);
                        if (data.equals("0")) {
                            //没有支付密码，去设置
                            SetPasswordDialog setPasswordDialog = new SetPasswordDialog(ShopPayActivity.this,Gravity.CENTER,false);
                            setPasswordDialog.showDialog(true,true);
                            return;
                        }else{
                            //输入平台的支付密码
                            passViewDialog.clearn();
                            passViewDialog.setPrice("¥"+oldPrice.getText().toString().trim());
                            passViewDialog.showDialog(true,true);
                            RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                            RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
                        }
                    }
                });
                break;
        }
    }

    private void calculate(){

        BigDecimal integral = null;//积分抵用的钱
        BigDecimal yue = null;//余额抵用的钱
        BigDecimal old_price = null;//原所需支付的钱
        if (!TextUtils.isEmpty(red_price)) {
            red = Float.parseFloat(red_price);
        }
        if (!TextUtils.isEmpty(oldPrice.getText().toString().trim())) {
            old_price = new BigDecimal(oldPrice.getText().toString().trim());
        }else{
            old_price =  new BigDecimal("0");
        }
        if (!TextUtils.isEmpty(integral2Money.getText().toString().trim())) {
            String trim = integral2Money.getText().toString().trim();
            System.out.println(trim);
            integral = new BigDecimal(integral2Money.getText().toString().trim());
        }else{
            integral = new BigDecimal("0");
        }
        if (!TextUtils.isEmpty(useBalance.getText().toString().trim())) {
            yue = new BigDecimal(useBalance.getText().toString().trim());
        }else{
            yue = new BigDecimal("0");
        }
        needPay = old_price.subtract(BigDecimal.valueOf(red)).subtract(integral).subtract(yue);
        if (needPay.doubleValue() <= 0 ) {
            payPrice.setText("0元");
        }else {
            payPrice.setText(needPay +"元");
        }
    }

    /**
     * 按比例算出积分抵用的钱
     * 传入积分
     */
    private void setIntegralMoney(String i) {
        double v = Double.parseDouble(i);
        v = v / 200;  //按比例算取可抵用人民币
        BigDecimal bigDecimal = new BigDecimal(v);
        String s = bigDecimal.setScale(2, BigDecimal.ROUND_FLOOR).toString();
        if (edittextFlag) {
            integral2Money.setText(s);
        }
        double v1 = score_balance - Double.parseDouble(i);
        if (v1 <= 0) {
            tvSurplusIntegral.setText("0");
        }else{
            BigDecimal b = new BigDecimal(v1);
            double f1  = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            tvSurplusIntegral.setText(String.valueOf(f1));
        }
    }

    class IngegralEdit implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int i = 0;
            if (TextUtils.isEmpty(s + "")) {
                etIntegral.removeTextChangedListener(this);
                etIntegral.setText("");
                tvSurplusIntegral.setText(score_balance+"");
                integral2Money.setText("");
                etIntegral.addTextChangedListener(this);
            } else {
                i = Integer.parseInt("" + s);
                setIntegralMoney(i + "");
            }
            if (i > score_balance) {
                ToastUtil.showLong("超出最大积分");
                etIntegral.setText((int)score_balance + "");
                setIntegralMoney(score_balance + "");
            }
            calculate();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instanceShopActivity != null) {
            instanceShopActivity = null;
        }
    }
}