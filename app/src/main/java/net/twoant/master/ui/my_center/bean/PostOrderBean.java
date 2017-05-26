package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/19.
 * 佛祖保佑   永无BUG
 */

public class PostOrderBean {

    /**
     * data : ["189",90.37]
     * result : true
     * success : true
     * type : map
     */

    private boolean result;
    private boolean success;
    private String type;
    private List<String> data;

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

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
