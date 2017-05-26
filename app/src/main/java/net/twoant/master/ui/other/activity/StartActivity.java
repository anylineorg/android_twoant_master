package net.twoant.master.ui.other.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hyphenate.chat.EMClient;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouUserBean;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.main.server.WriteDataIntentService;
import net.twoant.master.widget.jpush.HandlePushSever;

import java.io.File;

/**
 * Created by S_Y_H on 2016/11/14.
 * 开屏广告页
 */
public class StartActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 第一次安装的第一次启动
     */
    final static int FIRST_PAGE = 0xA;
    /**
     * 登录成功
     */
    final static int SUCCESSFUL_LOGIN = 0xB;
    /**
     * 清除数据 （备用）
     */
    private final static int CLEAN_STATE = 0xC;

    final static int DEF_FLAG_VALUE = -1;

    /**
     * 广告文件夹
     */
    public final static String IMG_FOLDER = "ad.jpg";
    private final static int CODE_RESULT = 0xA;
    private final static int ALL_TIME = 4000;
    private final static int INTERVAL_TIME = 500;
    private CountDownTimer mCountDownTimer;
    private boolean isFinish;

    /**
     * 剩余时间
     */
    private long mResidue = ALL_TIME;
    private AppCompatTextView mBtnSkip;

    @Override
    protected void doBeforeSetContentView() {
        super.doBeforeSetContentView();
        Window window = getWindow();
        View decorView;
        if (window != null && (decorView = window.getDecorView()) != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_start;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {

        File file = new File(getCacheDir(), IMG_FOLDER);
        AppCompatImageView imageView = (AppCompatImageView) findViewById(net.twoant.master.R.id.iv_ad);
        if (!file.exists() || file.length() <= 0) {
            imageView.setBackgroundResource(net.twoant.master.R.drawable.ic_start_img);
            mResidue = INTERVAL_TIME;
        } else {
            ImageLoader.getImageFromLocation(imageView, file, StartActivity.this, net.twoant.master.R.drawable.ic_start_img);
        }

        mBtnSkip = (AppCompatTextView) findViewById(net.twoant.master.R.id.btn_skip);
        mBtnSkip.setText(String.valueOf("跳过 " + ALL_TIME / 1000 + " 秒"));
        mBtnSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_ad:
                if (!StartActivity.this.isFinish && mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                   // DiscoverActivity.startActivityForResult(StartActivity.this, null, null, CODE_RESULT);
                    //DiscoverActivity.startActivityForResult(StartActivity.this,null,null,CODE_RESULT);
                }
                break;
            case net.twoant.master.R.id.btn_skip:
                if (!StartActivity.this.isFinish && mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                    mCountDownTimer.onFinish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (CODE_RESULT == resultCode) {
            if (null != mCountDownTimer) {
                mCountDownTimer.start();
            } else {
                StartActivity.this.onFinish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFinish && mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void start() {
        if (!isFinish) {

            mCountDownTimer = new CountDownTimer(mResidue, INTERVAL_TIME) {
                @Override
                public void onTick(long l) {
                    mResidue = l;
                    mBtnSkip.setText(String.valueOf("跳过 " + mResidue / 1000 + " 秒"));
                }

                @Override
                public void onFinish() {
                    isFinish = true;
                    StartActivity.this.onFinish();
                }
            }.start();
        } else {
            StartActivity.this.onFinish();
        }
    }

    private void onFinish() {
        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        if (SUCCESSFUL_LOGIN == sharedPreferences.getInt(AiSouAppInfoModel.KEY_FLAG, FIRST_PAGE)) {
            initChat();
        }
        skipActivity(StartActivity.this, sharedPreferences);
//        CheckUpdateService.startService(StartActivity.this);
    }

    static void skipActivity(Context context, SharedPreferences sharedPreferences) {
        if (null == sharedPreferences) {
            sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        }
        switch (sharedPreferences.getInt(AiSouAppInfoModel.KEY_FLAG, FIRST_PAGE)) {
            case FIRST_PAGE:
                NavigationActivity.startActivity(context);
                break;

            case SUCCESSFUL_LOGIN:
                if (sharedPreferences.getBoolean(AiSouAppInfoModel.KEY_IS_SET_J_PUSH_TAG, false)) {
                    HandlePushSever.startRegisterAliasAndTag(context, sharedPreferences.getString(AiSouAppInfoModel.KEY_UID, null), sharedPreferences.getString(AiSouAppInfoModel.KEY_J_PUSH_LAST_TAGS, ""));
                }
                HandlePushSever.startUpdateCrash(context);
                WriteDataIntentService.startUpdateUserData(context);
                WriteDataIntentService.startUpdateAdImg(context);
               LoginActivity.startActivity(context);
             //   MainActivity.startActivity(context);
                break;

            case CLEAN_STATE:

                break;

            default:
                LoginActivity.startActivity(context);
                break;
        }
    }

    /**
     * 初始化环信 数据
     */
    private void initChat() {
        if (ChatHelper.isLoggedIn()) {
            AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
            if (null != instance) {
                instance.setChatLogin(true);
            }
            EMClient.getInstance().groupManager().loadAllGroups();
            EMClient.getInstance().chatManager().loadAllConversations();
        }
    }

    /**
     * 获取本地存储的数据
     */
    public static void initializedUserData(SharedPreferences preferences) {
        if (preferences == null) {
            preferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
        }
        AiSouUserBean aiSouApplication = AiSouAppInfoModel.getInstance().getAiSouUserBean();
        aiSouApplication.setAiSouID(preferences.getString(AiSouAppInfoModel.KEY_UID, null));
        aiSouApplication.setPhone(preferences.getString(AiSouAppInfoModel.KEY_USER_PHONE, null));
        aiSouApplication.setLoginToken(preferences.getString(AiSouAppInfoModel.KEY_TOKEN, ""));
    }

}
