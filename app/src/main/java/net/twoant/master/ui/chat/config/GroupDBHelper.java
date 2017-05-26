package net.twoant.master.ui.chat.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.twoant.master.ui.chat.app.ChatHelper;

/**
 * Created by S_Y_H on 2017/2/21.
 * 用来保存用户的头像和昵称信息
 */

class GroupDBHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;

    final static String TABLE_NAME = "groups";
    //用户名
    final static String COLUMN_ID = "_id";
    /**
     * 头像网址的md5值
     */
    final static String COLUMN_MD = "md";

    private final static String SQL_USER = "CREATE TABLE "
            + TABLE_NAME + " ( "
            + COLUMN_ID + " TEXT PRIMARY KEY, " + COLUMN_MD
            + " TEXT);";

    GroupDBHelper(Context context) {
        super(context, getDBName(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static String getDBName() {
        return ChatHelper.getInstance().getCurrentUserName() + "_group_.db";
    }
}
