package net.twoant.master.widget;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.twoant.master.R;
import net.twoant.master.common_utils.ToastUtil;

import java.math.BigDecimal;

/**
 * Created by DZY on 2017/2/24.
 * 佛祖保佑   永无BUG
 */

public class RewardIntegralDialog extends BaseDialog implements View.OnClickListener {

    public final EditText etIntegral;
    public OnRewardOnlickListener onRewardOnlickListener;
    private double score = 0;
    private final TextWatcher mTextWatcher;
    private Double mBigDecimal;
    private String mTrim;

    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public RewardIntegralDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View view = LayoutInflater.from(context).inflate(R.layout.zy_integral_reward_dialog,null);
        etIntegral = (EditText) view.findViewById(R.id.et_integral_dialog_reward);
        etIntegral.setOnClickListener(this);
        mTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable edt) {
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                mTrim = arg0.toString().trim();
                if (null== mTrim ||"".equals(mTrim)){
                    return;
                }else {
                    mBigDecimal = new BigDecimal(mTrim).doubleValue();
                }
                etIntegral.removeTextChangedListener(mTextWatcher);
                if ("0".equals(mTrim.substring(0,1))&&mTrim.length()>1){
                    etIntegral.setText(mTrim.substring(1,mTrim.length()));
                    etIntegral.setSelection(etIntegral.getText().length());
                }
                if (mBigDecimal >score){
                    etIntegral.setText((int)score+"");
                    etIntegral.setSelection(etIntegral.getText().length());
                }
                etIntegral.addTextChangedListener(mTextWatcher);
            }
        };
        etIntegral.addTextChangedListener(mTextWatcher);

        view.findViewById(R.id.tv_exit_dialog_reward).setOnClickListener(this);
        view.findViewById(R.id.btn_dialog_reward).setOnClickListener(this);
        mAlertDialog.setView(view);
    }

    public void registerRewardOnlickListener(OnRewardOnlickListener onRewardOnlickListener) {
        this.onRewardOnlickListener = onRewardOnlickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_integral_dialog_reward:
                break;
            case R.id.tv_exit_dialog_reward:
                dismiss();
                break;
            case R.id.btn_dialog_reward:
                String integral = etIntegral.getText().toString().trim();
                if (TextUtils.isEmpty(integral)) {
                    ToastUtil.showLong("积分为空");
                    return;
                }
                if ("0".equals(integral)) {
                    ToastUtil.showLong("打赏积分不能为零");
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

    public void setScore(double score) {
        this.score = score;
    }
}
