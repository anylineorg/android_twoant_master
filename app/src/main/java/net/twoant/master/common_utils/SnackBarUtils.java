package net.twoant.master.common_utils;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2017/2/19.
 * snackBar 工具类
 */

public final class SnackBarUtils {

    public static Snackbar show(@NonNull View view, @NonNull CharSequence text, int duration, @ColorRes int backColor, @ColorRes int textColor
            , @DrawableRes int BtnBg) {
        Snackbar snackbar = Snackbar.make(view, text, duration);
        View snackView = snackbar.getView();
        if (snackView instanceof ViewGroup) {
            ViewGroup snackViewGroup = (ViewGroup) snackView;
            snackViewGroup.setBackgroundColor(ContextCompat.getColor(view.getContext(), backColor));
            int count = snackViewGroup.getChildCount();
            if (count > 0) {
                for (int i = snackViewGroup.getChildCount(); i >= 0; --i) {
                    snackView = snackViewGroup.getChildAt(i);
                    if (snackView instanceof Button) {
                        (snackView).setBackgroundResource(BtnBg);
                    } else if ((snackView = snackViewGroup.getChildAt(i)) instanceof TextView) {
                        ((TextView) snackView).setTextColor(ContextCompat.getColor(view.getContext(), textColor));
                    }
                }
            }
        }
        return snackbar;
    }

    public static void showShort(@NonNull View view, @NonNull CharSequence text) {
        show(view, text, Snackbar.LENGTH_SHORT, R.color.colorPrimary, R.color.whiteTextColor, R.drawable.yh_btn_press_transparent_grey);
    }
}
