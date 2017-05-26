package net.twoant.master.ui.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.adapter.control.ControlUtils;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/2/18.
 * 添加好友
 */
public class AddContactActivity extends ChatBaseActivity implements View.OnClickListener, HttpConnectedUtils.IOnStartNetworkSimpleCallBack {

    //当前账号
    final static int STATE_OWNER = 1;
    //在黑名单
    final static int STATE_BLACK_LIST = 2;
    //已添加
    final static int STATE_ALREADY_ADD = 3;
    //无效值
    private final static int STATE_INVALID = 4;
    //正常值，可添加为好友
    final static int STATE_NORMAL = 5;

    //输入的搜索帐号
    private AppCompatEditText mInputAccount;
    private HintDialogUtil mProgressDialog;
    private HttpConnectedUtils mHttpUtils;
    //网络请求id
    private final static int ID_FIND = 1 << 8;

    //搜索成功后的展示View
    private View mCardView;
    //添加好友时 填写附加信息
    private View mAppendView;
    //附加信息
    private AppCompatEditText mEditInfo;
    //附加信息的弹窗
    private AlertDialog mAlertDialog;
    //展示view 的 头像
    private CircleImageView mAvatar;
    //昵称
    private AppCompatTextView mNickName;
    //签名
    private AppCompatTextView mSignature;
    //添加 按钮
    private AppCompatButton mAddition;
    //用户信息
    private EaseUser mEaseUser;
    private EaseAlertDialog mEaseAlertDialog;

    public static void startActivity(EaseUser easeUser, Activity activity) {
        Intent intent = new Intent(activity, AddContactActivity.class);
        intent.putExtra(EASE_USER_NAME, easeUser);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_add_contact;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntentData(intent);
    }

