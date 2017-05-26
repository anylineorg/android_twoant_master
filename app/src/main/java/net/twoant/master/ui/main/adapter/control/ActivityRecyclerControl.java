package net.twoant.master.ui.main.adapter.control;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.ActionDetailActivity;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.activity.MerchantHomePageActivity;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerControlImpl;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.holder.ActivityViewHolder;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.entry.DataSet;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by S_Y_H on 2016/12/1.
 * 活动
 */

public class ActivityRecyclerControl extends BaseRecyclerControlImpl<DataRow> {

    /**
     * 活动类型的tag key
     */
    private final static int ID_KEY = 12 << 24;

    private SimpleDateFormat mSimpleMonthFormat;
    private SimpleDateFormat mSimpleCompleteFormat;
    private SimpleDateFormat mDate;
    private String mUID;

    public ActivityRecyclerControl() {
        super(1);
        mUID = mAiSouAppInfoModel.getUID();
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType) {
            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                view = inflater.inflate(net.twoant.master.R.layout.yh_item_activity_common, parent, false);
                break;
            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                view = inflater.inflate(net.twoant.master.R.layout.yh_item_activity_red_packet, parent, false);
                break;
        }
        return new ActivityViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(int state, int viewType, BaseRecyclerViewHolder holder, int position, View.OnClickListener onClickListener) {
        DataRow resultBean = mDataBean.get(position);

        if (resultBean == null) return;
        ActivityViewHolder viewHolder = (ActivityViewHolder) holder;

        View itemView = viewHolder.itemView;
        itemView.setTag(ID_KEY, viewType);
        itemView.setTag(resultBean.getString("ID"));
        itemView.setOnClickListener(onClickListener);

        View headerView = viewHolder.getLinearLayoutCompat();
        if (state != IRecyclerViewConstant.STATE_CODE_NOT_HEADER) {
            if (headerView.getVisibility() != View.VISIBLE) headerView.setVisibility(View.VISIBLE);
            initHeaderInfo(state, viewHolder, resultBean, onClickListener);
        } else
            headerView.setVisibility(View.GONE);

        switch (viewType) {
            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
                initCharge(resultBean, viewHolder);
                break;
            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
                initIntegral(resultBean, viewHolder);
                break;
            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
                initMeter(resultBean, viewHolder);
                break;
            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                initRedPacket(resultBean, viewHolder, position, onClickListener);
                break;
            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                initSaved(resultBean, viewHolder);
                break;
        }
    }

