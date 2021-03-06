package com.nyist.vnow.core;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.Constants;
import com.nyist.vnow.utils.LogTag;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.UpdateSoftManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.widget.Toast;

public class VNowApplication extends Application implements
        AMapLocationListener, Runnable {
    private static VNowApplication mInstance;
    // app启动状态判别，防止app被杀进程软件强杀后，应用一些静态变量导致的空指针异常。
    public static int mAppState;
    public static Context mContext;
    private VNowCore mCore;
    private LocationManagerProxy aMapLocManager = null;
    private AMapLocation mLocation;

    public synchronized static void setAppState(int state) {
        mAppState = state;
    }

    public static VNowApplication newInstance() {
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
        mContext = this;
        aMapLocManager = LocationManagerProxy.getInstance(this);
        startLocation();
        CommonUtil.set_httpUrl(Session.newInstance(this).getWebServerIp());
        // 默认的mediaServerIp=webServerIp
        initImageLoader(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stoptLocation();
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
    
    public static void initImageLoader(Context context) {
        // Create default options which will be used for every
        // displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3)
                // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                // default
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSizePercentage(13) // default
                .defaultDisplayImageOptions(defaultOptions).writeDebugLogs() // Remove
                                                                             // for
                                                                             // release
                                                                             // app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
