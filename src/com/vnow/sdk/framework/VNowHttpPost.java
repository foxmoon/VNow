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
 * @description VNow��HTTP������Post������
 * 
 */
public class VNowHttpPost {
    private final static String TAG = "VNowHttpPost";
    private HttpPostCallback mCallback = null;
    private String mStrURI = "";
    private String mStrContex = "";
    private List<NameValuePair> mParams = null;

    public void SetCallback(HttpPostCallback callback, String strContex) {
        mCallback = callback;
        mStrContex = strContex;
    }

    public void SetURI(String strURI) {
        mStrURI = strURI;
    }

    public void SetParam(List<NameValuePair> params) {
        mParams = params;
    }

    public int startPost() {
        if ("".equals(mStrURI) || mParams == null) {
            return -1;
        }
        new Thread() {
            public void run() {
                HttpPost httpRequest = null;
                httpRequest = new HttpPost(mStrURI);
                try {
                    httpRequest.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    // httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    // 6000);
                    // httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                    // 10000);
                    HttpResponse httpResponse = httpClient.execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String strResult = EntityUtils.toString(httpResponse.getEntity());
                        Log.d("", strResult);
                        mCallback.onHttpPostResult(mStrContex, strResult);
                    }
                    else {
                        Log.d("", "Error Response" + httpResponse.getStatusLine().toString());
                    }
                }
                catch (ClientProtocolException e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpPostResult(mStrContex, "{\"result\":0}");
                }
                catch (IOException e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpPostResult(mStrContex, "{\"result\":0}");
                }
                catch (Exception e) {
                    // Log.d("", e.getMessage().toString());
                    // e.printStackTrace();
                    mCallback.onHttpPostResult(mStrContex, "{\"result\":0}");
                }
            }
        }.start();
        return 0;
    }

    public interface HttpPostCallback {
        public void onHttpPostResult(String strContex, String strResult);
    }
}
