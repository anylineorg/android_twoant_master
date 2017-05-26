package net.twoant.master.ui.chat.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;

import com.hyphenate.EMChatRoomChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.ui.EaseGroupRemoveListener;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.CustomListView;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu.ChatInputMenuListener;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView.EaseVoiceRecorderCallback;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;

import net.twoant.master.R;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.activity.ForwardMessageActivity;
import net.twoant.master.ui.chat.activity.GroupSimpleDetailActivity;
import net.twoant.master.ui.chat.activity.ImageGridActivity;
import net.twoant.master.ui.chat.activity.PickAtUserActivity;
import net.twoant.master.ui.chat.activity.UserProfileActivity;
import net.twoant.master.ui.chat.activity.VideoCallActivity;
import net.twoant.master.ui.chat.activity.VoiceCallActivity;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.domain.EmojiconExampleGroupData;
import net.twoant.master.ui.chat.widget.ChatRowVoiceCall;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.widget.takephoto.app.TakePhoto;
import net.twoant.master.widget.takephoto.app.TakePhotoImpl;
import net.twoant.master.widget.takephoto.model.InvokeParam;
import net.twoant.master.widget.takephoto.model.TContextWrap;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;
import net.twoant.master.widget.takephoto.permission.InvokeListener;
import net.twoant.master.widget.takephoto.permission.PermissionManager;
import net.twoant.master.widget.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天
 */
