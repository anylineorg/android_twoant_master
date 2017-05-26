package net.twoant.master.ui.other.bean;

/**
 * Created by DZY on 2016/11/18.
 */

public class HttpResultBean {

    /**
     * code : -1
     * msg : 操作失败,用户已存在
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String code;
        private String msg;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
