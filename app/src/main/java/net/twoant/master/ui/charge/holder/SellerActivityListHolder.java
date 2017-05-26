package net.twoant.master.ui.charge.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.charge.bean.SellerManageListBean;

/**
 * Created by DZY on 2016/12/2.
 */

public class SellerActivityListHolder extends BaseHolder<SellerManageListBean.DataBean> {

    private ImageView imageView;
    public TextView name;
    public TextView people;
    public TextView time;

    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), net.twoant.master.R.layout.zy_item_activitylist_seller,null);
        imageView = (ImageView) view.findViewById(net.twoant.master.R.id.iv_item_activitylist);
        name = (TextView) view.findViewById(net.twoant.master.R.id.tv_name_activitylist);
        people = (TextView) view.findViewById(net.twoant.master.R.id.tv_people_activitylist);
        time = (TextView) view.findViewById(net.twoant.master.R.id.tv_time_activitylist);
        return view;
    }

    @Override
    public void bindData(SellerManageListBean.DataBean data) {
        name.setText(data.getSHOP_NM());
        people.setText(data.getJOIN_QTY()+"");
        time.setText(data.getSTART_TIME()+"至"+data.getEND_TIME());
        ImageLoader.getImageFromNetworkPlaceholderControlImg(imageView, BaseConfig.getCorrectImageUrl(data.getCOVER_IMG()), imageView.getContext(), net.twoant.master.R.drawable.ic_def_small);
    }
}
