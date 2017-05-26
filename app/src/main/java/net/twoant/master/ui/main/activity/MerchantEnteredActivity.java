package net.twoant.master.ui.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import net.twoant.master.R;
import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.base_app.BaseConfig;
import net.twoant.master.base_app.TakePhotoBaseActivity;
import net.twoant.master.common_utils.ConstUtils;
import net.twoant.master.common_utils.DisplayDimensionUtils;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.HintDialogUtil;
import net.twoant.master.common_utils.HttpConnectedUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.NetworkUtils;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.main.adapter.control.ControlUtils;
import net.twoant.master.ui.main.widget.MerchantEnteredWriteInfo;
import net.twoant.master.ui.my_center.activity.out.RealNameActivity;
import net.twoant.master.ui.other.activity.ImageScaleActivity;
import net.twoant.master.widget.ContentDialog;
import net.twoant.master.widget.ListViewDialog;
import net.twoant.master.widget.MerchantCommitDialog;
import net.twoant.master.widget.entry.DataRow;
import net.twoant.master.widget.takephoto.model.TImage;
import net.twoant.master.widget.takephoto.model.TResult;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by S_Y_H on 2016/11/23.
 * 商家入驻界面
 */

public class MerchantEnteredActivity extends TakePhotoBaseActivity implements AMap.OnMapTouchListener, GDLocationHelper.IOnLocationListener, HttpConnectedUtils.IOnStartNetworkCallBack, View.OnClickListener {

    /**
     * 界面类型 ：商家入驻
     */
    public final static int TYPE_MERCHANT_ENTRY = 0xAA;

    /**
     * 界面类型 ： 商家认证
     */
    public final static int TYPE_MERCHANT_CERTIFICATION = 0xBB;

    /**
     * 商家入驻
     */
    private final static int ID_ENTRY = 0xCC;

    /**
     * 商家认证
     */
    private final static int ID_CERTIFICATION = 0xDD;

    /**
     * 获取商家入驻的信息
     */
    private final static int ID_LOADING_INFO = 0xEE;

    /**
     * 获取商家入驻次数
     */
    private final static int ID_GET_COMMEND = 0xFF;

    /**
     * 上传图片
     */
    private final static int ID_UPDATE_IMAGE = 0x100;

    /**
     * 验证是否实名认证
     */
    private final static int ID_VERIFICATION_ACTUAL = 0x200;

    /**
     * 启动 Action
     */
    private final static String ACTION_START = "a_s";
    /**
     * 类型
     */
    private final static String EXTRA_TYPE = "e_t";
    /**
     * 商品id
     */
    private final static String EXTRA_SHOP_ID = "e_s_i";

    private final static String KEY_BUSINESS_PHOTO = "i_d_m_b_p";
    private final static String KEY_LEGAL_BANK_POSITIVE = "i_d_m_l_b_p";
    private final static String KEY_LEGAL_PHOTO_NEGATIVE = "i_d_m_l_p_n";
    private final static String KEY_LEGAL_PHOTO_POSITIVE = "i_d_m_l_p_p";
    private final static String KEY_LEGAL_PHOTO = "i_d_m_l_p";
    private final static String KEY_MERCHANT_COVER = "i_d_m_c";
    private final static String KEY_IMG_PREFIX = "img";

    /**
     * 常量 一张照片
     */
    private final static int ONE_PHOTO = 1;

    /**
     * 宽度 比例
     */
    private final static int SCALE_WIDTH_SIZE = 100;

    /**
     * 除了店铺照片以外的照片高度比
     */
    private final static int SCALE_OTHER_HEIGHT_SIZE = 84;

    //身份证高度
    private final static int SCALE_ID_CARD_HEIGHT = 100;

    //身份证宽度
    private final static int SCALE_ID_CARD_WIDTH = 160;

    /**
     * 店铺照片高度（比例）
     */
    private final static int SCALE_MERCHANT_PHOTO_HEIGHT_SIZE = 75;

    /**
     * 通过图库可以同时选择最多4张照片
     */
    private final static int MAX_SELECT_PHOTO_NUM = 4;

    private final static int TAG = MAX_SELECT_PHOTO_NUM << 24;

    /**
     * 商家id
     */
    private String mShopId;

    /**
     * 图片目录集合
     */
    private HashMap<String, String> mFileArrayMap;
    /**
     * 信息集合
     */
    private HashMap<String, String> mStringArrayMap;

    private final ArrayMap<String, MerchantEnteredInnerImageInfo> fImageViewDelete = new ArrayMap<>(12);

    private AppCompatEditText mEtCommendPerson;
    private AppCompatEditText mEtMerchantPhone;
    private AppCompatEditText mEtMerchantName;
    private AppCompatEditText mEtMerchantLegalName;
    private AppCompatEditText mEtMerchantLegalId;
    private AppCompatEditText mEtMerchantLegalBankCard;
    private AppCompatEditText mEtMerchantMerchantLocation;

    /**
     * 关键字展示
     */
    private AppCompatTextView mTvMerchantEnteredKeyword;

    private LinearLayoutCompat mLlAddMerchantPhoto;
    /**
     * 高德地图
     */
    private TextureMapView mTextureMapViewMerchantEntered;
    private AppCompatButton mBtnCommitMerchantEntered;

    private float mLastY = .0F;
    private float mLastX = .0F;
    private ListViewDialog mBottomDialog;
    private ContentDialog mCenterDialog;
    /**
     * 图片保存路径
     */
    private File mImageParentPath;

    /**
     * 商家认证界面 menu 状态切换
     */
    private boolean isEnable;

    /**
     * 选择照片的数量，默认为 一张
     */
    private int mMultiPhotoCount = ONE_PHOTO;
    /**
     * 宽度
     */
    private int mWidthScaleSize = 0;
    /**
     * 高度
     */
    private int mHeightScaleSize = 0;

    /**
     * 当前需要设置图片的imageView
     */
    private String mCurrentImageView;

    private HttpConnectedUtils mHttpConnected;

    private HintDialogUtil mHintDialog;

    /**
     * 是否是设置店铺照片
     */
    private boolean isMerchantPhoto = false;

    /**
     * 店铺照片 父布局
     */
    private HorizontalScrollView mHorizontalScrollView;
    private AppCompatEditText mEtMerchantDescribe;

    /**
     * 滑动的限制
     */
    private int mScaledSize;
    /**
     * 纬度
     */
    private String mLatitude = null;
    /**
     * 经度
     */
    private String mLongitude = null;

    /**
     * 店铺照片 起始值
     */
    private int mPositionKey = ONE_PHOTO;
    private AMap mAMap;

    /**
     * 记录x, y 坐标
     */
    private Point mPoint = new Point();
    private GeocodeSearch mGeocodeSearch;
    private AppCompatEditText mBankName;
    /**
     * 当前界面类型
     */
    private int mType;
    private int mMerchantImageWidth;

    /**
     * 商家认证用， 是否是管理员
     */
    private boolean isOwnerMerchant;
    /**
     * 是否进行图片裁剪
     */
    private boolean isScale;

    /**
     * 商家认证 重新的选择的图片路径
     */
    private Stack<File> mFileList;

    /**
     * 商家认证 重新的选择的图片 key
     */
    private ArrayMap<File, String> mKeySet;

    /**
     * 当前是否 是 点击了添加 按钮 进行 添加 商家照片
     */
    private boolean isAddMerchantImg;
    //勾选 已同意
    private AppCompatCheckBox mCbAgreeEntered;

    /**
     * 是否检查过第一次了入驻了
     */
    private boolean isCheckOnce;
    /**
     * 关键字 btn
     */
    private AppCompatButton mBtnConfigKeyword;
    private AppCompatImageView mIvAddMerchantPhoto;
    /**
     * 是否进行验证了实名认证
     */
    private boolean isConfirmVerification;

    /**
     * 启动 商家入驻、商家认证 界面
     *
     * @param type   界面类型
     *               {@link #TYPE_MERCHANT_ENTRY 商家入驻界面;}
     *               {@link #TYPE_MERCHANT_CERTIFICATION 商家认证界面}
     *               <p>
     *               如果为商家认证界面，参数为：content, MerchantEnteredActivity.TYPE_MERCHANT_CERTIFICATION
     * @param shopId 商家认证界面 不能为空
     */
    public static void startActivity(Context context, int type, @Nullable String shopId) {
        Intent intent = new Intent(context, MerchantEnteredActivity.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_SHOP_ID, shopId);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                activity.startActivity(intent);
            else {
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } else {
            context.startActivity(intent);
        }
    }


