package net.twoant.master.widget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by DZY on 2017/2/25.
 * 佛祖保佑   永无BUG
 */

public class TwoDotTextWatcher implements TextWatcher {
    public void afterTextChanged(Editable edt) {
        String temp = edt.toString();
        int posDot = temp.indexOf(".");
        if (posDot <= 0) return;
        if (temp.length() - posDot - 1 > 2) {
            edt.delete(posDot + 3, posDot + 4);
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
