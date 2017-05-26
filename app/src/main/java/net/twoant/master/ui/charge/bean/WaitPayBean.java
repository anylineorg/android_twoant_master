package net.twoant.master.ui.charge.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/30.
 * 佛祖保佑   永无BUG
 */

public class WaitPayBean {

    /**
     * data : [{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0.01,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":123,"PAY_STATUS":0,"PURSE_PRICE":0,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":0.01,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0.01,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":124,"PAY_STATUS":0,"PURSE_PRICE":0,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":0.01,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":104,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":125,"PAY_STATUS":0,"PURSE_PRICE":0,"SELLER_ID":13,"SELLER_SCORE":0,"SHOP_SELLER_AVATAR":"/shop_img/1480556808397f9ac1619.jpg","SHOP_SELLER_ID":13,"SHOP_SELLER_NM":"店铺","SORT_ID":1,"SORT_NM":"商品支付","TOTAL_PRICE":104,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":133,"PAY_STATUS":0,"PURSE_PRICE":1,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":1,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":134,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":135,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":136,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":137,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":138,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0},{"BUYER_AVATAR":"/avatar/14806582023632382bdd1.jpg","BUYER_ID":12,"BUYER_NM":"意潇","CASH_PRICE":0,"CASH_SCORE":0,"FETCH_PRICE":0,"FETCH_RATE":0,"ID":139,"PAY_STATUS":0,"PURSE_PRICE":11,"SELLER_SCORE":0,"SORT_ID":4,"SORT_NM":"积分充值","TOTAL_PRICE":11,"VOUCHER_VAL":0}]
     * result : true
     * success : true
     * type : list
     */

    private boolean result;
    private boolean success;
    private String type;
    private List<DataBean> data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * BUYER_AVATAR : /avatar/14806582023632382bdd1.jpg
         * BUYER_ID : 12
         * BUYER_NM : 意潇
         * CASH_PRICE : 0.01
         * CASH_SCORE : 0
         * FETCH_PRICE : 0
         * FETCH_RATE : 0
         * ID : 123
         * PAY_STATUS : 0
         * PURSE_PRICE : 0
         * SELLER_SCORE : 0
         * SORT_ID : 4
         * SORT_NM : 积分充值
         * TOTAL_PRICE : 0.01
         * VOUCHER_VAL : 0
         * SELLER_ID : 13
         * SHOP_SELLER_AVATAR : /shop_img/1480556808397f9ac1619.jpg
         * SHOP_SELLER_ID : 13
         * SHOP_SELLER_NM : 店铺
         */

        private String BUYER_AVATAR;
        private int BUYER_ID;
        private String BUYER_NM;
        private double CASH_PRICE;
        private int CASH_SCORE;
        private int FETCH_PRICE;
        private int FETCH_RATE;
        private int ID;
        private int PAY_STATUS;
        private int PURSE_PRICE;
        private int SELLER_SCORE;
        private int SORT_ID;
        private String SORT_NM;
        private double TOTAL_PRICE;
        private int VOUCHER_VAL;
        private int SELLER_ID;
        private String SHOP_SELLER_AVATAR;
        private int SHOP_SELLER_ID;
        private String SHOP_SELLER_NM;

        public String getBUYER_AVATAR() {
            return BUYER_AVATAR;
        }

        public void setBUYER_AVATAR(String BUYER_AVATAR) {
            this.BUYER_AVATAR = BUYER_AVATAR;
        }

        public int getBUYER_ID() {
            return BUYER_ID;
        }

        public void setBUYER_ID(int BUYER_ID) {
            this.BUYER_ID = BUYER_ID;
        }

        public String getBUYER_NM() {
            return BUYER_NM;
        }

        public void setBUYER_NM(String BUYER_NM) {
            this.BUYER_NM = BUYER_NM;
        }

        public double getCASH_PRICE() {
            return CASH_PRICE;
        }

        public void setCASH_PRICE(double CASH_PRICE) {
            this.CASH_PRICE = CASH_PRICE;
        }

        public int getCASH_SCORE() {
            return CASH_SCORE;
        }

        public void setCASH_SCORE(int CASH_SCORE) {
            this.CASH_SCORE = CASH_SCORE;
        }

        public int getFETCH_PRICE() {
            return FETCH_PRICE;
        }

        public void setFETCH_PRICE(int FETCH_PRICE) {
            this.FETCH_PRICE = FETCH_PRICE;
        }

        public int getFETCH_RATE() {
            return FETCH_RATE;
        }

        public void setFETCH_RATE(int FETCH_RATE) {
            this.FETCH_RATE = FETCH_RATE;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getPAY_STATUS() {
            return PAY_STATUS;
        }

        public void setPAY_STATUS(int PAY_STATUS) {
            this.PAY_STATUS = PAY_STATUS;
        }

        public int getPURSE_PRICE() {
            return PURSE_PRICE;
        }

        public void setPURSE_PRICE(int PURSE_PRICE) {
            this.PURSE_PRICE = PURSE_PRICE;
        }

        public int getSELLER_SCORE() {
            return SELLER_SCORE;
        }

        public void setSELLER_SCORE(int SELLER_SCORE) {
            this.SELLER_SCORE = SELLER_SCORE;
        }

        public int getSORT_ID() {
            return SORT_ID;
        }

        public void setSORT_ID(int SORT_ID) {
            this.SORT_ID = SORT_ID;
        }

        public String getSORT_NM() {
            return SORT_NM;
        }

        public void setSORT_NM(String SORT_NM) {
            this.SORT_NM = SORT_NM;
        }

        public double getTOTAL_PRICE() {
            return TOTAL_PRICE;
        }

        public void setTOTAL_PRICE(double TOTAL_PRICE) {
            this.TOTAL_PRICE = TOTAL_PRICE;
        }

        public int getVOUCHER_VAL() {
            return VOUCHER_VAL;
        }

        public void setVOUCHER_VAL(int VOUCHER_VAL) {
            this.VOUCHER_VAL = VOUCHER_VAL;
        }

        public int getSELLER_ID() {
            return SELLER_ID;
        }

        public void setSELLER_ID(int SELLER_ID) {
            this.SELLER_ID = SELLER_ID;
        }

        public String getSHOP_SELLER_AVATAR() {
            return SHOP_SELLER_AVATAR;
        }

        public void setSHOP_SELLER_AVATAR(String SHOP_SELLER_AVATAR) {
            this.SHOP_SELLER_AVATAR = SHOP_SELLER_AVATAR;
        }

        public int getSHOP_SELLER_ID() {
            return SHOP_SELLER_ID;
        }

        public void setSHOP_SELLER_ID(int SHOP_SELLER_ID) {
            this.SHOP_SELLER_ID = SHOP_SELLER_ID;
        }

        public String getSHOP_SELLER_NM() {
            return SHOP_SELLER_NM;
        }

        public void setSHOP_SELLER_NM(String SHOP_SELLER_NM) {
            this.SHOP_SELLER_NM = SHOP_SELLER_NM;
        }
    }
}
