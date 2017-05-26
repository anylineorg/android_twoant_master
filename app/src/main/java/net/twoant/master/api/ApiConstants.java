package net.twoant.master.api;

/**
 * Created by S_Y_H on 2016/11/14.9:56
 * 接口链接 常量类， 所有的接口链接 都放在这个类里面
 */

public interface ApiConstants {
    String URL="http://sk.deepbit.cn";
    /**
     * 图片 链接（暂时只是用于 开屏页 ad）
     */
    String IMG_BASE = "http://file.deepbit.cn/";

    /**
     * 文件服务器地址
     */
    String FILE_BASE = "http://file.deepbit.cn/";

    /**
     * 张哥 搜索链接
     */
    String SEARCH_BASE = "http://sk.deepbit.cn/";

    String GUO = "http://sk.deepbit.cn/";  //外网 现在用的
    String BASE = "http://sk.deepbit.cn/";//外网 现在用的

    /**
     * 便民信息赞
     */
    String CONVENIENT_ZAN = BASE + "mbr/msg/jud/ju";
    /**
     * .首页商家信息
     */
    String HOME_SHOP_INFO = URL+"/js/hm/tt/jvi";
    /**
     * 便民信息踩
     */
    String CONVENIENT_CAI = BASE + "mbr/msg/jud/jd";

    /**
     * 便民信息举报
     */
    String CONVENIENT_REPORT = BASE + "js/hm/tip/js";

    /**
     * 动态评论接口
     */
    String COMMENT_DYNAMIC = URL + "/js/mbr/im/msg/rpl/js";

    /**
     * 动态点赞
     */
    String ZAN_DYNAMIC = URL + "/js/mbr/im/msg/jud/ju";

    /**
     * 删除动态
     */
    String DELETE_DYNAMIC = URL + "/js/mbr/im/msg/jd";

    /**
     * 图片转换
     */
    String IMG_TRANSFORM = "http://file.deepbit.cn/file/up";

    /**
     * 便民信息详情打赏
     */
    String CONVENIENT_REWARD = BASE + "/mbr/msg/pay/jp";

    /**
     * 便民信息详情评论列表接口
     */
    String CONVENIENT_COMMENT_LIST = BASE + "/js/hm/msg/rpl/jl";

    /**
     * 便民信息评论接口
     */
    String CONVENIENT_COMMENT = BASE + "mbr/msg/rpl/js";

    /**
     * 便民信息详情
     */
    String CONVENIENT_DETAIL = BASE + "js/hm/msg/jv";
    /***
     * 短信验证码重置密码
     */
    String REST_LOGINPWD = URL+"/js/pwd/rst";
    /**
     * 发布便民信息
     */
    String POST_CONVENIENT = BASE + "mbr/msg/msg/jpub";

    /**
     * 获取所有便民信息一二级类别
     */
    String ALL_CONVENIENT_CLASS = BASE + "js/hm/msg/sort/jla";

    /**
     * 动态 查所有人 hm/im/msg/jl?  查好友user=好友id
     */
    String POST_DYNAMIC_LIST = URL + "/js/hm/im/msg/jl";

    /**
     * 动态列表(查自己的)
     */
    String POST_DYNAMIC_LIST_SELF = BASE + "mbr/im/msg/jl";

    /**
     * 发布动态
     */
    String POST_DYNAMIC = URL + "/js/mbr/im/msg/jpub";

    /**
     * 收付款 - 银行卡 列表
     */
    String BANK_LIST = BASE + "mbr/bc/jl";
//    String BANK_LIST = "http://192.168.11.200:7200/mbr/bc/jl";

    /**
     * 更换手机号
     */
    String FIX_PHONE = BASE + "mbr/usr/up";

    /**
     * 绑定银行卡
     */
    String BING_BANK = BASE + "mbr/bc/jbd";

    /**
     * 商品删除
     */
    String DETEL_GOODS = BASE + "shp/gd/jd";

