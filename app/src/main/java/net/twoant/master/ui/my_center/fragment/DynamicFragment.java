package net.twoant.master.ui.my_center.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseBaseFragment;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.convenient.activity.PublishConvenientActivity;
import net.twoant.master.ui.my_center.activity.DynamicListActivity;
import net.twoant.master.ui.my_center.bean.DynamicCommentListBean;
import net.twoant.master.ui.my_center.interfaces.OnPullToRefreshListener;
import net.twoant.master.ui.my_center.interfaces.RefreshListener;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.CommentDialog;
import net.twoant.master.widget.DZYToast;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.MyGridView;
import net.twoant.master.widget.NoScrollListview;
import net.twoant.master.widget.OnPostCommentListener;
import net.twoant.master.widget.ParallaxListView;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by DZY on 2017/2/9.
 * 佛祖保佑   永无BUG
 */

public class DynamicFragment extends BaseBaseFragment implements View.OnClickListener {

    private int headerViewHeight;
    private int disparityHeight;
    public RelativeLayout viewTitle;
    private View mToolbarBgMerchantHomePage;
    private boolean isLoadMore = false;// 表示是否正在加载更多
    private RefreshListener mListener;// 下拉刷新监听

    private int mFooterHeight;// 脚布局高度
    private Map<String, String> map;
    public List<DataRow> dataRowList;
    private MyAdapter adapter;
    private ParallaxListView listView;
    private int pagePosition = 1;
    private View footView;
    private CommentDialog commentDialog;
    private int onClickPosition = -1; //动态列表中触击的位置
    private boolean canJoinComment = false;
    private String commentContent;
    private ImageView headerPhotoImg;
    private String nick_name;
    private HintDialogUtil hintDialogUtil;
    private boolean isFinished = false;
    private List<Integer> zanList;
    public ImageView headerViewImg;

    public void setOnRefreshListener(RefreshListener listener) {
        mListener = listener;
    }
    // 创建一个以当前系统时间为名称的文件，防止重复
    private File tempFile = new File(Environment.getExternalStorageDirectory() + File.separator + getPhotoFileName());
    @Override
    protected int getLayoutRes() {
        return net.twoant.master.R.layout.zy_fragment_dynamic;
    }

