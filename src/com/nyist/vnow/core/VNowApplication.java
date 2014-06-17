package com.nyist.vnow.core;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nyist.vnow.utils.UpdateSoftManager;
import com.nyist.vnow.utils.VNowSetting;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class VNowApplication extends Application implements
AMapLocationListener,Runnable{
	private static VNowApplication mInstance;
	private VNowCore mCore;
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation mLocation;
	
	public static VNowApplication the() {
		return mInstance;
	}
	
	public VNowApplication(){
		mInstance = this;
	}
	public  VNowCore getCore() {
		if (null == mCore)
			createCore();
		return mCore;
	}

	public void createCore() {
		if (null == mCore) {
			mCore = new VNowCore(mInstance);
		}
	}

	public void destroyCore() {
		if (null != mCore) {
			mCore.clearCore();
			mCore = null;
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		aMapLocManager = LocationManagerProxy.getInstance(this);
		startLocation();
	}
	
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		stoptLocation();
		
	}

	public void showToast(String msg) {
		Toast.makeText(mInstance, msg, Toast.LENGTH_SHORT).show();
	}
	// setting params-------
	public String getSetting(String key,String defultValue) {
		return VNowSetting.the(mInstance).getSetting(key, defultValue);
	}
	public boolean getSetting(String key,boolean denfultValue) {
		return VNowSetting.the(mInstance).getSetting(key, denfultValue);
	}
	public void setSetting(String key, String value) {
		VNowSetting.the(mInstance).setSetting(key, value);
	}

	public void setSetting(String key, boolean value) {
		VNowSetting.the(mInstance).setSetting(key, value);
	}
	
	public int getSetting(String key,int mode) {
		return VNowSetting.the(mInstance).getSetting(key, mode);
	}
	public void setSetting(String key, int value) {
		VNowSetting.the(mInstance).setSetting(key, value);
	}
	/**
	 * the method to get the current version of this app
	 * @return
	 */
	public String getVersion() {
		final String unknown = "Unknown";

		try {
			return mInstance.getPackageManager().getPackageInfo(
					mInstance.getPackageName(), 0).versionName;
		} catch (NameNotFoundException ex) {
		}
		return unknown;
	}
	
	/**
	 * the method to get the application package name
	 * @return
	 */
	public String getPkgName() {
		return getPackageName();
	}
	
	/**
	 * the method to get the device id
	 * @return
	 */
	public String getDeviceId() {
		return Secure.getString(getContentResolver(), Secure.ANDROID_ID);
	}
	
	public AMapLocation getBDLocation(){
		return mLocation;
	}
	private void startLocation(){
		if(null!=aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER)){
			mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
		}else if(null!=aMapLocManager.getLastKnownLocation(LocationManagerProxy.NETWORK_PROVIDER)){
			mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
		}
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}
	private void stoptLocation(){
		/**
		 * 销毁定位
		 */
			if (aMapLocManager != null) {
				aMapLocManager.removeUpdates(this);
				aMapLocManager.destory();
			}
			aMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation mapLocation) {
		// TODO Auto-generated method stub
		if(null != mapLocation)
			mLocation = mapLocation;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}
	
}
