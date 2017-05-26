package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/18.
 * 佛祖保佑   永无BUG
 */

public class RedListToShopBean {

    /**
     * data : [{"ACTIVE_VAL":0,"ACTIVITY_ID":54,"ACTIVITY_ITEM_ID":157,"ACTIVITY_ITEM_TITLE":"系统红包100","ACTIVITY_SORT_ID":4,"ACTIVITY_SORT_NM":"红包活动","ACTIVITY_TITLE":"系统红包100","BALANCE":100,"BUY_TIME":"2017-01-05 05:47:28","END_TIME":"2017-01-26 12:00:00","ID":273,"IS_ENABLE":1,"ORDER_ID":671,"ORDER_ITEM_ID":29,"ORDER_ITEM_PRICE":0,"ORDER_ITEM_SCORE":0,"SORT_ID":12,"SORT_NM":"商家一次性代金券","USER_AVATAR":"/avatar/14833582468327101fa7b.jpg","USER_ID":62,"USER_NM":"17660635389","USE_STATUS":0,"VAL":100}]
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
         * ACTIVE_VAL : 0
         * ACTIVITY_ID : 54
         * ACTIVITY_ITEM_ID : 157
         * ACTIVITY_ITEM_TITLE : 系统红包100
         * ACTIVITY_SORT_ID : 4
         * ACTIVITY_SORT_NM : 红包活动
         * ACTIVITY_TITLE : 系统红包100
         * BALANCE : 100
         * BUY_TIME : 2017-01-05 05:47:28
         * END_TIME : 2017-01-26 12:00:00
         * ID : 273
         * IS_ENABLE : 1
         * ORDER_ID : 671
         * ORDER_ITEM_ID : 29
         * ORDER_ITEM_PRICE : 0
         * ORDER_ITEM_SCORE : 0
         * SORT_ID : 12
         * SORT_NM : 商家一次性代金券
         * USER_AVATAR : /avatar/14833582468327101fa7b.jpg
         * USER_ID : 62
         * USER_NM : 17660635389
         * USE_STATUS : 0
         * VAL : 100
         */

        private int ACTIVE_VAL;
        private int ACTIVITY_ID;
        private int ACTIVITY_ITEM_ID;
        private String ACTIVITY_ITEM_TITLE;
        private int ACTIVITY_SORT_ID;
        private String ACTIVITY_SORT_NM;
        private String ACTIVITY_TITLE;
        private int BALANCE;
        private String BUY_TIME;
        private String END_TIME;
        private int ID;
        private int IS_ENABLE;
        private int ORDER_ID;
        private int ORDER_ITEM_ID;
        private int ORDER_ITEM_PRICE;
        private int ORDER_ITEM_SCORE;
        private int SORT_ID;
        private String SORT_NM;
        private String USER_AVATAR;
        private int USER_ID;
        private String USER_NM;
        private int USE_STATUS;
        private int VAL;

        public int getACTIVE_VAL() {
            return ACTIVE_VAL;
        }

        public void setACTIVE_VAL(int ACTIVE_VAL) {
            this.ACTIVE_VAL = ACTIVE_VAL;
        }

        public int getACTIVITY_ID() {
            return ACTIVITY_ID;
        }

        public void setACTIVITY_ID(int ACTIVITY_ID) {
            this.ACTIVITY_ID = ACTIVITY_ID;
        }

        public int getACTIVITY_ITEM_ID() {
            return ACTIVITY_ITEM_ID;
        }

        public void setACTIVITY_ITEM_ID(int ACTIVITY_ITEM_ID) {
            this.ACTIVITY_ITEM_ID = ACTIVITY_ITEM_ID;
        }

        public String getACTIVITY_ITEM_TITLE() {
            return ACTIVITY_ITEM_TITLE;
        }

        public void setACTIVITY_ITEM_TITLE(String ACTIVITY_ITEM_TITLE) {
            this.ACTIVITY_ITEM_TITLE = ACTIVITY_ITEM_TITLE;
        }

        public int getACTIVITY_SORT_ID() {
            return ACTIVITY_SORT_ID;
        }

        public void setACTIVITY_SORT_ID(int ACTIVITY_SORT_ID) {
            this.ACTIVITY_SORT_ID = ACTIVITY_SORT_ID;
        }

        public String getACTIVITY_SORT_NM() {
            return ACTIVITY_SORT_NM;
        }

        public void setACTIVITY_SORT_NM(String ACTIVITY_SORT_NM) {
            this.ACTIVITY_SORT_NM = ACTIVITY_SORT_NM;
        }

        public String getACTIVITY_TITLE() {
            return ACTIVITY_TITLE;
        }

        public void setACTIVITY_TITLE(String ACTIVITY_TITLE) {
            this.ACTIVITY_TITLE = ACTIVITY_TITLE;
        }

        public int getBALANCE() {
            return BALANCE;
        }

        public void setBALANCE(int BALANCE) {
            this.BALANCE = BALANCE;
        }

        public String getBUY_TIME() {
            return BUY_TIME;
        }

        public void setBUY_TIME(String BUY_TIME) {
            this.BUY_TIME = BUY_TIME;
        }

        public String getEND_TIME() {
            return END_TIME;
        }

        public void setEND_TIME(String END_TIME) {
            this.END_TIME = END_TIME;
        }

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getIS_ENABLE() {
            return IS_ENABLE;
        }

        public void setIS_ENABLE(int IS_ENABLE) {
            this.IS_ENABLE = IS_ENABLE;
        }

        public int getORDER_ID() {
            return ORDER_ID;
        }

        public void setORDER_ID(int ORDER_ID) {
            this.ORDER_ID = ORDER_ID;
        }

        public int getORDER_ITEM_ID() {
            return ORDER_ITEM_ID;
        }

        public void setORDER_ITEM_ID(int ORDER_ITEM_ID) {
            this.ORDER_ITEM_ID = ORDER_ITEM_ID;
        }

        public int getORDER_ITEM_PRICE() {
            return ORDER_ITEM_PRICE;
        }

        public void setORDER_ITEM_PRICE(int ORDER_ITEM_PRICE) {
            this.ORDER_ITEM_PRICE = ORDER_ITEM_PRICE;
        }

        public int getORDER_ITEM_SCORE() {
            return ORDER_ITEM_SCORE;
        }

        public void setORDER_ITEM_SCORE(int ORDER_ITEM_SCORE) {
            this.ORDER_ITEM_SCORE = ORDER_ITEM_SCORE;
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

        public String getUSER_AVATAR() {
            return USER_AVATAR;
        }

        public void setUSER_AVATAR(String USER_AVATAR) {
            this.USER_AVATAR = USER_AVATAR;
        }

        public int getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(int USER_ID) {
            this.USER_ID = USER_ID;
        }

        public String getUSER_NM() {
            return USER_NM;
        }

        public void setUSER_NM(String USER_NM) {
            this.USER_NM = USER_NM;
        }

        public int getUSE_STATUS() {
            return USE_STATUS;
        }

        public void setUSE_STATUS(int USE_STATUS) {
            this.USE_STATUS = USE_STATUS;
        }

        public int getVAL() {
            return VAL;
        }

        public void setVAL(int VAL) {
            this.VAL = VAL;
        }
    }
}
