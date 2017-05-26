package net.twoant.master.base_app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

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

import java.io.File;

/**
 * Created by S_Y_H on 2016/12/3.
 * 照片选择、裁剪 基类
 */

public abstract class TakePhotoBaseActivity extends AppCompatActivity implements TakePhoto.TakeResultListener, InvokeListener {

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
    protected Activity mContext;

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
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
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

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        setContentView(getLayoutId());
        mContext = this;
        getTakePhoto().onCreate(savedInstanceState);
        this.subOnCreate(savedInstanceState);
    }

    /**
     * 获取布局文件的资源layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        else {
            this.startActivity(intent);
            this.overridePendingTransition(net.twoant.master.R.anim.fade_in, net.twoant.master.R.anim.fade_out);
        }
    }


    /**
     * 子类实现方法，实现方法后，子类无需再重写 OnCreate（Bundle savedInstanceState）方法;
     */
    protected abstract void subOnCreate(Bundle savedInstanceState);

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void setStatusBarColor(@ColorRes int colorRes) {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, colorRes));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseConfig.onRestart(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseConfig.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BaseConfig.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseConfig.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseConfig.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseConfig.removeStack(this);
        mTakePhoto = null;
        invokeParam = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        BaseConfig.onCreate(this);
    }


    private void cleanState() {
        if (!isConfigCompress) {
            getTakePhoto().onEnableCompress(null, false);
        } else {
            isConfigCompress = false;
        }
    }
}
