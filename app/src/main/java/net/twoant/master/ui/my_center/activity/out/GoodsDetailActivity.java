package net.twoant.master.ui.my_center.activity.out;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CartAnimationUtils;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.bean.GoodsItemBean;
import net.twoant.master.ui.main.widget.UMShareHelper;
import net.twoant.master.ui.my_center.bean.GoodsDetailBean;
import net.twoant.master.ui.my_center.bean.GoodsDetailImageBean;
import net.twoant.master.ui.my_center.httputils.SearchGoodsHttpUtils;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.ui.other.bean.HttpResultBean;
import net.twoant.master.widget.MarqueeTextView;
import net.twoant.master.widget.entry.DataRow;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by DZY on 2016/11/28.
 * Modify by S_Y_H on 2016/12/10 11:50 添加静态启动方法
 */

public class GoodsDetailActivity extends LongBaseActivity implements View.OnClickListener {

    private ConvenientBanner convenientBanner;
    private ArrayList<String> localImages = new ArrayList<>();
    private ImageView ivAdd, ivMinus, detailImg1, detailImg2, detailImg3, detailImg4;
    private TextView tvCountCart, needPayNum, tvPrice, needPayPrice, tvGoodsName, shopName, tvClick;
    private MarqueeTextView tvAddress;
    private RelativeLayout rlContainer;
    private Handler mHanlder;
    private float soloPrice;
    private String goods_id;
    private String shop_id;
    public static int goods_where;
    private int positionList = -1;//goods_id 位于selectedList集合哪个位置,存-1为防止传入的list没有此页的goods_id,bottomsheet默认选择第一个显示"+""-"
    /**
     * 传进的数据集合
     */
    public static ArrayList<GoodsItemBean> selectedList;//bottomsheet选中集合
    private SimpleStringRecyclerViewAdapter adapter;
    private GoodsDetailImageBean.ResultBean result;
    public final static String EXTRA_GOODS_ID = "goods_id";
    public final static String EXTRA_GOODS_LIST = "goods_list";
    public final static String EXTRA_GOODS_FROMWHERE = "goods_from";
    public final static int FROM_HOME = 0X199;
    private Button btSubmit;
    private String shop_name;
    private String mShopAvatar;
    public float payPrice;
    public float totalPrice;//selectedList的总价
    public int totalCount;//selectedList的总数量
    public WebView webView;
    private UMShareHelper mUmShareHelper;
    public HintDialogUtil hintDialogUtil;

    private boolean noNet = false;
    public ImageView ivCollect;
    private BottomSheetDialog dialog;

