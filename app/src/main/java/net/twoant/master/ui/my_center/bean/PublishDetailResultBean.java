package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/12/22.
 * 佛祖保佑   永无BUG
 */

public class PublishDetailResultBean {

    /**
     * result : true
     * data : {"info":"OK","infocode":"10000","status":1,"_id":"48"}
     * success : true
     * type : string
     * message : null
     */

    private boolean result;
    private String data;
    private boolean success;
    private String type;
    private Object message;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
}
