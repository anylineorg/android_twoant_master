package net.twoant.master.ui.main.adapter.control;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.EmojiUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.convenient.activity.ConvenientInfoContentActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by S_Y_H on 2017/3/17.
 * 便民信息搜索
 */

public class SearchConventionControl extends BaseRecyclerControlImpl<DataRow> {

    public final static int CATEGORY_MERCHANT = 0xff;
    public final static int CATEGORY_PERSON = 0xee;
    private String mString;
    private String mTitle;

    public SearchConventionControl() {
        super(1);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new SearchConventionViewHolder(inflater.inflate(net.twoant.master.R.layout.yh_item_search_convenient, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow dataRow = mDataBean.get(position);
        if (null == dataRow) {
            return;
        }
        holder.itemView.setTag(dataRow.getString("ID"));
        holder.itemView.setOnClickListener(onClickListener);
        if (holder instanceof SearchConventionViewHolder) {
            SearchConventionViewHolder searchViewHolder = (SearchConventionViewHolder) holder;
            mTitle = dataRow.getStringDef("TITLE", "");
            mTitle = mTitle.replace("&#126","~");
            searchViewHolder.mTvConvenientInfoTitle.setText(EmojiUtil.getEmjoiStrig(mTitle));
            searchViewHolder.mTvConvenientInfoPubType.setText(ControlUtils.isNull(dataRow.getString("FROM_SHOP_ID")) ? "个人发布" : "商家发布");
            searchViewHolder.mTvConvenientInfoData.setText(dataRow.getStringDef("ADD_TIME", "未知"));
            searchViewHolder.mTvConvenientInfoClick.setText(String.valueOf("阅览: " + dataRow.getStringDef("CLICK", "0")));
            searchViewHolder.mTvConvenientInfoId.setText(String.valueOf("ID: " + dataRow.getStringDef("FROM_USER_AISOU_ID", "-")));

            String temp = EmojiUtil.getEmjoiStrig(dataRow.getString("CONTENT"));
            ArrayList<String> imageUrl = getImageUrl(temp);
            if (null != imageUrl) {

                int size = imageUrl.size();
                switchState(searchViewHolder.mIvInfo1, 1 <= size ? View.VISIBLE : View.GONE);
                switchState(searchViewHolder.mIvInfo2, 2 <= size ? View.VISIBLE : View.GONE);
                switchState(searchViewHolder.mIvInfo3, 3 <= size ? View.VISIBLE : View.GONE);
                switch (size) {
                    default:
                    case 3:
                        ImageLoader.getImageFromNetworkPlaceholderControlImg(searchViewHolder.mIvInfo3, BaseConfig.getCorrectImageUrl(imageUrl.get(2)),
                                mEnvironment, net.twoant.master.R.drawable.ic_def_large);
                    case 2:
                        ImageLoader.getImageFromNetworkPlaceholderControlImg(searchViewHolder.mIvInfo2, BaseConfig.getCorrectImageUrl(imageUrl.get(1)),
                                mEnvironment, net.twoant.master.R.drawable.ic_def_large);

                    case 1:
                        ImageLoader.getImageFromNetworkPlaceholderControlImg(searchViewHolder.mIvInfo1, BaseConfig.getCorrectImageUrl(imageUrl.get(0)),
                                mEnvironment, net.twoant.master.R.drawable.ic_def_large);
                    case 0:
                        break;
                }
            } else {
                switchState(searchViewHolder.mIvInfo1, View.GONE);
                switchState(searchViewHolder.mIvInfo2, View.GONE);
                switchState(searchViewHolder.mIvInfo3, View.GONE);
            }
            mString = getContent(temp);
            mString = mString.replace("&lt;","<");
            mString = mString.replace("&gt;",">");
            mString = mString.replace("&#126","~");
            searchViewHolder.mTvConvenientInfoContent.setText(mString);
        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.CONVENIENT_INFO_SEARCH;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        mKeySet.put("kw", mKeyword);
        switch (category) {
            // 0 个人， 1 商家, 2 最新， 3最热， 4 评论量
            case CATEGORY_PERSON:
                mKeySet.put("role", String.valueOf(0));
                break;
            case CATEGORY_MERCHANT:
                mKeySet.put("role", String.valueOf(1));
                break;
            case IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_NEW:
                mKeySet.put("order", String.valueOf(2));
                break;
            case IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_FOLLOW:
                mKeySet.put("order", String.valueOf(3));
                break;
            case IRecyclerViewConstant.CATEGORY_MESSAGE_SEARCH_COMMENT:
                mKeySet.put("order", String.valueOf(4));
                break;
        }
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
        if (view.getId() == net.twoant.master.R.id.ll_parent && view.getTag() instanceof String) {
            ConvenientInfoContentActivity.startActivity(adapter.getActivity(), (String) view.getTag());
        }
    }

    /*
    {"result":true,"code":"200",


    "data":[{"BASE_SORT_ID":17,"SORT_ID":20,"TO_USER_ID":null,"LON":null,
    "FROM_USER_ARATAR":"/avatar/1487327713709d9ab1be3.jpg","FROM_SHOP_ID":324,
    "JUDGE_DP":null,"FROM_USER_ID":288,"IMG_FILE_PATH":null,"FROM_USER_NM":"juicelover",
    "ADD_TIME":"2017-03-15 17:25:57","FROM_USER_AISOU_ID":170210004,"FROM_SHOP_NM":"果汁专卖店",
    "JUDGE_UP":null,"TITLE":"测试是否商家","ID":49,"CLICK":0,"CONTENT":"我是商家","BASE_SORT_NM":"二手",
    "FROM_SHOP_AVATAR":"/ig?id=4311","LAT":null,"SORT_NM":"比亚迪"}]


    ,"success":true,"type":"list","message":null}
     */
    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        if (intercept) {
            DataRow dataRow = DataRow.parseJson(response);
            if (dataRow.getBoolean("result", false)) {
                DataSet dataSet = dataRow.getSet("data");
                if (null != dataSet) {
                    return dataSet.getRows();
                }
            }
        }
        return null;
    }

    /**
     * 获取内容
     */
    @NonNull
    public static String getContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        if (content.contains("&nbsp;")) {
            content = content.replace("&nbsp;", "");
        }

        content = content.trim();

        int left = content.indexOf(">");
        int right = content.indexOf("<", left);
        if (-1 != left && -1 != right) {
            StringBuilder stringBuilder = new StringBuilder(content.length() / 2);
            do {
                left += 1;
                stringBuilder.append(content.substring(left, right));
                left = content.indexOf(">", right);
                right = content.indexOf("<", left);
            } while (-1 != left && -1 != right);

            String temp = stringBuilder.toString();

            if (temp.isEmpty()) {
                left = content.indexOf(">");
                right = content.indexOf("<");
                int index = left >= right ? (right > 0 ? right : -1) : (left > 0 ? left : -1);
                if (-1 != index) {
                    return content.substring(0, index);
                }
            }
            return temp;
        }
        if (-1 == left) {
            return content;
        }

        return "";
    }

    /**
     * 获取图片链接
     */
    @Nullable
    public static ArrayList<String> getImageUrl(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        String temp = "<img src=\"";
        int start = content.indexOf(temp);
        if (-1 != start) {
            ArrayList<String> imgList = new ArrayList<>();
            do {
                start += temp.length();
                imgList.add(content.substring(start, start = content.indexOf("\"", start)));
                start = content.indexOf(temp, start);
            } while (-1 != start);
            return imgList;
        }
        return null;
    }

    private static void switchState(View view, int visibility) {
        if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    private static class SearchConventionViewHolder extends BaseRecyclerViewHolder {

        private AppCompatTextView mTvConvenientInfoTitle;
        private AppCompatTextView mTvConvenientInfoPubType;
        private AppCompatTextView mTvConvenientInfoData;
        private AppCompatTextView mTvConvenientInfoClick;
        private AppCompatTextView mTvConvenientInfoId;
        private AppCompatTextView mTvConvenientInfoContent;
        private AppCompatImageView mIvInfo1;
        private AppCompatImageView mIvInfo2;
        private AppCompatImageView mIvInfo3;

        private SearchConventionViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View view, int viewType) {
            this.mTvConvenientInfoTitle = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_title);
            this.mTvConvenientInfoPubType = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_pub_type);
            this.mTvConvenientInfoData = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_data);
            this.mTvConvenientInfoClick = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_click);
            this.mTvConvenientInfoId = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_id);
            this.mTvConvenientInfoContent = (AppCompatTextView) view.findViewById(net.twoant.master.R.id.tv_convenient_info_content);
            this.mIvInfo1 = (AppCompatImageView) view.findViewById(net.twoant.master.R.id.iv_info1);
            this.mIvInfo2 = (AppCompatImageView) view.findViewById(net.twoant.master.R.id.iv_info2);
            this.mIvInfo3 = (AppCompatImageView) view.findViewById(net.twoant.master.R.id.iv_info3);
        }
    }

}
