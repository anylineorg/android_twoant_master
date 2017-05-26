package net.twoant.master.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */

public class CloseActivityDialog extends BaseDialog {

    private final TextView tvEntery;
    private IOnClickListener onClickListener;
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public CloseActivityDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View view = LayoutInflater.from(context).inflate(R.layout.zy_dialog_closeactivity,null);
        view.findViewById(R.id.tv_canel_closeactivity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvEntery = (TextView) view.findViewById(R.id.tv_enter_closeactivity);
        mAlertDialog.setView(view);
    }

    /**
     * 设置点击回调
     *
     * @param clickListener 监听对象
     */
    public void setOnClickListener(IOnClickListener clickListener) {
        if (onClickListener == null) {
            onClickListener = clickListener;
            tvEntery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null)
                        onClickListener.onClickListener(view);
                }
            });
        }
    }

    /**
     * 点击回调接口
     */
    public interface IOnClickListener {
        void onClickListener(View v);
    }
}
