package net.twoant.master.api;

/**
 * Created by S_Y_H on 2016/11/13.23:42
 * App配置类 以后的微信、支付宝等各种key什么的，都放在这里。
 */
public interface AppConfig {

    /**
     * 当前为开发模式
     */
    boolean IS_DEBUG = false;

    String APP_VERSION = "1.2.6_0411_1400";

    /**
     * 默认的log Tag 名称
     */
    String DEBUG_TAG = "QDXuanZe";

    /**
     * AES加密Key
     */
    String KEY = "jraldu6veju4ibc0te99orow9igi15x6";

    /**
     * AES 加密是否为调试模式
     */
    boolean IS_AES_DEBUG = false;

    /**
     * 高德key(web)
     */
    String GaoDe_Key = "ffb8c24eefb9b1e738546a41f992d94f";
    /*
     * 高德sig
     * */
//    String GaoDe_Sig = "D0:7F:21:AB:F0:A8:97:EA:04:08:47:3F:8B:55:71:C2:05:80:24:5F";
    /**
     * 附近人用户表id
     */
    String NEARBY_USER_TABLEID = "58c3b0fb2376c11121cde70d";

    /**
     * 个人中心个人二维码
     */
    String CODE_URL = "http://sk.deepbit.cn/wap/hm/reg/go?iv=";

    /**
     * 微信支付APP_id
     */
    String APP_ID = "wxc794fc8561b76d39";
    /**
     * 腾讯 bugly
     */
    String APP_CRASH_ID = "ca4914a38c";

}