public class EaseChatFragment extends Fragment implements ChatInputMenuListener,
        EaseChatExtendMenu.EaseChatExtendMenuItemClickListener,
        View.OnClickListener,
        EaseChatMessageList.MessageListItemClickListener,
        EMMessageListener,
        TakePhoto.TakeResultListener,
        InvokeListener,
        UserInfoDiskHelper.IOnUpdateFinishListener<EaseUser> {
    protected static final String TAG = "EaseChatFragment";
    private final static int CODE_CLOSE_SELF = 0xAA;

    private TakePhoto mTakePhoto;
    private InvokeParam mInvokeParam;
    /**
     * 当前会话是否是第一次
     */
    private boolean isFirstSend = true;
    private boolean isFirstReceived = true;

    protected int mChatType;
    private EaseUser mEaseUser;
    protected String mToChatUsername;


    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;

    protected EaseChatMessageList mMessageList;
    protected EaseChatInputMenu mChatInputMenu;

    protected EMConversation mConversation;

    protected ClipboardManager mClipboardManager;

    protected File cameraFile;
    protected EaseVoiceRecorderView mVoiceRecorderView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected CustomListView mChatListView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    private final static int PAGE_SIZE = 20;
    protected GroupListener groupListener;
    protected EMMessage mContextMenuMessage;


    private static final int ITEM_VIDEO = 11;
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;


    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    /**
     * 群详情
     */
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;


    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;


    private static final int ITEM_TAKE_PICTURE = 1;
    private static final int ITEM_PICTURE = 2;
//    static final int ITEM_LOCATION = 3;

    private int[] itemStrings = {R.string.attach_take_pic, net.twoant.master.R.string.attach_picture/*, R.string.attach_location*/};
    private int[] itemdrawables = {R.drawable.ease_chat_takepic_selector, net.twoant.master.R.drawable.ease_chat_image_selector/*,
            R.drawable.ease_chat_location_selector*/};
    private int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE/*, ITEM_LOCATION*/};

    private EMChatRoomChangeListener chatRoomChangeListener;
    private boolean isMessageListInited;
    protected EaseTitleBar mEaseTitleBar;
    private AlertDialog mResendMessageDialog;
    private View mContentPopupView;
    private PopupWindow mContentPopupWindow;
    private AppCompatTextView mTvPopupCopy;
    private AppCompatTextView mTvPopupRelay;

    protected InputMethodManager inputMethodManager;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == mActivity) {
            mActivity = getActivity();
        }
        inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        setUpView();
    }

    private void hideSoftKeyboard() {
        if (mActivity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (mActivity.getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mEaseUser = arguments.getParcelable(ChatActivity.EXTRA_USER_DATA);
            mChatType = arguments.getInt(ChatActivity.CHANT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            mToChatUsername = arguments.getString(EaseConstant.EXTRA_USER_BEAN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_chat, container, false);
    }

    protected void initView() {

        View view = getView();
        if (null == view) {
            return;
        }

        mEaseTitleBar = (EaseTitleBar) view.findViewById(R.id.title_bar);
        mVoiceRecorderView = (EaseVoiceRecorderView) view.findViewById(R.id.voice_recorder);
        mMessageList = (EaseChatMessageList) view.findViewById(net.twoant.master.R.id.message_list);
        //如果是群组就显示昵称
        if (mChatType != EaseConstant.CHATTYPE_SINGLE) {
            mMessageList.setShowUserNick(true);
        }

        mChatListView = mMessageList.getListView();

        mChatInputMenu = (EaseChatInputMenu) view.findViewById(R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        mChatInputMenu.init(null);
        mChatInputMenu.setChatInputMenuListener(this);

        mSwipeRefreshLayout = mMessageList.getSwipeRefreshLayout();
        mSwipeRefreshLayout.setColorSchemeResources(R.color.common_top_bar_primary);

        mClipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    protected void setUpView() {
        if (null == mEaseTitleBar) {
            return;
        }

        if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
            if (null != mEaseUser && !mEaseUser.isEmpty()) {//存在传入的信息
                mEaseTitleBar.setTitle(mEaseUser.getNickname());
                if (null == EaseUserUtils.getUserInfo(mToChatUsername)) {//进行保存
                    UserInfoDiskHelper.getInstance().putUserInfo(false, mEaseUser);
                }
            } else if (null != EaseUserUtils.getUserInfo(mToChatUsername)) {
                mEaseUser = EaseUserUtils.getUserInfo(mToChatUsername);
                if (mEaseUser != null) {
                    mEaseTitleBar.setTitle(mEaseUser.getNick());

                    if (mEaseUser.isEmpty()) {
                        //UserInfoDiskHelper.IOnUpdateFinishListener
                        UserInfoDiskHelper.getInstance().saveUser(mEaseUser.getUsername(), this);
                    }
                } else {
                    //UserInfoDiskHelper.IOnUpdateFinishListener
                    UserInfoDiskHelper.getInstance().saveUser(mToChatUsername, this);
                }
            }
            mEaseTitleBar.setRightImageResource(R.drawable.em_contact_list_normal);
        } else {
            mEaseTitleBar.setRightImageResource(R.drawable.ease_to_group_details_normal);
            if (mChatType == EaseConstant.CHATTYPE_GROUP) {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(mToChatUsername);
                if (group != null)
                    mEaseTitleBar.setTitle(group.getGroupName());
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
            }
        }

        if (mChatType != EaseConstant.CHATTYPE_CHATROOM) {
            onConversationInit();
            onMessageListInit();
        }

        mEaseTitleBar.setRightLayoutClickListener(this);

        setRefreshLayoutListener();

        String forward_msg_id = getArguments().getString(ChatActivity.EXTRA_RELAY_MSG);
        if (forward_msg_id != null) {
            forwardMessage(forward_msg_id);
        }

        if (mEaseTitleBar != null)
            mEaseTitleBar.setLeftLayoutClickListener(this);

        ((EaseEmojiconMenu) mChatInputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());

        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            mChatInputMenu.getPrimaryMenu().getEditText().addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && "@".equals(String.valueOf(s.charAt(start)))) {
                        startActivityForResult(new Intent(mActivity, PickAtUserActivity.class).
                                putExtra("groupId", mToChatUsername), REQUEST_CODE_SELECT_AT_USER);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }


    private void registerExtendMenuItem() {
        for (int i = 0; i < itemStrings.length; i++) {
            mChatInputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], this);
        }
        /*mChatInputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
        mChatInputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if (mChatType == Constant.CHATTYPE_SINGLE) {
            mChatInputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
            mChatInputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
        }*/
    }

    /**
     * 初始化 聊天信息
     */
    protected void onConversationInit() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mToChatUsername, EaseCommonUtils.getConversationType(mChatType), true);
        mConversation.markAllMessagesAsRead();
        final List<EMMessage> msgs = mConversation.getAllMessages();
        int msgCount = null != msgs ? msgs.size() : 0;
        if (msgCount < mConversation.getAllMsgCount() && msgCount < PAGE_SIZE) {
            String msgId = null;
            if (null != msgs && !msgs.isEmpty()) {
                msgId = msgs.get(0).getMsgId();
            }
            mConversation.loadMoreMsgFromDB(msgId, PAGE_SIZE - msgCount);
        }
    }

    protected void onMessageListInit() {
        mMessageList.init(mToChatUsername, mChatType, new CustomChatRowProvider());
        //设置 EaseChatMessageList.MessageListItemClickListener
        mMessageList.setItemClickListener(this);
        mChatListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                mChatInputMenu.hideExtendMenuContainer();
                return false;
            }
        });

        isMessageListInited = true;
    }

    //--------------------------- Actionbar 点击事件 start -----------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.left_btn_tool_bar:
                if (EasyUtils.isSingleActivity(getActivity())) {
                    Intent intent = new Intent(mActivity, MainActivity.class);
                    startActivity(intent);
                }
                onBackPressed();
                break;
            case R.id.right_btn_tool_bar:
                if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
                    toUserDetail();
                } else {
                    toGroupDetails();
                }
                break;
            case R.id.tv_relay://转发
                if (mContentPopupWindow.isShowing()) {
                    mContentPopupWindow.dismiss();
                }
                Intent intent = new Intent(mActivity, ForwardMessageActivity.class);
                intent.putExtra("forward_msg_id", mContextMenuMessage.getMsgId());
                startActivity(intent);
                break;

            case R.id.tv_copy://复制
                if (mContentPopupWindow.isShowing()) {
                    mContentPopupWindow.dismiss();
                }
                EMMessageBody body = mContextMenuMessage.getBody();
                if (body instanceof EMTextMessageBody) {
                    mClipboardManager.setPrimaryClip(ClipData.newPlainText(null,
                            ((EMTextMessageBody) body).getMessage()));
                    ToastUtil.showShort(R.string.chat_copy_successful);
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.chat_copy_fail);
                }
                break;

            case R.id.tv_delete://删除
                if (mContentPopupWindow.isShowing()) {
                    mContentPopupWindow.dismiss();
                }
                mConversation.removeMessage(mContextMenuMessage.getMsgId());
                mMessageList.refresh();
                break;

        }
    }
