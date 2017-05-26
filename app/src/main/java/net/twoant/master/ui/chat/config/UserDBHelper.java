package net.twoant.master.ui.chat.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.twoant.master.app.AiSouAppInfoModel;

/**
 * Created by S_Y_H on 2017/2/21.
 * 用来保存用户的头像和昵称信息
 */

class UserDBHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;

    final static String TABLE_NAME = "users";
    //用户名
    final static String COLUMN_ID = "_id";
    /**
     * 头像网址的md5值
     */
    final static String COLUMN_MD = "md";
    /**
     * 昵称
     */
    final static String COLUMN_NN = "nick";

    private final static String SQL_USER = "CREATE TABLE "
            + TABLE_NAME + " ( "
            + COLUMN_ID + " TEXT PRIMARY KEY, " + COLUMN_MD
            + " TEXT, " + COLUMN_NN + " TEXT );";

    UserDBHelper(Context context) {
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
        AiSouAppInfoModel instance = AiSouAppInfoModel.getInstance();
        if (instance != null) {
            String uid = instance.getUID();
            Log.e(UserInfoDiskHelper.TAG, "getDBName: " + uid);
            return uid + "_user_.db";
        } else {
            return "public_user_.db";
        }
    }
}
