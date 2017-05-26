package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.adapter.ActivityPaymentAdapter;
import net.twoant.master.ui.main.bean.ActionDetailBean;
import net.twoant.master.ui.main.bean.PayPostOrderBean;
import net.twoant.master.ui.main.bean.PaymentDataBean;
import net.twoant.master.ui.main.interfaces.IOnPayBtnClickListener;
import net.twoant.master.ui.main.model.PayOrderTextWatcher;
import net.twoant.master.ui.main.presenter.ThirdPartyPayOrderControl;
import net.twoant.master.ui.my_center.fragment.SetPayPasswordActivity;
import net.twoant.master.widget.PasswordDialog;
import net.twoant.master.widget.entry.DataRow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import okhttp3.Call;

public class ActionPaymentActivity extends BaseActivity implements HttpConnectedUtils.IOnStartNetworkSimpleCallBack, IOnPayBtnClickListener {

    private final static String EXTRA_DATA = "ActionPaymentActivity_data";
    private final static String EXTRA_DATA_LIST = "ActionPaymentActivity_list";

    /**
     * 提交订单
     */
    private final static int ID_SUBMIT_ODER = 1;

    /**
     * 验证支付密码
     */
    private final static int ID_AFFIRM_PASSWORD = 2;

    /**
     * 验证是否有支付密码
     */
    private final static int ID_HAS_PASSWORD = 3;

    private ArrayList<ActionDetailBean.DataBean.ITEMSBean> mDataRows;
    private PaymentDataBean mPaymentDataBean;
    private HttpConnectedUtils mHttpConnectedUtils;
    /**
     * 密码输入弹窗
     */
    private PasswordDialog mPayDialog;

    /**
     * 未设置支付密码弹窗
     */
    private AlertDialog mWithoutPasswordAlertDialog;

    /**
     * 加载提示 弹窗
     */
    private HintDialogUtil mHintDialog;

    private PayPostOrderBean mPayPostOrderBean;
    /**
     * 是否有支付密码
     */
    private boolean hasPassword;

    private String mPassword;

    private ActivityPaymentAdapter mActivityPaymentAdapter;


    /**
     * @param activity activity
     * @param dataRows 数据
     * @param data     数据
     */
    public static void startActivity(@NonNull PaymentDataBean data, Activity activity
            , @NonNull ArrayList<ActionDetailBean.DataBean.ITEMSBean> dataRows) {
        Intent intent = new Intent(activity, ActionPaymentActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_DATA_LIST, dataRows);
        intent.putExtra(EXTRA_DATA, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }


    @Override
    protected int getLayoutId() {
        AppManager.addPaymentActivity(ActionPaymentActivity.this);
        return R.layout.yh_activity_action_payment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        affirmHasPassword();
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initIntentData(getIntent());
        initToolbar();

        if (savedInstanceState != null) {
            mDataRows = savedInstanceState.getParcelableArrayList("mDR");
            mPaymentDataBean = savedInstanceState.getParcelable("mPDB");
            hasPassword = savedInstanceState.getBoolean("hP");
        }

        initView();
        mHttpConnectedUtils = HttpConnectedUtils.getInstance(ActionPaymentActivity.this);
        mHintDialog = new HintDialogUtil(ActionPaymentActivity.this).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ActionPaymentActivity.this.finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDR", mDataRows);
        outState.putParcelable("mPDB", mPaymentDataBean);
        outState.putBoolean("hP", hasPassword);
    }

    /**
     * 确认是否有支付密码
     */
    private void affirmHasPassword() {
        if (mHintDialog != null)
            mHintDialog.showLoading(R.string.payment_dialog_confirm_password, true, false);
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());

        if (mHttpConnectedUtils != null)
            mHttpConnectedUtils.startNetworkGetString(ID_HAS_PASSWORD, arrayMap, ApiConstants.STATE_PAY_PASSWORD);
    }

