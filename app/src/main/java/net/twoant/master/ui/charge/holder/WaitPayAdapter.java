package net.twoant.master.ui.charge.holder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.charge.activity.GoodsOrderDetailActivity;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.List;

/**
 * Created by DZY on 2017/1/8.
 * 佛祖保佑   永无BUG
 */

public class WaitPayAdapter extends RecyclerView.Adapter<WaitHolder> {

    public static int SHOP_TEL = 3<<24;
    public static int ID = 4<<24;
    public static int SELLER_ID = 5<<24;

    private List<DataRow> rowsList;
    public View.OnClickListener mOnClickListener;
    private Activity mActivity;
    private WaitOnclickCancelListener waitOnclickCancelListener;
    private WaitOnclickPayListener waitOnclickPayListener;

    @Override
    public WaitHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WaitHolder( LayoutInflater.from(parent.getContext()).inflate(net.twoant.master.R.layout.zy_item_waitpay, parent, false));
    }

    public void setDataBean(List<DataRow> dataBean,Activity mActivity ,WaitOnclickCancelListener waitOnclickCancelListener,WaitOnclickPayListener waitOnclickPayListener) {
        this.rowsList = dataBean;
        this.notifyDataSetChanged();
        this.mActivity = mActivity;
        this.waitOnclickCancelListener = waitOnclickCancelListener;
        this.waitOnclickPayListener = waitOnclickPayListener;
    }

    @Override
    public void onBindViewHolder(WaitHolder holder, final int position) {
        DataRow dataRow = rowsList.get(position);
        if (dataRow != null) {
            holder.shopName.setText(rowsList.get(position).getString("SHOP_SELLER_NM"));
            holder.goodsMoney.setText("¥"+rowsList.get(position).getString("TOTAL_PRICE"));
            String total = rowsList.get(position).getSet("ITEMS").sum("QTY")+"";
            holder.totalNum.setText(total);
            if (mOnClickListener == null) {
                mOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case net.twoant.master.R.id.btn_contact_waitpay:
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                Uri data = Uri.parse("tel:" +  v.getTag());
                                intent.setData(data);
                                mActivity.startActivity(intent);
                                break;
                            case net.twoant.master.R.id.btn_canel_order_waitpay:
                                String s = v.getTag() + "";
                                System.out.println(s);
                                waitOnclickCancelListener.onClickListener(s);
                                break;
                            case net.twoant.master.R.id.btn_pay_waitpay:
                                String s1 = v.getTag() + "";
                                System.out.println(s1);
                                waitOnclickPayListener.onClickListener(v.getTag()+"");
                                break;
                            case net.twoant.master.R.id.rl_shop_waitpay:
                                //商家详情页
                                MerchantHomePageActivity.startActivity(mActivity,v.getTag()+"");
                                break;
                            case net.twoant.master.R.id.rl_itemcontain_waitpay:
                                Intent intent2 = new Intent(mActivity, GoodsOrderDetailActivity.class);
                                intent2.putExtra("waitpay_id",v.getTag()+"");
                                mActivity.startActivity(intent2);
                                break;
                            case net.twoant.master.R.id.rl_item_state_waitpay:
                                Intent intent1 = new Intent(mActivity, GoodsOrderDetailActivity.class);
                                intent1.putExtra("waitpay_id",v.getTag()+"");
                                mActivity.startActivity(intent1);
                                break;
                        }
                    }
                };
            }
            holder.contain.removeAllViews();
