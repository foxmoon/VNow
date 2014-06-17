package com.vnow.sdk.framework;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import com.vnow.sdk.core.IVNowCore;
import com.vnow.sdk.core.IVNowCoreCallback;

/**
 * 访问服务器方法的封装
 * @author harry
 * @version Creat on 2014-6-17上午9:44:50
 */
public class IVNowFramework {
    private final boolean mbTesting = false;
    private final String TAG = "IVNowFramework";
    private final String WebServerURL = "http://www.vnow.com.cn/";
    private final String MMServerIP = "www.vnow.com.cn";
    private final String actionName = "com.vnow.service";
    private boolean mIsInit = false;
    private IVNowCore mIVNowCore = null;
    private IFrameworkEventListener mFrameworkEventListener = null;
    private EventHandler mEventHandler = new EventHandler();
    private int mAudioQuality = 0;
    private int mVideoBitrate = 384;
    private int mVideoWidth = 320;
    private int mVideoHeight = 240;
    private int mVideoFrameRate = 15;
    private int mVideoEncoderType = 0; // 0: stagefright 1: recorder

    // ////////////////////////////////////////////////////////////////
    // interface
    public void init(Context context) {
        Log.i(TAG, "init");
        if (mIsInit == true) {
            // return;
        }
        mIsInit = true;
        Intent intent = new Intent(actionName);
        boolean bResult = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        ComponentName name = context.startService(intent);
        Log.i(TAG, "service name = " + name);
        if (bResult == true) {
            Log.i(TAG, "bindService successful!");
        }
        else {
            Log.i(TAG, "bindService failed!");
        }
    }

    public void deInit(Context context) {
        Log.i(TAG, "deInit");
        // context.unbindService(connection);
    }

    public void coreExit(Context context) {
        Log.i(TAG, "coreExit");
        if (mIVNowCore != null) {
            try {
                mIVNowCore.stopService();
            }
            catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            context.unbindService(connection);
        }
    }

    public void setEventListener(IFrameworkEventListener listener) {
        mFrameworkEventListener = listener;
    }

    public int httpPost(String URI, List<NameValuePair> params, String strContex) {
        VNowHttpPost httpPost = new VNowHttpPost();
        httpPost.SetCallback(mHttpPostCallback, strContex);
        httpPost.SetParam(params);
        // httpPost.SetURI("http://192.168.5.163:8080/vnowService/remote/register/userregister/loginRegister");
        // httpPost.SetURI("http://www.beijibear.com/android_post.php");
        httpPost.SetURI(/* WebServerURL + */URI);
        httpPost.startPost();
        return 0;
    }

    public int httpGet(String URI, String strContex) {
        VNowHttpGet httpGet = new VNowHttpGet();
        httpGet.SetCallback(mHttpGetCallback, strContex);
        // httpPost.SetURI("http://192.168.5.163:8080/vnowService/remote/register/userregister/loginRegister");
        // httpPost.SetURI("http://www.beijibear.com/android_post.php");
        httpGet.SetURI(/* WebServerURL + */URI);
        httpGet.startGet();
        return 0;
    }

    public int httpUpLoad(String URI, String strFileName, String strParams, String strContex) {
        VNowHttpUpLoad httpUpLoad = new VNowHttpUpLoad();
        httpUpLoad.SetCallback(mHttpUpLoadCallback, strContex);
        // httpPost.SetURI("http://192.168.5.163:8080/vnowService/remote/register/userregister/loginRegister");
        // httpPost.SetURI("http://www.beijibear.com/android_post.php");
        httpUpLoad.SetURI(/* WebServerURL + */URI);
        httpUpLoad.SetLocalFileName(strFileName);
        httpUpLoad.SetParam(strParams);
        httpUpLoad.startUpLoad();
        return 0;
    }

