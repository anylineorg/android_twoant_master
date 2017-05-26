package net.twoant.master.ui.chat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import net.twoant.master.app.AiSouAppInfoModel;
import net.twoant.master.ui.chat.Constant;
import net.twoant.master.ui.chat.domain.InviteMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ChatDBManager {

    private static ChatDBManager mChatDBManager;
    private DbOpenHelper mDbOpenHelper;

    private ChatDBManager() {
        mDbOpenHelper = DbOpenHelper.getInstance(AiSouAppInfoModel.getAppContext());
    }

    public static ChatDBManager getInstance() {
        if (mChatDBManager == null) {
            synchronized (ChatDBManager.class) {
                if (null == mChatDBManager) {
                    mChatDBManager = new ChatDBManager();
                }
            }
        }
        return mChatDBManager;
    }

    @Nullable
    public static ChatDBManager getInstanceNull() {
        return mChatDBManager;
    }

    /**
     * 保存消息列表
     */
    synchronized void saveContactList(Collection<EaseUser> contactList) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                if (user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    /**
     * 获取消息列表
     */
    public synchronized Map<String, EaseUser> getContactList() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Hashtable<String, EaseUser> users = new Hashtable<String, EaseUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                        EaseUser user = new EaseUser(username);
                        user.setNick(cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK)));
                        user.setAvatar(cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR)));
                        if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                                || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                            user.setInitialLetter("");
                        } else {
                            EaseCommonUtils.setUserInitialLetter(user);
                        }
                        users.put(username, user);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return users;
    }

    /**
     * 删除联系人
     */
    synchronized void deleteContact(String username) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存联系人
     */
    synchronized void saveContact(EaseUser user) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        if (user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    @Nullable
    private synchronized List<String> getList(String column) {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array.length > 0) {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, array);
            return list;
        }

        return null;
    }


    synchronized Integer saveMessage(InviteMessage message) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessageDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessageDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessageDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessageDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessageDao.TABLE_NAME, null);
            try {
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return id;
    }

    synchronized void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessageDao.TABLE_NAME, values, InviteMessageDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    synchronized List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " desc", null);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        InviteMessage msg = new InviteMessage();
                        int id = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_ID));
                        String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                        String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                        String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_Name));
                        String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                        long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                        int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));
                        String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUPINVITER));

                        msg.setId(id);
                        msg.setFrom(from);
                        msg.setGroupId(groupid);
                        msg.setGroupName(groupname);
                        msg.setReason(reason);
                        msg.setTime(time);
                        msg.setGroupInviter(groupInviter);

                        if (status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
                        else if (status == InviteMessage.InviteMesageStatus.BEAGREED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
                        else if (status == InviteMessage.InviteMesageStatus.BEREFUSED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEREFUSED);
                        else if (status == InviteMessage.InviteMesageStatus.AGREED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                        else if (status == InviteMessage.InviteMesageStatus.REFUSED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.REFUSED);
                        else if (status == InviteMessage.InviteMesageStatus.BEAPPLYED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
                        else if (status == InviteMessage.InviteMesageStatus.GROUPINVITATION.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION);
                        else if (status == InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED);
                        else if (status == InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED.ordinal())
                            msg.setStatus(InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED);

                        msgs.add(msg);
                    } while (cursor.moveToNext());

                }
            } finally {
                cursor.close();
            }
        }
        return msgs;
    }

    synchronized void deleteMessage(String from) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessageDao.TABLE_NAME, null);
            try {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessageDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (mDbOpenHelper != null) {
            mDbOpenHelper.close();
            mDbOpenHelper = null;
        }
        mChatDBManager = null;
    }
}
