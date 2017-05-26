package net.twoant.master.common_utils;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import net.twoant.master.api.AppConfig;
import net.twoant.master.app.AiSouLogAdapter;

/**
 * Created by S_Y_H on 2016/11/13.22:38
 * 使用logger封装
 */

public class LogUtils {

    /**
     * 在application调用初始化
     */
    public static void logInit() {
        if (AppConfig.IS_DEBUG) {
            Logger.init(AppConfig.DEBUG_TAG)
                    .methodCount(3)
                    .logLevel(LogLevel.FULL)
                    .methodOffset(2)
                    .logAdapter(new AiSouLogAdapter());
        } else {
            Logger
                    .init(AppConfig.DEBUG_TAG)
                    .methodCount(3)
                    .hideThreadInfo()
                    .logLevel(LogLevel.NONE)
                    .methodOffset(2)
                    .logAdapter(new AiSouLogAdapter());
        }
    }

    public static void d(String tag, String message) {
        if (AppConfig.IS_DEBUG) {
            Logger.d(tag, message);
        }
    }

    public static void d(String message) {
        if (AppConfig.IS_DEBUG) {
            Logger.d(message);
        }
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.e(throwable, message, args);
        }
    }

    public static void e(String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.e(message, args);
        }
    }

    public static void i(String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.i(message, args);
        }
    }

    public static void v(String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.v(message, args);
        }
    }

    public static void w(String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.v(message, args);
        }
    }

    public static void wtf(String message, Object... args) {
        if (AppConfig.IS_DEBUG) {
            Logger.wtf(message, args);
        }
    }

    public static void json(String message) {
        if (AppConfig.IS_DEBUG) {
            Logger.json(message);
        }
    }

    public static void xml(String message) {
        if (AppConfig.IS_DEBUG) {
            Logger.xml(message);
        }
    }
}
