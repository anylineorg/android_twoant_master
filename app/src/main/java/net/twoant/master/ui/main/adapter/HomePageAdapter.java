package net.twoant.master.ui.main.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.app.GDLocationHelper;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerNetworkAdapter;
import net.twoant.master.ui.main.adapter.base.BaseRecyclerViewHolder;
import net.twoant.master.ui.main.adapter.control.ActivityRecyclerControl;
import net.twoant.master.ui.main.adapter.control.GoodsRecyclerControl;
import net.twoant.master.ui.main.adapter.control.MerchantRecyclerControl;
import net.twoant.master.ui.main.adapter.control.SearchConventionControl;
import net.twoant.master.ui.main.interfaces.IRecyclerController;
import net.twoant.master.ui.main.interfaces.IRecyclerViewConstant;
import net.twoant.master.ui.main.presenter.HomePagerHttpControl;

/**
 * Created by S_Y_H on 2017/2/27.
 * 首页的适配器
 */

public class HomePageAdapter extends BaseRecyclerNetworkAdapter {

    /**
     * 关键字
     */
    private String mKeyword;

    private boolean isSkipCheckLocation;

    public HomePageAdapter(Activity activity, int type, int category, @Nullable String keyword,
                           IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> isRecycler, boolean skip) {
        super(activity, isRecycler);
        this.mCategory = category;
        this.mType = type;
        isSkipCheckLocation = skip;
        this.iRecyclerController = isRecycler;

        if (keyword != null)
            iRecyclerController.refreshData(keyword);
    }

    public HomePageAdapter(Activity activity, int type, int category, @Nullable String keyword, IRecyclerController<BaseRecyclerNetworkAdapter, BaseRecyclerViewHolder> isRecycler) {
        super(activity, isRecycler);
        this.mCategory = category;
        this.mType = type;
        isSkipCheckLocation = false;
        this.iRecyclerController = isRecycler;

        if (keyword != null)
            iRecyclerController.refreshData(keyword);
    }
    public HomePageAdapter(Activity activity, int type, int category, @Nullable String keyword) {
        super(activity, null);
        this.mCategory = category;
        this.mType = type;
        this.isSkipCheckLocation = false;

        initRecyclerController();

        if (keyword != null)
            iRecyclerController.refreshData(keyword);
    }
    public HomePageAdapter(Activity activity, int type, int category, @Nullable String keyword,int state) {
        super(activity, null);
        this.mCategory = category;
        this.mType = type;
        this.isSkipCheckLocation = false;
        this.mStateCode=state;
        initRecyclerController();

        if (keyword != null)
            iRecyclerController.refreshData(keyword);
    }

    public String getKeyword() {
        return this.mKeyword;
    }

    /**
     * @param type     类型
     * @param category 分类
     * @param keyword  关键字
     */
    public void setRecyclerController(int type, int category, @Nullable String keyword) {
        this.mCategory = category;

        if (mHomePagerHttpControl == null) {
            mHomePagerHttpControl = HomePagerHttpControl.getInstance();
        }
        mHomePagerHttpControl.cancelRequest(this);

        if (type != mType) {
            mType = type;
            initRecyclerController();
        }

        if (keyword != null) {
            this.mKeyword = keyword;
            iRecyclerController.refreshData(keyword);
            mConfigNetwork = false;
            isRefresh = true;
        }

        this.notifyDataSetChanged();
    }

    @Override
    protected boolean canLoadingDataFormNetwork() {
        if (isSkipCheckLocation) {
            return true;
        }
        if (AiSouAppInfoModel.getInstance().getAiSouLocationBean().getLocationSuccessfulState() || AiSouAppInfoModel.getInstance().getAiSouLocationBean().isUserSelectLocation()) {
            return true;
        } else if (0 == GDLocationHelper.getLocationCount()) {
            showLoadingHint(true, "获取位置中…");
            GDLocationHelper.getInstance().getOnceLocation(iOnLocationListener);
            return false;
        } else {
            GDLocationHelper.getInstance().getOnceLocation(iOnLocationListener);
            showLoadingHint(false, "获取位置失败");
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GDLocationHelper.getInstance().removeLocationListener(iOnLocationListener);
    }

    private void initRecyclerController() {
        if (null != iRecyclerController) {
            this.iRecyclerController.bindingAdapter(null);
        }
        switch (mType) {
            case IRecyclerViewConstant.TYPE_ACTIVITY:
                this.iRecyclerController = new ActivityRecyclerControl();
                break;

            case IRecyclerViewConstant.TYPE_MERCHANT_LIST_NEW:
                this.iRecyclerController = new MerchantRecyclerControl();
                break;

            case IRecyclerViewConstant.TYPE_FRIEND:

                break;

            case IRecyclerViewConstant.TYPE_MERCHANT_GOODS_LIST_NEW:
                this.iRecyclerController = new GoodsRecyclerControl(mStateCode);
                break;

            case IRecyclerViewConstant.TYPE_MESSAGE:
                this.iRecyclerController = new SearchConventionControl();
                break;
            default:
                throw new IllegalArgumentException("无法找到 type");
        }

        if (null != iRecyclerController) {
            this.iRecyclerController.bindingAdapter(this);
        }
    }

    private GDLocationHelper.IOnLocationListener iOnLocationListener = new GDLocationHelper.IOnLocationListener() {
        @Override
        public void onLocationListener(boolean isSuccessful, String description, double latitude, double longitude, AMapLocation aMapLocation) {
            if (isSuccessful) {
                refreshData();
            } else {
                showLoadingHint(false, "获取位置失败");
            }
        }
    };


}
