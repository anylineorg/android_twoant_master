package net.twoant.master.ui.other.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.widget.entry.DataRow;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/16.
 * Modify by S_Y_H on 2016/12/7 添加的高德定位（替换原先的），添加了环信注册
 */

public class RegisterActivity extends LongBaseActivity implements View.OnClickListener, GDLocationHelper.IOnLocationListener {

    final static int REQUEST_CODE = 0x11;

    final static String EXTRA_PHONE = "register_phone";
    final static String EXTRA_PASSWORD = "register_password";

    private AppCompatEditText mEtPhoneRegister;
    private EditText mEtCodeRegister;
    private TextView mTvCodeRegister;
    private AppCompatEditText mEtPasswordRegister;
    private AppCompatEditText mEtIniviteRegister;
    private AppCompatImageView mIvInviteTipsRegister;
    private HintDialogUtil progressDialog;
    private MyHandler mHandler;
    public static boolean canClick = true;
    public final static int VERIFICATION_SUCCESS = 1;

    public final static int VERIFICATION_FAILE = 2;

    public final static int VERIFICATION_SUNMIT = 3;
    public final static int VERIFICATION_SERVERERROR = 5;


    public final static int UPDATA = 4;

    public static int TIME = 1000;

    public static int UPDATE = 99;

    private PopupWindow popOption;
    private View viewOption;//popwindow的页面
    /**
     * 手机号
     */
    private String mEtPhone;
    /**
     * 密码
     */
    private String mEtPassword;

