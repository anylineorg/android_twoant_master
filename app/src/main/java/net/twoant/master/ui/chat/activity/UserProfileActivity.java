package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseUserUtils;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.main.activity.MerchantEnteredActivity;
import net.twoant.master.ui.main.adapter.control.ControlUtils;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * 个人资料
 */
public class UserProfileActivity extends ChatBaseActivity implements View.OnClickListener, HttpConnectedUtils.IOnStartNetworkSimpleCallBack {

    private final static int REQUEST_CODE_MODIFY_INFO = 1;
    private final static String EXTRA_EASE_USER = "e_e_u";
    private final static String EXTRA_TYPE = "e_ty";
    public final static int TYPE_FRIEND_PAGE = 0xA;

    private HttpConnectedUtils mHttpUtils;
    private AppCompatTextView mTvNickname;
    private AppCompatTextView mTvSex;
    private AppCompatTextView mTvAge;
    private AppCompatTextView mTvSignature;
    private AppCompatButton mBtnSendMess;
    private CircleImageView mIvHeaderImage;
    private EaseUser mEaseUser;
    private int mType;
    private String mHeaderImgUrl;//头像链接
    private String mUsername;


    public static void startActivity(Context context, EaseUser easeUser) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EASE_USER_NAME, easeUser.getUsername());
        intent.putExtra(EXTRA_EASE_USER, easeUser);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String easeUser) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EASE_USER_NAME, easeUser);
        context.startActivity(intent);
    }

    public static void startActivityForResult(int requestCode, Activity activity, EaseUser easeUser) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(EASE_USER_NAME, easeUser.getUsername());
        intent.putExtra(EXTRA_EASE_USER, easeUser);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Activity activity, String easeUser, int type) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(EASE_USER_NAME, easeUser);
        intent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        if (intent != null) {
            mEaseUser = intent.getParcelableExtra(EXTRA_EASE_USER);
            mType = intent.getIntExtra(EXTRA_TYPE, -1);
        }
        return R.layout.yh_activity_user_info;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mType == TYPE_FRIEND_PAGE) {
            getMenuInflater().inflate(R.menu.yh_menu_clean, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mHttpUtils = HttpConnectedUtils.getInstance(this);
        initView();
        initState();
    }

    private void initView() {

        Toolbar toolbar = initSimpleToolbarData(this, getString(R.string.info_person), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.finish();
            }
        });
        if (mType == TYPE_FRIEND_PAGE) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (R.id.clean == item.getItemId()) {
                        new AlertDialog.Builder(UserProfileActivity.this, R.style.AlertDialogStyle)
                                .setMessage(R.string.Whether_to_empty_all_chats)
                                .setNegativeButton(R.string.merchant_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (EMClient.getInstance().chatManager().deleteConversation(mUsername, true)) {
                                            ToastUtil.showShort(R.string.chat_clean_history_successful);
                                        } else {
                                            ToastUtil.showShort(R.string.chat_clean_history_fail);
                                        }

                                    }
                                })
                                .setPositiveButton(R.string.merchant_dialog_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .create().show();
                        return true;
                    }
                    return false;
                }
            });
        }

        this.mTvNickname = (AppCompatTextView) findViewById(R.id.tv_nickname);
        this.mTvSex = (AppCompatTextView) findViewById(R.id.tv_sex);
        this.mTvAge = (AppCompatTextView) findViewById(R.id.tv_age);
        this.mTvSignature = (AppCompatTextView) findViewById(R.id.tv_signature);
        this.mBtnSendMess = (AppCompatButton) findViewById(R.id.btn_send_mess);
        this.mIvHeaderImage = (CircleImageView) findViewById(R.id.iv_header_image);
        mIvHeaderImage.setOnClickListener(this);
    }

    /**
     * 初始化布局的显示样式和状态
     */
    private void initState() {
        Intent intent = getIntent();
        mUsername = intent.getStringExtra(EASE_USER_NAME);
        if (mUsername != null) {
            ((AppCompatTextView) findViewById(R.id.tv_uid)).setText(mUsername);
            initUserData(mUsername);
            switch (AddContactActivity.getAdditionState(mUsername)) {

                case AddContactActivity.STATE_OWNER:
                    mBtnSendMess.setText(getString(R.string.info_modify));
                    mBtnSendMess.setTag(true);
                    mBtnSendMess.setOnClickListener(this);
                    break;

                case AddContactActivity.STATE_BLACK_LIST:
                    mBtnSendMess.setText(getString(R.string.info_black_hint));
                    mBtnSendMess.setTag(AddContactActivity.STATE_BLACK_LIST);
                    mBtnSendMess.setOnClickListener(this);
                    break;

                case AddContactActivity.STATE_ALREADY_ADD:
                    mBtnSendMess.setText(getString(R.string.info_send_mess));
                    mBtnSendMess.setTag(mUsername);
                    mBtnSendMess.setOnClickListener(this);
                    break;

                case AddContactActivity.STATE_NORMAL:
                    mBtnSendMess.setText(getString(R.string.info_add_friend));
                    mBtnSendMess.setTag(AddContactActivity.STATE_NORMAL);
                    mBtnSendMess.setOnClickListener(this);
                    break;
            }
        } else {
            UserProfileActivity.this.finish();
            ToastUtil.showShort("无法找到该账号");
        }
    }

    /**
     * 初始化用户数据， 若没有从上一页带入，则联网请求
     *
     * @param username 用户名
     */
    private void initUserData(String username) {
        if (mEaseUser == null) {
            mEaseUser = new EaseUser(username);
            getUserInfo(username);
        } else {
            mTvNickname.setText(mEaseUser.getNickname());
            mTvAge.setText(String.valueOf(mEaseUser.getAge()));
            mTvSex.setText(mEaseUser.getSex());
            mTvSignature.setText(mEaseUser.getSignature());
            ImageLoader.getImageFromLocation(mIvHeaderImage, mEaseUser.getAvatar(), this, R.drawable.ic_def_circle);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_mess:
                Object tag = v.getTag();
                if (tag instanceof Boolean) {//编辑个人信息
                   // EditDataActivity.startActivityForResult(UserProfileActivity.this, REQUEST_CODE_MODIFY_INFO);
                } else if (tag instanceof String) {//聊天
                    String username = (String) tag;
                    if (username.equals(mEaseUser.getUsername()))
                        ChatActivity.startActivity(UserProfileActivity.this, mEaseUser);
                } else if (tag instanceof Integer) {

                    int temp = (int) tag;
                    if (temp == AddContactActivity.STATE_BLACK_LIST) {
                        BlacklistActivity.startActivity(UserProfileActivity.this);
                    } else if (temp == AddContactActivity.STATE_NORMAL) {
                        AddContactActivity.startActivity(mEaseUser, UserProfileActivity.this);
                        if (!UserProfileActivity.this.isFinishing()) {
                            UserProfileActivity.this.finish();
                        }
                    }
                }
                break;
            case R.id.iv_header_image:
                v.setClickable(false);
                if (mEaseUser != null && mEaseUser.getAvatar() != null)
                    MerchantEnteredActivity.displayPhoto(UserProfileActivity.this, mHeaderImgUrl);
                v.setClickable(true);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQUEST_CODE_MODIFY_INFO == requestCode && resultCode == Activity.RESULT_OK) {
            getUserInfo(AiSouAppInfoModel.getInstance().getUID());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取用户信息
     *
     * @param uid uid账号
     */
    private void getUserInfo(String uid) {
        if (NetworkUtils.isNetworkConnected()) {
            ArrayMap<String, String> arrayMap = new ArrayMap<>(4);
            arrayMap.put("m", uid);
            mHttpUtils.startNetworkGetString(0, arrayMap, ApiConstants.FIND_USER);
        } else {
            EaseUserUtils.setUserNick(uid, mTvNickname);
//            EaseUserUtils.setUserAvatar(this, uid, mIvHeaderImage);
        }
    }

    @Override
    public void onResponse(String response, int id) {
        DataRow dataRow = DataRow.parseJson(response);
        if (mEaseUser != null && dataRow != null  && !UserProfileActivity.this.isFinishing()) {

            DataSet set=dataRow.getSet("data");
            dataRow=set.getRow(0);
            String temp = BaseConfig.getCorrectImageUrl(dataRow.getString("IMG_FILE_PATH"));
            mHeaderImgUrl = temp;
            mEaseUser.setAvatarMd5(MD5Util.getMD5ToHex(temp));
            ImageLoader.getImageFromNetwork(mIvHeaderImage, temp, this, R.drawable.ic_def_circle);
            mEaseUser.setAvatar(temp);

            temp = dataRow.getStringDef("NM", "");
            mEaseUser.setNickname(temp);
            mTvNickname.setText(temp);

            int age = dataRow.getInt("AGE");
            mEaseUser.setAge(age);
            mTvAge.setText(String.valueOf(age));

            temp = dataRow.getInt("SEX") != 0 ? "男" : "女";
            mTvSex.setText(temp);
            mEaseUser.setSex(temp);
            temp = dataRow.getStringDef("SIGN", "");
            temp = ControlUtils.isNull(temp) ? getString(R.string.info_signature_hint) : temp;
            mTvSignature.setText(temp);
            mEaseUser.setSignature(temp);
            if (mEaseUser.getUsername().equals(ChatHelper.getInstance().getCurrentUserName())) {
                UserInfoDiskHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        ChatHelper.getUserProfileManager().updateCurrentUserInfo(mEaseUser);
                    }
                });
            } else {
                UserInfoDiskHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        UserInfoDiskHelper.getInstance().syncUpdateUserInfo(mEaseUser, true);
                    }
                });
            }
        } else if (null != mBtnSendMess) {
            mBtnSendMess.setEnabled(false);
            mBtnSendMess.setText(R.string.chat_not_find_user_info);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (null != mBtnSendMess) {
            mBtnSendMess.setEnabled(false);
            mBtnSendMess.setText(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.chat_get_user_info_fail)));
        } else
            ToastUtil.showShort(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.chat_get_user_info_fail)));
    }
}
