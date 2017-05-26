package net.twoant.master.wxapi;

/**
 * Created by DZY on 2016/12/12.
 * 佛祖保佑   永无BUG
 */

public class WXPayBean {

    /**
     * appid : wxc794fc8561b76d39
     * noncestr : WHCPAQXWMQTXMXNDDOQOKIWWQTRKYIPA
     * package : Sign=WXPay
     * partnerid : 1383950902
     * prepayid : wx20161212014030f14381d5c40814365730
     * sign : 68E1C98BE0DE437B7F8F21E1B58918E2
     * timestamp : 1481478030118
     */

    private DataBean data;
    /**
     * data : {"appid":"wxc794fc8561b76d39","noncestr":"WHCPAQXWMQTXMXNDDOQOKIWWQTRKYIPA","package":"Sign=WXPay","partnerid":"1383950902","prepayid":"wx20161212014030f14381d5c40814365730","sign":"68E1C98BE0DE437B7F8F21E1B58918E2","timestamp":"1481478030118"}
     * result : true
     * success : true
     * type : map
     */

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
        private String appid;
        private String noncestr;
        private String packageX;
        private String partnerid;
        private String prepayid;
        private String sign;
        private String timestamp;

        @Override
        public String toString() {
            return "DataBean{" +
                    "appid='" + appid + '\'' +
                    ", noncestr='" + noncestr + '\'' +
                    ", packageX='" + packageX + '\'' +
                    ", partnerid='" + partnerid + '\'' +
                    ", prepayid='" + prepayid + '\'' +
                    ", sign='" + sign + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