    @Override
    public String getUrl(int category) {
        switch (category) {
            //商家首页中的
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_MERCHANT_PAGE:
                return ApiConstants.ACTIVITY_MERCHANT_HOME_PAGE;
            //搜索
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_NEARBY:
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEARBY:
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEW:
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_FAVOURITE:
                return ApiConstants.ACTIVITY_SEARCH;
            //首页的
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_LIST_HOME:
                return ApiConstants.ACTIVITY_HOME_PAGE;
            //附近的
            case IRecyclerViewConstant.CATEGORY_RED_PACKER_ACTIVITY:
            case IRecyclerViewConstant.CATEGORY_CHARGE_ACTIVITY:
            case IRecyclerViewConstant.CATEGORY_METER_ACTIVITY:
            case IRecyclerViewConstant.CATEGORY_SAVED_ACTIVITY:
            case IRecyclerViewConstant.CATEGORY_INTEGRAL_ACTIVITY:
                return ApiConstants.ACTIVITY_NEARBY_SELECT_TYPE;
        }
        return "";
    }
    public static boolean isNull(CharSequence charSequence) {
        return charSequence == null || "null".equals(charSequence) || charSequence.length() == 0;
    }
    static void getDistance(TextView textView, String... distances) {
        if (distances == null) {
            textView.setText("");
            return;
        }

        String distance;
        if (distances.length > 1 && !isNull((distance = distances[distances.length - 1]))) {
            textView.setText(distance);
            return;
        }

        distance = distances[0];
        if ("-1".equals(distance)) {
            SpannableString spannableString = new SpannableString("50km+");
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableString.setSpan(superscriptSpan, 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        } else {
            if (distance == null) {
                textView.setText("");
                return;
            }

            int length = distance.length();
            switch (length) {
                case 0:
                case 1:
                case 2:
                    textView.setText(String.valueOf(distance + "m"));
                    break;
                case 3:
                    textView.setText(String.valueOf("0." + distance.substring(0, length - 1) + "km"));
                    break;
                case 4:
                    textView.setText(String.valueOf(distance.charAt(0) + "." + distance.substring(1, length - 1) + "km"));
                    break;
                case 5:
                    textView.setText(String.valueOf(distance.substring(0, 2) + "." + distance.substring(2, length - 1) + "km"));
                    break;
            }
        }
    }
    @Override
    public Map<String, String> getParameter(int category) {
        mKeySet.clear();
        mKeySet.put("user", mUID);
        mKeySet.put("_anyline_page", String.valueOf(mIndex++));
        mKeySet.put("radius", String.valueOf(mRadiusSize));
        mKeySet.put("center", mLatLng);
        mAutoincrement = false;

        switch (category) {
            //首页 最新活动
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_LIST_HOME:
                break;

            case IRecyclerViewConstant.CATEGORY_ACTIVITY_NEARBY:
                break;
            //搜索活动 按距离
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEARBY:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "0");
                break;
            //搜索活动 最新
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_NEW:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "1");
                break;
            //搜索活动 最热
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_SEARCH_FAVOURITE:
                mKeySet.put("k", mKeyword);
                mKeySet.put("order", "2");
                break;
            //分类活动 积分活动
            case IRecyclerViewConstant.CATEGORY_INTEGRAL_ACTIVITY:
                mKeySet.put("sort", "0");
                break;
            //分类活动 收费活动
            case IRecyclerViewConstant.CATEGORY_CHARGE_ACTIVITY:
                mKeySet.put("sort", "1");
                break;
            //分类活动 储值活动
            case IRecyclerViewConstant.CATEGORY_SAVED_ACTIVITY:
                mKeySet.put("sort", "2");
                break;
            //分类活动 记次活动
            case IRecyclerViewConstant.CATEGORY_METER_ACTIVITY:
                mKeySet.put("sort", "3");
                break;
            //分类活动 红包活动
            case IRecyclerViewConstant.CATEGORY_RED_PACKER_ACTIVITY:
                mKeySet.put("sort", "4");
                break;
            //商家详情页 活动列表
            case IRecyclerViewConstant.CATEGORY_ACTIVITY_MERCHANT_PAGE:
                mKeySet.put("shop", mKeyword);
                break;
        }
        return mKeySet;
    }

    @Override
    protected boolean canAutoLoading() {
        if (mAutoincrement) {
            return mRadiusSize <= MAX_AUTOINCREMENT_SIZE;
        }
        return super.canAutoLoading();
    }

    @Override
    protected int getItemViewType(int position, DataRow dataBean) {

        int kind = dataBean.getInt("SORT_ID");

        //活动类型, 0是积分活动1是收费活动，2是储值活动，3是计次活动,4红包活动
        switch (kind) {
            case IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY:
                return IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY;
            case IRecyclerViewConstant.KIND_CHARGE_ACTIVITY:
                return IRecyclerViewConstant.KIND_CHARGE_ACTIVITY;
            case IRecyclerViewConstant.KIND_SAVED_ACTIVITY:
                return IRecyclerViewConstant.KIND_SAVED_ACTIVITY;
            case IRecyclerViewConstant.KIND_METER_ACTIVITY:
                return IRecyclerViewConstant.KIND_METER_ACTIVITY;
            case IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY:
                return IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY;
            default:
                return IRecyclerViewConstant.TYPE_LOADING;
        }
    }

    private String mTemp;

    @Override
    public void onClickListener(View view, HomePagerHttpControl control, BaseRecyclerNetworkAdapter adapter) {
        Object tag = view.getTag();
        switch (view.getId()) {
            case net.twoant.master.R.id.btn_draw_red_packet:
                Object position = view.getTag(ControlUtils.DRAW_KEY);
                if (tag instanceof String && position instanceof Integer) {

                    if (mFlagDisable.contains(position)) {
                        return;
                    }
                    mKeySet.clear();
                    mKeySet.put("activity", (String) tag);
                    mKeySet.put("user", mUID);
                    mKeySet.put("sort", "3");
                    mKeySet.put("vsort", "12");
                    mFlagDisable.add((Integer) position);
                    control.startNetwork((Integer) position, adapter, mKeySet, ApiConstants.ACTIVITY_GET_RED_PACKET);
                }
                break;
            case net.twoant.master.R.id.ll_header_parent:
                if (tag instanceof String) {
                    String shopId = (String) tag;
                    if ("null".equals(shopId)) {
                        return;
                    }

                    if (shopId.equals(mTemp)) {
                        return;
                    }
                    mTemp = shopId;

                    MerchantHomePageActivity.startActivity(adapter.getActivity(), shopId);
                }
                break;
            default:
                Object id;
                if (tag instanceof String && ((id = view.getTag(ID_KEY)) instanceof Integer)) {
                    if (tag.equals(mTemp)) {
                        return;
                    }
                    mTemp = (String) tag;
                    ActionDetailActivity.startActivity(ActionDetailActivity.TYPE_PURCHASER, adapter.getActivity()
                            , (String) tag, (Integer) id);
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTemp = null;
    }

    @Override
    protected List<DataRow> subRemoveMethod(boolean nonNull, List<DataRow> resultBean, boolean isRefresh) {
        return ControlUtils.removeDuplicate(resultBean, isRefresh ? null : mDataBean, "ID");
    }

    //{"result":true,"code":"200","data":{"SORT_ID":3,"VOUCHER_VAL":0,"SELLER_ID":117,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1485444570109bd0e3903.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":"18363601073","BUYER_ID":200,"PURSE_PRICE":0,"TOTAL_PRICE":0,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":363,"CREATE_TIME":"2017-02-05 10:04:20","RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/ig?id=1200","IDX":null,"SHOP_SELLER_NM":"大光明眼镜","ACTIVITY_TITLE":"元宵配镜优惠","SORT_NM":"活动支付","USER_SELLER_AVATAR":null,"PAY_STATUS":0,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":27,"BUYER_TEL":"18396800426","PAY_TIME":null,"CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":117,"BUYER_NM":"晴天","ACTIVITY_SORT_ID":4},"success":true,"type":"map","message":null}
    @Override
    protected List<DataRow> decodeResponseData(BaseRecyclerNetworkAdapter adapter, String response, int id, boolean intercept) {
        DataRow dataRow = DataRow.parseJson(response);
        MainActivity.checkState(adapter.getActivity(), dataRow);

        if (intercept) {
            DataSet dataSet;
            if (dataRow == null || (dataSet = dataRow.getSet("data")) == null || (dataSet.getRows()) == null)
                return null;

            return dataSet.getRows();

        } else {
            if (dataRow.getBoolean("result", false)) {
                View view = adapter.getVisibilityViewAtPosition(id);
                if (view != null) {
                    AppCompatButton button = (AppCompatButton) view.findViewById(net.twoant.master.R.id.btn_draw_red_packet);
                    if (button != null) {
                        button.setText("领取成功");
                        button.setEnabled(false);
                        button.setClickable(false);
                    }
                    mDataBean.get(id).put("IS_JOIN", true);
                } else {
                    if (mDataBean.size() > id) {
                        mDataBean.get(id).put("IS_JOIN", true);
                    }
                }
            } else {
                ToastUtil.showShort("领取失败");
            }
        }

        return null;
    }

    /**
     * 初始化记次活动数据
     */
    private void initMeter(DataRow resultBean, ActivityViewHolder viewHolder) {

        initCommonData(viewHolder, resultBean);

        AppCompatTextView typeFooter = viewHolder.getTvTypeActivityFooter();
        if (typeFooter.getVisibility() != View.GONE) {
            typeFooter.setVisibility(View.GONE);
        }

        AppCompatTextView typePrice = viewHolder.getTvTypePriceActivityFooter();
        if (typePrice.getVisibility() != View.GONE) {
            typePrice.setVisibility(View.GONE);
        }
        DataRow item = resultBean.getRow("ITEM");
        viewHolder.getTvPriceActivityFooter().setText(String.valueOf("￥" + (item == null ? "" : item.getString("PRICE"))));
    }

    /**
     * 积分活动数据初始化
     */
    private void initIntegral(DataRow resultBean, ActivityViewHolder viewHolder) {
        initCommonData(viewHolder, resultBean);

        AppCompatTextView typeFooter = viewHolder.getTvTypeActivityFooter();
        if (typeFooter.getVisibility() != View.GONE)
            typeFooter.setVisibility(View.GONE);

        AppCompatTextView typePrice = viewHolder.getTvTypePriceActivityFooter();
        if (typePrice.getVisibility() != View.GONE)
            typePrice.setVisibility(View.GONE);

        DataRow item = resultBean.getRow("ITEM");
        viewHolder.getTvPriceActivityFooter().setText(String.valueOf((item == null ? "" : item.getString("SCORE")) + "积分"));
    }

    /**
     * 收费活动数据初始化
     */
    private void initCharge(DataRow resultBean, ActivityViewHolder viewHolder) {
        initCommonData(viewHolder, resultBean);
        DataRow item = resultBean.getRow("ITEM");

        String integral = item == null ? null : item.getString("SCORE");
        AppCompatTextView typeFooter = viewHolder.getTvTypeActivityFooter();
        AppCompatTextView typePrice = viewHolder.getTvTypePriceActivityFooter();
        if (integral != null) {
            if (typeFooter.getVisibility() != View.VISIBLE) {
                typeFooter.setVisibility(View.VISIBLE);
            }
            typeFooter.setText("积分:");

            if (typePrice.getVisibility() != View.VISIBLE) {
                typePrice.setVisibility(View.VISIBLE);
            }

            typePrice.setText(integral);

        } else {

            if (typeFooter.getVisibility() != View.GONE) {
                typeFooter.setVisibility(View.GONE);
            }

            if (typePrice.getVisibility() != View.GONE) {
                typePrice.setVisibility(View.GONE);
            }
        }

        viewHolder.getTvPriceActivityFooter().setText(String.valueOf("￥" + (item == null ? "" : item.getString("PRICE"))));
    }

    /**
     * 储值活动初始化
     */
    private void initSaved(DataRow resultBean, ActivityViewHolder viewHolder) {
        initCommonData(viewHolder, resultBean);

        AppCompatTextView typeFooter = viewHolder.getTvTypeActivityFooter();
        DataRow item = resultBean.getRow("ITEM");

        if (typeFooter.getVisibility() != View.VISIBLE) {
            typeFooter.setVisibility(View.VISIBLE);
        }
        typeFooter.setText("储值:");

        AppCompatTextView typePrice = viewHolder.getTvTypePriceActivityFooter();
        if (typePrice.getVisibility() != View.VISIBLE) {
            typePrice.setVisibility(View.VISIBLE);
        }
        typePrice.setText(String.valueOf("￥" + (item == null ? "" : item.getString("PRICE"))));

        viewHolder.getTvPriceActivityFooter().setText(String.valueOf("到账:￥" + (item == null ? "" : item.getString("VAL"))));

    }

    /**
     * 初始化公有数据
     */
    private void initCommonData(ActivityViewHolder viewHolder, DataRow resultBean) {
        AppCompatImageView displayImg = viewHolder.getIvDisplayImageActivityCommon();
        ViewGroup.LayoutParams layoutParams = displayImg.getLayoutParams();
        layoutParams.height = (int) (mScreenWidth * 374F / 640.0F + 0.5F);
        displayImg.setLayoutParams(layoutParams);
        ImageLoader.getImageFromNetworkPlaceholderControlImg(displayImg, BaseConfig.getCorrectImageUrl(resultBean.getString("COVER_IMG")), mEnvironment, net.twoant.master.R.drawable.ic_def_action);

        viewHolder.getTvDetailsActivityFooter().setText(resultBean.getString("TITLE"));
        viewHolder.getTvClickCountFooterActivity().setText(String.valueOf(resultBean.get("CLICK")));

        viewHolder.getTvDateActivityFooter().setText("活动时间:" + parseStringData(resultBean.getString("START_TIME"), false)
                + "-" + parseStringData(resultBean.getString("END_TIME"), false));
        viewHolder.getTvAddressActivityFooter().setText(resultBean.getString("ADDRESS"));
    }

    private String parseStringData(String date, boolean isMonth) {
        try {
            if (mDate == null) {
                mDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            }
            if (isMonth) {
                if (mSimpleMonthFormat == null) {
                    mSimpleMonthFormat = new SimpleDateFormat("MM/dd", Locale.CHINA);
                }
                return mSimpleMonthFormat.format(mDate.parse(date));
            } else {
                if (mSimpleCompleteFormat == null) {
                    mSimpleCompleteFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
                }
                return mSimpleCompleteFormat.format(mDate.parse(date));
            }
        } catch (Exception e) {
            return "";
        }
    }

    private Date getActionDate(String date) throws ParseException {
        if (mDate == null) {
            mDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        }
        return mDate.parse(date);
    }

    @Override
    public boolean requestFail(int id, BaseRecyclerNetworkAdapter adapter) {
        if (mFlagDisable.contains(id)) {
            ToastUtil.showShort(net.twoant.master.R.string.action_get_red_packet_fail);
        }
        return super.requestFail(id, adapter);
    }

    /**
     * 填充红包数据
     */
    private void initRedPacket(DataRow resultBean, ActivityViewHolder viewHolder, int position, View.OnClickListener onClickListener) {

        viewHolder.getTvRedPacketNameRedPacket().setText(ControlUtils.isNull(resultBean.getString("SHOP_ID")) ? "系统红包" : "店铺红包");

        viewHolder.getmTvRedPacketDescriptionRedPacket().setText(resultBean.getString("TITLE"));

        //红包时间
        viewHolder.getTvValidDateRedPacket().setText(parseStringData(resultBean.getString("START_TIME"), true) + "-" + parseStringData(resultBean.getString("END_TIME"), true));
        DataRow item = resultBean.getRow("ITEM");
        boolean isOnceAction = false;
        if (item != null) {
            SpannableString spannableString = new SpannableString("￥" + (item.getString("VAL")));
            spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.getTvPriceRedPacket().setText(spannableString);
            //使用条件
            String active_val = item.getStringDef("ACTIVE_VAL", "-1");
            isOnceAction = resultBean.getInt("JOIN_QTY_LIMIT") == 0;
            viewHolder.getTvConditionRedPacket().setText(0 == new BigDecimal(active_val).compareTo(BigDecimal.ZERO) ? "无金额限制"
                    : "-1".equals(active_val) ? "未知" : "满" + active_val + "可用");
        }

        AppCompatButton button = viewHolder.getBtnDrawRedPacket();

        try {
            ControlUtils.initRedState(resultBean, position, onClickListener, item, isOnceAction, button, getActionDate(resultBean.getString("END_TIME")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.getTvDetailsRedPacket().setText(resultBean.getString("TITLE"));
        viewHolder.getTvReadCountFooterRedPacket().setText(String.valueOf(resultBean.getString("CLICK")));
        viewHolder.getTvDateActivityFooterRedPacket().setText("活动时间:" + parseStringData(resultBean.getString("START_TIME"), false)
                + "-" + parseStringData(resultBean.getString("END_TIME"), false));
    }

    /**
     * 顶部数据
     */
    private void initHeaderInfo(int flag, ActivityViewHolder viewHolder, DataRow resultBean, View.OnClickListener onClickListener) {

        AppCompatTextView tvDistanceActivityHeader = viewHolder.getTvDistanceActivityHeader();
        if (flag == IRecyclerViewConstant.STATE_CODE_NOT_DISTANCE) {
            if (tvDistanceActivityHeader.getVisibility() != View.GONE)
                tvDistanceActivityHeader.setVisibility(View.GONE);
        } else {
            if (tvDistanceActivityHeader.getVisibility() != View.VISIBLE)
                tvDistanceActivityHeader.setVisibility(View.VISIBLE);
            ControlUtils.getDistance(tvDistanceActivityHeader, resultBean.getString("_DISTANCE"), resultBean.getString("_DISTANCE_TXT"));
        }

        String temp = resultBean.getString("SHOP_ID");
        String useId = resultBean.getString("USER_ID");
        if (ControlUtils.isNull(temp) && ControlUtils.isNull(useId)) {
            viewHolder.getIvHeadImgActivityHeader().setImageResource(net.twoant.master.R.mipmap.ic_launcher);
            viewHolder.getTvNameActivityHeader().setText("官方");
            if (tvDistanceActivityHeader.getVisibility() != View.GONE)
                tvDistanceActivityHeader.setVisibility(View.GONE);

        } else if (!ControlUtils.isNull(temp)) {
            viewHolder.getTvNameActivityHeader().setText(resultBean.getString("SHOP_NM"));
            View layoutCompat = viewHolder.getLinearLayoutCompat();
            layoutCompat.setOnClickListener(onClickListener);
            layoutCompat.setTag(resultBean.getString("SHOP_ID"));
            ImageLoader.getImageFromNetworkControlImg(viewHolder.getIvHeadImgActivityHeader(), BaseConfig.getCorrectImageUrl(resultBean.getString("SHOP_AVATAR")), mEnvironment, net.twoant.master.R.drawable.ic_def_circle);
        } else if (!ControlUtils.isNull(useId)) {
            viewHolder.getTvNameActivityHeader().setText(resultBean.getString("USER_NM"));
            ImageLoader.getImageFromNetworkControlImg(viewHolder.getIvHeadImgActivityHeader(), BaseConfig.getCorrectImageUrl(resultBean.getString("USER_AVATAR")), mEnvironment, net.twoant.master.R.drawable.ic_def_circle);
        }

    }
}
