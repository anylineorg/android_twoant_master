package net.twoant.master.base_app;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Window;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AppManager;
import net.twoant.master.ui.other.activity.StartActivity;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by S_Y_H on 2016/11/17.9:03
 * 放置本app 默认的配置
 */
public final class BaseConfig {

    /**
     * 获取完整的图片地址链接
     *
     * @param result 服务器返回地址
     * @return 正确的图片链接
     */
    public static String getCorrectImageUrl(String result) {
        if (TextUtils.isEmpty(result)) {
            return "";
        }

        if (result.startsWith("http:")) {
            return result;
        }

        return  result;//ApiConstants.IMAGE_HEADER_URL + result;
    }

    /**
     * 从json中获取服务器返回的图片地址
     *
     * @param json response
     * @return null 为上传不成功
     */
    @Nullable
    public static String getImageUrlForJson(String json) {
        DataRow msg = DataRow.parseJson(json);
        if (null != msg && msg.getBoolean("result", false) && null != (msg = msg.getRow("data"))) {
            return msg.getString("URL");
        }
        return null;
    }

    static int getDefStateBarColor() {
        return net.twoant.master.R.color.colorPrimaryDark;
    }

    public static AppManager sAppManager = AppManager.getAppManager();

    //----------------以下对应Activity的生命周期-----------------------------

    /**
     * 该方法在setContentView之前调用
     *
     * @param activity 实例
     */
    public static void onCreate(AppCompatActivity activity) {
        // 把activity放到application栈中管理
        sAppManager.addActivity(activity);
        activity.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (AiSouAppInfoModel.getInstance() == null) {
            AiSouAppInfoModel.getInstance(activity);
            StartActivity.initializedUserData(null);
        }

    }

    static void onStart(Activity activity) {

    }

    static void onResume(Activity activity) {
        sAppManager.onResume(activity);
    }

    static void onPause(Activity activity) {
    }

    static void onStop(Activity activity) {

    }

    public static void onDestroy(Activity activity) {
        sAppManager.removeActivity(activity);
    }

    static void removeStack(Activity activity) {
        sAppManager.removeActivity(activity);
    }

    static void onRestart(Activity activity) {

    }

    public static void checkState(Intent intent, String requestAction) {
        String action;
        if (intent == null || (action = intent.getAction()) == null || !requestAction.equals(action))
            throw new IllegalArgumentException("only static method can start");
    }
}
