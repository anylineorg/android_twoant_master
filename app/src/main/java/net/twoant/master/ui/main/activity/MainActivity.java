package net.twoant.master.ui.main.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AppManager;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.charge.fragment.ChargeFragment;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.activity.ChatActivity;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.db.InviteMessageDao;
import net.twoant.master.ui.chat.fragment.ChatPageFragment;
import net.twoant.master.ui.chat.runtimepermissions.PermissionsManager;
import net.twoant.master.ui.chat.runtimepermissions.PermissionsResultAction;
import net.twoant.master.ui.convenient.fragment.ConvenientFragment;
import net.twoant.master.ui.main.fragment.HomeFragment;
import net.twoant.master.ui.main.presenter.FragmentControl;
import net.twoant.master.ui.my_center.fragment.MdCenterFragment;
import net.twoant.master.ui.other.activity.LoginActivity;
import net.twoant.master.ui.other.activity.SplashActivity;
import net.twoant.master.widget.entry.DataRow;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION;

/**
 * Created by S_Y_H on 2016/11/18.
 * 首页
 */
public class MainActivity extends ChatBaseActivity implements View.OnClickListener, FragmentControl.IOrderTabProvider {

    private final static String ACTION_START = "MainActivity_a_s";
    private final static String EXTRA_OVERDUE = "overdue";

    private static WeakReference<InputMethodManager> sInputMethodManager;

    private FragmentControl mFragmentControl;

    /**
     * 交流中心 未读消息
     */
    private AppCompatTextView mTvUnreadMsgNumber;
    /**
     * Tab 名字
     */
    AppCompatTextView[] mTabNames = new AppCompatTextView[5];
    /**
     * 首页图标
     */
    private AppCompatImageView mHomeNavigationTab;

    private double mLastKeyDownTime;

    private int mSelectColor;
    private int mUnselectColor;

    private final static String TAG = "MainActivity";

    /**
     * 当前账号 被顶，dialog
     */
    private AlertDialog mConflictDialog;
    /**
     * 账号移除 dialog
     */
    private AlertDialog mAccountRemovedDialog;

    /**
     * 账号移除 dialog
     */
    private AlertDialog mAccountOverdueDialog;

    /**
     * 环信未登录成功 dialog
     */
    private AlertDialog mChatNotLoginDialog;

    /**
     * 账号被顶 dialog是否在显示
     */
    private boolean isConflictDialogShow;

    /**
     * 账号被移除dialog 是否在显示
     */
    private boolean isAccountRemovedDialogShow;

    /**
     * 账号被移除dialog 是否在显示
     */
    private boolean isAccountOverdueDialogShow;

    private BroadcastReceiver broadcastReceiver;
    //
    private LocalBroadcastManager broadcastManager;

    /**
     * 当前账号在别的设备上登录
     */
    public boolean isConflict = false;
    /**
     * 当前账号被删除
     */
    private boolean isCurrentAccountRemoved = false;

