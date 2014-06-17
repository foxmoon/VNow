package com.nyist.vnow.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.Friend;
import com.nyist.vnow.struct.Group;
import com.nyist.vnow.struct.VNConfItem;
import com.nyist.vnow.struct.VNowFriend;
import com.nyist.vnow.struct.VNowRctContact;

public class VNTaskDao {
    private VNDBHelper mDbHelper;
    private Context mContext;
    private SQLiteDatabase _db;

    public VNTaskDao(Context context) {
        mDbHelper = new VNDBHelper(context);
        mContext = context;
        _db = mDbHelper.getWritableDatabase();
    }

    public void insertHistoryData(VNConfItem jInfo) {
        if (_db.isOpen()) {
            ContentValues values;
            values = new ContentValues();
            values.put("jStrName", jInfo.getmConfName());
            values.put("jStrTheme", jInfo.getmConfTheme());
            values.put("jStrTime", jInfo.getmConfTime());
            _db.insert(VNDBHelper.USER_TABLE_NAME, null, values);
        }
    }

    public ArrayList<VNConfItem> getMyHistoryData() {
        ArrayList<VNConfItem> taskInfoList = null;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.USER_TABLE_NAME;
            Cursor cursor = _db.rawQuery(sql, null);
            taskInfoList = new ArrayList<VNConfItem>();
            VNConfItem jInfo = null;
            while (cursor.moveToNext()) {
                jInfo = new VNConfItem();
                jInfo.setmConfName(cursor.getString(cursor
                        .getColumnIndex("jStrName")));
                jInfo.setmConfTheme(cursor.getString(cursor
                        .getColumnIndex("jStrTheme")));
                jInfo.setmConfTime(cursor.getString(cursor
                        .getColumnIndex("jStrTime")));
                taskInfoList.add(jInfo);
            }
        }
        return taskInfoList;
    }

    public void deleteHistInfo(String confName) {
        if (_db.isOpen()) {
            _db.execSQL("delete from " + VNDBHelper.USER_TABLE_NAME
                    + " where jStrName = " + confName);
        }
    }

    public void insertRctContact(VNowRctContact rctItem) {
        if (_db.isOpen()) {
            ContentValues values;
            values = new ContentValues();
            values.put("jUserId", rctItem.getmStrUserId());
            values.put("jUserName", rctItem.getmStrUserName());
            values.put("jContactName", rctItem.getmStrContactName());
            values.put("jContactPhone", rctItem.getmStrConPhone());
            values.put("jCallType", rctItem.ismIsCallIn() ? 1 : 0);
            values.put("jStrTime", rctItem.getmCallTime());
            String selSql = "select * from " + VNDBHelper.RCT_CALL_HISTORY
                    + " where jContactName = '" + rctItem.getmStrContactName()
                    + "' and jUserId = '" + rctItem.getmStrUserId() + "'";
            Cursor cursor = _db.rawQuery(selSql, null);
            if (cursor.moveToNext()) {
                _db.update(
                        VNDBHelper.RCT_CALL_HISTORY,
                        values,
                        "jContactName = ? and jUserId = ?",
                        new String[] { rctItem.getmStrContactName(),
                                rctItem.getmStrUserId() });
            }
            else {
                _db.insert(VNDBHelper.RCT_CALL_HISTORY, null, values);
            }
        }
    }

    public ArrayList<VNowRctContact> getCallHistory(String userId) {
        ArrayList<VNowRctContact> rctContactList = null;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.RCT_CALL_HISTORY
                    + " where jUserId = '" + userId
                    + "' ORDER BY jStrTime DESC";
            Cursor cursor = _db.rawQuery(sql, null);
            rctContactList = new ArrayList<VNowRctContact>();
            VNowRctContact rctItem = null;
            while (cursor.moveToNext()) {
                rctItem = new VNowRctContact();
                rctItem.setmStrUserId(cursor.getString(cursor
                        .getColumnIndex("jUserId")));
                rctItem.setmStrUserName(cursor.getString(cursor
                        .getColumnIndex("jUserName")));
                rctItem.setmStrContactName(cursor.getString(cursor
                        .getColumnIndex("jContactName")));
                rctItem.setmStrContactName(getStrName(cursor.getString(cursor
                        .getColumnIndex("jContactPhone"))));
                rctItem.setmStrConPhone(cursor.getString(cursor
                        .getColumnIndex("jContactPhone")));
                rctItem.setmCallTime(cursor.getLong(cursor
                        .getColumnIndex("jStrTime")));
                rctItem.setmIsCallIn(cursor.getInt(cursor
                        .getColumnIndex("jCallType")) == 0 ? false : true);
                rctContactList.add(rctItem);
            }
        }
        return rctContactList;
    }

    public int deleteCallHistory(String userId, String rctItemName) {
        if (_db.isOpen()) {
            return _db.delete(VNDBHelper.RCT_CALL_HISTORY, " jUserId = '"
                    + userId + "' and jContactName = '" + rctItemName + "'",
                    null);
        }
        return -1;
    }

    public String getStrName(String phoneNum) {
        String name = getColleageName(_db, phoneNum);
        if (null == name) {
            String desNum = new DES().decrypt(phoneNum);
            List<VNowFriend> list = VNowApplication.getInstance().getCore()
                    .getmListPhoneContacts();
            if (list.size() == 0) {
                VNowApplication.getInstance().getCore().getPhoneContacts();
                VNowApplication.getInstance().getCore().getSIMContacts();
                list = VNowApplication.getInstance().getCore()
                        .getmListPhoneContacts();
            }
            for (int i = 0; i < list.size(); i++) {
                if (desNum.equals(list.get(i).getmPhoneNum())) {
                    name = list.get(i).getmName();
                }
            }
        }
        if (null == name) {
            name = "未知";
        }
        return name;
    }

    // g_name text, g_pid text, g_type text, g_phone text,"
    // + " jCallType text, g_code text,g_updatenum text,g_createtime text,g_head
    // text,g_uuid text
    public void insertColleage(Colleage colleage, String userId) {
        if (_db.isOpen()) {
            ContentValues values;
            values = new ContentValues();
            values.put("jUserId", userId);
            values.put("g_name", colleage.getG_name());
            values.put("g_pid", colleage.getG_pid());
            values.put("g_type", colleage.getG_type());
            values.put("g_phone", colleage.getG_phone());
            values.put("g_code", colleage.getG_code());
            values.put("g_updatenum", colleage.getG_updatenum());
            values.put("g_createtime", colleage.getG_createtime());
            values.put("g_head", colleage.getG_head());
            values.put("g_uuid", colleage.getG_uuid());
            _db.insert(VNDBHelper.VN_COLLEAGE, null, values);
        }
    }

    public ArrayList<Colleage> getVNColleage(String userId) {
        ArrayList<Colleage> colleageList = null;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_COLLEAGE
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            colleageList = new ArrayList<Colleage>();
            Colleage colleageItem = null;
            while (cursor.moveToNext()) {
                colleageItem = new Colleage();
                colleageItem.setG_name(cursor.getString(cursor
                        .getColumnIndex("g_name")));
                colleageItem.setG_pid(cursor.getString(cursor
                        .getColumnIndex("g_pid")));
                colleageItem.setG_type(cursor.getString(cursor
                        .getColumnIndex("g_type")));
                colleageItem.setG_phone(cursor.getString(cursor
                        .getColumnIndex("g_phone")));
                colleageItem.setG_code(cursor.getString(cursor
                        .getColumnIndex("g_code")));
                colleageItem.setG_updatenum(cursor.getString(cursor
                        .getColumnIndex("g_updatenum")));
                colleageItem.setG_createtime(cursor.getString(cursor
                        .getColumnIndex("g_createtime")));
                colleageItem.setG_head(cursor.getString(cursor
                        .getColumnIndex("g_head")));
                colleageItem.setG_uuid(cursor.getString(cursor
                        .getColumnIndex("g_uuid")));
                colleageList.add(colleageItem);
            }
        }
        return colleageList;
    }

    public String getColleageName(SQLiteDatabase db, String phoneNum) {
        String name = null;
        if (db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_COLLEAGE
                    + " where g_phone = '" + phoneNum + "'";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex("g_name"));
            }
        }
        if (name == null) {
            if (db.isOpen()) {
                String sql = "SELECT * FROM " + VNDBHelper.VN_FRIEND
                        + " where f_phone = '" + phoneNum + "'";
                Cursor cursor = db.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    name = cursor.getString(cursor.getColumnIndex("f_name"));
                }
            }
        }
        return name;
    }

    public int deleteColleage(String userId, String g_phone) {
        if (_db.isOpen()) {
            return _db.delete(VNDBHelper.VN_COLLEAGE, " jUserId = '"
                    + userId + "' and g_phone = '" + g_phone + "'",
                    null);
        }
        return -1;
    }

    public void deleteAllColleage(String userId) {
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_COLLEAGE
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                _db.delete(VNDBHelper.VN_COLLEAGE, " jUserId = '"
                        + userId + "'",
                        null);
            }
        }
    }

    public int reLoadColleage(String userId, List<Colleage> newList) {
        List<Colleage> oldList = getVNColleage(userId);
        for (Colleage new_colleage : newList) {
            boolean canAdd = true;
            for (Colleage old_colleage : oldList) {
                if (old_colleage.getG_phone().equals(new_colleage.getG_phone())) {
                    int errorCode = deleteColleage(userId, old_colleage.getG_phone());
                    if (errorCode != -1)
                        insertColleage(new_colleage, userId);
                    canAdd = false;
                    break;
                }
            }
            if (canAdd)
                insertColleage(new_colleage, userId);
        }
        return -1;
    }

    // jUserId text,f_uuid text, f_phone text, f_updatenum text, f_createtime
    // text,"
    // + " f_a_uuid text, f_name text,f_head text)"
    public void insertFriend(Friend friend, String userId) {
        if (_db.isOpen()) {
            ContentValues values;
            values = new ContentValues();
            values.put("jUserId", userId);
            values.put("f_uuid", friend.getF_uuid());
            values.put("f_phone", friend.getF_phone());
            values.put("f_updatenum", friend.getF_updatenum());
            values.put("f_createtime", friend.getF_createtime());
            values.put("f_a_uuid", friend.getF_a_uuid());
            values.put("f_name", friend.getF_name());
            values.put("f_head", friend.getF_head());
            _db.insert(VNDBHelper.VN_FRIEND, null, values);
        }
    }

    public ArrayList<Friend> getVNFriend(String userId) {
        ArrayList<Friend> friendList = null;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_FRIEND
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            friendList = new ArrayList<Friend>();
            Friend friendItem = null;
            while (cursor.moveToNext()) {
                friendItem = new Friend();
                friendItem.setF_uuid(cursor.getString(cursor
                        .getColumnIndex("f_uuid")));
                friendItem.setF_phone(cursor.getString(cursor
                        .getColumnIndex("f_phone")));
                friendItem.setF_updatenum(cursor.getString(cursor
                        .getColumnIndex("f_updatenum")));
                friendItem.setF_createtime(cursor.getString(cursor
                        .getColumnIndex("f_createtime")));
                friendItem.setF_a_uuid(cursor.getString(cursor
                        .getColumnIndex("f_a_uuid")));
                friendItem.setF_name(cursor.getString(cursor
                        .getColumnIndex("f_name")));
                friendItem.setF_head(cursor.getString(cursor
                        .getColumnIndex("f_head")));
                friendList.add(friendItem);
            }
        }
        return friendList;
    }

    public int deleteFriend(String userId, String f_phone) {
        if (_db.isOpen()) {
            return _db.delete(VNDBHelper.VN_FRIEND, " jUserId = '"
                    + userId + "' and f_phone = '" + f_phone + "'",
                    null);
        }
        return -1;
    }

    public void deleteAllFriend(String userId) {
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_FRIEND
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                _db.delete(VNDBHelper.VN_FRIEND, " jUserId = '"
                        + userId + "'",
                        null);
            }
        }
    }

    public int reLoadFriend(String userId, List<Friend> newList) {
        List<Friend> oldList = getVNFriend(userId);
        for (Friend new_friend : newList) {
            boolean canAdd = true;
            for (Friend old_friend : oldList) {
                if (old_friend.getF_phone().equals(new_friend.getF_phone())) {
                    int errorCode = deleteFriend(userId, old_friend.getF_phone());
                    if (errorCode != -1)
                        insertFriend(new_friend, userId);
                    canAdd = false;
                    break;
                }
            }
            if (canAdd)
                insertFriend(new_friend, userId);
        }
        return -1;
    }

    // ----------------------------group--------------------
    public void insertGroup(Group group, String userId) {
        if (_db.isOpen()) {
            ContentValues values;
            values = new ContentValues();
            values.put("jUserId", userId);
            values.put("updatenum", group.getUpdatenum());
            values.put("a_uuid", group.getA_uuid());
            values.put("uuid", group.getUuid());
            values.put("phone", group.getPhone());
            values.put("createtime", group.getCreatetime());
            values.put("parentId", group.getParentId());
            values.put("head", group.getHead());
            values.put("name", group.getName());
            values.put("type", group.getType());
            _db.insert(VNDBHelper.VN_GROUP, null, values);
        }
    }

    public ArrayList<Group> getVNGroup(String userId) {
        ArrayList<Group> groupList = null;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_GROUP
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            groupList = new ArrayList<Group>();
            Group groupItem = null;
            while (cursor.moveToNext()) {
                groupItem = new Group();
                groupItem.setUpdatenum(cursor.getString(cursor
                        .getColumnIndex("updatenum")));
                groupItem.setA_uuid(cursor.getString(cursor
                        .getColumnIndex("a_uuid")));
                groupItem.setUuid(cursor.getString(cursor
                        .getColumnIndex("uuid")));
                groupItem.setPhone(cursor.getString(cursor
                        .getColumnIndex("phone")));
                groupItem.setCreatetime(cursor.getString(cursor
                        .getColumnIndex("createtime")));
                groupItem.setParentId(cursor.getString(cursor
                        .getColumnIndex("parentId")));
                groupItem.setHead(cursor.getString(cursor
                        .getColumnIndex("head")));
                groupItem.setName(cursor.getString(cursor
                        .getColumnIndex("name")));
                groupItem.setType(cursor.getString(cursor
                        .getColumnIndex("type")));
                groupList.add(groupItem);
            }
        }
        return groupList;
    }

    public int deleteGroup(String userId, String uuid) {
        if (_db.isOpen()) {
            return _db.delete(VNDBHelper.VN_GROUP, " jUserId = '"
                    + userId + "' and uuid = '" + uuid + "'",
                    null);
        }
        int serverVersion = VNowApplication.getInstance().getSetting(mContext.getString(R.string.group_update_version) + userId, 0);
        VNowApplication.getInstance().setSetting(mContext.getString(R.string.group_update_version) + userId, serverVersion - 1);
        return -1;
    }

    public void deleteAllGroup(String userId) {
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_GROUP
                    + " where jUserId = '" + userId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                _db.delete(VNDBHelper.VN_GROUP, " jUserId = '"
                        + userId + "'",
                        null);
            }
        }
    }

    public void deleteGChild(String userId, String parentId) {
        int versionMute = 0;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_GROUP
                    + " where jUserId = '" + userId + "' and parentId = '"
                    + parentId + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            versionMute = cursor.getCount();
            while (cursor.moveToNext()) {
                _db.delete(VNDBHelper.VN_GROUP, " jUserId = '"
                        + userId + "' and uuid = '" + cursor.getString(cursor
                                .getColumnIndex("uuid")) + "'",
                        null);
            }
        }
        int serverVersion = VNowApplication.getInstance().getSetting(mContext.getString(R.string.group_update_version) + userId, 0);
        VNowApplication.getInstance().setSetting(mContext.getString(R.string.group_update_version) + userId, serverVersion - versionMute);
    }

    public void deleteChild(String userId, String phone) {
        int versionMute = 0;
        if (_db.isOpen()) {
            String sql = "SELECT * FROM " + VNDBHelper.VN_GROUP
                    + " where jUserId = '" + userId + "' and phone = '"
                    + phone + "'";
            Cursor cursor = _db.rawQuery(sql, null);
            versionMute = cursor.getCount();
            while (cursor.moveToNext()) {
                _db.delete(VNDBHelper.VN_GROUP, " jUserId = '"
                        + userId + "' and uuid = '" + cursor.getString(cursor
                                .getColumnIndex("uuid")) + "'",
                        null);
            }
        }
        int serverVersion = VNowApplication.getInstance().getSetting(mContext.getString(R.string.group_update_version) + userId, 0);
        VNowApplication.getInstance().setSetting(mContext.getString(R.string.group_update_version) + userId, serverVersion - versionMute);
    }

    public int reLoadGroup(String userId, List<Group> newList) {
        List<Group> oldList = getVNGroup(userId);
        for (Group new_group : newList) {
            boolean canAdd = true;
            for (Group old_group : oldList) {
                if (old_group.getUuid().equals(new_group.getUuid())) {
                    int errorCode = deleteGroup(userId, old_group.getUuid());
                    if (errorCode != -1)
                        insertGroup(new_group, userId);
                    canAdd = false;
                    break;
                }
            }
            if (canAdd)
                insertGroup(new_group, userId);
        }
        return -1;
    }

    public void closeDB() {
        if (_db.isOpen())
            _db.close();
    }
}
