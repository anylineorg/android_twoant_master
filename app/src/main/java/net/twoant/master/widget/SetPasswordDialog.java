package net.twoant.master.widget;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;

/**
 * Created by DZY on 2016/12/23.
 * 佛祖保佑   永无BUG
 */

public class SetPasswordDialog extends BaseDialog{
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public SetPasswordDialog(final Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View mView = LayoutInflater.from(context).inflate(R.layout.zy_dialog_setpassword,null);
        mView.findViewById(R.id.btn_setpassword_dialogset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,SetPayPasswordActivity.class);
                intent.putExtra("firstSet","is");
                context.startActivity(intent);
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.setView(mView);
    }
}
