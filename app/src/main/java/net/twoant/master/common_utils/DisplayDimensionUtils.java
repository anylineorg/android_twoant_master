package net.twoant.master.common_utils;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import net.twoant.master.app.AiSouAppInfoModel;

/**
 * Created by S_Y_H on 2016/11/22.
 * 获取屏幕尺寸的工具类
 */

public final class DisplayDimensionUtils {

    private static int sWidth = -1;
    private static int sHeight = -1;
    private static float sDensity = -1;

    /**
     * @return 屏幕的宽度
     */
    public static int getScreenWidth() {
        if (sWidth != -1) {
            return sWidth;
        }
        initData();
        return sWidth;
    }

    /**
     * @return 屏幕的高度
     */
    public static int getScreenHeight() {
        if (sHeight != -1) {
            return sHeight;
        }
        initData();
        return sHeight;
    }

    /**
     * dp 尺寸 转成 px尺寸
     *
     * @param dpSize dp尺寸
     * @return px int 尺寸
     */
    public static int dpToPx(int dpSize) {
        if (sDensity == -1) {
            initData();
        }
        return (int) (dpSize * sDensity + 0.5);
    }

    /**
     * px尺寸转成dp尺寸
     *
     * @param pxSize px尺寸
     * @return dp尺寸
     */
    public static int pxToDp(float pxSize) {
        if (sDensity == -1) {
            initData();
        }
        return (int) (pxSize / sDensity + 0.5);
    }

    public static int getPxFromRes(@DimenRes int res) {
        return AiSouAppInfoModel.getAppContext().getResources().getDimensionPixelSize(res);
    }

    public static int getPxFromRes(@DimenRes int res, @NonNull Context context) {
        return context.getResources().getDimensionPixelSize(res);
    }

    private static void initData() {
        WindowManager windowManager = (WindowManager) AiSouAppInfoModel.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        sHeight = displayMetrics.heightPixels;
        sDensity = displayMetrics.density;
        sWidth = displayMetrics.widthPixels;
    }
}
