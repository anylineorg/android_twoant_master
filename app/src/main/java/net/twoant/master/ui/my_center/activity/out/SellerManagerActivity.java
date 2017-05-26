package net.twoant.master.ui.my_center.activity.out;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.common_utils.AESHelper;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.ui.charge.zxing.android.Contents;
import net.twoant.master.ui.charge.zxing.android.Intents;
import net.twoant.master.ui.main.activity.DiscoverActivity;
import net.twoant.master.ui.main.activity.MerchantEnteredActivity;
import net.twoant.master.ui.my_center.activity.ActivityManagerActivity;
import net.twoant.master.ui.my_center.activity.AddAdministerActivity;
import net.twoant.master.ui.my_center.activity.Fi_ShopActivity;
import net.twoant.master.ui.my_center.activity.GoodsManagerActivity;
import net.twoant.master.ui.my_center.fragment.MyConvenientInfoActivity;


/**
 * Created by DZY on 2016/11/17.
 */

public class SellerManagerActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton product,fenlei,activity,mongey,ll_shoukuan,admin_admin,shop_orderslist;
    public String shop_id;  //从商家管理列表带入的shop_id
    public String from_mymessage;

    @Override
    protected int getLayoutId() {
        return R.layout.zy_activity_admin_manager;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    void initView(){
        findViewById(R.id.ll_fenlei).setOnClickListener(this);
        findViewById(R.id.ll_shangpin).setOnClickListener(this);
        findViewById(R.id.ll_dingdan).setOnClickListener(this);
        findViewById(R.id.ll_huodong).setOnClickListener(this);
        LinearLayout llGuanli = (LinearLayout) findViewById(R.id.ll_guanliyuan);
        llGuanli.setOnClickListener(this);
        findViewById(R.id.ll_manage_member).setOnClickListener(this);
        findViewById(R.id.ll_manage_vip_card).setOnClickListener(this);
        findViewById(R.id.ll_manage_gift).setOnClickListener(this);
        findViewById(R.id.ll_caiwu).setOnClickListener(this);
        findViewById(R.id.ll_xinxi).setOnClickListener(this);
        findViewById(R.id.ll_renzheng).setOnClickListener(this);
        findViewById(R.id.ll_shoukuan).setOnClickListener(this);
        findViewById(R.id.ib_2code_manager).setOnClickListener(this);
        product = (ImageButton) findViewById(R.id.ib_product_manager);
        fenlei = (ImageButton) findViewById(R.id.ib_classify_manager);
        activity = (ImageButton) findViewById(R.id.ib_activity_manager);
        mongey = (ImageButton) findViewById(R.id.ib_finance_manager);
        findViewById(R.id.ib_safe_manager).setOnClickListener(this);
        ll_shoukuan = (ImageButton) findViewById(R.id.ll_shoukuan1);
        admin_admin =(ImageButton) findViewById(R.id.ib_addmanager_manager);
        shop_orderslist = (ImageButton) findViewById(R.id.ib_order_manager);
        //   save.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellerManagerActivity.this.finish();
            }
        });
        TextView title= (TextView) findViewById(R.id.tv_Title);
        title.setText("商家管理");
        product.setOnClickListener(this);
        fenlei.setOnClickListener(this);
        activity.setOnClickListener(this);
        mongey.setOnClickListener(this);
        shop_orderslist.setOnClickListener(this);
        ll_shoukuan.setOnClickListener(this);
        admin_admin.setOnClickListener(this);

        //管理列表进入的shop_id
        Intent intent = getIntent();

        //若为0则是从“我管理的商家界面”进入“商家管理”，否则直接进入“商家管理”
        from_mymessage = intent.getStringExtra("from_mymessage");
        if ("0".equals(from_mymessage)) {
            admin_admin.setBackground(CommonUtil.getDrawable(R.drawable.guanli_55));
            admin_admin.setEnabled(false);
            llGuanli.setEnabled(false);
        }
        shop_id = intent.getStringExtra("shop_id");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_shoukuan:
            case R.id.ib_2code_manager:
                String masterPassword = "aisou";
                String originalText = shop_id;
                String encryptingCode = AESHelper.encrypt(originalText, masterPassword);
                //launchSearch(AES.encryptAESToString(encryptingCode));
                launchSearch(encryptingCode);
                break;
            case R.id.ll_shangpin:
            case R.id.ib_product_manager:
                Intent intent = new Intent(SellerManagerActivity.this,GoodsManagerActivity.class);
                intent.putExtra("shop_id",shop_id);
                System.out.println(shop_id);
                startActivity(intent);
                break;
            case R.id.ll_fenlei:
            case R.id.ib_classify_manager:
                Intent fenlei = new Intent(SellerManagerActivity.this,ClassifyActivity.class);
                fenlei.putExtra("shop_id",shop_id);
                startActivity(fenlei);
                break;
            case R.id.ll_huodong:
            case R.id.ib_activity_manager:
                Intent huodong = new Intent(SellerManagerActivity.this,ActivityManagerActivity.class);
                huodong.putExtra("shop_id",shop_id);
                startActivity(huodong);
                break;
            case R.id.ll_caiwu:
            case R.id.ib_finance_manager:
                intent = new Intent(SellerManagerActivity.this,Fi_ShopActivity.class);//SellerManagerActivity.class);
                intent.putExtra("from_mymessage",from_mymessage);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
                break;
            case R.id.ll_xinxi:
            case R.id.ll_shoukuan1:
//                ToastUtil.showLong("便民信息功能程序员正努力开发中");
                MyConvenientInfoActivity.startActivity(this,true);
                break;
            case R.id.tv_Save:
                //  startActivity(new Intent(Gk_AdminManager.this,GK_EditGoodsManagerActivity.class));
                break;
            case R.id.ll_guanliyuan:
            case R.id.ib_addmanager_manager:
                intent = new Intent(SellerManagerActivity.this, AddAdministerActivity.class);
                intent.putExtra("shop_id",shop_id);
                startActivity(intent);
                break;
            case R.id.ll_dingdan:
            case R.id.ib_order_manager:
                Intent intent1 = new Intent(SellerManagerActivity.this, ShopOrderActivity.class);
                intent1.putExtra("shop_id",shop_id);
                startActivity(intent1);
                break;
            case R.id.ll_renzheng:
            //跳转到商家认证
            case R.id.ib_safe_manager:
                MerchantEnteredActivity.startActivity(this,
                MerchantEnteredActivity.TYPE_MERCHANT_CERTIFICATION, shop_id);
                break;
            //会员
            case R.id.ll_manage_member:
                DiscoverActivity.startActivity(SellerManagerActivity.this, getString(R.string.merchant_manage_member), ApiConstants.MERCHANT_MANAGE_MEMBER + "?shop=" + shop_id);
                break;
            //会员卡
            case R.id.ll_manage_vip_card:
                DiscoverActivity.startActivity(SellerManagerActivity.this, getString(R.string.merchant_manage_vip_card), ApiConstants.MERCHANT_MANAGE_VIP_CARD + "?shop=" + shop_id);
                break;
            //营销活动
            case R.id.ll_manage_gift:
                DiscoverActivity.startActivity(SellerManagerActivity.this, getString(R.string.merchant_manage_gift), ApiConstants.MERCHANT_MANAGE_GIFT + "?shop=" + shop_id);
                break;
        }
    }

    private void launchSearch(String text) {
        try {
            Intent intent = new Intent(Intents.Encode.ACTION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
            intent.putExtra(Intents.Encode.DATA, text);
            intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
            startActivity(intent);
        }catch (Exception e){
            Intent intent_shoukuan = new Intent(SellerManagerActivity.this,ShopQRCodeActivity.class);
            intent_shoukuan.putExtra("shop_id",text);
            startActivity(intent_shoukuan);
        }
    }
}