    /**
     * 添加管理员
     */
    String POST_ADD_ADMINITOR = BASE + "mbr/usr/shp/aam";

    /**
     * 获取用户对商品的收藏状态
     */
    String GET_COLLECT_STATE = BASE + "mbr/usr/gd/is_col";

    /**
     * 用户收藏或者取消收藏一个商品
     */
    String ALTER_COLLECT_GOODS = BASE + "mbr/usr/gd/col";

    /**
     * 收付款 广告轮播
     */
    String CHARGE_AD = BASE + "hm/bn/scb";

    /**
     * 收藏_商家,商品，活动
     */
    String COLLECT_SHOP = BASE + "mbr/usr/shp/cols";
    String COLLECT_GOODS = BASE + "mbr/usr/gd/cols";
    String COLLECT_ACTION = BASE + "mbr/act/cols";


    /**
     * 买家待付款 订单详情
     */
    String WAITPAY_DETAIL = BASE + "mbr/od/jv";
    String WAITPAY_SHOP_DETAIL = BASE + "shp/od/jv";

    /**
     * 根据shopid查商家详情
     */
    String GET_SHOPINFOR = BASE + "hm/shp/jv";

    /**
     * 待付款 支付宝支付
     */
    String WAIT_PAY_ALI = BASE + "pay/call/od/alp";

    /**
     * 待付款 微信支付
     */
    String WAIT_PAY_WX = BASE + "pay/call/od/wx";
    /**
     * 计算需要支付的价格
     */
    String CUA_PRICE = URL + "/js/mbr/od/jpn?";
    /**
     * 待付款 支付(活动、商品)
     */
    String WAITPAY_PAY = BASE + "mbr/od/pay";

    /**
     * 设置默认收货地址
     */
    String SET_DEFAULT_ADDRESS = BASE + "mbr/adr/set_def";

    /**
     * 添加分类
     */
    String ADD_CLASS = ApiConstants.BASE + "shp/gd/tp/js";

    /**
     * 获取默认收货地址
     */
    String GET_DEFAULT_ADDRESS = BASE + "mbr/adr/def";
    /***
     * 学校列表
     */
    String SCHOOL = URL + "/js/hm/adr/jsl";
    /***
     * 宿舍楼
     */
    String BUILD = URL + "/js/hm/adr/jbl";
    /**
     * 删除收货地址
     */
    String DEL_ADDRESS = BASE + "mbr/adr/dl";

    /**
     * 获取实名认证后的信息
     */
    String REALNAME_INFOR = BASE + "mbr/usr/info";
    /*
    * 修改登录密码，无短信验证
    * */
    String FIX_LOGIN_PASSWOR = BASE + "ifo/usr/uppd";

    /**
     * 个人待付款
     */
    String PERSON_WATI_PAY = BASE + "mbr/od/jl0";

    /**
     * 个人待付款  取消订单
     */
    String PERSON_WATI_PAY_DELETE = BASE + "mbr/od/jd";

    /**
     * 个人待收货
     */
    String PERSON_WATI_GOODS = BASE + "mbr/od/jl1";

    /**
     * 商家待、收付款
     */
    String SHOP_WATI_PAY = BASE + "shp/od/jl";
    /***
     * 升级
     */
    String APP_UPDATE = "http://192.168.11.200/ver/jv";

    /***
     * 联合运营协议
     */
    String TIP = BASE + "hm/sys/cfg/v?c=6";
    /**
     * 用余额充值积分
     */
    String YU_E_GETINTEGRAL = BASE + "mbr/od/srcg";

    /**
     * 商家活动列表 商家发布的活动 进行中
     */
    String SELLER_ACTIVTTY = BASE + "shp/act/jl1";
    /***
     * 创建订单
     */
    String CREATE_ORDER=URL+"/js/mbr/od/crtn?";
    /**
     * 商家活动列表 商家发布的活动 已完成
     */
    String SELLER_ACTIVTTY_FINISH = BASE + "shp/act/jl0";