//--------------------------- Actionbar 点击事件 end -----------------------------------------------------------------------

//--------------------------- 菜单点击事件 start -----------------------------------------------------------------------

    @Override
    public void onClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO:
                Intent intent = new Intent(mActivity, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            case ITEM_FILE: //file
                selectFileFromLocal();
                break;
            case ITEM_VOICE_CALL:
                startVoiceCall();
                break;
            case ITEM_VIDEO_CALL:
                startVideoCall();
                break;
            case ITEM_TAKE_PICTURE:
                selectPicFromCamera();
                break;
            case ITEM_PICTURE:
                selectPicFromLocal();
                break;
            default:
                break;
        }
    }
//--------------------------- 菜单点击事件 end -----------------------------------------------------------------------


    //--------------------------- 输入菜单 start -------------------ChatInputMenuListener----------------------------------------------------
    @Override
    public void onSendMessage(String content) {
        sendTextMessage(content);
    }

    @Override
    public void onBigExpressionClicked(EaseEmojicon emojicon) {
        sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
    }

    @Override
    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
        return mVoiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {

            @Override
            public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                sendVoiceMessage(voiceFilePath, voiceTimeLength);
            }
        });
    }

//--------------------------- 输入菜单 end -----------------------------------------------------------------------


//--------------------------- item监听 start -----------------------------EaseChatMessageList.MessageListItemClickListener------------------------------------------

    @Override
    public void onResendClick(final EMMessage message) {
        mContextMenuMessage = message;
        if (null == mResendMessageDialog) {
            mResendMessageDialog = new AlertDialog.Builder(mActivity, R.style.AlertDialogStyle)
                    .setMessage(R.string.confirm_resend)
                    .setPositiveButton(R.string.resend, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            resendMessage(message);
                        }
                    })
                    .setNegativeButton(net.twoant.master.R.string.merchant_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
        mResendMessageDialog.show();
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        mContextMenuMessage = message;

        return false;
    }

    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        mContextMenuMessage = message;
        if (null == mContentPopupView || null == mContentPopupWindow) {
            mContentPopupView = mActivity.getLayoutInflater().inflate(R.layout.yh_popup_window_chat_content, null);
            mTvPopupRelay = (AppCompatTextView) mContentPopupView.findViewById(R.id.tv_relay);
            mTvPopupRelay.setOnClickListener(this);
            mTvPopupCopy = (AppCompatTextView) mContentPopupView.findViewById(R.id.tv_copy);
            mTvPopupCopy.setOnClickListener(this);
            mContentPopupView.findViewById(R.id.tv_delete).setOnClickListener(this);
            mContentPopupWindow = new PopupWindow(mContentPopupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mContentPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, net.twoant.master.R.drawable.yh_shape_popup_window_bg));
            mContentPopupWindow.setOutsideTouchable(false);
        }

        int type = message.getType().ordinal();
        if (type == EMMessage.Type.TXT.ordinal()) {
            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                    || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {

                //只有删除
                if (View.GONE != mTvPopupCopy.getVisibility()) {
                    mTvPopupCopy.setVisibility(View.GONE);
                }
                if (View.GONE != mTvPopupRelay.getVisibility()) {
                    mTvPopupRelay.setVisibility(View.GONE);
                }
            } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                //转发、删除
                if (View.VISIBLE != mTvPopupRelay.getVisibility()) {
                    mTvPopupRelay.setVisibility(View.VISIBLE);
                }
                if (View.GONE != mTvPopupCopy.getVisibility()) {
                    mTvPopupCopy.setVisibility(View.GONE);
                }
            } else {
                //复制、删除、转发
                if (View.VISIBLE != mTvPopupRelay.getVisibility()) {
                    mTvPopupRelay.setVisibility(View.VISIBLE);
                }
                if (View.VISIBLE != mTvPopupCopy.getVisibility()) {
                    mTvPopupCopy.setVisibility(View.VISIBLE);
                }
            }
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            //转发、删除
            if (View.GONE != mTvPopupCopy.getVisibility()) {
                mTvPopupCopy.setVisibility(View.GONE);
            }
            if (View.VISIBLE != mTvPopupRelay.getVisibility()) {
                mTvPopupRelay.setVisibility(View.VISIBLE);
            }
        } else if (type == EMMessage.Type.LOCATION.ordinal() || type == EMMessage.Type.VOICE.ordinal()
                || type == EMMessage.Type.VIDEO.ordinal() || type == EMMessage.Type.FILE.ordinal()) {
            //只有删除
            if (View.GONE != mTvPopupCopy.getVisibility()) {
                mTvPopupCopy.setVisibility(View.GONE);
            }
            if (View.GONE != mTvPopupRelay.getVisibility()) {
                mTvPopupRelay.setVisibility(View.GONE);
            }
        }

        if (ChatHelper.getInstance().getCurrentUserName().equals(message.getFrom())) {
            mContentPopupView.setBackgroundResource(R.drawable.ic_popup_window_right_bg);
        } else {
            mContentPopupView.setBackgroundResource(R.drawable.ic_popup_window_left_bg);
        }
        mContentPopupWindow.showAtLocation(mChatListView, Gravity.NO_GRAVITY, mChatListView.getListX(), mChatListView.getListY());
    }

    @Override
    public void onUserAvatarClick(String username) {
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showShort("获取信息失败");
        } else {
            UserProfileActivity.startActivity(mActivity, username);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        inputAtUsername(username);
    }

