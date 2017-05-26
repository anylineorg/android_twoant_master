package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by kaiguokai on 2016/10/26.
 */

public class BaseCode {


    /**
     * code : 200
     * message : 产品成功上架
     * data : []
     */

    private int code;
    private String message;
    private List<?> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
