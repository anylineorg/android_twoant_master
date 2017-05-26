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
import net.twoant.master.common_utils.LogUtils;
import net.twoant.master.common_utils.MD5Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by S_Y_H on 2017/2/21.
 * 用户 头像、昵称信息 管理
 */

final class UserManage {

    private UserDBHelper mUserDBHelper;
    private ContentValues mSaveContentValues;

    UserManage(Context context) {
        mUserDBHelper = new UserDBHelper(context);
    }

    /**
     * 保存用户信息
     *
     * @return true 添加成功， FALSE 失败
     */
    synchronized boolean saveUserInfo(EaseUser easeUser) {
        if (null == easeUser) return false;

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();

        if (mSaveContentValues == null) {
            mSaveContentValues = new ContentValues(3);
        } else {
            mSaveContentValues.clear();
        }
        mSaveContentValues.put(UserDBHelper.COLUMN_ID, easeUser.getUsername());
        mSaveContentValues.put(UserDBHelper.COLUMN_MD, MD5Util.getMD5ToHex(easeUser.getAvatar()));
        mSaveContentValues.put(UserDBHelper.COLUMN_NN, easeUser.getNickname());
        return -1 != writableDatabase.replace(UserDBHelper.TABLE_NAME, null, mSaveContentValues);
    }

    @Nullable
    synchronized List<EaseUser> getUserInfo(List<EaseUser> easeUsers) {
        if (easeUsers == null || easeUsers.isEmpty()) return easeUsers;

        String[] strings = new String[easeUsers.size()];
        for (int i = 0, size = strings.length; i < size; ++i) {
            strings[i] = easeUsers.get(i).getUsername();
        }

        return getEaseUsers(strings, mUserDBHelper);
    }

    /**
     * 获取用户信息
     */
    @NonNull
    synchronized List<EaseUser> getUserInfo(String... users) {

        if (users.length == 0) return getDefaultInfo(users);

        List<EaseUser> arrayList = getEaseUsers(users, mUserDBHelper);
        if (arrayList != null) return arrayList;

        return getDefaultInfo(users);
    }

    /**
     * 获取指定用户信息
     */
    synchronized EaseUser getUserInfo(String user) {
        return getEaseUser(user, mUserDBHelper);
    }

