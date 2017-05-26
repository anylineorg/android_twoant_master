package net.twoant.master.ui.charge.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/22.
 * 佛祖保佑   永无BUG
 */

public class MyActivityDetailFinishBean {

    /**
     * data : {"ADDRESS":"月亮","CLICK":4,"COVER_IMG":"/ig?id=2666","CREATE_TIME":"2017-01-04 03:28:32","DESCRIPTION":"活动藏青","END_TIME":"2017-12-04 03:27:00","ID":19,"IS_ENABLE":1,"ITEMS":[],"JOIN_QTY_LIMIT":0,"LAT":36.849547,"LON":119.86934,"SHOP_ADDRESS":"月亮","SHOP_AVATAR":"/shop_img/1480556808397f9ac1619.jpg","SHOP_ID":13,"SHOP_NM":"店铺","SHOP_TEL":13455521312,"SORT_ID":1,"SORT_NM":"收费活动","START_TIME":"2017-01-04 03:27:00","TITLE":"收费活动大标题","USER_ID":-1,"USES":[{"ACTIVITY_ID":19,"ACTIVITY_ITEM_ID":120,"ACTIVITY_ITEM_TITLE":"收费小标题","ID":34,"LOG_DATE":"2017-01-04","LOG_TIME":"2017-01-04 03:46:14","ORDER_ITEM_PRICE":100,"ORDER_ITEM_SCORE":200,"PK_ID":177,"SHOP_ID":13,"SHOP_NM":"店铺","SORT_ID":2240,"SORT_NM":"用户消费出账代金券","USER_ID":62,"USER_NM":"17660635389","VAL":105}]}
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
         * ADDRESS : 月亮
         * CLICK : 4
         * COVER_IMG : /ig?id=2666
         * CREATE_TIME : 2017-01-04 03:28:32
         * DESCRIPTION : 活动藏青
         * END_TIME : 2017-12-04 03:27:00
         * ID : 19
         * IS_ENABLE : 1
         * ITEMS : []
         * JOIN_QTY_LIMIT : 0
         * LAT : 36.849547
         * LON : 119.86934
         * SHOP_ADDRESS : 月亮
         * SHOP_AVATAR : /shop_img/1480556808397f9ac1619.jpg
         * SHOP_ID : 13
         * SHOP_NM : 店铺
         * SHOP_TEL : 13455521312
         * SORT_ID : 1
         * SORT_NM : 收费活动
         * START_TIME : 2017-01-04 03:27:00
         * TITLE : 收费活动大标题
         * USER_ID : -1
         * USES : [{"ACTIVITY_ID":19,"ACTIVITY_ITEM_ID":120,"ACTIVITY_ITEM_TITLE":"收费小标题","ID":34,"LOG_DATE":"2017-01-04","LOG_TIME":"2017-01-04 03:46:14","ORDER_ITEM_PRICE":100,"ORDER_ITEM_SCORE":200,"PK_ID":177,"SHOP_ID":13,"SHOP_NM":"店铺","SORT_ID":2240,"SORT_NM":"用户消费出账代金券","USER_ID":62,"USER_NM":"17660635389","VAL":105}]
         */

        private String ADDRESS;
        private int CLICK;
        private String COVER_IMG;
        private String CREATE_TIME;
        private String DESCRIPTION;
        private String END_TIME;
        private int ID;
        private int IS_ENABLE;
        private int JOIN_QTY_LIMIT;
        private double LAT;
        private double LON;
        private String SHOP_ADDRESS;
        private String SHOP_AVATAR;
        private int SHOP_ID;
        private String SHOP_NM;
        private long SHOP_TEL;
        private int SORT_ID;
        private String SORT_NM;
        private String START_TIME;
        private String TITLE;
        private int USER_ID;
        private List<?> ITEMS;
        private List<USESBean> USES;

        public String getADDRESS() {
            return ADDRESS;
        }

        public void setADDRESS(String ADDRESS) {
            this.ADDRESS = ADDRESS;
        }

        public int getCLICK() {
            return CLICK;
        }

        public void setCLICK(int CLICK) {
            this.CLICK = CLICK;
        }

        public String getCOVER_IMG() {
            return COVER_IMG;
        }

        public void setCOVER_IMG(String COVER_IMG) {
            this.COVER_IMG = COVER_IMG;
        }

        public String getCREATE_TIME() {
            return CREATE_TIME;
        }

