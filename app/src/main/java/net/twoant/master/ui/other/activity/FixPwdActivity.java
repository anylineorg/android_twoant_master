package net.twoant.master.ui.other.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.ConstUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.entry.DataRow;

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

public class  FixPwdActivity extends LongBaseActivity implements View.OnClickListener {
    private EditText etPwd, etPwdOk,etOldPassWord;
    private TextView etPhone;
    private MyHandler mHandler;
    private HintDialogUtil progressDialog;
    public static boolean canForgetClick = true;
    public final static int VERIFICATION_SUCCESS = 1;

    public final static int VERIFICATION_FAILE = 2;

    public final static int VERIFICATION_SUNMIT = 3;
    public final static int VERIFICATION_SERVERERROR = 5;

    public final static int UPDATA = 4;

    public static int TIME = 1000;

    public static int FORGET_UPDATE = 60;

    private boolean hadChecked = false;  //判断是否已经经过mob提交验证


    private void initView() {
        etPhone = (TextView) findViewById(net.twoant.master.R.id.et_phone);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etPwdOk = (EditText) findViewById(net.twoant.master.R.id.et_pwd_ok);
        etOldPassWord = (EditText) findViewById(R.id.et_old_fixpwd);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_confirm).setOnClickListener(this);
        progressDialog = new HintDialogUtil(this);
        //判断如果是从账户安全界面进入的带值“1”，则修改标题为修改密码（原，找回密码）
//        TextView tvTitle = (TextView) findViewById(R.id.textView9);
//        String message_titile = getIntent().getStringExtra("message_titile");
//        if ("1".equals(message_titile)) {
//            tvTitle.setText("修改密码");
//        }
        mHandler = new MyHandler(FixPwdActivity.this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_code://获取验证码
                if (canForgetClick) {
                    progressDialog.showLoading("获取验证码中..",true,false);
                }
//                String inputPhone = etPhone.getText().toString();
//                if (TextUtils.isEmpty(inputPhone)) {
//                    ToastUtil.showLong( "请输入手机号");
//                } else if (!isPhone(inputPhone)) {
//                    ToastUtil.showLong("请输入正确的手机号");
//                } else {
//                    CountDownTimerUtils code = new CountDownTimerUtils(mTvCodeRegister, 10000, 1000);
//                    code.start();
//                    Map<String,String> map=new HashMap<>();
//                    map.put("phone",inputPhone);
//                    map.put("type","pwd");
//                }
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
        return net.twoant.master.R.layout.zy_activity_fixpwd;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    //弱引用handler，防止内存泄露
    class MyHandler extends Handler {

        private WeakReference<FixPwdActivity> weakReference;

        MyHandler(FixPwdActivity sms_verificationActivity) {
            this.weakReference = new WeakReference<>(sms_verificationActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FixPwdActivity sms_verificationActivity = weakReference.get();
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
            }
        }
    }

    private void submit() {
        String oldPassword = etOldPassWord.getText().toString();
        String inputPwd = etPwd.getText().toString();
        String inputPwdOk = etPwdOk.getText().toString();
        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtil.showLong("请输入旧密码");
            return;
        } else if (TextUtils.isEmpty(inputPwd)) {
            ToastUtil.showLong("请输入新密码");
            return;
        } else if (TextUtils.isEmpty(inputPwdOk)) {
            ToastUtil.showLong("请输入确认密码");
            return;
        }else if (inputPwd.length() < 6) {
            ToastUtil.showLong("请至少输入6位密码");
            return;
        } else if (!inputPwd.equals(inputPwdOk)) {
            ToastUtil.showLong("两次密码输入不一致");
            return;
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());
            String md5String = MD5Util.getMD5String(oldPassword);
            map.put("lpd", md5String);//旧密码
            map.put("npd", MD5Util.getMD5String(inputPwdOk));//新密码
            String s = ApiConstants.BASE + "ifo/usr/uppd?&aisou_id=" + AiSouAppInfoModel.getInstance().getUID() + "&lpd=" + md5String + "&npd=" + MD5Util.getMD5String(inputPwdOk);
            System.out.println(s);
            LongHttp(ApiConstants.FIX_LOGIN_PASSWOR, "", map, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    progressDialog.dismissDialog();
                    ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                }

                @Override
                public void onResponse(String response, int id) {
                    progressDialog.dismissDialog();
                    DataRow dataRow = DataRow.parseJson(response);
                    String result = dataRow.getString("result");
                    String msg = dataRow.getString("message");
                    if (result.equals("true")) {
                        ToastUtil.show("修改成功", Toast.LENGTH_LONG);
                        LoginActivity.logoutResetData(FixPwdActivity.this);
                    } else {
                        ToastUtil.show(msg, Toast.LENGTH_LONG);
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
                Toast.makeText(FixPwdActivity.this, des, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //do something
        }
    }
}