    private String mInviteCode;
    /**
     */
    private String mMsg;


    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_register;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        ChatBaseActivity.initSimpleToolbarData(this, "注册", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        mEtPhoneRegister = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_phone_register);
        mEtPhoneRegister.setOnClickListener(this);
        mEtCodeRegister = (AppCompatEditText) findViewById(R.id.et_code_register);
        mEtCodeRegister.setOnClickListener(this);
        mTvCodeRegister = (TextView) findViewById(R.id.tv_code_register);
        mTvCodeRegister.setOnClickListener(this);
        mEtPasswordRegister = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_password_register);
        mEtPasswordRegister.setOnClickListener(this);
        mEtIniviteRegister = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_inivite_register);
        mEtIniviteRegister.setOnClickListener(this);
        mIvInviteTipsRegister = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_invite_tips_register);
        mIvInviteTipsRegister.setOnClickListener(this);
        AppCompatButton mBtnRegister = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
        progressDialog = new HintDialogUtil(this);
        mHandler = new MyHandler(RegisterActivity.this);


        if (!canClick) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!canClick) {
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
        LogUtils.d(canClick + "");
    }

    @Override
    protected void initData() {
        initOptionPopupWindow();
    }

    void upDataColock() {
        mTvCodeRegister.setText(UPDATE + "" + RegisterActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
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
        LogUtils.d(UPDATE + "");
        if (UPDATE == 0) {
            mTvCodeRegister.setClickable(true);
            mTvCodeRegister.setText(net.twoant.master.R.string.code_open);
            mTvCodeRegister.setTextColor(getResources().getColor(R.color.btnTextColor));
            mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_normal);  //还原背景色
        }
    }

    ;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_code_register:

                if (canClick) {
                    final HashMap<String, String> map = new HashMap<>();
                    progressDialog.showLoading(net.twoant.master.R.string.code_loging);
                    String phone = mEtPhoneRegister.getText() + "";
                    if (TextUtils.isEmpty(phone)) {
                        progressDialog.dismissDialog();
                        ToastUtil.showLong("手机号码为空");
                        return;
                    }
                    map.put("m", mEtPhoneRegister.getText() + "");
                    LongHttpN(ApiConstants.CHECKPHONE,  map, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                            ToastUtil.showLong(e.getMessage());
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            DataRow row = DataRow.parseJson(response);
                            if (row.getBoolean("result", false)) {
                                starttime();
                                LongHttp(ApiConstants.SMS, "", map, new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        progressDialog.dismissDialog();
                                        LogUtils.d(e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        progressDialog.dismissDialog();
                                        if (DataRow.parseJson(response).getBoolean("result", false)) {
                                            ToastUtil.showShort("请查收验证信息");
                                        } else {
                                            ToastUtil.showShort(DataRow.parseJson(response).getString("message"));
                                        }
                                    }
                                });
                            } else {
                                ToastUtil.showShort("此号码已注册");
                                progressDialog.dismissDialog();
                            }
                        }
                    });
                }
                break;

            case net.twoant.master.R.id.btn_register:
                submit();
                break;

            case net.twoant.master.R.id.iv_invite_tips_register://邀请人提示
                if (popOption != null && popOption.isShowing()) {
                    popOption.dismiss();
                } else {
                    showOptionPopupWindow(v);
                }
                break;
        }
    }

    private void submit() {
        mEtPhone = mEtPhoneRegister.getText().toString().trim();
        if (TextUtils.isEmpty(mEtPhone)) {
            Toast.makeText(this, getString(R.string.empty_phone), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ForgetPwdActivity.isPhone(mEtPhone)) {
            Toast.makeText(this, getString(net.twoant.master.R.string.error_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        String etCode = mEtCodeRegister.getText().toString().trim();
        if (TextUtils.isEmpty(etCode)) {
            Toast.makeText(this, getString(net.twoant.master.R.string.empty_sms), Toast.LENGTH_SHORT).show();
            return;
        }

        mEtPassword = mEtPasswordRegister.getText().toString().trim();
        if (TextUtils.isEmpty(mEtPassword)) {
            Toast.makeText(this, getString(net.twoant.master.R.string.empty_pwd), Toast.LENGTH_SHORT).show();
            return;
        }
        mInviteCode = mEtIniviteRegister.getText().toString().trim();
        if (TextUtils.isEmpty(mEtPassword)) {
            mInviteCode = "";
        }
        mEtPassword = MD5Util.getMD5String(mEtPassword);
        progressDialog.showLoading(R.string.get_posting);
        GDLocationHelper.getInstance().getOnceLocation(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GDLocationHelper.getInstance().onDestroy();
    }

    DataRow row;

    @Override
    public void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation) {
        HashMap<String, String> m = new HashMap<>();
//        m.put("phone", mEtPhone);
//        m.put("login_pwd", mEtPassword);
//        m.put("longitude", String.valueOf(longitude));
//        m.put("latitude", String.valueOf(latitude));
//        m.put("invitecode", mInviteCode);
//        m.put("code", mEtCodeRegister.getText() + "");
        m.put("m", mEtPhone);
        m.put("p", mEtPassword);
        m.put("lon", longitude+"");
        m.put("lat", latitude+"");
        m.put("iv", mInviteCode);
        m.put("s", mEtCodeRegister.getText()+"");
        LongHttp(ApiConstants.REG, "", m, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (null != progressDialog) {
                    progressDialog.dismissDialog();
                }
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call, e, "网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                row = DataRow.parseJson(response);
                if (null == row) {
                    ToastUtil.showShort("注册失败，请重新注册");
                    return;
                }
                if (!row.getBoolean("result", false)) {
                    ToastUtil.showShort(row.getString("message"));
                    return;
                }
                mMsg = row.getString("data");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // call method in SDK
                          EMClient client =  EMClient.getInstance();
                            if (null!=client) {
                                client.createAccount(mMsg, mEtPassword);
                            }
                            if (!RegisterActivity.this.isFinishing()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent();
                                        intent.putExtra(EXTRA_PHONE, mEtPhone);
                                        intent.putExtra(EXTRA_PASSWORD, mEtPassword);
                                        setResult(Activity.RESULT_OK, intent);
                                        if (null != progressDialog) {
                                            progressDialog.dismissDialog();
                                        }
                                        RegisterActivity.this.finish();
                                    }
                                });
                            }
                        } catch (final HyphenateException e) {
                            if (!RegisterActivity.this.isFinishing()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (null != progressDialog) {
                                            progressDialog.showError(UserInfoUtil.getErrorDescription(e, "注册失败"));
                                        } else {
                                            int errorCode = e.getErrorCode();
                                            if (errorCode == EMError.NETWORK_ERROR) {
                                                ToastUtil.showShort(R.string.network_anomalies);
                                            } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                                ToastUtil.showShort(net.twoant.master.R.string.User_already_exists);
                                            } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                                ToastUtil.showShort(net.twoant.master.R.string.registration_failed_without_permission);
                                            } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                                ToastUtil.showShort(net.twoant.master.R.string.illegal_user_name);
                                            } else {
                                                ToastUtil.showShort(net.twoant.master.R.string.Registration_failed);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }

                }).start();
            }
        });
    }


    //弱引用handler，防止内存泄露
    class MyHandler extends Handler {

        private WeakReference<RegisterActivity> weakReference;

        MyHandler(RegisterActivity sms_verificationActivity) {
            this.weakReference = new WeakReference<>(sms_verificationActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RegisterActivity sms_verificationActivity = weakReference.get();
            switch (msg.what) {

                case VERIFICATION_SUCCESS:
                    //获取验证码成功
                    Toast.makeText(sms_verificationActivity, net.twoant.master.R.string.code_success, Toast.LENGTH_SHORT).show();
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
                case VERIFICATION_SERVERERROR:
                    //mob服务器繁忙获取验证码失败
                    Toast.makeText(sms_verificationActivity, net.twoant.master.R.string.code_servererror, Toast.LENGTH_SHORT).show();
                    break;
            }
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
            LogUtils.d("detail:" + des + "status:" + status);
            if (status > 0 && !TextUtils.isEmpty(des)) {
                Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //do something
        }
    }

    //计时器
    private void starttime() {
        canClick = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mTvCodeRegister.setText(UPDATE-- + "" + RegisterActivity.this.getResources().getString(net.twoant.master.R.string.code_close));
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
                mTvCodeRegister.setBackgroundResource(R.drawable.bg_identify_code_press); //设置按钮为灰色，这时是不能点击的
                if (UPDATE < 0) {
                    mTvCodeRegister.setClickable(true);
                    mTvCodeRegister.setText(net.twoant.master.R.string.code_open);
                    mTvCodeRegister.setTextColor(getResources().getColor(net.twoant.master.R.color.btnTextColor));
                    mTvCodeRegister.setBackgroundResource(net.twoant.master.R.drawable.bg_identify_code_normal);  //还原背景色
                    UPDATE = 60;
                    canClick = true;
                } else {
                    mHandler.postDelayed(this, TIME);
                }
            }
        };
        mHandler.post(runnable);
    }

    private void initOptionPopupWindow() {
        // 初始化poPupwindow控件
        viewOption = this.getLayoutInflater().inflate(
                R.layout.zy_pop_invite, null);
        popOption = new PopupWindow(viewOption, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置点击周围取消poPupwindow
        popOption.setOutsideTouchable(true);
        // 设置puPupwindow获取焦点
        popOption.setFocusable(false);
        // 设置poPupwindow可以点后退键取消poPupwindow
        popOption.setBackgroundDrawable(new BitmapDrawable());
        // 点击周围取消poPupwindow
        viewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOptionPopupWindow();
            }
        });
    }

    private void showOptionPopupWindow(View v) {
        //获取控件的坐标
        int[] location = new int[2];
        //获取控件的坐标
        mIvInviteTipsRegister.getLocationOnScreen(location);
        int width = location[0];
        int height = location[1];
        int measureHeight = mIvInviteTipsRegister.getMeasuredHeight() >> 1;
        popOption.setAnimationStyle(R.style.pop_invite_anim);

        popOption.showAtLocation(v, Gravity.NO_GRAVITY, width - CommonUtil.getViewHeight(viewOption, false)
                        + CommonUtil.getViewHeight(mIvInviteTipsRegister, false),
                height - CommonUtil.getViewHeight(viewOption, true) + measureHeight);

        popOption.update();
    }

    @Override
    public void onBackPressed() {
        if (popOption.isShowing()) {
            closeOptionPopupWindow();
        } else {
            finish();
        }
    }

    /**
     * 关闭窗口
     */
    private void closeOptionPopupWindow() {
        if (popOption != null && popOption.isShowing()) {
            popOption.dismiss();
        }
    }
}

