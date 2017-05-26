package net.twoant.master.ui.my_center.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.FixPhoneActivityManager;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.other.receiver.SMSBroadcastReceiver;
import net.twoant.master.ui.other.receiver.SmsImp;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import okhttp3.Call;

public class FixPhoneGetCodeActivity extends LongBaseActivity implements View.OnClickListener,SmsImp {

    private String phone;
    public TextView tvText;
    private HintDialogUtil progressDialog;
    public EditText etCode;
    public AppCompatButton btnNext;

    public static boolean canFixPhoneClick = true;

    public static int UPDATE_TIME_FIXPHONE  = 99;

    private TextView tvGetCode;
    private MyHandler mHandler;
    public final static int UPDATA = 4;
    public static int TIME = 1000;
    private SMSBroadcastReceiver receiver;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_fix_phone_get_code;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        phone = getIntent().getStringExtra("phone");
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_time).setOnClickListener(this);
        btnNext = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_nex_fixphone);
        tvGetCode = (TextView) findViewById(net.twoant.master.R.id.tv_time);
        findViewById(net.twoant.master.R.id.tv_time);
        btnNext.setOnClickListener(this);
        tvText = (TextView) findViewById(net.twoant.master.R.id.tv_text_fixphone_getcode);
        etCode = (EditText) findViewById(net.twoant.master.R.id.et_code_fixphone_getcode);
        FixPhoneActivityManager.getStackManager().pushActivity(this);
        etCode.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            StringBuffer buffer = new StringBuffer();
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
                if (etCode.getText().length()>=6) {
                    //值满6位时
                    btnNext.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_frame_btn));
                    btnNext.setTextColor(Color.WHITE);
                    btnNext.setEnabled(true);
                }else {
                    //不满时
                    btnNext.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_fix_phone));
                    btnNext.setTextColor(Color.GRAY);
                    btnNext.setEnabled(false);
                };
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvText.setText("请输入"+phone+"收到的短信验证码");
        progressDialog = new HintDialogUtil(this);

        if (canFixPhoneClick) {
            postSms();
        }

        //短信监听注册
        receiver = new SMSBroadcastReceiver();
        receiver.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);
    }

    private void postSms() {
        HashMap<String,String> map = new HashMap<>();
//        String trim = etCode.getText().toString().trim();
//        phone = trim.replace("-","");
        map.put("p", phone);
        progressDialog.showLoading(net.twoant.master.R.string.getCodeIng);
        if (map!=null) {
            LongHttp(ApiConstants.SMS, "", map, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    progressDialog.dismissDialog();
                    ToastUtil.showLong(e+"");
                    LogUtils.d(e.getMessage());
                }

                @Override
                public void onResponse(String response, int id) {
                    System.out.println(response);
                    progressDialog.dismissDialog();
                    if (DataRow.parseJson(response).getBoolean("result",false)){
                        ToastUtil.showLong("已发送");
                        starttime();
                    }else{
                        ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                    }
                }
            });
        }

    }

    @Override
    protected void initData() {
        super.initData();
        mHandler = new MyHandler();
        if (!canFixPhoneClick) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!canFixPhoneClick){
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.btn_nex_fixphone:
                HashMap<String,String> map = new HashMap<>();
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code) && code.length() < 6) {
                    return;
                }
                progressDialog.showLoading();
                map.put("phone", phone);
                map.put("code", code);
                LongHttp(ApiConstants.FIX_PHONE, "", map, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismissDialog();
                        ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println(response);
                        progressDialog.dismissDialog();
                        boolean result = DataRow.parseJson(response).getBoolean("result", false);
                        String message = DataRow.parseJson(response).getString("message");
                        if (result) {
                            Intent intent = new Intent(FixPhoneGetCodeActivity.this,FixPhoneSuccessAcitivity.class);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                            finish();
                        }else {
                            ToastUtil.showLong(message);
                        }
                    }
                });
                break;
            case net.twoant.master.R.id.tv_time:
                progressDialog.showLoading("获取验证码中..",true,false);
                HashMap<String,String> map1 = new HashMap<>();
                map1.put("p", phone);
                LongHttp(ApiConstants.SMS, "", map1, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismissDialog();
                        ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println(response);
                        progressDialog.dismissDialog();
                        boolean result = DataRow.parseJson(response).getBoolean("result", false);
                        if (result) {
                            ToastUtil.showLong("已发送");
                        }else {
                            ToastUtil.showLong("获取验证码失败");
                        }
                    }
                });
                starttime();
                break;
        }
    }

    //计时器
    private void starttime() {
        canFixPhoneClick = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tvGetCode.setText(UPDATE_TIME_FIXPHONE -- + ""+FixPhoneGetCodeActivity.this.getResources().getString(net.twoant.master.R.string.code_fixphone));
                tvGetCode.setClickable(false); //设置不可点击
                tvGetCode.setTextColor(getResources().getColor(net.twoant.master.R.color.subordinationContentTextColor));
                SpannableString spannableString = new SpannableString(tvGetCode.getText().toString());  //获取按钮上的文字
                ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                if (UPDATE_TIME_FIXPHONE < 9) {
                    spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
                }else {
                    spannableString.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//将倒计时的时间设置为红色
                }
                tvGetCode.setText(spannableString);
                if (UPDATE_TIME_FIXPHONE  < 0) {
                    tvGetCode.setClickable(true);
                    tvGetCode.setText(net.twoant.master.R.string.code_open_afresh);
                    tvGetCode.setTextColor(getResources().getColor(net.twoant.master.R.color.colorPrimary));
                    UPDATE_TIME_FIXPHONE  = 99;
                    canFixPhoneClick = true;
                } else {
                    mHandler.postDelayed(this, TIME);
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    public void onGetMsmCode(String msg) {
        etCode.setText(msg);
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
        tvGetCode.setText(UPDATE_TIME_FIXPHONE  + ""+FixPhoneGetCodeActivity.this.getResources().getString(net.twoant.master.R.string.code_fixphone));
        tvGetCode.setClickable(false); //设置不可点击
        tvGetCode.setTextColor(getResources().getColor(net.twoant.master.R.color.subordinationContentTextColor));
        if (UPDATE_TIME_FIXPHONE  == 0) {
            tvGetCode.setClickable(true);
            tvGetCode.setText(net.twoant.master.R.string.code_open_afresh);
            tvGetCode.setTextColor(getResources().getColor(net.twoant.master.R.color.title_color));
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
    }
}
