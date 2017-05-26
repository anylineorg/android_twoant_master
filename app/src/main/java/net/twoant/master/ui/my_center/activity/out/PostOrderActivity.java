package net.twoant.master.ui.my_center.activity.out;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.fragment.HomeFragment;
import net.twoant.master.ui.my_center.bean.GoodsDetailBean;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.widget.PayDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/5.
 */
public class PostOrderActivity extends LongBaseActivity implements View.OnClickListener {
    private ListView listView;
    private MyAdapter adapter;
  //  private TextView tvDispatchMethod;
    private AlertDialog alertDialog;
    public static List<GoodsItem> selectedList_im;//bottomsheet选中集合
    public static List<GoodsItemBean> selectedList;//bottomsheet选中集合
    private TextView tvDispatchMethod,tv_count_postorder2,tv_waitpay_postorder2,tv_count_postorder3;//运费//免减//商品总价

    private TextView singlePrice, needPayPrice, goodsCount, shopName;
    private TextView goodsName, goodsNum;
    private EditText etBody,address,seller_name,seller_mobile;
    public static String shop_name;
    private static boolean Mis_score=false;//如果是积分商品
    private static String shop_id;
    private Spinner school,build;//学校／楼号
    List<String> categoryList;
    List<String> buildList;//／楼号
    public static HashMap<String,GoodsItem> selectedList_map;//bottomsheet选中集合
    private DataRow school_row,build_row;
    private int sort = 1;
    private static String pay_url="";
    private static String head;
    public static void startActivity(Context context, List<GoodsItemBean> selectedList,String shop_name,String shopid,int a) {
        Intent intent = new Intent(context, PostOrderActivity.class);
        context.startActivity(intent);
        PostOrderActivity.selectedList = selectedList;
        PostOrderActivity.shop_name = shop_name;
        PostOrderActivity.shop_id = shopid;
        pay_url= ApiConstants.CREATE_ORDER;
        head="";
        for (GoodsItem item : selectedList) {

            head+="pro="+item.getGoodsId()+"&qty="+item.getGoodsCount()+"&";
        }
        head=head.substring(0,head.length()-1);
        pay_url+=head;

    }

    public static void startActivity(Context context, List<GoodsItemBean> selectedList,String shop_name,String shopid,boolean is_score) {
        Intent intent = new Intent(context, PostOrderActivity.class);
        context.startActivity(intent);
        PostOrderActivity.selectedList = selectedList;
        PostOrderActivity.shop_name = shop_name;
        PostOrderActivity.shop_id = shopid;
        pay_url= ApiConstants.CREATE_ORDER;
        Mis_score=is_score;
        head="";
        for (GoodsItem item : selectedList) {

            head+="pro="+item.getGoodsId()+"&qty="+item.getGoodsCount()+"&";
        }
        head=head.substring(0,head.length()-1);
        pay_url+=head;

    }

