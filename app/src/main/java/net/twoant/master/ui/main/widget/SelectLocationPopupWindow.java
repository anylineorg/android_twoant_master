package net.twoant.master.ui.main.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;

import com.aigestudio.wheelpicker.WheelPicker;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.main.bean.ProvinceDataBean;
import net.twoant.master.ui.main.interfaces.ISaveRestoreInstance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by S_Y_H on 2017/3/15.
 * 地址选择 弹窗
 */

public class SelectLocationPopupWindow implements ISaveRestoreInstance<Bundle>, WheelPicker.OnItemSelectedListener, Handler.Callback, View.OnClickListener {

    /**
     * 地址选择 弹窗
     */
    private PopupWindow mSelectLocationPopupWindow;

    /**
     * 地址
     */
    private AppCompatTextView mPopupLocationText;
    private AiSouAppInfoModel mAiSouAppInfoModel;
    private View mContentView;
    private Activity mActivity;
    private LinearLayoutCompat mWheelParent;
    private WheelPicker mRightWheelPicker;
    private WheelPicker mCenterWheelPicker;
    private WheelPicker mLeftWheelPicker;
    private Handler mHandler;
    private String mProvince;
    private String mCity;
    private String mCounty;
    private SelectLocationPopupWindow.IOnClickListener iOnClickListener;

    /**
     * 获取省市区数据成功
     */
    private final static int CODE_GET_PROVINCE_SUCCESSFUL = 1;
    /**
     * 获取当前位置
     */
    private final static int CODE_GET_CURRENT_LOCATION = 2;
    /**
     * 省市县数据
     */
    private ProvinceDataBean mProvinceDataBean;
    /**
     * 开始获取省市区数据
     */
    private boolean isStartGetProvince;

    public SelectLocationPopupWindow(Activity activity) {
        this.mActivity = activity;
        mHandler = new Handler(this);
        this.mAiSouAppInfoModel = AiSouAppInfoModel.getInstance();
        getProvinceData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_address_header_popup_window:
                if (null != iOnClickListener) {
                    iOnClickListener.onCurrentLocationClickListener(v, this);
                }
                selectCurrentLocation();
                break;
            case R.id.btn_confirm_popup_window:
                if (null != iOnClickListener) {
                    iOnClickListener.onConfirmClickListener(v, this);
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("mPDB", mProvinceDataBean);
        outState.putBoolean("iSGP", isStartGetProvince);
        outState.putString("mP", mProvince);
        outState.putString("mC", mCity);
        outState.putString("mCo", mCounty);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mProvinceDataBean = (ProvinceDataBean) savedInstanceState.getSerializable("mPDB");
        isStartGetProvince = savedInstanceState.getBoolean("iSGP");
        mProvince = savedInstanceState.getString("mP");
        mCity = savedInstanceState.getString("mC");
        mCounty = savedInstanceState.getString("mCo");
    }

    public interface IOnClickListener {
        /**
         * 点击当前位置VIew
         */
        void onCurrentLocationClickListener(View view, SelectLocationPopupWindow popupWindow);

        /**
         * 点击确认按钮
         */
        void onConfirmClickListener(View view, SelectLocationPopupWindow popupWindow);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case CODE_GET_PROVINCE_SUCCESSFUL:
                initProvinceData();
                break;
            case CODE_GET_CURRENT_LOCATION:
                String temp = null;
                if (null != mAiSouAppInfoModel)
                    temp = mAiSouAppInfoModel.getAiSouLocationBean().getCompletionAddress();
                if (!TextUtils.isEmpty(temp)) {
                    mPopupLocationText.setEnabled(true);
                    mPopupLocationText.setText(temp);
                } else {
                    mPopupLocationText.setEnabled(false);
                    mPopupLocationText.setText("获取位置失败");
                }
                break;
        }
        return true;
    }

    /**
     * @return 县、区
     */
    public String getCounty() {
        return this.mCounty;
    }

    public String getProvince() {
        return this.mProvince;
    }

    public String getCity() {
        return this.mCity;
    }

