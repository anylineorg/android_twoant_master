package net.twoant.master.ui.my_center.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

import net.twoant.master.base_app.BaseActivity;
import net.twoant.master.base_app.ChatBaseActivity;
import net.twoant.master.common_utils.FileUtils;
import net.twoant.master.common_utils.ImageLoader;
import net.twoant.master.common_utils.ToastUtil;
import net.twoant.master.ui.chat.config.UserInfoDiskHelper;
import net.twoant.master.ui.chat.widget.CustomCheckBox;

import java.io.File;

/**
 * Created by S_Y_H on 2017/3/31.
 * 缓存设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private AppCompatTextView mTvCacheSize;
    private AppCompatCheckBox mCbShowImg;

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, SettingActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return net.twoant.master.R.layout.yh_activity_setting;
    }

    @Override
    protected void subOnCreate(Bundle savedInstanceState) {
        initView();
        initCacheData();
    }

    /**
     * 初始化 缓存数据
     */
    private void initCacheData() {
        File cacheFile = ImageLoader.getCacheFile(this);
        String cacheSize = null;
        if (null != cacheFile) {
            if (cacheFile.isDirectory()) {
                long temp = 0;
                File[] files = cacheFile.listFiles();
                for (File f : files) {
                    temp += f.length();
                }
                cacheSize = Formatter.formatFileSize(this, temp);
            } else {
                cacheSize = Formatter.formatFileSize(this, cacheFile.length());
            }
        }
        mTvCacheSize.setText(String.format(getString(net.twoant.master.R.string.setting_clean_cache), TextUtils.isEmpty(cacheSize) ? "0k" : cacheSize));
    }

    private void initView() {
        ChatBaseActivity.initSimpleToolbarData(this, getString(net.twoant.master.R.string.setting_clean_title), this);
        CustomCheckBox customCheckBox = (CustomCheckBox) findViewById(net.twoant.master.R.id.cb_switch_show_img);
        customCheckBox.setOnClickListener(this);
        mCbShowImg = customCheckBox.getCheckBox();
        mCbShowImg.setChecked(ImageLoader.STATE_LOADING_IMG == ImageLoader.getIsLoadingImgState());
        mTvCacheSize = (AppCompatTextView) findViewById(net.twoant.master.R.id.tv_cache_size);
        findViewById(net.twoant.master.R.id.btn_clean_cache).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case net.twoant.master.R.id.btn_clean_cache:
                new AlertDialog.Builder(SettingActivity.this, net.twoant.master.R.style.AlertDialogStyle)
                        .setMessage(net.twoant.master.R.string.setting_clean_hint)
                        .setPositiveButton(net.twoant.master.R.string.merchant_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(net.twoant.master.R.string.merchant_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanCacheData();
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                break;
            case net.twoant.master.R.id.cb_switch_show_img:
                if (mCbShowImg.isChecked()) {
                    initHintDialog();
                } else {
                    mCbShowImg.setChecked(true);
                    ImageLoader.setIsLoadingImgState(ImageLoader.STATE_LOADING_IMG);
                }
                break;
            default:
                SettingActivity.this.finish();
                break;
        }
    }

    private void cleanCacheData() {
        final File cacheFile = ImageLoader.getCacheFile(SettingActivity.this);
        if (null != cacheFile) {
            ToastUtil.showShort(net.twoant.master.R.string.setting_cleaning_cache);
            UserInfoDiskHelper.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (cacheFile.isDirectory()) {
                        FileUtils.deleteDir(cacheFile);
                    } else {
                        FileUtils.deleteFile(cacheFile);
                    }

                    if (!SettingActivity.this.isFinishing()) {
                        SettingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initCacheData();
                                ToastUtil.showShort(net.twoant.master.R.string.setting_clean_successful);
                            }
                        });
                    }
                }
            });
        } else {
            ToastUtil.showShort(net.twoant.master.R.string.setting_clean_fail);
        }
    }


    private void initHintDialog() {
        new AlertDialog.Builder(SettingActivity.this, net.twoant.master.R.style.AlertDialogStyle)
                .setTitle(net.twoant.master.R.string.setting_close_img_hint_title)
                .setMessage(net.twoant.master.R.string.setting_close_img_hint_msg)
                .setNegativeButton(net.twoant.master.R.string.setting_close_img_mobile, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCbShowImg.setChecked(false);
                        ImageLoader.setIsLoadingImgState(ImageLoader.STATE_CLOSE_AT_MOBILE);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(net.twoant.master.R.string.setting_close_img_all, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCbShowImg.setChecked(false);
                        ImageLoader.setIsLoadingImgState(ImageLoader.STATE_CLOSE_AT_ALL);
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
