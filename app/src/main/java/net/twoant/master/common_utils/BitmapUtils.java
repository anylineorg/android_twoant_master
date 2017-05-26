package net.twoant.master.common_utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import net.twoant.master.app.AiSouAppInfoModel;

import java.io.File;

/**
 * Created by S_Y_H on 2016/11/16.0:06
 */

public class BitmapUtils {

    /**
     * 应先判断 sd卡不存在，将返回null；
     */
    @Nullable
    public static String getPhotoPath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + context.getString(net.twoant.master.R.string.app_name);
        }
        return null;
    }
/*
    public static Observable<Boolean> savaImage(Context context, Bitmap bitmap, String fileName) {
        return Observable.just(fileName).map(s -> {
            try {
                return savaImageInNewThread(context, bitmap, s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private static boolean savaImageInNewThread(Context context, Bitmap bitmap, String fileName) throws IOException {
        File f = new File(getPhotoPath(context), fileName);
        LogUtils.d(f.getAbsolutePath());
        if (!f.exists()) {
            FileOutputStream out = null;
            f.getParentFile().mkdirs();
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        }
        return true;
    }
    */

    public static int getColorFromRes(@ColorRes int res) {
        return ContextCompat.getColor(AiSouAppInfoModel.getAppContext(), res);
    }
}
