package net.twoant.master.ui.main.bean;

import net.twoant.master.ui.main.interfaces.IDataBean;

import java.util.List;

/**
 * Created by S_Y_H on 2016/11/24.
 * 商品的数据类
 */

public class GoodsDataBean {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements IDataBean {
        /**
         * goods_id : 22
         * goods_pk_shop : 13
         * goods_count : 580
         * goods_send_price : null
         * shop_name : null
         * _distance : 0
         * sales : 0
         * goods_name : xx
         * goods_title : 商品标题
         * goods_price : 52.0
         * goods_introduce : null
         * goods_img : /goods_img/goods_148064704781349b08790.jpg
         * click : 1
         * goods_bander_img : null
         * goods_details_img : null
         */

        private String goods_id;
        private int goods_pk_shop;
        private int goods_count;
        private float goods_send_price;
        private String shop_name;
        private String _distance;//距离
        private String sales;
        private String goods_name;
        private String goods_title;
        private String goods_price;
        private String goods_introduce;
        private String goods_img;
        private String click;
        private List<String> goods_bander_img;
        private List<String> goods_details_img;

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public int getGoods_pk_shop() {
            return goods_pk_shop;
        }

        public void setGoods_pk_shop(int goods_pk_shop) {
            this.goods_pk_shop = goods_pk_shop;
        }

        public int getGoods_count() {
            return goods_count;
        }

        public void setGoods_count(int goods_count) {
            this.goods_count = goods_count;
        }

        public float getGoods_send_price() {
            return goods_send_price;
        }

        public void setGoods_send_price(float goods_send_price) {
            this.goods_send_price = goods_send_price;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String get_distance() {
            return _distance;
        }

        public void set_distance(String _distance) {
            this._distance = _distance;
        }

        public String getSales() {
            return sales;
        }

        public void setSales(String sales) {
            this.sales = sales;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getGoods_title() {
            return goods_title;
        }

        public void setGoods_title(String goods_title) {
            this.goods_title = goods_title;
        }

        public String getGoods_price() {
            return goods_price;
        }

        public void setGoods_price(String goods_price) {
            this.goods_price = goods_price;
        }

        public String getGoods_introduce() {
            return goods_introduce;
        }

        public void setGoods_introduce(String goods_introduce) {
            this.goods_introduce = goods_introduce;
        }

        public String getGoods_img() {
            return goods_img;
        }

        public void setGoods_img(String goods_img) {
            this.goods_img = goods_img;
        }

        public String getClick() {
            return click;
        }

        public void setClick(String click) {
            this.click = click;
        }

        public List<String> getGoods_bander_img() {
            return goods_bander_img;
        }

        public void setGoods_bander_img(List<String> goods_bander_img) {
            this.goods_bander_img = goods_bander_img;
        }

        public List<String> getGoods_details_img() {
            return goods_details_img;
        }

        public void setGoods_details_img(List<String> goods_details_img) {
            this.goods_details_img = goods_details_img;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "goods_id='" + goods_id + '\'' +
                    ", goods_pk_shop=" + goods_pk_shop +
                    ", goods_count=" + goods_count +
                    ", goods_send_price=" + goods_send_price +
                    ", shop_name='" + shop_name + '\'' +
                    ", _distance=" + _distance +
                    ", sales=" + sales +
                    ", goods_name='" + goods_name + '\'' +
                    ", goods_title='" + goods_title + '\'' +
                    ", goods_price='" + goods_price + '\'' +
                    ", goods_introduce='" + goods_introduce + '\'' +
                    ", goods_img='" + goods_img + '\'' +
                    ", click=" + click +
                    ", goods_bander_img=" + goods_bander_img +
                    ", goods_details_img=" + goods_details_img +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ResultBean that = (ResultBean) o;

            return goods_id != null ? goods_id.equals(that.goods_id) : that.goods_id == null
                    && (goods_name != null ? goods_name.equals(that.goods_name) : that.goods_name == null
                    && (goods_price != null ? goods_price.equals(that.goods_price) : that.goods_price == null));

        }

        @Override
        public int hashCode() {
            int result = goods_id != null ? goods_id.hashCode() : 0;
            result = 31 * result + (goods_name != null ? goods_name.hashCode() : 0);
            result = 31 * result + (goods_price != null ? goods_price.hashCode() : 0);

            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsDataBean that = (GoodsDataBean) o;

        return result != null ? result.equals(that.result) : that.result == null;

    }

    @Override
    public int hashCode() {
        return result != null ? result.hashCode() : 0;
    }
}


