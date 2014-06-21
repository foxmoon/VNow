package com.nyist.vnow.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 检测网络
 * 
 * @author chenjh
 * @version Creat on 2014-4-11下午5:38:44
 */
public class NetUtil {
    public static boolean checkNet(Context context) {
        return checkNet(context, false);
    }

    /**
     * @param context
     * @param showToast
     * @return 如果有网络返回true，没有网络返回false并提示
     */
    public static boolean checkNet(Context context, boolean showToast) {
        boolean conn = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                conn = true;
            }
            else {
                conn = false;
            }
        }
        catch (Exception e) {
            conn = false;
        }
        if (showToast && !conn) {
            Toast.makeText(context, "网络连接失败，请检查网络设置",
                    Toast.LENGTH_SHORT).show();
        }
        return conn;
    }

    // 检查 URL 是否有效
    public static boolean checkURL(String url) {
        boolean value = false;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            int code = conn.getResponseCode();
            LogTag.i("checkURL", ">>>>>>>>>>>>>>>> " + code
                    + " <<<<<<<<<<<<<<<<<<");
            if (code != 200) {
                value = false;
            }
            else {
                value = true;
            }
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    // 判断wifi是否可用
    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.getState() == State.CONNECTED) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 获取网络连接的类型
     * 
     * @return
     */
    public static String getConnectType(Context context) {
        String type = null;
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        String typeStr = null;
        if (info != null) {
            typeStr = info.getExtraInfo();
        }
        if (TextUtils.isEmpty(typeStr)) {
            type = "WIFI";
        }
        else {
            type = typeStr;
        }
        return type;
    }

    /**
     * 获取Ip地址
     * 
     * @return
     */
    public static String getHostIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (Exception e) {
            LogTag.sysout(e);
        }
        return null;
    }

    public static enum NetType {
        MOBILE, WIFI
    }

    public static interface NetChangedListener {
        void onNetConnected(NetType type);
    }

    /**
     * 端口号是否被占用
     * 
     * @param port
     *            端口号
     */
    public static boolean isLocalPortInUse(int port) {
        boolean isUsed = false;
        try {
            isUsed = isPortInUse("127.0.0.1", port);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return isUsed;
    }

    /**
     * @brief 检查主机端口是否被占用
     * @param host
     *            主机
     * @param port
     *            端口
     * @return true: already in use, false: not.
     * @throws UnknownHostException
     */
    private static boolean isPortInUse(String host, int port)
            throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            socket.close();
            flag = true;
        }
        catch (IOException e) {}
        return flag;
    }
    
    /**
     * the method to open net settings
     * @return
     */
    public static void openNetSetting(Context context){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(android.os.Build.VERSION.SDK_INT > 10 ){
            intent.setAction(android.provider.Settings.ACTION_SETTINGS);
        } else {
            intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }
        context.startActivity(intent);
    }

}