        public void setCREATE_TIME(String CREATE_TIME) {
            this.CREATE_TIME = CREATE_TIME;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
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

        public int getJOIN_QTY_LIMIT() {
            return JOIN_QTY_LIMIT;
        }

        public void setJOIN_QTY_LIMIT(int JOIN_QTY_LIMIT) {
            this.JOIN_QTY_LIMIT = JOIN_QTY_LIMIT;
        }

        public double getLAT() {
            return LAT;
        }

        public void setLAT(double LAT) {
            this.LAT = LAT;
        }

        public double getLON() {
            return LON;
        }

        public void setLON(double LON) {
            this.LON = LON;
        }

        public String getSHOP_ADDRESS() {
            return SHOP_ADDRESS;
        }

        public void setSHOP_ADDRESS(String SHOP_ADDRESS) {
            this.SHOP_ADDRESS = SHOP_ADDRESS;
        }

        public String getSHOP_AVATAR() {
            return SHOP_AVATAR;
        }

        public void setSHOP_AVATAR(String SHOP_AVATAR) {
            this.SHOP_AVATAR = SHOP_AVATAR;
        }

        public int getSHOP_ID() {
            return SHOP_ID;
        }

        public void setSHOP_ID(int SHOP_ID) {
            this.SHOP_ID = SHOP_ID;
        }

        public String getSHOP_NM() {
            return SHOP_NM;
        }

        public void setSHOP_NM(String SHOP_NM) {
            this.SHOP_NM = SHOP_NM;
        }

        public long getSHOP_TEL() {
            return SHOP_TEL;
        }

        public void setSHOP_TEL(long SHOP_TEL) {
            this.SHOP_TEL = SHOP_TEL;
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

        public String getSTART_TIME() {
            return START_TIME;
        }

        public void setSTART_TIME(String START_TIME) {
            this.START_TIME = START_TIME;
        }

        public String getTITLE() {
            return TITLE;
        }

        public void setTITLE(String TITLE) {
            this.TITLE = TITLE;
        }

        public int getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(int USER_ID) {
            this.USER_ID = USER_ID;
        }

        public List<?> getITEMS() {
            return ITEMS;
        }

        public void setITEMS(List<?> ITEMS) {
            this.ITEMS = ITEMS;
        }

        public List<USESBean> getUSES() {
            return USES;
        }

        public void setUSES(List<USESBean> USES) {
            this.USES = USES;
        }

        public static class USESBean {
            /**
             * ACTIVITY_ID : 19
             * ACTIVITY_ITEM_ID : 120
             * ACTIVITY_ITEM_TITLE : 收费小标题
             * ID : 34
             * LOG_DATE : 2017-01-04
             * LOG_TIME : 2017-01-04 03:46:14
             * ORDER_ITEM_PRICE : 100
             * ORDER_ITEM_SCORE : 200
             * PK_ID : 177
             * SHOP_ID : 13
             * SHOP_NM : 店铺
             * SORT_ID : 2240
             * SORT_NM : 用户消费出账代金券
             * USER_ID : 62
             * USER_NM : 17660635389
             * VAL : 105
             */

            private int ACTIVITY_ID;
            private int ACTIVITY_ITEM_ID;
            private String ACTIVITY_ITEM_TITLE;
            private int ID;
            private String LOG_DATE;
            private String LOG_TIME;
            private int ORDER_ITEM_PRICE;
            private int ORDER_ITEM_SCORE;
            private int PK_ID;
            private int SHOP_ID;
            private String SHOP_NM;
            private int SORT_ID;
            private String SORT_NM;
            private int USER_ID;
            private String USER_NM;
            private int VAL;

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

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getLOG_DATE() {
                return LOG_DATE;
            }

            public void setLOG_DATE(String LOG_DATE) {
                this.LOG_DATE = LOG_DATE;
            }

            public String getLOG_TIME() {
                return LOG_TIME;
            }

            public void setLOG_TIME(String LOG_TIME) {
                this.LOG_TIME = LOG_TIME;
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

            public int getPK_ID() {
                return PK_ID;
            }

            public void setPK_ID(int PK_ID) {
                this.PK_ID = PK_ID;
            }

            public int getSHOP_ID() {
                return SHOP_ID;
            }

            public void setSHOP_ID(int SHOP_ID) {
                this.SHOP_ID = SHOP_ID;
            }

            public String getSHOP_NM() {
                return SHOP_NM;
            }

            public void setSHOP_NM(String SHOP_NM) {
                this.SHOP_NM = SHOP_NM;
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

            public int getVAL() {
                return VAL;
            }

            public void setVAL(int VAL) {
                this.VAL = VAL;
            }
        }
    }
}
