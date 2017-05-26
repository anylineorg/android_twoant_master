package net.twoant.master.common_utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.twoant.master.app.AiSouAppInfoModel;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by S_Y_H on 2016/11/16.14:28
 * 图片获取封装类，所有获取图片的地方，都应该调用该类
 */

public final class ImageLoader {

    public final static int STATE_LOADING_IMG = 0xA;
    public final static int STATE_CLOSE_AT_MOBILE = 0xB;
    public final static int STATE_CLOSE_AT_ALL = 0xC;
    private final static String KEY_LOADING = "K_IL_IG";
    private static int sIsLoadingImg;

    static {
        sIsLoadingImg = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES).getInt(KEY_LOADING, STATE_LOADING_IMG);
    }

    public static void setIsLoadingImgState(int state) {
        switch (state) {
            case STATE_CLOSE_AT_ALL:
            case STATE_CLOSE_AT_MOBILE:
            case STATE_LOADING_IMG:
                break;
            default:
                state = STATE_LOADING_IMG;
                break;
        }
        sIsLoadingImg = state;
        SharedPreferences.Editor edit = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES).edit();
        edit.putInt(KEY_LOADING, state);
        edit.apply();
    }

    public static int getIsLoadingImgState() {
        return sIsLoadingImg;
    }

    /**
     * 获取网络图片，获取失败后显示 默认的加载错误图片
     */
    public static void getImageFromNetwork(ImageView view, String url, Context context) {
        getImageFromNetwork(view, url, context, net.twoant.master.R.drawable.ic_def_small);
    }

    public static void getImageFromNetwork(ImageView view, String url) {
        getImageFromNetwork(view, url, AiSouAppInfoModel.getAppContext(), net.twoant.master.R.drawable.ic_def_large);
    }

    public static void getImageFromNetwork(ImageView view, String url, @DrawableRes int errorResId) {
        getImageFromNetwork(view, url, AiSouAppInfoModel.getAppContext(), errorResId);
    }

    public static void onDestroy(Context context) {
        if (context != null) Glide.with(context).onDestroy();
    }

    public static void onDestroy(Fragment context) {
        if (context != null) Glide.with(context).onDestroy();
    }

    public static void onStart(Fragment context) {
        if (context != null) Glide.with(context).resumeRequestsRecursive();
    }

    public static void onStop(Context context) {
        if (context != null) Glide.with(context).onStop();
    }

    public static void onStop(Fragment context) {
        if (context != null) Glide.with(context).pauseRequestsRecursive();
    }

    public static void onResume(Object context) {
        if (context instanceof Activity) {
            Glide.with((Activity) context).resumeRequests();
        } else if (context instanceof Fragment) {
            Glide.with((Fragment) context).resumeRequests();
        } else if (context instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) context).resumeRequests();
        } else if (context instanceof Context) {
            Glide.with((Context) context).resumeRequests();
        }
    }


    public static void onResumeRecursive(Context context) {
        if (context != null) Glide.with(context).resumeRequestsRecursive();
    }

    public static void onPause(Object context) {

        if (context instanceof Activity) {
            Glide.with((Activity) context).pauseRequests();
        } else if (context instanceof Fragment) {
            Glide.with((Fragment) context).pauseRequests();
        } else if (context instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) context).pauseRequests();
        } else if (context instanceof Context) {
            Glide.with((Context) context).pauseRequests();
        }

    }

    public static void getImageFromNetwork(ImageView view, String url, Object o, @DrawableRes int errorResId) {
        if (o instanceof Activity) {
            Glide.with((Activity) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof Fragment) {
            Glide.with((Fragment) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof Context) {
            Glide.with((Context) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        }
    }

    public static void getImageFromNetworkControlImg(ImageView view, String url, Object o, @DrawableRes int errorResId) {
        if (o instanceof Activity) {
            Glide.with((Activity) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof Fragment) {
            Glide.with((Fragment) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        } else if (o instanceof Context) {
            Glide.with((Context) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).crossFade().into(view);
        }
    }

    public static void getImageFromNetworkPlaceholderControlImg(ImageView view, String url, Object o, @DrawableRes int errorResId) {
        if (o instanceof Activity) {
            Glide.with((Activity) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof Fragment) {
            Glide.with((Fragment) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof Context) {
            Glide.with((Context) o).load(canLoadingImg() ? url : "").diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        }
    }


    public static void getImageFromNetworkPlaceholder(ImageView view, String url, Object o, @DrawableRes int errorResId) {
        if (o instanceof Activity) {
            Glide.with((Activity) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof Fragment) {
            Glide.with((Fragment) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof android.app.Fragment) {
            Glide.with((android.app.Fragment) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        } else if (o instanceof Context) {
            Glide.with((Context) o).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(errorResId).placeholder(errorResId).crossFade().into(view);
        }
    }

    public static void onPauseRecursive(Context context) {
        if (context != null) Glide.with(context).pauseRequestsRecursive();
    }

    public static void onStart(Context context) {
        if (context != null) Glide.with(context).onStart();
    }

    @Nullable
    public static File getCacheFile(Context context) {
        if (null == context) return null;
        return Glide.getPhotoCacheDir(context);
    }

    /**
     * @param view       ImageView 实例
     * @param url        网址
     * @param context    上下文
     * @param errorResId 加载失败后的显示图片
     */
    public static void getImageFromNetwork(ImageView view, String url, Context context, @DrawableRes int errorResId) {
        Glide.with(context).load(url).error(errorResId).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(view);
    }


    public static void getImageFromNetwork(ImageView view, String url, Fragment context, @DrawableRes int errorResId) {
        Glide.with(context).load(url).error(errorResId).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(view);
    }

    public static void getImageFromLocation(ImageView view, String filePath) {
        getImageFromLocation(view, filePath, AiSouAppInfoModel.getAppContext(), net.twoant.master.R.drawable.test);
    }

    public static void getImageFromLocation(ImageView view, String filePath, @DrawableRes int errorResId) {
        getImageFromLocation(view, filePath, AiSouAppInfoModel.getAppContext(), errorResId);
    }

    public static void getImageFromLocation(ImageView view, File filePath) {
        getImageFromLocation(view, filePath, AiSouAppInfoModel.getAppContext(), net.twoant.master.R.drawable.ic_def_large);
    }

    public static void getImageFromLocation(ImageView view, File filePath, @DrawableRes int errorResId) {
        getImageFromLocation(view, filePath, AiSouAppInfoModel.getAppContext(), errorResId);
    }

    public static void getImageFromLocation(ImageView view, File file, Context context, @DrawableRes int errorResId) {
        getImageFromLocation(view, file, context, errorResId, errorResId);
    }

    public static void getImageFromLocation(ImageView view, File file, Context context, @DrawableRes int errorResId, @DrawableRes int holder) {
        Glide.with(context).load(file).error(errorResId).crossFade().into(view);

    }

    public static void getImageFromLocation(ImageView view, String file, Context context) {
        getImageFromLocation(view, file, context, net.twoant.master.R.drawable.ic_def_small);
    }

    public static void getImageFromLocation(ImageView view, String filePath, Context context, @DrawableRes int errorResId) {
        getImageFromLocation(view, filePath, context, errorResId, errorResId);
    }

    public static void getImageFromLocation(ImageView view, String filePath, Context context, @DrawableRes int holder, @DrawableRes int errorResId) {
        Glide.with(context).load(filePath).error(errorResId).crossFade().into(view);
    }


    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean canLoadingImg() {
        switch (sIsLoadingImg) {
            case STATE_CLOSE_AT_ALL:
                return false;
            case STATE_CLOSE_AT_MOBILE:
                return !NetworkUtils.isgMobileConnected();
            case STATE_LOADING_IMG:
                return true;
        }
        return true;
    }
}
