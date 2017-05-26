package net.twoant.master.ui.chat.activity;

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

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.GroupInfoBean;

import net.twoant.master.R;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.ui.chat.adapter.NewFriendMessageAdapter;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.domain.InviteMessage;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by S_Y_H on 2017/2/24.
 * 新朋友 界面
 */
public class NewFriendsMsgActivity extends ChatBaseActivity implements Handler.Callback {

    private Handler mHandler;
    private final static int CODE_SEND = 1;
    private final static int CODE_USER_MAP = 2;
    private final static int CODE_GROUP_MAP = 3;
    private NewFriendMessageAdapter mMessageAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.yh_activity_new_friends_msg;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        initSimpleToolbarData(this, getString(R.string.category_new_friend), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewFriendsMsgActivity.this.finish();
            }
        }).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (R.id.find == item.getItemId()) {
                    startActivity(new Intent(NewFriendsMsgActivity.this, AddContactActivity.class));
                    return true;
                }
                return false;
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, R.color.dividerLineColor, 0, R.dimen.px_2));
        mMessageAdapter = new NewFriendMessageAdapter(this);
        recyclerView.setAdapter(mMessageAdapter);
        mHandler = new Handler(this);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                InviteMessageDao dao = new InviteMessageDao(NewFriendsMsgActivity.this);
                dao.saveUnreadMessageCount(0);
                List<InviteMessage> messagesList = dao.getMessagesList();
                Collections.sort(messagesList, new Comparator<InviteMessage>() {
                    @Override
                    public int compare(InviteMessage o1, InviteMessage o2) {
                        return (int) (o2.getTime() - o1.getTime());
                    }
                });
                if (null != mHandler) {
                    Message message = mHandler.obtainMessage();
                    message.obj = messagesList;
                    message.what = CODE_SEND;
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yh_menu_find, menu);
        MenuItem item = menu.findItem(R.id.find);
        if (null != item) {
            item.setTitle("添加");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_SEND:
                if (msg.obj instanceof List) {
                    mMessageAdapter.setDataBean((List<InviteMessage>) msg.obj);
                    getUserAndGroupInfo((List<InviteMessage>) msg.obj);
                }
                break;
            case CODE_GROUP_MAP:
                if (msg.obj instanceof ArrayMap) {
                    mMessageAdapter.setGroupInfoMap((ArrayMap<String, GroupInfoBean>) msg.obj);
                }
                break;

            case CODE_USER_MAP:
                if (msg.obj instanceof ArrayMap) {
                    mMessageAdapter.setmUserInfoMap((ArrayMap<String, EaseUser>) msg.obj);
                }
                break;
        }
        return true;
    }

    private void getUserAndGroupInfo(List<InviteMessage> messagesList) {
        if (null != messagesList && !messagesList.isEmpty()) {

            final ArrayList<String> groups = new ArrayList<>();
            final ArrayList<String> users = new ArrayList<>();

            for (InviteMessage inviteMessage : messagesList) {
                if (null != inviteMessage.getGroupId()) {
                    groups.add(inviteMessage.getGroupId());
                    users.add(null == inviteMessage.getGroupInviter() ? inviteMessage.getFrom() : inviteMessage.getGroupInviter());
                } else {
                    users.add(inviteMessage.getFrom());
                }
            }

            if (!users.isEmpty()) {
                UserInfoDiskHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayMap<String, EaseUser> userInfo = UserInfoDiskHelper.getInstance().getUserInfo(users);
                        if (null != mHandler) {
                            Message message = mHandler.obtainMessage();
                            message.what = CODE_USER_MAP;
                            message.obj = userInfo;
                            mHandler.sendMessage(message);
                        }
                    }
                });
            }

            if (!groups.isEmpty()) {
                UserInfoDiskHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        ArrayMap<String, GroupInfoBean> groupInfo = UserInfoDiskHelper.getInstance().getGroupInfo(groups);
                        if (null != mHandler) {
                            Message message = mHandler.obtainMessage();
                            message.what = CODE_GROUP_MAP;
                            message.obj = groupInfo;
                            mHandler.sendMessage(message);
                        }
                    }
                });
            }
        }
    }
}
