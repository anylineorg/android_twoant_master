package net.twoant.master.ui.my_center.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DZY on 2017/2/24.
 * 佛祖保佑   永无BUG
 */

public class DynamicListController extends BaseRecyclerControlImpl<DataRow> {
    private String mId;

    public DynamicListController(String id) {
        super(1);
        this.mId = id == null ? "" : id;
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
                    return  dataRow.getSet("data") != null ? dataRow.getSet("data").getRows() : null;
                }
            }

        }
        return null;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new ConvenientInfoViewHolder(inflater.inflate(R.layout.zy_item_convenient_comment, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        ConvenientInfoViewHolder circleImageView = (ConvenientInfoViewHolder) holder;
        DataRow dataRow = mDataBean.get(position);
        if (null != dataRow) {
            circleImageView.tvContent.setText(dataRow.getString("CONTENT"));
            circleImageView.tvName.setText(dataRow.getString("USER_NM"));
            circleImageView.tvTime.setText(dataRow.getString("UPT_TIME"));
            ImageLoader.getImageFromNetwork(circleImageView.CircleImageView, BaseConfig.getCorrectImageUrl( dataRow.getString("USER_AVATAR")));
        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.CONVENIENT_COMMENT_LIST;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("msg", mId);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {

    }

    @Override
    public void onDestroy() {

    }

    private static class ConvenientInfoViewHolder extends BaseRecyclerViewHolder {
        private TextView tvContent, tvName, tvTime;
        private de.hdodenhof.circleimageview.CircleImageView CircleImageView;

        private ConvenientInfoViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            tvContent = (TextView) itemView.findViewById(R.id.tv_item_content_convenientcomment);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name_convenientcomment);
            CircleImageView = (CircleImageView) itemView.findViewById(R.id.tv_item_photo_convenientcomment);
            tvTime = (TextView) itemView.findViewById(R.id.tv_item_time_convenient);
        }
    }


}
