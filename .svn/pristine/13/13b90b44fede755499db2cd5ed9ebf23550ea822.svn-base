package com.nyist.vnow.db;

import java.util.HashMap;


/**
 * 刷新管理
 * @author harry
 * @version Creat on 2014-6-19下午2:35:42
 */
public class RefreshManager {
	private static RefreshManager instance;
	public HashMap<String, Long> mRefreshMap = null;
	public static final long REFRESH_GAP = 1000l * 60 * 5;

	public static final RefreshManager getInstance() {
		if (instance == null) {
			instance = new RefreshManager();
		}
		return instance;
	}

	private RefreshManager() {
		mRefreshMap = new HashMap<String, Long>();
	}

	public boolean needRefresh(String key) {
		if (!mRefreshMap.containsKey(key)) {
			mRefreshMap.put(key, System.currentTimeMillis());
			return true;
		}
		long timestamp = mRefreshMap.get(key);
		if (System.currentTimeMillis() - timestamp > REFRESH_GAP) {
			mRefreshMap.put(key, System.currentTimeMillis());
			return true;
		}
		return false;
	}

	public boolean needRefresh(Class<?> key, String id) {
		return needRefresh(key.getName() + "_" + id);
	}

	public boolean needRefresh(Class<?> clz) {
		return needRefresh(clz.getName());
	}

	public void remove(String key) {
		mRefreshMap.remove(key);
	}

	public void release() {
		mRefreshMap.clear();
	}

}
