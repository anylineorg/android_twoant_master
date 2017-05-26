package net.twoant.master.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by DZY on 2017/2/23.
 * 佛祖保佑   永无BUG
 */

public class DZYToast{
    private static Toast sToast;

    public static void showToast(Context context, String msg){
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
        View toastView = View.inflate(context, net.twoant.master.R.layout.zy_mytoast, null);
        TextView tv = (TextView) toastView.findViewById(net.twoant.master.R.id.tv_toast_text);
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(Gravity.CENTER);
        sToast.setGravity(Gravity.CENTER, 0, 0);
        sToast.setView(toastView);

        tv.setText(msg);
        //如果这个Toast已经在显示了，那么这里会立即修改文本
        sToast.show();
    }
}
