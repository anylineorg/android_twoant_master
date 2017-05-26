package net.twoant.master.ui.charge.bean;

/**
 * Created by DZY on 2016/12/29.
 * 佛祖保佑   永无BUG
 */

public class ChargeBean {

    /**
     * result : true
     * code : 200
     * data : {"REG_TIME":"2017-01-09 09:35:16","UPT_CLIENT_ID":null,"PURSE_FROZEN":0,"SCORE_BALANCE":200,"SCORE_FROZEN":0,"USER_ID":197,"REG_IP":null,"PURSE_IN":0,"CASH_OUT":0,"VOUCHER_CNT":4,"BANKCARD_CNT":0,"CASH_IN":0,"UPT_IP":null,"VOUCHER_BALANCE":260,"ID":208,"UPT_ID":null,"IDX":null,"UPT_TIME":"2017-01-09 09:35:16","REMARK":null,"REG_ID":null,"PURSE_WITHDRAW":0,"SCORE_IN":1000,"REG_CLIENT_ID":null,"PURSE_BALANCE":0,"VOUCHER_IN":260,"VOUCHER_OUT":0,"PURSE_OUT":0,"SCORE_OUT":800,"DATA_STATUS":1,"CODE":null,"CASH_BALANCE":0,"VOUCHER_FROZEN":0,"CASH_FROZEN":0}
     * success : true
     * type : map
     * message : null
     */

    private boolean result;
    private String code;
    private DataBean data;
    private boolean success;
    private String type;
    private Object message;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * REG_TIME : 2017-01-09 09:35:16
         * UPT_CLIENT_ID : null
         * PURSE_FROZEN : 0
         * SCORE_BALANCE : 200
         * SCORE_FROZEN : 0
         * USER_ID : 197
         * REG_IP : null
         * PURSE_IN : 0
         * CASH_OUT : 0
         * VOUCHER_CNT : 4
         * BANKCARD_CNT : 0
         * CASH_IN : 0
         * UPT_IP : null
         * VOUCHER_BALANCE : 260
         * ID : 208
         * UPT_ID : null
         * IDX : null
         * UPT_TIME : 2017-01-09 09:35:16
         * REMARK : null
         * REG_ID : null
         * PURSE_WITHDRAW : 0
         * SCORE_IN : 1000
         * REG_CLIENT_ID : null
         * PURSE_BALANCE : 0
         * VOUCHER_IN : 260
         * VOUCHER_OUT : 0
         * PURSE_OUT : 0
         * SCORE_OUT : 800
         * DATA_STATUS : 1
         * CODE : null
         * CASH_BALANCE : 0
         * VOUCHER_FROZEN : 0
         * CASH_FROZEN : 0
         */

        private String REG_TIME;
        private Object UPT_CLIENT_ID;
        private String PURSE_FROZEN;
        private String SCORE_BALANCE;
        private String SCORE_FROZEN;
        private String USER_ID;
        private Object REG_IP;
        private String PURSE_IN;
        private String CASH_OUT;
        private String VOUCHER_CNT;
        private String BANKCARD_CNT;
        private String CASH_IN;
        private Object UPT_IP;
        private String VOUCHER_BALANCE;
        private String ID;
        private Object UPT_ID;
        private Object IDX;
        private String UPT_TIME;
        private Object REMARK;
        private Object REG_ID;
        private String PURSE_WITHDRAW;
        private String SCORE_IN;
        private Object REG_CLIENT_ID;
        private String PURSE_BALANCE;
        private String VOUCHER_IN;
        private String VOUCHER_OUT;
        private String PURSE_OUT;
        private String SCORE_OUT;
        private String DATA_STATUS;
        private Object CODE;
        private String CASH_BALANCE;
        private String VOUCHER_FROZEN;
        private String CASH_FROZEN;

        public String getREG_TIME() {
            return REG_TIME;
        }

        public void setREG_TIME(String REG_TIME) {
            this.REG_TIME = REG_TIME;
        }

        public Object getUPT_CLIENT_ID() {
            return UPT_CLIENT_ID;
        }

        public void setUPT_CLIENT_ID(Object UPT_CLIENT_ID) {
            this.UPT_CLIENT_ID = UPT_CLIENT_ID;
        }

        public String getPURSE_FROZEN() {
            return PURSE_FROZEN;
        }

        public void setPURSE_FROZEN(String PURSE_FROZEN) {
            this.PURSE_FROZEN = PURSE_FROZEN;
        }

        public String getSCORE_BALANCE() {
            return SCORE_BALANCE;
        }

        public void setSCORE_BALANCE(String SCORE_BALANCE) {
            this.SCORE_BALANCE = SCORE_BALANCE;
        }

        public String getSCORE_FROZEN() {
            return SCORE_FROZEN;
        }

        public void setSCORE_FROZEN(String SCORE_FROZEN) {
            this.SCORE_FROZEN = SCORE_FROZEN;
        }

        public String getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(String USER_ID) {
            this.USER_ID = USER_ID;
        }

        public Object getREG_IP() {
            return REG_IP;
        }

