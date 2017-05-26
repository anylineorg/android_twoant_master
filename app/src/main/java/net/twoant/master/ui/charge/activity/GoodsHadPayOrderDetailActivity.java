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

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.my_center.activity.out.GoodsDetailActivity;
import net.twoant.master.widget.CancelOrderDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2017/1/5.
 * 佛祖保佑   永无BUG
 */

public class GoodsHadPayOrderDetailActivity extends LongBaseActivity implements View.OnClickListener{

    private String waitpay_id,shopid;
    private HintDialogUtil hintDialogUtil;
    private DataRow dataR;
    private TextView tvAutoCancelOrder,shopName,realPrice,words,orderNum,creatTime;
    private LinearLayout itemContain;;
    private CancelOrderDialog cancelOrderDialog;
    public String orderId;
    public DataSet items;
    public TextView redP;
    public TextView integralP;
    public TextView yueP;
    public TextView chongzhiP;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_order_details_hadpay;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        findViewById(net.twoant.master.R.id.tv_contact_waitpay_detail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_tel_waitpay_detail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        tvAutoCancelOrder = (TextView) findViewById(net.twoant.master.R.id.tv_cancelorder_waitpat_detail);
        shopName = (TextView) findViewById(net.twoant.master.R.id.textView54);
        realPrice = (TextView) findViewById(net.twoant.master.R.id.textView62);
        words =  (TextView) findViewById(net.twoant.master.R.id.textView64);
        orderNum =  (TextView) findViewById(net.twoant.master.R.id.textView66);
        creatTime =  (TextView) findViewById(net.twoant.master.R.id.textView68);
        itemContain = (LinearLayout) findViewById(net.twoant.master.R.id.ll_contain_waitpay_detail);
        redP = (TextView) findViewById(net.twoant.master.R.id.tv_red_hadpay);
        integralP = (TextView) findViewById(net.twoant.master.R.id.tv_integral_hadpay);
        yueP = (TextView) findViewById(net.twoant.master.R.id.tv_yue_hadpay);
        chongzhiP = (TextView) findViewById(net.twoant.master.R.id.tv_chongzhi_hadpay);
        findViewById(net.twoant.master.R.id.ll_seller_waitpay_detail).setOnClickListener(this);
        waitpay_id = getIntent().getStringExtra("waitpay_id");
        hintDialogUtil = new HintDialogUtil(this);
        cancelOrderDialog = new CancelOrderDialog(this, Gravity.CENTER,false);

        AiSouAppInfoModel.getInstance().addWaitOrderActivity(this);
    }

    @Override
    protected void requestNetData() {
        Map<String,String> map = new HashMap<>();
        map.put("id",waitpay_id);
        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.WAITPAY_DETAIL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d(e+"");
                hintDialogUtil.showError("网络连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                LogUtils.d(response);
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
                    tvAutoCancelOrder.setText("剩"+ints[0]+"天"+ints[1]+"小时自动取消订单");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                shopName.setText(dataR.getString("SHOP_SELLER_NM"));
                realPrice.setText("¥"+dataR.getString("TOTAL_PRICE"));
                String description = dataR.getString("DESCRIPTION");
                if (TextUtils.isEmpty(description) || "null".equals(description)) {
                    description = "";
                }
                words.setText(description);
                orderId = dataR.getString("ID");
                shopid = dataR.getString("SHOP_SELLER_ID");
                orderNum.setText(orderId);
                creatTime.setText(""+dataR.getString("CREATE_TIME"));
                redP.setText("¥"+dataR.getString("VOUCHER_VAL"));
                if (!TextUtils.isEmpty(dataR.getString("CASH_SCORE"))) {
                    integralP.setText("¥"+Double.parseDouble(dataR.getString("CASH_SCORE"))/200);
                }else {
                    integralP.setText("¥"+0);
                }
                yueP.setText("¥"+dataR.getString("PURSE_PRICE"));
                chongzhiP.setText("¥"+dataR.getString("CASH_PRICE"));
                items = dataR.getSet("ITEMS");
                int size = items.size();
                System.out.println(size);
                for (int i = 0; i < items.size(); i++){
                    View convertView2 = View.inflate(GoodsHadPayOrderDetailActivity.this, net.twoant.master.R.layout.zy_item_waitpay_orderdetail, null);
                    TextView goodsName = (TextView) convertView2.findViewById(net.twoant.master.R.id.textView55);
                    ImageView imageView = (ImageView) convertView2.findViewById(net.twoant.master.R.id.imageView12);
                    TextView goodsNum = (TextView) convertView2.findViewById(net.twoant.master.R.id.textView58);
                    TextView liangdian = (TextView) convertView2.findViewById(net.twoant.master.R.id.tv_liangdian_waitpay_detail_item);
                    TextView price = (TextView) convertView2.findViewById(net.twoant.master.R.id.tv_price_waitpay_detail);
                    String goods_id = items.getRow(i).getString("GOODS_ID");
                    convertView2.setTag(goods_id);
                    convertView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GoodsHadPayOrderDetailActivity.this, GoodsDetailActivity.class);
                            intent.putExtra(GoodsDetailActivity.EXTRA_GOODS_ID, v.getTag().toString());
                            startActivity(intent);
                        }
                    });
                    goodsName.setText(items.getRow(i).getString("GOODS_NM"));
                    goodsNum.setText("x"+ items.getRow(i).getString("QTY"));
                    liangdian.setText(items.getRow(i).getString("GOODS_LD"));
                    price.setText("¥"+ items.getRow(i).getString("TOTAL_PRICE"));
                    ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(items.getRow(i).getString("GOODS_IMG")),GoodsHadPayOrderDetailActivity.this);
                    itemContain.addView(convertView2);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case net.twoant.master.R.id.ll_seller_waitpay_detail:
                MerchantHomePageActivity.startActivity(GoodsHadPayOrderDetailActivity.this,shopid);
                break;
            case net.twoant.master.R.id.tv_tel_waitpay_detail:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + dataR.getString("SHOP_TEL"));
                intent.setData(data);
                startActivity(intent);
                break;
            case net.twoant.master.R.id.tv_contact_waitpay_detail:
                UserInfoUtil.startChat(null, shopid, GoodsHadPayOrderDetailActivity.this);
                break;
            case net.twoant.master.R.id.iv_back:
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
                ToastUtil.showLong("取消失败:"+e);
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
