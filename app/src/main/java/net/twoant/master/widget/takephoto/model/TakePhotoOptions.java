package net.twoant.master.widget.takephoto.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: crazycodeboy
 * Date: 2016/11/5 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.devio.org/
 * GitHub:https://github.com/crazycodeboy
 * Eamil:crazycodeboy@gmail.com
 */
public class TakePhotoOptions implements Parcelable {
    /**
     * 是否使用TakePhoto自带的相册进行图片选择，默认不使用，但选择多张图片会使用
     */
    private boolean withOwnGallery;
    /**
     * 是对拍的照片进行旋转角度纠正
     */
    private boolean correctImage;

    private TakePhotoOptions() {
    }

    protected TakePhotoOptions(Parcel in) {
        withOwnGallery = in.readByte() != 0;
        correctImage = in.readByte() != 0;
    }

    public static final Creator<TakePhotoOptions> CREATOR = new Creator<TakePhotoOptions>() {
        @Override
        public TakePhotoOptions createFromParcel(Parcel in) {
            return new TakePhotoOptions(in);
        }

        @Override
        public TakePhotoOptions[] newArray(int size) {
            return new TakePhotoOptions[size];
        }
    };

    public boolean isWithOwnGallery() {
        return withOwnGallery;
    }

    public void setWithOwnGallery(boolean withOwnGallery) {
        this.withOwnGallery = withOwnGallery;
    }

    public boolean isCorrectImage() {
        return correctImage;
    }

    public void setCorrectImage(boolean correctImage) {
        this.correctImage = correctImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (withOwnGallery ? 1 : 0));
        dest.writeByte((byte) (correctImage ? 1 : 0));
    }

    public static class Builder implements Parcelable {
        private TakePhotoOptions options;

        public Builder() {
            this.options = new TakePhotoOptions();
        }

        protected Builder(Parcel in) {
            options = in.readParcelable(TakePhotoOptions.class.getClassLoader());
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

        public Builder setWithOwnGallery(boolean withOwnGallery) {
            options.setWithOwnGallery(withOwnGallery);
            return this;
        }

        public Builder setCorrectImage(boolean isCorrectImage) {
            options.setCorrectImage(isCorrectImage);
            return this;
        }

        public TakePhotoOptions create() {
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
