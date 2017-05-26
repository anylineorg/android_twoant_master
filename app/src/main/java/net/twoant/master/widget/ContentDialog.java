package net.twoant.master.widget;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by S_Y_H on 2016/11/26.
 * 带填充内容的dialog
 */

public class ContentDialog extends BaseDialog {

    private AppCompatEditText mFirstKeyword;
    private AppCompatEditText mSecondKeyword;
    private AppCompatEditText mThirdKeyword;
    private IOnConfirmListener iOnConfirmListener;

    /**
     * @param context 必须是activity实例
     * @param gravity 选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     */
    public ContentDialog(Activity context, int gravity) {
        super(context, gravity, false);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.yh_dialog_merchant_keyword, null);
        mAlertDialog.setView(mView);
        mFirstKeyword = (AppCompatEditText) mView.findViewById(net.twoant.master.R.id.et_keyword_first);
        mSecondKeyword = (AppCompatEditText) mView.findViewById(net.twoant.master.R.id.et_keyword_second);
        mThirdKeyword = (AppCompatEditText) mView.findViewById(net.twoant.master.R.id.et_keyword_third);
        mView.findViewById(net.twoant.master.R.id.btn_confirm_dialog_merchant_keyword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iOnConfirmListener != null) {
                    StringBuilder builder = getKeywords();
                    iOnConfirmListener.onConfirmListener(builder.toString());
                }
                dismiss();
            }
        });
    }

    @NonNull
    public StringBuilder getKeywords() {
        String first = mFirstKeyword.getText().toString().trim();
        String second = mSecondKeyword.getText().toString().trim();
        String third = mThirdKeyword.getText().toString().trim();

        StringBuilder builder = new StringBuilder(26);

        if (first.length() != 0)
            builder.append(first).append(",");
        if (second.length() != 0)
            builder.append(second).append(",");
        if (third.length() != 0)
            builder.append(third).append(",");
        return builder;
    }


    public interface IOnConfirmListener {
        void onConfirmListener(String data);
    }

    public void setKeywords(String... keys) {
        for (String str : keys) {

            if (TextUtils.isEmpty(mFirstKeyword.getText())) {
                mFirstKeyword.setText(str);
            } else if (TextUtils.isEmpty(mSecondKeyword.getText())) {
                mSecondKeyword.setText(str);
            } else if (TextUtils.isEmpty(mThirdKeyword.getText())) {
                mThirdKeyword.setText(str);
            }
        }
    }

    public void setOnConfirmListener(IOnConfirmListener iOnConfirmListener) {
        this.iOnConfirmListener = iOnConfirmListener == null ? this.iOnConfirmListener : iOnConfirmListener;
    }

    public void onDestroy() {
        mActivity = null;
        iOnConfirmListener = null;
        if (mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        mAlertDialog = null;
    }

}
