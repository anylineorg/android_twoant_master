package net.twoant.master.ui.main.widget;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.ui.main.activity.DiscoverActivity;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/15.
 * 默认首页头bar配置类
 */

public class HomePageToolbarSetter extends LocationToolbarSetter {

    private ImageView mRightHintIcon;

    public HomePageToolbarSetter(Activity activity) {
        super(activity);
        this.mActivity = activity;
        this.mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
    }

    @Override
    protected void onDistrictSearched(boolean isSuccessful, @Nullable DistrictResult districtResult, @Nullable ArrayList<DistrictItem> district) {
        if (isSuccessful) {
            AiSouLocationBean instance = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
            DistrictItem districtItem = district.get(0);
            LatLonPoint center = districtItem.getCenter();
            //切换状态 并设置数据
            instance.setSelectLatitudeAndLongitude(center.getLatitude(), center.getLongitude()
                    , districtItem.getName(), mUserSelectLocation, districtItem.getAdcode(), districtItem.getCitycode());
            GDLocationHelper.getInstance().stopLocation();
        }
    }

    /**
     * 显示消息提示点
     *
     * @param isShow true 显示
     */
    private void showInfoHint(boolean isShow) {
        if (isShow)
            mRightHintIcon.setImageResource(net.twoant.master.R.drawable.ic_exsit_info);
        else {
            mRightHintIcon.setImageResource(R.drawable.ic_none_info);
        }
    }

    @Override
    public boolean getRightIcon(ImageView view) {
        mRightHintIcon = view;
        view.setImageResource(R.drawable.ic_none_info);
        return true;
    }

    @Override
    public boolean getRightText(TextView textView) {
        textView.setText(net.twoant.master.R.string.main_page_info);
        return true;
    }

    @Override
    public void onRightClickListener(View view) {
        DiscoverActivity.startActivity(mActivity, "消息", ApiConstants.MESSAGE + mAiSouAppInfoModel.getUID());
    }

    @Override
    public void onLeftIconClickListener(View view) {

    }
}
