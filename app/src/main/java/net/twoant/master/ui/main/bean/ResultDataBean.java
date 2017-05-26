package net.twoant.master.ui.main.bean;

import com.google.gson.Gson;

/**
 * Created by S_Y_H on 2016/12/5.
 * 结果码
 */

public class ResultDataBean {

    /**
     * result : {"code":"0","msg":"入驻成功"}
     * {"result":{"code":"0","msg":"操作成功"}}
     */

    public static ResultDataBean getInstance(String response) {
        return new Gson().fromJson(response, ResultDataBean.class);
    }

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * code : 0
         * msg : 入驻成功
         */

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