//            View inflate = View.inflate(AiSouAppInfoModel.getAppContext(), R.layout.zy_item_waitpay_orderdetail, null);
            DataSet items = rowsList.get(position).getSet("ITEMS");
            for (int i = 0 ; i < items.size();i++){
                final DataRow row = items.getRow(i);
                View inflateGoodsItem = View.inflate(mActivity, net.twoant.master.R.layout.zy_item_goods_waitpay, null);
                inflateGoodsItem.setTag(dataRow.getString("ID"));
                inflateGoodsItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent2 = new Intent(mActivity, GoodsOrderDetailActivity.class);
                        intent2.putExtra("waitpay_id",v.getTag()+"");
                        mActivity.startActivity(intent2);
                    }
                });
                TextView itemGoodsName = (TextView) inflateGoodsItem.findViewById(net.twoant.master.R.id.tv_goodsname_waitpay);
                itemGoodsName.setText(row.getString("GOODS_NM"));
                ImageView itemImg = (ImageView) inflateGoodsItem.findViewById(net.twoant.master.R.id.iv_goodsimg_postorder);
                ImageLoader.getImageFromNetworkPlaceholderControlImg(itemImg, BaseConfig.getCorrectImageUrl(row.getString("GOODS_IMG")), mActivity, net.twoant.master.R.drawable.ic_def_small);
                TextView itemGoodsDetail = (TextView) inflateGoodsItem.findViewById(net.twoant.master.R.id.tv_detail_waitpay);
                itemGoodsDetail.setText(row.getString("GOODS_LD"));
                TextView itemGoodsMoney = (TextView) inflateGoodsItem.findViewById(net.twoant.master.R.id.tv_price_waitpay);
                itemGoodsMoney.setText("¥"+row.getString("PRICE"));
                TextView itemGoodsNum = (TextView) inflateGoodsItem.findViewById(net.twoant.master.R.id.tv_num_waitpay);
                itemGoodsNum.setText("x"+row.getString("QTY"));
                holder.contain.addView(inflateGoodsItem);
            }
            holder.contact.setTag(dataRow.getString("SHOP_TEL"));
            holder.contact.setOnClickListener(mOnClickListener);
            String id = dataRow.getString("ID");
            System.out.println(id);
            holder.cancel.setTag(dataRow.getString("ID"));
            holder.cancel.setOnClickListener(mOnClickListener);
            holder.pay.setTag(dataRow.getString("ID"));
            holder.pay.setOnClickListener(mOnClickListener);
            holder.rlShopTitle.setTag(dataRow.getString("SELLER_ID"));
            holder.rlShopTitle.setOnClickListener(mOnClickListener);
            holder.rlCaontain1.setTag(dataRow.getString("ID"));
            holder.rlCaontain2.setTag(dataRow.getString("ID"));
            holder.rlCaontain1.setOnClickListener(mOnClickListener);
            holder.rlCaontain2.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return rowsList == null ? 0 : rowsList.size();
    }
}

class WaitHolder extends RecyclerView.ViewHolder{
    TextView shopName,totalNum;
    TextView goodsMoney;
    LinearLayout contain;
    Button contact,cancel,pay;
    RelativeLayout rlShopTitle;
    RelativeLayout rlCaontain1;
    RelativeLayout rlCaontain2;
    
    public WaitHolder(View itemView) {
        super(itemView);
        shopName =  (TextView) itemView.findViewById(net.twoant.master.R.id.tv_shopname_waitpay);
        goodsMoney = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_itemprice_waitpay);
        totalNum = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_item_count_waitpay);
        contain = (LinearLayout) itemView.findViewById(net.twoant.master.R.id.ll_item_contain_waitpay);
        contact = (Button) itemView.findViewById(net.twoant.master.R.id.btn_contact_waitpay);
        cancel = (Button) itemView.findViewById(net.twoant.master.R.id.btn_canel_order_waitpay);
        pay = (Button) itemView.findViewById(net.twoant.master.R.id.btn_pay_waitpay);
        rlShopTitle = (RelativeLayout) itemView.findViewById(net.twoant.master.R.id.rl_shop_waitpay);
        rlCaontain1 = (RelativeLayout) itemView.findViewById(net.twoant.master.R.id.rl_itemcontain_waitpay);
        rlCaontain2 = (RelativeLayout) itemView.findViewById(net.twoant.master.R.id.rl_item_state_waitpay);
    }
}
