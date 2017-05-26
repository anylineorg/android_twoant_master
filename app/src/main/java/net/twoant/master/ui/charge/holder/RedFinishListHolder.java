package net.twoant.master.ui.charge.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by DZY on 2017/1/6.
 * 佛祖保佑   永无BUG
 */

public class RedFinishListHolder extends BaseHolder<DataRow> {

    public ImageView imageView;
    public TextView name;
    public TextView time;
    public TextView price;
    public TextView use_price;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), net.twoant.master.R.layout.zy_item_finish_redpacketlist, null);
        imageView = (ImageView) view.findViewById(net.twoant.master.R.id.iv_img_redpacketlist);
        name = (TextView) view.findViewById(net.twoant.master.R.id.shop_name);
        time = (TextView) view.findViewById(net.twoant.master.R.id.shop_start_end);
        price = (TextView) view.findViewById(net.twoant.master.R.id.red_price);
        use_price = (TextView) view.findViewById(net.twoant.master.R.id.red_use_price);
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
            ImageLoader.getImageFromNetworkPlaceholderControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getString("SHOP_AVATAR")),
                    imageView.getContext(), net.twoant.master.R.drawable.ic_def_circle_orange1);
            price.setText("￥"+data.getString("VAL"));
            if (data.getDouble("ACTIVE_VAL")==0.00){
                use_price.setText("无金额限制");
            }else {
                use_price.setText("￥" + data.getString("ACTIVE_VAL"));
            }
            time.setText("使用时间:"+data.getString("USE_TIME"));
        }/*else {
            ImageLoader.getImageFromNetwork(imageView, ApiConstants.GUO+data.getString("SHOP_AVATAR"),R.drawable.ic_def_circle_orange);
            price.setText("￥"+data.getString("VAL"));
            if (data.getDouble("ACTIVE_VAL")==0.00){
                use_price.setText("无金额限制");
            }else {
                use_price.setText("￥" + data.getString("ACTIVE_VAL"));
            }
            name.setText(data.getString("SHOP_NM"));
            time.setText("使用时间:"+data.getString("USE_TIME"));
        }*/
    }
}
