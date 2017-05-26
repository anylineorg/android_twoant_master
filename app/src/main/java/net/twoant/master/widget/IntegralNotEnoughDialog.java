package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.charge.activity.RechargeIntegralActivity;

/**
 * Created by S_Y_H on 2016/12/28.
 * 积分余额不足弹窗
 */

public class IntegralNotEnoughDialog extends BaseDialog {

    /**
     * @param context 必须是activity实例
     * @param gravity 选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     */
    public IntegralNotEnoughDialog(final Activity context, int gravity, boolean fillWidth, String integralBalance, String integralLack) {
        super(context, gravity, fillWidth);
        View inflate = LayoutInflater.from(context).inflate(R.layout.yh_dialog_hint_recharge_integral, null);
        ((AppCompatTextView) inflate.findViewById(R.id.tv_integral_balance)).setText(integralBalance);
        ((AppCompatTextView) inflate.findViewById(R.id.tv_lack_integral)).setText(integralLack);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_know) {
                    mAlertDialog.dismiss();
                } else {
                    mActivity.startActivity(new Intent(mActivity, RechargeIntegralActivity.class));
                    mAlertDialog.dismiss();
                }
            }
        };
        inflate.findViewById(R.id.btn_know).setOnClickListener(onClickListener);
        inflate.findViewById(R.id.btn_recharge).setOnClickListener(onClickListener);
        mAlertDialog.setView(inflate);
    }
}
