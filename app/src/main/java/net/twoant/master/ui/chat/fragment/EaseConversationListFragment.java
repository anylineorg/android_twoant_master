package net.twoant.master.ui.chat.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.util.NetUtils;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.main.activity.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 交流中心中的 消息界面
 */
public class EaseConversationListFragment extends EaseBaseFragment {
    private final static int MSG_REFRESH = 2;
    private EditText query;
    private ImageButton clearSearch;
    private boolean hidden;
    private List<EMConversation> conversationList = new ArrayList<>();
    private EaseConversationList conversationListView;
    private FrameLayout errorItemContainer;
    private TextView errorText;

    private boolean isConflict;

    protected EMConversationListener convListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            refresh();
        }

    };

    public static EaseConversationListFragment newInstance() {
        return new EaseConversationListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutRes() {
        return com.hyphenate.easeui.R.layout.ease_fragment_conversation_list;
    }

    @Override
    protected void initFragmentComponentsData(View view) {
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
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (null != mActivity) {
            mActivity.getMenuInflater().inflate(net.twoant.master.R.menu.em_delete_message, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            boolean deleteMessage = false;
            if (item.getItemId() == net.twoant.master.R.id.delete_message) {
                deleteMessage = true;
            } else if (item.getItemId() == net.twoant.master.R.id.delete_conversation) {
                deleteMessage = false;
            }
            EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
            if (tobeDeleteCons == null) {
                return true;
            }
            if (tobeDeleteCons.getType() == EMConversation.EMConversationType.GroupChat) {
                EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.getUserName());
            }
            try {
                // delete mConversation
                EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.getUserName(), deleteMessage);
                InviteMessageDao inviteMessgeDao = new InviteMessageDao(getActivity());
                inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            refresh();

            // update unread count
            ((MainActivity) getActivity()).updateUnreadLabel();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }


    protected void initView(View view) {
        conversationListView = (EaseConversationList) view.findViewById(com.hyphenate.easeui.R.id.list);
        query = (EditText) view.findViewById(com.hyphenate.easeui.R.id.et_query);
        // button to clear content in search bar
        clearSearch = (ImageButton) view.findViewById(com.hyphenate.easeui.R.id.btn_search_clear);
        errorItemContainer = (FrameLayout) view.findViewById(com.hyphenate.easeui.R.id.fl_error_item);

        View errorView = View.inflate(getActivity(), net.twoant.master.R.layout.yh_item_chat_net_error, null);
        errorItemContainer.addView(errorView);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity != null) {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    ComponentName componentName = intent.resolveActivity(mActivity.getPackageManager());
                    if (componentName != null) {
                        getActivity().startActivity(intent);
                    }
                }
            }
        });
        errorText = (TextView) errorView.findViewById(net.twoant.master.R.id.tv_connect_error_msg);
    }


    protected void setUpView() {
        conversationList.addAll(loadConversationList());
        conversationListView.init(conversationList);

        EMClient.getInstance().addConnectionListener(connectionListener);
        EMClient.getInstance().chatManager().addConversationListener(convListener);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
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

        conversationListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                ChatActivity.startActivity(mActivity, conversation);
            }
        });
    }


    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    conversationListView.refresh();
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    private void onConnectionConnected() {
        errorItemContainer.setVisibility(View.GONE);
    }

    /**
     * disconnected with server
     */
    private void onConnectionDisconnected() {
        errorItemContainer.setVisibility(View.VISIBLE);
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(net.twoant.master.R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(net.twoant.master.R.string.the_current_network);
        }
    }


    /**
     * refresh ui
     */
    public void refresh() {
        if (null != handler && !handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EMClient.getInstance().chatManager().removeConversationListener(convListener);
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }
}
