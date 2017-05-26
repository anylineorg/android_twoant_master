package net.twoant.master.ui.my_center.activity.out;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.JsonUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.activity.ShopPaySuccessActivtity;
import net.twoant.master.ui.charge.bean.ShopPayBean;
import net.twoant.master.ui.main.bean.GoodsItemBean;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/6.
 */
public class PayDetailActivity extends LongBaseActivity implements View.OnClickListener{
    private static int sort;
    private static String extra;
    private List<String> redList;
    private static float total_price;
    private static String shop_id;
    private static String receive_party;
    private TextView receiveParty,oldPrice,payPrice,redUseAble,tvSurplusIntegral,tvBanlance,
         etAddressName,etTel,etAddress;
    private EditText etIntegral,useBalance,integral2Money;
    private int score_balance;//个人积分剩余
    private int x = 0;//记录两个网络请求是否都请求完毕
    private HintDialogUtil hintDialogUtil;
    public List<RedListToShopBean.DataBean> itemsReds;
    public ListViewDialog listViewDialog;
    private List<String> redUseAbleList;
    private List<String> redSelectedListId = new ArrayList<>();
    public String red_price;//存 选取的红包的钱
    private int redSelectedPosition = -1;
    float red = 0;//红包抵用钱
    public float needPay;
    public double purse_balance;//个人账户余额
    private List<GoodsItemBean> selectedList;
    private PassViewDialog passViewDialog;
    private LinearLayout llAddress;
    public DataRow data;
    public String addressName;
    public String addressTel;
    public String addresssAddress;
    private boolean flag = false;
    public double score2price;
    private boolean edittextFlag = false;
    private ErrorPayPasswordDialog errorPayPasswordDialog;

