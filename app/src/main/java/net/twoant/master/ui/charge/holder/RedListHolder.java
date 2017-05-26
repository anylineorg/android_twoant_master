package net.twoant.master.ui.charge.holder;

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
 * Created by DZY on 2017/1/6.
 * 佛祖保佑   永无BUG
 */

public class RedListHolder extends BaseHolder<DataRow> {

    public ImageView imageView;
    public TextView name;
    public TextView endtime,starttime;
    public TextView price;
    public TextView use_price;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_hava_redpacketlist, null);
        imageView = (ImageView) view.findViewById(R.id.iv_img_redpacketlist);
        name = (TextView) view.findViewById(R.id.shop_name);
        endtime = (TextView) view.findViewById(R.id.shop_start_end);
        starttime = (TextView) view.findViewById(R.id.shop_start_start);
        price = (TextView) view.findViewById(R.id.red_price);
        use_price = (TextView) view.findViewById(R.id.red_use_price);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        String shop_id = data.getString("SHOP_ID");
        if ("null".equals(shop_id) || TextUtils.isEmpty(shop_id)) {
            name.setText(data.getString("ACTIVITY_ITEM_TITLE"));
        }else{
            name.setText(data.getString("ACTIVITY_TITLE"));
        }
        if (data.getInt("ACTIVITY_SORT_ID")==4) {//商家红包
            ImageLoader.getImageFromNetworkPlaceholderControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getString("SHOP_AVATAR")), imageView.getContext() ,R.drawable.ic_def_circle_orange1);
            price.setText("￥"+data.getString("VAL"));
            if (data.getDouble("ACTIVE_VAL")==0.00){
                use_price.setText("无金额限制");
            }else {
                use_price.setText("满" + data.getString("ACTIVE_VAL")+"元可用");
            }
            String end_time = data.getString("END_TIME");
            System.out.println(end_time);
            endtime.setText("到期时间:"+data.getString("ACTIVITY_END_TIME"));
            starttime.setText("开始时间:"+data.getString("ACTIVITY_START_TIME"));
        }/*else {
            ImageLoader.getImageFromNetwork(imageView, ApiConstants.GUO+data.getString("SHOP_AVATAR"),R.drawable.ic_def_circle_orange);
            price.setText("￥"+data.getString("VAL"));
            if (data.getDouble("ACTIVE_VAL")==0.00){
                use_price.setText("无金额限制");
            }else {
                use_price.setText("￥" + data.getString("ACTIVE_VAL"));
            }
            name.setText(data.getString("SHOP_NM"));
            endtime.setText("到期时间:"+data.getString("ACTIVITY_END_TIME"));
            starttime.setText("开始时间:"+data.getString("ACTIVITY_START_TIME"));
        }*/
    }
}
