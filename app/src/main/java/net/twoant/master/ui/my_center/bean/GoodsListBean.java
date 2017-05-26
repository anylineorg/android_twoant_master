package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/13.
 * 佛祖保佑   永无BUG
 */

public class GoodsListBean {

    /**
     * goods_id : 21
     * goods_pk_shop : 13
     * goods_count : 516
     * goods_send_price : null
     * shop_name : null
     * _distance : 0
     * sales : 0
     * goods_name : 德力媛小面包
     * goods_title : 商品标题
     * goods_price : 52.0
     * goods_introduce : 别看贵我好吃
     * goods_img : /goods_img/goods_14806434915826b32a898.jpg
     * click : 1
     * goods_bander_img : null
     * goods_details_img : null
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private int goods_id;
        private int goods_pk_shop;
        private int goods_count;
        private Object goods_send_price;
        private Object shop_name;
        private int _distance;
        private int sales;
        private String goods_name;
        private String goods_title;
        private String goods_price;
        private String goods_introduce;
        private String goods_img;
        private String goods_ld;
        private int click;
        private Object goods_bander_img;
        private Object goods_details_img;

        public String getGoods_ld() {
            return goods_ld;
        }

        public void setGoods_ld(String goods_ld) {
            this.goods_ld = goods_ld;
        }

        public int getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(int goods_id) {
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

        public Object getGoods_send_price() {
            return goods_send_price;
        }

        public void setGoods_send_price(Object goods_send_price) {
            this.goods_send_price = goods_send_price;
        }

        public Object getShop_name() {
            return shop_name;
        }

        public void setShop_name(Object shop_name) {
            this.shop_name = shop_name;
        }

        public int get_distance() {
            return _distance;
        }

        public void set_distance(int _distance) {
            this._distance = _distance;
        }

        public int getSales() {
            return sales;
        }

        public void setSales(int sales) {
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

        public int getClick() {
            return click;
        }

        public void setClick(int click) {
            this.click = click;
        }

        public Object getGoods_bander_img() {
            return goods_bander_img;
        }

        public void setGoods_bander_img(Object goods_bander_img) {
            this.goods_bander_img = goods_bander_img;
        }

        public Object getGoods_details_img() {
            return goods_details_img;
        }

        public void setGoods_details_img(Object goods_details_img) {
            this.goods_details_img = goods_details_img;
        }
    }
}
