package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.adapter.UserBlackListAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerAdapter;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.List;

/**
 * Created by S_Y_H on 2017/3/14.
 * 用户黑名单列表
 */
public class BlacklistActivity extends ChatBaseActivity implements BaseRecyclerAdapter.IRefreshData, View.OnClickListener, Handler.Callback {

    private final static int CODE_GET_BLACK_LIST_SUCCESSFUL = 1;
    private final static int CODE_GET_BLACK_LIST_FAIL = 2;
    private final static int CODE_REMOVE_SUCCESSFUL = 3;
    private final static int CODE_REMOVE_FAIL = 4;

    private UserBlackListAdapter mUserBlackListAdapter;
    private boolean isFirstObtain = true;
    private HintDialogUtil mDialog;
    private Handler mHandler;

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, BlacklistActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.em_activity_black_list;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.black_item), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlacklistActivity.this.finish();
            }
        });
        mHandler = new Handler(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(net.twoant.master.R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserBlackListAdapter = new UserBlackListAdapter(this, this, this);
        recyclerView.setAdapter(mUserBlackListAdapter);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, net.twoant.master.R.dimen.px_2));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (null != mUserBlackListAdapter) {
            mUserBlackListAdapter.refreshData();
        }
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

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        switch (v.getId()) {
            case net.twoant.master.R.id.ll_parent:
                if (tag instanceof String) {
                    UserProfileActivity.startActivity(mContext, (String) tag);
                }
                break;

            case net.twoant.master.R.id.btn_remove:
                Object position = v.getTag(net.twoant.master.R.id.chat_current_click_id);
                if (tag instanceof String && position instanceof Integer) {
                    removeOutBlacklist((String) tag, (int) position);
                }
                break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_GET_BLACK_LIST_SUCCESSFUL:
                if (msg.obj instanceof List) {
                    List<EaseUser> easeUsers = (List<EaseUser>) msg.obj;
                    isFirstObtain = false;
                    if (easeUsers.isEmpty()) {
                        mUserBlackListAdapter.showErrorLoadingHint("没有更多");
                    } else {
                        mUserBlackListAdapter.setDataBean(easeUsers, false);
                    }

                }
                break;

            case CODE_GET_BLACK_LIST_FAIL:
                if (msg.obj instanceof String) {
                    isFirstObtain = false;
                    mUserBlackListAdapter.showErrorLoadingHint((String) msg.obj);
                } else {
                    mUserBlackListAdapter.showErrorLoadingHint("加载失败");
                }
                break;

            case CODE_REMOVE_FAIL:
                if (msg.obj instanceof String) {
                    if (null != mDialog) {
                        mDialog.showError((String) msg.obj);
                    } else {
                        ToastUtil.showShort((String) msg.obj);
                    }
                }
                break;

            case CODE_REMOVE_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }

                mUserBlackListAdapter.removePosition(msg.arg1);
                ToastUtil.showShort("移除成功");
                break;
        }
        return true;
    }

    @Override
    public void getData() {
        getBlackList();
    }

    @Override
    public boolean canLoadingData() {
        return isFirstObtain;
    }

    @Override
    public void refreshData() {
        this.isFirstObtain = true;
    }

    /**
     * 获取黑名单
     */
    private void getBlackList() {
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EaseUser> groupBlackListUsersInfo = UserInfoDiskHelper.getInstance().
                            getGroupBlackListUsersInfo(EMClient.getInstance().contactManager().getBlackListFromServer());
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_GET_BLACK_LIST_SUCCESSFUL;
                        message.obj = groupBlackListUsersInfo;
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_GET_BLACK_LIST_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "获取黑名单失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }


    /**
     * 将用户移除黑名单
     */
    private void removeOutBlacklist(final String tobeRemoveUser, final int position) {
        if (null == mDialog) {
            mDialog = new HintDialogUtil(this);
        }

        if (TextUtils.isEmpty(tobeRemoveUser)) {
            ToastUtil.showShort("获取待移除用户信息失败");
            return;
        }

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading(net.twoant.master.R.string.be_removing, false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().removeUserFromBlackList(tobeRemoveUser);
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.arg1 = position;
                        message.what = CODE_REMOVE_SUCCESSFUL;
                        mHandler.sendMessage(message);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_REMOVE_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "移除失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }
}
