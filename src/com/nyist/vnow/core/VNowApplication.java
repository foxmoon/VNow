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
        AMapLocationListener, Runnable {
    private static VNowApplication mInstance;
    public static int mAppState;
    private VNowCore mCore;
    private LocationManagerProxy aMapLocManager = null;
    private AMapLocation mLocation;

    public synchronized static void setAppState(int state) {
        mAppState = state;
    }

    public static VNowApplication getInstance() {
        return mInstance;
    }

    public VNowApplication() {
        mInstance = this;
    }

    public VNowCore getCore() {
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
        super.onCreate();
        mAppState = -1;
        aMapLocManager = LocationManagerProxy.getInstance(this);
        startLocation();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        stoptLocation();
    }

    // setting params-------
    public String getSetting(String key, String defultValue) {
        return VNowSetting.the(mInstance).getSetting(key, defultValue);
    }

    public boolean getSetting(String key, boolean denfultValue) {
        return VNowSetting.the(mInstance).getSetting(key, denfultValue);
    }

    public void setSetting(String key, String value) {
        VNowSetting.the(mInstance).setSetting(key, value);
    }

    public void setSetting(String key, boolean value) {
        VNowSetting.the(mInstance).setSetting(key, value);
    }

    public int getSetting(String key, int mode) {
        return VNowSetting.the(mInstance).getSetting(key, mode);
    }

    public void setSetting(String key, int value) {
        VNowSetting.the(mInstance).setSetting(key, value);
    }

    public AMapLocation getBDLocation() {
        return mLocation;
    }

    private void startLocation() {
        if (null != aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER)) {
            mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
        }
        else if (null != aMapLocManager.getLastKnownLocation(LocationManagerProxy.NETWORK_PROVIDER)) {
            mLocation = aMapLocManager.getLastKnownLocation(LocationManagerProxy.GPS_PROVIDER);
        }
        aMapLocManager.requestLocationUpdates(
                LocationProviderProxy.AMapNetwork, 5000, 10, this);
    }

    private void stoptLocation() {
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
        if (null != mapLocation)
            mLocation = mapLocation;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }
}