        public void setREG_IP(Object REG_IP) {
            this.REG_IP = REG_IP;
        }

        public String getPURSE_IN() {
            return PURSE_IN;
        }

        public void setPURSE_IN(String PURSE_IN) {
            this.PURSE_IN = PURSE_IN;
        }

        public String getCASH_OUT() {
            return CASH_OUT;
        }

        public void setCASH_OUT(String CASH_OUT) {
            this.CASH_OUT = CASH_OUT;
        }

        public String getVOUCHER_CNT() {
            return VOUCHER_CNT;
        }

        public void setVOUCHER_CNT(String VOUCHER_CNT) {
            this.VOUCHER_CNT = VOUCHER_CNT;
        }

        public String getBANKCARD_CNT() {
            return BANKCARD_CNT;
        }

        public void setBANKCARD_CNT(String BANKCARD_CNT) {
            this.BANKCARD_CNT = BANKCARD_CNT;
        }

        public String getCASH_IN() {
            return CASH_IN;
        }

        public void setCASH_IN(String CASH_IN) {
            this.CASH_IN = CASH_IN;
        }

        public Object getUPT_IP() {
            return UPT_IP;
        }

        public void setUPT_IP(Object UPT_IP) {
            this.UPT_IP = UPT_IP;
        }

        public String getVOUCHER_BALANCE() {
            return VOUCHER_BALANCE;
        }

        public void setVOUCHER_BALANCE(String VOUCHER_BALANCE) {
            this.VOUCHER_BALANCE = VOUCHER_BALANCE;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public Object getUPT_ID() {
            return UPT_ID;
        }

        public void setUPT_ID(Object UPT_ID) {
            this.UPT_ID = UPT_ID;
        }

        public Object getIDX() {
            return IDX;
        }

        public void setIDX(Object IDX) {
            this.IDX = IDX;
        }

        public String getUPT_TIME() {
            return UPT_TIME;
        }

        public void setUPT_TIME(String UPT_TIME) {
            this.UPT_TIME = UPT_TIME;
        }

        public Object getREMARK() {
            return REMARK;
        }

        public void setREMARK(Object REMARK) {
            this.REMARK = REMARK;
        }

        public Object getREG_ID() {
            return REG_ID;
        }

        public void setREG_ID(Object REG_ID) {
            this.REG_ID = REG_ID;
        }

        public String getPURSE_WITHDRAW() {
            return PURSE_WITHDRAW;
        }

        public void setPURSE_WITHDRAW(String PURSE_WITHDRAW) {
            this.PURSE_WITHDRAW = PURSE_WITHDRAW;
        }

        public String getSCORE_IN() {
            return SCORE_IN;
        }

        public void setSCORE_IN(String SCORE_IN) {
            this.SCORE_IN = SCORE_IN;
        }

        public Object getREG_CLIENT_ID() {
            return REG_CLIENT_ID;
        }

        public void setREG_CLIENT_ID(Object REG_CLIENT_ID) {
            this.REG_CLIENT_ID = REG_CLIENT_ID;
        }

        public String getPURSE_BALANCE() {
            return PURSE_BALANCE;
        }

        public void setPURSE_BALANCE(String PURSE_BALANCE) {
            this.PURSE_BALANCE = PURSE_BALANCE;
        }

        public String getVOUCHER_IN() {
            return VOUCHER_IN;
        }

        public void setVOUCHER_IN(String VOUCHER_IN) {
            this.VOUCHER_IN = VOUCHER_IN;
        }

        public String getVOUCHER_OUT() {
            return VOUCHER_OUT;
        }

        public void setVOUCHER_OUT(String VOUCHER_OUT) {
            this.VOUCHER_OUT = VOUCHER_OUT;
        }

        public String getPURSE_OUT() {
            return PURSE_OUT;
        }

        public void setPURSE_OUT(String PURSE_OUT) {
            this.PURSE_OUT = PURSE_OUT;
        }

        public String getSCORE_OUT() {
            return SCORE_OUT;
        }

        public void setSCORE_OUT(String SCORE_OUT) {
            this.SCORE_OUT = SCORE_OUT;
        }

        public String getDATA_STATUS() {
            return DATA_STATUS;
        }

        public void setDATA_STATUS(String DATA_STATUS) {
            this.DATA_STATUS = DATA_STATUS;
        }

        public Object getCODE() {
            return CODE;
        }

        public void setCODE(Object CODE) {
            this.CODE = CODE;
        }

        public String getCASH_BALANCE() {
            return CASH_BALANCE;
        }

        public void setCASH_BALANCE(String CASH_BALANCE) {
            this.CASH_BALANCE = CASH_BALANCE;
        }

        public String getVOUCHER_FROZEN() {
            return VOUCHER_FROZEN;
        }

        public void setVOUCHER_FROZEN(String VOUCHER_FROZEN) {
            this.VOUCHER_FROZEN = VOUCHER_FROZEN;
        }

        public String getCASH_FROZEN() {
            return CASH_FROZEN;
        }

        public void setCASH_FROZEN(String CASH_FROZEN) {
            this.CASH_FROZEN = CASH_FROZEN;
        }
    }
}
