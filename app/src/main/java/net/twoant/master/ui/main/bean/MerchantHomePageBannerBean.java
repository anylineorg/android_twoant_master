package net.twoant.master.ui.main.bean;

import java.util.ArrayList;

/**
 * Created by S_Y_H on 2016/12/31.
 * 商家首页的轮播bean
 */

public class MerchantHomePageBannerBean {


    /**
     * result : {"img1":"/shop_img/1481513187789f27483ff.jpg","img2":"/shop_img/1481513187789f27483ff.jpg","img3":"/shop_img/1481513187789f27483ff.jpg","img4":"/shop_img/1481513187789f27483ff.jpg"}
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
         * img1 : /shop_img/1481513187789f27483ff.jpg
         * img2 : /shop_img/1481513187789f27483ff.jpg
         * img3 : /shop_img/1481513187789f27483ff.jpg
         * img4 : /shop_img/1481513187789f27483ff.jpg
         */

        private String img1;
        private String img2;
        private String img3;
        private String img4;

        public ArrayList<String> getList() {
            ArrayList<String> strings = new ArrayList<>();
            if (img1 != null)
                strings.add(img1);
            if (img2 != null)
                strings.add(img2);
            if (img3 != null)
                strings.add(img3);
            if (img4 != null)
                strings.add(img4);
            return strings;
        }

        public String getImg1() {
            return img1;
        }

        public void setImg1(String img1) {
            this.img1 = img1;
        }

        public String getImg2() {
            return img2;
        }

        public void setImg2(String img2) {
            this.img2 = img2;
        }

        public String getImg3() {
            return img3;
        }

        public void setImg3(String img3) {
            this.img3 = img3;
        }

        public String getImg4() {
            return img4;
        }

        public void setImg4(String img4) {
            this.img4 = img4;
        }
    }
}
