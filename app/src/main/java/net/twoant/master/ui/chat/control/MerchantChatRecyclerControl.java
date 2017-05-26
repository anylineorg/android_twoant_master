package net.twoant.master.ui.chat.control;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.easeui.domain.EaseUser;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.ui.chat.holder.MerchantChatViewHolder;
import net.twoant.master.ui.chat.util.UserInfoUtil;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/2/18.
 * 商家咨询
 */

public class MerchantChatRecyclerControl extends BaseRecyclerControlImpl<DataRow> {

    private String mShopId;

    public MerchantChatRecyclerControl(String shopId) {
        super(1);
        this.mShopId = shopId;
        this.mShopId = TextUtils.isEmpty(shopId) ? "" : mShopId;
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        // {"result":true,"code":"200","data":[],"success":true,"type":"list","message":null}
        if (intercept) {
            DataRow dataRow = DataRow.parseJson(response);
            if (dataRow != null && dataRow.getBoolean("result", false) && dataRow.getSet("data") != null) {
                return dataRow.getSet("data").getRows();
            }
        }

        return null;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new MerchantChatViewHolder(inflater.inflate(R.layout.yh_item_merchant_chat_list, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow dataRow = mDataBean.get(position);
        if (dataRow != null && holder instanceof MerchantChatViewHolder) {
            // {"AUTOGRAPH":null,"AVATAR":null,"AISOU_ID":14794600,"NM":"张三三"}
            MerchantChatViewHolder merchantChatViewHolder = (MerchantChatViewHolder) holder;
            CircleImageView ivHeaderImage = merchantChatViewHolder.getIvHeaderImage();
            ViewGroup.LayoutParams layoutParams = ivHeaderImage.getLayoutParams();
            final int size = mScreenWidth / 6;
            layoutParams.height = size;
            layoutParams.width = size;
            ivHeaderImage.setLayoutParams(layoutParams);

            ImageLoader.getImageFromNetwork(ivHeaderImage, BaseConfig.getCorrectImageUrl(dataRow.getStringDef("AVATAR", ""))
                    , mEnvironment, R.drawable.ic_def_circle);
            merchantChatViewHolder.getTvNickname().setText(dataRow.getStringDef("NM", ""));
            merchantChatViewHolder.getTvSignature().setText(dataRow.getStringDef("AUTOGRAPH", "这个人很懒，什么都没留下。"));

            AppCompatTextView tvState = merchantChatViewHolder.getTvState();
            if (tvState.getVisibility() != View.GONE) {
                tvState.setVisibility(View.GONE);
            }

            merchantChatViewHolder.itemView.setTag(position);//dataRow.getString("AISOU_ID")
            merchantChatViewHolder.itemView.setOnClickListener(onClickListener);


        }
    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.GET_MANAGE_LIST;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("id", mShopId);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
        Object tag = view.getTag();
        if (tag instanceof Integer) {
            final Activity activity = adapter.getActivity();
            if (null != activity) {
                if (mDataBean.size() - 1 > (Integer) tag) {
                    DataRow dataRow = mDataBean.get((Integer) tag);
                    if (dataRow == null) {
                        return;
                    }
                    EaseUser user = new EaseUser(dataRow.getString("AISOU_ID"));
                    user.setNickname(dataRow.getString("NM"));
                    user.setAvatar(BaseConfig.getCorrectImageUrl(dataRow.getString("AVATAR")));
                    user.setSex(dataRow.getInt("sex") == 0 ? "男" : "女");
                    user.setAge(dataRow.getInt("age"));
                    user.setSignature(dataRow.getStringDef("autograph", ""));
                    UserInfoUtil.startChat(user, null, activity);
                }
            }
        }
    }

}
