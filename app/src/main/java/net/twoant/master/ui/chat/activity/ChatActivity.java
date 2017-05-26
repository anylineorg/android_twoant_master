package net.twoant.master.ui.chat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.util.EasyUtils;

import net.twoant.master.R;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.fragment.EaseChatFragment;
import net.twoant.master.ui.chat.runtimepermissions.PermissionsManager;
import net.twoant.master.ui.main.activity.MainActivity;

/**
 * 聊天界面
 */
public class ChatActivity extends ChatBaseActivity {

    public static ChatActivity activityInstance;

    public final static String USER_ID = "userId";
    public final static String CHANT_TYPE = "chatType";
    public final static String EXTRA_USER_DATA = "e_u_d";
    public final static String EXTRA_RELAY_MSG = "forward_msg_id";

    private EaseChatFragment mChatFragment;
    //当前正在聊天的人
    public String mCurrentToChatUsername;
    private final static String ACTION_START = "ca_as";

    /**
     * 启动单聊
     *
     * @param easeUser 用户信息
     */
    public static void startActivity(Context context, EaseUser easeUser) {
        if (easeUser == null) return;
        if (TextUtils.isEmpty(easeUser.getUsername())) {
            ToastUtil.showShort("不能找到该用户");
            return;
        }
        if (TextUtils.equals(easeUser.getUsername(), EMClient.getInstance().getCurrentUser())) {
            ToastUtil.showShort("不能跟自己聊天");
            return;
        }
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_USER_DATA, easeUser);
        intent.putExtra(Constant.EXTRA_USER_BEAN, easeUser.getUsername());
        context.startActivity(intent);
    }

    public static void startActivity(Context context, EMConversation emConversation) {
        String uid = emConversation.getUserName();
        if (TextUtils.isEmpty(uid) || null == context) {
            ToastUtil.showShort("未找到");
            return;
        }

        if (uid.equals(EMClient.getInstance().getCurrentUser())) {
            ToastUtil.showShort(R.string.Cant_chat_with_yourself);
        } else {
            Intent intent = new Intent(context, ChatActivity.class);
            if (emConversation.isGroup()) {
                if (emConversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                    intent.putExtra(CHANT_TYPE, Constant.CHATTYPE_CHATROOM);
                } else {
                    intent.putExtra(CHANT_TYPE, Constant.CHATTYPE_GROUP);
                }
            }
            intent.putExtra(Constant.EXTRA_USER_BEAN, uid);
            context.startActivity(intent);
        }
    }

    /**
     * 转发消息
     *
     * @param msgId 消息id
     * @param uid
     */
    public static void startActivityRelay(Activity activity, String msgId, String uid) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(USER_ID, uid);
        intent.putExtra(EXTRA_RELAY_MSG, msgId);
        activity.startActivity(intent);
    }

    /**
     * 启动群聊，带返回结果
     *
     * @param resultCode 请求code
     * @param userID     群id
     */
    public static void startGroupActivityForResult(Activity activity, int resultCode, String userID) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(CHANT_TYPE, Constant.CHATTYPE_GROUP);
        intent.putExtra(USER_ID, userID);
        activity.startActivityForResult(intent, resultCode);
    }

    /**
     * 启动群聊
     *
     * @param groupId 群id
     */
    public static void startGroupActivity(Activity activity, String groupId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(CHANT_TYPE, Constant.CHATTYPE_GROUP);
        intent.putExtra(USER_ID, groupId);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.em_activity_chat;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent == null) {
            this.finish();
            return;
        }

        //get user id or group id
        mCurrentToChatUsername = intent.getExtras().getString(Constant.EXTRA_USER_BEAN);
        //use EaseChatFratFragment
        mChatFragment = new EaseChatFragment();
        //pass parameters to chat fragment
        mChatFragment.setArguments(intent.getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, mChatFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityInstance = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra(USER_ID);
        if (mCurrentToChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        mChatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public String getmCurrentToChatUsername() {
        return mCurrentToChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
