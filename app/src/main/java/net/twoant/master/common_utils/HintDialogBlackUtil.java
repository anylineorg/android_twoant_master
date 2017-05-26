package net.twoant.master.common_utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;

import java.lang.ref.SoftReference;

/**
 * Created by S_Y_H on 2016/11/17.15:49
 * 弹窗工具类
 */

public final class HintDialogBlackUtil {
    /**
     * 加载数据对话框
     */
    private Dialog mLoadingDialog;

    private View mView;

    private boolean isInitialized = false;

    private SoftReference<Activity> mSoftReference;
    private Context mApplicationContext;
    private TextView mLoadingText;
    private ProgressBar mProgress;
    private AppCompatImageView mWarmHint;
    private AppCompatImageView mBtnCancel;
    public LinearLayoutCompat llBackGround;

    /**
     * @param context 不能为 Application context
     */
    public HintDialogBlackUtil(Activity context) {
        mApplicationContext = AiSouAppInfoModel.getAppContext();
        mSoftReference = new SoftReference<>(context);
        this.mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_dialog_loading_black, null);
        mLoadingDialog = new Dialog(context, net.twoant.master.R.style.CustomProgressDialog);
        mLoadingDialog.setContentView(mView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public HintDialogBlackUtil showLoading(@StringRes int msg, boolean cancelable, boolean cancelableTouchOutside) {
        showLoading(mApplicationContext.getString(msg), cancelable, cancelableTouchOutside);
        return this;
    }

    /**
     * 显示加载对话框
     *
     * @param msg                    对话框显示内容
     * @param cancelable             对话框是否可以取消
     * @param cancelableTouchOutside 点击对话框以外是否可以取消
     */
    public HintDialogBlackUtil showLoading(CharSequence msg, boolean cancelable, boolean cancelableTouchOutside) {
        if (!isInitialized) {
            isInitialized = true;
            initComponent();
        }

        if (mProgress.getVisibility() != View.VISIBLE)
            this.mProgress.setVisibility(View.VISIBLE);
        if (mWarmHint.getVisibility() != View.GONE)
            this.mWarmHint.setVisibility(View.GONE);
        if (mBtnCancel.getVisibility() != View.GONE)
            this.mBtnCancel.setVisibility(View.GONE);

        mLoadingText.setText(msg);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelableTouchOutside);
        show();
        return this;
    }

    public HintDialogBlackUtil showLoading() {
        showLoading(true, false);
        return this;
    }

    public HintDialogBlackUtil showLoading(@StringRes int stringRes) {
        showLoading(stringRes, true, false);
        return this;
    }

    /**
     * 不显示任何提示的加载中弹窗
     */
    public HintDialogBlackUtil showLoading(boolean cancelable, boolean cancelableTouchOutside) {
        if (!isInitialized) {
            isInitialized = true;
            initComponent();
        }

        if (mProgress.getVisibility() != View.VISIBLE)
            this.mProgress.setVisibility(View.VISIBLE);

        if (mWarmHint.getVisibility() != View.GONE)
            this.mWarmHint.setVisibility(View.GONE);

        if (mLoadingText.getVisibility() != View.GONE) {
            this.mLoadingText.setVisibility(View.GONE);
        }
        if (mBtnCancel.getVisibility() != View.GONE)
            this.mBtnCancel.setVisibility(View.GONE);

        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelableTouchOutside);
        show();
        return this;
    }


    /**
     * 设置取消dialog 的监听，只有在调用cancelDialogForLoading()方法 并且取消了dialog 后才会回调
     */
    public HintDialogBlackUtil setOnCancelListener(final DialogInterface.OnCancelListener listener) {
        mLoadingDialog.setOnCancelListener(listener);
        return this;
    }

    public HintDialogBlackUtil setOnDismissListener(final DialogInterface.OnDismissListener listener) {
        mLoadingDialog.setOnDismissListener(listener);
        return this;
    }

    public Dialog getDialog() {
        return this.mLoadingDialog;
    }

    /**
     * 设置错误提示 默认按返回键可退出，按dialog以外的地方 可退出。
     *
     * @param hintInfo 错误提示
     */
    public HintDialogBlackUtil showError(CharSequence hintInfo) {
        showError(net.twoant.master.R.drawable.dialog_hint_warm, hintInfo, true, true);
        return this;
    }

    public HintDialogBlackUtil showError(CharSequence hintInfo, boolean cancelable, boolean cancelableTouchOutside) {
        showError(net.twoant.master.R.drawable.dialog_hint_warm, hintInfo, cancelable, cancelableTouchOutside);
        return this;
    }

    public HintDialogBlackUtil showError(@StringRes int hintInfo) {
        showError(net.twoant.master.R.drawable.dialog_hint_warm, mApplicationContext.getString(hintInfo), true, true);
        return this;
    }

    private View.OnClickListener mOnClickListener;

    /**
     * 设置错误提示 默认将setCanceledOnTouchOutside设为true
     *
     * @param hintImageRes           显示错误的 图片 资源id
     * @param hintInfo               显示错误提示信息
     * @param cancelable             返回可退出
     * @param cancelableTouchOutside 点击 对话框 外部可退出
     * @return 当前对象
     */
    public HintDialogBlackUtil showError(@DrawableRes int hintImageRes, CharSequence hintInfo, boolean cancelable, boolean cancelableTouchOutside) {
        if (!isInitialized) {
            isInitialized = true;
            initComponent();
        }
        if (mProgress.getVisibility() == View.VISIBLE)
            this.mProgress.setVisibility(View.GONE);
        if (mWarmHint.getVisibility() == View.GONE) {
            this.mWarmHint.setVisibility(View.VISIBLE);
            this.mWarmHint.setImageResource(hintImageRes);
        }
        if (mBtnCancel.getVisibility() != View.VISIBLE)
            this.mBtnCancel.setVisibility(View.VISIBLE);

        if (mOnClickListener == null) {
            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoadingDialog.cancel();
                }
            };
            mBtnCancel.setOnClickListener(mOnClickListener);
        }
        this.mLoadingText.setText(hintInfo);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(cancelableTouchOutside);
        show();
        return this;
    }


    private void initComponent() {
        mLoadingText = (TextView) mView.findViewById(net.twoant.master.R.id.tv_loading_dialog_text);
        mWarmHint = (AppCompatImageView) mView.findViewById(net.twoant.master.R.id.iv_warm_hint);
        mProgress = (ProgressBar) mView.findViewById(net.twoant.master.R.id.progress_bar);
        mBtnCancel = (AppCompatImageView) mView.findViewById(net.twoant.master.R.id.btn_close_dialog);
        llBackGround = (LinearLayoutCompat) mView.findViewById(net.twoant.master.R.id.ll_background);
    }

    /**
     * 取消 对话框
     */
    public void cancelDialogForLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
    }

    /**
     * 关闭 对话框
     */
    public void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 显示dialog
     */
    private void show() {
        Activity activity;
        if (!mLoadingDialog.isShowing() && (activity = mSoftReference.get()) != null && !activity.isFinishing()) {
            mLoadingDialog.show();
        }
    }
}
