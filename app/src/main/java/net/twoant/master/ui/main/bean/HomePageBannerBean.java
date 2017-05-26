package net.twoant.master.ui.main.bean;

import java.util.List;

/**
 * Created by S_Y_H on 2016/12/8.
 * 轮播图的实体类
 */

public class HomePageBannerBean {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {

        private String  ad_picurl;
        private String  ad_jump_url;
        private String ad_shop_id;
        private String ad_goods_id;
        private String ad_activity_id;
        private int ad_activity_type;
        /***
         * 0是跳商品详情, 1是活动详情, 2是商家详情， 3是跳链接
         */
        private int ad_type;

        public int getAd_activity_type() {
            return ad_activity_type;
        }

        public void setAd_activity_type(int ad_activity_type) {
            this.ad_activity_type = ad_activity_type;
        }

        public int getAd_type() {
            return ad_type;
        }
        public void setAd_type(int ad_type) {
            this.ad_type = ad_type;
        }
        public String getAd_picurl() {
            return ad_picurl;
        }
        public void setAd_picurl(String ad_picurl) {
            this.ad_picurl = ad_picurl;
        }
        public String getAd_jump_url() {
            return ad_jump_url;
        }
        public void setAd_jump_url(String ad_jump_url) {
            this.ad_jump_url = ad_jump_url;
        }
        public String getAd_shop_id() {
            return ad_shop_id;
        }
        public void setAd_shop_id(String ad_shop_id) {
            this.ad_shop_id = ad_shop_id;
        }
        public String getAd_goods_id() {
            return ad_goods_id;
        }
        public void setAd_goods_id(String ad_goods_id) {
            this.ad_goods_id = ad_goods_id;
        }
        public String getAd_activity_id() {
            return ad_activity_id;
        }
        public void setAd_activity_id(String ad_activity_id) {
            this.ad_activity_id = ad_activity_id;
        }

    }
}
