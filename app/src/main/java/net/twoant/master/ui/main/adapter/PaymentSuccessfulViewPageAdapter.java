package net.twoant.master.ui.main.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by S_Y_H on 2017/4/15.
 * 订单支付成页 的viewPage Adapter
 */

public class PaymentSuccessfulViewPageAdapter extends PagerAdapter {

    private List<View> mViewList;

    public PaymentSuccessfulViewPageAdapter(List<View> view) {
        this.mViewList = view;
    }

    @Override
    public int getCount() {
        return null == mViewList ? 0 : mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View child = mViewList.get(position);
        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }
}
