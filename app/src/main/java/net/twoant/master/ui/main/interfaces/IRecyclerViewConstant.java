package net.twoant.master.ui.main.interfaces;

/**
 * Created by S_Y_H on 2016/12/1.
 * 首页RecyclerView 的常量类
 */

public interface IRecyclerViewConstant {

    /**
     * 活动类型
     */
    int TYPE_ACTIVITY = 0x100;
    /**
     * 商家列表类型
     */
    int TYPE_MERCHANT_LIST = 0x200;
    int TYPE_MERCHANT_LIST_NEW = 0x2000;


    /**
     * 商品列表类型
     */
    int TYPE_MERCHANT_GOODS_LIST = 0x300;
    int TYPE_MERCHANT_GOODS_LIST_NEW = 0x3000;

    /**
     * 消息列表类型
     */
    int TYPE_MESSAGE = 0x400;

    /**
     * 朋友列表类型
     */
    int TYPE_FRIEND = 0x500;

    /**
     * 刷新
     */
    int TYPE_REFRESH = 0x600;

    /**
     * 加载
     */
    int TYPE_LOADING = 0x700;

    /**
     * 标准顶部栏
     */
    int STATE_CODE_NORMAL_HEADER = 0x800;

    /**
     * 没有顶部栏
     */
    int STATE_CODE_NOT_HEADER = 0x801;

    /**
     * 没有距离
     */
    int STATE_CODE_NOT_DISTANCE = 0x802;

    /**
     * 报表
     */
    int TYPE_REPORT = 0x900;

    /**
     * 报表 分类
     */
    int CATEGORY_REPORT = 0x901;

    /**
     * 联系商家 聊天的所有可以管理商家的管理人员列表
     */
    int TYPE_MERCHANT_CHAT_LIST = 0x1000;
    int CATEGORY_MERCHANT_CHAT_LIST = 0x1001;

    /**
     * 个人活动 列表
     */
    int TYPE_ACTIVITY_PERSONAL = 0x1100;

    /**
     * 分类 按关键字搜索附近活动
     */
    int CATEGORY_ACTIVITY_SEARCH_NEARBY = 0x101;

    /**
     * 分类 附近的活动
     */
    int CATEGORY_ACTIVITY_NEARBY = 0x102;

    /**
     * 分类 按关键字搜索最新活动
     */
    int CATEGORY_ACTIVITY_SEARCH_NEW = 0x103;

    /**
     * 分类 首页活动
     */
    int CATEGORY_ACTIVITY_LIST_HOME = 0x104;

    /**
     * 分类 商品列表 首页
     */
    int CATEGORY_MERCHANT_GOODS_LIST_HOME1 = 0x15;
    int CATEGORY_MERCHANT_GOODS_LIST_HOME2 = 0x25;
    int CATEGORY_MERCHANT_GOODS_LIST_HOME3 = 0x35;

    /**
     * 分类 活动 按关键字搜索最热活动
     */
    int CATEGORY_ACTIVITY_SEARCH_FAVOURITE = 0x105;

    /**
     * 附近 红包活动
     */
    int CATEGORY_RED_PACKER_ACTIVITY = 0x106;

    /**
     * 附近 积分活动
     */
    int CATEGORY_INTEGRAL_ACTIVITY = 0x107;

    /**
     * 附近 储值活动
     */
    int CATEGORY_SAVED_ACTIVITY = 0x108;

    /**
     * 附近 记次活动
     */
    int CATEGORY_METER_ACTIVITY = 0x109;

    /**
     * 附近 收费活动
     */
    int CATEGORY_CHARGE_ACTIVITY = 0x110;
    /**
     * 商家详情活动
     */
    int CATEGORY_ACTIVITY_MERCHANT_PAGE = 0x111;

    /**
     * 种类 红包活动
     */
    int KIND_RED_PACKER_ACTIVITY = 4;

    /**
     * 种类 积分活动
     */
    int KIND_INTEGRAL_ACTIVITY = 0;

    /**
     * 种类 储值卡活动
     */
    int KIND_SAVED_ACTIVITY = 2;

    /**
     * 种类 记次活动
     */
    int KIND_METER_ACTIVITY = 3;

    /**
     * 种类 收费活动
     */
    int KIND_CHARGE_ACTIVITY = 1;

    /**
     * 分类 商家列表 按关键字搜索 附近
     */
    int CATEGORY_MERCHANT_LIST_SEARCH_NEARBY = 0x201;

    /**
     * 分类 商家列表 按关键字搜索 评论量
     */
    int CATEGORY_MERCHANT_LIST_SEARCH_COMMENT = 0x202;

    /**
     * 分类 商家列表 按关键字搜索 交易量
     */
    int CATEGORY_MERCHANT_LIST_SEARCH_SALES = 0x203;

    /**
     * 分类 商家列表 首页
     */
    int CATEGORY_MERCHANT_LIST_HOME = 0x204;

    /**
     * 分类 商家列表 附近的商家
     */
    int CATEGORY_MERCHANT_LIST_NEARBY = 0x205;

    /**
     * 种类 商家列表 通用
     */
    int KIND_MERCHANT_LIST_COMMON = 0x206;


    /**
     * 分类 商品列表 附近
     */
    int CATEGORY_MERCHANT_GOODS_LIST_NEARBY = 0x301;
    /**
     * 分类 商品列表 按关键字搜索最新
     */
    int CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEW = 0x302;
    /**
     * 分类 商品列表 按关键字搜索最热
     */
    int CATEGORY_MERCHANT_GOODS_LIST_SEARCH_FAVOURITE = 0x303;

    /**
     * 分类 商品列表 按关键字搜索最近商品
     */
    int CATEGORY_MERCHANT_GOODS_LIST_SEARCH_NEARBY = 0x304;

    /**
     * 分类 商品列表 首页
     */
    int CATEGORY_MERCHANT_GOODS_LIST_HOME = 0x305;

    /**
     * 种类 商品通用列表
     */
    int KIND_MERCHANT_COMMON_GOODS = 0x306;


    /**
     * 分类 搜索消息 最新
     */
    int CATEGORY_MESSAGE_SEARCH_NEW = 0x401;

    /**
     * 分类 搜索消息 关注
     */
    int CATEGORY_MESSAGE_SEARCH_FOLLOW = 0x402;

    /**
     * 分类 搜索消息 评论量
     */
    int CATEGORY_MESSAGE_SEARCH_COMMENT = 0x403;

    /**
     * 分类 搜索好友 联系人
     */
    int CATEGORY_FRIEND_SEARCH_CONTACTS = 0x501;

    /**
     * 分类 搜索好友 群组
     */
    int CATEGORY_FRIEND_SEARCH_GROUP = 0x502;

    /**
     * 分类 搜索好友 消息记录
     */
    int CATEGORY_FRIEND_SEARCH_CHAT_HISTORY = 0x503;

    /**
     * 分类 个人活动 收费
     */
    int CATEGORY_ACTIVITY_PERSONAL_CHARGE = 0x1101;

    /**
     * 分类 个人活动 免费
     */
    int CATEGORY_ACTIVITY_PERSONAL_FREE = 0x1102;

}
