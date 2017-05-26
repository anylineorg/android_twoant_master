package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/12/2.
 */

public class GoodsDetailBean {

    /**
     * result : {"_distance":0,"click":2,"goods_count":0,"goods_id":112,"goods_img":"/goods_img/goods_148531028394093c0f49d.jpg","goods_introduce":"哦哦哦","goods_ld":"","goods_name":"请勿","goods_pk_shop":203,"goods_price":66,"goods_title":"商品标题","goods_type":5,"sales":0,"shop_id":0,"shop_name":"八颗牙齿晒太阳"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * _distance : 0
         * click : 2
         * goods_count : 0
         * goods_id : 112
         * goods_img : /goods_img/goods_148531028394093c0f49d.jpg
         * goods_introduce : 哦哦哦
         * goods_ld :
         * goods_name : 请勿
         * goods_pk_shop : 203
         * goods_price : 66.0
         * goods_title : 商品标题
         * goods_type : 5
         * sales : 0
         * shop_id : 0
         * shop_name : 八颗牙齿晒太阳
         */

        private int _distance;
        private int click;
        private int goods_count;
        private int goods_id;
        private String goods_img;
        private String goods_introduce;
        private String goods_ld;
        private String goods_name;
        private int goods_pk_shop;
        private String goods_price;
        private String goods_title;
        private String goods_type;
        private int sales;
        private int shop_id;
        private String shop_name;

        public int get_distance() {
            return _distance;
        }

        public void set_distance(int _distance) {
            this._distance = _distance;
        }

        public int getClick() {
            return click;
        }

        public void setClick(int click) {
            this.click = click;
        }

        public int getGoods_count() {
            return goods_count;
        }

        public void setGoods_count(int goods_count) {
            this.goods_count = goods_count;
        }

        public int getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(int goods_id) {
            this.goods_id = goods_id;
        }

        public String getGoods_img() {
            return goods_img;
        }

        public void setGoods_img(String goods_img) {
            this.goods_img = goods_img;
        }

        public String getGoods_introduce() {
            return goods_introduce;
        }

        public void setGoods_introduce(String goods_introduce) {
            this.goods_introduce = goods_introduce;
        }

        public String getGoods_ld() {
            return goods_ld;
        }

        public void setGoods_ld(String goods_ld) {
            this.goods_ld = goods_ld;
        }

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public int getGoods_pk_shop() {
            return goods_pk_shop;
        }

        public void setGoods_pk_shop(int goods_pk_shop) {
            this.goods_pk_shop = goods_pk_shop;
        }

        public String getGoods_price() {
            return goods_price;
        }

        public void setGoods_price(String goods_price) {
            this.goods_price = goods_price;
        }

        public String getGoods_title() {
            return goods_title;
        }

        public void setGoods_title(String goods_title) {
            this.goods_title = goods_title;
        }

        public String getGoods_type() {
            return goods_type;
        }

        public void setGoods_type(String goods_type) {
            this.goods_type = goods_type;
        }

        public int getSales() {
            return sales;
        }

        public void setSales(int sales) {
            this.sales = sales;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }
    }
}
