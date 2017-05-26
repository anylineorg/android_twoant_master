package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.google.gson.Gson;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouUserBean;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ConstUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.StringUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.activity.MerchantChatActivity;
import net.twoant.master.ui.main.adapter.ActionJoinUserAdapter;
import net.twoant.master.ui.main.adapter.ActivityDetailItemAdapter;
import net.twoant.master.ui.main.adapter.control.ControlUtils;
import net.twoant.master.ui.main.adapter.holder.BannerViewHolder;
import net.twoant.master.ui.main.bean.ActionDetailBean;
import net.twoant.master.ui.main.bean.PaymentDataBean;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.widget.RecyclerViewItemDecoration;
import net.twoant.master.ui.main.widget.UMShareHelper;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.ActionDetailWriteInfoDialog;
import net.twoant.master.widget.IntegralNotEnoughDialog;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;


public class ActionDetailActivity extends BaseActivity implements View.OnClickListener, HttpConnectedUtils.IOnStartNetworkCallBack {

    /**
     * 类型 卖家 界面
     */
    public final static int TYPE_MERCHANT = 0xA;

    /**
     * 类型 买家 界面
     */
    public final static int TYPE_PURCHASER = 0xB;

    private final static String ACTION_START = "ActionDetailActivity_start_action";
    private final static String EXTRA_ACTIVITY_ID = "ActionDetailActivity_id";
    private final static String EXTRA_TYPE = "ActionDetailActivity_type";
    private final static String EXTRA_KIND = "ActionDetailActivity_kind";
    private final static String EXTRA_AUTO_SELECT_LOCATION = "ADA_E_ASL";
    /**
     * 请求数据
     */
    private final static int ID_REQUEST_DATA = 1;
    /**
     * 收藏
     */
    private final static int ID_COLLECTION = 2;
    /**
     * 用户钱包、积分信息
     */
    private final static int ID_USER_INFO = 3;

    /**
     * 领取红包
     */
    private final static int ID_DRAW_RED_PACKET = 4;

    private ConvenientBanner<String> mCbConvenientBanner;
    private AppCompatTextView mTvTitle;
    private AppCompatTextView mTvActionMerchant;
    private AppCompatTextView mTvActionReleaseTime;
    private AppCompatTextView mTvActionReadCount;
    private AppCompatTextView mTvActionAddress;
    private AppCompatTextView mTvActionContinueTime;
    private AppCompatTextView mTvActionApplyCount;
    private AppCompatTextView mTvActionCost;
    private WebView mWvActionDetailDescribe;
    private AppCompatTextView mTvActionApplied;


    //红包 start
    private AppCompatTextView mTvPriceRedPacket;
    private AppCompatTextView mTvRedPacketNameRedPacket;
    private AppCompatTextView mTvRedPacketDescriptionRedPacket;
    private AppCompatTextView mTvValidDateRedPacket;
    private AppCompatTextView mTvConditionRedPacket;
    private AppCompatButton mBtnDrawRedPacket;

    private SimpleDateFormat mDate;
    private SimpleDateFormat mSimpleMonthFormat;
    private SimpleDateFormat mSimpleCompleteFormat;
    //红包 end
    /**
     * 收藏状态 显示
     */
    private AppCompatImageView mTvCollectionImage;

    /**
     * 网络
     */
    private HttpConnectedUtils mHttpConnectedUtils;
    private String mActivityId = "";
    /**
     * 友盟分享
     */
    private UMShareHelper mUmShareHelper;
    /**
     * 广告条 数据
     */
    private BannerViewHolder.ViewHolderCreator mViewHolderCreator;
    /**
     * 加载提示弹窗
     */
    private HintDialogUtil mHintDialogUtil;
    /**
     * 选择活动item 弹窗
     */
    private BottomSheetDialog mBottomSheetDialog;
    /**
     * 活动子item 适配器
     */
    private ActivityDetailItemAdapter mActivityDetailAdapter;
    /**
     * 填写信息弹窗
     */
    private ActionDetailWriteInfoDialog mWriteInfoDialog;
    /**
     * 输入的姓名
     */
    private AppCompatEditText mInputName;
    /**
     * 输入的手机号
     */
    private AppCompatEditText mInputPhone;
    /**
     * 当前活动是否被收藏，true 收藏
     */
    private boolean isCollectionActivity;
    /**
     * 用户选择的收费项
     */
    private ArrayList<ActionDetailBean.DataBean.ITEMSBean> mUserSelectDataRow;
    /**
     * 用户钱包、积分
     */
    private PaymentDataBean mPaymentDataBean = new PaymentDataBean();
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    /**
     * 积分不足弹窗
     */
    private IntegralNotEnoughDialog mIntegralNotEnoughDialog;

    /**
     * 当前界面类型
     */
    private int mType;

    /**
     * 活动类型
     */
    private int mKind;

    /**
     * 是否是 红包活动
     */
    private boolean isRedPacketAction;

    /**
     * 当前活动是否可报名
     */
    private boolean isEnable;

    /**
     * 当前活动错误描述
     * 只有在 isEnable false 才会有值
     */
    private String mErrorDescription;

    /**
     * 报名活动
     */
    private AppCompatTextView mApplyAction;

    /**
     * 距离
     */
    private AppCompatTextView mDistanceText;
    private AppCompatTextView mChatMerchant;

    public static void startActivity(int type, Activity activity, String actionId, int activityKind) {
        startActivity(type, activity, actionId, activityKind, true);
    }

