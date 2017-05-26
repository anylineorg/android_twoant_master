package net.twoant.master.ui.charge.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */

public class TransationRecordBean {

    /**
     * result : true
     * data : [{"SORT_ID":1,"VOUCHER_VAL":0,"SELLER_ID":975,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1484040562074ab276f72.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":15538464666,"BUYER_ID":58,"PURSE_PRICE":1,"TOTAL_PRICE":1,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":739,"CREATE_TIME":null,"RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/shop_img/1483599645669bf0df4e7.jpg","IDX":null,"SHOP_SELLER_NM":"来一发","ACTIVITY_TITLE":null,"SORT_NM":"商品支付","USER_SELLER_AVATAR":null,"PAY_STATUS":1,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":null,"BUYER_TEL":"15054227813","PAY_TIME":"2017-01-06 10:57:01","CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":975,"BUYER_NM":"150542"},{"SORT_ID":1,"VOUCHER_VAL":0,"SELLER_ID":975,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1484040562074ab276f72.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":15538464666,"BUYER_ID":58,"PURSE_PRICE":1,"TOTAL_PRICE":1,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":736,"CREATE_TIME":null,"RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/shop_img/1483599645669bf0df4e7.jpg","IDX":null,"SHOP_SELLER_NM":"来一发","ACTIVITY_TITLE":null,"SORT_NM":"商品支付","USER_SELLER_AVATAR":null,"PAY_STATUS":1,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":null,"BUYER_TEL":"15054227813","PAY_TIME":"2017-01-06 10:54:14","CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":975,"BUYER_NM":"150542"},{"SORT_ID":1,"VOUCHER_VAL":0,"SELLER_ID":975,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1484040562074ab276f72.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":15538464666,"BUYER_ID":58,"PURSE_PRICE":1,"TOTAL_PRICE":1,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":735,"CREATE_TIME":null,"RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/shop_img/1483599645669bf0df4e7.jpg","IDX":null,"SHOP_SELLER_NM":"来一发","ACTIVITY_TITLE":null,"SORT_NM":"商品支付","USER_SELLER_AVATAR":null,"PAY_STATUS":1,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":null,"BUYER_TEL":"15054227813","PAY_TIME":"2017-01-06 10:52:24","CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":975,"BUYER_NM":"150542"},{"SORT_ID":1,"VOUCHER_VAL":0,"SELLER_ID":975,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1484040562074ab276f72.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":15538464666,"BUYER_ID":58,"PURSE_PRICE":1,"TOTAL_PRICE":1,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":734,"CREATE_TIME":null,"RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/shop_img/1483599645669bf0df4e7.jpg","IDX":null,"SHOP_SELLER_NM":"来一发","ACTIVITY_TITLE":null,"SORT_NM":"商品支付","USER_SELLER_AVATAR":null,"PAY_STATUS":1,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":null,"BUYER_TEL":"15054227813","PAY_TIME":"2017-01-06 10:51:33","CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":975,"BUYER_NM":"150542"},{"SORT_ID":1,"VOUCHER_VAL":0,"SELLER_ID":975,"USER_SELLER_ID":null,"BUYER_AVATAR":"/avatar/1484040562074ab276f72.jpg","FETCH_RATE":0,"CASH_SCORE":0,"DESCRIPTION":null,"SHOP_TEL":15538464666,"BUYER_ID":58,"PURSE_PRICE":1,"TOTAL_PRICE":1,"CASH_PRICE":0,"TRADE_CALL_ID":null,"ID":631,"CREATE_TIME":null,"RECEIVE_ADDRESS":null,"SHOP_SELLER_AVATAR":"/shop_img/1483599645669bf0df4e7.jpg","IDX":null,"SHOP_SELLER_NM":"来一发","ACTIVITY_TITLE":null,"SORT_NM":"商品支付","USER_SELLER_AVATAR":null,"PAY_STATUS":1,"FETCH_PRICE":0,"RECEIVE_NAME":null,"USER_SELLER_NM":null,"SELLER_SCORE":0,"ACTIVITY_ID":null,"BUYER_TEL":"15054227813","PAY_TIME":"2017-01-05 04:17:36","CODE":null,"VOUCHER_ID":null,"RECEIVE_TEL":null,"SHOP_SELLER_ID":975,"BUYER_NM":"150542"}]
     * success : true
     * type : list
     * message : null
     */

