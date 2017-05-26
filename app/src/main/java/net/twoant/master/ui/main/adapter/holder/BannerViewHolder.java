package net.twoant.master.ui.main.adapter.holder;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import net.twoant.master.R;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ImageLoader;

/**
 * Created by S_Y_H on 2016/12/22.
 * 广告展示，只有String类型的图片链接
 */

public class BannerViewHolder implements Holder<String> {

    private AppCompatImageView mImageView;

    private BannerViewHolder() {
    }

    @Override
    public View createView(Context context) {
        mImageView = new AppCompatImageView(context);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return mImageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        ImageLoader.getImageFromNetwork(mImageView, BaseConfig.getCorrectImageUrl(data), context, R.drawable.ic_def_large);
    }

    public static class ViewHolderCreator implements CBViewHolderCreator<BannerViewHolder> {

        @Override
        public BannerViewHolder createHolder() {
            return new BannerViewHolder();
        }
    }
}
