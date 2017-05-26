package net.twoant.master.ui.other.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.SharedPreferencesUtils;
import net.twoant.master.common_utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2016/11/17.9:37
 * 第一次使用的导航类
 */
public class NavigationActivity extends BaseActivity implements View.OnClickListener {

    private final static String ACTION_START = "NA_a_s";
    public ConvenientBanner mVpNavigation;
    private AppCompatButton mEnterBtn;
    private AppCompatButton mSkipBtn;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        intent.setAction(ACTION_START);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.act_fade_in_center, R.anim.act_fade_out_center);
            activity.finish();
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        return R.layout.yh_activity_navigation;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        BaseConfig.checkState(getIntent(), ACTION_START);
        initView();
        initBannerData();
    }

    private void initBannerData() {
        mVpNavigation.setPointViewVisible(true)
                .setPageIndicator(new int[]{R.drawable.yh_banner_grey_dot, R.drawable.yh_banner_orange_dot})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(3000)
                .setCanLoop(false);

        /*if (mVpNavigation.isTurning()) {
            mVpNavigation.stopTurning();
        }*/

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(R.drawable.img1);
        arrayList.add(R.drawable.img2);
        arrayList.add(R.drawable.img3);
        mVpNavigation.setPageTransformer(

                new ViewPager.PageTransformer() {
                    @Override
                    public void transformPage(View view, float position) {
//                        int pageWidth = view.getWidth() >> 1;
                        int pageHeight = view.getHeight() >> 1;
                        if (position < -1) {
                            // [-Infinity,-1)
                            // This page is way off-screen to the left.
                            view.setAlpha(0);
                        } else if (position <= 0) {
                            // a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
                            // { // [-1,0]
                            // Use the default slide transition when moving to the left page
                            view.setAlpha(1);
//                            view.setTranslationX(-position);
                            view.setTranslationY((-position) * pageHeight);
//                            view.setScaleX(position + 1);
                            view.setScaleY(position + 1);

                        } else if (position <= 1) {
                            // (0,1]
                            // Fade the page out.
                            view.setAlpha(1 - position);
                            // Counteract the default slide transition
//                            view.setTranslationX(-position);
                            view.setTranslationY(position * pageHeight);
                            // Scale the page down (between MIN_SCALE and 1)
//                            view.setScaleX(1 - position);
                            view.setScaleY(1 - position);
                        } else { // (1,+Infinity]
                            // This page is way off-screen to the right.
                            view.setAlpha(0);
                        }
                    }
                }

        );

        mVpNavigation.setPages(new CBViewHolderCreator<NavigationViewHolder>()

                               {
                                   @Override
                                   public NavigationViewHolder createHolder() {
                                       return new NavigationViewHolder();
                                   }
                               }

                , arrayList);

        mVpNavigation.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mVpNavigation.getViewPager().getLastItem()) {
                    if (mEnterBtn.getVisibility() != View.VISIBLE) {
                        mEnterBtn.animate().scaleX(1).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mEnterBtn.setVisibility(View.VISIBLE);
                            }
                        }).setDuration(300).start();
                    }
                    if (mSkipBtn.getVisibility() != View.GONE) {
                        mSkipBtn.animate().scaleY(.1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mSkipBtn.setVisibility(View.GONE);
                            }
                        }).setDuration(300).start();
                    }
                } else {
                    if (mEnterBtn.getVisibility() != View.GONE) {
                        mEnterBtn.animate().scaleX(.1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mEnterBtn.setVisibility(View.GONE);
                            }
                        }).setDuration(300).start();
                    }

                    if (mSkipBtn.getVisibility() != View.VISIBLE) {
                        mSkipBtn.animate().scaleY(1).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mSkipBtn.setVisibility(View.VISIBLE);
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter:

                SharedPreferences sharedPreferences = SharedPreferencesUtils.getSharedPreferences(AiSouAppInfoModel.NAME_SHARED_PREFERENCES);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(AiSouAppInfoModel.KEY_FLAG, -1);
                if (editor.commit()) {
                    StartActivity.skipActivity(NavigationActivity.this, sharedPreferences);
                } else {
                    ToastUtil.showShort(R.string.enter_navigation_fail);
                    NavigationActivity.this.finish();
                }
                break;
            case R.id.btn_skip:
                mVpNavigation.setcurrentitem(mVpNavigation.getViewPager().getLastItem());
                break;
        }
    }

    private static class NavigationViewHolder implements Holder<Integer> {

        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            mImageView = new ImageView(context);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return mImageView;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            mImageView.setImageResource(data);
        }
    }

    private void initView() {
        this.mVpNavigation = (ConvenientBanner) findViewById(R.id.vp_navigation);
        mEnterBtn = (AppCompatButton) findViewById(R.id.btn_enter);
        mEnterBtn.setOnClickListener(this);


        mSkipBtn = (AppCompatButton) findViewById(R.id.btn_skip);
        mSkipBtn.setOnClickListener(this);
    }
}