    /***
     * 商家活动列表 关闭活动操作动作
     */
    String SELLER_ACTIVITY_CL = BASE + "shp/act/cl";

    /**
     * 商家活动列表 删除活动(前提:商家已关闭的活动)
     */
    String SELLER_ACTIVITY_DL = BASE + "shp/act/dl";

    /**
     * 个人交易记录
     */
    String TRANSATION_RECORD = BASE + "mbr/fi/usr/ac/tl";

    /**
     * 个人积分收入
     */
    String INTEGRAL_IN = BASE + "/usr/fi/scr/in";

    /***
     * 联合运营轮播图
     */
    String LHYY = "http://sk.deepbit.cn/hm/bn/jl";
    /**
     * 个人积分支出
     */
    String INTEGRAL_OUT = BASE + "/usr/fi/scr/out";

    /**
     * 获取某店可用红包
     */
    String SHOP_REDABLE = BASE + "mbr/fi/vch/jlu";

    /**
     * 店内支付
     */
    String SHOP_PAY = BASE + "mbr/od/crt?shop=%s&user=%s&voucher=%s&score=%s&purse=%s&total=%s&sort=2";
    String MERCHANT_PAY = BASE + "mbr/od/crt";
    /**
     * 解绑银行卡
     */
    String UNBARDCARD = BASE + "mbr/bc/jub";

    /***
     * DZY 使用活动
     */
    String USEACTIVITY = BASE + "mbr/fi/vch/use";
    /**
     * DZY 设置支付密码
     */
    String SET_PAY_PASSWORD = BASE + "ifo/usr/uppsms";

    /**
     * DZY 获取是否有支付密码状态
     */
    String STATE_PAY_PASSWORD = BASE + "mbr/fi/usr/ac/hpp";


    //   String APP_ID = "wxc794fc8561b76d39";


    //IP
    String EXTERNAL_BASE_IP = BASE;


    /***
     * 注册
     */
   // String REG = BASE + "ifo/usr/reg";
    String REG = URL + "/js/hm/reg/reg";
    /***
     * 找回密码短信验证码
     */
    String FIND_SMS = URL+ "/js/pwd/sms_rst";
    /***
     * 短信验证码
     */
   // String SMS = BASE + "sc/scc";
    String SMS = URL+ "/js/hm/reg/sms";
    /**
     * 更改资料
     */
    String UPDATE_USER = URL + "/js/mbr/if/js";

    /**
     * 上传文件
     */
   String UPLOAD_FILE = "http://service.deepbit.cn/fl/up";
    /**
     * 用户详情
     */
    String USER_INFO = URL+"/js/mbr/if/jv";
    /**
     * 校验改手机为否注册
     */
 //   String CHECKPHONE = BASE + "sc/isc";
    String CHECKPHONE = URL+"/js/hm/reg/c_m";
    /***
     * DZY 已领取的红包
     */
    String REDUSE = BASE + "/mbr/usr/acts_4";

    /***
     * 已使用红包
     */
    String REDUSEED = BASE + "/mbr/usr/acts_4_0";

    /***
     * DZY 活动列表（参加未完成的）
     */
//    String ACTIVITYF = BASE + "mbr/usr/acts_0";
    String ACTIVITYF = BASE + "mbr/act/jl_jn";

    /***
     * DZY 活动列表（参加已完成）
     */
//    String ACTIVITYNF = BASE + "mbr/usr/acts_1";
    String ACTIVITYNF = BASE + "mbr/act/jl_fn";

    /**
     * DZY 使用活动详情
     */
    String ACTIVITY_DETAIL = BASE + "mbr/act/jv?user=%s&id=%s";

    /**
     * 获取订单
     */
    String GET_ORDDER = BASE + "mbr/od/crt";

    /**
     * 获取订单
     */
    String GET_ORDDER_GET = BASE + "mbr/od/crt?shop=%s&user=%s&voucher=%s&score=%s&purse=%s";
    String GET_GOODS_ORDER = BASE + "mbr/od/crt";