    public static void startActivity(Context context, List<GoodsDetailBean> selectedList, String shop_name, String shopid) {
        Intent intent = new Intent(context, PostOrderActivity.class);
        context.startActivity(intent);
      //  PostOrderActivity.selectedList = selectedList;
        PostOrderActivity.shop_name = shop_name;
        PostOrderActivity.shop_id = shopid;
        //调试时放开
       /* int size = selectedList.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (GoodsItemBean item:selectedList){
            stringBuilder.append(item.toString()+"\r\n");
        }
        LogUtils.d(stringBuilder+"");*/
    }
    public static void startActivity(Context context, HashMap<String,GoodsItem> selectedList_map, String shop_name, String shopid, String url) {
        Intent intent = new Intent(context, PostOrderActivity.class);
        context.startActivity(intent);
        pay_url=url;
        PostOrderActivity.selectedList_map = selectedList_map;
        head=shopid;
        Set<String> keys= selectedList_map.keySet();
        selectedList_im=new ArrayList<GoodsItem>();
        for (String str : keys) {
            selectedList_im.add(selectedList_map.get(str));

        }
        //  selectedList.get(0).getGoodsName()
        PostOrderActivity.shop_name = selectedList_im.get(0).getShop_name();
        PostOrderActivity.shop_id = selectedList_im.get(0).getShop_id();

    }
    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.activity_postorder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        listView = (ListView) findViewById(net.twoant.master.R.id.lv_goods_postorder);
        tvDispatchMethod = (TextView) findViewById(net.twoant.master.R.id.tv_dispatchmethod_postorder);
        needPayPrice = (TextView) findViewById(net.twoant.master.R.id.tv_waitpay_postorder);
        tv_waitpay_postorder2=(TextView) findViewById(net.twoant.master.R.id.tv_waitpay_postorder2);
        tv_count_postorder2=(TextView) findViewById(net.twoant.master.R.id.tv_count_postorder2);
        tv_count_postorder3=(TextView) findViewById(net.twoant.master.R.id.tv_count_postorder3);
        goodsCount = (TextView) findViewById(net.twoant.master.R.id.tv_count_postorder);
        shopName = (TextView) findViewById(net.twoant.master.R.id.tv_shopname_postorder);
        etBody = (EditText) findViewById(net.twoant.master.R.id.et_body_postorder);
        school=(Spinner) findViewById(net.twoant.master.R.id.sp_selectclass_publish);
        build=(Spinner) findViewById(net.twoant.master.R.id.sp_selectclass_publish_build);
        seller_name=(EditText) findViewById(net.twoant.master.R.id.seller_name);
        seller_mobile=(EditText) findViewById(net.twoant.master.R.id.seller_mobile);
        categoryList = new ArrayList<>();
        buildList= new ArrayList<>();
        address=(EditText) findViewById(net.twoant.master.R.id.et_body_address);
        if (HomeFragment.address==null || HomeFragment.address.equals("null") || HomeFragment.address=="null") {

        }else
        {
            address.setText(HomeFragment.address);
        }
        if (AiSouAppInfoModel.RECEIVE_MOBILE!=null && !AiSouAppInfoModel.RECEIVE_MOBILE.equals("null") && AiSouAppInfoModel.RECEIVE_MOBILE!="null") {

            seller_mobile.setText(AiSouAppInfoModel.RECEIVE_MOBILE);
        } if (AiSouAppInfoModel.RECEIVE_NAME!=null && !AiSouAppInfoModel.RECEIVE_NAME.equals("null") && AiSouAppInfoModel.RECEIVE_NAME!="null") {

            seller_name.setText(AiSouAppInfoModel.RECEIVE_NAME);
        }
        findViewById(net.twoant.master.R.id.rl_dispatchmethod_postorder).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.btn_post_postorder).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        adapter = new MyAdapter(selectedList);
        listView.setAdapter(adapter);

        AiSouAppInfoModel.getInstance().addOrderActivity(this);
        //  seller_name.setText(SharedPreferencesUtils.getSharedStringData(PostOrderActivity.this,"nick_name"));
        //  seller_mobile.setText(SharedPreferencesUtils.getSharedStringData(PostOrderActivity.this,"account"));
        CUA();
        initSplear();
    }

    HashMap<String,String>map;
    void initSplear(){
        map=new HashMap<>();
        LongHttp(ApiConstants.SCHOOL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                categoryList.clear();
                school_row=DataRow.parseJson(response);
                DataSet set= school_row.getSet("data");
                for (int i=0;i<set.getRows().size();i++){
                    DataRow row=set.getRow(i);
                    categoryList.add(row.getString("NM"));
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(PostOrderActivity.this,android.R.layout.simple_list_item_1,categoryList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                school.setAdapter(arrayAdapter);


                school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int poion, long id) {
                        buildList.clear();
                        AiSouAppInfoModel.SCHOOL_CD= school_row.getSet("data").getRow(poion).getString("ID");
                        map.put("school",school_row.getSet("data").getRow(poion).getString("ID"));
                        LongHttp(ApiConstants.BUILD, "", map, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {

                                //  Log.e("tag2",response);
                                build_row=DataRow.parseJson(response);
                                DataSet set= build_row.getSet("data");
                                for (int i=0;i<set.getRows().size();i++){
                                    DataRow row=set.getRow(i);
                                    buildList.add(row.getString("NM"));
                                }
                                ArrayAdapter arrayAdapter2 = new ArrayAdapter(PostOrderActivity.this,android.R.layout.simple_list_item_1,buildList);
                                arrayAdapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                build.setAdapter(arrayAdapter2);
                                build.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int poion, long id) {

                                        AiSouAppInfoModel.BUILD_CD= build_row.getSet("data").getRow(poion).getString("ID");
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                                if (!isNull(AiSouAppInfoModel.BUILD_CD)){
                                    for (int i=0;i<set.getRows().size();i++){
                                        DataRow row=set.getRow(i);
                                        if (row.getString("ID").equals(AiSouAppInfoModel.BUILD_CD)){
                                            build.setSelection(i,true);
                                            return;
                                        }
                                    }
                                }

                            }
                        });

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (!isNull(AiSouAppInfoModel.SCHOOL_CD)){
                    for (int i=0;i<set.getRows().size();i++){
                        DataRow row=set.getRow(i);
                        if (row.getString("ID").equals(AiSouAppInfoModel.SCHOOL_CD)){
                            school.setSelection(i,true);
                            return;
                        }
                    }
                }
            }
        });

    }
    //CUA_PRICE
    String CUA(){

        String url= ApiConstants.CREATE_ORDER;//CUA_PRICE;
        url+=head;
        url+="&des="+etBody.getText()+"&adr="+address.getText()+"&_t="+ AiSouAppInfoModel.getInstance().getToken()+"&tt="+shop_id+"&create=0";

        OkHttpUtils.get().url(url).params(null).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                DataRow row=null;
                try{

                    row=DataRow.parseJson(response);

                    //  DataRow childer=row.getRow("data");
                    if (row.getBoolean("result",false)){
                        row=row.getRow("data");
                        needPayPrice.setText("¥" + row.getString("PAY_PRICE"));
                        tv_waitpay_postorder2.setText("¥"+row.getString("SUB_PRICE"));
                        tv_count_postorder2.setText("¥"+row.getString("POST_PRICE"));//PAY_PRICE
                        tv_count_postorder3.setText("¥" + row.getString("PRODUCT_PRICE"));
                    }else {

                        HintDialogUtil dialogUtil=new HintDialogUtil(PostOrderActivity.this);
                        dialogUtil.showError(row.getString("message"),true,true);
                        dialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                PostOrderActivity.this.finish();
                            }
                        });

                        //  ToastUtil.showLong(row.getString("message"));
                    }

                    // Log.e("计算需要支付的价格:",response);
                }catch (Exception e){
                    ToastUtil.showLong(e.getMessage()+":"+row.getString("message"));
                }
            }
        });
        return "";
    }
