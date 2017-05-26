package net.twoant.master.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.twoant.master.ui.main.model.PayOrderTextWatcher;
import net.twoant.master.ui.my_center.bean.GoodsItem;

/**
 * Created by S_Y_H on 2017/3/30.
 * 商品列表的 数量bean
 */

public final class GoodsItemBean extends GoodsItem implements Parcelable {


    private String mGoodsId;
    /**
     * 商品名
     */
    private String mGoodsName;

    /**
     * 商品的数量
     */
    private int mGoodsCount;

    /**
     * 商品的总价格(数量 * 单价)
     */
    private float mGoodsPrice;

    /**
     * 商品单价
     */
    private float mGoodsUnitPrice;

    /**
     * 商品展图
     */
    private String mGoodsAvatar;

    public GoodsItemBean() {}

    public GoodsItemBean(String goodsId, String goodsName, String avatar, String unitPrice) {
        this.mGoodsId = goodsId;
        this.mGoodsName = goodsName;
        this.mGoodsAvatar = avatar;
        this.mGoodsUnitPrice = Float.valueOf(PayOrderTextWatcher.checkInputNumber(unitPrice, true));
    }

    public GoodsItemBean(@NonNull GoodsItemBean goodsItemBean) {
        this.mGoodsCount = 0;
        this.mGoodsPrice = 0;
        this.mGoodsId = goodsItemBean.getGoodsId();
        this.mGoodsName = goodsItemBean.getGoodsName();
        this.mGoodsAvatar = goodsItemBean.getGoodsAvatar();
        this.mGoodsUnitPrice = goodsItemBean.getGoodsUnitPrice();
    }

    private GoodsItemBean(Parcel in) {
        mGoodsId = in.readString();
        mGoodsName = in.readString();
        mGoodsCount = in.readInt();
        mGoodsPrice = in.readFloat();
        mGoodsUnitPrice = in.readFloat();
        mGoodsAvatar = in.readString();
    }

    public static final Creator<GoodsItemBean> CREATOR = new Creator<GoodsItemBean>() {
        @Override
        public GoodsItemBean createFromParcel(Parcel in) {
            return new GoodsItemBean(in);
        }

        @Override
        public GoodsItemBean[] newArray(int size) {
            return new GoodsItemBean[size];
        }
    };

    public float getGoodsUnitPrice() {
        return mGoodsUnitPrice;
    }

    public void setGoodsUnitPrice(float mGoodsUnitPrice) {
        this.mGoodsUnitPrice = mGoodsUnitPrice;
    }

    public float getGoodsPrice() {
        return mGoodsPrice;
    }

    public void setGoodsPrice(float mGoodsPrice) {
        this.mGoodsPrice = mGoodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        try {
            this.mGoodsPrice = Float.valueOf(goodsPrice);
        } catch (Exception e) {
            //
        }
    }

    public String getGoodsAvatar() {
        return this.mGoodsAvatar;
    }

    public void setGoodsAvatar(String avatar) {
        this.mGoodsAvatar = avatar;
    }

    public String getGoodsId() {
        return mGoodsId;
    }

    public void setGoodsId(String mGoodsId) {
        this.mGoodsId = mGoodsId;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    public void setGoodsName(String mGoodsName) {
        this.mGoodsName = mGoodsName;
    }

    public int getGoodsCount() {
        return mGoodsCount;
    }

    public void setGoodsCount(int mGoodsCount) {
        this.mGoodsCount = mGoodsCount;
    }

    public void setGoodsCount(String goodsCount) {
        try {
            this.mGoodsCount = Integer.valueOf(goodsCount);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsItemBean bean = (GoodsItemBean) o;

        return mGoodsId != null ? mGoodsId.equals(bean.mGoodsId) : bean.mGoodsId == null;

    }

    @Override
    public int hashCode() {
        return mGoodsId != null ? mGoodsId.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mGoodsId);
        dest.writeString(mGoodsName);
        dest.writeInt(mGoodsCount);
        dest.writeFloat(mGoodsPrice);
        dest.writeFloat(mGoodsUnitPrice);
        dest.writeString(mGoodsAvatar);
    }
}
