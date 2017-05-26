package net.twoant.master.ui.my_center.holder;

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
 * Created by DZY on 2017/1/11.
 * 佛祖保佑   永无BUG
 */

public class CollectShopHolder extends BaseHolder<DataRow> {

    public ImageView imageView;
    public TextView title,distance,tel,volum,count,address,more;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_mycollect_seller_item, null);
        imageView = (ImageView) view.findViewById(R.id.iv_mycollect_item);
        title = (TextView) view.findViewById(R.id.tv_title_shopcollect_item);
        distance = (TextView) view.findViewById(R.id.tv_distance_mycollect_goods);
        tel = (TextView) view.findViewById(R.id.tv_tel_shopcollect_item);
        volum = (TextView) view.findViewById(R.id.tv_volum_shopcollect_item);
//        count = (TextView) view.findViewById(R.id.tv_count_shopcollect_item);
        address = (TextView) view.findViewById(R.id.tv_address_shopcollect_item);
        more = (TextView) view.findViewById(R.id.tv_more_shopcollect_item);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        title.setText(data.getString("SHOP_NAME"));
        String distance = data.getString("DISTANCE");
        if (!TextUtils.isEmpty(distance)) {
            if (distance.equals("-1")) {
                distance = "5km";
                more.setVisibility(View.VISIBLE);
            }
        }
        this.distance.setText(distance+"M");
        title.setText(data.getString("SHOP_NM"));
        tel.setText("电话:"+data.getString("SHOP_TEL"));
        volum.setText(data.getString("SHOP_SELL_QTY_SUM"));
        address.setText("地址"+data.getString("SHOP_ADDRESS"));
        ImageLoader.getImageFromNetworkPlaceholderControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getString("SHOP_AVATAR")), imageView.getContext(), R.drawable.ic_def_small);
    }
}
