package com.nyist.vnow.utils;

import android.os.Looper;


/**
 * @author harry
 * @version Creat on 2014-6-24上午9:11:59
 */
public class ThreadHelper {

    public static String getThreadInfo() {

        String textMainThread =
                Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()
                        ? " - the MAIN thread!"
                        : " - NOT the MAIN thread!";

        return "Thread " + Thread.currentThread().getId() + textMainThread;
    }

}
