package net.twoant.master.ui.charge.bean;

/**
 * Created by DZY on 2016/12/27.
 * 佛祖保佑   永无BUG
 */

public class ShopPayBean {

    /**
     * data : {"BUYER_ID":"12","CASH_PRICE":0.15,"CASH_SCORE":10,"ID":31,"PURSE_PRICE":0.2,"SELLER_ID":"8","SHOP_SELLER_ID":"8","SORT_ID":"2","TOTAL_PRICE":0.4,"VOUCHER_ID":"","VOUCHER_VAL":0}
     * result : true
     * success : true
     * type : map
     */

    private DataBean data;
    private boolean result;
    private boolean success;
    private String type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

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

    public static class DataBean {
        /**
         * BUYER_ID : 12
         * CASH_PRICE : 0.15
         * CASH_SCORE : 10
         * ID : 31
         * PURSE_PRICE : 0.2
         * SELLER_ID : 8
         * SHOP_SELLER_ID : 8
         * SORT_ID : 2
         * TOTAL_PRICE : 0.4
         * VOUCHER_ID :
         * VOUCHER_VAL : 0
         */

        private String BUYER_ID;
        private double CASH_PRICE;
        private int CASH_SCORE;
        private int ID;
        private double PURSE_PRICE;
        private String SELLER_ID;
        private String SHOP_SELLER_ID;
        private String SORT_ID;
        private double TOTAL_PRICE;
        private String VOUCHER_ID;
        private int VOUCHER_VAL;

        public String getBUYER_ID() {
            return BUYER_ID;
        }

        public void setBUYER_ID(String BUYER_ID) {
            this.BUYER_ID = BUYER_ID;
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

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public double getPURSE_PRICE() {
            return PURSE_PRICE;
        }

        public void setPURSE_PRICE(double PURSE_PRICE) {
            this.PURSE_PRICE = PURSE_PRICE;
        }

        public String getSELLER_ID() {
            return SELLER_ID;
        }

        public void setSELLER_ID(String SELLER_ID) {
            this.SELLER_ID = SELLER_ID;
        }

        public String getSHOP_SELLER_ID() {
            return SHOP_SELLER_ID;
        }

        public void setSHOP_SELLER_ID(String SHOP_SELLER_ID) {
            this.SHOP_SELLER_ID = SHOP_SELLER_ID;
        }

        public String getSORT_ID() {
            return SORT_ID;
        }

        public void setSORT_ID(String SORT_ID) {
            this.SORT_ID = SORT_ID;
        }

        public double getTOTAL_PRICE() {
            return TOTAL_PRICE;
        }

        public void setTOTAL_PRICE(double TOTAL_PRICE) {
            this.TOTAL_PRICE = TOTAL_PRICE;
        }

        public String getVOUCHER_ID() {
            return VOUCHER_ID;
        }

        public void setVOUCHER_ID(String VOUCHER_ID) {
            this.VOUCHER_ID = VOUCHER_ID;
        }

        public int getVOUCHER_VAL() {
            return VOUCHER_VAL;
        }

        public void setVOUCHER_VAL(int VOUCHER_VAL) {
            this.VOUCHER_VAL = VOUCHER_VAL;
        }
    }
}
