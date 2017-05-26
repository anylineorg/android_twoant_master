package net.twoant.master.ui.chat.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.GroupInfoBean;
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by S_Y_H on 2017/2/21.
 * 用户 头像、昵称信息 管理
 */

final class GroupManage {

    private GroupDBHelper mGroupDBHelper;
    private ContentValues mSaveContentValues;

    GroupManage(Context context) {
        mGroupDBHelper = new GroupDBHelper(context);
    }

    /**
     * 保存用户信息
     *
     * @return true 添加成功， FALSE 失败
     */
    synchronized boolean saveUserInfo(GroupInfoBean groupInfoBean) {
        if (null == groupInfoBean) return false;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();

        if (mSaveContentValues == null) {
            mSaveContentValues = new ContentValues(2);
        } else {
            mSaveContentValues.clear();
        }
        mSaveContentValues.put(GroupDBHelper.COLUMN_ID, groupInfoBean.getGroupId());
        mSaveContentValues.put(GroupDBHelper.COLUMN_MD, MD5Util.getMD5ToHex(groupInfoBean.getGroupAvatar()));
        return -1 != writableDatabase.replace(GroupDBHelper.TABLE_NAME, null, mSaveContentValues);
    }


    /**
     * 以 key 用户id ， value EaseUser实体 返回数据库中的所有数据
     */
    @Nullable
    synchronized ArrayMap<String, GroupInfoBean> getAllUserInfo() {
        SQLiteDatabase writableDatabase = mGroupDBHelper.getReadableDatabase();
        Cursor query = writableDatabase.query(GroupDBHelper.TABLE_NAME, null, null, null, null, null, null);

        try {
            if (query.moveToFirst()) {
                ArrayMap<String, GroupInfoBean> map = new ArrayMap<>(query.getCount());
                do {
                    GroupInfoBean easeUser = new GroupInfoBean(query.getString(query.getColumnIndex(GroupDBHelper.COLUMN_ID)));
                    easeUser.setGroupAvatar(query.getString(query.getColumnIndex(GroupDBHelper.COLUMN_MD)));
                    easeUser.setGroupAvatar(UserInfoDiskHelper.sImgPath + MD5Util.getMD5ToHex(easeUser.getGroupId()) + ".0");
                    map.put(easeUser.getGroupId(), easeUser);
                } while (query.moveToNext());
                return map;
            }
        } finally {
            query.close();
        }
        return null;
    }

    /**
     * 更新用户信息
     */
    synchronized void updateUserInfo(GroupInfoBean... users) {
        if (0 == users.length) return;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2 * users.length);
        String[] strings = new String[1];
        for (GroupInfoBean user : users) {
            updateUserInfo(writableDatabase, contentValues, strings, user);
        }
    }

    synchronized void updateUserInfo(Collection<GroupInfoBean> users) {
        if (null == users || users.isEmpty()) return;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2 * users.size());
        String[] strings = new String[1];
        for (GroupInfoBean user : users) {
            updateUserInfo(writableDatabase, contentValues, strings, user);
        }
    }

    synchronized boolean updateUserInfo(GroupInfoBean user) {
        if (null == user) return false;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2);
        String[] strings = new String[1];
        return updateUserInfo(writableDatabase, contentValues, strings, user);
    }


    private static boolean updateUserInfo(@NonNull SQLiteDatabase writableDatabase,
                                          @NonNull ContentValues contentValues,
                                          @NonNull String[] strings,
                                          @NonNull GroupInfoBean user) {
        contentValues.clear();
        strings[0] = user.getGroupId();
        contentValues.put(GroupDBHelper.COLUMN_MD, MD5Util.getMD5ToHex(user.getGroupAvatar()));
        return -1 != writableDatabase.update(GroupDBHelper.TABLE_NAME, contentValues, GroupDBHelper.COLUMN_ID + " =? ", strings);
    }

    /**
     * 删除用户信息
     */
    synchronized void deleteUserInfo(String... users) {
        if (0 == users.length) return;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();

        ArrayList<String> arrayString = new ArrayList<>(users.length);

        for (String user : users) {
            if (null != user) {//去空
                arrayString.add(user);
            }
        }
        deleteData(writableDatabase, arrayString);

    }

    /**
     * 删除用户信息
     */
    synchronized void deleteUserInfo(Collection<EaseUser> users) {
        if (users == null || users.isEmpty()) return;

        SQLiteDatabase writableDatabase = mGroupDBHelper.getWritableDatabase();

        ArrayList<String> arrayString = new ArrayList<>(users.size());

        String temp;
        for (EaseUser easeUser : users) {
            if (null != easeUser && null != (temp = easeUser.getUsername())) {//去空
                arrayString.add(temp);
            }
        }

        deleteData(writableDatabase, arrayString);
    }

    private static void deleteData(SQLiteDatabase writableDatabase, ArrayList<String> arrayString) {
        String[] names = new String[1];
        for (String name : arrayString) {
            names[0] = name;
            writableDatabase.delete(GroupDBHelper.TABLE_NAME, GroupDBHelper.COLUMN_ID + " =? ", names);
        }
    }

    synchronized void closeDB() {
        if (mGroupDBHelper != null) {
            try {
                mGroupDBHelper.close();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
        if (mSaveContentValues != null) {
            mSaveContentValues.clear();
            mSaveContentValues = null;
        }
        Log.d(UserInfoDiskHelper.TAG, "onDestroy: group close db");
    }
}
