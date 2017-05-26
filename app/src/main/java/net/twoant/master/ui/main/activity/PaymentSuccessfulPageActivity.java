package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ContentFrameLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import net.twoant.master.R;
import net.twoant.master.app.AppManager;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.ui.main.adapter.PaymentSuccessfulViewPageAdapter;
import net.twoant.master.ui.main.bean.PayPostOrderBean;
import net.twoant.master.ui.main.presenter.ThirdPartyPayOrderControl;
import net.twoant.master.ui.main.widget.MarqueeTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by S_Y_H on 2017/4/14.
 * 订单支付完成页
 */

public class PaymentSuccessfulPageActivity extends BaseActivity implements View.OnClickListener {

    private final static String EXTRA_DATA = "PS_PA_E_DA";
    private final static String ACTION_START = "PS_PA_A_ST";
    private PayPostOrderBean mPayPostOrderBean;

    public static void startActivity(Activity activity, @NonNull PayPostOrderBean payPostOrderBean) {
        Intent intent = new Intent(activity, PaymentSuccessfulPageActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_DATA, payPostOrderBean);
        activity.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        AppManager.cleanPaymentActivity();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //完成
            case R.id.btn_complete:
                AppManager.cleanPaymentActivity();
                PaymentSuccessfulPageActivity.this.finish();
                break;
            default:
                if (v instanceof ViewPager && 1 == ((ViewPager) v).getCurrentItem()) {
                    DiscoverActivity.startActivity(PaymentSuccessfulPageActivity.this, getString(R.string.payment_prize_page), "");// TODO: 2017/4/15
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        Window window = getWindow();
        if (null != window) {
            View decorView = window.getDecorView();
            if (null != decorView) {
                decorView.setPadding(0, 0, 0, 0);
            }
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.gravity = Gravity.BOTTOM;
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(attributes);
        }
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mPayPostOrderBean = intent.getParcelableExtra(EXTRA_DATA);
        return R.layout.yh_activity_payment_sucessful_page;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        /*this.mIvImageState = (AppCompatImageView) findViewById(R.id.iv_image_state);
        this.mTvHintInfo = (AppCompatTextView) findViewById(R.id.tv_hint_info);
        ((AppCompatTextView) findViewById(R.id.tv_payment_state)).setText("支付状态");*/
        ((AppCompatTextView) findViewById(R.id.tv_bargain_price)).setText(mPayPostOrderBean.getSumPayPriceResult());
        ((MarqueeTextView) findViewById(R.id.tv_bargain_merchant)).setText(mPayPostOrderBean.getShopName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        ((AppCompatTextView) findViewById(R.id.tv_bargain_date)).setText(dateFormat.format(new Date()));
        switch (mPayPostOrderBean.getCurrentPaymentPlatform()) {
            case ThirdPartyPayOrderControl.ID_ALI_PLATFORM:
                ((AppCompatTextView) findViewById(R.id.tv_payment_platform)).setText(R.string.payment_platform_ali_pay);
                break;

            case ThirdPartyPayOrderControl.ID_OWN_PLATFORM:
                ((AppCompatTextView) findViewById(R.id.tv_payment_platform)).setText(R.string.payment_platform_own);
                break;

            case ThirdPartyPayOrderControl.ID_WE_CHANT_PLATFORM:
                ((AppCompatTextView) findViewById(R.id.tv_payment_platform)).setText(R.string.payment_platform_we_chat);
                break;
        }
        findViewById(R.id.btn_complete).setOnClickListener(this);
        initViewPager();

    }

    private void initViewPager() {
        final ArrayList<View> views = new ArrayList<>(2);
        views.add(getSuccessfulLayout());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_top_content);

        if (mPayPostOrderBean.isHasPrize()) {
            views.add(getPrizeLayout(viewPager));
        }

        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = (int) (DisplayDimensionUtils.getScreenWidth() / 2.0F + 0.5F);
        viewPager.setLayoutParams(layoutParams);
        viewPager.setAdapter(new PaymentSuccessfulViewPageAdapter(views));
    }

    /**
     * 获取抽奖布局
     */
    @NonNull
    private AppCompatImageView getPrizeLayout(final ViewPager viewPager) {
        AppCompatImageView imageView = new AppCompatImageView(PaymentSuccessfulPageActivity.this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.ic_merchant_prize);
        imageView.setLayoutParams(new ViewPager.LayoutParams());
        viewPager.setClickable(true);
        viewPager.setOnClickListener(this);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(1, true);
            }
        }, 1000);
        return imageView;
    }

    /**
     * 获取默认的支付成功布局
     */
    @NonNull
    private ContentFrameLayout getSuccessfulLayout() {
        ContentFrameLayout layoutCompat = new ContentFrameLayout(PaymentSuccessfulPageActivity.this);
        layoutCompat.setLayoutParams(new ViewPager.LayoutParams());
        AppCompatTextView textView = new AppCompatTextView(PaymentSuccessfulPageActivity.this);
        textView.setText(R.string.payment_pay_successful);
        textView.setTextColor(ContextCompat.getColor(PaymentSuccessfulPageActivity.this, R.color.colorPrimary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.px_text_60));
        Drawable drawable = ContextCompat.getDrawable(PaymentSuccessfulPageActivity.this, R.drawable.duigou_big);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.px_10));
        textView.setLayoutParams(new ContentFrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        layoutCompat.addView(textView);
        return layoutCompat;
    }
}
