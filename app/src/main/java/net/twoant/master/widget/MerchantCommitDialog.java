package net.twoant.master.widget;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by S_Y_H on 2016/11/28.
 * 商家入驻的提交成功弹窗
 */

public class MerchantCommitDialog extends BaseDialog {

    private IOnConfirmClickListener iOnConfirmClickListener;

    /**
     * @param context   必须是activity实例
     */
    public MerchantCommitDialog(Activity context, @Nullable String hint) {
        super(context, Gravity.CENTER, true);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_dialog_merchant_entered_commit, null);
        mAlertDialog.setView(mView);
        if (hint != null) {
            ((AppCompatTextView) mView.findViewById(net.twoant.master.R.id.tv_hint_info)).setText(hint);
        } else {
            ((AppCompatTextView) mView.findViewById(net.twoant.master.R.id.tv_hint_info)).setText(net.twoant.master.R.string.merchant_wait_audit);
        }
        mView.findViewById(net.twoant.master.R.id.btn_confirm_dialog_merchant_entered_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOnConfirmClickListener != null) {
                    iOnConfirmClickListener.onConfirmClickListener(MerchantCommitDialog.this, v);
                }
            }
        });

    }

    public void setOnClickListener(IOnConfirmClickListener listener) {
        iOnConfirmClickListener = listener;
    }

    public interface IOnConfirmClickListener {
        void onConfirmClickListener(MerchantCommitDialog dialog, View view);
    }
}
