package net.twoant.master.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.AppCompatEditText;
import android.view.Gravity;
import android.view.View;

/**
 * Created by S_Y_H on 2017/2/26.
 * 添加好友、群 的附近消息框
 */

public class ApplyAppendInfoDialog extends BaseDialog {

    private final View mAppendView;
    private View.OnClickListener mOnClickListener;
    private ApplyAppendInfoDialog.IOnClickListener iOnClickListener;
    private final AppCompatEditText mEditInfo;

    /**
     * @param context 必须是activity实例
     */
    @SuppressLint("InflateParams")
    public ApplyAppendInfoDialog(Activity context) {
        super(context, Gravity.CENTER, true);

        mAppendView = context.getLayoutInflater().inflate(net.twoant.master.R.layout.yh_write_append_info, null);
        mAppendView.setBackgroundResource(net.twoant.master.R.drawable.yh_white_bg_arc);
        mAlertDialog.setView(mAppendView);
        mEditInfo = (AppCompatEditText) mAppendView.findViewById(net.twoant.master.R.id.et_info);
        mEditInfo.setText(context.getString(net.twoant.master.R.string.info_friendly_hint));
        mEditInfo.setSelection(mEditInfo.length());
    }

    public AppCompatEditText getEditInfo() {
        return this.mEditInfo;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mAppendView.findViewById(net.twoant.master.R.id.btn_cancel).setOnClickListener(onClickListener);
        mAppendView.findViewById(net.twoant.master.R.id.btn_send).setOnClickListener(onClickListener);
    }

    public void setOnClickListener(ApplyAppendInfoDialog.IOnClickListener iOnClickListener) {
        this.iOnClickListener = iOnClickListener;
        if (null == mOnClickListener) {
            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case net.twoant.master.R.id.btn_cancel:
                            if (null != ApplyAppendInfoDialog.this.iOnClickListener) {
                                ApplyAppendInfoDialog.this.iOnClickListener.onCancelClickListener(v);
                            } else {
                                ApplyAppendInfoDialog.this.dismiss();
                            }
                            break;
                        case net.twoant.master.R.id.btn_send:
                            if (null != ApplyAppendInfoDialog.this.iOnClickListener) {
                                ApplyAppendInfoDialog.this.iOnClickListener.onConfirmClickListener(v);
                            } else {
                                ApplyAppendInfoDialog.this.dismiss();
                            }
                            break;
                    }
                }
            };
        }
        setOnClickListener(mOnClickListener);
    }

    public interface IOnClickListener {

        void onCancelClickListener(View view);

        void onConfirmClickListener(View view);
    }
}
