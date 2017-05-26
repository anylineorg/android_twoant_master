package net.twoant.master.common_utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.twoant.master.app.AiSouAppInfoModel;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by S_Y_H on 2016/11/17.18:38
 * 网络工具类
 */

public class NetworkUtils {

    private static WeakReference<ConnectivityManager> sWeakReference;

    /**
     * 检查网络是否可用
     *
     * @return true为可用
     */
    public static boolean isNetworkConnected() {
        if (initConnectivityManager()) {
            NetworkInfo localNetworkInfo = sWeakReference.get().getActiveNetworkInfo();
            return (localNetworkInfo != null) && (localNetworkInfo.isAvailable());
        }
        return false;
    }

    /**
     * 检测wifi是否连接
     */
    public static boolean isWifiConnected(Context... contexts) {
        if (initConnectivityManager(contexts)) {
            NetworkInfo networkInfo = sWeakReference.get().getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * 检测移动数据网络是否连接
     * 默认 为 移动数据连接
     */
    public static boolean isgMobileConnected() {
        if (initConnectivityManager()) {
            NetworkInfo networkInfo = sWeakReference.get().getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return true;
    }

    /**
     * 判断网址是否有效
     */
    public static boolean isLinkAvailable(String link) {
        Pattern pattern = Pattern.compile("^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(link);
        return matcher.matches();
    }

    public static String getNetworkStateDescription(Call call, Exception e, String... hint) {
        if (e instanceof SocketTimeoutException) {
            return "连接服务器超时";
        } else if (e instanceof UnknownHostException) {
            return "网络连接失败";
        }
        return hint != null && hint.length > 0 ? hint[0] : "连接服务器失败";
    }

    /**
     * @return true 初始化成功
     */
    public static boolean initConnectivityManager(Context... context) {
        if (sWeakReference == null || sWeakReference.get() == null) {
            if (context != null && context.length > 0) {
                sWeakReference = new WeakReference<>((ConnectivityManager) context[0].getSystemService(Context.CONNECTIVITY_SERVICE));
                return true;
            } else {
                if (null != AiSouAppInfoModel.getAppContext()) {
                    sWeakReference = new WeakReference<>((ConnectivityManager) AiSouAppInfoModel.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE));
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
