package net.twoant.master.ui.my_center.activity.out;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.zhy.http.okhttp.callback.StringCallback;

import net.twoant.master.R;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.AiSouLocationBean;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.base_app.LongBaseActivity;
import net.twoant.master.common_utils.CloseUtils;
import net.twoant.master.common_utils.ConstUtils;
import net.twoant.master.common_utils.GsonUtil;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.bean.ProvinceDataBean;
import net.twoant.master.ui.other.bean.HttpResultBean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * Created by DZY on 2016/12/6.
 * 佛祖保佑   永无BUG
 */
public class AddAddressActivity extends LongBaseActivity implements View.OnClickListener,WheelPicker.OnItemSelectedListener{

    private EditText etDeliveryName,etTelphone,etAddress;
    private boolean isStartGetProvince;
    private WheelPicker mLeftWheelPicker;
    private String mProvince;
    private AiSouLocationBean mAiSouApplication;
    private String mCity;
    private String mCounty;
    private WheelPicker mCenterWheelPicker;
    private WheelPicker mRightWheelPicker;
    private LinearLayoutCompat mWheelParent;
    private View mContentView;
    /**
     * 地址是否获取成功
     */
    private boolean mLocationGetSuccessful = true;
    /**
     * 地址选择 弹窗
     */
    private PopupWindow mSelectLocationPopupWindow;
    /**
     * 省市县数据
     */
    private ProvinceDataBean mProvinceDataBean;
    /**
     * 地址
     */
    private AppCompatTextView mPopupLocationText;
    public RelativeLayout mTvLocationMainActivity;
    /**
     * 用户当前选择的位置
     */
    private String sUserSelectLocation;

