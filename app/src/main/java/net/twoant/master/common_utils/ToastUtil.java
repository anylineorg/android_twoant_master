package net.twoant.master.common_utils;

/**
 * Created by S_Y_H on 2016/11/14.16:22
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.twoant.master.app.AiSouAppInfoModel;

import java.lang.ref.WeakReference;

import static android.R.attr.duration;

/**
 * Toast统一管理类
 */
public class ToastUtil {


    private static WeakReference<Toast> sWeakReference;

    @SuppressLint("ShowToast")
    private static Toast initToast(CharSequence message, int duration, boolean showImage) {

        Toast toast;
        if (sWeakReference == null || (toast = sWeakReference.get()) == null) {
            Context context = AiSouAppInfoModel.getAppContext();
            toast = Toast.makeText(context, message, duration);
            sWeakReference = new WeakReference<>(toast);
            View view = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_toast_view, null);
            AppCompatTextView textView = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_toast_text);
            textView.setText(message);
            toast.setView(view);
            toast.setGravity(Gravity.BOTTOM, 0, DisplayDimensionUtils.getScreenHeight() >> 3);
        } else {
            View view = toast.getView();
            if (view != null && view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int count = viewGroup.getChildCount();

                for (int i = 0; i < count; ++i) {
                    View textView = viewGroup.getChildAt(i);
                    if (textView instanceof AppCompatTextView) {
                        ((AppCompatTextView) textView).setText(message);
                    } else {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        }
        toast.setDuration(duration);
        return toast;
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(CharSequence message) {
        initToast(message, Toast.LENGTH_SHORT, false).show();
    }

    public static Toast getToastShort(CharSequence message) {
        return initToast(message, Toast.LENGTH_SHORT, false);
    }

    public static Toast getToastShortSafety(CharSequence message) {
        try {
            return initToast(message, Toast.LENGTH_SHORT, false);
        } catch (Exception e) {
            return null;
        }
    }

    public static void cancel() {
        Toast toast = sWeakReference.get();
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * 短时间显示Toast
     */
    public static void showShort(int strResId) {
        initToast(AiSouAppInfoModel.getAppContext().getResources().getText(strResId), Toast.LENGTH_SHORT, false).show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(CharSequence message) {
        initToast(message, Toast.LENGTH_LONG, false).show();
    }

    /**
     * 长时间显示Toast
     */
    public static void showLong(int strResId) {
        initToast(AiSouAppInfoModel.getAppContext().getResources().getText(strResId), Toast.LENGTH_LONG, false).show();
    }

    /**
     * 显示Toast
     */
    public static void show(CharSequence message, int duration) {
        initToast(message, duration, false).show();
    }

    /**
     * 自定义显示Toast
     */
    public static void show(Context context, int strResId, int duration) {
        initToast(context.getResources().getText(strResId), duration, false).show();
    }

    /**
     * 显示有image的toast
     */
    public static void showToastWithImg(String content, @DrawableRes int imageResource) {
        Toast toast = initToast(content, duration, false);
        View view = toast.getView();
        if (view != null && view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            View imageView;

            if ((imageView = viewGroup.getChildAt(0)) instanceof AppCompatImageView) {
                AppCompatImageView compatImageView = (AppCompatImageView) imageView;
                compatImageView.setVisibility(View.VISIBLE);
                compatImageView.setImageResource(imageResource);
            }
        }
        toast.show();
    }
}

