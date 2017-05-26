package net.twoant.master.common_utils;

import android.support.annotation.Nullable;

/**
 * Created by DZY on 2016/12/25.
 * 佛祖保佑   永无BUG
 */

public class AESHelper {

    ///** 加密
    @Nullable
    public static String encrypt(String content, String password) {
        String result = null;
        try {
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        result = String.valueOf(Long.parseLong(content) + 99);
                        break;
                    case 1:
                        result = String.valueOf(Long.parseLong(result) * 88);
                        break;
                    case 2:
                        result = String.valueOf(Long.parseLong(result) - 77);
                        break;
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }


    // ** 解密
    @Nullable
    public static String decrypt(String content, String password) {
        String result = null;
        try {
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        result = String.valueOf(Long.parseLong(content) + 77);
                        break;
                    case 1:
                        result = String.valueOf(Long.parseLong(result) / 88);
                        break;
                    case 2:
                        result = String.valueOf(Long.parseLong(result) - 99);
                        break;
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