    /***
     * 获取子活动详情
     */
    String CHILDACTIVITY = BASE + "act/items";


    /***
     * DZY 提现数据展示
     */

    String WITHDRAWALS = BASE + "shp/wtd/wrs";


    /***
     * DZY 个人钱包余额
     */

    String USERPRICEBAG = BASE + "mbr/fi/usr/ac/blc";

    /***
     * DZY 管理员列表
     */

    String MANAGERLIST = BASE + "shp/adm/spl";

    /***
     * DZY 解绑管理员
     */
    String UNMANAGER = BASE + "shp/adm/spu";

    /***
     * 个人钱包流水
     */
    String USERPRICEBAGW = BASE + "mbr/fi/pur/rcd";


    /***
     * 商家银行卡信息
     */
    String SHOPBANKINFO = BASE + "shp/bank";

    /***
     * 商家发起提现申请
     */

    String PUSHWITHDRAWALS = BASE + "shp/wtd/req";

    /***
     * 个人发起提现申请
     */

    String SIGNPUSHWITHDRAWALS = BASE + "usr/fi/wtd/req";

    /***
     * 商家入账流水
     */
    String SHOPMONEYIN = BASE + "/shp/ac/rcd_in";

    /***
     * 商家提现流水
     */

    String SHOPTIXIAN = BASE + "shp/wtd/jl";

    /***
     * 用户详情
     */
    String USERINFO = BASE + "ifo/usr/gtu";

    /***
     * 判断支付密码
     */
    String CHECKPAYPWD = BASE + "/mbr/fi/usr/ac/cpp";

    /***
     * 商家账户总额
     */

    String SHOPACOUNT = BASE + "shp/ac/jv";


    /***
     * DZY 商家管理
     */

    String MANAGERSHOP = BASE + "mbr/usr/shops";

    /***
     * DZY 我管理的商家列表
     */

    String MYSHOPMANAGER = BASE + "mbr/usr/shp/jl";

    /**
     * DZY 微信支付
     */
    String WX_PAY = BASE + "/pay/cal/rcg/wx";

    /**
     * DZY 支付宝支付
     */
    String ALI_PAY = BASE + "/rcg/alp";

    /**
     * DZY 支付宝充值
     */
    String ALI_GETCASH = BASE + "/pay/cal/rcg/alp";

    /***
     * DZY 支付宝支付订单
     */
  //  String ALI_PAY_NEW = BASE + "/pay/cal/od/alp";


    /**
     * DZY 支付宝支付
     */
    String ALI_PAY_NEW = URL + "/pay/sbm/alp";

    /***
     * DZY 发布活动
     */
    String ACTIVITY_PUB = BASE + "mbr/act/pub";

    /***
     * DZY 支付宝支付订单
     */
   // String WX_PAY_NEW = BASE + "pay/cal/od/wx";
    String WX_PAY_NEW =  URL + "/pay/sbm/wx";//"pay/cal/od/wx";
    /**
     * 图片转换接口
     */
    String PHOTO_TRANSFORM = GUO + "upload.do";

    /*
    * DZY 个人活动详情
    **/
    String MY_ACTIVITY_URL = BASE + "act/astd";

    //添加表单行数据
    String ADD_USER = "http://yuntuapi.amap.com/datamanage/data/create";
    //查询有没有存在
    String SEARCH = "http://yuntuapi.amap.com/datamanage/data/list";
    //更新
    String UPDATE_TABLE = "http://yuntuapi.amap.com/datamanage/data/update";
    //删除
    String DELETE = "http://yuntuapi.amap.com/datamanage/data/delete";
    //检索附近人
    String SEARCHINFO = "http://yuntuapi.amap.com/datasearch/around";

    /**
     * DZY 上下架
     */
    String GDS_JU = BASE + "/gds/ju?";