    private void initView() {
        ((AppCompatTextView) findViewById(R.id.tv_title_tool_bar)).setText(R.string.action_pay_title);
        RecyclerView mRecycler = (RecyclerView) findViewById(R.id.rv_recycler_view);
        mActivityPaymentAdapter = new ActivityPaymentAdapter(mDataRows, mPaymentDataBean);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mActivityPaymentAdapter.setOnPayBtnClickListener(this);
        mRecycler.addItemDecoration(new InnerItemDecoration(this, R.dimen.px_2, R.color.dividerLineColor));
        mRecycler.setAdapter(mActivityPaymentAdapter);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
                    MainActivity.closeIME(false, recyclerView);
            }

        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionPaymentActivity.this.finish();
            }
        });
        toolbar.setTitle(null);
    }

    private void initIntentData(Intent intent) {
        if (intent == null) throw new IllegalArgumentException("intent 不能为null");
        mDataRows = intent.getParcelableArrayListExtra(EXTRA_DATA_LIST);
        mPaymentDataBean = intent.getParcelableExtra(EXTRA_DATA);
    }

    @Override
    public void onPayBtnClickListener(String integral, String wallet, String shopId, List<String> actionId) {
        IdentityHashMap<String, String> arrayMap = new IdentityHashMap<>(16);
        if ((!ActivityPaymentAdapter.isEmpty(integral) || !ActivityPaymentAdapter.isEmpty(wallet)) &&
                TextUtils.isEmpty(mPassword)) {
            mHintDialog.showError(R.string.action_verification_fail);
            return;
        }
        arrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());
        if (!TextUtils.isEmpty(mPassword)) {
            arrayMap.put("pwd", mPassword);
        }
        arrayMap.put("score", integral);
        arrayMap.put("purse", wallet);
        for (String str : actionId) {
            arrayMap.put(new String("activity"), str);
            arrayMap.put(new String("qty"), "1");
        }
        arrayMap.put("sort", "3");
        arrayMap.put("shop", shopId);
        arrayMap.put("receive_name", mPaymentDataBean.getName());
        arrayMap.put("receive_tel", mPaymentDataBean.getPhone());
        mPassword = null;
        mHttpConnectedUtils.startNetworkGetString(ID_SUBMIT_ODER, arrayMap, ApiConstants.ACTIVITY_GET_RED_PACKET);
    }

    @Override
    public void showPasswordDialog(String useIntegral, String useWallet) {
        if (hasPassword) {
            if (mPayDialog == null) {
                mPayDialog = new PasswordDialog(this);
                mPayDialog.setOnCompleteInputPassword(new PasswordDialog.IOnCompleteInputPassword() {
                    @Override
                    public void onCompleteInputPassword(String password) {
                        mPassword = password;
                        ArrayMap<String, String> pass = new ArrayMap<>();
                        pass.put("user", AiSouAppInfoModel.getInstance().getUID());
                        pass.put("pwd", mPassword);
                        mHttpConnectedUtils.startNetworkGetString(ID_AFFIRM_PASSWORD, pass, ApiConstants.CHECKPAYPWD);
                    }
                });
            }
            if (!mPayDialog.isShowing()) {
                mPayDialog.setCurrentMerchantName(null == mDataRows || mDataRows.isEmpty() ? "" : mDataRows.get(0).getSHOP_NM());
                mPayDialog.setCurrentPrice(useIntegral, useWallet);
                mPayDialog.showDialog(true);
            }

        } else {
            if (mWithoutPasswordAlertDialog == null)
                mWithoutPasswordAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                        .setTitle(R.string.payment_no_password_hint).setMessage(R.string.payment_set_password)
                        .setPositiveButton(R.string.payment_setting, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ActionPaymentActivity.this, SetPayPasswordActivity.class);
                                ActionPaymentActivity.this.startActivity(intent);
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.payment_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(true).create();
            mWithoutPasswordAlertDialog.show();
        }
    }

    @Override
    public void onResponse(String response, int id) {
        if (ActionPaymentActivity.this.isFinishing()) {
            return;
        }
        DataRow dataRow = DataRow.parseJson(response);
        if (dataRow == null) return;
        MainActivity.checkState(this, dataRow);
        switch (id) {
            case ID_AFFIRM_PASSWORD:
                if (dataRow.getBoolean("result", false)) {
                    if (null != mActivityPaymentAdapter) {
                        mHintDialog.showLoading(R.string.payment_apply_order, true, false);
                        mActivityPaymentAdapter.applyOrder();
                    }
                    mPayDialog.dismiss();
                } else {
                    mPayDialog.cleanPassword();
                    ToastUtil.showShort(dataRow.getStringDef("message", getString(R.string.payment_password_error)));
                }
                break;
            case ID_HAS_PASSWORD:
                hasPassword = "1".equals(dataRow.getString("DATA"));
                mHintDialog.dismissDialog();
                break;
            case ID_SUBMIT_ODER:
                if (!dataRow.getBoolean("result", false)) {
                    ToastUtil.showShort(R.string.payment_order_invalid);
                    return;
                }
                if (null != (dataRow = dataRow.getRow("DATA"))) {
                    String cash_price = dataRow.getString("CASH_PRICE");
                    if (null == mPayPostOrderBean) {//TODO 活动是否需要 抽奖？
                        mPayPostOrderBean = new PayPostOrderBean(mPaymentDataBean.getShopId(), mPaymentDataBean.getShopName(), false);
                    }
                    mPayPostOrderBean.setSumPayPriceResult(mPaymentDataBean.getFinalSumDescription());
                    mPayPostOrderBean.setOrderIdAndPrice(dataRow.getString("ID"), cash_price);
                    BigDecimal bigDecimal = new BigDecimal(PayOrderTextWatcher.checkInputNumber(cash_price, true));
                    if (0 == BigDecimal.ZERO.compareTo(bigDecimal)) {
                        mHintDialog.dismissDialog();
                        if (mPayDialog != null)
                            mPayDialog.dismiss();

                        // 跳转页面
                        mPayPostOrderBean.setCurrentPaymentPlatform(ThirdPartyPayOrderControl.ID_OWN_PLATFORM);
                        PaymentSuccessfulPageActivity.startActivity(ActionPaymentActivity.this, mPayPostOrderBean);
                        this.finish();
                    } else {
                        mHintDialog.dismissDialog();
                        PayPostOrderActivity.startActivity(ActionPaymentActivity.this, mPayPostOrderBean);
                    }
                } else {
                    ToastUtil.showShort(R.string.payment_order_invalid);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.removePaymentActivity(ActionPaymentActivity.this);

        if (mWithoutPasswordAlertDialog != null && mWithoutPasswordAlertDialog.isShowing()) {
            mWithoutPasswordAlertDialog.dismiss();
        }
        mWithoutPasswordAlertDialog = null;

        if (mPayDialog != null) {
            mPayDialog.dismiss();
            mPayDialog.onDestroy();
            mPayDialog = null;
        }

        if (mHintDialog != null) {
            mHintDialog.dismissDialog();
            mHintDialog = null;
        }
        if (null != mActivityPaymentAdapter) {
            mActivityPaymentAdapter.onDestroy();
        }
        if (null != mHttpConnectedUtils) {
            mHttpConnectedUtils.onDestroy();
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (ActionPaymentActivity.this.isFinishing()) {
            return;
        }
        if (ID_AFFIRM_PASSWORD == id) {
            if (null != mPayDialog) {
                mPayDialog.dismiss();
            }
            if (null != mHintDialog) {
                mHintDialog.showError(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.action_password_fail)));
            }
        } else {
            ToastUtil.showShort(NetworkUtils.getNetworkStateDescription(call, e, "数据获取失败"));
        }
    }

    private static class InnerItemDecoration extends RecyclerView.ItemDecoration {

        private int mHeight;
        private Paint mPaint;

        private InnerItemDecoration(Context context, @DimenRes int height, @ColorRes int color) {
            mHeight = context.getResources().getDimensionPixelSize(height);
            mPaint = new Paint();
            mPaint.setColor(ContextCompat.getColor(context, color));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int itemCount = parent.getAdapter().getItemCount();
            int childAdapterPosition = parent.getChildAdapterPosition(view);

            if (childAdapterPosition == 0) {
                outRect.set(0, mHeight << 2, 0, 0);
                return;
            }

            if (childAdapterPosition == itemCount - 1) {
                outRect.set(0, mHeight << 3, 0, 0);
            } else {
                outRect.set(0, mHeight, 0, 0);

            }
        }
    }
}
