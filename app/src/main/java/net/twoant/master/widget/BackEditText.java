package net.twoant.master.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by DZY on 2017/2/22.
 * 佛祖保佑   永无BUG
 */

public class BackEditText extends EditText {
    public BackEditText (Context context) {
        super(context);
    }

    public BackEditText (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackEditText (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    interface BackListener {
        void back(TextView textView);
    }



    private BackListener listener;

    public void setBackListener(BackListener listener) {
        this.listener = listener;
    }

    @Override

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listener != null) {
                listener.back(this);
            }
        }
        return false;
    }
}