    /**
     * 微信支付APP_id
     */
    String APP_ID = "wxc794fc8561b76d39";


// *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-* Y_H 接口集合 start *-*-*-*-*-*-* 其他 禁入 -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

    /**
     * 图片拼接头
     */
    String IMAGE_HEADER_URL = ApiConstants.GUO;
    /**
     * 积分比例
     */
    int INTEGRAL_SCALE = 1000;

    /**
     * 现金比例
     */
    int WALLET_SCALE = 5;

    /**
     * 联合运营 起投金额
     */
    int COMBINED_LIMIT = 10000;

    /**
     * 商品 轮播图 宽度 比例
     */
    int GOODS_BANNER_WIDTH_SCALE = 640;

    /**
     * 商品 轮播图 高度 比例
     */
    int GOODS_BANNER_HEIGHT_SCALE = 500;
    /**
     */
    String EXTERNAL_Z_BASE_IP = BASE;

    /**
     */
    String CURRENT_SELECT = GUO;
    /**
     * 我的订单
     *
     */
    String MY_ORDER_URL = URL + "/wap/mbr/od/idx";
    /**
     * 配送中心
     *
     */
    String SEND_CENTER = URL + "/wap/mbr/snd/idx";
    /**
     * 店铺管理
     *
     */
    String SHOP_MANGER = URL + "/wap/mbr/tt/idx";
    /**
     * 张哥 1网
     */
    String EXTERNAL_BASE_URL_Z_ONE = EXTERNAL_Z_BASE_IP + "act/";

    /**
     * 张哥
     */
    String EXTERNAL_BASE_PORT = EXTERNAL_Z_BASE_IP + "mbr/";

    /**
     * 张哥 2网
     */
    String EXTERNAL_BASE_URL_Z_TWO = EXTERNAL_BASE_PORT + "act/";

    /**
     * 张哥 3网
     */
    String EXTERNAL_BASE_URL_Z_THREE = EXTERNAL_BASE_PORT + "od/";

    /**
     * 发现界面的默认URL
     */
    String DISCOVER_URL = EXTERNAL_Z_BASE_IP + "fnd/top";

    /**
     * 请求每页的数量
     */
    int REQUEST_PAGE_SIZE = 10;

    /**
     * 我们的网址
     */
    String OWN_URL = "www.twoant.net";

    /**
     * 查询附近半径
     */
    int NEARBY_REQUEST_RADIUS = 2000;

    /**
     * 图片拼接头部
     */
    String ACTIVITY_IMAGE_URL_HEAD = EXTERNAL_Z_BASE_IP;

    /**
     * 获取 指定商家的所有 可管理者（包括所有者）
     * /hm/shp/jadmin?id=商家ID
     */
    String GET_MANAGE_LIST = BASE + "hm/shp/jadmin";

    /**
     * 获取用户信息
     */
    String GET_USER_INFO = GUO + "search_user.do";

    /**
     * 查找好友
     * String FIND_USER = "http://192.168.1.200:7200/hm/usr/jsh"; m nm
     */
    String FIND_USER = URL + "/js/hm/mbr/jvm";

    /**
     * 登录
     */
//    String LOGIN = CURRENT_SELECT + "/login.do";
//    String LOGIN = "http://192.168.11.200:7200/lg/jlg";
    //String LOGIN = EXTERNAL_Z_BASE_IP + "lg/jlg";
    String LOGIN = URL + "/js/hm/lg/chk";
    /**
     * 更新开屏页的 ad 图片
     */
    String START_AD_IMG = IMG_BASE + "startAd.png";

    /**
     * 打开消息的同时 需要调用/mbr/msg/jr?id=消息ID  来更新消息阅读状态
     * 消息ID从 extras中取MSG_ID
     */
    String MESSAGE_READ = EXTERNAL_BASE_IP + "mbr/msg/jr";
    /**
     * 商家入驻
     */
    String MERCHANT_ENTERED = CURRENT_SELECT + "enter.do";

