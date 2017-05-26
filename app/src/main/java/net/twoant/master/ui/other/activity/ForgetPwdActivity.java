package net.twoant.master.ui.other.activity;

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
import android.widget.Toast;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.ConstUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/17.
 *
 * 忘记密码
 */

public class ForgetPwdActivity extends LongBaseActivity implements View.OnClickListener {
    private EditText etCode, etPwd, etPwdOk,etPhone;
    private TextView mTvCodeRegister;
    private MyHandler mHandler;
    private HintDialogUtil progressDialog;
    public static boolean canForgetClick = true;
    private SMSBroadcastReceiver receiver;
    public final static int VERIFICATION_SUCCESS = 1;

    public final static int VERIFICATION_FAILE = 2;

    public final static int VERIFICATION_SUNMIT = 3;

    public final static int UPDATA = 4;

    public static int TIME = 1000;

    public static int FORGET_UPDATE = 60;

    private void initView() {
        etPhone = (EditText) findViewById(net.twoant.master.R.id.et_phone);
       /* etPhone.addTextChangedListener(new TextWatcher() {
            int beforeTextLength=0;
            int onTextLength=0;
            boolean isChanged = false;

            int location=0;//记录光标的位置
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if(buffer.length()>0){
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if(s.charAt(i) == '-'){
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if(onTextLength == beforeTextLength || onTextLength <= 3 || isChanged){
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isChanged){
                    location = etPhone.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if(buffer.charAt(index) == '-'){
                            buffer.deleteCharAt(index);
                        }else{
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        //银行卡号的话需要改这里
                        if((index == 3 || index == 8)){
                            buffer.insert(index, '-');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if(konggeNumberC>konggeNumberB){
                        location+=(konggeNumberC-konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if(location>str.length()){
                        location = str.length();
                    }else if(location < 0){
                        location = 0;
                    }
                    etPhone.setText(str);
                    Editable etable = etPhone.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });*/
        etCode = (EditText) findViewById(net.twoant.master.R.id.et_code);
        etPwd = (EditText) findViewById(net.twoant.master.R.id.et_pwd);
        etPwdOk = (EditText) findViewById(net.twoant.master.R.id.et_pwd_ok);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_confirm).setOnClickListener(this);
        mTvCodeRegister = (TextView) findViewById(net.twoant.master.R.id.tv_code);
        mTvCodeRegister.setOnClickListener(this);
        progressDialog = new HintDialogUtil(this);
        mHandler = new MyHandler(ForgetPwdActivity.this);

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
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_code://获取验证码
                String inputPhone = etPhone.getText().toString();
                if (TextUtils.isEmpty(inputPhone)) {
                    ToastUtil.showLong( "请输入手机号");
                } else if (!isPhone(inputPhone)) {
                    ToastUtil.showLong("请输入正确的手机号");
                } else {
                    if (canForgetClick) {
                        progressDialog.showLoading("获取验证码中..",true,false);
                        starttime();
                        HashMap<String,String> map = new HashMap<>();
                        map.put("m",inputPhone.replaceAll("-",""));
                        LongHttp(ApiConstants.FIND_SMS, "", map, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                progressDialog.dismissDialog();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                progressDialog.dismissDialog();
                                if (DataRow.parseJson(response).getBoolean("result",false)){
                                    ToastUtil.showLong("请查收验证信息");
                                }else{
                                    ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                                }
                            }
                        });
                    }
                }
                break;

            case net.twoant.master.R.id.btn_confirm://确认修改
                submit();
                break;

            case net.twoant.master.R.id.iv_back://后退
                finish();
                break;
        }
    }

    // 判断手机格式是否正确
    public static boolean isPhone(String str) {
        Pattern p = Pattern.compile(ConstUtils.REGEX_MOBILE_EXACT);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_forgetpwd;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    //弱引用handler，防止内存泄露
    class MyHandler extends Handler {

        private WeakReference<ForgetPwdActivity> weakReference;

        MyHandler(ForgetPwdActivity sms_verificationActivity) {
            this.weakReference = new WeakReference<>(sms_verificationActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ForgetPwdActivity sms_verificationActivity = weakReference.get();
            switch (msg.what) {

                case VERIFICATION_SUCCESS:
                    //获取验证码成功
                    ToastUtil.showLong(net.twoant.master.R.string.code_success);
                    break;

                case VERIFICATION_FAILE:
                    //获取验证失败
                    sms_verificationActivity.getErrorMsg(msg);
                    progressDialog.dismissDialog();
                    break;

                case VERIFICATION_SUNMIT:
                    submit();
                    break;

                case UPDATA:
                    upDataColock();
                    break;
            }
        }
    }

    private void submit() {
        String inputPhone = etPhone.getText().toString();
        String inputPwd = etPwd.getText().toString();
        String inputPwdOk = etPwdOk.getText().toString();
        String inputeCode = etCode.getText().toString();
        if (TextUtils.isEmpty(inputPhone)) {
            ToastUtil.showLong("请输入手机号");
        }else if (!isPhone(inputPhone)){
            ToastUtil.showLong("请输入正确的手机号");
        }else if (TextUtils.isEmpty(inputeCode)) {
            ToastUtil.showLong("请输入验证码");
        } else if (TextUtils.isEmpty(inputPwd)) {
            ToastUtil.showLong("请输入密码");
        } else if (inputPwd.length() < 6) {
            ToastUtil.showLong("至少输入6位密码");
        } else if (TextUtils.isEmpty(inputPwdOk)) {
            ToastUtil.showLong("请确认密码");
        } else if (!inputPwdOk.equals(inputPwd)) {
            ToastUtil.showLong("两次密码输入不一致");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("m", etPhone.getText().toString().trim());
            String s = etPwd.getText().toString();
            System.out.println(s);
            map.put("p", MD5Util.getMD5String(s));
            map.put("c", etCode.getText()+"");
            LongHttp(ApiConstants.REST_LOGINPWD,"", map, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    progressDialog.dismissDialog();
                    ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                }

                @Override
                public void onResponse(String response, int id) {
                    progressDialog.dismissDialog();
                    LogUtils.d(response);
                    DataRow dataRow = DataRow.parseJson(response);
                    boolean result = dataRow.getBoolean("result",false);
                    String data = dataRow.getString("data");
                    String message = dataRow.getString("message");
                    if (result) {
                        ToastUtil.show(data, Toast.LENGTH_LONG);
                        finish();
                    } else {
                        ToastUtil.show(message, Toast.LENGTH_LONG);
                    }
                }
            });
        }
    }

    private void getErrorMsg(Message msg) {
        try {
            Object data = msg.obj;
            Throwable throwable = (Throwable) data;
            throwable.printStackTrace();
            JSONObject object = new JSONObject(throwable.getMessage());
            String des = object.optString("detail");//错误描述
            int status = object.optInt("status");//错误代码
            LogUtils.d("detail:"+des +"status:"+status);
            if (status > 0 && !TextUtils.isEmpty(des)) {
                Toast.makeText(ForgetPwdActivity.this, des, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //do something
        }
    }

    void upDataColock(){
        mTvCodeRegister.setText(FORGET_UPDATE + ""+ForgetPwdActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
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
                mTvCodeRegister.setText(FORGET_UPDATE-- + ""+ForgetPwdActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
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

    class SMSBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pduses = (Object[]) intent.getExtras().get("pdus");
            for (Object pdus : pduses) {
                byte[] pdusmessage = (byte[]) pdus;
                SmsMessage sms = SmsMessage.createFromPdu(pdusmessage);
                String content = sms.getMessageBody(); //短信内容
                String smss = getYzmFromSms(content);
                etCode.setText(smss);
            }
        }
    }
    private String getYzmFromSms(String smsBody) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(smsBody);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
}