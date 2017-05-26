package net.twoant.master.widget.takephoto.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 裁剪配置类
 * Author: JPH
 * Date: 2016/7/27 13:19
 */
public class CropOptions implements Parcelable {
    /**使用TakePhoto自带的裁切工具进行裁切*/
    private boolean withOwnCrop;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;
    private CropOptions(){}

    protected CropOptions(Parcel in) {
        withOwnCrop = in.readByte() != 0;
        aspectX = in.readInt();
        aspectY = in.readInt();
        outputX = in.readInt();
        outputY = in.readInt();
    }

    public static final Creator<CropOptions> CREATOR = new Creator<CropOptions>() {
        @Override
        public CropOptions createFromParcel(Parcel in) {
            return new CropOptions(in);
        }

        @Override
        public CropOptions[] newArray(int size) {
            return new CropOptions[size];
        }
    };

    public int getAspectX() {
        return aspectX;
    }

    public void setAspectX(int aspectX) {
        this.aspectX = aspectX;
    }

    public int getAspectY() {
        return aspectY;
    }

    public void setAspectY(int aspectY) {
        this.aspectY = aspectY;
    }

    public int getOutputX() {
        return outputX;
    }

    public void setOutputX(int outputX) {
        this.outputX = outputX;
    }

    public int getOutputY() {
        return outputY;
    }

    public void setOutputY(int outputY) {
        this.outputY = outputY;
    }

    public boolean isWithOwnCrop() {
        return withOwnCrop;
    }

    public void setWithOwnCrop(boolean withOwnCrop) {
        this.withOwnCrop = withOwnCrop;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (withOwnCrop ? 1 : 0));
        dest.writeInt(aspectX);
        dest.writeInt(aspectY);
        dest.writeInt(outputX);
        dest.writeInt(outputY);
    }

    public static class Builder implements Parcelable {
        private CropOptions options;

        public Builder() {
            options=new CropOptions();
        }

        protected Builder(Parcel in) {
            options = in.readParcelable(CropOptions.class.getClassLoader());
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

        public Builder setAspectX(int aspectX) {
            options.setAspectX(aspectX);
            return this;
        }

        public Builder setAspectY(int aspectY) {
            options.setAspectY(aspectY);
            return this;
        }

        public Builder setOutputX(int outputX) {
            options.setOutputX(outputX);
            return this;
        }

        public Builder setOutputY(int outputY) {
            options.setOutputY(outputY);
            return this;
        }

        public Builder setWithOwnCrop(boolean withOwnCrop) {
            options.setWithOwnCrop(withOwnCrop);
            return this;
        }
        public CropOptions create(){
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