    /**
     * VIP会员 /wap/shp/mms/mbr/idx
     * 会员卡 /wap/shp/mms/crd/idx
     * 营销活动/wap/shp/mms/act/idx
     */
    String MERCHANT_MANAGE_MEMBER = BASE + "/wap/shp/mms/mbr/idx";
    //会员卡
    String MERCHANT_MANAGE_VIP_CARD = BASE + "/wap/shp/mms/crd/idx";
    //营销活动
    String MERCHANT_MANAGE_GIFT = BASE + "/wap/shp/mms/act/idx";
    //会员商家列表
    String MEMBER_MERCHANT_LIST = BASE + "/mbr/mms/shp/jl";
    //设置是否接收商家推送/mbr/mms/shp/jrp?shopo=商家ID
    String MEMBER_MERCHANT_CHANGE_PUSH_STATE = BASE + "/mbr/mms/shp/jrp";
    /**
     * /mbr/mms/shp/jv?shop=商家ID
     * 当前用户在指定店铺中的会员信息
     */
    String MERCHANT_MEMBER_INFO = BASE + "mbr/mms/shp/jv";
    /**
     * 首页 最新商品列表
     * String GOODS_LIST_HOME = CURRENT_SELECT + "goods_list.do";
     */
    String GOODS_LIST_HOME = URL + "/js/hm/tt/jpl";
    // String GOODS_LIST_HOME = EXTERNAL_BASE_IP + "hm/gd/jln";

    /**
     * 附近店铺
     * */
    String YM_SHOP = URL+"/js/hm/tt/jl";//js/hm/tt/jl
    String GOODS_CARD = URL + "/js/hm/pd/jln";//"/js/hm/pd/jl"


    /**
     * 常买店铺
     * */
    String HOME_MINDLE = URL + "/js/hm/pd/jlh";//"/js/hm/pd/jl"
    /**
     * 积分商城
     */
    String SCORE_STORE =  URL+"/js/hm/pd/jlp";
    /**
     * 商品详情
     */
    String BUYER_GOODS_DETAIL = "http://sk.deepbit.cn/js/hm/pd/jv";//EXTERNAL_BASE_IP + "hm/gd/jv";

    /**
     * 附近的商家列表
     */
    String MERCHANT_LIST_NEARBY = EXTERNAL_BASE_IP + "hm/shp/jar";

    /**
     * 首页商家列表
     * String MERCHANT_LIST_HOME = CURRENT_SELECT + "main_shop.do";
     */
    String MERCHANT_LIST_HOME = EXTERNAL_BASE_IP + "hm/shp/jln";

    /**
     * 搜索商家
     * String MERCHANT_LIST_SEARCH = CURRENT_SELECT + "search_shop.do";
     */
    String MERCHANT_LIST_SEARCH = SEARCH_BASE + "hm/sch/jshp";


    /**
     * 搜索商品
     */
    String GOODS_LIST_SEARCH = SEARCH_BASE + "hm/sch/jgd";

    /**
     * 搜索活动
     * String ACTIVITY_SEARCH = EXTERNAL_BASE_URL_Z_ONE + "astan";
     */
    String ACTIVITY_SEARCH = SEARCH_BASE + "hm/sch/jact";

    /**
     * .轮播图(广告) ad_index	int	轮播图索引	0 首页轮播图, 1 次页轮播
     */
    String HOME_PAGE_BANNER = URL+"/js/hm/art/tb";

    /**
     * 商家详情页
     * String MERCHANT_PAGER_DETAIL = CURRENT_SELECT + "shop_info.do";
     */
    String MERCHANT_HOME_PAGE_DETAIL = EXTERNAL_Z_BASE_IP + "hm/shp/jv";
    /**
     * 商家商品列表
     *
     */
    String GOODS_LIST_4SHOP = URL+"/js/hm/tt/jv";
    /**
     * 商家详情页 ，商品列表 右侧商品
     * String MERCHANT_GOODS_LIST = CURRENT_SELECT + "search_goods.do";
     */
    String MERCHANT_GOODS_LIST = EXTERNAL_Z_BASE_IP + "shp/gd/jl";
    /**
     * 收藏或者取消商家收藏
     */
    String COLLECTION = EXTERNAL_Z_BASE_IP + "mbr/usr/shp/col";

