package net.twoant.master.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.NotificationCompat;
import android.widget.Button;
import android.widget.ProgressBar;

import com.zhy.http.okhttp.callback.Callback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.ui.other.activity.HintUpdateActivity;
import net.twoant.master.widget.entry.DataRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by S_Y_H on 2016/12/17.9:37
 * 检查app更新服务
 */
public class CheckUpdateService extends Service implements Handler.Callback {

    private final static int STATE_FAIL = 1;
    private final static int STATE_FINISH = 2;
    private final static int STATE_DOWNLOAD = 3;
    private final static int NOTIFICATION_ID = 4;
    private final static int STATE_CONNECT_FAIL = 5;
    private static final int STATE_START_DOWNLOADING = 6;
    private final static int STATE_CHECK = 7;


    private final static String FOLDER_NAME = "app";
    /**
     * 错误代码
     */
    public final static int ERROR_CODE = -1;

    private File mFilePath;

    private int mVersionCode;

    private int mDelayLength = 1;

    private int mProgress = 0;

    private Handler mHandler;

    private String mDownloadUrl;

    private NotificationCompat.Builder mBuilder;

    /**
     * 请求服务器是否有更新
     */
    private boolean isCheckRequest;

    /**
     * 是否在下载文件
     */
    private boolean isDownFile;


    private NotificationManager mNotifyManager;

    private final static String APK_DOWNLOAD_URL = "down_url";

    private HttpConnectedUtils mHttpConnectedUtils;

    public CheckUpdateService() {
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, CheckUpdateService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != mFilePath && !isCheckRequest) {
            mHandler.sendEmptyMessageDelayed(STATE_CHECK, 2000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        boolean isSuccessful = false;
        if (mHandler == null) {
            mHandler = new Handler(this);
        }

        if (mFilePath == null) {

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                File externalCacheDir = getExternalCacheDir();
                if (externalCacheDir != null) {
                    mFilePath = new File(externalCacheDir, FOLDER_NAME);
                }
            } else {
                mFilePath = new File(getCacheDir().getAbsolutePath(), FOLDER_NAME);
            }
        }

        if (mFilePath == null) {
            return;
        }

        File file;
        if (!((file = mFilePath.getParentFile()).exists())) {
            if (!file.mkdirs()) {
                isSuccessful = true;
            }
        }
        File[] files = file.listFiles();
        for (File sub : files) {
            sub.delete();
        }
        if (!isSuccessful)
            mHandler.sendEmptyMessageDelayed(STATE_CHECK, 2000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyDownBinder();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case STATE_FAIL:
                if (mButton != null) {
                    mButton.setText("下载失败");
                }
                break;
            case STATE_DOWNLOAD:
                if (msg.arg1 > mProgress) {
                    mProgress = msg.arg1;

                    if (mProgressBar != null)
                        mProgressBar.setProgress(mProgress);

                    updateProgress(mProgress);
                }
                break;
            case STATE_FINISH:
                this.stopSelf();
                break;
            case STATE_START_DOWNLOADING:
                if (mButton != null) {
                    mButton.setText("下载中...");
                }
                break;
            case STATE_CONNECT_FAIL:
                if (mDelayLength <= 20) {
                    getUpdateMsg();
                } else {
                    stopSelf();
                }
                break;
            case STATE_CHECK:
                getUpdateMsg();
                break;
        }
        return true;
    }

    private ProgressBar mProgressBar;
    private Button mButton;

    public class MyDownBinder extends Binder {


        public void downApp() {
            if (!isCheckRequest && mDownloadUrl != null) {
                showStateBarDownloading();
                downloadFile(mDownloadUrl);
            }
        }

        public int getProgress() {
            return mProgress;
        }

        public int getVersion() {
            return mVersionCode;
        }

        public void setProgressBar(ProgressBar progressBar) {
            CheckUpdateService.this.mProgressBar = progressBar;
        }

        public void setBtn(Button btn) {
            CheckUpdateService.this.mButton = btn;
        }

        public void destroy() {
            mProgressBar = null;
            mButton = null;
        }
    }

