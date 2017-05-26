package net.twoant.master.ui.my_center.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.SwipeMenuLayout;
import net.twoant.master.widget.entry.DataRow;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by S_Y_H on 2017/4/10.
 * 我的会员商家列表
 */

public class BuyerMemberListControl extends BaseRecyclerControlImpl<DataRow> {

    private boolean isFirstClickPush = true;

    //金币
    private String mGoldCount;
    //优惠
    private String mDiscount;

    public BuyerMemberListControl() {
        super(1);
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new BuyerMemberListViewHolder(inflater.inflate(net.twoant.master.R.layout.yh_item_buyer_member_list, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow dataRow = mDataBean.get(position);
        if (null != dataRow) {
            BuyerMemberListViewHolder buyerMemberListViewHolder = (BuyerMemberListViewHolder) holder;
            String shopId = dataRow.getString("SHOP_ID");

            AppCompatButton closePush = buyerMemberListViewHolder.mBtnClosePush;

            AppCompatButton pushState = buyerMemberListViewHolder.mBtnPushState;
            if (dataRow.getBoolean("IS_RECEIVE_PUSH", true)) {
                if (!pushState.isEnabled()) {
                    pushState.setEnabled(true);
                }
                pushState.setTag(position);
                pushState.setOnClickListener(onClickListener);
                closePush.setBackgroundResource(net.twoant.master.R.drawable.yh_btn_press_orange);
            } else {
                if (pushState.isEnabled()) {
                    pushState.setEnabled(false);
                }
                closePush.setBackgroundResource(net.twoant.master.R.drawable.yh_btn_press_grey);
            }

            buyerMemberListViewHolder.mPrimaryLayout.setTag(shopId);
            buyerMemberListViewHolder.mPrimaryLayout.setOnClickListener(onClickListener);
            buyerMemberListViewHolder.mTvVipGrade.setText(dataRow.getStringDef("LEVEL_NM", "-"));
            if (null == mGoldCount) {
                mDiscount = mContext.getString(net.twoant.master.R.string.buyer_gold_discount);
                mGoldCount = mContext.getString(net.twoant.master.R.string.buyer_gold_count);
            }
            buyerMemberListViewHolder.mTvVipGoldCount.setText(String.format(mGoldCount, dataRow.getStringDef("BALANCE", "0")));
            buyerMemberListViewHolder.mTvVipDiscount.setText(String.format(mDiscount, dataRow.getStringDef("RATE", "0")));

            ImageLoader.getImageFromNetworkControlImg(buyerMemberListViewHolder.mIvHeadImg,
                    BaseConfig.getCorrectImageUrl(dataRow.getStringDef("SHOP_AVATAR", "")), mEnvironment, net.twoant.master.R.drawable.ic_def_circle);
            buyerMemberListViewHolder.mTvNickname.setText(dataRow.getStringDef("SHOP_NM", "-"));
            closePush.setTag(shopId);
            closePush.setTag(net.twoant.master.R.id.buyer_member_list, position);
            closePush.setOnClickListener(onClickListener);
        }

    }

    @Override
    public String getUrl(int category) {
        return ApiConstants.MEMBER_MERCHANT_LIST;
    }

    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        return mKeySet;
    }

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {

        switch (view.getId()) {
            case net.twoant.master.R.id.ll_buyer_swipe_menu:
                if (view.getTag() instanceof String && null != adapter.getActivity()) {
                    MerchantHomePageActivity.startActivity(adapter.getActivity(), (String) view.getTag());
                }
                break;

            case net.twoant.master.R.id.btn_close_push:
                if (view.getTag() instanceof String) {
                    if (isFirstClickPush) {
                        showFirstHintDialog(control, adapter, view);
                    } else {
                        closeOrOpenPush(control, adapter, AUTO_PUSH, (String) view.getTag(), (Integer) view.getTag(net.twoant.master.R.id.buyer_member_list));
                    }
                }
                break;

            case net.twoant.master.R.id.btn_push_state:
                BaseRecyclerNetworkAdapter networkAdapter = getBaseRecyclerNetworkAdapter();
                if (null == networkAdapter || !(view.getTag() instanceof Integer)) {
                    return;
                }
                View viewAtPosition = networkAdapter.getVisibilityViewAtPosition((Integer) view.getTag());
                if (viewAtPosition instanceof SwipeMenuLayout) {
                    SwipeMenuLayout swipeMenuLayout = (SwipeMenuLayout) viewAtPosition;
                    swipeMenuLayout.smoothExpand();
                }
                break;
        }
    }

    private final static int AUTO_PUSH = 0;
    private final static int OPEN_PUSH = 1;
    private final static int CLOSE_PUSH = 2;

    private void closeOrOpenPush(HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter, int code, String shopId, int position) {
        DataRow dataRow;
        if (mDataBean.size() < position || null == (dataRow = mDataBean.get(position))) {
            ToastUtil.showShort(net.twoant.master.R.string.buyer_data_invalid);
            return;
        }
        switch (code) {
            case CLOSE_PUSH:
                if (dataRow.getBoolean("IS_RECEIVE_PUSH", false)) {
                    closeOrOpenPush(adapter, control, shopId, position);
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.buyer_push_is_close);
                }
                break;

            case AUTO_PUSH:
                closeOrOpenPush(adapter, control, shopId, position);
                break;

            case OPEN_PUSH:
                if (dataRow.getBoolean("IS_RECEIVE_PUSH", false)) {
                    ToastUtil.showShort(net.twoant.master.R.string.buyer_push_is_open);
                } else {
                    closeOrOpenPush(adapter, control, shopId, position);
                }
                break;
        }
    }

