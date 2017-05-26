package net.twoant.master.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;

/**
 * Created by S_Y_H on 2017/1/1.
 * 活动想去的 填写报名信息 弹窗
 */

public class ActionDetailWriteInfoDialog extends BaseDialog {

    /**
     * @param context   必须是activity实例
     */
    public ActionDetailWriteInfoDialog(Activity context) {
        super(context, Gravity.BOTTOM, true);
    }

    public void setContentView(View view) {
        mAlertDialog.setView(view);
    }

}
