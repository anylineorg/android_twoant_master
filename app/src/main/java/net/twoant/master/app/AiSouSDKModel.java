package net.twoant.master.app;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import net.twoant.master.api.AppConfig;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.other.activity.NavigationActivity;
import net.twoant.master.ui.other.activity.SplashActivity;
import net.twoant.master.ui.other.activity.StartActivity;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by S_Y_H on 2016/11/15.
 * 所用到的第三方
 */

final class AiSouSDKModel {

    static void initComponents(Context context, boolean isDebug) {

    /* 进行初始的位置已经保证了只会初始化一次，因此将该处注释
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(context, pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        // 则此application::onCreate 是被service 调用的，直接返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(context.getPackageName())) {
            return;
        }
    */
     //   initCrash(context, isDebug);
        iniUM(context, isDebug);
        initIPush(context, isDebug);
        initChat(context);
        initLogger();
    }

    /**
     * 初始化bugly
     *
     * @param context 上下文
     */
    private static void initCrash(Context context, boolean isDebug) {
        //初始化后台的bug收集
        CrashHandler.getInstance().init(context);

       /* // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        strategy.setAppVersion(appVersion);*/


        // Beta高级设置

        /*
          true表示app启动自动初始化升级模块;
          false不会自动初始化;
          开发者如果担心sdk初始化影响app启动速度，可以设置为false，
          在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
         */
        Beta.autoInit = true;

        /*
         * true表示初始化时自动检查升级;
         * false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
         */
        Beta.autoCheckUpgrade = true;

        /*
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        Beta.upgradeCheckPeriod = 60 * 1000;

        /*
         * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
         */
        Beta.initDelay = 6 * 1000;

        /*
         * 设置通知栏大图标，largeIconId为项目中的图片资源;
         */
        Beta.largeIconId = net.twoant.master.R.mipmap.ic_launcher;

        /*
         * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
         */
        Beta.smallIconId = net.twoant.master.R.mipmap.ic_launcher;

        /*
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = net.twoant.master.R.drawable.ic_banner;

        /*
         * 设置是否显示弹窗中的apk信息
         */
        Beta.canShowApkInfo = true;

        /*
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        /**
         * 已经确认过的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;

        Beta.enableHotfix = true;

        Beta.canNotShowUpgradeActs.add(StartActivity.class);
        Beta.canNotShowUpgradeActs.add(NavigationActivity.class);
        Beta.canNotShowUpgradeActs.add(SplashActivity.class);

        BuglyStrategy buglyStrategy = new BuglyStrategy();
        buglyStrategy.setAppVersion(AppConfig.APP_VERSION);
        buglyStrategy.setAppPackageName("net.twoant.model");

        /***** 统一初始化Bugly产品，包含Beta *****/
        Bugly.init(context, AppConfig.APP_CRASH_ID, isDebug, buglyStrategy);
    }

    /**
     * 初始化logger
     */
    private static void initLogger() {
        LogUtils.logInit();
    }

    /**
     * 初始化友盟
     */
    private static void iniUM(Context context, boolean isDebug) {
        //开启debug模式，方便定位错误，具体错误检查方式可以查看
        // http://dev.umeng.com/social/android/quick-integration的报错必看，
//        Config.DEBUG = isDebug;
//        UMShareAPI.get(context);
//        PlatformConfig.setWeixin("wxc794fc8561b76d39", "22C116B3B20397C796EDBE4ADA874339");
//        PlatformConfig.setQQZone("1105635154", "y4xK6OI9wRLt1YWP");
    }

    /**
     * 初始化 极光推送
     */
    private static void initIPush(Context context, boolean isDebug) {
        JPushInterface.setDebugMode(isDebug);
        JPushInterface.init(context);
    }

    /**
     * 初始化环信
     */
    private static void initChat(Context context) {
        //init demo helper
        ChatHelper.getInstance().init(context);
        //red packet code : 初始化红包上下文，开启日志输出开关
//        RedPacket.getInstance().initContext(sApplicationContext);
        //todo 在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        RedPacket.getInstance().setDebugMode(AiSouAppInfoModel.IS_AES_DEBUG);
    }

    private static String getAppName(Context context, int pID) {
        String processName;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        if (l != null) {

            for (Object aL : l) {
                ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (aL);
                try {
                    if (info.pid == pID) {
                        processName = info.processName;
                        return processName;
                    }
                } catch (Exception e) {
                    LogUtils.d("Process", "Error>> :" + e.toString());
                }
            }
        }
        return null;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


}
