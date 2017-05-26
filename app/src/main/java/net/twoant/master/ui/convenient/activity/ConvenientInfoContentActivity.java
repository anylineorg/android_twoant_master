package net.twoant.master.ui.convenient.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.CommentDialog;
import net.twoant.master.widget.DZYToast;
import net.twoant.master.widget.MyScrollView;
import net.twoant.master.widget.OnPostCommentListener;
import net.twoant.master.widget.PassViewDialog;
import net.twoant.master.widget.ReportDialog;
import net.twoant.master.widget.RewardIntegralDialog;
import net.twoant.master.widget.ScrollViewListView;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by J on 2017/2/19.
 * 便民信息详情页
 */

public class ConvenientInfoContentActivity extends LongBaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, HttpConnectedUtils.IOnStartNetworkSimpleCallBack {
    private WebView mWebView;
    private String id;
    private TextView tvTitle, tvCount, tvTotal, tvZan, tvCai, tvClick;
    private CircleImageView circleImageView;
    private AppCompatTextView tvName;
    private AppCompatTextView tvTime;
    private HintDialogUtil hintDialogUtil;
    private ArrayList<String> images;
    private CommentDialog commentDialog;
    private MyScrollView scrollView;
    private LinearLayout ll_title;
    private RelativeLayout ll_info;
    private int totalHight = 0;
    private LinearLayout ll_toptitle;
    private AppCompatTextView tvTopTitle;
    private CircleImageView civTopImg;
    private Animation an_in;
    private Animation an_out;
    private List<DataRow> commentRowList = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private ScrollViewListView listView;
    private ProgressBar footViewProgressBar;
    private AppCompatTextView footViewText;
    private int pageCount = 1;
    //    private int tempCount;
    private List<DataRow> tempDataRowList;
    private boolean canLoading = true;
    private RewardIntegralDialog rewardIntegralDialog;
    private ReportDialog reportDialog;
    private PassViewDialog passViewDialog;
    private String score;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Handler handler;
    private ImageView iv_contact;
    private String shopId;
    private String userAisouId;
    private String userNm;
    private HttpConnectedUtils mHttpConnectedUtils;

