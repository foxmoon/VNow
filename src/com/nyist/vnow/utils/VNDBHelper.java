package com.nyist.vnow.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class VNDBHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String DB_NAME = "vnow";
	public static String USER_TABLE_NAME = "vn_conf";
	public static String RCT_CALL_HISTORY = "vn_call_history";
	public static String VN_COLLEAGE = "vn_colleage";
	public static String VN_FRIEND = "vn_friend";
	public static String VN_GROUP = "vn_group";
//	private SQLiteDatabase mDB;
	
	public VNDBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists "
				+ USER_TABLE_NAME
				+ " (_id integer primary key autoincrement,jStrName text,jStrTheme text,jStrTime text)");
		db.execSQL("create table if not exists "
				+ RCT_CALL_HISTORY
				+ " (_id integer primary key autoincrement,jUserId text, jUserName text, jContactName text, jContactPhone text, jCallType integer, jStrTime long not null)");
		db.execSQL("create table if not exists "
				+ VN_COLLEAGE
				+ " (_id integer primary key autoincrement,jUserId text,g_name text, g_pid text, g_type text, g_phone text,"
				+ " g_code text,g_updatenum text,g_createtime text,g_head text,g_uuid text)");
		db.execSQL("create table if not exists "
				+ VN_GROUP
				+ " (_id integer primary key autoincrement,jUserId text,updatenum text, a_uuid text, uuid text, phone text,"
				+ " createtime text, parentId text,head text,name text,type text)");
		db.execSQL("create table if not exists "
				+ VN_FRIEND
				+ " (_id integer primary key autoincrement,jUserId text, f_uuid text, "
				+ "f_phone text, f_updatenum text,"
				+ " f_createtime text,f_a_uuid text,f_name text,f_head text)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "drop table if exists "+USER_TABLE_NAME;
		String rctsql = "drop table if exists "+RCT_CALL_HISTORY;
		String colleagesql = "drop table if exists "+VN_COLLEAGE;
		String friendsql = "drop table if exists "+VN_FRIEND;
		String groupsql = "drop table if exists "+VN_GROUP;
		db.execSQL(sql);
		db.execSQL(rctsql);
		db.execSQL(colleagesql);
		db.execSQL(friendsql);
		db.execSQL(groupsql);
		onCreate(db);
	}
}
