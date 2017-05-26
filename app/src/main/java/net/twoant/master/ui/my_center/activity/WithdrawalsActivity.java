package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.bean.BanckCardBean;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.SetPasswordDialog;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/22 0022.
 */
public class WithdrawalsActivity extends LongBaseActivity implements View.OnClickListener {

    private String shop_id;
    ImageView bank_img;
    TextView bank_name, money_bag, price_tip;
    double price;
    private DataRow mDataRow;
    EditText editText;
    Button submit;
    String pp;
    BanckCardBean.ResultBean bank_info;
    private int position = 0;  //默认提现到银行卡
    public ImageView circleBank;
    public ImageView circleYue;
    private HintDialogUtil hintDialogUtil;
    public TextView tvFiveDayGet;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.withdrawals_activity;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        //从上层获取shop_id
        shop_id = getIntent().getStringExtra("shop_id");
//        DataRow row = (DataRow) getIntent().getSerializableExtra("bank_info");
//        bank_info = new BanckCardBean.ResultBean();
//        bank_info.setBankcard(row.getString("BANKCARD"));
//        bank_info.setBankname(row.getString("BANKNAME"));
//        bank_info.setCardtype(row.getString("CARDTYPE"));
//        bank_info.setLogourl(row.getString("LOGOUR"));
        BanckCardBean bank_info1 = (BanckCardBean) getIntent().getSerializableExtra("bank_info");
        try {
            bank_info = bank_info1.getResult().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(net.twoant.master.R.id.rl_bank_widthdraw).setOnClickListener(this);
        RelativeLayout rlBanlance = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_yue_withdraw);
        rlBanlance.setOnClickListener(this);
        getPayPasswordState();
        TextView title = (TextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar);
        title.setText("提现");
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        tvFiveDayGet = (TextView) findViewById(net.twoant.master.R.id.tv_fiveday_get);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawalsActivity.this.finish();
            }
        });
        String balance_getmoney = getIntent().getStringExtra("balance_getmoney");
        if ("true".equals(balance_getmoney)) {
            rlBanlance.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.rl_bank_widthdraw:
                position = 0;
                circleBank.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_withdrawable_true));
                circleYue.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_withdrawable_false));
                editText.setHint("单笔提现不能超过50000.00，手续费2.00");
                tvFiveDayGet.setVisibility(View.VISIBLE);
                break;
            case net.twoant.master.R.id.rl_yue_withdraw:
                position = 1;
                circleBank.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_withdrawable_false));
                circleYue.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_withdrawable_true));
                editText.setHint("提现至钱包不限额,即时到账");
                tvFiveDayGet.setVisibility(View.INVISIBLE);
                break;
        }
    }

    void initSource() {
        String card = bank_info.getBankcard();//mDataRow.getRow("data").getString("SHOP_OWNER_BANKCARD_NO");
        ImageLoader.getImageFromNetwork(bank_img,BaseConfig.getCorrectImageUrl(bank_info.getLogourl()),this, net.twoant.master.R.drawable.ic_info);
        String end = bank_info.getBankcard();
        end = end.substring(end.length() - 4, end.length());
        bank_name.setText(bank_info.getBankname() + "(" + end + ")");
    }

    HashMap<String, String> map;

    @Override
    protected void initData() {
        map = new HashMap<>();
        if (bank_info != null) {
            pp = getIntent().getStringExtra("accont_price");
            price_tip.setText("当前财务账上余额:" + pp);
            initSource();
            return;
        }

        pp = getIntent().getStringExtra("accont_price");
        price_tip.setText("当前钱包余额:" + pp);
        map.put("shop", shop_id);
        LongHttp(ApiConstants.SHOPBANKINFO, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if (DataRow.parseJson(response).getBoolean("result", false)) {
                    mDataRow = DataRow.parseJson(response);
                    if (mDataRow != null) {
                        ImageLoader.getImageFromNetwork(bank_img, BaseConfig.getCorrectImageUrl( mDataRow.getRow("data").getString("BANK_LOGO")),WithdrawalsActivity.this, net.twoant.master.R.drawable.ic_info);
                        String end = mDataRow.getRow("data").getString("SHOP_OWNER_BANKCARD_NO");
                        end = end.substring(end.length() - 4, end.length());
                        bank_name.setText(mDataRow.getRow("data").getString("SHOP_OWNER_BANK") + "(" + end + ")");
                        // price_tip.setText("当前钱包余额:"+mDataRow.get(0).getString("PURSE_BALANCE"));
                    }
                } else {
                    ToastUtil.showShort(DataRow.parseJson(response).getString("message"));
                }
            }
        });
    }

    PassViewDialog mAlertDialog;

    void initView() {
        hintDialogUtil = new HintDialogUtil(this);
        bank_img = (ImageView) findViewById(net.twoant.master.R.id.bank_icon);
        bank_name = (TextView) findViewById(net.twoant.master.R.id.bank_nme);
        money_bag = (TextView) findViewById(net.twoant.master.R.id.tx_all);
        price_tip = (TextView) findViewById(net.twoant.master.R.id.price_tip);
        submit = (Button) findViewById(net.twoant.master.R.id.tx_submit);
        editText = (EditText) findViewById(net.twoant.master.R.id.tx_price);
        editText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable edt){
                String temp = edt.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2){
                    edt.delete(posDot + 3, posDot + 4);
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        circleBank = (ImageView) findViewById(net.twoant.master.R.id.img_bank_circle_withdraw);
        circleYue = (ImageView) findViewById(net.twoant.master.R.id.img_yue_circle_withdraw);
        money_bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position==0) {
                    Double tp = Double.parseDouble(pp)-2;
                    if (tp>0) {
                        BigDecimal bigDecimal = new BigDecimal(tp);
                        editText.setText(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                        price = Double.parseDouble(editText.getText() + "");
                    }else{
                        ToastUtil.showLong("提现金额小于2元");
                    }
                }else {
                    editText.setText(pp);
                    price = Double.parseDouble(editText.getText() + "");
                }
            }
        });
        mAlertDialog = new PassViewDialog(WithdrawalsActivity.this, Gravity.BOTTOM, true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 try {
                      price = Double.parseDouble(editText.getText() + "");
                    } catch (Exception e) {
                     ToastUtil.showShort("请输入数字");
                     return;
                    }
                Double tp = Double.parseDouble(editText.getText().toString().trim());
                if (tp>Double.parseDouble(pp) || tp==0.00 || tp==0 || tp==0.0) {
                    ToastUtil.showLong("提现金额有误");
                    return;
                }
                if (position == 0) {
                    if (tp>50000) {
                        ToastUtil.showLong("提现数额超出5万元");
                        return;
                    }
                    if (tp+2 > Double.parseDouble(pp)) {
                        ToastUtil.showLong("提现金额有误");
                        return;
                    }
                }
                mAlertDialog.showDialog(true, true);
                mAlertDialog.clearn();
                mAlertDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
                    @Override
                    public void inputFinish(String strPassword) {
                        map.clear();
                        map.put("user", AiSouAppInfoModel.getInstance().getUID());
                        map.put("pwd", strPassword);
                        hintDialogUtil.showLoading();
                        LongHttp(ApiConstants.CHECKPAYPWD, "", map, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtil.showShort(e.getMessage());
                                mAlertDialog.dismiss();
                                hintDialogUtil.dismissDialog();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                // DataRow.parseJson(response).getBoolean("result",false);
                                if (DataRow.parseJson(response).getBoolean("result", false)) {
                                    push_tx();
                                } else {
                                    mAlertDialog.dismiss();
                                    ToastUtil.showLong("密码错误");
                                }
                                hintDialogUtil.dismissDialog();
                            }
                        });
                    }
                });
            }
        });
    }

    //支付密码状态
    int pay_pwd_state;

    private void getPayPasswordState() {
        Map<String, String> map = new HashMap<>();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        LongHttp(ApiConstants.STATE_PAY_PASSWORD, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //mAlertDialog.showError(e+"");
            }

            @Override
            public void onResponse(String response, int id) {
                //     mAlertDialog.dismiss();
                DataRow dataRow = DataRow.parseJson(response);
                pay_pwd_state = Integer.parseInt(dataRow.get("data") + "");
                if (pay_pwd_state == 0) {
                    //没有支付密码，去设置
                    SetPasswordDialog setPasswordDialog = new SetPasswordDialog(WithdrawalsActivity.this, Gravity.CENTER, false);
                    setPasswordDialog.showDialog(true, true);
                    return;
                }
            }
        });
    }

    void push_tx() {
        if(map==null){
            map=new HashMap<>();
        }
        hintDialogUtil.showLoading();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        if(shop_id!=null) {
            map.put("shop", shop_id);
        }else{
            map.put("card", bank_info.getId()+"");
        }
        map.put("val", price + "");
        if (position==0) {//银行卡
            map.put("tar", "0");
        }else {//余额
            map.put("tar", "1");
        }
        //0：正常,1：加急
        map.put("sort", "0");
        String url = bank_info != null ? ApiConstants.SIGNPUSHWITHDRAWALS : ApiConstants.PUSHWITHDRAWALS;
        LongHttp(url, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
                mAlertDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {

                if (DataRow.parseJson(response).getBoolean("result", false)) {
                    ToastUtil.showShort("提现申请成功");
                    WithdrawalsActivity.this.finish();
                    Intent intent = new Intent(WithdrawalsActivity.this,WithdrawMonySuccessActivity.class);
                    if (position == 0) {
                        intent.putExtra("tvTitle","提现申请已提交");
                        intent.putExtra("tvBank","银行卡");
                        intent.putExtra("tvBankNum",bank_name.getText().toString().trim());
                    }else{
                        intent.putExtra("tvTitle","您已提现到钱包");
                        intent.putExtra("tvBank","蚂蚁号");
                        intent.putExtra("tvBankNum",bank_name.getText().toString().trim());
                    }
                    intent.putExtra("tvMoney",editText.getText().toString().trim());
                    startActivity(intent);
                } else {
                    ToastUtil.showShort("提现申请失败");
                }
                mAlertDialog.dismiss();
                hintDialogUtil.dismissDialog();
            }
        });
    }
}