    /**
     * 关闭或者打开推送
     */
    private void closeOrOpenPush(BaseRecyclerNetworkAdapter adapter, HomePagerHttpControl control, String shopId, int position) {
        if (mFlagDisable.contains(position)) {
            return;
        }
        mFlagDisable.add(position);
        mKeySet.clear();
        mKeySet.put("shop", TextUtils.isEmpty(shopId) ? "" : shopId);
        control.startNetwork(position, adapter, mKeySet, ApiConstants.MEMBER_MERCHANT_CHANGE_PUSH_STATE);
    }

    /**
     * 显示第一次点击 推送 按钮的 提示弹窗
     */
    private void showFirstHintDialog(final HomePagerHttpControl control, final BaseRecyclerNetworkAdapter adapter, final View view) {
        isFirstClickPush = false;
        BaseRecyclerNetworkAdapter baseRecyclerNetworkAdapter = getBaseRecyclerNetworkAdapter();
        if (null != baseRecyclerNetworkAdapter) {
            Activity activity = baseRecyclerNetworkAdapter.getActivity();
            if (null != activity) {
                new AlertDialog.Builder(activity, net.twoant.master.R.style.AlertDialogStyle)
                        .setMessage(net.twoant.master.R.string.buyer_push_hint_info)
                        .setNegativeButton(net.twoant.master.R.string.buyer_push_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeOrOpenPush(control, adapter, CLOSE_PUSH, (String) view.getTag(), (Integer) view.getTag(net.twoant.master.R.id.buyer_member_list));
                            }
                        })
                        .setPositiveButton(net.twoant.master.R.string.buyer_push_open, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closeOrOpenPush(control, adapter, OPEN_PUSH, (String) view.getTag(), (Integer) view.getTag(net.twoant.master.R.id.buyer_member_list));
                            }
                        })
                        .create().show();
            }
        }
    }

    @Override
    public boolean requestFail(int id, BaseRecyclerNetworkAdapter adapter) {
        if (mFlagDisable.contains(id)) {
            ToastUtil.showShort(net.twoant.master.R.string.buyer_push_state_change_fail);
        }
        return super.requestFail(id, adapter);
    }

    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        if (intercept) {
            DataRow dataRow = DataRow.parseJson(response);
            if (null == dataRow || !dataRow.getBoolean("result", false) || null == dataRow.getSet("data")) {
                return null;
            }
            return dataRow.getSet("data").getRows();
        } else {
            DataRow dataRow = DataRow.parseJson(response);
            if (null != dataRow && dataRow.getBoolean("result", false)) {

                DataRow bean;
                if (mDataBean.size() < id || null == (bean = mDataBean.get(id))) {
                    ToastUtil.showShort(net.twoant.master.R.string.buyer_data_invalid);
                    return null;
                }
                boolean state = dataRow.getBoolean("data", false);
                bean.put("IS_RECEIVE_PUSH", state);
                ToastUtil.showShort(state ? net.twoant.master.R.string.buyer_push_is_open : net.twoant.master.R.string.buyer_push_is_close);

                BaseRecyclerNetworkAdapter close = getBaseRecyclerNetworkAdapter();
                if (null != close) {
                    close.notifyItemChanged(id);
                }
            } else {
                ToastUtil.showShort(net.twoant.master.R.string.buyer_push_state_change_fail);
            }
        }
        return null;
    }

    private static class BuyerMemberListViewHolder extends BaseRecyclerViewHolder {

        private LinearLayoutCompat mPrimaryLayout;
        private CircleImageView mIvHeadImg;
        private AppCompatTextView mTvNickname;
        private AppCompatTextView mTvVipDiscount;
        private AppCompatTextView mTvVipGoldCount;
        private AppCompatTextView mTvVipGrade;
        private AppCompatButton mBtnPushState;
        private AppCompatButton mBtnClosePush;

        private BuyerMemberListViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }

        @Override
        protected void initView(View itemView, int viewType) {
            this.mIvHeadImg = (CircleImageView) itemView.findViewById(net.twoant.master.R.id.iv_head_img);
            this.mTvNickname = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_nickname);
            this.mTvVipDiscount = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_vip_discount);
            this.mTvVipGoldCount = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_vip_gold_count);
            this.mTvVipGrade = (AppCompatTextView) itemView.findViewById(net.twoant.master.R.id.tv_vip_grade);
            this.mBtnPushState = (AppCompatButton) itemView.findViewById(net.twoant.master.R.id.btn_push_state);
            this.mPrimaryLayout = (LinearLayoutCompat) itemView.findViewById(net.twoant.master.R.id.ll_buyer_swipe_menu);
            this.mBtnClosePush = (AppCompatButton) itemView.findViewById(net.twoant.master.R.id.btn_close_push);
        }
    }

}