//--------------------------- item 监听 end -----------------------------------------------------------------------


//--------------------------- 更新用户信息 监听 end -------------UserInfoDiskHelper.IOnUpdateFinishListener----------------------------------------------------------

    @Override
    public void onUpdateFinishListener(boolean isSuccessful, final EaseUser easeUser) {
        if (null != mActivity && !mActivity.isFinishing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null != mEaseTitleBar && null != easeUser) {
                        mEaseTitleBar.setTitle(easeUser.getNickname());
                    }
                }
            });
        }
    }
//--------------------------- 更新用户信息 监听 end -----------------------------------------------------------------------


    /**
     * 每条会话的提供
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 8;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {
                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    return new ChatRowVoiceCall(mActivity, message, position, adapter);
                }
            }
            return null;
        }
    }

    /**
     * 设置下拉刷新监听
     */
    private void setRefreshLayoutListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mChatListView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
                            List<EMMessage> messages;
                            try {
                                if (mChatType == EaseConstant.CHATTYPE_SINGLE) {
                                    messages = mConversation.loadMoreMsgFromDB(mMessageList.getItem(0).getMsgId(),
                                            PAGE_SIZE);
                                } else {
                                    messages = mConversation.loadMoreMsgFromDB(mMessageList.getItem(0).getMsgId(),
                                            PAGE_SIZE);
                                }
                            } catch (Exception e1) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            if (messages.size() > 0) {
                                mMessageList.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != PAGE_SIZE) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }

                            isloading = false;

                        } else {
                            ToastUtil.showShort(getResources().getString(R.string.no_more_messages));
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String username = data.getStringExtra("username");
                        inputAtUsername(username, false);
                    }
                    break;

                case REQUEST_CODE_CAMERA: // capture new image
                    if (cameraFile != null && cameraFile.exists()) {
                        sendImageMessage(cameraFile.getAbsolutePath());
                    } /*else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                       if (data != null) {
                     Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        sendPicByUri(selectedImage);
                    }
                }
                    } */
                    break;
                case REQUEST_CODE_MAP:
                    // location
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    if (locationAddress != null && !locationAddress.equals("")) {
                        sendLocationMessage(latitude, longitude, locationAddress);
                    } else {
                        ToastUtil.showShort(net.twoant.master.R.string.unable_to_get_loaction);
                    }

                    break;

                case CODE_CLOSE_SELF:
                    if (null == data) {
                        if (null != mActivity) {
                            mActivity.finish();
                            if (mChatType == EaseConstant.CHATTYPE_GROUP) {
                                EaseAtMessageHelper.get().removeAtMeGroup(mToChatUsername);
                                EaseAtMessageHelper.get().cleanToAtUserList();
                            }
                        }
                    } else {
                        String title = data.getStringExtra(GroupSimpleDetailActivity.EXTRA_GROUP_NAME);
                        if (!TextUtils.isEmpty(title) && null != mEaseTitleBar) {
                            mEaseTitleBar.setTitle(title);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited)
            mMessageList.refresh();
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);

        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EaseAtMessageHelper.get().removeAtMeGroup(mToChatUsername);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(mToChatUsername);
        }

        if (chatRoomChangeListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomChangeListener(chatRoomChangeListener);
        }

    }

    public void onBackPressed() {
        if (mChatInputMenu.onBackPressed()) {
            mActivity.finish();
            if (mChatType == EaseConstant.CHATTYPE_GROUP) {
                EaseAtMessageHelper.get().removeAtMeGroup(mToChatUsername);
                EaseAtMessageHelper.get().cleanToAtUserList();
            }
            if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
                EMClient.getInstance().chatroomManager().leaveChatRoom(mToChatUsername);
            }
        }
    }

    // implement methods in EMMessageListener
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username;
            // group message
            if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current mConversation
            if (username.equals(mToChatUsername)) {
                if (isFirstReceived && null != mEaseUser) {
                    isFirstReceived = false;
                    String nickname = message.getStringAttribute(Constant.USER_NAME, null);
                    String avatar = message.getStringAttribute(Constant.USER_AVATAR, null);

                    if ((!TextUtils.isEmpty(nickname) && !TextUtils.equals(mEaseUser.getNickname(), nickname)) ||
                            (!TextUtils.isEmpty(avatar) && !TextUtils.equals(mEaseUser.getAvatarMd5(), avatar))) {
                        mEaseUser.setNickname(nickname);
                        mEaseUser.setAvatarMd5(avatar);
                        UserInfoDiskHelper.getInstance().saveUser(mToChatUsername, this);
                    }
                }
                mMessageList.refreshSelectLast();
                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        //red packet code : 处理红包回执透传消息
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action
           /* if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)) {
                RedPacketUtil.receiveRedPacketAckMessage(message);
                mMessageList.refresh();
            }*/
        }
        //end of red packet code
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }


    /**
     * make a voice call
     */
    protected void startVoiceCall() {
        if (!EMClient.getInstance().isConnected()) {
            ToastUtil.showShort(R.string.not_connect_to_server);
        } else {
            startActivity(new Intent(mActivity, VoiceCallActivity.class).putExtra("username", mToChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            mChatInputMenu.hideExtendMenuContainer();
        }
    }


    /**
     * make a video call
     */
    protected void startVideoCall() {
        if (!EMClient.getInstance().isConnected())
            ToastUtil.showShort(net.twoant.master.R.string.not_connect_to_server);
        else {
            startActivity(new Intent(mActivity, VideoCallActivity.class).putExtra("username", mToChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            mChatInputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(username) ||
                mChatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(username);
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null) {
            username = user.getNick();
        }
        if (autoAddAtSymbol)
            mChatInputMenu.insertText("@" + username + " ");
        else
            mChatInputMenu.insertText(username + " ");
    }


    /**
     * input @
     *
     * @param username
     */
    protected void inputAtUsername(String username) {
        inputAtUsername(username, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
            sendMessage(message);
        }
    }

    /**
     * send @ message, only support group chat message
     *
     * @param content
     */
    @SuppressWarnings("ConstantConditions")
    private void sendAtMessage(String content) {
        if (mChatType != EaseConstant.CHATTYPE_GROUP) {
            EMLog.e(TAG, "only support group chat message");
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content, mToChatUsername);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(mToChatUsername);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message = EaseCommonUtils.createExpressionMessage(mToChatUsername, name, identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, mToChatUsername);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, mToChatUsername);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude, String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, mToChatUsername);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath, int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, mToChatUsername);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath, mToChatUsername);
        sendMessage(message);
    }


    protected void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (isFirstSend) {
            isFirstSend = false;
            EaseUser currentUserInfo = ChatHelper.getUserProfileManager().getCurrentUserInfo();
            String nickname = currentUserInfo.getNickname();
            nickname = TextUtils.isEmpty(nickname) ? currentUserInfo.getUsername() : nickname;
            message.setAttribute(Constant.USER_NAME, nickname);

            String avatar = currentUserInfo.getAvatarMd5();
            avatar = TextUtils.isEmpty(avatar) ? "" : avatar;
            message.setAttribute(Constant.USER_AVATAR, avatar);
        }
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(ChatType.GroupChat);
        } else if (mChatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.setChatType(ChatType.ChatRoom);
        }
        //send message
        EMClient.getInstance().chatManager().sendMessage(message);
        //refresh ui
        if (isMessageListInited) {
            mMessageList.refreshSelectLast();
        }
    }


    public void resendMessage(EMMessage message) {
        message.setStatus(EMMessage.Status.CREATE);
        EMClient.getInstance().chatManager().sendMessage(message);
        mMessageList.refresh();
    }

    //===================================================================================


    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = mActivity.getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtil.showShort(R.string.File_does_not_exist);
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            ToastUtil.showShort(R.string.The_file_is_not_greater_than_10_m);
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            ToastUtil.showShort(R.string.sd_card_does_not_exist);
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        File parentFile = cameraFile.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            ToastUtil.showShort(R.string.chat_photo_parent_fail);
            return;
        }

