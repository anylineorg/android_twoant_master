package net.twoant.master.ui.my_center.bean;


import net.twoant.master.common_utils.LogUtils;

import java.io.Serializable;

public class GoodsItem implements Serializable {

        private String goodsId;
        private String goodsName;
        private int goodsCount=0;
        private int stock_qty;//库存
        private String goods_img;
        private float member_price;
        private String shop_name;
        private String shop_id;
        private float goodsPrice;

        public int getStock_qty() {
            return stock_qty;
        }

        public void setStock_qty(int stock_qty) {
            this.stock_qty = stock_qty;
        }



        public String getGoods_img() {
            return goods_img;
        }

        public void setGoods_img(String goods_img) {
            this.goods_img = goods_img;
        }



        public String getShop_name() {
            return shop_name;
        }

        public float getMember_price() {
            return member_price;
        }

        public void setMember_price(float member_price) {
            this.member_price = member_price;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        /**
         * 商品单价
         */
        private float goodsUnitPrice;

        public float getGoodsUnitPrice() {
            return goodsUnitPrice;
        }

        public void setGoodsUnitPrice(float goodsUnitPrice) {
            this.goodsUnitPrice = goodsUnitPrice;
        }

        public float getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(float goodsPrice) {
            this.goodsPrice = goodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            try {
                this.goodsPrice = Float.valueOf(goodsPrice);
            } catch (Exception e) {
                LogUtils.e("GoodsItem setGoodsPrice" + e.toString());
            }
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public int getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(int goodsCount) {
            this.goodsCount = goodsCount;
        }

        public void setGoodsCount(String goodsCount) {
            try {
                this.goodsCount = Integer.valueOf(goodsCount);
            } catch (Exception e) {
                LogUtils.e("GoodsItem setGoodsCount" + e.toString());
            }
        }

        @Override
        public String toString() {
            return "GoodsItem{" +
                    "goodsId='" + goodsId + '\'' +
                    ", goodsName='" + goodsName + '\'' +
                    ", goodsCount=" + goodsCount +
                    ", goodsPrice=" + goodsPrice +
                    ", goodsUnitPrice=" + goodsUnitPrice +
                    '}';
        }
    }
