package net.twoant.master.ui.my_center.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import net.twoant.master.common_utils.ImageLoader;

/**
 * Created by Administrator on 2017/1/6 0006.
 */
public class NetImageHolderView implements Holder<String> {
    private ImageView imageView;
    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {

        ImageLoader.getImageFromNetwork(imageView,data);
    }
}