    private TextView tvProvice,tvCity,tvArea;
    private HintDialogUtil hintDialogUtil;
    public Drawable drawable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_addaddress;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        etDeliveryName = (EditText) findViewById(R.id.et_name_addaddress);
        etTelphone = (EditText) findViewById(R.id.et_telphone_addaddress);
        etAddress = (EditText) findViewById(R.id.et_detailaddress_addaddress);
        etAddress.setCompoundDrawables(null,null,null,null);
        findViewById(R.id.iv_getaddress_addaddress).setOnClickListener(this);
        mTvLocationMainActivity = (RelativeLayout) findViewById(R.id.rl_select_city_addaddress);
        tvProvice = (TextView) findViewById(R.id.tv_province_addaddress);
        tvCity = (TextView) findViewById(R.id.tv_city_addaddress);
        tvArea = (TextView) findViewById(R.id.tv_area_addaddress);
        mTvLocationMainActivity.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);
        mAiSouApplication = AiSouAppInfoModel.getInstance().getAiSouLocationBean();
        String provice = mAiSouApplication.getProvince();
        String city = mAiSouApplication.getCity();
        String area = mAiSouApplication.getCounty();
        tvProvice.setText(provice);
        tvCity.setText(city);
        tvArea.setText(area);
        sUserSelectLocation = provice + city + area;
        etAddress.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
                Drawable drawable = etAddress.getCompoundDrawables()[2];
                //如果右边没有图片，不再处理
                if (drawable == null)
                    return false;
                //如果不是按下事件，不再处理
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (event.getX() > etAddress.getWidth()
                        - etAddress.getPaddingRight()
                        - drawable.getIntrinsicWidth()){
                    etAddress.setText("");
                }
                return false;
            }
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etAddress.getText().length() > 0) {
                    etAddress.setCompoundDrawables(null,null,drawable,null);
                }else {
                    etAddress.setCompoundDrawables(null,null,null,null);
                };
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hintDialogUtil = new HintDialogUtil(this);
        drawable = getResources().getDrawable(R.drawable.icon_x);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rl_select_city_addaddress:
                getProvince();
                showLocationPopupWindows(v);
                if (mLocationGetSuccessful) {
                    mPopupLocationText.setText(mAiSouApplication.getCompletionAddress());
                    mPopupLocationText.setEnabled(true);
                } else {
                    GDLocationHelper.getInstance().getOnceLocation(null);
                    String temp = null;
                    if (mAiSouApplication != null)
                        temp = mAiSouApplication.getCompletionAddress();
                    if (temp != null) {
                        mPopupLocationText.setEnabled(true);
                        mPopupLocationText.setText(temp);
                    } else {
                        mPopupLocationText.setEnabled(false);
                        mPopupLocationText.setText("获取位置失败");
                    }
                }
                break;
            case R.id.iv_getaddress_addaddress:
                String completionAddress = mAiSouApplication.getCompletionAddress();
                String provice = mAiSouApplication.getProvince();
                String city = mAiSouApplication.getCity();
                String area = mAiSouApplication.getCounty();
                tvProvice.setText(provice);
                tvCity.setText(city);
                tvArea.setText(area);
                completionAddress = completionAddress.replace(provice,"");
                completionAddress = completionAddress.replace(city,"");
                completionAddress = completionAddress.replace(area,"");
                etAddress.setText(completionAddress);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_save:
                judgeEmpty();
                break;
            case R.id.btn_confirm_popup_window:
                if (mCounty != null) {
                    sUserSelectLocation = mProvince + mCity + mCounty;
                    tvProvice.setText(mProvince);
                    tvCity.setText(mCity);
                    tvArea.setText(mCounty);
                }
                closeOptionPopupWindow();
                break;
        }
    }

    private void judgeEmpty() {
        String deliveryName = etDeliveryName.getText().toString().trim();
        if (TextUtils.isEmpty(deliveryName)) {
            ToastUtil.showLong("名为空");
            return;
        }
        String tel = etTelphone.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            ToastUtil.showLong("电话为空");
            return;
        } else if (!isMobileNum(tel)) {
            ToastUtil.showLong("手机号码不正确");
            return;
        }
        if (TextUtils.isEmpty(sUserSelectLocation)) {
            ToastUtil.showLong("请填写地址");
            return;
        }
        String detail = etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(detail)) {
            ToastUtil.showLong("详细地址为空");
            return;
        }
        postNetData(deliveryName,tel,sUserSelectLocation+detail);
    }

    private void postNetData(String deliveryName,String tel,String address) {
        Map<String,String> map = new HashMap<>();
        map.put("aisou_id",AiSouAppInfoModel.getInstance().getUID());
        map.put("receipt_name",deliveryName);
        map.put("receipt_tel",tel);
        map.put("receipt_address",address);
        hintDialogUtil.showLoading();
        LongHttp("add_receipt.do", map, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
               ToastUtil.showLong("连接失败:"+e);
                hintDialogUtil.dismissDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                hintDialogUtil.dismissDialog();
                HttpResultBean httpResultBean = GsonUtil.gsonToBean(response, HttpResultBean.class);
                HttpResultBean.ResultBean result = httpResultBean.getResult();
                String msg = result.getMsg();
                if (msg.contains("成功")) {
                    ToastUtil.showLong(msg);
                    finish();
                }
            }
        });
    }

    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern
                .compile(ConstUtils.REGEX_MOBILE_EXACT);
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    /**
     * 获取省市区信息
     */
   private void getProvince() {
        if (mProvinceDataBean == null && !isStartGetProvince) {
            isStartGetProvince = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    ObjectInputStream objectInputStream = null;

                    try {
                        objectInputStream = new ObjectInputStream(
                                getResources().openRawResource(R.raw.province_data));
                        ProvinceDataBean province = (ProvinceDataBean) objectInputStream.readObject();
                        if (province != null ) {
                            AddAddressActivity.this.mProvinceDataBean = province;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initWheelPicker();
                                    mLeftWheelPicker.setData(mProvinceDataBean.getDistrict());
                                    HashMap<String, List<String>> county = mProvinceDataBean.getCounty();
                                    mProvince = mAiSouApplication.getProvince();
                                    mCity = mAiSouApplication.getCity();
                                    mCounty = mAiSouApplication.getCounty();
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
                            });
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
            }).start();
        }
    }

    /**
     * 显示地址选择popupWindow
     *
     * @param view 显示位置
     */
   private void showLocationPopupWindows(View view) {
        if (mSelectLocationPopupWindow == null) {
            initLocationPopupWindow();
            initLocationView();
        }
        if (mSelectLocationPopupWindow.isShowing()) {
            return;
        }
        setBackgroundAlpha(0.7F);
        mSelectLocationPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
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
     * 初始化选择地址的popupWindow
     */
    @SuppressLint("InflateParams")
    private void initLocationPopupWindow() {
        mContentView = getLayoutInflater().inflate(
                R.layout.yh_popup_window_select_location, null);
        mSelectLocationPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectLocationPopupWindow.setAnimationStyle(R.style.DialogStyle_Y);
        // 设置点击周围取消
        mSelectLocationPopupWindow.setOutsideTouchable(true);
        // 设置获取焦点
        mSelectLocationPopupWindow.setFocusable(true);
        // 设置poPupwindow可以点后退键取消poPupwindow(5.0 以下)
        mSelectLocationPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.yh_shape_popup_window_bg));

        mSelectLocationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1F);
            }
        });
    }

    private void initLocationView() {
        mPopupLocationText = (AppCompatTextView) mContentView.findViewById(R.id.tv_address_header_popup_window);
        mPopupLocationText.setOnClickListener(this);
        mPopupLocationText.setEnabled(false);
        mWheelParent = (LinearLayoutCompat) mContentView.findViewById(R.id.ll_wheel_parent);
        AppCompatButton confirm = (AppCompatButton) mContentView.findViewById(R.id.btn_confirm_popup_window);
        confirm.setOnClickListener(this);
//        setOnclickListener(this);
    }

    /**
     * 设置背景变暗
     */
    private void setBackgroundAlpha(float alpha) {
        Window window = this.getWindow();
        if (window != null) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                decorView.setAlpha(alpha);
            }
        }
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        switch (picker.getId()) {
            case R.id.main_wheel_left:
                if (mProvinceDataBean != null && data instanceof String) {
                    List<String> cities = mProvinceDataBean.getCitys().get(mProvince = (String) data);
                    mCenterWheelPicker.setData(cities);
                    List<String> rightCounty = mProvinceDataBean.getCounty().get(mCity = cities.get(0));
                    if (rightCounty.size() > 0)
                        mCounty = rightCounty.get(0);
                    mRightWheelPicker.setData(rightCounty);
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
     * 关闭 popupWindow
     */
    private void closeOptionPopupWindow() {
        if (mSelectLocationPopupWindow != null && mSelectLocationPopupWindow.isShowing()) {
            mSelectLocationPopupWindow.dismiss();
            setBackgroundAlpha(1F);
        }
    }
}
