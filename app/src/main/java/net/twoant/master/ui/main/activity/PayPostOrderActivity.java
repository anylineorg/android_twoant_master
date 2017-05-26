package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.bean.PayPostOrderBean;
import net.twoant.master.ui.main.presenter.ThirdPartyPayOrderControl;
import net.twoant.master.widget.entry.DataRow;

/**
 * Created by S_Y_H on 2017/4/13.
 * 调用支付宝、微信支付的Activity
 */

public class PayPostOrderActivity extends BaseActivity implements ThirdPartyPayOrderControl.IOnOrderResultListener, View.OnClickListener, AppCompatRadioButton.OnCheckedChangeListener {
    private final static String ACTION_START = "PP_OA_ST";
    private final static String EXTRA_DATA = "PP_OA_DA";
    private AppCompatRadioButton mRbAliPay;
    private ThirdPartyPayOrderControl mThirdPartyPayOrderControl;
    private AppCompatRadioButton mRbWeChatPay;
    private PayPostOrderBean mPayPostOrderBean;
    private ContentLoadingProgressBar mProgressBar;
    private AppCompatButton mSubmitOrder;

    public static void startActivityForResult(@NonNull Activity activity, @NonNull PayPostOrderBean payPostOrderBean, int requestCode) {
        Intent intent = new Intent(activity, PayPostOrderActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_DATA, payPostOrderBean);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(@NonNull Activity activity, @NonNull PayPostOrderBean payPostOrderBean) {
        Intent intent = new Intent(activity, PayPostOrderActivity.class);
        intent.setAction(ACTION_START);
        payPostOrderBean.setUseDefPaymentCompletePage(true);
        intent.putExtra(EXTRA_DATA, payPostOrderBean);
        activity.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        AppManager.addPaymentActivity(this);
        Window window = getWindow();
        if (null != window) {
            View decorView = window.getDecorView();
            if (null != decorView) {
                decorView.setPadding(0, 0, 0, 0);
            }
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            window.setAttributes(lp);
        }
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mPayPostOrderBean = intent.getParcelableExtra(EXTRA_DATA);
        if (null == mPayPostOrderBean) {
            PayPostOrderActivity.this.finish();
        }
        return net.twoant.master.R.layout.yh_activity_post_order;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.removePaymentActivity(this);
        if (null != mThirdPartyPayOrderControl) {
            mThirdPartyPayOrderControl.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_close:
                if (mProgressBar.isShown()) {
                    return;
                }
                PayPostOrderActivity.this.finish();
                break;

            case net.twoant.master.R.id.ll_we_chat_parent:
                if (!mRbWeChatPay.isChecked()) {
                    mRbWeChatPay.setChecked(true);
                }
                break;

            case net.twoant.master.R.id.ll_ali_pay_parent:
                if (!mRbAliPay.isChecked()) {
                    mRbAliPay.setChecked(true);
                }
                break;

            case net.twoant.master.R.id.btn_pay_order:
                if (mProgressBar.isShown()) {
                    return;
                }
                mSubmitOrder.setEnabled(false);
                mProgressBar.show();

                if (null == mThirdPartyPayOrderControl) {
                    mThirdPartyPayOrderControl = new ThirdPartyPayOrderControl(this, PayPostOrderActivity.this);
                }
                if (mRbAliPay.isChecked()) {
                    mThirdPartyPayOrderControl.getAliPayOrder(mPayPostOrderBean.getOrderId());
                } else if (mRbWeChatPay.isChecked()) {
                    mThirdPartyPayOrderControl.getWeChatOrder(mPayPostOrderBean.getOrderId());
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.payment_please_check_third_party);
                }
                break;
        }
    }

    @Override
    public boolean onOrderResultListener(boolean isSuccessful, @NonNull DataRow response, int id) {
        if (isSuccessful && !PayPostOrderActivity.this.isFinishing()) {
            mPayPostOrderBean.setCurrentPaymentPlatform(id);
        }
        return true;
    }

    @Override
    public void onPaymentResult(int state, @NonNull String hintInfo) {
        if (PayPostOrderActivity.this.isFinishing()) {
            return;
        }
        switch (state) {
            case ThirdPartyPayOrderControl.STATE_SUCCESSFUL:
                if (mPayPostOrderBean.isUseDefPaymentCompletePage()) {
                    PaymentSuccessfulPageActivity.startActivity(PayPostOrderActivity.this, mPayPostOrderBean);
                    PayPostOrderActivity.this.finish();
                } else {
                    setResult(Activity.RESULT_OK);
                    PayPostOrderActivity.this.finish();
                }
                break;

            case ThirdPartyPayOrderControl.STATE_FAIL:
            case ThirdPartyPayOrderControl.STATE_EXCEPTION:
                if (mPayPostOrderBean.isUseDefPaymentCompletePage()) {
                    ToastUtil.showShort(hintInfo);
                    if (null != mProgressBar) {
                        mProgressBar.hide();
                        mSubmitOrder.setEnabled(true);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("data", intent);
                    setResult(Activity.RESULT_OK, intent);
                    PayPostOrderActivity.this.finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mThirdPartyPayOrderControl) {
            mThirdPartyPayOrderControl.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        if (null != mProgressBar && !mProgressBar.isShown()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == mRbAliPay) {
                mRbWeChatPay.setChecked(false);
            } else if (buttonView == mRbWeChatPay) {
                mRbAliPay.setChecked(false);
            }
        }
    }

    private void initView() {
        findViewById(net.twoant.master.R.id.btn_close).setOnClickListener(this);
        mSubmitOrder = (AppCompatButton) findViewById(net.twoant.master.R.id.btn_pay_order);
        mSubmitOrder.setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ll_ali_pay_parent).setOnClickListener(this);
        findViewById(net.twoant.master.R.id.ll_we_chat_parent).setOnClickListener(this);
        this.mProgressBar = (ContentLoadingProgressBar) findViewById(net.twoant.master.R.id.pb_progress_bar);
        this.mRbWeChatPay = (AppCompatRadioButton) findViewById(net.twoant.master.R.id.rb_we_chat_pay);
        this.mRbWeChatPay.setOnCheckedChangeListener(this);
        this.mRbAliPay = (AppCompatRadioButton) findViewById(net.twoant.master.R.id.rb_ali_pay);
        this.mRbAliPay.setOnCheckedChangeListener(this);
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_remain_price)).setText(String.format(getString(net.twoant.master.R.string.payment_remain_price
        ), mPayPostOrderBean.getNeedPayPrice()));
    }
}