    /**
     * 初始化选择地址的popupWindow
     */
    @SuppressLint("InflateParams")
    private void initLocationPopupWindow() {
        mContentView = mActivity.getLayoutInflater().inflate(
                R.layout.yh_popup_window_select_location, null);
        mSelectLocationPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectLocationPopupWindow.setAnimationStyle(R.style.DialogStyle_Y);
        // 设置点击周围取消
        mSelectLocationPopupWindow.setOutsideTouchable(true);
        // 设置获取焦点
        mSelectLocationPopupWindow.setFocusable(true);
        // 设置poPupwindow可以点后退键取消poPupwindow(5.0 以下)
        mSelectLocationPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.yh_shape_popup_window_bg));

        mSelectLocationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1F);
            }
        });
    }

    /**
     * 显示地址选择popupWindow
     *
     * @param view 显示位置
     */
    public void show(View view) {
        if (mSelectLocationPopupWindow == null) {
            initLocationPopupWindow();
            initLocationView();
        }
        if (mSelectLocationPopupWindow.isShowing()) {
            return;
        }
        setBackgroundAlpha(0.7F);
        mSelectLocationPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        showCurrentLocation();
    }

    private void initLocationView() {
        mPopupLocationText = (AppCompatTextView) mContentView.findViewById(R.id.tv_address_header_popup_window);
        mPopupLocationText.setOnClickListener(this);
        mPopupLocationText.setEnabled(false);
        mWheelParent = (LinearLayoutCompat) mContentView.findViewById(R.id.ll_wheel_parent);
        AppCompatButton confirm = (AppCompatButton) mContentView.findViewById(R.id.btn_confirm_popup_window);
        confirm.setOnClickListener(this);
    }


    public void setOnClickListener(SelectLocationPopupWindow.IOnClickListener onClickListener) {
        this.iOnClickListener = onClickListener;
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        switch (picker.getId()) {
            case R.id.main_wheel_left:
                if (mProvinceDataBean != null && data instanceof String) {

                    List<String> cities = mProvinceDataBean.getCitys().get(mProvince = (String) data);
                    mCenterWheelPicker.setData(cities);
                    if (cities.size() > 0) {
                        List<String> rightCounty = mProvinceDataBean.getCounty().get(mCity = cities.get(0));
                        mCenterWheelPicker.setSelectedItemPosition(0);
                        if (rightCounty.size() > 0) {

                            mCounty = rightCounty.get(0);
                            mRightWheelPicker.setData(rightCounty);
                            mRightWheelPicker.setSelectedItemPosition(0);
                        }
                    }

                }
                break;

            case R.id.main_wheel_center:
                if (mProvinceDataBean != null && data instanceof String) {
                    List<String> county = mProvinceDataBean.getCounty().get(mCity = (String) data);
                    if (county.size() > 0)
                        mCounty = county.get(0);
                    mRightWheelPicker.setData(county);
                }
                break;

            case R.id.main_wheel_right:
                if (data instanceof String) {
                    mCounty = (String) data;
                }
                break;
        }
    }

    /**
     * 选择当前定位位置
     */
    private void selectCurrentLocation() {
        mProvince = mAiSouAppInfoModel.getAiSouLocationBean().getProvince();
        mCity = mAiSouAppInfoModel.getAiSouLocationBean().getCity();
        mCounty = mAiSouAppInfoModel.getAiSouLocationBean().getPrivateCounty();
        HashMap<String, List<String>> county;
        List<String> strings;
        if (mProvince != null && mCity != null && mCounty != null) {
            mLeftWheelPicker.setSelectedItemPosition(mProvinceDataBean.getDistrict().indexOf(mProvince));

            county = mProvinceDataBean.getCitys();
            mCenterWheelPicker.setData((strings = county.get(mProvince)));
            mCenterWheelPicker.setSelectedItemPosition(strings.indexOf(mCity));

            county = mProvinceDataBean.getCounty();
            mRightWheelPicker.setData((strings = county.get(mCity)));
            mRightWheelPicker.setSelectedItemPosition(strings.indexOf(mCounty));
        }

        mAiSouAppInfoModel.getAiSouLocationBean().setCounty(mCounty);
        //切换状态， 设为 自动定位
        mAiSouAppInfoModel.getAiSouLocationBean().setUserSelectLocation(false);
        //启动多次定位
        GDLocationHelper.getInstance().getRepeatedly(null);

        dismiss();
    }

    /**
     * 显示当前位置地址
     */
    private void showCurrentLocation() {
        if (mAiSouAppInfoModel.getAiSouLocationBean().getLocationSuccessfulState()) {
            mPopupLocationText.setText(mAiSouAppInfoModel.getAiSouLocationBean().getCompletionAddress());
            mPopupLocationText.setEnabled(true);
        } else {
            mPopupLocationText.setText(R.string.hint_get_current_location);
            GDLocationHelper.getInstance().getOnceLocation(null);
            mHandler.sendEmptyMessageDelayed(CODE_GET_CURRENT_LOCATION, 3500);
        }
    }


    /**
     * 关闭 popupWindow
     */
    public void dismiss() {
        if (null != mSelectLocationPopupWindow && mSelectLocationPopupWindow.isShowing()) {
            mSelectLocationPopupWindow.dismiss();
            setBackgroundAlpha(1F);
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 设置背景变暗
     */
    private void setBackgroundAlpha(float alpha) {
        Window window = this.mActivity.getWindow();
        if (window != null) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                decorView.setAlpha(alpha);
            }
        }
    }


    /**
     * 初始化 地址选择的 滚动view
     */
    private void initWheelPicker() {
        mLeftWheelPicker = (WheelPicker) mContentView.findViewById(R.id.main_wheel_left);
        mLeftWheelPicker.setOnItemSelectedListener(this);
        mCenterWheelPicker = (WheelPicker) mContentView.findViewById(R.id.main_wheel_center);
        mCenterWheelPicker.setOnItemSelectedListener(this);
        mRightWheelPicker = (WheelPicker) mContentView.findViewById(R.id.main_wheel_right);
        mRightWheelPicker.setOnItemSelectedListener(this);
    }

    /**
     * 获取省市区信息
     */
    private void getProvinceData() {
        if (mProvinceDataBean == null && !isStartGetProvince) {
            isStartGetProvince = true;
            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    ObjectInputStream objectInputStream = null;
                    try {
                        objectInputStream = new ObjectInputStream(
                                mActivity.getResources().openRawResource(R.raw.province_data));
                        ProvinceDataBean province = (ProvinceDataBean) objectInputStream.readObject();
                        if (null != province) {
                            SelectLocationPopupWindow.this.mProvinceDataBean = province;
                            if (null != mHandler) {
                                mHandler.sendEmptyMessage(CODE_GET_PROVINCE_SUCCESSFUL);
                            }
                        }
                    } catch (IOException e) {
                        isStartGetProvince = false;
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        CloseUtils.closeIO(objectInputStream);
                    }
                }
            });
        }
    }

    /**
     * 初始化省市区数据
     */
    private void initProvinceData() {
        initWheelPicker();
        mLeftWheelPicker.setData(mProvinceDataBean.getDistrict());
        HashMap<String, List<String>> county = mProvinceDataBean.getCounty();
        mProvince = mAiSouAppInfoModel.getAiSouLocationBean().getProvince();
        mCity = mAiSouAppInfoModel.getAiSouLocationBean().getCity();
        mCounty = mAiSouAppInfoModel.getAiSouLocationBean().getCounty();
        if (mProvince != null && mCity != null && mCounty != null) {
            mLeftWheelPicker.setSelectedItemPosition(mProvinceDataBean.getDistrict().indexOf(mProvince));
            mCenterWheelPicker.setData(mProvinceDataBean.getCitys().get(mProvince));
            mCenterWheelPicker.setSelectedItemPosition(mProvinceDataBean.getCitys().get(mProvince).indexOf(mCity));
            mRightWheelPicker.setData(county.get(mCity));
            mRightWheelPicker.setSelectedItemPosition(county.get(mCity).indexOf(mCounty));
        } else {
            mProvince = mProvinceDataBean.getDistrict().get(0);
            mCity = mProvinceDataBean.getCitys().get(mProvince).get(0);
            mCounty = county.get(mCity).get(0);
            mLeftWheelPicker.setSelectedItemPosition(0);
            mCenterWheelPicker.setData(mProvinceDataBean.getCitys().get(mProvince));
            mCenterWheelPicker.setSelectedItemPosition(0);
            mRightWheelPicker.setData(county.get(mCity));
            mRightWheelPicker.setSelectedItemPosition(0);
        }
        mWheelParent.setVisibility(View.VISIBLE);
    }
}
