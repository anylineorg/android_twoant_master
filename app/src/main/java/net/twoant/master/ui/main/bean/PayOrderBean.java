package net.twoant.master.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2017/3/23.
 * 订单支付的实体类
 */

public class PayOrderBean implements Parcelable {


    /**
     * 店内支付
     */
    public final static int TYPE_INNER = 0xA;

    /**
     * 商品支付
     */
    public final static int TYPE_GOODS = 0xB;

    /**
     * 扫码支付
     */
    public final static int TYPE_SCANNER = 0xC;

    private int mType;
    private String mPrice;
    private String mShopId;
    private String mShopName;
    private String mShopAvatar;

    private String mRedPacketId;
    /**
     * 买家留言
     */
    private String mLeaveMessage;
    /**
     * 配送类型 1.店内消费 2.商家配送， 3.平台配送， 4.买家自取
     */
    private String mPurchaseSort;
    private String mRedPacketPrice;

    private ArrayList<GoodsItemBean> mGoodsItemBean;

    /**
     * 当 type 为 {@link #TYPE_INNER}时， 标记为@Nullable 的才可以为空
     *
     * @param type         类型
     * @param price        待支付金额
     * @param shopId       商家id
     * @param shopName     商家名
     * @param shopAvatar   商家头像
     * @param sortType     配送方式
     * @param leaveMessage 买家留言
     */
    public PayOrderBean(int type, @Nullable String price, @NonNull String shopId, @NonNull String shopName, String shopAvatar, @Nullable String sortType, @Nullable String leaveMessage) {
        this.mType = type;
        this.mPrice = price;
        this.mShopId = shopId;
        this.mShopName = shopName;
        this.mShopAvatar = shopAvatar;
        this.mPurchaseSort = sortType;
        this.mLeaveMessage = leaveMessage;
    }

    private PayOrderBean(Parcel in) {
        mType = in.readInt();
        mPrice = in.readString();
        mShopId = in.readString();
        mShopName = in.readString();
        mShopAvatar = in.readString();
        mRedPacketId = in.readString();
        mLeaveMessage = in.readString();
        mPurchaseSort = in.readString();
        mRedPacketPrice = in.readString();
        mGoodsItemBean = in.createTypedArrayList(GoodsItemBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeString(mPrice);
        dest.writeString(mShopId);
        dest.writeString(mShopName);
        dest.writeString(mShopAvatar);
        dest.writeString(mRedPacketId);
        dest.writeString(mLeaveMessage);
        dest.writeString(mPurchaseSort);
        dest.writeString(mRedPacketPrice);
        dest.writeTypedList(mGoodsItemBean);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PayOrderBean> CREATOR = new Creator<PayOrderBean>() {
        @Override
        public PayOrderBean createFromParcel(Parcel in) {
            return new PayOrderBean(in);
        }

        @Override
        public PayOrderBean[] newArray(int size) {
            return new PayOrderBean[size];
        }
    };

    public String getRedPacketId() {
        return this.mRedPacketId;
    }

    public void setRedPacketId(String mRedPacketId) {
        this.mRedPacketId = mRedPacketId;
    }

    public String getRedPacketPrice() {
        return this.mRedPacketPrice;
    }

    public void setRedPacketPrice(String mRedPacketPrice) {
        this.mRedPacketPrice = mRedPacketPrice;
    }

    public String getLeaveMessage() {
        return this.mLeaveMessage;
    }

    public void setLeaveMessage(String message) {
        this.mLeaveMessage = message;
    }

    public void setGoodsItemBean(ArrayList<GoodsItemBean> goodsItemBean) {
        this.mGoodsItemBean = goodsItemBean;
    }

    public ArrayList<GoodsItemBean> getGoodsItemBean() {
        return this.mGoodsItemBean;
    }

    public String getPurchaseSort() {
        return this.mPurchaseSort;
    }

    public void setPurchaseSort(String sort) {
        this.mPurchaseSort = sort;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getShopName() {
        return this.mShopName;
    }

    public String getShopAvatar() {
        return this.mShopAvatar;
    }

    public String getShopId() {
        return this.mShopId;
    }

    public void setShopName(String mReceiver) {
        this.mShopName = mReceiver;
    }

    public void setShopAvatar(String shopAvatar) {
        this.mShopAvatar = shopAvatar;
    }

    public void setShopId(String shopId) {
        this.mShopId = shopId;
    }
}
