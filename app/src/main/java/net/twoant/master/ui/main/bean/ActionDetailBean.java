package net.twoant.master.ui.main.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by S_Y_H on 2016/12/26.
 * 活动详情bean
 */

public class ActionDetailBean implements Parcelable {


    /**
     * result : true
     * data : {"VAL":0,"START_TIME":null,"SCORE":100,"SORT_ID":0,"IS_COL":"1","SHOP_ID":9,"USER_ID":null,"ITEMS":[{"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":null,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":null,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":20,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null},{"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":0,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":0,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":42,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null}],"END_TIME":"2016-12-26 12:00:00","DESCRIPTION":null,"SHOP_TEL":null,"COVER_IMG":null,"ACTIVE_VAL":0,"ID":30,"CREATE_TIME":"2016-12-26 12:05:54","SORT_NM":"积分活动","SHOP_ADDRESS":"山东青岛胶南","PRICE":200,"SHOP_NM":"张店铺","LON":120.049086,"JOIN_QTY":0,"ITEM":{"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":null,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":null,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":20,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null},"IMGS":{"ACTIVITY_IMG2":null,"ACTIVITY_IMG_TXT":null,"ACTIVITY_IMG1":"activity_img/1480904658570d076488a.jpg","ACTIVITY_IMG4":null,"ACTIVITY_IMG3":null,"ID":23,"ACTIVITY_ID":30},"USER_NM":null,"ADDRESS":"山东青岛胶南","JOIN_QTY_LIMIT":50,"IS_JOIN":"0","TITLE":"活动标题XXX","CLICK":2,"SHOP_AVATAR":"1479351878490480c88f1.jpg","LAT":35.883327}
     * success : true
     * type : map
     * message : null
     */

    private boolean result;
    private DataBean data;
    private boolean success;
    private String type;
    private String message;

    public ActionDetailBean(){}

