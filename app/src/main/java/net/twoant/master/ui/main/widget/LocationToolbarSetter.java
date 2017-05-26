package net.twoant.master.ui.main.widget;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.activity.MainActivity;
import net.twoant.master.ui.main.interfaces.ISaveRestoreInstance;
import net.twoant.master.ui.main.widget.search_toolbar.IOnSwitchListener;
import net.twoant.master.ui.main.widget.search_toolbar.ISearchToolbarSetter;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/15.
 * 带 地址选择的 头bar配置类
 */

public abstract class LocationToolbarSetter implements ISearchToolbarSetter, ISaveRestoreInstance<Bundle>, DistrictSearch.OnDistrictSearchListener {

    protected Activity mActivity;
    private DistrictSearch mDistrictSearch;
    private TextView mTvLocationNavigation;
    private DistrictSearchQuery mSearchQuery;
    private IOnSwitchListener iOnSwitchListener;
    protected AiSouAppInfoModel mAiSouAppInfoModel;
    protected String mUserSelectLocation;
    private GDLocationHelper.IOnLocationListener iOnLocationListener;
    private SelectLocationPopupWindow mSelectLocationPopupWindow;

    protected LocationToolbarSetter(Activity activity) {
        this.mActivity = activity;
        this.mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
    }

    /**
     * 设置地址切换监听
     */
    public void setOnSwitchListener(IOnSwitchListener iOnSwitchListener) {
        this.iOnSwitchListener = iOnSwitchListener;
    }

    @Override
    public boolean getLeftIcon(ImageView view) {
        return false;
    }

    @Override
    public boolean getLeftText(TextView textView) {
        mTvLocationNavigation = textView;
        if (mAiSouAppInfoModel.getAiSouLocationBean().getLocationSuccessfulState() || mAiSouAppInfoModel.getAiSouLocationBean().isUserSelectLocation()) {
            textView.setText(mAiSouAppInfoModel.getAiSouLocationBean().getCounty());
        } else {
            initGDListener();
            GDLocationHelper.getInstance().getOnceLocation(iOnLocationListener);
            textView.setText(R.string.main_page_county);
        }
        return true;
    }

    @Override
    public void onLeftTextClickListener(View view) {
        if (null != mActivity) {
            MainActivity.closeIME(false, mActivity.getCurrentFocus());
        }

        if (null == mSelectLocationPopupWindow) {
            mSelectLocationPopupWindow = new SelectLocationPopupWindow(mActivity);
            mSelectLocationPopupWindow.setOnClickListener(new SelectLocationPopupWindow.IOnClickListener() {
                @Override
                public void onCurrentLocationClickListener(View view, SelectLocationPopupWindow popupWindow) {
                    String privateCounty = mAiSouAppInfoModel.getAiSouLocationBean().getPrivateCounty();
                    if (!TextUtils.equals(popupWindow.getCounty(), privateCounty)) {
                        mTvLocationNavigation.setText(privateCounty);
                        switchCurrentAddress(privateCounty);
                    }
                }

                @Override
                public void onConfirmClickListener(View view, SelectLocationPopupWindow popupWindow) {
                    confirmSelectLocation(popupWindow);
                }
            });
        }
        mSelectLocationPopupWindow.show(view);
    }

    protected abstract void onDistrictSearched(boolean isSuccessful, @Nullable DistrictResult districtResult, @Nullable ArrayList<DistrictItem> district);

    @Override
    public void onDistrictSearched(DistrictResult districtResult) {
        if (districtResult != null && districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
            ArrayList<DistrictItem> district = districtResult.getDistrict();
            if (district != null && !district.isEmpty()) {
                onDistrictSearched(true, districtResult, district);
                if (iOnSwitchListener != null) {
                    iOnSwitchListener.onSwitchListener(true, district);
                }
            } else {
                onDistrictSearched(false, districtResult, district);
                if (iOnSwitchListener != null) {
                    iOnSwitchListener.onSwitchListener(false, null);
                }
            }
        } else {
            onDistrictSearched(false, null, null);
            if (iOnSwitchListener != null) {
                iOnSwitchListener.onSwitchListener(false, null);
            }
        }
    }

    /**
     * 提交切换地址
     */
    private void confirmSelectLocation(SelectLocationPopupWindow popupWindow) {
        String county = popupWindow.getCounty();
        if (!TextUtils.isEmpty(county)) {
            mTvLocationNavigation.setText(county);
            mUserSelectLocation = popupWindow.getProvince() + popupWindow.getCity() + county;
            String completionAddress = mAiSouAppInfoModel.getAiSouLocationBean().getCompletionAddress();

            //是当前定位的地址就没有必要请求了
            if (completionAddress != null && completionAddress.contains(mUserSelectLocation)) {
                switchCurrentAddress(county);
                popupWindow.dismiss();
                return;
            }

            if (mDistrictSearch == null) {
                mDistrictSearch = new DistrictSearch(AiSouAppInfoModel.getAppContext());
            }
            if (mSearchQuery == null) {
                mSearchQuery = new DistrictSearchQuery();
            }

            mSearchQuery.setKeywords(county);//传入关键字
            mSearchQuery.setShowBoundary(true);//是否返回边界值
            mDistrictSearch.setQuery(mSearchQuery);
            mDistrictSearch.setOnDistrictSearchListener(this);//绑定监听器
            mDistrictSearch.searchDistrictAsyn();//开始搜索
            ToastUtil.showShort(R.string.hint_change_location);
            popupWindow.dismiss();
        }
    }

    /**
     * 切换到当前位置
     */
    private void switchCurrentAddress(String county) {
        mAiSouAppInfoModel.getAiSouLocationBean().setCounty(county);
        //切换状态， 设为 自动定位
        mAiSouAppInfoModel.getAiSouLocationBean().setUserSelectLocation(false);
        //启动多次定位
        GDLocationHelper.getInstance().getRepeatedly(null);

        if (iOnSwitchListener != null) {
            iOnSwitchListener.onSwitchListener(true, null);
        }
    }

    /**
     * 初始化地址监听
     */
    private void initGDListener() {
        if (null == iOnLocationListener) {
            iOnLocationListener = new GDLocationHelper.IOnLocationListener() {
                @Override
                public void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation) {
                    if (isSuccessful && null != mAiSouAppInfoModel && !mAiSouAppInfoModel.getAiSouLocationBean().isUserSelectLocation()) {
                        mTvLocationNavigation.setText(aMapLocation.getDistrict());
                    }
                }
            };
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        if (null != mSelectLocationPopupWindow) {
            mSelectLocationPopupWindow.onSaveInstanceState(bundle);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        if (null != mSelectLocationPopupWindow) {
            mSelectLocationPopupWindow.onRestoreInstanceState(bundle);
        }
    }
}
