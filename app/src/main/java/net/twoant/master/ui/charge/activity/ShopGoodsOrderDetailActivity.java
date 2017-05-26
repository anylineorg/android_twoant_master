package net.twoant.master.ui.charge.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.widget.CancelOrderDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2017/1/5.
 * 佛祖保佑   永无BUG
 */

public class ShopGoodsOrderDetailActivity extends LongBaseActivity implements View.OnClickListener{

    private String waitpay_id;
    private HintDialogUtil hintDialogUtil;
    private DataRow dataR;
    private TextView tvAutoCancelOrder,shopName,realPrice,words,orderNum,creatTime,redMoney,integralMoney,tvBuyerState,
            yueMoney,chongzhiMoney,tv_totalMoney,tv_redMoney,tv_integralMoney,tv_yueMoney,tv_chongzhiMoney,title;
    private LinearLayout itemContain;;
    private CancelOrderDialog cancelOrderDialog;
    public String orderId;
    public DataSet items;
    public ImageView imgBuyer;
    public EaseUser mBuyer;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_shoporder_details_waitpay;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(R.id.tv_contact_waitpay_detail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_tel_waitpay_detail).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvAutoCancelOrder = (TextView) findViewById(R.id.tv_cancelorder_waitpat_detail);
        shopName = (TextView) findViewById(R.id.textView54);
        realPrice = (TextView) findViewById(R.id.textView62);
        words =  (TextView) findViewById(R.id.textView64);
        orderNum =  (TextView) findViewById(R.id.textView66);
        creatTime =  (TextView) findViewById(R.id.textView68);
        imgBuyer = (ImageView) findViewById(R.id.imageView10);
        redMoney = (TextView) findViewById(R.id.tv_red_shop_waitpay);
        integralMoney = (TextView) findViewById(R.id.tv_integral_shop_waitpay);
        yueMoney = (TextView) findViewById(R.id.tv_yue_shop_waitpay);
        chongzhiMoney = (TextView) findViewById(R.id.tv_chongzhi_shop_waitpay);
        itemContain = (LinearLayout) findViewById(R.id.ll_contain_waitpay_detail);
        tv_redMoney = (TextView) findViewById(R.id.tv_red_shop_waitpay);
        tv_integralMoney = (TextView) findViewById(R.id.tv_integral_shop_waitpay);
        tv_yueMoney = (TextView) findViewById(R.id.tv_yue_shop_waitpay);
        tv_chongzhiMoney = (TextView) findViewById(R.id.tv_chongzhi_shop_waitpay);
        tv_totalMoney = (TextView) findViewById(R.id.textView60);
        title = (TextView) findViewById(R.id.text_title_shopwaitpay);
        tvBuyerState = (TextView) findViewById(R.id.tv_buyer_state_shopwaitdetail);
        waitpay_id = getIntent().getStringExtra("waitpay_id");
        hintDialogUtil = new HintDialogUtil(this);
        cancelOrderDialog = new CancelOrderDialog(this, Gravity.CENTER,false);
        boolean isshop_waitpay_enjoy = getIntent().getBooleanExtra("isshop_waitpay_enjoy", false);
        if (isshop_waitpay_enjoy) {
            tvAutoCancelOrder.setVisibility(View.VISIBLE);
            tvBuyerState.setText("等待买家付款");
            tv_totalMoney.setText("总需付款");
            tv_redMoney.setText("选用红包");
            tv_integralMoney.setText("选用积分抵扣");
            tv_yueMoney.setText("选用钱包抵扣");
            tv_chongzhiMoney.setText("需充值付款");
            title.setText("买家待付款订单");
        }
        AiSouAppInfoModel.getInstance().addWaitOrderActivity(this);
    }

    @Override
    protected void requestNetData() {
        Map<String,String> map = new HashMap<>();
        map.put("id",waitpay_id);
        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.WAITPAY_SHOP_DETAIL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();

                DataRow dataRow = DataRow.parseJson(response);
                dataR = dataRow.getRow("data");
                String create_time = dataR.getString("CREATE_TIME");
                long currentMetre = 604800;// 7天的 秒数
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTimeStr = sdf.format(new Date());
                try {
                    long currentTime = sdf.parse(currentTimeStr).getTime();
                    long createTime = sdf.parse(create_time).getTime();
                    long banlanceMetre = currentMetre - ((currentTime - createTime)/1000);
                    int[] ints = formatDateTime(banlanceMetre);
                    tvAutoCancelOrder.setText("剩"+ints[0]+"天"+ints[1]+"小时买家自动取消订单");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mBuyer = new EaseUser(dataR.getStringDef("BUYER_AISOU_ID", ""));
                mBuyer.setNickname(dataR.getStringDef("BUYER_NM", mBuyer.getUsername()));
                shopName.setText(dataR.getString("BUYER_NM"));//买家的名
                realPrice.setText("¥"+dataR.getString("TOTAL_PRICE"));
                String description = dataR.getString("DESCRIPTION");
                if (TextUtils.isEmpty(description) || "null".equals(description)) {
                    description = "";
                }
                words.setText(description);
                orderId = dataR.getString("ID");
                orderNum.setText(orderId);
                creatTime.setText(dataR.getString("CREATE_TIME"));
                redMoney.setText("-¥"+dataR.getString("VOUCHER_VAL"));
                double cash_score = Double.parseDouble(dataR.getString("CASH_SCORE")) / 200;
                BigDecimal bigDecimal = new BigDecimal(cash_score);
                if (cash_score <= 0) {
                    integralMoney.setText("-¥0");
                }else {
                    integralMoney.setText("-¥"+ CommonUtil.subZeroAndDot(bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
                }
                yueMoney.setText("-¥"+dataR.getString("PURSE_PRICE"));
                chongzhiMoney.setText("-¥"+dataR.getString("CASH_PRICE"));
                ImageLoader.getImageFromNetwork(imgBuyer, BaseConfig.getCorrectImageUrl(dataR.getString("BUYER_AVATAR")),ShopGoodsOrderDetailActivity.this);
                items = dataR.getSet("ITEMS");
                int size = items.size();
                System.out.println(size);
                for (int i = 0; i < items.size(); i++){
                    View convertView2 = View.inflate(ShopGoodsOrderDetailActivity.this, R.layout.zy_item_waitpay_orderdetail, null);
                    TextView goodsName = (TextView) convertView2.findViewById(R.id.textView55);
                    ImageView imageView = (ImageView) convertView2.findViewById(R.id.imageView12);
                    TextView goodsNum = (TextView) convertView2.findViewById(R.id.textView58);
                    TextView liangdian = (TextView) convertView2.findViewById(R.id.tv_liangdian_waitpay_detail_item);
                    TextView price = (TextView) convertView2.findViewById(R.id.tv_price_waitpay_detail);
                    String goods_id = items.getRow(i).getString("GOODS_ID");
                    convertView2.setTag(goods_id);
                    convertView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(ShopGoodsOrderDetailActivity.this, GoodsDetailActivity.class);
//                            intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_ID, v.getTag().toString());
//                            startActivity(intent);
                        }
                    });
                    goodsName.setText(items.getRow(i).getString("GOODS_NM"));
                    goodsNum.setText("x"+ items.getRow(i).getString("QTY"));
                    liangdian.setText(items.getRow(i).getString("GOODS_LD"));
                    price.setText("¥"+ items.getRow(i).getString("PRICE"));
                    ImageLoader.getImageFromNetwork(imageView,BaseConfig.getCorrectImageUrl(items.getRow(i).getString("GOODS_IMG")),ShopGoodsOrderDetailActivity.this);
                    itemContain.addView(convertView2);
                }
            }
        });
    }

    class ItemHolder{
        ImageView imageView;
        TextView goodsName,goodsNum,liangdian,price;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ll_seller_waitpay_detail:
//                MerchantHomePageActivity.startActivity(ShopGoodsOrderDetailActivity.this,shopid);
                break;
            case R.id.tv_tel_waitpay_detail:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + dataR.getString("BUYER_TEL"));
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.tv_contact_waitpay_detail:
                if (null == mBuyer) {
                    ToastUtil.showShort("获取买家信息失败");
                    return;
                }
                UserInfoUtil.startChat(mBuyer, null, ShopGoodsOrderDetailActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private int[] formatDateTime(long mss) {
        int[] arr = new int[2];
        String DateTimes = null;
        long days = mss / ( 60 * 60 * 24);
        long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % ( 60 * 60)) /60;
        long seconds = mss % 60;
        if(days>0){
            arr[0] = (int) days;
            arr[1] = (int) hours;
            DateTimes= days + "天" + hours + "小时" + minutes + "分钟"
                    + seconds + "秒";
        }else if(hours>0){
            arr[0] = 0;
            arr[1] = (int) hours;
            DateTimes=hours + "小时" + minutes + "分钟"
                    + seconds + "秒";
        }else if(minutes>0){
            DateTimes=minutes + "分钟"
                    + seconds + "秒";
        }else{
            DateTimes=seconds + "秒";
        }
        return arr;
    }

    private void requestNetCancel() {
        cancelOrderDialog.dismiss();
        hintDialogUtil.showLoading();
        Map<String,String> map = new HashMap<>();
        map.put("id",orderId);
        LongHttp(ApiConstants.PERSON_WATI_PAY_DELETE, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("取消失败:");
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                String result = DataRow.parseJson(response).getString("result");
                if (result.contains("true")) {
                    ToastUtil.showLong("已取消");
                    finish();
                }
                hintDialogUtil.dismissDialog();
            }
        });
    }
}
