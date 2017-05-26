package net.twoant.master.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */

public class SavePhotoDialog extends BaseDialog {

    private TextView tvContent;
    private IOnClickListener onClickListener;
    /**
     * @param context   必须是activity实例
     * @param gravity   选择其中之一 Gravity.BOTTOM, Gravity.CENTER, Gravity.TOP
     * @param fillWidth
     */
    public SavePhotoDialog(Activity context, int gravity, boolean fillWidth) {
        super(context, gravity, fillWidth);
        View view = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_dialog_sava_titlecenter,null);
        tvContent = (TextView) view.findViewById(net.twoant.master.R.id.tv_content_canceldialog);
        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onClickListener != null)
                        onClickListener.onClickListener(view);
                }
            });
        }
    }

    public void setTitle(String str){
        tvContent.setText(str);
    }

    /**
     * 点击回调接口
     */
    public interface IOnClickListener {
        void onClickListener(View v);
    }
}
