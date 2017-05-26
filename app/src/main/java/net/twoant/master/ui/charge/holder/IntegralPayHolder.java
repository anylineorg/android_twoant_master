package net.twoant.master.ui.charge.holder;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */

public class IntegralPayHolder extends BaseHolder<DataRow> {
    private TextView time;
    private TextView name;
    private TextView detail;
    private TextView money,explain;
    private ImageView imageView;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_incomelist_integral, null);
        time = (TextView) view.findViewById(R.id.tv_time_integral);
        name =  (TextView) view.findViewById(R.id.tv_name_integral);
        detail = (TextView) view.findViewById(R.id.tv_detail_integral);
        money = (TextView) view.findViewById(R.id.tv_money_integral);
        imageView = (ImageView) view.findViewById(R.id.ing_integral);
        explain = (TextView) view.findViewById(R.id.tv_explain_integral);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        time.setText(data.getString("LOG_TIME"));
        if ("null".equals(data.getString("SHOP_NM"))) {
            name.setText("两只蚂蚁");
        }else{
            name.setText(data.getString("SHOP_NM")+"");
        }
        detail.setText(data.getString("SORT_NM"));
        money.setText("-"+data.getString("VAL"));
        String remark = data.getString("REMARK");
        if (!TextUtils.isEmpty(remark) && !"null".equals(remark)) {
            explain.setVisibility(View.VISIBLE);
            explain.setText(remark);
        }
        money.setTextColor(Color.GRAY);
        ImageLoader.getImageFromNetworkControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getString("SHOP_AVATAR")),R.drawable.ic_def_circle_orange, R.drawable.ic_def_circle);
    }
}
