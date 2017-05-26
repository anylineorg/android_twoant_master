package net.twoant.master.ui.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.exceptions.HyphenateException;

import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.activity.GroupsActivity;
import net.twoant.master.ui.chat.activity.NewFriendsMsgActivity;
import net.twoant.master.ui.chat.activity.PublicGroupsActivity;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.db.UserDao;
import net.twoant.master.ui.chat.util.UserInfoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 交流中心的 联系人界面
 */
public class EaseContactListFragment extends EaseBaseFragment implements Handler.Callback, View.OnClickListener {

    private final static int CODE_ADD_BACK_LIST_SUCCESSFUL = 1;
    private final static int CODE_SHOW_DIALOG_FAIL = 2;
    private final static int CODE_SYNC_ERROR = 3;
    private final static int CODE_SYNC_SUCCESSFUL = 4;
    private final static int CODE_DELETE_SUCCESSFUL = 5;


    private List<EaseUser> contactList;
    private ListView listView;
    private ImageButton clearSearch;
    private EditText query;
    private Handler mHandler;
    private EaseUser toBeProcessUser;
    private EaseContactList contactListLayout;
    private HintDialogUtil mDialog;

    private final Map<String, EaseUser> contactsMap = new Hashtable<>();

    private ContactSyncListener contactSyncListener;
    private BlackListSyncListener blackListSyncListener;
    private View loadingView, mNewFriendHint;
    private InviteMessageDao inviteMessgeDao;

    public static EaseContactListFragment newInstance() {
        return new EaseContactListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.ease_fragment_contact_list;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
        mHandler = new Handler(this);
        initView(view);
        setUpView();
    }

    @Override
    protected void onUserVisible() {
        refresh();
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //to avoid crash when open app after long time stay in background after user logged into another device
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }


    private void initView(View view) {
        FrameLayout contentContainer = (FrameLayout) view.findViewById(net.twoant.master.R.id.content_container);

        contactListLayout = (EaseContactList) view.findViewById(net.twoant.master.R.id.contact_list);
        listView = contactListLayout.getListView();

        //search
        query = (EditText) view.findViewById(net.twoant.master.R.id.et_query);
        clearSearch = (ImageButton) view.findViewById(net.twoant.master.R.id.btn_search_clear);


        View headerView = LayoutInflater.from(getActivity()).inflate(net.twoant.master.R.layout.yh_contacts_header, null);
        headerView.findViewById(net.twoant.master.R.id.rl_new_friend).setOnClickListener(this);
        mNewFriendHint = headerView.findViewById(net.twoant.master.R.id.hint_new_friend);
        headerView.findViewById(net.twoant.master.R.id.rl_group).setOnClickListener(this);
        headerView.findViewById(net.twoant.master.R.id.rl_add_group).setOnClickListener(this);
        listView.addHeaderView(headerView);
        //add loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(net.twoant.master.R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);

        registerForContextMenu(listView);
    }

