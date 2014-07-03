package com.nyist.vnow.struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VnowInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String VNOW_INFO = "vnow_info";
    public static final String CLOUD_RESPONSE_BODY = "cloud_response_body";
    public static final String CLOUD_RESPONSE_STATUS_CODE = "cloud_response_status_code";
    public static final String FRAGMENT_ARGUMENTS = "fragment_arguments";
    private HashMap<String, Object> mInfoMap;

    public VnowInfo() {
        mInfoMap = new HashMap<String, Object>();
    }

    public VnowInfo(HashMap<String, Object> map) {
        mInfoMap = map;
    }

    public void putInfo(String key, Object value) {
        mInfoMap.put(key, value);
    }

    public void removeInfo(String key) {
        mInfoMap.remove(key);
    }

    public ArrayList getUserInfoArrayList(String key) {
        return containsKey(key) ? (ArrayList) mInfoMap.get(key) : null;
    }

    public void clear() {
        mInfoMap.clear();
    }

    public boolean containsKey(String key) {
        return mInfoMap.containsKey(key);
    }

    public Object getInfoValue(String key) {
        return getInfoValue(key, null);
    }

    public Object getInfoValue(String key, Object defaultValue) {
        if (mInfoMap.containsKey(key)) {
            return mInfoMap.get(key);
        }
        else {
            return defaultValue;
        }
    }

    public String getString(String key) {
        return mInfoMap.containsKey(key) ? (String) mInfoMap.get(key) : null;
    }

    public int getInt(String key) {
        return mInfoMap.containsKey(key) ? (Integer) mInfoMap.get(key) : 0;
    }

    public Class getObjectClass(String key) {
        return mInfoMap.containsKey(key) ? (Class) mInfoMap.get(key) : null;
    }

    public String[] getStringArray(String key) {
        return mInfoMap.containsKey(key) ? (String[]) mInfoMap.get(key) : null;
    }

    public boolean getBoolean(String key) {
        return mInfoMap.containsKey(key) ? (Boolean) mInfoMap.get(key) : false;
    }

    public HashMap<String, Object> getUserInfo() {
        return mInfoMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : mInfoMap.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return sb.toString();
    }
}
