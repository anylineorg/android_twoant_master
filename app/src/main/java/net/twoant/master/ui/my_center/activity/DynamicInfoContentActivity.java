package net.twoant.master.ui.my_center.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.BaseHolder;
import net.twoant.master.base_app.BasicAdapter;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.widget.CommentDialog;
import net.twoant.master.widget.DZYToast;
import net.twoant.master.widget.MyScrollView;
import net.twoant.master.widget.OnPostCommentListener;
import net.twoant.master.widget.PassViewDialog;
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
 */

public class DynamicInfoContentActivity extends LongBaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout ll_toptitle, ll_info, ll_zan, ll_comment;
    private GridView mGrdPic;
    private ScrollViewListView listView;
    private AppCompatTextView tvTopTitle, tvName, tvTime;
    private CircleImageView civTopImg, circleImageView;
    private String cId;
    private MyScrollView scrollView;
    private TextView tvContent, zan_count_dynamic, tvComment, tvAgree, tv_comment_dynamicinfo;
    private ImageView ivAgreeShow, ivComment, ivAgree;
    private AppCompatImageButton fab_back_top;
    private Animation an_in, an_out;
    private PassViewDialog passViewDialog;
    private HintDialogUtil hintDialogUtil;
    private CommentDialog commentDialog;
    private ProgressBar footViewProgressBar;
    private AppCompatTextView footViewText;
    private Handler handler;
    private boolean canLoading = true;
    private int pageCount = 1;
    private List<DataRow> commentRowList;
    private List<DataRow> tempDataRowList;
    private List<String> imageList;
    private DynamicAdapter dynamicAdapter;
    private String commentContent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView zan_count;


    private int alterPosition = -1;
    private String pos;
    public int judge_up;
    public String from_user_nm;

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.xj_activity_dynamicinfo;
    }

    public static void startActivity(Context context, @Nullable String infoId) {
        Intent intent = new Intent(context, DynamicInfoContentActivity.class);
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

        ll_toptitle = (LinearLayout) findViewById(net.twoant.master.R.id.ll_toptitle_dynamicinfo);
        ll_toptitle.setVisibility(View.INVISIBLE);
        mGrdPic = (GridView) findViewById(net.twoant.master.R.id.grd_pic);
        listView = (ScrollViewListView) findViewById(net.twoant.master.R.id.list_comment);

        tvTopTitle = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar);
        civTopImg = (CircleImageView) findViewById(net.twoant.master.R.id.iv_photo_center);
        Toolbar toolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("alterPosition",alterPosition);
                setResult(RESULT_OK,intent);
                DynamicInfoContentActivity.this.finish();
            }
        });

        cId = getIntent().getStringExtra("Id");
        pos = getIntent().getStringExtra("pos");
        scrollView = (MyScrollView) findViewById(net.twoant.master.R.id.sv_dynamicinfo);
        ll_info = (LinearLayout) findViewById(net.twoant.master.R.id.ll_info_dynamicinfo);
        circleImageView = (CircleImageView) findViewById(net.twoant.master.R.id.img_dynamicinfo);
        tvName = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_name_dynamicinfo);
        tvTime = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_time_dynamicinfo);
        tvContent = (TextView) findViewById(net.twoant.master.R.id.tv_dynamic_content);
        ivAgreeShow = (ImageView) findViewById(net.twoant.master.R.id.ivAgreeShow);
        zan_count_dynamic = (TextView) findViewById(net.twoant.master.R.id.item_zan_count_dynamic);
        //评论
        ll_comment = (LinearLayout) findViewById(net.twoant.master.R.id.item_ll_comment_dynamic);
        ll_comment.setOnClickListener(this);
        tvComment = (TextView) findViewById(net.twoant.master.R.id.tvComment);
        ivComment = (ImageView) findViewById(net.twoant.master.R.id.ivComment);
        //赞
        ll_zan = (LinearLayout) findViewById(net.twoant.master.R.id.ll_item_zan_dynamic);
        ll_zan.setOnClickListener(this);
        tvAgree = (TextView) findViewById(net.twoant.master.R.id.tvAgree);
        ivAgree = (ImageView) findViewById(net.twoant.master.R.id.ivAgree);
        fab_back_top = (AppCompatImageButton) findViewById(net.twoant.master.R.id.fab_back_top);
        tv_comment_dynamicinfo = (TextView) findViewById(net.twoant.master.R.id.tv_comment_dynamicinfo);
        zan_count = (TextView) findViewById(net.twoant.master.R.id.tv_zan_count);

        an_in = AnimationUtils.loadAnimation(this, net.twoant.master.R.anim.zy_toptitle_convenient_in);
        an_out = AnimationUtils.loadAnimation(this, net.twoant.master.R.anim.zy_toptitle_convenient_out);

        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ll_info.measure(h, 0);
        final int ll_infoHeight = ll_info.getMeasuredHeight();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(net.twoant.master.R.id.srl_convenientinfo);
        swipeRefreshLayout.setColorSchemeResources(net.twoant.master.R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        scrollView.setOnScrollListener(new MyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                System.out.println(scrollY + "");
                if (scrollY >= ll_infoHeight) {
                    //如果已经显示状态下就不展出动画
                    if (View.VISIBLE != ll_toptitle.getVisibility()) {
                        ll_toptitle.startAnimation(an_in);
                    }
                    ll_toptitle.setVisibility(View.VISIBLE);
                } else {
                    //如果已经是显示状态下就展出动画
                    if (View.INVISIBLE != ll_toptitle.getVisibility()) {
                        ll_toptitle.startAnimation(an_out);
                    }
                    ll_toptitle.setVisibility(View.INVISIBLE);
                }
            }
        });

        tv_comment_dynamicinfo.setOnClickListener(this);
        passViewDialog = new PassViewDialog(this, Gravity.BOTTOM, true);
        hintDialogUtil = new HintDialogUtil(this);


        commentDialog = new CommentDialog(this, Gravity.BOTTOM, true);
        //评论对话框内容回调
        commentDialog.setOnPostListener(new OnPostCommentListener() {
            @Override
            public void onPost(String content) {
                commentContent = content + "";
                System.out.println(content);
                requestNet2Comment(cId, commentContent);
            }
        });

        hintDialogUtil.showLoading();

        handler = new Handler();

    }

    private void requestNet2Comment(String id, final String content) {
        map.clear();
        map.put("msg", id);
        map.put("title", content);
        LongHttp(ApiConstants.COMMENT_DYNAMIC, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                DataRow dataRow = DataRow.parseJson(response);
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    commentDialog.dismiss();
                    DZYToast.showToast(DynamicInfoContentActivity.this, "已评论");
                    updataComments(true);
                } else {
                    DZYToast.showToast(DynamicInfoContentActivity.this, dataRow.getString("message"));
                }
            }
        });
    }

    protected void updataComments(final boolean isBottom) {
        map.clear();
        map.put("id", cId);
        LongHttp(ApiConstants.DYNAMIC_DETAIL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();
                swipeRefreshLayout.setRefreshing(false);
                DataRow data = DataRow.parseJson(response).getRow("data");
                List<DataRow> reply = data.getSet("REPLY").getRows();
                if (null != reply){
                    dynamicAdapter = new DynamicAdapter(reply);
                    listView.setAdapter(dynamicAdapter);
                    if (isBottom) {
                        listView.setSelection(reply.size());
                    }
                }
            }
        });
    }

    @Override
    protected void requestNetData() {
        super.requestNetData();
        map.clear();
        map.put("id", cId);
        LongHttp(ApiConstants.DYNAMIC_DETAIL, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong(NetworkUtils.getNetworkStateDescription(call,e,"网络连接失败"));
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                hintDialogUtil.dismissDialog();

                DataRow data = DataRow.parseJson(response).getRow("data");//data对象
                if (null != data) {
                    from_user_nm = data.getString("FROM_USER_NM");
                    tvName.setText(from_user_nm);
                    tvTopTitle.setText(data.getString("FROM_USER_NM"));
                    tvTime.setText(data.getString("ADD_TIME"));
                    circleImageView.setOnClickListener(DynamicInfoContentActivity.this);
                    String photoUrl = BaseConfig.getCorrectImageUrl(data.getString("FROM_USER_ARATAR"));
                    ImageLoader.getImageFromNetwork(circleImageView, photoUrl, DynamicInfoContentActivity.this);
                    ImageLoader.getImageFromNetwork(civTopImg, photoUrl, DynamicInfoContentActivity.this);

                    tvContent.setText(data.getString("CONTENT"));
                    imageList = new ArrayList<String>();
                    imageList.add(data.getString("IMG_FILE_PATH"));
                    mGrdPic.setAdapter(new MyGridAdapter(imageList, DynamicInfoContentActivity.this));
                    judge_up = data.getInt("JUDGE_UP");
                    if (0!= judge_up){
                        List<DataRow> judge = data.getSet("JUDGE").getRows();//ZAN-LIST
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < judge.size(); i++) {
                            if (i==0){
                                stringBuffer.append(judge.get(i).getString("USER_NM"));
                            }else if (i>0&&i<10){
                                stringBuffer.append("、"+judge.get(i).getString("USER_NM"));
                            }else {
                                break;
                            }
                        }
                        if (judge.size()<=10){
                            zan_count.setText(stringBuffer.toString()+" 觉得很赞");
                            zan_count.setVisibility(View.VISIBLE);
                        }else {
                            zan_count.setText(stringBuffer.toString()+"等"+ judge_up +"人觉得很赞");
                            zan_count.setVisibility(View.VISIBLE);
                        }
                    }else {
                        zan_count.setText("");
                        zan_count.setVisibility(View.GONE);
                    }
                    List<DataRow> reply = data.getSet("REPLY").getRows();
                    dynamicAdapter = new DynamicAdapter(reply);
                    listView.setAdapter(dynamicAdapter);
                    dynamicAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    class MyGridAdapter extends BaseAdapter {
        private List<String> imgUrls;
        private Context context;

        public MyGridAdapter(List<String> imgUrls, Context context) {
            this.imgUrls = imgUrls;
            this.context = context;
        }

        @Override
        public int getCount() {
            return imgUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return imgUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder myViewHolder;
            if (convertView == null) {
                myViewHolder = new MyViewHolder();
                convertView = LayoutInflater.from(context).inflate(net.twoant.master.R.layout.zy_item_gridview_img, null, false);
                convertView.findViewById(net.twoant.master.R.id.iv_delete).setVisibility(View.GONE);
                myViewHolder.imageView = (ImageView) convertView.findViewById(net.twoant.master.R.id.iv_add);
                convertView.setTag(myViewHolder);
            } else {
                myViewHolder = (MyViewHolder) convertView.getTag();
            }
            ImageLoader.getImageFromNetwork(myViewHolder.imageView, BaseConfig.getCorrectImageUrl(imgUrls.get(position)), DynamicInfoContentActivity.this, net.twoant.master.R.drawable.ic_def_large);
            return convertView;
        }

        class MyViewHolder {
            ImageView imageView;
        }
    }
    protected void addZan(){
        map.clear();
        map.put("id", cId);
        LongHttp(ApiConstants.ZAN_DYNAMIC ,"",map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response+"");
                boolean result = DataRow.parseJson(response).getBoolean("result", false);
                if (result) {
                    alterPosition = Integer.parseInt(pos);
                    String zanStr = zan_count.getText().toString().trim();
                    String[] split = zanStr.split("、");
                    if(zanStr.length()==0){
                        zan_count.setText(from_user_nm+" 觉得很赞");
                        zan_count.setVisibility(View.VISIBLE);
                        return;
                    } else if (split.length <= 9) {
                        zan_count.setText(from_user_nm+"、"+zanStr);
                    }else {
                        zan_count.setText(from_user_nm+"、"+zanStr);
                    }
                    zan_count.setVisibility(View.VISIBLE);
                }else {
                    ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                }
            }
        });

    }


    @Override
    public void onRefresh() {

        updataComments(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_comment_dynamicinfo:
                commentDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.item_ll_comment_dynamic:
                commentDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.ll_item_zan_dynamic:
                addZan();
                break;
        }
    }

    class DynamicHolder extends BaseHolder<DataRow> {
        TextView tvContent, tvName, tvTime;
        CircleImageView CircleImageView;

        @Override
        public View initHolderView() {
            View view = View.inflate(DynamicInfoContentActivity.this, net.twoant.master.R.layout.zy_item_convenient_comment, null);
            tvContent = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_content_convenientcomment);
            tvName = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_name_convenientcomment);
            CircleImageView = (CircleImageView) view.findViewById(net.twoant.master.R.id.tv_item_photo_convenientcomment);
            tvTime = (TextView) view.findViewById(net.twoant.master.R.id.tv_item_time_convenient);
            return view;
        }

        @Override
        public void bindData(DataRow data) {
            tvContent.setText(data.getString("TITLE"));
            try {
                EmojiUtil.handlerEmojiText(tvContent, data.getString("TITLE"), DynamicInfoContentActivity.this, CommonUtil.getDimens(net.twoant.master.R.dimen.px_25), CommonUtil.getDimens(net.twoant.master.R.dimen.px_25));
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvName.setText(data.getString("USER_NM"));
            tvTime.setText(data.getString("UPT_TIME"));
            ImageLoader.getImageFromNetwork(CircleImageView, BaseConfig.getCorrectImageUrl(data.getString("USER_AVATAR")));

        }
    }

    class DynamicAdapter extends BasicAdapter<DataRow> {

        public DynamicAdapter(List<DataRow> list) {
            super(list);
        }

        @Override
        protected BaseHolder<DataRow> getHolder(int position) {
            return new DynamicHolder();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("alterPosition",alterPosition);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
