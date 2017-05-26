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
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.ui.my_center.bean.GoodsListBean;

/**
 * Created by DZY on 2016/12/2.
 */

public class GoodsDetailHolder extends BaseHolder<GoodsListBean.ResultBean> {
    private ImageView img;
    private TextView tvName,tvPrice,tvCount,tvLd,tvClickCount;
    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), net.twoant.master.R.layout.zy_item_goodslist_publishgoods,null);
        img = (ImageView) view.findViewById(R.id.iv_item_publishgoods);
        tvName = (TextView) view.findViewById(R.id.tv_itemname_publishgoods);
        tvPrice = (TextView) view.findViewById(R.id.tv_item_price_publishgoods);
        tvCount = (TextView) view.findViewById(R.id.tv_item_count_publishgoods);
        tvLd = (TextView) view.findViewById(R.id.tv_ld_goodsdetail_item);
        tvClickCount = (TextView) view.findViewById(net.twoant.master.R.id.tv_clickcount_goodsdetail_item);
        return view;
    }

    @Override
    public void bindData(GoodsListBean.ResultBean data) {
        int goods_count = data.getGoods_count();
        LogUtils.d(goods_count+"");
        tvName.setText(data.getGoods_name());
        tvPrice.setText("¥"+data.getGoods_price()+"");
        tvCount.setText("库存:"+data.getGoods_count());
        String goods_ld = data.getGoods_introduce();
        if (TextUtils.isEmpty(goods_ld)) {
            tvLd.setVisibility(View.GONE);
        }else {
            tvLd.setText(""+goods_ld);
        }
        tvClickCount.setText(""+data.getClick());
        final String imgurl = BaseConfig.getCorrectImageUrl(data.getGoods_img());
        LogUtils.d(imgurl);
        ImageLoader.getImageFromNetworkPlaceholderControlImg(img,imgurl, img.getContext(), R.drawable.ic_def_small);
    }
}
