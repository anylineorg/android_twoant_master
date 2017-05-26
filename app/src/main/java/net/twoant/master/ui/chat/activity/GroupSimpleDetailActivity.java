package net.twoant.master.ui.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.adapter.EMAError;
import com.hyphenate.chat.adapter.EMAGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.TakePhotoBaseActivity;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import com.hyphenate.easeui.model.GroupInfoBean;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.activity.MerchantEnteredActivity;
import net.twoant.master.widget.ApplyAppendInfoDialog;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TResult;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2017/2/26.
 * 群详情页
 */
public class GroupSimpleDetailActivity extends TakePhotoBaseActivity implements Handler.Callback, View.OnClickListener {

    private final static String ACTION_START = "G_S_D_A";
    /**
     * 群id
     */
    private final static String EXTRA_GROUP_ID = "E_G_I";
    /**
     * 群名
     */
    public final static String EXTRA_GROUP_NAME = "E_G_N";

    /**
     * 群信息
     */
    private final static String EXTRA_GROUP_INFO = "E_G_IN";


    /**
     * 控制按钮 群主 解散
     */
    private final static int STATE_BTN_DISSOLVE = 0xA;

    /**
     * 控制按钮 群员 退出
     */
    private final static int STATE_BTN_QUIT = 0xB;

    private final static int GROUP_MEMBER = 0xC;
    private final static int GROUP_MODERATOR = 0xD;
    private final static int GROUP_NON_MEMBER = 0xE;

    private final static int MEMBER_LIST_REQUEST_CODE = 0xF;

    private final static int IS_MODERATOR = 0x11;
    private final static int IS_MEMBER = 0x12;

    private final static int FLAG_MODIFY_GROUP_NAME = 0x13;
    private final static int FLAG_MODIFY_GROUP_DESCRIPTION = 0x14;

    /**
     * 群信息获取成功
     */
    private final static int CODE_SUCCESSFUL = 1;

    /**
     * 群信息获取失败
     */
    private final static int CODE_FAIL = 2;

    /**
     * 加入开放群成功
     */
    private final static int CODE_APPLY_OPEN_GO = 3;

    /**
     * 申请入群成功
     */
    private final static int CODE_APPLY_SUCCESSFUL = 4;

    /**
     * 发送申请失败
     */
    private final static int CODE_APPLY_FAIL = 5;

    /**
     * 解散群组成功
     */
    private final static int CODE_DISSOLVE_SUCCESSFUL = 6;

    /**
     * 解散群组失败
     */
    private final static int CODE_DIALOG_SHOW_FAIL = 7;

    private final static int CODE_MODIFY_GROUP_NAME_SUCCESSFUL = 8;
    private final static int CODE_TOAST_SUCCESSFUL = 9;
    private final static int CODE_BLOCK_GROUP_INFO_SUCCESSFUL = 10;

    private final static int CODE_MODIFY_DESCRIPTION_SUCCESSFUL = 11;


    private AppCompatImageView mIvGroupAvatar;
    private CollapsingToolbarLayout mCollapsingToolBar;
    private AppCompatTextView mTvGroupDescription;
    private AppCompatTextView mTvGroupMaster;
    private AppCompatTextView mTvGroupMemberCount;
    private AppCompatButton mBtnApply;
    private AppCompatTextView mTvGroupId;

    private Handler mHandler;
    private ApplyAppendInfoDialog mApplyAppendInfoDialog;
    private HintDialogUtil mHintDialog;

    /**
     * 当前群组id
     */
    private String mCurrentGroupId;
    /**
     * 操作 btn
     * 群主：解散
     * 群员：退出
     * 非成员：不显示
     */
    private AppCompatButton mBtnOperation;

    private HintDialogUtil mDialog;
    private ViewStub mGroupMemberLayout;
    private ViewStub mGroupModeratorLayout;
    private AppCompatCheckBox mCbShieldGroupMsg;
    private AlertDialog mModifyGroupNameDialog;
    private AppCompatEditText mEtModifyInputView;
    private TextInputLayout mInputTextInputLayout;
    private AppCompatTextView mTvGroupName;
    private List<String> mMembersList;
    private LinearLayoutCompat mLlMemberList;

