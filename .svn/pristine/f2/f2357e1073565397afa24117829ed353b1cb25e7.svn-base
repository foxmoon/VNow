package com.nyist.vnow.db;

import com.nyist.vnow.core.VNowApplication;

public class DBController {
    private static final int DB_VERSION = 1;
    private static DatabaseHelper db = null;
    private static final String DB_NAME = "vnow.db";

    public static void createDB() {
        initialDB();
    }

    private static synchronized void initialDB() {
        if (db == null) {
            db = new DatabaseHelper(VNowApplication.mContext, DB_NAME, null, DB_VERSION);
        }
    }

    public static DatabaseHelper getDB() throws DBNotInitializeException {
        initialDB();
        if (db == null) {
            throw new DBNotInitializeException("DB not created.");
        }
        return db;
    }

    public static synchronized void destoryDB() {
        if (db != null) {
            db.close();
            // OpenHelperManager.releaseHelper();
            db = null;
        }
    }

    public static synchronized void clearAllData() {
        if (db != null) {
            db.clearAllData();
        }
    }
}