    /**
     * 获取当前版本号
     */
    public static int getCurrentVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void getUpdateMsg() {
        if (isCheckRequest) {
            return;
        }

        isCheckRequest = true;


        ArrayMap<String, String> arrayMap = new ArrayMap<>(2);
        arrayMap.put("p", "android");
        if (mHttpConnectedUtils == null) {
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(new HttpConnectedUtils.IOnStartNetworkCallBack() {
                @Override
                public void onBefore(Request request, int id) {
                }

                @Nullable
                @Override
                public HintDialogUtil getHintDialog() {
                    return null;
                }

                @Override
                public void onResponse(String response, int id) {
                    isCheckRequest = false;
                    DataRow row = DataRow.parseJson(response);
                    if (row != null && (row = row.getRow("data")) != null) {
                        mDownloadUrl = row.getString("FILE_URL");
                        String description = row.getString("VERSION_DESCRIPTION");
                        String title = row.getString("VERSION_TITLE");
                        int currentVersion = getCurrentVersion(CheckUpdateService.this);
                        SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
                        int anInt = sharedPreferences.getInt(AiSouAppInfoModel.KEY_HINT_UPDATE, ERROR_CODE);
                        if (anInt < currentVersion && !isDownFile) {
                            if (currentVersion < (mVersionCode = row.getInt("MIN_VERSION"))) {
                                HintUpdateActivity.startActivity(CheckUpdateService.this, true, title, description);
                            } else if (currentVersion < row.getInt("MAX_VERSION")) {
                                HintUpdateActivity.startActivity(CheckUpdateService.this, false, title, description);
                            }
                        }
                    }
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    isCheckRequest = false;
                    mHandler.sendEmptyMessageDelayed(STATE_CONNECT_FAIL, mDelayLength++ * 1000 * 60);
                }
            });
        }
        mHttpConnectedUtils.startNetworkGetString(0, arrayMap, ApiConstants.UPDATE);
    }


    /**
     * 下载app
     *
     * @param url 网址
     */
    private void downloadFile(String url) {
        mHttpConnectedUtils.startNetworkGetFile(0, url, new MyFileCallBack());
    }

    private class MyFileCallBack extends Callback<File> {

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            isDownFile = true;
        }

        @Override
        public File parseNetworkResponse(Response response, int id) throws Exception {
            mHandler.sendEmptyMessage(STATE_START_DOWNLOADING);
            return saveFile(response);
        }


        File saveFile(Response response) throws IOException {
            InputStream is = null;
            byte[] buf = new byte[4096];
            int len;
            FileOutputStream fos = null;
            try {
                is = response.body().byteStream();
                final long total = response.body().contentLength();

                long sum = 0;
                int tempCurrent;
                int tempLast = 0;

                if (!mFilePath.exists()) {
                    mFilePath.mkdirs();
                }

                File file = new File(mFilePath, "aisou.apk");
                Message message;
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    sum += len;
                    fos.write(buf, 0, len);

                    tempCurrent = (int) (((sum * 1.0F) / (total * 1.0F)) * 100);
                    if (tempCurrent > tempLast) {
                        tempLast = tempCurrent;
                        message = mHandler.obtainMessage();
                        message.arg1 = tempCurrent;
                        message.what = STATE_DOWNLOAD;
                        mHandler.sendMessage(message);
                    }
                }
                fos.flush();
                return file;

            } finally {
                response.body().close();

                CloseUtils.closeIO(is);

                CloseUtils.closeIO(fos);
            }
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            mProgress = ERROR_CODE;
            isDownFile = false;
            if (mHandler != null) {
                mHandler.sendEmptyMessage(STATE_FAIL);
            }
            if (mNotifyManager != null) {
                mNotifyManager.cancel(NOTIFICATION_ID);
                mBuilder.setTicker("下载蚂蚁失败").setContentText("下载蚂蚁失败").setContentTitle("下载失败");
                PendingIntent pendingintent = PendingIntent.getActivity(CheckUpdateService.this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder.setContentIntent(pendingintent);
                mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }

        @Override
        public void onResponse(File response, int id) {
            isDownFile = false;

            if (!response.exists()) {
                return;
            }

            if (mNotifyManager != null) {
                mNotifyManager.cancel(NOTIFICATION_ID);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(response), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mHandler.sendEmptyMessage(STATE_FINISH);
        }
    }


    private void showStateBarDownloading() {
        checkOrInit();
        String appName = getString(getApplicationInfo().labelRes);
        mBuilder.setContentTitle(appName).setSmallIcon(getApplicationInfo().icon);
    }

    private void updateProgress(int progress) {
        checkOrInit();
        mBuilder.setContentText(String.format(getString(R.string.update_schedule), progress)).setProgress(100, progress, false);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void checkOrInit() {
        if (mNotifyManager == null) {
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this);
        }
    }


    /**
     * 显示 通知（非强制升级）
     */
    private void showNotification(Context context, String title, String content, String apkUrl) {
        Intent myIntent = new Intent(context, CheckUpdateService.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myIntent.putExtra(APK_DOWNLOAD_URL, apkUrl);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notify = new NotificationCompat.Builder(context)
                .setTicker("发现新版本,点击进行升级")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentIntent(pendingIntent).build();

        notify.flags = android.app.Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notify);
    }

}