    /**
     * 是否进行了修改群名
     */
    private String mCurrentGroupName;
    private File mImageParentPath;
    private ListViewDialog mBottomDialog;
    private int mScreenWidth;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mCGI", mCurrentGroupId);
    }

    /**
     * 开启该界面 静态方法
     *
     * @param activity  活动
     * @param groupId   群组id
     * @param groupName 群名
     */
    public static void startActivity(Activity activity, String groupId, String groupName) {
        Intent intent = new Intent(activity, GroupSimpleDetailActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_GROUP_NAME, groupName);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(@Nullable Fragment fragment, @NonNull Activity activity,
                                              String groupId, String groupName, int code) {
        Intent intent = new Intent(activity, GroupSimpleDetailActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_GROUP_NAME, groupName);
        if (null == fragment) {
            activity.startActivityForResult(intent, code);
        } else {
            fragment.startActivityForResult(intent, code);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_SUCCESSFUL:
                EMGroup obj = (EMGroup) msg.obj;
                Bundle data = msg.getData();
                GroupInfoBean groupInfoBean;
                if (null != data && (groupInfoBean = data.getParcelable(EXTRA_GROUP_INFO)) != null) {
                    mIvGroupAvatar.setTag(net.twoant.master.R.id.chat_id_show_group_img, groupInfoBean.getGroupAvatar());
                    mIvGroupAvatar.setOnClickListener(this);
                    ImageLoader.getImageFromNetwork(mIvGroupAvatar, groupInfoBean.getGroupAvatar(), GroupSimpleDetailActivity.this
                            , net.twoant.master.R.drawable.ic_def_action);
                }
                mMembersList = obj.getMembers();
                //初始化群信息
                initGroupInfo(obj);
                //已经在群里了，就直接显示直接进入
                initGroupMemberInfo(initBtnInfo(obj));
                break;
            case CODE_FAIL:
                mBtnApply.setClickable(false);
                mBtnApply.setEnabled(false);
                if (msg.obj instanceof String) {
                    mBtnApply.setText((String) msg.obj);
                }
                break;
            case CODE_APPLY_OPEN_GO:
                if (null != mHintDialog) {
                    mHintDialog.dismissDialog();
                }
                ToastUtil.showShort(net.twoant.master.R.string.Join_the_group_chat);
                mBtnApply.setTag(null);
                mBtnApply.setText("立即进入");
                break;
            case CODE_APPLY_SUCCESSFUL:
                if (null != mHintDialog) {
                    mHintDialog.dismissDialog();
                }
                mBtnApply.setClickable(false);
                mBtnApply.setEnabled(false);
                ToastUtil.showShort(net.twoant.master.R.string.send_the_request_is);
                break;
            case CODE_APPLY_FAIL:
                if (null != mHintDialog) {
                    mHintDialog.showError(net.twoant.master.R.string.Failed_to_join_the_group_chat);
                }
                if (msg.obj instanceof String) {
                    mBtnApply.setText((String) msg.obj);
                }

                break;

            case CODE_DISSOLVE_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }

                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }

                setResult(RESULT_OK);
                finish();
                break;

            case CODE_DIALOG_SHOW_FAIL:
                if (null != mDialog && msg.obj instanceof String) {
                    mDialog.showError((String) msg.obj);
                }
                break;

            case CODE_MODIFY_GROUP_NAME_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                if (null != mCollapsingToolBar && msg.obj instanceof String) {
                    mCollapsingToolBar.setTitle((String) msg.obj);
                    mTvGroupName.setText(String.valueOf("群名: " + msg.obj));
                    mCurrentGroupName = (String) msg.obj;
                }
                ToastUtil.showShort("修改成功");
                break;

            case CODE_TOAST_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }
                break;

            case CODE_BLOCK_GROUP_INFO_SUCCESSFUL:
                if (null != mDialog && msg.obj instanceof Boolean) {
                    mDialog.dismissDialog();
                    mCbShieldGroupMsg.setChecked((Boolean) msg.obj);
                    ToastUtil.showShort((Boolean) msg.obj ? "屏蔽群消息成功" : "取消屏蔽成功");
                }
                break;

            case CODE_MODIFY_DESCRIPTION_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }

                if (msg.obj instanceof String) {
                    initGroupDescription((String) msg.obj);
                }
                ToastUtil.showShort("修改成功");
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mCurrentGroupName)) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_GROUP_NAME, mCurrentGroupName);
            setResult(RESULT_OK, intent);
            this.finish();
        }
        super.onBackPressed();
    }

    /**
     * 初始化群组状态信息 、 群主、群员、游客
     */
    private void initGroupMemberInfo(int code) {
        switch (code) {
            case GROUP_MEMBER:
                View member = mGroupMemberLayout.inflate();
                member.findViewById(net.twoant.master.R.id.tv_clean_record).setOnClickListener(this);
                member.findViewById(net.twoant.master.R.id.ll_shield_group).setOnClickListener(this);
                mCbShieldGroupMsg = (AppCompatCheckBox) member.findViewById(net.twoant.master.R.id.cb_shield_group_message);
                member.findViewById(net.twoant.master.R.id.tv_search_message).setOnClickListener(this);
                break;
            case GROUP_MODERATOR:
                View moderator = mGroupModeratorLayout.inflate();
                moderator.findViewById(net.twoant.master.R.id.tv_clean_record).setOnClickListener(this);
                moderator.findViewById(net.twoant.master.R.id.tv_moderator_change_group_name).setOnClickListener(this);
                moderator.findViewById(net.twoant.master.R.id.tv_moderator_black_list).setOnClickListener(this);
                moderator.findViewById(net.twoant.master.R.id.tv_search_message).setOnClickListener(this);
                break;
            case GROUP_NON_MEMBER:

                break;
        }
    }

    /**
     * 初始化当前群信息
     */
    private void initGroupInfo(EMGroup obj) {
        initGroupDescription(obj.getDescription());
        mTvGroupMemberCount.setText(String.valueOf(obj.getMemberCount() + "人"));

        String temp = obj.getGroupId();
        mTvGroupId.setText(String.valueOf("群号: " + temp));
        mBtnApply.setTag(net.twoant.master.R.id.chat_group_id, temp);
        mBtnApply.setTag(obj.isMembersOnly());
        mBtnApply.setOnClickListener(this);

        temp = obj.getOwner();
        mTvGroupMaster.setText(temp);//群所有者
        mTvGroupMaster.setTag(temp);
        mTvGroupMaster.setOnClickListener(this);
        getMasterInfo(temp);
        temp = obj.getGroupName();
        mTvGroupName.setText(String.valueOf("群名: " + temp));
        mCollapsingToolBar.setTitle(temp);
    }

    /**
     * 初始化群介绍
     */
    private void initGroupDescription(String description) {
        SpannableString spannableString = new SpannableString("群简介\n" + description);
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, net.twoant.master.R.color.colorPrimary)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvGroupDescription.setText(spannableString);
    }

    /**
     * 初始化 下面的两个btn 信息
     *
     * @param obj 群组信息
     */
    private int initBtnInfo(EMGroup obj) {
        String owner = obj.getOwner();
        String currentUser = AiSouAppInfoModel.getInstance().getUID();
        if (null != owner && owner.equals(currentUser)) {

            mIvGroupAvatar.setTag(net.twoant.master.R.id.chat_id_group_img, owner);
            mIvGroupAvatar.setOnClickListener(this);

            if (View.VISIBLE != mBtnOperation.getVisibility()) {
                mBtnOperation.setVisibility(View.VISIBLE);
            }
            View hint = findViewById(net.twoant.master.R.id.tv_modify_group_name);
            if (View.VISIBLE != hint.getVisibility()) {
                hint.setVisibility(View.VISIBLE);
            }
            mBtnOperation.setTag(STATE_BTN_DISSOLVE);
            mBtnOperation.setText("解散");
            mBtnOperation.setOnClickListener(this);

            mBtnApply.setTag(null);
            mBtnApply.setText("立即进入");
            mLlMemberList.setTag(IS_MODERATOR);
            mLlMemberList.setOnClickListener(this);

            mTvGroupDescription.setClickable(true);
            mTvGroupDescription.setBackgroundResource(net.twoant.master.R.drawable.yh_btn_press_transparent_grey);
            mTvGroupDescription.setOnClickListener(this);
            initBottomDialogData();
            return GROUP_MODERATOR;
        } else {
            List<String> members = obj.getMembers();
            if (null != members && !members.isEmpty()) {
                for (String user : members) {
                    if (user.equals(currentUser)) {
                        mBtnApply.setTag(null);
                        mBtnApply.setText("立即进入");

                        if (View.VISIBLE != mBtnOperation.getVisibility()) {
                            mBtnOperation.setVisibility(View.VISIBLE);
                        }
                        mBtnOperation.setTag(STATE_BTN_QUIT);
                        mBtnOperation.setText("退群");
                        mBtnOperation.setOnClickListener(this);
                        mLlMemberList.setTag(IS_MEMBER);
                        mLlMemberList.setOnClickListener(this);
                        return GROUP_MEMBER;
                    }
                }
            }
        }
        return GROUP_NON_MEMBER;
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_visitor_group_details;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            mCurrentGroupId = savedInstanceState.getString("mCGI");
        }
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mDialog = new HintDialogUtil(this);
        mHandler = new Handler(this);
        initView();
        getGroupInfo(intent.getStringExtra(EXTRA_GROUP_ID));
        mCollapsingToolBar.setTitle(intent.getStringExtra(EXTRA_GROUP_NAME));
        mCollapsingToolBar.setCollapsedTitleGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        mCollapsingToolBar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolBar.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolBar.setContentScrimResource(net.twoant.master.R.color.colorPrimary);
        mIvGroupAvatar.setImageResource(net.twoant.master.R.drawable.ic_def_action);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (MEMBER_LIST_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            getGroupInfo(mCurrentGroupId);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (null != mDialog) {
            mDialog.dismissDialog();
        }
    }

    /**
     * 获取群主信息
     */
    private void getMasterInfo(String owner) {
        UserInfoUtil.getUserInfo(owner, new HttpConnectedUtils.IOnStartNetworkSimpleCallBack() {
            @Override
            public void onResponse(String response, int id) {
                DataRow data = DataRow.parseJson(response);
                if (null != data && null != (data = data.getRow("result")) && !GroupSimpleDetailActivity.this.isFinishing()) {
                    mTvGroupMaster.setText(String.valueOf("群主: " + data.getStringDef("nick_name", "") + " (" + mTvGroupMaster.getText() + ")"));
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }
        });
    }

    /**
     * 获取群信息
     *
     * @param groupId 群id
     */
    private void getGroupInfo(final String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            GroupSimpleDetailActivity.this.finish();
            ToastUtil.showShort("群信息获取异常");
            return;
        }
        mCurrentGroupId = groupId;

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroup groupFromServer = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                    GroupInfoBean groupInfo = UserInfoDiskHelper.getInstance().getSyncSpecifyGroupInfo(groupFromServer.getGroupId(), false);
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = groupFromServer;
                        if (null != groupInfo) {
                            Bundle bundle = new Bundle(1);
                            bundle.putParcelable(EXTRA_GROUP_INFO, groupInfo);
                            message.setData(bundle);
                        }
                        message.what = CODE_SUCCESSFUL;
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = UserInfoUtil.getErrorDescription(e, "获取群信息失败");
                        message.what = CODE_FAIL;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    private void dissolveOrExit(final boolean isDissolve, String hint) {
        new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle)
                .setMessage(hint)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDissolve) {
                            dissolveGroup();
                        } else {
                            exitGroup();
                        }
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * 解散群组
     */
    private void dissolveGroup() {
        if (TextUtils.isEmpty(mCurrentGroupId)) {
            ToastUtil.showShort("无法获取当前群信息");
            return;
        }
        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("请稍后…", false, false);
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().destroyGroup(mCurrentGroupId);
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DISSOLVE_SUCCESSFUL;
                        message.obj = "群解散成功";
                        mHandler.sendMessage(message);
                    }
                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_SHOW_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "解散群失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 退出群组
     */
    private void exitGroup() {
        Object tag = mTvGroupMaster.getTag();
        if (tag instanceof String && tag.equals(AiSouAppInfoModel.getInstance().getUID())) {
            ToastUtil.showShort("群主不能退出群");
            return;
        }

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().leaveGroup(mCurrentGroupId);
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DISSOLVE_SUCCESSFUL;
                        message.obj = "退出群成功";
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_SHOW_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "退出群失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });

    }

    /**
     * 清空群聊天记录
     */
    private void clearGroupHistory() {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mCurrentGroupId,
                EMConversation.EMConversationType.GroupChat);
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        ToastUtil.showShort(net.twoant.master.R.string.messages_are_empty);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_group_master:
                Object tag = v.getTag();
                if (tag instanceof String) {
                    UserProfileActivity.startActivity(this, (String) tag);
                }
                break;

            case net.twoant.master.R.id.btn_apply:
                Object parameter = v.getTag();
                if (parameter instanceof Boolean) {
                    beforeAddToGroup((Boolean) parameter);
                } else if ((parameter = v.getTag(net.twoant.master.R.id.chat_group_id)) instanceof String) {
                    ChatActivity.startGroupActivity(this, (String) parameter);
                    GroupSimpleDetailActivity.this.finish();
                }
                break;

            case net.twoant.master.R.id.ll_group_member_count:
                Object state = v.getTag();
                if (mMembersList instanceof ArrayList && state instanceof Integer) {
                    GroupMemberListActivity.startActivityForResult(this, mCurrentGroupId
                            , IS_MODERATOR == (int) state, (ArrayList<String>) mMembersList, MEMBER_LIST_REQUEST_CODE
                            , (String) mTvGroupMaster.getTag());
                }
                break;

            case net.twoant.master.R.id.btn_cancel:
                mApplyAppendInfoDialog.dismiss();
                break;

            case net.twoant.master.R.id.btn_send:
                addToGroup(true);
                break;

            case net.twoant.master.R.id.btn_operation:
                Object stateCode = v.getTag();
                if (stateCode instanceof Integer) {
                    int code = (int) stateCode;
                    if (STATE_BTN_DISSOLVE == code) {//解散
                        dissolveOrExit(true, "确定解散该群组？");
                    } else if (STATE_BTN_QUIT == code) {//退出
                        dissolveOrExit(false, "确定退出该群组？");
                    }
                }
                break;

            case net.twoant.master.R.id.tv_clean_record:
                new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle)
                        .setMessage("是否清除聊天记录？")
                        .setNegativeButton("清除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearGroupHistory();
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;

            case net.twoant.master.R.id.ll_shield_group://屏蔽群消息
                if (mCbShieldGroupMsg.isChecked()) {
                    toggleBlockGroup(false);
                } else {
                    toggleBlockGroup(true);
                }
                break;

            case net.twoant.master.R.id.tv_search_message:
                startActivity(new Intent(this, GroupSearchMessageActivity.class).putExtra("groupId", mCurrentGroupId));
                break;

            case net.twoant.master.R.id.tv_moderator_change_group_name:
                initInputDialog();
                mInputTextInputLayout.setCounterMaxLength(30);
                mEtModifyInputView.setTag(FLAG_MODIFY_GROUP_NAME);
                mInputTextInputLayout.setHint("请输入新群名");

                if (!mModifyGroupNameDialog.isShowing()) {
                    mModifyGroupNameDialog.show();
                }

                break;

            case net.twoant.master.R.id.tv_moderator_black_list:
                GroupBlacklistActivity.startActivity(this, mCurrentGroupId);
                break;

            case net.twoant.master.R.id.btn_modify_cancel:
                if (mModifyGroupNameDialog.isShowing()) {
                    mModifyGroupNameDialog.cancel();
                }
                break;
            case net.twoant.master.R.id.btn_modifiy_apply:
                if (mModifyGroupNameDialog.isShowing()) {
                    mModifyGroupNameDialog.dismiss();
                }
                Object flag = mEtModifyInputView.getTag();
                if (flag instanceof Integer) {
                    switch ((int) flag) {
                        case FLAG_MODIFY_GROUP_DESCRIPTION:
                            modifyGroupDescription(mEtModifyInputView.getText().toString());
                            break;
                        case FLAG_MODIFY_GROUP_NAME:
                            modifyGroupName(mEtModifyInputView.getText().toString());
                            break;
                    }
                }
                break;

            case net.twoant.master.R.id.iv_group_avatar:
                Object own = v.getTag(net.twoant.master.R.id.chat_id_group_img);
                if (own instanceof String) {
                    if (own.equals(mTvGroupMaster.getTag())) {
                        mBottomDialog.showDialog(true, true);
                    }
                } else {
                    own = v.getTag(net.twoant.master.R.id.chat_id_show_group_img);
                    if (own instanceof String) {
                        MerchantEnteredActivity.displayPhoto(this, (String) own);
                    }
                }
                break;

            case net.twoant.master.R.id.tv_group_description:
                if (AiSouAppInfoModel.getInstance().getUID().equals(mTvGroupMaster.getTag())) {
                    initInputDialog();
                    mInputTextInputLayout.setCounterMaxLength(500);
                    mEtModifyInputView.setTag(FLAG_MODIFY_GROUP_DESCRIPTION);
                    mInputTextInputLayout.setHint("请输入新的群简介");

                    if (!mModifyGroupNameDialog.isShowing()) {
                        mModifyGroupNameDialog.show();
                    }
                }
                break;
        }
    }

    /**
     * 修改群描述
     */
    private void modifyGroupDescription(final String description) {
        Object tag = mTvGroupMaster.getTag();
        if (!(tag instanceof String) || !tag.equals(AiSouAppInfoModel.getInstance().getUID())) {
            ToastUtil.showShort("您没有权限");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            ToastUtil.showShort("群描述为空");
            return;
        }

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupManager emGroupManager = EMClient.getInstance().groupManager();

                Class<? extends EMGroupManager> aClass = emGroupManager.getClass();
                try {
                    Field emaObject = aClass.getDeclaredField("emaObject");
                    emaObject.setAccessible(true);
                    Object o = null;
                    try {
                        o = emaObject.get(emGroupManager);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (o instanceof EMAGroupManager) {
                        EMAError error = new EMAError();
                        ((EMAGroupManager) o).changeGroupDescription(mCurrentGroupId, description, error);
                        if (0 == error.errCode()) {
                            if (null != mHandler) {
                                Message message = mHandler.obtainMessage();
                                message.obj = description;
                                message.what = CODE_MODIFY_DESCRIPTION_SUCCESSFUL;
                                mHandler.sendMessage(message);
                                return;
                            }
                        } else {
                            if (null != mHandler) {
                                Message message = mHandler.obtainMessage();
                                message.obj = UserInfoUtil.getErrorDescription(error.errCode(), "更改群简介失败");
                                message.what = CODE_DIALOG_SHOW_FAIL;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }

                if (null != mHandler) {
                    Message message = mHandler.obtainMessage();
                    message.obj = "更改失败";
                    message.what = CODE_DIALOG_SHOW_FAIL;
                    mHandler.sendMessage(message);

                }
            }
        });

    }

    /**
     * 初始化 输入弹窗
     */
    private void initInputDialog() {
        if (null == mModifyGroupNameDialog) {
            @SuppressLint("InflateParams")
            View modifyView = getLayoutInflater().inflate(net.twoant.master.R.layout.yh_dialog_modify_group_name, null);
            mEtModifyInputView = (AppCompatEditText) modifyView.findViewById(net.twoant.master.R.id.et_input_group_name);
            mInputTextInputLayout = (TextInputLayout) modifyView.findViewById(net.twoant.master.R.id.til_input_parent);
            modifyView.findViewById(net.twoant.master.R.id.btn_modify_cancel).setOnClickListener(this);
            modifyView.findViewById(net.twoant.master.R.id.btn_modifiy_apply).setOnClickListener(this);
            mModifyGroupNameDialog = new AlertDialog.Builder(this, net.twoant.master.R.style.AlertDialogStyle)
                    .setView(modifyView)
                    .create();
        }

        if (0 != mEtModifyInputView.length()) {
            mEtModifyInputView.getText().clear();
        }
    }

    /**
     * 切换群组 屏蔽状态
     *
     * @param isBlock true 屏蔽 ，  false 取消屏蔽
     */
    private void toggleBlockGroup(final boolean isBlock) {

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isBlock) {
                        EMClient.getInstance().groupManager().unblockGroupMessage(mCurrentGroupId);
                    } else {
                        EMClient.getInstance().groupManager().blockGroupMessage(mCurrentGroupId);
                    }

                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_BLOCK_GROUP_INFO_SUCCESSFUL;
                        message.obj = isBlock;
                        mHandler.sendMessage(message);
                    }


                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_SHOW_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, isBlock ? "屏蔽群消息失败" : "取消屏蔽失败");
                        mHandler.sendMessage(message);
                    }

                }
            }
        });
    }


    private void modifyGroupName(String inputName) {
        Object tag = mTvGroupMaster.getTag();
        if (!(tag instanceof String) || !tag.equals(AiSouAppInfoModel.getInstance().getUID())) {
            ToastUtil.showShort("您没有权限");
            return;
        }

        if (TextUtils.isEmpty(inputName)) {
            ToastUtil.showShort("群名不能为空");
            return;
        }

        final String name = inputName.replace("\n", "");

        if (name.equals(mCollapsingToolBar.getTitle())) {
            ToastUtil.showShort("新群名不能与旧群名相同");
            return;
        }

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().groupManager().changeGroupName(mCurrentGroupId, name);
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = name;
                        message.what = CODE_MODIFY_GROUP_NAME_SUCCESSFUL;
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_SHOW_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "修改群名失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });

    }

    /**
     * 初始化View
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mCurrentGroupName)) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_GROUP_NAME, mCurrentGroupName);
                    setResult(RESULT_OK, intent);
                }
                GroupSimpleDetailActivity.this.finish();
            }
        });

        this.mTvGroupName = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_group_name);
        this.mTvGroupMaster = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_group_master);
        this.mTvGroupMemberCount = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_group_member_count);
        this.mBtnApply = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_apply);
        this.mBtnOperation = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_operation);
        this.mIvGroupAvatar = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_group_avatar);
        this.mLlMemberList = (LinearLayoutCompat) findViewById(net.twoant.master.R.id.ll_group_member_count);
        ViewGroup.LayoutParams layoutParams = mIvGroupAvatar.getLayoutParams();
        layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() / 5.0F * 3 + 0.5F);
        mIvGroupAvatar.setLayoutParams(layoutParams);
        this.mCollapsingToolBar = (CollapsingToolbarLayout) findViewById(net.twoant.master.R.id.collapsing_tool_bar);
        this.mTvGroupDescription = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_group_description);
        this.mTvGroupId = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_group_id);
        this.mGroupMemberLayout = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_member_view);
        this.mGroupModeratorLayout = (ViewStub) findViewById(net.twoant.master.R.id.view_stub_moderator_view);
    }


    /**
     * 申请入群 前准备
     *
     * @param isMembersOnly 是否是对外开放的（true 需要审核， false 不要审核直接进入)
     */
    private void beforeAddToGroup(boolean isMembersOnly) {
        if (isMembersOnly) {
            if (null == mApplyAppendInfoDialog) {
                mApplyAppendInfoDialog = new ApplyAppendInfoDialog(this);
                mApplyAppendInfoDialog.setOnClickListener(this);
            }
            if (!mApplyAppendInfoDialog.isShowing()) {
                mApplyAppendInfoDialog.getEditInfo().setText("申请入群");
                mApplyAppendInfoDialog.showDialog(true, false);
            }
        } else {
            addToGroup(true);
        }
    }

    /**
     * 申请入群 前准备
     *
     * @param isMembersOnly 是否是对外开放的（true 需要审核， false 不要审核直接进入)
     */
    private void addToGroup(final boolean isMembersOnly) {

        if (null == mHintDialog) {
            mHintDialog = new HintDialogUtil(this);
        }

        if (mHintDialog.isShowing()) {
            return;
        }

        if (null != mApplyAppendInfoDialog) {
            mApplyAppendInfoDialog.dismiss();
        }

        mHintDialog.showLoading(net.twoant.master.R.string.Is_sending_a_request, true, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null == mBtnApply || (isMembersOnly && null == mApplyAppendInfoDialog)) {
                        if (null != mHandler) {
                            Message message = mHandler.obtainMessage();
                            message.obj = "申请失败";
                            message.what = CODE_APPLY_FAIL;
                            mHandler.sendMessage(message);
                        }
                        return;
                    }

                    if (isMembersOnly) {
                        EMClient.getInstance().groupManager().applyJoinToGroup((String) mBtnApply.getTag(net.twoant.master.R.id.chat_group_id),
                                mApplyAppendInfoDialog.getEditInfo().getText().toString());
                        if (null != mHandler)
                            mHandler.sendEmptyMessage(CODE_APPLY_SUCCESSFUL);
                    } else {
                        EMClient.getInstance().groupManager().joinGroup((String) mBtnApply.getTag(net.twoant.master.R.id.chat_group_id));
                        if (null != mHandler)
                            mHandler.sendEmptyMessage(CODE_APPLY_OPEN_GO);
                    }

                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = UserInfoUtil.getErrorDescription(e);
                        message.what = CODE_APPLY_FAIL;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });

    }

    @Override
    public void takeSuccess(TResult result) {
        if (null == mDialog || mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        final String compressPath = result.getImage().getCompressPath();
        if (ImageView.ScaleType.FIT_XY != mIvGroupAvatar.getScaleType()) {
            mIvGroupAvatar.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        ImageLoader.getImageFromLocation(mIvGroupAvatar, compressPath, this, net.twoant.master.R.drawable.ic_def_small);
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (NewGroupActivity.updateGroupAvatar(compressPath, mCurrentGroupId)) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = "上传群头像完成";
                        message.what = CODE_TOAST_SUCCESSFUL;
                        mHandler.sendMessage(message);
                    }
                } else {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = "上传群头像失败";
                        message.what = CODE_DIALOG_SHOW_FAIL;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 选择照片
     */
    private void initBottomDialogData() {
        mScreenWidth = DisplayDimensionUtils.getScreenWidth();
        String path = AiSouAppInfoModel.getInstance().getUID() + File.separator + "group";
        if (FileUtils.sdCardExists()) {
            File externalCacheDir = this.getExternalCacheDir();
            if (null != externalCacheDir)
                mImageParentPath = new File(externalCacheDir.getAbsolutePath() + File.separator + path);
        } else {
            mImageParentPath = new File(getCacheDir().getAbsolutePath(), path);
        }

        mBottomDialog = new ListViewDialog(mContext);
        mBottomDialog.setInitData(getString(net.twoant.master.R.string.merchant_dialog_cancel), getResources().getStringArray(net.twoant.master.R.array.bottom_dialog));
        mBottomDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                File file;
                if (null != mImageParentPath) {
                    file = new File(mImageParentPath, System.currentTimeMillis() + ".jpg");
                    if (!file.getParentFile().exists())
                        if (!file.getParentFile().mkdirs()) {
                            ToastUtil.showShort(net.twoant.master.R.string.merchant_dialog_folder_create_fail);
                            return;
                        }
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.merchant_dialog_folder_create_fail);
                    return;
                }

                configCompress(150 * 1024, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F), true);
                if (position == 0) {
                    startGetPhoto(true, 1, file, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F));
                } else {
                    startGetPhoto(false, 1, file, mScreenWidth, (int) (mScreenWidth / 5.0F * 3 + .5F));
                }
            }
        });
    }
}