    @Override
    protected void onViewCreate(View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.tv_Title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PublishConvenientActivity.class));
            }
        });
        listView = (ParallaxListView) view.findViewById(R.id.lv_dynamic);
        view.findViewById(net.twoant.master.R.id.iv_back).setOnClickListener(this);
        viewTitle = (RelativeLayout) view.findViewById(net.twoant.master.R.id.incloud_titile_dynamic);
        mToolbarBgMerchantHomePage = view.findViewById(R.id.toolbar_bg_merchant_home_page);
        view.findViewById(net.twoant.master.R.id.tv_publish_dynamic).setOnClickListener(this);
        final View headerView = View.inflate(getActivity(), R.layout.zy_dynamic_header, null);
        footView = View.inflate(getActivity(), net.twoant.master.R.layout.zy_pull_to_refresh_foot, null);
        headerViewImg = (ImageView) headerView.findViewById(net.twoant.master.R.id.iv_header);
        headerViewImg.setOnClickListener(this);
        headerPhotoImg = (ImageView) headerView.findViewById(net.twoant.master.R.id.iv_photo_dynamic_header);

        /**
         * 上拉加载更多
         * */
        setOnRefreshListener(new RefreshListener() {
            @Override
            public void onLoadMore() {
                if (isLoadMore) {
                    requestNetData(++pagePosition, false);
                }
            }
        });

        headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 宽高已经测量完毕
                listView.setParallaxImage(headerViewImg, headerPhotoImg);
                headerViewHeight = headerView.getHeight();
                int actionBarHeight = viewTitle.getHeight();
                disparityHeight = (headerViewHeight - actionBarHeight) * 100;
                headerViewImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        footView.measure(0, 0);// 测量View
        mFooterHeight = footView.getMeasuredHeight();
        footView.setPadding(0, -mFooterHeight, 0, 0);// 隐藏头布局
        listView.addHeaderView(headerView);
        listView.addFooterView(footView);

        // 监听 ListView 滑动事件
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private SparseArray recordSp = new SparseArray(0);
            private int mCurrentfirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // 快速滑动或者静止时
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_FLING) {
                    if (listView.getLastVisiblePosition() == listView.getCount() - 1 && !isLoadMore) {
                        System.out.println("到底了");
                        isLoadMore = true;

                        footView.setPadding(0, 0, 0, 0);
                        listView.setSelection(listView.getCount());// 设置ListView显示位置

                        if (mListener != null) {
                            mListener.onLoadMore();
                        }
                    }
                }
                canJoinComment = false;
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
                mCurrentfirstVisibleItem = arg1;
                View firstView = arg0.getChildAt(0);
                if (null != firstView) {
                    ItemRecod itemRecord = (ItemRecod) recordSp.get(arg1);
                    if (null == itemRecord) {
                        itemRecord = new ItemRecod();
                    }
                    itemRecord.height = firstView.getHeight();
                    itemRecord.top = firstView.getTop();
                    recordSp.append(arg1, itemRecord);
                    int h = getScrollY();//滚动距离
                    int i1 = h * 100;
                    double val = 0;
                    float finalResult = 0;
                    try {
                        val = (double) i1 / (double) disparityHeight;
                        // .## #代表精确到小数点后的多少位
                        // 如精确到小数点后的两位(四舍五入)
                        finalResult = new Float(new DecimalFormat(".##").format(val)).floatValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(finalResult + "");
                    if (finalResult < 1) {
                        mToolbarBgMerchantHomePage.setAlpha(finalResult);
                    } else {
                        mToolbarBgMerchantHomePage.setAlpha(1);
                    }
                }
            }

            private int getScrollY() {
                int height = 0;
                for (int i = 0; i < mCurrentfirstVisibleItem; i++) {
                    ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
                    try {
                        height += itemRecod.height;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ItemRecod itemRecod = (ItemRecod) recordSp.get(mCurrentfirstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }

            class ItemRecod {
                int height = 0;
                int top = 0;
            }
        });

        /**
         * 下拉刷新
         * */
        listView.registOnPullToRefreshListener(new OnPullToRefreshListener() {
            @Override
            public void onLoadMore() {
                requestNetForInformation();
                pagePosition = 1;
            }
        });

        adapter = new MyAdapter();
        dataRowList = new ArrayList<>();
        listView.setAdapter(adapter);

        commentDialog = new CommentDialog(getActivity(), Gravity.BOTTOM, true);
        //评论对话框内容回调
        commentDialog.setOnPostListener(new OnPostCommentListener() {
            @Override
            public void onPost(String content) {
                commentContent = content+"";
                System.out.println(content);
                if (null != dataRowList && onClickPosition != -1) {
                    String id = dataRowList.get(onClickPosition).getString("ID");
                    requestNet2Comment(id, commentContent);
                }
            }
        });

        hintDialogUtil = new HintDialogUtil(getActivity());
        hintDialogUtil.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isFinished) {
//                    getActivity().finish();
                }
            }
        });

        requestNetForInformation();
        zanList = new ArrayList<>();
        items = new ArrayList();
        items.add("相机");
        items.add("图库");
    }

    /**
     * @param page        页数
     * @param isClearList 是否清除集合
     */
    private void requestNetData(int page, final boolean isClearList) {
        if (null == map) {
            map = new HashMap<>();
        }
        //传 -1 为默认请求不带分页
        if (-1 != page) {
            map.put("_anyline_page", page + "");
        }
        map.put("_t",AiSouAppInfoModel.getInstance().getToken());
        LongHttp(ApiConstants.POST_DYNAMIC_LIST, "", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showLong("请求失败");
            }

            @Override
            public void onResponse(String response, int id) {
                System.out.println(response);
                List<DataRow> data = DataRow.parseJson(response).getSet("data").getRows();
                if (isClearList && 0 != dataRowList.size()) {
                    zanList.clear();
                    dataRowList.clear();
                    DynamicCommentListBean.clearn();
                }
                if (data.size() == 0) {
                    ToastUtil.showLong("已没有更多数据");
                    footView.setPadding(0, -mFooterHeight, 0, 0);// 隐藏脚布局
                    isLoadMore = false;
                    listView.clearAnimation();
                    return;
                } else {
                    dataRowList.addAll(data);
                    adapter.notifyDataSetChanged();
                    footView.setPadding(0, -mFooterHeight, 0, 0);// 隐藏脚布局
                    isLoadMore = false;
                }
                listView.clearAnimation();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getActivity().finish();
                break;
            case net.twoant.master.R.id.tv_publish_dynamic:
                startActivityForResult(new Intent(getActivity(), PublishDynamicActivity.class), 1);
                break;
            case net.twoant.master.R.id.iv_header:
                // 调用拍照
//                configCompress(200*1024,1980,1900,true);
//                startGetPhoto(true,1,tempFile);
                selectPhoto();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 999) {
                    pagePosition = 1;
                    requestNetData(1, true);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MyAdapter extends BaseAdapter {
        int pageCount = 1;

        @Override
        public int getCount() {
            return dataRowList.size();
        }

        @Override
        public Object getItem(int position) {
            return getView(position, null, null);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final DataRow dataRow = dataRowList.get(position);
            final View view = View.inflate(getActivity(), R.layout.zy_listview_item, null);
            TextView name = (TextView) view.findViewById(net.twoant.master.R.id.tvName);
            TextView content = (TextView) view.findViewById(net.twoant.master.R.id.tvContent);
            ImageView photo = (ImageView) view.findViewById(net.twoant.master.R.id.imgHead);
            TextView time= (TextView) view.findViewById(net.twoant.master.R.id.tvDate);
            time.setText(dataRow.getString("ADD_TIME"));
            final TextView tvZanCount = (TextView) view.findViewById(net.twoant.master.R.id.item_zan_count_dynamic);
            LinearLayout llZan = (LinearLayout) view.findViewById(net.twoant.master.R.id.ll_item_zan_dynamic);
            final int id = dataRow.getInt("ID");
            int judge_up = dataRow.getInt("JUDGE_UP");
            tvZanCount.setText(judge_up+"人觉得很赞!");

            for (int i : zanList) {
                if (i == position) {
                    tvZanCount.setText((++judge_up)+"人觉得很赞!");
                }
            }

            llZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    map.put("id",id+"");
                    LongHttp(ApiConstants.ZAN_DYNAMIC,"",map, new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            System.out.println(response+"");
                            boolean result = DataRow.parseJson(response).getBoolean("result", false);
                            if (result) {
                                String str = tvZanCount.getText().toString();
                                String regEx="[^0-9]";
                                Pattern p = Pattern.compile(regEx);
                                Matcher m = p.matcher(str);
                                int zanNum = Integer.parseInt(m.replaceAll("").trim());
                                tvZanCount.setText((++zanNum)+"人觉得很赞!");

                                zanList.add(position);
                            }else {
                                ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                            }
                        }
                    });
                }
            });
            view.findViewById(net.twoant.master.R.id.rl_delete_dynamic).setVisibility(View.GONE);
            LinearLayout llComment = (LinearLayout) view.findViewById(net.twoant.master.R.id.item_ll_comment_dynamic);
            llComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentDialog.showDialog(true, true);
                    onClickPosition = position;
                }
            });
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
                    ArrayList itemImages = new ArrayList();
                    itemImages.clear();
                    itemImages.add(BaseConfig.getCorrectImageUrl(dataRow.getString("FROM_MEMBER_IMG_FILE_PATH")));
                    intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, itemImages);
                    intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, "1");
                    startActivity(intent);
                    getActivity().overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
                }
            });
            NoScrollListview itemCommentList = (NoScrollListview) view.findViewById(R.id.item_comment_list);
            MyGridView itemGv = (MyGridView) view.findViewById(net.twoant.master.R.id.item_gv);
            final ArrayList<String> itemImages = new ArrayList<>();
            String img_file_path = dataRow.getString("IMG_FILE_PATH");

            name.setText(dataRow.getString("FROM_MEMBER_NM"));
            try {
                EmojiUtil.handlerEmojiText(content, dataRow.getString("CONTENT"), getActivity(),CommonUtil.getDimens(net.twoant.master.R.dimen.px_40), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40));
            } catch (IOException e) {
                e.printStackTrace();
            }

            final List<DataRow> replyList = dataRow.getSet("REPLY").getRows();
            List<DynamicCommentListBean.DataBean> itemList = new ArrayList<>();
            String listTojson = listTojson(replyList);
            try {
                JSONObject jsonObject = new JSONObject(listTojson);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                DynamicCommentListBean.DataBean item = null;
                if (jsonArray.length() == 0) {
                    item = new DynamicCommentListBean.DataBean();
                    item.setID_(id);
                    itemList.add(item);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);
                    item = new DynamicCommentListBean.DataBean();
                    item.setID_(id);
                    item.setID(json.getInt("ID"));
                    item.setTITLE(json.getString("TITLE"));
                    item.setUSER_NM(json.getString("MEMBER_NM"));
                    itemList.add(item);
                }
                if (DynamicCommentListBean.itemCommentList != null) {
                    boolean canJoin = false;
                    for (int i = 0 ; i < DynamicCommentListBean.itemCommentList.size(); i++){
                        List<DynamicCommentListBean.DataBean> dataBeanList = DynamicCommentListBean.itemCommentList.get(i);
                        if (null != dataBeanList) {
                            int id_ = dataBeanList.get(0).getID_();
                            if (id_ == id) {
                                canJoin = true;
                            }
                        }
                    }
                    if (!canJoin) {
                        DynamicCommentListBean.addData(itemList);
                    }
                }else{
                    DynamicCommentListBean.addData(itemList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                final String user_nm = DynamicCommentListBean.getData().get(position).get(0).getUSER_NM();
                if (canJoinComment && position == onClickPosition) {
                    if (null == user_nm) {
                        DynamicCommentListBean.getData().get(position).get(0).setUSER_NM(nick_name);
                        DynamicCommentListBean.getData().get(position).get(0).setTITLE(commentContent);
                    }else {
                        DynamicCommentListBean.DataBean item = new DynamicCommentListBean.DataBean();
                        item.setUSER_NM(nick_name);
                        item.setTITLE(commentContent);
                        DynamicCommentListBean.itemCommentList.get(position).add(item);
                    }
                    canJoinComment = false;
                }

                itemCommentList.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            if (null == DynamicCommentListBean.getData().get(position).get(0).getUSER_NM()) {
                                return 0;
                            }else {
                                return DynamicCommentListBean.getData().get(position).size();
                            }
                        }

                        @Override
                        public Object getItem(int position) {
                            return null;
                        }

                        @Override
                        public long getItemId(int position) {
                            return 0;
                        }

                        @Override
                        public View getView(int commentposition, View convertView, ViewGroup parent) {
                            View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_listview_text, null);
                            TextView itemCommentName = (TextView) inflate.findViewById(net.twoant.master.R.id.tv_commmentname_dynamic);
                            TextView itemCommentContent = (TextView) inflate.findViewById(R.id.tv_commment_content_dynamic);
                            itemCommentName.setText(DynamicCommentListBean.getData().get(position).get(commentposition).getUSER_NM()+":");
                            try {
                                EmojiUtil.handlerEmojiText(itemCommentContent,DynamicCommentListBean.getData().get(position).get(commentposition).getTITLE(), getActivity(),CommonUtil.getDimens(net.twoant.master.R.dimen.px_25), CommonUtil.getDimens(net.twoant.master.R.dimen.px_25));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return inflate;
                        }
                    });

                itemCommentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ToastUtil.showLong(position + ":" + id);
                    }
                });

                ImageLoader.getImageFromNetwork(photo, BaseConfig.getCorrectImageUrl(dataRow.getString("FROM_MEMBER_IMG_FILE_PATH")), getActivity());

                if (TextUtils.isEmpty(img_file_path) || "null".equals(img_file_path)) {
                    return view;
                }
                itemGv.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return pageCount;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View inflate = View.inflate(getActivity(), net.twoant.master.R.layout.zy_item_gridview_img, null);
                        inflate.findViewById(R.id.iv_delete).setVisibility(View.GONE);
                        AppCompatImageView imageView = (AppCompatImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);
                        if (itemImages.size() < pageCount) {
                            itemImages.add(ApiConstants.BASE + dataRow.getString("IMG_FILE_PATH"));
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ImageScaleActivity.class);
                                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, itemImages);
                                intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, position);
                                startActivity(intent);
                                getActivity().overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, R.anim.pv_my_alpha_action);
                            }
                        });
                        ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(dataRow.getString("IMG_FILE_PATH")), getActivity());
                        return inflate;
                    }
                });
                itemGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    private void requestNet2Comment(String id, String content) {
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
                    DZYToast.showToast(getContext(), "已评论");
                    View view = (View) adapter.getItem(onClickPosition);
                    NoScrollListview myGridView = (NoScrollListview) view.findViewById(net.twoant.master.R.id.item_comment_list);
                    BaseAdapter baseAdapter = (BaseAdapter) myGridView.getAdapter();
                    canJoinComment = true;
                    baseAdapter.notifyDataSetChanged();

//                    View inflate = View.inflate(getActivity(), R.layout.zy_item_listview_text, null);
//                    TextView textView = (TextView) inflate.findViewById(R.id.tv_commment_content_dynamic);
//                    textView.setText("sssssssssssss");

                } else {
                    DZYToast.showToast(getActivity(), dataRow.getString("message"));
                }
            }
        });
    }


    /**
     * list集合转换成json
     *
     * @param list
     * @return json字符串
     */
    public static String listTojson(List<DataRow> list) {
        StringBuilder json = new StringBuilder();
        json.append("{'data':[");
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                json.append(new JSONObject(map));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]}");
        }
        json.append("}");
        return json.toString();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DynamicCommentListBean.clearn();
    }



    private void requestNetForInformation() {
        HashMap<String,String> m = new HashMap<>();
        m.put("_t", AiSouAppInfoModel.getInstance().getToken());
        LongHttp(ApiConstants.USER_INFO, m, new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                isFinished = true;
                hintDialogUtil.showError("连接失败");
            }

            @Override
            public void onResponse(String response, int id) {
                DataRow row=DataRow.parseJson(response);
                row=row.getRow("data");

                nick_name = row.getString("NM");//result.getNick_name();
               // UserDataBean userBean = JsonUtil.parseJsonToBean(response,UserDataBean.class);
               // UserDataBean.ResultBean result = userBean.getResult();
               // nick_name = result.getNick_name();
                //final String photoUrl = BaseConfig.getCorrectImageUrl(result.getAvatar());
                ImageLoader.getImageFromNetwork(headerPhotoImg,row.getString("IMG_FILE_PATH"),getActivity());
                headerPhotoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /**
                         * 打开图片查看器
                         */
                        startActivity(new Intent(getActivity(),DynamicListActivity.class));
                    }
                });

                requestNetData(1, true);
            }
        });
    }

    @Override
    public void takeSuccess(TResult result) {
        TImage image = result.getImage();
        String compressPath = image.getCompressPath();;
        ImageLoader.getImageFromLocation(headerViewImg,compressPath);
    }
    private List items;
    // 使用系统当前日期加以调整作为照片的名称
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
        return sdf.format(date) + ".png";
    }

    private void selectPhoto() {
        ListViewDialog listViewDialog = new ListViewDialog(getActivity(), Gravity.BOTTOM, true);
        listViewDialog.setInitData(items,"取消");
        listViewDialog.setTextColor(net.twoant.master.R.color.principalTitleTextColor);
        listViewDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                switch (position) {
                    case 0:
                        // 调用拍照
                        configCompress(200*1024,1980,1900,true);
                        startGetPhoto(true,1,tempFile,900,600,true);
                        break;
                    case 1:
                        // 调用相册
                        configCompress(200*1024,1980,1900,true);
                        startGetPhoto(false,1,tempFile,900,600,true);
                        break;
                }
            }
        });
        listViewDialog.showDialog(true,true);
    }
}