    /**
     * @param activity   实例
     * @param goodsId    商品id
     * @param goodsItems bean实体，可为空。
     */
    public static void startActivity(Activity activity, @NonNull String goodsId, ArrayList<GoodsItemBean> goodsItems) {
        Intent intent = new Intent(activity, GoodsDetailActivity.class);
        intent.putExtra(EXTRA_GOODS_ID, goodsId);
        intent.putExtra(EXTRA_GOODS_LIST, goodsItems);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.zy_activity_goodsdetail;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            goods_id = intent.getStringExtra(EXTRA_GOODS_ID);
            goods_where = intent.getIntExtra(EXTRA_GOODS_FROMWHERE, 0);
            //获取传进来的List<GoodsItemBean>
            selectedList = intent.getParcelableArrayListExtra(EXTRA_GOODS_LIST);
            if (null != selectedList) {
                StringBuffer stringBuffer = new StringBuffer();
                for (GoodsItemBean goodsItem : selectedList) {
                    stringBuffer.append(goodsItem);
                }
//                System.out.println(stringBuffer);
            }
        }
        initView();
        AiSouAppInfoModel.getInstance().addOrderActivity(this);
        //商品点击量
//        postNetclicktNum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectedList = null;
    }

    private void initView() {
        convenientBanner = (ConvenientBanner) findViewById(net.twoant.master.R.id.cb_convenientBanner_gooodsdetail);
        int screenWidth = DisplayDimensionUtils.getScreenWidth();
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) convenientBanner.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.width = screenWidth;
        linearParams.height = (int) (screenWidth / 640f * 500f);
        convenientBanner.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        ivAdd = (ImageView) findViewById(net.twoant.master.R.id.iv_addd_goodsdetail);
        tvGoodsName = (TextView) findViewById(net.twoant.master.R.id.tv_name_goodsdetail);
        tvCountCart = (TextView) findViewById(net.twoant.master.R.id.tv_countcart_goodsdetail);
        needPayNum = (TextView) findViewById(net.twoant.master.R.id.tv_number_goodsdetail);
        rlContainer = (RelativeLayout) findViewById(net.twoant.master.R.id.rl_container);
        ivMinus = (ImageView) findViewById(net.twoant.master.R.id.iv_minus_goodsdetail);
        tvPrice = (TextView) findViewById(net.twoant.master.R.id.tv_price_goodsdetail);
        tvClick = (TextView) findViewById(net.twoant.master.R.id.tv_clickcount_goodsdetail);
        webView = (WebView) findViewById(net.twoant.master.R.id.web_goodsdtail);
        needPayPrice = (TextView) findViewById(net.twoant.master.R.id.m_list_all_price);
        tvAddress = (MarqueeTextView) findViewById(net.twoant.master.R.id.tv_address_goodsdetail);
        ivCollect = (ImageView) findViewById(net.twoant.master.R.id.iv_collect_goodsdetail);
        detailImg1 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetail);
        detailImg2 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai2);
        detailImg3 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai3);
        detailImg4 = (ImageView) findViewById(net.twoant.master.R.id.iv_detaildt1_goodsdetai4);
        shopName = (TextView) findViewById(net.twoant.master.R.id.tv_shopname_goodsdetail);
        btSubmit = (Button) findViewById(net.twoant.master.R.id.m_list_submit);
        btSubmit.setOnClickListener(this);
        ivCollect.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_share_goodsdetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_friend_goodsdetail).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.m_list_bt).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        mHanlder = new Handler(getMainLooper());
        ivAdd.setOnClickListener(this);
        ivMinus.setOnClickListener(this);
        goods_id = getIntent().getStringExtra("goods_id");
        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
        tvAddress.setText(instance.getAiSouLocationBean().getCurrentCompletionAddress());

        hintDialogUtil = new HintDialogUtil(this);

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
                //设置指示器的方向
                .setPageIndicator(new int[]{net.twoant.master.R.drawable.yh_banner_grey_dot, net.twoant.master.R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setCanLoop(true);
        if (localImages.size() > 1) {
            convenientBanner.startTurning(5000);
        }

        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                displayImage(position);
            }
        });
    }

    @Override
    protected void initData() {
        if (null == selectedList) selectedList = new ArrayList();
        adapter = new SimpleStringRecyclerViewAdapter();
        //从selectedList集合中取出位置
        for (int i = 0; i < selectedList.size(); i++) {
            totalCount += selectedList.get(i).getGoodsCount();
            totalPrice += selectedList.get(i).getGoodsPrice();
            String id = selectedList.get(i).getGoodsId();
            if (id.equals(goods_id)) {
                //如果从首页selectedList带进来的值被选中，初始化值
                positionList = i;
                ivMinus.setVisibility(View.VISIBLE);
                needPayNum.setVisibility(View.VISIBLE);
                tvCountCart.setVisibility(View.VISIBLE);
                needPayNum.setText(selectedList.get(positionList).getGoodsCount() + "");
            }
        }
        //若selectedList中有值
        if (0 != selectedList.size()) {
            btSubmit.setEnabled(true);
            tvCountCart.setVisibility(View.VISIBLE);
            //如果positionList为-1，意:所进入的商品详情页selectedList中没有此页goods_id
            if (positionList != -1) {
                needPayNum.setText(selectedList.get(positionList).getGoodsCount() + "");
            }
            btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.title_color));
        } else {

        }
        needPayPrice.setText(" ¥" + totalPrice);
        tvCountCart.setText(totalCount + "");

        // SDK1.5本地文件处理(不能显示图片)
        //MyWebView.loadData(URLEncoder.encode(data, encoding), mimeType, encoding);
        // SDK1.6及以后版本
        //MyWebView.loadData(data, mimeType, encoding);
        // 本地文件处理(能显示图片)
        //获取用户对商品的状态
        getCollectSate();

    }

    private void postNetclicktNum() {
        LongHttpGet(ApiConstants.BASE + "gds/clk?user=" + AiSouAppInfoModel.getInstance().getUID() + "&goods=" + goods_id, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
            }
        });
    }

    public void getCollectSate() {
        Map<String, String> map = new HashMap();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("id", goods_id);
        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.GET_COLLECT_STATE, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.showError("连接失败");
                noNet = true;
                hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (noNet) {
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.d(response);
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                String data = dataRow.getString("data");
                if ("0".equals(data)) {
                    //为收藏
                    ivCollect.setBackgroundResource(net.twoant.master.R.drawable.ic_collection_uncheck);
                } else if ("1".equals(data)) {
                    //已收藏
                    ivCollect.setBackgroundResource(net.twoant.master.R.drawable.ic_action_collection_check);
                }
            }
        });
    }

    private void alterCollect() {
        Map<String, String> map = new HashMap();
        map.put("user", AiSouAppInfoModel.getInstance().getUID());
        map.put("id", goods_id);
//        hintDialogUtil.showLoading();
        LongHttp(ApiConstants.ALTER_COLLECT_GOODS, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                hintDialogUtil.showError("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
//                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                String data = dataRow.getString("data");
                if ("0".equals(data)) {
                    //为收藏
                    ivCollect.setBackgroundResource(net.twoant.master.R.drawable.ic_collection_uncheck);
                } else if ("1".equals(data)) {
                    //已收藏
                    ivCollect.setBackgroundResource(net.twoant.master.R.drawable.ic_action_collection_check);
                }
            }
        });
    }

    @Override
    protected void requestNetData() {
        SearchGoodsHttpUtils.LongHttp(goods_id, "", "true", AiSouAppInfoModel.getInstance().getUID(), "", new StringCallback() {
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
                String goods_img_txt = "<HTML>" + result.getGoods_img_txt();
                webView.loadDataWithBaseURL(null, goods_img_txt, null, "UTF-8", null);
                String goods_bander_dt1 = result.getGoods_bander_dt1();
                if (TextUtils.isEmpty(goods_bander_dt1)) {
                    return;
                } else {
                    ImageLoader.getImageFromNetwork(detailImg1, BaseConfig.getCorrectImageUrl(goods_bander_dt1), GoodsDetailActivity.this);
                    String goods_bander_dt2 = result.getGoods_bander_dt2();
                    if (TextUtils.isEmpty(goods_bander_dt2)) {
                        return;
                    } else {
                        ImageLoader.getImageFromNetwork(detailImg2, BaseConfig.getCorrectImageUrl(goods_bander_dt2), GoodsDetailActivity.this);
                        String goods_bander_dt3 = result.getGoods_bander_dt3();
                        if (TextUtils.isEmpty(goods_bander_dt3)) {
                            return;
                        } else {
                            ImageLoader.getImageFromNetwork(detailImg3, BaseConfig.getCorrectImageUrl(goods_bander_dt3), GoodsDetailActivity.this);
                            String goods_bander_dt4 = result.getGoods_bander_dt4();
                            if (TextUtils.isEmpty(goods_bander_dt4)) {
                                return;
                            } else {
                                ImageLoader.getImageFromNetwork(detailImg4, BaseConfig.getCorrectImageUrl(goods_bander_dt4), GoodsDetailActivity.this);
                            }
                        }
                    }
                }
            }
        });

        SearchGoodsHttpUtils.LongHttp(goods_id, "", "", "", "1", new StringCallback() {
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
                mShopAvatar = resultBean.getGoods_img();
                shopName.setText(shop_name);
                shopName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                shopName.getPaint().setAntiAlias(true);//抗锯齿
                shopName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MerchantHomePageActivity.startActivity(GoodsDetailActivity.this, shop_id);
//                        finish();
                    }
                });
                tvGoodsName.setText(resultBean.getGoods_name());
                tvClick.setText("点击量:" + resultBean.getClick());
                tvPrice.setText(resultBean.getGoods_price() + "");
                String price = tvPrice.getText().toString().trim();
                soloPrice = Float.parseFloat(price);
                shop_id = resultBean.getGoods_pk_shop() + "";
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
                System.out.println(count);
                firstAdd(count, v);
                break;
            case net.twoant.master.R.id.iv_minus_goodsdetail:
                firstDelete(count);
                break;
            case net.twoant.master.R.id.iv_collect_goodsdetail:
                alterCollect();
                break;
            case net.twoant.master.R.id.m_list_submit:

                if (null != selectedList) {
                    //PostOrderActivity.startActivity(GoodsDetailActivity.this, selectedList, shop_name, shop_id);
                }
                break;
            case net.twoant.master.R.id.iv_back:
                setResultData();
                finish();
                break;
            case net.twoant.master.R.id.iv_share_goodsdetail:
                //分享
                if (mUmShareHelper == null)
                    mUmShareHelper = new UMShareHelper(GoodsDetailActivity.this);
                mUmShareHelper.showDialogAtBottom("通过以下方式邀请朋友参与活动", true);
                break;
            case net.twoant.master.R.id.iv_friend_goodsdetail:
                //聊天，联系卖家
                v.setClickable(false);
                UserInfoUtil.startChat(null, shop_id, GoodsDetailActivity.this);
                v.setClickable(true);
                break;
            case net.twoant.master.R.id.m_list_bt:
                //显示bottommSheet
                showBottomSheet();
                break;
        }
    }

    private void firstAdd(int count, View v) {
        if (count < 1) {
            ivMinus.setAnimation(CartAnimationUtils.getShowAnimation());
            ivMinus.setVisibility(View.VISIBLE);
            btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.title_color));
            btSubmit.setEnabled(true);
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
//            ivMinus.setAnimation(CartAnimationUtils.getHiddenAnimation());
            ivMinus.setVisibility(View.INVISIBLE);
            //测试结束将0改为positionList
            selectedList.remove(positionList);
            if (selectedList.size() == 0) {
                btSubmit.setEnabled(false);
                btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.light_white));
            }
            needPayNum.setVisibility(View.INVISIBLE);
        }
        count--;
        System.out.println(count + "");
        remove();//activity.getSelectedItemCountById(item.id)
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
        m.put("aisou_id", AiSouAppInfoModel.getInstance().getUID());
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

    //添加商品到购物车
    private void add() {
        String numstr = needPayNum.getText().toString().trim();
        int num = Integer.parseInt(numstr);//需要购买的数量
        totalCount++;
        if (num > 0) {
            tvCountCart.setVisibility(View.VISIBLE);
        } else {
            tvCountCart.setVisibility(View.INVISIBLE);
        }
        tvCountCart.setText(totalCount + "");
        float v = num * soloPrice;
        totalPrice += soloPrice;
        BigDecimal bigDecimal = new BigDecimal(totalPrice);
        totalPrice = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        BigDecimal b = new BigDecimal(v);
        payPrice = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        needPayPrice.setText(" ￥" + totalPrice);
        if (positionList == -1) {
            addSelectedItem(numstr);
        } else {
            selectedList.get(positionList).setGoodsPrice(payPrice + "");
            selectedList.get(positionList).setGoodsCount(num);
        }
        adapter.notifyDataSetChanged();
    }

    private void addSelectedItem(String numstr) {
        GoodsItemBean goodsItem1 = new GoodsItemBean();
        goodsItem1.setGoodsName(tvGoodsName.getText().toString().trim());
        goodsItem1.setGoodsCount(numstr);
        goodsItem1.setGoodsPrice(payPrice);
        goodsItem1.setGoodsId(goods_id);
        if (null == selectedList)
            selectedList = new ArrayList<>();

        selectedList.add(goodsItem1);
        positionList = selectedList.size() - 1;
    }

    //删除操作
    private void remove() {
        String numstr = needPayNum.getText().toString().trim();
        System.out.println(numstr);
        int num = Integer.parseInt(numstr) - 1;//需要支付的数量
        System.out.println(num);
        //详情页单商品的数量
        if (num > 0) {
            tvCountCart.setVisibility(View.VISIBLE);
        } else {//tvCountCart.setVisibility(View.INVISIBLE);
            //删除单商品为0时
            positionList = -1;
            tvCountCart.setVisibility(View.INVISIBLE);
        }
        tvCountCart.setText(--totalCount + "");
        float v = num * soloPrice;
        totalPrice = totalPrice - soloPrice;
        BigDecimal bigDecimal = new BigDecimal(totalPrice);
        float integral2f = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        needPayPrice.setText(" ￥" + (integral2f));
        //selectedList.add(goodsItem);
        if (selectedList.size() == 0) {
            btSubmit.setEnabled(false);
            btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.light_white));
        } else {
            if (positionList != -1) {
                selectedList.get(positionList).setGoodsPrice(v);
                selectedList.get(positionList).setGoodsCount(num);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void playAnimation(int[] start_location) {
        ImageView img = new ImageView(this);
        img.setImageResource(net.twoant.master.R.drawable.goods_anim_point);
        setAnim(img, start_location);
    }

    private void setAnim(final View v, int[] start_location) {
        addViewToAnimLayout(rlContainer, v, start_location);
        Animation set = createAnim(start_location[0], start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                add();
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rlContainer.removeView(v);
                    }
                }, 100);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(set);
    }

    private void addViewToAnimLayout(final ViewGroup vg, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y - loc[1]);
        vg.addView(view);
    }

    private Animation createAnim(int startX, int startY) {
        int[] des = new int[2];
        tvCountCart.getLocationInWindow(des);//设置动画去的位置
        AnimationSet set = new AnimationSet(false);
        Animation translationX = new TranslateAnimation(0, des[0] - startX, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1] - startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1, 0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
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
            ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(localImages.get(position)), GoodsDetailActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null == selectedList) {
            String trim = tvCountCart.getText().toString().trim();
            if (Integer.parseInt(trim) > 0) {
                tvCountCart.setText("0");
                needPayNum.setText("0");
                positionList = -1;
                totalPrice = 0;
                totalCount = 0;
                ivMinus.setVisibility(View.INVISIBLE);
                tvCountCart.setVisibility(View.INVISIBLE);
                btSubmit.setEnabled(false);
                btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.light_white));
            }
            selectedList = new ArrayList<>();
        }
    }

    // 开始自动翻页
    @Override
    protected void onResume() {
        super.onResume();
        //开始自动翻页
        if (localImages.size() > 1) {
            convenientBanner.startTurning(8000);
        }
    }

    // 停止自动翻页
    @Override
    protected void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    private void showBottomSheet() {
        if (null == dialog) {
            dialog = new BottomSheetDialog(this);
        }
        View view = LayoutInflater.from(this).inflate(net.twoant.master.R.layout.layout_bottom_sheet, null);
        view.findViewById(net.twoant.master.R.id.tv_clear_bottomsheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = new ArrayList<>();
                adapter.notifyDataSetChanged();
                ivMinus.setVisibility(View.INVISIBLE);
                needPayNum.setText("0");
                needPayNum.setVisibility(View.INVISIBLE);
                tvCountCart.setText("0");
                tvCountCart.setVisibility(View.INVISIBLE);
                needPayPrice.setText("0.0");
                btSubmit.setEnabled(false);
                btSubmit.setBackgroundColor(CommonUtil.getColor(net.twoant.master.R.color.light_white));
                totalCount = 0;
                totalPrice = 0;
                positionList = -1;
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(net.twoant.master.R.id.selectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        dialog.setContentView(view);
        dialog.show();
    }


    class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

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
            holder.sheetPrice.setText(String.valueOf("¥ " + goodsPrice));
            holder.sheetCount.setText(String.valueOf(selectedList.get(position).getGoodsCount()));
            if (positionList == -1) {
                holder.sheetMinus.setVisibility(View.INVISIBLE);
                holder.sheetAdd.setVisibility(View.INVISIBLE);
            }
            if (position == positionList) {
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
                    firstAdd(count, ivAdd);
                }
            });

        }

        @Override
        public int getItemCount() {
            return null == selectedList ? 0 : selectedList.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUmShareHelper != null)
            mUmShareHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUmShareHelper != null)
            mUmShareHelper.onConfigurationChanged();
    }

    /**
     * 展示照片
     */
    private void displayImage(int position) {
        ArrayList<String> images = new ArrayList<>();
        if (localImages != null) {
            for (String str : localImages)
                images.add(ApiConstants.CURRENT_SELECT + str);
        } else {
            return;
        }
//        Intent intent = new Intent(this, ImagePagerActivity.class);
        Intent intent = new Intent(this, ImageScaleActivity.class);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, images);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
        this.overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
    }

  /*  @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("selectedList", (ArrayList) selectedList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selectedList = (ArrayList) savedInstanceState.getSerializable("selectedList");
    }*/
}
