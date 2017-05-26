package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/12/19.
 * 佛祖保佑   永无BUG
 */

public class BalanceBean {

    /**
     * data : {"PURSE_BALANCE":9999994.5,"SCORE_BALANCE":1.000016471E7}
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
         * PURSE_BALANCE : 9999994.5
         * SCORE_BALANCE : 1.000016471E7
         */

        private double PURSE_BALANCE;
        private double SCORE_BALANCE;

        public double getPURSE_BALANCE() {
            return PURSE_BALANCE;
        }

        public void setPURSE_BALANCE(double PURSE_BALANCE) {
            this.PURSE_BALANCE = PURSE_BALANCE;
        }

        public double getSCORE_BALANCE() {
            return SCORE_BALANCE;
        }

        public void setSCORE_BALANCE(double SCORE_BALANCE) {
            this.SCORE_BALANCE = SCORE_BALANCE;
        }
    }
}