//        getTakePhoto().onPickFromCapture(Uri.fromFile(cameraFile));

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
//        getTakePhoto().onPickMultipleWithCrop(50, TakePhotoUtils.initCropOptions(1024, 1024, false, true));
        getTakePhoto().onPickMultiple(50);
    }


    /**
     * 到用户详情页
     */
    protected void toUserDetail() {
        UserProfileActivity.startActivity(mActivity, mToChatUsername, UserProfileActivity.TYPE_FRIEND_PAGE);
        /*String msg = getResources().getString(R.string.Whether_to_empty_all_chats);
        new EaseAlertDialog(mActivity, null, msg, null, new AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    EMClient.getInstance().chatManager().deleteConversation(mToChatUsername, true);
                    mMessageList.refresh();
                }
            }
        }, true).show();*/
    }

    /**
     * 打开群详情
     */
    private void toGroupDetails() {
        if (mChatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(mToChatUsername);
            if (group == null) {
                ToastUtil.showShort(net.twoant.master.R.string.gorup_not_found);
                return;
            }
            if (null != mActivity) {
                GroupSimpleDetailActivity.startActivityForResult(this, mActivity, group.getGroupId(), group.getGroupName()
                        , CODE_CLOSE_SELF);
            }
        }
    }

    /**
     * forward message
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String content = ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath = ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath = ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * 群监听
     */
    private class GroupListener extends EaseGroupRemoveListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            mActivity.runOnUiThread(new Runnable() {

                public void run() {
                    if (mToChatUsername.equals(groupId)) {
                        ToastUtil.showShort(R.string.you_are_group);
                        Activity activity = mActivity;
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    if (mToChatUsername.equals(groupId)) {
                        ToastUtil.showShort(net.twoant.master.R.string.the_current_group);
                        Activity activity = mActivity;
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(mActivity, type, mInvokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     */
    public TakePhoto getTakePhoto() {
        if (mTakePhoto == null) {
            mTakePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return mTakePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        final ArrayList<TImage> images = result.getImages();
        if (null != images) {
            for (TImage image : images) {
                sendImageMessage(image.getOriginalPath());
            }
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(net.twoant.master.R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.mInvokeParam = invokeParam;
        }
        return type;
    }

}