    private boolean result;
    private boolean success;
    private String type;
    private Object message;
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

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * SORT_ID : 1
         * VOUCHER_VAL : 0
         * SELLER_ID : 975
         * USER_SELLER_ID : null
         * BUYER_AVATAR : /avatar/1484040562074ab276f72.jpg
         * FETCH_RATE : 0
         * CASH_SCORE : 0
         * DESCRIPTION : null
         * SHOP_TEL : 15538464666
         * BUYER_ID : 58
         * PURSE_PRICE : 1
         * TOTAL_PRICE : 1
         * CASH_PRICE : 0
         * TRADE_CALL_ID : null
         * ID : 739
         * CREATE_TIME : null
         * RECEIVE_ADDRESS : null
         * SHOP_SELLER_AVATAR : /shop_img/1483599645669bf0df4e7.jpg
         * IDX : null
         * SHOP_SELLER_NM : 来一发
         * ACTIVITY_TITLE : null
         * SORT_NM : 商品支付
         * USER_SELLER_AVATAR : null
         * PAY_STATUS : 1
         * FETCH_PRICE : 0
         * RECEIVE_NAME : null
         * USER_SELLER_NM : null
         * SELLER_SCORE : 0
         * ACTIVITY_ID : null
         * BUYER_TEL : 15054227813
         * PAY_TIME : 2017-01-06 10:57:01
         * CODE : null
         * VOUCHER_ID : null
         * RECEIVE_TEL : null
         * SHOP_SELLER_ID : 975
         * BUYER_NM : 150542
         */

        private String SORT_ID;
        private String VOUCHER_VAL;
        private String SELLER_ID;
        private Object USER_SELLER_ID;
        private String BUYER_AVATAR;
        private String FETCH_RATE;
        private String CASH_SCORE;
        private Object DESCRIPTION;
        private long SHOP_TEL;
        private String BUYER_ID;
        private String PURSE_PRICE;
        private String TOTAL_PRICE;
        private String CASH_PRICE;
        private Object TRADE_CALL_ID;
        private String ID;
        private Object CREATE_TIME;
        private Object RECEIVE_ADDRESS;
        private String SHOP_SELLER_AVATAR;
        private Object IDX;
        private String SHOP_SELLER_NM;
        private Object ACTIVITY_TITLE;
        private String SORT_NM;
        private Object USER_SELLER_AVATAR;
        private String PAY_STATUS;
        private String FETCH_PRICE;
        private Object RECEIVE_NAME;
        private Object USER_SELLER_NM;
        private String SELLER_SCORE;
        private Object ACTIVITY_ID;
        private String BUYER_TEL;
        private String PAY_TIME;
        private Object CODE;
        private Object VOUCHER_ID;
        private Object RECEIVE_TEL;
        private String SHOP_SELLER_ID;
        private String BUYER_NM;
        private String CASH_SORT_NM;

        public String getCASH_SORT_NM() {
            return CASH_SORT_NM;
        }

        public void setCASH_SORT_NM(String CASH_SORT_NM) {
            this.CASH_SORT_NM = CASH_SORT_NM;
        }

        public String getSORT_ID() {
            return SORT_ID;
        }

        public void setSORT_ID(String SORT_ID) {
            this.SORT_ID = SORT_ID;
        }

        public String getVOUCHER_VAL() {
            return VOUCHER_VAL;
        }

        public void setVOUCHER_VAL(String VOUCHER_VAL) {
            this.VOUCHER_VAL = VOUCHER_VAL;
        }

        public String getSELLER_ID() {
            return SELLER_ID;
        }

        public void setSELLER_ID(String SELLER_ID) {
            this.SELLER_ID = SELLER_ID;
        }

        public Object getUSER_SELLER_ID() {
            return USER_SELLER_ID;
        }

        public void setUSER_SELLER_ID(Object USER_SELLER_ID) {
            this.USER_SELLER_ID = USER_SELLER_ID;
        }

        public String getBUYER_AVATAR() {
            return BUYER_AVATAR;
        }

        public void setBUYER_AVATAR(String BUYER_AVATAR) {
            this.BUYER_AVATAR = BUYER_AVATAR;
        }

        public String getFETCH_RATE() {
            return FETCH_RATE;
        }

        public void setFETCH_RATE(String FETCH_RATE) {
            this.FETCH_RATE = FETCH_RATE;
        }

        public String getCASH_SCORE() {
            return CASH_SCORE;
        }

        public void setCASH_SCORE(String CASH_SCORE) {
            this.CASH_SCORE = CASH_SCORE;
        }

