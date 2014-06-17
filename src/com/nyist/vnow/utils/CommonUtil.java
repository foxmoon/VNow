package com.nyist.vnow.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.nyist.vnow.core.VNowApplication;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

@SuppressLint("NewApi")
public class CommonUtil {
	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	public static final String TIME_FORMAT = "ahh:mm:ss";
	public static final String TIME_24FORMAT = "HH:mm:ss";
	public static final String DATE_FORMAT = "dd/MM/yy";
	public static String _httpUrl;
	public static String _channelPic;
	public static String _svrAddr;
	public static String _svrIP;

	
	
	public static String get_httpUrl() {
		return _httpUrl;
	}

	public static void set_httpUrl(String ip) {
		CommonUtil._svrIP = ip;
		CommonUtil._httpUrl = "http://"+ip+":8080/remote/";
		CommonUtil._channelPic = "http://"+ip+":8080/other/file/"; 
		CommonUtil._svrAddr = ip+":5065";
	}

	public static String CommandStringDecode(String in) {
		String out = in;
		while (true) {
			int index;
			index = out.indexOf("&space;");
			if (index != -1) {
				out.replace("&space;", " ");
			} else {
				break;
			}
		}

		while (true) {
			int index;
			index = out.indexOf("&equal;");
			if (index != -1) {
				out.replace("&equal;", "=");
			} else {
				break;
			}
		}

		while (true) {
			int index;
			index = out.indexOf("&amp;");
			if (index != -1) {
				out.replace("&amp;", "&");
			} else {
				break;
			}
		}

		while (true) {
			int index;
			index = out.indexOf("&return;");
			if (index != -1) {
				out.replace("&return;", "\r");
			} else {
				break;
			}
		}

		while (true) {
			int index;
			index = out.indexOf("&newline;");
			if (index != -1) {
				out.replace("&newline;", "\n");
			} else {
				break;
			}
		}

		return out;
	}

	/**
	 * the method to get the file name of url
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(String url) {
		String out = null;
		int index = url.lastIndexOf("/") + 1;
		out = url.substring(index, url.length());
		return out;
	}

	/**
	 * the method to get system time
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		String paramFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(System.currentTimeMillis()).toString();
	}

	public static String yearTimeFormat(long timeMillis) {
		String paramFormat = "yyyy-MM-dd a HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String thisYearTimeFormat(long timeMillis) {
		String paramFormat = "MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String differentYearTimeFormat(long timeMillis) {
		String paramFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String monthTimeFormat(long timeMillis) {
		String paramFormat = "MM-dd HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String thisWeekTimeFormat(long timeMillis) {
		String paramFormat = "EEE a HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String thisDayTimeFormat(long timeMillis) {
		String paramFormat = "a HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String currentDayTimeFormat(long timeMillis) {
		String paramFormat = "HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat(paramFormat);
		return sdf.format(timeMillis).toString();
	}

	public static String currentDayTimeRegionFormat(long dayTime) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("H");
		int hour = Integer.parseInt(sdf.format(new Date(dayTime)));
		if (hour >= 0 && hour < 8) {
			result = "早上";
		} else if (hour >= 8 && hour < 11) {
			result = "上午";
		} else if (hour >= 11 && hour < 13) {
			result = "中午";
		} else if (hour >= 13 && hour < 18) {
			result = "下午";
		} else if (hour >= 18 && hour <= 23) {
			result = "晚上";
		}
		return result;
	}

	public static HashMap<String, Long> compareTime(long before, long current) {
		HashMap<String, Long> result = new HashMap<String, Long>();
		long diff = current - before;
		long day = diff / (24 * 60 * 60 * 1000);
		long hour = (diff / (60 * 60 * 1000) - day * 24);
		long minute = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long second = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);
		result.put("day", day);
		result.put("hour", hour);
		result.put("minute", minute);
		result.put("second", second);
		return result;
	}

	public static int daysDiffCompareToToday(long time) {
		int result = -1;
		SimpleDateFormat sdf = new SimpleDateFormat("D");
		Date today = new Date(System.currentTimeMillis());
		Date date = new Date(time);
		result = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(date));
		return result;
	}

	public static boolean isSameWeekCompareToToday(long time) {
		boolean isSame = false;
		SimpleDateFormat sdf = new SimpleDateFormat("w");
		Date today = new Date(System.currentTimeMillis());
		Date date = new Date(time);
		int diff = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(date));
		if (diff == 0) {
			isSame = true;
		}
		return isSame;
	}

	public static boolean isSameYearCompareToToday(long time) {
		boolean isSame = false;
		SimpleDateFormat sdf = new SimpleDateFormat("y");
		Date today = new Date(System.currentTimeMillis());
		Date date = new Date(time);
		int diff = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(date));
		if (diff == 0) {
			isSame = true;
		}
		return isSame;
	}

	public static boolean isSameTimeInterval(long before, long current) {
		long interval = 1;
		boolean isSame = false;
		HashMap<String, Long> result = compareTime(before, current);
		if (result.get("day") == 0 && result.get("hour") == 0
				&& result.get("minute") < interval) {
			isSame = true;
		}
		return isSame;
	}
	
	public static boolean isSameDayInterval(long before, long current) {
		boolean isSame = false;
		HashMap<String, Long> result = compareTime(before, current);
		if (result.get("day") == 0) {
			isSame = true;
		}
		return isSame;
	}

	/**
	 * the method to format the type of system time to string
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		if (DateFormat.is24HourFormat(VNowApplication.the()
				.getApplicationContext())) {
			return new SimpleDateFormat(TIME_24FORMAT).format(time);
		} else {
			return new SimpleDateFormat(TIME_FORMAT).format(time);
		}
	}

	/**
	 * the method to get current system time
	 * 
	 * @return
	 */
	public static String getCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		return sdf.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * the method to get the data which is in CMD order
	 * 
	 * @param data
	 * @param cmd
	 * @return
	 */
	public static String getCMD(String data, String cmd) {
		String allcmd = cmd + "=";
		String[] strs = data.split(" ");
		for (String str : strs) {
			if (str.indexOf(allcmd) == 0) {
				return str.substring(allcmd.length());
			}
		}
		return "";
	}