    @Override
    protected int getLayoutId() {
        Intent intent = getIntent();
        BaseConfig.checkState(intent, ACTION_START);
        mType = intent.getIntExtra(EXTRA_TYPE, -1);
        mShopId = intent.getStringExtra(EXTRA_SHOP_ID);
        return R.layout.yh_activity_merchant_entered;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void subOnCreate(Bundle savedInstanceState) {
        setStatusBarColor(R.color.colorPrimaryDark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_simple_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(this);

        if (savedInstanceState != null) {
            this.mFileArrayMap = (HashMap<String, String>) savedInstanceState.getSerializable("mFAM");
            this.mStringArrayMap = (HashMap<String, String>) savedInstanceState.getSerializable("mSAM");
            this.isConfirmVerification = savedInstanceState.getBoolean("isCV");
            Set<String> set = fImageViewDelete.keySet();

            for (String str : set) {
                fImageViewDelete.get(str).set((MerchantEnteredInnerImageInfo) savedInstanceState.getParcelable(str));
            }
            mCurrentImageView = savedInstanceState.getString("mCIV");
            mMerchantImageWidth = savedInstanceState.getInt("mMIW");
            isMerchantPhoto = savedInstanceState.getBoolean("iMP");
            isAddMerchantImg = savedInstanceState.getBoolean("iAMI");

            if (mType == TYPE_MERCHANT_CERTIFICATION) {
                mShopId = savedInstanceState.getString("mSI");
                isScale = savedInstanceState.getBoolean("iS");
                isEnable = savedInstanceState.getBoolean("iE");
                isOwnerMerchant = savedInstanceState.getBoolean("iOM");
            } else {
                isCheckOnce = savedInstanceState.getBoolean("iCO");
            }
        } else {
            mFileArrayMap = new HashMap<>(10);
            mStringArrayMap = new HashMap<>(12);
        }

        initView();

        if (null == mHintDialog) {
            mHintDialog = new HintDialogUtil(MerchantEnteredActivity.this);
        }

        if (MerchantEnteredActivity.this.mType == MerchantEnteredActivity.TYPE_MERCHANT_CERTIFICATION) {
            ((AppCompatTextView) findViewById(R.id.tv_title_tool_bar)).setText(R.string.merchant_attestation_title);

            if (mBtnCommitMerchantEntered != null)
                mBtnCommitMerchantEntered.setText(R.string.merchant_attestation);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_modify) {
                        if (isEnable) {
                            item.setTitle(R.string.merchant_modify);
                            setEnableModify(false);
                        } else {
                            item.setTitle(R.string.merchant_dialog_cancel);
                            setEnableModify(true);
                        }
                        return true;
                    }
                    return false;
                }
            });

            mHintDialog.showLoading(R.string.merchant_getting_info, true, false);


        } else {
            ((AppCompatTextView) findViewById(R.id.tv_title_tool_bar)).setText(R.string.main_page_entered);
        }

        if (mHttpConnected == null) {
            mHttpConnected = HttpConnectedUtils.getInstance(MerchantEnteredActivity.this);
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                initBottomDialogData();
                initCenterDialogData();
                mMerchantImageWidth = (int) (mLlAddMerchantPhoto.getHeight() * 640 / 374F + .5F);

                if (MerchantEnteredActivity.this.mType == MerchantEnteredActivity.TYPE_MERCHANT_CERTIFICATION) {
                    ArrayMap<String, String> arrayMap = new ArrayMap<>(4);
                    arrayMap.put("id", mShopId == null ? "" : mShopId);
                    mHttpConnected.startNetworkGetString(MerchantEnteredActivity.ID_LOADING_INFO, arrayMap, ApiConstants.ACTIVITY_MERCHANT_ENTRY_INFO);
                }
            }
        });

        mTextureMapViewMerchantEntered.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mType == TYPE_MERCHANT_CERTIFICATION) {
            getMenuInflater().inflate(R.menu.yh_menu_info_modify, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mType == TYPE_MERCHANT_ENTRY) {
            GDLocationHelper.getInstance().getOnceLocation(this);
            if (!NetworkUtils.isNetworkConnected()) {
                new AlertDialog.Builder(MerchantEnteredActivity.this, R.style.AlertDialogStyle)
                        .setMessage(R.string.merchant_network_fail)
                        .setCancelable(false)
                        .setNegativeButton(R.string.merchant_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                MerchantEnteredActivity.this.finish();
                            }
                        })
                        .setPositiveButton(R.string.merchant_check_network, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                if (null != intent.resolveActivity(MerchantEnteredActivity.this.getPackageManager())) {
                                    MerchantEnteredActivity.this.startActivity(intent);
                                }
                            }
                        })
                        .create().show();
            } else if (!isConfirmVerification) {
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        mHintDialog.showLoading(R.string.merchant_confirm_data, false, false).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                MerchantEnteredActivity.this.finish();
                            }
                        });
                        ArrayMap<String, String> arrayMap = new ArrayMap<>(4);
                        arrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());
                        mHttpConnected.startNetworkGetString(ID_VERIFICATION_ACTUAL, arrayMap, ApiConstants.REALNAME_INFOR);
                    }
                });
            }
        }

        /*if (!isCheckOnce && mEtCommendPerson == null && MerchantEnteredActivity.this.mType == TYPE_MERCHANT_ENTRY) {
            mHttpConnected.startNetworkGetString(MerchantEnteredActivity.ID_GET_COMMEND,
                    new ArrayMap<String, String>(1), ApiConstants.ACTIVITY_MERCHANT_ENTRY_COMMEND);
        }*/// TODO: 2017/3/11 推荐人
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextureMapViewMerchantEntered.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTextureMapViewMerchantEntered != null)
            mTextureMapViewMerchantEntered.onDestroy();

        if (mCenterDialog != null) {
            mCenterDialog.dismiss();
            mCenterDialog.onDestroy();
        }
        if (mHttpConnected != null) {
            mHttpConnected.onDestroy();
            mHttpConnected = null;
        }

        if (mBottomDialog != null) {
            mBottomDialog.dismiss();
            mBottomDialog.onDestroy();
            mBottomDialog = null;
        }

        if (mFileArrayMap != null) {
            mFileArrayMap.clear();
            mFileArrayMap = null;
        }

        if (mStringArrayMap != null) {
            mStringArrayMap.clear();
            mStringArrayMap = null;
        }

        if (mKeySet != null) {
            mKeySet.clear();
            mKeySet = null;
        }

        if (mFileList != null) {
            mFileList.clear();
            mFileList = null;
        }

        GDLocationHelper.getInstance().removeLocationListener(this);
        fImageViewDelete.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mTextureMapViewMerchantEntered.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mTextureMapViewMerchantEntered.onSaveInstanceState(outState);
        outState.putSerializable("mFAM", mFileArrayMap);
        outState.putSerializable("mSAM", mStringArrayMap);
        Set<String> set = fImageViewDelete.keySet();
        for (String str : set) {
            outState.putParcelable(str, fImageViewDelete.get(str));
        }
        outState.putString("mCIV", mCurrentImageView);
        outState.putInt("mPK", mPositionKey);
        outState.putParcelable("mP", mPoint);
        outState.putBoolean("iMP", isMerchantPhoto);
        outState.putInt("mMIW", mMerchantImageWidth);
        outState.putBoolean("iAMI", isAddMerchantImg);

        if (mType == TYPE_MERCHANT_CERTIFICATION) {
            outState.putString("mSI", mShopId);
            outState.putBoolean("iS", isScale);
            outState.putBoolean("iE", isEnable);
            outState.putBoolean("iOM", isOwnerMerchant);
        } else {
            outState.putBoolean("iCO", isCheckOnce);
            outState.putBoolean("isCV", isConfirmVerification);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPositionKey = savedInstanceState.getInt("mPK");
        mPoint = savedInstanceState.getParcelable("mP");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_keyword:
                mCenterDialog.showDialog(true, false);
                break;
            case R.id.iv_select_merchant_cover:
                showDialog(true, KEY_MERCHANT_COVER, SCALE_WIDTH_SIZE, SCALE_OTHER_HEIGHT_SIZE);
                break;
            case R.id.iv_delete_merchant_cover:
                resetImageView(KEY_MERCHANT_COVER, false);
                break;
            case R.id.iv_select_merchant_legal_photo:
                showDialog(false, KEY_LEGAL_PHOTO, SCALE_WIDTH_SIZE, SCALE_OTHER_HEIGHT_SIZE);
                break;
            case R.id.iv_delete_merchant_legal_photo:
                resetImageView(KEY_LEGAL_PHOTO, false);
                break;
            case R.id.iv_select_merchant_legal_photo_positive:
                showDialog(false, KEY_LEGAL_PHOTO_POSITIVE, SCALE_ID_CARD_WIDTH, SCALE_ID_CARD_HEIGHT);
                break;
            case R.id.iv_delete_merchant_legal_photo_positive:
                resetImageView(KEY_LEGAL_PHOTO_POSITIVE, false);
                break;
            case R.id.iv_select_merchant_legal_photo_negative:
                showDialog(false, KEY_LEGAL_PHOTO_NEGATIVE, SCALE_ID_CARD_WIDTH, SCALE_ID_CARD_HEIGHT);
                break;
            case R.id.iv_delete_merchant_legal_photo_negative:
                resetImageView(KEY_LEGAL_PHOTO_NEGATIVE, false);
                break;
            case R.id.iv_select_merchant_legal_bank_positive:
                showDialog(false, KEY_LEGAL_BANK_POSITIVE, SCALE_ID_CARD_WIDTH, SCALE_ID_CARD_HEIGHT);
                break;
            case R.id.iv_delete_merchant_legal_bank_positive:
                resetImageView(KEY_LEGAL_BANK_POSITIVE, false);
                break;
            case R.id.iv_select_merchant_business_photo:
                showDialog(false, KEY_BUSINESS_PHOTO, SCALE_WIDTH_SIZE, SCALE_OTHER_HEIGHT_SIZE);
                break;
            case R.id.iv_delete_merchant_business_photo:
                resetImageView(KEY_BUSINESS_PHOTO, false);
                break;
            case R.id.tv_merchant_entered_agreement:
                DiscoverActivity.startActivity(this, getString(R.string.merchant_entry_deal), ApiConstants.MERCHANT_ENTRY_DEAL);
                break;
            case R.id.btn_commit_merchant_entered:
                mBtnCommitMerchantEntered.setClickable(false);
                if (submit()) {
                    mBtnCommitMerchantEntered.setClickable(true);
                }
                break;
            case R.id.iv_add_merchant_photo:
                showDialogOfMerchantPhoto(v, true);
                break;
            case R.id.iv_add:
                showDialogOfMerchantPhoto(v, false);
                break;
            case R.id.iv_delete:
                resetImageView((String) v.getTag(TAG), false);
                break;
            case R.id.btn_search_address:
                String city = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCity();
                city = city == null ? "" : city;
                if (mGeocodeSearch != null) {
                    if (mGeocodeQuery == null) {
                        mGeocodeQuery = new GeocodeQuery(mEtMerchantMerchantLocation.getText().toString(), city);
                    } else {
                        mGeocodeQuery.setCity(city);
                        mGeocodeQuery.setLocationName(mEtMerchantMerchantLocation.getText().toString());
                    }
                    MainActivity.closeIME(false, v);
                    mGeocodeSearch.getFromLocationNameAsyn(mGeocodeQuery);
                    ToastUtil.showShort(R.string.merchant_find_location);
                }
                break;
            default:
                this.finish();
                break;
        }
    }

