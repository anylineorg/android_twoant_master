package net.twoant.master.ui.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.R;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.adapter.GroupBlackListAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.Collections;
import java.util.List;

/**
 * by S_Y_H
 * 群黑名单列表
 */
public class GroupBlacklistActivity extends ChatBaseActivity implements Handler.Callback {

    private final static String EXTRA_GROUP_ID = "groupId";
    /**
     * 群id
     */
    private String mGroupId;

    private GroupBlackListAdapter mGroupBlackListAdapter;

    private Handler mHandler;

    /**
     * 获取黑名单列表成功
     */
    private final static int ID_SUCCESSFUL = 1;
    /**
     * 获取黑名单列表失败
     */
    private final static int ID_REQUEST_FAIL = 2;
    /**
     * 移除黑名单成功
     */
    private final static int ID_REMOVE_SUCCESSFUL = 3;
    /**
     * 移除黑名单失败
     */
    private final static int ID_REMOVE_FAIL = 4;

    public static void startActivity(Context context, String groupId) {
        Intent intent = new Intent(context, GroupBlacklistActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        context.startActivity(intent);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ID_SUCCESSFUL:
                if (msg.obj instanceof List) {
                    List<EaseUser> list = (List<EaseUser>) msg.obj;
                    if (list.isEmpty()) {
                        ToastUtil.showShort("没有成员");
                    } else {
                        mGroupBlackListAdapter.setDataBean(list);
                    }
                }
                break;
            case ID_REQUEST_FAIL:
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }
                break;
            case ID_REMOVE_SUCCESSFUL:
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                    mGroupBlackListAdapter.removePosition(msg.arg1);
                }
                break;
            case ID_REMOVE_FAIL:
                if (msg.obj instanceof String) {
                    ToastUtil.showShort((String) msg.obj);
                }
                break;
        }
        return false;
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_group_blacklist;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initSimpleToolbarData(this, getString(net.twoant.master.R.string.blacklist), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupBlacklistActivity.this.finish();
            }
        });
        mHandler = new Handler(this);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.dividerLineColor, 0, R.dimen.px_2));
        mGroupBlackListAdapter = new GroupBlackListAdapter(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag instanceof Integer) {
                    int pos = (int) tag;
                    EaseUser easeUser = mGroupBlackListAdapter.getDataList().get(pos);
                    if (null != easeUser) {
                        ToastUtil.showShort("移除中…");
                        removeOutBlacklist(easeUser.getUsername(), pos);
                    }
                }
            }
        });
        recyclerView.setAdapter(mGroupBlackListAdapter);
        getBlackListData();
    }


    /**
     * 将用户移除黑名单
     */
    private void removeOutBlacklist(String tobeRemoveUser, int position) {
        try {
            EMClient.getInstance().groupManager().unblockUser(mGroupId, tobeRemoveUser);
            if (null != mHandler) {
                Message message = mHandler.obtainMessage();
                message.what = ID_REMOVE_SUCCESSFUL;
                message.obj = "移除成功";
                message.arg1 = position;
                mHandler.sendMessage(message);
            }

        } catch (HyphenateException e) {
            if (null != mHandler) {
                Message message = mHandler.obtainMessage();
                message.obj = UserInfoUtil.getErrorDescription(e, "移除失败");
                message.what = ID_REMOVE_FAIL;
                mHandler.sendMessage(message);
            }
        }
    }

    /**
     * 获取黑名单用户数据
     */
    private void getBlackListData() {
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> blockedList = EMClient.getInstance().groupManager().getBlockedUsers(mGroupId);
                    if (blockedList != null) {
                        Collections.sort(blockedList);
                        if (null != mHandler) {
                            final List<EaseUser> easeUsers = UserInfoDiskHelper.getInstance().getGroupBlackListUsersInfo(blockedList);
                            Message message = mHandler.obtainMessage();
                            message.what = ID_SUCCESSFUL;
                            message.obj = easeUsers;
                            mHandler.sendMessage(message);
                        }
                    }
                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.obj = UserInfoUtil.getErrorDescription(e, "获取黑名单失败");
                        message.what = ID_REQUEST_FAIL;
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }
}