    private ActionDetailBean(Parcel in) {
        result = in.readByte() != 0;
        data = in.readParcelable(DataBean.class.getClassLoader());
        success = in.readByte() != 0;
        type = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (result ? 1 : 0));
        dest.writeParcelable(data, flags);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(type);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActionDetailBean> CREATOR = new Creator<ActionDetailBean>() {
        @Override
        public ActionDetailBean createFromParcel(Parcel in) {
            return new ActionDetailBean(in);
        }

        @Override
        public ActionDetailBean[] newArray(int size) {
            return new ActionDetailBean[size];
        }
    };

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public static class DataBean implements Parcelable {
        /**
         * VAL : 0
         * START_TIME : null
         * SCORE : 100
         * SORT_ID : 0
         * IS_COL : 1
         * SHOP_ID : 9
         * USER_ID : null
         * ITEMS : [{"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":null,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":null,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":20,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null},{"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":0,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":0,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":42,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null}]
         * END_TIME : 2016-12-26 12:00:00
         * DESCRIPTION : null
         * SHOP_TEL : null
         * COVER_IMG : null
         * ACTIVE_VAL : 0
         * ID : 30
         * CREATE_TIME : 2016-12-26 12:05:54
         * SORT_NM : 积分活动
         * SHOP_ADDRESS : 山东青岛胶南
         * PRICE : 200
         * SHOP_NM : 张店铺
         * LON : 120.049086
         * JOIN_QTY : 0
         * ITEM : {"VAL":null,"SORT_ID":null,"SCORE":100,"PRICE":null,"SHOP_ID":9,"SHOP_NM":"张店铺","ACTIVITY_ID":30,"JOIN_QTY":0,"ACTIVITY_COVER_IMG":null,"ACTIVITY_END_TIME":"2016-12-26 12:00:00","AJOIN_LIMIT":null,"DESCRIPTION":null,"JOIN_QTY_LIMIT":20,"ACTIVE_VAL":0,"TITLE":"送积分","ID":20,"ACTIVITY_START_TIME":null,"CLICK":null,"ACTIVITY_SORT_ID":0,"IDX":null}
         * IMGS : {"ACTIVITY_IMG2":null,"ACTIVITY_IMG_TXT":null,"ACTIVITY_IMG1":"activity_img/1480904658570d076488a.jpg","ACTIVITY_IMG4":null,"ACTIVITY_IMG3":null,"ID":23,"ACTIVITY_ID":30}
         * USER_NM : null
         * ADDRESS : 山东青岛胶南
         * JOIN_QTY_LIMIT : 50
         * IS_JOIN : 0
         * TITLE : 活动标题XXX
         * CLICK : 2
         * SHOP_AVATAR : 1479351878490480c88f1.jpg
         * LAT : 35.883327
         */

        private String _DISTANCE_TXT;
        private String VAL;
        private String START_TIME;
        private String SCORE;
        private int SORT_ID;
        private String IS_COL;
        private String SHOP_ID;
        private String USER_ID;
        private String END_TIME;
        private String DESCRIPTION;
        private String SHOP_TEL;
        private String COVER_IMG;
        private String ACTIVE_VAL;
        private String ID;
        private String CREATE_TIME;
        private String SORT_NM;
        private String SHOP_ADDRESS;
        private String PRICE;
        private String SHOP_NM;
        private String IS_ENABLE;
        private double LON;
        private int JOIN_QTY;
        private IMGSBean IMGS;
        private String USER_NM;
        private String ADDRESS;
        private int JOIN_QTY_LIMIT;
        private String IS_JOIN;
        private String TITLE;
        private String CLICK;
        private String SHOP_AVATAR;
        private double LAT;
        private List<ITEMSBean> ITEMS;
        private int IS_ENABLE_JOIN;

        protected DataBean(Parcel in) {
            _DISTANCE_TXT = in.readString();
            VAL = in.readString();
            START_TIME = in.readString();
            SCORE = in.readString();
            SORT_ID = in.readInt();
            IS_COL = in.readString();
            SHOP_ID = in.readString();
            USER_ID = in.readString();
            END_TIME = in.readString();
            DESCRIPTION = in.readString();
            SHOP_TEL = in.readString();
            COVER_IMG = in.readString();
            ACTIVE_VAL = in.readString();
            ID = in.readString();
            CREATE_TIME = in.readString();
            SORT_NM = in.readString();
            SHOP_ADDRESS = in.readString();
            PRICE = in.readString();
            SHOP_NM = in.readString();
            LON = in.readDouble();
            JOIN_QTY = in.readInt();
            IMGS = in.readParcelable(IMGSBean.class.getClassLoader());
            USER_NM = in.readString();
            ADDRESS = in.readString();
            JOIN_QTY_LIMIT = in.readInt();
            IS_JOIN = in.readString();
            TITLE = in.readString();
            CLICK = in.readString();
            SHOP_AVATAR = in.readString();
            LAT = in.readDouble();
            ITEMS = in.createTypedArrayList(ITEMSBean.CREATOR);
            IS_ENABLE = in.readString();
            IS_ENABLE_JOIN = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(_DISTANCE_TXT);
            dest.writeString(VAL);
            dest.writeString(START_TIME);
            dest.writeString(SCORE);
            dest.writeInt(SORT_ID);
            dest.writeString(IS_COL);
            dest.writeString(SHOP_ID);
            dest.writeString(USER_ID);
            dest.writeString(END_TIME);
            dest.writeString(DESCRIPTION);
            dest.writeString(SHOP_TEL);
            dest.writeString(COVER_IMG);
            dest.writeString(ACTIVE_VAL);
            dest.writeString(ID);
            dest.writeString(CREATE_TIME);
            dest.writeString(SORT_NM);
            dest.writeString(SHOP_ADDRESS);
            dest.writeString(PRICE);
            dest.writeString(SHOP_NM);
            dest.writeDouble(LON);
            dest.writeInt(JOIN_QTY);
            dest.writeParcelable(IMGS, flags);
            dest.writeString(USER_NM);
            dest.writeString(ADDRESS);
            dest.writeInt(JOIN_QTY_LIMIT);
            dest.writeString(IS_JOIN);
            dest.writeString(TITLE);
            dest.writeString(CLICK);
            dest.writeString(SHOP_AVATAR);
            dest.writeDouble(LAT);
            dest.writeTypedList(ITEMS);
            dest.writeString(IS_ENABLE);
            dest.writeInt(IS_ENABLE_JOIN);
        }

        public String get_DISTANCE_TXT() {
            return _DISTANCE_TXT;
        }

        public void set_DISTANCE_TXT(String _DISTANCE_TXT) {
            this._DISTANCE_TXT = _DISTANCE_TXT;
        }

        public int getIS_ENABLE_JOIN() {
            return IS_ENABLE_JOIN;
        }

        public void setIS_ENABLE_JOIN(int IS_ENABLE_JOIN) {
            this.IS_ENABLE_JOIN = IS_ENABLE_JOIN;
        }

        public String getIS_ENABLE() {
            return IS_ENABLE;
        }

        public void setIS_ENABLE(String IS_ENABLE) {
            this.IS_ENABLE = IS_ENABLE;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel in) {
                return new DataBean(in);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };

        public String getVAL() {
            return VAL;
        }

        public void setVAL(String VAL) {
            this.VAL = VAL;
        }

        public String getSTART_TIME() {
            return START_TIME;
        }

        public void setSTART_TIME(String START_TIME) {
            this.START_TIME = START_TIME;
        }

        public String getSCORE() {
            return SCORE;
        }

        public void setSCORE(String SCORE) {
            this.SCORE = SCORE;
        }

        public int getSORT_ID() {
            return SORT_ID;
        }

        public void setSORT_ID(int SORT_ID) {
            this.SORT_ID = SORT_ID;
        }

        public String getIS_COL() {
            return IS_COL;
        }

        public void setIS_COL(String IS_COL) {
            this.IS_COL = IS_COL;
        }

        public String getSHOP_ID() {
            return SHOP_ID;
        }

        public void setSHOP_ID(String SHOP_ID) {
            this.SHOP_ID = SHOP_ID;
        }

        public String getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(String USER_ID) {
            this.USER_ID = USER_ID;
        }

        public String getEND_TIME() {
            return END_TIME;
        }

        public void setEND_TIME(String END_TIME) {
            this.END_TIME = END_TIME;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public String getSHOP_TEL() {
            return SHOP_TEL;
        }

        public void setSHOP_TEL(String SHOP_TEL) {
            this.SHOP_TEL = SHOP_TEL;
        }

        public String getCOVER_IMG() {
            return COVER_IMG;
        }

        public void setCOVER_IMG(String COVER_IMG) {
            this.COVER_IMG = COVER_IMG;
        }

        public String getACTIVE_VAL() {
            return ACTIVE_VAL;
        }

        public void setACTIVE_VAL(String ACTIVE_VAL) {
            this.ACTIVE_VAL = ACTIVE_VAL;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getCREATE_TIME() {
            return CREATE_TIME;
        }

        public void setCREATE_TIME(String CREATE_TIME) {
            this.CREATE_TIME = CREATE_TIME;
        }

        public String getSORT_NM() {
            return SORT_NM;
        }

        public void setSORT_NM(String SORT_NM) {
            this.SORT_NM = SORT_NM;
        }

        public String getSHOP_ADDRESS() {
            return SHOP_ADDRESS;
        }

        public void setSHOP_ADDRESS(String SHOP_ADDRESS) {
            this.SHOP_ADDRESS = SHOP_ADDRESS;
        }

        public String getPRICE() {
            return PRICE;
        }

        public void setPRICE(String PRICE) {
            this.PRICE = PRICE;
        }

        public String getSHOP_NM() {
            return SHOP_NM;
        }

        public void setSHOP_NM(String SHOP_NM) {
            this.SHOP_NM = SHOP_NM;
        }

        public double getLON() {
            return LON;
        }

        public void setLON(double LON) {
            this.LON = LON;
        }

        public int getJOIN_QTY() {
            return JOIN_QTY;
        }

        public void setJOIN_QTY(int JOIN_QTY) {
            this.JOIN_QTY = JOIN_QTY;
        }

        public IMGSBean getIMGS() {
            return IMGS;
        }

        public void setIMGS(IMGSBean IMGS) {
            this.IMGS = IMGS;
        }

        public String getUSER_NM() {
            return USER_NM;
        }

        public void setUSER_NM(String USER_NM) {
            this.USER_NM = USER_NM;
        }

        public String getADDRESS() {
            return ADDRESS;
        }

        public void setADDRESS(String ADDRESS) {
            this.ADDRESS = ADDRESS;
        }

        public int getJOIN_QTY_LIMIT() {
            return JOIN_QTY_LIMIT;
        }

        public void setJOIN_QTY_LIMIT(int JOIN_QTY_LIMIT) {
            this.JOIN_QTY_LIMIT = JOIN_QTY_LIMIT;
        }

        public String getIS_JOIN() {
            return IS_JOIN;
        }

        public void setIS_JOIN(String IS_JOIN) {
            this.IS_JOIN = IS_JOIN;
        }

        public String getTITLE() {
            return TITLE;
        }

        public void setTITLE(String TITLE) {
            this.TITLE = TITLE;
        }

        public String getCLICK() {
            return CLICK;
        }

        public void setCLICK(String CLICK) {
            this.CLICK = CLICK;
        }

        public String getSHOP_AVATAR() {
            return SHOP_AVATAR;
        }

        public void setSHOP_AVATAR(String SHOP_AVATAR) {
            this.SHOP_AVATAR = SHOP_AVATAR;
        }

        public double getLAT() {
            return LAT;
        }

        public void setLAT(double LAT) {
            this.LAT = LAT;
        }

        public List<ITEMSBean> getITEMS() {
            return ITEMS;
        }

        public void setITEMS(List<ITEMSBean> ITEMS) {
            this.ITEMS = ITEMS;
        }

        public static class IMGSBean implements Parcelable {
            /**
             * ACTIVITY_IMG2 : null
             * ACTIVITY_IMG_TXT : null
             * ACTIVITY_IMG1 : activity_img/1480904658570d076488a.jpg
             * ACTIVITY_IMG4 : null
             * ACTIVITY_IMG3 : null
             * ID : 23
             * ACTIVITY_ID : 30
             */

            private String ACTIVITY_IMG2;
            private String ACTIVITY_IMG_TXT;
            private String ACTIVITY_IMG1;
            private String ACTIVITY_IMG4;
            private String ACTIVITY_IMG3;
            private String ID;
            private String ACTIVITY_ID;

            private IMGSBean(Parcel in) {
                ACTIVITY_IMG2 = in.readString();
                ACTIVITY_IMG_TXT = in.readString();
                ACTIVITY_IMG1 = in.readString();
                ACTIVITY_IMG4 = in.readString();
                ACTIVITY_IMG3 = in.readString();
                ID = in.readString();
                ACTIVITY_ID = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(ACTIVITY_IMG2);
                dest.writeString(ACTIVITY_IMG_TXT);
                dest.writeString(ACTIVITY_IMG1);
                dest.writeString(ACTIVITY_IMG4);
                dest.writeString(ACTIVITY_IMG3);
                dest.writeString(ID);
                dest.writeString(ACTIVITY_ID);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public static final Creator<IMGSBean> CREATOR = new Creator<IMGSBean>() {
                @Override
                public IMGSBean createFromParcel(Parcel in) {
                    return new IMGSBean(in);
                }

                @Override
                public IMGSBean[] newArray(int size) {
                    return new IMGSBean[size];
                }
            };

            public ArrayList<String> getList() {
                ArrayList<String> imgUrl = new ArrayList<>();
                if (ACTIVITY_IMG1 != null)
                    imgUrl.add(ACTIVITY_IMG1);
                if (ACTIVITY_IMG2 != null)
                    imgUrl.add(ACTIVITY_IMG2);
                if (ACTIVITY_IMG3 != null)
                    imgUrl.add(ACTIVITY_IMG3);
                if (ACTIVITY_IMG4 != null)
                    imgUrl.add(ACTIVITY_IMG4);
                return imgUrl;
            }


            public String getACTIVITY_IMG2() {
                return ACTIVITY_IMG2;
            }

            public void setACTIVITY_IMG2(String ACTIVITY_IMG2) {
                this.ACTIVITY_IMG2 = ACTIVITY_IMG2;
            }

            public String getACTIVITY_IMG_TXT() {
                return ACTIVITY_IMG_TXT;
            }

            public void setACTIVITY_IMG_TXT(String ACTIVITY_IMG_TXT) {
                this.ACTIVITY_IMG_TXT = ACTIVITY_IMG_TXT;
            }

            public String getACTIVITY_IMG1() {
                return ACTIVITY_IMG1;
            }

            public void setACTIVITY_IMG1(String ACTIVITY_IMG1) {
                this.ACTIVITY_IMG1 = ACTIVITY_IMG1;
            }

            public String getACTIVITY_IMG4() {
                return ACTIVITY_IMG4;
            }

            public void setACTIVITY_IMG4(String ACTIVITY_IMG4) {
                this.ACTIVITY_IMG4 = ACTIVITY_IMG4;
            }

            public String getACTIVITY_IMG3() {
                return ACTIVITY_IMG3;
            }

            public void setACTIVITY_IMG3(String ACTIVITY_IMG3) {
                this.ACTIVITY_IMG3 = ACTIVITY_IMG3;
            }

            public String getID() {
                return ID;
            }

            public void setID(String ID) {
                this.ID = ID;
            }

            public String getACTIVITY_ID() {
                return ACTIVITY_ID;
            }

            public void setACTIVITY_ID(String ACTIVITY_ID) {
                this.ACTIVITY_ID = ACTIVITY_ID;
            }

        }

        public static class ITEMSBean implements Parcelable {
            /**
             * VAL : null
             * SORT_ID : null
             * SCORE : 100
             * PRICE : null
             * SHOP_ID : 9
             * SHOP_NM : 张店铺
             * ACTIVITY_ID : 30
             * JOIN_QTY : 0
             * ACTIVITY_COVER_IMG : null
             * ACTIVITY_END_TIME : 2016-12-26 12:00:00
             * AJOIN_LIMIT : null
             * DESCRIPTION : null
             * JOIN_QTY_LIMIT : 20
             * ACTIVE_VAL : 0
             * TITLE : 送积分
             * ID : 20
             * ACTIVITY_START_TIME : null
             * CLICK : null
             * ACTIVITY_SORT_ID : 0
             * IDX : null
             */

            private String VAL;
            private String SORT_ID;
            private String SCORE;
            private String PRICE;
            private String SHOP_ID;
            private String SHOP_NM;
            private String ACTIVITY_ID;
            private String JOIN_QTY;
            private String ACTIVITY_COVER_IMG;
            private String ACTIVITY_END_TIME;
            private String AJOIN_LIMIT;
            private String DESCRIPTION;
            private String JOIN_QTY_LIMIT;
            private String ACTIVE_VAL;
            private String TITLE;
            private String ID;
            private String ACTIVITY_START_TIME;
            private String CLICK;
            private int ACTIVITY_SORT_ID;
            private String IDX;


            private ITEMSBean(Parcel in) {
                VAL = in.readString();
                SORT_ID = in.readString();
                SCORE = in.readString();
                PRICE = in.readString();
                SHOP_ID = in.readString();
                SHOP_NM = in.readString();
                ACTIVITY_ID = in.readString();
                JOIN_QTY = in.readString();
                ACTIVITY_COVER_IMG = in.readString();
                ACTIVITY_END_TIME = in.readString();
                AJOIN_LIMIT = in.readString();
                DESCRIPTION = in.readString();
                JOIN_QTY_LIMIT = in.readString();
                ACTIVE_VAL = in.readString();
                TITLE = in.readString();
                ID = in.readString();
                ACTIVITY_START_TIME = in.readString();
                CLICK = in.readString();
                ACTIVITY_SORT_ID = in.readInt();
                IDX = in.readString();
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(VAL);
                dest.writeString(SORT_ID);
                dest.writeString(SCORE);
                dest.writeString(PRICE);
                dest.writeString(SHOP_ID);
                dest.writeString(SHOP_NM);
                dest.writeString(ACTIVITY_ID);
                dest.writeString(JOIN_QTY);
                dest.writeString(ACTIVITY_COVER_IMG);
                dest.writeString(ACTIVITY_END_TIME);
                dest.writeString(AJOIN_LIMIT);
                dest.writeString(DESCRIPTION);
                dest.writeString(JOIN_QTY_LIMIT);
                dest.writeString(ACTIVE_VAL);
                dest.writeString(TITLE);
                dest.writeString(ID);
                dest.writeString(ACTIVITY_START_TIME);
                dest.writeString(CLICK);
                dest.writeInt(ACTIVITY_SORT_ID);
                dest.writeString(IDX);
            }

            public static final Creator<ITEMSBean> CREATOR = new Creator<ITEMSBean>() {
                @Override
                public ITEMSBean createFromParcel(Parcel in) {
                    return new ITEMSBean(in);
                }

                @Override
                public ITEMSBean[] newArray(int size) {
                    return new ITEMSBean[size];
                }
            };

            public String getVAL() {
                return VAL;
            }

            public void setVAL(String VAL) {
                this.VAL = VAL;
            }

            public String getSORT_ID() {
                return SORT_ID;
            }

            public void setSORT_ID(String SORT_ID) {
                this.SORT_ID = SORT_ID;
            }

            public String getSCORE() {
                return SCORE;
            }

            public void setSCORE(String SCORE) {
                this.SCORE = SCORE;
            }

            public String getPRICE() {
                return PRICE;
            }

            public void setPRICE(String PRICE) {
                this.PRICE = PRICE;
            }

            public String getSHOP_ID() {
                return SHOP_ID;
            }

            public void setSHOP_ID(String SHOP_ID) {
                this.SHOP_ID = SHOP_ID;
            }

            public String getSHOP_NM() {
                return SHOP_NM;
            }

            public void setSHOP_NM(String SHOP_NM) {
                this.SHOP_NM = SHOP_NM;
            }

            public String getACTIVITY_ID() {
                return ACTIVITY_ID;
            }

            public void setACTIVITY_ID(String ACTIVITY_ID) {
                this.ACTIVITY_ID = ACTIVITY_ID;
            }

            public String getJOIN_QTY() {
                return JOIN_QTY;
            }

            public void setJOIN_QTY(String JOIN_QTY) {
                this.JOIN_QTY = JOIN_QTY;
            }

            public String getACTIVITY_COVER_IMG() {
                return ACTIVITY_COVER_IMG;
            }

            public void setACTIVITY_COVER_IMG(String ACTIVITY_COVER_IMG) {
                this.ACTIVITY_COVER_IMG = ACTIVITY_COVER_IMG;
            }

            public String getACTIVITY_END_TIME() {
                return ACTIVITY_END_TIME;
            }

            public void setACTIVITY_END_TIME(String ACTIVITY_END_TIME) {
                this.ACTIVITY_END_TIME = ACTIVITY_END_TIME;
            }

            public String getAJOIN_LIMIT() {
                return AJOIN_LIMIT;
            }

            public void setAJOIN_LIMIT(String AJOIN_LIMIT) {
                this.AJOIN_LIMIT = AJOIN_LIMIT;
            }

            public String getDESCRIPTION() {
                return DESCRIPTION;
            }

            public void setDESCRIPTION(String DESCRIPTION) {
                this.DESCRIPTION = DESCRIPTION;
            }

            public String getJOIN_QTY_LIMIT() {
                return JOIN_QTY_LIMIT;
            }

            public void setJOIN_QTY_LIMIT(String JOIN_QTY_LIMIT) {
                this.JOIN_QTY_LIMIT = JOIN_QTY_LIMIT;
            }

            public String getACTIVE_VAL() {
                return ACTIVE_VAL;
            }

            public void setACTIVE_VAL(String ACTIVE_VAL) {
                this.ACTIVE_VAL = ACTIVE_VAL;
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

            public String getACTIVITY_START_TIME() {
                return ACTIVITY_START_TIME;
            }

            public void setACTIVITY_START_TIME(String ACTIVITY_START_TIME) {
                this.ACTIVITY_START_TIME = ACTIVITY_START_TIME;
            }

            public String getCLICK() {
                return CLICK;
            }

            public void setCLICK(String CLICK) {
                this.CLICK = CLICK;
            }

            public int getACTIVITY_SORT_ID() {
                return ACTIVITY_SORT_ID;
            }

            public void setACTIVITY_SORT_ID(int ACTIVITY_SORT_ID) {
                this.ACTIVITY_SORT_ID = ACTIVITY_SORT_ID;
            }

            public String getIDX() {
                return IDX;
            }

            public void setIDX(String IDX) {
                this.IDX = IDX;
            }

            @Override
            public int describeContents() {
                return 0;
            }

        }
    }
}
