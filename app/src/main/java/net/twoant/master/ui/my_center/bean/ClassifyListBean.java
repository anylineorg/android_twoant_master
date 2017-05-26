package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/1.
 */

public class ClassifyListBean {

    /**
     * goods_type : 饼干
     * goods_typeid : 3
     * shop_id : 13
     */

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String CD;
        private String NM;

        public String getNM() {
            return NM;
        }

        public void setNM(String NM) {
            this.NM = NM;
        }

        public String getCD() {
            return CD;
        }

        public void setCD(String CD) {
            this.CD = CD;
        }
    }
}