    // //////////////////////////////////////////////////////////////////
    // core api
    public int dispatchApi(String strCmd) {
        if (mIVNowCore != null) {
            try {
                mIVNowCore.dispatchApi(strCmd);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    public int setRemoteVidSurface(Surface remoteVideoSurface) {
        if (mIVNowCore != null) {
            try {
                mIVNowCore.setRemoteVidSurface(remoteVideoSurface);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    public int openLocalVideo(Surface previewSurface, String strParam) {
        if (mIVNowCore != null) {
            try {
                mIVNowCore.openLocalVideo(previewSurface, strParam);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    public int closeLocalVideo() {
        if (mIVNowCore != null) {
            try {
                mIVNowCore.closeLocalVideo();
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    public int echoDelaySet(String strEchoDelayMs) {
        if (mIVNowCore != null) {
            try {
                mIVNowCore.echoDelaySet(strEchoDelayMs);
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    /*
     * public int openScreenShare() { return 0; } public int closeScreenShare()
     * { return 0; } public int setVideoBitrate(int nVidBitrate) { if
     * (nVidBitrate < 512) { mVideoBitrate = 384; mVideoWidth = 320;
     * mVideoHeight = 240; mVideoFrameRate = 15; } else if (nVidBitrate < 768) {
     * mVideoBitrate = 512; mVideoWidth = 640; mVideoHeight = 480;
     * mVideoFrameRate = 15; } else if (nVidBitrate < 1024) { mVideoBitrate =
     * 768; mVideoWidth = 640; mVideoHeight = 480; mVideoFrameRate = 30; } else
     * if (nVidBitrate < 2048) { mVideoBitrate = 1280; mVideoWidth = 1280;
     * mVideoHeight = 720; mVideoFrameRate = 15; } else { mVideoBitrate = 2048;
     * mVideoWidth = 1280; mVideoHeight = 720; mVideoFrameRate = 30; } return 0;
     * } public int SetVideoEncoderType(int nVidEncType) { if (nVidEncType == 0
     * || nVidEncType == 1) { mVideoEncoderType = nVidEncType; return 0; }
     * return -1; }
     */
    // ////////////////////////////////////////////////////////////////
    // event process
    private void sendMessage(String strEvent, String strMsg) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("event", strEvent);
        bundle.putString("msg", strMsg);
        msg.setData(bundle);
        mEventHandler.sendMessage(msg);
    }

    class EventHandler extends Handler {
        public EventHandler() {}

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String strEvent = bundle.getString("event");
            String strMsg = bundle.getString("msg");
            Log.d("EventHandler", "handleMessage: " + strMsg);
            mFrameworkEventListener.onEventNotify(strEvent, strMsg);
        }
    }

    public interface IFrameworkEventListener {
        public void onEventNotify(String strEvent, String strResult);
    }

    // ////////////////////////////////////////////////////////////////
    // core service
    private class NVCoreServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mIVNowCore = IVNowCore.Stub.asInterface(service);
            if (mIVNowCore == null) {
                Log.i(TAG, "bind service failed!");
                return;
            }
            try {
                mIVNowCore.setEventCallback(mCallback);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "<root><Info DispType=\"getstatus\" /></root>");
            dispatchApi("<root><Info DispType=\"getstatus\" /></root>");
            if (mbTesting) {
                TestThread thread = new TestThread();
                new Thread(thread).start();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected...");
            mIVNowCore = null;
        }
    }

    // ////////////////////////////////////////////////////////////////
    // core callback
    private class CoreCallback extends IVNowCoreCallback.Stub {
        public void eventCallback(String strEvent, String strResult) throws RemoteException {
            Log.d(TAG, "eventCallback: " + strEvent);
            sendMessage(strEvent, strResult);
        }
    }

    private NVCoreServiceConnection connection = new NVCoreServiceConnection();
    private CoreCallback mCallback = new CoreCallback();

    // ////////////////////////////////////////////////////////////////
    // http callback
    private class VNHttpPostCallback implements VNowHttpPost.HttpPostCallback {
        @Override
        public void onHttpPostResult(String strContex, String strResult) {
            Log.d(TAG, "onHttpPostResult: contex(" + strContex + ") Result:" + strResult);
            sendMessage(strContex, strResult);
        }
    }

    private class VNHttpGetCallback implements VNowHttpGet.HttpGetCallback {
        @Override
        public void onHttpGetResult(String strContex, String strResult) {
            Log.d(TAG, "onHttpGetResult: contex(" + strContex + ") Result:" + strResult);
            sendMessage(strContex, strResult);
        }
    }

    private class VNHttpUpLoadCallback implements VNowHttpUpLoad.HttpUpLoadCallback {
        @Override
        public void onHttpUpLoadResult(String strContex, String strResult) {
            Log.d(TAG, "onHttpUpLoadResult: contex(" + strContex + ") Result:" + strResult);
            sendMessage(strContex, strResult);
        }
    }

    private VNHttpPostCallback mHttpPostCallback = new VNHttpPostCallback();
    private VNHttpGetCallback mHttpGetCallback = new VNHttpGetCallback();
    private VNHttpUpLoadCallback mHttpUpLoadCallback = new VNHttpUpLoadCallback();

    // ////////////////////////////////////////////////////////////////
    // test interface
    class TestThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Log.d("TestThread", "running...");
                // sendMessage("",
                // "{\"cmd\":\"res_register\", \"result\":\"true\"}");
            }
        }
    }
}