//    @Override
//    protected void initData() {
//        //遍历集合得到所有的商品数量
//        for (GoodsItemBean goodsItem : selectedList) {
//            String count = goodsItem.getGoodsCount() + "";
//            totalGoodsCount += Integer.parseInt(count);
//            totalGoodsPrice += goodsItem.getGoodsPrice();
//        }
//        int size = selectedList.size();
//        System.out.println(size + "");
//        needPayPrice.setText("¥" + StringUtils.subZeroAndDot(new BigDecimal(totalGoodsPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()));
//        shopName.setText(shop_name);
////        goodsCount.setText(totalGoodsCount + "");
////    }
//@Override
//protected void initData() {
//    //遍历集合得到所有的商品数量
//    for (GoodsItem goodsItem:selectedList_im){
//        String count = goodsItem.getGoodsCount()+"";
//        totalGoodsCount += Integer.parseInt(count);
//        if (AiSouAppInfoModel.IS_VIP==0){
//            float price=goodsItem.getGoodsUnitPrice()*goodsItem.getGoodsCount();
//            totalGoodsPrice += price;//goodsItem.getGoodsUnitPrice();
//        }else if(AiSouAppInfoModel.IS_VIP==1){
//            float price=goodsItem.getMember_price()*goodsItem.getGoodsCount();
//            totalGoodsPrice += price;//goodsItem.getMember_price();
//        }
//
//
//    }
//    int size = selectedList.size();
//    System.out.println(size+"");
//    //  needPayPrice.setText("¥" + totalGoodsPrice);
//    shopName.setText(shop_name);
//    goodsCount.setText(totalGoodsCount+"");
//}
private int totalGoodsCount;
    private float totalGoodsPrice;
    @Override
    protected void initData() {
        //遍历集合得到所有的商品数量
        for (GoodsItem goodsItem:selectedList){
            String count = goodsItem.getGoodsCount()+"";
            totalGoodsCount += Integer.parseInt(count);
            if (AiSouAppInfoModel.IS_VIP==0){
                float price=goodsItem.getGoodsUnitPrice()*goodsItem.getGoodsCount();
                totalGoodsPrice += price;//goodsItem.getGoodsUnitPrice();
            }else if(AiSouAppInfoModel.IS_VIP==1){
                float price=goodsItem.getMember_price()*goodsItem.getGoodsCount();
                totalGoodsPrice += price;//goodsItem.getMember_price();
            }


        }
        int size = selectedList.size();
        System.out.println(size+"");
        //  needPayPrice.setText("¥" + totalGoodsPrice);
        shopName.setText(shop_name);
        goodsCount.setText(totalGoodsCount+"");
    }
    String createOrder(){

        Stack<Activity> ac_list= BaseConfig.sAppManager.ACTIVITIES;
        for (int a = 0; a < ac_list.size(); a++) {
            if (ac_list.get(a) instanceof MerchantHomePageActivity){
                MerchantHomePageActivity cc= (MerchantHomePageActivity) ac_list.get(a);
                cc.getBuyCartView().restoreData();

                break;
            }
        }

        String url=pay_url+"&des="+etBody.getText()+"&mobile="+seller_mobile.getText()+""+"&name="+seller_name.getText()+""+"&school="+ AiSouAppInfoModel.SCHOOL_CD+"&build="+AiSouAppInfoModel.BUILD_CD+"&room="+address.getText()+"&_t="+ AiSouAppInfoModel.getInstance().getToken()+"&tt="+shop_id+"&create=1";

        Log.e("生成订单==",""+url);
        OkHttpUtils.get().url(url).params(null).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

                ToastUtil.showLong("生成订单出错:"+e.getMessage());

            }

            @Override
            public void onResponse(String response, int id) {

                DataRow row=DataRow.parseJson(response);

                if (row.getBoolean("result",false)){
                    DataRow childer=row.getRow("data");
                    needPayPrice.setText("¥" + childer.getString("PAY_PRICE"));
                    HashMap<String,String> map=new HashMap<String, String>();
                    map.put("order",childer.getString("ID"));
                    //   map.put("_t",AiSouApplication.getInstance().getToken());
                    map.put("type","app");

                     dialog=new PayDialog(PostOrderActivity.this,Gravity.CENTER,true,childer.getString("ID"),childer.getString("PAY_PRICE")+"");

                    dialog.showDialog(true,true);

                }else {

                    ToastUtil.showLong(row.getString("message"));
                }

                Log.e("创建订单:",response);
            }
        });
        return "";
    }
    PayDialog dialog;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_back:
                finish();
                break;
            case net.twoant.master.R.id.rl_dispatchmethod_postorder:
