package net.twoant.master.common_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.twoant.master.app.AiSouAppInfoModel;

/**
 * Created by S_Y_H on 2016/11/16.15:28
 * 对SharedPreference文件中的各种类型的数据进行存取操作
 * 需要注意！： 使用apply方法是异步的，因此也没有是否成功的提示。
 * 如果要确保数据一定会存储成功，需要调用方法名后跟Commit的方法，来提交，
 * Commit是同步方法，带有返回值，true为存储成功。
 *
 * 该工具类的默认 存储位置为getDefaultSharedPreferences
 */

public class SharedPreferencesUtils {

    private static SharedPreferences sSharedPreferences;

    /**
     * 初始化该工具类
     */
    private static void initSharedPreferencesUtils() {
        Context context = AiSouAppInfoModel.getAppContext();
        if (sSharedPreferences == null) {
            sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void setSharedIntData(String key, int value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        sSharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * @param key 键
     * @param value 值
     * @return 是否写入成功
     */
    public static boolean setSharedIntDataCommit(String key, int value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getSharedIntData(String key) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.getInt(key, 0);
    }

    public static void setSharedLongData(String key, long value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        sSharedPreferences.edit().putLong(key, value).apply();
    }

    /**
     * @param key 键
     * @param value 值
     * @return 是否写入成功
     */
    public static boolean setSharedLongDataCommit(String key, long value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.edit().putLong(key, value).commit();
    }

    public static long getSharedLongData( String key) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.getLong(key, 0L);
    }

    public static void setSharedFloatData(String key,
                                          float value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        sSharedPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * @param key 键
     * @param value 值
     * @return 是否写入成功
     */
    public static boolean setSharedFloatDataCommit(String key,
                                          float value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.edit().putFloat(key, value).commit();
    }

    public static Float getSharedFloatData(String key) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.getFloat(key, 0f);
    }

    public static void setSharedBooleanData(String key,
                                            boolean value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        sSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * @param key 键
     * @param value 值
     * @return 是否写入成功
     */
    public static boolean setSharedBooleanDataCommit(String key,
                                            boolean value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.edit().putBoolean(key, value).commit();
    }


    public static Boolean getSharedBooleanData(String key) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.getBoolean(key, false);
    }

    public static void setSharedStringData(String key, String value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        sSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * @param key 键
     * @param value 值
     * @return 是否写入成功
     */
    public static boolean setSharedStringDataCommit(String key, String value) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.edit().putString(key, value).commit();
    }

    public static String getSharedStringData(Context context, String key) {
        if (sSharedPreferences == null) {
            initSharedPreferencesUtils();
        }
        return sSharedPreferences.getString(key, "");
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return AiSouAppInfoModel.getAppContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
