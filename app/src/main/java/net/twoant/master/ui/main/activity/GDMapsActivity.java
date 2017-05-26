package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.common_utils.ToastUtil;


public class GDMapsActivity extends BaseActivity {

    private static final String START_ACTION = "gd_start";
    private static final String EXTRA_LATITUDE = "gd_latitude";
    private static final String EXTRA_LONGITUDE = "gd_longitude";
    private static final String EXTRA_SHOP_NAME = "gd_shop";

    private double mLatitude;
    private double mLongitude;
    private String mShopName;

    private int mToolbarHeight;

    private MapView mMvMapView;
    private Toolbar mToolbar;
    private View mLocationView;
    private int mScaledTouchSlop;

    public static void startActivity(Activity context, String latitude, String longitude, String shopName) {
        Intent intent = new Intent(context, GDMapsActivity.class);
        intent.setAction(START_ACTION);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_SHOP_NAME, shopName);
        context.startActivity(intent);
        context.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
    }

    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, START_ACTION);
        try {
            mLatitude = Double.valueOf(intent.getStringExtra(EXTRA_LATITUDE));
            mLongitude = Double.valueOf(intent.getStringExtra(EXTRA_LONGITUDE));
        } catch (Exception e) {
            ToastUtil.showShort("获取商家位置失败");
            GDMapsActivity.this.finish();
        }
        mShopName = intent.getStringExtra(EXTRA_SHOP_NAME);
        return net.twoant.master.R.layout.yh_activity_gd_maps;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        mScaledTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mScaledTouchSlop = mScaledTouchSlop << 1;
        initView();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mToolbarHeight = mToolbar.getHeight();
            }
        });
        mMvMapView.onCreate(savedInstanceState);
        initMapData();
    }

    private void initMapData() {
        AMap map = mMvMapView.getMap();
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setIndoorSwitchEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 19, 0, 0)));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(mShopName).position(latLng).draggable(false).visible(true);
        map.addMarker(markerOptions);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMvMapView.onPause();
        overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMvMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMvMapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMvMapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMvMapView.onSaveInstanceState(outState);
        outState.putDouble("mLatitude", mLatitude);
        outState.putDouble("mLongitude", mLongitude);
        outState.putString("mShopName", mShopName);
        outState.putInt("mToolbarHeight", mToolbarHeight);
        outState.putInt("mScaledTouchSlop", mScaledTouchSlop);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLatitude = savedInstanceState.getDouble("mLatitude");
        mLongitude = savedInstanceState.getDouble("mLongitude");
        mShopName = savedInstanceState.getString("mShopName");
        mToolbarHeight = savedInstanceState.getInt("mToolbarHeight");
        mScaledTouchSlop = savedInstanceState.getInt("mScaledTouchSlop");
    }

    private void initView() {
        ((AppCompatTextView) findViewById(net.twoant.master.R.id.tv_title_tool_bar)).setText("地图详情");
        mToolbar = (Toolbar) findViewById(net.twoant.master.R.id.tb_simple_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GDMapsActivity.this.finish();
            }
        });
        this.mMvMapView = (MapView) findViewById(net.twoant.master.R.id.mv_map_view);

        mMvMapView.getMap().setOnMapTouchListener(new AMap.OnMapTouchListener() {

            private boolean isShow = true;
            private float mLastX;
            private float mLastY;

            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = motionEvent.getY();
                        mLastX = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isShow && ((Math.abs(motionEvent.getY()) - mLastY) >= mScaledTouchSlop || (Math.abs(motionEvent.getX()) - mLastX) >= mScaledTouchSlop)) {
                            isShow = false;
                            showOrHindToolbar(false);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (!isShow) {
                            isShow = true;
                            showOrHindToolbar(true);
                        }
                        break;
                }
            }
        });
        mLocationView = findViewById(net.twoant.master.R.id.fab_back_shop_location);
        mLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMvMapView.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition(new LatLng(mLatitude, mLongitude), 19, 0, 0)));
            }
        });
    }

    private void showOrHindToolbar(boolean isShow) {
        if (isShow) {
            mToolbar.animate().translationY(0).start();
            mLocationView.animate().translationY(0).start();
        } else {
            mToolbar.animate().translationY(-mToolbarHeight).start();
            mLocationView.animate().translationY(mToolbarHeight << 1).start();
        }
    }

}
