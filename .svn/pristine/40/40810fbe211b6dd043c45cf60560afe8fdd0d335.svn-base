package com.nyist.vnow.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 配置文件内容
 * 
 * @author harry
 */
public class PreferencesUtils {

	public static final String P_CONFIG = "p_config"; //配置文件不删除的
	private Context mCtx;
	private Editor mEditor;
	private SharedPreferences mPreferences;

	// 根据文件名，取配置文件
	public PreferencesUtils(Context context, String preferenceName) {
		mCtx = context;
		mPreferences = mCtx.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
	}

	// 不包含文件名，取默认配置文件
	public PreferencesUtils(Context context) {
		mCtx = context;
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mPreferences.getBoolean(key, defValue);
	}

	public void putBoolean(String key, boolean state) {
		mEditor = mPreferences.edit();
		mEditor.putBoolean(key, state);
		mEditor.commit();
	}

	public String getString(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}

	public void putString(String key, String value) {
		mEditor = mPreferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public int getInt(String key, int defValue) {
		return mPreferences.getInt(key, defValue);
	}

	public void putInt(String key, int value) {
		mEditor = mPreferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public void putLong(String key, long value) {
		mEditor = mPreferences.edit();
		mEditor.putLong(key, value);
		mEditor.commit();
	}

	public void remove(String key) {
		mEditor = mPreferences.edit();
		mEditor.remove(key);
		mEditor.commit();
	}

	public long getLong(String key, Long defValue) {
		return mPreferences.getLong(key, defValue);
	}

	public boolean contains(String key) {
		return mPreferences.contains(key);
	}
	public boolean clear(){
		mEditor = mPreferences.edit();
		this.mEditor.clear();
		return mEditor.commit();
	}
}