    /**
     * 未读事件、通知
     */
    private InviteMessageDao mInviteMessageDao;
    /**
     * 聊天 Fragment
     */
    private ChatPageFragment mChatFragment;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                activity.startActivity(intent);
            else {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } else {
            context.startActivity(intent);
        }
    }

    public static void startActivityNewTask(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setAction(ACTION_START);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            activity.startActivity(intent);
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }


    /**
     * 账号在其他设备登录
     */
    public static void accountConflict(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        context.startActivity(intent);
    }

    /**
     * 账号被移除, 该方法只用在环信上，环信后台把账号删除后，就会用这个方法
     */
    public static void accountRemoved(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.ACCOUNT_CONFLICT, true);
        context.startActivity(intent);
    }


    /**
     * 移除账号，主要用于因为验证 不通过
     */
    public static void accountRemove(Context context) {
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra(EXTRA_OVERDUE, true);
//        context.startActivity(intent);
    }

    /**
     * 判断是否需要重新登录
     *
     * @param dataRow 最外层的
     */
    public static boolean checkState(Context context, DataRow dataRow) {
        return dataRow != null && checkState(context, dataRow.getString("CODE"));
    }

    public static boolean checkState(Context context, String dataRow) {
        if ("403.20".equals(dataRow)) {
            MainActivity.accountRemove(context);
            return true;
        }
        return false;
    }

    /**
     * 更新当前未读的消息数量
     */
    public void updateUnreadLabel(int count) {
        if (count > 0) {
            if (null != mChatFragment) {
                mChatFragment.setHintPoint(0, true);
            }
            mTvUnreadMsgNumber.setText(String.valueOf(count));
            mTvUnreadMsgNumber.setVisibility(View.VISIBLE);
        } else {
            mTvUnreadMsgNumber.setVisibility(View.INVISIBLE);
            if (null != mChatFragment) {
                mChatFragment.setHintPoint(0, false);
            }
        }
    }

    public void updateUnreadLabel() {
        updateUnreadLabel(getUnreadMsgCountTotal());
    }

    /**
     * 获取未读消息数量
     */

    private int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal;
        int chatRoomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        for (EMConversation mConversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (mConversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatRoomUnreadMsgCount = chatRoomUnreadMsgCount + mConversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatRoomUnreadMsgCount;
    }


    // 更新当前联系人界面未读数量
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (null != mChatFragment) {
                    int count = null == mInviteMessageDao ? 0 : mInviteMessageDao.getUnreadMessagesCount();
                    if (count > 0) {
                        mChatFragment.setHintPoint(1, true);
                    } else {
                        mChatFragment.setHintPoint(1, false);

                    }
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
                showConflictDialog();
            } else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
                showAccountRemovedDialog();
            } else if (intent.getBooleanExtra(EXTRA_OVERDUE, false) && !isAccountOverdueDialogShow) {
                showAccountOverdueDialog();
            }
        }
        checkGDLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
        }

        EaseUI instance = EaseUI.getInstance();
        if (null != instance) {
            instance.pushActivity(this);
        }
        try {
            EMClient.getInstance().chatManager().addMessageListener(messageListener);
        } catch (Exception e) {
            startActivity(SplashActivity.class);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        EaseUI instance = EaseUI.getInstance();
        if (null != instance) {
            instance.popActivity(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mConflictDialog != null && mConflictDialog.isShowing()) {
            mConflictDialog.dismiss();
        }
        mConflictDialog = null;

        unregisterBroadcastReceiver();
    }
    /**
     * 移除账号，主要用于因为验证 不通过
     */
    public static void removeAccount(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_OVERDUE, true);
        context.startActivity(intent);
    }

    private class MyContactListener implements EMContactListener {
        @Override
        public void onContactAdded(String username) {
        }

        @Override
        public void onContactDeleted(final String username) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.mCurrentToChatUsername != null &&
                            username.equals(ChatActivity.activityInstance.mCurrentToChatUsername)) {
                        ToastUtil.showShort("好友关系解除");
                        ChatActivity.activityInstance.finish();
                    }
                }
            });
        }

        @Override
        public void onContactInvited(String username, String reason) {
        }

        @Override
        public void onContactAgreed(String username) {
        }

        @Override
        public void onContactRefused(String username) {
        }
    }

    private EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                ChatHelper.getInstance().getEaseNotifier().onNewMsg(message);
            }
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            refreshUIWithMessage();
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    @Override
    protected boolean doBeforeSetContentView(Bundle savedInstanceState) {
        super.doBeforeSetContentView(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }

        //make sure activity will not in background if user is logged into another device or removed
        if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
            LoginActivity.logoutResetData(this);
            return true;
        } else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            LoginActivity.startActivity(MainActivity.this);
            return true;
        }
        return false;
    }

    /**
     * 检查是否获取位置成功
     */
    private void checkGDLocation() {
        if (!AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLocationSuccessfulState()) {
            //启动多次定位
            GDLocationHelper.getInstance().getRepeatedly(null);
        }
    }

    private void initEMClient() {
        requestPermissions();
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
            showConflictDialog();
        } else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
            showAccountRemovedDialog();
        } else if (intent.getBooleanExtra(EXTRA_OVERDUE, false) && !isAccountOverdueDialogShow) {
            showAccountOverdueDialog();
        }

        mInviteMessageDao = new InviteMessageDao(this);

        //register broadcast receiver to receive the change of group from DemoHelper
        registerBroadcastReceiver();

        EMClient.getInstance().contactManager().setContactListener(new MainActivity.MyContactListener());

    }

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                // refresh unread count
                updateUnreadLabel();

                if (mChatFragment != null) {
                    mChatFragment.refreshList(0);
                }
            }
        });
    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateUnreadLabel();
                updateUnreadAddressLable();
                if (mChatFragment != null) {
                    mChatFragment.refreshList(0);
                    mChatFragment.refreshList(1);
                }
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcastReceiver() {
        try {
            broadcastManager.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            //empty
        }
    }

    private void initView() {
        mTabNames[0] = (AppCompatTextView) findViewById(R.id.tv_chat);
        mTabNames[1] = (AppCompatTextView) findViewById(R.id.tv_people);
        mTabNames[2] = null;
        mTabNames[3] = (AppCompatTextView) findViewById(R.id.tv_payment);
        mTabNames[4] = (AppCompatTextView) findViewById(R.id.tv_my_center);

        mTvUnreadMsgNumber = (AppCompatTextView) findViewById(R.id.tv_unread_msg_number);
        mHomeNavigationTab = (AppCompatImageView) findViewById(R.id.iv_home_navigation_tab);


        mSelectColor = ContextCompat.getColor(this, R.color.colorPrimary);
        mUnselectColor = ContextCompat.getColor(this, R.color.subordinationTitleTextColor);

        findViewById(R.id.ll_chat_navigation_tab).setOnClickListener(this);
        findViewById(R.id.ll_info_navigation_tab).setOnClickListener(this);
        findViewById(R.id.ll_payment_navigation_tab).setOnClickListener(this);
        findViewById(R.id.ll_center_navigation_tab).setOnClickListener(this);
        mHomeNavigationTab.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isConflict", isConflict);
        outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
        if (null != mFragmentControl) {
            mFragmentControl.onSaveInstanceState(outState);
        }
    }

    /**
     * 关闭或者打开输入法
     *
     * @param show true显示
     * @param v    获取焦点的view
     */
    public static void closeIME(boolean show, View v) {
        if (v == null) return;

        InputMethodManager inputMethodManager = getInputMethodManager();

        if (!show && inputMethodManager.isActive()) {
            IBinder windowToken = v.getWindowToken();
            if (windowToken == null) return;
            inputMethodManager.hideSoftInputFromWindow(windowToken, FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        } else if (show)
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static InputMethodManager getInputMethodManager() {
        if (sInputMethodManager == null || sInputMethodManager.get() == null)
            sInputMethodManager = new WeakReference<>((InputMethodManager) AiSouAppInfoModel.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE));

        return sInputMethodManager.get();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_chat_navigation_tab:
                if (AiSouAppInfoModel.getInstance().isChatLogin()) {
                    onPageSelected(0);
                } else {
                    showChatNotLogin();
                }
                break;
            case R.id.ll_info_navigation_tab:
                onPageSelected(1);
                break;
            case R.id.iv_home_navigation_tab:
                onPageSelected(2);
                break;
            case R.id.ll_payment_navigation_tab:
                onPageSelected(3);
                break;
            case R.id.ll_center_navigation_tab:
                onPageSelected(4);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long timeMillis = System.currentTimeMillis();
            if (timeMillis - mLastKeyDownTime <= 2000) {
                moveTaskToBack(false);
                //停止高德定位
                GDLocationHelper.getInstance().stopLocation();
                this.finish();
            } else {
                ToastUtil.showShort(R.string.try_again_to_back);
                mLastKeyDownTime = timeMillis;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.yh_activity_main;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        mFragmentControl = new FragmentControl(this, this);
        mFragmentControl.onCreate(savedInstanceState);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                initEMClient();
                checkGDLocation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppManager.getAppManager().cleanAllOtherActivity(this);

    }

    private void onPageSelected(int position) {
        MainActivity.closeIME(false, getCurrentFocus());

        switch (position) {
            case 0:
                Fragment currentFragment = mFragmentControl.getCurrentFragment();
                if (currentFragment instanceof ChatPageFragment) {
                    mChatFragment = (ChatPageFragment) currentFragment;
                    mChatFragment.switchPage(0);
                } else {
                    mFragmentControl.switchFragment(ChatPageFragment.class);
                    currentFragment = mFragmentControl.getCurrentFragment();
                    if (currentFragment instanceof ChatPageFragment) {
                        mChatFragment = (ChatPageFragment) currentFragment;
                    }
                }
                mTabNames[0].setTextColor(mSelectColor);
                MainActivity.this.setStatusBarColor(R.color.colorPrimaryDark);
                break;
            case 1:
//                mFragmentControl.switchFragment(ConvenientFragment.class);
//                mTabNames[1].setTextColor(mSelectColor);
                mFragmentControl.switchFragment(HomeFragment.class);
//                mHomeNavigationTab.setImageResource(R.drawable.navigation_tab_home_select);
                MainActivity.this.setStatusBarColor(R.color.colorPrimaryDark);
                break;
            case 2:
                mFragmentControl.switchFragment(HomeFragment.class);
//                mHomeNavigationTab.setImageResource(R.drawable.navigation_tab_home_select);
                MainActivity.this.setStatusBarColor(R.color.colorPrimaryDark);
                break;
            case 3:
//                mFragmentControl.switchFragment(ChargeFragment.class);
//                mTabNames[3].setTextColor(mSelectColor);
                mFragmentControl.switchFragment(HomeFragment.class);
//                mHomeNavigationTab.setImageResource(R.drawable.navigation_tab_home_select);
                MainActivity.this.setStatusBarColor(R.color.colorPrimaryDark);
                break;
            case 4:
                mFragmentControl.switchFragment(MdCenterFragment.class);
                mTabNames[4].setTextColor(mSelectColor);
                break;
        }

        for (int i = 0; i < mTabNames.length; ++i) {
            if (i != position) {
                if (mTabNames[i] == null) {
                    mHomeNavigationTab.setImageResource(R.drawable.navigation_tab_home);
                } else {
                    mTabNames[i].setTextColor(mUnselectColor);
                }
            }
        }
    }

    @Override
    @NonNull
    public List<FragmentControl.OrderClass> getOrderTabProvider() {
        ArrayList<FragmentControl.OrderClass> arrayList = new ArrayList<>();
        arrayList.add(new FragmentControl.OrderClass(HomeFragment.class, R.id.main_content_fragment).setSelected(true).setSaved(true));
        arrayList.add(new FragmentControl.OrderClass(ChatPageFragment.class, R.id.main_content_fragment).setSaved(true));
        arrayList.add(new FragmentControl.OrderClass(ConvenientFragment.class, R.id.main_content_fragment));
        arrayList.add(new FragmentControl.OrderClass(MdCenterFragment.class, R.id.main_content_fragment).setDetach(true));
        arrayList.add(new FragmentControl.OrderClass(ChargeFragment.class, R.id.main_content_fragment));
        return arrayList;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mFragmentControl) {
            mFragmentControl.onLowMemory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 当前用户在别的设备上登录
     */
    private void showConflictDialog() {
        if (isAccountOverdueDialogShow || isAccountRemovedDialogShow || isConflictDialogShow) {
            return;
        }

        isConflictDialogShow = true;
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(false);
            }
        });
        if (!MainActivity.this.isFinishing()) {
            LoginActivity.resetData(MainActivity.this);
            try {
                if (mConflictDialog == null) {
                    mConflictDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle).create();
                    mConflictDialog.setTitle(R.string.Logoff_notification);
                    mConflictDialog.setMessage(getString(R.string.connect_conflict));
                    mConflictDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mConflictDialog = null;
                            LoginActivity.startActivity(MainActivity.this);
                            MainActivity.this.finish();
                        }
                    });
                }
                mConflictDialog.setCanceledOnTouchOutside(false);
                mConflictDialog.setCancelable(false);

                if (!mConflictDialog.isShowing()) {
                    mConflictDialog.show();
                }

                isConflict = true;
            } catch (Exception e) {
                LogUtils.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 当前用户被移除
     */
    private void showAccountRemovedDialog() {
        if (isAccountOverdueDialogShow || isAccountRemovedDialogShow || isConflictDialogShow) {
            return;
        }
        isAccountRemovedDialogShow = true;
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(false);
            }
        });
        if (!MainActivity.this.isFinishing()) {
            LoginActivity.resetData(MainActivity.this);
            try {
                if (mAccountRemovedDialog == null) {
                    mAccountRemovedDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle).create();
                    mAccountRemovedDialog.setTitle(R.string.Remove_the_notification);
                    mAccountRemovedDialog.setMessage(getString(R.string.em_user_remove));
                    mAccountRemovedDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mAccountRemovedDialog = null;
                            LoginActivity.startActivity(MainActivity.this);
                            MainActivity.this.finish();
                        }
                    });
                }
                mAccountRemovedDialog.setCancelable(false);
                mAccountRemovedDialog.setCanceledOnTouchOutside(false);

                if (!mAccountRemovedDialog.isShowing()) {
                    mAccountRemovedDialog.show();
                }

                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                LogUtils.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }
        }
    }

    /**
     * 登录凭证过期
     */
    private void showAccountOverdueDialog() {
        if (isAccountOverdueDialogShow || isAccountRemovedDialogShow || isConflictDialogShow) {
            return;
        }

        isAccountOverdueDialogShow = true;
        UserInfoDiskHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                EMClient.getInstance().logout(false);
            }
        });
        if (!MainActivity.this.isFinishing()) {
            LoginActivity.resetData(MainActivity.this);
            try {
                if (mAccountOverdueDialog == null) {
                    mAccountOverdueDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle).create();
                    mAccountOverdueDialog.setTitle(R.string.logout_inform);
                    mAccountOverdueDialog.setMessage(getString(R.string.logout_inform_info));
                    mAccountOverdueDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //避免多次移除
                            AiSouAppInfoModel.getInstance().setRequestRemoveAccount(false);
                            mAccountOverdueDialog = null;
                            LoginActivity.startActivity(MainActivity.this);
                            MainActivity.this.finish();
                        }
                    });
                }
                mAccountOverdueDialog.setCancelable(false);
                mAccountOverdueDialog.setCanceledOnTouchOutside(false);

                if (!mAccountOverdueDialog.isShowing()) {
                    mAccountOverdueDialog.show();
                }

                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                LogUtils.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
            }
        }
    }

    /**
     * 显示环信未登录弹窗
     */
    private void showChatNotLogin() {
        if (isAccountOverdueDialogShow || isAccountRemovedDialogShow || isConflictDialogShow) {
            return;
        }
        if (null == mChatNotLoginDialog) {
            mChatNotLoginDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                    .setTitle(AiSouAppInfoModel.getInstance().getChatLoginDescription())
                    .setMessage(R.string.chat_not_login)
                    .setPositiveButton(R.string.chat_to_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginActivity.resetData(MainActivity.this);
                            LoginActivity.startActivity(MainActivity.this);
                        }
                    })
                    .setNegativeButton(R.string.chat_cancel_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }
        mChatNotLoginDialog.show();
    }

}