    protected void setUpView() {

        //设置联系人数据
        this.contactsMap.clear();
        this.contactsMap.putAll(ChatHelper.getContactList());

        contactList = new ArrayList<>();
        getContactList();
        //init list
        contactListLayout.init(contactList);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactListLayout.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        listView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                if (user != null) {
                    ChatActivity.startActivity(mActivity, user);
                }
            }
        });

        contactSyncListener = new ContactSyncListener();
        ChatHelper.getInstance().addSyncContactListener(contactSyncListener);

        blackListSyncListener = new BlackListSyncListener();
        ChatHelper.getInstance().addSyncBlackListListener(blackListSyncListener);

        if (ChatHelper.getInstance().isContactsSyncedWithServer()) {
            loadingView.setVisibility(View.GONE);
        } else if (ChatHelper.getInstance().isSyncingContactsWithServer()) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 将用户添加到黑名单
     */
    private void moveToBlacklist() {
        if (null == toBeProcessUser) {
            ToastUtil.showShort("移入黑名单失败");
            return;
        }
        if (null == mDialog) {
            mDialog = new HintDialogUtil(mActivity);
        }
        if (mDialog.isShowing()) {
            return;
        }
        mDialog.showLoading(net.twoant.master.R.string.Is_moved_into_blacklist, false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addUserToBlackList(toBeProcessUser.getUsername(), true);
                    if (null != mHandler) {
                        mHandler.sendEmptyMessage(CODE_ADD_BACK_LIST_SUCCESSFUL);
                    }
                } catch (HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_SHOW_DIALOG_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "移入黑名单失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    // refresh ui
    public void refresh() {
        if (!isVisibility) {
            return;
        }
        this.contactsMap.clear();
        this.contactsMap.putAll(ChatHelper.getContactList());

        if (null != contactList && null != contactListLayout) {
            getContactList();
            contactListLayout.refresh();
        }


        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessageDao(getActivity());
        }
        if (inviteMessgeDao.getUnreadMessagesCount() > 0) {
            if (View.VISIBLE != mNewFriendHint.getVisibility())
                mNewFriendHint.setVisibility(View.VISIBLE);
        } else {
            if (View.GONE != mNewFriendHint.getVisibility())
                mNewFriendHint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (contactSyncListener != null) {
            ChatHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }

        if (blackListSyncListener != null) {
            ChatHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }
    }


    /**
     * get contact list and sort, will filter out users in blacklist
     */
    protected void getContactList() {
        contactList.clear();

        synchronized (this.contactsMap) {
            Iterator<Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
            List<String> blackList = EMClient.getInstance().contactManager().getBlackListUsernames();

            while (iterator.hasNext()) {
                Entry<String, EaseUser> entry = iterator.next();
                // to make it compatible with data in previous version, you can remove this check if this is new app
                if (!entry.getKey().equals("item_new_friends")
                        && !entry.getKey().equals("item_groups")
                        && !entry.getKey().equals("item_chatroom")
                        && !entry.getKey().equals("item_robots")) {
                    if (!blackList.contains(entry.getKey())) {
                        //filter out users in blacklist
                        EaseUser user = entry.getValue();
                        EaseCommonUtils.setUserInitialLetter(user);
                        contactList.add(user);
                    }
                }
            }
        }

        // sorting
        Collections.sort(contactList, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                    return lhs.getNick().compareTo(rhs.getNick());
                } else {
                    if ("#".equals(lhs.getInitialLetter())) {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter())) {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_ADD_BACK_LIST_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                refresh();
                ToastUtil.showShort(net.twoant.master.R.string.Move_into_blacklist_success);
                break;

            case CODE_SHOW_DIALOG_FAIL:
                if (msg.obj instanceof String) {
                    if (null != mDialog) {
                        mDialog.showError((String) msg.obj);
                    } else {
                        ToastUtil.showShort((String) msg.obj);
                    }
                }
                break;

            case CODE_SYNC_ERROR:
                ToastUtil.showShort(net.twoant.master.R.string.get_failed_please_check);
                if (View.GONE != loadingView.getVisibility()) {
                    loadingView.setVisibility(View.GONE);
                }
                break;

            case CODE_SYNC_SUCCESSFUL:
                if (View.GONE != loadingView.getVisibility()) {
                    loadingView.setVisibility(View.GONE);
                }
                refresh();
                break;

            case CODE_DELETE_SUCCESSFUL:
                if (null != mDialog) {
                    mDialog.dismissDialog();
                }
                if (msg.obj instanceof EaseUser) {
                    contactList.remove(msg.obj);
                    contactListLayout.refresh();
                    InviteMessageDao dao = new InviteMessageDao(getActivity());
                    dao.deleteMessage(((EaseUser) msg.obj).getUsername());
                }
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibility) {
            refresh();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.rl_new_friend:
                // 进入申请与通知页面
                startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
                break;
            case net.twoant.master.R.id.rl_group:
                // 进入群聊列表页面
                startActivity(new Intent(getActivity(), GroupsActivity.class));
                break;
            case net.twoant.master.R.id.rl_add_group:
                // 公开群列表
                startActivity(new Intent(getActivity(), PublicGroupsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (menuInfo instanceof AdapterView.AdapterContextMenuInfo) {
            toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);
            mActivity.getMenuInflater().inflate(net.twoant.master.R.menu.em_context_contact_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case net.twoant.master.R.id.contact_delete_contact:
                deleteContact(toBeProcessUser);
                return true;
            case net.twoant.master.R.id.contact_add_to_blacklist:
                moveToBlacklist();
                return true;

        }
        return super.onContextItemSelected(item);
    }


    /**
     * 删除联系人
     */
    private void deleteContact(final EaseUser tobeDeleteUser) {
        if (null == tobeDeleteUser) {
            ToastUtil.showShort("删除失败");
            return;
        }

        if (null == mDialog) {
            mDialog = new HintDialogUtil(mActivity);
        }

        if (mDialog.isShowing()) {
            return;
        }

        mDialog.showLoading(net.twoant.master.R.string.deleting, false, false);

        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getUsername());
                    if (null != mHandler) {
                        UserDao dao = new UserDao(getActivity());
                        dao.deleteContact(tobeDeleteUser.getUsername());
                        ChatHelper.getContactList().remove(tobeDeleteUser.getUsername());
                        Message message = mHandler.obtainMessage();
                        message.obj = tobeDeleteUser;
                        message.what = CODE_DELETE_SUCCESSFUL;
                        mHandler.sendMessage(message);
                    }
                } catch (final HyphenateException e) {
                    if (null != mHandler) {
                        Message message = mHandler.obtainMessage();
                        message.what = CODE_SHOW_DIALOG_FAIL;
                        message.obj = UserInfoUtil.getErrorDescription(e, "删除失败");
                        mHandler.sendMessage(message);
                    }
                }
            }
        });
    }

    /**
     * 联系人同步监听
     */
    private class ContactSyncListener implements ChatHelper.IDataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            if (null != mHandler) {
                if (success) {
                    mHandler.sendEmptyMessage(CODE_SYNC_SUCCESSFUL);
                } else {
                    mHandler.sendEmptyMessage(CODE_SYNC_ERROR);
                }
            }
        }
    }

    /**
     * 黑名单同步监听
     */
    private class BlackListSyncListener implements ChatHelper.IDataSyncListener {

        @Override
        public void onSyncComplete(boolean success) {
            if (null != mHandler && success) {
                mHandler.sendEmptyMessage(CODE_SYNC_SUCCESSFUL);
            }
        }

    }


}
