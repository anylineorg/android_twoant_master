package net.twoant.master.widget.jpush;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.AppConfig;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.SharedPreferencesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by S_Y_H on 2016/12/11.
 * 用来处理极光推送的过来的数据
 */

public class HandlePushSever extends IntentService {

    private final static String ACTION_TAGS = "action_tags";
    private final static String ACTION_CRASH = "action_crash";
    private final static String ACTION_CLEAN = "action_clean";

    private final static String ACTION_TAGS_ALIAS = "action_tags_alias";
    private final static String ACTION_RESTORE_ALIA_TAGS = "action_restore";
    private final static String EXTRA_ALIAS = "extra_alias";
    private final static String EXTRA_SAVE_TAGS = "extra_save_tag";
    private final static String EXTRA_TAGS = "extra_tags";

    public HandlePushSever() {
        super("HandlePushSever");
    }


    /**
     * 判断是否存在崩溃日志，如果存在就上传到服务器，并进行删除
     */
    public static void startUpdateCrash(Context context) {
        Intent intent = new Intent(context, HandlePushSever.class);
        intent.setAction(ACTION_CRASH);
        context.startService(intent);
    }

    /**
     * 注册极光tag
     */
    public static void startRegisterTag(Context context, String tags) {
        Intent intent = new Intent(context, HandlePushSever.class);
        intent.setAction(ACTION_TAGS);
        intent.putExtra(EXTRA_SAVE_TAGS, tags);
        context.startService(intent);
    }

    /**
     * 注册极光tag
     */
    public static void startRegisterAliasAndTag(Context context, String alias, String tags) {
        Intent intent = new Intent(context, HandlePushSever.class);
        intent.setAction(ACTION_RESTORE_ALIA_TAGS);
        intent.putExtra(EXTRA_ALIAS, alias);
        intent.putExtra(EXTRA_SAVE_TAGS, tags);
        context.startService(intent);
    }

    /**
     * 清除极光tag
     */
    public static void cleanAliasAndTag(Context context) {
        Intent intent = new Intent(context, HandlePushSever.class);
        intent.setAction(ACTION_CLEAN);
        context.startService(intent);
    }

    /**
     * 注册极光tag alias
     */
    public static void startRegisterAliasAndTag(Context context, String alias, ArrayList<String> tags) {
        Intent intent = new Intent(context, HandlePushSever.class);
        intent.setAction(ACTION_TAGS_ALIAS);
        intent.putStringArrayListExtra(EXTRA_TAGS, tags);
        intent.putExtra(EXTRA_ALIAS, alias);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                //用于app 运行时根据推送进行注册
                case ACTION_TAGS:
                    String tags = intent.getStringExtra(EXTRA_SAVE_TAGS);
                    if (tags != null) {
                        String[] strings = tags.split("-");
                        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
                        JPushSetting.getInstance(HandlePushSever.this).setAliasAndTag(instance.getUID(), Arrays.asList(strings));
                        instance.setJPushTags(null);
                        instance.setHasJPushFailTags(false);
                        final SharedPreferences preferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(AiSouAppInfoModel.KEY_IS_SET_J_PUSH_TAG, false);
                        editor.putString(AiSouAppInfoModel.KEY_J_PUSH_LAST_TAGS, null);
                        editor.apply();
                    }
                    break;
                //用于app 登录时
                case ACTION_TAGS_ALIAS:
                    final ArrayList<String> tagList = intent.getStringArrayListExtra(EXTRA_TAGS);
                    final String alias = intent.getStringExtra(EXTRA_ALIAS);
                    if (!JPushSetting.checkTagAndAlias(alias, tagList)) {
                        JPushSetting.getInstance(HandlePushSever.this).setAliasAndTag(alias, tagList);
                    }
                    break;
                //用于app 刚启动时
                case ACTION_RESTORE_ALIA_TAGS:
                    String restoreAlias = intent.getStringExtra(EXTRA_ALIAS);
                    String restoreTags = intent.getStringExtra(EXTRA_SAVE_TAGS);
                    if (restoreAlias != null && restoreTags != null) {
                        String[] strings = restoreTags.split("-");
                        JPushSetting.getInstance(HandlePushSever.this).setAliasAndTag(restoreAlias, Arrays.asList(strings));
                    } else if (restoreAlias != null) {
                        JPushSetting.getInstance(HandlePushSever.this).setAlias(restoreAlias);
                    } else if (restoreTags != null) {
                        String[] strings = restoreTags.split("-");
                        JPushSetting.getInstance(HandlePushSever.this).setTag(Arrays.asList(strings));
                    }
                    final SharedPreferences preferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(AiSouAppInfoModel.KEY_IS_SET_J_PUSH_TAG, false);
                    editor.putString(AiSouAppInfoModel.KEY_J_PUSH_LAST_TAGS, null);
                    editor.apply();
                    break;
                case ACTION_CRASH:
                    if (!AppConfig.IS_DEBUG) {
                        File file = new File(HandlePushSever.this.getCacheDir(), AiSouAppInfoModel.CRASH_FOLDER_NAME);
                        if (file.isDirectory()) {
                            File[] files = file.listFiles();
                            if (files != null && files.length > 0) {
                                for (File sub : files) {
                                    if (sub.getName().endsWith(".crash")) {
                                        readCrashAndUpdate(sub);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case ACTION_CLEAN:
                    JPushSetting.getInstance(HandlePushSever.this).cleanAliasAndTag();
                    break;
            }

        }
    }


    private void readCrashAndUpdate(final File file) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder(2 << 10);
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp).append("\n\r");
            }
            arrayMap.put("des", stringBuilder.toString());
        } catch (IOException e) {
            LogUtils.e("des = " + e.toString());
        } finally {
            CloseUtils.closeIO(bufferedReader);
        }

        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            arrayMap.put("ver", String.valueOf(pi.versionCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HttpConnectedUtils.getInstance(new HttpConnectedUtils.IOnStartNetworkCallBack() {
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
                LogUtils.e("Crash update onResponse: " + file.delete());
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

        }).startNetworkGetString(0, arrayMap, ApiConstants.ACTIVITY_CRASH);

    }
}
