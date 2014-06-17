package com.nyist.vnow.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

/**
 * 
 * �ϴ�������
 * @author spring sky<br>
 * Email :vipa1888@163.com<br>
 * QQ: 840950105<br>
 * ֧���ϴ��ļ��Ͳ���
 */
public class UploadUtil {
	private static UploadUtil uploadUtil;
	private static final String BOUNDARY =  UUID.randomUUID().toString(); // �߽��ʶ ������
	private static final String PREFIX = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data"; // ��������
	
	private AsyncHttpClient mHttpClient = null;
	
	private UploadUtil() {
		mHttpClient = new AsyncHttpClient();
	}

	/**
	 * ����ģʽ��ȡ�ϴ�������
	 * @return
	 */
	public static UploadUtil getInstance() {
		if (null == uploadUtil) {
			uploadUtil = new UploadUtil();
		}
		return uploadUtil;
	}

	private static final String TAG = "UploadUtil";
	private int readTimeOut = 10 * 1000; // ��ȡ��ʱ
	private int connectTimeout = 10 * 1000; // ��ʱʱ��
	/***
	 * ����ʹ�ö೤ʱ��
	 */
	private static int requestTime = 0;
	
	private static final String CHARSET = "utf-8"; // ���ñ���

	/***
	 * �ϴ��ɹ�
	 */
	public static final int UPLOAD_SUCCESS_CODE = 1;
	/**
	 * �ļ�������
	 */
	public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
	/**
	 * ����������
	 */
	public static final int UPLOAD_SERVER_ERROR_CODE = 3;
	protected static final int WHAT_TO_UPLOAD = 1;
	protected static final int WHAT_UPLOAD_DONE = 2;
	
	/**
	 * android�ϴ��ļ���������
	 * 
	 * @param filePath
	 *            ��Ҫ�ϴ����ļ���·��
	 * @param fileKey
	 *            ����ҳ��<input type=file name=xxx/> xxx���������fileKey
	 * @param RequestURL
	 *            �����URL
	 */
	public void uploadFile(String filePath, String fileKey, String RequestURL,
			Map<String, String> param) {
		if (filePath == null) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"�ļ�������");
			return;
		}
		try {
			File file = new File(filePath);
			uploadFile(file, fileKey, RequestURL, param);
		} catch (Exception e) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"�ļ�������");
			e.printStackTrace();
			return;
		}
	}

	/**
	 * android�ϴ��ļ���������
	 * 
	 * @param file
	 *            ��Ҫ�ϴ����ļ�
	 * @param fileKey
	 *            ����ҳ��<input type=file name=xxx/> xxx���������fileKey
	 * @param RequestURL
	 *            �����URL
	 */
	public void uploadFile(final File file, final String fileKey,
			final String RequestURL, final Map<String, String> param) {
		if (file == null || (!file.exists())) {
			sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE,"�ļ�������");
			return;
		}

		Log.i(TAG, "�����URL=" + RequestURL);
		Log.i(TAG, "�����fileName=" + file.getName());
		Log.i(TAG, "�����fileKey=" + fileKey);
