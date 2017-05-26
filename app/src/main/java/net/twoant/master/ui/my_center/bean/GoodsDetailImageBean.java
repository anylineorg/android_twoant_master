package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/12/2.
 */

public class GoodsDetailImageBean {

    /**
     * goods_bander_dt1 : /goods_img/goods_14806623509417536e4ea.jpg
     * goods_bander_dt2 : /goods_img/goods_148066235094229165360.jpg
     * goods_bander_dt3 : /goods_img/goods_148066235094285091a33.jpg
     * goods_bander_dt3 : /goods_img/goods_148066235094285091a33.jpg
     * goods_bander_img1 : /goods_img/goods_14806623509346d881533.jpg
     * goods_bander_img2 : /goods_img/goods_148066235093587f0c976.jpg
     * goods_bander_img3 : /goods_img/goods_14806623509409a945e1b.jpg
     * goods_bander_img3 : /goods_img/goods_14806623509409a945e1b.jpg
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String goods_bander_dt1;
        private String goods_bander_dt2;
        private String goods_bander_dt4;
        private String goods_bander_dt3;
        private String goods_bander_img1;
        private String goods_bander_img2;
        private String goods_bander_img3;
        private String goods_bander_img4;
        private String goods_img_txt;

        public String getGoods_img_txt() {
            return goods_img_txt;
        }

        public void setGoods_img_txt(String goods_img_txt) {
            this.goods_img_txt = goods_img_txt;
        }

        public String getGoods_bander_dt1() {
            return goods_bander_dt1;
        }

        public void setGoods_bander_dt1(String goods_bander_dt1) {
            this.goods_bander_dt1 = goods_bander_dt1;
        }

        public String getGoods_bander_dt2() {
            return goods_bander_dt2;
        }

        public void setGoods_bander_dt2(String goods_bander_dt2) {
            this.goods_bander_dt2 = goods_bander_dt2;
        }

        public String getGoods_bander_dt3() {
            return goods_bander_dt3;
        }

        public void setGoods_bander_dt3(String goods_bander_dt3) {
            this.goods_bander_dt3 = goods_bander_dt3;
        }

        public String getGoods_bander_dt4() {
            return goods_bander_dt4;
        }

        public void setGoods_bander_dt4(String goods_bander_dt4) {
            this.goods_bander_dt4 = goods_bander_dt4;
        }

        public String getGoods_bander_img1() {
            return goods_bander_img1;
        }

        public void setGoods_bander_img1(String goods_bander_img1) {
            this.goods_bander_img1 = goods_bander_img1;
        }

        public String getGoods_bander_img2() {
            return goods_bander_img2;
        }

        public void setGoods_bander_img2(String goods_bander_img2) {
            this.goods_bander_img2 = goods_bander_img2;
        }

        public String getGoods_bander_img3() {
            return goods_bander_img3;
        }

        public void setGoods_bander_img3(String goods_bander_img3) {
            this.goods_bander_img3 = goods_bander_img3;
        }

        public String getGoods_bander_img4() {
            return goods_bander_img4;
        }

        public void setGoods_bander_img4(String goods_bander_img4) {
            this.goods_bander_img4 = goods_bander_img4;
        }

    }
}
