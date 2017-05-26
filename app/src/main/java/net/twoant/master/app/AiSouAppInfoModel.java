package net.twoant.master.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import net.twoant.master.api.AppConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by S_Y_H on 2016/11/13.18:13
 */
public final class AiSouAppInfoModel {

    @SuppressLint("StaticFieldLeak")
    private static Context sApplicationContext;
    private final static AtomicReference<AiSouAppInfoModel> AI_SOU_APP_INFO_MODEL_ATOMIC_REFERENCE = new AtomicReference<>();

    public static int IS_VIP;//1是会员  =0不是会员
    public static String vip_data;
    public static String BUILD_CD;
    public static String SCHOOL_CD;
    public static String RECEIVE_MOBILE;
    public static String RECEIVE_NAME;
    /**
     * 启动标记
     */
    public final static String KEY_FLAG = "K_FG";
    /**
     * DZY
     */
    public static Handler mHandler;
    //uid
    public final static String KEY_UID = "k_U";
    //极光推送 是否有上次未设置成功的tag
    public final static String KEY_IS_SET_J_PUSH_TAG = "k_JP";
    //极光推送 上次为成功设置的tag
    public final static String KEY_J_PUSH_LAST_TAGS = "k_JPT";
    //登录的手机号
    public final static String KEY_USER_PHONE = "k_P";
    //token
    public final static String KEY_TOKEN = "k_T";
    //文件名
    public final static String NAME_SHARED_PREFERENCES = "AS_AS";

    /**
     * 崩溃文件目录
     */
    public final static String CRASH_FOLDER_NAME = "ASC";

    /**
     * 是否忽略了升级
     */
    public final static String KEY_HINT_UPDATE = "k_h_u";

    /**
     * 用户信息
     */
    private AiSouUserBean mAiSouUserBean = new AiSouUserBean();

    /**
     * 位置信息
     */
    private AiSouLocationBean mAiSouLocationBean = new AiSouLocationBean();

    /**
     * 是否有极光推送未设置成功的tag
     */
    private boolean hasJPushFailTags;

    /**
     * 显示 消息的提示点
     */
    private boolean hasMessageHint;

    /**
     * 未设置成功的tags
     */
    private String mJPushTags;

    /**
     * 请求移除当前账号
     */
    private boolean mRequestRemoveAccount;

    private String token;
    /**
     * 环信是否已经登录
     */
    private boolean isChatLogin;
    /**
     * 环信未成功登录的描述信息
     */
    private String mChatLoginDescription;

    private List<Activity> activitys = null;
    private List<Activity> activitysWaitPay = null;

    private AiSouAppInfoModel(Context application) {
        if (application != null && !(application instanceof Application)) {
            sApplicationContext = application.getApplicationContext();
        } else {
            sApplicationContext = application;
        }
    }

    public static AiSouAppInfoModel getInstance(Context application) {
        for (; ; ) {
            AiSouAppInfoModel aiSouAppInfoModel = AI_SOU_APP_INFO_MODEL_ATOMIC_REFERENCE.get();
            if (null != aiSouAppInfoModel) {
                return aiSouAppInfoModel;
            }
            aiSouAppInfoModel = new AiSouAppInfoModel(application);

            if (AI_SOU_APP_INFO_MODEL_ATOMIC_REFERENCE.compareAndSet(null, aiSouAppInfoModel)) {
                return aiSouAppInfoModel;
            }
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 对应application 的onCreate方法
     */
    public void onCreate() {
        //主线程的handler
        mHandler = new Handler(Looper.getMainLooper());
        activitys = new LinkedList<>();
        activitysWaitPay = new LinkedList<>();
    }

    public static AiSouAppInfoModel getInstance() {
        return AI_SOU_APP_INFO_MODEL_ATOMIC_REFERENCE.get();
    }

    public static Context getAppContext() {
        return sApplicationContext;
    }

    public static void initComponents() {
        AiSouSDKModel.initComponents(sApplicationContext, AppConfig.IS_DEBUG);
    }

    @NonNull
    public AiSouUserBean getAiSouUserBean() {
        return this.mAiSouUserBean;
    }

    @NonNull
    public AiSouLocationBean getAiSouLocationBean() {
        return this.mAiSouLocationBean;
    }

    public boolean isHasMessageHint() {
        return this.hasMessageHint;
    }

    public boolean isChatLogin() {
        return this.isChatLogin;
    }

    public String getChatLoginDescription() {
        return this.mChatLoginDescription;
    }

    public void setChatLoginDescription(String des) {
        this.mChatLoginDescription = des;
    }

    public void setChatLogin(boolean chatLogin) {
        this.isChatLogin = chatLogin;
    }

    public void setHasMessageHint(boolean hasMessageHint) {
        this.hasMessageHint = hasMessageHint;
    }

    public void setHasJPushFailTags(boolean has) {
        this.hasJPushFailTags = has;
    }

    public boolean getHasJPushFailTags() {
        return this.hasJPushFailTags;
    }

    public String getJPushTags() {
        return this.mJPushTags;
    }

    public void setJPushTags(String jPushTags) {
        this.mJPushTags = jPushTags;
    }

    public void setRequestRemoveAccount(boolean requestRemoveAccount) {
        this.mRequestRemoveAccount = requestRemoveAccount;
    }

    boolean getRequestRemoveAccount() {
        return this.mRequestRemoveAccount;
    }

    /**
     * DZY
     * 添加Activity到容器中
     * 购买商品提交订单用
     */
    public void addOrderActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        }
    }


    /**
     * DZY
     * 添加Activity到容器中
     * 购买待支付商品提交订单用
     */
    public void addWaitOrderActivity(Activity activity) {
        if (activitysWaitPay != null && activitysWaitPay.size() > 0) {
            if (!activitysWaitPay.contains(activity)) {
                activitysWaitPay.add(activity);
            }
        }
    }

    /**
     * DZY
     * 遍历所有Activity并finish
     * 购买商品提交订单用
     */
    public void exitOrderActivity() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
            activitys.clear();
        }
    }

    /***
     * DZY
     * 遍历所有Activity并finish
     * 购买商品提交订单用
     */
    public void exitWaitOrderActivity() {
        if (activitysWaitPay != null && activitysWaitPay.size() > 0) {
            for (Activity activity : activitysWaitPay) {
                activity.finish();
            }
            activitysWaitPay.clear();
        }
    }

    public String getUID() {
        return mAiSouUserBean.getAiSouID();
    }
}
