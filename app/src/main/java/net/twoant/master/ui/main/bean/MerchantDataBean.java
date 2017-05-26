package net.twoant.master.ui.main.bean;

import net.twoant.master.ui.main.interfaces.IDataBean;

import java.util.List;

/**
 * Created by S_Y_H on 2016/11/24.
 * 商家列表的数据 实体类
 */

public class MerchantDataBean implements IDataBean {
    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements IDataBean {
        /**
         * id : 13
         * aisou_id : 0
         * seller_count : 0
         * shop_name : 店铺
         * shop_avatar : /shop_img/1480556808397f9ac1619.jpg
         * shop_introduce : 店铺简介
         * shop_longitude : 123.2342
         * shop_latitude : 30.1231
         * shop_address : 月亮
         * shop_tel : null
         * collection : 1
         * _distance : null
         */

        private String id;
        private int aisou_id;
        private String shop_name;
        private String shop_avatar;
        private String shop_introduce;
        private String shop_longitude;
        private String shop_latitude;
        private String shop_address;
        private String shop_tel;
        private int collection;
        private String _distance;
        private String seller_count; //销售合计
        private String click_QTY_SUM;//点击合计

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public int getAisou_id() {
            return aisou_id;
        }

        public void setAisou_id(int aisou_id) {
            this.aisou_id = aisou_id;
        }

        public String getSeller_count() {
            return seller_count;
        }

        public void setSeller_count(String seller_count) {
            this.seller_count = seller_count;
        }

        public String getShop_avatar() {
            return shop_avatar;
        }

        public void setShop_avatar(String shop_avatar) {
            this.shop_avatar = shop_avatar;
        }

        public String getShop_introduce() {
            return shop_introduce;
        }

        public void setShop_introduce(String shop_introduce) {
            this.shop_introduce = shop_introduce;
        }

        public String getShop_longitude() {
            return shop_longitude;
        }

        public void setShop_longitude(String shop_longitude) {
            this.shop_longitude = shop_longitude;
        }

        public String getShop_latitude() {
            return shop_latitude;
        }

        public void setShop_latitude(String shop_latitude) {
            this.shop_latitude = shop_latitude;
        }

        public String getShop_address() {
            return shop_address;
        }

        public void setShop_address(String shop_address) {
            this.shop_address = shop_address;
        }

        public String getClick_QTY_SUM() {
            return click_QTY_SUM;
        }

        public void setClick_QTY_SUM(String cLICK_QTY_SUM) {
            click_QTY_SUM = cLICK_QTY_SUM;
        }

        public int getCollection() {
            return collection;
        }

        public void setCollection(int collection) {
            this.collection = collection;
        }

        public String getShop_tel() {
            return shop_tel;
        }

        public void setShop_tel(String shop_tel) {
            this.shop_tel = shop_tel;
        }

        public String get_distance() {
            return _distance;
        }

        public void set_distance(String _distance) {
            this._distance = _distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ResultBean that = (ResultBean) o;

            return id != null ? id.equals(that.id) : that.id == null && shop_name != null ? shop_name.equals(that.shop_name) : that.shop_name == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (shop_name != null ? shop_name.hashCode() : 0);
            result = 31 * result + (shop_tel != null ? shop_tel.hashCode() : 0);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MerchantDataBean that = (MerchantDataBean) o;

        return result != null ? result.equals(that.result) : that.result == null;

    }

    @Override
    public int hashCode() {
        return result != null ? result.hashCode() : 0;
    }
}
