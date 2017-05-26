package net.twoant.master.ui.main.bean;

import net.twoant.master.ui.main.interfaces.IDataBean;

import java.util.List;

/**
 * Created by S_Y_H on 2016/11/24.
 * 活动 数据实体类
 */

public class ActivityDataBean {

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean implements IDataBean {
        private boolean isRedDraw;

        public void setRedDraw(boolean isRedDraw) {
            this.isRedDraw = isRedDraw;
        }

        public boolean getRedDraw() {
            return isRedDraw;
        }

        /**
         * activity_id : 8
         * activity_type : 1
         * shop_id_pk : 0
         * antivity_uid : 0
         * activity_cover : activity_img/1479450690528643afeae.jpg
         * activity_introduce : 活动简介
         * activity_price :
         * activity_use_count : 1
         * activity_count : 12
         * activity_start_time : 2016-11-26 12:12:12
         * activity_end_time : 2016-12-26 14:12:12
         * activity_longitude : 2342.234
         * activity_latitude : 123.32424
         * end : 0
         * disable : 0
         * click_count : 1
         * activity_img : null
         */

        private String activity_id;
        private int activity_type;

        private int antivity_uid;
        private String activity_cover;
        private String activity_introduce;
        private String activity_price;
        private int activity_use_count;
        private int activity_count;
        private String activity_start_time;
        private String activity_end_time;
        private String activity_longitude;
        private String activity_latitude;
        private int end;
        private int disable;
        private int click_count;//总浏览量
        private String activity_red_price;//红包价格
        private String activity_chuzhi;//储值字段
        private String activity_havemoney;//收费活动条目
        private String activity_integral;
        private String activity_address;//活动地址
        private String activity_title;//活动主题
        private int _distance;//距离

        private String activity_name_integral;//xx活动_100,yy活动_200
        private String activity_name_chuzhi;//啊啊_100_200,问问_200_100

        private String activity_name_jici;//洗车_10,打蜡_20
        private String shop_id;
        private String shop_name;
        private String shop_cover;

        private int activity_red_shop;//0不是平台红包，1是平台红包
        private int activity_jici;//计次活动次数

        public String getActivity_name_integral() {
            return activity_name_integral;
        }

        public void setActivity_name_integral(String activity_name_integral) {
            this.activity_name_integral = activity_name_integral;
        }

        public String getActivity_name_jici() {
            return activity_name_jici;
        }

        public void setActivity_name_jici(String activity_name_jici) {
            this.activity_name_jici = activity_name_jici;
        }


        public String getActivity_name_chuzhi() {
            return activity_name_chuzhi;
        }

        public void setActivity_name_chuzhi(String activity_name_chuzhi) {
            this.activity_name_chuzhi = activity_name_chuzhi;
        }

        public int get_distance() {
            return _distance;
        }

        public void set_distance(int _distance) {
            this._distance = _distance;
        }

        public String getActivity_title() {
            return activity_title;
        }

        public void setActivity_title(String activity_title) {
            this.activity_title = activity_title;
        }

        public String getActivity_address() {
            return activity_address;
        }

        public void setActivity_address(String activity_address) {
            this.activity_address = activity_address;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getShop_cover() {
            return shop_cover;
        }

        public void setShop_cover(String shop_cover) {
            this.shop_cover = shop_cover;
        }

        public String getActivity_integral() {
            return activity_integral;
        }

        public void setActivity_integral(String activity_integral) {
            this.activity_integral = activity_integral;
        }

        public String getActivity_red_price() {
            return activity_red_price;
        }

        public void setActivity_red_price(String activity_red_price) {
            this.activity_red_price = activity_red_price;
        }

        public String getActivity_chuzhi() {
            return activity_chuzhi;
        }

        public void setActivity_chuzhi(String activity_chuzhi) {
            this.activity_chuzhi = activity_chuzhi;
        }

        public String getActivity_havemoney() {
            return activity_havemoney;
        }

        public void setActivity_havemoney(String activity_havemoney) {
            this.activity_havemoney = activity_havemoney;
        }

        public int getActivity_red_shop() {
            return activity_red_shop;
        }

        public void setActivity_red_shop(int activity_red_shop) {
            this.activity_red_shop = activity_red_shop;
        }

        public int getActivity_jici() {
            return activity_jici;
        }

        public void setActivity_jici(int activity_jici) {
            this.activity_jici = activity_jici;
        }


        public int getClick_count() {
            return click_count;
        }

        public void setClick_count(int click_count) {
            this.click_count = click_count;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getDisable() {
            return disable;
        }

        public void setDisable(int disable) {
            this.disable = disable;
        }

        private List<String> activity_img;

        public String getActivity_id() {
            return activity_id;
        }

        public void setActivity_id(String activity_id) {
            this.activity_id = activity_id;
        }

        public int getActivity_type() {
            return activity_type;
        }

        public void setActivity_type(int activity_type) {
            this.activity_type = activity_type;
        }

        //	public int getShop_id_pk() {
//		return shop_id_pk;
//	}
//	public void setShop_id_pk(int shop_id_pk) {
//		this.shop_id_pk = shop_id_pk;
//	}
        public int getAntivity_uid() {
            return antivity_uid;
        }

        public void setAntivity_uid(int antivity_uid) {
            this.antivity_uid = antivity_uid;
        }

        public String getActivity_cover() {
            return activity_cover;
        }

        public void setActivity_cover(String activity_cover) {
            this.activity_cover = activity_cover;
        }

        public String getActivity_introduce() {
            return activity_introduce;
        }

        public void setActivity_introduce(String activity_introduce) {
            this.activity_introduce = activity_introduce;
        }

        public String getActivity_price() {
            return activity_price;
        }

        public void setActivity_price(String activity_price) {
            this.activity_price = activity_price;
        }

        public int getActivity_use_count() {
            return activity_use_count;
        }

        public void setActivity_use_count(int activity_use_count) {
            this.activity_use_count = activity_use_count;
        }

        public int getActivity_count() {
            return activity_count;
        }

        public void setActivity_count(int activity_count) {
            this.activity_count = activity_count;
        }

        public String getActivity_start_time() {
            return activity_start_time;
        }

        public void setActivity_start_time(String activity_start_time) {
            this.activity_start_time = activity_start_time;
        }

        public String getActivity_end_time() {
            return activity_end_time;
        }

        public void setActivity_end_time(String activity_end_time) {
            this.activity_end_time = activity_end_time;
        }

        public String getActivity_longitude() {
            return activity_longitude;
        }

        public void setActivity_longitude(String activity_longitude) {
            this.activity_longitude = activity_longitude;
        }

        public String getActivity_latitude() {
            return activity_latitude;
        }

        public void setActivity_latitude(String activity_latitude) {
            this.activity_latitude = activity_latitude;
        }

        public List<String> getActivity_img() {
            return activity_img;
        }

        public void setActivity_img(List<String> activity_img) {
            this.activity_img = activity_img;
        }
    }
}
