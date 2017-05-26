package net.twoant.master.base_app;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.twoant.master.api.ApiConstants;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.TakePhotoUtils;
import net.twoant.master.widget.StatusBarCompat;
import net.twoant.master.widget.takephoto.app.TakePhoto;
import net.twoant.master.widget.takephoto.app.TakePhotoActivity;
import net.twoant.master.widget.takephoto.app.TakePhotoImpl;
import net.twoant.master.widget.takephoto.model.InvokeParam;
import net.twoant.master.widget.takephoto.model.TContextWrap;
import net.twoant.master.widget.takephoto.model.TResult;
import net.twoant.master.widget.takephoto.permission.InvokeListener;
import net.twoant.master.widget.takephoto.permission.PermissionManager;
import net.twoant.master.widget.takephoto.permission.TakePhotoInvocationHandler;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Map;

/**
 * Created by DZY on 2017/3/10.
 * 佛祖保佑   永无BUG
 */

public abstract class BaseBaseFragment extends Fragment implements TakePhoto.TakeResultListener, InvokeListener {

    private final static int ONE_PHOTO = 1;
    private static final String TAG = TakePhotoActivity.class.getName();
    private TakePhoto mTakePhoto;
    private InvokeParam invokeParam;
    /**
     * 配置了 压缩
     */
    private boolean isConfigCompress;

