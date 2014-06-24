package com.vnow.sdk.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * 
 * @author WangLr
 * @description VNow��HTTP������Get������
 * 
 */
public class VNowHttpGet {
    private final static String TAG = "VNowHttpGet";
    private HttpGetCallback mCallback = null;
    private String mStrURI = "";
    private String mStrContex = "";

    public void SetCallback(HttpGetCallback callback, String strContex) {
        mCallback = callback;
        mStrContex = strContex;
    }

    public void SetURI(String strURI) {
        mStrURI = strURI;
    }

    public int startGet() {
        if ("".equals(mStrURI)) {
            return -1;
        }
        new Thread() {
            public void run() {
                HttpGet httpRequest = new HttpGet(mStrURI);
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    // httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    // 6000);
                    // httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    // 10000);
                    HttpResponse httpResponse = httpClient.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String strResult = EntityUtils.toString(httpResponse.getEntity());
                        Log.d("", strResult);
                        mCallback.onHttpGetResult(mStrContex, strResult);
                    }
                    else {
                        Log.d("", "Error Response: " + httpResponse.getStatusLine().toString());
                    }
                }
                catch (ClientProtocolException e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpGetResult(mStrContex, "{\"result\":0}");
                }
                catch (IOException e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpGetResult(mStrContex, "{\"result\":0}");
                }
                catch (Exception e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpGetResult(mStrContex, "{\"result\":0}");
                }
            }
        }.start();
        return 0;
    }

    public interface HttpGetCallback {
        public void onHttpGetResult(String strContex, String strResult);
    }
}
