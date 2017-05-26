package net.twoant.master.ui.my_center.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DZY on 2017/3/2.
 * 佛祖保佑   永无BUG
 */

public class DynamicCommentListBean {

    public static List<List<DataBean>> itemCommentList;

    public static List<List<DataBean>> getData() {
        return itemCommentList;
    }

    public static void setData(List<List<DataBean>> data) {
       itemCommentList = data;
    }

    public static void addData( List<DataBean> dataBean) {
        if (null == itemCommentList) {
            itemCommentList = new ArrayList<>();
        }
        itemCommentList.add(dataBean);
    }

    public static void clearn() {
        if (null != itemCommentList) {
            itemCommentList.clear();
            itemCommentList = null;
        }
    }

    public static class DataBean {
        /*
         * ID : 5
         * MESSAGE_ID : 31
         * TITLE : 垃来来来
         * USER_AVATAR : /avatar/1485448859204555fd6c8.jpg
         * USER_ID : 197
         * USER_NM : 志颖
         */

        private int ID;
        private int MESSAGE_ID;
        private String TITLE;
        private String USER_AVATAR;
        private int USER_ID;
        private String USER_NM;
        private int ID_;

       public int getID_() {
           return ID_;
       }

       public void setID_(int ID_) {
           this.ID_ = ID_;
       }

       public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getMESSAGE_ID() {
            return MESSAGE_ID;
        }

        public void setMESSAGE_ID(int MESSAGE_ID) {
            this.MESSAGE_ID = MESSAGE_ID;
        }

        public String getTITLE() {
            return TITLE;
        }

        public void setTITLE(String TITLE) {
            this.TITLE = TITLE;
        }

        public String getUSER_AVATAR() {
            return USER_AVATAR;
        }

        public void setUSER_AVATAR(String USER_AVATAR) {
            this.USER_AVATAR = USER_AVATAR;
        }

        public int getUSER_ID() {
            return USER_ID;
        }

        public void setUSER_ID(int USER_ID) {
            this.USER_ID = USER_ID;
        }

        public String getUSER_NM() {
            return USER_NM;
        }

        public void setUSER_NM(String USER_NM) {
            this.USER_NM = USER_NM;
        }
    }
}
