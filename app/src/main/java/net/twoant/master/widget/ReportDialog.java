package net.twoant.master.widget;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.twoant.master.common_utils.ToastUtil;

/**
 * Created by DZY on 2017/3/9.
 * 佛祖保佑   永无BUG
 */

public class ReportDialog extends BaseDialog implements View.OnClickListener {

    public final EditText etIntegral;
    public RewardIntegralDialog.OnRewardOnlickListener onRewardOnlickListener;

    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public ReportDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View view = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_report_dialog,null);
        etIntegral = (EditText) view.findViewById(net.twoant.master.R.id.et_integral_dialog_reward);
        etIntegral.setOnClickListener(this);

        etIntegral.addTextChangedListener(new TwoDotTextWatcher());

        view.findViewById(net.twoant.master.R.id.tv_exit_dialog_reward).setOnClickListener(this);
        view.findViewById(net.twoant.master.R.id.btn_dialog_reward).setOnClickListener(this);
        mAlertDialog.setView(view);
    }

    public void registerRewardOnlickListener(RewardIntegralDialog.OnRewardOnlickListener onRewardOnlickListener) {
        this.onRewardOnlickListener = onRewardOnlickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.et_integral_dialog_reward:
                break;
            case net.twoant.master.R.id.tv_exit_dialog_reward:
                dismiss();
                break;
            case net.twoant.master.R.id.btn_dialog_reward:
                String integral = etIntegral.getText().toString().trim();
                if (TextUtils.isEmpty(integral)) {
                    ToastUtil.showLong("内容为空");
                    return;
                }
                onRewardOnlickListener.onClick(integral);
                etIntegral.setText("");
                dismiss();
                break;
        }
    }

    public interface OnRewardOnlickListener{
        void onClick(String integral);
    }
}