    /**
     * 上下文
     */
    protected Context mContext;

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, this);
    }


    /**
     * 获取TakePhoto实例
     *
     * @return 实例
     */
    public TakePhoto getTakePhoto() {
        if (mTakePhoto == null) {
            mTakePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return mTakePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {

    }

    @Override
    public void takeFail(TResult result, String msg) {
        LogUtils.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        LogUtils.i(TAG, getResources().getString(net.twoant.master.R.string.msg_operation_canceled));
    }

    /**
     * 开始 获取图片，并进行裁剪，裁剪使用 比例 来限制宽高
     *
     * @param isTakePhoto      是否启动相机拍摄图片
     * @param selectPhotoCount 选择图片的数量，最小 一 张
     * @param file             是照片存储的位置！！！（要把照片存到哪里）
     * @param width            裁剪的“比例”宽度
     * @param height           裁剪的“比例”高度
     */
    protected void startGetPhoto(boolean isTakePhoto, int selectPhotoCount, File file, int width, int height) {
        this.startGetPhoto(isTakePhoto, selectPhotoCount, file, width, height, true);
    }

    /**
     * 开始 获取图片，并进行裁剪
     *
     * @param isTakePhoto      是否启动相机拍摄图片
     * @param selectPhotoCount 选择图片的数量，最小 一 张
     * @param file             是照片存储的位置！！！（要把照片存到哪里）
     * @param width            裁剪的宽度
     * @param height           裁剪的高度
     * @param isScale          是否是进行 比例 裁剪，若为false，则为精确裁剪
     */
    protected void startGetPhoto(boolean isTakePhoto, int selectPhotoCount, File file, int width, int height, boolean isScale) {

        cleanState();

        if (isTakePhoto)
            getTakePhoto().onPickFromCaptureWithCrop(Uri.fromFile(file), TakePhotoUtils.initCropOptions(width, height, isScale, true));
        else {
            if (selectPhotoCount <= ONE_PHOTO) {
//              一张
                getTakePhoto().onPickMultipleWithCrop(ONE_PHOTO, TakePhotoUtils.initCropOptions(width, height, isScale, true));
            } else {
//               多张
                getTakePhoto().onPickMultipleWithCrop(selectPhotoCount, TakePhotoUtils.initCropOptions(width, height, isScale, true));
            }
        }
    }

    /**
     * 配置 图片压缩选项， 如果需要配置压缩，在每次调用以下方法前， 都应该调用该方法
     * {@link #startGetPhoto(boolean, int, File, int, int, boolean)}
     * {@link #startGetPhoto(boolean, int, File)}
     * {@link #startGetPhoto(boolean, int, File, int, int)}
     *
     * @param maxSize           压缩到的最大大小，单位B
     * @param width             宽不超过的最大像素,单位px
     * @param height            宽不超过的最大像素,单位px
     * @param isShowProgressbar 是否显示压缩 进度弹窗
     * @param useSelfCompress   true使用框架自带的压缩， false 使用Luban 压缩
     */
    protected void configCompress(int maxSize, int width, int height, boolean isShowProgressbar, boolean useSelfCompress) {
        TakePhotoUtils.configCompress(getTakePhoto(), true, maxSize, width, height, isShowProgressbar, useSelfCompress);
        isConfigCompress = true;
    }


    /**
     * 配置 图片压缩选项， 如果需要配置压缩，在每次调用以下方法前， 都应该调用该方法
     * {@link #startGetPhoto(boolean, int, File, int, int, boolean)}
     * {@link #startGetPhoto(boolean, int, File)}
     * {@link #startGetPhoto(boolean, int, File, int, int)}
     *
     * @param maxSize           压缩到的最大大小，单位B
     * @param width             宽不超过的最大像素,单位px
     * @param height            宽不超过的最大像素,单位px
     * @param isShowProgressbar 是否显示压缩 进度弹窗
     */
    protected void configCompress(int maxSize, int width, int height, boolean isShowProgressbar) {
        configCompress(maxSize, width, height, isShowProgressbar, true);
    }

    /**
     * 配置图片压缩
     * 配置 图片压缩选项， 如果需要配置压缩，在每次调用以下方法前， 都应该调用该方法
     * {@link #startGetPhoto(boolean, int, File, int, int, boolean)}
     * {@link #startGetPhoto(boolean, int, File)}
     * {@link #startGetPhoto(boolean, int, File, int, int)}
     *
     * @param maxSize 压缩到的最大大小，单位B
     * @param size    长或宽不超过的最大像素,单位px
     */
    protected void configCompress(int maxSize, int size) {
        configCompress(maxSize, size, size, false);
    }


    /**
     * 获取图片，不裁剪
     *
     * @param isTakePhoto      是否启动相机拍摄照片
     * @param selectPhotoCount 选择图片的数量，最小 一 张
     * @param file             是照片存储的位置！！！（要把照片存到哪里）
     */
    protected void startGetPhoto(boolean isTakePhoto, int selectPhotoCount, File file) {

        cleanState();

        if (isTakePhoto)
            getTakePhoto().onPickFromCapture(Uri.fromFile(file));
        else {
            if (selectPhotoCount <= ONE_PHOTO) {
//              一张
                getTakePhoto().onPickMultiple(ONE_PHOTO);
            } else {
//               多张
                getTakePhoto().onPickMultiple(selectPhotoCount);
            }
        }
    }

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        else {
            this.startActivity(intent);
            getActivity().overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void setStatusBarColor(@ColorRes int colorRes) {
        StatusBarCompat.setStatusBarColor(getActivity(), ContextCompat.getColor(getActivity(), colorRes));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseConfig.removeStack(getActivity());
        mTakePhoto = null;
        invokeParam = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 该方法在setContentView 之前调用
     * 设置layout前的配置
     */
    private void doBeforeSetContentView() {
        // 默认着色状态栏
        setStatusBarColor(BaseConfig.getDefStateBarColor());
//        BaseConfig.onCreate((AppCompatActivity) getActivity());
    }


    private void cleanState() {
        if (!isConfigCompress) {
            getTakePhoto().onEnableCompress(null, false);
        } else {
            isConfigCompress = false;
        }
    }


    /**
     * @return 子类的资源布局id
     */
    protected abstract
    @LayoutRes
    int getLayoutRes();

    /**
     * 子类 实现该方法，无需在覆写 onViewCreate（）和onViewCreated（）两个方法，
     *
     * @param view getLayoutRes() 初始化后的view
     */
    protected abstract void onViewCreate(View view, @Nullable Bundle savedInstanceState);

    /**
     * 该方法 子类不应该再覆写
     */
    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        doBeforeSetContentView();
        mContext = getActivity();
        getTakePhoto().onCreate(savedInstanceState);
        return inflater.inflate(getLayoutRes(), container, false);
    }



    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewCreate(view, savedInstanceState);
    }

    public void LongHttp(int id, String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().id(id).url(ApiConstants.GUO + name).params(map).build().execute(callback);
    }
    public void LongHttp(String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(name).params(map).build().execute(callback);
    }
    public void LongHttp(String url, String name, Map<String,String> map, StringCallback callback){
        map.put("_t", AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken());
        map.put("_cc", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode());
        map.put("_ac", AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode());
        OkHttpUtils.post().url(url).params(map).build().execute(callback);
    }
    public void LongHttpGet(String url, StringCallback callback){
        url = url+"&_t="+ AiSouAppInfoModel.getInstance().getAiSouUserBean().getLoginToken()+"&_cc="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentCityCode()+"&_ac="+AiSouAppInfoModel.getInstance().getAiSouLocationBean().getCurrentAddressCode();
        OkHttpUtils.get().url(url).build().execute(callback);
    }
}
