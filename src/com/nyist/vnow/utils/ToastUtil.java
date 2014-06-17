package com.nyist.vnow.utils;

import com.nyist.vnow.R;

import android.app.Activity;
import android.content.Context;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {
    private Toast mToast;
    private static ToastUtil mInstance;
    public static int SHORT = Toast.LENGTH_SHORT;
    public static int LONG = Toast.LENGTH_LONG;

    public static ToastUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ToastUtil(context);
        }
        return mInstance;
    }

    private ToastUtil(Context context) {
        mToast = makeToast(context);
    }

    public Toast makeToast(Context context) {
        if (mToast == null) {
            mToast = new Toast(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.toast, null);
            mToast.setView(view);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                    100);
        }
        return mToast;
    }

    /**
     * 
     * @param content
     *            要显示的文本
     * @param duration
     *            {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public void show(int content, int durationTime) {
        try {
            mToast.setText(content);
            mToast.setDuration(durationTime);
            mToast.show();
        }
        catch (Exception e) {
            LogTag.sysout(e);
        }
    }

    /**
     * 
     * @param content
     *            要显示的文本
     * @param duration
     *            {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public void show(Spanned content, int duration) {
        // toast.cancel();
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.show();
    }

    /**
     * 
     * @param content
     *            要显示的文本
     * @param duration
     *            {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public void show(String content, int duration) {
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.show();
    }
    
    /**
     * 
     * @param content
     *            要显示的文本
     * @param duration
     *            {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public void showShort(String content) {
        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
    
    /**
     * 
     * @param content
     *            要显示的文本
     * @param duration
     *            {@link Toast#LENGTH_SHORT} {@link Toast#LENGTH_LONG}
     */
    public void showLong(String content) {
        mToast.setText(content);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }
//
//    public interface MessageFilter
//    {
//        String filter(String msg);
//    }
//
//    public static MessageFilter msgFilter;
//
//    public static void showShort(
//            final Activity activity, final String message
//            )
//    {
//        final String msg = msgFilter != null ? msgFilter
//                .filter(message) : message;
//        activity.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                Toast toast = Toast.makeText(activity, msg,
//                        Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        });
//    }
//
//    /**
//     * 长时间显示Toast消息，并保证运行在UI线程中
//     * 
//     * @param activity
//     *            Activity
//     * @param message
//     *            消息内容
//     */
//    public static void showLong(final Activity activity,
//            final String message)
//    {
//        final String msg = msgFilter != null ? msgFilter
//                .filter(message) : message;
//        activity.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                Toast.makeText(activity, msg,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    /**
//     * 以较长时间显示Toast消息
//     * 
//     * @param activity
//     *            Activity
//     * @param msgResID
//     *            消息资源ID
//     */
//    public static void showLong(Activity activity, int msgResID)
//    {
//        showLong(activity,
//                activity.getResources().getString(msgResID));
//    }
//
//
//    /**
//     * 以较短时间显示Toast消息
//     * 
//     * @param activity
//     *            Activity
//     * @param msgResID
//     *            消息资源ID
//     */
//    public static void showShort(Activity activity, int msgResID)
//    {
//        showShort(activity,
//                activity.getResources().getString(msgResID));
//    }
}