//		new Thread(new Runnable() {  //�����߳��ϴ��ļ�
//			@Override
//			public void run() {
//				toUploadFile(file, fileKey, RequestURL, param);
//			}
//		}).start();
		asyncUploadFile(file, RequestURL, param);
	}
	
	private void asyncUploadFile(File file, String url, Map<String, String> param) {
		RequestParams requestParams = new RequestParams();
        try {
            requestParams.put("filevalue", file);
        } catch (FileNotFoundException e) {
        	return;
        }

        mHttpClient.post(url, requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                    	UploadUtil.getInstance().sendMessage(UPLOAD_SUCCESS_CODE, "�ϴ����" + response);
                    }
                    
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    	UploadUtil.getInstance().sendMessage(UPLOAD_SERVER_ERROR_CODE,"�ϴ�ʧ�ܣ�code=" + statusCode);
                    }
                });
	}
	
	public void asyncUploadFile(String url, RequestParams requestParams) {
        mHttpClient.post(url, requestParams,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                    	UploadUtil.getInstance().sendMessage(UPLOAD_SUCCESS_CODE, "�ϴ����" + response);
                    }
                    
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    	UploadUtil.getInstance().sendMessage(UPLOAD_SERVER_ERROR_CODE,"�ϴ�ʧ�ܣ�code=" + statusCode);
                    }
                });
	}
	

	private void toUploadFile(File file, String fileKey, String RequestURL,
			Map<String, String> param) {
		String result = null;
		requestTime= 0;
		
		long requestTime = System.currentTimeMillis();
		long responseTime = 0;

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(connectTimeout);
			conn.setDoInput(true); // ����������
			conn.setDoOutput(true); // ���������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST"); // ����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
//			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			/**
			 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
			 */
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer sb = null;
			String params = "";
			
			/***
			 * �����������ϴ�����
			 */
			if (param != null && param.size() > 0) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					sb = null;
					sb = new StringBuffer();
					String key = it.next();
					String value = param.get(key);
					sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
					sb.append(value).append(LINE_END);
					params = sb.toString();
					Log.i(TAG, key+"="+params+"##");
					dos.write(params.getBytes());
//					dos.flush();
				}
			}
			
			sb = null;
			params = null;
			sb = new StringBuffer();
			/**
			 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�
			 * filename���ļ������֣����׺��� ����:abc.png
			 */
			sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
			sb.append("Content-Disposition:form-data; name=\"" + fileKey
					+ "\"; filename=\"" + file.getName() + "\"" + LINE_END);
			sb.append("Content-Type:image/pjpeg" + LINE_END); // �������õ�Content-type����Ҫ�� �����ڷ������˱���ļ������͵�
			sb.append(LINE_END);
			params = sb.toString();
			sb = null;
			
			Log.i(TAG, file.getName()+"=" + params+"##");
			dos.write(params.getBytes());
			/**�ϴ��ļ�*/
			InputStream is = new FileInputStream(file);
			onUploadProcessListener.initUpload((int)file.length());
			byte[] bytes = new byte[1024];
			int len = 0;
			int curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				dos.flush();
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();
			
			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
			dos.write(end_data);
			dos.flush();
//			
//			dos.write(tempOutputStream.toByteArray());
			/**
			 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
			 */
			int res = conn.getResponseCode();
			responseTime = System.currentTimeMillis();
			this.requestTime = (int) ((responseTime-requestTime)/1000);
			Log.e(TAG, "response code:" + res);
			if (res == 200) {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				sendMessage(UPLOAD_SUCCESS_CODE, "�ϴ����"
						+ result);
				return;
			} else {
				Log.e(TAG, "request error");
				sendMessage(UPLOAD_SERVER_ERROR_CODE,"�ϴ�ʧ�ܣ�code=" + res);
				return;
			}
		} catch (MalformedURLException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE,"�ϴ�ʧ�ܣ�error=" + e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			sendMessage(UPLOAD_SERVER_ERROR_CODE,"�ϴ�ʧ�ܣ�error=" + e.getMessage());
			e.printStackTrace();
			return;
		}
	}

	/**
	 * �����ϴ����
	 * @param responseCode
	 * @param responseMessage
	 */
	private void sendMessage(int responseCode,String responseMessage)
	{
		System.out.println("responseCode-->"+responseCode+"----"+responseMessage);
//		onUploadProcessListener.onUploadDone(responseCode, responseMessage);
	}
	
	/**
	 * ������һ���Զ���Ļص������õ��ص��ϴ��ļ��Ƿ����
	 * 
	 * @author shimingzheng
	 * 
	 */
	public static interface OnUploadProcessListener {
		/**
		 * �ϴ���Ӧ
		 * @param responseCode
		 * @param message
		 */
		void onUploadDone(int responseCode, String message);
		/**
		 * �ϴ���
		 * @param uploadSize
		 */
		void onUploadProcess(int uploadSize);
		/**
		 * ׼���ϴ�
		 * @param fileSize
		 */
		void initUpload(int fileSize);
	}
	private OnUploadProcessListener onUploadProcessListener;
	
	

	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	/**
	 * ��ȡ�ϴ�ʹ�õ�ʱ��
	 * @return
	 */
	public static int getRequestTime() {
		return requestTime;
	}
	
	public static interface uploadProcessListener{
		
	}
	
	
	
	
}
