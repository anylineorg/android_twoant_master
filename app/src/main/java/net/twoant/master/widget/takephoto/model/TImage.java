package net.twoant.master.widget.takephoto.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * TakePhoto 操作成功返回的处理结果
 * <p>
 * Author: JPH
 * Date: 2016/8/11 17:01
 */
public class TImage implements Parcelable {
    private String originalPath;
    private String compressPath;
    private FromType fromType;
    private boolean cropped;
    private boolean compressed;

    protected TImage(Parcel in) {
        originalPath = in.readString();
        compressPath = in.readString();
        cropped = in.readByte() != 0;
        compressed = in.readByte() != 0;
    }

    public static final Creator<TImage> CREATOR = new Creator<TImage>() {
        @Override
        public TImage createFromParcel(Parcel in) {
            return new TImage(in);
        }

        @Override
        public TImage[] newArray(int size) {
            return new TImage[size];
        }
    };

    public static TImage of(String path, FromType fromType) {
        return new TImage(path, fromType);
    }

    public static TImage of(Uri uri, FromType fromType) {
        return new TImage(uri, fromType);
    }

    private TImage(String path, FromType fromType) {
        this.originalPath = path;
        this.fromType = fromType;
    }

    private TImage(Uri uri, FromType fromType) {
        this.originalPath = uri.getPath();
        this.fromType = fromType;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public FromType getFromType() {
        return fromType;
    }

    public void setFromType(FromType fromType) {
        this.fromType = fromType;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalPath);
        dest.writeString(compressPath);
        dest.writeByte((byte) (cropped ? 1 : 0));
        dest.writeByte((byte) (compressed ? 1 : 0));
    }

    public enum FromType {
        CAMERA, OTHER
    }
}
