package net.twoant.master.wxapi;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.ui.charge.activity.GetCashActivity;
import net.twoant.master.ui.charge.activity.RechargeIntegralActivity;
import net.twoant.master.ui.charge.activity.ShopPayActivity_;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by DZY on 2016/12/12.
 * 佛祖保佑   永无BUG
 */
public class RechargeSuccessActivtity extends LongBaseActivity implements View.OnClickListener {
    public static String paymoney;
    public static String payShopName;
    public static String payType;
    public String is_shop_pay;

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_recharge_suceess;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        if (RechargeIntegralActivity.integralActivity != null) {
            RechargeIntegralActivity.integralActivity.finish();
            RechargeIntegralActivity.integralActivity = null;
        }
        if (ShopPayActivity_.instanceShopActivity != null) {
            ShopPayActivity_.instanceShopActivity.finish();
            ShopPayActivity_.instanceShopActivity = null;
        }
        if (GetCashActivity.instanceShopActivity != null) {
            GetCashActivity.instanceShopActivity.finish();
            GetCashActivity.instanceShopActivity = null;
        }
        TextView textView = (TextView) findViewById(R.id.tv_price_recharge_success);
        TextView tvPart = (TextView) findViewById(R.id.tv_payee_recharge_success);
        tvPart.setText(payShopName);
        textView.setText(paymoney);

        if (paymoney != null) {
            Pattern pattern = Pattern.compile("[0-9]*");
            if (!pattern.matcher(paymoney).matches())
                findViewById(R.id.tv_symbol).setVisibility(View.GONE);
        }

        TextView startTime = (TextView) findViewById(R.id.tv_transaction_recharge_success);
        TextView type = (TextView) findViewById(R.id.tv_paytype_recharge_success);
        type.setText(payType);
        findViewById(R.id.btn_enter_recharge_success).setOnClickListener(this);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        startTime.setText(dateFormat.format(curDate));
        /***
         *  判断是否从商家购买商品进入的
         */
        is_shop_pay = getIntent().getStringExtra("is_shop_pay");
        if (!TextUtils.isEmpty(is_shop_pay)) {
            AiSouAppInfoModel.getInstance().exitOrderActivity();
        }

        /**
         *
         * */
       /* if (GoodsDetailActivity.selectedList != null) {
            GoodsDetailActivity.selectedList = null;
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter_recharge_success:
                finish();
                /***
                 *  判断是否从商家购买商品进入的
                 */
               // if (!TextUtils.isEmpty(is_shop_pay)) {
               //     Order4Activity.startActivity(RechargeSuccessActivtity.this,1);
               // }
                break;
        }
    }


}
