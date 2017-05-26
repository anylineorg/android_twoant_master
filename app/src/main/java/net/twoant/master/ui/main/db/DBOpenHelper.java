package net.twoant.master.ui.main.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.twoant.master.app.AiSouAppInfoModel;

/**
 * Created by S_Y_H on 2017/1/12.
 * 消息的数据库帮助类
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private final static String AUTOINCREMENT_ID = "_id";
    private final static int DATABASE_VERSION = 1;


    /**
     * 活动消息
     */
    private static final String ACTIVITY_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + ActivityMessageDao.TABLE_NAME + " ( "
            + AUTOINCREMENT_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ActivityMessageDao.COLUMN_ACTIVITY_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_ID + " TEXT );";

    /**
     * 支付消息
     */
    private static final String PAYMENT_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + ActivityMessageDao.TABLE_NAME + " ( "
            + AUTOINCREMENT_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ActivityMessageDao.COLUMN_ACTIVITY_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_ID + " TEXT );";
    /**
     * 审核消息
     */
    private static final String AUDIT_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + ActivityMessageDao.TABLE_NAME + " ( "
            + AUTOINCREMENT_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ActivityMessageDao.COLUMN_ACTIVITY_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_ID + " TEXT );";

    /**
     * 提醒消息
     */
    private static final String ALERT_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + ActivityMessageDao.TABLE_NAME + " ( "
            + AUTOINCREMENT_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ActivityMessageDao.COLUMN_ACTIVITY_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_ID + " TEXT );";
    /**
     * 订单消息
     */
    private static final String ORDER_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + ActivityMessageDao.TABLE_NAME + " ( "
            + AUTOINCREMENT_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ActivityMessageDao.COLUMN_ACTIVITY_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_NAME + " TEXT, "
            + ActivityMessageDao.COLUMN_SHOP_ID + " TEXT );";


    public DBOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ACTIVITY_MESSAGE_TABLE_CREATE);
        db.execSQL(PAYMENT_MESSAGE_TABLE_CREATE);
        db.execSQL(AUDIT_MESSAGE_TABLE_CREATE);
        db.execSQL(ALERT_MESSAGE_TABLE_CREATE);
        db.execSQL(ORDER_MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getUserDatabaseName() {
        return AiSouAppInfoModel.getInstance().getUID() + "_aisou.db";
    }
}
