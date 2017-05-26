package net.twoant.master.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.amap.api.services.nearby.UploadInfoCallback;

import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.ToastUtil;

import java.util.List;
import java.util.Stack;

/**
 * Created by S_Y_H on 2016/12/3.
 * 高德API 帮助类
 * 为了防止内存泄漏， 需要在activity 中的 onDestroy方法中调用方法 {@link #onDestroy()}
 */
//------------------------------------------搜索附近-----------------定位----------
public class GDLocationHelper implements NearbySearch.NearbyListener, AMapLocationListener {

    /**
     * 请求超时时间
     */
    private final static long HTTP_TIME_OUT = 60000;

    /**
     * 附近的 搜索半径
     */
    private final static int SEARCH_RADIUS = 10000;

    /**
     * 附近的 搜索时间范围
     */
    private final static int SEARCH_TIME_RANGE = 10000;

    /**
     * 多次上传附近信息的间隔时间
     */
    private final static int UPLOAD_NEARBY_INFO_INTERVAL = 10000;
    private final static String TAG = "GaoDeHelper";
    @SuppressLint("StaticFieldLeak")
    //实例
    private static GDLocationHelper sGDLocationHelper;
    //Application 实例
    private Context mContext;
    /**
     * 定位次数
     */
    private static int sLocationCount;

    /**
     * 附近派单功能
     */
    private NearbySearch mNearbySearch;

    /**
     * 声明AMapLocationClientOption对象
     */
    private AMapLocationClientOption mLocationOption = null;

    private AiSouAppInfoModel mAiSouApplication;
    private AiSouLocationBean mAiSouLocationBean;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private NearbySearch.NearbyQuery mNearbyQuery;
    private LatLonPoint mLatLonPoint;

    private IOnNearbyDataListener iOnNearbyDataListener;
    private Stack<IOnLocationListener> iOnLocationListenerStack = new Stack<>();
    /**
     * 是否获取附近的数据
     */
    private boolean mIsGetNearbyData;
    /**
     * 多次定位 时间间隔
     * 单位毫秒,当前为5分钟。
     */
    private long mRepeatedlyRequestInterval = 5 * 60 * 1000;
    private UploadInfo mUploadInfo;

    {
        mContext = AiSouAppInfoModel.getAppContext();
        mAiSouApplication = AiSouAppInfoModel.getInstance();
        mAiSouLocationBean = mAiSouApplication.getAiSouLocationBean();
    }

    private GDLocationHelper() {
        initBaseData();
    }

    public static GDLocationHelper getInstance() {
        if (sGDLocationHelper == null) {
            synchronized (GDLocationHelper.class) {
                if (sGDLocationHelper == null) {
                    sGDLocationHelper = new GDLocationHelper();
                }
            }
        }
        return sGDLocationHelper;
    }

    public static int getLocationCount() {
        return sLocationCount;
    }

    /**
     * 获取单次定位, 默认使用高精度模式
     * 为了防止内存泄漏， 需要在activity 中的 onDestroy方法中调用方法 {@link #onDestroy()}
     */
    public GDLocationHelper getOnceLocation(@Nullable IOnLocationListener iOnLocationListener) {
        if (null != iOnLocationListener) {
            if (!iOnLocationListenerStack.contains(iOnLocationListener)) {
                this.iOnLocationListenerStack.push(iOnLocationListener);
            }
        }
        initLocationOption(true, true);
        return this;
    }

    /**
     * 获取多次定位,默认使用低功耗模式
     * 为了防止内存泄漏， 需要在activity 中的 onDestroy方法中调用方法 {@link #onDestroy()}
     */
    public GDLocationHelper getRepeatedly(@Nullable IOnLocationListener iOnLocationListener) {
        if (null != iOnLocationListener) {
            if (!iOnLocationListenerStack.contains(iOnLocationListener)) {
                this.iOnLocationListenerStack.push(iOnLocationListener);
            }
        }
        initLocationOption(false, true);
        return this;
    }