    /**
     * 查询商家是否被收藏
     */
    String INQUIRE_COLLECTION = EXTERNAL_Z_BASE_IP + "mbr/usr/shp/is_col";

    /**
     * 商家商品分类列表
     */
   // String MERCHANT_GOODS_CATEGORY = CURRENT_SELECT + "goods_type.do";
    String MERCHANT_GOODS_CATEGORY = URL + "/js/hm/tt/jpsl";
    /**
     * 活动首页接口
     * String ACTIVITY_HOME_PAGE = EXTERNAL_BASE_URL_Z_ONE + "asta";
     */
    String ACTIVITY_HOME_PAGE = EXTERNAL_Z_BASE_IP + "/hm/act/jln";

    /**
     * 附近的活动接口 区分活动类型
     * String ACTIVITY_NEARBY_SELECT_TYPE = EXTERNAL_BASE_URL_Z_ONE + "nearby";
     */
    String ACTIVITY_NEARBY_SELECT_TYPE = EXTERNAL_BASE_IP + "hm/act/jar";

    /**
     * 活动详情（非商家管理调用）
     * activity_id 活动id
     * user 蚂蚁ID
     */
    String ACTIVITY_DETAIL_USER = EXTERNAL_BASE_URL_Z_ONE + "astd";

    /**
     * 领取红包
     * 参加活动
     * http://localhost/mbr/od/crt?shop=8&user=9&score=1000&purse=10&activity=1&qty=1&sort=3
     * score 使用多少积分
     * purse 使用多少钱包
     * qty 数量
     * sort 固定的 代表活动
     * http://192.168.11.200/mbr/od/crt?shop=8&user=9activity=10&sort=3
     * <p>
     * String shipSort = getParam("ship_sort");								//1店内 2商家 3平台 4自取
     * String receiveName = getParam("receive_name");							//收货人
     * String recevieTel = getParam("receive_tel");							//收货电话
     * String address = getParam("receive_address");							//收货地址
     */
    String ACTIVITY_GET_RED_PACKET = EXTERNAL_BASE_URL_Z_THREE + "crt";

    /**
     * /mbr/act/col?user=1&activity=1
     * 活动收藏
     */
    String ACTIVITY_COLLECTION = EXTERNAL_BASE_URL_Z_TWO + "col";

    /**
     * 活动详情（商家管理调用）
     */
    String ACTIVITY_DETAIL_MERCHANT = EXTERNAL_BASE_URL_Z_ONE + "astsp";

    /**
     * 商家首页的 最新活动
     */
    String ACTIVITY_MERCHANT_HOME_PAGE = EXTERNAL_Z_BASE_IP + "shp/act/jl";

    /**
     * 根据活动id 查询已报名用户
     * act/users?activity=12
     */
    String ACTIVITY_USER_LIST = EXTERNAL_BASE_URL_Z_ONE + "users";

    /**
     * /mbr/fi/usr/ac/jv?user=12
     * 用户帐户信息
     */
    String ACTIVITY_USER_ACCOUNT_INFO = EXTERNAL_BASE_PORT + "fi/usr/ac/jv";

    /**
     * 商家活动报表
     */
    String ACTIVITY_MERCHANT_REPORT_STATEMENT = EXTERNAL_Z_BASE_IP + "shp/act/jn/orders";

    /**
     * 商家活动报表 总额
     */
    String ACTIVITY_MERCHANT_REPORT_SUM = EXTERNAL_Z_BASE_IP + "shp/act/jn/stat";