        public Object getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(Object DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public long getSHOP_TEL() {
            return SHOP_TEL;
        }

        public void setSHOP_TEL(long SHOP_TEL) {
            this.SHOP_TEL = SHOP_TEL;
        }

        public String getBUYER_ID() {
            return BUYER_ID;
        }

        public void setBUYER_ID(String BUYER_ID) {
            this.BUYER_ID = BUYER_ID;
        }

        public String getPURSE_PRICE() {
            return PURSE_PRICE;
        }

        public void setPURSE_PRICE(String PURSE_PRICE) {
            this.PURSE_PRICE = PURSE_PRICE;
        }

        public String getTOTAL_PRICE() {
            return TOTAL_PRICE;
        }

        public void setTOTAL_PRICE(String TOTAL_PRICE) {
            this.TOTAL_PRICE = TOTAL_PRICE;
        }

        public String getCASH_PRICE() {
            return CASH_PRICE;
        }

        public void setCASH_PRICE(String CASH_PRICE) {
            this.CASH_PRICE = CASH_PRICE;
        }

        public Object getTRADE_CALL_ID() {
            return TRADE_CALL_ID;
        }

        public void setTRADE_CALL_ID(Object TRADE_CALL_ID) {
            this.TRADE_CALL_ID = TRADE_CALL_ID;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public Object getCREATE_TIME() {
            return CREATE_TIME;
        }

        public void setCREATE_TIME(Object CREATE_TIME) {
            this.CREATE_TIME = CREATE_TIME;
        }

        public Object getRECEIVE_ADDRESS() {
            return RECEIVE_ADDRESS;
        }

        public void setRECEIVE_ADDRESS(Object RECEIVE_ADDRESS) {
            this.RECEIVE_ADDRESS = RECEIVE_ADDRESS;
        }

        public String getSHOP_SELLER_AVATAR() {
            return SHOP_SELLER_AVATAR;
        }

        public void setSHOP_SELLER_AVATAR(String SHOP_SELLER_AVATAR) {
            this.SHOP_SELLER_AVATAR = SHOP_SELLER_AVATAR;
        }

        public Object getIDX() {
            return IDX;
        }

        public void setIDX(Object IDX) {
            this.IDX = IDX;
        }

        public String getSHOP_SELLER_NM() {
            return SHOP_SELLER_NM;
        }

        public void setSHOP_SELLER_NM(String SHOP_SELLER_NM) {
            this.SHOP_SELLER_NM = SHOP_SELLER_NM;
        }

        public Object getACTIVITY_TITLE() {
            return ACTIVITY_TITLE;
        }

        public void setACTIVITY_TITLE(Object ACTIVITY_TITLE) {
            this.ACTIVITY_TITLE = ACTIVITY_TITLE;
        }

        public String getSORT_NM() {
            return SORT_NM;
        }

        public void setSORT_NM(String SORT_NM) {
            this.SORT_NM = SORT_NM;
        }

        public Object getUSER_SELLER_AVATAR() {
            return USER_SELLER_AVATAR;
        }

        public void setUSER_SELLER_AVATAR(Object USER_SELLER_AVATAR) {
            this.USER_SELLER_AVATAR = USER_SELLER_AVATAR;
        }

        public String getPAY_STATUS() {
            return PAY_STATUS;
        }

        public void setPAY_STATUS(String PAY_STATUS) {
            this.PAY_STATUS = PAY_STATUS;
        }

        public String getFETCH_PRICE() {
            return FETCH_PRICE;
        }

        public void setFETCH_PRICE(String FETCH_PRICE) {
            this.FETCH_PRICE = FETCH_PRICE;
        }

        public Object getRECEIVE_NAME() {
            return RECEIVE_NAME;
        }

        public void setRECEIVE_NAME(Object RECEIVE_NAME) {
            this.RECEIVE_NAME = RECEIVE_NAME;
        }

        public Object getUSER_SELLER_NM() {
            return USER_SELLER_NM;
        }

        public void setUSER_SELLER_NM(Object USER_SELLER_NM) {
            this.USER_SELLER_NM = USER_SELLER_NM;
        }

        public String getSELLER_SCORE() {
            return SELLER_SCORE;
        }

        public void setSELLER_SCORE(String SELLER_SCORE) {
            this.SELLER_SCORE = SELLER_SCORE;
        }

        public Object getACTIVITY_ID() {
            return ACTIVITY_ID;
        }

        public void setACTIVITY_ID(Object ACTIVITY_ID) {
            this.ACTIVITY_ID = ACTIVITY_ID;
        }

        public String getBUYER_TEL() {
            return BUYER_TEL;
        }

        public void setBUYER_TEL(String BUYER_TEL) {
            this.BUYER_TEL = BUYER_TEL;
        }

        public String getPAY_TIME() {
            return PAY_TIME;
        }

        public void setPAY_TIME(String PAY_TIME) {
            this.PAY_TIME = PAY_TIME;
        }

        public Object getCODE() {
            return CODE;
        }

        public void setCODE(Object CODE) {
            this.CODE = CODE;
        }

        public Object getVOUCHER_ID() {
            return VOUCHER_ID;
        }

        public void setVOUCHER_ID(Object VOUCHER_ID) {
            this.VOUCHER_ID = VOUCHER_ID;
        }

        public Object getRECEIVE_TEL() {
            return RECEIVE_TEL;
        }

        public void setRECEIVE_TEL(Object RECEIVE_TEL) {
            this.RECEIVE_TEL = RECEIVE_TEL;
        }

        public String getSHOP_SELLER_ID() {
            return SHOP_SELLER_ID;
        }

        public void setSHOP_SELLER_ID(String SHOP_SELLER_ID) {
            this.SHOP_SELLER_ID = SHOP_SELLER_ID;
        }

        public String getBUYER_NM() {
            return BUYER_NM;
        }

        public void setBUYER_NM(String BUYER_NM) {
            this.BUYER_NM = BUYER_NM;
        }
    }
}
