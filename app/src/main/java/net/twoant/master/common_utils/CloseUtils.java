package net.twoant.master.common_utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by S_Y_H on 2016/11/25.
 * 关闭相关工具类
 * 实现了{@link Closeable }接口的类都可以使用该工具类
 *
 */

public final class CloseUtils {

    private CloseUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
