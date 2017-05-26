package net.twoant.master.ui.convenient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DZY on 2017/2/20.
 * 佛祖保佑   永无BUG
 */

public class ConvenientClassHelper extends SQLiteOpenHelper {

    public ConvenientClassHelper(Context context,int version) {
        super(context, "convenient", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table clazz(" +
                "class_layer text," +   // 存放id
                "parent_id text," +     //上一级id ， 为0为一级菜单
                "sort_id integer," +    //所在通类下的优先级id
                "title text)");          //标题
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("ssss");
    }
}