    /**
     * @param type         {@link #TYPE_MERCHANT} 是商家查看的活动界面， {@link #TYPE_PURCHASER} 是买家查看的活动界面
     * @param activity     实例
     * @param actionId     活动id
     * @param activityKind 当前活动类型 （红包，记次，储值，收费，积分）
     */
    public static void startActivity(int type, Activity activity, String actionId, int activityKind, boolean autoSelectLocation) {
        Intent intent = new Intent(activity, ActionDetailActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_ACTIVITY_ID, actionId);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_KIND, activityKind);
        intent.putExtra(EXTRA_AUTO_SELECT_LOCATION, autoSelectLocation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            activity.startActivity(intent);

        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }


    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mActivityId = intent.getStringExtra(EXTRA_ACTIVITY_ID);
        mType = intent.getIntExtra(EXTRA_TYPE, -1);
        mKind = intent.getIntExtra(EXTRA_KIND, -1);

        if (mKind == IRecyclerViewConstant.KIND_RED_PACKER_ACTIVITY) {
            isRedPacketAction = true;
            return net.twoant.master.R.layout.yh_activity_red_packet_detail;
        } else {
            isRedPacketAction = false;
            return net.twoant.master.R.layout.yh_activity_action_detail;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        if (savedInstanceState != null) {
            if (!isRedPacketAction)
                initBannerData((ArrayList<String>) savedInstanceState.getSerializable("mBD"));
            isCollectionActivity = savedInstanceState.getBoolean("iCA", false);
            mActivityId = savedInstanceState.getString("mAI");
            mPaymentDataBean = savedInstanceState.getParcelable("mPDB");
            mType = savedInstanceState.getInt("mT");
            mKind = savedInstanceState.getInt("mK");
            isRedPacketAction = savedInstanceState.getBoolean("iRPA");
            isEnable = savedInstanceState.getBoolean("iE");

            if (!isEnable)
                mErrorDescription = savedInstanceState.getString("mED");
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {

                if (!isRedPacketAction) {
                    initItemDialog();
                    initInfoDialog();
                    initWebView();
                }

                startNetwork();
            }
        });
    }

    private void initWebView() {
        WebSettings settings = mWvActionDetailDescribe.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultFontSize(18);
        mWvActionDetailDescribe.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

//        mWvActionDetailDescribe.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView CURRENT_SELECT, String url) {

//                ViewGroup.LayoutParams layoutParams = mWebViewParent.getLayoutParams();
//                layoutParams.height = (int) (CURRENT_SELECT.getContentHeight() * CURRENT_SELECT.getScale() + 0.5);
//                mWebViewParent.setLayoutParams(layoutParams);
//            }
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isRedPacketAction)
            outState.putSerializable("mBD", mBannerData);
        outState.putBoolean("iCA", isCollectionActivity);
        outState.putString("mAI", mActivityId);
        outState.putParcelable("mPDB", mPaymentDataBean);
        outState.putInt("mT", mType);
        outState.putInt("mK", mKind);
        outState.putBoolean("iRPA", isRedPacketAction);
        outState.putBoolean("iE", isEnable);

        if (!isEnable)
            outState.putString("mED", mErrorDescription);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHintDialogUtil != null)
            mHintDialogUtil.dismissDialog();

        if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.dismiss();
        }

        if (mWriteInfoDialog != null) {
            mWriteInfoDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(net.twoant.master.R.menu.yh_menu_activity_detail, menu);
        return true;
    }

    /**
     * 进行网络请求
     */
    private void startNetwork() {
        if (mHintDialogUtil == null)
            mHintDialogUtil = new HintDialogUtil(this).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ActionDetailActivity.this.finish();
                }
            });

        mHintDialogUtil.showLoading("加载中...", true, false);

        if (mHttpConnectedUtils == null)
            mHttpConnectedUtils = HttpConnectedUtils.getInstance(this);

        if (!isRedPacketAction) {
            ArrayMap<String, String> parameter = new ArrayMap<>();
            AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
            parameter.put("user", instance.getUID());
            parameter.put("center", String.valueOf(instance.getAiSouLocationBean().getLongitude() + ","
                    + instance.getAiSouLocationBean().getLongitude()));
            mHttpConnectedUtils.startNetworkGetString(ID_USER_INFO, parameter, ApiConstants.ACTIVITY_USER_ACCOUNT_INFO);
        } else {
            startGetInfoData();
        }
    }

    private void startGetInfoData() {
        ArrayMap<String, String> parameter = new ArrayMap<>();
        parameter.put("activity_id", mActivityId);
        parameter.put("user", AiSouAppInfoModel.getInstance().getUID());
        mHttpConnectedUtils.startNetworkGetString(ID_REQUEST_DATA, parameter, ApiConstants.ACTIVITY_DETAIL_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mUmShareHelper != null)
            mUmShareHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUmShareHelper != null)
            mUmShareHelper.onConfigurationChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.tv_action_merchant:
                Object tag = v.getTag();
                if (tag instanceof String)
                    MerchantHomePageActivity.startActivity(this, (String) tag);
                break;
            case net.twoant.master.R.id.tv_server_action_detail:
                Object shopId = v.getTag();
                if (shopId instanceof String) {
                    MerchantChatActivity.startActivity(ActionDetailActivity.this, (String) shopId);
                }
                break;
            case net.twoant.master.R.id.ll_collection_action_detail:
                ArrayMap<String, String> arrayMap = new ArrayMap<>(2);
                arrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());
                arrayMap.put("activity", mActivityId);
                mHttpConnectedUtils.startNetworkGetString(ID_COLLECTION, arrayMap, ApiConstants.ACTIVITY_COLLECTION);
                break;
            case net.twoant.master.R.id.tv_join_action_detail:

                if (!isEnable) {
                    ToastUtil.showShort(mErrorDescription);
                    return;
                }

                if (mBottomSheetDialog != null) {
                    if (mBottomSheetBehavior != null && mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    mBottomSheetDialog.show();
                } else {
                    initItemDialog();
                    mBottomSheetDialog.show();
                }

                break;

            //选择项目的下一步
            case net.twoant.master.R.id.btn_next:
                if (!isEnable) {
                    ToastUtil.showShort(mErrorDescription);
                    return;
                }

                mUserSelectDataRow = mActivityDetailAdapter.getDataRow();
                int activityKind = mActivityDetailAdapter.getActivityKind();

                if (mUserSelectDataRow.size() > 0) {

                    //判断积分余额是能够买
                    if (activityKind == IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY || activityKind == IRecyclerViewConstant.KIND_CHARGE_ACTIVITY) {
                        BigDecimal bigDecimal = new BigDecimal(0);
                        for (ActionDetailBean.DataBean.ITEMSBean itemsBean : mUserSelectDataRow) {
                            bigDecimal = bigDecimal.add(new BigDecimal(itemsBean.getSCORE()));
                        }

                        //设置收费活动要求的积分
                        if (activityKind == IRecyclerViewConstant.KIND_CHARGE_ACTIVITY) {
                            mPaymentDataBean.setRequestIntegral(bigDecimal.doubleValue());
                        }

                        BigDecimal sum;
                        if ((sum = new BigDecimal(mPaymentDataBean.getIntegralSum())).compareTo((bigDecimal)) == -1) {
                            if (mIntegralNotEnoughDialog == null)
                                mIntegralNotEnoughDialog = new IntegralNotEnoughDialog(this, Gravity.CENTER, false,
                                        sum.setScale(2, RoundingMode.HALF_UP).toString(),
                                        bigDecimal.subtract(sum).setScale(2, RoundingMode.HALF_UP).toString());
                            mIntegralNotEnoughDialog.showDialog(false, true);
                            return;
                        }
                    }

                    if (mWriteInfoDialog != null) {
                        mWriteInfoDialog.showDialog(true, true);
                    } else {
                        initInfoDialog();
                        mWriteInfoDialog.showDialog(true, true);
                    }

                    if (mBottomSheetDialog.isShowing())
                        mBottomSheetDialog.dismiss();
                } else
                    ToastUtil.showShort("请选择费用项目");

                break;

            case net.twoant.master.R.id.iv_close:
                if (mWriteInfoDialog != null)
                    mWriteInfoDialog.dismiss();
                break;

            case net.twoant.master.R.id.btn_apply:
                if (!isEnable) {
                    ToastUtil.showShort(mErrorDescription);
                    return;
                }

                if (mUserSelectDataRow == null)
                    mUserSelectDataRow = mActivityDetailAdapter.getDataRow();
                String name = mInputName.getText().toString().trim();
                String phone = mInputPhone.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.showShort("请填写姓名");
                    mInputName.requestFocus();
                    if (!MainActivity.getInputMethodManager().isActive())
                        MainActivity.closeIME(true, mInputName);
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort("请填写手机号");
                    mInputPhone.requestFocus();
                    if (!MainActivity.getInputMethodManager().isActive())
                        MainActivity.closeIME(true, mInputPhone);
                    return;
                }

                Pattern patternName = Pattern.compile(ConstUtils.REGEX_ZH);

                if (!patternName.matcher(name).matches()) {
                    ToastUtil.showShort("姓名不能包含除汉字以外其他字符");
                    mInputName.requestFocus();
                    MainActivity.closeIME(true, mInputName);
                    return;
                }

                Pattern pattern = Pattern.compile(ConstUtils.REGEX_MOBILE_EXACT);

                if (!pattern.matcher(phone).matches()) {
                    ToastUtil.showShort("请填写正确的手机号");
                    mInputPhone.requestFocus();
                    MainActivity.closeIME(true, mInputPhone);
                    return;
                }
                AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
                instance.getAiSouUserBean().setNickName(name);
                instance.getAiSouUserBean().setPhone(phone);
                mPaymentDataBean.setName(name);
                mPaymentDataBean.setPhone(phone);

                ActionPaymentActivity.startActivity(mPaymentDataBean, this, mUserSelectDataRow);
                break;
            case net.twoant.master.R.id.btn_close_dialog:
                if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
                break;
            //查看商家报表
            case net.twoant.master.R.id.btn_examine_statement:
                ReportStatementActivity.startActivity(this, mActivityId, mKind);
                break;

            case net.twoant.master.R.id.btn_draw_red_packet:
                if (!isEnable) {
                    ToastUtil.showShort(mErrorDescription);
                    return;
                }
                ArrayMap<String, String> keySet = new ArrayMap<>();
                keySet.put("activity", mActivityId);
                keySet.put("user", AiSouAppInfoModel.getInstance().getUID());
                keySet.put("sort", "3");
                keySet.put("vsort", "12");
                mHttpConnectedUtils.startNetworkGetString(ID_DRAW_RED_PACKET, keySet, ApiConstants.ACTIVITY_GET_RED_PACKET);
                break;
            default:
                this.finish();
        }
    }

    @Override
    public void onBefore(Request request, int id) {

    }


    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_REQUEST_DATA:
                if (isRedPacketAction) {
                    DataRow dataRow = DataRow.parseJson(response);
                    if (dataRow == null || !dataRow.getBoolean("result", false)) {
                        mHintDialogUtil.showError("获取失败", true, false);
                        return;
                    }
                    MainActivity.checkState(this, dataRow);
                    dataRow = dataRow.getRow("data");

                    if (dataRow != null) {
                        initRedPacket(dataRow);
                        mHintDialogUtil.dismissDialog();
                    } else {
                        mHintDialogUtil.showError("获取失败", true, false);
                        return;
                    }

                } else {
                    try {
                        ActionDetailBean actionDetailBean = new Gson().fromJson(response, ActionDetailBean.class);
                        if (actionDetailBean != null) {
                            initPrimaryData(actionDetailBean);
                        }
                    } catch (Exception e) {
                        mHintDialogUtil.showError("数据异常", true, false);
                        return;
                    }
                    mHintDialogUtil.dismissDialog();
                }
                break;
            case ID_COLLECTION:
                DataRow parse = DataRow.parseJson(response);
                if (parse == null) return;
                boolean result = parse.getBoolean("RESULT", false);
                if (!result) {
                    ToastUtil.showShort(parse.getString("MESSAGE"));
                    return;
                }
                String data = parse.getString("DATA");
                isCollectionActivity = "1".equals(data);
                setCollection();
                break;
            case ID_USER_INFO:
                DataRow dataRow = DataRow.parseJson(response);
                if (dataRow != null) {
                    initUserData(dataRow.getRow("DATA"));
                    startGetInfoData();
                } else
                    mHintDialogUtil.showError("加载失败", true, false);
                break;
            case ID_DRAW_RED_PACKET:
                DataRow redResult = DataRow.parseJson(response);
                if (redResult != null && redResult.getBoolean("result", false)) {
                    mBtnDrawRedPacket.setText("领取成功");
                    mBtnDrawRedPacket.setEnabled(false);
                    mBtnDrawRedPacket.setClickable(false);
                } else {
                    ToastUtil.showShort("领取失败");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWvActionDetailDescribe != null)
            mWvActionDetailDescribe.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWvActionDetailDescribe != null)
            mWvActionDetailDescribe.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHttpConnectedUtils != null) {
            mHttpConnectedUtils.onDestroy();
        }

        if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.dismiss();
            mBottomSheetDialog = null;
        }
        mBottomSheetDialog = null;
        if (mWriteInfoDialog != null) {
            mWriteInfoDialog.dismiss();
            mWriteInfoDialog = null;
        }
        mWriteInfoDialog = null;
        if (mWvActionDetailDescribe != null) {
            ViewParent parent = mWvActionDetailDescribe.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(mWvActionDetailDescribe);
            }
            mWvActionDetailDescribe.destroy();
        }
    }

    /**
     * 填充红包数据
     */

    private void initRedPacket(DataRow resultBean) {

        mTvRedPacketNameRedPacket.setText(resultBean.getString("SHOP_ID") != null ? "店铺红包" : "系统红包");

        mTvRedPacketDescriptionRedPacket.setText(resultBean.getString("TITLE"));
        boolean isOnceAction = false;
        DataRow item = resultBean.getRow("ITEM");
        if (item != null) {
            SpannableString spannableString = new SpannableString("￥" + item.getString("VAL"));
            spannableString.setSpan(new AbsoluteSizeSpan(18, true), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvPriceRedPacket.setText(spannableString);
            //使用条件
            String active_val = item.getStringDef("ACTIVE_VAL", "-1");

            mTvConditionRedPacket.setText(0 == new BigDecimal(active_val).compareTo(BigDecimal.ZERO) ? "无金额限制"
                    : "-1".equals(active_val) ? "未知" : "满￥" + active_val + "可用");
            mTvActionApplyCount.setText(String.valueOf("已报名" + item.getInt("JOIN_QTY") + "人/" + ((isOnceAction = (resultBean.getInt("JOIN_QTY_LIMIT") == 0)) ? "单人多次活动" : "单人单次活动")));

        }

      /*  //单人多次活动
        if (isOnceAction) {
            mBtnDrawRedPacket.setClickable(true);
            mBtnDrawRedPacket.setEnabled(true);
            mBtnDrawRedPacket.setOnClickListener(this);
            mBtnDrawRedPacket.setText("再次\n领取");
            isEnable = true;
        } else
            //是否已领取
            if (resultBean.getBoolean("IS_JOIN", false)) {
                mBtnDrawRedPacket.setClickable(false);
                mBtnDrawRedPacket.setEnabled(false);
                mBtnDrawRedPacket.setText("已经\n领取");
            } else {

                try {
                    isEnable = false;
                    if (!resultBean.getBoolean("IS_ENABLE", false)) {
                        mBtnDrawRedPacket.setClickable(false);
                        mBtnDrawRedPacket.setEnabled(false);
                        mErrorDescription = "活动\n关闭";
                        mBtnDrawRedPacket.setText(mErrorDescription);
                    } else if (new BigDecimal(System.currentTimeMillis()).compareTo(new BigDecimal(getActionDate(resultBean.getString("END_TIME")).getTime())) == 1) {
                        mBtnDrawRedPacket.setClickable(false);
                        mBtnDrawRedPacket.setEnabled(false);
                        mErrorDescription = "已经\n结束";
                        mBtnDrawRedPacket.setText(mErrorDescription);
                    } else if (ActivityRecyclerControl.isFullJoin(item)) {
                        mBtnDrawRedPacket.setClickable(false);
                        mBtnDrawRedPacket.setEnabled(false);
                        mErrorDescription = "全部\n抢完";
                        mBtnDrawRedPacket.setText(mErrorDescription);
                    } else {
                        mBtnDrawRedPacket.setClickable(true);
                        mBtnDrawRedPacket.setEnabled(true);
                        mBtnDrawRedPacket.setOnClickListener(this);
                        mBtnDrawRedPacket.setText("立即\n领取");
                        isEnable = true;
                    }

                } catch (ParseException e) {
                    mBtnDrawRedPacket.setClickable(false);
                    mBtnDrawRedPacket.setEnabled(false);
                    mBtnDrawRedPacket.setText("未知\n活动");
                }
            }*/

        try {
            String start_time = resultBean.getString("START_TIME");
            String end_time = resultBean.getString("END_TIME");
            int state = ControlUtils.initRedState(resultBean, 0, this, item, isOnceAction, mBtnDrawRedPacket, getActionDate(end_time));
            isEnable = ControlUtils.GET_NEW == state;
            if (!isEnable) {
                mErrorDescription = ControlUtils.getRedStateDescription(state);
            }
            String start = parseStringData(getActionDate(start_time), false);
            String end = parseStringData(getActionDate(end_time), false);
            mTvActionReleaseTime.setText(start);
            mTvActionContinueTime.setText(String.valueOf(start + " - " + end));

            //红包时间
            mTvValidDateRedPacket.setText(parseStringData(getActionDate(resultBean.getString("START_TIME")), true) + "-" + parseStringData(getActionDate(resultBean.getString("END_TIME")), true));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        mTvTitle.setText(resultBean.getString("TITLE"));

        String shopId = resultBean.getString("SHOP_ID");
        String userId = resultBean.getString("USER_ID");
        if (ControlUtils.isNull(shopId) && ControlUtils.isNull(userId)) {
            mTvActionMerchant.setText("官方");
            mTvActionAddress.setText("官方活动");
        } else if (!ControlUtils.isNull(shopId)) {
            mTvActionMerchant.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            mTvActionMerchant.setText(resultBean.getString("SHOP_NM"));
            mTvActionMerchant.setTag(resultBean.getString("SHOP_ID"));
            mTvActionAddress.setText(resultBean.getString("ADDRESS"));
        } else if (!ControlUtils.isNull(userId)) {
            mTvActionMerchant.setText(resultBean.getString("USER_NM"));
            mTvActionAddress.setText(resultBean.getString("ADDRESS"));
        }

        mDistanceText.setText(resultBean.getStringDef("_DISTANCE_TXT", ""));
        mTvActionReadCount.setText(String.format(getString(net.twoant.master.R.string.action_reader_format), resultBean.getString("CLICK")));
        mTvActionCost.setText("红包活动");
    }

    private Date getActionDate(String date) throws ParseException {
        if (mDate == null) {
            mDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        }
        return mDate.parse(date);
    }

    private String parseStringData(Date date, boolean isMonth) {
        if (isMonth) {
            if (mSimpleMonthFormat == null) {
                mSimpleMonthFormat = new SimpleDateFormat("MM/dd", Locale.CHINA);
            }
            return mSimpleMonthFormat.format(date);
        } else {
            if (mSimpleCompleteFormat == null) {
                mSimpleCompleteFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA);
            }
            return mSimpleCompleteFormat.format(date);
        }
    }

    /**
     * 初始化用户数据
     *
     * @param row 数据
     */
    private void initUserData(DataRow row) {
        if (row == null) {
            return;
        }
        mPaymentDataBean.setIntegralSum(row.getDouble("SCORE_BALANCE"));
        mPaymentDataBean.setWalletSum(row.getDouble("PURSE_BALANCE"));
    }

    /**
     * 设置收藏状态
     */
    private void setCollection() {
        if (isCollectionActivity) {
            mTvCollectionImage.setImageResource(net.twoant.master.R.drawable.ic_action_collection_check);
        } else {
            mTvCollectionImage.setImageResource(net.twoant.master.R.drawable.ic_action_collection_uncheck);
        }
    }

    /**
     * 初始化 外层数据
     *
     * @param parse json
     */
    private void initPrimaryData(ActionDetailBean parse) {

        if (parse == null) return;
        ActionDetailBean.DataBean data = parse.getData();
        if (data == null) return;

        mDistanceText.setText(data.get_DISTANCE_TXT());
        mTvTitle.setText(data.getTITLE());
        mTvActionMerchant.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvActionMerchant.setText(data.getSHOP_NM());
        mPaymentDataBean.setShopName(data.getSHOP_NM());
        String shopId = data.getSHOP_ID();
        if (null != mChatMerchant) {
            mChatMerchant.setTag(shopId);
            mChatMerchant.setOnClickListener(this);
        }
        mTvActionMerchant.setTag(shopId);
        mPaymentDataBean.setShopId(shopId);

        isCollectionActivity = "1".equals(data.getIS_COL());

        String start_time = data.getSTART_TIME();
        start_time = start_time == null ? "" : start_time;
        String end_time = data.getEND_TIME();
        end_time = end_time == null ? "" : end_time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd HH:mm", Locale.CHINA);
        Date endDate = null;
        try {
            endDate = getActionDate(end_time);
            String start = simpleDateFormat.format(getActionDate(start_time));
            String end = simpleDateFormat.format(endDate);
            mTvActionReleaseTime.setText(start);
            mTvActionContinueTime.setText(String.valueOf(start + " - " + end));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mTvActionReadCount.setText(String.format(getString(net.twoant.master.R.string.action_reader_format), data.getCLICK()));
        mTvActionAddress.setText(data.getADDRESS());
        int join_qty_limit = data.getJOIN_QTY_LIMIT();
        int joinCount = data.getJOIN_QTY();
        mTvActionApplyCount.setText(String.valueOf("已报名" + joinCount + "人/" + (join_qty_limit == 0 ? "单人多次活动" : "单人单次活动")));
        if (mTvActionApplied != null)
            mTvActionApplied.setText(String.format(getString(net.twoant.master.R.string.action_applied), joinCount));

        if (mApplyAction != null) {
            if (0 == data.getIS_ENABLE_JOIN()) {
                isEnable = false;
                mErrorDescription = "已经报名";
                mApplyAction.setText("已经报名");
                mApplyAction.setEnabled(false);
            } else if (!"1".equals(data.getIS_ENABLE())) {
                isEnable = false;
                mErrorDescription = "活动不可用";
                mApplyAction.setText("活动关闭");
                mApplyAction.setEnabled(false);
            }/* else if (join_qty_limit != 0 && (join_qty_limit - joinCount <= 0)) {
                isEnable = false;
                mErrorDescription = "活动报名人数已满";
                mApplyAction.setText("人数已满");
                mApplyAction.setEnabled(false);
            }*/ else if (new BigDecimal(System.currentTimeMillis()).compareTo(new BigDecimal(endDate != null ? endDate.getTime() : 0)) == 1) {
                isEnable = false;
                mErrorDescription = "活动已经结束";
                mApplyAction.setEnabled(false);
                mApplyAction.setText("已经结束");
            } else {
                isEnable = true;
                mApplyAction.setEnabled(true);
                mApplyAction.setClickable(true);
                mApplyAction.setText("参加活动");
            }
        }

        String description = data.getDESCRIPTION();
        if (description != null)
            mWvActionDetailDescribe.loadDataWithBaseURL(null, description, "text/html", "UTF-8", null);


        if (!isRedPacketAction) {
            //轮播图数据
            ActionDetailBean.DataBean.IMGSBean imgs = data.getIMGS();
            if (imgs != null) {
                initBannerData(imgs.getList());
            }
            initItemData(data);

            //如果当前界面为 买家界面且不是红包活动，初始化收藏状态
            if (mType == TYPE_PURCHASER) {
                setCollection();
            }
        }
    }

    /**
     * 初始化活动子项数据
     *
     * @param data 活动子项数据
     */
    private void initItemData(ActionDetailBean.DataBean data) {
        //费用项目数据
        List<ActionDetailBean.DataBean.ITEMSBean> items = data.getITEMS();
        if (items != null) {
            int sort_id = data.getSORT_ID();
            if (mActivityDetailAdapter == null)
                mActivityDetailAdapter = new ActivityDetailItemAdapter();

            mActivityDetailAdapter.setDataAndKind(items, sort_id);

            if (IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY == sort_id) {
                BigDecimal bigDecimal = new BigDecimal(Float.MAX_VALUE);
                BigDecimal temp;
                String str;
                for (ActionDetailBean.DataBean.ITEMSBean itemsBean : items) {
                    if (itemsBean != null && (str = itemsBean.getSCORE()) != null) {
                        if (bigDecimal.compareTo(temp = new BigDecimal(str)) == 1) {
                            bigDecimal = temp;
                        }
                    }
                }
                mTvActionCost.setText(String.format(getString(net.twoant.master.R.string.action_integral), StringUtils.subZeroAndDot(bigDecimal.compareTo(new BigDecimal(Float.MAX_VALUE)) == 0 ? "--" : bigDecimal.toString())));
            } else {
                BigDecimal bigDecimal = new BigDecimal(Float.MAX_VALUE);
                BigDecimal temp;
                String str;
                for (ActionDetailBean.DataBean.ITEMSBean itemsBean : items) {
                    if (itemsBean != null && (str = itemsBean.getPRICE()) != null) {
                        if (bigDecimal.compareTo(temp = new BigDecimal(str)) == 1) {
                            bigDecimal = temp;
                        }
                    }
                }
                mTvActionCost.setText(String.format(getString(net.twoant.master.R.string.action_price), StringUtils.subZeroAndDot(bigDecimal.compareTo(new BigDecimal(Float.MAX_VALUE)) == 0 ? "--" : bigDecimal.toString())));
            }
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (id == ID_COLLECTION) {
            ToastUtil.showShort(isCollectionActivity ? "取消收藏失败" : "收藏失败");
        } else if (id == ID_DRAW_RED_PACKET) {
            ToastUtil.showShort("领取失败");
        } else {
            mHintDialogUtil.showError(NetworkUtils.getNetworkStateDescription(call, e), true, true);
        }
    }

    /**
     * 初始化View
     */
    @SuppressWarnings("unchecked")
    private void initView() {
        Toolbar mToolbarActionDetail = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("活动");
        setSupportActionBar(mToolbarActionDetail);
        mToolbarActionDetail.setNavigationOnClickListener(this);
        mToolbarActionDetail.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == net.twoant.master.R.id.action_share) {
                    if (mUmShareHelper == null)
                        mUmShareHelper = new UMShareHelper(ActionDetailActivity.this);
                    mUmShareHelper.showDialogAtBottom("通过以下方式邀请朋友参与活动", true);
                }
                return true;
            }
        });

        if (isRedPacketAction) {
            this.mTvPriceRedPacket = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_price_red_packet);
            this.mTvRedPacketNameRedPacket = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_red_packet_name_red_packet);
            this.mTvRedPacketDescriptionRedPacket = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_red_packet_description_red_packet);
            this.mTvValidDateRedPacket = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_valid_date_red_packet);
            this.mTvConditionRedPacket = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_condition_red_packet);
            this.mBtnDrawRedPacket = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_draw_red_packet);
        } else {

            mCbConvenientBanner = (ConvenientBanner) findViewById(net.twoant.master.R.id.cb_convenientBanner);
            ViewGroup.LayoutParams layoutParams = mCbConvenientBanner.getLayoutParams();
            layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() * 374F / 640F + 0.5F);
            RecyclerView mRvEnrolledPerson = (RecyclerView) findViewById(net.twoant.master.R.id.rv_enrolled_person);
            mRvEnrolledPerson.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            //用户列表适配器
            mRvEnrolledPerson.setAdapter(new ActionJoinUserAdapter(ActionJoinUserAdapter.HORIZONTAL, mActivityId, this));
            mWvActionDetailDescribe = (WebView) findViewById(net.twoant.master.R.id.wv_action_detail_describe);
            mTvActionApplied = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_applied);
        }
        if (getIntent().getBooleanExtra(EXTRA_AUTO_SELECT_LOCATION, true)) {
            ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_address_header)).setText(AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCompletionAddress());

        } else {
            ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_address_header)).setText(AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCompletionAddress());
        }
        mDistanceText = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_distance);
        mTvTitle = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title);
        mTvActionMerchant = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_merchant);
        //跳转到商家
        mTvActionMerchant.setOnClickListener(this);
        mTvActionReleaseTime = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_release_time);
        mTvActionReadCount = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_read_count);
        mTvActionAddress = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_address);
        mTvActionContinueTime = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_continue_time);
        mTvActionApplyCount = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_apply_count);
        mTvActionCost = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_action_cost);

        switch (mType) {
            case TYPE_MERCHANT:
                View merchantView = ((ViewStub) findViewById(net.twoant.master.R.id.view_stub_merchant_action_detail)).inflate();
                merchantView.findViewById(net.twoant.master.R.id.btn_examine_statement).setOnClickListener(this);
                break;
            case TYPE_PURCHASER:
                //如果是红包活动，则不显示，直接返回。
                if (isRedPacketAction) return;

                View purchaserView = ((ViewStub) findViewById(net.twoant.master.R.id.view_stub_purchaser_action_detail)).inflate();
                mTvCollectionImage = (AppCompatImageView) purchaserView.findViewById(net.twoant.master.R.id.iv_collection);
                mChatMerchant = (AppCompatTextView) purchaserView.findViewById(net.twoant.master.R.id.tv_server_action_detail);
                findViewById(net.twoant.master.R.id.ll_collection_action_detail).setOnClickListener(this);
                mApplyAction = (AppCompatTextView) purchaserView.findViewById(net.twoant.master.R.id.tv_join_action_detail);
                mApplyAction.setOnClickListener(this);
                break;
            default:
                throw new IllegalArgumentException("mType 类型找不到");
        }
    }


    /**
     * 初始化填写信息 dialog
     */
    @SuppressWarnings("InflateParams")
    private void initInfoDialog() {

        if (mWriteInfoDialog == null) {
            View view = getLayoutInflater().inflate(net.twoant.master.R.layout.yh_dialog_activity_write_info, null);
            mWriteInfoDialog = new ActionDetailWriteInfoDialog(this);
            view.findViewById(net.twoant.master.R.id.iv_close).setOnClickListener(this);
            view.findViewById(net.twoant.master.R.id.btn_apply).setOnClickListener(this);
            mInputName = (AppCompatEditText) view.findViewById(net.twoant.master.R.id.tv_name);

            AiSouUserBean instance = AiSouAppInfoModel.getInstance().getAiSouUserBean();
            mInputName.setText(TextUtils.isEmpty(instance.getRealName()) ? instance.getNickName() : instance.getRealName());
            mInputPhone = (AppCompatEditText) view.findViewById(net.twoant.master.R.id.tv_phone);
            mInputPhone.setText(instance.getPhone());
            mWriteInfoDialog.setContentView(view);
            mWriteInfoDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mBottomSheetDialog != null && !mBottomSheetDialog.isShowing()) {
                        mBottomSheetDialog.show();
                    } else {
                        initItemDialog();
                        mBottomSheetDialog.show();
                    }
                }
            });
        }

    }

    /**
     * 初始化列表项dialog
     */
    @SuppressWarnings("InflateParams")
    private void initItemDialog() {
        if (mBottomSheetDialog == null) {
            mBottomSheetDialog = new BottomSheetDialog(this, net.twoant.master.R.style.BottomSheetDialog);
            View view = getLayoutInflater().inflate(net.twoant.master.R.layout.yh_dialog_activitiy_item, null);
            RecyclerView recycler = (RecyclerView) view.findViewById(net.twoant.master.R.id.rv_item_charge_dialog);
            view.findViewById(net.twoant.master.R.id.btn_next).setOnClickListener(this);
            view.findViewById(net.twoant.master.R.id.btn_close_dialog).setOnClickListener(this);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.addItemDecoration(new RecyclerViewItemDecoration(this, net.twoant.master.R.color.mediumGreyColor, 0, net.twoant.master.R.dimen.px_2));
            mActivityDetailAdapter = new ActivityDetailItemAdapter();
            recycler.setAdapter(mActivityDetailAdapter);
            mBottomSheetDialog.setContentView(view);
            ViewParent parent = view.getParent();
            if (parent instanceof View) {
                mBottomSheetBehavior = BottomSheetBehavior.from(((View) parent));
            }
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.setCanceledOnTouchOutside(true);
        }
    }

    private ArrayList<String> mBannerData;

    /**
     * 初始化banner
     *
     * @param result 数据
     */
    private void initBannerData(ArrayList<String> result) {
        //为null就初始化一个
        if (result == null) {
            result = new ArrayList<>();
            result.add("");
        }

        if (result.size() == 0) {
            result.add("");
        }

        mBannerData = result;

        if (mViewHolderCreator == null)
            mViewHolderCreator = new BannerViewHolder.ViewHolderCreator();

        mCbConvenientBanner.setPages(mViewHolderCreator, result);


        mCbConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                displayImage(position);
            }
        })
                .setPageIndicator(new int[]{net.twoant.master.R.drawable.yh_banner_grey_dot, net.twoant.master.R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setCanLoop(true);


    }

    /**
     * 展示照片
     */
    private void displayImage(int position) {
        ArrayList<String> images = new ArrayList<>();
        if (mBannerData != null) {
            for (String str : mBannerData)
                images.add(ApiConstants.ACTIVITY_IMAGE_URL_HEAD + str);
        }
        Intent intent = new Intent(this, ImageScaleActivity.class);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, images);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
        this.overridePendingTransition(net.twoant.master.R.anim.pv_my_scale_action, net.twoant.master.R.anim.pv_my_alpha_action);
    }


    /*
    private void initPrimaryData(DataRow parse) {
        DataRow dataRow;
        if (parse != null && (dataRow = parse.getRow("DATA")) != null) {
            mTvTitle.setText(dataRow.getString("TITLE"));

            mTvActionMerchant.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            mTvActionMerchant.setText(dataRow.getString("SHOP_NM"));
            mTvActionMerchant.setTag(dataRow.getString("SHOP_ID"));

            isCollectionActivity = dataRow.getBoolean("IS_COL", false);
            setCollection();

            String start_time = dataRow.getString("START_TIME");
            String end_time = dataRow.getString("END_TIME");
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy.MM.dd HH:mm", Locale.CHINA);
            try {
                String start = simpleDateFormat.format(simple.parse(start_time));
                String end = simpleDateFormat.format(simple.parse(end_time));
                mTvActionReleaseTime.setText(start);
                mTvActionContinueTime.setText(String.valueOf(start + " - " + end));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            mTvActionReadCount.setText(dataRow.getString("CLICK"));
            mTvActionAddress.setText(dataRow.getString("ADDRESS"));
            int join_qty_limit = dataRow.getInt("JOIN_QTY_LIMIT");
            int joinCount = dataRow.getInt("JOIN_QTY");
            mTvActionApplyCount.setText(String.valueOf("已报名" + joinCount + "人/" + (join_qty_limit == 0 ? "不限制名额" : join_qty_limit)));
            mTvActionApplied.setText(String.format(getString(R.string.action_applied), joinCount));


            dataRow.getString("DESCRIPTION");

            //轮播图数据
            DataRow bannerData = dataRow.getRow("IMGS");
            if (bannerData != null) {
                ArrayList<String> imgUrl = new ArrayList<>();

                for (int i = 0, size = bannerData.size(); i < size; ++i) {
                    String activity_imgs = bannerData.getString("ACTIVITY_IMG" + i);
                    if (activity_imgs != null)
                        imgUrl.add(activity_imgs);
                }
                initBannerData(false, imgUrl);
            }

            //费用项目数据
            DataSet items = dataRow.getSet("ITEMS");//条目
            if (items != null) {
                int sort_id = dataRow.getInt("SORT_ID");
                if (mActivityDetailAdapter == null)
                    mActivityDetailAdapter = new ActivityDetailItemAdapter();

                mActivityDetailAdapter.setDataAndKind(dataRow.getSet("ITEMS"), sort_id);
                initItemDialog();
                initInfoDialog();

                if (IRecyclerViewConstant.KIND_INTEGRAL_ACTIVITY == sort_id) {
                    DataRow score = items.minRow("SCORE");
                    mTvActionCost.setText(String.format(getString(R.string.action_integral), score == null ? "-" : score.getString("SCORE")));
                } else {
                    DataRow price = items.minRow("PRICE");
                    mTvActionCost.setText(String.format(getString(R.string.action_price), price == null ? "" : price.getString("PRICE")));
                }
            }
        }
    }*/

}