//                showDialog();
                break;
            case net.twoant.master.R.id.btn_post_postorder:
//                PayOrderBean payOrderBean = new PayOrderBean(PayOrderBean.TYPE_GOODS, StringUtils.subZeroAndDot(new BigDecimal(totalGoodsPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString()),
//                        shop_id, shop_name, null, "1", etBody.getText().toString().trim());
//                if (selectedList instanceof ArrayList) {
//                    payOrderBean.setGoodsItemBean((ArrayList<GoodsItemBean>) selectedList);
//                }
//                PayOrderActivity.startActivity(payOrderBean, this);//// TODO: 2017/3/24 sortType暂时写死
////                PayDetailActivity.startActivity(PostOrderActivity.this,totalGoodsPrice,shop_name,
//// shop_id,sort,etBody.getText().toString().trim());
                createOrder();
                break;
            case net.twoant.master.R.id.rb_store_consume:
                tvDispatchMethod.setText("店内消费");
                alertDialog.dismiss();
                sort = 1;
                break;
            case net.twoant.master.R.id.rb_store_dispatch:
                tvDispatchMethod.setText("商家配送");
                alertDialog.dismiss();
                sort = 2;
                break;
            case net.twoant.master.R.id.rb_platform_dispatch:
                tvDispatchMethod.setText("平台配送");
                alertDialog.dismiss();
                sort = 3;
                break;
            case net.twoant.master.R.id.rb_selfe_dispatch:
                tvDispatchMethod.setText("买家自取");
                alertDialog.dismiss();
                sort = 4;
                break;
        }
    }

    private void showDialog() {
        View view = View.inflate(this, net.twoant.master.R.layout.zy_dialog_dispatch, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(view);
        alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        alertDialog.show();
        view.findViewById(net.twoant.master.R.id.rb_store_consume).setOnClickListener(this);
        view.findViewById(net.twoant.master.R.id.rb_store_dispatch).setOnClickListener(this);
        view.findViewById(net.twoant.master.R.id.rb_platform_dispatch).setOnClickListener(this);
        view.findViewById(net.twoant.master.R.id.rb_selfe_dispatch).setOnClickListener(this);
    }

    class MyAdapter extends BasicAdapter {

        public MyAdapter(List list) {
            super(list);
        }

        @Override
        protected BaseHolder getHolder(int position) {
            return new PayOrderHoler();
        }
    }

    class PayOrderHoler extends BaseHolder<GoodsItemBean> {
        ImageView imageView;

        @Override
        public View initHolderView() {
            View view = View.inflate(PostOrderActivity.this, net.twoant.master.R.layout.zy_item_goods_postorder, null);
            goodsName = (TextView) view.findViewById(net.twoant.master.R.id.tv_goodsname_postorder);
            singlePrice = (TextView) view.findViewById(net.twoant.master.R.id.tv_singleprice_postorder);
            goodsNum = (TextView) view.findViewById(net.twoant.master.R.id.tv_num_postorder);
            imageView = (ImageView) view.findViewById(net.twoant.master.R.id.iv_goodsimg_postorder);
            return view;
        }

        @Override
        public void bindData(GoodsItemBean data) {
            int count = data.getGoodsCount();
            float goodsPrice = AiSouAppInfoModel.IS_VIP==1?data.getMember_price():data.getGoodsUnitPrice();
            goodsName.setText(data.getGoodsName());
            // float v = goodsPrice / count;
            singlePrice.setText(goodsPrice+"");
            goodsNum.setText("x" + count);
            ImageLoader.getImageFromNetwork(imageView,data.getGoods_img());
//            int count = data.getGoodsCount();
//            float goodsPrice = data.getGoodsPrice();
//            goodsName.setText(data.getGoodsName());
//            float v = goodsPrice / count;
//            singlePrice.setText(v + "");
//            goodsNum.setText("x" + count);

//            SearchGoodsHttpUtils.LongHttp(data.getGoodsId(), "", "", "", "", new StringCallback() {
//                @Override
//                public void onError(Call call, Exception e, int id) {
//                }
//
//                @Override
//                public void onResponse(String response, int id) {
//                    GoodsDetailBean goodsDetailBean = GsonUtil.gsonToBean(response, GoodsDetailBean.class);
//                    GoodsDetailBean.ResultBean result = goodsDetailBean.getResult();
//                    ImageLoader.getImageFromNetworkPlaceholderControlImg(imageView, BaseConfig.getCorrectImageUrl(result.getGoods_img()), PostOrderActivity.this, R.drawable.ic_def_small);
//                }
//            });
        }
    }
}