    /**
     * 以 key 用户id ， value EaseUser实体 返回数据库中的所有数据
     */
    @Nullable
    synchronized ArrayMap<String, EaseUser> getAllUserInfo() {
        SQLiteDatabase writableDatabase = mUserDBHelper.getReadableDatabase();
        Cursor query = writableDatabase.query(UserDBHelper.TABLE_NAME, null, null, null, null, null, null);

        try {
            if (query.moveToFirst()) {
                ArrayMap<String, EaseUser> map = new ArrayMap<>(query.getCount());
                do {
                    EaseUser easeUser = new EaseUser(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_ID)));
                    easeUser.setNickname(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_NN)));
                    easeUser.setAvatarMd5(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_MD)));
                    easeUser.setAvatar(UserInfoDiskHelper.sImgPath + MD5Util.getMD5ToHex(easeUser.getUsername()) + ".0");
                    map.put(easeUser.getUsername(), easeUser);
                } while (query.moveToNext());
                return map;
            }
        } finally {
            query.close();
        }
        return null;
    }

    /**
     * 获取指定user 的数据
     */
    private static EaseUser getEaseUser(String user, UserDBHelper userDBHelper) {
        if (null == user) return null;
        SQLiteDatabase writableDatabase = userDBHelper.getWritableDatabase();
        Cursor query = writableDatabase.query(UserDBHelper.TABLE_NAME, null,
                UserDBHelper.COLUMN_ID + " =? ", new String[]{user}
                , null, null, null);

        try {
            if (query.moveToFirst()) {
                EaseUser easeUser = new EaseUser(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_ID)));
                easeUser.setNickname(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_NN)));
                easeUser.setAvatarMd5((query.getString(query.getColumnIndex(UserDBHelper.COLUMN_MD))));
                easeUser.setAvatar(UserInfoDiskHelper.sImgPath + MD5Util.getMD5ToHex(easeUser.getUsername()) + ".0");
                return easeUser;
            }
        } finally {
            query.close();
        }
        return null;
    }

    @Nullable
    private static List<EaseUser> getEaseUsers(String[] users, UserDBHelper userDBHelper) {
        SQLiteDatabase writableDatabase = userDBHelper.getReadableDatabase();
        Cursor query;
        if (users != null) {
            query = writableDatabase.query(UserDBHelper.TABLE_NAME, null,
                    UserDBHelper.COLUMN_ID + "  =? ", users
                    , null, null, null);
        } else {
            query = writableDatabase.query(UserDBHelper.TABLE_NAME, null, null, null, null, null, null);
        }

        try {
            if (query.moveToFirst()) {
                ArrayList<EaseUser> arrayList = new ArrayList<>(query.getCount());
                do {
                    EaseUser easeUser = new EaseUser(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_ID)));
                    easeUser.setNickname(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_NN)));
                    easeUser.setAvatarMd5(query.getString(query.getColumnIndex(UserDBHelper.COLUMN_MD)));
                    easeUser.setAvatar(UserInfoDiskHelper.sImgPath + MD5Util.getMD5ToHex(easeUser.getUsername()) + ".0");
                    arrayList.add(easeUser);
                } while (query.moveToNext());
                return arrayList;
            }
        } finally {
            query.close();
        }
        return null;
    }

    /**
     * 更新用户信息
     */
    synchronized void updateUserInfo(EaseUser... users) {
        if (0 == users.length) return;

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2 * users.length);
        String[] strings = new String[1];
        for (EaseUser user : users) {
            updateUserInfo(writableDatabase, contentValues, strings, user);
        }
    }

    synchronized void updateUserInfo(Collection<EaseUser> users) {
        if (null == users || users.isEmpty()) return;

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2 * users.size());
        String[] strings = new String[1];
        for (EaseUser user : users) {
            updateUserInfo(writableDatabase, contentValues, strings, user);
        }
    }

    synchronized boolean updateUserInfo(EaseUser user) {
        if (null == user) return false;

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues(2);
        String[] strings = new String[1];
        return updateUserInfo(writableDatabase, contentValues, strings, user);
    }


    private static boolean updateUserInfo(@NonNull SQLiteDatabase writableDatabase,
                                          @NonNull ContentValues contentValues,
                                          @NonNull String[] strings,
                                          @NonNull EaseUser user) {
        contentValues.clear();
        strings[0] = user.getUsername();
        contentValues.put(UserDBHelper.COLUMN_MD, MD5Util.getMD5ToHex(user.getAvatar()));
        contentValues.put(UserDBHelper.COLUMN_NN, user.getNickname());
        return -1 != writableDatabase.update(UserDBHelper.TABLE_NAME, contentValues, UserDBHelper.COLUMN_ID + " =? ", strings);
    }

    /**
     * 删除用户信息
     */
    synchronized void deleteUserInfo(String... users) {
        if (0 == users.length) return;

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();

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

        SQLiteDatabase writableDatabase = mUserDBHelper.getWritableDatabase();

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
            writableDatabase.delete(UserDBHelper.TABLE_NAME, UserDBHelper.COLUMN_ID + " =? ", names);
        }
    }

    /**
     * 获取默认信息
     */
    private static ArrayList<EaseUser> getDefaultInfo(String... user) {
        ArrayList<EaseUser> easeUsers = new ArrayList<>(user.length);
        for (String str : user) {
            EaseUser easeUser = new EaseUser(str);
            easeUser.setAvatar("");
            easeUser.setUserName(str);
            easeUsers.add(easeUser);
        }
        return easeUsers;
    }

    synchronized void closeDB() {
        if (mUserDBHelper != null) {
            try {
                mUserDBHelper.close();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }
        }
        if (mSaveContentValues != null) {
            mSaveContentValues.clear();
            mSaveContentValues = null;
        }
        Log.d(UserInfoDiskHelper.TAG, "onDestroy: close db");
    }
}
