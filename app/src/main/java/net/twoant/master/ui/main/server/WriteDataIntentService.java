package net.twoant.master.ui.main.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.ui.chat.app.ChatHelper;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.widget.HistoryPopupWindow;
import net.twoant.master.ui.other.activity.LoginActivity;
import net.twoant.master.ui.other.activity.StartActivity;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Create by S_Y_H date 2016/1/13
 */
public class WriteDataIntentService extends IntentService {

    private static final String ACTION_HISTORY = "history";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_UP_AD_IMG = "ad";

    private static final String EXTRA_HISTORY_FILE_NAME = "ex_fn";

    private static final String EXTRA_HISTORY_DATA = "ex_hi";


    public WriteDataIntentService() {
        super("WriteDataIntentService");
    }


    /**
     * 保存用户搜索记录
     */
    public static void startWriteHistoryUserData(Context context, String fileName, ArrayList<String> data) {
        Intent intent = new Intent(context, WriteDataIntentService.class);
        intent.setAction(ACTION_HISTORY);
        intent.putExtra(EXTRA_HISTORY_FILE_NAME, fileName);
        intent.putStringArrayListExtra(EXTRA_HISTORY_DATA, data);
        context.startService(intent);
    }

    /**
     * 用于每次启动后，更新用户数据
     */
    public static void startUpdateUserData(Context context) {
        Intent intent = new Intent(context, WriteDataIntentService.class);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    /**
     * 保存开屏页
     */
    public static void startUpdateAdImg(Context context) {
        Intent intent = new Intent(context, WriteDataIntentService.class);
        intent.setAction(ACTION_UP_AD_IMG);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UP_AD_IMG:
                    updateAdImg();
                    break;
                case ACTION_HISTORY:
                    writeHistoryData(intent.getStringArrayListExtra(EXTRA_HISTORY_DATA), intent.getStringExtra(EXTRA_HISTORY_FILE_NAME));
                    break;
                case ACTION_UPDATE:
                    updateUserInfo();
                    break;
            }
        }
    }

    /**
     * 更新开屏广告图片
     */
    private void updateAdImg() {
        File file = new File(WriteDataIntentService.this.getCacheDir(), StartActivity.IMG_FOLDER);
        if (file.exists() && file.isFile() && file.length() > 0) {
            //每隔6小时更新
            if (!NetworkUtils.isWifiConnected(WriteDataIntentService.this) || System.currentTimeMillis() - file.lastModified() <= 1000 * 60 * 60 * 6) {
                return;
            }
        }

        Response execute = null;
        try {
            execute = OkHttpUtils.get().url(ApiConstants.START_AD_IMG).build().execute();
            if (execute.isSuccessful()) {
                ResponseBody body = execute.body();
                long l = body.contentLength();
                if (l > 0) {
                    byte[] bytes = body.bytes();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    int width = options.outWidth, height = options.outHeight,
                            w = DisplayDimensionUtils.getScreenWidth(),
                            h = DisplayDimensionUtils.getScreenHeight(),
                            scale = 1;
                    while (width / scale > w || height / scale > h) {
                        scale = scale << 1;
                    }
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scale;
                    BufferedOutputStream bufferedOutputStream = null;
                    try {
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream);
                        LogUtils.d("ad img save = " + compress);
                        bufferedOutputStream.flush();
                    } catch (Exception e) {
                        LogUtils.e("ad图片保存失败" + e.toString());
                    } catch (OutOfMemoryError error) {
                        LogUtils.e("ad oom");
                    } finally {
                        CloseUtils.closeIO(bufferedOutputStream);
                    }
                }
            } else {
                LogUtils.e("ad 链接失败" + execute.code());
            }
        } catch (Exception e) {
            LogUtils.e("获取ad Img" + e.toString());
        } finally {
            CloseUtils.closeIO(execute);
        }
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        String token;
        Response execute;
        int mCount = 0;

        while (true) {
            token = AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken();
            if (token == null || token.length() == 0) {
                if (mCount > 60) {
                    return;
                }

                try {
                    ++mCount;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        mCount = 0;
        while (true) {
            try {
                execute = OkHttpUtils.post().url(ApiConstants.LOGIN).addParams("_t", token).build().execute();
                if (execute.isSuccessful()) {
                    DataRow dataRow = DataRow.parseJson(execute.body().string());
                    if (dataRow == null) {
                        return;
                    }
                    boolean result = dataRow.getBoolean("result", false);
                    dataRow = dataRow.getRow("data");
                    if (result && !MainActivity.checkState(WriteDataIntentService.this, dataRow)) {
                        String uid = dataRow.getString("CODE");
                        LoginActivity.saveUserData(dataRow, uid, WriteDataIntentService.this);
                        if (AiSouAppInfoModel.getInstance().isChatLogin()) {
                            EaseUser easeUser = new EaseUser(uid);
                            easeUser.setNickname(dataRow.getStringDef("NM", uid));
                            uid = BaseConfig.getCorrectImageUrl(dataRow.getStringDef("IMG_FILE_PATH", ""));
                            easeUser.setAvatar(uid);
                            easeUser.setAvatarMd5(MD5Util.getMD5ToHex(uid));
                            if (EaseUser.NAME_SAME == UserInfoDiskHelper.getInstance().hasIdenticalUser(easeUser)) {
                                ChatHelper.getUserProfileManager().updateCurrentUserInfo(easeUser);
                            }
                        }
                    } else {
                        AiSouAppInfoModel.getInstance().setRequestRemoveAccount(true);
                    }
                    break;
                } else {

                    if (mCount > 60) {
                        return;
                    }

                    ++mCount;
                    Thread.sleep(60000);
                }
            } catch (Exception e) {
                Log.e("Write", "check _t = " + e.toString());

                if (mCount > 10) {
                    return;
                }

                ++mCount;
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e1) {
                    break;
                }
            }
        }
    }

    /**
     * 保存用户的搜索记录
     */
    private void writeHistoryData(ArrayList<String> sUserHistoryData, String fileName) {

        File file = HistoryPopupWindow.getHistoryFile(WriteDataIntentService.this, fileName, AiSouAppInfoModel.getInstance());
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            return;
        }

        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(sUserHistoryData);
            objectOutputStream.flush();
        } catch (Exception e) {
            LogUtils.e("SearchToolbarHelper =" + e.toString());
        } finally {
            CloseUtils.closeIO(objectOutputStream);
        }


    }
}