    /**
     * 用户钱包、积分信息
     */
    private final static int ID_USER_INFO = 0x11;
    private ArrayMap<String, String> mParameter;
    private double mScore_balance;
    private String mTitle;
    private String mEmjoiStrig;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.xj_activity_convenientinfo;
    }

    public static void startActivity(Context context, @Nullable String infoId) {
        Intent intent = new Intent(context, ConvenientInfoContentActivity.class);
        intent.putExtra("Id", infoId);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                activity.startActivity(intent);
            else {
                activity.startActivity(intent);
                activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
            }
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
        ll_toptitle = (LinearLayout) findViewById(net.twoant.master.R.id.ll_toptitle_convenientinfo);
        ll_toptitle.setVisibility(View.INVISIBLE);
        listView = (ScrollViewListView) findViewById(net.twoant.master.R.id.list_comment);

        tvTopTitle = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar);
        civTopImg = (CircleImageView) findViewById(net.twoant.master.R.id.iv_photo_center);
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConvenientInfoContentActivity.this.finish();
            }
        });
        id = getIntent().getStringExtra("Id");
        mWebView = (WebView) findViewById(net.twoant.master.R.id.web_show);
        tvTitle = (TextView) findViewById(net.twoant.master.R.id.convenient_info_title);
        tvCount = (TextView) findViewById(net.twoant.master.R.id.tv_integral_count_convenientinfo);
        tvTotal = (TextView) findViewById(net.twoant.master.R.id.tv_integral_total_convenientinfo);
        tvZan = (TextView) findViewById(net.twoant.master.R.id.tv_zan_convenientinfo);
        tvCai = (TextView) findViewById(net.twoant.master.R.id.tv_cai_convenientinfo);
        tvClick = (TextView) findViewById(net.twoant.master.R.id.tv_clickcount_convenientinfo);
        circleImageView = (CircleImageView) findViewById(net.twoant.master.R.id.img_convenientinfo);
        tvName = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_name_convenientinfo);
        tvTime = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_time_convenientinfo);
        scrollView = (MyScrollView) findViewById(net.twoant.master.R.id.sv_convenientinfo);
        ll_title = (LinearLayout) findViewById(net.twoant.master.R.id.ll_title_convenientinfo);
        ll_info = (RelativeLayout) findViewById(net.twoant.master.R.id.ll_info_convenientinfo);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.srl_convenientinfo);
        iv_contact = (ImageView) findViewById(net.twoant.master.R.id.iv_contact);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ll_title.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 宽高已经测量完毕
                int ll_TitleHeight = ll_title.getHeight();
                totalHight += ll_TitleHeight;
                ll_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_info.measure(h, 0);
        final int ll_infoHeight1 = ll_info.getMeasuredHeight();
        totalHight += ll_infoHeight1;
        // 仿头条 动画
       /*scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                System.out.println(scrollY+"");
                if (scrollY >= totalHight) {
                    //如果已经显示状态下就不展出动画
                    if (View.VISIBLE != ll_toptitle.getVisibility()) {
                        ll_toptitle.startAnimation(an_in);
                    }
                    ll_toptitle.setVisibility(View.VISIBLE);
                }else{
                    //如果已经是显示状态下就展出动画
                    if (View.INVISIBLE != ll_toptitle.getVisibility()) {
                        ll_toptitle.startAnimation(an_out);
                    }
                    ll_toptitle.setVisibility(View.INVISIBLE);
                }
            }
        });*/
        ll_toptitle.setVisibility(View.VISIBLE);
        findViewById(net.twoant.master.R.id.tv_comment_convenientinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_reward_convenientinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.tv_report_convenientinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ll_zan_convenientinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ll_cai_convenientinfo).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.iv_contact).setOnClickListener(this);

        passViewDialog = new PassViewDialog(this, Gravity.BOTTOM, true);
        hintDialogUtil = new HintDialogUtil(this);

        commentDialog = new CommentDialog(this, Gravity.BOTTOM, true);
        //评论对话框内容回调
        commentDialog.setOnPostListener(new OnPostCommentListener() {
            @Override
            public void onPost(String content) {
                footViewProgressBar.setVisibility(View.VISIBLE);
                footViewText.setText("加载中");
                requestNetPostComment(content);
            }
        });

        hintDialogUtil.showLoading();
        an_in = AnimationUtils.loadAnimation(this, net.twoant.master.R.anim.zy_toptitle_convenient_in);
        an_out = AnimationUtils.loadAnimation(this, net.twoant.master.R.anim.zy_toptitle_convenient_out);

        View footView = View.inflate(ConvenientInfoContentActivity.this, net.twoant.master.R.layout.yh_item_transparent_loading, null);
        footView.findViewById(net.twoant.master.R.id.ll_item_loading).setOnClickListener(this);
        //footview点击事件
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                footViewProgressBar.setVisibility(View.VISIBLE);
                footViewText.setText("加载中...");
                getNetForCommentList(false, false);
            }
        });
        footViewProgressBar = (ProgressBar) footView.findViewById(net.twoant.master.R.id.progress_bar);
        footViewText = (AppCompatTextView) footView.findViewById(net.twoant.master.R.id.tv_hint_info);

        listView.addFooterView(footView);

        handler = new Handler();
        // scrollview 滑到底部监听
        scrollView.registerOnScrollViewScrollToBottom(new MyScrollView.OnScrollBottomListener() {
            @Override
            public void srollToBottom() {
                if (canLoading) {
                    //避免卡顿现象，延迟一下
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNetForCommentList(false, false);
                        }
                    }, 800);
                }
            }
        });

        //打赏dialog初始化
        rewardIntegralDialog = new RewardIntegralDialog(this, Gravity.CENTER, false);
        rewardIntegralDialog.registerRewardOnlickListener(new RewardIntegralDialog.OnRewardOnlickListener() {
            @Override
            public void onClick(String integral) {
                passViewDialog.showDialog(true, true);
                score = integral;
            }
        });

        //支付密码输入回调
        passViewDialog.setDialogOnFinishInput(new PassViewDialog.OnDialogPasswordInputFinish() {
            @Override
            public void inputFinish(String strPassword) {
                requestNetPostIntegral(strPassword);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        mParameter = new ArrayMap<>();
        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
        mParameter.put("user", instance.getUID());
        mParameter.put("center", String.valueOf(instance.getAiSouLocationBean().getLongitude() + ","
                + instance.getAiSouLocationBean().getLongitude()));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != hintDialogUtil) {
            hintDialogUtil.dismissDialog();
            hintDialogUtil = null;
        }
        if (mHttpConnectedUtils != null) {
            mHttpConnectedUtils.onDestroy();
        }
    }

    private void requestNetPostComment(String content) {
        hintDialogUtil.showLoading();
        map.put("des", content);
        map.put("msg", id);
        LongHttp(ApiConstants.CONVENIENT_COMMENT, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    commentDialog.dismiss();
                    DZYToast.showToast(ConvenientInfoContentActivity.this, "已评论");

                    getNetForCommentList(true, true);
                } else {
                    String message = dataRow.getString("message");
                    DZYToast.showToast(ConvenientInfoContentActivity.this, message);
                }
            }
        });

    }

    @Override
    protected void requestNetData() {
        super.requestNetData();
        map.put("id", id);
        LongHttp(ApiConstants.CONVENIENT_DETAIL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("连接失败" + e);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                if (ConvenientInfoContentActivity.this.isFinishing()) {
                    return;
                }
                hintDialogUtil.dismissDialog();
//                DataRow data = DataRow.parseJson(response).getSet("data").getRows().get(0);
                DataRow data = DataRow.parseJson(response).getRow("data");
                if (null != data) {
                    userNm = data.getString("FROM_USER_NM");
                    tvName.setText(userNm);
                    mTitle = data.getString("TITLE");
                    mTitle = mTitle.replace("&#126","~");
                    tvTitle.setText(EmojiUtil.getEmjoiStrig(mTitle));
                    tvTopTitle.setText(userNm);
                    String goods_img_txt = "<HTML>" + data.getString("CONTENT");
                    mEmjoiStrig = EmojiUtil.getEmjoiStrig(goods_img_txt);
                    mEmjoiStrig = mEmjoiStrig.replace("&#126","~");
                    mWebView.loadDataWithBaseURL(null, mEmjoiStrig, null, "UTF-8", null);
                    tvTime.setText(data.getString("REG_TIME"));
                    tvCount.setText(data.getString("AWARD_QTY"));
                    tvTotal.setText(data.getString("AWARD_SCORE"));
                    tvClick.setText(data.getString("CLICK"));
                    tvTime.setText(data.getString("ADD_TIME"));
                    String judge_up = data.getString("JUDGE_UP");
                    if (TextUtils.isEmpty(judge_up) || "null".equals(judge_up)) {
                        judge_up = "0";
                    }
                    tvZan.setText(judge_up);
                    String judge_dp = data.getString("JUDGE_DP");
                    if (TextUtils.isEmpty(judge_dp) || "null".equals(judge_dp)) {
                        judge_dp = "0";
                    }
                    tvCai.setText(judge_dp);
                    circleImageView.setOnClickListener(ConvenientInfoContentActivity.this);
                    String photoUrl = BaseConfig.getCorrectImageUrl(data.getString("FROM_USER_ARATAR"));
                    if (null == images) {
                        images = new ArrayList<>();
                    }
                    images.clear();
                    images.add(photoUrl);
                    ImageLoader.getImageFromNetwork(circleImageView, photoUrl, ConvenientInfoContentActivity.this);
                    ImageLoader.getImageFromNetwork(civTopImg, photoUrl, ConvenientInfoContentActivity.this);
                    shopId = data.getString("FROM_SHOP_ID");
                    userAisouId = data.getString("FROM_USER_AISOU_ID");
                }
                //请求评论列表
                getNetForCommentList(false, false);
            }
        });

    }

    /**
     * @param isBottom 是否滑到底部
     */
    private void getNetForCommentList(final boolean isBottom, final boolean isFromComment) {
        map.put("msg", id);
        map.put("_anyline_page", pageCount + "");
        if (isBottom) {
            hintDialogUtil.showLoading();
        }
        LongHttp(ApiConstants.CONVENIENT_COMMENT_LIST, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
                ToastUtil.showLong(e + "");
                if (isFinishing()) {
                    return;
                }
                hintDialogUtil.dismissDialog();

            }

            @Override
            public void onResponse(String response, int id) {
                if (isFinishing()) {
                    return;
                }
                hintDialogUtil.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
                DataRow dataRow = DataRow.parseJson(response);
                if (null != dataRow) {
                    boolean result = dataRow.getBoolean("result", false);
                    if (result) {
                        //如果是评论发送的，先移除再进行添加操作
                        if (null != tempDataRowList) {
                            commentRowList.removeAll(tempDataRowList);
                        }

                        List<DataRow> dataList = dataRow.getSet("data").getRows();

                        // 第一次时赋值，以后便不赋值，累加
                        if (pageCount == 1) {
                            commentRowList = dataList;
                        }
                        if (null != dataList) {
                            if (pageCount != 1)
                                commentRowList.addAll(dataList);
                            if (dataList.size() == 10) {
                                pageCount++;
                                if (isBottom) {
                                    footViewProgressBar.setVisibility(View.GONE);
                                    if (isFromComment) {
                                        footViewText.setText("点击加载更多");
                                    } else {
                                        footViewText.setText("已无更多数据");
                                    }
                                    canLoading = false;

                                }
                            } else {
                                tempDataRowList = dataList;
                                footViewProgressBar.setVisibility(View.GONE);
                                footViewText.setText("已无更多数据");
                                canLoading = false;
                            }
                        }


                        commentAdapter = new CommentAdapter(commentRowList);
                        listView.setAdapter(commentAdapter);
                        commentAdapter.notifyDataSetChanged();
                        if (isBottom) {
                        /*Android很多函数都是基于消息队列来同步，所以需要一部操作，
                        addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll，
                        view可能还没有显示出来，所以会失败应该通过handler在新线程中更新*/
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        }

//                        try {
//                            int size1 = commentRowList.size();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.img_convenientinfo:
                Intent intent = new Intent(this, ImageScaleActivity.class);
                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, images);
                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, "1");
                startActivity(intent);
                this.overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
                break;

            case net.twoant.master.R.id.tv_comment_convenientinfo:
                commentDialog.showDialog(true, true);
                break;

            case net.twoant.master.R.id.ll_item_loading:

                break;

            case net.twoant.master.R.id.tv_reward_convenientinfo:
                if (null == mParameter) {
                    mParameter = new ArrayMap<>();
                    AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
                    mParameter.put("user", instance.getUID());
                    mParameter.put("center", String.valueOf(instance.getAiSouLocationBean().getLongitude() + ","
                            + instance.getAiSouLocationBean().getLongitude()));
                }
                if (null==mHttpConnectedUtils){
                    mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);
                }
                mHttpConnectedUtils.startNetworkGetString(ID_USER_INFO, mParameter, ApiConstants.ACTIVITY_USER_ACCOUNT_INFO);

                break;

            case net.twoant.master.R.id.tv_report_convenientinfo:
                if (null == reportDialog) {
                    reportDialog = new ReportDialog(this, Gravity.CENTER, false);
                    reportDialog.registerRewardOnlickListener(new RewardIntegralDialog.OnRewardOnlickListener() {
                        @Override
                        public void onClick(String integral) {
                            System.out.println(integral + "");
                            postReportInfo(integral);
                        }
                    });
                }
                reportDialog.showDialog(true, true);
                break;

            case net.twoant.master.R.id.ll_zan_convenientinfo:
                requestNetZan();
                break;

            case net.twoant.master.R.id.ll_cai_convenientinfo:
                requestNetCai();
                break;
            case net.twoant.master.R.id.iv_contact:
                if (null!=userAisouId){
                    EaseUser easeUser = new EaseUser(userAisouId);
                    easeUser.setNickname(userNm);
                    UserInfoUtil.startChat(easeUser, null, ConvenientInfoContentActivity.this);
                }
                break;
        }
    }

    private void requestNetZan() {
        map.clear();
        map.put("msg", id);
        LongHttp(ApiConstants.CONVENIENT_ZAN, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    String trim = tvZan.getText().toString().trim();
                    tvZan.setText((Integer.parseInt(trim) + 1) + "");
                } else {
                    ToastUtil.showLong(dataRow.getString("message"));
                }
            }
        });

    }

    private void requestNetCai() {
        map.clear();
        map.put("msg", id);
        LongHttp(ApiConstants.CONVENIENT_CAI, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    tvCai.setText((Integer.parseInt(tvCai.getText().toString().trim()) + 1) + "");
                } else {
                    ToastUtil.showLong(dataRow.getString("message"));
                }
            }
        });
    }


    private void postReportInfo(String info) {
        map.clear();
        map.put("msg", id);
        map.put("des", info);
        LongHttp(ApiConstants.CONVENIENT_REPORT, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    DZYToast.showToast(ConvenientInfoContentActivity.this, "√ 已举报,客服人员将尽快处理");
                } else {
                    ToastUtil.showLong(dataRow.getString("message"));
                }
            }
        });

    }

    private void requestNetPostIntegral(String password) {
        map.clear();
        map.put("msg", id);
        map.put("score", score);
        map.put("ps", password);

        LongHttp(ApiConstants.CONVENIENT_REWARD, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                passViewDialog.clearn();
                passViewDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                passViewDialog.dismiss();
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    tvCount.setText((Integer.parseInt(tvCount.getText().toString().trim()) + 1) + "");
                    tvTotal.setText((Double.parseDouble(tvTotal.getText().toString().trim()) + Double.parseDouble(score)) + "");
                    DZYToast.showToast(ConvenientInfoContentActivity.this, "√ 已打赏" + score + "积分");
                }
                passViewDialog.clearn();
            }
        });

    }

    @Override
    public void onRefresh() {
        pageCount = 1;
        footViewProgressBar.setVisibility(View.VISIBLE);
        footViewText.setText("加载中");
        canLoading = true;
        requestNetData();
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_USER_INFO:
                DataRow dataRow = DataRow.parseJson(response);
                if (dataRow != null) {
                    mScore_balance = dataRow.getRow("DATA").getDouble("SCORE_BALANCE");
                } else{
                    hintDialogUtil.showError("加载失败", true, false);
                    break;
                }
                if (null == rewardIntegralDialog) {
                    rewardIntegralDialog = new RewardIntegralDialog(this, Gravity.CENTER, false);
                    rewardIntegralDialog.registerRewardOnlickListener(new RewardIntegralDialog.OnRewardOnlickListener() {
                        @Override
                        public void onClick(String integral) {
                            passViewDialog.showDialog(true, true);
                            score = integral;
                        }
                    });
                }
                rewardIntegralDialog.showDialog(true, true);
                rewardIntegralDialog.setScore(mScore_balance);
                break;
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    class CommentHolder extends BaseHolder<DataRow> {
        TextView tvContent, tvName, tvTime;
        CircleImageView CircleImageView;

        @Override
        public View initHolderView() {
            View view = View.inflate(ConvenientInfoContentActivity.this, net.twoant.master.R.layout.zy_item_convenient_comment, null);
            tvContent = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_content_convenientcomment);
            tvName = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_name_convenientcomment);
            CircleImageView = (CircleImageView) view.findViewById(net.twoant.master.R.id.tv_item_photo_convenientcomment);
            tvTime = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_time_convenient);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            String content = data.getString("CONTENT");
            String emjoiStrig = EmojiUtil.getEmjoiStrig(content);
            try {
                EmojiUtil.handlerEmojiText(tvContent, emjoiStrig, AiSouAppInfoModel.getAppContext(), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvName.setText(data.getString("USER_NM"));
            tvTime.setText(data.getString("UPT_TIME"));
            ImageLoader.getImageFromNetworkControlImg(CircleImageView, BaseConfig.getCorrectImageUrl(data.getString("USER_AVATAR")), CircleImageView.getContext(), net.twoant.master.R.drawable.ic_def_circle);

        }

    }

    class CommentAdapter extends BasicAdapter<DataRow> {

        public CommentAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new CommentHolder();
        }
    }
}
