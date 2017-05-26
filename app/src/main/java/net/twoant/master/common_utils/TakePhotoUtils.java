package net.twoant.master.common_utils;

import net.twoant.master.widget.takephoto.app.TakePhoto;
import net.twoant.master.widget.takephoto.compress.CompressConfig;
import net.twoant.master.widget.takephoto.model.CropOptions;
import net.twoant.master.widget.takephoto.model.LubanOptions;

/**
 * Created by S_Y_H on 2016/11/26.
 * TakePhoto的工具类
 */

public class TakePhotoUtils {

    /**
     * 初始化 裁剪
     *
     * @param width        宽
     * @param height       高
     * @param isScale      true 是高宽比，false 精确的高宽值
     * @param useInnerCrop true使用自带的裁剪工具
     */
    public static CropOptions initCropOptions(int width, int height, boolean isScale, boolean useInnerCrop) {
        CropOptions.Builder builder = new CropOptions.Builder();
        if (isScale) {
            //设置比例
            builder.setAspectX(width).setAspectY(height);
        } else {
            //设置大小
            builder.setOutputX(width).setOutputY(height);
        }
        //使用自带裁剪工具
        builder.setWithOwnCrop(useInnerCrop);
        return builder.create();
    }

    /**
     * 配置压缩选项
     *
     * @param takePhoto         实例
     * @param isCompress        true启用压缩
     * @param maxSize           最大文件大小 压缩到的最大大小，单位B
     * @param width             图片的宽度
     * @param height            图片的高度
     * @param isShowProgressbar 显示压缩进度弹窗
     * @param useSelfCompress   使用自带压缩
     */
    public static void configCompress(TakePhoto takePhoto, boolean isCompress, int maxSize, int width, int height
            , boolean isShowProgressbar, boolean useSelfCompress) {

        if (!isCompress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }

        CompressConfig config;

        if (useSelfCompress) {
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(true)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
        }
        takePhoto.onEnableCompress(config, isShowProgressbar);
    }


}