	/**
	 * the method to check the sdCard is exist or not
	 * 
	 * @return
	 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}

		return false;
	}

	public static String getImagePath() {
		String path = "/sdcard/vnow/image/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getApkLastPath() {
		String path = "/sdcard/vnow/lastapk/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getHeaderPath() {
		File file = new File("/sdcard/vnow/header/");
		file.mkdirs();
		return file.getAbsolutePath() + "/";
	}

	public static String getMomentsPath() {
		String path = "/sdcard/vnow/Moments/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getFilePath() {
		File file = new File("/sdcard/vnow/file/");
		file.mkdirs();
		return file.getAbsolutePath() + "/";
	}
	
	public static File getVideoDir() {
		String path = "/sdcard/vnow/video/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	/*
	 * HACKISH: These constants aren't yet available in my API level (7), but I
	 * need to handle these cases if they come up, on newer versions
	 */
	public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
	public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
	public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
	public static final int NETWORK_TYPE_IDEN = 11; // Level 8
	public static final int NETWORK_TYPE_LTE = 13; // Level 11

	/**
	 * the method to check the device connect the Internet use 3G or WIFI
	 * 
	 * @return
	 */
	public static boolean checkWIFIor3G() {
		ConnectivityManager mConnectivity = (ConnectivityManager) VNowApplication
				.the().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}

