package com.nyist.vnow.utils;

import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class VNowSetting {
		private Context mContext;	
		private SharedPreferences mSharedSettings;
		private Editor mEditor;
		private static VNowSetting mSetting;
		
		public static VNowSetting the(Context context) {
			if(null == mSetting) {
				mSetting = new VNowSetting(context);
			}
			return mSetting;
		}
		
		public VNowSetting(Context context) {
			mContext = context;
			mSharedSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
			mEditor = mSharedSettings.edit();
		}
		
		public void setSetting(Map<String ,String> map) {
			Iterator iter = map.entrySet().iterator(); 
			while (iter.hasNext()) { 
			    Map.Entry entry = (Map.Entry) iter.next(); 
			    String key = (String)entry.getKey(); 
			    String val = (String)entry.getValue(); 
			    mEditor.putString(key, val);
			} 
			mEditor.commit();
		}
		
		public void setSetting(String prefName, String value) {
			mEditor.putString(prefName, value);
			mEditor.commit();
		}
		
		public void setSetting(String prefName, boolean value) {
			mEditor.putBoolean(prefName, value);
			mEditor.commit();
		}
		public void setSetting(String prefName, int value) {
			mEditor.putInt(prefName, value);
			mEditor.commit();
		}
		public String getSetting(String prefName, String deftValue) {
			return mSharedSettings.getString(prefName, deftValue);
		}
		public boolean getSetting(String prefName, boolean deftValue) {
			return mSharedSettings.getBoolean(prefName, deftValue);
		}
		
		public int getSetting(String prefName, int deftValue) {
			return mSharedSettings.getInt(prefName,deftValue);
		}

}
