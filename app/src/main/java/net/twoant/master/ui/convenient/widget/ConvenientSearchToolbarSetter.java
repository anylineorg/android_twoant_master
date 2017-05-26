package net.twoant.master.ui.convenient.widget;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;

import net.twoant.master.R;
import net.twoant.master.ui.convenient.activity.PublishConvenientActivity;
import net.twoant.master.ui.main.widget.LocationToolbarSetter;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/16.
 * 便民信息的 toolbar 配置类
 */

public class ConvenientSearchToolbarSetter extends LocationToolbarSetter {


    public ConvenientSearchToolbarSetter(Activity activity) {
        super(activity);
    }

    @Override
    public boolean getRightIcon(ImageView view) {
        view.setImageResource(R.drawable.ic_goods_add);
        return true;
    }

    @Override
    public boolean getLeftIcon(ImageView view) {
        return false;
    }

    @Override
    protected void onDistrictSearched(boolean isSuccessful, @Nullable DistrictResult districtResult, @Nullable ArrayList<DistrictItem> district) {
        if (isSuccessful) {
           /*
            DistrictItem districtItem = district.get(0);
            LatLonPoint center = districtItem.getCenter();
            */

        }
    }

    @Override
    public boolean getRightText(TextView textView) {
        textView.setText("发布");
        return true;
    }

    @Override
    public void onRightClickListener(View view) {
        mActivity.startActivity(new Intent(mActivity, PublishConvenientActivity.class));
    }

    @Override
    public void onLeftIconClickListener(View view) {

    }
}
