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
 * @description VNow锟斤拷HTTP锟斤拷锟斤拷锟斤拷Post锟斤拷锟斤拷锟斤拷
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
				int TIME_OUT = 60 * 1000; // 超时时间
				String CHARSET = "utf-8"; // 设置编码
				//String RequestURL = "http://192.168.32.170:8080/remote/file/req_upload_file.html";
				String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成// String PREFIX// = "--" ,// LINE_END =// "\r\n";
				String CONTENT_TYPE = "multipart/form-data"; // 内容类型
				String PREFIX = "--", LINE_END = "\r\n";
				//String file = "/mnt/sdcard/20140520124636.jpg";
				try {
					URL url = new URL(mStrURI);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(TIME_OUT);
					conn.setConnectTimeout(TIME_OUT);
					conn.setDoInput(true); // 允许输入流
					conn.setDoOutput(true); // 允许输出流
					conn.setUseCaches(false); // 不允许使用缓存
					conn.setRequestMethod("POST"); // 请求方式
					conn.setRequestProperty("Charset", CHARSET);
					// 设置编码
					conn.setRequestProperty("connection", "keep-alive");
					conn.setRequestProperty("Content-Type", CONTENT_TYPE+ ";boundary=" + BOUNDARY);
					if (mStrFileName != null && "".equals(mStrFileName) == false) {
						/** * 当文件不为空，把文件包装并且上传 */
						OutputStream outputSteam = conn.getOutputStream();
						DataOutputStream dos = new DataOutputStream(outputSteam);
						StringBuffer sb = new StringBuffer();
						sb.append(PREFIX);
						sb.append(BOUNDARY);
						sb.append(LINE_END);
						/**
						 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
						 * filename是文件的名字，包含后缀名的 比如:abc.png
						 */
						sb.append("Content-Disposition: form-data; name=\"" + mStrParams + "\"; filename=\""+ mStrFileName + "\"" + LINE_END);
						sb.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINE_END);
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
						 * 获取响应码 200=成功 当响应成功，获取响应的流
						 */
						int res = conn.getResponseCode();
						Log.e(TAG, "response code:" + res);
						if (res == 200) {
							BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
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
				} catch (Exception e) {
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
