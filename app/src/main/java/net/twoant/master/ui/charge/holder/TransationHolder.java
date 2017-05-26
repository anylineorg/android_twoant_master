package net.twoant.master.ui.charge.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;

/**
 * Created by DZY on 2016/12/22.
 * 佛祖保佑   永无BUG
 */

public class TransationHolder extends BaseHolder<DataRow> {

    private TextView tvName,tvTime,tvMoney,red,integral,yue,chongzhi;
    @Override
    public View initHolderView() {
        View view = View.inflate(AiSouAppInfoModel.getAppContext(), net.twoant.master.R.layout.zy_item_transation_record,null);
        tvName = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_name_transation);
        tvTime = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_time_transation);
        tvMoney = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_money_transation);
        red = (TextView) view.findViewById(net.twoant.master.R.id.tv_red_transation_item);
        integral = (TextView) view.findViewById(net.twoant.master.R.id.tv_integral_transation_item);
        yue = (TextView) view.findViewById(net.twoant.master.R.id.tv_yue_transation_item);
        chongzhi = (TextView) view.findViewById(net.twoant.master.R.id.tv_chongzhi_transation_item);
        return view;
    }

    @Override
    public void bindData(DataRow data) {
        if (data.getInt("SORT_ID")==1) {
            String shop_seller_nm = data.getString("SHOP_SELLER_NM");
            if (TextUtils.isEmpty(shop_seller_nm) || "null".equals(shop_seller_nm)) {
                tvName.setText(data.getString("SORT_NM"));
            }else{
                tvName.setText(data.getString("SORT_NM")+"—"+shop_seller_nm);
            }
        }else if (data.getInt("SORT_ID")==2) {
            String shop_seller_nm = data.getString("SHOP_SELLER_NM");
            if (TextUtils.isEmpty(shop_seller_nm) || "null".equals(shop_seller_nm)) {
                tvName.setText(data.getString("SORT_NM"));
            }else{
                tvName.setText(data.getString("SORT_NM")+"—"+shop_seller_nm);
            }
        } else if (data.getInt("SORT_ID") == 3) {
            String shop_seller_nm = data.getString("ACTIVITY_TITLE");
            if (TextUtils.isEmpty(shop_seller_nm) || "null".equals(shop_seller_nm)) {
                tvName.setText(data.getString("SORT_NM"));
            }else{
                tvName.setText(data.getString("SORT_NM")+"—"+shop_seller_nm);
            }
        }

        tvTime.setText(data.getString("PAY_TIME"));
        tvMoney.setText(data.getString("TOTAL_PRICE"));
        red.setText("红包:"+data.getString("VOUCHER_VAL"));
        BigDecimal bigDecimal = new BigDecimal(data.getString("CASH_SCORE"));
        integral.setText("积分:"+ CommonUtil.subZeroAndDot(bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).toString()));
        yue.setText("钱包:"+data.getString("PURSE_PRICE"));
//        chongzhi.setText(data.getCASH_SORT_NM()+":"+data.getCASH_PRICE());
        chongzhi.setText("充值"+":"+data.getDouble("CASH_PRICE"));
    }
}
