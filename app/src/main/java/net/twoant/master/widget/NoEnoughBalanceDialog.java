package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.ui.charge.activity.GetCashActivity;

/**
 * Created by DZY on 2016/12/23.
 * 佛祖保佑   永无BUG
 */

public class NoEnoughBalanceDialog extends BaseDialog{
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public NoEnoughBalanceDialog(final Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View mView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_setpassword,null);
        TextView titleName = (TextView) mView.findViewById(net.twoant.master.R.id.tv_title_dialogset);
        titleName.setText("所剩余额已不足");
        TextView tvSet = (TextView) mView.findViewById(net.twoant.master.R.id.btn_setpassword_dialogset);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,GetCashActivity.class));
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.setView(mView);
    }
}
