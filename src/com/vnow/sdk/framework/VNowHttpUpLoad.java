package com.vnow.sdk.framework;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public class VNowHttpUpLoad {
    private final static String TAG = "VNowHttpUpLoad";
    private HttpUpLoadCallback mCallback = null;
    private String mStrURI = "";
    private String mStrContex = "";
    private String mStrFileName = "";
    private String mStrParams = "";

    public void SetCallback(HttpUpLoadCallback callback, String strContex) {
        mCallback = callback;
        mStrContex = strContex;
    }

    public void SetURI(String strURI) {
        mStrURI = strURI;
    }

    public void SetLocalFileName(String strFileName) {
        mStrFileName = strFileName;
    }

    public void SetParam(String strParams) {
        mStrParams = strParams;
    }

    public int startUpLoad() {
        if ("".equals(mStrURI) || "".equals(mStrFileName)) {
            return -1;
        }
        new Thread() {
            public void run() {
                int TIME_OUT = 60 * 1000; // ��ʱʱ��
                String CHARSET = "utf-8"; // ���ñ���
                // String RequestURL =
                // "http://192.168.32.170:8080/remote/file/req_upload_file.html";
                String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ
                                                                // ������//
                                                                // String
                                                                // PREFIX// =
                                                                // "--" ,//
                                                                // LINE_END =//
                                                                // "\r\n";
                String CONTENT_TYPE = "multipart/form-data"; // ��������
                String PREFIX = "--", LINE_END = "\r\n";
                // String file = "/mnt/sdcard/20140520124636.jpg";
                try {
                    URL url = new URL(mStrURI);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(TIME_OUT);
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setDoInput(true); // ����������
                    conn.setDoOutput(true); // ���������
                    conn.setUseCaches(false); // ������ʹ�û���
                    conn.setRequestMethod("POST"); // ����ʽ
                    conn.setRequestProperty("Charset", CHARSET);
                    // ���ñ���
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    if (mStrFileName != null && "".equals(mStrFileName) == false) {
                        /** * ���ļ���Ϊ�գ����ļ���װ�����ϴ� */
                        OutputStream outputSteam = conn.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(outputSteam);
                        StringBuffer sb = new StringBuffer();
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        /**
                         * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key
                         * �ſ��Եõ���Ӧ���ļ� filename���ļ������֣����׺���
                         * ����:abc.png
                         */
                        sb.append("Content-Disposition: form-data; name=\"" + mStrParams + "\"; filename=\"" + mStrFileName + "\""
                                + LINE_END);
                        sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                        sb.append(LINE_END);
                        dos.write(sb.toString().getBytes());
                        InputStream is = new FileInputStream(mStrFileName);
                        byte[] bytes = new byte[4096];
                        int len = 0;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }
                        is.close();
                        dos.write(LINE_END.getBytes());
                        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                        dos.write(end_data);
                        dos.flush();
                        /**
                         * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
                         */
                        int res = conn.getResponseCode();
                        Log.e(TAG, "response code:" + res);
                        if (res == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));// ���ñ���,������������
                            String line = "";
                            String strResult = "";
                            while ((line = reader.readLine()) != null) {
                                strResult += line;
                            }
                            reader.close();
                            Log.d("", strResult);
                            mCallback.onHttpUpLoadResult(mStrContex, strResult);
                            return;
                        }
                        else {
                            mCallback.onHttpUpLoadResult(mStrContex, "{\"result\":0}");
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    mCallback.onHttpUpLoadResult(mStrContex, "{\"result\":0}");
                }
            }
        }.start();
        return 0;
    }

    public interface HttpUpLoadCallback {
        public void onHttpUpLoadResult(String strContex, String strResult);
    }
}
