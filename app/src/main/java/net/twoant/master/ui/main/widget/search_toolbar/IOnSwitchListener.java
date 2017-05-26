package net.twoant.master.ui.main.widget.search_toolbar;

import android.support.annotation.Nullable;

import com.amap.api.services.district.DistrictItem;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/15.
 * 切换地址监听器
 */

public interface IOnSwitchListener {
    void onSwitchListener(boolean isSuccessful, @Nullable ArrayList<DistrictItem> districtItems);

}
