package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/12/15.
 * 佛祖保佑   永无BUG
 */

public class GroundGoodsBean {

    /**
     * code : 1
     * msg : 操作成功
     */

    private DataBean data;
    /**
     * data : {"code":"1","msg":"操作成功"}
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