    /**
     * 商家活动报表 条目总额
     */
    String ACTIVITY_MERCHANT_REPORT_ITEM_SUM = EXTERNAL_Z_BASE_IP + "shp/act/ist";

    /**
     * 商家入驻信息修改
     * String ACTIVITY_MERCHANT_ENTRY_MODIFY = EXTERNAL_Z_BASE_IP + "hm/shp/up";
     */
    String ACTIVITY_MERCHANT_ENTRY_MODIFY = EXTERNAL_Z_BASE_IP + "shp/jn/js";

    /**
     * 获取 商家入驻信息
     * String ACTIVITY_MERCHANT_ENTRY_INFO = EXTERNAL_Z_BASE_IP + "hm/shp/ens";
     */
    String ACTIVITY_MERCHANT_ENTRY_INFO = EXTERNAL_Z_BASE_IP + "shp/jn/jv";

    /**
     * 商家入驻 获取账号 入驻的账号数量，为零时 显示 推荐人
     */
    String ACTIVITY_MERCHANT_ENTRY_COMMEND = EXTERNAL_Z_BASE_IP + "mbr/usr/shp/cnt";

    /**
     * 单张图片的上传接口
     */
//    String ACTIVITY_MERCHANT_IMG_UPDATE = EXTERNAL_Z_BASE_IP + "file/up";
    String ACTIVITY_MERCHANT_IMG_UPDATE = FILE_BASE + "file/up";

    /**
     * /log/abug?user=1&des=内容
     * ver=? 版本号
     * p=android
     */
    String ACTIVITY_CRASH = EXTERNAL_Z_BASE_IP + "log/abug";

    /**
     * app 更新接口
     */
    String UPDATE = EXTERNAL_Z_BASE_IP + "ver/jv";

    /**
     * 商家入驻协议
     * /hm/sys/cfg/v?c=编号(数据库中查询)
     */
    String MERCHANT_ENTRY_DEAL = EXTERNAL_Z_BASE_IP + "hm/sys/cfg/v?c=5";

    String MESSAGE = EXTERNAL_Z_BASE_IP + "mbr/msg/l?user=";

    /**
     * 检查用户状态
     */
    String CHECK_USER = EXTERNAL_Z_BASE_IP + "hm/usr/exists";

    /**
     * 查询群组信息
     */
    String CHAT_INQUIRY_GROUP_INFO = EXTERNAL_Z_BASE_IP + "mbr/im/gp/jv";

    /**
     * 创建群信息
     * /mbr/im/gp/js?id=群ID&code=群号&avatar=图片&admin=管理员ID
     */
    String CHAT_CREATE_GROUP_INFO = EXTERNAL_Z_BASE_IP + "mbr/im/gp/js";


// *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*Y_H 接口集合 end *-*-*-*-*-*-*其他 接口 禁入 -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*XJ 接口  start*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    /**
     * 便民信息
     * 分类
     */
    String CONVENIENT_INFO_A_MENU = BASE + "js/hm/msg/sort/jl";

    String CONVENIENT_INFO_B_MENU = CONVENIENT_INFO_A_MENU;
    /**
     * 便民信息
     * 信息列表
     */
    String CONVENIENT_INFO_LIST = BASE + "js/hm/msg/jl";

    /**
     * 便民信息搜索
     */
    String CONVENIENT_INFO_SEARCH = BASE + "/js/hm/msg/jl";

    /**
     * 便民信息
     * 信息详情
     */
    String CONVENIENT_INFO_CONTENT = BASE + "js/hm/msg/jv";

    /**
     * 便民信息
     * 自己列表
     */
    String CONVENIENT_INFO_LIST_SELF = BASE + "mbr/msg/msg/jl";
    /**
     * 便民信息
     * 删除信息
     */
    String CONVENIENT_INFO_LIST_DELETE = BASE + "mbr/msg/msg/jd";

    /**
     * 动态详情
     */
    String DYNAMIC_DETAIL = BASE + "hm/im/msg/jv";


    //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*XJ 接口  end*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
}
