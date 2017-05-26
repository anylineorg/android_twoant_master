package net.twoant.master.app;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.other.activity.SplashActivity;

import java.util.Stack;

/**
 * activity管理
 * Created by S_Y_H on 2016/11/14.16:33
 */

public class AppManager {
    public final static Stack<Activity> ACTIVITIES;
    public final static AppManager INSTANCE;
    public AiSouAppInfoModel mAiSouAppInfoModel;
    public static Stack<Activity> sPayActivityList;

    static {
        ACTIVITIES = new Stack<>();
        INSTANCE = new AppManager();
    }

    private AppManager() {
        mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        return INSTANCE;
    }

    public static int getBackSize() {
        return ACTIVITIES.size();
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        ACTIVITIES.addElement(activity);
    }

    /**
     * 移除账号
     */
    public void onResume(Activity activity) {
        if (mAiSouAppInfoModel == null) {
            mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
            if (mAiSouAppInfoModel == null) {
               /* mAiSouAppInfoModel = AiSouAppInfoModel.getInstance(activity);
                WriteDataIntentService.startUpdateUserData(activity);*/
                //如果被回收的，就重新进入
                activity.startActivity(new Intent(activity, SplashActivity.class));
                activity.finish();
                return;
            }
        }
        if (mAiSouAppInfoModel.getRequestRemoveAccount()) {
            mAiSouAppInfoModel.setRequestRemoveAccount(false);
            MainActivity.accountRemove(activity);
        }
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        try {
            return ACTIVITIES.lastElement();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前Activity的前一个Activity
     */
    public Activity preActivity() {
        int index = ACTIVITIES.size() - 2;
        if (index < 0) {
            return null;
        }
        return ACTIVITIES.get(index);
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = ACTIVITIES.lastElement();
        if (activity != null)
            finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            ACTIVITIES.remove(activity);
            if (!activity.isFinishing())
                activity.finish();
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            ACTIVITIES.remove(activity);
        }
    }

    public static void addPaymentActivity(Activity activity) {
        if (null == activity || activity.isFinishing()) {
            return;
        }
        if (null == sPayActivityList) {
            sPayActivityList = new Stack<>();
        }
        if (!sPayActivityList.contains(activity)) {
            sPayActivityList.push(activity);
        }
    }

    public static void removePaymentActivity(Activity activity) {
        if (null == activity || null == sPayActivityList) {
            return;
        }

        if (sPayActivityList.contains(activity)) {
            sPayActivityList.remove(activity);
        }
    }

    public static void cleanPaymentActivity() {
        if (null == sPayActivityList) {
            return;
        }
        Activity activity;
        for (; !sPayActivityList.isEmpty(); ) {
            activity = sPayActivityList.pop();
            if (null != activity && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : ACTIVITIES) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanAllActivity() {
        ACTIVITIES.clear();
    }

    /**
     * 其他的都finish
     */
    public void cleanAllOtherActivity(Activity activity) {

        for (Activity a : ACTIVITIES) {
            if (a != null && !a.equals(activity) && !a.isFinishing()) {
                a.finish();
            }
        }
        ACTIVITIES.clear();
        ACTIVITIES.add(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = ACTIVITIES.size(); i < size; ++i) {
            Activity activity;
            if (null != (activity = ACTIVITIES.get(i)) && !activity.isFinishing()) {
                activity.finish();
            }
        }
        ACTIVITIES.clear();
    }

    /**
     * 返回到指定的activity
     */
    public void returnToActivity(Class<?> cls) {
        while (ACTIVITIES.size() != 0)
            if (ACTIVITIES.peek().getClass() == cls) {
                break;
            } else {
                finishActivity(ACTIVITIES.peek());
            }
    }


    /**
     * 是否已经打开指定的activity
     */
    public boolean isOpenActivity(Class<?> cls) {
        if (ACTIVITIES != null) {
            for (int i = 0, size = ACTIVITIES.size(); i < size; i++) {
                if (cls == ACTIVITIES.peek().getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 退出应用程序
     *
     * @param context      上下文
     * @param isBackground 是否开开启后台运行
     */
    public void appExit(Context context, Boolean isBackground) {
        try {
            finishAllActivity();
            if (!isBackground) {
                ActivityManager activityMgr = (ActivityManager) context
                        .getSystemService(Context.ACTIVITY_SERVICE);
                if (activityMgr != null)
                    activityMgr.killBackgroundProcesses(context.getPackageName());
            }
        } catch (Exception e) {
            Process.killProcess(Process.myPid());
        } finally {
            // 注意，如果您有后台程序运行，请不要支持此句子
//            if (!isBackground) {
//                System.exit(0);
//            }
        }
    }

}
