package net.twoant.master.ui.other.bean;

import java.util.List;

/**
 * Created by S_Y_H on 2016/11/29.
 * 登录类的数据实体类
 */

public class LoginBean {
    /**
     * result : {"code":"0","msg":"用户详情","uid":14794601,"login_name":null,"login_pwd":null,"pay_pwd":"","nick_name":"","phone":null,"invitecode":null,"sex":null,"age":null,"autograph":null,"avatar":null,"state":0,"longitude":"","latitude":"","time":null,"identity_status":0,"pay_pwd_state":null,"owner_shop":null,"manager_shop":null}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {

        private String uid;//用户id
        private String code;
        private String msg;
        private String login_name;
        private String login_pwd;
        private String pay_pwd;
        private String sex;
        private String age;
        private String autograph;
        private String time;
        private String nick_name;
        private List<String> manager_phone;

        public List<String> getManager_phone() {
            return manager_phone;
        }

        public void setManager_phone(List<String> manager_phone) {
            this.manager_phone = manager_phone;
        }

        private String phone;//手机号
        private String invitecode;//邀请码
        private String avatar;//头像
        private String state;//0 不可登录， 1 不可登录
        private String longitude;//经度
        private String latitude;//纬度
        private String identity_status;//0:没有实名认证，1:有
        private String pay_pwd_state;//0:没有，1:有

        private List<String> owner_shop;//我的店铺id
        private List<String> manager_shop;//我管理的店铺id

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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getLogin_name() {
            return login_name;
        }
        public void setLogin_name(String login_name) {
            this.login_name = login_name;
        }
        public String getLogin_pwd() {
            return login_pwd;
        }
        public void setLogin_pwd(String login_pwd) {
            this.login_pwd = login_pwd;
        }
        public String getPay_pwd() {
            return pay_pwd;
        }
        public void setPay_pwd(String pay_pwd) {
            this.pay_pwd = pay_pwd;
        }
        public String getNick_name() {
            return nick_name;
        }
        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getAvatar() {
            return avatar;
        }
        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getState() {
            return state;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getLongitude() {
            return longitude;
        }
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        public String getLatitude() {
            return latitude;
        }
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }

        public String getSex() {
            return sex;
        }
        public void setSex(String sex) {
            this.sex = sex;
        }
        public String getAge() {
            return age;
        }
        public void setAge(String age) {
            this.age = age;
        }
        public String getAutograph() {
            return autograph;
        }
        public void setAutograph(String autograph) {
            this.autograph = autograph;
        }
        public String getInvitecode() {
            return invitecode;
        }
        public void setInvitecode(String invitecode) {
            this.invitecode = invitecode;
        }

        public String getIdentity_status() {
            return identity_status;
        }
        public void setIdentity_status(String identity_status) {
            this.identity_status = identity_status;
        }
        public String getPay_pwd_state() {
            return pay_pwd_state;
        }
        public void setPay_pwd_state(String pay_pwd_state) {
            this.pay_pwd_state = pay_pwd_state;
        }
        public List<String> getOwner_shop() {
            return owner_shop;
        }
        public void setOwner_shop(List<String> owner_shop) {
            this.owner_shop = owner_shop;
        }
        public List<String> getManager_shop() {
            return manager_shop;
        }
        public void setManager_shop(List<String> manager_shop) {
            this.manager_shop = manager_shop;
        }
    }


}
