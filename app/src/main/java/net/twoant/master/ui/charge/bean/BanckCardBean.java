package net.twoant.master.ui.charge.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by DZY on 2016/12/3.
 */

public class BanckCardBean implements Serializable{

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable{
        /**
         * bankcard : 6228482378776787978
         * bankname : 中国农业银行
         * cardtype : 借记卡
         * id : 25
         * logourl : http://osspb.datatiny.com/banklogo/abc.png
         */

        private String bankcard;
        private String bankname;
        private String cardtype;
        private int id;
        private String logourl;

        public String getBankcard() {
            return bankcard;
        }

        public void setBankcard(String bankcard) {
            this.bankcard = bankcard;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getCardtype() {
            return cardtype;
        }

        public void setCardtype(String cardtype) {
            this.cardtype = cardtype;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogourl() {
            return logourl;
        }

        public void setLogourl(String logourl) {
            this.logourl = logourl;
        }
    }
}