//    ---------------------TakePhoto Start-------------------------------------------------------------------------

    @Override
    public void takeSuccess(TResult result) {

        if (!isAddMerchantImg && mMultiPhotoCount <= ONE_PHOTO) {
            if (isMerchantPhoto) {
                addMerchantPhoto(true, result.getImage().getCompressPath(), true);
            } else {
                addMerchantPhoto(false, result.getImage().getCompressPath(), true);
            }
        } else {
            if (isMerchantPhoto) {
                ArrayList<TImage> arrayList = result.getImages();
                for (TImage tImage : arrayList)
                    addMerchantPhoto(true, tImage.getCompressPath(), false);
                mHorizontalScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                    }
                }, 300);
            }
            isAddMerchantImg = false;
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        ToastUtil.showShort(R.string.merchant_get_img_cancel);
    }

    /**
     * @param isMerchantPhoto 当前为 商铺照片
     * @param path            照片路径或网址
     * @param modify          修改原先的
     */
    private void addMerchantPhoto(boolean isMerchantPhoto, String path, boolean modify) {

        if (TextUtils.isEmpty(path))
            return;

        if (isMerchantPhoto) {

            if (modify) {
                MerchantEnteredInnerImageInfo imageInfo = fImageViewDelete.get(mCurrentImageView);
                if (imageInfo == null) return;
                AppCompatImageView imageView = imageInfo.mAppCompatImageView;
                ImageLoader.getImageFromLocation(imageView, path, this, R.drawable.pick_camera);
                imageInfo.mDeleteAppCompatImageView.setVisibility(View.VISIBLE);
                imageInfo.isSetImage = true;
                imageInfo.mFilePath = path;
                mFileArrayMap.put(imageInfo.mUpdateKey, path);
            } else {
                View view = getLayoutInflater().inflate(R.layout.yh_merchant_entered_select_photo, mLlAddMerchantPhoto, false);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                        mMerchantImageWidth, ViewGroup.LayoutParams.MATCH_PARENT);

                int count = mLlAddMerchantPhoto.getChildCount();
                if (--count <= MAX_SELECT_PHOTO_NUM) {
                    mLlAddMerchantPhoto.addView(view, count < 0 ? 0 : count, layoutParams);
                    AppCompatImageView imageView = (AppCompatImageView) view.findViewById(R.id.iv_add);
                    ImageLoader.getImageFromLocation(imageView, path, this, R.drawable.pick_camera);
                    View delete = view.findViewById(R.id.iv_delete);
                    delete.setVisibility(View.VISIBLE);
                    String tag = "";
                    if (TYPE_MERCHANT_ENTRY == mType) {
                        //接口 key
                        tag = String.valueOf(KEY_IMG_PREFIX + mPositionKey++);
                    } else {
                        if (isAddMerchantImg) {
                            for (int i = 1; i <= MAX_SELECT_PHOTO_NUM; ++i) {
                                if (!mFileArrayMap.containsKey(KEY_IMG_PREFIX + i)) {
                                    tag = KEY_IMG_PREFIX + i;
                                    break;
                                }
                            }
                        } else {
                            tag = mCurrentImageView;
                        }
                    }

                    fImageViewDelete.put(tag, new MerchantEnteredInnerImageInfo(tag, imageView, (AppCompatImageView) delete, true, path));
                    delete.setTag(TAG, tag);
                    imageView.setTag(TAG, tag);
                    delete.setOnClickListener(this);
                    imageView.setOnClickListener(this);
                    mFileArrayMap.put(tag, path);
                } else
                    ToastUtil.showShort(R.string.merchant_photo_max);
            }

        } else {
            MerchantEnteredInnerImageInfo imageInfo = fImageViewDelete.get(mCurrentImageView);
            AppCompatImageView imageView;
            if (imageInfo == null || (imageView = imageInfo.mAppCompatImageView) == null)
                return;

            ImageLoader.getImageFromLocation(imageView, path, this, R.drawable.pick_camera);
            imageInfo.mDeleteAppCompatImageView.setVisibility(View.VISIBLE);
            imageInfo.isSetImage = true;
            imageInfo.mFilePath = path;
            mFileArrayMap.put(imageInfo.mUpdateKey, path);
        }
    }

    @Override
    public void onBefore(Request request, int id) {
    }

    @Nullable
    @Override
    public HintDialogUtil getHintDialog() {
        return null;
    }

    @Override
    public void onResponse(String response, int id) {
        switch (id) {
            case ID_ENTRY:
                initEnteredInfo(DataRow.parseJson(response), getString(R.string.merchant_entry_successful), getString(R.string.merchant_entry_fail));
                break;
            case ID_CERTIFICATION://{"result":true,"data":[],"success":true,"type":"map","message":null}
                initEnteredInfo(DataRow.parseJson(response), getString(R.string.merchant_attestation_successful), getString(R.string.merchant_attestation_fail));
                break;
            case ID_UPDATE_IMAGE://{"result":{"code":"0","msg":"http://xxx/148367507467470b53c16.jpg"}}
                //{"result":true,"code":"200",
                // "data":{"FILE_PATH":"/ig?id=2759","FILE_URL":"http://xxxig?id=2759"},
                // "success":true,"type":"map","message":null}
                DataRow msg = DataRow.parseJson(response);
                if (msg != null) {

                    if (msg.getBoolean("result", false)) {

                        if (mFileList != null && !mFileList.empty()) {

                            if ((msg = msg.getRow("data")) != null) {
                                String img = msg.getString("FILE_URL");
                                if (!ControlUtils.isNull(img)) {
                                    mStringArrayMap.put(mKeySet.get(mFileList.pop()), img);
                                    getModifyImage(false);
                                    return;
                                }
                            }
                        }

                    } else {
                        mHintDialog.showError(msg.getStringDef("message", getString(R.string.merchant_upload_img_fail)));
                        return;
                    }
                }

                mHintDialog.showError(getString(R.string.merchant_upload_img_fail));
                break;
            case ID_LOADING_INFO:
                DataRow infoData = DataRow.parseJson(response);
                if (infoData.getBoolean("result", false) && (infoData = infoData.getRow("data")) != null) {
                    if (infoData.getBoolean("is_reviewed", false)) {
                        mHintDialog.dismissDialog();
                        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                                .setMessage(R.string.merchant_attestation_ing).setPositiveButton(R.string.merchant_confirm,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MerchantEnteredActivity.this.finish();
                                    }
                                }).setCancelable(false).create().show();
                    } else {
                        /*
                        {"result":true,"code":"200",
                        "data":{"AREA_ID":null,"SHOP_KEYWORD3":"","SHOP_KEYWORD2":"","SHOP_LON":"120.054609",
                        "SHOP_NAME":"青岛隐珠假日酒店","SHOP_KEYWORD":"","USER_ID":193,
                        "IMG1":"/activity_img/14844628851683e50f7cf.jpg","IMG2":"/activity_img/1484463137499210f5a30.jpg",
                        "SHOP_SHOP_SHOPPIC":"/activity_img/14844631362807d794fd8.jpg","SHOP_LAT":"35.868528",
                        "IMG3":"/activity_img/1485153498291f1ee7e1f.jpg","SHOP_LONGITUDE":"120.054609",
                        "IMG4":"/activity_img/1485153432079ed0ffcfb.jpg","SHOP_TEL":"18300267060",
                        "SHOP_OWNER_NAME":"张培勤","SHOP_TC":2,"UID_PK":170109003,"ID":211,"SHOP_COMMENT_COUNT":0,
                        "SHOP_OWNER_PERSION_CARD_F":"/shop_img/1484403527239ddc7d5ae.jpg","UPT_TIME":"2017-01-23 04:24:55",
                        "SHOP_OWNER_BANK":"青岛银行","MAP_ID":569,"SHOP_ADDRESS":"山东省青岛市黄岛区隐珠街道海景路城市阳台景区",
                        "SHOP_INTRODUCE":"青岛隐珠","SHOP_OWNER_PERSION_CARD_Z":"/activity_img/1484458760389a7c0a2d9.jpg",
                        "INVITE_USER_ID":null,"SHOP_OWNER_PERSION_CARD_NO":"370285198211253257","SHOP_OWNER_BANKCARD_NO":"3702",
                        "TIME":"2017-01-14 10:18:47","IS_REVIEWED":2,"SHOP_OWNER_PIC":"/activity_img/148440709647242919125.jpg",
                        "SHOP_TRADING_COUNT":0,"USER_NM":"遇水搭桥",
                        "SHOP_OWNER_BACKCARD_PIC":"/activity_img/14844587631082d906f04.jpg","SHOP_LATITUDE":"35.868528",
                        "CLICK":206,"AREA_NM":null,"SHOP_AVATAR":"/shop_img/1484403527239e326a859.jpg","BANK_NAME":"青岛银行",
                        "NM":"青岛隐珠假日酒店"},"success":true,"type":"map","message":null}
                         */
                        isOwnerMerchant = infoData.getStringDef("UID_PK", "").equals(AiSouAppInfoModel.getInstance().getUID());
                        initGDLocationData(infoData);
                        initEditTextData(infoData);
                        initImageViewData(infoData);
                        initKeywordDialogData(infoData);
                        setEnableModify(false);
                        mHintDialog.dismissDialog();
                    }
                } else {
                    mHintDialog.showError(R.string.merchant_get_info_fail);
                    mHintDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            MerchantEnteredActivity.this.finish();
                        }
                    });
                }
                break;
            case MerchantEnteredActivity.ID_GET_COMMEND://{"result":true,"code":"200","data":"0","success":true,"type":"number","message":null}
                DataRow count = DataRow.parseJson(response);
                isCheckOnce = true;
                if (mEtCommendPerson == null && count != null && count.getBoolean("result", false) && "0".equals(count.getString("data"))) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.view_stub_recommend);
                    View inflate = viewStub.inflate();
                    mEtCommendPerson = (AppCompatEditText) inflate.findViewById(R.id.et_merchant_commend_person);
                }
                break;

            case MerchantEnteredActivity.ID_VERIFICATION_ACTUAL:
                DataRow dataRow = DataRow.parseJson(response);
                boolean isShowDialog = true;
                if (null != dataRow) {
                    if (dataRow.getBoolean("result", false) && null != (dataRow = dataRow.getRow("data"))) {
                        if (!ControlUtils.isNull(dataRow.getString("REALNAME"))) {
                            isShowDialog = false;
                            isConfirmVerification = true;
                            initMapSearch();
                            mHintDialog.setOnCancelListener(null);
                        }
                    }
                }

                if (null != mHintDialog) {
                    mHintDialog.dismissDialog();
                }

                if (isShowDialog) {
                    new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                            .setMessage(R.string.merchant_entry_request)
                            .setPositiveButton(R.string.merchant_attestation, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(MerchantEnteredActivity.this, RealNameActivity.class));
                                }
                            }).setNeutralButton(R.string.merchant_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MerchantEnteredActivity.this.finish();
                        }
                    }).setCancelable(true).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            MerchantEnteredActivity.this.finish();
                        }
                    }).create().show();
                }
                break;
        }
    }

    /**
     * 初始化提交 入驻、认证后的信息
     *
     * @param success 信息成功提示
     * @param fail    信息失败提示
     */
    public void initEnteredInfo(DataRow dataRow, String success, String fail) {
        if (dataRow != null) {

            if (dataRow.getBoolean("result", false)) {

                mHintDialog.dismissDialog();
                MerchantCommitDialog dialog = new MerchantCommitDialog(this, success);
                dialog.setOnClickListener(new MerchantCommitDialog.IOnConfirmClickListener() {
                    @Override
                    public void onConfirmClickListener(MerchantCommitDialog dialog, View view) {
                        dialog.dismiss();
                        MerchantEnteredActivity.this.finish();
                    }
                });
                dialog.showDialog(false, false);

            } else {
                mBtnCommitMerchantEntered.setClickable(true);
                mHintDialog.showError(dataRow.getStringDef("MESSAGE", fail), true, true);
            }
        } else {
            mBtnCommitMerchantEntered.setClickable(true);
            mHintDialog.showError(fail, true, true);
        }
    }

    /**
     * 初始化 图片数据
     */
    private void initImageViewData(DataRow dataRow) {
        ToastUtil.showShort(R.string.merchant_loading_attestation);
        String temp = dataRow.getString("IMG1");
        if (!ControlUtils.isNull(temp)) {
            mCurrentImageView = "img1";
            addMerchantPhoto(true, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("IMG2"))) {
            mCurrentImageView = "img2";
            addMerchantPhoto(true, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("IMG3"))) {
            mCurrentImageView = "img3";
            addMerchantPhoto(true, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("IMG4"))) {
            mCurrentImageView = "img4";
            addMerchantPhoto(true, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_avatar"))) {
            mCurrentImageView = KEY_MERCHANT_COVER;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_owner_pic"))) {
            mCurrentImageView = KEY_LEGAL_PHOTO;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_owner_persion_card_z"))) {
            mCurrentImageView = KEY_LEGAL_PHOTO_POSITIVE;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_owner_persion_card_f"))) {
            mCurrentImageView = KEY_LEGAL_PHOTO_NEGATIVE;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_owner_backcard_pic"))) {
            mCurrentImageView = KEY_LEGAL_BANK_POSITIVE;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        if (!ControlUtils.isNull(temp = dataRow.getString("shop_shop_shoppic"))) {
            mCurrentImageView = KEY_BUSINESS_PHOTO;
            addMerchantPhoto(false, BaseConfig.getCorrectImageUrl(temp), false);
        }

        mHorizontalScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
            }
        }, 500);
    }

    /**
     * 初始化 关键字数据
     */
    private void initKeywordDialogData(DataRow dataRow) {
        if (mCenterDialog == null) {
            mCenterDialog = new ContentDialog(mContext, Gravity.CENTER);
        }
        String shop_keyword = dataRow.getString("SHOP_KEYWORD");
        if (!isNull(shop_keyword)) {
            mCenterDialog.setKeywords(shop_keyword);
        }
        shop_keyword = dataRow.getString("SHOP_KEYWORD2");
        if (!isNull(shop_keyword)) {
            mCenterDialog.setKeywords(shop_keyword);
        }
        shop_keyword = dataRow.getString("SHOP_KEYWORD3");
        if (!isNull(shop_keyword)) {
            mCenterDialog.setKeywords(shop_keyword);
        }

        StringBuilder keywords = mCenterDialog.getKeywords();

        if (keywords.length() != 0) {
            mTvMerchantEnteredKeyword.setText(keywords);
            mStringArrayMap.put("keyword", keywords.toString());
        } else {
            mTvMerchantEnteredKeyword.setText(R.string.merchant_keyword);
        }
    }

    private boolean isNull(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0 || "null".equals(charSequence);
    }

    /**
     * 初始化 文本框数据
     */
    private void initEditTextData(DataRow dataRow) {
        String temp = dataRow.getString("SHOP_NAME");
        mEtMerchantName.setText(temp == null || "null".equals(temp) ? "" : temp);
        mEtMerchantPhone.setText((temp = dataRow.getString("SHOP_TEL")) == null || "null".equals(temp) ? "" : temp);
        mEtMerchantLegalName.setText((temp = dataRow.getString("SHOP_OWNER_NAME")) == null || "null".equals(temp) ? "" : temp);
        mEtMerchantLegalId.setText((temp = dataRow.getString("SHOP_OWNER_PERSION_CARD_NO")) == null || "null".equals(temp) ? "" : temp);
        mEtMerchantLegalBankCard.setText((temp = dataRow.getString("SHOP_OWNER_BANKCARD_NO")) == null || "null".equals(temp) ? "" : temp);
        mBankName.setText((temp = dataRow.getString("SHOP_OWNER_BANK")) == null || "null".equals(temp) ? "" : temp);
        mEtMerchantDescribe.setText((temp = dataRow.getString("SHOP_INTRODUCE")) == null || "null".equals(temp) ? "" : temp);
    }

    /**
     * 用于 认证
     */
    private void setEnableModify(boolean enable) {

        if (isOwnerMerchant) {//是自己的商家
            mEtMerchantName.setEnabled(enable);
            mEtMerchantPhone.setEnabled(enable);
            mEtMerchantLegalName.setEnabled(enable);
            mEtMerchantLegalId.setEnabled(enable);
            mEtMerchantLegalBankCard.setEnabled(enable);
            mBankName.setEnabled(enable);
            int visibility = enable ? View.VISIBLE : View.GONE;
            Set<String> key = fImageViewDelete.keySet();
            for (String imageView : key) {
                fImageViewDelete.get(imageView).mDeleteAppCompatImageView.setVisibility(visibility);
            }
        } else {//是管理员

            //管理员 不可编辑 信息
            int color = ContextCompat.getColor(this, R.color.darkGreyColor);
            mEtMerchantName.setTextColor(color);
            mEtMerchantPhone.setTextColor(color);
            mEtMerchantLegalName.setTextColor(color);
            mEtMerchantLegalId.setTextColor(color);
            mEtMerchantLegalBankCard.setTextColor(color);
            mBankName.setTextColor(color);

            mEtMerchantName.setEnabled(false);
            mEtMerchantPhone.setEnabled(false);
            mEtMerchantLegalName.setEnabled(false);
            mEtMerchantLegalId.setEnabled(false);
            mEtMerchantLegalBankCard.setEnabled(false);
            mBankName.setEnabled(false);

            int visibility = enable ? View.VISIBLE : View.GONE;
            Set<String> key = fImageViewDelete.keySet();
            for (String imageView : key) {
                if (KEY_LEGAL_BANK_POSITIVE.equals(imageView) || KEY_LEGAL_PHOTO.equals(imageView)
                        || KEY_LEGAL_PHOTO_NEGATIVE.equals(imageView) || KEY_LEGAL_PHOTO_POSITIVE.equals(imageView)
                        || KEY_BUSINESS_PHOTO.equals(imageView)) {
                    fImageViewDelete.get(imageView).mDeleteAppCompatImageView.setVisibility(View.GONE);
                } else {
                    fImageViewDelete.get(imageView).mDeleteAppCompatImageView.setVisibility(visibility);
                }
            }
        }

        mIvAddMerchantPhoto.setEnabled(enable);
        mBtnConfigKeyword.setEnabled(enable);
        mEtMerchantDescribe.setEnabled(enable);
        mCbAgreeEntered.setChecked(false);
        mCbAgreeEntered.setEnabled(enable);
        mEtMerchantMerchantLocation.setEnabled(enable);
        mAMap.getUiSettings().setAllGesturesEnabled(enable);
        isEnable = enable;
    }

    /**
     * 初始化高德数据
     */
    private void initGDLocationData(DataRow dataRow) {
        mLongitude = dataRow.getString("SHOP_LONGITUDE");
        mLatitude = dataRow.getString("SHOP_LATITUDE");

        try {
            if (mLongitude != null && mLongitude.length() != 0 && new BigDecimal(mLongitude).compareTo(BigDecimal.ZERO) != 0
                    && mLatitude != null && mLatitude.length() != 0 && new BigDecimal(mLatitude).compareTo(BigDecimal.ZERO) != 0) {
                initGDLocationData(Double.valueOf(mLatitude), Double.valueOf(mLongitude), dataRow.getString("SHOP_ADDRESS"));
            } else {
                GDLocationHelper.getInstance().getOnceLocation(this);
            }
        } catch (Exception e) {
            GDLocationHelper.getInstance().getOnceLocation(this);
        }
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        switch (id) {
            case ID_ENTRY:
                if (!mBtnCommitMerchantEntered.isClickable()) {
                    mBtnCommitMerchantEntered.setClickable(true);
                }
                if (null != mHintDialog)
                    mHintDialog.showError(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.merchant_entry_apply_error)), true, true);

                break;
            case ID_CERTIFICATION:
            case ID_UPDATE_IMAGE:
                if (!mBtnCommitMerchantEntered.isClickable()) {
                    mBtnCommitMerchantEntered.setClickable(true);
                }
                if (null != mHintDialog)
                    mHintDialog.showError(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.merchant_entry_img_apply_error)), true, true);

                break;
            case ID_LOADING_INFO:
                if (null != mHintDialog) {
                    mHintDialog.showError(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.merchant_entry_loading_info_error)), true, true);
                    mHintDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            MerchantEnteredActivity.this.finish();
                        }
                    });
                }
                break;

            case ID_VERIFICATION_ACTUAL:
                if (null != mHintDialog) {
                    mHintDialog.showError(NetworkUtils.getNetworkStateDescription(call, e, getString(R.string.merchant_entry_verification_error)));
                }
                break;
        }
    }

    //    ---------------------TakePhoto End-------------------------------------------------------------------------

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Marker marker = mAMap.addMarker(new MarkerOptions());
            int x = mTextureMapViewMerchantEntered.getMeasuredWidth() >> 1;
            int y = mTextureMapViewMerchantEntered.getMeasuredHeight() >> 1;
            marker.setPositionByPixels(x, y);
            mPoint.set(x, y);
        }
    }

    private LatLonPoint mLatLonPoint;
    private RegeocodeQuery mRegeocodeQuery;
    private GeocodeQuery mGeocodeQuery;

    /**
     * 高德的地图滑动
     */
    @Override
    public void onTouch(MotionEvent motionEvent) {
        mTextureMapViewMerchantEntered.requestDisallowInterceptTouchEvent(true);
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (mGeocodeSearch != null) {
                LatLng latLng = mAMap.getProjection().fromScreenLocation(mPoint);
                if (mLatLonPoint == null) {
                    mLatLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                    mRegeocodeQuery = new RegeocodeQuery(mLatLonPoint, 50, GeocodeSearch.AMAP);
                } else {
                    mLatLonPoint.setLongitude(latLng.longitude);
                    mLatLonPoint.setLatitude(latLng.latitude);
                    mRegeocodeQuery.setPoint(mLatLonPoint);
                }
                mGeocodeSearch.getFromLocationAsyn(mRegeocodeQuery);
            }
        }
    }

    //高德地图回调
    @Override
    public void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation) {
        if (isSuccessful) {
            initGDLocationData(latitude, longitude, aMapLocation.getAddress());
        }
    }

    private void initGDLocationData(double latitude, double longitude, String address) {
        mLatitude = String.valueOf(latitude);//获取纬度
        mLongitude = String.valueOf(longitude);//获取经度

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(latitude, longitude),//新的中心点坐标
                18, //新的缩放级别
                0, //俯仰角0°~45°（垂直与地图时为0）
                0  ////偏航角 0~360° (正北方为0)
        ));

        mAMap.moveCamera(cameraUpdate);
        mEtMerchantMerchantLocation.setText(address);

        initMapSearch();
    }

    /**
     * 初始化地图搜索模块
     */
    private void initMapSearch() {
        mAMap.setOnMapTouchListener(this);
        if (mGeocodeSearch == null) {
            mGeocodeSearch = new GeocodeSearch(getApplicationContext());
            mGeocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    if (rCode == 1000) {
                        RegeocodeAddress regeocodeAddress;
                        RegeocodeQuery regeocodeQuery;
                        if (result != null && (regeocodeAddress = result.getRegeocodeAddress()) != null
                                && (regeocodeQuery = result.getRegeocodeQuery()) != null) {

                            LatLonPoint point = regeocodeQuery.getPoint();
                            mLatitude = String.valueOf(point.getLatitude());//获取纬度
                            mLongitude = String.valueOf(point.getLongitude());//获取经度
                            mHttpConnected.setCityAndAddressCode(regeocodeAddress.getCityCode(), regeocodeAddress.getAdCode());
                            String formatAddress = regeocodeAddress.getFormatAddress();
                            mEtMerchantMerchantLocation.setText(formatAddress);
                            mEtMerchantMerchantLocation.setSelection(formatAddress.length());
                        } else {
                            ToastUtil.showShort(R.string.location_get_fail);
                        }
                    } else {
                        ToastUtil.showShort(R.string.location_get_fail);
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult result, int rCode) {
                    if (rCode == 1000) {
                        if (result != null && result.getGeocodeAddressList() != null
                                && result.getGeocodeAddressList().size() > 0) {
                            GeocodeAddress address = result.getGeocodeAddressList().get(0);
                            LatLonPoint latLonPoint = address.getLatLonPoint();

                            double latitudeDouble = latLonPoint.getLatitude();
                            double longitudeDouble = latLonPoint.getLongitude();
                            mLatitude = String.valueOf(latitudeDouble);//获取纬度
                            mLongitude = String.valueOf(longitudeDouble);//获取经度
                            mHttpConnected.setCityAndAddressCode(null, address.getAdcode());
                            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitudeDouble, longitudeDouble), 18));
                            ToastUtil.showShort(R.string.merchant_find_location_successful);
                        } else {
                            ToastUtil.showShort(R.string.merchant_find_location_not_find);

                        }
                    } else {
                        ToastUtil.showShort(R.string.merchant_find_location_fail);
                    }
                }
            });
        }
    }

    private static class MerchantEnteredInnerImageInfo implements Parcelable {
        private String mUpdateKey;
        private AppCompatImageView mAppCompatImageView;
        private boolean isSetImage;
        private AppCompatImageView mDeleteAppCompatImageView;
        private String mFilePath;

        private MerchantEnteredInnerImageInfo(String updateKey, AppCompatImageView appCompatImageView, AppCompatImageView deleteAppCompatImageView) {
            this.mUpdateKey = updateKey;
            this.mAppCompatImageView = appCompatImageView;
            this.mDeleteAppCompatImageView = deleteAppCompatImageView;
        }

        private void set(MerchantEnteredInnerImageInfo innerImageInfo) {
            this.mUpdateKey = innerImageInfo.mUpdateKey;
            this.isSetImage = innerImageInfo.isSetImage;
            this.mFilePath = innerImageInfo.mFilePath;
        }

        private MerchantEnteredInnerImageInfo(String updateKey
                , AppCompatImageView appCompatImageView
                , AppCompatImageView deleteAppCompatImageView
                , boolean isSetImage
                , String filePath) {
            this.mUpdateKey = updateKey;
            this.mAppCompatImageView = appCompatImageView;
            this.isSetImage = isSetImage;
            this.mDeleteAppCompatImageView = deleteAppCompatImageView;
            this.mFilePath = filePath;
        }

        private MerchantEnteredInnerImageInfo(Parcel in) {
            mUpdateKey = in.readString();
            isSetImage = in.readByte() != 0;
            mFilePath = in.readString();
        }

        public static final Creator<MerchantEnteredInnerImageInfo> CREATOR = new Creator<MerchantEnteredInnerImageInfo>() {
            @Override
            public MerchantEnteredInnerImageInfo createFromParcel(Parcel in) {
                return new MerchantEnteredInnerImageInfo(in);
            }

            @Override
            public MerchantEnteredInnerImageInfo[] newArray(int size) {
                return new MerchantEnteredInnerImageInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mUpdateKey);
            dest.writeByte((byte) (isSetImage ? 1 : 0));
            dest.writeString(mFilePath);
        }
    }


    /**
     * 设置关键字
     */
    private void initCenterDialogData() {
        if (mCenterDialog == null)
            mCenterDialog = new ContentDialog(mContext, Gravity.CENTER);
        mCenterDialog.setOnConfirmListener(new ContentDialog.IOnConfirmListener() {
            @Override
            public void onConfirmListener(String data) {
                if (data.length() != 0) {
                    mTvMerchantEnteredKeyword.setText(data);
                    if (mStringArrayMap.get("keyword") != null) {
                        mStringArrayMap.remove("keyword");
                    }
                    mStringArrayMap.put("keyword", data);
                } else {
                    ToastUtil.showShort(R.string.merchant_key_word_null);
                    mTvMerchantEnteredKeyword.setText(R.string.merchant_keyword);
                }
            }
        });
    }


    /**
     * 选择照片
     */
    private void initBottomDialogData() {
        if (FileUtils.sdCardExists()) {
            mImageParentPath = new File(Environment.getExternalStorageDirectory(), "merchant");
        } else {
            mImageParentPath = new File(getCacheDir().getAbsolutePath(), "merchant");
        }
        mBottomDialog = new ListViewDialog(mContext);
        mBottomDialog.setInitData(getString(R.string.merchant_dialog_cancel), getResources().getStringArray(R.array.bottom_dialog));
        mBottomDialog.setOnItemClickListener(new ListViewDialog.IOnItemClickListener() {
            @Override
            public void onItemClickListener(int position, View v) {
                File file = new File(mImageParentPath, System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists())
                    if (!file.getParentFile().mkdirs()) {
                        ToastUtil.showShort(R.string.merchant_dialog_folder_create_fail);
                        return;
                    }

                if (position == 0) {
                    if (isScale) {
                        startGetPhoto(true, ONE_PHOTO, file, mWidthScaleSize, mHeightScaleSize);
                    } else {
                        startGetPhoto(true, ONE_PHOTO, file);
                    }
                } else {
                    if (mMultiPhotoCount <= ONE_PHOTO) {
//                        一张
                        if (isScale) {
                            startGetPhoto(false, ONE_PHOTO, file, mWidthScaleSize, mHeightScaleSize);
                        } else {
                            startGetPhoto(false, ONE_PHOTO, file);
                        }
                    } else {
//                        多张
                        if (isScale) {
                            startGetPhoto(false, mMultiPhotoCount, file, mWidthScaleSize, mHeightScaleSize);
                        } else {
                            startGetPhoto(false, mMultiPhotoCount, file);
                        }
                    }
                }
            }
        });
    }

    /**
     * 显示底部弹窗
     *
     * @param imageView key
     * @param width     比例 宽
     * @param height    比例 高
     */
    private void showDialog(boolean isScale, String imageView, int width, int height) {
        this.isScale = isScale;
        if (isScale) {
            configCompress(150 * 1024, DisplayDimensionUtils.getScreenWidth(), DisplayDimensionUtils.getScreenHeight(), true);
        } else {
            configCompress(200 * 1024, DisplayDimensionUtils.getScreenWidth(), DisplayDimensionUtils.getScreenHeight(), true);
        }

        isMerchantPhoto = false;
        isAddMerchantImg = false;
        mMultiPhotoCount = ONE_PHOTO;
        mWidthScaleSize = width;
        mHeightScaleSize = height;
        mCurrentImageView = imageView;
        MerchantEnteredInnerImageInfo imageInfo = fImageViewDelete.get(mCurrentImageView);
        if (imageInfo != null && imageInfo.isSetImage) {
            //展示照片
            displayPhoto(MerchantEnteredActivity.this, imageInfo.mFilePath);
        } else {
            mBottomDialog.showDialog(true, true);
        }
    }

    /**
     * 重置指定的已拍摄照片
     *
     * @param imageView fImageViewDelete 的key
     */
    private void resetImageView(String imageView, boolean delete) {
        MerchantEnteredInnerImageInfo imageInfo = fImageViewDelete.get(imageView);
        if (null == imageInfo) {
            ToastUtil.showShort(R.string.merchant_entered_data_exception);
            return;
        }
        AppCompatImageView compatImageView = imageInfo.mAppCompatImageView;
        if (compatImageView != null && imageInfo.isSetImage) {
            compatImageView.setImageResource(R.drawable.pick_camera);
            if (delete)
                fImageViewDelete.remove(imageInfo.mUpdateKey);
            else {
                mFileArrayMap.put(imageInfo.mUpdateKey, null);
            }
            imageInfo.mDeleteAppCompatImageView.setVisibility(View.GONE);
            imageInfo.isSetImage = false;
        }
    }

    /**
     * 展示照片
     */
    public static void displayPhoto(Activity activity, String... url) {
        ArrayList<String> images = new ArrayList<>(Arrays.asList(url));
        Intent intent = new Intent(activity, ImageScaleActivity.class);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_URLS, images);
        intent.putExtra(ImageScaleActivity.EXTRA_IMAGE_INDEX, 0);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.pv_my_scale_action, R.anim.pv_my_alpha_action);
    }

    /**
     * 显示弹窗 店铺照片专用
     */
    private void showDialogOfMerchantPhoto(View view, boolean isAdd) {
        String tag = (String) view.getTag(TAG);
        this.isScale = true;
        isMerchantPhoto = true;
        mWidthScaleSize = SCALE_WIDTH_SIZE;
        mHeightScaleSize = SCALE_MERCHANT_PHOTO_HEIGHT_SIZE;
        MerchantEnteredInnerImageInfo imageInfo = fImageViewDelete.get(mCurrentImageView = tag);
        if (imageInfo != null && imageInfo.isSetImage) {
            //展示照片
            displayPhoto(MerchantEnteredActivity.this, imageInfo.mFilePath);
        } else {
            configCompress(150 * 1024, DisplayDimensionUtils.getScreenWidth(), DisplayDimensionUtils.getScreenHeight(), true);
            //当前已添加的照片 ：  父布局的子view 数量 - 加号图片
            mMultiPhotoCount = mLlAddMerchantPhoto.getChildCount() - 1;

            //已添加的照片 不能大于 可添加的最大值
            if (mMultiPhotoCount < MAX_SELECT_PHOTO_NUM) {

                // 点击了 add 加号
                if (isAdd) {
                    mCurrentImageView = "";
                    isAddMerchantImg = true;
                    mMultiPhotoCount = MAX_SELECT_PHOTO_NUM - mMultiPhotoCount;
                    mBottomDialog.showDialog(true, true);
                } else if (tag != null && imageInfo != null) {
                    //点击了 相机 照片， 则只选择一张
                    isAddMerchantImg = false;
                    mMultiPhotoCount = ONE_PHOTO;
                    mBottomDialog.showDialog(true, true);
                }
            } else {
                if (tag != null && imageInfo != null) {
                    //点击了 相机 照片， 则只选择一张
                    isAddMerchantImg = false;
                    mMultiPhotoCount = ONE_PHOTO;
                    mBottomDialog.showDialog(true, true);
                } else
                    ToastUtil.showShort(R.string.merchant_photo_max);
            }
        }
    }

    private void getFocus(AppCompatEditText e) {
        e.setFocusable(true);
        e.requestFocus();
        MainActivity.closeIME(true, e);
    }

    private boolean submit() {

        //清除上次的数据
        mStringArrayMap.clear();

        if (mLatitude == null || mLongitude == null) {
            ToastUtil.showLong(R.string.location_error);
            return true;
        }

        if (isOwnerMerchant || mType == TYPE_MERCHANT_ENTRY) {
            String name = mEtMerchantName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                getFocus(mEtMerchantName);
                ToastUtil.showShort(R.string.merchant_name_request);
                return true;
            }
            ToastUtil.showShort(name);


            String phone = mEtMerchantPhone.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                getFocus(mEtMerchantPhone);
                ToastUtil.showShort(R.string.merchant_phone_write);
                return true;
            }

            String legalName = mEtMerchantLegalName.getText().toString().trim();
            if (TextUtils.isEmpty(legalName)) {
                getFocus(mEtMerchantLegalName);
                ToastUtil.showShort(R.string.merchant_legal_name_request);
                return true;
            }

            String id = mEtMerchantLegalId.getText().toString().trim();
            if (TextUtils.isEmpty(id)) {
                getFocus(mEtMerchantLegalId);
                ToastUtil.showShort(R.string.merchant_id_request);
                return true;
            }

            int length = id.length();
            if (length < 15 || (length > 15 && length < 18) || length > 18) {
                getFocus(mEtMerchantLegalId);
                ToastUtil.showShort(R.string.merchant_id_length_error);
                return true;
            }

            Pattern pattern;
            if (length == 15) {
                pattern = Pattern.compile(ConstUtils.REGEX_ID_CARD15);
            } else {
                pattern = Pattern.compile(ConstUtils.REGEX_ID_CARD18);
            }

            if (!pattern.matcher(id).matches()) {
                getFocus(mEtMerchantLegalId);
                ToastUtil.showShort(R.string.merchant_id_format_error);
                return true;
            }

            String card = mEtMerchantLegalBankCard.getText().toString().trim();
            if (TextUtils.isEmpty(card)) {
                getFocus(mEtMerchantLegalBankCard);
                ToastUtil.showShort(R.string.merchant_legal_bank_card_request);
                return true;
            }

            String bankName = mBankName.getText().toString().trim();
            if (TextUtils.isEmpty(bankName)) {
                getFocus(mBankName);
                ToastUtil.showShort(R.string.merchant_legal_bank_name_request);
                return true;
            }

            //商店名称
            mStringArrayMap.put("shop_name", name);
            //电话
            mStringArrayMap.put("shop_tel", phone);
            //法人身份证号
            mStringArrayMap.put("shop_owner_persion_card_no", id);
            //开户
            mStringArrayMap.put("bank_name", bankName);
            //法人名
            mStringArrayMap.put("shop_owner_name", legalName);
            //银行卡
            mStringArrayMap.put("shop_owner_bankcard_no", card);
        }

        String describe = mEtMerchantDescribe.getText().toString().trim();

        if (TextUtils.isEmpty(describe)) {
            getFocus(mEtMerchantDescribe);
            ToastUtil.showShort(R.string.merchant_detail);
            return true;
        }

        String location = mEtMerchantMerchantLocation.getText().toString().trim();

        if (TextUtils.isEmpty(location)) {
            getFocus(mEtMerchantMerchantLocation);
            ToastUtil.showShort(R.string.merchant_merchant_location_request);
            return true;
        }

        if (mType == TYPE_MERCHANT_ENTRY) {

            //判断照片信息是否完整
            Set<String> key = mFileArrayMap.keySet();
            //其他照片出现的次数
            int time = 0;
            //商家店铺照片出现的次数
            int merchantPhoto = 0;
            String value;
            for (String str : key) {
                value = mFileArrayMap.get(str);
                if (value != null) {
                    if (str.contains(KEY_IMG_PREFIX)) {
                        ++merchantPhoto;
                    } else {
                        ++time;
                    }
                }

            }
            String hintInfo = getLackName();

            if (time < 6 || merchantPhoto == 0 || mFileArrayMap.isEmpty()) {

                if (TextUtils.isEmpty(hintInfo)) {
                    ToastUtil.showShort(R.string.merchant_photo_incomplete);
                } else {
                    ToastUtil.showShort(String.format(getString(R.string.merchant_hint_add_img), hintInfo));
                }

                return true;
            }
        }

        if (mType == TYPE_MERCHANT_ENTRY && mEtCommendPerson != null) {
            mStringArrayMap.put("invitecode", mEtCommendPerson.getText().toString());
        }

        //店铺描述
        mStringArrayMap.put("shop_introduce", describe);
        //经度
        mStringArrayMap.put("shop_longitude", mLongitude);
        //纬度
        mStringArrayMap.put("shop_latitude", mLatitude);
        //地址
        mStringArrayMap.put("shop_address", location);

        if (mType == TYPE_MERCHANT_ENTRY) {
            //uid
            mStringArrayMap.put("user", AiSouAppInfoModel.getInstance().getUID());
        } else {
            mStringArrayMap.put("id", mShopId);
        }
        getModifyImage(true);
        ToastUtil.showShort(R.string.merchant_uploading);
        mHintDialog.showLoading(getString(R.string.merchant_upload), false, false);
        return false;
    }

    private String getLackName() {
        String[] temp = {"shop_avatar"
                , "shop_owner_pic"
                , "shop_owner_persion_card_z"
                , "shop_owner_persion_card_f"
                , "shop_owner_backcard_pic"
                , "shop_shop_shoppic"
                , "img1", "img2", "img3", "img4"};
        StringBuilder stringBuilder = new StringBuilder();
        boolean hasImg = false;
        for (String str : temp) {
            if (!mFileArrayMap.containsKey(str)) {
                stringBuilder.append(getLackName(str));
            } else if (null == mFileArrayMap.get(str)) {
                stringBuilder.append(getLackName(str));
            } else if (str.startsWith("img")) {
                hasImg = true;
            }
        }
        String string = stringBuilder.toString();
        if (hasImg) {
            return string.replace(getString(R.string.merchant_merchant_photo), "");
        } else {
            int indexOf = string.indexOf(getString(R.string.merchant_merchant_photo));
            if (indexOf != -1 && string.length() > (indexOf = indexOf + 4)) {
                return string.substring(0, indexOf);
            }
        }
        return string;
    }

    private String getLackName(String key) {
        if (key == null)
            return "";

        switch (key) {
            case "shop_avatar":
                key = getString(R.string.merchant_check_cover);
                break;
            case "shop_owner_pic":
                key = getString(R.string.merchant_check_legal);
                break;
            case "shop_owner_persion_card_z":
                key = getString(R.string.merchant_check_id_positive);
                break;
            case "shop_owner_persion_card_f":
                key = getString(R.string.merchant_check_id_negative);
                break;
            case "shop_owner_backcard_pic":
                key = getString(R.string.merchant_check_card_positive);
                break;
            case "shop_shop_shoppic":
                key = getString(R.string.merchant_check_shop_ic);
                break;
            default:
                key = key.contains(KEY_IMG_PREFIX) ? getString(R.string.merchant_merchant_photo) : "";
                break;
        }

        return key;
    }

    private void getModifyImage(boolean init) {
        File file;
        if (init) {
            String filePath;
            if (mFileList == null) {
                mFileList = new Stack<>();
                mKeySet = new ArrayMap<>(10);
            }
            mFileList.clear();
            mKeySet.clear();

            Set<String> strings = mFileArrayMap.keySet();
            for (String string : strings) {
                filePath = mFileArrayMap.get(string);
                if (filePath != null) {
                        file = new File(filePath);
                        mFileList.addElement(file);
                        mKeySet.put(file, string);
                }
            }
            if (mType == TYPE_MERCHANT_ENTRY && mFileList.size() < 7) {
                ToastUtil.showShort(R.string.merchant_entered_commit_exception);
                return;
            }
        }
        if (!mFileList.empty() && (file = mFileList.peek()) != null) {
            mHttpConnected.startNetworkUploading(ID_UPDATE_IMAGE, ApiConstants.ACTIVITY_MERCHANT_IMG_UPDATE, "file", file);
        } else {
            mHttpConnected.startNetworkGetString(mType == TYPE_MERCHANT_ENTRY ? ID_ENTRY : ID_CERTIFICATION, mStringArrayMap, ApiConstants.ACTIVITY_MERCHANT_ENTRY_MODIFY);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mScaledSize = ViewConfiguration.get(this).getScaledTouchSlop();
        mTextureMapViewMerchantEntered = (TextureMapView) findViewById(R.id.texture_map_view_merchant_entered);
        mAMap = mTextureMapViewMerchantEntered.getMap();
        findViewById(R.id.btn_search_address).setOnClickListener(this);
        mEtMerchantName = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_name)).getEditView();
        mTvMerchantEnteredKeyword = (AppCompatTextView) findViewById(R.id.tv_merchant_entered_keyword);
        final LinearLayoutCompat linearLayout = (LinearLayoutCompat) findViewById(R.id.ll_view_parent_content_merchant_entered_activity);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view_parent_content_merchant_entered_activity);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastY = event.getY();
                        mLastX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float y = Math.abs(event.getY() - mLastY);
                        if (mScaledSize < y && y - Math.abs(event.getX() - mLastX) > 0) {
                            MainActivity.closeIME(false, linearLayout.getFocusedChild());
                        }
                        break;
                }
                return false;
            }
        });

        mEtMerchantPhone = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_phone)).getEditView();

        fImageViewDelete.put(KEY_MERCHANT_COVER, new MerchantEnteredInnerImageInfo(
                "shop_avatar", (AppCompatImageView) findViewById(R.id.iv_select_merchant_cover), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_cover)));

        fImageViewDelete.put(KEY_LEGAL_PHOTO, new MerchantEnteredInnerImageInfo(
                "shop_owner_pic", (AppCompatImageView) findViewById(R.id.iv_select_merchant_legal_photo), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_legal_photo)));

        fImageViewDelete.put(KEY_LEGAL_PHOTO_POSITIVE, new MerchantEnteredInnerImageInfo(
                "shop_owner_persion_card_z", (AppCompatImageView) findViewById(R.id.iv_select_merchant_legal_photo_positive), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_legal_photo_positive)));

        fImageViewDelete.put(KEY_LEGAL_PHOTO_NEGATIVE, new MerchantEnteredInnerImageInfo(
                "shop_owner_persion_card_f", (AppCompatImageView) findViewById(R.id.iv_select_merchant_legal_photo_negative), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_legal_photo_negative)));

        fImageViewDelete.put(KEY_LEGAL_BANK_POSITIVE, new MerchantEnteredInnerImageInfo(
                "shop_owner_backcard_pic", (AppCompatImageView) findViewById(R.id.iv_select_merchant_legal_bank_positive), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_legal_bank_positive)));

        fImageViewDelete.put(KEY_BUSINESS_PHOTO, new MerchantEnteredInnerImageInfo(
                "shop_shop_shoppic", (AppCompatImageView) findViewById(R.id.iv_select_merchant_business_photo), (AppCompatImageView) findViewById(R.id.iv_delete_merchant_business_photo)));

        mEtMerchantDescribe = (AppCompatEditText) findViewById(R.id.et_merchant_describe);
        mEtMerchantLegalName = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_legal_name)).getEditView();
        mEtMerchantLegalId = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_legal_id)).getEditView();
        mEtMerchantLegalId.setKeyListener(DigitsKeyListener.getInstance("0123456789xX"));

        mEtMerchantLegalBankCard = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_legal_bank_card)).getEditView();
        mLlAddMerchantPhoto = (LinearLayoutCompat) findViewById(R.id.ll_add_merchant_photo);
        mHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scroll_view_merchant_entered);
        mBankName = ((MerchantEnteredWriteInfo) findViewById(R.id.et_merchant_legal_bank_name)).getEditView();
        mEtMerchantMerchantLocation = (AppCompatEditText) findViewById(R.id.et_merchant_merchant_location);
        mCbAgreeEntered = (AppCompatCheckBox) findViewById(R.id.cb_agree_entered);
        mBtnCommitMerchantEntered = (AppCompatButton) findViewById(R.id.btn_commit_merchant_entered);
        mEtMerchantMerchantLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    switch (event.getAction()) {
                        case KeyEvent.ACTION_UP:
                            String city = AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCity();
                            city = city == null ? "" : city;
                            if (mGeocodeSearch != null) {
                                if (mGeocodeQuery == null) {
                                    mGeocodeQuery = new GeocodeQuery(mEtMerchantMerchantLocation.getText().toString(), city);
                                } else {
                                    mGeocodeQuery.setCity(city);
                                    mGeocodeQuery.setLocationName(mEtMerchantMerchantLocation.getText().toString());
                                }
                                MainActivity.closeIME(false, v);
                                mGeocodeSearch.getFromLocationNameAsyn(mGeocodeQuery);
                                ToastUtil.showShort(R.string.merchant_find_location);
                            }
                            return true;
                        default:
                            return true;
                    }
                }
                return false;
            }
        });

        Set<String> key = fImageViewDelete.keySet();
        for (String imageView : key) {
            MerchantEnteredInnerImageInfo info = fImageViewDelete.get(imageView);
            info.mAppCompatImageView.setOnClickListener(this);
            info.mDeleteAppCompatImageView.setOnClickListener(this);
        }

        findViewById(R.id.tv_merchant_entered_agreement).setOnClickListener(this);
        mBtnConfigKeyword = (AppCompatButton) findViewById(R.id.btn_set_keyword);
        mBtnConfigKeyword.setOnClickListener(this);
        mBtnCommitMerchantEntered.setOnClickListener(this);

        mIvAddMerchantPhoto = (AppCompatImageView) findViewById(R.id.iv_add_merchant_photo);
        mIvAddMerchantPhoto.setOnClickListener(this);

        mCbAgreeEntered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBtnCommitMerchantEntered.setEnabled(true);
                    mBtnCommitMerchantEntered.setClickable(true);
                } else {
                    mBtnCommitMerchantEntered.setEnabled(false);
                    mBtnCommitMerchantEntered.setClickable(false);
                }
            }
        });
        setTheme();
    }

    private void setTheme() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.CHINA);
        String hour = sdf.format(new Date());
        try {
            int time = Integer.parseInt(hour);
            if (time >= 6 && time <= 18)
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
            else
                mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
