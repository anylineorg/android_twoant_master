package net.twoant.master.ui.charge.holder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by DZY on 2017/1/5.
 * 佛祖保佑   永无BUG
 */

public class WalletHolder extends BaseHolder<DataRow> {

    public TextView sort_name;
    public TextView time;
    public TextView price;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.wallet_list_item, null);
        sort_name = (TextView) view.findViewById(R.id.sort_name);
        time = (TextView) view.findViewById(R.id.time);
        price = (TextView) view.findViewById(R.id.price);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        sort_name.setText(data.getString("SORT_TITLE"));
        time.setText(data.getString("LOG_TIME"));
        String temp = data.getString("VAL");
        int flag = data.getInt("FLAG");
        if (flag == 1) {
            price.setText("+"+temp);
            price.setTextColor(CommonUtil.getColor(R.color.red_f9));
        }else{
            price.setText("-"+temp);
            price.setTextColor(Color.GRAY);
        }
//        price.setText((temp*flag)+"");
    }
}
