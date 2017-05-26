package net.twoant.master.widget.takephoto.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Luban配置类
 * Author: crazycodeboy
 * Date: 2016/11/5 0007 20:10
 * Version:4.0.1
 * 技术博文：http://www.devio.org/
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class LubanOptions implements Parcelable {
    /**
     * 压缩到的最大大小，单位B
     */
    private int maxSize;
    private int maxHeight;
    private int maxWidth;

    private LubanOptions() {
    }

    protected LubanOptions(Parcel in) {
        maxSize = in.readInt();
        maxHeight = in.readInt();
        maxWidth = in.readInt();
    }

    public static final Creator<LubanOptions> CREATOR = new Creator<LubanOptions>() {
        @Override
        public LubanOptions createFromParcel(Parcel in) {
            return new LubanOptions(in);
        }

        @Override
        public LubanOptions[] newArray(int size) {
            return new LubanOptions[size];
        }
    };

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(maxSize);
        dest.writeInt(maxHeight);
        dest.writeInt(maxWidth);
    }

    public static class Builder implements Parcelable {
        private LubanOptions options;

        public Builder() {
            options = new LubanOptions();
        }

        protected Builder(Parcel in) {
            options = in.readParcelable(LubanOptions.class.getClassLoader());
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
            options.setMaxSize(maxSize);
            return this;
        }

        public Builder setMaxHeight(int maxHeight) {
            options.setMaxHeight(maxHeight);
            return this;
        }

        public Builder setMaxWidth(int maxWidth) {
            options.setMaxWidth(maxWidth);
            return this;
        }

        public LubanOptions create() {
            return options;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(options, flags);
        }
    }
}
