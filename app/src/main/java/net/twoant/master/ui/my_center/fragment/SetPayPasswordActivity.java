package net.twoant.master.ui.my_center.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/23.
 * 佛祖保佑   永无BUG
 */
public class SetPayPasswordActivity extends LongBaseActivity implements View.OnClickListener{
    private EditText firstPassword,secondPassword,etCode;
    private String phone;
    private TextView mTvCodeRegister;
    private HintDialogUtil progressDialog;
    private SMSBroadcastReceiver receiver;


    public static boolean canForgetClick = true;

    public final static int UPDATA = 4;

    public static int TIME = 1000;

    public static int FORGET_UPDATE = 60;

    private MyHandler mHandler;
    public TextView phone1;
    public TextView title;
    public String firstSet;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_setpaypassword;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        phone = AiSouAppInfoModel.getInstance().getAiSouUserBean().getPhone();
        if (!TextUtils.isEmpty(phone)) {
            String start = phone.substring(0,3);
            String end = phone.substring(7,11);
            String resultphone = start+"****"+end;
            phone1 = (TextView) findViewById(net.twoant.master.R.id.et_phone);
            phone1.setText(resultphone);
        }
        firstPassword = (EditText) findViewById(net.twoant.master.R.id.et_pwd);
        secondPassword = (EditText) findViewById(net.twoant.master.R.id.et_pwd_ok);
        etCode = (EditText) findViewById(net.twoant.master.R.id.et_code);
        title = (TextView) findViewById(net.twoant.master.R.id.textView9);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_confirm).setOnClickListener(this);
        mTvCodeRegister = (TextView) findViewById(net.twoant.master.R.id.tv_code);
        mTvCodeRegister.setOnClickListener(this);
        progressDialog = new HintDialogUtil(this);
        firstSet = getIntent().getStringExtra("firstSet");
        if (!TextUtils.isEmpty(firstSet)) {
            title.setText("设置支付密码");
        }
    }

    @Override
    protected void initData() {
        mHandler = new MyHandler();

        if (!canForgetClick) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!canForgetClick){
                        try {
                            mHandler.sendEmptyMessage(UPDATA);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        //短信监听注册
        receiver = new SMSBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.tv_code:
                 if (canForgetClick) {
                    progressDialog.showLoading("获取验证码中..",true,false);
                    starttime();
                    HashMap<String,String> map = new HashMap<>();
                    map.put("p",phone);
                    LongHttp(ApiConstants.SMS, "", map, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressDialog.dismissDialog();
                            ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            System.out.println(response);
                            progressDialog.dismissDialog();
                            if (DataRow.parseJson(response).getBoolean("result",false)){
                                ToastUtil.showLong("请查收验证信息");
                            }else{
                                ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                            }
                        }
                    });
                }
                break;
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_confirm:
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.showLong("验证密码为空");
                    return;
                }
                String first = firstPassword.getText().toString().trim();
                if (first.length() < 6) {
                    ToastUtil.showLong("支付密码只能为6位密码");
                    return;
                }
                String second = secondPassword.getText().toString().trim();
                if (!first.equals(second)) {
                    ToastUtil.showLong("两次密码不相同");
                    return;
                }
                Map<String,String> map = new HashMap<>();
                map.put("user", AiSouAppInfoModel.getInstance().getUID());
                map.put("npd", MD5Util.getMD5String(first));
                map.put("code", etCode.getText()+"");
                map.put("phone", phone);
                LongHttp(ApiConstants.SET_PAY_PASSWORD, "", map, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showLong("修改失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        DataRow dataRow = DataRow.parseJson(response);
                        boolean success = dataRow.getBoolean("success",false);
                        String data = dataRow.getString("data");
                        String message = dataRow.getString("message");
                        if (success) {
                            if (!TextUtils.isEmpty(firstSet)) {
                                ToastUtil.showLong("设置支付密码成功");
                            }else {
                                ToastUtil.showLong(data);
                            }

                            finish();
                        }else{
                            ToastUtil.showLong(message);
                        }
                    }
                });
                break;
        }
    }

    //弱引用handler，防止内存泄露
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATA:
                    upDataColock();
                    break;
            }
        }
    }

    void upDataColock(){
        mTvCodeRegister.setText(FORGET_UPDATE + ""+SetPayPasswordActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
        mTvCodeRegister.setClickable(false); //设置不可点击
        mTvCodeRegister.setTextColor(getResources().getColor(net.twoant.master.R.color.black));
        SpannableString spannableString = new SpannableString(mTvCodeRegister.getText().toString());  //获取按钮上的文字
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        /**
         * public void setSpan(Object what, int start, int end, int flags) {
         * 主要是start跟end，start是起始位置,无论中英文，都算一个。
         * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
         */
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
        mTvCodeRegister.setText(spannableString);
        mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_press); //设置按钮为灰色，这时是不能点击的
        LogUtils.d(FORGET_UPDATE+"");
        if (FORGET_UPDATE == 0) {
            mTvCodeRegister.setClickable(true);
            mTvCodeRegister.setText(net.twoant.master.R.string.code_open);
            mTvCodeRegister.setTextColor(getResources().getColor(net.twoant.master.R.color.btnTextColor));
            mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_normal);  //还原背景色
        }
    };

    //计时器
    private void starttime() {
        canForgetClick = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTvCodeRegister.setText(FORGET_UPDATE-- + ""+SetPayPasswordActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
                mTvCodeRegister.setClickable(false); //设置不可点击
                mTvCodeRegister.setTextColor(getResources().getColor(net.twoant.master.R.color.black));
                SpannableString spannableString = new SpannableString(mTvCodeRegister.getText().toString());  //获取按钮上的文字
                ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                /**
                 * public void setSpan(Object what, int start, int end, int flags) {
                 * 主要是start跟end，start是起始位置,无论中英文，都算一个。
                 * 从0开始计算起。end是结束位置，所以处理的文字，包含开始位置，但不包含结束位置。
                 */
                spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
                mTvCodeRegister.setText(spannableString);
                mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_press); //设置按钮为灰色，这时是不能点击的
                if (FORGET_UPDATE < 0) {
                    mTvCodeRegister.setClickable(true);
                    mTvCodeRegister.setText(net.twoant.master.R.string.code_open);
                    mTvCodeRegister.setTextColor(getResources().getColor(net.twoant.master.R.color.btnTextColor));
                    mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_normal);  //还原背景色
                    FORGET_UPDATE = 60;
                    canForgetClick = true;
                } else {
                    mHandler.postDelayed(this, TIME);
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
           unregisterReceiver(receiver);
        }
    }


    class SMSBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pduses = (Object[]) intent.getExtras().get("pdus");
            for (Object pdus : pduses) {
                byte[] pdusmessage = (byte[]) pdus;
                SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
                String content = sms.getMessageBody(); //短信内容
                String smss = CommonUtil.getYzmFromSms(content);
                etCode.setText(smss);
            }
        }
    }
/*    private String getYzmFromSms(String smsBody) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(smsBody);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }*/
}