    private void initIntentData(Intent intent) {
        if (null != intent) {
            EaseUser userName = intent.getParcelableExtra(EASE_USER_NAME);
            if (null != userName) {
                mInputAccount.setText(userName.getUsername());
                mInputAccount.setSelection(mInputAccount.length());
                addContact(userName.getUsername());
                initCardView();

                ImageLoader.getImageFromNetwork(mAvatar, userName.getAvatar()
                        , AddContactActivity.this, net.twoant.master.R.drawable.ic_def_circle);

                mNickName.setText(userName.getNickname());
                if (ControlUtils.isNull(userName.getNickname())) {
                    mNickName.setTextColor(ContextCompat.getColor(this, net.twoant.master.R.color.mediumGreyColor));
                } else {
                    mNickName.setTextColor(ContextCompat.getColor(this, net.twoant.master.R.color.principalTitleTextColor));
                }

                String temp = userName.getSignature();
                temp = ControlUtils.isNull(temp) ? getString(net.twoant.master.R.string.info_signature_hint) : temp;
                mSignature.setText(temp);

                temp = userName.getUsername();
                mAddition.setTag(temp);
                mCardView.setTag(temp);
                getAddBtnState(temp);
            }
        }
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.add_friend), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContactActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.find) {
                    findContact();
                    return true;
                }
                return false;
            }
        });

        mInputAccount = (AppCompatEditText) findViewById(net.twoant.master.R.id.edit_note);
        mInputAccount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            findContact();
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });
        initIntentData(getIntent());
    }

    /**
     * 查询
     */
    private void findContact() {
        final String name = mInputAccount.getText().toString();
        if (TextUtils.isEmpty(name)) {
            if (mEaseAlertDialog == null)
                mEaseAlertDialog = new EaseAlertDialog(AddContactActivity.this, net.twoant.master.R.string.Please_enter_a_username);
            if (!mEaseAlertDialog.isShowing())
                mEaseAlertDialog.show();
            return;
        }

        if (mEditInfo != null && name.equals(mEditInfo.getText().toString())) {
            return;
        }

        if (mHttpUtils == null) {
            mHttpUtils = HttpConnectedUtils.getInstance(this);
        }

        ArrayMap<String, String> arrayMap = new ArrayMap<>(2);
        arrayMap.put("m", name);
        MainActivity.closeIME(false, mInputAccount);
        mHttpUtils.startNetworkGetString(ID_FIND, arrayMap, ApiConstants.FIND_USER);
        showLoading(getResources().getString(net.twoant.master.R.string.info_account_find));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_find, menu);
        return true;
    }

    @Override
    public void onResponse(String response, int id) {
        if (AddContactActivity.this.isFinishing()) {
            return;
        }
        DataRow dataRow = DataRow.parseJson(response);

        if (dataRow != null) {
            if (dataRow.getBoolean("result", false)) {
                DataSet set=dataRow.getSet("data");
                if (null!=set){
                    dataRow=set.getRow(0);
                }
                if (dataRow != null) {
                    initCardView();

                    if (mEaseUser == null) {
                        mEaseUser = new EaseUser(dataRow.getString("CODE"));
                    }

                    String temp = BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", ""));
                    ImageLoader.getImageFromNetwork(mAvatar, temp
                            , AddContactActivity.this, net.twoant.master.R.drawable.ic_def_circle);
                    mEaseUser.setAvatar(temp);

                    temp = dataRow.getString("NM");
                    mNickName.setText(temp);
                    mEaseUser.setNickname(temp);
                    if (ControlUtils.isNull(temp)) {
                        mNickName.setTextColor(ContextCompat.getColor(this, net.twoant.master.R.color.mediumGreyColor));
                    } else {
                        mNickName.setTextColor(ContextCompat.getColor(this, net.twoant.master.R.color.principalTitleTextColor));
                    }

                    temp = dataRow.getString("SIGN");
                    temp = ControlUtils.isNull(temp) ? getString(net.twoant.master.R.string.info_signature_hint) : temp;
                    mEaseUser.setSignature(temp);
                    mSignature.setText(temp);

                    temp = dataRow.getString("CODE");
                    mAddition.setTag(temp);
                    mCardView.setTag(temp);
                    getAddBtnState(temp);

                    mEaseUser.setSex(dataRow.getInt("SEX") != 0 ? "男" : "女");
                    mEaseUser.setAge(dataRow.getInt("AGE"));
                }

            } else {
                ToastUtil.showShort(dataRow.getStringDef("message", getString(net.twoant.master.R.string.info_account_nonentity)));
            }
        } else {
            ToastUtil.showShort(getString(net.twoant.master.R.string.info_account_nonentity));
        }
        closeDialog();
    }

    private void getAddBtnState(String temp) {
        //初始化添加按钮状态
        switch (getAdditionState(temp)) {
            case STATE_OWNER:
                mAddition.setClickable(false);
                mAddition.setEnabled(false);
                mAddition.setText("本人");

                break;
            case STATE_BLACK_LIST:
                mAddition.setClickable(true);
                mAddition.setEnabled(true);
                mAddition.setText("已拉黑");
                break;
            case STATE_ALREADY_ADD:
                mAddition.setClickable(false);
                mAddition.setEnabled(false);
                mAddition.setText("已添加");
                break;
            case STATE_NORMAL:
                mAddition.setClickable(true);
                mAddition.setEnabled(true);
                mAddition.setText("添加");
                break;
        }
    }

    private void initCardView() {
        if (mCardView == null) {
            mCardView = ((ViewStub) findViewById(net.twoant.master.R.id.view_stub_info_card)).inflate();
            mAvatar = (CircleImageView) mCardView.findViewById(net.twoant.master.R.id.iv_header_image);
            mNickName = (AppCompatTextView) mCardView.findViewById(net.twoant.master.R.id.tv_nickname);
            mSignature = (AppCompatTextView) mCardView.findViewById(net.twoant.master.R.id.tv_signature);
            mAddition = (AppCompatButton) mCardView.findViewById(net.twoant.master.R.id.btn_add_friend);
            mAddition.setOnClickListener(this);
            mCardView.setOnClickListener(this);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        ToastUtil.showShort(NetworkUtils.getNetworkStateDescription(call, e, "查找失败"));
        closeDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_cancel:
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                    MainActivity.closeIME(false, mEditInfo);
                }
                break;
            case net.twoant.master.R.id.btn_send:
                sendAppendInfo(v);
                if (mAlertDialog != null && mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }
                break;
            case net.twoant.master.R.id.btn_add_friend:
                v.setClickable(false);
                if (v.getTag() instanceof String) {
                    addContact((String) v.getTag());
                }
                v.setClickable(true);
                break;
            case net.twoant.master.R.id.ll_watch_info:
                if (v.getTag() instanceof String) {
                    if (mEaseUser == null) {
                        UserProfileActivity.startActivity(AddContactActivity.this, (String) v.getTag());
                    } else {
                        UserProfileActivity.startActivity(AddContactActivity.this, mEaseUser);
                        mEaseUser.setUserName((String) v.getTag());
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHttpUtils != null) {
            mHttpUtils.onDestroy();
        }

        closeDialog();

    }

    /**
     * 关闭弹窗
     */
    private void closeDialog() {

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismissDialog();
        }
    }

    /**
     * 获取添加好友前状态码
     *
     * @param account uid
     * @return 状态码
     */
    public static int getAdditionState(String account) {
        if (account == null) return STATE_INVALID;

        if (EMClient.getInstance().getCurrentUser().equals(account)) {

            return STATE_OWNER;
        }

        if (ChatHelper.getContactList().containsKey(account)) {

            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(account)) {

                return STATE_BLACK_LIST;
            }

            return STATE_ALREADY_ADD;
        }

        return STATE_NORMAL;
    }

    /**
     * 添加联系人
     */
    private void addContact(String account) {
        if (TextUtils.isEmpty(account)) return;

        switch (getAdditionState(account)) {
            case STATE_OWNER:
                ToastUtil.showShort(net.twoant.master.R.string.not_add_myself);
                break;
            case STATE_BLACK_LIST:
                new AlertDialog.Builder(AddContactActivity.this, net.twoant.master.R.style.AlertDialogStyle)
                        .setMessage(net.twoant.master.R.string.user_already_in_contactlist)
                        .setCancelable(false)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("去黑名单", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                BlacklistActivity.startActivity(AddContactActivity.this);
                            }
                        }).create().show();
                break;
            case STATE_ALREADY_ADD:
                ToastUtil.showShort(net.twoant.master.R.string.This_user_is_already_your_friend);
                break;
            case STATE_NORMAL:
                initAppendInfo();
                break;
        }
    }

    /**
     * 初始化添加好友时的 附加消息
     */
    @SuppressLint("InflateParams")
    private void initAppendInfo() {
        if (mAppendView == null) {
            mAppendView = getLayoutInflater().inflate(net.twoant.master.R.layout.yh_write_append_info, null);
            mEditInfo = (AppCompatEditText) mAppendView.findViewById(net.twoant.master.R.id.et_info);
            mAppendView.findViewById(net.twoant.master.R.id.btn_cancel).setOnClickListener(this);
            mAppendView.findViewById(net.twoant.master.R.id.btn_send).setOnClickListener(this);
            mEditInfo.setText(getString(net.twoant.master.R.string.info_friendly_hint));
            mEditInfo.setSelection(mEditInfo.length());
        }

        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle)
                    .setCancelable(false)
                    .setView(mAppendView)
                    .create();
        }
        mAlertDialog.show();
    }

    /**
     * 发送验证信息
     */
    private void sendAppendInfo(View view) {
        view.setClickable(false);
        showLoading(getResources().getString(net.twoant.master.R.string.Is_sending_a_request));

        new Thread(new Runnable() {
            public void run() {

                try {

                    EMClient.getInstance().contactManager().addContact((String) mAddition.getTag(), mEditInfo.getText().toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            closeDialog();
                            if (mAddition != null) {
                                mAddition.setClickable(false);
                                mAddition.setEnabled(false);
                                mAddition.setText("已发送");
                            }
                            ToastUtil.showShort(net.twoant.master.R.string.send_successful);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            LogUtils.e("add friend fail" + e.toString());
                            showError(getString(net.twoant.master.R.string.Request_add_buddy_failure));
                        }
                    });
                }
            }
        }).start();
        view.setClickable(true);
    }

    /**
     * 初始化进度弹窗
     */
    private void showLoading(String hint) {
        if (mProgressDialog == null) {
            mProgressDialog = new HintDialogUtil(this);
        }
        mProgressDialog.showLoading(hint, false, false);
    }

    private void showError(String hint) {
        if (mProgressDialog == null) {
            mProgressDialog = new HintDialogUtil(this);
        }
        mProgressDialog.showError(hint, true, true);
    }

}
