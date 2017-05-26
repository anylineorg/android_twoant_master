package net.twoant.master.ui.my_center.bean;

/**
 * Created by DZY on 2016/11/29.
 */

public class UserDataBean {

    /**
     * code : null
     * msg : null
     * uid : 14794601
     * login_name : null
     * login_pwd : null
     * pay_pwd : null
     * nick_name : 意潇
     * phone : 13253871210
     * invitecode : null
     * sex : 0
     * age : 22
     * autograph : 我叫鱼丸，喜欢拍照摄，喜欢你好你换你撒发
     * avatar : /avatar/14803980596771e555f27.jpg
     * state : 0
     * longitude : null
     * latitude : null
     * time : null
     * identity_status : 0
     * pay_pwd_state : null
     * owner_shop : null
     * manager_shop : null
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String code;
        private Object msg;
        private int uid;
        private Object login_name;
        private Object login_pwd;
        private Object pay_pwd;
        private String nick_name;
        private String phone;
        private Object invitecode;
        private int sex;
        private int age;
        private String autograph;
        private String avatar;
        private int state;
        private Object longitude;
        private Object latitude;
        private Object time;
        private int identity_status;
        private Object pay_pwd_state;
        private Object owner_shop;
        private Object manager_shop;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Object getMsg() {
            return msg;
        }

        public void setMsg(Object msg) {
            this.msg = msg;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public Object getLogin_name() {
            return login_name;
        }

        public void setLogin_name(Object login_name) {
            this.login_name = login_name;
        }

        public Object getLogin_pwd() {
            return login_pwd;
        }

        public void setLogin_pwd(Object login_pwd) {
            this.login_pwd = login_pwd;
        }

        public Object getPay_pwd() {
            return pay_pwd;
        }

        public void setPay_pwd(Object pay_pwd) {
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

        public Object getInvitecode() {
            return invitecode;
        }

        public void setInvitecode(Object invitecode) {
            this.invitecode = invitecode;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAutograph() {
            return autograph;
        }

        public void setAutograph(String autograph) {
            this.autograph = autograph;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public Object getLongitude() {
            return longitude;
        }

        public void setLongitude(Object longitude) {
            this.longitude = longitude;
        }

        public Object getLatitude() {
            return latitude;
        }

        public void setLatitude(Object latitude) {
            this.latitude = latitude;
        }

        public Object getTime() {
            return time;
        }

        public void setTime(Object time) {
            this.time = time;
        }

        public int getIdentity_status() {
            return identity_status;
        }

        public void setIdentity_status(int identity_status) {
            this.identity_status = identity_status;
        }

        public Object getPay_pwd_state() {
            return pay_pwd_state;
        }

        public void setPay_pwd_state(Object pay_pwd_state) {
            this.pay_pwd_state = pay_pwd_state;
        }

        public Object getOwner_shop() {
            return owner_shop;
        }

        public void setOwner_shop(Object owner_shop) {
            this.owner_shop = owner_shop;
        }

        public Object getManager_shop() {
            return manager_shop;
        }

        public void setManager_shop(Object manager_shop) {
            this.manager_shop = manager_shop;
        }
    }
}
