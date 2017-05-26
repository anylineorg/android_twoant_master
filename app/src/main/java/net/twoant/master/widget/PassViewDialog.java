package net.twoant.master.widget;

import android.app.Activity;

import net.twoant.master.common_utils.MD5Util;

/**
 * Created by DZY on 2016/12/14.
 * 佛祖保佑   永无BUG
 */

public class PassViewDialog extends BaseDialog{

    public final PassView passView;

    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public PassViewDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        passView = new PassView(context);
        passView.setOnExit(new PassView.OnExitButton() {
            @Override
            public void exit() {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.setView(passView);
    }

    public void setDialogOnFinishInput(final OnDialogPasswordInputFinish pass) {
        passView.setOnFinishInput(new PassView.OnPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                pass.inputFinish(MD5Util.getMD5String(strPassword));
            }
        });
    }

    public void onDestroy() {
        mActivity = null;
        if (mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        mAlertDialog = null;
    }

    public void clearn() {
        passView.clearn();
    }

    //用于给密码输入完成添加回掉事
    public interface OnDialogPasswordInputFinish {
        void inputFinish(String strPassword);
    }
    //设置支付框的标题
    public void setTile(String tile){
        passView.setTitle(tile);
    }
    //设置支付框的顶部标题
    public void setBigTile(String bigTile){
        passView.setBigTitle(bigTile);
    }
    //设置支付框的支付钱
    public void setPrice(String price){
        passView.setPrice(price);
    }
}