		int netType = info.getType();
		int netSubtype = info.getSubtype();

		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE) {
			switch (netSubtype) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
				// NOT AVAILABLE YET IN API LEVEL 7
			case NETWORK_TYPE_EHRPD:
				return true; // ~ 1-2 Mbps
			case NETWORK_TYPE_EVDO_B:
				return true; // ~ 5 Mbps
			case NETWORK_TYPE_HSPAP:
				return true; // ~ 10-20 Mbps
			case NETWORK_TYPE_IDEN:
				return false; // ~25 kbps
			case NETWORK_TYPE_LTE:
				return true; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return false;
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isIpv4(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}
	public static int countOfAssignString(String source, String regexNew) {
		String regex = "[a-zA-Z]+";
		if (regexNew != null && !regexNew.equals("")) {
			regex = regexNew;
		}
		Pattern expression = Pattern.compile(regex);
		Matcher matcher = expression.matcher(source);
		TreeMap<Object, Integer> myTreeMap = new TreeMap<Object, Integer>();
		int n = 0;
		Object word = null;
		Object num = null;
		while (matcher.find()) {
			word = matcher.group();
			n++;
			if (myTreeMap.containsKey(word)) {
				num = myTreeMap.get(word);
				Integer count = (Integer) num;
				myTreeMap.put(word, new Integer(count.intValue() + 1));
			} else {
				myTreeMap.put(word, new Integer(1));
			}
		}
		return n;
	}

	public static void makeAssignDir(File dir) {
		if (!dir.getParentFile().exists()) {
			makeAssignDir(dir.getParentFile());
		}
		dir.mkdir();
	}

	public static String fileSizeFormat(long size) {
		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (size < 1024 * 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "MB");
		}
	}

	public static void copyText(String content, Context context) {
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	public static String pasteText(Context context) {
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	public static String getFilePostfix(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
	}

	/**
	 * 判断当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			NetworkInfo info[] = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取手机经纬度
	 * 
	 * @param context
	 *            上下文
	 * @return 可用的location 可能为空
	 * 
	 */
	public static Location getLocation(Context context) {
		Location currentLocation = null;
		try {
			// 获取到LocationManager对象
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			// 创建一个Criteria对象
			Criteria criteria = new Criteria();
			// 设置粗略精确度
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			// 设置是否需要返回海拔信息
			criteria.setAltitudeRequired(false);
			// 设置是否需要返回方位信息
			criteria.setBearingRequired(false);
			// 设置是否允许付费服务
			criteria.setCostAllowed(true);
			// 设置电量消耗等级
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
			// 设置是否需要返回速度信息
			criteria.setSpeedRequired(false);

			// 根据设置的Criteria对象，获取最符合此标准的provider对象
			String currentProvider = locationManager.getBestProvider(criteria,
					true);
			Log.d("Location", "currentProvider: " + currentProvider);
			// 根据当前provider对象获取最后一次位置信息
			currentLocation = locationManager
					.getLastKnownLocation(currentProvider);
		} catch (Exception e) {
			currentLocation = null;
		}
		return currentLocation;
	}

	private String intToIp(int i) {
		String ipStr = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
				+ ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);

		return ipStr;
	}

	/**
	 * @description 通过网络地址获得图片
	 * @param imageUrl
	 *            图片地址
	 * @return drawable
	 */
	public static Drawable loadImageFromUrl(String imageUrl) {
		try {
			URL u = new URL(imageUrl);
			URLConnection conn = u.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 2;// 宽度和高度设置为原来的1/2
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
			return new BitmapDrawable(bitmap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * the method to get bitmap from local image file path
	 * 
	 * @param path
	 * @param w
	 *            the width you want to scale,if w==0 is the original width of
	 *            the image
	 * @param h
	 *            the height you want to scale,if h==0 is the original height of
	 *            the image
	 * @return
	 */
	public static Bitmap getBitmapFromLocalPath(String path, int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;

		float scaleWidth = 0.f;
		float scaleHeight = 0.f;
		float scale = 0.f;
		if (w > 0 && h > 0) {
			if (width > w || height > h) {
				// 缩放
				scaleWidth = ((float) width) / w;
				scaleHeight = ((float) height) / h;
			}
			scale = Math.max(scaleWidth, scaleHeight);
		} else {
			// w = width;
			// h = height;
			w = 100;
			h = 100;
		}
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
				BitmapFactory.decodeFile(path, opts));

		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	/**
	 * @des 得到config.properties配置文件中的所有配置
	 * @return Properties对象
	 */
	public static Properties getConfig() {
		Properties props = new Properties();
		InputStream in = CommonUtil.class
				.getResourceAsStream("/com/vnow/utils/config.properties");
		try {
			props.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		if (offset < 0) {
			return null;
		}
		if (len <= 0) {
			return null;
		}
		if (offset + len > (int) file.length()) {
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public static Bitmap extractThumbNail(final String path, final int height,
			final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0
				&& width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;

			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
					: (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				return null;
			}

			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
					newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm,
						(bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			options = null;
		}

		return null;
	}

	/**
	 * the method to get the push server url
	 * 
	 * @return
	 */
	public static String getPushUrl() {
		String url = "tcp://192.168.9.102:9010";
		return url;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 获取手机ip
	 * 
	 * @param context
	 *            上下文
	 * @return 可用的ip
	 * 
	 */
	public static String getLocalIPAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		int i = info.getIpAddress();
		String ipStr = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
				+ ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
		return ipStr;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnailFromPath(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}


	public static void setGridViewHeightBasedOnChildren(GridView gridView,
			int rowNum) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i = i + rowNum) {
			View gridItem = listAdapter.getView(0, null, gridView);
			gridItem.measure(0, 0);
			totalHeight += gridItem.getMeasuredHeight();
		}
		int lineNum = listAdapter.getCount() / 3;
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (gridView.getHeight() * (lineNum - 1))
				+ 3 * (lineNum - 1);
		gridView.setLayoutParams(params);
	}

//	public static String intervalTime(long beforeTime, Context context) {
//		String intervalTime = null;
//		long currentTime = System.currentTimeMillis();
//		HashMap<String, Long> result = compareTime(beforeTime, currentTime);
//		if (result.get("day") > 0) {
//			intervalTime = result.get("day")
//					+ context.getString(R.string.str_time_day_before);
//			return intervalTime;
//		} else if (result.get("hour") > 0) {
//			intervalTime = result.get("hour")
//					+ context.getString(R.string.str_time_hour_before);
//			return intervalTime;
//		} else if (result.get("minute") > 0) {
//			intervalTime = result.get("minute")
//					+ context.getString(R.string.str_time_minute_before);
//			return intervalTime;
//		} else {
//			intervalTime = context.getString(R.string.str_time_now);
//			return intervalTime;
//		}
//
//	}

	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	public static boolean checkMobile(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	
	public static String parseEmailJsonUrl(String jsonUrl) {
		String isSuccess = "";
		try {
			JSONTokener jsonParser = new JSONTokener(jsonUrl);
			JSONObject object = (JSONObject) jsonParser.nextValue();

			isSuccess = object.getString("success");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	public static String getVerifyCode() {
		String code = "";
		for (int i = 0; i < 6; i++) {
			int number = new Random().nextInt(10);
			code += number;
		}
		return code;
	}

	public static String[] getInterceptString(String str) {
		String[] array = new String[3];
		array[0] = str.charAt(0) +""+ str.charAt(1);
		array[1] = str.charAt(2) + ""+str.charAt(3);
		array[2] = str.charAt(4) + ""+str.charAt(5);
		return array;
	}

}
