package net.twoant.master.ui.my_center.bean;

import java.util.List;

/**
 * Created by DZY on 2016/12/15.
 * 佛祖保佑   永无BUG
 */

public class AddressListBean {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * aisou_id : 14794601
         * receipt_name : 邓白氏
         * receipt_tel : 123131231
         * receipt_address : 吉尔吉斯斯坦
         * receipt_default : 0
         * id : 1
         */

        private int aisou_id;
        private String receipt_name;
        private String receipt_tel;
        private String receipt_address;
        private int receipt_default;
        private int id;

        public int getAisou_id() {
            return aisou_id;
        }

        public void setAisou_id(int aisou_id) {
            this.aisou_id = aisou_id;
        }

        public String getReceipt_name() {
            return receipt_name;
        }

        public void setReceipt_name(String receipt_name) {
            this.receipt_name = receipt_name;
        }

        public String getReceipt_tel() {
            return receipt_tel;
        }

        public void setReceipt_tel(String receipt_tel) {
            this.receipt_tel = receipt_tel;
        }

        public String getReceipt_address() {
            return receipt_address;
        }

        public void setReceipt_address(String receipt_address) {
            this.receipt_address = receipt_address;
        }

        public int getReceipt_default() {
            return receipt_default;
        }

        public void setReceipt_default(int receipt_default) {
            this.receipt_default = receipt_default;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
