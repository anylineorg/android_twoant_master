package net.twoant.master.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import net.twoant.master.api.AppConfig;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by S_Y_H on 2016/11/25.
 * 未捕获的Crash 处理类
 */

class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler mInstance;

    private Thread.UncaughtExceptionHandler mHandler;

    /**
     * 是否已经初始化
     */
    private boolean mInitialized;

    /**
     * Crash 绝对目录
     */
    private String mCrashDir;

    /**
     * 版本名
     */
    private String mVersionName;

    /**
     * 版本号
     */
    private int mVersionCode;


    private CrashHandler() {
    }

    /**
     * 获取单例
     * <p>在Application中初始化{@code CrashUtils.getInstance().init(this);}</p>
     *
     * @return 单例
     */
    public static CrashHandler getInstance() {
        if (mInstance == null) {
            synchronized (CrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new CrashHandler();
                } else {
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @return {@code true}: 成功<br>{@code false}: 失败
     */
    public boolean init(Context context) {

        if (mInitialized) {
            return true;
        }
        mInitialized = true;

        if (AppConfig.IS_DEBUG && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                mCrashDir = externalCacheDir.getAbsolutePath() + File.separator + AiSouAppInfoModel.CRASH_FOLDER_NAME + File.separator;
            }
        } else {
            mCrashDir = context.getCacheDir().getAbsolutePath() + File.separator + AiSouAppInfoModel.CRASH_FOLDER_NAME + File.separator;
        }

        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            mVersionName = pi.versionName;
            mVersionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        return mInitialized = true;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        String now = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒", Locale.getDefault()).format(new Date());
        final String fullPath = mCrashDir + now + ".crash";

        if ((AppConfig.IS_DEBUG || !FileUtils.createOrExistsFile(fullPath)) && mHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mHandler.uncaughtException(thread, throwable);

        } else {
            new Thread(new WriteCrashRunnable(fullPath, throwable, getCrashHead(now))).start();


            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LogUtils.e("CrashHandler", "error : ", e);
            }

            AppManager.getAppManager().appExit(AiSouAppInfoModel.getAppContext(), false);
        }
    }

    private static class WriteCrashRunnable implements Runnable {

        private Throwable mThrowable;
        private String mPath;
        private String mDescription;

        private WriteCrashRunnable(String path, Throwable throwable, String description) {
            this.mThrowable = throwable;
            this.mPath = path;
            this.mDescription = description;
        }

        @Override
        public void run() {
            Looper.prepare();
            Toast toast = null;

            try {
                toast = Toast.makeText(AiSouAppInfoModel.getAppContext(), "蚂蚁未响应，即将退出，请重新进入。", Toast.LENGTH_SHORT);
                toast.show();
            } catch (Exception e) {
                try {
                    if (toast != null)
                        toast.cancel();
                } catch (Exception o) {
                    LogUtils.e("CrashHandler - WriteCrashRunnable - ToastUtil.showShort" + o.toString());
                }
            }

            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mPath), "UTF-8")));
                pw.write(mDescription);
                mThrowable.printStackTrace(pw);
                Throwable cause = mThrowable.getCause();
                while (cause != null) {
                    cause.printStackTrace(pw);
                    cause = cause.getCause();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                ToastUtil.cancel();
                CloseUtils.closeIO(pw);
            }
            Looper.loop();
        }
    }

    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private String getCrashHead(String time) {
        return "\n************* Crash Log Head ****************" +
                "\n Crash Date And Time（创建时间）:" + time +
                "\n Device Manufacturer(设备厂商): " + Build.MANUFACTURER +// 设备厂商
                "\n Device Model（设备型号）       : " + Build.MODEL +// 设备型号
                "\n Android Version （系统版本）   : " + Build.VERSION.RELEASE +// 系统版本
                "\n Android SDK （平台版本）       : " + Build.VERSION.SDK_INT +// SDK版本
                "\n App VersionName（版本名）    : " + mVersionName +
                "\n App VersionCode（版本号）    : " + mVersionCode +
                "\n************* Crash Log Head ****************\n\n";
    }
}
