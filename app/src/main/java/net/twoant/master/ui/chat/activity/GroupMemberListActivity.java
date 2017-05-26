package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.adapter.GroupMemberListAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/9.
 * 群组 成员列表
 */
public class GroupMemberListActivity extends ChatBaseActivity implements Handler.Callback {

    private final static String EXTRA_GROUP_ID = "E_G_I";
    private final static String EXTRA_IS_MODERATOR = "E_I_MR";
    private final static String EXTRA_MEMBERS_LIST = "E_M_L";
    private final static String EXTRA_MODERATOR = "E_M_R";
    private final static String ACTION_START = "G_M_L_A_ST";
    private final static int REQUEST_CODE_ADD_MEMBER = 0x11;
    private final static int CODE_DELETE_SUCCESSFUL = 0x12;
    private final static int CODE_DIALOG_FAIL = 0x13;
    private final static int CODE_DIALOG_SUCCESSFUL = 0x14;
    private final static int CODE_GET_USER_INFO_SUCCESSFUL = 0x15;

    private String mGroupId;
    private boolean isModerator;
    private GroupMemberListAdapter mGroupMemberListAdapter;
    private ArrayList<String> mMembersList;
    private String mModeratorId;
    private HintDialogUtil mDialog;
    private Handler mHandler;
    /**
     * 是否进行了修改
     */
    private boolean isModified;


    public static void startActivityForResult(Activity context, String groupId, boolean isModerator,
                                              ArrayList<String> members, int requestCode, String moderateId) {
        Intent intent = new Intent(context, GroupMemberListActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.setAction(ACTION_START);
        intent.putStringArrayListExtra(EXTRA_MEMBERS_LIST, members);
        intent.putExtra(EXTRA_IS_MODERATOR, isModerator);
        intent.putExtra(EXTRA_MODERATOR, moderateId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("iM", isModerator);
        outState.putString("mGI", mGroupId);
        outState.putString("mMI", mModeratorId);
        outState.putBoolean("isM", isModified);
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mGroupId = intent.getStringExtra(EXTRA_GROUP_ID);
        isModerator = intent.getBooleanExtra(EXTRA_IS_MODERATOR, false);
        mMembersList = intent.getStringArrayListExtra(EXTRA_MEMBERS_LIST);
        mModeratorId = intent.getStringExtra(EXTRA_MODERATOR);
        return net.twoant.master.R.layout.yh_activity_group_member_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mHandler = new Handler(this);
        mDialog = new HintDialogUtil(this);
        if (null != savedInstanceState) {
            isModerator = savedInstanceState.getBoolean("iM");
            mGroupId = savedInstanceState.getString("mGI");
            mModeratorId = savedInstanceState.getString("mMI");
            isModified = savedInstanceState.getBoolean("isM");
        }
        initView();
        initUserData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_group_member_list, menu);
        if (!isModerator) {
            MenuItem item = menu.findItem(net.twoant.master.R.id.remove);
            if (null != item) {
                item.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_DELETE_SUCCESSFUL:
                if (mGroupMemberListAdapter.doRemoveOperation()) {
                    ToastUtil.showShort("删除成功");
                }
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                break;

            case CODE_DIALOG_FAIL:
                if (null != mDialog && msg.obj instanceof String) {
                    mDialog.showError((String) msg.obj);
                }
                break;
            case CODE_DIALOG_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }
                break;
            case CODE_GET_USER_INFO_SUCCESSFUL:
                if (null != mGroupMemberListAdapter && msg.obj instanceof ArrayMap) {
                    mGroupMemberListAdapter.setUserInfoData((ArrayMap<String, EaseUser>) msg.obj);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_ADD_MEMBER == requestCode && Activity.RESULT_OK == resultCode) {
            if (null != data) {
                String[] extra = data.getStringArrayExtra(GroupPickContactsActivity.EXTRA_MEMBER_LIST);
                if (null != extra && extra.length > 0) {
                    addMembersToGroup(extra);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (null != mGroupMemberListAdapter && mGroupMemberListAdapter.getIsRemovedMemberState()) {
            mGroupMemberListAdapter.setModeIsRemoved(false);
        } else {
            setResult(isModified ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
            this.finish();
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    /**
     * 删除群成员
     */
    private void deleteMembersFromGroup(final Collection<String> username) {
        if (!isModerator || !AiSouAppInfoModel.getInstance().getUID().equals(mModeratorId)) {
            ToastUtil.showShort("您没有该权限");
            return;
        }

        if (null == username || username.isEmpty()) {
            ToastUtil.showShort("没有选择成员");
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
                    for (String uid : username) {
                        // 删除被选中的成员
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroupId, uid);
                    }
                    if (null != mHandler) {
                        mHandler.sendEmptyMessage(CODE_DELETE_SUCCESSFUL);
                    }

                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "删除操作失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 增加群成员
     */
    private void addMembersToGroup(final String[] newMembers) {

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading("处理中…", false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 创建者调用add方法
                    if (isModerator && AiSouAppInfoModel.getInstance().getUID().equals(mModeratorId)) {
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroupId, newMembers);
                    } else {
                        // 一般成员调用invite方法
                        EMClient.getInstance().groupManager().inviteUser(mGroupId, newMembers, "我觉得这个群不错");
                    }

                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_SUCCESSFUL;
                        message.obj = "发送成功";
                        mHandler.sendMessage(message);
                    }
                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_DIALOG_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "发送失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }


    private void initUserData() {
        if (null != mMembersList && !mMembersList.isEmpty()) {
            int size = mMembersList.size();
            if (2 < size) {
                getUsersData(mMembersList.subList(0, size >> 1));
                getUsersData(mMembersList.subList(size >> 1, size));
            } else {
                getUsersData(mMembersList);
            }
        }
    }

    private void getUsersData(final List<String> prefixList) {
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                ArrayMap<String, EaseUser> userInfo = UserInfoDiskHelper.getInstance().getUserInfo(prefixList);
                if (null != mHandler && !userInfo.isEmpty()) {
                    Message message = mHandler.obtainMessage();
                    message.obj = userInfo;
                    message.what = CODE_GET_USER_INFO_SUCCESSFUL;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private void initView() {
        initSimpleToolbarData(this, "群成员", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mGroupMemberListAdapter && mGroupMemberListAdapter.getIsRemovedMemberState()) {
                    mGroupMemberListAdapter.setModeIsRemoved(false);
                } else {
                    setResult(isModified ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
                    GroupMemberListActivity.this.finish();
                }
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case net.twoant.master.R.id.remove:
                        if (!isModerator) {
                            return false;
                        }

                        if (mGroupMemberListAdapter.getIsRemovedMemberState()) {
                            Collection<String> members = mGroupMemberListAdapter.getDeleteMembers();
                            if (null != members && !members.isEmpty()) {
                                deleteMembersFromGroup(members);
                            } else {
                                ToastUtil.showShort("未选中成员");
                                mGroupMemberListAdapter.setModeIsRemoved(false);
                            }
                        } else {
                            mGroupMemberListAdapter.setModeIsRemoved(true);
                        }

                        return true;

                    case net.twoant.master.R.id.add:
                        GroupPickContactsActivity.startActivityForResult(GroupMemberListActivity.this, mGroupId, REQUEST_CODE_ADD_MEMBER);
                        return true;
                }
                return false;
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_content_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGroupMemberListAdapter = new GroupMemberListAdapter(this, mModeratorId);
        recyclerView.setAdapter(mGroupMemberListAdapter);
        mGroupMemberListAdapter.setDataBean(mMembersList);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));
    }
}