    public static void startActivity(Context context, float total_price,String receive_party,String shopid,int sort,String extraStr) {
        Intent intent = new Intent(context, PayDetailActivity.class);
        context.startActivity(intent);
        extra = extraStr;
        PayDetailActivity.total_price = total_price;
        PayDetailActivity.receive_party = receive_party;
        PayDetailActivity.shop_id = shopid;
        PayDetailActivity.sort = sort;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_paydetail;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        receiveParty = (TextView) findViewById(R.id.tv_receiveparty_paydetail);
        oldPrice = (TextView) findViewById(R.id.tv_oldprice_paydetail);
        payPrice = (TextView) findViewById(R.id.tv_payprice_paydetail);
        etIntegral = (EditText) findViewById(R.id.et_integral_paydetail);
        etAddressName = (TextView) findViewById(R.id.tv_name_paydetail);
        etAddress = (TextView) findViewById(R.id.tv_address_paydetail);
        etTel = (TextView) findViewById(R.id.tv_tel_paydetail);
        etIntegral.addTextChangedListener(new IngegralEdit());
        errorPayPasswordDialog = new ErrorPayPasswordDialog(this,Gravity.CENTER);
        redUseAble = (TextView) findViewById(R.id.tv_red_useable_paydetail);
        integral2Money = (EditText) findViewById(R.id.tv_integral_to_money_paydetail);

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
                if (i > score2price) {
                    ToastUtil.showLong("超出最大可抵用金额");
                    BigDecimal bigDecimal = new BigDecimal(score2price);
                    double v = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    integral2Money.setText("" + v);
                    etIntegral.setText((int) score_balance+"");
                }
                integral2Money.addTextChangedListener(this);
                calculatePrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvSurplusIntegral = (TextView) findViewById(R.id.tv_surplus_paydetail);
        useBalance = (EditText) findViewById(R.id.bb);
        tvBanlance = (TextView) findViewById(R.id.cc);
        llAddress = (LinearLayout) findViewById(R.id.ll_address_paydetail);
        llAddress.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.rl_useablered_paydetail).setOnClickListener(this);
        findViewById(R.id.btn_entery_paydetail).setOnClickListener(this);
        redList = new ArrayList();
        hintDialogUtil = new HintDialogUtil(this);
        hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (flag) {
                    finish();
                }
            }
        });
        hintDialogUtil.showLoading(R.string.get_order);
        //设置useBalance只能输入到小数点后两位
        useBalance.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(".") && dest.toString().length() == 0){
                    return "0.";
                }
                if(dest.toString().contains(".")){
                    int index = dest.toString().indexOf(".");
                    int mlength = dest.toString().substring(index).length();
                    if(mlength == 3){
                        return "";
                    }
                }
                return null;
            }
        }});
        useBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
         * 自定义输入密码框输密码的回调
         * */
        passViewDialog = new PassViewDialog(PayDetailActivity.this, Gravity.BOTTOM, true);
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                hintDialogUtil.showLoading();
                PayHttpUtils.isPassword(AiSouAppInfoModel.getInstance().getUID(), strPassword, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hintDialogUtil.dismissDialog();
                        ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = (boolean) dataRow.get("success");
                        if (success) {
                            hintDialogUtil.showLoading(R.string.use_activity);
                            String redid;
                            if (redSelectedPosition!=-1) {
                                redid = redSelectedListId.get(redSelectedPosition);
                            }else{
                                redid="";
                            }
                            //密码验证成功，获取订单号
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

        passViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                passViewDialog.clearn();
            }
        });

        selectedList = PostOrderActivity.selectedList;
        //调试时放开
       /* StringBuilder stringBuilder = new StringBuilder();
        for (GoodsItem item:selectedList){
            stringBuilder.append(item.toString()+"\r\n");
        }
        LogUtils.d(stringBuilder+"");*/
        AiSouAppInfoModel.getInstance().addOrderActivity(this);
    }

    @Override
    protected void initData() {
        receiveParty.setText(receive_party);
        oldPrice.setText(total_price+"元");
        redUseAbleList = new ArrayList();
        calculatePrice();
        /**
         * 根据配送类型 显示隐藏收货地址
         * */
        switch(sort){
            case 1://店内消费
                llAddress.setVisibility(View.GONE);
                break;
            case 2://商家配送
                getAddress();
                break;
            case 3://平台配送
                getAddress();
                break;
            case 4://买家自取配送
                llAddress.setVisibility(View.GONE);
                break;
        }
    }

    private void getAddress(){
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.GET_DEFAULT_ADDRESS, "", addressMap, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("获取信息失败，请检查网络");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                DataRow dataRow = DataRow.parseJson(response);
                data = dataRow.getRow("data");
                String message = dataRow.getString("message");
                if (message.contains("没有返回数据")) {
                }else{
                    addressName = data.getString("RECEIPT_NAME");
                    etAddressName.setText(addressName);
                    addressTel = data.getString("RECEIPT_TEL");
                    etTel.setText(addressTel);
                    addresssAddress = data.getString("RECEIPT_ADDRESS");
                    etAddress.setText("收货地址："+ addresssAddress);
                }
            }
        });
    }

    @Override
    protected void requestNetData() {
        Map<String,String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("shop", shop_id);
        map.put("sort", "12");//获取红包，写死
        LongHttp(ApiConstants.SHOP_REDABLE,"",map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("获取信息失败，请检查网络");
                flag = true;
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                RedListToShopBean redListToShopBean = JsonUtil.parseJsonToBean(response, RedListToShopBean.class);
                //获取该商家可用红包
                 itemsReds = redListToShopBean.getData();

                ++x;
                if (x==2){
                    hintDialogUtil.dismissDialog();
                    x=0;
                }
            }
        });
        //获取个人账户余额
        Map<String,String> m = new HashMap<>();
        m.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.USERPRICEBAG,"",m, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("积分获取失败");
                hintDialogUtil.showError("获取信息失败，请检查网络");
                flag = true;
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                try{
                    BalanceBean balanceBean = JsonUtil.parseJsonToBean(response, BalanceBean.class);
                    BalanceBean.DataBean data = balanceBean.getData();
                    //账户余额
                    purse_balance = data.getPURSE_BALANCE();
                    double score_balance_temp = data.getSCORE_BALANCE();
                    score_balance = (int) score_balance_temp;//账户积分
                    score2price = score_balance_temp / 200;
//                    score2price = score_balance / 200;
                    setIntegralMoney("0");

                    tvBanlance.setText(purse_balance+"");

                    ++x;
                    if (x==2){
                        hintDialogUtil.dismissDialog();
                        x=0;
                    }
                }catch (Exception e){
                    hintDialogUtil.dismissDialog();
                    x=0;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_address_paydetail:
                startActivity(new Intent(PayDetailActivity.this,AddressManageActivity.class));
                break;
            case R.id.rl_useablered_paydetail:
                listViewDialog = new ListViewDialog(PayDetailActivity.this, Gravity.BOTTOM, true);
                redList.clear();
                for (RedListToShopBean.DataBean itemsBean : itemsReds){
                    float active_val = itemsBean.getACTIVE_VAL();
                    if (total_price >= active_val) {//获取满的钱的条件对比
                        float val = itemsBean.getBALANCE();
                        redList.add(val+"元红包");//添加红包名
                        redUseAbleList.add(val+"");//添加红包可用钱
                        redSelectedListId.add(itemsBean.getID()+"");//添加红包id
                    }
                }
                if (redList.size()==0) {
                    listViewDialog.setInitData(redList,"暂无该商家可用红包");
                }else{
                    listViewDialog.setInitData(redList,"取消");
                }
                listViewDialog.setTextColor(R.color.principalTitleTextColor);
                listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position, View v) {
                        redUseAble.setText(redList.get(position));
                        red_price = redUseAbleList.get(position);
                        redUseAble.setText(red_price +"元红包");
                        redSelectedPosition = position;
                        calculatePrice();
                    }
                });
                listViewDialog.showDialog(true,true);
                break;
            case R.id.btn_entery_paydetail:
                String inputYue = useBalance.getText().toString().trim();
                if (!TextUtils.isEmpty(inputYue)) {
                    if (purse_balance < Float.parseFloat(inputYue) ){
                        ToastUtil.showLong("输入余额超出最大");
                        return;
                    }
                }
                if (needPay < 0) {
                    ToastUtil.showLong("所抵用钱大于所支付的钱，请重新输入");
                    return;
                }
                String inputIntegral = etIntegral.getText().toString().trim();
                String inputBalance = useBalance.getText().toString().trim();
                if (TextUtils.isEmpty(inputIntegral) | "0".equals(inputIntegral) && TextUtils.isEmpty(inputBalance) | "0".equals(inputBalance)) {
                    //获取订单号
                    getOrder();
                    return;
                }

                //获取支付密码状态  0：未设置
                PayHttpUtils.getPayPasswordState(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showLong("获取支付密码状态失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.d(response);
                        DataRow dataRow = DataRow.parseJson(response);
                        String data = (String) dataRow.get("data");
                        LogUtils.d(data);
                        if (data.equals("0")) {
                            //没有支付密码，去设置
                            SetPasswordDialog setPasswordDialog = new SetPasswordDialog(PayDetailActivity.this,Gravity.CENTER,false);
                            setPasswordDialog.showDialog(true,true);
                            return;
                        }else{
                            //获取订单号
//                            getOrder();
                            //输入平台的支付密码
                            passViewDialog.clearn();
                            passViewDialog.showDialog(true,true);
                            passViewDialog.setPrice("¥"+total_price);
                            RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                            RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
                        }
                    }
                });
                break;
        }
    }

   StringBuilder url_getOder;
    /*
    * 获取订单号
    * */
    private void getOrder() {
        url_getOder = new StringBuilder();
        String redid;
        hintDialogUtil.showLoading();
        if (redSelectedPosition==-1 ) {
            redid = "";
        }else{
            redid = redSelectedListId.get(redSelectedPosition);
        }
        String url = ApiConstants.GET_ORDDER_GET;
        String url_getOde = String.format(url, shop_id, AiSouAppInfoModel.getInstance().getUID(),redid,etIntegral.getText().toString().trim(),useBalance.getText().toString().trim());
        url_getOder.append(url_getOde);
        for (GoodsItemBean goodsItem : selectedList) {
            url_getOder.append("&goods="+goodsItem.getGoodsId());
            url_getOder.append("&qty="+goodsItem.getGoodsCount());
        }
        url_getOder.append("&sort="+"1");//死值，1为商品支付
        url_getOder.append("&ship_sort="+sort);
        if (!TextUtils.isEmpty(addressName))
            url_getOder.append("&receive_name="+addressName);
        if (!TextUtils.isEmpty(addressTel))
            url_getOder.append("&receive_tel="+addressTel);
        if (!TextUtils.isEmpty(addresssAddress))
            url_getOder.append("&receive_address="+addresssAddress);
        if (!TextUtils.isEmpty(extra))
            url_getOder.append("&des="+extra);
        System.out.println(url_getOder+"");
        LongHttpGet(url_getOder+"", new StringCallback() {
           @Override
           public void onError(Call call, Exception e, int id) {
               hintDialogUtil.dismissDialog();
               ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
           }

           @Override
           public void onResponse(String response, int id) {
               LogUtils.d(response);
               hintDialogUtil.dismissDialog();
               ShopPayBean shopPayBean = GsonUtil.gsonToBean(response, ShopPayBean.class);
               ShopPayBean.DataBean data = shopPayBean.getData();
               double cash_price = data.getCASH_PRICE();
               hintDialogUtil.dismissDialog();
                /*
                * 0 系统内支付，其他微信、支付宝支付
                * */
               if ("0.0".equals(String.valueOf(cash_price)) || "0".equals(String.valueOf(cash_price))) {
                   passViewDialog.dismiss();
                   ShopPaySuccessActivtity.startActivity(PayDetailActivity.this,
                           oldPrice.getText().toString().trim(),receiveParty.getText().toString(),"平台支付");
               }else{
                   passViewDialog.dismiss();
                   int dataID = data.getID();
                   PayDialog payDialog = new PayDialog(PayDetailActivity.this,Gravity.BOTTOM,true,dataID+"",true);
                   payDialog.setNeedPayMoney(needPay+"");
                   payDialog.showDialog(true,true);
                   RechargeSuccessActivtity.paymoney = oldPrice.getText().toString().trim();//给支付成功界面赋值
                   RechargeSuccessActivtity.payShopName = receiveParty.getText().toString().trim();//给支付成功界面赋值
               }
           }
        });
    }

    /**
     * 按比例算出积分抵用的钱
     * 传入积分
     * */
    private void setIntegralMoney (String i){
        float v = Float.parseFloat(i);
        v = v/200;  //按比例算取可抵用人民币
//        NumberFormat ddf1 = NumberFormat.getNumberInstance() ;
//        ddf1.setMaximumFractionDigits(2);
//        String s = ddf1.format(v) ;
        BigDecimal bigDecimal = new BigDecimal(v);
        String s = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_DOWN).toString();
        if (edittextFlag) {
            integral2Money.setText(s);
        }
        double v1 = score_balance - Double.parseDouble(i);
        if (v1 <= 0) {
            tvSurplusIntegral.setText("0");
        }else{
            tvSurplusIntegral.setText(String.valueOf(v1));
        }
    }

    class IngegralEdit implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int i = 0;
            if (TextUtils.isEmpty(s+"")){
                etIntegral.removeTextChangedListener(this);
                etIntegral.setText("");
                setIntegralMoney("0");
                etIntegral.addTextChangedListener(this);
            }else{
                i = Integer.parseInt("" + s);
                setIntegralMoney(i+"");
            }
            if (i>score_balance){
                ToastUtil.showLong("超出最大积分");
                etIntegral.setText(score_balance+"");
                setIntegralMoney(score_balance+"");
            }
            calculatePrice();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void calculatePrice() {
        float integral = 0;//积分抵用的钱
        float yue  = 0;//余额抵用的钱
        if (!TextUtils.isEmpty(red_price)) {
            red = Float.parseFloat(red_price);
        }
        if (!TextUtils.isEmpty(integral2Money.getText().toString().trim())) {
//            String trim = integral2Money.getText().toString().trim();
//            System.out.println(trim);
            try {
                integral =  Float.parseFloat(integral2Money.getText().toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                integral = 0;
            }
        }
        if (!TextUtils.isEmpty(useBalance.getText().toString().trim())) {
            yue = Float.parseFloat(useBalance.getText().toString().trim());
        }
        needPay = total_price - red - integral - yue;
        if (needPay < 0) {
            payPrice.setText("0元");
        }else {
            BigDecimal bigDecimal = new BigDecimal(needPay);
            needPay = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            payPrice.setText(needPay +"元");
        }
    }
}
