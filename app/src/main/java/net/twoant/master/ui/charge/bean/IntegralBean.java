package net.twoant.master.ui.charge.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/28.
 * 佛祖保佑   永无BUG
 */

public class IntegralBean {

    /**
     * result : true
     * data : [{"VAL":100,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":974,"LOG_TIME":"2017-01-05 06:53:22","FK_ID":691,"USER_ID":964,"SHOP_NM":"测试商家2","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":41,"SHOP_AVATAR":"/ig?id=2669","SORT_NM":"用户消费入帐积分"},{"VAL":11,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":972,"LOG_TIME":"2017-01-05 05:26:26","FK_ID":664,"USER_ID":964,"SHOP_NM":"测试商家","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":26,"SHOP_AVATAR":"/ig?id=2632","SORT_NM":"用户消费入帐积分"},{"VAL":1.5,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":976,"LOG_TIME":"2017-01-05 05:24:03","FK_ID":657,"USER_ID":964,"SHOP_NM":"八颗牙齿晒太阳","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":19,"SHOP_AVATAR":"/shop_img/1483600336103ada049cb.jpg","SORT_NM":"用户消费入帐积分"},{"VAL":530,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":976,"LOG_TIME":"2017-01-05 04:49:35","FK_ID":637,"USER_ID":964,"SHOP_NM":"八颗牙齿晒太阳","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":11,"SHOP_AVATAR":"/shop_img/1483600336103ada049cb.jpg","SORT_NM":"用户消费入帐积分"},{"VAL":89,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":976,"LOG_TIME":"2017-01-05 04:46:31","FK_ID":636,"USER_ID":964,"SHOP_NM":"八颗牙齿晒太阳","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":10,"SHOP_AVATAR":"/shop_img/1483600336103ada049cb.jpg","SORT_NM":"用户消费入帐积分"},{"VAL":295,"LOG_DATE":"2017-01-05","SORT_ID":1230,"SHOP_ID":976,"LOG_TIME":"2017-01-05 03:57:51","FK_ID":623,"USER_ID":964,"SHOP_NM":"八颗牙齿晒太阳","USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":4,"SHOP_AVATAR":"/shop_img/1483600336103ada049cb.jpg","SORT_NM":"用户消费入帐积分"},{"VAL":40000,"LOG_DATE":"2017-01-05","SORT_ID":1231,"SHOP_ID":null,"LOG_TIME":"2017-01-05 03:56:44","FK_ID":622,"USER_ID":964,"SHOP_NM":null,"USER_NM":"18677176100","PK_ID":null,"TITLE":null,"ID":3,"SHOP_AVATAR":null,"SORT_NM":"用户现金购买积分"}]
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
         * VAL : 100
         * LOG_DATE : 2017-01-05
         * SORT_ID : 1230
         * SHOP_ID : 974
         * LOG_TIME : 2017-01-05 06:53:22
         * FK_ID : 691
         * USER_ID : 964
         * SHOP_NM : 测试商家2
         * USER_NM : 18677176100
         * PK_ID : null
         * TITLE : null
         * ID : 41
         * SHOP_AVATAR : /ig?id=2669
         * SORT_NM : 用户消费入帐积分
         */

        private String VAL;
        private String LOG_DATE;
        private String SORT_ID;
        private String SHOP_ID;
        private String LOG_TIME;
        private String FK_ID;
        private String USER_ID;
        private String SHOP_NM;
        private String USER_NM;
        private Object PK_ID;
        private String TITLE;
        private String ID;
        private String SHOP_AVATAR;
        private String SORT_NM;

        public String getVAL() {
            return VAL;
        }

        public void setVAL(String VAL) {
            this.VAL = VAL;
        }

        public String getLOG_DATE() {
            return LOG_DATE;
        }

        public void setLOG_DATE(String LOG_DATE) {
            this.LOG_DATE = LOG_DATE;
        }

        public String getSORT_ID() {
            return SORT_ID;
        }

        public void setSORT_ID(String SORT_ID) {
            this.SORT_ID = SORT_ID;
        }

        public String getSHOP_ID() {
            return SHOP_ID;
        }

        public void setSHOP_ID(String SHOP_ID) {
            this.SHOP_ID = SHOP_ID;
        }

        public String getLOG_TIME() {
            return LOG_TIME;
        }

        public void setLOG_TIME(String LOG_TIME) {
            this.LOG_TIME = LOG_TIME;
        }

        public String getFK_ID() {
            return FK_ID;
        }

        public void setFK_ID(String FK_ID) {
            this.FK_ID = FK_ID;
        }

        public String getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(String USER_ID) {
            this.USER_ID = USER_ID;
        }

        public String getSHOP_NM() {
            return SHOP_NM;
        }

        public void setSHOP_NM(String SHOP_NM) {
            this.SHOP_NM = SHOP_NM;
        }

        public String getUSER_NM() {
            return USER_NM;
        }

        public void setUSER_NM(String USER_NM) {
            this.USER_NM = USER_NM;
        }

        public Object getPK_ID() {
            return PK_ID;
        }

        public void setPK_ID(Object PK_ID) {
            this.PK_ID = PK_ID;
        }

        public String getTITLE() {
            return TITLE;
        }

        public void setTITLE(String TITLE) {
            this.TITLE = TITLE;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getSHOP_AVATAR() {
            return SHOP_AVATAR;
        }

        public void setSHOP_AVATAR(String SHOP_AVATAR) {
            this.SHOP_AVATAR = SHOP_AVATAR;
        }

        public String getSORT_NM() {
            return SORT_NM;
        }

        public void setSORT_NM(String SORT_NM) {
            this.SORT_NM = SORT_NM;
        }
    }
}
