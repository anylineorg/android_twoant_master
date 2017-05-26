package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;

/**
 * Created by DZY on 2017/2/15
 * 带填充内容的dialog
 */

public class ErrorPayPasswordDialog extends BaseDialog {
    private CancelCenterDialog.IOnClickListener onClickListener;
    public final TextView tvReTry;

    /**
     * @param context 必须是activity实例
     * @param gravity 选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     */
    public ErrorPayPasswordDialog(final Activity context, int gravity) {
        super(context, gravity, false);
        View mView = LayoutInflater.from(context).inflate(R.layout.zy_dialog_error_password, null);
        tvReTry = (TextView) mView.findViewById(R.id.tv_retry_dialog_error);
//        TextView tvForget = (TextView) mView.findViewById(R.id.tv_forget_dialog_error);
        mView.findViewById(R.id.tv_forget_dialog_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                context.startActivity(new Intent(context, SetPayPasswordActivity.class));
            }
        });
        mAlertDialog.setView(mView);
    }

    public void setReTry(CancelCenterDialog.IOnClickListener clickListener){
        if (onClickListener == null) {
            onClickListener = clickListener;
            tvReTry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null)
                        dismiss();
                        onClickListener.onClickListener(view);
                }
            });
        }
    }

    public void onDestroy() {
        mActivity = null;
        if (mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        mAlertDialog = null;
    }

}
