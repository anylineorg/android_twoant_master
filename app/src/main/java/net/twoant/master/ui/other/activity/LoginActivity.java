package net.twoant.master.ui.other.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouUserBean;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.Model.LoginUserBean;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.server.ChatHandlerService;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.widget.HomePageSearchHelper;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.jpush.HandlePushSever;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by S_Y_H on 2016/11/22.9:37
 * 登录类
 */
public class LoginActivity extends BaseActivity implements HttpConnectedUtils.IOnStartNetworkCallBack, View.OnClickListener {

    private final static String ACTION_START = "LoginActivity_action_start";

    private AppCompatImageView mIvHeaderImage;
    private AppCompatTextView mTvNickname;
    private AutoCompleteTextView mAtvPhone;
    private AppCompatEditText mEtPassword;
    private TextInputLayout mTextInputLayout;
    private HintDialogUtil mHintDialogUtil;
    private HttpConnectedUtils mConnectedUtils;
    private AppCompatButton mBtnSignIn;

    public static void startActivity(Context context) {
        AppManager.getAppManager().cleanAllActivity();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setAction(ACTION_START);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.act_fade_in_center, net.twoant.master.R.anim.act_fade_out_center);
            activity.finish();
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * 退出登录
     */
    public static void logoutResetData(@Nullable final Activity activity) {

        final ProgressDialog progressDialog;
        if (activity != null) {
            progressDialog = ProgressDialog.show(activity, "请稍等", "正在退出登录..", true, false);
        } else progressDialog = null;

        resetData(activity);

        EMClient.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                if (progressDialog != null)
                    progressDialog.dismiss();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoginActivity.startActivity(activity);
                        }
                    });
                } else {
                    LoginActivity.startActivity(AiSouAppInfoModel.getAppContext());
                }
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                if (progressDialog != null)
                    progressDialog.dismiss();

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort("退出失败");
                        }
                    });
                }
            }
        });
    }

    /**
     * 重置状态
     */
    public static void resetData(Context context) {
        //清除极光tag和alis
        HandlePushSever.cleanAliasAndTag(context);
        //清除搜索栏数据
        HomePageSearchHelper.logoutClean();
        //重置移除标记
        AiSouAppInfoModel infoModel = AiSouAppInfoModel.getInstance();
        infoModel.setRequestRemoveAccount(false);
        //重置登录标记
        infoModel.setChatLogin(false);
        //重置登录错误描述
        infoModel.setChatLoginDescription(null);
        AiSouUserBean aiSouUserBean = infoModel.getAiSouUserBean();
        aiSouUserBean.setLoginToken("");
        aiSouUserBean.setAiSouID("");
        //关闭用户数据库
        UserInfoDiskHelper instanceNull = UserInfoDiskHelper.getInstanceNull();
        if (null != instanceNull) {
            instanceNull.onDestroy();
        }
        //重置环信状态
        ChatHelper instance = ChatHelper.getInstance();
        if (null != instance) {
            instance.reset();
        }
        //清除本地数据
        SharedPreferences preferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(AiSouAppInfoModel.KEY_FLAG, StartActivity.DEF_FLAG_VALUE);
        editor.putString(AiSouAppInfoModel.KEY_UID, null);
        editor.putString(AiSouAppInfoModel.KEY_TOKEN, "");
        editor.putString(AiSouAppInfoModel.KEY_USER_PHONE, null);
        boolean commit = editor.commit();

        if (!commit) {
            editor.clear();
            editor.putInt(AiSouAppInfoModel.KEY_FLAG, StartActivity.FIRST_PAGE);
            editor.apply();
        }
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_login;
    }


    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        BaseConfig.checkState(getIntent(), ACTION_START);
        mConnectedUtils = HttpConnectedUtils.getInstance(this);
        initView();
        String token=SharedPreferencesUtils.getSharedStringData(LoginActivity.this,"token");
        if (!token.equals("") && token!=""){
            login_token();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RegisterActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String phone = data.getStringExtra(RegisterActivity.EXTRA_PHONE);
            String password = data.getStringExtra(RegisterActivity.EXTRA_PASSWORD);
            login(phone, password);
        }
    }


    private void initView() {
        mHintDialogUtil = new HintDialogUtil(this);
        mIvHeaderImage = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_header_image);
        mTvNickname = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_nickname);
        mAtvPhone = (AutoCompleteTextView) findViewById(net.twoant.master.R.id.atv_phone);
        mEtPassword = (AppCompatEditText) findViewById(net.twoant.master.R.id.et_password);
        mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (mBtnSignIn.isClickable() && event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            mBtnSignIn.setClickable(false);
                            startLogin();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });
        AppCompatTextView mTvForgetPassword = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_forget_password);
        mTvForgetPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvForgetPassword.setText(getString(net.twoant.master.R.string.forget_password));
        mTextInputLayout = (TextInputLayout) findViewById(net.twoant.master.R.id.til_password);
        mBtnSignIn = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_register).setOnClickListener(this);
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0 && mTextInputLayout.isErrorEnabled()) {
                    mTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_sign_in:
                v.setClickable(false);
                startLogin();
                break;
            case net.twoant.master.R.id.tv_forget_password:
                startActivity(ForgetPwdActivity.class);
                break;
            case net.twoant.master.R.id.tv_register:
                startActivityForResult(RegisterActivity.REQUEST_CODE, RegisterActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHintDialogUtil) {
            mHintDialogUtil.dismissDialog();
        }
    }

    private void startLogin() {
        String account = mAtvPhone.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            mAtvPhone.setError(getResources().getString(net.twoant.master.R.string.account_is_empty));
            mAtvPhone.setFocusable(true);
            mAtvPhone.requestFocus();
            mBtnSignIn.setClickable(true);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mTextInputLayout.setError(getResources().getString(net.twoant.master.R.string.password_is_empty));
            mEtPassword.setFocusable(true);
            mEtPassword.requestFocus();
            mBtnSignIn.setClickable(true);
            return;
        }
        login(account, MD5Util.getMD5String(password));
    }

    private void showLoadingDialog(@StringRes int charSequence) {
        mHintDialogUtil.showLoading(charSequence, false, false);
        mBtnSignIn.setClickable(true);
    }

    private void showErrorDialog(String charSequence) {
        mBtnSignIn.setClickable(true);
        mHintDialogUtil.showError(charSequence);
    }

    /**
     * 保存用户信息
     */
    public static void saveUserData(DataRow data, String uid, Context context) {
        if (data == null) {
            return;
        }

        if (uid == null) {
            uid = data.getStringDef("CODE", null);
        }

        AiSouAppInfoModel aiSouApplication = AiSouAppInfoModel.getInstance();
        aiSouApplication.getAiSouUserBean().initData(data);
        SharedPreferences preferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        SharedPreferences.Editor editor = preferences.edit();

        //手机号
        String phone = data.getStringDef("ACCOUNT", "");
        editor.putString(AiSouAppInfoModel.KEY_USER_PHONE, phone);

        //注册极光用
//        DataSet shops = data.getSet("SHOPS");
//        ArrayList<String> arrayList = new ArrayList<>();
//        if (shops != null) {
//            String tel;
//            for (int i = 0, size = shops.size(); i < size; ++i) {
//                tel = shops.getRow(i).getString("SHOP_TEL");
//                arrayList.add(tel);
//            }
//        }
//        arrayList.add(phone);
//        HandlePushSever.startRegisterAliasAndTag(context, uid, arrayList);
        //uid
        editor.putString(AiSouAppInfoModel.KEY_UID, uid);
        //token
        editor.putString(AiSouAppInfoModel.KEY_TOKEN, data.getStringDef("LOGIN_TOKEN", ""));
        //是否登录成功
        editor.putInt(AiSouAppInfoModel.KEY_FLAG, StartActivity.SUCCESSFUL_LOGIN);
        editor.apply();
    }

    private String mPassword;

    public void login(final String account, String password) {
        showLoadingDialog(net.twoant.master.R.string.dialog_login_ing);
        if (!NetworkUtils.isNetworkConnected()) {
            showErrorDialog(getString(net.twoant.master.R.string.network_connected_fail));
            return;
        }
        mPassword = password;
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("a", account);
        arrayMap.put("p", mPassword);
        mConnectedUtils.startNetworkGetStringWithoutToken(0, arrayMap, ApiConstants.LOGIN);
    }

    void login_token(){
        showLoadingDialog(net.twoant.master.R.string.dialog_login_ing);
        ArrayMap<String, String> arrayMap = new ArrayMap<>();

        String token=SharedPreferencesUtils.getSharedStringData(LoginActivity.this,"token");

        arrayMap.put("_t", token);

        mConnectedUtils.startNetworkGetStringWithoutToken(0, arrayMap, ApiConstants.LOGIN);

    }

    public void onResponse(String response, int id) {
        DataRow dataRow = DataRow.parseJson(response);
        if (dataRow != null) {
            String error = dataRow.getString("message");
            if (dataRow.getBoolean("result", false) && (dataRow = dataRow.getRow("data")) != null) {
                String uid = dataRow.getStringDef("CODE", "");//
                String nickname = dataRow.getStringDef("NICK_NAME", "两只蚂蚁");

                AiSouAppInfoModel.SCHOOL_CD=dataRow.getString("SCHOOL_ID");
                AiSouAppInfoModel.BUILD_CD=dataRow.getString("BUILD_ID");
                AiSouAppInfoModel.IS_VIP=dataRow.getInt("IS_VIP");
                AiSouAppInfoModel.vip_data= dataRow.getString("VIP_DATE");
                HandlePushSever.startRegisterAliasAndTag(getApplicationContext(), dataRow.getString("ACCOUNT"), dataRow.getString("ACCOUNT"));
                SharedPreferencesUtils.setSharedStringData("token",dataRow.getString("LOGIN_TOKEN"));
                SharedPreferencesUtils.setSharedStringData("nick_name",nickname);
                AiSouAppInfoModel.getInstance().setToken(dataRow.getString("LOGIN_TOKEN"));
                SharedPreferencesUtils.setSharedStringData("account",dataRow.getString("ACCOUNT"));
                mTvNickname.setText(nickname);
                saveUserData(dataRow, uid, LoginActivity.this);
                //后台登录环信
                mPassword+="";
                if (mPassword!="" && !mPassword.equals("") &&mPassword!="null" && !mPassword.equals("null")) {

                    ChatHandlerService.startService(this, new LoginUserBean(uid, mPassword, nickname,
                            BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", ""))));
                }else{
                    AiSouAppInfoModel.getInstance().setChatLogin(true);//token登录后就不验证
                }
                mHintDialogUtil.dismissDialog();
                MainActivity.startActivity(LoginActivity.this);
            } else {
                mTvNickname.setText("两只蚂蚁");
                mIvHeaderImage.setImageResource(net.twoant.master.R.mipmap.ic_launcher);
                mBtnSignIn.setClickable(true);
                mHintDialogUtil.dismissDialog();
                mHintDialogUtil.showError(error);
            }
        } else {
            mBtnSignIn.setClickable(true);
            mHintDialogUtil.dismissDialog();
            mHintDialogUtil.showError("获取用户信息失败");
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        showErrorDialog(NetworkUtils.getNetworkStateDescription(call, e, getString(net.twoant.master.R.string.failed_load_data)));
    }

    @Override
    public void onBefore(Request request, int id) {
        resetData(LoginActivity.this);
    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }
}