    /**
     * 为了防止内存泄漏， 需要在activity 中的 onDestroy方法中调用方法
     */
    public GDLocationHelper onDestroy() {
        sLocationCount = 0;
        if (null != iOnLocationListenerStack) {
            this.iOnLocationListenerStack.clear();
        }
        this.iOnNearbyDataListener = null;
        return this;
    }

    public void removeLocationListener(IOnLocationListener iOnLocationListener) {
        if (null != iOnLocationListenerStack) {
            iOnLocationListenerStack.remove(iOnLocationListener);
        }
    }

    /**
     * 定位回调监听
     */
    public interface IOnLocationListener {
        /**
         * @param isSuccessful true 位置获取成功
         * @param description  状态描述
         * @param latitude     纬度
         * @param longitude    经度
         * @param aMapLocation 信息对象
         */
        void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation);

    }


    /**
     * 设置定位时间间隔
     *
     * @param interval 间隔
     */
    public GDLocationHelper setRepeatedlyRequestInterval(long interval) {
        this.mRepeatedlyRequestInterval = interval;
        return this;
    }

    /**
     * 关闭定位服务
     */
    public GDLocationHelper destroyLocationServer(boolean isStop) {
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        return this;
    }

    /**
     * 停止定位服务
     */
    public GDLocationHelper stopLocation() {
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        return this;

    }

    /**
     * 释放附近派单功能资源
     * 在停止使用附近派单功能时，需释放资源。
     */
    public GDLocationHelper destroy() {
        //调用销毁功能，在应用的合适生命周期需要销毁附近功能
        NearbySearch.destroy();
        return this;
    }


    /**
     * 连续上传的数据包括用户位置信息和用户业务逻辑id信息。异步上传，
     * 实现NearbyListener接口的onNearbyInfoUploaded方法接收上传的回调结果。
     */
    public GDLocationHelper updateRepeatedlyUserNearbyLocation(int interval) {
        mNearbySearch.startUploadNearbyInfoAuto(new UploadInfoCallback() {
            //设置自动上传数据和上传的间隔时间
            @Override
            public UploadInfo OnUploadInfoCallback() {
                if (mUploadInfo == null)
                    mUploadInfo = new UploadInfo();

                mUploadInfo.setCoordType(NearbySearch.AMAP);
                //位置信息
                mUploadInfo.setPoint(mLatLonPoint);
                //用户id信息
                mUploadInfo.setUserID(mAiSouApplication.getAiSouUserBean().getAiSouID());
                return mUploadInfo;
            }
        }, interval <= 1000 ? UPLOAD_NEARBY_INFO_INTERVAL : interval);
        return this;
    }


    /**
     * 单次上传的数据包括坐标系类型、用户位置信息和用户业务逻辑id信息，
     * 接口为异步上传，实现 NearbyListener 接口的 onNearbyInfoUploaded 方法接收上传的回调结果。
     */
    public GDLocationHelper updateOnceUserNearbyLocation() {
        //构造上传位置信息
        if (mUploadInfo == null)
            mUploadInfo = new UploadInfo();

        //设置上传位置的坐标系支持AMap坐标数据与GPS数据
        mUploadInfo.setCoordType(NearbySearch.AMAP);
        //设置上传数据位置,位置的获取推荐使用高德定位sdk进行获取
        mUploadInfo.setPoint(mLatLonPoint);
        //设置上传用户id
        mUploadInfo.setUserID(mAiSouApplication.getAiSouUserBean().getAiSouID());
        //调用异步上传接口
        mNearbySearch.uploadNearbyInfoAsyn(mUploadInfo);
        return this;
    }

    /**
     * 附近的 信息 监听接口
     */
    public interface IOnNearbyDataListener {
        void onNearbyDataListener(List<NearbyInfo> nearbyInfoList);
    }

    /**
     * 如果需要多次获取附近的信息， 不推荐 使用匿名内部类， 因为 IOnNearbyDataListener 只能被回调一次。
     *
     * @param iOnNearbyDataListener 监听
     */
    public GDLocationHelper getNearbyData(boolean useCache, @NonNull IOnNearbyDataListener iOnNearbyDataListener) {

        this.iOnNearbyDataListener = iOnNearbyDataListener;
        if (useCache) {
            double latitude = mAiSouApplication.getAiSouLocationBean().getLatitude();
            double longitude = mAiSouApplication.getAiSouLocationBean().getLongitude();
            if (latitude == 0 || longitude == 0) {
                mIsGetNearbyData = true;
                getOnceLocation(null);
            } else {
                getNearbyData(latitude, longitude);
            }
        } else {
            mIsGetNearbyData = true;
            getOnceLocation(null);
        }
        return this;
    }

    @Override
    public void onUserInfoCleared(int i) {
        LogUtils.e(TAG, "onUserInfoCleared = " + i);
    }

    /**
     * 通过回调接口 onNearbyInfoSearched 解析返回的结果。
     * 周边检索的回调函数
     */
    @Override
    public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int resultCode) {
        //搜索周边附近用户回调处理
        if (1000 == resultCode) {
            if (nearbySearchResult != null
                    && nearbySearchResult.getNearbyInfoList() != null
                    && nearbySearchResult.getNearbyInfoList().size() > 0) {

                NearbyInfo nearbyInfo = nearbySearchResult.getNearbyInfoList().get(0);
                if (iOnNearbyDataListener != null) {
                    iOnNearbyDataListener.onNearbyDataListener(nearbySearchResult.getNearbyInfoList());
                    iOnNearbyDataListener = null;
                }
                LogUtils.e(TAG, "周边搜索结果为size " + nearbySearchResult.getNearbyInfoList().size() +
                        "first" + nearbyInfo.getUserID() + "  ,getDistance" + nearbyInfo.getDistance() + " ,getDrivingDistance "
                        + nearbyInfo.getDrivingDistance() + ", getTimeStamp " + nearbyInfo.getTimeStamp() + "  ,getPoint" +
                        nearbyInfo.getPoint().toString());

            } else {
                ToastUtil.showShort("搜索附近结果为空");
            }
        } else {

            if (2101 == resultCode)
                LogUtils.e(TAG, "App Key未开通“附近”功能");

            if (2203 == resultCode)
                LogUtils.e(TAG, "两次单次上传的间隔低于7秒");

            LogUtils.e(TAG, "周边搜索出现异常，异常码为：" + resultCode);

            ToastUtil.showShort("搜索附近失败");
        }
    }

    @Override
    public void onNearbyInfoUploaded(int i) {
        LogUtils.e(TAG, "onNearbyInfoUploaded = " + i);
    }

    /**
     * 用户信息清除后，将不会再被检索到，比如接单的美甲师下班后可以清除其位置信息。
     * 清除用户信息
     */
    public GDLocationHelper clearUserData() {
        //获取附近实例，并设置要清楚用户的id
        mNearbySearch.setUserID(mAiSouApplication.getAiSouUserBean().getAiSouID());
        //调用异步清除用户接口
        mNearbySearch.clearUserInfoAsyn();
        return this;
    }

    /**
     * 定位回调接口
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        ++sLocationCount;
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();

                mAiSouLocationBean.setLatitudeAndLongitude(latitude, longitude
                        , aMapLocation.getDistrict() + aMapLocation.getStreet() + aMapLocation.getStreetNum()
                        , aMapLocation.getAddress(), aMapLocation.getAdCode(), aMapLocation.getCityCode());

                mAiSouLocationBean.setProvince(aMapLocation.getProvince());
                mAiSouLocationBean.setCity(aMapLocation.getCity());
                mAiSouLocationBean.setPrivateCounty(aMapLocation.getDistrict());
                mAiSouLocationBean.setGetLocationSuccessful(true);
                if (mIsGetNearbyData) {
                    getNearbyData(latitude, longitude);
                    mIsGetNearbyData = false;
                }
                initLocationListener(aMapLocation, true, "定位成功", latitude, longitude);

                /*
                //获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLocationType();
                //获取纬度
                aMapLocation.getLatitude();
                //获取经度
                aMapLocation.getLongitude();
                //获取精度信息
                aMapLocation.getAccuracy();

                //地址，如果option中设置isNeedAddress为false，则没有此结果，
                // 网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getAddress();
                //省信息
                aMapLocation.getProvince();
                //城市信息
                aMapLocation.getCity();
                //城区信息
                aMapLocation.getDistrict();
                //街道信息
                aMapLocation.getStreet();
                //街道门牌号信息
                aMapLocation.getStreetNum();
                //城市编码
                aMapLocation.getCityCode();
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息
                aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
                //获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);
                */
            } else {
                if (aMapLocation.getErrorCode() == 12) {
                    ToastUtil.showShort(net.twoant.master.R.string.location_permission_fail);
                    initLocationListener(aMapLocation, false, "未授予位置权限", 0, 0);
                } else {
                    ToastUtil.showShort(net.twoant.master.R.string.location_get_fail);
                    initLocationListener(aMapLocation, false, "位置获取失败", 0, 0);
                }
                mAiSouLocationBean.resetLocationData();
                mAiSouLocationBean.setGetLocationSuccessful(false);
            }
        } else {
            ToastUtil.showShort(net.twoant.master.R.string.location_get_fail);
            initLocationListener(null, false, "配置错误", 0, 0);
            mAiSouLocationBean.resetLocationData();
            mAiSouLocationBean.setGetLocationSuccessful(false);
        }
    }

    private void initLocationListener(AMapLocation aMapLocation, boolean isSuccessful, String hint, double latitude, double longitude) {
        for (; ; ) {
            if (!iOnLocationListenerStack.empty()) {
                iOnLocationListenerStack.pop().onLocationListener(isSuccessful, hint, latitude, longitude, aMapLocation);
            } else {
                break;
            }
        }
    }


    /**
     * @param isOnce   true 只获取一次定位， false 获取多次定位
     * @param isSaving true 低功耗模式, false 高精度
     */
    private void initLocationOption(boolean isOnce, boolean isSaving) {
        if (mLocationOption == null) {
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
        }
        if (isSaving)
            //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        else
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);

        if (isOnce) {
            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);
            /*  获取最近3s内精度最高的一次定位结果：
                设置setOnceLocationLatest(boolean b)接口为true，
                启动定位时SDK会返回最近3s内精度最高的一次定位结果。
                如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
             */
            mLocationOption.setOnceLocationLatest(true);
        } else {
            mLocationOption.setOnceLocation(false);
            mLocationOption.setOnceLocationLatest(false);
            mLocationOption.setInterval(mRepeatedlyRequestInterval);
        }
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(HTTP_TIME_OUT);

        //启用缓存机制
        mLocationOption.setLocationCacheEnable(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    private void getNearbyData(double latitude, double longitude) {

        if (mNearbyQuery == null) {
            //设置搜索条件
            mNearbyQuery = new NearbySearch.NearbyQuery();
            //设置附近监听
            mNearbySearch.addNearbyListener(this);
        }

        if (mLatLonPoint == null)
            mLatLonPoint = new LatLonPoint(latitude, longitude);
        else {
            mLatLonPoint.setLatitude(latitude);
            mLatLonPoint.setLongitude(longitude);
        }

        //设置搜索的中心点
        mNearbyQuery.setCenterPoint(mLatLonPoint);

        //设置搜索的坐标体系
        mNearbyQuery.setCoordType(NearbySearch.AMAP);

        //设置搜索半径
        mNearbyQuery.setRadius(SEARCH_RADIUS);

        //设置查询的时间
        mNearbyQuery.setTimeRange(SEARCH_TIME_RANGE);

        //设置查询的方式驾车还是距离
        mNearbyQuery.setType(NearbySearchFunctionType.DISTANCE_SEARCH);
        //调用异步查询接口
        mNearbySearch.searchNearbyInfoAsyn(mNearbyQuery);
    }

    private void initBaseData() {
        mNearbySearch = NearbySearch.getInstance(mContext);
        //初始化定位
        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
    }


}
