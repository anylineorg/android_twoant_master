package net.twoant.master.ui.my_center.activity.out;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CartAnimationUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.my_center.bean.GoodsDetailBean;
import net.twoant.master.ui.my_center.bean.GoodsDetailImageBean;
import net.twoant.master.ui.my_center.bean.GoodsItem;
import net.twoant.master.ui.my_center.httputils.SearchGoodsHttpUtils;
import net.twoant.master.ui.other.bean.HttpResultBean;
import net.twoant.master.widget.MarqueeTextView;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/28.
 * Modify by S_Y_H on 2016/12/10 11:50 添加静态启动方法
 */

public class GoodsDetailSellerActivity extends LongBaseActivity implements View.OnClickListener {

    private ConvenientBanner convenientBanner;
    private ArrayList<String> localImages = new ArrayList<>();
    private ImageView ivMinus,detailImg1,detailImg2,detailImg3,detailImg4;
    private TextView needPayNum,tvPrice,tvGoodsName;
    private MarqueeTextView tvAddress;
    private RelativeLayout rlContainer;
    private Handler mHanlder;
    private float soloPrice;
    private String goods_id;
    private String shop_id;
    private int positionList =-1;//goods_id 位于selectedList集合哪个位置,存-1为防止传入的list没有此页的goods_id,bottomsheet默认选择第一个显示"+""-"
    /**
     * 传进的数据集合
     */
    private static ArrayList<GoodsItemBean> selectedList;//bottomsheet选中集合
    private GoodsDetailImageBean.ResultBean result;
    public final static String EXTRA_GOODS_ID = "goods_id";
    public final static String EXTRA_GOODS_LIST = "goods_list";
    private String shop_name;
    public float payPrice;
    public float totalPrice;//selectedList的总价
    public int totalCount;//selectedList的总数量
    private WebView webView;
    /**
     * @param activity   实例
     * @param goodsId    商品id
     * @param goodsItems bean实体，可为空。
     */
    public static void startActivity(Activity activity, @NonNull String goodsId, ArrayList<GoodsItem> goodsItems) {
        Intent intent = new Intent(activity, GoodsDetailSellerActivity.class);
        intent.putExtra(EXTRA_GOODS_ID, goodsId);
        intent.putExtra(EXTRA_GOODS_LIST, goodsItems);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_goodsdetail_seller;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            goods_id = intent.getStringExtra(EXTRA_GOODS_ID);
            //获取传进来的List<GoodsItem>
            selectedList = (ArrayList<GoodsItemBean>) intent.getSerializableExtra(EXTRA_GOODS_LIST);
            if (null!=selectedList){
                StringBuffer stringBuffer = new StringBuffer();
                for (GoodsItemBean goodsItem:selectedList){
                    stringBuffer.append(goodsItem);
                }
            }
        }
        initView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedList = null;
    }

    private void initView() {
        convenientBanner = (ConvenientBanner) findViewById(net.twoant.master.R.id.cb_convenientBanner_gooodsdetail);
        int screenWidth = DisplayDimensionUtils.getScreenWidth();
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) convenientBanner.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.width = screenWidth;
        linearParams.height = (int) (screenWidth/640f*500f);
        convenientBanner.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        tvGoodsName = (TextView) findViewById(net.twoant.master.R.id.tv_name_goodsdetail);
        needPayNum = (TextView) findViewById(net.twoant.master.R.id.tv_number_goodsdetail);
        rlContainer = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_container);
        ivMinus = (ImageView) findViewById(net.twoant.master.R.id.iv_minus_goodsdetail);
        tvPrice = (TextView) findViewById(net.twoant.master.R.id.tv_price_goodsdetail);
        tvAddress = (MarqueeTextView) findViewById(net.twoant.master.R.id.tv_address_goodsdetail);
        detailImg1 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetail);
        detailImg2 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai2);
        detailImg3 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai3);
        detailImg4 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai4);
        webView = (WebView) findViewById(net.twoant.master.R.id.web_goodsdtail);
        findViewById(net.twoant.master.R.id.iv_share_goodsdetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_friend_goodsdetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        mHanlder = new Handler(getMainLooper());
        ivMinus.setOnClickListener(this);
        goods_id = getIntent().getStringExtra("goods_id");
        AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        tvAddress.setText(instance.getCompletionAddress());
    }

    private void setPageIndicator() {
        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{net.twoant.master.R.drawable.ic_page_indicator, net.twoant.master.R.drawable.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        if (localImages.size()>1) {
            convenientBanner.startTurning(5000);
        }
      /*  convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);*/
    }

    @Override
    protected void initData() {
        if (null==selectedList) selectedList = new ArrayList();
        //从selectedList集合中取出位置
        for(int i=0; i< selectedList.size();i++){
            totalCount += selectedList.get(i).getGoodsCount();
            totalPrice += selectedList.get(i).getGoodsPrice();
            String id = selectedList.get(i).getGoodsId();
            if (id.equals(goods_id)){
                //如果从首页selectedList带进来的值被选中，初始化值
                positionList = i;
                ivMinus.setVisibility(View.VISIBLE);
                needPayNum.setVisibility(View.VISIBLE);
                needPayNum.setText(selectedList.get(positionList).getGoodsCount()+"");
            }
        }

    }

    @Override
    protected void requestNetData() {
       SearchGoodsHttpUtils.LongHttp(goods_id, "", "", "","1", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d("goods_id:" + goods_id + response);
                //详情图
                GoodsDetailBean.ResultBean resultBean = GsonUtil.gsonToBean(response, GoodsDetailBean.class).getResult();
                shop_name = resultBean.getShop_name();
                tvGoodsName.setText(resultBean.getGoods_title());
                tvPrice.setText(resultBean.getGoods_price()+"");
                String price = tvPrice.getText().toString().trim();
                soloPrice = Float.parseFloat(price);
                shop_id = resultBean.getGoods_pk_shop()+"";
            }
        });
        SearchGoodsHttpUtils.LongHttp(goods_id, "", "true", AiSouAppInfoModel.getInstance().getUID(),"", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.d("");
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                result = GsonUtil.gsonToBean(response, GoodsDetailImageBean.class).getResult();
                //对商品的展示图做添加判断
                addShowImg();

                //设置轮播
                setPageIndicator();
                String goods_img_txt = "<HTML>"+result.getGoods_img_txt();
                webView.loadDataWithBaseURL(null, goods_img_txt, null, "UTF-8", null);
                String goods_bander_dt1 = result.getGoods_bander_dt1();
                if (TextUtils.isEmpty(goods_bander_dt1)) {
                    return;
                } else {
                    ImageLoader.getImageFromNetwork(detailImg1, BaseConfig.getCorrectImageUrl( goods_bander_dt1),GoodsDetailSellerActivity.this);
                    String goods_bander_dt2 = result.getGoods_bander_dt2();
                    if (TextUtils.isEmpty(goods_bander_dt2)) {
                        return;
                    } else {
                        ImageLoader.getImageFromNetwork(detailImg2, BaseConfig.getCorrectImageUrl( goods_bander_dt2),GoodsDetailSellerActivity.this);
                        String goods_bander_dt3 = result.getGoods_bander_dt3();
                        if (TextUtils.isEmpty(goods_bander_dt3)) {
                            return;
                        } else {
                            ImageLoader.getImageFromNetwork(detailImg3, BaseConfig.getCorrectImageUrl( goods_bander_dt3),GoodsDetailSellerActivity.this);
                            String goods_bander_dt4 = result.getGoods_bander_dt4();
                            if (TextUtils.isEmpty(goods_bander_dt4)) {
                                return;
                            } else {
                                ImageLoader.getImageFromNetwork(detailImg4, BaseConfig.getCorrectImageUrl( goods_bander_dt4) ,GoodsDetailSellerActivity.this);
                            }
                        }
                    }
                }
            }
        });
    }

    private void addShowImg() {
        String goods_bander_img1 = result.getGoods_bander_img1();
        if (TextUtils.isEmpty(goods_bander_img1)) {
            return;
        } else {
            localImages.add(goods_bander_img1);
            String goods_bander_img2 = result.getGoods_bander_img2();
            if (TextUtils.isEmpty(goods_bander_img2)) {
                return;
            } else {
                localImages.add(goods_bander_img2);
                String goods_bander_img3 = result.getGoods_bander_img3();
                if (TextUtils.isEmpty(goods_bander_img3)) {
                    return;
                } else {
                    localImages.add(goods_bander_img3);
                    String goods_bander_img4 = result.getGoods_bander_img4();
                    if (TextUtils.isEmpty(goods_bander_img4)) {
                        return;
                    } else {
                        localImages.add(goods_bander_img4);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResultData();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int count = Integer.parseInt(needPayNum.getText().toString().trim());
        switch (v.getId()) {
            case net.twoant.master.R.id.iv_addd_goodsdetail:
                firstAdd(count,v);
                break;
            case net.twoant.master.R.id.iv_minus_goodsdetail:
                firstDelete(count);
                break;
            case net.twoant.master.R.id.m_list_submit:
                int size = selectedList.size();
                System.out.println(size+"");
               // PostOrderActivity.startActivity(GoodsDetailSellerActivity.this,selectedList,shop_name,shop_id);
                break;
            case net.twoant.master.R.id.iv_back:
                setResultData();
                finish();
                break;
            case net.twoant.master.R.id.iv_share_goodsdetail:
                //分享
                break;
            case net.twoant.master.R.id.iv_friend_goodsdetail:
                //聊天，联系卖家
                startActivity(new Intent(GoodsDetailSellerActivity.this, ChatWithSellerActivity.class));
                break;
        }
    }

    private void firstAdd(int count,View v) {
        if (count < 1) {
            ivMinus.setAnimation(CartAnimationUtils.getShowAnimation());
            ivMinus.setVisibility(View.VISIBLE);
            needPayNum.setVisibility(View.VISIBLE);
        }
        count++;
        needPayNum.setText(String.valueOf(count));
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        playAnimation(loc);
    }

    private void firstDelete(int count) {
        if (count < 2) {
            ivMinus.setAnimation(CartAnimationUtils.getHiddenAnimation());
            ivMinus.setVisibility(View.GONE);
            //测试结束将0改为positionList
            selectedList.remove(positionList);
            if (selectedList.size() == 0) {

            }
            needPayNum.setVisibility(View.GONE);
        }
        count--;
        System.out.println(count+"");
        needPayNum.setText(String.valueOf(count));
    }

    /**
     * 回传数据
     */
    private void setResultData() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_GOODS_LIST, selectedList);
        setResult(Activity.RESULT_OK, intent);
    }

    private void postCollect() {
        HashMap<String, String> m = new HashMap<>();
        m.put("aisou_id", "14805577");
        m.put("goods_id", goods_id);
        m.put("state", "1");//1收藏、2取消收藏
        LongHttp("collection.do", m, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                HttpResultBean.ResultBean result = GsonUtil.gsonToBean(response, HttpResultBean.class).getResult();
                ToastUtil.showLong(result.getMsg());
            }
        });
    }


    private void addSelectedItem(String numstr) {
        GoodsItemBean goodsItem1 = new GoodsItemBean();
        goodsItem1.setGoodsName(tvGoodsName.getText().toString().trim());
        goodsItem1.setGoodsCount(numstr);
        goodsItem1.setGoodsPrice(payPrice);
        goodsItem1.setGoodsId(goods_id);
        selectedList.add(goodsItem1);
        positionList = selectedList.size()-1;
    }


    public void playAnimation(int[] start_location) {
        ImageView img = new ImageView(this);
        img.setImageResource(net.twoant.master.R.drawable.icon_goodsdetail_plus);
    }



    class LocalImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String data) {
            ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl( localImages.get(position)),GoodsDetailSellerActivity.this);
        }
    }

    // 开始自动翻页
    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }



     class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        public  class ViewHolder extends RecyclerView.ViewHolder {

            public TextView sheetName;
            public TextView sheetPrice;
            public TextView sheetCount;
            public TextView sheetMinus;
            public TextView sheetAdd;

            public ViewHolder(View view) {
                super(view);
                sheetName = (TextView) view.findViewById(net.twoant.master.R.id.tvName);
                sheetPrice = (TextView) view.findViewById(net.twoant.master.R.id.tvCost);
                sheetCount = (TextView) view.findViewById(net.twoant.master.R.id.count);
                sheetMinus = (TextView) view.findViewById(net.twoant.master.R.id.tvMinus);
                sheetAdd = (TextView) view.findViewById(net.twoant.master.R.id.tvAdd);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(net.twoant.master.R.layout.item_selected_goods, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.sheetName.setText(selectedList.get(position).getGoodsName());
            float goodsPrice = selectedList.get(position).getGoodsPrice();
            holder.sheetPrice.setText(String.valueOf("¥ "+goodsPrice));
            holder.sheetCount.setText(String.valueOf(selectedList.get(position).getGoodsCount()));
            if (positionList == -1) {
                holder.sheetMinus.setVisibility(View.INVISIBLE);
                holder.sheetAdd.setVisibility(View.INVISIBLE);
            }
            if (position==positionList) {
                holder.sheetMinus.setVisibility(View.VISIBLE);
                holder.sheetAdd.setVisibility(View.VISIBLE);
            }
            holder.sheetMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToastUtil.showLong("减");
                    int count = Integer.parseInt(needPayNum.getText().toString().trim());
                    firstDelete(count);
                }
            });
            holder.sheetAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToastUtil.showLong("加");
                    int count = Integer.parseInt(needPayNum.getText().toString().trim());

                }
            });

        }

        @Override
        public int getItemCount() {
            return selectedList.size();
        }
    }
}
