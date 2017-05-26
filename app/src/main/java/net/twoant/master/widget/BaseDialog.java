package net.twoant.master.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import net.twoant.master.R;

/**
 * Created by S_Y_H on 2016/11/26.
 * 弹窗的基类
 */

abstract class BaseDialog {

    protected Activity mActivity;
    protected AlertDialog mAlertDialog;

    /**
     * @param context 必须是activity实例
     * @param gravity 选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     */
    BaseDialog(Activity context, int gravity, boolean fillWidth) {
        this.mActivity = context;
        if (gravity == Gravity.BOTTOM)
            mAlertDialog = new AlertDialog.Builder(context, R.style.ThemeLightDialog_Y).create();
        else
            mAlertDialog = new AlertDialog.Builder(context, R.style.ThemeLightDialog_X).create();
        setWindow(gravity, fillWidth);
    }

    /**
     * 显示dialog
     *
     * @param cancelable             返回可 取消
     * @param canceledOnTouchOutside 触摸 外部可 取消
     */
    public void showDialog(boolean cancelable, boolean canceledOnTouchOutside) {
        mAlertDialog.setCancelable(cancelable);
        mAlertDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
        mAlertDialog.show();
    }

    public boolean isShowing() {
        return null != mAlertDialog && mAlertDialog.isShowing();
    }

    /**
     * 设置取消监听
     *
     * @param listener 取消监听
     */
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mAlertDialog.setOnCancelListener(listener);
    }

    /**
     * 设置关闭监听
     *
     * @param listener 关闭监听
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mAlertDialog.setOnDismissListener(listener);
    }

    /**
     * 关闭弹窗
     */
    public void dismiss() {
        if (null != mAlertDialog && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
    }

    public void onDestroy() {
        mActivity = null;
        mAlertDialog = null;
    }

    /**
     * 取消弹窗
     */
    public void cancel() {
        if (null != mAlertDialog && mAlertDialog.isShowing()) {
            mAlertDialog.cancel();
        }
    }

    /**
     * 设置dialog显示位置
     *
     * @param gravity 显示位置
     */
    private void setWindow(int gravity, boolean fillScreen) {
        Window window = mAlertDialog.getWindow();
        if (window != null) {
            window.setGravity(gravity);
            View decorView = window.getDecorView();
            if (decorView != null) {
                decorView.setPadding(0, 0, 0, 0);
            }
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = fillScreen ? WindowManager.LayoutParams.MATCH_PARENT : WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = gravity;
            window.setAttributes(lp);
        }
    }
}


