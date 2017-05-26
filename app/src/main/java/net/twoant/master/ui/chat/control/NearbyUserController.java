package net.twoant.master.ui.chat.control;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.api.AppConfig;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.CommonUtil;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.chat.activity.UserProfileActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DZY on 2017/3/14.
 * 佛祖保佑   永无BUG
 */

public class NearbyUserController extends BaseRecyclerControlImpl<DataRow> {
    private ProgressBar progressBar;
    private Activity activity;

    public NearbyUserController(ProgressBar progressBar, Activity activity) {
        super(1);
        this.progressBar = progressBar;
        this.activity = activity;
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
                progressBar.setVisibility(View.INVISIBLE);
                int result = dataRow.getInt("status");
                if (result == 1) {
                    return dataRow.getSet("datas") != null ? dataRow.getSet("datas").getRows() : null;
                }
            }
        }
        return null;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new NearbyUserController.ConvenientInfoViewHolder(inflater.inflate(net.twoant.master.R.layout.zy_nearbyuser_item, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        NearbyUserController.ConvenientInfoViewHolder convenientInfoViewHolder = (NearbyUserController.ConvenientInfoViewHolder) holder;
        DataRow dataRow = mDataBean.get(position);
        convenientInfoViewHolder.itemView.setOnClickListener(onClickListener);
        convenientInfoViewHolder.itemView.setTag(position);
        if (null != dataRow) {
            String sex = dataRow.getString("sex");
            if ("1".equals(sex)) {
                convenientInfoViewHolder.tvAge.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_nearby_age_male));
                convenientInfoViewHolder.tvAge.setText("♀" + dataRow.getString("age"));
            } else {
                convenientInfoViewHolder.tvAge.setBackground(CommonUtil.getDrawable(net.twoant.master.R.drawable.zy_nearby_age_female));
                convenientInfoViewHolder.tvAge.setText("♂" + dataRow.getString("age"));
            }
            ImageLoader.getImageFromNetwork(convenientInfoViewHolder.circleImageView, BaseConfig.getCorrectImageUrl(dataRow.getString("uphoto")));
            convenientInfoViewHolder.tvName.setText(dataRow.getString("nickname"));
            convenientInfoViewHolder.tvSign.setText(dataRow.getStringDef("autographo", "这个人很懒，什么也没留下。"));
            String distance = dataRow.getString("_distance");
            if (distance.length() >= 4) {
                double i = Double.parseDouble(distance) / 1000;
                DecimalFormat df = new DecimalFormat("######0.000");
                distance = df.format(i) + "km";
            } else {
                distance = distance + "m";
            }
            convenientInfoViewHolder.tvDistance.setText(distance);
        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.SEARCHINFO;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        String latitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLatitude() + "";
        String longitude = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLongitude() + "";
        mKeySet.put("key", AppConfig.GaoDe_Key);
        mKeySet.put("tableid", AppConfig.NEARBY_USER_TABLEID);
        mKeySet.put("center", longitude + "," + latitude);
        mKeySet.put("radius", "50000");
        mKeySet.put("limit", 20 + "");                     //每页记录数
        mKeySet.put("page", String.valueOf(mIndex++));   //当前页数
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, final HomePagerHttpControl control, final BaseRecyclerNetworkAdapter adapter) {
        switch (view.getId()) {
            case net.twoant.master.R.id.rl_contain_nearby:
                DataRow dataRow = mDataBean.get((Integer) view.getTag());
                if (dataRow == null) return;
                /*EaseUser user = new EaseUser(dataRow.getString("_name"));
                user.setNickname(dataRow.getString("nickname"));
                user.setAvatar(BaseConfig.getCorrectImageUrl(dataRow.getString("uphoto")));
                user.setSex(dataRow.getInt("sex") == 0 ? "男" : "女");
                user.setAge(dataRow.getInt("age"));
                user.setSignature(dataRow.getStringDef("autographo", ""));*/
                UserProfileActivity.startActivity(activity, dataRow.getString("_name"));
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static class ConvenientInfoViewHolder extends BaseRecyclerViewHolder {
        private de.hdodenhof.circleimageview.CircleImageView circleImageView;
        private TextView tvName, tvAge, tvSign, tvDistance;

        private ConvenientInfoViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            circleImageView = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.iv_photo_center);
            tvName = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_name_nearbyuser);
            tvAge = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_age_nearbyuser);
            tvSign = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_sign_nearbyuser);
            tvDistance = (TextView) itemView.findViewById(net.twoant.master.R.id.tv_distance_nearby);
        }
    }

}
