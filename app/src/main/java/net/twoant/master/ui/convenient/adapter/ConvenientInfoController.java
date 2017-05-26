package net.twoant.master.ui.convenient.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.ui.my_center.activity.DynamicInfoContentActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.CancelCenterDialog;
import net.twoant.master.widget.NoScrollListview;
import net.twoant.master.widget.entry.DataRow;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

/**
 * Created by DZY on 2017/2/24.
 * 佛祖保佑   永无BUG
 */

public class ConvenientInfoController extends BaseRecyclerControlImpl<DataRow> {
    private String mId;
    private Activity context;
    private final static int ID_ZAN = 0xB;
    private final static int TAG = 5<<24;
    private final static int POS = 6<<24;

    public ConvenientInfoController(String id, Activity context) {
        super(1);
        this.mId = id == null ? "" : id;
        this.context = context;
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {
        return 0;
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        if (intercept) {
            DataRow dataRow = DataRow.parseJson(response);
            if (dataRow != null) {
                boolean result = dataRow.getBoolean("result", false);
                if (result) {
                    return dataRow.getSet("data") != null ? dataRow.getSet("data").getRows() : null;
                }
            }

        } else {
          /*  switch (id) {
                case ID_ZAN:
                    System.out.println(response+"");
                    boolean result = DataRow.parseJson(response).getBoolean("result", false);
                    if (result) {


                    }else {
                        ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                    }
                    break;
            }*/
        }
        return null;
    }


    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ConvenientInfoViewHolder(inflater.inflate(net.twoant.master.R.layout.zy_listview_item, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        final int pageCount = 3;
        final ConvenientInfoViewHolder convenientInfoViewHolder = (ConvenientInfoViewHolder) holder;
        final DataRow dataRow = mDataBean.get(position);
        if (null != dataRow) {
            try {
                String content = dataRow.getString("CONTENT");
                System.out.println(content);
                EmojiUtil.handlerEmojiText(convenientInfoViewHolder.tvContent, content, AiSouAppInfoModel.getAppContext(), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40), CommonUtil.getDimens(net.twoant.master.R.dimen.px_40));
            } catch (Exception e) {
                e.printStackTrace();
            }
            convenientInfoViewHolder.tvName.setText(dataRow.getString("FROM_USER_NM"));
            int judge_up = dataRow.getInt("JUDGE_UP");
            System.out.println(judge_up + "");
            convenientInfoViewHolder.tvZanCount.setText(judge_up + "人觉得很赞!");
            convenientInfoViewHolder.tvDelete.setOnClickListener(onClickListener);

            convenientInfoViewHolder.itemView.setOnClickListener(onClickListener);
            convenientInfoViewHolder.itemView.setTag(TAG,dataRow.getInt("ID"));
            convenientInfoViewHolder.itemView.setTag(POS,position);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) convenientInfoViewHolder.llZan.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            convenientInfoViewHolder.llZan.setOnClickListener(onClickListener);
            convenientInfoViewHolder.llZan.setLayoutParams(layoutParams);

            convenientInfoViewHolder.tvDelete.setTag(position);
            ImageLoader.getImageFromNetwork(convenientInfoViewHolder.circleImageView, BaseConfig.getCorrectImageUrl(dataRow.getString("FROM_USER_ARATAR")));

            String img_file_path = dataRow.getString("IMG_FILE_PATH");

            if (TextUtils.isEmpty(img_file_path) || "null".equals(img_file_path)) {
                return;
            }

            convenientInfoViewHolder.itemGridView.setAdapter(new BaseAdapter() {

                @Override
                public int getCount() {
                    return 1;
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
                    View inflate = View.inflate(context, net.twoant.master.R.layout.zy_item_gridview_img, null);
                    inflate.findViewById(net.twoant.master.R.id.iv_delete).setVisibility(View.GONE);
                    AppCompatImageView imageView = (AppCompatImageView) inflate.findViewById(net.twoant.master.R.id.iv_add);
                    final ArrayList<String> itemImages = new ArrayList<>();
                    itemImages.add(ApiConstants.BASE + dataRow.getString("IMG_FILE_PATH"));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ImageScaleActivity.class);
                            intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, itemImages);
                            intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, position);
                            getBaseRecyclerNetworkAdapter().getActivity().startActivity(intent);
                            context.overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
                        }
                    });
                    String img_file_path = dataRow.getString("IMG_FILE_PATH");

                    ImageLoader.getImageFromNetwork(imageView, BaseConfig.getCorrectImageUrl(img_file_path));

                    return inflate;
                }
            });


            convenientInfoViewHolder.myGridView.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return 0;
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
                public View getView(int position, View convertView, ViewGroup parent) {
                    return null;
                }
            });
        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.POST_DYNAMIC_LIST_SELF;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        return mKeySet;
    }

    @Override
    public void onClickListener(final View view, final HomePagerHttpControl control, final BaseRecyclerNetworkAdapter adapter) {
        switch (view.getId()) {
            case net.twoant.master.R.id.tv_item_delete_convenivent:
                CancelCenterDialog cancelCenterDialog = new CancelCenterDialog(context, Gravity.CENTER, false);
                cancelCenterDialog.setTitle("确定删除这条动态");
                cancelCenterDialog.setOnClickListener(new CancelCenterDialog.IOnClickListener() {
                    @Override
                    public void onClickListener(View v) {
                        final BaseRecyclerNetworkAdapter baseRecyclerNetworkAdapter = getBaseRecyclerNetworkAdapter();
                        if (null != baseRecyclerNetworkAdapter) {

                            final int pos = getPos(view);

                            if (-1 != pos && mDataBean.size() > 0) {
                                mKeySet.clear();
                                mKeySet.put("id", mDataBean.get(pos).getString("ID") + "");
                                final int finalPos = pos;

                                mFlagDisable.add(12);
                                control.startNetwork(12, adapter, mKeySet, ApiConstants.ZAN_DYNAMIC);

                                LongHttp(ApiConstants.DELETE_DYNAMIC, mKeySet, new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
//                                        System.out.println(response + "");
                                        boolean result = DataRow.parseJson(response).getBoolean("result", false);
                                        if (result) {
                                            baseRecyclerNetworkAdapter.removePositionItem(finalPos);
                                        } else {
                                            ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                cancelCenterDialog.showDialog(true, true);
                break;
            case net.twoant.master.R.id.ll_item_zan_dynamic:
                final BaseRecyclerNetworkAdapter baseRecyclerNetworkAdapter = getBaseRecyclerNetworkAdapter();
                if (null != baseRecyclerNetworkAdapter) {
                    int pos = getPos(view);
                    if (-1 != pos) {
                        mKeySet.clear();
                        mKeySet.put("id", mDataBean.get(pos).getString("ID") + "");

                        final int finalPos = pos;
                        LongHttp(ApiConstants.ZAN_DYNAMIC ,mKeySet, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                System.out.println(response+"");
                                boolean result = DataRow.parseJson(response).getBoolean("result", false);
                                if (result) {
                                    View view = adapter.getVisibilityViewAtPosition(finalPos);
                                    if (null != view) {
                                        TextView tvZanCount = (TextView) view.findViewById(net.twoant.master.R.id.item_zan_count_dynamic);
                                        String str = tvZanCount.getText().toString();
                                        String regEx="[^0-9]";
                                        Pattern p = Pattern.compile(regEx);
                                        Matcher m = p.matcher(str);
                                        int zanNum = Integer.parseInt(m.replaceAll("").trim());
                                        tvZanCount.setText((++zanNum)+"人觉得很赞!");
                                    }
                                }else {
                                    ToastUtil.showLong(DataRow.parseJson(response).getString("message"));
                                }
                            }
                        });

                    }
                }
                break;
            case net.twoant.master.R.id.contacts_items:
                Object tag = view.getTag(TAG);
                int position = (int) view.getTag(POS);
                if (tag instanceof Integer) {
                    Intent intent = new Intent(context, DynamicInfoContentActivity.class);
                    intent.putExtra("Id",tag.toString() +"");
                    intent.putExtra("pos",position+"");
                    context.startActivityForResult(intent,200);
                }
            break;
        }
    }

    /**
     * 获取对应item的位置
     */
    private int getPos(View view) {
        ViewParent parent;
        View rootView = view;
        ViewGroup.LayoutParams layoutParams;
        int pos = -1;
        while (null != (parent = rootView.getParent())) {
            if (parent instanceof View) {
                layoutParams = (rootView = (View) parent).getLayoutParams();
                if (layoutParams instanceof RecyclerView.LayoutParams) {
                    pos = ((RecyclerView.LayoutParams) layoutParams).getViewAdapterPosition();
                    break;
                }
            }
        }
        return pos;
    }

    @Override
    public void onDestroy() {

    }

    private static class ConvenientInfoViewHolder extends BaseRecyclerViewHolder {
        private TextView tvContent, tvName, tvTime, tvDelete, tvZanCount;
        private de.hdodenhof.circleimageview.CircleImageView circleImageView;
        private GridView itemGridView;
        private NoScrollListview myGridView;
        private LinearLayout llZan,llComment;

        private ConvenientInfoViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            tvContent = (TextView) itemView.findViewById(net.twoant.master.R.id.tvContent);
            tvName = (TextView) itemView.findViewById(net.twoant.master.R.id.tvName);
            circleImageView = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.imgHead);
            tvTime = (TextView) itemView.findViewById(net.twoant.master.R.id.tvDate);
            tvDelete = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_item_delete_convenivent);
            itemGridView = (GridView) itemView.findViewById(net.twoant.master.R.id.item_gv);
            llZan = (LinearLayout) itemView.findViewById(net.twoant.master.R.id.ll_item_zan_dynamic);

            tvZanCount = (TextView) itemView.findViewById(net.twoant.master.R.id.item_zan_count_dynamic);
            myGridView = (NoScrollListview) itemView.findViewById(net.twoant.master.R.id.item_comment_list);
            llComment = (LinearLayout) itemView.findViewById(net.twoant.master.R.id.item_ll_comment_dynamic);
            llComment.setVisibility(View.GONE);
        }
    }

    public void LongHttp(String url, Map<String, String> map, StringCallback callback) {
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }

    public void setPositionZan(int positionZan) {

    }
}
