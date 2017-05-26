package net.twoant.master.widget.takephoto.compress;


import android.os.Parcel;
import android.os.Parcelable;

import net.twoant.master.widget.takephoto.model.LubanOptions;

/**
 * 压缩配置类
 * Author: JPH
 * Date: 2016/6/7 0007 18:01
 */
public class CompressConfig implements Parcelable {

    /**
     * 长或宽不超过的最大像素,单位px
     */
    private int maxPixel = 1200;
    /**
     * 压缩到的最大大小，单位B
     */
    private int maxSize = 100 * 1024;

    /**
     * 是否启用像素压缩
     */
    private boolean enablePixelCompress = true;
    /**
     * 是否启用质量压缩
     */
    private boolean enableQualityCompress = true;

    /**
     * 是否保留原文件
     */
    private boolean enableReserveRaw = true;

    /**
     * Luban压缩配置
     */
    private LubanOptions lubanOptions;

    protected CompressConfig(Parcel in) {
        maxPixel = in.readInt();
        maxSize = in.readInt();
        enablePixelCompress = in.readByte() != 0;
        enableQualityCompress = in.readByte() != 0;
        enableReserveRaw = in.readByte() != 0;
    }

    public static final Creator<CompressConfig> CREATOR = new Creator<CompressConfig>() {
        @Override
        public CompressConfig createFromParcel(Parcel in) {
            return new CompressConfig(in);
        }

        @Override
        public CompressConfig[] newArray(int size) {
            return new CompressConfig[size];
        }
    };

    public static CompressConfig ofDefaultConfig() {
        return new CompressConfig();
    }

    public static CompressConfig ofLuban(LubanOptions options) {
        return new CompressConfig(options);
    }

    private CompressConfig() {
    }

    private CompressConfig(LubanOptions options) {
        this.lubanOptions = options;
    }

    public LubanOptions getLubanOptions() {
        return lubanOptions;
    }

    public int getMaxPixel() {
        return maxPixel;
    }

    public CompressConfig setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isEnablePixelCompress() {
        return enablePixelCompress;
    }

    public void enablePixelCompress(boolean enablePixelCompress) {
        this.enablePixelCompress = enablePixelCompress;
    }

    public boolean isEnableQualityCompress() {
        return enableQualityCompress;
    }

    public void enableQualityCompress(boolean enableQualityCompress) {
        this.enableQualityCompress = enableQualityCompress;
    }

    public boolean isEnableReserveRaw() {
        return enableReserveRaw;
    }

    public void enableReserveRaw(boolean enableReserveRaw) {
        this.enableReserveRaw = enableReserveRaw;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxPixel);
        dest.writeInt(maxSize);
        dest.writeByte((byte) (enablePixelCompress ? 1 : 0));
        dest.writeByte((byte) (enableQualityCompress ? 1 : 0));
        dest.writeByte((byte) (enableReserveRaw ? 1 : 0));
    }

    public static class Builder implements Parcelable {
        private CompressConfig config;

        public Builder() {
            config = new CompressConfig();
        }

        protected Builder(Parcel in) {
            config = in.readParcelable(CompressConfig.class.getClassLoader());
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder setMaxSize(int maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }

        public Builder setMaxPixel(int maxPixel) {
            config.setMaxPixel(maxPixel);
            return this;
        }

        public Builder enablePixelCompress(boolean enablePixelCompress) {
            config.enablePixelCompress(enablePixelCompress);
            return this;
        }

        public Builder enableQualityCompress(boolean enableQualityCompress) {
            config.enableQualityCompress(enableQualityCompress);
            return this;
        }

        public Builder enableReserveRaw(boolean enableReserveRaw) {
            config.enableReserveRaw(enableReserveRaw);
            return this;
        }

        public CompressConfig create() {
            return config;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(config, flags);
        }
    }
}

