package net.twoant.master.ui.charge.holder;

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
 * Created by DZY on 2016/12/22.
 * 佛祖保佑   永无BUG
 */

public class MyAcitivityHolder extends BaseHolder<DataRow> {

    private TextView activity_name;
    private TextView shop_tel;
    private TextView shop_name;
    private TextView shop_address;
    private ImageView imageView;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_activitylist_myactivity, null);
        activity_name = (TextView) view.findViewById(R.id.tv_name_myactivity);
        shop_tel = (TextView) view.findViewById(R.id.tv_tel_myactivity);
        shop_name = (TextView) view.findViewById(R.id.tv_shopname_myactivity);
        shop_address = (TextView) view.findViewById(R.id.tv_address_myactivity);
        imageView = (ImageView) view.findViewById(R.id.iv_head_img_myactivity);
        view.findViewById(R.id.tv_state_myactivity).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        activity_name.setText(data.getString("TITLE"));
        shop_tel.setText(data.getString("SHOP_TEL"));
        shop_name.setText(data.getString("SHOP_NM"));
        shop_address.setText(data.getString("SHOP_ADDRESS"));
        ImageLoader.getImageFromNetworkControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getString("COVER_IMG")), imageView.getContext(), R.drawable.ic_def_circle);
    }
}
